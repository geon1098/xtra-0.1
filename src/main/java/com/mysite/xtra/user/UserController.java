package com.mysite.xtra.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final EmailService emailService;
	private final EmailVerificationRepository emailVerificationRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	
	public UserController(UserService userService, EmailService emailService, 
						 EmailVerificationRepository emailVerificationRepository,
						 PasswordResetTokenRepository passwordResetTokenRepository) {
		this.userService = userService;
		this.emailService = emailService;
		this.emailVerificationRepository = emailVerificationRepository;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
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
	
	// 아이디 찾기 폼
	@GetMapping("/find-id")
	public String findIdForm(FindIdForm findIdForm) {
		return "find_id_form";
	}
	
	// 아이디 찾기 처리
	@PostMapping("/find-id")
	public String findId(@Valid FindIdForm findIdForm, BindingResult bindingResult, 
						RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "find_id_form";
		}
		
		try {
			SiteUser user = userService.findByEmail(findIdForm.getEmail());
			emailService.sendFindIdEmail(user.getEmail(), user.getUsername());
			
			redirectAttributes.addFlashAttribute("message", 
				"입력하신 이메일로 아이디를 발송했습니다. 이메일을 확인해주세요.");
			return "redirect:/user/login";
			
		} catch(Exception e) {
			bindingResult.reject("findIdFailed", "해당 이메일로 등록된 사용자를 찾을 수 없습니다.");
			return "find_id_form";
		}
	}
	
	// 비밀번호 찾기 폼
	@GetMapping("/find-password")
	public String findPasswordForm(FindPasswordForm findPasswordForm) {
		return "find_password_form";
	}
	
	// 비밀번호 찾기 처리
	@PostMapping("/find-password")
	public String findPassword(@Valid FindPasswordForm findPasswordForm, BindingResult bindingResult, 
							  RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "find_password_form";
		}
		
		try {
			SiteUser user = userService.findByUsernameAndEmail(
				findPasswordForm.getUsername(), findPasswordForm.getEmail());
			
			// 기존 토큰이 있다면 삭제
			passwordResetTokenRepository.findByUserId(user.getId())
				.ifPresent(passwordResetTokenRepository::delete);
			
			// 새로운 비밀번호 재설정 토큰 생성
			PasswordResetToken resetToken = PasswordResetToken.createPasswordResetToken(user);
			passwordResetTokenRepository.save(resetToken);
			
			// 비밀번호 재설정 이메일 발송
			emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
			
			redirectAttributes.addFlashAttribute("message", 
				"입력하신 이메일로 비밀번호 재설정 링크를 발송했습니다. 이메일을 확인해주세요.");
			return "redirect:/user/login";
			
		} catch(Exception e) {
			bindingResult.reject("findPasswordFailed", "아이디와 이메일이 일치하는 사용자를 찾을 수 없습니다.");
			return "find_password_form";
		}
	}
	
	// 비밀번호 재설정 폼
	@GetMapping("/reset-password")
	public String resetPasswordForm(@RequestParam String token, ResetPasswordForm resetPasswordForm, 
								   RedirectAttributes redirectAttributes) {
		try {
			PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
			
			if (resetToken.isExpired()) {
				redirectAttributes.addFlashAttribute("error", "비밀번호 재설정 링크가 만료되었습니다.");
				return "redirect:/user/login";
			}
			
			if (resetToken.isUsed()) {
				redirectAttributes.addFlashAttribute("error", "이미 사용된 링크입니다.");
				return "redirect:/user/login";
			}
			
			resetPasswordForm.setToken(token);
			return "reset_password_form";
			
		} catch(Exception e) {
			redirectAttributes.addFlashAttribute("error", "유효하지 않은 링크입니다.");
			return "redirect:/user/login";
		}
	}
	
	// 비밀번호 재설정 처리
	@PostMapping("/reset-password")
	public String resetPassword(@Valid ResetPasswordForm resetPasswordForm, BindingResult bindingResult, 
							   RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "reset_password_form";
		}
		
		if(!resetPasswordForm.getNewPassword().equals(resetPasswordForm.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
			return "reset_password_form";
		}
		
		try {
			PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(resetPasswordForm.getToken())
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
			
			if (resetToken.isExpired()) {
				redirectAttributes.addFlashAttribute("error", "비밀번호 재설정 링크가 만료되었습니다.");
				return "redirect:/user/login";
			}
			
			if (resetToken.isUsed()) {
				redirectAttributes.addFlashAttribute("error", "이미 사용된 링크입니다.");
				return "redirect:/user/login";
			}
			
			// 비밀번호 업데이트
			userService.updatePassword(resetToken.getUser(), resetPasswordForm.getNewPassword());
			
			// 토큰 사용 처리
			resetToken.setUsed(true);
			passwordResetTokenRepository.save(resetToken);
			
			redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다. 새로운 비밀번호로 로그인해주세요.");
			return "redirect:/user/login";
			
		} catch(Exception e) {
			bindingResult.reject("resetPasswordFailed", "비밀번호 재설정에 실패했습니다.");
			return "reset_password_form";
		}
	}
}
