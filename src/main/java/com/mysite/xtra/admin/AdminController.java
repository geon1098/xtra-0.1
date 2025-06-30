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
        List<Offer> pendingOffers = offerService.getPendingOffers();
        List<Offer> approvedOffers = offerService.getApprovedOffers();
        List<Offer> rejectedOffers = offerService.getOffersByApprovalStatus(Offer.ApprovalStatus.REJECTED);
        
        model.addAttribute("pendingOffers", pendingOffers);
        model.addAttribute("approvedOffers", approvedOffers);
        model.addAttribute("rejectedOffers", rejectedOffers);
        model.addAttribute("pendingCount", pendingOffers.size());
        model.addAttribute("approvedCount", approvedOffers.size());
        model.addAttribute("rejectedCount", rejectedOffers.size());
        
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
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        return "admin/pending_offers";
    }
    
    // 승인된 게시글 목록
    @GetMapping("/approved")
    public String approvedOffers(Model model) {
        List<Offer> approvedOffers = offerService.getApprovedOffers();
        model.addAttribute("offers", approvedOffers);
        return "admin/approved_offers";
    }
    
    // 거절된 게시글 목록
    @GetMapping("/rejected")
    public String rejectedOffers(Model model) {
        List<Offer> rejectedOffers = offerService.getOffersByApprovalStatus(Offer.ApprovalStatus.REJECTED);
        model.addAttribute("offers", rejectedOffers);
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
    
    // 모든 게시글 관리
    @GetMapping("/all-offers")
    public String allOffers(Model model) {
        List<Offer> allOffers = offerService.getOffersByApprovalStatus(Offer.ApprovalStatus.APPROVED);
        model.addAttribute("offers", allOffers);
        return "admin/all_offers";
    }
    
    // 게시글 수정 폼 (관리자용)
    @GetMapping("/edit/{id}")
    public String editOfferForm(@PathVariable Long id, Model model) {
        Offer offer = offerService.getOffer(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid offer Id:" + id));
        model.addAttribute("offer", offer);
        model.addAttribute("isEdit", true);
        model.addAttribute("isAdmin", true);
        return "admin/offer_edit_form";
    }
    
    // 게시글 수정 처리 (관리자용)
    @PostMapping("/edit/{id}")
    public String editOffer(@PathVariable Long id, @ModelAttribute Offer offer) {
        Offer existingOffer = offerService.getOffer(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid offer Id:" + id));
        
        // 기존 데이터 유지
        offer.setId(id);
        offer.setCreateDate(existingOffer.getCreateDate());
        offer.setAuthor(existingOffer.getAuthor());
        // offer.setApprovalStatus(existingOffer.getApprovalStatus());
        offer.setImageUrl(existingOffer.getImageUrl());
        
        offerService.save(offer);
        return "redirect:/admin/all-offers";
    }
    
    // 게시글 삭제 (관리자용)
    @PostMapping("/delete/{id}")
    public String deleteOffer(@PathVariable Long id) {
        offerService.delete(id);
        return "redirect:/admin/all-offers";
    }
} 