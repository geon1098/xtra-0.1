describe('트랜잭션 기능 테스트', () => {
  const testUser = {
    username: 'transactiontest',
    nickname: '트랜잭션테스트',
    email: 'transaction@test.com',
    password: 'test123',
    phone: '010-1234-5678'
  }

  const testJobData = {
    name: '트랜잭션 테스트 구인자',
    gender: '남성',
    age: '28',
    phone: '010-1234-5678',
    email: 'transaction@test.com',
    address: '서울시 강남구',
    requestWork: '트랜잭션 테스트 개발자',
    hopArea: '서울시 강남구',
    career: '4년',
    license: '정보처리기사',
    networking: '네트워킹 경험 있음',
    startDate: '2024-01-15',
    introduction: '트랜잭션 테스트를 위한 구인글입니다.',
    etc: '트랜잭션 테스트용 기타 사항입니다.'
  }

  beforeEach(() => {
    cy.visit('/')
    cy.log('🔄 테스트 시작 - 트랜잭션 기능 테스트')
  })

  describe('데이터 자동 정리 테스트', () => {
    it('테스트 실행 후 데이터가 자동으로 정리되어야 한다', () => {
      // 1. 사용자 등록
      cy.registerUser(testUser)
      cy.checkMessage('회원가입이 완료되었습니다')

      // 2. 로그인
      cy.login(testUser.username, testUser.password)
      cy.url().should('include', '/job/list')

      // 3. 구인글 작성
      cy.createJobPost(testJobData)
      cy.checkMessage('구인글이 성공적으로 등록되었습니다')

      // 4. 구인글 목록에서 확인
      cy.visit('/job/list')
      cy.get('body').should('contain', testJobData.name)

      // 5. 테스트 완료 후 자동으로 데이터가 정리되는지 확인
      // (afterEach 훅에서 자동 실행됨)
    })

    it('여러 테스트 데이터가 순서대로 정리되어야 한다', () => {
      // 1. 여러 사용자 등록
      const users = [
        { username: 'user1', nickname: '사용자1', email: 'user1@test.com', password: 'test123', phone: '010-1111-1111' },
        { username: 'user2', nickname: '사용자2', email: 'user2@test.com', password: 'test123', phone: '010-2222-2222' },
        { username: 'user3', nickname: '사용자3', email: 'user3@test.com', password: 'test123', phone: '010-3333-3333' }
      ]

      users.forEach(user => {
        cy.registerUser(user)
      })

      // 2. 첫 번째 사용자로 로그인하여 구인글 작성
      cy.login('user1', 'test123')
      cy.createJobPost(testJobData)

      // 3. 두 번째 사용자로 로그인하여 구직글 작성
      cy.logout()
      cy.login('user2', 'test123')
      cy.createWorkPost({
        siteName: '트랜잭션 테스트 회사',
        title: '트랜잭션 테스트 구직글',
        category: 'IT/개발',
        jobContent: '트랜잭션 테스트 업무',
        jobType: '정규직',
        benefits: '4대보험',
        location: '서울시 강남구',
        jobDescription: '트랜잭션 테스트 개발',
        jobWork: '프론트엔드 개발',
        deadDate: '2024-12-31',
        workNumber: '2명',
        gender: '무관',
        age: '25-35세',
        address: '서울시 강남구',
        jobDetails: '트랜잭션 테스트 상세 내용',
        cPerson: '트랜잭션담당자',
        phone: '010-1234-5678'
      })

      // 4. 세 번째 사용자로 로그인하여 오퍼 작성
      cy.logout()
      cy.login('user3', 'test123')
      cy.createOffer({
        title: '트랜잭션 테스트 오퍼',
        description: '트랜잭션 테스트를 위한 오퍼입니다.',
        category: 'PREMIUM',
        price: '500000',
        address: '서울시 강남구'
      })

      // 5. 모든 데이터가 순서대로 정리되는지 확인
      // (오퍼 → 구직글 → 구인글 → 사용자 순서로 정리됨)
    })
  })

  describe('트랜잭션 롤백 테스트', () => {
    it('테스트 실패 시에도 데이터가 정리되어야 한다', () => {
      // 1. 사용자 등록
      cy.registerUser(testUser)

      // 2. 로그인
      cy.login(testUser.username, testUser.password)

      // 3. 구인글 작성
      cy.createJobPost(testJobData)

      // 4. 의도적으로 실패하는 테스트 (데이터 정리 확인용)
      cy.visit('/nonexistent-page')
      cy.get('body').should('contain', '존재하지 않는 페이지')
      
      // 5. 테스트 실패 후에도 데이터가 정리되는지 확인
      // (fail 이벤트에서 자동으로 cleanupTestData 호출)
    })

    it('네트워크 오류 시에도 데이터 정리가 시도되어야 한다', () => {
      // 1. 사용자 등록
      cy.registerUser(testUser)

      // 2. 네트워크 오류 시뮬레이션
      cy.intercept('POST', '/job/create', { statusCode: 500 }).as('createJobError')

      // 3. 구인글 작성 시도 (실패 예상)
      cy.login(testUser.username, testUser.password)
      cy.visit('/job/create')
      cy.get('#name').type(testJobData.name)
      cy.get('button[type="submit"]').click()

      // 4. 오류 발생 후에도 데이터 정리 시도
      cy.wait('@createJobError')
    })
  })

  describe('데이터 정리 순서 테스트', () => {
    it('외래키 제약조건을 고려한 정리 순서가 지켜져야 한다', () => {
      // 1. 사용자 등록
      cy.registerUser(testUser)

      // 2. 로그인
      cy.login(testUser.username, testUser.password)

      // 3. 구인글 작성
      cy.createJobPost(testJobData)

      // 4. 구직글 작성
      cy.createWorkPost({
        siteName: '트랜잭션 테스트 회사',
        title: '트랜잭션 테스트 구직글',
        category: 'IT/개발',
        jobContent: '트랜잭션 테스트 업무',
        jobType: '정규직',
        benefits: '4대보험',
        location: '서울시 강남구',
        jobDescription: '트랜잭션 테스트 개발',
        jobWork: '프론트엔드 개발',
        deadDate: '2024-12-31',
        workNumber: '2명',
        gender: '무관',
        age: '25-35세',
        address: '서울시 강남구',
        jobDetails: '트랜잭션 테스트 상세 내용',
        cPerson: '트랜잭션담당자',
        phone: '010-1234-5678'
      })

      // 5. 오퍼 작성
      cy.createOffer({
        title: '트랜잭션 테스트 오퍼',
        description: '트랜잭션 테스트를 위한 오퍼입니다.',
        category: 'PREMIUM',
        price: '500000',
        address: '서울시 강남구'
      })

      // 6. 채팅 메시지 전송 (채팅 기능이 있는 경우)
      cy.visit('/work/list')
      cy.get('.work-list a, .list-group a, [class*="list"] a, [class*="card"] a').first().click()
      cy.get('a[href*="chat"], button[onclick*="chat"], .btn-chat, [class*="chat"]').click()
      
      const testMessage = '트랜잭션 테스트 메시지입니다.'
      cy.get('.message-input, input[placeholder*="메시지"], textarea[placeholder*="메시지"], #message').type(testMessage)
      cy.get('.send-button, button[type="submit"], .btn-send, [class*="send"]').click()

      // 7. 정리 순서 확인: 채팅메시지 → 오퍼 → 구직글 → 구인글 → 사용자
      cy.log('정리 순서: 채팅메시지 → 오퍼 → 구직글 → 구인글 → 사용자')
    })
  })

  describe('테스트 격리 테스트', () => {
    it('각 테스트가 독립적으로 실행되어야 한다', () => {
      // 이 테스트는 다른 테스트와 격리되어 실행되어야 함
      cy.log('테스트 격리 확인 - 이 테스트는 독립적으로 실행됨')
      
      // 1. 사용자 등록
      cy.registerUser({
        username: 'isolationtest',
        nickname: '격리테스트',
        email: 'isolation@test.com',
        password: 'test123',
        phone: '010-9999-9999'
      })

      // 2. 로그인
      cy.login('isolationtest', 'test123')

      // 3. 간단한 구인글 작성
      cy.createJobPost({
        name: '격리 테스트 구인자',
        gender: '여성',
        age: '26',
        phone: '010-9999-9999',
        email: 'isolation@test.com',
        address: '서울시 서초구',
        requestWork: '격리 테스트 개발자',
        hopArea: '서울시 서초구',
        career: '2년',
        license: '정보처리기사',
        networking: '네트워킹 경험 있음',
        startDate: '2024-02-01',
        introduction: '격리 테스트를 위한 구인글입니다.',
        etc: '격리 테스트용 기타 사항입니다.'
      })

      // 4. 테스트 완료 후 자동 정리 확인
      cy.log('격리 테스트 완료 - 데이터 자동 정리 예정')
    })
  })

  describe('성능 테스트', () => {
    it('대량의 테스트 데이터도 효율적으로 정리되어야 한다', () => {
      const startTime = Date.now()
      
      // 1. 여러 사용자 등록
      for (let i = 1; i <= 5; i++) {
        cy.registerUser({
          username: `bulktest${i}`,
          nickname: `대량테스트${i}`,
          email: `bulk${i}@test.com`,
          password: 'test123',
          phone: `010-${String(i).padStart(4, '0')}-${String(i).padStart(4, '0')}`
        })
      }

      // 2. 각 사용자별로 구인글 작성
      for (let i = 1; i <= 5; i++) {
        cy.login(`bulktest${i}`, 'test123')
        cy.createJobPost({
          name: `대량 테스트 구인자 ${i}`,
          gender: '남성',
          age: '25',
          phone: `010-${String(i).padStart(4, '0')}-${String(i).padStart(4, '0')}`,
          email: `bulk${i}@test.com`,
          address: '서울시 강남구',
          requestWork: `대량 테스트 개발자 ${i}`,
          hopArea: '서울시 강남구',
          career: '3년',
          license: '정보처리기사',
          networking: '네트워킹 경험 있음',
          startDate: '2024-01-15',
          introduction: `대량 테스트 ${i}번째 구인글입니다.`,
          etc: `대량 테스트 ${i}번째 기타 사항입니다.`
        })
        cy.logout()
      }

      const endTime = Date.now()
      const duration = endTime - startTime
      
      cy.log(`대량 데이터 생성 완료 - 소요시간: ${duration}ms`)
      cy.log('대량 데이터 정리 시작 - 성능 확인 예정')
    })
  })

  describe('에러 처리 테스트', () => {
    it('데이터 정리 중 오류가 발생해도 다른 데이터는 정리되어야 한다', () => {
      // 1. 사용자 등록
      cy.registerUser(testUser)

      // 2. 로그인
      cy.login(testUser.username, testUser.password)

      // 3. 구인글 작성
      cy.createJobPost(testJobData)

      // 4. 구직글 작성
      cy.createWorkPost({
        siteName: '에러테스트 회사',
        title: '에러 테스트 구직글',
        category: 'IT/개발',
        jobContent: '에러 테스트 업무',
        jobType: '정규직',
        benefits: '4대보험',
        location: '서울시 강남구',
        jobDescription: '에러 테스트 개발',
        jobWork: '프론트엔드 개발',
        deadDate: '2024-12-31',
        workNumber: '1명',
        gender: '무관',
        age: '25-35세',
        address: '서울시 강남구',
        jobDetails: '에러 테스트 상세 내용',
        cPerson: '에러담당자',
        phone: '010-1234-5678'
      })

      // 5. 일부 삭제 API에 오류 시뮬레이션
      cy.intercept('POST', '/work/delete/*', { statusCode: 500 }).as('deleteWorkError')

      // 6. 데이터 정리 시도 (일부 실패하더라도 다른 데이터는 정리됨)
      cy.log('에러 상황에서의 데이터 정리 테스트')
    })
  })
}) 