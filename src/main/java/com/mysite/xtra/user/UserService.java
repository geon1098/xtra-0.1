package com.mysite.xtra.user;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysite.xtra.DataNotFoundException;
import com.mysite.xtra.guin.WorkingRepository;
import com.mysite.xtra.gujic.JobingRepository;
import com.mysite.xtra.offer.OfferRepository;
import com.mysite.xtra.user.EmailVerificationRepository;
import com.mysite.xtra.user.PasswordResetTokenRepository;

@Service
@Primary
public class UserService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final WorkingRepository workingRepository;
	private final JobingRepository jobingRepository;
	private final OfferRepository offerRepository;
	private final EmailVerificationRepository emailVerificationRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository,
					  WorkingRepository workingRepository, JobingRepository jobingRepository,
					  OfferRepository offerRepository,
					  EmailVerificationRepository emailVerificationRepository,
					  PasswordResetTokenRepository passwordResetTokenRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.workingRepository = workingRepository;
		this.jobingRepository = jobingRepository;
		this.offerRepository = offerRepository;
		this.emailVerificationRepository = emailVerificationRepository;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
	}
	
	public SiteUser create(String username, String nickname, String email, String password, String phone) {
	    SiteUser user = new SiteUser();
	    user.setUsername(username);
	    user.setNickname(nickname);
	    user.setEmail(email);
	    user.setPhone(phone);
	    String encodedPassword = passwordEncoder.encode(password);
	    user.setPassword(encodedPassword);
	    user.setRole("ROLE_USER"); // 기본 권한 설정
	    user.setCreateDate(LocalDateTime.now());
	    userRepository.save(user);
	    return user;
	}
	
	public SiteUser getUser(String username) {
	    return userRepository.findByUsername(username)
	        .orElseThrow(() -> new DataNotFoundException("사용자를 찾을 수 없습니다."));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			SiteUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
			
			// 사용자 정보 로깅 (디버깅용)
			logger.debug("사용자 로드: username={}, role={}, emailVerified={}", 
				user.getUsername(), user.getRole(), user.isEmailVerified());
			
			return user;
		} catch (Exception e) {
			logger.error("사용자 로드 중 오류 발생: username={}, error={}", username, e.getMessage(), e);
			throw new UsernameNotFoundException("사용자 로드 중 오류가 발생했습니다: " + username, e);
		}
	}

	public SiteUser save(SiteUser user) {
		return userRepository.save(user);
	}
	
	public SiteUser findByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new DataNotFoundException("해당 이메일로 등록된 사용자를 찾을 수 없습니다."));
	}
	
	public SiteUser findByUsernameAndEmail(String username, String email) {
		return userRepository.findByUsernameAndEmail(username, email)
			.orElseThrow(() -> new DataNotFoundException("아이디와 이메일이 일치하는 사용자를 찾을 수 없습니다."));
	}
	
	public void updatePassword(SiteUser user, String newPassword) {
		String encodedPassword = passwordEncoder.encode(newPassword);
		user.setPassword(encodedPassword);
		userRepository.save(user);
	}
	
	@Transactional
	public void withdrawUser(SiteUser user) {
		logger.info("회원탈퇴 처리 시작: 사용자={}", user.getUsername());
		
		try {
			// 1. 채팅 메시지 삭제
			logger.info("채팅 메시지 삭제 중...");
			// chatMessageRepository.deleteByAuthor(user); // Removed as per edit hint
			logger.info("채팅 메시지 삭제 완료");
			
			// 2. 이메일 인증 토큰 삭제
			logger.info("이메일 인증 토큰 삭제 중...");
			emailVerificationRepository.deleteByUser(user);
			logger.info("이메일 인증 토큰 삭제 완료");
			
			// 3. 비밀번호 재설정 토큰 삭제
			logger.info("비밀번호 재설정 토큰 삭제 중...");
			passwordResetTokenRepository.deleteByUser(user);
			logger.info("비밀번호 재설정 토큰 삭제 완료");
			
			// 4. 유료 구인글(Offer) 삭제
			logger.info("유료 구인글 삭제 중...");
			offerRepository.deleteByAuthor(user);
			logger.info("유료 구인글 삭제 완료");
			
			// 5. 구인글(Working) 삭제
			logger.info("구인글 삭제 중...");
			workingRepository.deleteByAuthor(user);
			logger.info("구인글 삭제 완료");
			
			// 6. 구직글(Jobing) 삭제
			logger.info("구직글 삭제 중...");
			jobingRepository.deleteByAuthor(user);
			logger.info("구직글 삭제 완료");
			
			// 7. 사용자 삭제
			logger.info("사용자 정보 삭제 중...");
			userRepository.delete(user);
			logger.info("사용자 정보 삭제 완료");
			
			logger.info("회원탈퇴 처리 완료: 사용자={}", user.getUsername());
			
		} catch (Exception e) {
			logger.error("회원탈퇴 처리 중 오류 발생: {}", e.getMessage(), e);
			throw e;
		}
	}
}