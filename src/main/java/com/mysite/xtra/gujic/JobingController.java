package com.mysite.xtra.gujic;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@RequestMapping("/job")
@Controller
public class JobingController {

    private final JobingService jobingService;

    public JobingController(JobingService jobingService) {
        this.jobingService = jobingService;
    }

    @GetMapping("/list")
    public String listJobings(Model model, @RequestParam(value="page", defaultValue="0") int page) {
    	Page<Jobing> paging = this.jobingService.getPageList(page);
        model.addAttribute("paging", paging);
        return "job_list";
    }
    
	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable("id") Long id) {
		Jobing jobing = this.jobingService.getJobing(id);
		model.addAttribute("jobing",jobing);
		return "job_detail";
	}
	
	@GetMapping("/create")
	public String jobCreateForm(Model model) {
		model.addAttribute("jobingForm", new JobingForm());
		return "job_form";
	}
	@PostMapping("/create")
	public String jobCreate(@Valid @ModelAttribute("jobingForm") JobingForm jobingForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "job_form";
		}
		jobingService.createJobing(jobingForm);
		redirectAttributes.addFlashAttribute("message","구인글이 성공적으로 등록되었습니다.");
		return "redirect:/job/list";
	}
}
