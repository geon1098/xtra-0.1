// ***********************************************************
// This example support/e2e.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

// Alternatively you can use CommonJS syntax:
// require('./commands')

// 전역 설정
beforeEach(() => {
  // 각 테스트 전에 로컬 스토리지와 세션 스토리지 초기화
  cy.clearLocalStorage()
  cy.clearCookies()
  
  // 테스트 환경 설정
  cy.setupTestEnvironment()
})

// 각 테스트 후 데이터 정리
afterEach(() => {
  // 테스트 데이터 정리
  cy.cleanupTestData()
})

// 모든 테스트 완료 후 최종 정리
after(() => {
  cy.log('🏁 모든 테스트 완료 - 최종 데이터 정리')
  cy.cleanupTestData()
})

// 테스트 실패 시 스크린샷 저장
Cypress.on('test:after:run', (attributes) => {
  if (attributes.state === 'failed') {
    cy.screenshot(`${attributes.title} - failed`)
  }
})

// 네트워크 요청 로깅
Cypress.on('window:before:load', (win) => {
  cy.spy(win.console, 'log').as('consoleLog')
})

// 테스트 데이터 정리 실패 시 로깅
Cypress.on('fail', (error) => {
  cy.log('❌ 테스트 실패 - 데이터 정리 시도')
  cy.cleanupTestData()
  throw error
})

// 테스트 타임아웃 설정
Cypress.on('test:before:run', () => {
  // 테스트별 타임아웃 설정
  Cypress.config('defaultCommandTimeout', 10000)
  Cypress.config('requestTimeout', 10000)
  Cypress.config('responseTimeout', 10000)
}) 