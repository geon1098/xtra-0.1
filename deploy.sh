#!/bin/bash

# 배포 스크립트
echo "🚀 Xtra 애플리케이션 배포를 시작합니다..."

# 환경 변수 파일 확인
if [ ! -f .env ]; then
    echo "❌ .env 파일이 없습니다. env.example을 복사하여 .env 파일을 생성하고 설정을 완료해주세요."
    exit 1
fi

# Docker Compose로 서비스 시작
echo "📦 Docker 컨테이너를 빌드하고 시작합니다..."
docker-compose up -d --build

# 서비스 상태 확인
echo "⏳ 서비스가 시작될 때까지 잠시 기다립니다..."
sleep 30

# 컨테이너 상태 확인
echo "🔍 컨테이너 상태를 확인합니다..."
docker-compose ps

# 로그 확인
echo "📋 최근 로그를 확인합니다..."
docker-compose logs --tail=20

echo "✅ 배포가 완료되었습니다!"
echo "🌐 웹사이트: http://localhost"
echo "📊 컨테이너 상태 확인: docker-compose ps"
echo "📋 로그 확인: docker-compose logs -f" 