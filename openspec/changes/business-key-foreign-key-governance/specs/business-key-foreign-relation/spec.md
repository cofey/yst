## ADDED Requirements

### Requirement: 外键关联必须引用业务主键
The system MUST define foreign key relationships using business primary keys (`<entity>_id`).

#### Scenario: 建立用户与角色关联
- **WHEN** 系统定义 `sys_user_role` 外键约束
- **THEN** `sys_user_role.user_id` 必须引用 `sys_user.user_id`
- **AND** `sys_user_role.role_id` 必须引用 `sys_role.role_id`

### Requirement: 业务键外键类型必须全链路一致
The system MUST enforce consistent datatype and collation for all business-key foreign key columns.

#### Scenario: 新增菜单关联表
- **WHEN** 系统新增与 `menu_id` 相关的关联字段
- **THEN** 字段类型必须为 `CHAR(36)`
- **AND** 字符集与排序规则必须与被引用主表字段一致

### Requirement: 业务主键一经创建不得更新
The system MUST treat business primary keys as immutable after creation to preserve referential integrity.

#### Scenario: 修改用户资料
- **WHEN** 管理员更新用户昵称、邮箱等字段
- **THEN** `user_id` 必须保持不变
