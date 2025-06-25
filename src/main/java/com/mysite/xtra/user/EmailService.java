package com.mysite.xtra.user;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("이메일 인증을 완료해주세요");
        message.setText("아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n\n" +
                "http://localhost:8083/user/verify-email?token=" + token);
        
        mailSender.send(message);
    }
    
    public void sendFindIdEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("아이디 찾기 결과");
        message.setText("요청하신 아이디는 다음과 같습니다:\n\n" +
                "아이디: " + username + "\n\n" +
                "보안을 위해 이메일을 삭제하시기 바랍니다.");
        
        mailSender.send(message);
    }
    
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("비밀번호 재설정");
        message.setText("비밀번호 재설정을 위해 아래 링크를 클릭해주세요:\n\n" +
                "http://localhost:8083/user/reset-password?token=" + token + "\n\n" +
                "이 링크는 1시간 후에 만료됩니다.");
        
        mailSender.send(message);
    }
} 