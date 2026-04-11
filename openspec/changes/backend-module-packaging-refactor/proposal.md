## Why

后端目前按全局分层组织（controller/entity/service/vo/mapper），随着系统模块增多，跨目录改动成本高、边界不清晰；同时分页查询参数定义重复，前端排序协议缺少统一兼容。

需要一次性完成：
- Java 根包统一到 `com.shunbo.yst`；
- 后端按业务模块分包，模块内分层；
- 抽取分页查询公共对象；
- 兼容前端排序字段双协议，并统一默认 `create_time` 倒序。
- 菜单由固定写死改为按当前用户权限树全量渲染。

## What Changes

- 根包：`com.yst` 迁移到 `com.shunbo.yst`。
- 模块结构：system 域改为 `modules.system.<module>.(controller/service/entity/vo/mapper)`。
- 新增分页查询基类：统一 `pageNum/pageSize` 与排序参数。
- 分页列表接口统一支持：
  - 新协议：`sortField/sortOrder`
  - 兼容协议：`orderByColumn/isAsc`
- 排序安全：各列表启用字段白名单，非法字段回退 `create_time desc`。
- 新增“当前用户菜单树”能力，用于前端顶部菜单全量动态渲染。
- 顶栏菜单改为全量动态渲染，菜单项以权限树为准（不做前端缺失路由过滤）。
- 路由无权限访问策略为“仅提示不强制拦截”。

## Capabilities

### New Capabilities
- `backend-module-packaging`: 后端模块化包结构与边界约束。

### Modified Capabilities
- `list-query-and-pagination`: 增加分页查询公共对象与排序协议兼容要求。
- `identity-org-model`: 用户域后端包结构模块化。
- `rbac-menu-button-authz`: 角色与菜单域后端包结构模块化。
- `ruoyi-dict-management`: 字典域后端包结构模块化与排序兼容。
- `menu-permission-tree-render`: 按用户权限树返回并渲染导航菜单。

## Impact

- 后端：import 与 package 大范围变更，但 API 路径/响应结构保持不变。
- 前端：列表查询参数类型补充分页排序字段，默认传 `create_time desc`。
- 前后端：新增当前用户菜单树接口与顶栏动态渲染链路。
- 验证：需执行后端编译、分页排序回归、菜单权限树渲染回归。
