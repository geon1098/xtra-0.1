package com.mysite.xtra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class XtraApplication {

	public static void main(String[] args) {
		// .env 파일 로드
		Dotenv dotenv = Dotenv.configure()
							  .directory("src/main/resources") // .env 파일 경로 지정
							  .ignoreIfMissing() // .env 파일이 없어도 에러를 발생시키지 않음
							  .load();

		// .env 파일의 변수들을 시스템 속성으로 설정
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		
		SpringApplication.run(XtraApplication.class, args);
	}

}
