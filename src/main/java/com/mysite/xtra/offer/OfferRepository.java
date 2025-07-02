package com.mysite.xtra.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.mysite.xtra.user.SiteUser;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByCategory(Offer.OfferCategory category);
    
    // 승인 상태별 조회 (임시 주석 처리)
    List<Offer> findByApprovalStatus(Offer.ApprovalStatus status);
    
    // 승인된 게시글만 조회 (임시 주석 처리)
    // @Query("SELECT o FROM Offer o WHERE o.approvalStatus = 'APPROVED'")
    // List<Offer> findApprovedOffers();
    
    // 사용자별 게시글 조회
    List<Offer> findByAuthorId(Long authorId);
    
    // 승인 대기중인 게시글 조회 (임시 주석 처리)
    // @Query("SELECT o FROM Offer o WHERE o.approvalStatus = 'PENDING' ORDER BY o.createDate DESC")
    // List<Offer> findPendingOffers();

    List<Offer> findByAuthorOrderByCreateDateDesc(SiteUser author);
} 