## Why

当前系统缺少统一的数据字典能力，枚举值分散在前后端代码中，导致配置不可运营、字段展示不一致、变更成本高。需要按若依的通用实现补齐“字典类型 + 字典数据 + 前后端统一取值”机制，支撑后续模块复用。

## What Changes

- 新增若依风格数据字典主模型：`sys_dict_type`、`sys_dict_data`，并提供完整 CRUD 能力。
- 新增字典查询能力：按 `dictType` 获取启用字典项列表，供前端表单/表格渲染使用。
- 新增字典缓存与失效策略：字典变更后刷新/清理缓存，保证读性能与一致性。
- 新增前端字典管理页（字典类型、字典数据）和统一字典加载方式（按类型拉取并复用）。
- 新增菜单与权限点（`system:dict:*`），保持与现有若依式权限模型一致。

## Capabilities

### New Capabilities
- `ruoyi-dict-management`: 提供字典类型与字典数据的后端管理、权限控制、缓存策略及前端管理界面。
- `ruoyi-dict-consumption`: 提供按 `dictType` 查询可用字典项的统一接口与前端消费机制，保障页面展示与表单选项一致。

### Modified Capabilities
- 无

## Impact

- 后端数据库：新增 `sys_dict_type`、`sys_dict_data` 表及索引、初始化数据。
- 后端模块：新增字典实体/Mapper/Service/Controller、缓存组件与权限点。
- 前端模块：新增字典 API、类型定义、字典管理页面与公共字典加载工具。
- 权限与菜单：`init.sql` / `DataInitRunner` 增加“字典管理”菜单及 `system:dict:list|add|edit|remove` 按钮权限。
