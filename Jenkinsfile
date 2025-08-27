pipeline {
  agent any

  // No specific environment variables needed

  options {
    timestamps()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Install Node DevDeps') {
      steps {
        sh 'node -v && npm -v || true'
        sh 'npm ci'
        sh 'npx playwright install --with-deps'
      }
    }

    stage('Backend Build') {
      steps {
        sh 'chmod +x ./gradlew'
        sh './gradlew clean build -x test'
      }
    }

    stage('E2E (Playwright + CI Profile)') {
      environment {
        SPRING_PROFILES_ACTIVE = 'ci'
      }
      steps {
        sh '''
          # 백엔드 웹서버를 백그라운드에서 시작
          nohup ./gradlew bootRun --args="--spring.profiles.active=ci" > server.log 2>&1 &
          echo "Starting backend server..."
          
          # 웹서버가 시작될 때까지 대기
          sleep 30
          # Playwright 테스트 실행 (headless 모드 - Jenkins 환경에 적합)
          npx playwright test tests/e2e-job-flow.spec.ts
        '''
        archiveArtifacts artifacts: 'playwright-report/**', fingerprint: true, onlyIfSuccessful: false
        junit allowEmptyResults: true, testResults: 'playwright-results.xml'
        archiveArtifacts artifacts: 'server.log', fingerprint: true, onlyIfSuccessful: false
      }
    }

    stage('Docker Build (Optional)') {
      when { expression { return fileExists('Dockerfile') } }
      steps {
        sh 'docker build -t xtra:latest .'
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true, onlyIfSuccessful: false
    }
  }
}


