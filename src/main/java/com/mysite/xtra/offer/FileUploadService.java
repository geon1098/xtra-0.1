package com.mysite.xtra.offer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String uploadFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리가 없으면 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일명 중복 방지를 위해 타임스탬프 사용
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = System.currentTimeMillis() + fileExtension;

        // 파일 저장
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 정적 리소스 경로로 반환 (앞에 /static을 붙이지 않음)
        return "/images/" + filename;
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl != null && fileUrl.startsWith("/images/")) {
            String filename = fileUrl.substring("/images/".length());
            Path filePath = Paths.get(uploadDir, filename);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // 로그만 남기고 예외는 던지지 않음
                System.err.println("파일 삭제 실패: " + filePath);
            }
        }
    }
} 