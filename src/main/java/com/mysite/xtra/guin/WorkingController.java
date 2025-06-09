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

import com.mysite.xtra.user.SiteUser;
import com.mysite.xtra.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Principal;

@RequestMapping("/work")
@Controller
public class WorkingController {
	
	private final WorkingService workingService;
	private final UserService userService;
	private static final Logger logger = LoggerFactory.getLogger(WorkingController.class);
	
	public WorkingController(WorkingService workingService, UserService userService) {
		this.workingService = workingService;
		this.userService = userService;
	}
	
	@GetMapping("/info")
	public String workInfo() {
		return "work_info";
	}
	
	@GetMapping("/list")
	public String listWorkings(Model model, @RequestParam(value="page", defaultValue="0") int page) {
		Page<Working> paging = this.workingService.getPageList(page);
		model.addAttribute("paging",paging);
		System.out.println("✅ work_list.html 렌더링됨!");
		return "work_list";
	}
	
	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable("id") Long id) {
		Working working = this.workingService.getWorking(id);
		model.addAttribute("working",working);
		return "work_detail";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String workCreateForm(Model model) {
		model.addAttribute("workingForm", new WorkingForm());
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
}
