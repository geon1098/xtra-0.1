package com.mysite.xtra.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import javax.servlet.http.HttpServletRequest;

import com.mysite.xtra.offer.Offer;
import com.mysite.xtra.offer.OfferService;
import com.mysite.xtra.user.SiteUser;
import com.mysite.xtra.user.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final OfferService offerService;
    private final UserService userService;
    
    // 관리자 대시보드
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        // 현재 로그인한 사용자 정보 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            System.out.println("🔐 현재 로그인한 사용자: " + auth.getName());
            System.out.println("🔐 사용자 권한: " + auth.getAuthorities());
        }
        
        List<Offer> pendingOffers = offerService.getPendingOffers();
        List<Offer> approvedOffers = offerService.getApprovedOffers();
        List<Offer> rejectedOffers = offerService.getOffersByApprovalStatus(Offer.ApprovalStatus.REJECTED);
        
        model.addAttribute("pendingOffers", pendingOffers);
        model.addAttribute("approvedOffers", approvedOffers);
        model.addAttribute("rejectedOffers", rejectedOffers);
        model.addAttribute("pendingCount", pendingOffers.size());
        model.addAttribute("approvedCount", approvedOffers.size());
        model.addAttribute("rejectedCount", rejectedOffers.size());
        
        // CSRF 토큰 추가
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        
        return "admin/dashboard";
    }
    
    // 승인 대기 게시글 목록
    @GetMapping("/pending")
    public String pendingOffers(Model model, HttpServletRequest request) {
        List<Offer> pendingOffers = offerService.getPendingOffers();
        model.addAttribute("offers", pendingOffers);
        
        // CSRF 토큰 추가
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "admin/pending_offers";
    }
    
    // 승인된 게시글 목록
    @GetMapping("/approved")
    public String approvedOffers(Model model, HttpServletRequest request) {
        List<Offer> approvedOffers = offerService.getApprovedOffers();
        model.addAttribute("offers", approvedOffers);
        
        // CSRF 토큰 추가
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "admin/approved_offers";
    }
    
    // 거절된 게시글 목록
    @GetMapping("/rejected")
    public String rejectedOffers(Model model, HttpServletRequest request) {
        List<Offer> rejectedOffers = offerService.getOffersByApprovalStatus(Offer.ApprovalStatus.REJECTED);
        model.addAttribute("offers", rejectedOffers);
        
        // CSRF 토큰 추가
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "admin/rejected_offers";
    }
    
    // 게시글 승인
    @PostMapping("/approve/{id}")
    @ResponseBody
    public String approveOffer(@PathVariable Long id) {
        try {
            offerService.approveOffer(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
    
    // 게시글 거절
    @PostMapping("/reject/{id}")
    @ResponseBody
    public String rejectOffer(@PathVariable Long id) {
        try {
            offerService.rejectOffer(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
    
    // 전체 게시글 관리
    @GetMapping("/all-offers")
    public String allOffers(Model model, HttpServletRequest request) {
        List<Offer> allOffers = offerService.getAllOffers();
        model.addAttribute("offers", allOffers);
        
        // CSRF 토큰 추가
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "admin/all_offers";
    }
    
    // 게시글 수정 폼
    @GetMapping("/edit/{id}")
    public String editOfferForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        Offer offer = offerService.getOffer(id).orElseThrow();
        model.addAttribute("offer", offer);
        
        // CSRF 토큰 추가
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "admin/edit_offer";
    }
    
    // 게시글 수정 처리
    @PostMapping("/edit/{id}")
    public String editOffer(@PathVariable Long id, @ModelAttribute Offer offer) {
        Optional<Offer> existingOfferOpt = offerService.getOffer(id);
        if (existingOfferOpt.isPresent()) {
            Offer existingOffer = existingOfferOpt.get();
            // 기존 데이터 유지
            offer.setId(id);
            offer.setCreateDate(existingOffer.getCreateDate());
            offer.setAuthor(existingOffer.getAuthor());
            offer.setApprovalStatus(existingOffer.getApprovalStatus());
            offer.setImageUrl(existingOffer.getImageUrl());
            
            offerService.save(offer);
        }
        return "redirect:/admin/all-offers";
    }
    
    // 게시글 삭제
    @PostMapping("/delete/{id}")
    public String deleteOffer(@PathVariable Long id) {
        offerService.delete(id);
        return "redirect:/admin/all-offers";
    }
} 