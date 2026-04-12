# AGENTS.md

必须用中文回答和询问。

## 执行摘要（先看这个）

- 边界优先：未获本轮明确授权，不进入 `SmartST2020/`、`tools/`、`yst-ui/node_modules/`、`.idea/`、`yst-server/target/` 等目录。
- 结构优先：后端按域分层，前端按模块落点，禁止回流到旧聚合文件。
- 一致性优先：权限点统一建模并前后端一致执行；字典统一来源，禁止硬编码。
- 质量优先：后端通过 SpotBugs/Checkstyle；核心层必须中文 Javadoc，component 额外要求真实示例。
- 冻结优先：未获本轮授权，禁止任何技术栈与依赖版本变更。

## 技术栈

这是一个前后端分离的管理系统项目。

### 后端技术栈

- 语言与运行时：Java (OpenJDK 17)
- 框架：Spring Boot 3.5.9、Spring Security
- 数据访问：MyBatis-Plus 3.5.15、Hikari
- 数据与中间件：MySQL 8.0.24、polarDB、Redis 7.0.15、RocketMQ 4.9.6
- 接口文档：SpringDoc 2.8.14、Knife4j 4.5.0
- 日志与工具：logback 1.5.x、Lombok 1.18.42、EasyExcel 3.3.3
- 构建工具：Maven 3.6.x
- 目录：`yst-server/`

### 前端技术栈

- 核心框架：Vue 3 + TypeScript + ES6
- 构建工具：Vite 5.4.15
- UI 与样式：Element Plus 2.11.7、UnoCSS
- 状态与路由生态：Pinia 2.1.7、Qiankun 2.10.16
- 图表与表格：ECharts、vxe-table 4.13.31
- 包管理与运行时：pnpm 9、Node.js v20.15.0
- 目录：`yst-ui/`

## 后端约束

### 1) 边界与结构

- 新增功能必须落在：`com.shunbo.yst.modules.<domain>`（如后续细分子模块，可扩展为 `com.shunbo.yst.modules.<domain>.<module>`）。
- 必须按分层组织：`controller/service/service.impl/mapper/entity/vo`。
- 禁止将新增代码放入全局混合包或无业务归属目录。
- 完全禁止 `service -> service` 直接调用；公共业务处理必须抽取到 component。
- component 统一放在：`yst-server/src/main/java/com/shunbo/yst/component`。
- 禁止循环依赖（含跨模块循环依赖）；服务复用应下沉到 mapper/领域组件等非 service 层。

### 2) 数据与权限

- 主表主键必须为语义化 `xxx_id CHAR(36)`，禁止 `id` 自增主键。
- 业务键字符集/排序规则统一：`CHARACTER SET ascii COLLATE ascii_bin`。
- 外键必须使用物理外键约束，且与被引用列类型/字符集/排序规则一致。
- 业务主键创建后不可更新。
- 菜单/目录/按钮必须按权限点建模，禁止仅前端控制不做后端鉴权。
- 接口必须声明并执行权限表达式校验（如 `hasPermi`、`hasRole`）。
- 权限点命名统一：`<domain>:<module>:<action>`。

### 3) 代码质量与文档

- `List/Set/Map` 字段必须做防御性拷贝：getter 返回副本、setter 存副本、字段默认初始化为空集合。
- 含集合字段的 VO/DTO 禁止直接使用 `@Data`，必须使用 `@Getter + @Setter` 并手写集合字段 getter/setter。
- Spring 托管 Bean（controller/service/filter）构造注入的 `EI_EXPOSE_REP2` 误报，统一使用：
  - `@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring manages bean lifecycle for injected dependencies")`
- 包装类型默认值处理必须使用 `Objects.requireNonNullElse`。
- 字符串大小写转换必须显式指定 `Locale.ROOT`。
- 禁止吞异常：禁止 `catch (Exception ignored) {}` 或空 `catch`，必须记录日志并携带关键业务上下文，且优先收窄为具体异常类型。
- `TypeReference` 禁止在方法体内匿名重复创建，必须抽取为静态常量复用。
- 禁止死存储/死变量（赋值后不再使用）。
- 日志常量命名必须为 `LOGGER`（`private static final Logger LOGGER = ...`）。
- 核心层中文注释强约束（`controller/service/component/entity/vo`）：
  - 类：必须使用中文类级 Javadoc，说明“这是一个做什么的类”（职责与边界）。
  - 方法：`public` 方法必须使用中文 Javadoc，说明“这是一个做什么事的方法”（输入、输出、关键行为/副作用）。
  - 字段：`entity`、`vo` 的字段必须使用中文注释，说明“这个字段表示什么业务含义”（必要时补充取值语义/单位/状态）。
  - 字段注释豁免：若字段已使用 `@ExcelProperty` 或 `@Schema` 且注解文案已完整表达中文业务含义，可不再重复写字段注释。
- `yst-server/src/main/java/com/shunbo/yst/component/**` 下每个 component 额外要求：
  - 必须满足上述“核心层中文注释强约束”；
  - 必须提供至少 1 个真实调用示例（`<pre>{@code ...}</pre>`），禁止伪代码占位。

### 4) 交付校验

- 后端提交前必须执行并通过：
  - `tools/maven-3.6.3/bin/mvn -f yst-server/pom.xml -DskipTests verify`

## 前端约束

### 1) 边界与结构

- 新增功能必须落在：`yst-ui/src/modules/<domain>/<module>`。
- 模块标准结构：`pages/index.vue + api.ts + types.ts`。
- 禁止新增代码回流到旧聚合文件（如全局 `api/system.ts`、`types/index.ts`）。

### 2) 路由、样式与权限一致性

- 系统管理路由统一使用 `/system/*` 前缀。
- 路由按模块文件维护（`router/modules/*`），主路由仅做聚合。
- 菜单路径、前端路由、权限菜单树必须一致。
- `yst-ui/src/modules/**/pages/*.vue` 禁止写 `<style>` 块，页面样式必须外置或复用组件内部样式。
- 前端按钮显隐必须走统一权限机制（如 `v-hasPermi`），禁止页面内散落字符串权限判断。
- 权限点命名统一：`<domain>:<module>:<action>`。

### 3) 数据字典

- 业务枚举展示值必须来自统一字典 API/封装，禁止页面硬编码。
- 禁止在页面、组件、`api.ts` 中写死字典标签映射。
- 字典管理变更后必须触发缓存失效或刷新。

### 4) 交付校验

- 前端提交前必须执行并通过：
  - `cd yst-ui && pnpm run check`

## 技术栈冻结约束（硬约束）

- 未经用户在当前轮明确授权，禁止升级前后端任何版本。
- 后端禁止升级：JDK、Spring Boot、Spring Security、MyBatis-Plus、MySQL、Redis、RocketMQ、Maven 及相关依赖版本。
- 前端禁止升级：Node.js、pnpm、Vue、Vite、Element Plus、Pinia、Qiankun、vxe-table 及相关依赖版本。
- 禁止因“顺手优化”修改 `pom.xml`、`package.json`、`pnpm-lock.yaml` 中的版本号。
- 禁止执行任何会触发版本变更的依赖安装/升级命令（如 `pnpm add`、`pnpm up`、`npm install <pkg>@<version>`、`mvn versions:*`）；确需执行时必须先获用户在当前轮明确授权。

## 最小自检清单

- 是否越界访问了 `SmartST2020/`、`tools/`、`yst-ui/node_modules/`、`.idea/`、`yst-server/target/` 等目录。
- 是否满足后端/前端新增代码落点与结构约束。
- 是否新增了权限点并保持前后端一致。
- 是否出现字典硬编码。
- 核心层类/`public` 方法/`entity`、`vo` 字段是否补齐中文注释。
- 是否触发技术栈/锁文件版本变更。
- 后端是否完成编译与 SpotBugs 检查。
