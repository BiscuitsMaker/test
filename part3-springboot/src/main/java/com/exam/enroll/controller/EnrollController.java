package com.exam.enroll.controller;

import com.exam.enroll.dto.ImportRequest;
import com.exam.enroll.dto.ImportResult;
import com.exam.enroll.dto.SearchResult;
import com.exam.enroll.entity.EnrollRecord;
import com.exam.enroll.service.EnrollService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 选课记录 REST 接口。
 *
 * <p>遵循分层设计：本类仅负责接收请求参数、调用 Service、返回结果，
 * 不包含任何业务逻辑（去重/排序/分类/检索均在 {@link EnrollService} 中实现）。
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollController {

    private final EnrollService enrollService;

    /** 构造器注入，便于测试与解耦。 */
    public EnrollController(EnrollService enrollService) {
        this.enrollService = enrollService;
    }

    /** 批量导入 CSV 文本，返回处理（去重/排序/分类）后的结果。 */
    @PostMapping("/import")
    public ImportResult importCsv(@RequestBody ImportRequest request) {
        return enrollService.importCsv(request.getCsv());
    }

    /** 查询全量（去重 + 排序）记录。 */
    @GetMapping
    public List<EnrollRecord> findAll() {
        return enrollService.findAll();
    }

    /** 按课程类型分组展示。 */
    @GetMapping("/grouped")
    public Map<String, List<EnrollRecord>> grouped() {
        return enrollService.groupByType();
    }

    /** 关键词检索（学生ID/课程ID/课程名称/课程类型）。 */
    @GetMapping("/search")
    public SearchResult search(@RequestParam(value = "keyword", required = false) String keyword) {
        List<EnrollRecord> hits = enrollService.search(keyword);
        return new SearchResult(keyword, hits);
    }
}
