package com.mysite.xtra.user;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String token;

    private LocalDateTime expiryDate;

    private boolean verified;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser user;

    public static EmailVerification createEmailVerification(String email, SiteUser user) {
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setUser(user);
        verification.setToken(generateToken());
        verification.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24시간 유효
        verification.setVerified(false);
        return verification;
    }

    private static String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
} 