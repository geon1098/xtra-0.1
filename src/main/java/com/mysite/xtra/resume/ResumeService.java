package com.mysite.xtra.resume;

import com.mysite.xtra.DataNotFoundException;
import com.mysite.xtra.guin.Working;
import com.mysite.xtra.guin.WorkingService;
import com.mysite.xtra.offer.Offer;
import com.mysite.xtra.offer.OfferService;
import com.mysite.xtra.user.SiteUser;
import com.mysite.xtra.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeService {
    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);
    
    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final WorkingService workingService;
    private final OfferService offerService;
    
    // 파일 업로드 경로 설정
    private static final String UPLOAD_DIR = "uploads/resumes/";
    
    @Autowired
    public ResumeService(ResumeRepository resumeRepository, UserService userService, WorkingService workingService, OfferService offerService) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.workingService = workingService;
        this.offerService = offerService;
        
        // 업로드 디렉토리 생성
        createUploadDirectory();
    }
    
    private void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("이력서 업로드 디렉토리 생성: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("이력서 업로드 디렉토리 생성 실패", e);
        }
    }
    
    @Transactional
    public Resume sendResume(Long workingId, String senderUsername, MultipartFile file, String message) 
            throws IOException, DataNotFoundException {
        
        logger.info("이력서 전송 시작 - workingId: {}, sender: {}, fileName: {}", 
                   workingId, senderUsername, file.getOriginalFilename());
        
        // 사용자 및 구인 게시글 조회
        SiteUser sender = userService.getUser(senderUsername);
        Working working = workingService.getWorking(workingId);
        SiteUser receiver = working.getAuthor();
        
        // 이미 이력서를 보냈는지 확인
        if (resumeRepository.existsBySenderAndWorking(sender, working)) {
            throw new IllegalStateException("이미 이력서를 전송했습니다.");
        }
        
        // 파일 검증
        validateFile(file);
        
        // 파일 저장
        String storedFileName = saveFile(file);
        
        // 이력서 엔티티 생성 및 저장
        Resume resume = new Resume();
        resume.setSender(sender);
        resume.setReceiver(receiver);
        resume.setWorking(working);
        resume.setOriginalFileName(file.getOriginalFilename());
        resume.setStoredFileName(storedFileName);
        resume.setFilePath(UPLOAD_DIR + storedFileName);
        resume.setFileSize(file.getSize());
        resume.setContentType(file.getContentType());
        resume.setMessage(message);
        resume.setSendDate(LocalDateTime.now());
        resume.setRead(false);
        
        Resume savedResume = resumeRepository.save(resume);
        
        logger.info("이력서 전송 완료 - resumeId: {}", savedResume.getId());
        return savedResume;
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 파일 크기 검증 (10MB 제한)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 허용된 확장자 기반 검증 (contentType 신뢰 어려움 대비)
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new IllegalArgumentException("확장자를 확인할 수 없습니다. 문서 파일만 업로드 가능합니다.");
        }
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();

        // 문서 포맷 화이트리스트
        List<String> allowedExtensions = List.of(
                "pdf", "hwp", "hwpx", "doc", "docx", "ppt", "pptx", "rtf", "txt", "odt", "xls", "xlsx", "csv"
        );

        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (문서 파일만 가능: PDF, HWP/HWPX, DOC/DOCX, PPT/PPTX, TXT, RTF, ODT, XLS/XLSX, CSV)");
        }
    }
    
    private String saveFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        // 고유한 파일명 생성
        String storedFileName = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(UPLOAD_DIR, storedFileName);
        
        // 파일 저장
        Files.copy(file.getInputStream(), filePath);
        
        logger.info("파일 저장 완료: {}", filePath.toAbsolutePath());
        return storedFileName;
    }
    
    public List<Resume> getReceivedResumes(String username) throws DataNotFoundException {
        SiteUser user = userService.getUser(username);
        return resumeRepository.findByReceiverOrderBySendDateDesc(user);
    }
    
    public List<Resume> getSentResumes(String username) throws DataNotFoundException {
        SiteUser user = userService.getUser(username);
        return resumeRepository.findBySenderOrderBySendDateDesc(user);
    }
    
    public List<Resume> getResumesByWorking(Long workingId) throws DataNotFoundException {
        Working working = workingService.getWorking(workingId);
        return resumeRepository.findByWorkingOrderBySendDateDesc(working);
    }
    
    public Resume getResume(Long resumeId) throws DataNotFoundException {
        return resumeRepository.findById(resumeId)
                .orElseThrow(() -> new DataNotFoundException("이력서를 찾을 수 없습니다."));
    }
    
    @Transactional
    public void markAsRead(Long resumeId) throws DataNotFoundException {
        Resume resume = getResume(resumeId);
        resume.setRead(true);
        resumeRepository.save(resume);
    }
    
    public long getUnreadResumeCount(String username) throws DataNotFoundException {
        SiteUser user = userService.getUser(username);
        return resumeRepository.countUnreadResumesByReceiver(user);
    }
    
    public boolean hasAlreadySentResume(String username, Long workingId) throws DataNotFoundException {
        SiteUser user = userService.getUser(username);
        Working working = workingService.getWorking(workingId);
        return resumeRepository.existsBySenderAndWorking(user, working);
    }
    
    @Transactional
    public Resume sendResumeToOffer(Long offerId, String senderUsername, MultipartFile file, String message) 
            throws IOException, DataNotFoundException {
        
        logger.info("오퍼에 이력서 전송 시작 - offerId: {}, sender: {}, fileName: {}", 
                   offerId, senderUsername, file.getOriginalFilename());
        
        // 사용자 및 오퍼 게시글 조회
        SiteUser sender = userService.getUser(senderUsername);
        Offer offer = offerService.getOffer(offerId).orElseThrow(() -> new DataNotFoundException("오퍼를 찾을 수 없습니다."));
        SiteUser receiver = offer.getAuthor();
        
        // 이미 이력서를 보냈는지 확인
        if (resumeRepository.existsBySenderAndOffer(sender, offer)) {
            throw new IllegalStateException("이미 이력서를 전송했습니다.");
        }
        
        // 파일 검증
        validateFile(file);
        
        // 파일 저장
        String storedFileName = saveFile(file);
        
        // 이력서 엔티티 생성 및 저장
        Resume resume = new Resume();
        resume.setSender(sender);
        resume.setReceiver(receiver);
        resume.setOffer(offer);
        resume.setOriginalFileName(file.getOriginalFilename());
        resume.setStoredFileName(storedFileName);
        resume.setFilePath(UPLOAD_DIR + storedFileName);
        resume.setFileSize(file.getSize());
        resume.setContentType(file.getContentType());
        resume.setMessage(message);
        resume.setSendDate(LocalDateTime.now());
        resume.setRead(false);
        
        Resume savedResume = resumeRepository.save(resume);
        
        logger.info("오퍼에 이력서 전송 완료 - resumeId: {}", savedResume.getId());
        return savedResume;
    }
    
    public boolean hasAlreadySentResumeToOffer(String username, Long offerId) throws DataNotFoundException {
        SiteUser user = userService.getUser(username);
        Offer offer = offerService.getOffer(offerId).orElseThrow(() -> new DataNotFoundException("오퍼를 찾을 수 없습니다."));
        return resumeRepository.existsBySenderAndOffer(user, offer);
    }
    
    public File getResumeFile(Long resumeId) throws DataNotFoundException {
        Resume resume = getResume(resumeId);
        Path filePath = Paths.get(resume.getFilePath());
        
        if (!Files.exists(filePath)) {
            throw new DataNotFoundException("이력서 파일을 찾을 수 없습니다.");
        }
        
        return filePath.toFile();
    }
    
    @Transactional
    public void deleteResume(Long resumeId, String username) throws DataNotFoundException {
        Resume resume = getResume(resumeId);
        SiteUser user = userService.getUser(username);
        
        // 삭제 권한 확인 (발신자만 삭제 가능)
        if (!resume.getSender().getId().equals(user.getId())) {
            throw new IllegalStateException("이력서를 삭제할 권한이 없습니다.");
        }
        
        // 파일 삭제
        try {
            Path filePath = Paths.get(resume.getFilePath());
            Files.deleteIfExists(filePath);
            logger.info("이력서 파일 삭제 완료: {}", filePath);
        } catch (IOException e) {
            logger.error("이력서 파일 삭제 실패", e);
        }
        
        // 데이터베이스에서 삭제
        resumeRepository.delete(resume);
        logger.info("이력서 삭제 완료 - resumeId: {}", resumeId);
    }
} 