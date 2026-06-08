package com.exam.enroll.dto;

import com.exam.enroll.entity.EnrollRecord;

import java.util.List;

/**
 * 检索结果：包含命中记录与提示信息。
 * 无命中时 message 为"无匹配选课记录"。
 */
public class SearchResult {

    private String keyword;
    private int count;
    private String message;
    private List<EnrollRecord> records;

    public SearchResult(String keyword, List<EnrollRecord> records) {
        this.keyword = keyword;
        this.records = records;
        this.count = records == null ? 0 : records.size();
        this.message = this.count == 0 ? "无匹配选课记录" : ("共匹配到 " + this.count + " 条记录");
    }

    public String getKeyword() {
        return keyword;
    }

    public int getCount() {
        return count;
    }

    public String getMessage() {
        return message;
    }

    public List<EnrollRecord> getRecords() {
        return records;
    }
}
