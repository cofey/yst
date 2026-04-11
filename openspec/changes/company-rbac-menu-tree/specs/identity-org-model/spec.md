## ADDED Requirements

### Requirement: 系统组织模型必须以 company 为唯一组织维度
The system MUST model organization with companies (non-hierarchical) and remove department/post dependencies from user management flows.

#### Scenario: 创建用户并维护公司关系
- **WHEN** 管理员创建或编辑用户并配置公司集合
- **THEN** 系统应在用户-公司关系表持久化关联数据

### Requirement: 用户公司关系必须支持多对多维护
The system MUST support many-to-many relationships between users and companies via a dedicated relation table.

#### Scenario: 调整用户可管理公司
- **WHEN** 管理员修改用户关联公司
- **THEN** 系统应替换旧关联并保存新关联，且查询结果即时生效

## REMOVED Requirements

### Requirement: 部门必须支持树形结构管理
The system no longer requires hierarchical department management in this change.
