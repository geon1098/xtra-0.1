package com.mysite.xtra.chat;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;

import com.mysite.xtra.user.SiteUser;
import com.mysite.xtra.guin.Working;

@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String content;
    private String sender;
    private Long senderId;
    private Long receiverId;
    private String time;
    private Long roomId;
    
    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;
    
    @ManyToOne
    private SiteUser user;
    
    @ManyToOne
    private Working working;

    public ChatMessage() {
        this.time = LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }
    
    public SiteUser getUser() { return user; }
    public void setUser(SiteUser user) { this.user = user; }
    
    public Working getWorking() { return working; }
    public void setWorking(Working working) { this.working = working; }
} 