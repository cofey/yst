## 1. 模型与脚本重构

- [ ] 1.1 新增 `sys_company`、`sys_user_company` 表与索引。
- [ ] 1.2 删除/停用 `sys_dept`、`sys_post`、`sys_user_post`、`sys_role_dept` 相关结构。
- [ ] 1.3 更新初始化 SQL 与启动初始化逻辑，统一 company 权限码。

## 2. 后端接口改造

- [ ] 2.1 公司管理接口替换部门接口：`/api/companies`。
- [ ] 2.2 删除岗位接口与角色数据权限接口。
- [ ] 2.3 用户接口支持 company 关系维护与按 company/role 筛选。
- [ ] 2.4 角色菜单授权改造为树形联动兼容的保存规则。

## 3. 前端界面改造

- [ ] 3.1 删除岗位页面，部门页面替换为公司页面。
- [ ] 3.2 用户页改为公司关系维护与 company 维度筛选展示。
- [ ] 3.3 菜单页改为层级列表展示维护。
- [ ] 3.4 角色页菜单授权改为树形联动勾选。

## 4. 验证与回归

- [ ] 4.1 后端通过 `mvn -DskipTests compile` 与 `mvn clean verify`。
- [ ] 4.2 前端通过 `npm run check`。
- [ ] 4.3 核验 admin 下公司、用户、角色、菜单链路可闭环操作。
