# B 站视频资产 AI 分析 Agent 后端 MVP 协作交接记录

## 当前结论

- 当前工作分支：`codex/bili-agent-backend-mvp`
- 远端跟踪分支：`origin/codex/bili-agent-backend-mvp`
- 当前基线 HEAD：`3abd004`（`feat: complete auth baseline with jwt login`）
- 当前工作树状态：`Task 3` 代码已提交，文档状态更新待提交
- 当前完成进度：`Task 1`、`Task 2`、`Task 3` 已由主线程完成并通过本地测试
- 下一步：进入 `Task 4: 实现 B 站绑定与视频资产同步`

## 已完成内容

### Task 1: 搭建后端工程骨架

已落地文件：

- `backend/pom.xml`
- `backend/mvnw`
- `backend/mvnw.cmd`
- `backend/.mvn/wrapper/maven-wrapper.properties`
- `backend/src/main/java/com/jodio/biliagent/BiliAgentApplication.java`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/application-test.yml`
- `backend/src/test/java/com/jodio/biliagent/BiliAgentApplicationTests.java`

关键说明：

- `BiliAgentApplicationTests` 使用了 `@ActiveProfiles("test")`
- 同时通过 `spring.autoconfigure.exclude=org.redisson.spring.starter.RedissonAutoConfigurationV2` 避免上下文测试强依赖真实 Redis
- `backend/mvnw.cmd` 已修复 Windows PowerShell 下 `.Target[0]` 空指针问题

### Task 2: 落数据库迁移与通用基础设施

已落地文件：

- `backend/src/main/resources/db/migration/V1__init_schema.sql`
- `backend/src/main/java/com/jodio/biliagent/common/model/BaseEntity.java`
- `backend/src/main/java/com/jodio/biliagent/common/model/ApiResponse.java`
- `backend/src/main/java/com/jodio/biliagent/common/exception/GlobalExceptionHandler.java`
- `backend/src/main/java/com/jodio/biliagent/analysis/domain/AnalysisStatus.java`
- `backend/src/test/java/com/jodio/biliagent/schema/FlywaySchemaTests.java`

关键说明：

- `application-test.yml` 的 H2 URL 使用了 `NON_KEYWORDS=USER`，用于兼容 `user` 表名
- `GlobalExceptionHandler` 已做过一次安全修复，不再把通用异常明文回显给客户端
- `BaseEntity` 已补齐基础 getter/setter，方便后续复用

### Task 3: 实现注册登录与数据隔离基线

当前已落地/修改文件：

- `backend/pom.xml`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/application-test.yml`
- `backend/src/main/java/com/jodio/biliagent/auth/controller/AuthController.java`
- `backend/src/main/java/com/jodio/biliagent/auth/service/AuthService.java`
- `backend/src/main/java/com/jodio/biliagent/auth/security/JwtService.java`
- `backend/src/main/java/com/jodio/biliagent/auth/security/JwtAuthenticationFilter.java`
- `backend/src/main/java/com/jodio/biliagent/auth/security/SecurityConfig.java`
- `backend/src/main/java/com/jodio/biliagent/auth/model/UserEntity.java`
- `backend/src/main/java/com/jodio/biliagent/auth/mapper/UserMapper.java`
- `backend/src/main/java/com/jodio/biliagent/common/exception/GlobalExceptionHandler.java`
- `backend/src/test/java/com/jodio/biliagent/auth/AuthControllerTests.java`

当前实现结果：

- 新增 `POST /api/auth/register`
  - 返回 `ApiResponse<String>`，`data = "registered"`
  - 注册时会写入 `user` 表
- 新增 `POST /api/auth/login`
  - 使用用户名 + 密码校验
  - 登录成功返回 JWT token
- `JwtService` 已从 Base64 占位实现升级为 `io.jsonwebtoken` 真实 JWT
  - 包含 `subject`
  - 包含 `uid`
  - 包含 `issuedAt`
  - 包含 `expiration`
  - 使用 HMAC 签名
- 新增 `JwtAuthenticationFilter`
  - 从 Bearer Token 解析 `uid/username`
  - 放入 Spring Security 上下文
- `/api/auth/me`
  - 不再返回固定 `"me"`
  - 现在会返回当前用户视图：`userId/username/nickname/status`
- `GlobalExceptionHandler`
  - `IllegalArgumentException -> 400`
  - `BadCredentialsException -> 401`

## 已完成验证

### Java 环境

已验证通过：

```powershell
$env:JAVA_HOME='C:\tmp\oracle-jdk-21'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
java -version
```

当前可用版本：

- `Java 21.0.11`

### Redis / Redisson 环境

已验证通过：

```powershell
Get-Service -Name *redis*,*memurai*
Test-NetConnection -ComputerName 127.0.0.1 -Port 6379
```

结论：

- 本机 `127.0.0.1:6379` 可连通
- 实际服务为 `Memurai`
- 当前 Redisson 测试链路可正常连通本机 Redis

### Maven 依赖现状

已验证本地缓存中存在以下依赖：

- `com.baomidou:mybatis-plus-spring-boot3-starter:3.5.16`
- `org.redisson:redisson-spring-boot-starter:3.52.0`
- `dev.langchain4j:langchain4j-open-ai:1.16.1`

补充结论：

- `Lua` 当前没有独立 `lua.exe`
- 但本项目 Task 5 的 Lua 用法是“Redis 脚本文件 + Redisson/Redis 执行”，不需要单独安装 Lua 解释器

### 测试结果

已验证以下命令通过：

```powershell
$env:JAVA_HOME='C:\tmp\oracle-jdk-21'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
backend\mvnw.cmd -f backend/pom.xml -Dtest=AuthControllerTests test
backend\mvnw.cmd -f backend/pom.xml test
```

当前结果：

- `AuthControllerTests`：`3 tests, 0 failures`
- 全量 backend 测试：`5 tests, 0 failures`

## 代码审查流程记录

### Task 3 规范审查结论

主线程根据 Task 3 原文重新核对后，当前实现已覆盖此前 spec reviewer 提出的缺口：

1. 已补 `POST /api/auth/login`
2. 已补真实 JWT 与 `jjwt` 依赖
3. 已补当前用户解析与 `/api/auth/me` 的真实返回
4. `register()` 返回值已对齐计划中的 `"registered"`

结论：当前 Task 3 可视为 **Spec compliant**

### Task 3 代码质量结论

主线程已额外做了两处质量修复：

1. 修正 `BadCredentialsException` 返回 401，避免错误地混到 400
2. 修正 `/api/auth/me` 中对 principal 的强制类型假设，避免未来非 JWT principal 直接 `ClassCastException`

另外，修复过程中遇到并解决了一个真实问题：

- 初版为了复用 `BaseEntity`，让 `UserEntity` 继承了包含 `userId` 字段的基类
- 但 `user` 表并没有 `user_id` 列，导致注册插入时触发 `BadSqlGrammarException`
- 现已回退为 `UserEntity` 自身维护最小字段映射，仅保留 `password_hash` 的显式映射

## 子代理使用经验与限制

### 已确认的限制

- **显式指定 `gpt-5.4` 的子代理在当前 ChatGPT 账号下会被平台 400 拒绝**
- 典型报错：
  - `The 'gpt-5.4' model is not supported when using Codex with a ChatGPT account.`

这类失败是平台/账号能力限制，**不是任务卡死，也不是代码问题**。

### 新观察

用户反馈：有些“看起来卡住”的子代理，手动点进去随意输入一段文字后，确实可能继续运行。

因此后续建议区分两类情况：

1. **明确 400 / model not supported**
   - 直接判定为平台硬失败
   - 不要继续死等
   - 改为不显式指定 `gpt-5.4` 的子代理，或者主线程接管
2. **仅表现为长时间无响应，但没有明确错误**
   - 可优先保留，视作“可能可手动激活”
   - 不必第一时间关闭

## Git 与推送注意事项

- 当前分支：`codex/bili-agent-backend-mvp`
- 当前相对远端：代码提交后已继续领先远端，push 前请以 `git status --branch` 现状为准
- Task 3 最新修复已提交为 `3abd004`
- 工作区中存在大量未跟踪内容，提交时要严格限定文件范围，避免误带：
  - `backend/target/`
  - `notes/`
  - 图片、本地资料
  - 非本任务文档草稿

### 敏感信息注意事项

此前已做过一次 tracked 文件安全扫描，未发现真实密钥、私钥、证书、token 泄露。

本轮新增的 JWT 配置使用的是：

- `application.yml`：`${APP_JWT_SECRET:bili-agent-demo-secret-key-bili-agent-demo}`
- `application-test.yml`：测试用 demo secret

这两项都属于开发占位，不是用户私钥，但 push 前仍建议再做一轮聚焦扫描。

## 建议下一步

1. 先提交当前 Task 3 修复与 handoff 更新
2. push 前做一轮敏感信息扫描，只扫本次拟提交文件
3. push 到 `origin/codex/bili-agent-backend-mvp`
4. 然后从 `Task 4: 实现 B 站绑定与视频资产同步` 继续
5. 如果继续用子代理：
   - 不要显式指定 `gpt-5.4`
   - 优先使用默认继承模型
   - 若“无报错但像卡住”，可先保留，允许人工激活再观察
