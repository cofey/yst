# 后端契约改造规范

## 1. DTO/VO 暴露规则

1. 对外请求参数、响应字段仅允许使用业务键（`userId`、`roleId`、`menuId`、`companyId`）。
2. 禁止在 Controller 层 DTO/VO 中出现内部数值主键字段（如 `id`）。
3. OpenAPI/Knife4j 示例统一使用 UUID 字符串。

## 2. Service 查询与关联规则

1. Service 入参统一采用业务键字符串。
2. Repository/Mapper 查询条件统一基于业务键列。
3. 关联保存时统一写入业务外键（例如 `sys_user_role.user_id`）。
4. 业务主键不可更新；变更请求中不提供主键更新能力。

## 3. 鉴权与登录态切换规范

1. JWT claim 中用户标识使用 `user_id`。
2. 权限缓存（登录用户、权限集合）索引键统一为 `user_id`。
3. 审计日志中的操作者标识统一使用 `user_id`。
4. 权限相关接口（例如获取当前用户权限）统一按 `user_id` 查询。
