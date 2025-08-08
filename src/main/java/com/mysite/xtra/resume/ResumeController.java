package com.mysite.xtra.resume;

import com.mysite.xtra.DataNotFoundException;
import com.mysite.xtra.guin.Working;
import com.mysite.xtra.guin.WorkingService;
import com.mysite.xtra.offer.Offer;
import com.mysite.xtra.offer.OfferService;
import com.mysite.xtra.user.SiteUser;
import com.mysite.xtra.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/resume")
public class ResumeController {
    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);
    
    private final ResumeService resumeService;
    private final UserService userService;
    private final WorkingService workingService;
    private final OfferService offerService;
    
    @Autowired
    public ResumeController(ResumeService resumeService, UserService userService, WorkingService workingService, OfferService offerService) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.workingService = workingService;
        this.offerService = offerService;
    }
    
    // 이력서 전송 폼 페이지
    @GetMapping("/send/{workingId}")
    public String sendResumeForm(@PathVariable Long workingId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Working working = workingService.getWorking(workingId);
            
            // 이미 이력서를 보냈는지 확인
            boolean alreadySent = resumeService.hasAlreadySentResume(username, workingId);
            
            model.addAttribute("working", working);
            model.addAttribute("alreadySent", alreadySent);
            
            return "resume_send_form";
        } catch (DataNotFoundException e) {
            return "redirect:/work/list";
        }
    }
    
    // 이력서 전송 처리
    @PostMapping("/send/{workingId}")
    public String sendResume(@PathVariable Long workingId, 
                           @RequestParam("file") MultipartFile file,
                           @RequestParam(value = "message", required = false) String message,
                           RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Resume resume = resumeService.sendResume(workingId, username, file, message);
            
            redirectAttributes.addFlashAttribute("successMessage", "이력서가 성공적으로 전송되었습니다.");
            return "redirect:/work/detail/" + workingId;
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/resume/send/" + workingId;
        } catch (IOException e) {
            logger.error("이력서 파일 처리 중 오류", e);
            redirectAttributes.addFlashAttribute("errorMessage", "파일 처리 중 오류가 발생했습니다.");
            return "redirect:/resume/send/" + workingId;
        } catch (DataNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "요청한 데이터를 찾을 수 없습니다.");
            return "redirect:/work/list";
        }
    }
    
    // 오퍼에 이력서 전송 폼 페이지
    @GetMapping("/send/offer/{offerId}")
    public String sendResumeToOfferForm(@PathVariable Long offerId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Offer offer = offerService.getOffer(offerId).orElseThrow(() -> new DataNotFoundException("오퍼를 찾을 수 없습니다."));
            
            // 이미 이력서를 보냈는지 확인
            boolean alreadySent = resumeService.hasAlreadySentResumeToOffer(username, offerId);
            
            model.addAttribute("offer", offer);
            model.addAttribute("alreadySent", alreadySent);
            
            return "resume_send_form";
        } catch (DataNotFoundException e) {
            return "redirect:/work/list";
        }
    }
    
    // 오퍼에 이력서 전송 처리
    @PostMapping("/send/offer/{offerId}")
    public String sendResumeToOffer(@PathVariable Long offerId, 
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "message", required = false) String message,
                                   RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Resume resume = resumeService.sendResumeToOffer(offerId, username, file, message);
            
            redirectAttributes.addFlashAttribute("successMessage", "이력서가 성공적으로 전송되었습니다.");
            return "redirect:/offer/detail/" + offerId;
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/resume/send/offer/" + offerId;
        } catch (IOException e) {
            logger.error("이력서 파일 처리 중 오류", e);
            redirectAttributes.addFlashAttribute("errorMessage", "파일 처리 중 오류가 발생했습니다.");
            return "redirect:/resume/send/offer/" + offerId;
        } catch (DataNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "요청한 데이터를 찾을 수 없습니다.");
            return "redirect:/work/list";
        }
    }
    
    // 받은 이력서 목록
    @GetMapping("/received")
    public String receivedResumes(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            List<Resume> resumes = resumeService.getReceivedResumes(username);
            long unreadCount = resumeService.getUnreadResumeCount(username);
            
            model.addAttribute("resumes", resumes);
            model.addAttribute("unreadCount", unreadCount);
            
            return "resume_received_list";
        } catch (DataNotFoundException e) {
            return "redirect:/";
        }
    }
    
    // 보낸 이력서 목록
    @GetMapping("/sent")
    public String sentResumes(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            List<Resume> resumes = resumeService.getSentResumes(username);
            
            model.addAttribute("resumes", resumes);
            
            return "resume_sent_list";
        } catch (DataNotFoundException e) {
            return "redirect:/";
        }
    }
    
    // 이력서 상세 보기
    @GetMapping("/detail/{resumeId}")
    public String resumeDetail(@PathVariable Long resumeId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Resume resume = resumeService.getResume(resumeId);
            SiteUser currentUser = userService.getUser(username);
            
            // 권한 확인 (발신자 또는 수신자만 접근 가능)
            if (!resume.getSender().getId().equals(currentUser.getId()) && 
                !resume.getReceiver().getId().equals(currentUser.getId())) {
                return "redirect:/";
            }
            
            // 수신자가 보는 경우 읽음 처리
            if (resume.getReceiver().getId().equals(currentUser.getId()) && !resume.isRead()) {
                resumeService.markAsRead(resumeId);
            }
            
            model.addAttribute("resume", resume);
            model.addAttribute("isReceiver", resume.getReceiver().getId().equals(currentUser.getId()));
            
            return "resume_detail";
        } catch (DataNotFoundException e) {
            return "redirect:/";
        }
    }
    
    // 이력서 파일 다운로드
    @GetMapping("/download/{resumeId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long resumeId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Resume resume = resumeService.getResume(resumeId);
            SiteUser currentUser = userService.getUser(username);
            
            // 권한 확인 (발신자 또는 수신자만 다운로드 가능)
            if (!resume.getSender().getId().equals(currentUser.getId()) && 
                !resume.getReceiver().getId().equals(currentUser.getId())) {
                return ResponseEntity.notFound().build();
            }
            
            File file = resumeService.getResumeFile(resumeId);
            Resource resource = new FileSystemResource(file);
            
            // 파일명 인코딩
            String encodedFileName = URLEncoder.encode(resume.getOriginalFileName(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename*=UTF-8''" + encodedFileName)
                    .body(resource);
                    
        } catch (Exception e) {
            logger.error("이력서 다운로드 중 오류", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    // 이력서 삭제
    @PostMapping("/delete/{resumeId}")
    public String deleteResume(@PathVariable Long resumeId, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            resumeService.deleteResume(resumeId, username);
            
            redirectAttributes.addFlashAttribute("successMessage", "이력서가 삭제되었습니다.");
            return "redirect:/resume/sent";
            
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/resume/detail/" + resumeId;
        } catch (DataNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "이력서를 찾을 수 없습니다.");
            return "redirect:/resume/sent";
        }
    }
    
    // 구인 게시글별 이력서 목록 (구인자용)
    @GetMapping("/working/{workingId}")
    public String resumesByWorking(@PathVariable Long workingId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Working working = workingService.getWorking(workingId);
            SiteUser currentUser = userService.getUser(username);
            
            // 구인자만 접근 가능
            if (!working.getAuthor().getId().equals(currentUser.getId())) {
                return "redirect:/work/list";
            }
            
            List<Resume> resumes = resumeService.getResumesByWorking(workingId);
            
            model.addAttribute("working", working);
            model.addAttribute("resumes", resumes);
            
            return "resume_working_list";
        } catch (DataNotFoundException e) {
            return "redirect:/work/list";
        }
    }
} 