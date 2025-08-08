describe('사용자 관리 기능 테스트', () => {
  const testUser = {
    username: 'testuser',
    nickname: '테스트유저',
    email: 'test@example.com',
    password: 'test123',
    phone: '010-1234-5678'
  }

  const adminUser = {
    username: 'admin',
    password: 'admin123'
  }

  beforeEach(() => {
    cy.visit('/')
  })

  describe('회원가입 기능', () => {
    it('새로운 사용자 회원가입이 성공적으로 완료되어야 한다', () => {
      cy.visit('/user/signup')
      
      // 회원가입 폼 작성
      cy.get('#username').type(testUser.username)
      cy.get('#nickname').type(testUser.nickname)
      cy.get('#email').type(testUser.email)
      cy.get('#password1').type(testUser.password)
      cy.get('#password2').type(testUser.password)
      cy.get('#phone').type(testUser.phone)
      
      // 제출
      cy.get('button[type="submit"]').click()
      
      // 성공 메시지 확인
      cy.checkMessage('회원가입이 완료되었습니다')
      
      // 로그인 페이지로 리다이렉트 확인
      cy.url().should('include', '/user/login')
    })

    it('중복된 사용자명으로 회원가입 시 오류가 발생해야 한다', () => {
      cy.visit('/user/signup')
      
      // 이미 존재하는 사용자 정보로 회원가입 시도
      cy.get('#username').type('existinguser')
      cy.get('#nickname').type('기존유저')
      cy.get('#email').type('existing@example.com')
      cy.get('#password1').type('test123')
      cy.get('#password2').type('test123')
      cy.get('#phone').type('010-1234-5678')
      
      cy.get('button[type="submit"]').click()
      
      // 오류 메시지 확인
      cy.checkMessage('이미 등록된 사용자입니다')
    })

    it('비밀번호 불일치 시 오류가 발생해야 한다', () => {
      cy.visit('/user/signup')
      
      cy.get('#username').type('newuser')
      cy.get('#nickname').type('새유저')
      cy.get('#email').type('new@example.com')
      cy.get('#password1').type('password1')
      cy.get('#password2').type('password2') // 다른 비밀번호
      cy.get('#phone').type('010-1234-5678')
      
      cy.get('button[type="submit"]').click()
      
      // 오류 메시지 확인
      cy.checkMessage('2개의 패스워드가 일치하지 않습니다')
    })

    it('필수 필드 누락 시 유효성 검사 오류가 발생해야 한다', () => {
      cy.visit('/user/signup')
      
      // 빈 폼 제출
      cy.get('button[type="submit"]').click()
      
      // 유효성 검사 오류 메시지들이 표시되어야 함
      cy.get('.invalid-feedback, .error, [class*="error"]').should('exist')
    })
  })

  describe('로그인 기능', () => {
    it('올바른 사용자 정보로 로그인이 성공해야 한다', () => {
      cy.login(testUser.username, testUser.password)
      
      // 로그인 성공 후 메인 페이지로 이동
      cy.url().should('include', '/job/list')
      
      // 로그인 상태 확인 (네비게이션에 사용자 정보 표시)
      cy.get('nav, .navbar, [class*="nav"]').should('contain', testUser.nickname)
    })

    it('잘못된 사용자 정보로 로그인 시 오류가 발생해야 한다', () => {
      cy.visit('/user/login')
      
      cy.get('#username').type('wronguser')
      cy.get('#password').type('wrongpassword')
      cy.get('button[type="submit"]').click()
      
      // 로그인 실패 메시지 확인
      cy.checkMessage('아이디 또는 비밀번호가 올바르지 않습니다')
    })

    it('빈 필드로 로그인 시도 시 유효성 검사 오류가 발생해야 한다', () => {
      cy.visit('/user/login')
      
      // 빈 폼 제출
      cy.get('button[type="submit"]').click()
      
      // 유효성 검사 오류 메시지 확인
      cy.get('.invalid-feedback, .error, [class*="error"]').should('exist')
    })
  })

  describe('아이디 찾기 기능', () => {
    it('등록된 이메일로 아이디 찾기가 성공해야 한다', () => {
      cy.visit('/user/find-id')
      
      cy.get('#email').type(testUser.email)
      cy.get('button[type="submit"]').click()
      
      // 성공 메시지 확인
      cy.checkMessage('입력하신 이메일로 아이디를 발송했습니다')
      
      // 로그인 페이지로 리다이렉트 확인
      cy.url().should('include', '/user/login')
    })

    it('등록되지 않은 이메일로 아이디 찾기 시 오류가 발생해야 한다', () => {
      cy.visit('/user/find-id')
      
      cy.get('#email').type('nonexistent@example.com')
      cy.get('button[type="submit"]').click()
      
      // 오류 메시지 확인
      cy.checkMessage('해당 이메일로 등록된 사용자를 찾을 수 없습니다')
    })
  })

  describe('비밀번호 찾기 기능', () => {
    it('올바른 아이디와 이메일로 비밀번호 찾기가 성공해야 한다', () => {
      cy.visit('/user/find-password')
      
      cy.get('#username').type(testUser.username)
      cy.get('#email').type(testUser.email)
      cy.get('button[type="submit"]').click()
      
      // 성공 메시지 확인
      cy.checkMessage('입력하신 이메일로 비밀번호 재설정 링크를 발송했습니다')
      
      // 로그인 페이지로 리다이렉트 확인
      cy.url().should('include', '/user/login')
    })

    it('잘못된 아이디와 이메일 조합으로 비밀번호 찾기 시 오류가 발생해야 한다', () => {
      cy.visit('/user/find-password')
      
      cy.get('#username').type('wronguser')
      cy.get('#email').type('wrong@example.com')
      cy.get('button[type="submit"]').click()
      
      // 오류 메시지 확인
      cy.checkMessage('아이디와 이메일이 일치하는 사용자를 찾을 수 없습니다')
    })
  })

  describe('로그아웃 기능', () => {
    it('로그인 후 로그아웃이 성공해야 한다', () => {
      // 먼저 로그인
      cy.login(testUser.username, testUser.password)
      
      // 로그아웃
      cy.logout()
      
      // 로그아웃 후 로그인 페이지로 이동 확인
      cy.url().should('include', '/user/login')
      
      // 로그인 상태가 해제되었는지 확인
      cy.get('nav, .navbar, [class*="nav"]').should('not.contain', testUser.nickname)
    })
  })

  describe('마이페이지 기능', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('마이페이지에 접근할 수 있어야 한다', () => {
      cy.visit('/mypage')
      
      // 마이페이지 요소들이 표시되어야 함
      cy.get('h1, h2, h3').should('contain', '마이페이지')
      cy.get('a[href*="profile"], a[href*="password"], a[href*="jobings"], a[href*="workings"]').should('exist')
    })

    it('프로필 수정이 가능해야 한다', () => {
      cy.visit('/mypage/profile')
      
      // 프로필 수정 폼이 표시되어야 함
      cy.get('form').should('exist')
      cy.get('input[name="nickname"], input[name="email"], input[name="phone"]').should('exist')
    })

    it('비밀번호 변경이 가능해야 한다', () => {
      cy.visit('/mypage/password')
      
      // 비밀번호 변경 폼이 표시되어야 함
      cy.get('form').should('exist')
      cy.get('input[name="currentPassword"], input[name="newPassword"], input[name="confirmPassword"]').should('exist')
    })

    it('내 구인글 목록을 볼 수 있어야 한다', () => {
      cy.visit('/mypage/jobings')
      
      // 구인글 목록 페이지가 표시되어야 함
      cy.get('h1, h2, h3').should('contain', '내 구인글')
    })

    it('내 구직글 목록을 볼 수 있어야 한다', () => {
      cy.visit('/mypage/workings')
      
      // 구직글 목록 페이지가 표시되어야 함
      cy.get('h1, h2, h3').should('contain', '내 구직글')
    })
  })
}) 