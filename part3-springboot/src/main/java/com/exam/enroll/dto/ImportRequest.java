package com.exam.enroll.dto;

/**
 * 批量导入请求体：前端文本框提交的 CSV 文本。
 */
public class ImportRequest {

    /** 多行 CSV 文本 */
    private String csv;

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }
}
