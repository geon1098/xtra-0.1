package com.mysite.xtra;

import com.mysite.xtra.guin.WorkingService;
import com.mysite.xtra.offer.OfferService;
import com.mysite.xtra.property.PropertyService;
import com.mysite.xtra.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private WorkingService workingService;
    @Autowired
    private OfferService offerService;
    @Autowired
    private PropertyService propertyService;

    @GetMapping("/")
    public String root(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", required = false) String kw) {
        // 무료 구인 리스트 (페이징)
        Page<com.mysite.xtra.guin.Working> paging = workingService.getPageList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        // 유료 오퍼(프리미엄) 리스트 - 승인된 오퍼만
        model.addAttribute("premiumOffers", offerService.getApprovedOffers());
        // VIP/익스퍼트 오퍼 (카테고리별)
        model.addAttribute("vipOffers", offerService.getOffersByCategory(Offer.OfferCategory.VIP));
        model.addAttribute("expertOffers", offerService.getOffersByCategory(Offer.OfferCategory.EXPERT));
        // 매물정보 리스트
        model.addAttribute("propertyList", propertyService.getAll());
        return "main_page";
    }
}
