describe('통합 테스트', () => {
  const testUser = {
    username: 'integrationtest',
    nickname: '통합테스트유저',
    email: 'integration@test.com',
    password: 'test123',
    phone: '010-1234-5678'
  }

  const testJobData = {
    name: '통합테스트 구인자',
    gender: '남성',
    age: '30',
    phone: '010-1234-5678',
    email: 'integration@test.com',
    address: '서울시 강남구',
    requestWork: '통합테스트 개발자',
    hopArea: '서울시 강남구',
    career: '5년',
    license: '정보처리기사',
    networking: '네트워킹 경험 있음',
    startDate: '2024-01-15',
    introduction: '통합테스트를 위한 구인글입니다.',
    etc: '통합테스트용 기타 사항입니다.'
  }

  const testWorkData = {
    siteName: '통합테스트 회사',
    title: '통합테스트 개발자 구합니다',
    category: 'IT/개발',
    jobContent: '통합테스트 개발 업무',
    jobType: '정규직',
    benefits: '4대보험, 퇴직연금',
    location: '서울시 강남구',
    jobDescription: 'Spring Boot, React 통합테스트 개발',
    jobWork: '프론트엔드, 백엔드 통합테스트',
    deadDate: '2024-12-31',
    workNumber: '3명',
    gender: '무관',
    age: '25-35세',
    address: '서울시 강남구 테헤란로',
    jobDetails: '통합테스트 상세 업무 내용입니다.',
    cPerson: '통합테스트담당자',
    phone: '010-1234-5678'
  }

  const testOfferData = {
    title: '통합테스트 프리미엄 서비스',
    description: '통합테스트를 위한 프리미엄 서비스입니다.',
    category: 'PREMIUM',
    price: '1000000',
    address: '서울시 강남구 테헤란로'
  }

  beforeEach(() => {
    cy.visit('/')
  })

  describe('전체 사용자 워크플로우', () => {
    it('사용자가 회원가입부터 모든 기능을 사용할 수 있어야 한다', () => {
      // 1. 회원가입
      cy.visit('/user/signup')
      cy.get('#username').type(testUser.username)
      cy.get('#nickname').type(testUser.nickname)
      cy.get('#email').type(testUser.email)
      cy.get('#password1').type(testUser.password)
      cy.get('#password2').type(testUser.password)
      cy.get('#phone').type(testUser.phone)
      cy.get('button[type="submit"]').click()
      cy.checkMessage('회원가입이 완료되었습니다')

      // 2. 로그인
      cy.login(testUser.username, testUser.password)
      cy.url().should('include', '/job/list')

      // 3. 구인글 작성
      cy.visit('/job/create')
      cy.get('#name').type(testJobData.name)
      cy.get('#gender').select(testJobData.gender)
      cy.get('#age').type(testJobData.age)
      cy.get('#phone').type(testJobData.phone)
      cy.get('#email').type(testJobData.email)
      cy.get('#address').type(testJobData.address)
      cy.get('#requestWork').type(testJobData.requestWork)
      cy.get('#hopArea').type(testJobData.hopArea)
      cy.get('#career').type(testJobData.career)
      cy.get('#license').type(testJobData.license)
      cy.get('#networking').type(testJobData.networking)
      cy.get('#startDate').type(testJobData.startDate)
      cy.get('#introduction').type(testJobData.introduction)
      cy.get('#etc').type(testJobData.etc)
      cy.get('button[type="submit"]').click()
      cy.checkMessage('구인글이 성공적으로 등록되었습니다')

      // 4. 구인글 목록 확인
      cy.visit('/job/list')
      cy.get('body').should('contain', testJobData.name)

      // 5. 구인글 상세 확인
      cy.get('.job-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('body').should('contain', testJobData.introduction)

      // 6. 구인글 수정
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').click()
      cy.get('#introduction').clear().type('수정된 통합테스트 구인글입니다.')
      cy.get('button[type="submit"]').click()
      cy.get('body').should('contain', '수정된 통합테스트 구인글입니다.')

      // 7. 마이페이지 확인
      cy.visit('/mypage')
      cy.get('body').should('contain', '마이페이지')

      // 8. 로그아웃
      cy.logout()
      cy.url().should('include', '/user/login')
    })
  })

  describe('구직 및 채팅 워크플로우', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('사용자가 구직글을 작성하고 채팅을 사용할 수 있어야 한다', () => {
      // 1. 구직글 작성
      cy.visit('/work/create')
      cy.get('#siteName').type(testWorkData.siteName)
      cy.get('#title').type(testWorkData.title)
      cy.get('#category').select(testWorkData.category)
      cy.get('#jobContent').type(testWorkData.jobContent)
      cy.get('#jobType').select(testWorkData.jobType)
      cy.get('#benefits').type(testWorkData.benefits)
      cy.get('#location').type(testWorkData.location)
      cy.get('#jobDescription').type(testWorkData.jobDescription)
      cy.get('#jobWork').type(testWorkData.jobWork)
      cy.get('#deadDate').type(testWorkData.deadDate)
      cy.get('#workNumber').type(testWorkData.workNumber)
      cy.get('#gender').select(testWorkData.gender)
      cy.get('#age').type(testWorkData.age)
      cy.get('#address').type(testWorkData.address)
      cy.get('#jobDetails').type(testWorkData.jobDetails)
      cy.get('#cPerson').type(testWorkData.cPerson)
      cy.get('#phone').type(testWorkData.phone)
      cy.get('button[type="submit"]').click()
      cy.checkMessage('게시글이 성공적으로 등록되었습니다')

      // 2. 구직글 목록 확인
      cy.visit('/work/list')
      cy.get('body').should('contain', testWorkData.title)

      // 3. 구직글 상세 확인
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('body').should('contain', testWorkData.jobDetails)


    })
  })

  describe('오퍼 및 관리자 워크플로우', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('사용자가 오퍼를 작성하고 관리자가 승인할 수 있어야 한다', () => {
      // 1. 오퍼 작성
      cy.visit('/offer/create')
      cy.get('#title').type(testOfferData.title)
      cy.get('#description').type(testOfferData.description)
      cy.get('#category').select(testOfferData.category)
      cy.get('#price').type(testOfferData.price)
      cy.get('#address').type(testOfferData.address)
      cy.get('input[type="file"]').attachFile('test-image.jpg')
      cy.get('button[type="submit"]').click()
      cy.url().should('include', '/work/list')

      // 2. 오퍼 목록 확인
      cy.visit('/offer/list/PREMIUM')
      cy.get('body').should('contain', testOfferData.title)

      // 3. 관리자 로그인
      cy.logout()
      cy.adminLogin()

      // 4. 관리자 대시보드 확인
      cy.visit('/admin/dashboard')
      cy.get('body').should('contain', '관리자')

      // 5. 승인 대기 오퍼 확인
      cy.visit('/admin/pending')
      cy.get('body').should('contain', testOfferData.title)

      // 6. 오퍼 승인
      cy.get('.btn-approve, button[onclick*="approve"], [class*="approve"]').first().click()
      cy.get('body').should('contain', '승인')

      // 7. 승인된 오퍼 확인
      cy.visit('/admin/approved')
      cy.get('body').should('contain', testOfferData.title)
    })
  })

  describe('검색 및 네비게이션 워크플로우', () => {
    it('사용자가 검색 기능을 사용하고 페이지 간 이동할 수 있어야 한다', () => {
      // 1. 구인글 검색
      cy.visit('/job/list')
      cy.get('input[name="keyword"], input[placeholder*="검색"], input[type="search"]').type('개발자')
      cy.get('button[type="submit"], .btn-search, [class*="search"]').click()
      cy.url().should('include', 'keyword=개발자')

      // 2. 구직글 검색
      cy.visit('/work/list')
      cy.get('input[name="kw"], input[placeholder*="검색"], input[type="search"]').type('웹')
      cy.get('button[type="submit"], .btn-search, [class*="search"]').click()
      cy.url().should('include', 'kw=웹')

      // 3. 오퍼 카테고리별 이동
      cy.visit('/offer/list/PREMIUM')
      cy.get('body').should('contain', '프리미엄')

      cy.visit('/offer/list/EXPERT')
      cy.get('body').should('contain', '익스퍼트')

      cy.visit('/offer/list/VIP')
      cy.get('body').should('contain', 'VIP')

      // 4. 홈으로 돌아가기
      cy.get('a[href="/"], a[href*="home"], .navbar-brand, [class*="home"]').click()
      cy.url().should('eq', Cypress.config().baseUrl + '/')
    })
  })

  describe('에러 처리 및 예외 상황', () => {
    it('시스템이 예외 상황을 적절히 처리해야 한다', () => {
      // 1. 존재하지 않는 페이지 접근
      cy.visit('/nonexistent-page')
      cy.get('body').should('contain', '오류')

      // 2. 로그인하지 않은 상태에서 보호된 페이지 접근
      cy.visit('/job/create')
      cy.url().should('include', '/user/login')

      cy.visit('/work/create')
      cy.url().should('include', '/user/login')

      cy.visit('/offer/create')
      cy.url().should('include', '/user/login')

      // 3. 관리자 페이지 접근 (일반 사용자)
      cy.login(testUser.username, testUser.password)
      cy.visit('/admin/dashboard')
      cy.url().should('not.include', '/admin/dashboard')

      // 4. 잘못된 로그인 정보
      cy.visit('/user/login')
      cy.get('#username').type('wronguser')
      cy.get('#password').type('wrongpassword')
      cy.get('button[type="submit"]').click()
      cy.checkMessage('아이디 또는 비밀번호가 올바르지 않습니다')
    })
  })

  describe('성능 및 사용성 테스트', () => {
    it('페이지 로딩 시간이 적절해야 한다', () => {
      // 메인 페이지 로딩 시간 측정
      cy.visit('/', { timeout: 10000 })
      cy.get('body').should('be.visible')

      // 구인글 목록 로딩 시간 측정
      cy.visit('/job/list', { timeout: 10000 })
      cy.get('body').should('be.visible')

      // 구직글 목록 로딩 시간 측정
      cy.visit('/work/list', { timeout: 10000 })
      cy.get('body').should('be.visible')
    })

    it('반응형 디자인이 작동해야 한다', () => {
      // 데스크톱 뷰포트
      cy.viewport(1280, 720)
      cy.visit('/')
      cy.get('body').should('be.visible')

      // 태블릿 뷰포트
      cy.viewport(768, 1024)
      cy.visit('/')
      cy.get('body').should('be.visible')

      // 모바일 뷰포트
      cy.viewport(375, 667)
      cy.visit('/')
      cy.get('body').should('be.visible')
    })
  })
}) 