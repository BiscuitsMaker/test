package com.exam.enroll;

import com.exam.enroll.dto.ImportResult;
import com.exam.enroll.entity.EnrollRecord;
import com.exam.enroll.service.impl.EnrollServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * EnrollService 业务逻辑单元测试：去重、排序、分类、检索、性能。
 */
class EnrollServiceTest {

    private EnrollServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EnrollServiceImpl();
        // 不调用 initSampleData，使用空白库做精确断言。
    }

    @Test
    void importDeduplicatesAndSorts() {
        String csv = String.join("\n",
                "S000001,C000001,Java程序设计,专业课",
                "S000002,C000003,计算机网络,公共课",
                "S000001,C000001,Java程序设计,专业课", // 重复
                "S000001,C000002,数据库原理,专业课",
                "S000003,C000001,Java程序设计,专业课");

        ImportResult result = service.importCsv(csv);

        // 5 行解析，去重 1 条，入库 4 条
        assertEquals(5, result.getParsedCount());
        assertEquals(1, result.getDuplicateCount());
        assertEquals(4, result.getAddedCount());

        List<EnrollRecord> all = result.getRecords();
        assertEquals(4, all.size());
        // 排序校验：S1/C1, S1/C2, S2/C3, S3/C1
        assertEquals("S000001", all.get(0).getStudentId());
        assertEquals("C000001", all.get(0).getCourseId());
        assertEquals("C000002", all.get(1).getCourseId());
        assertEquals("S000002", all.get(2).getStudentId());
        assertEquals("S000003", all.get(3).getStudentId());
    }

    @Test
    void autoDetectCourseTypeWhenMissing() {
        // 第 4 列缺失 -> 自动识别；"大学英语"含"英语"关键字 -> 公共课
        service.importCsv("S000005,C000009,大学英语");
        List<EnrollRecord> all = service.findAll();
        assertEquals("公共课", all.get(0).getCourseType());
    }

    @Test
    void groupByType() {
        service.importCsv(String.join("\n",
                "S000001,C000001,Java程序设计,专业课",
                "S000002,C000003,计算机网络,公共课",
                "S000003,C000005,音乐鉴赏,选修课"));
        Map<String, List<EnrollRecord>> grouped = service.groupByType();
        assertEquals(1, grouped.get("公共课").size());
        assertEquals(1, grouped.get("专业课").size());
        assertEquals(1, grouped.get("选修课").size());
    }

    @Test
    void searchAcrossFields() {
        service.importCsv(String.join("\n",
                "S000001,C000001,Java程序设计,专业课",
                "S000002,C000003,计算机网络,公共课"));
        assertEquals(1, service.search("Java").size());
        assertEquals(1, service.search("公共课").size());
        assertEquals(1, service.search("S000001").size());
        assertEquals(1, service.search("C000003").size());
        assertTrue(service.search("不存在的关键词").isEmpty());
    }

    @Test
    void performanceOver1000Records() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1500; i++) {
            sb.append(String.format("S%06d,C%06d,课程%d,专业课%n", i, i, i));
        }
        long t0 = System.currentTimeMillis();
        service.importCsv(sb.toString());
        service.findAll();
        service.search("课程999");
        long cost = System.currentTimeMillis() - t0;
        // 1500 条导入 + 排序 + 检索应远小于 1 秒
        assertTrue(cost < 1000, "处理 1500 条耗时应 < 1s，实际 " + cost + "ms");
    }
}
