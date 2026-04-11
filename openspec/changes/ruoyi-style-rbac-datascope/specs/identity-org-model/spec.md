## ADDED Requirements

### Requirement: 系统必须维护完整的组织与身份主数据
The system MUST provide master data models for users, departments, posts, and roles, and maintain required associations for authorization and organizational ownership.

#### Scenario: 创建用户并绑定组织与授权实体
- **WHEN** 管理员创建用户并提交部门、岗位、角色信息
- **THEN** 系统应完成用户写入并建立用户-岗位、用户-角色关联
- **AND** 用户必须归属一个有效部门

#### Scenario: 用户禁用后不可登录
- **WHEN** 用户状态被设置为禁用
- **THEN** 该用户后续登录应被拒绝并返回明确提示

### Requirement: 部门必须支持树形结构管理
The system MUST support hierarchical department management and maintain ancestor-chain data required for "department and descendants" scope resolution.

#### Scenario: 新增子部门
- **WHEN** 管理员在已有部门下新增子部门
- **THEN** 系统应正确维护父子关系与祖先链字段

#### Scenario: 查询部门树
- **WHEN** 前端请求部门树用于表单选择
- **THEN** 系统应按层级返回可渲染的树结构数据
