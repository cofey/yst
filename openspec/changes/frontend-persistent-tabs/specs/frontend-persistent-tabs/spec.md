## ADDED Requirements

### Requirement: 系统必须提供可管理的前端多标签导航
The frontend MUST provide a manageable tab navigation model for opened pages, including open, activate, and close behaviors.

#### Scenario: 打开页面自动加入标签栏
- **WHEN** 用户通过菜单或路由访问一个应进入标签栏的页面
- **THEN** 系统必须在标签栏新增或激活对应标签，并保持标签顺序稳定

#### Scenario: 关闭当前标签自动切换
- **WHEN** 用户关闭当前激活标签
- **THEN** 系统必须自动切换到相邻可用标签或回退到默认页

### Requirement: 系统必须在刷新后恢复标签与激活页面
The frontend MUST restore visited tabs and active tab after browser refresh within the same login session scope.

#### Scenario: 刷新恢复标签状态
- **WHEN** 用户刷新浏览器页面
- **THEN** 系统必须从本地持久化中恢复 `visitedTabs` 与 `activeTabKey`

#### Scenario: 恢复目标无效回退
- **WHEN** 持久化中的激活路由不可达（不存在或当前用户无权限）
- **THEN** 系统必须回退到 `/home` 并保持可用标签状态

### Requirement: 动态路由必须按参数拆分标签实例
The frontend MUST generate distinct tab instances for the same route when route params differ.

#### Scenario: 不同字典类型生成独立标签
- **WHEN** 用户分别访问 `/dicts/A/data` 与 `/dicts/B/data`
- **THEN** 系统必须生成两个不同标签实例并可独立切换

#### Scenario: 关闭一个动态标签不影响其他参数标签
- **WHEN** 用户关闭 `/dicts/A/data` 对应标签
- **THEN** `/dicts/B/data` 标签与其状态必须保持不变

### Requirement: 页面缓存必须受路由元信息控制
The frontend MUST control view caching through route metadata and KeepAlive include strategy.

#### Scenario: cache 打开参与缓存
- **WHEN** 路由配置 `meta.cache=true`
- **THEN** 页面实例必须进入缓存白名单并在标签切换后保留实例

#### Scenario: cache 关闭不保留实例
- **WHEN** 路由配置 `meta.cache=false`
- **THEN** 页面切换离开后不应保留组件实例

### Requirement: 登出必须清理标签持久化数据
The frontend MUST clear persisted tab states when user logs out to avoid cross-session leakage.

#### Scenario: 退出登录清理状态
- **WHEN** 用户执行退出登录
- **THEN** 系统必须清除标签相关本地持久化数据

#### Scenario: 重新登录不恢复旧会话标签
- **WHEN** 用户在退出后重新登录
- **THEN** 系统不应自动恢复上一次登录会话的标签列表

