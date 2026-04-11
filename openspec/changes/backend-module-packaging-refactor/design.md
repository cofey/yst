## Context

本变更同时覆盖命名空间迁移、模块化分包、分页排序协议统一，以及菜单按权限树动态渲染。核心目标是降低后续演进成本，且不破坏线上 API 契约。

## Decisions

### 1. 根包迁移
- 全量 Java 包名由 `com.yst` 迁移为 `com.shunbo.yst`。
- Spring Boot 扫描根包保持入口类所在根路径，`@MapperScan` 调整为模块 mapper 根路径扫描。

### 2. 模块内分层
- system 域按模块组织：`user/role/company/dict/menu`。
- 模块目录语义：`controller/service/service.impl/entity/vo/mapper`。
- 公共能力保留：`common/config/security`。

### 3. 分页公共对象
- 抽取 `PageQuery`，统一字段：`pageNum/pageSize`。
- 增加排序兼容字段：`sortField/sortOrder`、`orderByColumn/isAsc`。
- 提供统一归一化方法：有效分页值、排序字段与方向解析。

### 4. 排序兼容与默认规则
- 列表服务统一读取归一化排序参数。
- 每个列表使用白名单映射字段进行排序。
- 未传或非法字段时，统一回退 `create_time desc`（对应实体 `createTime`）。

### 5. 前端兼容
- 分页查询类型新增排序参数定义。
- 列表 API 默认附带 `sortField=create_time`、`sortOrder=desc`，保持行为一致。

### 6. 菜单权限树渲染
- 后端新增当前用户菜单树接口（推荐 `/api/auth/menus`），菜单来源于用户角色关联菜单。
- 菜单筛选规则：`status=1`、`visible=1`、`menuType in (M,C)`；按 `parentId/sort/menuId` 组装稳定树。
- 前端顶部菜单取消写死，改为基于权限树全量动态渲染。
- 菜单树存在前端未注册路由时，按产品决策“照单渲染”，不预过滤。
- 路由无权限策略：仅提示，不强制跳转（保留当前登录态拦截）。

## Risks / Trade-offs

- 风险：大规模包迁移可能引入遗漏 import。
  - 对策：批量替换后执行完整编译验证并修复。
- 风险：排序字段不在白名单导致与预期不一致。
  - 对策：统一默认回退，避免 SQL 风险与运行时异常。
- 风险：菜单树与前端路由不一致会出现跳转异常或 404。
  - 对策：先接受“照单渲染”策略，回归中识别并收敛菜单配置。
