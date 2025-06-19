package com.mysite.xtra.offer;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import com.mysite.xtra.user.SiteUser;

@Entity
@Getter
@Setter
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 카테고리(프리미엄/VIP/익스퍼트)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferCategory category;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser author;

    // 근무지 정보
    private String workPlace;

    // 사업자 정보
    private String bisiInfo;

    // 담당자 이름
    private String perName;

    // 전화번호
    private String phone;

    // 사업지 정보
    private String localInfo;

    // 업종
    private String type;

    // 성별
    private String gender;

    // 경력
    private String career;

    // 직종
    private String bisiClass;

    // 나이
    private String age;

    // 인원
    private String inOne;

    // 급여 형태
    private String pay;

    // 근무후생(복리후생)
    private String afterWork;

    // 이미지 URL
    private String imageUrl;

    // 상세내용
    @Column(columnDefinition = "TEXT")
    private String content;

    // 등록일
    private LocalDateTime createDate;

    public enum OfferCategory {
        PREMIUM, VIP, EXPERT
    }
}