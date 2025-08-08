package com.mysite.xtra.resume;

import com.mysite.xtra.guin.Working;
import com.mysite.xtra.user.SiteUser;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private SiteUser sender; // 이력서를 보내는 구직자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private SiteUser receiver; // 이력서를 받는 구인자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "working_id")
    private Working working; // 관련 구인 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private com.mysite.xtra.offer.Offer offer; // 관련 오퍼 게시글

    @Column(nullable = false)
    private String originalFileName; // 원본 파일명

    @Column(nullable = false)
    private String storedFileName; // 저장된 파일명

    @Column(nullable = false)
    private String filePath; // 파일 경로

    @Column(nullable = false)
    private Long fileSize; // 파일 크기

    @Column(nullable = false)
    private String contentType; // 파일 타입

    @Column(columnDefinition = "TEXT")
    private String message; // 이력서와 함께 보내는 메시지

    @Column(nullable = false)
    private LocalDateTime sendDate; // 전송일시

    @Column(nullable = false)
    private boolean isRead = false; // 읽음 여부

    @PrePersist
    protected void onCreate() {
        sendDate = LocalDateTime.now();
    }
} 