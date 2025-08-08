package com.mysite.xtra.user;

import java.security.Principal;
import java.util.List;
import java.io.File;
import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import com.mysite.xtra.guin.Working;
import com.mysite.xtra.guin.WorkingRepository;
import com.mysite.xtra.gujic.Jobing;
import com.mysite.xtra.gujic.JobingRepository;
import com.mysite.xtra.offer.Offer;
import com.mysite.xtra.offer.OfferRepository;

@Controller
@RequestMapping("/mypage")
@PreAuthorize("isAuthenticated()")
public class MyPageController {

    private final UserService userService;
    private final WorkingRepository workingRepository;
    private final JobingRepository jobingRepository;
    private final PasswordEncoder passwordEncoder;
    private final OfferRepository offerRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;

    public MyPageController(UserService userService, WorkingRepository workingRepository, 
                          JobingRepository jobingRepository, PasswordEncoder passwordEncoder, OfferRepository offerRepository) {
        this.userService = userService;
        this.workingRepository = workingRepository;
        this.jobingRepository = jobingRepository;
        this.passwordEncoder = passwordEncoder;
        this.offerRepository = offerRepository;
    }

    @GetMapping("")
    public String myPage(Principal principal, Model model) {
        SiteUser user = userService.getUser(principal.getName());
        
        // 사용자 기본 정보
        model.addAttribute("user", user);
        
        // 내가 쓴 구인글 개수
        long workingCount = workingRepository.countByAuthor(user);
        model.addAttribute("workingCount", workingCount);
        
        // 내가 쓴 구직글 개수
        long jobingCount = jobingRepository.countByAuthor(user);
        model.addAttribute("jobbingCount", jobingCount);
        
        // 최근 구인글 5개
        List<Working> recentWorkings = workingRepository.findTop5ByAuthorOrderByCreateDateDesc(user);
        model.addAttribute("recentWorkings", recentWorkings);
        
        // 최근 구직글 5개
        List<Jobing> recentJobings = jobingRepository.findTop5ByAuthorOrderByCreateDateDesc(user);
        model.addAttribute("recentJobings", recentJobings);
        
        // 전체 구인글
        Pageable allPageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Working> allWorkings = workingRepository.findByAuthorOrderByCreateDateDesc(user, allPageable).getContent();
        model.addAttribute("allWorkings", allWorkings);
        // 전체 구직글
        List<Jobing> allJobings = jobingRepository.findByAuthorOrderByCreateDateDesc(user, allPageable).getContent();
        model.addAttribute("allJobings", allJobings);
        
        // 내 유료 구인글(프리미엄/VIP/익스퍼트)
        List<Offer> myOffers = offerRepository.findByAuthorOrderByCreateDateDesc(user);
        model.addAttribute("myOffers", myOffers);
        
        return "mypage";
    }

    @GetMapping("/workings")
    public String myWorkings(Principal principal, Model model, 
                           @RequestParam(defaultValue = "0") int page) {
        SiteUser user = userService.getUser(principal.getName());
        
        Pageable pageable = PageRequest.of(page, 10);
        Page<Working> workings = workingRepository.findByAuthorOrderByCreateDateDesc(user, pageable);
        
        model.addAttribute("workings", workings);
        model.addAttribute("user", user);
        
        return "mypage_workings";
    }

    @GetMapping("/jobings")
    public String myJobings(Principal principal, Model model, 
                          @RequestParam(defaultValue = "0") int page) {
        SiteUser user = userService.getUser(principal.getName());
        
        Pageable pageable = PageRequest.of(page, 10);
        Page<Jobing> jobings = jobingRepository.findByAuthorOrderByCreateDateDesc(user, pageable);
        
        model.addAttribute("jobings", jobings);
        model.addAttribute("user", user);
        
        return "mypage_jobings";
    }

    @GetMapping("/payments")
    public String myPayments(Principal principal, Model model) {
        SiteUser user = userService.getUser(principal.getName());
        
        // TODO: 결제 내역 구현 (현재는 더미 데이터)
        model.addAttribute("user", user);
        model.addAttribute("payments", List.of()); // 빈 리스트로 시작
        
        return "mypage_payments";
    }

    @GetMapping("/profile")
    public String editProfile(Principal principal, Model model) {
        SiteUser user = userService.getUser(principal.getName());
        model.addAttribute("user", user);
        return "mypage_profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Principal principal, 
                              @RequestParam String nickname,
                              @RequestParam String email,
                              @RequestParam String phone,
                              RedirectAttributes redirectAttributes) {
        try {
            SiteUser user = userService.getUser(principal.getName());
            user.setNickname(nickname);
            user.setEmail(email);
            user.setPhone(phone);
            
            userService.save(user);
            
            redirectAttributes.addFlashAttribute("message", "회원정보가 성공적으로 수정되었습니다.");
            return "redirect:/mypage/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원정보 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/mypage/profile";
        }
    }

    @GetMapping("/password")
    public String changePassword(Principal principal, Model model) {
        SiteUser user = userService.getUser(principal.getName());
        model.addAttribute("user", user);
        return "mypage_password";
    }

    @PostMapping("/password/update")
    public String updatePassword(Principal principal,
                               @RequestParam String currentPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        try {
            SiteUser user = userService.getUser(principal.getName());
            
            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
                return "redirect:/mypage/password";
            }
            
            // 새 비밀번호 확인
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
                return "redirect:/mypage/password";
            }
            
            // 새 비밀번호가 현재 비밀번호와 같은지 확인
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "새 비밀번호는 현재 비밀번호와 달라야 합니다.");
                return "redirect:/mypage/password";
            }
            
            // 비밀번호 변경
            userService.updatePassword(user, newPassword);
            
            redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
            return "redirect:/mypage/password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "비밀번호 변경에 실패했습니다: " + e.getMessage());
            return "redirect:/mypage/password";
        }
    }

    @PostMapping("/profile-image")
    public String uploadProfileImage(Principal principal, @RequestParam("profileImage") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir + fileName);
                file.transferTo(dest);

                // 사용자 정보에 이미지 경로 저장
                SiteUser user = userService.getUser(principal.getName());
                user.setProfileImageUrl("/images/profile/" + fileName);
                userService.save(user);

                redirectAttributes.addFlashAttribute("profileImageUrl", "/images/profile/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "파일 업로드 실패");
            }
        }
        return "redirect:/mypage/profile";
    }
} 