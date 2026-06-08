package com.exam.enroll.dto;

import com.exam.enroll.entity.EnrollRecord;

import java.util.List;
import java.util.Map;

/**
 * CSV 批量导入结果，返回给前端用于展示与提示。
 */
public class ImportResult {

    /** 本次提交解析出的有效行数（去重前） */
    private int parsedCount;

    /** 本次实际新增的记录数（去重后真正入库的条数） */
    private int addedCount;

    /** 本次被去重移除的条数 */
    private int duplicateCount;

    /** 解析失败/格式非法被跳过的行数 */
    private int invalidCount;

    /** 去重 + 排序后的全量记录 */
    private List<EnrollRecord> records;

    /** 按课程类型分组后的记录（公共课 / 专业课 / 选修课） */
    private Map<String, List<EnrollRecord>> groupedByType;

    public int getParsedCount() {
        return parsedCount;
    }

    public void setParsedCount(int parsedCount) {
        this.parsedCount = parsedCount;
    }

    public int getAddedCount() {
        return addedCount;
    }

    public void setAddedCount(int addedCount) {
        this.addedCount = addedCount;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }

    public void setDuplicateCount(int duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(int invalidCount) {
        this.invalidCount = invalidCount;
    }

    public List<EnrollRecord> getRecords() {
        return records;
    }

    public void setRecords(List<EnrollRecord> records) {
        this.records = records;
    }

    public Map<String, List<EnrollRecord>> getGroupedByType() {
        return groupedByType;
    }

    public void setGroupedByType(Map<String, List<EnrollRecord>> groupedByType) {
        this.groupedByType = groupedByType;
    }
}
