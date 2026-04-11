## MODIFIED Requirements

### Requirement: 系统管理列表必须支持分页查询

分页查询请求 MUST 复用公共分页查询对象，并兼容两套排序参数协议。

#### Scenario: 排序参数双协议兼容
- **WHEN** 客户端传 `sortField/sortOrder`
- **THEN** 服务端按该排序执行（命中白名单时）
- **AND** 客户端传 `orderByColumn/isAsc` 时也应被兼容解析

#### Scenario: 默认排序与非法字段回退
- **WHEN** 未传排序参数或传入非法字段
- **THEN** 服务端 MUST 使用 `create_time desc` 默认排序
- **AND** 不得直接拼接未校验字段进入 SQL
