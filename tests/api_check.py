#!/usr/bin/env python3
"""第三题接口功能测试。
对运行中的应用(localhost:8080)逐节点断言；
机器可读结果输出到 stdout（格式：PASS/FAIL\t节点号\t描述），
详细信息输出到 stderr（由 run_tests.sh 重定向到日志）。
"""
import json
import sys
import time
import urllib.parse
import urllib.request

BASE = "http://127.0.0.1:8080/api/enrollments"


def log(*a):
    print(*a, file=sys.stderr)


def report(ok, node, desc):
    print(f"{'PASS' if ok else 'FAIL'}\t{node}\t{desc}")


def get(path):
    with urllib.request.urlopen(BASE + path, timeout=10) as r:
        return json.loads(r.read().decode("utf-8"))


def post_import(csv):
    body = json.dumps({"csv": csv}).encode("utf-8")
    req = urllib.request.Request(
        BASE + "/import", data=body,
        headers={"Content-Type": "application/json"}, method="POST")
    with urllib.request.urlopen(req, timeout=30) as r:
        return json.loads(r.read().decode("utf-8"))


def is_sorted(records):
    keys = [(r["studentId"], r["courseId"]) for r in records]
    return keys == sorted(keys)


def main():
    run = int(time.time()) % 1000000  # 每次运行唯一，避免与历史数据撞键

    # 6.1 样例数据加载
    grouped = get("/grouped")
    ok = all(k in grouped for k in ("公共课", "专业课", "选修课")) and \
        sum(len(v) for v in grouped.values()) >= 6
    log("6.1 grouped 各类型数量:", {k: len(v) for k, v in grouped.items()})
    report(ok, "6.1", "样例数据加载并按类型分组展示")

    # 6.2 CSV 导入 + 去重（批内含 1 行重复）
    res = post_import(
        f"S{run:06d},C900001,操作系统,专业课\nS{run:06d},C900001,操作系统,专业课")
    log("6.2 import 结果:", {k: res[k] for k in (
        "parsedCount", "addedCount", "duplicateCount", "invalidCount")})
    report(res["duplicateCount"] >= 1, "6.2", "CSV 导入去重(重复行被移除)")

    # 6.3 自动分类识别（缺类型 + 课程名含'英语' -> 公共课）
    res = post_import(f"S{run:06d},C900002,大学英语")
    rec = next((r for r in res["records"]
                if r["studentId"] == f"S{run:06d}" and r["courseId"] == "C900002"), None)
    ok = rec is not None and rec["courseType"] == "公共课"
    log("6.3 自动识别记录:", rec)
    report(ok, "6.3", "缺类型自动识别(大学英语->公共课)")

    # 6.4 手动标注分类
    res = post_import(f"S{run:06d},C900003,数据结构,专业课")
    rec = next((r for r in res["records"]
                if r["studentId"] == f"S{run:06d}" and r["courseId"] == "C900003"), None)
    ok = rec is not None and rec["courseType"] == "专业课"
    log("6.4 手动标注记录:", rec)
    report(ok, "6.4", "手动标注分类(操作系统->专业课)")

    # 6.5 导入后全局排序
    ok = is_sorted(res["records"])
    log("6.5 records 是否有序:", ok, " 共", len(res["records"]), "条")
    report(ok, "6.5", "导入后按学生ID/课程ID升序")

    # 6.6 检索命中
    s = get("/search?keyword=" + urllib.parse.quote("专业课"))
    log("6.6 检索'专业课':", s["message"])
    report(s["count"] > 0, "6.6", "检索命中(专业课)")

    # 6.7 四字段检索
    fields = {
        "学生ID": "S000001", "课程ID": "C000001",
        "课程名称": "Java", "课程类型": "公共课",
    }
    detail = {}
    ok = True
    for name, kw in fields.items():
        r = get("/search?keyword=" + urllib.parse.quote(kw))
        detail[f"{name}({kw})"] = r["count"]
        ok = ok and r["count"] > 0
    log("6.7 四字段检索命中数:", detail)
    report(ok, "6.7", "四字段(学生ID/课程ID/课程名/类型)均可检索")

    # 6.8 检索无匹配提示
    s = get("/search?keyword=zzzz_no_match_" + str(run))
    log("6.8 无匹配返回:", s["message"])
    report(s["message"] == "无匹配选课记录", "6.8", "检索不到提示'无匹配选课记录'")

    # 6.9 单次批量导入 ≥500（导入 600 条全新记录）
    lines = "\n".join(
        f"S{(run + i) % 1000000:06d},C{run:06d},批量课程{i},专业课" for i in range(600))
    res = post_import(lines)
    log("6.9 批量600导入:", {k: res[k] for k in (
        "parsedCount", "addedCount", "duplicateCount", "invalidCount")})
    report(res["parsedCount"] == 600 and res["addedCount"] == 600,
           "6.9", "单次批量导入≥500(600条)")

    # 6.10 性能：≥1000 条下检索 ≤1s（先导入 1000 全新记录，再计时检索）
    run2 = (run + 500000) % 1000000
    lines = "\n".join(
        f"S{(run2 + i) % 1000000:06d},C{run2:06d},性能课程{i},公共课" for i in range(1000))
    res = post_import(lines)
    total = len(res["records"])
    t0 = time.time()
    get("/search?keyword=" + urllib.parse.quote("性能课程999"))
    cost_ms = (time.time() - t0) * 1000
    log(f"6.10 当前总记录数={total}, 检索耗时={cost_ms:.1f}ms")
    report(total >= 1000 and cost_ms < 1000, "6.10",
           f"1000+记录检索≤1s(实测{cost_ms:.0f}ms,总{total}条)")


if __name__ == "__main__":
    main()
