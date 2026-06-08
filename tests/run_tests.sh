#!/usr/bin/env bash
# =====================================================================
# 高校选课管理系统 - 全量测试运行器
# 按步骤执行，每个监测节点输出 PASS/FAIL，并写入 tests/logs/
# =====================================================================
set -u

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOGDIR="$ROOT/tests/logs"
SUMMARY="$LOGDIR/summary.log"
rm -rf "$LOGDIR"; mkdir -p "$LOGDIR"

PASS=0; FAIL=0
APP_PID=""

cleanup() { [ -n "$APP_PID" ] && kill "$APP_PID" 2>/dev/null; }
trap cleanup EXIT

ts() { date '+%Y-%m-%d %H:%M:%S'; }

# node <id> <desc> <ok(true/false)>
node() {
  local id="$1" desc="$2" ok="$3" line
  if [ "$ok" = "true" ]; then
    line="[PASS] 节点 $id  $desc"; PASS=$((PASS+1))
  else
    line="[FAIL] 节点 $id  $desc"; FAIL=$((FAIL+1))
  fi
  echo "$(ts) $line" | tee -a "$SUMMARY"
}

step() { echo "" | tee -a "$SUMMARY"; echo "===== $* =====" | tee -a "$SUMMARY"; }

echo "测试开始 $(ts)" | tee "$SUMMARY"

# ---------------------------------------------------------------------
step "步骤1 第一题 基础处理工具(Java)"
P1="$ROOT/part1-basic-tool"; P1LOG="$LOGDIR/step1_part1.log"
rm -rf "$P1/out"
javac -encoding UTF-8 -d "$P1/out" $(find "$P1/src" -name "*.java") > "$P1LOG" 2>&1
node 1.1 "源码编译通过" "$([ $? -eq 0 ] && echo true || echo false)"

OUT="$(java -Dfile.encoding=UTF-8 -cp "$P1/out" com.exam.enroll.Main 2>>"$P1LOG")"
echo "$OUT" >> "$P1LOG"
# 取"处理后"区块的记录行
PROC="$(echo "$OUT" | awk '/处理后（去重/{f=1;next} /处理后共/{f=0} f && NF')"

echo "$OUT" | grep -q "处理后共 4 条记录" && ok=true || ok=false
node 1.2 "去重正确(5条->4条)" "$ok"

L1="$(echo "$PROC" | sed -n 1p)"; L2="$(echo "$PROC" | sed -n 2p)"
L3="$(echo "$PROC" | sed -n 3p)"; L4="$(echo "$PROC" | sed -n 4p)"
if echo "$L1" | grep -q "S000001" && echo "$L1" | grep -q "C000001" \
 && echo "$L2" | grep -q "S000001" && echo "$L2" | grep -q "C000002" \
 && echo "$L3" | grep -q "S000002" && echo "$L3" | grep -q "C000003" \
 && echo "$L4" | grep -q "S000003" && echo "$L4" | grep -q "C000001"; then ok=true; else ok=false; fi
node 1.3 "排序正确(学生ID升序,相同按课程ID升序)" "$ok"

if echo "$L1" | grep -q "学生ID：" && echo "$L1" | grep -q "，课程ID：" \
 && echo "$L1" | grep -q "，课程名称："; then ok=true; else ok=false; fi
node 1.4 "输出格式正确(学生ID：XXX，课程ID：XXX，课程名称：XXX)" "$ok"

# ---------------------------------------------------------------------
step "步骤2 第二题 SQL(SQLite真实执行)"
P2LOG="$LOGDIR/step2_part2.log"; DB="$LOGDIR/test.db"; rm -f "$DB"
sqlite3 "$DB" < "$ROOT/tests/sql/seed.sql" > "$P2LOG" 2>&1
node 2.1 "建表+注入种子数据" "$([ $? -eq 0 ] && echo true || echo false)"

Q1="$(sqlite3 "$DB" < "$ROOT/tests/sql/q1.sql")"
{ echo "--- Q1 输出 ---"; echo "$Q1"; } >> "$P2LOG"
Q1_EXPECT="C000003|计算机网络|80
C000006|编译原理|70
C000001|Java程序设计|60
C000004|大学英语|55
C000002|数据库原理|30
C000005|数据结构|0"
[ "$Q1" = "$Q1_EXPECT" ] && ok=true || ok=false
node 2.2 "Q1结果与降序排序正确" "$ok"
echo "$Q1" | grep -q "C000005|数据结构|0" && ok=true || ok=false
node 2.3 "Q1含0人课程(LEFT JOIN生效)" "$ok"

Q2="$(sqlite3 "$DB" < "$ROOT/tests/sql/q2.sql")"
{ echo "--- Q2 输出 ---"; echo "$Q2"; } >> "$P2LOG"
Q2_EXPECT="C000001|Java程序设计|60
C000006|编译原理|70"
[ "$Q2" = "$Q2_EXPECT" ] && ok=true || ok=false
node 2.4 "Q2专业课>50且升序正确" "$ok"
if echo "$Q2" | grep -q "C000003" || echo "$Q2" | grep -q "C000002"; then ok=false; else ok=true; fi
node 2.5 "Q2正确排除公共课/≤50课程" "$ok"

# ---------------------------------------------------------------------
step "步骤3 第三题 后端单元测试"
P3="$ROOT/part3-springboot"; UTLOG="$LOGDIR/step3_unit.log"
( cd "$P3" && mvn -B test ) > "$UTLOG" 2>&1
grep -q "Tests run: 5, Failures: 0, Errors: 0" "$UTLOG" && ok=true || ok=false
node 3.1 "mvn test 全部通过(5用例)" "$ok"

# ---------------------------------------------------------------------
step "步骤4 第三题 构建打包"
BUILDLOG="$LOGDIR/step4_build.log"
( cd "$P3" && mvn -B -DskipTests package ) > "$BUILDLOG" 2>&1
[ -f "$P3/target/enroll-system-1.0.0.jar" ] && ok=true || ok=false
node 4.1 "打包成功(生成可执行jar)" "$ok"

# ---------------------------------------------------------------------
step "步骤5 第三题 启动与健康检查"
APPLOG="$LOGDIR/step5_app.log"
java -jar "$P3/target/enroll-system-1.0.0.jar" > "$APPLOG" 2>&1 &
APP_PID=$!
ok=false
for i in $(seq 1 30); do
  if grep -q "Started EnrollApplication" "$APPLOG"; then ok=true; break; fi
  sleep 1
done
node 5.1 "应用启动成功" "$ok"
CODE="$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/ 2>/dev/null)"
[ "$CODE" = "200" ] && ok=true || ok=false
node 5.2 "首页可访问(HTTP $CODE)" "$ok"

# ---------------------------------------------------------------------
step "步骤6 第三题 接口功能测试"
APILOG="$LOGDIR/step6_api.log"
# api_check.py: stdout=机器结果, stderr=明细
python3 "$ROOT/tests/api_check.py" 2>"$APILOG" | while IFS=$'\t' read -r res nid desc; do
  echo "$res|$nid|$desc"
done > "$LOGDIR/.api_results"
while IFS='|' read -r res nid desc; do
  [ "$res" = "PASS" ] && node "$nid" "$desc" true || node "$nid" "$desc" false
done < "$LOGDIR/.api_results"
rm -f "$LOGDIR/.api_results"

cleanup; APP_PID=""

# ---------------------------------------------------------------------
step "汇总"
echo "$(ts) 测试结束：通过 $PASS 项，失败 $FAIL 项" | tee -a "$SUMMARY"
echo "日志目录：tests/logs/" | tee -a "$SUMMARY"
[ "$FAIL" -eq 0 ] && exit 0 || exit 1
