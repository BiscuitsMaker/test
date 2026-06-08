-- 题目2：统计选课人数超过50人的专业课，按选课人数升序（与 part2-sql/answers.sql 一致）
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
