-- ============================================================
-- 第二题：SQL 编程
-- 基于选课记录表 enrollments 与课程表 courses
-- ============================================================

-- ------------------------------------------------------------
-- 表结构（用于本地验证，提交答案时可忽略，仅作参考）
-- ------------------------------------------------------------
-- CREATE TABLE courses (
--     course_id   VARCHAR(20)  PRIMARY KEY,        -- 课程ID
--     course_name VARCHAR(50)  NOT NULL,           -- 课程名称
--     course_type VARCHAR(20)  NOT NULL,           -- 课程类型（公共课/专业课）
--     capacity    INT          NOT NULL            -- 课程容量
-- );
--
-- CREATE TABLE enrollments (
--     student_id  VARCHAR(20)  NOT NULL,           -- 学生ID
--     course_id   VARCHAR(20)  NOT NULL,           -- 课程ID
--     enroll_time DATETIME     NOT NULL,           -- 选课时间
--     PRIMARY KEY (student_id, course_id),
--     FOREIGN KEY (course_id) REFERENCES courses(course_id)
-- );


-- ------------------------------------------------------------
-- 题目1：统计每门课程的选课人数
-- 返回：课程ID、课程名称、选课人数（别名 enroll_count）
-- 排序：按选课人数降序
-- 说明：使用 LEFT JOIN，保证没有人选的课程也能显示（选课人数为 0）。
--      若只需统计"有人选"的课程，可改为 INNER JOIN。
-- ------------------------------------------------------------
SELECT
    c.course_id,
    c.course_name,
    COUNT(e.student_id) AS enroll_count
FROM courses c
LEFT JOIN enrollments e
    ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_name
ORDER BY enroll_count DESC;


-- ------------------------------------------------------------
-- 题目2：统计选课人数超过 50 人的专业课
-- 返回：课程ID、课程名称、选课人数
-- 过滤：course_type = '专业课' 且 选课人数 > 50
-- 排序：按选课人数升序
-- 说明：人数过滤是对聚合结果的过滤，必须用 HAVING，不能用 WHERE。
-- ------------------------------------------------------------
SELECT
    c.course_id,
    c.course_name,
    COUNT(e.student_id) AS enroll_count
FROM courses c
INNER JOIN enrollments e
    ON c.course_id = e.course_id
WHERE c.course_type = '专业课'
GROUP BY c.course_id, c.course_name
HAVING COUNT(e.student_id) > 50
ORDER BY enroll_count ASC;
