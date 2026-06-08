package com.exam.enroll;

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
     * 重写toString方法，满足输出格式要求
     */
    @Override
    public String toString() {
        return String.format("学生ID：%s，课程ID：%s，课程名称：%s", studentId, courseId, courseName);
    }
}
