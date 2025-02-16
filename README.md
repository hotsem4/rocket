**📅 목표 기간:** 6개월 (각 실습은 2~3주 내 완성)
**📌 주요 키워드:** Spring Boot, JPA, AWS, Nginx, Docker, CI/CD
---

## **1️⃣ Spring 기초 & REST API 개발 (2주)**

📅 **목표 기한:** 2025년 2월 27일

🎯 **목표:** Spring Boot로 RESTful API 개발 & 기본적인 서비스 구조 익히기

### **👨‍💻 실습 과제: 사용자 & 게시판 API 만들기**

- [ ]  **회원 관리 API** (`UserController`)
    -[ ] 회원 가입 (`POST /users`)
    -[ ] 회원 정보 조회 (`GET /users/{id}`)
    -[ ] 회원 정보 수정 (`PUT /users/{id}`)
    -[ ] 회원 삭제 (`DELETE /users/{id}`)
- [ ]  **게시판 API** (`PostController`)
    -[ ] 게시글 작성 (`POST /posts`)
    -[ ] 게시글 목록 조회 (`GET /posts`)
    -[ ] 게시글 상세 조회 (`GET /posts/{id}`)
    -[ ] 게시글 수정 (`PUT /posts/{id}`)
    -[ ] 게시글 삭제 (`DELETE /posts/{id}`)
- [ ]  **DTO & Service 계층 분리**
- [ ]  **Spring Validation을 활용한 입력값 검증**
- [ ]  **예외 처리 (`@RestControllerAdvice`)**
- [ ]  **Postman 또는 Swagger로 API 테스트**
- [ ]  **GitHub에 코드 업로드**

### ✅ **완료 조건:** API 정상 작동 & 테스트 성공

---

## **2️⃣ JPA & QueryDSL 적용 (3주)**

📅 **목표 기한:** 2025년 3월 20일

🎯 **목표:** JPA & QueryDSL을 활용한 데이터베이스 최적화

### **👨‍💻 실습 과제: 게시판 기능 확장 & 성능 개선**

- [ ]  **JPA를 활용한 CRUD 구현**
- [ ]  **Lazy Loading & Fetch Join을 이용한 성능 최적화**
- [ ]  **QueryDSL을 활용한 게시글 검색 기능**
- [ ]  **페이지네이션 적용 (`Pageable`)**
- [ ]  **트랜잭션 & 동시성 문제 해결 (Optimistic Lock 적용)**
- [ ]  **배포 환경에서 H2 → MySQL 변경**
- [ ]  **게시판 API Postman 테스트**

### ✅ **완료 조건:** QueryDSL 적용 & 성능 최적화 완료

---

## **3️⃣ AWS 배포 & 데이터 저장 (3주)**

📅 **목표 기한:** 2025년 4월 10일

🎯 **목표:** EC2 + RDS + S3를 활용한 백엔드 배포

### **👨‍💻 실습 과제: AWS 배포 & 데이터 저장**

- [ ]  **EC2 인스턴스 생성 후 Spring Boot 애플리케이션 실행**
- [ ]  **RDS(MySQL) 설정 후 데이터베이스 연동**
- [ ]  **S3를 활용한 이미지 업로드 기능 추가**
- [ ]  **CloudWatch를 활용한 서버 모니터링**
- [ ]  **HTTPS 적용 (Let’s Encrypt or ACM)**

### ✅ **완료 조건:** AWS에서 정상 작동 & S3 업로드 테스트 성공

---

## **4️⃣ Nginx 적용 & 성능 최적화 (2주)**

📅 **목표 기한:** 2025년 4월 24일

🎯 **목표:** Nginx를 활용한 로드밸런싱 & 정적 파일 캐싱

### **👨‍💻 실습 과제: Nginx + Reverse Proxy 설정**

- [ ]  **EC2에 Nginx 설치 및 Spring Boot 애플리케이션과 연결**
- [ ]  **Nginx 리버스 프록시 설정**
- [ ]  **정적 파일 캐싱 적용 (CSS, JS, 이미지)**
- [ ]  **로드밸런싱 실습 (EC2 2개에 애플리케이션 배포 후 부하 분산)**
- [ ]  **부하 테스트 (`ab`, `wrk`) 수행 후 성능 비교**

### ✅ **완료 조건:** Nginx로 서버 운영 & 부하 테스트 통과

---

## **5️⃣ Docker & 컨테이너화 (3주)**

📅 **목표 기한:** 2025년 5월 15일

🎯 **목표:** Spring Boot를 Docker 컨테이너로 실행 & 배포

### **👨‍💻 실습 과제: Docker 기반 환경 구축**

- [ ]  **Spring Boot 애플리케이션을 Docker로 실행**
- [ ]  **Dockerfile 작성 및 컨테이너 실행**
- [ ]  **MySQL 컨테이너와 연동**
- [ ]  **Docker Compose를 활용한 `Spring Boot + MySQL + Redis` 컨테이너 실행**
- [ ]  **AWS 배포 환경에서 Docker 적용**
- [ ]  **도커 네트워크를 활용한 마이크로서비스 환경 실습**

### ✅ **완료 조건:** Docker 기반으로 서비스 실행 & AWS 배포 완료

---

## **6️⃣ CI/CD 자동 배포 (3주)**

📅 **목표 기한:** 2025년 6월 5일

🎯 **목표:** GitHub Actions or Jenkins로 자동 배포 설정

### **👨‍💻 실습 과제: CI/CD 자동화 구축**

- [ ]  **GitHub Actions을 활용한 CI/CD 구축**
- [ ]  **main 브랜치에 push 시 자동 배포 설정**
- [ ]  **Jenkins 설치 & EC2에서 자동 배포 적용**
- [ ]  **Docker 기반 무중단 배포 (`Rolling Update`, `Blue-Green Deployment`)**
- [ ]  **배포 과정 로그 저장 & 모니터링 시스템 구축**

### ✅ **완료 조건:** 코드 변경 시 자동 배포 & 무중단 배포 성공

---

# **🚀 최종 목표 (6개월 후)**

✅ **백엔드 개발 실력 완성 (Spring Boot + JPA + QueryDSL)**

✅ **AWS & Nginx 배포 가능**

✅ **Docker & CI/CD 자동 배포 구축 완료**

✅ **실무 수준의 백엔드 프로젝트 완성**

---

# **📢 마무리 & 실행 방법**

- **각 실습별로 진행하면서 GitHub에 코드 업로드**
- **Postman으로 API 테스트 후, 문서 정리**
- **배포 완료 후, 블로그 or Notion에 학습 내용 정리**
- **진행하면서 궁금한 점 있으면 언제든 질문!** 😎🔥