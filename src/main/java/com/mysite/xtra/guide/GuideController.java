package com.mysite.xtra.guide;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/guide")
public class GuideController {
    private final GuidePostService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", service.findAll());
        return "guide/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", service.getById(id));
        return "guide/detail";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("post", new GuidePost());
        model.addAttribute("action", "/guide/new");
        return "guide/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public String create(@RequestParam String title,
                         @RequestParam String content,
                         Principal principal) {
        String author = principal != null ? principal.getName() : "관리자";
        GuidePost post = service.create(title, content, author);
        return "redirect:/guide/" + post.getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", service.getById(id));
        model.addAttribute("action", "/guide/edit/" + id);
        return "guide/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String content) {
        service.update(id, title, content);
        return "redirect:/guide/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/guide";
    }
}


