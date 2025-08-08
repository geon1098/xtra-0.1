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
import com.mysite.xtra.DataNotFoundException;
import com.mysite.xtra.resume.ResumeService;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;



@RequestMapping("/work")
@RequiredArgsConstructor
@Controller
public class WorkingController {
	
	private final WorkingService workingService;
	private final UserService userService;
	private final OfferService offerService;
	private final ResumeService resumeService;
	private final KakaoProperties kakaoProperties;
	private static final Logger logger = LoggerFactory.getLogger(WorkingController.class);
	
    @GetMapping("/list")
    public String listWorkings(Model model,
            @RequestParam(value="page", defaultValue="0") int page,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "experience", required = false) String experience,
            @RequestParam(value = "salary", required = false) String salary) {
		try {
            Page<Working> paging = this.workingService.getPageListWithFilters(page, kw, gender, experience, salary);
			model.addAttribute("paging", paging);
            java.util.Map<String, Object> param = new java.util.HashMap<>();
            if (kw != null) param.put("kw", kw);
            if (gender != null) param.put("gender", gender);
            if (experience != null) param.put("experience", experience);
            if (salary != null) param.put("salary", salary);
            model.addAttribute("param", param);
			
			// 오퍼(프리미엄, 익스퍼트, VIP) 리스트 추가 - 예외 처리 추가
			try {
				model.addAttribute("premiumOffers", offerService.getOffersByCategory(Offer.OfferCategory.PREMIUM));
			} catch (Exception e) {
				logger.error("프리미엄 오퍼 조회 중 오류 발생: " + e.getMessage());
				model.addAttribute("premiumOffers", new java.util.ArrayList<>());
			}
			
			try {
				model.addAttribute("expertOffers", offerService.getOffersByCategory(Offer.OfferCategory.EXPERT));
			} catch (Exception e) {
				logger.error("익스퍼트 오퍼 조회 중 오류 발생: " + e.getMessage());
				model.addAttribute("expertOffers", new java.util.ArrayList<>());
			}
			
			try {
				model.addAttribute("vipOffers", offerService.getOffersByCategory(Offer.OfferCategory.VIP));
			} catch (Exception e) {
				logger.error("VIP 오퍼 조회 중 오류 발생: " + e.getMessage());
				model.addAttribute("vipOffers", new java.util.ArrayList<>());
			}
			
			System.out.println("✅ work_list.html 렌더링됨!");
			return "work_list";
		} catch (Exception e) {
			logger.error("work/list 페이지 로딩 중 오류 발생: " + e.getMessage(), e);
			model.addAttribute("error", "페이지 로딩 중 오류가 발생했습니다.");
			model.addAttribute("paging", null);
			model.addAttribute("premiumOffers", new java.util.ArrayList<>());
			model.addAttribute("expertOffers", new java.util.ArrayList<>());
			model.addAttribute("vipOffers", new java.util.ArrayList<>());
			return "work_list";
		}
	}
	
	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable("id") Long id, Authentication authentication) {
		Working working = this.workingService.getWorking(id);
		model.addAttribute("working", working);
		model.addAttribute("kakaoAppKey", kakaoProperties.getJavascriptAppKey());
		
		// 로그인한 사용자가 이미 이력서를 보냈는지 확인
		if (authentication != null && authentication.isAuthenticated()) {
			try {
				String username = authentication.getName();
				boolean alreadySentResume = resumeService.hasAlreadySentResume(username, id);
				model.addAttribute("alreadySentResume", alreadySentResume);
			} catch (DataNotFoundException e) {
				model.addAttribute("alreadySentResume", false);
			}
		} else {
			model.addAttribute("alreadySentResume", false);
		}
		
		return "work_detail";
	}
	
	@GetMapping("/create")
	public String workCreateForm(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/user/login";
		}
		model.addAttribute("workingForm", new WorkingForm());
		model.addAttribute("action", "/work/create");
		model.addAttribute("offer", new Offer());
		return "work_form";
	}

	@PostMapping("/create")
	public String workCreate(@Valid @ModelAttribute("workingForm") WorkingForm workingForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Principal principal) {
		
		if (principal == null) {
			return "redirect:/user/login";
		}
		
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
	public String chatRoom(Model model, @PathVariable("id") Long id, Authentication authentication, HttpServletRequest request, Principal principal) throws JsonProcessingException {
		if (principal == null) {
			return "redirect:/user/login";
		}
		// 먼저 Working에서 찾기
		Working working = null;
		Offer offer = null;
		
		try {
			working = this.workingService.getWorking(id);
			logger.debug("Working 찾음: {}", working.getId());
		} catch (Exception e) {
			logger.debug("Working에서 찾을 수 없음: {}", id);
		}
		
		// Working에서 찾지 못했다면 Offer에서 찾기
		if (working == null) {
			try {
				offer = this.offerService.getOffer(id).orElse(null);
				if (offer != null) {
					logger.debug("Offer 찾음: {}", offer.getId());
				}
			} catch (Exception e) {
				logger.debug("Offer에서도 찾을 수 없음: {}", id);
			}
		}
		
		// 둘 다 찾지 못했다면 에러
		if (working == null && offer == null) {
			throw new RuntimeException("채팅방을 찾을 수 없습니다.");
		}
		
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
			// JSON 문자열로 변환하여 추가 (DTO로 변환)
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 지원
			SimpleUserDTO simpleUser = currentUser != null ? new SimpleUserDTO(currentUser) : null;
			String currentUserJson = simpleUser != null ? objectMapper.writeValueAsString(simpleUser) : "null";
			logger.debug("현재 사용자 JSON: {}", currentUserJson);
			model.addAttribute("currentUserJson", currentUserJson);
		} else {
			logger.debug("인증되지 않은 사용자");
			model.addAttribute("currentUser", null);
			model.addAttribute("currentUserJson", "null");
		}
		
		// Working이면 working을, Offer면 offer를 DTO로 변환해서 모델에 추가
		if (working != null) {
			model.addAttribute("working", new WorkingChatDTO(working));
			model.addAttribute("offer", null);
		} else {
			model.addAttribute("working", null);
			model.addAttribute("offer", new OfferChatDTO(offer));
		}
		
		return "work_chat";
	}

	@GetMapping("/edit/{id}")
	public String workEditForm(@PathVariable("id") Long id, Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/user/login";
		}
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

	@PostMapping("/edit/{id}")
	public String workEdit(@PathVariable("id") Long id, @Valid @ModelAttribute("workingForm") WorkingForm workingForm, BindingResult bindingResult, Principal principal) {
		if (principal == null) {
			return "redirect:/user/login";
		}
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

	@PostMapping("/delete/{id}")
	public String workDelete(@PathVariable("id") Long id, Principal principal) {
		if (principal == null) {
			return "redirect:/user/login";
		}
		Working working = workingService.getWorking(id);
		if (!working.getAuthor().getUsername().equals(principal.getName())) {
			throw new RuntimeException("삭제 권한이 없습니다.");
		}
		workingService.deleteWorking(working);
		return "redirect:/work/list";
	}

	@PostMapping("/modify/{id}")
	public String workingUpdate(WorkingForm form, @PathVariable("id") Long id, Principal principal) {
		if (principal == null) {
			return "redirect:/user/login";
		}
		// ... (validation and authorization)
		
		Working working = this.workingService.getWorking(id);
		
		// ... (authorization check)

		this.workingService.update(working, form);
		return String.format("redirect:/work/detail/%s", id);
	}
	
	@GetMapping("/modify/{id}")
	public String workingModify(WorkingForm form, @PathVariable("id") Long id, Principal principal) {
		if (principal == null) {
			return "redirect:/user/login";
		}
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

	// SimpleUserDTO 클래스 추가 (컨트롤러 내부 또는 별도 파일로 분리 가능)
	static class SimpleUserDTO {
		public Long id;
		public String username;
		public String nickname;
		public String profileImageUrl;
		public SimpleUserDTO(com.mysite.xtra.user.SiteUser user) {
			this.id = user.getId();
			this.username = user.getUsername();
			this.nickname = user.getNickname();
			this.profileImageUrl = user.getProfileImageUrl();
		}
	}

	// --- DTO 클래스 추가 ---
	static class WorkingChatDTO {
		public Long id;
		public String siteName;
		public String cPerson;
		public Long authorId;
		public String authorUsername;
		public WorkingChatDTO(Working w) {
			this.id = w.getId();
			this.siteName = w.getSiteName();
			this.cPerson = w.getCPerson();
			this.authorId = w.getAuthor() != null ? w.getAuthor().getId() : null;
			this.authorUsername = w.getAuthor() != null ? w.getAuthor().getUsername() : null;
		}
	}
	static class OfferChatDTO {
		public Long id;
		public String workPlace;
		public String perName;
		public Long authorId;
		public String authorUsername;
		public OfferChatDTO(Offer o) {
			this.id = o.getId();
			this.workPlace = o.getWorkPlace();
			this.perName = o.getPerName();
			this.authorId = o.getAuthor() != null ? o.getAuthor().getId() : null;
			this.authorUsername = o.getAuthor() != null ? o.getAuthor().getUsername() : null;
		}
	}
}
