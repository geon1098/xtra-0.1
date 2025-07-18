package com.mysite.xtra.user;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.annotation.Primary;

import com.mysite.xtra.DataNotFoundException;

@Service
@Primary
public class UserService implements UserDetailsService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}
	
	public SiteUser create(String username, String nickname, String email, String password, String phone) {
	    SiteUser user = new SiteUser();
	    user.setUsername(username);
	    user.setNickname(nickname);
	    user.setEmail(email);
	    user.setPhone(phone);
	    String encodedPassword = passwordEncoder.encode(password);
	    user.setPassword(encodedPassword);
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
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
	}
}