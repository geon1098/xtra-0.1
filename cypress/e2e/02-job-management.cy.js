describe('구인 기능 테스트', () => {
  const testUser = {
    username: 'testuser',
    password: 'test123'
  }

  const testJobData = {
    name: '테스트 구인자',
    gender: '남성',
    age: '25',
    phone: '010-1234-5678',
    email: 'test@example.com',
    address: '서울시 강남구',
    requestWork: '웹 개발자',
    hopArea: '서울시 강남구',
    career: '3년',
    license: '정보처리기사',
    networking: '네트워킹 경험 있음',
    startDate: '2024-01-15',
    introduction: '안녕하세요. 웹 개발자를 구합니다.',
    etc: '기타 사항입니다.'
  }

  beforeEach(() => {
    cy.visit('/')
  })

  describe('구인글 목록 조회', () => {
    it('구인글 목록 페이지에 접근할 수 있어야 한다', () => {
      cy.visit('/job/list')
      
      // 페이지 제목 확인
      cy.get('h1, h2, h3').should('contain', '구인')
      
      // 구인글 목록이 표시되어야 함
      cy.get('.job-list, .list-group, [class*="list"], [class*="card"]').should('exist')
    })

    it('구인글 검색이 작동해야 한다', () => {
      cy.visit('/job/list')
      
      // 검색창이 존재해야 함
      cy.get('input[name="keyword"], input[placeholder*="검색"], input[type="search"]').should('exist')
      
      // 검색어 입력
      cy.get('input[name="keyword"], input[placeholder*="검색"], input[type="search"]').type('개발자')
      cy.get('button[type="submit"], .btn-search, [class*="search"]').click()
      
      // 검색 결과 확인
      cy.url().should('include', 'keyword=개발자')
    })

    it('페이지네이션이 작동해야 한다', () => {
      cy.visit('/job/list')
      
      // 페이지네이션 요소 확인
      cy.get('.pagination, .page-item, [class*="page"]').should('exist')
      
      // 다음 페이지로 이동 (페이지네이션이 있는 경우)
      cy.get('.pagination .page-item:not(.active) a').first().click()
      
      // URL에 페이지 파라미터가 포함되어야 함
      cy.url().should('include', 'page=')
    })
  })

  describe('구인글 상세 조회', () => {
    it('구인글 상세 페이지에 접근할 수 있어야 한다', () => {
      // 먼저 목록에서 첫 번째 구인글 클릭
      cy.visit('/job/list')
      cy.get('.job-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 상세 페이지 요소들 확인
      cy.get('h1, h2, h3').should('exist')
      cy.get('.job-detail, .detail-content, [class*="detail"]').should('exist')
    })

    it('구인글 상세 정보가 올바르게 표시되어야 한다', () => {
      cy.visit('/job/list')
      cy.get('.job-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 구인글 정보 요소들 확인
      cy.get('body').should('contain', '이름')
      cy.get('body').should('contain', '연락처')
      cy.get('body').should('contain', '주소')
    })
  })

  describe('구인글 작성', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('로그인한 사용자가 구인글을 작성할 수 있어야 한다', () => {
      cy.visit('/job/create')
      
      // 구인글 작성 폼이 표시되어야 함
      cy.get('h1, h2, h3').should('contain', '구인글')
      cy.get('form').should('exist')
      
      // 필수 필드들 확인
      cy.get('#name, input[name="name"]').should('exist')
      cy.get('#phone, input[name="phone"]').should('exist')
      cy.get('#email, input[name="email"]').should('exist')
    })

    it('구인글 작성이 성공적으로 완료되어야 한다', () => {
      cy.visit('/job/create')
      
      // 구인글 정보 입력
      cy.get('#name, input[name="name"]').type(testJobData.name)
      cy.get('#gender, select[name="gender"]').select(testJobData.gender)
      cy.get('#age, input[name="age"]').type(testJobData.age)
      cy.get('#phone, input[name="phone"]').type(testJobData.phone)
      cy.get('#email, input[name="email"]').type(testJobData.email)
      cy.get('#address, input[name="address"]').type(testJobData.address)
      cy.get('#requestWork, textarea[name="requestWork"], input[name="requestWork"]').type(testJobData.requestWork)
      cy.get('#hopArea, input[name="hopArea"]').type(testJobData.hopArea)
      cy.get('#career, input[name="career"]').type(testJobData.career)
      cy.get('#license, input[name="license"]').type(testJobData.license)
      cy.get('#networking, input[name="networking"]').type(testJobData.networking)
      cy.get('#startDate, input[name="startDate"]').type(testJobData.startDate)
      cy.get('#introduction, textarea[name="introduction"]').type(testJobData.introduction)
      cy.get('#etc, textarea[name="etc"], input[name="etc"]').type(testJobData.etc)
      
      // 제출
      cy.get('button[type="submit"]').click()
      
      // 성공 메시지 확인
      cy.checkMessage('구인글이 성공적으로 등록되었습니다')
      
      // 목록 페이지로 리다이렉트 확인
      cy.url().should('include', '/job/list')
    })

    it('필수 필드 누락 시 유효성 검사 오류가 발생해야 한다', () => {
      cy.visit('/job/create')
      
      // 빈 폼 제출
      cy.get('button[type="submit"]').click()
      
      // 유효성 검사 오류 메시지 확인
      cy.get('.invalid-feedback, .error, [class*="error"]').should('exist')
    })
  })

  describe('구인글 수정', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('본인이 작성한 구인글을 수정할 수 있어야 한다', () => {
      // 먼저 구인글 작성
      cy.createJobPost(testJobData)
      
      // 작성된 구인글 상세 페이지로 이동
      cy.visit('/job/list')
      cy.get('.job-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 수정 버튼 클릭
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').click()
      
      // 수정 폼이 표시되어야 함
      cy.get('form').should('exist')
      cy.get('h1, h2, h3').should('contain', '수정')
    })

    it('구인글 수정이 성공적으로 완료되어야 한다', () => {
      // 먼저 구인글 작성
      cy.createJobPost(testJobData)
      
      // 작성된 구인글 수정
      cy.visit('/job/list')
      cy.get('.job-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').click()
      
      // 수정 내용 입력
      cy.get('#introduction, textarea[name="introduction"]').clear().type('수정된 자기소개입니다.')
      
      // 제출
      cy.get('button[type="submit"]').click()
      
      // 상세 페이지로 리다이렉트 확인
      cy.url().should('include', '/job/detail')
      
      // 수정된 내용 확인
      cy.get('body').should('contain', '수정된 자기소개입니다.')
    })
  })

  describe('구인글 삭제', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('본인이 작성한 구인글을 삭제할 수 있어야 한다', () => {
      // 먼저 구인글 작성
      cy.createJobPost(testJobData)
      
      // 작성된 구인글 상세 페이지로 이동
      cy.visit('/job/list')
      cy.get('.job-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 삭제 버튼 클릭
      cy.get('button[onclick*="delete"], .btn-delete, [class*="delete"], form[action*="delete"] button').click()
      
      // 확인 다이얼로그 처리 (있는 경우)
      cy.on('window:confirm', () => true)
      
      // 목록 페이지로 리다이렉트 확인
      cy.url().should('include', '/job/list')
    })
  })

  describe('권한 관리', () => {
    it('로그인하지 않은 사용자는 구인글 작성 페이지에 접근할 수 없어야 한다', () => {
      cy.visit('/job/create')
      
      // 로그인 페이지로 리다이렉트되어야 함
      cy.url().should('include', '/user/login')
    })

    it('다른 사용자가 작성한 구인글을 수정할 수 없어야 한다', () => {
      // 다른 사용자로 로그인
      cy.login('otheruser', 'other123')
      
      // 다른 사용자가 작성한 구인글 상세 페이지로 이동
      cy.visit('/job/list')
      cy.get('.job-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 수정 버튼이 없어야 함
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').should('not.exist')
    })

    it('다른 사용자가 작성한 구인글을 삭제할 수 없어야 한다', () => {
      // 다른 사용자로 로그인
      cy.login('otheruser', 'other123')
      
      // 다른 사용자가 작성한 구인글 상세 페이지로 이동
      cy.visit('/job/list')
      cy.get('.job-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 삭제 버튼이 없어야 함
      cy.get('button[onclick*="delete"], .btn-delete, [class*="delete"]').should('not.exist')
    })
  })

  describe('네비게이션', () => {
    it('구인글 목록에서 홈으로 돌아갈 수 있어야 한다', () => {
      cy.visit('/job/list')
      
      // 홈 링크 클릭
      cy.get('a[href="/"], a[href*="home"], .navbar-brand, [class*="home"]').click()
      
      // 홈 페이지로 이동 확인
      cy.url().should('eq', Cypress.config().baseUrl + '/')
    })

    it('구인글 상세에서 목록으로 돌아갈 수 있어야 한다', () => {
      cy.visit('/job/list')
      cy.get('.job-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 목록으로 돌아가기 버튼 클릭
      cy.get('a[href*="list"], .btn-back, [class*="back"], [class*="list"]').click()
      
      // 목록 페이지로 이동 확인
      cy.url().should('include', '/job/list')
    })
  })
}) 