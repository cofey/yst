## Why

当前前端目录采用技术层扁平组织（`views/*View.vue`、`api/system.ts`、`types/index.ts`），随着功能增加出现以下问题：

- 页面文件过大，列表、弹窗、表单、接口调用、状态逻辑耦合在同一文件，维护成本持续上升。
- `system.ts` 与 `types/index.ts` 成为聚合大文件，模块边界不清晰，改动影响面难评估。
- 路由与页面命名缺乏业务域层级，不利于新增“新增/详情/编辑”页面演进。

需要将前端结构升级为按业务域（feature/module）组织，建立稳定可扩展的目录与依赖边界。

## What Changes

- 页面目录从扁平 `views/*` 调整为按业务域分层（如 `modules/system/user/pages`、`modules/auth/login/pages`）。
- `index.vue` 仅承载列表页编排，新增/编辑/详情拆分为独立页面文件。
- API、类型、页面状态逻辑按模块内聚：`api.ts`、`types.ts`、`useXxxPage.ts`。
- 路由按模块拆分（`router/modules/*.ts`），主路由仅做聚合。
- 制定并落地前端目录约束，防止回退到“大文件/大聚合”模式。

## Capabilities

### New Capabilities
- `frontend-module-boundary`: 前端按业务域组织，模块职责清晰。
- `page-separation-pattern`: 列表、创建、编辑、详情页面独立化。
- `router-modularization`: 路由模块化注册。

### Modified Capabilities
- `frontend-system-management`: system 域各页面从单文件模式迁移为模块化模式。
- `frontend-api-and-types-organization`: API/类型从全局聚合迁移为按模块内聚。

## Impact

- 前端目录结构变化较大，涉及路由 import 路径调整与模块化聚合注册。
- 业务功能不新增，仅做结构重组；需保证行为与接口调用语义不变。
- 后续新增功能可按模块模板扩展，减少重复耦合与跨域污染。
