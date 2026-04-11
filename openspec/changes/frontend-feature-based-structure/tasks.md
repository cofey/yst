## 1. 目录基线与规范

- [x] 1.1 建立 `modules/system/*` 与 `router/modules` 基础目录。
- [x] 1.2 定义页面/API/类型拆分规范并统一为 `modules/*/pages/index.vue + api.ts + types.ts`。
- [x] 1.3 落实“禁止新增到 `api/system.ts`、`types/index.ts`”约束并移除旧聚合文件。

## 2. 路由模块化改造

- [x] 2.1 新增 `router/modules/system.ts` 并迁移 system 路由定义。
- [x] 2.2 主路由改为聚合注册，保持原有鉴权守卫逻辑。
- [x] 2.3 校验历史路径兼容与默认重定向行为。

## 3. system 域模块迁移

- [x] 3.1 user 模块迁移：页面迁移并完成 `api.ts` 与 `types.ts` 内聚。
- [x] 3.2 role 模块迁移：列表与授权流程迁移并完成模块内聚。
- [x] 3.3 menu 模块迁移：树形维护页面迁移并完成模块内聚。
- [x] 3.4 company 模块迁移：页面迁移并完成接口与类型内聚。
- [x] 3.5 dict 模块迁移：类型管理/数据管理页面迁移并统一目录风格。

## 4. 清理与回归

- [x] 4.1 移除旧 `views/*View.vue` 实现与废弃引用。
- [x] 4.2 清理 `api/system.ts`、`types/index.ts` 中已迁移内容。
- [x] 4.3 执行前端检查与手工回归，确认核心流程无行为回归。
- [x] 4.4 输出迁移结果说明（模块完成度、遗留项、后续约束）。
