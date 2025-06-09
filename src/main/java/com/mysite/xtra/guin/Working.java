package com.mysite.xtra.guin;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.mysite.xtra.user.SiteUser;

@Entity
public class Working { //구인

	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(length = 200, nullable = false)
	    private String siteName; // 현장명

	    @Column(length = 200, nullable = false)
	    private String title; // 구인 제목

	    @Column(length = 50)
	    private String category; // 분류 (아파트, 오피스텔, 상가, 건물)

	    @Column(length = 100)
	    private String jobContent; // 업무

	    @Column(length = 50)
	    private String jobType; // 고용형태

	    @Column(columnDefinition = "TEXT")
	    private String benefits; // 복리후생

	    @Column(length = 255)
	    private String location; // 소재지

	    @Column(columnDefinition = "TEXT")
	    private String jobDescription; // 업무내용
	    
	    @Column(length = 50)
	    private String jobWork; //응시요견

	    // 모집 조건
	    private String deadDate; // 모집기간

	    private String workNumber; // 모집 인원

	    @Column(length = 10)
	    private String gender; // 성별

	    private String age; //나이

	    private LocalDateTime createDate; // 등록일자

	    // 근무 지역
	    @Column(length = 255)
	    private String address; // 주소

	    @Column(length = 255)
	    private String mapLocation; // 지도 (위경도 정보 가능)

	    @Column(columnDefinition = "TEXT")
	    private String jobDetails; // 상세요건

	    // 담당자 연락처
	    @Column(length = 50)
	    private String cPerson; // 담당자

	    @Column(length = 20)
	    private String Phone; // 연락처

    @ManyToOne
    private SiteUser author; // 작성자

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getJobContent() { return jobContent; }
    public void setJobContent(String jobContent) { this.jobContent = jobContent; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
    public String getJobWork() { return jobWork; }
    public void setJobWork(String jobWork) { this.jobWork = jobWork; }
    public String getDeadDate() { return deadDate; }
    public void setDeadDate(String deadDate) { this.deadDate = deadDate; }
    public String getWorkNumber() { return workNumber; }
    public void setWorkNumber(String workNumber) { this.workNumber = workNumber; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMapLocation() { return mapLocation; }
    public void setMapLocation(String mapLocation) { this.mapLocation = mapLocation; }
    public String getJobDetails() { return jobDetails; }
    public void setJobDetails(String jobDetails) { this.jobDetails = jobDetails; }
    public String getCPerson() { return cPerson; }
    public void setCPerson(String cPerson) { this.cPerson = cPerson; }
    public String getPhone() { return Phone; }
    public void setPhone(String Phone) { this.Phone = Phone; }
    public SiteUser getAuthor() { return author; }
    public void setAuthor(SiteUser author) { this.author = author; }
}
