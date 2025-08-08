package com.mysite.xtra.user;

import javax.validation.constraints.NotEmpty;

public class WithdrawForm {
    
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    private String password;
    
    @NotEmpty(message = "탈퇴 확인 문구를 입력해주세요.")
    private String confirmText;
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getConfirmText() {
        return confirmText;
    }
    
    public void setConfirmText(String confirmText) {
        this.confirmText = confirmText;
    }
} 