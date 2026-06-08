package com.exam.enroll.entity;

import java.util.Map;

/**
 * 课程类型枚举：公共课 / 专业课 / 选修课。
 *
 * <p>支持两种分类方式：
 * <ul>
 *   <li>手动标注：CSV 第 4 列直接给出类型文字（如"专业课"）。</li>
 *   <li>自动识别：未提供类型时，根据课程名称关键字推断，兜底为"选修课"。</li>
 * </ul>
 */
public enum CourseType {

    PUBLIC("公共课"),
    MAJOR("专业课"),
    ELECTIVE("选修课");

    private final String label;

    CourseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** 课程名称关键字 -> 课程类型，用于"自动识别"。 */
    private static final Map<String, CourseType> NAME_KEYWORD_RULES = Map.of(
            "英语", PUBLIC,
            "思想", PUBLIC,
            "政治", PUBLIC,
            "体育", PUBLIC,
            "数学", PUBLIC
    );

    /**
     * 归一化用户手动输入的类型文字。
     *
     * @param raw 原始文字，可为 null/空
     * @return 匹配到的标准 label；无法识别时返回 null（交由自动识别处理）
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String t = raw.trim();
        if (t.isEmpty()) {
            return null;
        }
        for (CourseType type : values()) {
            if (type.label.equals(t)) {
                return type.label;
            }
        }
        return null;
    }

    /**
     * 根据课程名称自动识别课程类型，兜底为"选修课"。
     *
     * @param courseName 课程名称
     * @return 标准类型 label
     */
    public static String autoDetect(String courseName) {
        if (courseName != null) {
            for (Map.Entry<String, CourseType> rule : NAME_KEYWORD_RULES.entrySet()) {
                if (courseName.contains(rule.getKey())) {
                    return rule.getValue().label;
                }
            }
        }
        return ELECTIVE.label;
    }

    /**
     * 综合判定：优先用手动标注，识别不出再自动识别。
     *
     * @param rawType    CSV 中给出的类型文字（可能为空）
     * @param courseName 课程名称
     * @return 标准类型 label
     */
    public static String resolve(String rawType, String courseName) {
        String manual = normalize(rawType);
        return manual != null ? manual : autoDetect(courseName);
    }
}
