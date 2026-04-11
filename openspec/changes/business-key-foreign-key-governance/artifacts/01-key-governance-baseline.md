# 键治理基线

## 1. 主表与业务主键命名

| 表名 | 主键字段 | 类型 | 说明 |
|---|---|---|---|
| sys_user | user_id | CHAR(36) | 用户业务主键 |
| sys_role | role_id | CHAR(36) | 角色业务主键 |
| sys_menu | menu_id | CHAR(36) | 菜单业务主键 |
| sys_company | company_id | CHAR(36) | 公司业务主键 |
| sys_dict_type | dict_id | CHAR(36) | 字典类型业务主键 |
| sys_dict_data | dict_code | CHAR(36) | 字典数据业务主键 |

说明：
- 主表统一使用语义化 `xxx_id` 作为主键。
- 禁止定义 `id BIGINT AUTO_INCREMENT` 主键。

## 2. 关联表与业务外键命名

| 表名 | 关联列1 | 关联列2 | 类型 | 约束建议 |
|---|---|---|---|---|
| sys_user_role | user_id | role_id | CHAR(36) | UNIQUE(user_id, role_id) |
| sys_role_menu | role_id | menu_id | CHAR(36) | UNIQUE(role_id, menu_id) |
| sys_user_company | user_id | company_id | CHAR(36) | UNIQUE(user_id, company_id) |

说明：
- 关联列必须与被引用主表主键同名同类型。
- 关联列字符集与排序规则与主表字段保持一致。

## 3. 数据库评审规则

1. 主表必须使用 `xxx_id CHAR(36)` 主键，禁止 `id` 自增主键。
2. 外键只能引用业务主键，禁止任何外键引用内部数值键。
3. 业务主键字符集与排序规则统一：`CHARACTER SET ascii COLLATE ascii_bin`。
4. 业务主键不可更新。
5. 审计字段统一为 `create_time`、`update_time`。
6. 不允许新增 `deleted`、`del_flag` 等软删除字段。
