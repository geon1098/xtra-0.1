describe('오퍼 기능 테스트', () => {
  const testUser = {
    username: 'testuser',
    password: 'test123'
  }

  const testOfferData = {
    title: '프리미엄 웹 개발 서비스',
    description: '고품질 웹 개발 서비스를 제공합니다.',
    category: 'PREMIUM',
    price: '500000',
    address: '서울시 강남구 테헤란로'
  }

  beforeEach(() => {
    cy.visit('/')
  })

  describe('오퍼 목록 조회', () => {
    it('프리미엄 오퍼 목록에 접근할 수 있어야 한다', () => {
      cy.visit('/offer/list/PREMIUM')
      cy.get('h1, h2, h3').should('contain', '프리미엄')
      cy.get('.offer-list, .list-group, [class*="list"], [class*="card"]').should('exist')
    })

    it('익스퍼트 오퍼 목록에 접근할 수 있어야 한다', () => {
      cy.visit('/offer/list/EXPERT')
      cy.get('h1, h2, h3').should('contain', '익스퍼트')
      cy.get('.offer-list, .list-group, [class*="list"], [class*="card"]').should('exist')
    })

    it('VIP 오퍼 목록에 접근할 수 있어야 한다', () => {
      cy.visit('/offer/list/VIP')
      cy.get('h1, h2, h3').should('contain', 'VIP')
      cy.get('.offer-list, .list-group, [class*="list"], [class*="card"]').should('exist')
    })
  })

  describe('오퍼 상세 조회', () => {
    it('오퍼 상세 페이지에 접근할 수 있어야 한다', () => {
      cy.visit('/offer/list/PREMIUM')
      cy.get('.offer-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('h1, h2, h3').should('exist')
      cy.get('.offer-detail, .detail-content, [class*="detail"]').should('exist')
    })

    it('오퍼 상세 정보가 올바르게 표시되어야 한다', () => {
      cy.visit('/offer/list/PREMIUM')
      cy.get('.offer-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('body').should('contain', '제목')
      cy.get('body').should('contain', '설명')
      cy.get('body').should('contain', '가격')
    })

    it('카카오 지도가 표시되어야 한다', () => {
      cy.visit('/offer/list/PREMIUM')
      cy.get('.offer-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('#map, .map, [id*="map"], [class*="map"]').should('exist')
    })
  })

  describe('오퍼 작성', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('로그인한 사용자가 오퍼를 작성할 수 있어야 한다', () => {
      cy.visit('/offer/create')
      cy.get('h1, h2, h3').should('contain', '오퍼')
      cy.get('form').should('exist')
      cy.get('#title, input[name="title"]').should('exist')
      cy.get('#description, textarea[name="description"]').should('exist')
      cy.get('#category, select[name="category"]').should('exist')
    })

    it('오퍼 작성이 성공적으로 완료되어야 한다', () => {
      cy.visit('/offer/create')
      cy.get('#title, input[name="title"]').type(testOfferData.title)
      cy.get('#description, textarea[name="description"]').type(testOfferData.description)
      cy.get('#category, select[name="category"]').select(testOfferData.category)
      cy.get('#price, input[name="price"]').type(testOfferData.price)
      cy.get('#address, input[name="address"]').type(testOfferData.address)
      cy.get('input[type="file"]').attachFile('test-image.jpg')
      cy.get('button[type="submit"]').click()
      cy.url().should('include', '/work/list')
    })

    it('이미지 파일 업로드가 작동해야 한다', () => {
      cy.visit('/offer/create')
      cy.get('input[type="file"]').should('exist')
      cy.get('input[type="file"]').attachFile('test-image.jpg')
      cy.get('input[type="file"]').should('have.value')
    })
  })

  describe('오퍼 수정', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('본인이 작성한 오퍼를 수정할 수 있어야 한다', () => {
      cy.createOffer(testOfferData)
      cy.visit('/work/list')
      cy.get('a[href*="offer/edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').first().click()
      cy.get('form').should('exist')
      cy.get('h1, h2, h3').should('contain', '수정')
    })

    it('오퍼 수정이 성공적으로 완료되어야 한다', () => {
      cy.createOffer(testOfferData)
      cy.visit('/work/list')
      cy.get('a[href*="offer/edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').first().click()
      cy.get('#description, textarea[name="description"]').clear().type('수정된 설명입니다.')
      cy.get('button[type="submit"]').click()
      cy.url().should('include', '/work/list')
    })
  })

  describe('오퍼 삭제', () => {
    beforeEach(() => {
      cy.login(testUser.username, testUser.password)
    })

    it('본인이 작성한 오퍼를 삭제할 수 있어야 한다', () => {
      cy.createOffer(testOfferData)
      cy.visit('/work/list')
      cy.get('button[onclick*="delete"], .btn-delete, [class*="delete"], form[action*="delete"] button').first().click()
      cy.on('window:confirm', () => true)
      cy.url().should('include', '/work/list')
    })
  })

  describe('권한 관리', () => {
    it('로그인하지 않은 사용자는 오퍼 작성 페이지에 접근할 수 없어야 한다', () => {
      cy.visit('/offer/create')
      cy.url().should('include', '/user/login')
    })

    it('다른 사용자가 작성한 오퍼를 수정할 수 없어야 한다', () => {
      cy.login('otheruser', 'other123')
      cy.visit('/offer/list/PREMIUM')
      cy.get('.offer-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="edit"], button[onclick*="edit"], .btn-edit, [class*="edit"]').should('not.exist')
    })
  })
}) 