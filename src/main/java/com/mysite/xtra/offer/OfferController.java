package com.mysite.xtra.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/offer")
public class OfferController {
    private final OfferService offerService;
    private final com.mysite.xtra.user.UserService userService;
    private final FileUploadService fileUploadService;

    // 카테고리별 리스트
    @GetMapping("/list/{category}")
    public String listByCategory(@PathVariable Offer.OfferCategory category, Model model) {
        List<Offer> offers = offerService.getOffersByCategory(category);
        model.addAttribute("offers", offers);
        model.addAttribute("category", category);
        return "offer_list";
    }

    // 상세
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Offer> offer = offerService.getOffer(id);
        if (offer.isPresent()) {
            model.addAttribute("offer", offer.get());
            
            // 현재 로그인한 사용자 정보 추가
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                com.mysite.xtra.user.SiteUser currentUser = userService.getUser(auth.getName());
                model.addAttribute("currentUser", currentUser);
            }
        }
        return "offer_detail";
    }

    // 등록 폼
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("offer", new Offer());
        model.addAttribute("isEdit", false);
        return "offer_form";
    }

    // 등록 처리 - work_list로 리다이렉트
    @PostMapping("/create")
    public String create(@ModelAttribute Offer offer, @RequestParam("imageFile") MultipartFile imageFile) {
        offer.setCreateDate(LocalDateTime.now());
        
        // 현재 로그인한 사용자를 작성자로 설정
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            com.mysite.xtra.user.SiteUser currentUser = userService.getUser(auth.getName());
            offer.setAuthor(currentUser);
        }
        
        // 이미지 파일 업로드 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileUploadService.uploadFile(imageFile);
                offer.setImageUrl(imageUrl);
            } catch (Exception e) {
                // 이미지 업로드 실패 시 기본 이미지 사용
                offer.setImageUrl("/static/images/default_offer.jpg");
            }
        } else {
            // 이미지가 없으면 기본 이미지 사용
            offer.setImageUrl("/static/images/default_offer.jpg");
        }
        
        offerService.save(offer);
        return "redirect:/work/list";
    }

    // 수정 폼
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Offer> offer = offerService.getOffer(id);
        if (offer.isPresent()) {
            model.addAttribute("offer", offer.get());
            model.addAttribute("isEdit", true);
        }
        return "offer_form";
    }

    // 수정 처리 - work_list로 리다이렉트
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute Offer offer, @RequestParam("imageFile") MultipartFile imageFile) {
        Optional<Offer> existingOffer = offerService.getOffer(id);
        if (existingOffer.isPresent()) {
            Offer existing = existingOffer.get();
            
            // 작성자 권한 확인
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                com.mysite.xtra.user.SiteUser currentUser = userService.getUser(auth.getName());
                if (existing.getAuthor() == null || !existing.getAuthor().getId().equals(currentUser.getId())) {
                    throw new RuntimeException("수정 권한이 없습니다.");
                }
            } else {
                throw new RuntimeException("로그인이 필요합니다.");
            }
            
            // 이미지 파일 업로드 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    // 기존 이미지 파일 삭제
                    if (existing.getImageUrl() != null && !existing.getImageUrl().equals("/static/images/default_offer.jpg")) {
                        fileUploadService.deleteFile(existing.getImageUrl());
                    }
                    
                    // 새 이미지 업로드
                    String imageUrl = fileUploadService.uploadFile(imageFile);
                    offer.setImageUrl(imageUrl);
                } catch (Exception e) {
                    // 이미지 업로드 실패 시 기존 이미지 유지
                    offer.setImageUrl(existing.getImageUrl());
                }
            } else {
                // 이미지가 없으면 기존 이미지 유지
                offer.setImageUrl(existing.getImageUrl());
            }
            
            offer.setId(id);
            offer.setCreateDate(existing.getCreateDate()); // 기존 등록일 유지
            offer.setAuthor(existing.getAuthor()); // 기존 작성자 유지
            offerService.save(offer);
        }
        return "redirect:/work/list";
    }

    // 삭제 - work_list로 리다이렉트
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Optional<Offer> offer = offerService.getOffer(id);
        if (offer.isPresent()) {
            Offer existing = offer.get();
            
            // 작성자 권한 확인
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                com.mysite.xtra.user.SiteUser currentUser = userService.getUser(auth.getName());
                if (existing.getAuthor() == null || !existing.getAuthor().getId().equals(currentUser.getId())) {
                    throw new RuntimeException("삭제 권한이 없습니다.");
                }
            } else {
                throw new RuntimeException("로그인이 필요합니다.");
            }
            
            // 이미지 파일 삭제
            if (existing.getImageUrl() != null && !existing.getImageUrl().equals("/static/images/default_offer.jpg")) {
                fileUploadService.deleteFile(existing.getImageUrl());
            }
            
            offerService.delete(id);
        }
        return "redirect:/work/list";
    }
} 