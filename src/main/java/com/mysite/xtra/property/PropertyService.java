package com.mysite.xtra.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public List<Property> getAll() {
        return propertyRepository.findAll();
    }

    public Property get(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }

    public Property save(Property property, List<MultipartFile> images) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    File dest = new File(uploadDir + File.separator + fileName);
                    file.transferTo(dest);
                    imageUrls.add("/images/" + fileName);
                }
            }
        }
        if (!imageUrls.isEmpty()) {
            property.setImageUrls(imageUrls);
        }
        if (property.getCreatedAt() == null) {
            property.setCreatedAt(LocalDate.now());
        }
        return propertyRepository.save(property);
    }

    public void delete(Long id) {
        propertyRepository.deleteById(id);
    }
} 