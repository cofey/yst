## ADDED Requirements

### Requirement: 角色必须支持标准数据权限范围配置
The system MUST support role-level data scopes: all, custom departments, own department, own department with descendants, and self only.

#### Scenario: 配置角色数据范围
- **WHEN** 管理员为角色设置数据权限范围
- **THEN** 系统应持久化范围值并在查询链路生效

### Requirement: 查询接口必须自动注入数据权限条件
The system MUST automatically inject data-scope predicates into controlled queries to avoid duplicated manual SQL constraints.

#### Scenario: 本部门数据范围查询
- **WHEN** 用户所属角色数据范围为“本部门”
- **THEN** 查询结果应仅包含该部门数据

#### Scenario: 本部门及以下数据范围查询
- **WHEN** 用户所属角色数据范围为“本部门及以下”
- **THEN** 查询结果应包含本部门与全部子部门数据

#### Scenario: 仅本人数据范围查询
- **WHEN** 用户所属角色数据范围为“仅本人”
- **THEN** 查询结果应仅包含该用户创建或归属的数据

### Requirement: 多角色场景应按最宽可见范围合并
The system MUST define and apply a deterministic multi-role merge strategy for final data visibility scope.

#### Scenario: 多角色合并
- **WHEN** 用户同时拥有“本部门”与“自定义部门”角色
- **THEN** 系统应按既定合并规则计算最终可见范围并稳定输出结果
