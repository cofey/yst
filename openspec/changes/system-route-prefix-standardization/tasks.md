## 1. 前端路由改造

- [x] 1.1 将 `router/modules/system.ts` 的系统页面路径统一改为 `/system/*`。
- [x] 1.2 将顶部菜单跳转路径与菜单激活基准改为 `/system/*`。
- [x] 1.3 将页面内硬编码跳转路径改为 `/system/*`（如字典数据页返回入口）。

## 2. 初始化数据同步

- [x] 2.1 将 `init.sql` 中 `sys_menu.path` 的系统页面路径改为 `/system/*`。

## 3. 验证

- [ ] 3.1 验证 `/system/users`、`/system/roles`、`/system/menus`、`/system/companies`、`/system/dicts` 可访问。
- [ ] 3.2 验证 `/system/dicts/:dictType/data` 可访问且返回类型跳转正确。
- [x] 3.3 验证顶部菜单高亮匹配正常（静态逻辑校验）。
- [ ] 3.4 验证旧一级路径（如 `/users`）不再可用。
