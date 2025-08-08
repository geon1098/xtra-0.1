describe('관리자 기능 테스트', () => {
  const adminUser = {
    username: 'admin',
    password: 'admin123'
  }

  const testUser = {
    username: 'testuser',
    password: 'test123'
  }

  beforeEach(() => {
    cy.visit('/')
  })

  describe('관리자 대시보드', () => {
    beforeEach(() => {
      cy.adminLogin()
    })

    it('관리자 대시보드에 접근할 수 있어야 한다', () => {
      cy.visit('/admin/dashboard')
      cy.get('h1, h2, h3').should('contain', '관리자')
      cy.get('body').should('contain', '대시보드')
    })

    it('대시보드에 통계 정보가 표시되어야 한다', () => {
      cy.visit('/admin/dashboard')
      cy.get('body').should('contain', '승인 대기')
      cy.get('body').should('contain', '승인됨')
      cy.get('body').should('contain', '거절됨')
    })

    it('대시보드에서 각 섹션으로 이동할 수 있어야 한다', () => {
      cy.visit('/admin/dashboard')
      cy.get('a[href*="pending"], a[href*="approved"], a[href*="rejected"], a[href*="all-offers"]').should('exist')
    })
  })

  describe('승인 대기 오퍼 관리', () => {
    beforeEach(() => {
      cy.adminLogin()
    })

    it('승인 대기 오퍼 목록에 접근할 수 있어야 한다', () => {
      cy.visit('/admin/pending')
      cy.get('h1, h2, h3').should('contain', '승인 대기')
      cy.get('.offer-list, .list-group, [class*="list"], [class*="card"]').should('exist')
    })

    it('오퍼를 승인할 수 있어야 한다', () => {
      cy.visit('/admin/pending')
      cy.get('.btn-approve, button[onclick*="approve"], [class*="approve"]').first().click()
      cy.get('body').should('contain', '승인')
    })

    it('오퍼를 거절할 수 있어야 한다', () => {
      cy.visit('/admin/pending')
      cy.get('.btn-reject, button[onclick*="reject"], [class*="reject"]').first().click()
      cy.get('body').should('contain', '거절')
    })
  })

  describe('승인된 오퍼 관리', () => {
    beforeEach(() => {
      cy.adminLogin()
    })

    it('승인된 오퍼 목록에 접근할 수 있어야 한다', () => {
      cy.visit('/admin/approved')
      cy.get('h1, h2, h3').should('contain', '승인됨')
      cy.get('.offer-list, .list-group, [class*="list"], [class*="card"]').should('exist')
    })

    it('승인된 오퍼를 수정할 수 있어야 한다', () => {
      cy.visit('/admin/approved')
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').first().click()
      cy.get('form').should('exist')
      cy.get('h1, h2, h3').should('contain', '수정')
    })

    it('승인된 오퍼를 삭제할 수 있어야 한다', () => {
      cy.visit('/admin/approved')
      cy.get('button[onclick*="delete"], .btn-delete, [class*="delete"], form[action*="delete"] button').first().click()
      cy.on('window:confirm', () => true)
      cy.url().should('include', '/admin/all-offers')
    })
  })

  describe('거절된 오퍼 관리', () => {
    beforeEach(() => {
      cy.adminLogin()
    })

    it('거절된 오퍼 목록에 접근할 수 있어야 한다', () => {
      cy.visit('/admin/rejected')
      cy.get('h1, h2, h3').should('contain', '거절됨')
      cy.get('.offer-list, .list-group, [class*="list"], [class*="card"]').should('exist')
    })
  })

  describe('전체 오퍼 관리', () => {
    beforeEach(() => {
      cy.adminLogin()
    })

    it('전체 오퍼 목록에 접근할 수 있어야 한다', () => {
      cy.visit('/admin/all-offers')
      cy.get('h1, h2, h3').should('contain', '전체')
      cy.get('.offer-list, .list-group, [class*="list"], [class*="card"]').should('exist')
    })

    it('전체 오퍼를 수정할 수 있어야 한다', () => {
      cy.visit('/admin/all-offers')
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').first().click()
      cy.get('form').should('exist')
      cy.get('h1, h2, h3').should('contain', '수정')
    })

    it('전체 오퍼를 삭제할 수 있어야 한다', () => {
      cy.visit('/admin/all-offers')
      cy.get('button[onclick*="delete"], .btn-delete, [class*="delete"], form[action*="delete"] button').first().click()
      cy.on('window:confirm', () => true)
      cy.url().should('include', '/admin/all-offers')
    })
  })

  describe('권한 관리', () => {
    it('일반 사용자는 관리자 페이지에 접근할 수 없어야 한다', () => {
      cy.login(testUser.username, testUser.password)
      cy.visit('/admin/dashboard')
      cy.url().should('not.include', '/admin/dashboard')
    })

    it('로그인하지 않은 사용자는 관리자 페이지에 접근할 수 없어야 한다', () => {
      cy.visit('/admin/dashboard')
      cy.url().should('include', '/user/login')
    })

    it('관리자가 아닌 사용자는 오퍼 승인/거절을 할 수 없어야 한다', () => {
      cy.login(testUser.username, testUser.password)
      cy.visit('/admin/pending')
      cy.url().should('not.include', '/admin/pending')
    })
  })

  describe('네비게이션', () => {
    beforeEach(() => {
      cy.adminLogin()
    })

    it('관리자 대시보드에서 다른 관리자 페이지로 이동할 수 있어야 한다', () => {
      cy.visit('/admin/dashboard')
      cy.get('a[href*="pending"]').click()
      cy.url().should('include', '/admin/pending')
      
      cy.visit('/admin/dashboard')
      cy.get('a[href*="approved"]').click()
      cy.url().should('include', '/admin/approved')
      
      cy.visit('/admin/dashboard')
      cy.get('a[href*="rejected"]').click()
      cy.url().should('include', '/admin/rejected')
      
      cy.visit('/admin/dashboard')
      cy.get('a[href*="all-offers"]').click()
      cy.url().should('include', '/admin/all-offers')
    })

    it('관리자 페이지에서 홈으로 돌아갈 수 있어야 한다', () => {
      cy.visit('/admin/dashboard')
      cy.get('a[href="/"], a[href*="home"], .navbar-brand, [class*="home"]').click()
      cy.url().should('eq', Cypress.config().baseUrl + '/')
    })
  })

  describe('CSRF 토큰', () => {
    beforeEach(() => {
      cy.adminLogin()
    })

    it('관리자 페이지에서 CSRF 토큰이 포함되어야 한다', () => {
      cy.visit('/admin/dashboard')
      cy.get('input[name="_csrf"], meta[name="_csrf"]').should('exist')
    })
  })
}) 