package com.mysite.xtra.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final EmailService emailService;
	private final EmailVerificationRepository emailVerificationRepository;
	
	public UserController(UserService userService, EmailService emailService, 
						 EmailVerificationRepository emailVerificationRepository) {
		this.userService = userService;
		this.emailService = emailService;
		this.emailVerificationRepository = emailVerificationRepository;
	}
	
	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}
	
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, 
						RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "signup_form";
		}
		
		if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "signup_form";
		}

		try {
			// 사용자 생성
			SiteUser user = userService.create(
				userCreateForm.getUsername(),
				userCreateForm.getNickname(),
				userCreateForm.getEmail(),
				userCreateForm.getPassword1(),
				userCreateForm.getPhone()
			);

			// 유저를 DB에서 다시 조회 (영속성 보장)
			SiteUser savedUser = userService.getUser(user.getUsername());

			// 이메일 인증 토큰 생성 및 저장
			EmailVerification verification = EmailVerification.createEmailVerification(savedUser.getEmail(), savedUser);
			emailVerificationRepository.save(verification);

			// 인증 이메일 발송
			emailService.sendVerificationEmail(savedUser.getEmail(), verification.getToken());

			redirectAttributes.addFlashAttribute("message", 
				"회원가입이 완료되었습니다. 이메일을 확인하여 인증을 완료해주세요.");
			return "redirect:/user/login";

		} catch(DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_form";
		} catch(Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_form";
		}
	}
	
	@GetMapping("/login")
	public String login() {
		return "login_form";
	}
}
