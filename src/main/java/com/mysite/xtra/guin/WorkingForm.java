package com.mysite.xtra.guin;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class WorkingForm {
    
    @NotBlank(message = "현장명은 필수 입력 값입니다.")
    @Size(max = 200)
    private String siteName;
    
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 200)
    private String title;
    
    private String category;
    private String jobContent;
    private String jobType;
    private String benefits;
    private String location;
    private String jobDescription;
    private String jobWork;
    private String deadDate;
    private String workNumber;
    private String gender;
    private String age;
    private LocalDateTime createDate;
    private String address;
    private String mapLocation;
    private String jobDetails;
    private String cPerson;
    private String phone;
    private String map_location;

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
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMap_location() { return map_location; }
    public void setMap_location(String map_location) { this.map_location = map_location; }
}
