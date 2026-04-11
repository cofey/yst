## ADDED Requirements

### Requirement: 系统主表必须使用业务键作为主键
The system MUST define `<entity>_id` (`CHAR(36)`) as the primary key for every primary domain table.

#### Scenario: 新增系统用户
- **WHEN** 系统创建一条 `sys_user` 记录
- **THEN** 记录必须包含 `user_id` 主键
- **AND** 不得额外定义自增 `id` 主键

### Requirement: 业务键命名必须按实体语义统一
The system MUST use semantic business key names (e.g., `user_id`, `role_id`, `menu_id`, `company_id`) instead of generic names.

#### Scenario: 新增角色主表
- **WHEN** 系统新增 `sys_role` 主表结构
- **THEN** 主键字段名称必须为 `role_id`
- **AND** 禁止使用 `biz_id`、`uuid` 等泛化命名替代

### Requirement: 审计字段必须统一且不含软删除字段
The system MUST keep only `create_time` and `update_time` as audit fields and MUST NOT introduce soft-delete fields.

#### Scenario: 设计任意主表DDL
- **WHEN** 团队定义主表审计字段
- **THEN** 必须包含 `create_time` 与 `update_time`
- **AND** 不得包含 `deleted` 或 `del_flag`
