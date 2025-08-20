pipeline {
  agent any

  environment {
    NODEJS_HOME = tool name: 'node18', type: 'nodejs'
    PATH = "${env.NODEJS_HOME}/bin:${env.PATH}"
    JAVA_HOME = tool name: 'jdk17', type: 'hudson.model.JDK'
  }

  options {
    timestamps()
    ansiColor('xterm')
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
        sh './gradlew clean build -x test'
      }
    }

    stage('E2E (Playwright + CI Profile)') {
      environment {
        SPRING_PROFILES_ACTIVE = 'ci'
      }
      steps {
        sh 'npx playwright test'
        archiveArtifacts artifacts: 'playwright-report/**', fingerprint: true, onlyIfSuccessful: false
        junit allowEmptyResults: true, testResults: 'playwright-results.xml'
      }
      post {
        always {
          publishHTML target: [
            allowMissing: true,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'playwright-report',
            reportFiles: 'index.html',
            reportName: 'Playwright Report'
          ]
        }
      }
    }

    stage('E2E Job Flow (등록→메인→상세)') {
      environment {
        SPRING_PROFILES_ACTIVE = 'ci'
      }
      steps {
        sh 'npx playwright test tests/e2e-job-flow.spec.ts'
        archiveArtifacts artifacts: 'playwright-report/**', fingerprint: true, onlyIfSuccessful: false
        junit allowEmptyResults: true, testResults: 'playwright-results.xml'
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


