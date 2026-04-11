## ADDED Requirements

### Requirement: 系统必须提供按字典类型查询可用字典项的统一接口
The system MUST provide an API to fetch enabled dictionary items by `dictType` for frontend/business consumption.

#### Scenario: 按类型查询字典项
- **WHEN** 前端或业务模块传入有效 `dictType` 发起查询
- **THEN** 系统必须返回该类型下启用状态字典项，且按 `dictSort` 升序输出

#### Scenario: 查询不存在的字典类型
- **WHEN** 调用方查询不存在或被禁用的 `dictType`
- **THEN** 系统必须返回空集合或约定错误码，并保持行为一致

### Requirement: 前端必须使用统一字典消费机制渲染选项与标签
The frontend MUST use a shared dictionary loading mechanism to render form options and value labels, avoiding scattered hardcoded enums.

#### Scenario: 表单下拉选项渲染
- **WHEN** 页面声明需要某 `dictType` 的选项数据
- **THEN** 前端必须通过统一字典 API/封装拉取并渲染选项

#### Scenario: 列表值标签渲染
- **WHEN** 表格中存在字典值字段（如状态值）
- **THEN** 前端必须使用同一字典来源将值转换为展示标签

### Requirement: 字典管理页面必须遵循若依交互模式
The frontend MUST provide dictionary type and dictionary data management pages with Ruoyi-style interaction patterns and permission-based buttons.

#### Scenario: 字典类型与字典数据联动管理
- **WHEN** 管理员在字典类型页面选中某一类型
- **THEN** 系统必须展示对应字典数据列表并支持新增、编辑、删除操作

#### Scenario: 无权限按钮隐藏
- **WHEN** 当前用户缺少 `system:dict:add|edit|remove` 权限
- **THEN** 前端必须隐藏对应操作按钮且后端仍执行最终鉴权
