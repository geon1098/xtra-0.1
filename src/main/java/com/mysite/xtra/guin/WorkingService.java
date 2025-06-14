package com.mysite.xtra.guin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.xtra.DataNotFoundException;
import com.mysite.xtra.user.SiteUser;

@Service
public class WorkingService {

	private final WorkingRepository workingRepository;
	
	public WorkingService(WorkingRepository workingRepository) {
		this.workingRepository = workingRepository;
	}
	
	public List<Working> getWorkList(){
		return workingRepository.findAll();
	}
	
	public Working getWorking(Long id) {
		Optional<Working> working = this.workingRepository.findById(id);
		if(working.isPresent()) {
			return working.get();
		}else {
			throw new DataNotFoundException("구인창 없음");
		}
	}
	
	@Transactional
    public Working createWorking(WorkingForm workingForm, SiteUser author) {
        Working working = new Working();
        working.setSiteName(workingForm.getSiteName());
        working.setTitle(workingForm.getTitle());
        working.setCategory(workingForm.getCategory());
        working.setJobContent(workingForm.getJobContent());
        working.setJobType(workingForm.getJobType());
        working.setBenefits(workingForm.getBenefits());
        working.setLocation(workingForm.getLocation());
        working.setJobDescription(workingForm.getJobDescription());
        working.setJobWork(workingForm.getJobWork());
        working.setDeadDate(workingForm.getDeadDate());
        working.setWorkNumber(workingForm.getWorkNumber());
        working.setGender(workingForm.getGender());
        working.setAge(workingForm.getAge());
        working.setAddress(workingForm.getAddress());
        working.setMapLocation(workingForm.getMapLocation());
        working.setJobDetails(workingForm.getJobDetails());
        working.setCPerson(workingForm.getCPerson());
        working.setPhone(workingForm.getPhone());
        working.setCreateDate(LocalDateTime.now());
        working.setAuthor(author);
        return workingRepository.save(working);
    }
	
	public Page<Working> getPageList(int page){
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return this.workingRepository.findAll(pageable);
	}
}
