package com.mysite.xtra.chat;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mysite.xtra.user.SiteUser;
import com.mysite.xtra.guin.Working;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    public ChatService(ChatMessageRepository chatMessageRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }
    
    @Transactional
    public ChatMessage saveMessage(ChatMessage chatMessage, SiteUser user, Working working) {
        logger.info("💬 [ChatService] 메시지 저장 시작 - content={}, senderId={}, receiverId={}, roomId={}, user={}, working={}",
            chatMessage.getContent(), chatMessage.getSenderId(), chatMessage.getReceiverId(), 
            chatMessage.getRoomId(), user.getUsername(), working.getId());

        try {
            // 메시지 정보 설정
            chatMessage.setUser(user);
            chatMessage.setWorking(working);
            
            // 메시지 저장 전 상태 로깅
            logger.info("💬 [ChatService] 메시지 저장 전 상태 - {}", chatMessage);
            
            // 메시지 저장
            ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
            logger.info("💬 [ChatService] 메시지 저장 완료 - ID: {}, 저장된 메시지: {}", 
                savedMessage.getId(), savedMessage);

            // 발신자에게 메시지 전송
            String senderDestination = "/user/" + chatMessage.getSenderId() + "/queue/messages";
            logger.info("💬 [ChatService] 발신자({})에게 메시지 전송 시도 - destination: {}", 
                chatMessage.getSenderId(), senderDestination);
            messagingTemplate.convertAndSendToUser(
                chatMessage.getSenderId().toString(),
                "/queue/messages",
                savedMessage
            );
            logger.info("💬 [ChatService] 발신자에게 메시지 전송 완료");

            // 수신자에게 메시지 전송
            if (chatMessage.getReceiverId() != null) {
                String receiverDestination = "/user/" + chatMessage.getReceiverId() + "/queue/messages";
                logger.info("💬 [ChatService] 수신자({})에게 메시지 전송 시도 - destination: {}", 
                    chatMessage.getReceiverId(), receiverDestination);
                messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId().toString(),
                    "/queue/messages",
                    savedMessage
                );
                logger.info("💬 [ChatService] 수신자에게 메시지 전송 완료");
            }

            logger.info("💬 [ChatService] 메시지 처리 완료 - ID: {}", savedMessage.getId());
            return savedMessage;
        } catch (Exception e) {
            logger.error("💬 [ChatService] 메시지 저장 실패 - 오류: {}", e.getMessage(), e);
            throw new RuntimeException("메시지 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public List<ChatMessage> getChatHistory(Long roomId) {
        logger.info("💬 [ChatService] 채팅 히스토리 조회 시작 - roomId: {}", roomId);
        
        try {
            List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreateDateAsc(roomId);
            logger.info("💬 [ChatService] 채팅 히스토리 조회 완료 - {}개 메시지", messages.size());
            return messages;
        } catch (Exception e) {
            logger.error("💬 [ChatService] 채팅 히스토리 조회 중 오류: {}", e.getMessage(), e);
            throw new RuntimeException("채팅 히스토리를 불러오는 중 오류가 발생했습니다.", e);
        }
    }
    
    public List<ChatMessage> getChatHistoryBetweenUsers(Long senderId, Long receiverId) {
        logger.info("💬 [ChatService] 사용자 간 채팅 기록 조회 - senderId: {}, receiverId: {}", 
            senderId, receiverId);
        List<ChatMessage> messages = chatMessageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        logger.info("💬 [ChatService] 사용자 간 채팅 기록 조회 완료 - 메시지 수: {}", messages.size());
        return messages;
    }
} 