## ADDED Requirements

### Requirement: 系统必须提供若依风格字典类型管理能力
The system MUST provide dictionary type management based on `sys_dict_type`, including create, list, update, and delete with permission control.

#### Scenario: 新增字典类型
- **WHEN** 管理员提交新的 `dictType`、`dictName`、状态与备注
- **THEN** 系统必须创建字典类型并保证 `dictType` 全局唯一

#### Scenario: 删除字典类型
- **WHEN** 管理员删除某个字典类型
- **THEN** 系统必须按既定规则处理其关联字典数据（禁止删除或级联删除，需一致实现）

### Requirement: 系统必须提供若依风格字典数据管理能力
The system MUST provide dictionary data management based on `sys_dict_data`, including create, list, update, delete, sorting, and status filtering.

#### Scenario: 新增字典数据项
- **WHEN** 管理员在某 `dictType` 下新增字典项并设置 `dictLabel`、`dictValue`、`dictSort`、状态
- **THEN** 系统必须成功落库并在该类型下按排序可查询

#### Scenario: 查询字典数据列表
- **WHEN** 管理员按 `dictType` 查询字典项
- **THEN** 系统必须返回该类型下全部字典项并支持按状态、标签关键字筛选

### Requirement: 字典管理接口必须纳入权限控制
The system MUST protect dictionary management endpoints with Ruoyi-style permissions and deny unauthorized access.

#### Scenario: 无权限用户访问字典管理接口
- **WHEN** 用户访问需要 `system:dict:list|add|edit|remove` 的接口且未具备对应权限
- **THEN** 系统必须返回 403

#### Scenario: 有权限用户执行字典管理操作
- **WHEN** 用户具备对应字典管理权限
- **THEN** 系统必须允许访问并返回业务响应

### Requirement: 字典变更必须触发缓存失效
The system MUST clear or refresh dictionary cache entries when dictionary type or dictionary data changes.

#### Scenario: 修改字典数据后再次查询
- **WHEN** 管理员修改某 `dictType` 下的字典项
- **THEN** 后续按该 `dictType` 查询必须读取到最新结果而非旧缓存
