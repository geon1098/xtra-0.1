package com.mysite.xtra.property;

import com.mysite.xtra.offer.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final FileUploadService fileUploadService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public PropertyService(PropertyRepository propertyRepository, FileUploadService fileUploadService) {
        this.propertyRepository = propertyRepository;
        this.fileUploadService = fileUploadService;
    }

    public List<Property> getAll() {
        return propertyRepository.findAll();
    }

    public Property get(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }

    public Property save(Property property, List<MultipartFile> images) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String url = fileUploadService.uploadFile(file); // 반드시 URL 전체를 저장
                    imageUrls.add(url);
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