# Bilibili AI Agent Frontend Implementation Plan

> **For agentic workers:** 建议使用 `subagent-driven-development`（推荐）或 `executing-plans` 配合本计划逐任务执行。步骤使用复选框（`- [ ]`）语法追踪。

**Goal:** 构建一个基于 Vite + React 的现代化前端工作台，支持视频管理、AI 分析沉淀及知识问答。

**Architecture:** 采用前后端分离架构，前端使用 `react-router-dom` 进行路由管理，利用 URL Query Params 实现抽屉状态同步。状态管理由 `TanStack Query` 负责，UI 基于 `Shadcn UI` 且遵循 `taste-skill` 的极简技术审美。

**Tech Stack:** React 18, TypeScript, Vite, Tailwind CSS v4, Shadcn UI, Framer Motion, TanStack Query, Axios.

---

## File Structure

- `frontend/`
    - `src/api/` (Axios 实例与请求封装)
    - `src/components/` (通用组件：Sidebar, Layout, UI 基础件)
    - `src/features/` (按业务域拆分)
        - `auth/` (登录/注册)
        - `workbench/` (视频列表、卡片、同步按钮)
        - `analysis/` (分析抽屉、轮询逻辑、状态显示)
        - `chat/` (对话窗口、引用标签)
        - `knowledge/` (知识卡片展示)
    - `src/hooks/` (自定义 Hook：useUrlState, useAuth)
    - `src/lib/` (工具类、Shadcn UI 适配)
    - `src/pages/` (页面容器层)

---

### Task 1: 工程初始化与视觉基线配置

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/index.html`
- Create: `frontend/tailwind.config.js` (Tailwind v4)
- Create: `frontend/src/index.css` (定义 Geist/Inter/Mono 字体变量)

- [ ] **Step 1: 使用 Vite 初始化 React + TS 项目**
- [ ] **Step 2: 安装 Tailwind CSS v4 及依赖**
- [ ] **Step 3: 配置字体方案：下载并自托管 Geist (标题)、Inter (正文) 和 JetBrains Mono (代码/状态)**
- [ ] **Step 4: 配置基本主题色 (Zinc-950/900) 与点睛蓝 (#3b82f6)**
- [ ] **Step 5: 验证编译通过并提交**

### Task 2: 基础布局与 URL 驱动路由

**Files:**
- Create: `frontend/src/App.tsx`
- Create: `frontend/src/components/layout/Sidebar.tsx`
- Create: `frontend/src/components/layout/MainLayout.tsx`
- Create: `frontend/src/hooks/useUrlState.ts`

- [ ] **Step 1: 安装 `react-router-dom`**
- [ ] **Step 2: 实现 `MainLayout`，包含左侧固定的 `Sidebar` 和右侧内容区**
- [ ] **Step 3: 编写 `useUrlState` Hook，用于同步 URL 搜索参数（videoId, panel, source）**
- [ ] **Step 4: 实现基础路由跳转：`/workbench`, `/chat`, `/knowledge`**
- [ ] **Step 5: 提交布局框架**

### Task 3: 鉴权模块与 API 客户端

**Files:**
- Create: `frontend/src/api/client.ts`
- Create: `frontend/src/features/auth/api.ts`
- Create: `frontend/src/features/auth/LoginPage.tsx`

- [ ] **Step 1: 初始化 Axios 客户端，支持 JWT 请求头拦截器**
- [ ] **Step 2: 实现登录/注册表单，确保输入对比度符合 WCAG AA**
- [ ] **Step 3: 实现 Token 本地持久化逻辑**
- [ ] **Step 4: 添加登录拦截路由（未登录跳转 `/login`）**
- [ ] **Step 5: 提交鉴权逻辑**

### Task 4: 视频工作台与卡片状态展示

**Files:**
- Create: `frontend/src/features/workbench/components/VideoCard.tsx`
- Create: `frontend/src/features/workbench/components/VideoGrid.tsx`
- Create: `frontend/src/features/workbench/WorkbenchPage.tsx`

- [ ] **Step 1: 安装 `@tanstack/react-query`**
- [ ] **Step 2: 对接 `GET /api/analysis/tasks` 获取视频任务列表**
- [ ] **Step 3: 实现 `VideoCard`，设计 16:9 封面及单像素蓝线进度条**
- [ ] **Step 4: 实现“同步视频”按钮点击逻辑（POST /api/bili/sync）**
- [ ] **Step 5: 提交工作台页面**

### Task 5: 异步分析抽屉 (AnalysisDrawer) 与智能轮询

**Files:**
- Create: `frontend/src/features/analysis/components/AnalysisDrawer.tsx`
- Create: `frontend/src/features/analysis/hooks/useAnalysisPolling.ts`

- [ ] **Step 1: 使用 `Shadcn UI (Sheet)` 实现右侧抽屉容器**
- [ ] **Step 2: 实现 `useAnalysisPolling` Hook，当任务状态为 PENDING/RUNNING 时自动轮询详情接口**
- [ ] **Step 3: 拆分抽屉区块：Overview (元数据), Analysis (观点流), Citations (引用)**
- [ ] **Step 4: 实现观点区块的左侧强调色线条装饰**
- [ ] **Step 5: 实现失败态诊断 UI（显示错误信息及重试按钮）**
- [ ] **Step 6: 提交抽屉功能**

### Task 6: 对话中心与可追溯引用

**Files:**
- Create: `frontend/src/features/chat/ChatPage.tsx`
- Create: `frontend/src/features/chat/components/SourceCitation.tsx`

- [ ] **Step 1: 实现极简对话界面 (ChatWindow)**
- [ ] **Step 2: 对接 `POST /api/qa/ask` 接口**
- [ ] **Step 3: 实现 `SourceCitation` 胶囊标签，点击后更新 URL 唤起 `AnalysisDrawer`**
- [ ] **Step 4: 确保从引用跳转时，抽屉能唯一定位到对应区块**
- [ ] **Step 5: 提交对话中心**

### Task 7: 知识卡片流与审美润色

**Files:**
- Create: `frontend/src/features/knowledge/KnowledgePage.tsx`
- Create: `frontend/src/features/knowledge/components/KnowledgeCard.tsx`

- [ ] **Step 1: 实现 `KnowledgeCard`，采用 `bg-zinc-900/40` 弱容器设计**
- [ ] **Step 2: 对接 `GET /api/knowledge/cards` 列表接口**
- [ ] **Step 3: 实现“沉淀卡片”交互逻辑，成功后即时刷新知识库**
- [ ] **Step 4: 全局自检：删除所有 Em-dash，检查 A11y (Esc 关闭抽屉等)**
- [ ] **Step 5: 最终提交并完成 MVP**
