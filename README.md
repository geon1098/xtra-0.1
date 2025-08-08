# Xtra 프로젝트 Cypress 자동화 테스트

이 프로젝트는 Xtra 웹 애플리케이션의 모든 기능을 테스트하는 Cypress 자동화 테스트입니다.

## 📋 테스트 범위

### 1. 사용자 관리 기능 (`01-user-management.cy.js`)
- 회원가입 (성공/실패 케이스)
- 로그인/로그아웃
- 아이디/비밀번호 찾기
- 이메일 인증
- 마이페이지 기능
- 프로필 관리

### 2. 구인 기능 (`02-job-management.cy.js`)
- 구인글 목록 조회
- 구인글 검색 및 페이지네이션
- 구인글 상세 조회
- 구인글 작성/수정/삭제
- 권한 관리

### 3. 구직 기능 (`03-work-management.cy.js`)
- 구직글 목록 조회
- 구직글 검색 및 페이지네이션
- 구직글 상세 조회
- 구직글 작성/수정/삭제
- 채팅 기능 연동
- 카카오 지도 연동

### 4. 오퍼 기능 (`04-offer-management.cy.js`)
- 프리미엄/익스퍼트/VIP 오퍼 목록
- 오퍼 상세 조회
- 오퍼 작성/수정/삭제
- 이미지 파일 업로드
- 카카오 지도 연동

### 5. 관리자 기능 (`05-admin-management.cy.js`)
- 관리자 대시보드
- 오퍼 승인/거절
- 승인 대기/승인됨/거절됨 오퍼 관리
- 전체 오퍼 관리
- 권한 관리

### 6. 채팅 기능 (`06-chat-functionality.cy.js`)
- 채팅방 접근
- 실시간 메시지 전송/수신
- 채팅 기록 조회
- WebSocket 연결
- 채팅 UI 테스트

### 7. 통합 테스트 (`07-integration-tests.cy.js`)
- 전체 사용자 워크플로우
- 구직 및 채팅 워크플로우
- 오퍼 및 관리자 워크플로우
- 검색 및 네비게이션
- 에러 처리 및 예외 상황
- 성능 및 사용성 테스트

### 8. 트랜잭션 기능 (`08-transactional-tests.cy.js`) 🆕
- **데이터 자동 정리**: 테스트 실행 후 자동으로 생성된 데이터 정리
- **트랜잭션 롤백**: 테스트 실패 시에도 데이터 정리
- **테스트 격리**: 각 테스트가 독립적으로 실행
- **외래키 제약조건 고려**: 올바른 순서로 데이터 정리
- **성능 테스트**: 대량 데이터 처리 성능
- **에러 처리**: 데이터 정리 중 오류 발생 시 처리

## 🔄 트랜잭션 기능 (Spring @Transactional과 유사)

### 주요 특징
- **자동 데이터 정리**: 테스트 실행 후 생성된 모든 데이터를 자동으로 삭제
- **외래키 제약조건 고려**: 채팅메시지 → 오퍼 → 구직글 → 구인글 → 사용자 순서로 정리
- **테스트 격리**: 각 테스트가 독립적으로 실행되어 다른 테스트에 영향 없음
- **에러 처리**: 테스트 실패 시에도 데이터 정리 시도
- **성능 최적화**: 대량 데이터도 효율적으로 처리

### 동작 방식
1. **테스트 시작 전**: 환경 초기화 및 데이터 추적 시작
2. **테스트 실행 중**: 생성되는 모든 데이터를 자동으로 추적
3. **테스트 완료 후**: 추적된 데이터를 올바른 순서로 자동 삭제
4. **테스트 실패 시**: 실패 이벤트에서도 데이터 정리 시도

### 데이터 정리 순서
```
채팅 메시지 → 오퍼 → 구직글 → 구인글 → 사용자
```

## 🚀 설치 및 설정

### 1. 의존성 설치
```bash
npm install
```

### 2. Cypress 설치 (선택사항)
```bash
npx cypress install
```

### 3. 환경 설정
- `cypress.config.js`에서 `baseUrl`을 실제 서버 주소로 변경
- 테스트용 사용자 계정 정보 확인
- 관리자 계정 정보 확인

## 🧪 테스트 실행

### 전체 테스트 실행
```bash
npm run test:all
```

### 개별 기능별 테스트 실행
```bash
# 사용자 관리 테스트
npm run test:user

# 구인 기능 테스트
npm run test:job

# 구직 기능 테스트
npm run test:work

# 오퍼 기능 테스트
npm run test:offer

# 관리자 기능 테스트
npm run test:admin

# 채팅 기능 테스트
npm run test:chat

# 통합 테스트
npm run test:integration

# 트랜잭션 기능 테스트 🆕
npm run test:transactional
```

### 트랜잭션 기능 세부 테스트 🆕
```bash
# 데이터 자동 정리 테스트
npm run test:clean

# 트랜잭션 롤백 테스트
npm run test:rollback

# 테스트 격리 테스트
npm run test:isolation

# 성능 테스트
npm run test:performance

# 에러 처리 테스트
npm run test:error-handling
```

### 브라우저별 테스트 실행
```bash
# Chrome 브라우저
npm run cypress:run:chrome

# Firefox 브라우저
npm run cypress:run:firefox

# Edge 브라우저
npm run cypress:run:edge
```

### Cypress Test Runner 실행
```bash
npm run cypress:open
```

## 📁 프로젝트 구조

```
cypress/
├── e2e/
│   ├── 01-user-management.cy.js      # 사용자 관리 테스트
│   ├── 02-job-management.cy.js       # 구인 기능 테스트
│   ├── 03-work-management.cy.js      # 구직 기능 테스트
│   ├── 04-offer-management.cy.js     # 오퍼 기능 테스트
│   ├── 05-admin-management.cy.js     # 관리자 기능 테스트
│   ├── 06-chat-functionality.cy.js   # 채팅 기능 테스트
│   ├── 07-integration-tests.cy.js    # 통합 테스트
│   └── 08-transactional-tests.cy.js  # 트랜잭션 기능 테스트 🆕
├── fixtures/
│   └── test-image.jpg                # 테스트용 이미지 파일
├── support/
│   ├── commands.js                   # 커스텀 명령어 (트랜잭션 기능 포함)
│   └── e2e.js                        # 전역 설정 (자동 데이터 정리)
└── cypress.config.js                 # Cypress 설정
```

## 🔧 커스텀 명령어

### 트랜잭션 관련 🆕
- `cy.trackTestData(type, data)` - 테스트 데이터 추적
- `cy.cleanupTestData()` - 테스트 데이터 정리
- `cy.resetTestData()` - 테스트 데이터 초기화
- `cy.setupTestEnvironment()` - 테스트 환경 설정
- `cy.resetDatabase()` - 데이터베이스 초기화 (관리자용)

### 사용자 관련
- `cy.registerUser(userData)` - 사용자 등록 (데이터 추적 포함)
- `cy.login(username, password)` - 로그인
- `cy.adminLogin()` - 관리자 로그인
- `cy.userLogin()` - 일반 사용자 로그인
- `cy.logout()` - 로그아웃

### 게시글 관련
- `cy.createJobPost(jobData)` - 구인글 작성 (데이터 추적 포함)
- `cy.createWorkPost(workData)` - 구직글 작성 (데이터 추적 포함)
- `cy.createOffer(offerData)` - 오퍼 작성 (데이터 추적 포함)

### 유틸리티
- `cy.waitForPageLoad()` - 페이지 로딩 대기
- `cy.checkMessage(expectedMessage)` - 메시지 확인
- `cy.uploadFile(selector, fileName)` - 파일 업로드

## 📊 테스트 데이터

### 테스트 사용자
- **일반 사용자**: `testuser` / `test123`
- **관리자**: `admin` / `admin123`
- **통합 테스트용**: `integrationtest` / `test123`
- **트랜잭션 테스트용**: `transactiontest` / `test123` 🆕

### 테스트 데이터 예시
```javascript
const testJobData = {
  name: '테스트 구인자',
  gender: '남성',
  age: '25',
  phone: '010-1234-5678',
  email: 'test@example.com',
  address: '서울시 강남구',
  requestWork: '웹 개발자',
  // ... 기타 필드
}
```

## 🔄 트랜잭션 기능 상세 설명

### 자동 데이터 추적
```javascript
// 사용자 등록 시 자동으로 추적
cy.registerUser(userData) // 내부적으로 cy.trackTestData('users', username) 호출

// 구인글 작성 시 자동으로 추적
cy.createJobPost(jobData) // 내부적으로 생성된 ID를 추적
```

### 자동 데이터 정리
```javascript
// 각 테스트 후 자동 실행 (afterEach 훅)
cy.cleanupTestData() // 추적된 모든 데이터를 순서대로 삭제
```

### 테스트 격리
```javascript
// 각 테스트 전에 실행 (beforeEach 훅)
cy.setupTestEnvironment() // 환경 초기화 및 데이터 추적 리셋
```

## 🐛 문제 해결

### 일반적인 문제들

1. **테스트 실패 시 스크린샷 확인**
   - `cypress/screenshots/` 폴더에서 실패한 테스트의 스크린샷 확인

2. **비디오 녹화 확인**
   - `cypress/videos/` 폴더에서 테스트 실행 비디오 확인

3. **네트워크 오류**
   - 서버가 실행 중인지 확인
   - `baseUrl` 설정 확인

4. **요소를 찾을 수 없는 경우**
   - 실제 HTML 구조와 테스트 코드의 선택자 일치 여부 확인
   - 페이지 로딩 시간 조정

5. **데이터 정리 실패** 🆕
   - 로그인 상태 확인
   - 삭제 권한 확인
   - 외래키 제약조건 확인

### 디버깅 팁

1. **Cypress Test Runner 사용**
   ```bash
   npm run cypress:open
   ```

2. **특정 테스트만 실행**
   ```bash
   npx cypress run --spec "cypress/e2e/08-transactional-tests.cy.js"
   ```

3. **헤드리스 모드 비활성화**
   ```bash
   npm run cypress:run:headed
   ```

4. **트랜잭션 기능 디버깅** 🆕
   ```bash
   # 데이터 정리 로그 확인
   npm run test:clean

   # 롤백 기능 확인
   npm run test:rollback
   ```

## 📈 CI/CD 통합

### GitHub Actions 예시
```yaml
name: Cypress Tests
on: [push, pull_request]
jobs:
  cypress-run:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: cypress-io/github-action@v6
        with:
          start: npm start
          wait-on: 'http://localhost:8083'
          browser: chrome
          # 트랜잭션 기능으로 인한 데이터 정리 자동화
```

## 🤝 기여하기

1. 새로운 테스트 케이스 추가 시 해당 기능별 파일에 추가
2. 커스텀 명령어는 `cypress/support/commands.js`에 추가
3. 테스트 데이터는 각 테스트 파일 상단에 정의
4. 테스트 실행 전 서버가 정상 동작하는지 확인
5. 트랜잭션 기능 사용 시 데이터 추적 및 정리 로직 확인 🆕

## 📝 라이선스

MIT License

## 📞 문의

테스트 관련 문의사항이 있으시면 개발팀에 연락해주세요. 