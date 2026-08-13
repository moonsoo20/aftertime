# Aftertime

React + Spring Boot로 만든 디지털 타임캡슐 MVP입니다. 운영 빌드는 React를 Spring Boot
JAR에 포함하므로 서버 하나만 실행하면 화면과 API가 함께 열립니다.

## 실행

요구사항: Java 21, Node.js 20+

```bash
cd backend
./gradlew bootRun
```

```bash
cd frontend
pnpm install
pnpm run dev
```

Windows에서는 프로젝트 루트의 `start-frontend.cmd`를 실행해도 됩니다. 포함된 사용자용
Node.js LTS 경로를 자동으로 사용하고 React 개발 서버를 시작합니다.

개발 중에는 프론트엔드가 `http://localhost:5173`, API가 `http://localhost:8080`에서 실행됩니다.

## 하나로 빌드하고 실행

프로젝트 루트에서 `build-all.cmd`를 실행한 뒤 `run-app.cmd`를 실행합니다. 브라우저에서
`http://localhost:8080`을 열면 React 화면과 Spring API가 한 서버에서 제공됩니다.

## Docker 배포

```bash
docker compose up --build
```

운영 환경에서는 `SPRING_PROFILES_ACTIVE=prod`와 `DATABASE_URL`, `DATABASE_USERNAME`,
`DATABASE_PASSWORD`를 설정하면 PostgreSQL을 사용합니다. `Dockerfile` 하나로 일반적인
Docker 지원 클라우드에 배포할 수 있습니다.

IntelliJ에서는 `backend/build.gradle`을 Gradle 프로젝트로 연결한 뒤 Gradle 배포판을
`Wrapper`로 선택하세요. 프로젝트는 Gradle 8.14.2와 설치된 Microsoft JDK 21 경로로
고정되어 있습니다.

## 핵심 기능

- 편지와 개봉 시간을 지정한 타임캡슐 생성
- 캡슐 목록과 실시간 카운트다운
- 개봉 시간 이전에는 서버가 내용 자체를 반환하지 않음
- 시간이 지난 캡슐 개봉
- H2 파일 데이터베이스에 영속 저장
