package com.mysite.xtra.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByCategory(Offer.OfferCategory category);
} 