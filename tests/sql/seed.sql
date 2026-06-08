-- 测试种子数据：用于真实执行验证第二题两道 SQL（SQLite 方言，语法与标准 SQL 一致）
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;

CREATE TABLE courses (
    course_id   VARCHAR(20) PRIMARY KEY,
    course_name VARCHAR(50) NOT NULL,
    course_type VARCHAR(20) NOT NULL,   -- 公共课 / 专业课
    capacity    INT NOT NULL
);

CREATE TABLE enrollments (
    student_id  VARCHAR(20) NOT NULL,
    course_id   VARCHAR(20) NOT NULL,
    enroll_time DATETIME    NOT NULL,
    PRIMARY KEY (student_id, course_id)
);

INSERT INTO courses VALUES
    ('C000001', 'Java程序设计', '专业课', 100),
    ('C000002', '数据库原理',   '专业课', 100),
    ('C000003', '计算机网络',   '公共课', 200),
    ('C000004', '大学英语',     '公共课', 200),
    ('C000005', '数据结构',     '专业课', 100),  -- 0 人选，用于验证 Q1 的 LEFT JOIN
    ('C000006', '编译原理',     '专业课', 100);

-- 按课程注入不同人数的选课记录（学生ID 用序号生成，保证组合主键唯一）
WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n+1 FROM seq WHERE n < 80)
INSERT INTO enrollments SELECT printf('S%06d', n), 'C000003', '2026-01-01 10:00:00' FROM seq; -- 公共课 80
WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n+1 FROM seq WHERE n < 70)
INSERT INTO enrollments SELECT printf('S%06d', n), 'C000006', '2026-01-01 10:00:00' FROM seq; -- 专业课 70
WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n+1 FROM seq WHERE n < 60)
INSERT INTO enrollments SELECT printf('S%06d', n), 'C000001', '2026-01-01 10:00:00' FROM seq; -- 专业课 60
WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n+1 FROM seq WHERE n < 55)
INSERT INTO enrollments SELECT printf('S%06d', n), 'C000004', '2026-01-01 10:00:00' FROM seq; -- 公共课 55
WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n+1 FROM seq WHERE n < 30)
INSERT INTO enrollments SELECT printf('S%06d', n), 'C000002', '2026-01-01 10:00:00' FROM seq; -- 专业课 30
-- C000005 数据结构：0 人选
