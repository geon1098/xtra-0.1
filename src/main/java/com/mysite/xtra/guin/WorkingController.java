package com.mysite.xtra.guin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.xtra.user.SiteUser;
import com.mysite.xtra.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mysite.xtra.offer.OfferService;
import com.mysite.xtra.offer.Offer;
import com.mysite.xtra.config.KakaoProperties;
import com.mysite.xtra.api.MapLocation;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Principal;
import lombok.RequiredArgsConstructor;

@RequestMapping("/work")
@RequiredArgsConstructor
@Controller
public class WorkingController {
	
	private final WorkingService workingService;
	private final UserService userService;
	private final OfferService offerService;
	private final KakaoProperties kakaoProperties;
	private static final Logger logger = LoggerFactory.getLogger(WorkingController.class);
	
	@GetMapping("/info")
	public String workInfo() {
		return "work_info";
	}
	
	@GetMapping("/list")
	public String listWorkings(Model model, @RequestParam(value="page", defaultValue="0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw) {
		Page<Working> paging = this.workingService.getPageList(page, kw);
		model.addAttribute("paging",paging);
		// 오퍼(프리미엄, 익스퍼트, VIP) 리스트 추가 - 승인된 게시글만 (임시 주석 처리)
		model.addAttribute("premiumOffers", offerService.getOffersByCategory(Offer.OfferCategory.PREMIUM));
		model.addAttribute("expertOffers", offerService.getOffersByCategory(Offer.OfferCategory.EXPERT));
		model.addAttribute("vipOffers", offerService.getOffersByCategory(Offer.OfferCategory.VIP));
		// model.addAttribute("premiumOffers", offerService.getOffersByCategory(Offer.OfferCategory.PREMIUM).stream()
		// 	.filter(offer -> offer.getApprovalStatus() == Offer.ApprovalStatus.APPROVED)
		// 	.collect(java.util.stream.Collectors.toList()));
		// model.addAttribute("expertOffers", offerService.getOffersByCategory(Offer.OfferCategory.EXPERT).stream()
		// 	.filter(offer -> offer.getApprovalStatus() == Offer.ApprovalStatus.APPROVED)
		// 	.collect(java.util.stream.Collectors.toList()));
		// model.addAttribute("vipOffers", offerService.getOffersByCategory(Offer.OfferCategory.VIP).stream()
		// 	.filter(offer -> offer.getApprovalStatus() == Offer.ApprovalStatus.APPROVED)
		// 	.collect(java.util.stream.Collectors.toList()));
		System.out.println("✅ work_list.html 렌더링됨!");
		return "work_list";
	}
	
	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable("id") Long id) {
		Working working = this.workingService.getWorking(id);
		model.addAttribute("working", working);
		model.addAttribute("kakaoAppKey", kakaoProperties.getJavascriptAppKey());
		return "work_detail";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String workCreateForm(Model model) {
		model.addAttribute("workingForm", new WorkingForm());
		model.addAttribute("action", "/work/create");
		model.addAttribute("offer", new Offer());
		return "work_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String workCreate(@Valid @ModelAttribute("workingForm") WorkingForm workingForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		if (bindingResult.hasErrors()) {
			return "work_form";
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		SiteUser currentUser = userService.getUser(auth.getName());
		
		workingService.createWorking(workingForm, currentUser);
		redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 등록되었습니다.");
		return "redirect:/work/list";
	}

	@GetMapping("/detail/create")
	public String workRequest() {
		return "work_request.html";
	}

	@GetMapping("/chat/{id}")
	public String chatRoom(Model model, @PathVariable("id") Long id, Authentication authentication, HttpServletRequest request) throws JsonProcessingException {
		Working working = this.workingService.getWorking(id);
		model.addAttribute("working", working);
		
		// CSRF 토큰 추가
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (token != null) {
			model.addAttribute("_csrf", token);
		}
		
		// 로그인한 사용자 정보 추가
		if (authentication != null && authentication.isAuthenticated()) {
			SiteUser currentUser = userService.getUser(authentication.getName());
			logger.debug("현재 사용자 정보: {}", currentUser);
			model.addAttribute("currentUser", currentUser);
			// JSON 문자열로 변환하여 추가
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 지원
			String currentUserJson = currentUser != null ? objectMapper.writeValueAsString(currentUser) : "null";
			logger.debug("현재 사용자 JSON: {}", currentUserJson);
			model.addAttribute("currentUserJson", currentUserJson);
		} else {
			logger.debug("인증되지 않은 사용자");
			model.addAttribute("currentUser", null);
			model.addAttribute("currentUserJson", "null");
		}
		
		return "work_chat";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/edit/{id}")
	public String workEditForm(@PathVariable("id") Long id, Model model, Principal principal) {
		Working working = workingService.getWorking(id);
		if (!working.getAuthor().getUsername().equals(principal.getName())) {
			throw new RuntimeException("수정 권한이 없습니다.");
		}
		WorkingForm form = new WorkingForm();
		form.setId(working.getId());
		form.setSiteName(working.getSiteName());
		form.setTitle(working.getTitle());
		form.setCategory(working.getCategory());
		form.setJobContent(working.getJobContent());
		form.setJobType(working.getJobType());
		form.setBenefits(working.getBenefits());
		form.setLocation(working.getLocation());
		form.setJobDescription(working.getJobDescription());
		form.setJobWork(working.getJobWork());
		form.setDeadDate(working.getDeadDate());
		form.setWorkNumber(working.getWorkNumber());
		form.setGender(working.getGender());
		form.setAge(working.getAge());
		form.setAddress(working.getAddress());
		form.setMapLocation(working.getMapLocation());
		form.setJobDetails(working.getJobDetails());
		form.setCPerson(working.getCPerson());
		form.setPhone(working.getPhone());
		model.addAttribute("workingForm", form);
		model.addAttribute("action", "/work/edit/" + id);
		model.addAttribute("offer", new Offer());
		return "work_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/edit/{id}")
	public String workEdit(@PathVariable("id") Long id, @Valid @ModelAttribute("workingForm") WorkingForm workingForm, BindingResult bindingResult, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "work_form";
		}
		Working working = workingService.getWorking(id);
		if (!working.getAuthor().getUsername().equals(principal.getName())) {
			throw new RuntimeException("수정 권한이 없습니다.");
		}
		workingService.updateWorking(working, workingForm);
		return "redirect:/work/detail/" + id;
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete/{id}")
	public String workDelete(@PathVariable("id") Long id, Principal principal) {
		Working working = workingService.getWorking(id);
		if (!working.getAuthor().getUsername().equals(principal.getName())) {
			throw new RuntimeException("삭제 권한이 없습니다.");
		}
		workingService.deleteWorking(working);
		return "redirect:/work/list";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String workingUpdate(WorkingForm form, @PathVariable("id") Long id, Principal principal) {
		// ... (validation and authorization)
		
		Working working = this.workingService.getWorking(id);
		
		// ... (authorization check)

		this.workingService.update(working, form);
		return String.format("redirect:/work/detail/%s", id);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String workingModify(WorkingForm form, @PathVariable("id") Long id, Principal principal) {
		Working working = this.workingService.getWorking(id);
		if(!working.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		form.setSiteName(working.getSiteName());
		form.setTitle(working.getTitle());
		// ... (setting other form fields)
		form.setAddress(working.getAddress());

		if (working.getMapLocation() != null) {
			form.setMapLocation(working.getMapLocation());
		} else {
			form.setMapLocation(new MapLocation());
		}

		form.setJobDetails(working.getJobDetails());
		form.setCPerson(working.getCPerson());
		form.setPhone(working.getPhone());
		return "work_form";
	}
}
