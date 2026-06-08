package com.exam.enroll;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 选课记录处理工具：去重、排序、输出。
 *
 * <p>三大核心功能：
 * <ol>
 *   <li>去重：学生ID + 课程ID 完全一致视为重复记录，直接移除（与课程名称无关）。</li>
 *   <li>排序：先按学生ID升序，学生ID相同时按课程ID升序。</li>
 *   <li>输出：返回处理后的列表，同时逐行打印格式化信息。</li>
 * </ol>
 */
public class EnrollProcessor {

    /**
     * 去重 + 排序，返回处理后的新列表（不修改入参）。
     *
     * @param input 原始选课记录列表，允许为 null
     * @return 去重并排序后的列表
     */
    public List<EnrollRecord> process(List<EnrollRecord> input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 去重：依赖 EnrollRecord 重写的 equals/hashCode（studentId + courseId）。
        //    使用 LinkedHashSet 保留首次出现顺序（保留首条重复记录的课程名称）。
        List<EnrollRecord> distinct = new ArrayList<>(new LinkedHashSet<>(input));

        // 2. 排序：先按学生ID升序，再按课程ID升序。
        distinct.sort(
                Comparator.comparing(EnrollRecord::getStudentId)
                        .thenComparing(EnrollRecord::getCourseId));

        return distinct;
    }

    /**
     * 去重 + 排序，并逐行打印格式化信息，最后返回处理后的列表。
     *
     * @param input 原始选课记录列表
     * @return 去重并排序后的列表
     */
    public List<EnrollRecord> processAndPrint(List<EnrollRecord> input) {
        List<EnrollRecord> result = process(input);
        for (EnrollRecord record : result) {
            System.out.println(record);
        }
        return result;
    }
}
