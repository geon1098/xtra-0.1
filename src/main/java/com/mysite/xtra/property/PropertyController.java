package com.mysite.xtra.property;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/property")
public class PropertyController {
    private final PropertyService propertyService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("propertyList", propertyService.getAll());
        return "property";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Principal principal) {
        Property property = propertyService.get(id);
        model.addAttribute("property", property);
        boolean isOwner = principal != null && property != null && principal.getName().equals(property.getAuthor());
        model.addAttribute("isOwner", isOwner);
        return "property_detail";
    }

    @GetMapping("/new")
    public String form(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("property", new Property());
        return "pro_form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Property property,
                        @RequestParam(value = "images", required = false) List<MultipartFile> images,
                        Principal principal) throws IOException {
        if (principal != null) {
            property.setAuthor(principal.getName());
        }
        propertyService.save(property, images);
        return "redirect:/property";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        Property property = propertyService.get(id);
        if (property == null || principal == null || !principal.getName().equals(property.getAuthor())) {
            return "redirect:/property";
        }
        model.addAttribute("property", property);
        return "pro_update";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                        @ModelAttribute Property property,
                        @RequestParam(value = "images", required = false) List<MultipartFile> images,
                        Principal principal) throws IOException {
        Property old = propertyService.get(id);
        if (old == null || principal == null || !principal.getName().equals(old.getAuthor())) {
            return "redirect:/property";
        }
        property.setId(id);
        property.setAuthor(old.getAuthor());
        property.setCreatedAt(old.getCreatedAt());
        propertyService.save(property, images);
        return "redirect:/property/" + id;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Principal principal) {
        Property property = propertyService.get(id);
        if (property != null && principal != null && principal.getName().equals(property.getAuthor())) {
            propertyService.delete(id);
        }
        return "redirect:/property";
    }
} 