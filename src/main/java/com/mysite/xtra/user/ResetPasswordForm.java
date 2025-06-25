package com.mysite.xtra.user;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class ResetPasswordForm {
    
    @NotEmpty(message = "새 비밀번호는 필수항목입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String newPassword;
    
    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String confirmPassword;
    
    private String token;
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
} 