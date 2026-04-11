## Context

既有变更已将系统推进至“用户+角色+菜单+部门+岗位+数据权限”模型，但当前产品方向已调整：
- 组织主维度改为 company（无层级）。
- 岗位与数据权限暂不纳入当前阶段。
- 权限体系聚焦菜单/按钮 RBAC，强调菜单树授权体验。

## Goals / Non-Goals

**Goals:**
- 以 company 替换 dept，全链路统一命名与权限码。
- 移除 post 与 data_scope 相关实现。
- 建立 `sys_user_company` 关系并支持用户维护。
- 实现菜单层级列表维护与角色菜单树授权。

**Non-Goals:**
- 不做历史数据迁移，仅重建/初始化新模型数据。
- 不引入 company 层级与跨租户隔离。
- 不恢复角色数据权限能力。

## Decisions

### 1. 组织模型
- `sys_company`：company 主数据（平铺，无 parent_id）。
- `sys_user_company`：用户-公司管理关系（多对多）。
- `sys_user` 不再承载 `dept_id`。

### 2. 权限模型
- 保留 `sys_user_role`、`sys_role_menu`。
- 移除 `sys_role_dept` 与 `data_scope` 逻辑。
- 权限码统一使用 `system:company:*` 替代 `system:dept:*` 与 `system:post:*`。

### 3. 菜单与授权交互
- 菜单继续使用 `parent_id` 表达树结构。
- 菜单管理页使用树形列表展示与维护。
- 角色授权使用树控件，勾选规则：父子联动（勾父带子、勾子补父）。

### 4. 实施策略
- 采用“新模型直接生效”方式，不做旧表历史搬迁。
- 初始化数据直接按 company 模型落库，确保 admin 开箱可用。

## Risks / Trade-offs

- 风险：去掉 data scope 后查询可见性仅由功能权限控制。
  - 对策：在文档与接口层明确这是阶段性收敛，不混入残留策略。
- 风险：菜单树授权联动可能引发历史角色菜单集变化。
  - 对策：保存前做父节点补齐，保证数据一致性可预测。
