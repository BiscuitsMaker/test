# 高校选课管理系统 · 考试作答

围绕"高校选课管理系统 - 学生选课处理"的四道题完整作答，每部分均可独立运行 / 验证。

| 题目 | 目录 | 内容 | 状态 |
|------|------|------|------|
| 一、基础处理工具（Java） | [`part1-basic-tool/`](./part1-basic-tool) | 选课记录去重、排序、格式化输出 | ✅ 可运行 |
| 二、SQL 编程 | [`part2-sql/`](./part2-sql) | 选课人数统计、专业课筛选两道 SQL | ✅ |
| 三、编程实战（SpringBoot 3.x） | [`part3-springboot/`](./part3-springboot) | 去重/排序 + 分类 + 检索 + CSV 批量导入页面，分层架构 | ✅ 可运行 |
| 四、分析及设计 | [`part4-design/`](./part4-design) | 数据模型 + ER图、并发风险、索引设计 | ✅ |

## 环境
- JDK 21（第一题 `javac`，第三题 SpringBoot 3.3.5）
- Maven 3.8+（第三题构建）

## 快速开始
```bash
# 第一题
cd part1-basic-tool && javac -encoding UTF-8 -d out $(find src -name "*.java") \
  && java -Dfile.encoding=UTF-8 -cp out com.exam.enroll.Main

# 第三题（启动后访问 http://localhost:8080/）
cd part3-springboot && mvn spring-boot:run
```

## 测试
完整测试方案与报告见 [`tests/`](./tests)：
```bash
bash tests/run_tests.sh      # 步骤1-6 自动化，输出每个监测节点 PASS/FAIL，日志写入 tests/logs/
```
- 测试方案：[tests/TEST_PLAN.md](./tests/TEST_PLAN.md)
- 测试报告：[tests/TEST_REPORT.md](./tests/TEST_REPORT.md)（**28 个节点全部通过**）
- 运行日志：`tests/logs/`；界面截图：`screenshots/`

## 说明
- 第三题按要求注明了所用 **AI 编程工具（Claude）**、**完整提示词**，以及 **AI 生成 / 自己修改优化** 的逐项标注，详见 [part3-springboot/README.md](./part3-springboot/README.md)。
- 各题去重规则统一：**学生ID + 课程ID 相同即为重复**（与课程名称无关）；排序统一：**学生ID 升序，相同再按课程ID 升序**。
