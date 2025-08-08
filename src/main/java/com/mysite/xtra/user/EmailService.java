package com.mysite.xtra.user;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(String to, String token) {
        try {
            logger.info("HTML 이메일 인증 메일 발송 시작: {}", to);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("이메일 인증을 완료해주세요");
            
            // Thymeleaf 템플릿을 사용하여 HTML 이메일 생성
            Context context = new Context();
            context.setVariable("token", token);
            String htmlContent = templateEngine.process("email/verification-email", context);
            
            logger.info("HTML 이메일 내용 생성 완료. 길이: {}", htmlContent.length());
            logger.debug("HTML 이메일 내용: {}", htmlContent);
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            logger.info("HTML 이메일 인증 메일 발송 완료: {}", to);
        } catch (MessagingException e) {
            logger.error("HTML 이메일 발송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        } catch (Exception e) {
            logger.error("이메일 템플릿 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이메일 템플릿 처리에 실패했습니다.", e);
        }
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
        try {
            logger.info("HTML 비밀번호 재설정 메일 발송 시작: {}", to);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("비밀번호 재설정");
            
            // Thymeleaf 템플릿을 사용하여 HTML 이메일 생성
            Context context = new Context();
            context.setVariable("token", token);
            String htmlContent = templateEngine.process("email/password-reset-email", context);
            
            logger.info("HTML 비밀번호 재설정 이메일 내용 생성 완료. 길이: {}", htmlContent.length());
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            logger.info("HTML 비밀번호 재설정 메일 발송 완료: {}", to);
        } catch (MessagingException e) {
            logger.error("HTML 비밀번호 재설정 이메일 발송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        } catch (Exception e) {
            logger.error("비밀번호 재설정 이메일 템플릿 처리 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이메일 템플릿 처리에 실패했습니다.", e);
        }
    }
} 