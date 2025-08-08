package com.mysite.xtra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;

import com.mysite.xtra.user.SiteUser;
import com.mysite.xtra.user.UserService;
import com.mysite.xtra.user.UserRole;

@SpringBootApplication
@EnableScheduling
public class XtraApplication {

	public static void main(String[] args) {
		// .env 파일 로드
		Dotenv dotenv = Dotenv.configure()
							  .directory(".") // .env 파일 경로를 루트(최상위)로 변경
							  .ignoreIfMissing() // .env 파일이 없어도 에러를 발생시키지 않음
							  .load();

		// .env 파일의 변수들을 시스템 속성으로 설정
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		
		SpringApplication.run(XtraApplication.class, args);
	}

	@Bean
	public CommandLineRunner initializeAdmin(UserService userService, PasswordEncoder passwordEncoder) {
		return args -> {
			// 환경 변수에서 관리자 정보 가져오기 (기본값 설정)
			String adminUsername = System.getProperty("ADMIN_USERNAME", "admin");
			String adminPassword = System.getProperty("ADMIN_PASSWORD", "admin1234");
			String adminEmail = System.getProperty("ADMIN_EMAIL", "admin@xtra.com");
			String adminNickname = System.getProperty("ADMIN_NICKNAME", "관리자");
			String adminPhone = System.getProperty("ADMIN_PHONE", "010-0000-0000");
			
			// 관리자 계정이 이미 존재하는지 확인
			try {
				SiteUser existingUser = userService.getUser(adminUsername);
				// 기존 사용자가 있지만 관리자 권한이 없는 경우 권한 부여
				if (!"ROLE_ADMIN".equals(existingUser.getRole())) {
					existingUser.setRole(UserRole.ADMIN.getValue());
					existingUser.setEmailVerified(true); // 관리자 계정은 이메일 인증 완료로 설정
					SiteUser updatedUser = userService.save(existingUser);
					System.out.println("✅ 기존 사용자에게 관리자 권한이 부여되었습니다.");
					System.out.println("👤 사용자명: " + updatedUser.getUsername());
					System.out.println("🔑 비밀번호: " + adminPassword);
					System.out.println("🔐 권한: " + updatedUser.getRole());
					System.out.println("✅ 이메일 인증: " + updatedUser.isEmailVerified());
					System.out.println("⚠️  보안을 위해 로그인 후 비밀번호를 변경해주세요.");
					System.out.println("🔗 관리자 페이지: http://localhost:8083/admin/dashboard");
				} else {
					System.out.println("✅ 관리자 계정이 이미 존재합니다.");
					System.out.println("👤 사용자명: " + existingUser.getUsername());
					System.out.println("🔐 권한: " + existingUser.getRole());
					System.out.println("✅ 이메일 인증: " + existingUser.isEmailVerified());
				}
			} catch (Exception e) {
				// 관리자 계정이 없으면 생성
				try {
					SiteUser adminUser = new SiteUser();
					adminUser.setUsername(adminUsername);
					adminUser.setNickname(adminNickname);
					adminUser.setEmail(adminEmail);
					adminUser.setPhone(adminPhone);
					adminUser.setPassword(passwordEncoder.encode(adminPassword));
					adminUser.setRole(UserRole.ADMIN.getValue());
					adminUser.setCreateDate(java.time.LocalDateTime.now());
					adminUser.setEmailVerified(true);
					
					SiteUser savedAdmin = userService.save(adminUser);
					
					// 저장된 관리자 계정 정보 확인
					System.out.println("✅ 관리자 계정이 성공적으로 생성되었습니다!");
					System.out.println("👤 사용자명: " + savedAdmin.getUsername());
					System.out.println("📧 이메일: " + savedAdmin.getEmail());
					System.out.println("🔑 비밀번호: " + adminPassword);
					System.out.println("🔐 권한: " + savedAdmin.getRole());
					System.out.println("✅ 이메일 인증: " + savedAdmin.isEmailVerified());
					System.out.println("⚠️  보안을 위해 로그인 후 비밀번호를 변경해주세요.");
					System.out.println("🔗 관리자 페이지: http://localhost:8083/admin/dashboard");
				} catch (Exception ex) {
					System.err.println("❌ 관리자 계정 생성 중 오류가 발생했습니다: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		};
	}
}
