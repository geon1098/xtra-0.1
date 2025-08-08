// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

// 테스트 데이터 정리를 위한 전역 변수
let testData = {
  users: [],
  jobPosts: [],
  workPosts: [],
  offers: [],
  chatMessages: []
}

// 테스트 데이터 추적 커맨드
Cypress.Commands.add('trackTestData', (type, data) => {
  if (!testData[type]) {
    testData[type] = []
  }
  testData[type].push(data)
})

// 테스트 데이터 정리 커맨드
Cypress.Commands.add('cleanupTestData', () => {
  cy.log('🧹 테스트 데이터 정리 시작...')
  
  // 로그인 상태 확인
  cy.getCookie('JSESSIONID').then((cookie) => {
    if (!cookie) {
      cy.log('로그인되지 않은 상태 - 데이터 정리 건너뜀')
      return
    }
    
    // 오퍼 데이터 정리
    if (testData.offers.length > 0) {
      cy.log(`오퍼 데이터 ${testData.offers.length}개 정리 중...`)
      testData.offers.forEach((offerId) => {
        cy.request({
          method: 'POST',
          url: `/offer/delete/${offerId}`,
          failOnStatusCode: false
        }).then((response) => {
          if (response.status === 200) {
            cy.log(`오퍼 ${offerId} 삭제 완료`)
          }
        })
      })
      testData.offers = []
    }
    
    // 구직글 데이터 정리
    if (testData.workPosts.length > 0) {
      cy.log(`구직글 데이터 ${testData.workPosts.length}개 정리 중...`)
      testData.workPosts.forEach((workId) => {
        cy.request({
          method: 'POST',
          url: `/work/delete/${workId}`,
          failOnStatusCode: false
        }).then((response) => {
          if (response.status === 200) {
            cy.log(`구직글 ${workId} 삭제 완료`)
          }
        })
      })
      testData.workPosts = []
    }
    
    // 구인글 데이터 정리
    if (testData.jobPosts.length > 0) {
      cy.log(`구인글 데이터 ${testData.jobPosts.length}개 정리 중...`)
      testData.jobPosts.forEach((jobId) => {
        cy.request({
          method: 'POST',
          url: `/job/delete/${jobId}`,
          failOnStatusCode: false
        }).then((response) => {
          if (response.status === 200) {
            cy.log(`구인글 ${jobId} 삭제 완료`)
          }
        })
      })
      testData.jobPosts = []
    }
    
    // 채팅 메시지 데이터 정리
    if (testData.chatMessages.length > 0) {
      cy.log(`채팅 메시지 데이터 ${testData.chatMessages.length}개 정리 중...`)
      testData.chatMessages.forEach((messageId) => {
        cy.request({
          method: 'DELETE',
          url: `/chat/message/${messageId}`,
          failOnStatusCode: false
        }).then((response) => {
          if (response.status === 200) {
            cy.log(`채팅 메시지 ${messageId} 삭제 완료`)
          }
        })
      })
      testData.chatMessages = []
    }
  })
  
  // 사용자 데이터 정리 (마지막에 실행)
  if (testData.users.length > 0) {
    cy.log(`사용자 데이터 ${testData.users.length}개 정리 중...`)
    testData.users.forEach((username) => {
      cy.request({
        method: 'POST',
        url: `/user/delete/${username}`,
        failOnStatusCode: false
      }).then((response) => {
        if (response.status === 200) {
          cy.log(`사용자 ${username} 삭제 완료`)
        }
      })
    })
    testData.users = []
  }
  
  cy.log('🧹 테스트 데이터 정리 완료')
})

// 테스트 데이터 초기화 커맨드
Cypress.Commands.add('resetTestData', () => {
  testData = {
    users: [],
    jobPosts: [],
    workPosts: [],
    offers: [],
    chatMessages: []
  }
  cy.log('🔄 테스트 데이터 초기화 완료')
})

// 사용자 등록 커맨드 (데이터 추적 포함)
Cypress.Commands.add('registerUser', (userData) => {
  cy.visit('/user/signup')
  cy.get('#username').type(userData.username)
  cy.get('#nickname').type(userData.nickname)
  cy.get('#email').type(userData.email)
  cy.get('#password1').type(userData.password)
  cy.get('#password2').type(userData.password)
  cy.get('#phone').type(userData.phone)
  cy.get('button[type="submit"]').click()
  
  // 테스트 데이터 추적
  cy.trackTestData('users', userData.username)
})

// 로그인 커맨드
Cypress.Commands.add('login', (username, password) => {
  cy.visit('/user/login')
  cy.get('#username').type(username)
  cy.get('#password').type(password)
  cy.get('button[type="submit"]').click()
})

// 관리자 로그인 커맨드
Cypress.Commands.add('adminLogin', () => {
  cy.login('admin', 'admin123')
})

// 일반 사용자 로그인 커맨드
Cypress.Commands.add('userLogin', () => {
  cy.login('testuser', 'test123')
})

// 구인글 작성 커맨드 (데이터 추적 포함)
Cypress.Commands.add('createJobPost', (jobData) => {
  cy.visit('/job/create')
  cy.get('#name').type(jobData.name)
  cy.get('#gender').select(jobData.gender)
  cy.get('#age').type(jobData.age)
  cy.get('#phone').type(jobData.phone)
  cy.get('#email').type(jobData.email)
  cy.get('#address').type(jobData.address)
  cy.get('#requestWork').type(jobData.requestWork)
  cy.get('#hopArea').type(jobData.hopArea)
  cy.get('#career').type(jobData.career)
  cy.get('#license').type(jobData.license)
  cy.get('#networking').type(jobData.networking)
  cy.get('#startDate').type(jobData.startDate)
  cy.get('#introduction').type(jobData.introduction)
  cy.get('#etc').type(jobData.etc)
  cy.get('button[type="submit"]').click()
  
  // 생성된 구인글 ID 추적
  cy.url().then((url) => {
    const match = url.match(/\/job\/detail\/(\d+)/)
    if (match) {
      cy.trackTestData('jobPosts', match[1])
    }
  })
})

// 구직글 작성 커맨드 (데이터 추적 포함)
Cypress.Commands.add('createWorkPost', (workData) => {
  cy.visit('/work/create')
  cy.get('#siteName').type(workData.siteName)
  cy.get('#title').type(workData.title)
  cy.get('#category').select(workData.category)
  cy.get('#jobContent').type(workData.jobContent)
  cy.get('#jobType').select(workData.jobType)
  cy.get('#benefits').type(workData.benefits)
  cy.get('#location').type(workData.location)
  cy.get('#jobDescription').type(workData.jobDescription)
  cy.get('#jobWork').type(workData.jobWork)
  cy.get('#deadDate').type(workData.deadDate)
  cy.get('#workNumber').type(workData.workNumber)
  cy.get('#gender').select(workData.gender)
  cy.get('#age').type(workData.age)
  cy.get('#address').type(workData.address)
  cy.get('#jobDetails').type(workData.jobDetails)
  cy.get('#cPerson').type(workData.cPerson)
  cy.get('#phone').type(workData.phone)
  cy.get('button[type="submit"]').click()
  
  // 생성된 구직글 ID 추적
  cy.url().then((url) => {
    const match = url.match(/\/work\/detail\/(\d+)/)
    if (match) {
      cy.trackTestData('workPosts', match[1])
    }
  })
})

// 오퍼 작성 커맨드 (데이터 추적 포함)
Cypress.Commands.add('createOffer', (offerData) => {
  cy.visit('/offer/create')
  cy.get('#title').type(offerData.title)
  cy.get('#description').type(offerData.description)
  cy.get('#category').select(offerData.category)
  cy.get('#price').type(offerData.price)
  cy.get('#address').type(offerData.address)
  cy.get('input[type="file"]').attachFile(offerData.imageFile || 'test-image.jpg')
  cy.get('button[type="submit"]').click()
  
  // 생성된 오퍼 ID 추적
  cy.url().then((url) => {
    const match = url.match(/\/offer\/detail\/(\d+)/)
    if (match) {
      cy.trackTestData('offers', match[1])
    }
  })
})

// 로그아웃 커맨드
Cypress.Commands.add('logout', () => {
  cy.get('a[href*="logout"]').click()
})

// 페이지 로딩 대기 커맨드
Cypress.Commands.add('waitForPageLoad', () => {
  cy.get('body').should('be.visible')
  cy.wait(1000) // 추가 대기 시간
})

// 메시지 확인 커맨드
Cypress.Commands.add('checkMessage', (expectedMessage) => {
  cy.get('.alert, .message, [class*="message"], [class*="alert"]')
    .should('contain', expectedMessage)
})

// 파일 업로드 커맨드
Cypress.Commands.add('uploadFile', (selector, fileName) => {
  cy.get(selector).attachFile(fileName)
})

// 테스트 데이터베이스 초기화 커맨드 (관리자용)
Cypress.Commands.add('resetDatabase', () => {
  cy.adminLogin()
  cy.visit('/admin/reset-database')
  cy.get('button[type="submit"]').click()
  cy.checkMessage('데이터베이스가 초기화되었습니다')
})

// 테스트 환경 설정 커맨드
Cypress.Commands.add('setupTestEnvironment', () => {
  cy.log('🔧 테스트 환경 설정 시작...')
  
  // 테스트 데이터 초기화
  cy.resetTestData()
  
  // 데이터베이스 초기화 (필요한 경우)
  cy.request({
    method: 'POST',
    url: '/api/test/reset',
    failOnStatusCode: false
  }).then((response) => {
    if (response.status === 200) {
      cy.log('데이터베이스 초기화 완료')
    }
  })
  
  cy.log('🔧 테스트 환경 설정 완료')
}) 