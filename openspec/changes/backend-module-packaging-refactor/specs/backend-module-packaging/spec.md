## ADDED Requirements

### Requirement: 后端必须按业务模块分包并在模块内分层

系统管理域后端代码 MUST 使用模块化包结构，并在每个模块内维护 controller/service/entity/vo/mapper 分层。

#### Scenario: 用户模块包结构
- **WHEN** 实现用户管理相关控制器、服务、实体、VO、Mapper
- **THEN** 其包路径应位于 `com.shunbo.yst.modules.system.user.*`
- **AND** 不应放回全局 `controller/entity/service/vo/mapper` 包

#### Scenario: 角色、字典、菜单、单位模块包结构
- **WHEN** 实现 role/dict/menu/company 相关代码
- **THEN** 包路径应位于 `com.shunbo.yst.modules.system.<module>.*`

### Requirement: 后端命名空间必须统一为 com.shunbo.yst

#### Scenario: 根包统一
- **WHEN** 定义后端类
- **THEN** 顶级包 MUST 以 `com.shunbo.yst` 开头
