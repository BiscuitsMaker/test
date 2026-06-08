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
> 使用过程如下：
> 首先发送考试要求及其题目给Claude code让其按照要求生成方案
>
> 方案大概如下---claude生成：
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

我认为测试比较重要因此主要通过测试，来检查功能是否准确完善、界面是否需要优化：
  提示词：Title.txt是题目信息，题目框架代码等已经初步生成，都是还需要再次进行测试。1、首先，你需要再次对照题目信息，检查当前项目功能是否和题目一致，不能有不一致的地方，也不要自己添加其他功能。2、其次你需要对照题目信息生成完整的测试方案，测试方案需要按照步骤进行，并且设置监测节点，每一步测试完成都要显示节点测试情况，并且生成日志。前端界面部分同样需要进行检测，你需要结合截图工具，查看界面是否工整、排版合理等。3、检测完成将整个项目包括日志代码等所有东西上传至github。


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
