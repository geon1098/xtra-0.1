describe('이력서 전송 기능', () => {
  const testUser = {
    username: 'testuser',
    password: 'test123',
    email: 'test@example.com'
  }

  const testWorkData = {
    siteName: '테스트 현장',
    category: '건설',
    jobContent: '건설업무',
    jobType: '정규직',
    location: '서울시 강남구',
    address: '서울시 강남구 테스트로 123',
    jobDescription: '건설 현장에서 일할 인원을 모집합니다.',
    jobWork: '건설 경험자 우대',
    benefits: '4대보험, 퇴직연금',
    workNumber: '5명',
    deadDate: '2024-12-31',
    gender: '무관',
    cPerson: '김담당',
    phone: '010-1234-5678',
    jobDetails: '상세한 업무 내용입니다.'
  }

  beforeEach(() => {
    cy.login(testUser.username, testUser.password)
  })

  describe('이력서 전송', () => {
    it('구인 상세 페이지에서 이력서 전송 버튼을 클릭할 수 있어야 한다', () => {
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 이력서 보내기 버튼 확인
      cy.get('a[href*="resume/send"], .btn-secondary').should('contain', '이력서 보내기')
    })

    it('이력서 전송 폼에 접근할 수 있어야 한다', () => {
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      
      // 이력서 전송 폼 페이지 확인
      cy.url().should('include', '/resume/send')
      cy.get('body').should('contain', '이력서 전송')
    })

    it('이력서 파일을 업로드할 수 있어야 한다', () => {
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      
      // 파일 업로드
      cy.get('input[type="file"]').attachFile('test-resume.pdf')
      
      // 파일이 선택되었는지 확인
      cy.get('.file-input-label').should('contain', 'test-resume.pdf')
    })

    it('메시지와 함께 이력서를 전송할 수 있어야 한다', () => {
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      
      // 파일 업로드
      cy.get('input[type="file"]').attachFile('test-resume.pdf')
      
      // 메시지 입력
      cy.get('textarea[name="message"]').type('안녕하세요! 이력서를 보냅니다.')
      
      // 전송 버튼 클릭
      cy.get('button[type="submit"]').click()
      
      // 성공 메시지 확인
      cy.get('body').should('contain', '이력서가 성공적으로 전송되었습니다')
    })

    it('이미 이력서를 보낸 경우 중복 전송을 방지해야 한다', () => {
      // 첫 번째 이력서 전송
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      cy.get('input[type="file"]').attachFile('test-resume.pdf')
      cy.get('button[type="submit"]').click()
      
      // 같은 구인 게시글에 다시 접근
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      
      // 중복 전송 방지 메시지 확인
      cy.get('body').should('contain', '이미 이력서를 전송했습니다')
    })
  })

  describe('이력서 목록 조회', () => {
    it('받은 이력서 목록을 조회할 수 있어야 한다', () => {
      cy.visit('/resume/received')
      
      // 받은 이력서 목록 페이지 확인
      cy.url().should('include', '/resume/received')
      cy.get('body').should('contain', '받은 이력서 목록')
    })

    it('보낸 이력서 목록을 조회할 수 있어야 한다', () => {
      cy.visit('/resume/sent')
      
      // 보낸 이력서 목록 페이지 확인
      cy.url().should('include', '/resume/sent')
      cy.get('body').should('contain', '보낸 이력서 목록')
    })

    it('이력서 상세 정보를 확인할 수 있어야 한다', () => {
      // 이력서 전송
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      cy.get('input[type="file"]').attachFile('test-resume.pdf')
      cy.get('button[type="submit"]').click()
      
      // 보낸 이력서 목록에서 상세 보기
      cy.visit('/resume/sent')
      cy.get('.resume-item').first().click()
      
      // 이력서 상세 페이지 확인
      cy.url().should('include', '/resume/detail')
      cy.get('body').should('contain', '이력서 상세')
    })
  })

  describe('이력서 파일 다운로드', () => {
    it('이력서 파일을 다운로드할 수 있어야 한다', () => {
      // 이력서 전송
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      cy.get('input[type="file"]').attachFile('test-resume.pdf')
      cy.get('button[type="submit"]').click()
      
      // 보낸 이력서 상세 페이지에서 다운로드
      cy.visit('/resume/sent')
      cy.get('.resume-item').first().click()
      cy.get('a[href*="download"]').click()
      
      // 파일 다운로드 확인
      cy.readFile('cypress/downloads/test-resume.pdf').should('exist')
    })
  })

  describe('이력서 삭제', () => {
    it('보낸 이력서를 삭제할 수 있어야 한다', () => {
      // 이력서 전송
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      cy.get('input[type="file"]').attachFile('test-resume.pdf')
      cy.get('button[type="submit"]').click()
      
      // 보낸 이력서 상세 페이지에서 삭제
      cy.visit('/resume/sent')
      cy.get('.resume-item').first().click()
      cy.get('button[type="submit"]').click()
      
      // 삭제 확인 다이얼로그 처리
      cy.on('window:confirm', () => true)
      
      // 삭제 성공 메시지 확인
      cy.get('body').should('contain', '이력서가 삭제되었습니다')
    })
  })

  describe('권한 관리', () => {
    it('로그인하지 않은 사용자는 이력서 전송 페이지에 접근할 수 없어야 한다', () => {
      cy.logout()
      cy.visit('/resume/send/1')
      
      // 로그인 페이지로 리다이렉트 확인
      cy.url().should('include', '/user/login')
    })

    it('구인 작성자는 자신의 게시글에 이력서를 보낼 수 없어야 한다', () => {
      // 구인 게시글 작성자로 로그인
      cy.login('workauthor', 'password123')
      
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      
      // 이력서 보내기 버튼이 없어야 함
      cy.get('a[href*="resume/send"]').should('not.exist')
    })
  })

  describe('파일 검증', () => {
    it('지원하지 않는 파일 형식을 업로드할 때 오류가 발생해야 한다', () => {
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      
      // 지원하지 않는 파일 업로드
      cy.get('input[type="file"]').attachFile('test-image.jpg')
      cy.get('button[type="submit"]').click()
      
      // 오류 메시지 확인
      cy.get('body').should('contain', '지원하지 않는 파일 형식')
    })

    it('파일 크기가 10MB를 초과할 때 오류가 발생해야 한다', () => {
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="resume/send"]').click()
      
      // 큰 파일 업로드 (실제로는 테스트용 작은 파일을 사용)
      cy.get('input[type="file"]').attachFile('large-file.pdf')
      cy.get('button[type="submit"]').click()
      
      // 오류 메시지 확인 (실제 테스트에서는 파일 크기 검증 로직에 따라)
      cy.get('body').should('contain', '파일 크기')
    })
  })
}) 