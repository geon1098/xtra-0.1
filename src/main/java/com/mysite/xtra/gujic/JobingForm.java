package com.mysite.xtra.gujic;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class JobingForm {

    @NotBlank(message = "현장명은 필수 입력 값입니다.")
    @Size(max = 200)
    private String name;
    
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 200)
    private String gender;
    
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 200)
    private String age;
    
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 200)
    private String phone;
    
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 200)
    private String email;
    
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 200)
    private String address;
    
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 200)
    private String requestWork;
    
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 200)
    private String hopArea;
    
    private String career;
    
    private String license;
    
    private String networking;
    
    private String startDate;
    
    private String introduction;
    
    private String etc;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getRequestWork() { return requestWork; }
    public void setRequestWork(String requestWork) { this.requestWork = requestWork; }
    public String getHopArea() { return hopArea; }
    public void setHopArea(String hopArea) { this.hopArea = hopArea; }
    public String getCareer() { return career; }
    public void setCareer(String career) { this.career = career; }
    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }
    public String getNetworking() { return networking; }
    public void setNetworking(String networking) { this.networking = networking; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getEtc() { return etc; }
    public void setEtc(String etc) { this.etc = etc; }
}
