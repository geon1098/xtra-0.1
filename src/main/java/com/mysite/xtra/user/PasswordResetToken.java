package com.mysite.xtra.user;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser user;

    private LocalDateTime expiryDate;

    private boolean used;

    public static PasswordResetToken createPasswordResetToken(SiteUser user) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(generateToken());
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // 1시간 유효
        resetToken.setUsed(false);
        return resetToken;
    }

    private static String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
} 