package com.mysite.xtra.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtTokenCleanupScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenCleanupScheduler.class);
    
    private final JwtUtil jwtUtil;
    
    public JwtTokenCleanupScheduler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    // 매시간 실행 (만료된 블랙리스트 토큰 정리)
    @Scheduled(fixedRate = 3600000) // 1시간 = 3600000ms
    public void cleanupExpiredTokens() {
        try {
            logger.info("만료된 JWT 블랙리스트 토큰 정리 시작");
            jwtUtil.cleanupExpiredBlacklistedTokens();
            logger.info("만료된 JWT 블랙리스트 토큰 정리 완료");
        } catch (Exception e) {
            logger.error("JWT 블랙리스트 토큰 정리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
} 