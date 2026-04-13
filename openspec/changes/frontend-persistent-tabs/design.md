## Context

`yst-ui` 当前使用 Vue 3 + vue-router + Pinia + Element Plus，主布局在 `App.vue` 中直接渲染 `router-view`。  
当前缺少标签导航状态管理、标签持久化与页面缓存白名单机制。

## Goals / Non-Goals

**Goals:**
- 在现有技术栈内提供多标签导航与刷新恢复能力。
- 支持标签切换、关闭、关闭其他、固定标签保护等基础交互。
- 对动态路由按参数拆分标签实例，避免互相覆盖。
- 通过路由 `meta` 控制页面是否进入标签栏及是否参与缓存。

**Non-Goals:**
- 不新增 `yst-ui/package.json` 之外的第三方依赖。
- 不实现页面内部业务状态（筛选条件、表单草稿）跨刷新恢复。
- 不在本变更中执行前端代码实现，仅定义可执行规范。

## Decisions

### 1) 状态管理方案
- 使用 Pinia 新增 `tabs` store，维护：
  - `visitedTabs`：已打开标签列表。
  - `activeTabKey`：当前激活标签键。
  - `cachedViewKeys`：参与 `KeepAlive` 的视图键集合。
- store 负责统一提供：打开、激活、关闭、关闭其他、恢复、清空等动作。

### 2) 持久化方案
- 使用 `localStorage` 存储 tabs 相关状态，与现有 `auth` store 持久化风格保持一致。
- 初始化阶段执行恢复逻辑；登出时清理 tabs 数据，避免跨会话污染。

### 3) 标签键与动态路由规则
- `tabKey` 由“路由名 + 参数签名”构成（例如：`dict-data::dictType=gender`）。
- 同一路由不同参数生成不同标签与缓存键，满足“按参数拆分多个标签”要求。

### 4) 渲染与缓存策略
- 布局层使用 `router-view` 插槽拿到组件实例。
- `KeepAlive` 通过 `:include="cachedViewKeys"` 受控缓存。
- 路由 `meta.cache=true` 才进入缓存白名单；`meta.cache=false` 切换后不保留实例。

### 5) 路由元信息约定
- 统一约定 `meta` 字段：
  - `title`: 标签显示名。
  - `affix`: 是否固定标签（不可关闭）。
  - `cache`: 是否参与缓存。
  - `tab`: 是否进入标签栏。
- 若目标路由缺失或无权限，恢复时回退到 `/home`。

### 6) 标签数量控制
- 默认最大标签数为 20。
- 超限时淘汰最旧的非固定标签，保证交互稳定性与内存可控。

## Risks / Trade-offs

- 风险：`KeepAlive include` 依赖组件命名一致性，配置不当会导致缓存失效。
  - 对策：实现阶段统一检查路由 name 与组件 name 对应关系。
- 风险：动态路由参数过多可能导致标签增长过快。
  - 对策：设置最大标签数并优先保留固定标签。

