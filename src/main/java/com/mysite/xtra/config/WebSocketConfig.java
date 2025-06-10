package com.mysite.xtra.config;

import java.security.Principal;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import javax.servlet.http.HttpSession;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.sockjs.transport.session.WebSocketServerSockJsSession;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.setErrorHandler(throwable -> logger.error("WebSocket 스케줄러 오류: ", throwable));
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        logger.info("WebSocket 메시지 브로커 설정 시작");
        config.enableSimpleBroker("/topic", "/queue", "/user")
              .setTaskScheduler(taskScheduler())
              .setHeartbeatValue(new long[] {5000, 5000});
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
        logger.info("WebSocket 메시지 브로커 설정 완료: /topic, /queue, /user 활성화, /app 프리픽스 설정");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("WebSocket 엔드포인트 등록 시작");
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setSessionCookieNeeded(true)
                .setHeartbeatTime(5000)
                .setWebSocketEnabled(true)
                .setDisconnectDelay(10000)
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js")
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setWebSocketEnabled(true)
                .setSessionCookieNeeded(true);
        logger.info("WebSocket 엔드포인트 등록 완료: /ws");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(128 * 1024)
                   .setSendBufferSizeLimit(512 * 1024)
                   .setSendTimeLimit(20000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        logger.info("WebSocket 클라이언트 인바운드 채널 설정 시작");
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor == null) {
                    logger.error("StompHeaderAccessor를 가져올 수 없습니다.");
                    return message;
                }

                String sessionId = accessor.getSessionId();
                StompCommand command = accessor.getCommand();
                
                logger.info("WebSocket 메시지 처리: command={}, sessionId={}, headers={}", 
                    command, sessionId, accessor.toNativeHeaderMap());

                if (StompCommand.CONNECT.equals(command)) {
                    logger.info("STOMP 연결 시도 - 세션: {}", sessionId);
                    
                    try {
                        // 헤더에서 사용자 정보 가져오기
                        String username = accessor.getFirstNativeHeader("X-User-Name");
                        String userId = accessor.getFirstNativeHeader("X-User-Id");
                        
                        if (username != null && userId != null) {
                            logger.info("헤더에서 사용자 정보 발견: username={}, userId={}", username, userId);
                            try {
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                if (userDetails != null) {
                                    Authentication auth = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                    accessor.setUser(auth);
                                    logger.info("사용자 인증 성공: {}", username);
                                    return message;
                                }
                            } catch (Exception e) {
                                logger.error("사용자 인증 실패: {}", e.getMessage());
                            }
                        }

                        // 세션에서 인증 정보 가져오기
                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                        if (sessionAttributes != null) {
                            HttpSession httpSession = (HttpSession) sessionAttributes.get(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                            if (httpSession != null) {
                                SecurityContext securityContext = (SecurityContext) httpSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                                if (securityContext != null) {
                                    Authentication auth = securityContext.getAuthentication();
                                    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                                        accessor.setUser(auth);
                                        logger.info("HTTP 세션에서 인증 정보 발견: {}", auth.getName());
                                        return message;
                                    }
                                }
                            }
                        }
                        
                        // SecurityContextHolder에서 인증 정보 가져오기
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                            accessor.setUser(auth);
                            logger.info("SecurityContextHolder에서 인증 정보 발견: {}", auth.getName());
                            return message;
                        }
                        
                        logger.warn("STOMP 연결 거부 - 인증되지 않은 사용자, 세션: {}", sessionId);
                        throw new RuntimeException("인증이 필요합니다.");
                    } catch (Exception e) {
                        logger.error("STOMP 연결 실패 - 예외 발생: {}, 세션: {}", e.getMessage(), sessionId);
                        throw new RuntimeException("인증이 필요합니다.");
                    }
                } else if (StompCommand.CONNECTED.equals(command)) {
                    logger.info("STOMP 연결 완료 - 세션: {}", sessionId);
                } else if (StompCommand.DISCONNECT.equals(command)) {
                    logger.info("STOMP 연결 종료 - 세션: {}", sessionId);
                }
                
                return message;
            }
        });
        logger.info("WebSocket 클라이언트 인바운드 채널 설정 완료");
    }

    @Bean
    public WebSocketHandler webSocketHandler(SubProtocolWebSocketHandler subProtocolWebSocketHandler) {
        return new WebSocketHandlerDecorator(subProtocolWebSocketHandler) {
            @Override
            public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                logger.info("WebSocket 연결 수립 - 세션: {}, 원격 주소: {}", 
                    session.getId(), session.getRemoteAddress());
                super.afterConnectionEstablished(session);
            }

            @Override
            public void handleMessage(WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) throws Exception {
                if (message instanceof TextMessage) {
                    logger.debug("WebSocket 메시지 수신 - 세션: {}, 메시지: {}", 
                        session.getId(), ((TextMessage) message).getPayload());
                }
                super.handleMessage(session, message);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                logger.info("WebSocket 연결 종료 - 세션: {}, 상태: {}", 
                    session.getId(), closeStatus);
                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
} 