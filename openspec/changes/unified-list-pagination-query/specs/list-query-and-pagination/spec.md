## ADDED Requirements

### Requirement: 系统管理列表必须遵循统一页面布局
The system MUST render user, role, dict type, and dict data pages with a unified list layout and action placement.

#### Scenario: 统一按钮区域
- **WHEN** 用户进入任一系统管理列表页
- **THEN** 新增/导入/导出按钮应显示在表格左上角
- **AND** 查询/重置/展开收起按钮应显示在右侧操作区

### Requirement: 系统管理列表必须支持分页查询
The system MUST provide backend-driven pagination for user, role, dict type, and dict data lists.

#### Scenario: 分页查询
- **WHEN** 用户提交查询条件并请求第 N 页
- **THEN** 后端应返回对应页记录与总数
- **AND** 前端应按分页器展示当前页数据

### Requirement: 列表查询必须显式触发
The system MUST not auto-query on page mount and MUST query only when the user explicitly clicks query.

#### Scenario: 页面初始态
- **WHEN** 页面首次打开
- **THEN** 列表不应自动发起查询请求

#### Scenario: 点击查询
- **WHEN** 用户点击“查询”按钮
- **THEN** 系统应基于当前筛选条件请求列表数据

#### Scenario: 点击重置
- **WHEN** 用户点击“重置”按钮
- **THEN** 系统应恢复默认筛选状态且不自动查询
