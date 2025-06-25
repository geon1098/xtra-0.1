package com.mysite.xtra.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class FindIdForm {
    
    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
} 