package com.mysite.xtra.gujic;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Jobing { //구직

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name; // 성명

    @Column(length = 10, nullable = false)
    private String gender; // 성별

    private String age; // 연령

    @Column(length = 20, nullable = false)
    private String phone; // 전화번호

    @Column(length = 100)
    private String email; // 이메일

    @Column(length = 255)
    private String address; // 주소

    @Column(length = 100)
    private String requestWork; // 희망 업무

    @Column(length = 100)
    private String hopArea; // 희망 지역

    private String career; // 경력 (년)

    @Column(columnDefinition = "TEXT")
    private String license; // 자격 사항

    @Column(columnDefinition = "TEXT")
    private String networking; // 인맥

    private String startDate; // 근무 가능 시작일

    @Column(columnDefinition = "TEXT")
    private String introduction; // 자기소개

    @Column(columnDefinition = "TEXT")
    private String etc; // 기타
    
    private LocalDateTime createDate;
	
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }
}
