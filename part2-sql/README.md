# 第二题：SQL 编程

基于 `enrollments`（选课记录表）与 `courses`（课程表）编写两道统计 SQL。完整语句见 [`answers.sql`](./answers.sql)。

## 题目1：统计每门课程的选课人数
返回 课程ID、课程名称、选课人数（别名 `enroll_count`），按选课人数**降序**。

```sql
SELECT c.course_id, c.course_name, COUNT(e.student_id) AS enroll_count
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_name
ORDER BY enroll_count DESC;
```
> 用 `LEFT JOIN` 让无人选的课程也显示（人数为 0）；只统计有人选的课可改 `INNER JOIN`。

## 题目2：统计选课人数超过 50 人的专业课
返回 课程ID、课程名称、选课人数，按选课人数**升序**。

```sql
SELECT c.course_id, c.course_name, COUNT(e.student_id) AS enroll_count
FROM courses c
INNER JOIN enrollments e ON c.course_id = e.course_id
WHERE c.course_type = '专业课'
GROUP BY c.course_id, c.course_name
HAVING COUNT(e.student_id) > 50
ORDER BY enroll_count ASC;
```
> 课程类型用 `WHERE` 行级过滤；人数 > 50 是对聚合结果过滤，必须用 `HAVING`。
