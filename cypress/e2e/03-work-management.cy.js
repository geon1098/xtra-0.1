describe('구직 기능 테스트', () => {
  const testUser = {
    username: 'testuser',
    password: 'test123'
  }

  const testWorkData = {
    siteName: '테스트 회사',
    title: '웹 개발자 구합니다',
    category: 'IT/개발',
    jobContent: '웹 개발 업무',
    jobType: '정규직',
    benefits: '4대보험, 퇴직연금',
    location: '서울시 강남구',
    jobDescription: 'React, Spring Boot 개발',
    jobWork: '프론트엔드, 백엔드 개발',
    deadDate: '2024-12-31',
    workNumber: '5명',
    gender: '무관',
    age: '25-35세',
    address: '서울시 강남구 테헤란로',
    jobDetails: '상세 업무 내용입니다.',
    cPerson: '김담당자',
    phone: '010-1234-5678'
  }

  beforeEach(() => {
    cy.visit('/')
  })

  describe('구직글 목록 조회', () => {
    it('구직글 목록 페이지에 접근할 수 있어야 한다', () => {
      cy.visit('/work/list')
      
      // 페이지 제목 확인
      cy.get('h1, h2, h3').should('contain', '구직')
      
      // 구직글 목록이 표시되어야 함
      cy.get('.work-list, .list-group, [class*="list"], [class*="card"]').should('exist')
    })

    it('구직글 검색이 작동해야 한다', () => {
      cy.visit('/work/list')
      
      // 검색창이 존재해야 함
      cy.get('input[name="kw"], input[placeholder*="검색"], input[type="search"]').should('exist')
      
      // 검색어 입력
      cy.get('input[name="kw"], input[placeholder*="검색"], input[type="search"]').type('개발자')
      cy.get('button[type="submit"], .btn-search, [class*="search"]').click()
      
      // 검색 결과 확인
      cy.url().should('include', 'kw=개발자')
    })

    it('페이지네이션이 작동해야 한다', () => {
      cy.visit('/work/list')
      
      // 페이지네이션 요소 확인
      cy.get('.pagination, .page-item, [class*="page"]').should('exist')
      
      // 다음 페이지로 이동 (페이지네이션이 있는 경우)
      cy.get('.pagination .page-item:not(.active) a').first().click()
      
      // URL에 페이지 파라미터가 포함되어야 함
      cy.url().should('include', 'page=')
    })

    it('오퍼 섹션이 표시되어야 한다', () => {
      cy.visit('/work/list')
      
      // 프리미엄, 익스퍼트, VIP 오퍼 섹션 확인
      cy.get('body').should('contain', '프리미엄')
      cy.get('body').should('contain', '익스퍼트')
      cy.get('body').should('contain', 'VIP')
    })
  })

  describe('구직글 상세 조회', () => {
    it('구직글 상세 페이지에 접근할 수 있어야 한다', () => {
      // 먼저 목록에서 첫 번째 구직글 클릭
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 상세 페이지 요소들 확인
      cy.get('h1, h2, h3').should('exist')
      cy.get('.work-detail, .detail-content, [class*="detail"]').should('exist')
    })

    it('구직글 상세 정보가 올바르게 표시되어야 한다', () => {
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 구직글 정보 요소들 확인
      cy.get('body').should('contain', '회사명')
      cy.get('body').should('contain', '연락처')
      cy.get('body').should('contain', '주소')
    })

    it('카카오 지도가 표시되어야 한다', () => {
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 카카오 지도 요소 확인
      cy.get('#map, .map, [id*="map"], [class*="map"]').should('exist')
    })
  })

  describe('구직글 작성', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('로그인한 사용자가 구직글을 작성할 수 있어야 한다', () => {
      cy.visit('/work/create')
      
      // 구직글 작성 폼이 표시되어야 함
      cy.get('h1, h2, h3').should('contain', '구직글')
      cy.get('form').should('exist')
      
      // 필수 필드들 확인
      cy.get('#siteName, input[name="siteName"]').should('exist')
      cy.get('#title, input[name="title"]').should('exist')
      cy.get('#phone, input[name="phone"]').should('exist')
    })

    it('구직글 작성이 성공적으로 완료되어야 한다', () => {
      cy.visit('/work/create')
      
      // 구직글 정보 입력
      cy.get('#siteName, input[name="siteName"]').type(testWorkData.siteName)
      cy.get('#title, input[name="title"]').type(testWorkData.title)
      cy.get('#category, select[name="category"]').select(testWorkData.category)
      cy.get('#jobContent, textarea[name="jobContent"], input[name="jobContent"]').type(testWorkData.jobContent)
      cy.get('#jobType, select[name="jobType"]').select(testWorkData.jobType)
      cy.get('#benefits, textarea[name="benefits"], input[name="benefits"]').type(testWorkData.benefits)
      cy.get('#location, input[name="location"]').type(testWorkData.location)
      cy.get('#jobDescription, textarea[name="jobDescription"]').type(testWorkData.jobDescription)
      cy.get('#jobWork, textarea[name="jobWork"], input[name="jobWork"]').type(testWorkData.jobWork)
      cy.get('#deadDate, input[name="deadDate"]').type(testWorkData.deadDate)
      cy.get('#workNumber, input[name="workNumber"]').type(testWorkData.workNumber)
      cy.get('#gender, select[name="gender"]').select(testWorkData.gender)
      cy.get('#age, input[name="age"]').type(testWorkData.age)
      cy.get('#address, input[name="address"]').type(testWorkData.address)
      cy.get('#jobDetails, textarea[name="jobDetails"]').type(testWorkData.jobDetails)
      cy.get('#cPerson, input[name="cPerson"]').type(testWorkData.cPerson)
      cy.get('#phone, input[name="phone"]').type(testWorkData.phone)
      
      // 제출
      cy.get('button[type="submit"]').click()
      
      // 성공 메시지 확인
      cy.checkMessage('게시글이 성공적으로 등록되었습니다')
      
      // 목록 페이지로 리다이렉트 확인
      cy.url().should('include', '/work/list')
    })

    it('필수 필드 누락 시 유효성 검사 오류가 발생해야 한다', () => {
      cy.visit('/work/create')
      
      // 빈 폼 제출
      cy.get('button[type="submit"]').click()
      
      // 유효성 검사 오류 메시지 확인
      cy.get('.invalid-feedback, .error, [class*="error"]').should('exist')
    })
  })

  describe('구직글 수정', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('본인이 작성한 구직글을 수정할 수 있어야 한다', () => {
      // 먼저 구직글 작성
      cy.createWorkPost(testWorkData)
      
      // 작성된 구직글 상세 페이지로 이동
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 수정 버튼 클릭
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').click()
      
      // 수정 폼이 표시되어야 함
      cy.get('form').should('exist')
      cy.get('h1, h2, h3').should('contain', '수정')
    })

    it('구직글 수정이 성공적으로 완료되어야 한다', () => {
      // 먼저 구직글 작성
      cy.createWorkPost(testWorkData)
      
      // 작성된 구직글 수정
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').click()
      
      // 수정 내용 입력
      cy.get('#jobDetails, textarea[name="jobDetails"]').clear().type('수정된 상세 업무 내용입니다.')
      
      // 제출
      cy.get('button[type="submit"]').click()
      
      // 상세 페이지로 리다이렉트 확인
      cy.url().should('include', '/work/detail')
      
      // 수정된 내용 확인
      cy.get('body').should('contain', '수정된 상세 업무 내용입니다.')
    })
  })

  describe('구직글 삭제', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('본인이 작성한 구직글을 삭제할 수 있어야 한다', () => {
      // 먼저 구직글 작성
      cy.createWorkPost(testWorkData)
      
      // 작성된 구직글 상세 페이지로 이동
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 삭제 버튼 클릭
      cy.get('button[onclick*="delete"], .btn-delete, [class*="delete"], form[action*="delete"] button').click()
      
      // 확인 다이얼로그 처리 (있는 경우)
      cy.on('window:confirm', () => true)
      
      // 목록 페이지로 리다이렉트 확인
      cy.url().should('include', '/work/list')
    })
  })



  describe('권한 관리', () => {
    it('로그인하지 않은 사용자는 구직글 작성 페이지에 접근할 수 없어야 한다', () => {
      cy.visit('/work/create')
      
      // 로그인 페이지로 리다이렉트되어야 함
      cy.url().should('include', '/user/login')
    })

    it('다른 사용자가 작성한 구직글을 수정할 수 없어야 한다', () => {
      // 다른 사용자로 로그인
      cy.login('otheruser', 'other123')
      
      // 다른 사용자가 작성한 구직글 상세 페이지로 이동
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 수정 버튼이 없어야 함
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').should('not.exist')
    })

    it('다른 사용자가 작성한 구직글을 삭제할 수 없어야 한다', () => {
      // 다른 사용자로 로그인
      cy.login('otheruser', 'other123')
      
      // 다른 사용자가 작성한 구직글 상세 페이지로 이동
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 삭제 버튼이 없어야 함
      cy.get('button[onclick*="delete"], .btn-delete, [class*="delete"]').should('not.exist')
    })
  })

  describe('네비게이션', () => {
    it('구직글 목록에서 홈으로 돌아갈 수 있어야 한다', () => {
      cy.visit('/work/list')
      
      // 홈 링크 클릭
      cy.get('a[href="/"], a[href*="home"], .navbar-brand, [class*="home"]').click()
      
      // 홈 페이지로 이동 확인
      cy.url().should('eq', Cypress.config().baseUrl + '/')
    })

    it('구직글 상세에서 목록으로 돌아갈 수 있어야 한다', () => {
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 목록으로 돌아가기 버튼 클릭
      cy.get('a[href*="list"], .btn-back, [class*="back"], [class*="list"]').click()
      
      // 목록 페이지로 이동 확인
      cy.url().should('include', '/work/list')
    })
  })

  describe('구직 정보 페이지', () => {
    it('구직 정보 페이지에 접근할 수 있어야 한다', () => {
      cy.visit('/work/info')
      
      // 구직 정보 페이지 요소들 확인
      cy.get('h1, h2, h3').should('contain', '구직')
      cy.get('body').should('contain', '정보')
    })
  })
}) 