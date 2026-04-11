## ADDED Requirements

### Requirement: 系统必须支持菜单与按钮级权限点建模
The system MUST model directory/menu/button resources and use permission identifiers (`perms`) for operation-level authorization checks.

#### Scenario: 配置按钮权限点
- **WHEN** 管理员在菜单管理中创建按钮类型资源并配置 `perms`
- **THEN** 该权限点应可被角色授权并用于接口鉴权与前端按钮显隐

### Requirement: 接口访问必须进行权限表达式校验
The system MUST enforce authorization expressions (`hasPermi`, `hasRole`) at API endpoints and reject unauthorized access.

#### Scenario: 无权限访问接口
- **WHEN** 用户调用其未被授权的受保护接口
- **THEN** 系统应返回 403 拒绝访问

#### Scenario: 有权限访问接口
- **WHEN** 用户拥有接口要求的权限标识
- **THEN** 系统应允许访问并返回业务响应

### Requirement: 前端必须支持动态菜单与按钮显隐
The frontend MUST build route menus from current user grants and apply button-level permission visibility controls.

#### Scenario: 登录后构建菜单
- **WHEN** 用户登录成功并请求路由树
- **THEN** 前端应仅渲染其有权限访问的目录与菜单

#### Scenario: 页面按钮显隐
- **WHEN** 页面声明按钮权限要求
- **THEN** 前端应隐藏无权限按钮且后端仍执行最终鉴权
