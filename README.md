# 구인/구직 광고 부동산 플랫폼 (4/8 ~ )
---

## 📝 프로젝트 프롤로그: 구인구직 광고 플랫폼

많은 사람들이 구직 정보를 얻기 위해 **알바몬**, **상가114**, **분양라인**과 같은 사이트를 찾습니다. 하지만 이들 플랫폼은 각각의 강점은 있지만,
사용자가 **원하는 정보를 쉽고 빠르게 찾고, 직접적으로 연결되는 구조**는 부족하다고 느꼈습니다.

저는 "사용자 중심의 간편하고 직관적인 구인구직 플랫폼은 없을까?"라는 물음에서 프로젝트를 시작했습니다.

### 🔍 벤치마킹

* **알바몬의 ‘간편 문자 보내기’ 기능**을 참고하여, 이력서를 구인 상세페이지에서 바로 이력서를 보낼수있고 확인할수있게 하는 기능을 도입했습니다.
* **상가114**처럼 지역 기반으로 정보를 조회할 수 있도록 구성했고,
* **분양라인**의 광고 형태에서 착안하여 시각적으로 보기 쉬운 **공고형 카드 디자인**을 적용했습니다.

### 🎯 기획 의도

> “채용자는 쉽게 공고를 등록하고,
> 구직자는 복잡한 회원가입 없이 필요한 정보를 한눈에 찾고,
> 양쪽 모두가 이력서 한 통으로 연결될 수 있다면 어떨까?”

이 프로젝트는 그러한 고민에서 출발했습니다.
기존 사이트들의 복잡한 절차와 정보를 줄이고, \*\*실용적인 사용자 경험(UX)\*\*에 집중했습니다.
## 🚀 사용기술

#### 🛠️ Backend  
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring MVC](https://img.shields.io/badge/Spring_MVC-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Validation](https://img.shields.io/badge/Validation-ff9800?style=for-the-badge&logo=checkmarx&logoColor=white)
![Mail](https://img.shields.io/badge/Mail-CA4245?style=for-the-badge&logo=gmail&logoColor=white)

#### 🎨 Frontend  
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![WebSocket](https://img.shields.io/badge/WebSocket-000000?style=for-the-badge&logo=websockets&logoColor=white)
![STOMP](https://img.shields.io/badge/STOMP-FF6F00?style=for-the-badge&logo=stomp&logoColor=white)
![Kakao API](https://img.shields.io/badge/Kakao_API-FFCD00?style=for-the-badge&logo=kakao&logoColor=black)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)

#### 🗄️ Database  
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

---

## 📸 주요 기능별 화면 및 기술 설명

---

<details>
<summary>구인/구직 게시판 화면</summary>

![구인게시판 전체](https://github.com/user-attachments/assets/0a0ee067-8b2b-40df-87d1-8bb0f101b4ee)
![구직 게시판](https://github.com/user-attachments/assets/2cbb77d6-5c0c-4c70-a719-5d0160e74232)

**기술 및 구현 포인트**
- **Spring Boot + JPA**로 구인/구직 게시글 CRUD 구현
- **검색/필터/페이징**: JPA Specification, Pageable, QueryDSL 활용
- **Thymeleaf**로 동적 HTML 렌더링, 게시글 리스트/카드형 UI
- **CSS Grid/Flexbox**로 반응형 카드 디자인
- **카테고리/지역 필터**: 프론트에서 select, 백엔드에서 동적 쿼리 처리

</details>

---

<details>
<summary>구인/구직 상세 화면</summary>

![구인상세이력서](https://github.com/user-attachments/assets/c57a97e3-3e0b-45bd-b773-578cd1b4f00a)

![구인상세이력서](https://github.com/user-attachments/assets/f4f2a1b9-7248-4957-975b-d07806611e23)

![구직상세](https://github.com/user-attachments/assets/1ae0ad63-b4d9-495f-a79f-34a71c7c99df)





**기술 및 구현 포인트**
- **Spring MVC**로 상세 페이지 라우팅 및 데이터 바인딩
- **JPA**로 게시글/작성자/댓글 등 연관관계 매핑
- **Thymeleaf**로 상세 정보, 지원/신청 버튼 등 동적 처리
- **권한 체크**: Spring Security로 본인/관리자만 수정/삭제 가능
- **지도 연동**: Kakao Map API로 위치 표시
- **이력서 보내기**: 구인글 사용자에게 이력서(hwp,docx,pdf 등) 
</details>

---

<details>
<summary>구인 이력서 보내기</summary>

![구인상세이력서보내기](https://github.com/user-attachments/assets/818c31f7-7434-4141-8929-8930e3afb037)
![이력서 상세](https://github.com/user-attachments/assets/219b4365-9e90-4f74-b4df-f122d2c9953b)

- **이력서 보내기**: 구직자는 구인글(Working) 또는 유료 오퍼(Offer) 상세에서 이력서 파일과 메시지를 첨부해 전송, 동일 게시글에 중복 전송 방지
- **받은/보낸 이력서**: 발신자는 보낸 이력서 목록/상세 확인 및 삭제 가능, 수신자는 받은 이력서 목록/상세에서 확인·다운로드, 읽음 처리

</details>

---

<details>
<summary>구인 등록/수정 화면</summary>

![구인 등록 유료](https://github.com/user-attachments/assets/835dd4a2-bb77-49b8-b666-8743badc8dff)
![구인등록](https://github.com/user-attachments/assets/897ed99d-91ee-4b13-921d-ca88b8ff96b8)
![구인 수정 유료](https://github.com/user-attachments/assets/f8764311-5083-486d-abf0-66477d485c53)
![구인 수정 무료](https://github.com/user-attachments/assets/74920f76-6c18-44ea-be26-1fcb55afb617)

**기술 및 구현 포인트**
- **Spring Boot + JPA**로 게시글 등록/수정/삭제
- **Form Validation**: javax.validation, BindingResult, 커스텀 Validator
- **Thymeleaf**로 폼 렌더링 및 에러 메시지 표시
- **파일 업로드**: MultipartFile, S3/로컬 저장소 연동(선택)
- **유료/무료 구분**: DB 필드 및 비즈니스 로직 분기
- **지도 연동**: Kakao Map API로 위치 표시
</details>

---

<details>
<summary>구직 등록/수정 화면</summary>

![구직 등록](https://github.com/user-attachments/assets/738ab816-5b85-44a9-8442-51628704c931)
![구직수정](https://github.com/user-attachments/assets/80e490a5-a7bc-4d35-8aa8-93ee0579b8e4)

**기술 및 구현 포인트**
- **Spring Boot + JPA**로 구직자 프로필/이력서 등록/수정
- **폼 검증/에러 처리**: Validation, BindingResult
- **Thymeleaf**로 입력 폼, 미리보기, 수정 기능 구현

</details>

---

<details>
<summary>매물정보 게시글 화면</summary>

![매물게시판](https://github.com/user-attachments/assets/54948a6b-ca9e-458c-90fe-9ad76b6f2945)
![매물상세2](https://github.com/user-attachments/assets/175d203f-e5ff-4543-8a79-5fb2145b851d)

**기술 및 구현 포인트**
- **Spring Boot + JPA**로 매물정보 CRUD
- **Thymeleaf**로 매물 상세/목록 렌더링
- **이미지 업로드/썸네일**: 파일 업로드, 이미지 경로 관리

</details>

---

<details>
<summary>마이페이지 화면</summary>

![마이페이지](https://github.com/user-attachments/assets/13863d9c-155a-4375-bbda-4e7b60b6f880)
![마이페이지 게시글관리 목록](https://github.com/user-attachments/assets/639d477a-8063-44d0-be72-ac6cd914ab49)
![비밀번호 변경](https://github.com/user-attachments/assets/12e5d7cd-fb19-4e87-a076-2b645a95f828)
![마이페이지 회원정보수정](https://github.com/user-attachments/assets/42b27e27-73d0-4ec1-99a5-f86ad5432da5)

**기술 및 구현 포인트**
- **Spring Security**로 인증/인가, 마이페이지 접근 제어
- **JPA**로 사용자 정보/비밀번호/게시글 관리
- **Thymeleaf**로 마이페이지 UI, 비밀번호 변경, 회원정보 수정
- **결제내역/활동내역**: DB 연동, 리스트/상세 구현

</details>

---

<details>
<summary>로그인 화면</summary>

![로그인](https://github.com/user-attachments/assets/165eb072-437a-45b8-808e-2e1e115a741d)

**기술 및 구현 포인트**
- **Spring Security**로 로그인/로그아웃/세션 관리
- **비밀번호 암호화**: BCryptPasswordEncoder
- **Thymeleaf**로 로그인 폼, 에러 메시지 처리
- **JSON WEB TOKEN으로 인증 및 권한 처리

</details>

---

<details>
<summary>회원가입 화면</summary>

![회원가입](https://github.com/user-attachments/assets/e23c3718-032a-4a57-bd2f-6d8eaa16faba)

**기술 및 구현 포인트**
- **Spring Security + JPA**로 회원가입/이메일 인증
- **이메일 인증**: JavaMailSender, 토큰 생성/검증
- **폼 검증**: Validation, 중복 체크, 에러 메시지 표시

</details>

---

<details>
<summary>관리자 페이지 화면</summary>

![메인 관리자 페이지](https://github.com/user-attachments/assets/04d10dec-22c4-4bd8-948e-2264c062e7b6)
![마이페이지 승인 대기](https://github.com/user-attachments/assets/7c070153-4117-4ee9-b4b4-af23336326c8)
![관리자 페이지 승인된 게시글](https://github.com/user-attachments/assets/3184897f-9224-4c6a-9f2a-0988b9302765)

**기술 및 구현 포인트**
- **Spring Security**로 관리자 권한 분리
- **JPA**로 게시글/회원/결제내역 관리
- **Thymeleaf**로 관리자 대시보드, 승인/거절/통계 UI
- **상태별 카드/리스트**: 승인대기/승인/거절 등 상태별 분기 처리
- **관리자 아이디**: 서버실행 시, 관리자아이디 자동으로 생성 처리 편리한 관리자 로그인

</details>

---
## 💡 기술적 고민/해결 경험
- **JWT 인증/권한 문제**:  세션/JWT토큰 인증 처리
- **JPA N+1 문제**: fetch join, @EntityGraph, 쿼리 최적화 적용 
- **대용량 데이터 페이징**: Pageable, Slice, QueryDSL 활용
