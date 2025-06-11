package com.mysite.xtra.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class EmailVerificationController {
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserService userService;

    public EmailVerificationController(EmailVerificationRepository emailVerificationRepository,
                                     UserService userService) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.userService = userService;
    }

    @Transactional
    @GetMapping("/user/verify-email")
    public String verifyEmail(@RequestParam String token, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== 인증 컨트롤러 진입: token = " + token);
            EmailVerification verification = emailVerificationRepository.findByToken(token)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 토큰입니다."));
    
            if (verification.isExpired()) {
                redirectAttributes.addFlashAttribute("error", "인증 링크가 만료되었습니다. 다시 시도해주세요.");
                return "redirect:/user/login";
            }
    
            if (verification.isVerified()) {
                redirectAttributes.addFlashAttribute("error", "이미 인증이 완료된 이메일입니다.");
                return "redirect:/user/login";
            }
    
            verification.setVerified(true);
            emailVerificationRepository.save(verification);
    
            SiteUser user = verification.getUser();
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "인증에 실패했습니다. 관리자에게 문의하세요.");
                return "redirect:/user/login";
            }
            user.setEmailVerified(true);
            userService.save(user);
    
            redirectAttributes.addFlashAttribute("success", "이메일 인증이 완료되었습니다. 로그인해주세요.");
            return "redirect:/user/login";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "서버 오류: " + e.getMessage());
            return "redirect:/user/login";
        }
    }
} 