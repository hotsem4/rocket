# 🚀 프로젝트 이름: Rocket Community API

## 📌 한 줄 소개
JWT 기반 인증 시스템과 Redis를 활용한 게시글 좋아요 기능이 통합된 커뮤니티 API 백엔드 서비스

---

## 📖 프로젝트 개요
사용자 인증 및 게시글 작성/수정/삭제, 좋아요 기능을 제공하는 RESTful API 백엔드 시스템입니다.

- **인증/인가**: JWT + Spring Security 기반 구현
- **캐싱 전략**: Redis를 통한 좋아요 수 실시간 반영
- **아키텍처**: CQRS 기반 계층 분리를 통해 확장성과 유지보수성 강화

**주요 도메인**: 사용자(User), 게시글(Post), 좋아요(Like)  
**목표**: 도메인 간 역할 분리 및 확장 가능한 구조 설계, 인증/인가 보안 강화  
**도입 배경**: 대용량 트래픽 상황에서도 성능 저하 없이 좋아요 수를 관리하기 위한 Redis 도입과 자동 토큰 재발급 UX 구현

---

## 🧪 주요 기능

### 🔐 인증 / 인가 (Auth)
- JWT 기반 인증/인가 구현 (AccessToken + RefreshToken)
- Role은 USER, ADMIN 2가지 (관리자 전용 API 예: @PreAuthorize("hasRole('ADMIN')"))
- AccessToken 만료 시 → RefreshToken으로 자동 재발급
- 비밀번호 암호화 (BCryptPasswordEncoder)
- 이메일 중복 검사 API
- 로그인 / 로그아웃 / 토큰 재발급 API 제공
- 비밀번호 변경: resetToken을 Redis에 저장하고, 해당 토큰으로 비밀번호 변경 가능

### 📝 게시글 (Post)
- 게시글 CRUD (제목 + 내용 + 작성자 식별)
- 제목 검색 및 전체 게시글 조회
- 좋아요 기능 (Redis에 likeCount 캐싱, 중복 방지)
- 스케줄러(likeCountSyncScheduler)로 매 분마다 DB 동기화
  - TTL은 30분이지만, 동기화는 TTL과 무관하게 likeCount > 0이면 DB 반영
- Redis 키 초기화(resetLikeCount)로 중복 반영 방지

### 👤 사용자 (User)
- 회원가입 시 이메일, 닉네임, 성별, 나이, 주소 입력
- 사용자 정보 조회 / 수정 / 삭제
- @Embeddable을 이용한 Address 객체 설계
- 닉네임 중복 방지

---

## 🛠️ 기술 스택

| 구분 | 사용 기술                    |
|------|--------------------------|
| Language | Java 23                  |
| Framework | Spring Boot 3.4.2        |
| ORM | Spring Data JPA          |
| Security | Spring Security + JWT    |
| Caching | Redis                    |
| Database | H2 / MySQL (전환 고려 가능)    |
| Docs | Swagger (OpenAPI 3)      |
| Validation | Jakarta Validation |
| Testing | JUnit + Mockito |

---

## 🧱 아키텍처 구조 (계층 분리)

```
Controller
   ↓
Application Service (Facade, Mapper, ServiceImpl)
   ↓
Domain Service (Interface 정의)
   ↓
Repository (Reader / Writer 분리 - CQRS 스타일)
   ↓
JPA / Redis 구현체
```

### 구조 특징
- `UserReader` / `UserWriter` 분리 → 단방향 의존
- `AuthUserReader` → 인증 전용 조회 전용 리더
- `PostResponseAssembler` → 좋아요 수 + 게시글 DTO 변환
- `JwtAuthenticationFilter` → 토큰 검증 및 자동 인증 처리
- `RedisRefreshTokenStore`, `PostLikeRedisService` → Redis 활용
- `ResetTokenStore`(예: RedisResetTokenStroe)를 통해 비밀번호 변경 토큰 관리

---

## ⏱ Redis 활용 전략

| 기능 | 목적 |
|------|------|
| RefreshToken 저장 | 자동 로그인 구현 (7일 TTL) |
| ResetToken 저장 | 비밀번호 변경 가능 여부 판단(5분 TTL) |
| 좋아요 수 캐싱 | 실시간 증가 처리 후 DB 동기화 |

- RefreshToken 저장 (유효기간 7일), 자동 로그인 구현
- 좋아요 수 캐싱 (TTL 30분이지만 스케줄러는 TTL 고려 안 함)
- `LikeCountSyncScheduler`로 매 분마다 DB 동기화
- User가 사용중일 경우, RefreshToken이 일정 시간밖에 남지 않은 경우 재발급
- 비밀번호 변경(ResetToken) 저장: 5분 유효기간 설정, 만료 시 자동 삭제
   - 사용자 이메일/전화번호 검증 후 생성된 resetToken을 Redis에 저장
   - resetToken 만료 전 사용시 비밀번호 변경 가능
   - 비밀번호 변경 성공 시 메모리 관리를 위해 자동 삭제

---

## 🔍 예외 처리
`GlobalExceptionHandler`에서 커스텀 예외를 `ErrorResponse` 형태로 통일 처리

**주요 예외 종류**:
- `AccessDeniedCustomException`
- `DuplicateEmailException`
- `DuplicateLikeException`
- `DuplicateNicknameException`
- `InvalidEmailFormatException`
- `InvalidTokenException`
- `LikeNotFoundException`
- `LoginFailedException`
- `PostNotFoundException`
- `RedisOperationException`
- `UserNotFoundException`

---

## 👨‍💻 나의 역할 및 기여
- 전체 도메인 설계 및 계층 구조 설계 주도
- Spring Security 기반 인증/인가 (JWT + Role) 직접 구현
- Redis를 활용한 좋아요 수 실시간 반영 & 스케줄러 동기화 구조 구축
- CQRS 스타일의 Repository 분리 (Reader/Writer)
- 좋아요 수 실시간 반영을 위한 Redis-DB 동기화 구조 구현
- Swagger 기반 API 문서화
- 예외 처리 구조 및 테스트 코드 정비
- Role은 USER/ADMIN 2가지로 인가 처리
- 비밀번호 변경 로직에서 resetToken을 Redis에 저장해 만료 시점 관리

---

## 🧩 트러블슈팅 & 아키텍처 회고
👉 [트러블슈팅 & 설계 고민 노션 페이지](https://woozy-cross-139.notion.site/1c50f444294f80ecb96dc1a94866cc71?pvs=4)


- Redis TTL 동기화 실패에 따른 좋아요 누락 문제 대응
- Auth-User 도메인 간 의존성 최소화를 위한 CQRS 분리
- 테스트에서 flush() 미작성 시 DB 반영이 안되어 테스트 실패 -> PostWriter에 entityManger.flush() 명시
- Role 기반 인가 정책: 사용자(기본 USER)와 관리자(ADMIN) 구분하여 접근 제어
- 비밀번호 변경용 resetToken도 Redis 관리: TTL 만료 시 자동 삭제로 보안 강화

---

> 💡 이 프로젝트는 추후 Kafka + ElasticSearch 연동, 관리자 페이지 기능 확장, QueryDSL 기반 검색 기능 구현 등으로 고도화할 예정입니다.

