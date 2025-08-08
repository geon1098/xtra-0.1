package com.mysite.xtra.resume;

import com.mysite.xtra.guin.Working;
import com.mysite.xtra.offer.Offer;
import com.mysite.xtra.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    
    // 구인자가 받은 이력서 목록 조회
    List<Resume> findByReceiverOrderBySendDateDesc(SiteUser receiver);
    
    // 구직자가 보낸 이력서 목록 조회
    List<Resume> findBySenderOrderBySendDateDesc(SiteUser sender);
    
    // 특정 구인 게시글에 대한 이력서 목록 조회
    List<Resume> findByWorkingOrderBySendDateDesc(Working working);
    
    // 구인자가 받은 읽지 않은 이력서 개수 조회
    @Query("SELECT COUNT(r) FROM Resume r WHERE r.receiver = :receiver AND r.isRead = false")
    long countUnreadResumesByReceiver(@Param("receiver") SiteUser receiver);
    
    // 특정 구인 게시글에 대한 이력서 개수 조회
    @Query("SELECT COUNT(r) FROM Resume r WHERE r.working = :working")
    long countResumesByWorking(@Param("working") Working working);
    
    // 구직자가 특정 구인 게시글에 이미 이력서를 보냈는지 확인
    @Query("SELECT COUNT(r) > 0 FROM Resume r WHERE r.sender = :sender AND r.working = :working")
    boolean existsBySenderAndWorking(@Param("sender") SiteUser sender, @Param("working") Working working);
    
    // 특정 오퍼 게시글에 대한 이력서 목록 조회
    List<Resume> findByOfferOrderBySendDateDesc(Offer offer);
    
    // 특정 오퍼 게시글에 대한 이력서 개수 조회
    @Query("SELECT COUNT(r) FROM Resume r WHERE r.offer = :offer")
    long countResumesByOffer(@Param("offer") Offer offer);
    
    // 구직자가 특정 오퍼 게시글에 이미 이력서를 보냈는지 확인
    @Query("SELECT COUNT(r) > 0 FROM Resume r WHERE r.sender = :sender AND r.offer = :offer")
    boolean existsBySenderAndOffer(@Param("sender") SiteUser sender, @Param("offer") Offer offer);
} 