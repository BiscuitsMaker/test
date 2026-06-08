package com.exam.enroll;

import java.util.Objects;

/**
 * 选课记录实体类
 */
public class EnrollRecord {
    /**
     * 学生ID，格式：S+6位数字
     */
    private String studentId;

    /**
     * 课程ID，格式：C+6位数字
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 全参构造器
     */
    public EnrollRecord(String studentId, String courseId, String courseName) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    // getter 和 setter 方法
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * 去重判定依据：学生ID + 课程ID 完全一致即视为重复（与课程名称无关）。
     * 重写 equals/hashCode 使其可直接用于基于 Set 的去重。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnrollRecord that = (EnrollRecord) o;
        return Objects.equals(studentId, that.studentId)
                && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }

    /**
     * 重写toString方法，满足输出格式要求
     */
    @Override
    public String toString() {
        return String.format("学生ID：%s，课程ID：%s，课程名称：%s", studentId, courseId, courseName);
    }
}
