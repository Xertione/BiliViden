# bili-agent-backend

Spring Boot MVP backend for Bilibili video asset sync, lightweight analysis, knowledge cards, and traceable QA.

## Local Run

### Prerequisites

1. Start MySQL 8
2. Start Redis 7
3. Create database `bili_agent`
4. Prepare Java 21

### Environment Variables

```powershell
$env:APP_DB_URL="jdbc:mysql://127.0.0.1:3306/bili_agent?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8"
$env:APP_DB_USERNAME="root"
$env:APP_DB_PASSWORD="replace-with-your-local-password"
$env:APP_JWT_SECRET="replace-with-your-local-secret"
$env:APP_AI_OPENAI_BASE_URL="https://api.openai.com/v1"
$env:APP_AI_OPENAI_API_KEY=""
$env:APP_AI_OPENAI_MODEL="deepseek-chat"
```

Notes:

- `APP_DB_URL`, `APP_DB_USERNAME`, and `APP_DB_PASSWORD` should match your local MySQL instance.
- `APP_JWT_SECRET` is required for local auth token signing.
- AI configuration is optional in the current MVP skeleton. When `APP_AI_OPENAI_API_KEY` is blank, AI beans stay unconfigured by design.
- Do not commit real secrets or provider keys into the repository.
- The default JDBC URL already includes `allowPublicKeyRetrieval=true` for local MySQL 8 compatibility.

### Start Command

```powershell
backend\mvnw.cmd -f backend/pom.xml spring-boot:run
```

If your local shell still points `java` to JDK 8, switch to JDK 21 first or run the packaged jar with a Java 21 path explicitly.

## Minimal Validation Flow

### 1. Register

`POST /api/auth/register`

```json
{
  "username": "demo-user",
  "password": "Password123!",
  "nickname": "Demo User"
}
```

### 2. Login

`POST /api/auth/login`

```json
{
  "username": "demo-user",
  "password": "Password123!"
}
```

Use the returned bearer token for the authenticated requests below.

Except `/api/auth/register` and `/api/auth/login`, the remaining MVP chain requests require a Bearer Token.

### 3. Bind Bilibili Account

`POST /api/bili/bind`

```json
{
  "biliUid": "123456",
  "cookieSnapshot": "SESSDATA=demo-cookie"
}
```

### 4. Sync Video Assets

`POST /api/bili/sync`

Request body is not required.

### 5. Create Analysis Task

`POST /api/analysis/tasks`

```json
{
  "videoId": 101,
  "analysisType": "SUMMARY"
}
```

### 6. Create Knowledge Card

`POST /api/knowledge/cards`

```json
{
  "videoId": 101,
  "analysisTaskId": 1,
  "analysisResultId": 1
}
```

### 7. Ask QA

`POST /api/qa/ask`

```json
{
  "question": "这条视频的核心观点是什么？"
}
```

Expected MVP chain:

1. User can register and login
2. Bilibili binding succeeds
3. Sync returns counts summary
4. Analysis task returns `PENDING`
5. Knowledge card creation returns `knowledge-card-created`
6. QA returns `answer` plus `sourceRefs`

## Test

```powershell
backend\mvnw.cmd -f backend/pom.xml test
```
