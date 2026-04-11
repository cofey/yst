## MODIFIED Requirements

### Requirement: 系统必须支持菜单与按钮级权限点建模
The system MUST keep menu/button permission modeling and additionally support hierarchical menu maintenance and tree-based role authorization.

#### Scenario: 菜单层级列表维护
- **WHEN** 管理员查看和维护菜单
- **THEN** 系统应以层级列表方式展示节点关系并支持增删改

#### Scenario: 角色菜单树授权
- **WHEN** 管理员在角色授权中勾选菜单树节点
- **THEN** 系统应按父子联动规则保存授权结果并用于接口鉴权
