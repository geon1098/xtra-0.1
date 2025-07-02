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
        // 1. 파일 크기 제한 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 2. 허용 확장자/Content-Type 체크
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        // 허용 확장자
        java.util.List<String> allowedExt = java.util.List.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
        if (!allowedExt.contains(fileExtension)) {
            throw new IOException("허용되지 않은 파일 형식입니다. (이미지 파일만 업로드 가능)");
        }
        // Content-Type 체크
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("이미지 파일만 업로드할 수 있습니다.");
        }

        // 3. 파일명 무작위화(이미 적용됨)
        String filename = System.currentTimeMillis() + "." + fileExtension;

        // 4. 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 5. 파일 저장
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 6. 실행권한 제거(리눅스 환경일 때)
        try {
            filePath.toFile().setExecutable(false, false);
        } catch (Exception ignore) {}

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