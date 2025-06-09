package com.mysite.xtra.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class UserCreateForm {
	@Size(min = 3, max = 25)
	@NotEmpty(message = "사용자ID는 필수항목입니다.")
	private String username;

	@NotEmpty(message = "사용자 닉네임은 필수입니다.")
	private String nickname;
	
	@NotEmpty(message = "사용자 비밀번호 필수입니다.")
	private String password1;
	
	@NotEmpty(message = "비밀번호 확인은 필수입니다.")
	private String password2;
	
	@NotEmpty(message = "이메일은 필수항목입니다.")
	@Email
	private String email;
	
	@NotEmpty(message = "전화번호는 필수항목입니다.")
	private String phone;
	
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getNickname() { return nickname; }
	public void setNickname(String nickname) { this.nickname = nickname; }
	public String getPassword1() { return password1; }
	public void setPassword1(String password1) { this.password1 = password1; }
	public String getPassword2() { return password2; }
	public void setPassword2(String password2) { this.password2 = password2; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
}
