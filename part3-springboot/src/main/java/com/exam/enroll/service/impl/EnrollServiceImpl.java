package com.exam.enroll.service.impl;

import com.exam.enroll.dto.ImportResult;
import com.exam.enroll.entity.CourseType;
import com.exam.enroll.entity.EnrollRecord;
import com.exam.enroll.service.EnrollService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 选课记录业务实现。
 *
 * <p>存储：使用 {@link ConcurrentHashMap}，以 "学生ID|课程ID" 为 key，
 * 天然实现去重，并支持高并发批量导入；插入/查找均为 O(1)。
 *
 * <p>性能：1000+ 条记录的排序为内存排序（O(n log n)，毫秒级）；
 * 检索为单次线性扫描（O(n)），均远快于 1 秒的要求。
 */
@Service
public class EnrollServiceImpl implements EnrollService {

    /** key = dedupKey(学生ID|课程ID)，天然去重；并发安全。 */
    private final Map<String, EnrollRecord> store = new ConcurrentHashMap<>();

    /** 排序规则：先按学生ID升序，再按课程ID升序。 */
    private static final Comparator<EnrollRecord> SORTER =
            Comparator.comparing(EnrollRecord::getStudentId)
                    .thenComparing(EnrollRecord::getCourseId);

    /** 启动时载入后台写死的样例数据，便于页面加载即可展示。 */
    @PostConstruct
    public void initSampleData() {
        addAllInternal(List.of(
                new EnrollRecord("S000001", "C000001", "Java程序设计", "专业课"),
                new EnrollRecord("S000002", "C000003", "计算机网络", "公共课"),
                new EnrollRecord("S000001", "C000002", "数据库原理", "专业课"),
                new EnrollRecord("S000003", "C000001", "Java程序设计", "专业课"),
                new EnrollRecord("S000002", "C000004", "大学英语", "公共课"),
                new EnrollRecord("S000003", "C000005", "音乐鉴赏", "选修课")
        ));
    }

    @Override
    public ImportResult importCsv(String csvText) {
        ImportResult result = new ImportResult();
        if (csvText == null || csvText.isBlank()) {
            result.setRecords(findAll());
            result.setGroupedByType(groupByType());
            return result;
        }

        // 支持以换行或中英文分号分隔多条记录。
        String[] lines = csvText.split("[\\r\\n;；]+");
        int parsed = 0;
        int invalid = 0;
        int added = 0;
        int duplicate = 0;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            EnrollRecord record = parseLine(trimmed);
            if (record == null) {
                invalid++;
                continue;
            }
            parsed++;
            // putIfAbsent 返回非 null 表示已存在 -> 命中去重。
            if (store.putIfAbsent(record.dedupKey(), record) == null) {
                added++;
            } else {
                duplicate++;
            }
        }

        result.setParsedCount(parsed);
        result.setAddedCount(added);
        result.setDuplicateCount(duplicate);
        result.setInvalidCount(invalid);
        result.setRecords(findAll());
        result.setGroupedByType(groupByType());
        return result;
    }

    @Override
    public List<EnrollRecord> findAll() {
        List<EnrollRecord> all = new ArrayList<>(store.values());
        all.sort(SORTER);
        return all;
    }

    @Override
    public Map<String, List<EnrollRecord>> groupByType() {
        // 使用 LinkedHashMap 固定展示顺序：公共课 -> 专业课 -> 选修课。
        Map<String, List<EnrollRecord>> grouped = new LinkedHashMap<>();
        for (CourseType type : CourseType.values()) {
            grouped.put(type.getLabel(), new ArrayList<>());
        }
        for (EnrollRecord r : findAll()) {
            grouped.computeIfAbsent(r.getCourseType(), k -> new ArrayList<>()).add(r);
        }
        return grouped;
    }

    @Override
    public List<EnrollRecord> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String kw = keyword.trim().toLowerCase();
        List<EnrollRecord> hits = new ArrayList<>();
        for (EnrollRecord r : store.values()) {
            if (matches(r.getStudentId(), kw)
                    || matches(r.getCourseId(), kw)
                    || matches(r.getCourseName(), kw)
                    || matches(r.getCourseType(), kw)) {
                hits.add(r);
            }
        }
        hits.sort(SORTER);
        return hits;
    }

    // ------------------- 内部辅助方法 -------------------

    private boolean matches(String field, String lowerKeyword) {
        return field != null && field.toLowerCase().contains(lowerKeyword);
    }

    /** 批量入库（内部使用，去重）。 */
    private void addAllInternal(List<EnrollRecord> records) {
        for (EnrollRecord r : records) {
            store.putIfAbsent(r.dedupKey(), r);
        }
    }

    /**
     * 解析单行 CSV：学生ID,课程ID,课程名称[,课程类型]。
     *
     * @return 解析成功返回记录；字段不足或非法返回 null
     */
    private EnrollRecord parseLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 3) {
            return null;
        }
        String studentId = parts[0].trim();
        String courseId = parts[1].trim();
        String courseName = parts[2].trim();
        if (studentId.isEmpty() || courseId.isEmpty() || courseName.isEmpty()) {
            return null;
        }
        String rawType = parts.length >= 4 ? parts[3].trim() : null;
        // 选课分类：优先手动标注，识别不出则按课程名称自动识别。
        String courseType = CourseType.resolve(rawType, courseName);
        return new EnrollRecord(studentId, courseId, courseName, courseType);
    }
}
