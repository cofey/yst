## Context

当前 `yst-ui` 已有登录、用户、角色、菜单、单位、字典等页面，主要问题是页面/接口/类型按技术层扁平堆叠，导致：

- 单页面逻辑过重（例如用户/字典/角色页体量明显偏大）。
- 模块改动会触发跨文件级连调整。
- 新增子页面缺乏统一落点和约束。

## Goals / Non-Goals

**Goals**
- 建立按业务域组织的统一目录规范。
- 完成 system 域页面、API、类型、状态逻辑拆分。
- 保持业务行为与接口契约不变（重构不改业务语义）。
- 形成可复用模块模板，支撑后续新增页面。

**Non-Goals**
- 不引入新的业务能力。
- 不调整后端接口协议。
- 不做全量 UI 重设计，仅做结构性重组。

## Architecture

```text
src
├─ modules
│  ├─ auth
│  │  ├─ api.ts
│  │  ├─ types.ts
│  │  └─ login/pages/index.vue
│  └─ system
│     ├─ user
│     │  ├─ pages/index.vue
│     │  ├─ api.ts
│     │  ├─ types.ts
│     │  └─ ...
│     ├─ role/...
│     ├─ menu/...
│     ├─ company/...
│     └─ dict/...
└─ router
   ├─ index.ts
   ├─ modules/auth.ts
   └─ modules/system.ts
```

## Key Decisions

### 1) 页面拆分策略
- `index.vue`：仅列表查询、表格展示、页面级动作入口。
- 复杂度较高的新增/编辑/详情应拆为独立页面；轻量操作可保留弹窗。

### 2) 模块内聚策略
- 每个业务域独立 `api.ts` 与 `types.ts`，禁止继续向全局 `system.ts`、`types/index.ts` 堆叠。
- 页面状态和行为提取至 `useXxxPage.ts`，降低 SFC 复杂度。

### 3) 路由组织策略
- 按模块分文件注册路由，主路由仅聚合和守卫。
- 路由命名遵循 `system-user-index / system-user-create / ...` 规则。

### 4) 渐进迁移策略
- 采用“分模块迁移 + 可运行校验”方式，不做一次性大爆炸迁移。
- 建议顺序：`user -> role -> menu -> company -> dict`（按复杂度和依赖面）。

## Risks / Trade-offs

- 风险：迁移期路径变更导致页面跳转/懒加载 import 失效。
  - 对策：每迁移一个模块即跑前端校验并做路由回归。
- 风险：短期内存在新旧目录并存。
  - 对策：设置迁移清单与截止条件，模块迁移完成后移除旧文件。
- 风险：团队成员继续按旧模式新增文件。
  - 对策：补充目录规范文档和 PR 检查项。

## Acceptance Criteria

- system 域页面完成目录迁移，旧 `views/*View.vue` 不再承载核心业务实现。
- API/类型不再新增到全局聚合文件，新增代码落在模块目录。
- 路由按模块拆分并可正常导航。
- 用户可完成核心流程（列表、查询、新增、编辑、删除、详情）且行为与迁移前一致。
