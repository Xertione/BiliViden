# Bilibili AI Agent Frontend MVP 设计方案

## 1. 项目概述
BiliAgent 的前端工作台，采用抽屉式沉浸流设计，旨在为用户提供高效的视频资产管理、AI 分析阅读及知识问答体验。

**设计读数 (Design Read):** 
面向重度知识用户的个人工具，采用 Linear 风格的极简技术语言。
- **技术栈**: Vite + React 18 + TS + Tailwind CSS v4 + Shadcn UI + TanStack Query + Framer Motion.
- **审美拨盘**: VARIANCE: 6 / MOTION: 4 / DENSITY: 3.

## 2. 架构设计

### 2.1 路由与状态 (Routing & URL State)
使用 `react-router-dom` 管理页面，抽屉状态完全受 URL 参数驱动，以支持持久化分享与可追溯跳转。

- **/workbench**: 视频资产网格。
- **/chat**: 对话式问答中心。
- **/knowledge**: 知识卡片流。
- **/profile**: 个人画像与偏好。

**URL 协议**: 
`?video={id}&panel={analysis|card}&source={chat|workbench|knowledge}`

### 2.2 状态管理与轮询 (Polling Strategy)
- **TanStack Query**:
    - `['videos']`: 获取视频列表。
    - `['analysis', videoId]`: 获取单条视频分析详情。
- **智能轮询**: 
    - 仅当 `status` 为 `PENDING` 或 `RUNNING` 时，开启 3s 间隔的 `refetchInterval`。
    - 抽屉关闭后停止轮询，成功后全局失效 `['videos']` 缓存。

## 3. UI/UX 规范

### 3.1 视觉风格 (Visual Identity)
- **色调**: 全站锁定 Dark Mode。底色 `Zinc-950`，面板色 `Zinc-900`。
- **点睛色**: `Electric Blue` (#3b82f6)，饱和度控制在 80% 以下。
- **字体定义**:
    - **标题**: `Geist` (Display/Sans)。
    - **正文**: `Inter` (Sans)。
    - **元数据/状态**: `JetBrains Mono` (Mono)。
- **禁止项**: 严禁使用 Em-dash (`—` / `–`)，严禁 AI 紫色渐变，严禁 Serif 字体。

### 3.2 核心组件
- **VideoCard**: 16:9 封面，12px 圆角。底部置入单像素蓝色进度条 + `Analyzing...` 文案表示运行态。
- **AnalysisDrawer**: 
    - **Overview**: 视频元数据（标题、UP、时间）。
    - **Analysis**: AI 生成的摘要与观点，观点区块带左侧蓝色强调线。
    - **Citations**: 来源依据追溯。
- **KnowledgeCard**: 使用 `bg-zinc-900/40` 的弱容器设计，保持呼吸感的同时具备清晰边界。

### 3.3 交互动效 (Motion)
- 使用 `motion/react` (Framer Motion)。
- **抽屉**: `type: "spring", stiffness: 300, damping: 30` 的侧滑载入。
- **列表**: Stagger 错落进入效果。
- **降级**: 必须封装在 `useReducedMotion` 钩子中，检测到用户偏好时直接禁用动画。

## 4. 可访问性与异常设计 (A11y & Error)
- **A11y**:
    - 全局 `focus-visible` 高亮。
    - 抽屉开启时启用 `Focus Trap`。
    - 支持 `Esc` 键关闭抽屉。
- **失败态**:
    - 展示具体的 `errorMessage`。
    - 提供 `Retry` 按钮重新触发任务。
- **空态**: 
    - 工作台：展示“同步视频”引导。
    - 知识库：提示如何从分析结果沉淀卡片。

## 5. 开发约束
- 页面必须在桌面端保持导航栏单行显示。
- 英雄区（Hero）高度必须在首屏内完成闭环。
- 所有图片必须有明确的比例预留（CLS 优化）。
