package com.mysite.xtra.chat;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mysite.xtra.guin.Working;
import com.mysite.xtra.guin.WorkingService;
import com.mysite.xtra.user.SiteUser;
import com.mysite.xtra.user.UserService;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.mysite.xtra.DataNotFoundException;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final UserService userService;
    private final WorkingService workingService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    public ChatController(ChatService chatService, UserService userService, 
                         WorkingService workingService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.workingService = workingService;
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        logger.info("🔔 [ChatController] 메시지 수신: {}, 세션: {}", 
            chatMessage, headerAccessor.getSessionId());

        Principal principal = headerAccessor.getUser();
        if (principal == null) {
            logger.error("🔔 [ChatController] 인증 정보가 없습니다. 세션: {}", 
                headerAccessor.getSessionId());
            throw new RuntimeException("인증이 필요합니다.");
        }

        String username = principal.getName();
        logger.info("🔔 [ChatController] 인증된 사용자: {}, 세션: {}", 
            username, headerAccessor.getSessionId());

        try {
            SiteUser currentUser = userService.getUser(username);
            if (currentUser == null) {
                logger.error("🔔 [ChatController] 사용자를 찾을 수 없습니다: {}, 세션: {}", 
                    username, headerAccessor.getSessionId());
                throw new RuntimeException("사용자를 찾을 수 없습니다.");
            }

            Working working = workingService.getWorking(chatMessage.getRoomId());
            if (working == null) {
                logger.error("🔔 [ChatController] 채팅방을 찾을 수 없습니다: {}, 세션: {}", 
                    chatMessage.getRoomId(), headerAccessor.getSessionId());
                throw new RuntimeException("채팅방을 찾을 수 없습니다.");
            }

            // 발신자 ID 설정 및 검증
            if (!currentUser.getId().equals(chatMessage.getSenderId())) {
                logger.error("🔔 [ChatController] 발신자 ID 불일치 - 요청: {}, 실제: {}, 세션: {}", 
                    chatMessage.getSenderId(), currentUser.getId(), headerAccessor.getSessionId());
                throw new RuntimeException("잘못된 발신자 정보입니다.");
            }

            // 메시지 저장 및 전송
            ChatMessage savedMessage = chatService.saveMessage(chatMessage, currentUser, working);
            logger.info("🔔 [ChatController] 메시지 전송 성공 - ID: {}, 세션: {}", 
                savedMessage.getId(), headerAccessor.getSessionId());
        } catch (DataNotFoundException e) {
            logger.error("🔔 [ChatController] 데이터를 찾을 수 없음: {}, 세션: {}", 
                e.getMessage(), headerAccessor.getSessionId());
            throw new RuntimeException("요청한 데이터를 찾을 수 없습니다: " + e.getMessage());
        } catch (Exception e) {
            logger.error("🔔 [ChatController] 메시지 처리 중 오류: {}, 세션: {}", 
                e.getMessage(), headerAccessor.getSessionId());
            throw new RuntimeException("메시지 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @GetMapping("/chat/history/{roomId}")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@PathVariable Long roomId) {
        logger.info("🔔 [ChatController] 채팅 기록 요청 - roomId: {}", roomId);
        List<ChatMessage> messages = chatService.getChatHistory(roomId);
        logger.info("🔔 [ChatController] 채팅 기록 조회 완료 - 메시지 수: {}", messages.size());
        return messages;
    }
    
    @GetMapping("/chat/history/{senderId}/{receiverId}")
    @ResponseBody
    public List<ChatMessage> getChatHistoryBetweenUsers(
            @PathVariable Long senderId,
            @PathVariable Long receiverId) {
        logger.info("🔔 [ChatController] 사용자 간 채팅 기록 요청 - senderId: {}, receiverId: {}", 
            senderId, receiverId);
        List<ChatMessage> messages = chatService.getChatHistoryBetweenUsers(senderId, receiverId);
        logger.info("🔔 [ChatController] 사용자 간 채팅 기록 조회 완료 - 메시지 수: {}", 
            messages.size());
        return messages;
    }

    @MessageMapping("/chat.history")
    public void loadChatHistory(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        logger.info("💬 [ChatController] 채팅 히스토리 요청 수신: {}, 세션: {}", payload, headerAccessor.getSessionId());
        
        try {
            Long roomId = Long.valueOf(payload.get("roomId").toString());
            Long userId = Long.valueOf(payload.get("userId").toString());
            
            // 채팅방 메시지 조회
            List<ChatMessage> messages = chatService.getChatHistory(roomId);
            logger.info("💬 [ChatController] 채팅 히스토리 조회 결과: {}개 메시지", messages.size());
            
            // 사용자에게 메시지 전송
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                messages
            );
            logger.info("💬 [ChatController] 채팅 히스토리 전송 완료");
        } catch (Exception e) {
            logger.error("🔔 [ChatController] 채팅 히스토리 처리 중 오류: {}", e.getMessage(), e);
        }
    }
} 