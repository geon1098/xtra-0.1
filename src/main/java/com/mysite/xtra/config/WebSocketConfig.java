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
                        // 세션에서 인증 정보 가져오기 시도
                        HttpSession httpSession = (HttpSession) accessor.getSessionAttributes()
                            .get(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                        
                        Authentication auth = null;
                        if (httpSession != null) {
                            SecurityContext securityContext = (SecurityContext) httpSession
                                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                            if (securityContext != null) {
                                auth = securityContext.getAuthentication();
                            }
                        }
                        
                        // SecurityContextHolder에서 인증 정보 가져오기 시도
                        if (auth == null) {
                            auth = SecurityContextHolder.getContext().getAuthentication();
                        }
                        
                        logger.info("현재 인증 상태: {}", auth != null ? 
                            "인증됨 (사용자: " + auth.getName() + ")" : "인증되지 않음");
                        
                        if (auth != null && auth.isAuthenticated()) {
                            accessor.setUser(auth);
                            logger.info("STOMP 연결 승인 - 사용자: {}, 세션: {}", 
                                auth.getName(), sessionId);
                        } else {
                            // 인증되지 않은 경우에도 연결을 허용하되, 제한된 권한 부여
                            logger.warn("STOMP 연결 - 인증되지 않은 사용자, 제한된 권한으로 연결 허용, 세션: {}", sessionId);
                            accessor.setUser(new AnonymousAuthenticationToken("anonymous", "anonymous", 
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
                        }
                    } catch (Exception e) {
                        logger.error("STOMP 연결 실패 - 예외 발생: {}, 세션: {}", 
                            e.getMessage(), sessionId, e);
                        throw new RuntimeException("연결 중 오류가 발생했습니다: " + e.getMessage());
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