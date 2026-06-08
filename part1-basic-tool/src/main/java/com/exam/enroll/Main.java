package com.exam.enroll;

import java.util.Arrays;
import java.util.List;

/**
 * 第一题演示入口：使用题目给定的样例数据，验证去重、排序、输出。
 */
public class Main {
    public static void main(String[] args) {
        List<EnrollRecord> input1 = Arrays.asList(
                new EnrollRecord("S000001", "C000001", "Java程序设计"),
                new EnrollRecord("S000002", "C000003", "计算机网络"),
                new EnrollRecord("S000001", "C000001", "Java程序设计"), // 重复记录
                new EnrollRecord("S000001", "C000002", "数据库原理"),
                new EnrollRecord("S000003", "C000001", "Java程序设计"));

        System.out.println("===== 原始记录（" + input1.size() + " 条）=====");
        for (EnrollRecord r : input1) {
            System.out.println(r);
        }

        System.out.println("\n===== 处理后（去重 + 排序）=====");
        EnrollProcessor processor = new EnrollProcessor();
        List<EnrollRecord> result = processor.processAndPrint(input1);

        System.out.println("\n处理后共 " + result.size() + " 条记录。");
    }
}
