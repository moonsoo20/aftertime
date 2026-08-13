# Aftertime

> 오늘의 마음을 미래로 보내는 디지털 타임캡슐 서비스

Aftertime은 편지를 작성하고 미래의 개봉 시각을 지정해 보관하는 웹 애플리케이션입니다.
개봉 시각 전에는 서버가 편지 내용을 반환하지 않으며, 시간이 지난 뒤에만 내용을 확인할
수 있습니다. React 프론트엔드와 Spring Boot API로 구성되어 있고, 운영 빌드에서는 React
정적 파일이 Spring Boot JAR에 포함됩니다.

## 주요 기능

- 이메일과 비밀번호를 이용한 회원가입 및 로그인
- BCrypt를 이용한 비밀번호 단방향 암호화
- Spring Security 세션 기반 인증
- 로그인 사용자별 타임캡슐 데이터 분리
- 제목, 수신자, 편지, 개봉 날짜를 지정한 캡슐 생성
- 캡슐별 실시간 개봉 카운트다운
- 개봉 시각 전 편지 내용의 서버 응답 차단
- 개봉 시각이 지난 캡슐의 편지 확인
- 데스크톱과 모바일을 지원하는 반응형 화면
- 개발 환경 H2 및 운영 환경 PostgreSQL 지원
- React와 Spring Boot를 하나의 실행 JAR로 패키징
- Docker 및 Docker Compose 기반 실행 환경

## 기술 스택

### Frontend

- React 19
- TypeScript
- Vite
- Lucide React
- CSS3 반응형 레이아웃

### Backend

- Java 21
- Spring Boot 3.5
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Bean Validation
- Gradle Wrapper 8.14.2

### Database & Infrastructure

- H2: 로컬 개발 환경
- PostgreSQL 17: Docker 및 운영 환경
- Docker / Docker Compose
- Cloudflare Quick Tunnel: 임시 외부 공유

## 프로젝트 구조

```text
aftertime/
├── backend/
│   ├── src/main/java/com/aftertime/api/
│   │   ├── auth/       # 회원가입, 로그인, 로그아웃
│   │   ├── capsule/    # 타임캡슐 도메인과 API
│   │   ├── config/     # Spring Security 설정
│   │   └── user/       # 사용자 엔티티와 저장소
│   └── src/main/resources/
│       ├── application.yml
│       └── application-prod.yml
├── frontend/
│   └── src/            # React UI
├── Dockerfile
├── compose.yml
├── build-all.cmd       # Windows 통합 빌드
└── run-app.cmd         # Windows 통합 JAR 실행
```

## 요구사항

- Java 21
- Node.js 20 이상
- npm 또는 pnpm
- Docker Desktop(선택 사항)

IntelliJ IDEA에서는 `backend/build.gradle`을 Gradle 프로젝트로 연결하고 Gradle JVM을
Java 21로 지정해야 합니다.

## 개발 환경 실행

개발 중에는 React 개발 서버와 Spring Boot API 서버를 각각 실행합니다. Vite의 빠른
새로고침을 사용할 수 있어 화면 개발에 편리합니다.

### 1. 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

Windows CMD에서는 다음 명령을 사용할 수 있습니다.

```cmd
cd backend
gradlew.bat bootRun
```

백엔드 주소:

```text
http://localhost:8080
```

### 2. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드 주소:

```text
http://localhost:5173
```

개발 서버의 `/api` 요청은 Vite 프록시를 통해 `http://localhost:8080`으로 전달됩니다.
Windows에서는 프로젝트 루트의 `start-frontend.cmd`도 사용할 수 있습니다.

## 단일 JAR 빌드 및 실행

운영 형태에서는 먼저 React를 빌드한 뒤 결과물인 `frontend/dist`를 Spring Boot의 정적
리소스로 포함합니다. 따라서 최종적으로는 Spring Boot 서버 하나만 실행하면 됩니다.

Windows에서 프로젝트 루트의 다음 파일을 순서대로 실행합니다.

```cmd
build-all.cmd
run-app.cmd
```

또는 직접 빌드할 수 있습니다.

```bash
cd frontend
npm install
npm run build

cd ../backend
./gradlew bootJar
java -jar build/libs/aftertime-api-0.0.1-SNAPSHOT.jar
```

통합 실행 후 접속 주소:

```text
http://localhost:8080
```

## Docker Compose 실행

Docker Compose는 애플리케이션과 PostgreSQL을 함께 실행합니다.

```bash
docker compose up --build
```

종료:

```bash
docker compose down
```

데이터베이스 볼륨까지 삭제하려면 아래 명령을 사용합니다. 기존 데이터가 모두 삭제되므로
주의하세요.

```bash
docker compose down -v
```

## 환경변수

`prod` 프로필에서는 다음 환경변수가 필요합니다.

| 환경변수 | 설명 | 예시 |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Spring 운영 프로필 | `prod` |
| `PORT` | 애플리케이션 포트 | `8080` |
| `DATABASE_URL` | PostgreSQL JDBC 주소 | `jdbc:postgresql://db:5432/aftertime` |
| `DATABASE_USERNAME` | 데이터베이스 사용자 | `aftertime` |
| `DATABASE_PASSWORD` | 데이터베이스 비밀번호 | 안전한 임의 문자열 |

운영 비밀번호는 Git에 커밋하지 말고 배포 플랫폼의 Secret 또는 환경변수로 관리해야 합니다.

## API 요약

### 인증

| Method | Endpoint | 설명 | 인증 필요 |
|---|---|---|---|
| `POST` | `/api/auth/signup` | 회원가입 후 자동 로그인 | 아니요 |
| `POST` | `/api/auth/login` | 로그인 및 세션 생성 | 아니요 |
| `GET` | `/api/auth/me` | 현재 로그인 사용자 조회 | 예 |
| `POST` | `/api/auth/logout` | 세션 종료 | 예 |

회원가입 요청 예시:

```json
{
  "name": "홍길동",
  "email": "user@example.com",
  "password": "password123"
}
```

### 타임캡슐

| Method | Endpoint | 설명 |
|---|---|---|
| `GET` | `/api/capsules` | 로그인 사용자의 캡슐 목록 |
| `POST` | `/api/capsules` | 새 캡슐 생성 |
| `GET` | `/api/capsules/{id}/open` | 개봉 가능한 캡슐 내용 조회 |

캡슐 생성 요청 예시:

```json
{
  "title": "스물아홉의 여름",
  "recipient": "미래의 나",
  "message": "이 편지를 열어볼 너에게...",
  "unlockAt": "2027-08-13T12:00:00Z"
}
```

개봉 시각 이전에 `/open`을 호출하면 서버는 `423 Locked`를 반환합니다. 다른 사용자의
캡슐 ID로 접근하면 해당 캡슐을 반환하지 않습니다.

## 데이터 저장

로컬 실행에서는 H2 파일 데이터베이스를 사용합니다. 데이터는 실행 위치의 `data` 폴더에
저장되며 Git에서 제외됩니다. Docker 및 운영 환경에서는 PostgreSQL을 사용합니다.

기존 로그인 기능 도입 전에 생성한 캡슐은 소유자 정보가 없으므로 새 계정의 목록에 표시되지
않습니다.

## 임시 외부 공유

로컬 서버를 잠시 휴대폰이나 외부 사용자에게 보여주려면 Spring Boot 서버를 실행한 상태에서
다음 파일을 실행할 수 있습니다.

```cmd
open-public-tunnel.cmd
```

명령창에 표시되는 `https://...trycloudflare.com` 주소를 공유합니다. 명령창을 닫거나 아래
파일을 실행하면 링크가 종료됩니다.

```cmd
close-public-tunnel.cmd
```

Quick Tunnel은 개발 데모 전용이며 고정 주소나 가용성을 보장하지 않습니다.

## 현재 제한사항

- 이메일 소유 확인과 비밀번호 재설정이 없습니다.
- CSRF 보호와 로그인 시도 횟수 제한을 추가해야 합니다.
- 캡슐 수정, 삭제, 사진 및 음성 첨부는 아직 지원하지 않습니다.
- 개봉일 이메일 알림과 공개 공유 링크는 아직 지원하지 않습니다.
- 운영 환경에서는 `ddl-auto: update` 대신 Flyway 같은 마이그레이션 도구가 권장됩니다.

## 다음 개발 목표

1. 캡슐 수정 및 삭제
2. 사진과 음성 메시지 첨부
3. 수신자용 비공개 공유 링크
4. 개봉일 이메일 알림과 재시도 처리
5. 이메일 인증 및 비밀번호 재설정
6. 테스트 코드와 CI 파이프라인
7. 고정 도메인을 사용하는 클라우드 배포

## 라이선스

현재 별도의 라이선스가 지정되어 있지 않습니다. 외부 공개 및 재사용을 허용하려면 목적에
맞는 라이선스 파일을 추가하세요.
