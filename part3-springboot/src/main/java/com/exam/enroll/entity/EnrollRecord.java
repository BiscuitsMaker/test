package com.exam.enroll.entity;

import java.util.Objects;

/**
 * 选课记录实体类（升级版）。
 *
 * <p>相比第一题新增 {@code courseType} 字段，用于"选课分类"功能
 * （公共课 / 专业课 / 选修课）。
 *
 * <p>去重判定依据仍为：学生ID + 课程ID 完全一致即视为重复
 * （与课程名称、课程类型无关）。
 */
public class EnrollRecord {

    /** 学生ID，格式：S+6位数字 */
    private String studentId;

    /** 课程ID，格式：C+6位数字 */
    private String courseId;

    /** 课程名称 */
    private String courseName;

    /** 课程类型：公共课 / 专业课 / 选修课 */
    private String courseType;

    public EnrollRecord() {
    }

    /** 兼容第一题的三参构造器（类型默认为选修课）。 */
    public EnrollRecord(String studentId, String courseId, String courseName) {
        this(studentId, courseId, courseName, CourseType.ELECTIVE.getLabel());
    }

    /** 全参构造器 */
    public EnrollRecord(String studentId, String courseId, String courseName, String courseType) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseType = courseType;
    }

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

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    /** 去重去标识：学生ID|课程ID。 */
    public String dedupKey() {
        return studentId + "|" + courseId;
    }

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

    @Override
    public String toString() {
        return String.format("学生ID：%s，课程ID：%s，课程名称：%s，课程类型：%s",
                studentId, courseId, courseName, courseType);
    }
}
