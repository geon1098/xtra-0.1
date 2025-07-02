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
import java.security.Principal;
import com.mysite.xtra.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;

@RequestMapping("/job")
@Controller
public class JobingController {

    private final JobingService jobingService;
    private final UserService userService;

    public JobingController(JobingService jobingService, UserService userService) {
        this.jobingService = jobingService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String listJobings(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="keyword", required=false) String keyword) {
    	Page<Jobing> paging = this.jobingService.getPageList(page, keyword);
        java.util.Map<String, Object> param = new java.util.HashMap<>();
        if (keyword != null) param.put("keyword", keyword);
        model.addAttribute("paging", paging);
        model.addAttribute("param", param);
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
		model.addAttribute("action", "/job/create");
		return "job_form";
	}
	@PostMapping("/create")
	public String jobCreate(@Valid @ModelAttribute("jobingForm") JobingForm jobingForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal) {
		if(bindingResult.hasErrors()) {
			return "job_form";
		}
		com.mysite.xtra.user.SiteUser user = userService.getUser(principal.getName());
		jobingService.createJobing(jobingForm, user);
		redirectAttributes.addFlashAttribute("message","구인글이 성공적으로 등록되었습니다.");
		return "redirect:/job/list";
	}

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String jobEditForm(@PathVariable("id") Long id, Model model, Principal principal) {
        Jobing jobing = jobingService.getJobing(id);
        if (!jobing.getAuthor().getUsername().equals(principal.getName())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        JobingForm form = new JobingForm();
        form.setId(jobing.getId());
        form.setName(jobing.getName());
        form.setGender(jobing.getGender());
        form.setAge(jobing.getAge());
        form.setPhone(jobing.getPhone());
        form.setEmail(jobing.getEmail());
        form.setAddress(jobing.getAddress());
        form.setRequestWork(jobing.getRequestWork());
        form.setHopArea(jobing.getHopArea());
        form.setCareer(jobing.getCareer());
        form.setLicense(jobing.getLicense());
        form.setNetworking(jobing.getNetworking());
        form.setStartDate(jobing.getStartDate());
        form.setIntroduction(jobing.getIntroduction());
        form.setEtc(jobing.getEtc());
        model.addAttribute("jobingForm", form);
        model.addAttribute("action", "/job/edit/" + id);
        return "job_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/edit/{id}")
    public String jobEdit(@PathVariable("id") Long id, @Valid @ModelAttribute("jobingForm") JobingForm jobingForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "job_form";
        }
        Jobing jobing = jobingService.getJobing(id);
        if (!jobing.getAuthor().getUsername().equals(principal.getName())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        jobingService.updateJobing(jobing, jobingForm);
        return "redirect:/job/detail/" + id;
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String jobDelete(@PathVariable("id") Long id, Principal principal) {
        Jobing jobing = jobingService.getJobing(id);
        if (!jobing.getAuthor().getUsername().equals(principal.getName())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        jobingService.deleteJobing(jobing);
        return "redirect:/job/list";
    }
}
