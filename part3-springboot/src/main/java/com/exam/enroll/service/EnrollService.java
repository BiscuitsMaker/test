package com.exam.enroll.service;

import com.exam.enroll.dto.ImportResult;
import com.exam.enroll.entity.EnrollRecord;

import java.util.List;
import java.util.Map;

/**
 * 选课记录业务接口。
 *
 * <p>所有业务逻辑（解析、去重、排序、分类、检索）均在 Service 层实现，
 * Controller 仅做参数接收与结果转发。
 */
public interface EnrollService {

    /**
     * 批量导入 CSV 文本：解析 -> 去重 -> 分类 -> 入库 -> 排序。
     *
     * @param csvText 多行 CSV 文本，每行格式：学生ID,课程ID,课程名称[,课程类型]
     * @return 导入结果（统计信息 + 全量排序后记录 + 按类型分组）
     */
    ImportResult importCsv(String csvText);

    /**
     * 获取去重 + 排序后的全量记录。
     */
    List<EnrollRecord> findAll();

    /**
     * 按课程类型分组（公共课 / 专业课 / 选修课）。
     */
    Map<String, List<EnrollRecord>> groupByType();

    /**
     * 关键词检索：在 学生ID、课程ID、课程名称、课程类型 四个字段中模糊匹配。
     *
     * @param keyword 检索关键词
     * @return 命中记录（已排序）；无命中返回空列表
     */
    List<EnrollRecord> search(String keyword);
}
