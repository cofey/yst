## Context

本次设计目标是统一实体标识与跨表关联规则，并收敛为“业务键主键 + 业务键外键”模型。该策略优先保证：

- 对外标识稳定且可读（兼容 GUID 生态）
- 跨系统集成字段语义一致
- 模型表达简单，不再存在 `id` 与 `xxx_id` 双通道歧义

已确认约束：
- 主表不使用 `id BIGINT AUTO_INCREMENT`
- 业务键统一 `CHAR(36)`（如 `user_id`、`role_id`、`menu_id`）
- 外键只能引用业务键
- 不使用软删除

## Goals / Non-Goals

**Goals:**
- 为主表、关联表、API 契约定义统一命名与类型规范。
- 明确业务键不可变策略与外键约束策略。
- 在不实现业务功能变更的前提下，给出现网可迁移路径。

**Non-Goals:**
- 不在本变更中定义具体业务模块逻辑调整。
- 不包含代码实现与数据库实际执行脚本。
- 不引入多租户隔离策略。

## Decisions

### 1. 业务键主键模型（主表）

每个主表统一：
- `<entity>_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL`
- `PRIMARY KEY (<entity>_id)`
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
- `update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`

命名示例：
- `sys_user.user_id`
- `sys_role.role_id`
- `sys_menu.menu_id`
- `sys_company.company_id`

### 2. 关联规则（外键）

所有外键必须引用业务键：
- `sys_user_role.user_id -> sys_user.user_id`
- `sys_user_role.role_id -> sys_role.role_id`
- `sys_role_menu.role_id -> sys_role.role_id`
- `sys_role_menu.menu_id -> sys_menu.menu_id`

字段类型必须一致：`CHAR(36)`，并统一字符集排序规则。

### 3. 字符集与格式

- 业务键字段统一 `CHAR(36) CHARACTER SET ascii COLLATE ascii_bin`
- 值格式统一为标准 UUID 字符串（小写，带连字符）
- 业务键一经创建不可更新

### 4. API 契约边界

- 对外接口（URL、请求体、响应）仅使用业务键
- 不再暴露或依赖内部数值型主键

### 5. 删除策略

- 采用物理删除
- 外键默认 `RESTRICT`，避免误删主数据
- 如确需级联删除，必须在具体模块规格中单独声明

## Migration Strategy

### Phase 1: Schema 标准化
- 主表主键统一切换为 `xxx_id`（`CHAR(36)`）
- 关联表外键列统一为业务键并建立外键约束
- 执行数据一致性巡检并修复孤儿关系

### Phase 2: Contract 切换
- 后端接口改为业务键输入/输出
- 前端实体主标识切换到业务键
- 下线所有数值型主键契约

### Phase 3: Governance 收敛
- CI 增加规则检查（DTO/VO 不允许出现内部 `id`）
- SQL 评审新增规则（主键、外键必须为业务键）

## Risks / Trade-offs

- 风险：`CHAR(36)` 主键与外键索引体积大于 `BIGINT`
  - 对策：控制索引数量，关键查询做覆盖索引评估
- 风险：历史数据中业务键可能存在空值或重复
  - 对策：迁移前执行完整性巡检并阻断脏数据发布
- 风险：主键类型调整会增加一次性改造范围
  - 对策：分阶段推进，先结构后契约
