## ADDED Requirements

### Requirement: 系统管理页面路由必须使用 `/system/*` 前缀
The system MUST use `/system/*` for system management page routes.

#### Scenario: 用户管理路由
- **WHEN** 用户访问用户管理页面
- **THEN** 路由应为 `/system/users`

#### Scenario: 字典数据路由
- **WHEN** 用户访问字典数据页面
- **THEN** 路由应为 `/system/dicts/:dictType/data`

### Requirement: 顶部菜单导航必须与新路由保持一致
The system MUST ensure top menu navigation and active state matching use `/system/*` routes.

#### Scenario: 菜单点击跳转
- **WHEN** 用户点击系统管理下任一菜单项
- **THEN** 页面应跳转到对应 `/system/*` 路由
- **AND** 菜单高亮应与当前路由一致

### Requirement: 初始化菜单路径必须与前端路由一致
The system MUST initialize `sys_menu.path` with `/system/*` route values.

#### Scenario: 初始化菜单数据
- **WHEN** 执行 `init.sql` 初始化菜单数据
- **THEN** `sys_menu.path` 的系统页面路径应为 `/system/*`

### Requirement: 旧一级路径不提供兼容重定向
The system MUST NOT provide compatibility redirects for legacy first-level routes.

#### Scenario: 访问旧路径
- **WHEN** 用户访问 `/users` 等旧一级路径
- **THEN** 系统不应自动重定向到新路径
