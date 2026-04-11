## Why

当前系统管理页面路由使用一级路径（如 `/users`、`/roles`），与系统模块化分组语义不一致，不利于后续扩展和统一治理。

需要将系统页面路由统一规范为 `/system/*` 前缀，提升路径可读性与维护一致性。

## What Changes

- 前端系统管理页面路由统一改为 `/system/*`：
  - `/system/users`
  - `/system/roles`
  - `/system/menus`
  - `/system/companies`
  - `/system/dicts`
  - `/system/dicts/:dictType/data`
- 顶部菜单跳转与激活匹配逻辑同步更新到新路径。
- 页面内硬编码跳转路径同步更新到新路径。
- 初始化 SQL 的 `sys_menu.path` 同步改为 `/system/*`。
- 不提供旧路由（如 `/users`）兼容重定向。

## Capabilities

### New Capabilities
- `system-route-prefixing`: 系统管理页面统一使用 `/system/*` 路由前缀。

## Impact

- 前端：路由注册、菜单导航、页面内跳转路径更新。
- 后端初始化数据：`yst-server/src/main/resources/sql/init.sql` 的菜单路径更新。
- 历史一级路径（`/users`、`/roles` 等）不再可用。
