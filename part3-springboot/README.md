# 第三题：编程实战 —— 选课处理工具 SpringBoot 3.x 升级版

在第一题"去重 + 排序"基础上，使用 SpringBoot 3.x 完成功能升级：新增**选课分类**、**选课检索**，并提供 **CSV 批量导入 + 数据展示**页面，严格遵循 Controller → Service → 实体 分层。

---

## 一、运行方式

```bash
cd part3-springboot
mvn spring-boot:run        # 或：mvn package 后 java -jar target/enroll-system-1.0.0.jar
```

启动后浏览器访问： **http://localhost:8080/**

- 页面加载即展示后台写死的样例数据（按课程类型分类）；
- 文本框粘贴多行 CSV → "导入并处理" → 后端去重/排序/分类后回显；
- 检索框支持按 学生ID / 课程ID / 课程名称 / 课程类型 模糊检索，无匹配提示"无匹配选课记录"。

测试：`mvn test`（5 个用例覆盖去重、排序、自动分类、检索、1500 条性能 < 1s）。

---

## 二、所用 AI 编程工具

**Claude（claude-opus-4-8，经由 Claude Code CLI 调用）**

---

## 三、给 AI 的完整提示词

> 使用 SpringBoot 3.x 框架，开发一个"高校选课管理系统 - 学生选课处理工具"。要求如下：
>
> **技术与架构**：SpringBoot 3.x（JDK 17+），严格分层 Controller → Service → 实体层，业务逻辑（去重/排序/分类/检索）一律写在 Service 层，禁止写在 Controller；前端用 HTML 原生开发（HTML+CSS+JavaScript），不引入复杂前端框架。
>
> **实体**：EnrollRecord 包含 studentId（S+6位数字）、courseId（C+6位数字）、courseName、courseType（公共课/专业课/选修课）。
>
> **后端功能**：
> 1. 去重：学生ID + 课程ID 完全一致视为重复，直接移除（与课程名称无关）；
> 2. 排序：先按学生ID升序，相同时按课程ID升序；
> 3. 选课分类：按课程类型（公共课/专业课/选修课）分类存储，支持手动标注（CSV 给出类型）与自动识别（未给类型时按课程名推断）；
> 4. 选课检索：支持按 学生ID、课程ID、课程名称、课程类型 四种关键词检索，检索不到提示"无匹配选课记录"；
> 5. 性能：1000 条以上记录检索/排序响应 ≤ 1 秒，支持单次 ≥ 500 条批量导入。
>
> **页面设计**：一个简单页面，含两个核心功能 —— ① 数据批量导入：提供文本框，支持输入多条 CSV 格式选课数据（格式：学生ID,课程ID,课程名称,课程类型，每条一行），点击导入按钮提交；② 数据展示：展示导入后经后端处理的数据或后台写死的样例数据，可按课程类型分类展示。
>
> **前后端衔接**：页面提交的数据发送到 SpringBoot 后端，经去重、排序、分类处理后回显到页面；后台写死的样例数据在页面加载时直接展示。
>
> 请给出完整可运行代码：pom.xml、启动类、实体类、Service 接口与实现、Controller、前端页面、application.properties，并附单元测试。

---

## 四、AI 生成 vs 自己修改优化

> 整体代码骨架由 AI（Claude）生成，以下为在 AI 产出基础上**自己修改/优化**的部分及原因。

| 模块 | 来源 | 自己的修改与原因 |
|------|------|------------------|
| `pom.xml` | AI 生成 | 锁定 SpringBoot **3.3.5** + **JDK 21**，删去用不到的依赖，保持精简可运行 |
| `EnrollRecord` 实体 | AI 生成 | **重写 equals/hashCode 仅用 studentId+courseId**，并新增 `dedupKey()`，让去重规则与实体绑定、语义清晰（适配选课去重场景） |
| `CourseType` 枚举 | 自己补充 | AI 初版把类型当普通字符串；自己抽出枚举 + `resolve()`，实现**手动标注优先、缺失时按课程名自动识别**，兜底"选修课"（完善"分类需支持手动/自动"的要求） |
| `EnrollServiceImpl` | AI 生成 + 优化 | 把存储从 `List` 改为 **`ConcurrentHashMap`（key=学生ID\|课程ID）**：① 天然去重 O(1)；② 并发批量导入安全；③ 满足 1000+ 条性能要求（优化性能 + 适配并发） |
| CSV 解析 | 自己优化 | 兼容**换行/中英文分号**分隔、跳过空行与 `#` 注释行、字段不足判为非法并统计 `invalidCount`（完善健壮性，适配真实粘贴数据） |
| 检索 `search` | 自己优化 | 统一 `toLowerCase` 做大小写不敏感模糊匹配，无命中返回空列表由上层给"无匹配选课记录"提示（优化交互） |
| `ImportResult` / `SearchResult` DTO | 自己补充 | AI 初版直接返回 List；自己增加**导入统计（新增/去重/非法数）与检索提示信息**，让前端能清晰反馈（完善前后端衔接与交互） |
| `EnrollController` | AI 生成 | 改为**构造器注入**、确保零业务逻辑，仅转发（严格落实分层要求） |
| `index.html` | AI 生成 + 优化 | 调整为 fetch 调用 REST、页面加载即拉取样例数据、**按课程类型分组 + 彩色类型标签**展示、导入后回显统计（优化页面交互与展示效果） |
| 单元测试 | 自己补充 | 新增 5 个用例，**显式断言去重 5→4、排序顺序、自动分类、四字段检索、1500 条 < 1s** 性能（保证功能可验证） |

---

## 五、分层结构

```
src/main/java/com/exam/enroll/
├── EnrollApplication.java          启动类
├── controller/EnrollController.java   控制层（仅接参 + 转发，无业务逻辑）
├── service/EnrollService.java         业务接口
├── service/impl/EnrollServiceImpl.java 业务实现（去重/排序/分类/检索）
├── entity/EnrollRecord.java           实体
├── entity/CourseType.java             课程类型枚举（手动标注 + 自动识别）
└── dto/                               ImportRequest / ImportResult / SearchResult
src/main/resources/
├── static/index.html                  前端页面（HTML+CSS+JS）
└── application.properties
```
