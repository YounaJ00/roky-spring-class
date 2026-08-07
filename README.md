# Spring Security 인증과 비관적 락 동시성 처리

JWT 기반 인증과 게시물 CRUD를 헥사고날 아키텍처로 구현하고, 동일 게시물의 조회수를 동시에 증가시킬 때 발생하는 Lost Update를 MySQL 비관적 락으로 해결하는 프로젝트입니다.

## 기술 스택

현재 저장소 설정을 기준으로 합니다.

| 구분 | 기술 |
| --- | --- |
| Language | Java 25 |
| Framework | Spring Boot 4.1.0, Spring Security |
| Persistence | Spring Data JPA, Flyway, MySQL 8.4 |
| Authentication | JJWT 0.13.0, BCrypt |
| Test | JUnit 5, Testcontainers, ExecutorService, CountDownLatch |

## 실행 방법

### 1. 환경 변수 준비

```bash
cp .env.example .env
```

`.env`에서 MySQL 접속 정보와 JWT 서명 키를 설정합니다.

```properties
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=spring_boot
MYSQL_USER=spring_boot
MYSQL_PASSWORD=change-me
MYSQL_ROOT_PASSWORD=change-root-password
JWT_SECRET=32-bytes-or-longer-secret-key-change-me
```

JWT HMAC 키는 최소 32바이트 이상의 예측하기 어려운 값으로 설정합니다. 실제 비밀키는 Git에 커밋하지 않습니다.

### 2. MySQL 실행

```bash
docker compose up -d mysql
```

애플리케이션 시작 시 Flyway가 `test`, `users`, `posts` 테이블을 생성합니다.

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

기본 주소는 `http://localhost:8080`입니다.

### 4. 테스트 실행

```bash
./gradlew test --no-daemon
```

동시성 테스트만 실행하려면 다음 명령을 사용합니다.

```bash
./gradlew test \
  --tests com.example.springboot.post.PostConcurrencyTest \
  --no-daemon
```

`PostConcurrencyTest`는 MySQL의 실제 락 동작을 확인하므로 로컬 MySQL이 실행 중이어야 합니다. 현재 테스트 픽스처는 `author_id=1`을 사용하므로 깨끗한 DB에서는 먼저 회원 한 명을 생성해야 합니다.

## API 명세

### 인증 API

| 기능 | Method | Path | 인증 | 성공 상태 |
| --- | --- | --- | --- | --- |
| 회원가입 | `POST` | `/open-api/v1/auth/signup` | 불필요 | `200 OK` |
| 로그인 | `POST` | `/open-api/v1/auth/login` | 불필요 | `200 OK` |
| 내 정보 조회 | `GET` | `/api/v1/users/me` | Bearer Token | `200 OK` |

회원가입 요청:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

회원가입 및 내 정보 응답:

```json
{
  "id": 1,
  "email": "user@example.com"
}
```

로그인 요청:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

로그인 응답:

```json
{
  "accessToken": "eyJ...",
  "expiresAt": "2026-08-03T08:00:00Z"
}
```

인증이 필요한 요청에는 다음 헤더를 전달합니다.

```http
Authorization: Bearer {accessToken}
```

### 게시물 API

| 기능 | Method | Path | 인증 | 성공 상태 |
| --- | --- | --- | --- | --- |
| 게시물 작성 | `POST` | `/api/v1/posts` | 필요 | `201 Created` |
| 단건 조회·조회수 증가 | `GET` | `/open-api/v1/posts/{id}` | 불필요 | `200 OK` |
| 목록 조회 | `GET` | `/open-api/v1/posts` | 불필요 | `200 OK` |
| 게시물 수정 | `PATCH` | `/api/v1/posts/{id}` | 작성자 | `200 OK` |
| 게시물 삭제 | `DELETE` | `/api/v1/posts/{id}` | 작성자 | `204 No Content` |

작성 및 수정 요청:

```json
{
  "title": "비관적 락 정리",
  "content": "select for update의 동작을 정리한다."
}
```

현재 수정 API는 `PATCH`이지만 `title`과 `content`를 모두 필수로 받습니다. 작성자 ID는 요청에서 받지 않고 `Authentication`의 principal에서 가져옵니다.

게시물 응답:

```json
{
  "id": 1,
  "authorId": 1,
  "title": "비관적 락 정리",
  "content": "select for update의 동작을 정리한다.",
  "viewCount": 1
}
```

### 실패 응답

```json
{
  "status": 401,
  "error": "UNAUTHORIZED",
  "message": "인증이 필요합니다."
}
```

| 상태 | 의미 |
| --- | --- |
| `400 Bad Request` | 입력값 검증 실패 |
| `401 Unauthorized` | 토큰 누락·만료·위조 또는 로그인 실패 |
| `403 Forbidden` | 다른 사용자의 게시물 수정·삭제 시도 |
| `404 Not Found` | 사용자 또는 게시물 없음 |
| `409 Conflict` | 이미 사용 중인 이메일 |

## 패키지 구조

```text
com.example.springboot
├── common
│   ├── exception                 # 공통 예외와 JSON 실패 응답
│   └── security                  # SecurityFilterChain, JWT 필터, principal
├── config                        # JPA Auditing 설정
├── user
│   ├── domain                    # 프레임워크 비의존 User 도메인
│   ├── application
│   │   ├── port.in               # UserUseCase
│   │   ├── port.out              # Repository/Auth/Token 포트
│   │   └── service               # 유스케이스 구현과 트랜잭션 경계
│   └── adapter
│       ├── in.web                # 인증·사용자 HTTP 어댑터
│       └── out                   # JPA 및 JWT/Security 어댑터
└── post
    ├── domain                    # Post 도메인
    ├── application
    │   ├── port.in               # PostUseCase
    │   ├── port.out              # PostRepositoryPort
    │   └── service               # CRUD 및 조회수 증가 트랜잭션
    └── adapter
        ├── in.web                # 게시물 HTTP 어댑터
        └── out.persistence       # JPA Entity/Repository/Mapper
```

의존성은 `adapter → application → domain` 방향으로 흐릅니다. 컨트롤러는 유스케이스 인터페이스에 의존하고, 애플리케이션 서비스는 아웃바운드 포트를 통해 영속성과 보안 기술을 사용합니다. JPA 엔티티와 도메인 객체는 분리되어 있습니다.

## 동시성 처리 요약

게시물 단건 조회는 `PESSIMISTIC_WRITE` 조회로 대상 행을 잠근 뒤 같은 트랜잭션 안에서 조회수를 증가시킵니다. 동시에 접근한 다른 트랜잭션은 선행 트랜잭션이 커밋하거나 롤백할 때까지 대기하므로 Lost Update가 발생하지 않습니다.

- 락 메서드: `PostJpaRepository.findByIdForUpdate`
- 락 타임아웃 힌트: `jakarta.persistence.lock.timeout=3000`
- 동시 요청: 100개 스레드
- 락 없음 결과: 최종 조회수 `1`로 증가 유실 재현
- 비관적 락 결과: 최종 조회수 `100`

## 완료 상태

- [x] 회원가입, 로그인, 내 정보 조회 API
- [x] BCrypt 비밀번호 해싱과 이메일 DB 유니크 제약
- [x] Stateless JWT 인증 및 401/403 구분
- [x] 게시물 CRUD와 작성자 검증
- [x] 락 없는 Lost Update 재현
- [x] 비관적 락 적용 후 100회 증가 보장
- [x] 실행 SQL의 `for update` 확인
- [x] 학습 확인 질문과 추가 동시성 주제 문서화
