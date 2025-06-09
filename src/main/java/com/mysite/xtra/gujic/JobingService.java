package com.mysite.xtra.gujic;

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
import com.mysite.xtra.guin.Working;

@Service
public class JobingService {

	private final JobingRepository jobingRepository;
	
	public JobingService(JobingRepository jobingRepository) {
		this.jobingRepository = jobingRepository;
	}
	
	public List<Jobing> getJobList(){
		return jobingRepository.findAll();
	}
	
	public Jobing getJobing(Long id) {
		Optional<Jobing> jobing = this.jobingRepository.findById(id);
		if(jobing.isPresent()) {
			return jobing.get();
		}else {
			throw new DataNotFoundException("구직창 없음");
		}
	}
	@Transactional
    public Jobing createJobing(JobingForm form) {
        Jobing jobing = new Jobing();
        jobing.setName(form.getName());
        jobing.setGender(form.getGender());
        jobing.setAge(form.getAge());
        jobing.setPhone(form.getPhone());
        jobing.setEmail(form.getEmail());
        jobing.setAddress(form.getAddress());
        jobing.setRequestWork(form.getRequestWork());
        jobing.setHopArea(form.getHopArea());
        jobing.setCareer(form.getCareer());
        jobing.setNetworking(form.getNetworking());
        jobing.setStartDate(form.getStartDate());
        jobing.setIntroduction(form.getIntroduction());
        jobing.setEtc(form.getEtc());
        return jobingRepository.save(jobing);
    }
	
	public Page<Jobing> getPageList(int page){
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return this.jobingRepository.findAll(pageable);
	}
}
