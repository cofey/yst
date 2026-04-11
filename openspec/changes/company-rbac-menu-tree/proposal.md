## Why

当前系统实现与新目标存在结构性偏差：
- 组织模型仍以部门(`dept`)为中心，且包含层级语义。
- 岗位(`post`)已接入用户关系与管理页面，但当前需求明确移除。
- 角色数据权限(`data_scope`)链路已引入，但当前需求要求暂时去掉。

为避免继续叠加技术债，需要以 `company` 为核心重构组织模型，并收敛权限体系为“用户 + 角色 + 菜单/按钮权限”。

## What Changes

- 将部门域替换为单位域（`company`），且 company 不支持层级。
- 删除岗位相关模型、接口、页面与权限码。
- 删除角色数据权限相关模型与接口。
- 新增用户可管理单位关系表（多对多）。
- 菜单管理改为层级列表展示/维护。
- 角色授权改为菜单树形层级选择，父子联动勾选。

## Capabilities

### New Capabilities
- `company-user-management`: 以 company 为组织维度并支持用户管理 company 关系。
- `menu-tree-authorization`: 菜单层级维护与角色层级授权联动。

### Modified Capabilities
- `identity-org-model`: 从 dept/post 模型迁移为 company-only 模型。
- `rbac-menu-button-authz`: 增强为树形菜单授权交互与层级一致性规则。

## Impact

- 数据库：新增 `sys_company`、`sys_user_company`；移除/停用 `sys_dept`、`sys_post`、`sys_user_post`、`sys_role_dept`。
- 后端：用户、角色、菜单、初始化数据与权限码全链路 company 化；去除 data scope 逻辑。
- 前端：删除岗位/部门页面，新增公司页面；用户页筛选与关系维护改为公司；角色页改为菜单树授权。
