package com.mysite.xtra.user;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class SiteUser implements UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //회원번호
	
	@Column(unique = true)
	private String nickname; //닉네임
	
	@Column(unique = true)
	private String username; // 로그인 아이디
	
	private String password; //로그인 패스워드
	
	@Column(unique = true)
	private String email; //이메일s
	
	private String phone; // 폰번호
	
	private String role; // 권한
	
	private LocalDateTime createDate; // 생성일

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNickname() { return nickname; }
	public void setNickname(String nickname) { this.nickname = nickname; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }
	public LocalDateTime getCreateDate() { return createDate; }
	public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(role != null ? role : "ROLE_USER"));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
