package com.mysite.xtra.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public List<Offer> getOffersByCategory(Offer.OfferCategory category) {
        return offerRepository.findByCategory(category);
    }

    public Optional<Offer> getOffer(Long id) {
        return offerRepository.findById(id);
    }

    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }

    public void delete(Long id) {
        offerRepository.deleteById(id);
    }
} 