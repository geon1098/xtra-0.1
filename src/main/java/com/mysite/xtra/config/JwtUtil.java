package com.mysite.xtra.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.security.Key;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    // 안전한 키 생성 (256비트 = 32바이트)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expiration = 86400000; // 24시간
    
    // 토큰 블랙리스트 (메모리 기반, 프로덕션에서는 Redis 등 사용 권장)
    private final ConcurrentMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            // 블랙리스트 확인
            if (isTokenBlacklisted(token)) {
                logger.warn("블랙리스트된 토큰 사용 시도: {}", token.substring(0, Math.min(20, token.length())) + "...");
                return false;
            }
            
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.warn("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    public void blacklistToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            Date expiration = claims.getExpiration();
            if (expiration != null) {
                blacklistedTokens.put(token, expiration.getTime());
                logger.info("토큰이 블랙리스트에 추가되었습니다: {}", 
                    token.substring(0, Math.min(20, token.length())) + "...");
            }
        } catch (Exception e) {
            logger.warn("토큰 블랙리스트 추가 실패: {}", e.getMessage());
        }
    }

    public void cleanupExpiredBlacklistedTokens() {
        long now = System.currentTimeMillis();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() < now);
    }
} 