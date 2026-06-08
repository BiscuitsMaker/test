# 第一题：学生选课基础处理工具（Java）

实现选课记录的**去重、排序、输出**三大核心功能。

## 功能规则
- **去重**：学生ID + 课程ID 完全一致视为重复记录，直接移除（与课程名称无关）。
- **排序**：先按学生ID升序，学生ID相同时按课程ID升序。
- **输出**：返回处理后的列表，同时逐行打印 `学生ID：XXX，课程ID：XXX，课程名称：XXX`。

## 实现要点
- `EnrollRecord`：重写 `equals/hashCode`，仅以 `studentId + courseId` 作为相等判定，使其可直接用于基于 `Set` 的去重。
- `EnrollProcessor`：用 `LinkedHashSet` 去重（保留首次出现的记录），再用 `Comparator.comparing(...).thenComparing(...)` 排序。
- `Main`：使用题目给定样例数据演示。

## 运行
```bash
cd part1-basic-tool
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -Dfile.encoding=UTF-8 -cp out com.exam.enroll.Main
```

## 运行结果（样例数据 5 条 → 去重排序后 4 条）
```
===== 处理后（去重 + 排序）=====
学生ID：S000001，课程ID：C000001，课程名称：Java程序设计
学生ID：S000001，课程ID：C000002，课程名称：数据库原理
学生ID：S000002，课程ID：C000003，课程名称：计算机网络
学生ID：S000003，课程ID：C000001，课程名称：Java程序设计

处理后共 4 条记录。
```
