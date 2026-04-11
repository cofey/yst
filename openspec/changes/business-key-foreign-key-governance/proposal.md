## Why

当前系统在实体标识上存在多种并存方式（`id`、`user_id`、`role_id`、复合键等），容易导致：

- 接口字段命名不统一，前后端与跨系统集成成本上升。
- 关联关系不稳定，部分链路混用技术主键与业务键，难以保持一致语义。
- 删除治理与唯一约束策略不一致，数据规则难以固化。

同时项目需要兼容老 `.NET` 系统（GUID 生态），因此需要明确一套统一可执行的键治理规范。

## What Changes

- 主表统一采用“业务键主键模型”：`xxx_id`（`CHAR(36)`）作为主键。
- 所有关联约束统一使用业务键 `xxx_id`，禁止出现 `id` 自增主键与其外键关系。
- 全库统一审计字段为 `create_time`、`update_time`，移除软删除字段（`deleted` / `del_flag`）。
- 统一 API 契约：对外仅使用业务键。

## Capabilities

### New Capabilities
- `entity-key-governance`: 统一实体业务主键命名、类型与不可变规范。
- `business-key-foreign-relation`: 统一外键仅关联业务键的规则。

### Modified Capabilities
- `identity-org-model`: 组织与身份相关实体需遵循统一键命名与关联规则。

## Impact

- 数据库：核心主表标准化为 `xxx_id` 主键，关联表外键类型统一为 `CHAR(36)`。
- 后端：Mapper/Service 统一按 `xxx_id` 查询与关联，接口参数/返回统一业务键。
- 前端：实体标识字段统一为字符串业务键，页面操作与路由参数统一按 `xxx_id`。
- 集成：与 .NET GUID 系统可直接按字符串业务键对接，降低映射复杂度。
