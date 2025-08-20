# Xtra 애플리케이션 배포 가이드

## 🐳 Docker + Nginx 배포

이 가이드는 Xtra 애플리케이션을 Docker와 Nginx를 사용하여 배포하는 방법을 설명합니다.

## 📋 사전 요구사항

- Docker
- Docker Compose
- 도메인 이름 (AWS Lightsail 배포 시)

## 🚀 로컬 배포

### 1. 환경 변수 설정

```bash
# env.example을 복사하여 .env 파일 생성
cp env.example .env

# .env 파일을 편집하여 실제 값으로 설정
nano .env
```

### 2. 애플리케이션 실행

```bash
# 배포 스크립트 실행
chmod +x deploy.sh
./deploy.sh

# 또는 직접 Docker Compose 실행
docker-compose up -d --build
```

### 3. 접속 확인

- 웹사이트: http://localhost
- 데이터베이스: localhost:5432

## ☁️ AWS Lightsail 배포

### 1. Lightsail 인스턴스 생성

1. AWS 콘솔에서 Lightsail로 이동
2. "Create instance" 클릭
3. 플랫폼: Linux/Unix
4. 블루프린트: Docker
5. 인스턴스 플랜 선택 (최소 1GB RAM 권장)
6. 인스턴스 이름 설정 (예: xtra-app)

### 2. 도메인 연결

1. Lightsail 콘솔에서 "Networking" 탭으로 이동
2. "Create DNS zone" 클릭
3. 도메인 이름 입력 (예: yourdomain.com)
4. 네임서버 정보를 도메인 등록업체에 설정

### 3. 프로젝트 업로드

```bash
# SSH로 인스턴스에 연결
ssh -i your-key.pem ubuntu@your-instance-ip

# 프로젝트 파일 업로드 (로컬에서)
scp -i your-key.pem -r ./xtra ubuntu@your-instance-ip:~/

# 또는 Git에서 클론
git clone https://github.com/your-username/xtra.git
cd xtra
```

### 4. 환경 변수 설정

```bash
# .env 파일 생성 및 설정
cp env.example .env
nano .env

# 실제 값으로 설정:
# - DB_URL: RDS 또는 외부 데이터베이스 URL
# - MAIL_USERNAME: 실제 이메일
# - MAIL_PASSWORD: 실제 이메일 비밀번호
# - KAKAO_JS_KEY: 실제 카카오 API 키
```

### 5. 애플리케이션 배포

```bash
# Docker 설치 확인
docker --version
docker-compose --version

# 애플리케이션 실행
docker-compose up -d --build

# 상태 확인
docker-compose ps
```

### 6. HTTPS 설정 (권장)

```bash
# Certbot 설치
sudo apt update
sudo apt install certbot python3-certbot-nginx

# SSL 인증서 발급
sudo certbot --nginx -d yourdomain.com

# 자동 갱신 설정
sudo crontab -e
# 다음 줄 추가: 0 12 * * * /usr/bin/certbot renew --quiet
```

## 🔧 설정 파일 수정

### Nginx 설정 (nginx.conf)

도메인 이름을 변경하려면:

```nginx
server {
    listen 80;
    server_name yourdomain.com;  # 여기를 변경
    
    # ... 나머지 설정
}
```

### Docker Compose 설정 (docker-compose.yml)

포트 변경이 필요한 경우:

```yaml
nginx:
  ports:
    - "80:80"      # 외부:내부
    - "443:443"    # HTTPS
```

## 🧰 Jenkins (Docker) 설정

### 1) 컨테이너 기동
```bash
docker compose up -d jenkins
```

초기 어드민 패스워드 확인:
```bash
docker exec -it xtra-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### 2) Jenkins 플러그인/툴
- NodeJS 플러그인 설치 후 Global Tool에 Node 18 등록 (이름: node18)
- JDK 17 등록 (이름: jdk17)

### 3) 파이프라인 잡 생성
- SCM에 현재 리포를 연결하여 루트 `Jenkinsfile`로 파이프라인 실행

### 4) Playwright 브라우저 설치
- 첫 실행 시 스테이지에서 `npx playwright install --with-deps`가 수행되도록 되어 있습니다. 필요 시 수동 실행 가능


## 📊 모니터링

### 로그 확인

```bash
# 전체 로그
docker-compose logs

# 실시간 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f spring-app
docker-compose logs -f nginx
```

### 상태 확인

```bash
# 컨테이너 상태
docker-compose ps

# 리소스 사용량
docker stats
```

## 🔄 업데이트

```bash
# 코드 변경 후 재배포
git pull
docker-compose down
docker-compose up -d --build
```

## 🛠️ 문제 해결

### 포트 충돌

```bash
# 사용 중인 포트 확인
sudo netstat -tulpn | grep :80
sudo netstat -tulpn | grep :443

# 충돌하는 서비스 중지
sudo systemctl stop apache2  # 예시
```

### 권한 문제

```bash
# 업로드 디렉토리 권한 설정
sudo chown -R 1000:1000 ./uploads
sudo chmod -R 755 ./uploads
```

### 데이터베이스 연결 문제

```bash
# PostgreSQL 컨테이너 확인
docker-compose logs postgres

# 데이터베이스 연결 테스트
docker-compose exec postgres psql -U xtra_user -d xtra
```

## 📞 지원

문제가 발생하면 다음을 확인하세요:

1. Docker 로그: `docker-compose logs`
2. Nginx 로그: `docker-compose logs nginx`
3. 애플리케이션 로그: `docker-compose logs spring-app`
4. 환경 변수 설정 확인
5. 네트워크 연결 확인

## 🔒 보안 고려사항

1. 강력한 데이터베이스 비밀번호 사용
2. 환경 변수 파일 (.env) 보안
3. 정기적인 보안 업데이트
4. 방화벽 설정
5. SSL/TLS 인증서 사용 