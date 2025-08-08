package com.mysite.xtra.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByToken(String token);
    Optional<EmailVerification> findByEmail(String email);
    
    // 사용자별 이메일 인증 토큰 삭제
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.user = :user")
    void deleteByUser(@Param("user") SiteUser user);
} 