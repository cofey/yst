## MODIFIED Requirements

### Requirement: 身份与组织实体必须使用统一业务主键对外标识
The system MUST expose identity and organization entities through semantic business primary keys.

#### Scenario: 查询用户与公司关系
- **WHEN** 客户端请求用户详情与所属公司信息
- **THEN** 返回数据中的身份字段必须使用 `user_id`、`company_id`
- **AND** 不得在公共契约中暴露内部数值型主键

### Requirement: 身份与组织关联约束必须基于业务主键
The system MUST model identity/organization relations with business-key foreign constraints.

#### Scenario: 维护用户公司关系
- **WHEN** 系统保存 `sys_user_company` 关系
- **THEN** 关联列必须为 `user_id` 与 `company_id`
- **AND** 外键必须分别引用 `sys_user.user_id` 与 `sys_company.company_id`
