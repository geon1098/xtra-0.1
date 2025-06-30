package com.mysite.xtra.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public List<Offer> getOffersByCategory(Offer.OfferCategory category) {
        // 승인된 오퍼만 반환
        return offerRepository.findByCategory(category).stream()
            .filter(offer -> offer.getApprovalStatus() == Offer.ApprovalStatus.APPROVED)
            .toList();
    }

    public Optional<Offer> getOffer(Long id) {
        return offerRepository.findById(id);
    }

    public void save(Offer offer) {
        offerRepository.save(offer);
    }

    public void delete(Long id) {
        offerRepository.deleteById(id);
    }

    // 승인 대기중인 게시글 조회
    public List<Offer> getPendingOffers() {
        return offerRepository.findByApprovalStatus(Offer.ApprovalStatus.PENDING);
    }

    // 승인된 게시글만 조회
    public List<Offer> getApprovedOffers() {
        return offerRepository.findByApprovalStatus(Offer.ApprovalStatus.APPROVED);
    }

    // 승인 상태별 조회
    public List<Offer> getOffersByApprovalStatus(Offer.ApprovalStatus status) {
        return offerRepository.findByApprovalStatus(status);
    }

    // 게시글 승인
    public void approveOffer(Long id) {
        Optional<Offer> offerOpt = offerRepository.findById(id);
        if (offerOpt.isPresent()) {
            Offer offer = offerOpt.get();
            offer.setApprovalStatus(Offer.ApprovalStatus.APPROVED);
            offerRepository.save(offer);
        }
    }

    // 게시글 거절
    public void rejectOffer(Long id) {
        Optional<Offer> offerOpt = offerRepository.findById(id);
        if (offerOpt.isPresent()) {
            Offer offer = offerOpt.get();
            offer.setApprovalStatus(Offer.ApprovalStatus.REJECTED);
            offerRepository.save(offer);
        }
    }

    // 사용자별 게시글 조회
    public List<Offer> getOffersByAuthorId(Long authorId) {
        return offerRepository.findByAuthorId(authorId);
    }
} 