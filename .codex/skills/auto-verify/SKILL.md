---
name: auto-verify
description: 代码修改后自动执行仓库规范校验，并在前端失败时自动修复一次后重跑
---

当且仅当你在本轮完成了代码修改后，必须执行校验步骤。

1. 基于改动路径选择校验范围：
- 若改动包含 `yst-server/**`，执行后端校验。
- 若改动包含 `yst-ui/**`，执行前端校验。
- 若两者都改动，两边都执行。

2. 使用固定命令（与 `AGENTS.md` 一致）：
- 后端：`tools/maven-3.6.3/bin/mvn -f yst-server/pom.xml -DskipTests verify`
- 前端首次校验：`cd yst-ui && pnpm run check`

3. 前端失败后的自动修复（只允许一次）：
- 前端首次校验失败时，执行：`cd yst-ui && pnpm run lint:fix && pnpm run format:write`
- 修复后仅重跑一次：`cd yst-ui && pnpm run check`
- 若重跑仍失败，停止自动修复，输出关键错误摘要，并询问是否继续修复。

4. 结果处理：
- 全部通过：回复“✅ 代码检查通过”，并简述执行了哪些命令。
- 自动修复后通过：回复“✅ 自动修复后代码检查通过”，并简述修复与重跑命令。

5. 安全与边界约束：
- 禁止执行会触发版本/锁文件变更的命令（如 `pnpm up`、`pnpm add`、`npm install`、`mvn versions:*`）。
- 未修改代码时，不执行校验。
- 自动修复流程最多一次，禁止循环重试。
