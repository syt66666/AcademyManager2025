<template>
  <div class="container">
    <div class="main-container">
      <!-- 顶部标题栏 -->
      <div class="nav">
        <div class="nav-content">
          <h2>
            <span class="score-icon">📚</span>
            学业成绩详情
            <span class="current-semester">{{ activeSemester }} 成绩单</span>
          </h2>
        </div>
      </div>

      <!-- 筛选条件卡片 -->
      <div class="filter-card">
        <el-form :inline="true" :model="queryParams" class="demo-form-inline">
          <el-form-item label="课程搜索">
            <el-input
              v-model="queryParams.courseName"
              placeholder="输入课程名称"
              clearable
              class="custom-input"
            />
          </el-form-item>
                    <el-form-item label="课程类型">
                      <el-select
                        v-model="queryParams.courseCategory"
                        placeholder="所有类型"
                        clearable
                        class="custom-select"
                      >
                        <el-option
                          v-for="item in courseCategorys"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="query-button"
              @click="handleQuery"
              icon="el-icon-search"
            >筛选</el-button
            >
          </el-form-item>
        </el-form>
      </div>

      <!-- 数据概览 -->
      <div class="stats-overview">
        <div class="stat-card">
          <div class="stat-icon">🎯</div>
          <div class="stat-content">
            <div class="stat-value">{{ currentGPA }}</div>
            <div class="stat-label">当前GPA</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">📈</div>
          <div class="stat-content">
            <div class="stat-value">{{ averageScore }}</div>
            <div class="stat-label">平均成绩</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">🏅</div>
          <div class="stat-content">
            <div class="stat-value">{{ maxScore }}</div>
            <div class="stat-label">最高成绩</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">📦</div>
          <div class="stat-content">
            <div class="stat-value">{{ totalCredits }}</div>
            <div class="stat-label">已获学分</div>
          </div>
        </div>
      </div>

      <!-- 成绩表格 -->
      <div class="score-table-card">
        <el-table
          :data="scoreList"
          style="width: 100%"
          class="custom-table"
          :header-cell-style="headerStyle"
          v-loading="loading"
        >
          <el-table-column prop="courseName" label="课程名称" width="220">
            <template slot-scope="{ row }">
              <div class="course-info">
                <span class="course-icon">📖</span>
                <div class="course-details">
                  <div class="course-title">{{ row.courseName }}</div>
                  <div class="course-code">{{ row.courseCode }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="credit" label="学分" width="100" sortable>
            <template slot-scope="{ row }">
              <span class="credit-badge">{{ row.credit }}</span>
            </template>
          </el-table-column>
          <!-- 修改成绩列 -->
          <el-table-column prop="scoreValue" label="成绩" width="100" sortable>
            <template slot-scope="{ row }">
              <span class="credit-badge">{{ row.scoreValue }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="percentRank" label="成绩位次" sortable>
            <template slot-scope="{ row }">
              <div class="score-progress">
                <div
                  class="progress-bar"
                  :style="{
          width: `${parseScore(row.percentRank).value}%`,
          background: scoreColor(row.percentRank),
        }"
                >
        <span class="score-text">
          {{ row.percentRank+'%' }}
          <span v-if="!parseScore(row.percentRank).isValid" class="score-tag">
            {{ parseScore(row.percentRank).value === 100 ? '✔' : '✖' }}
          </span>
        </span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="courseCategory" label="课程类型" width="140">
            <template slot-scope="{ row }">
              <el-tag :type="categoryTagType(row.courseCategory)">
                {{ row.courseCategory }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="gpa" label="绩点" width="120" sortable>
            <template slot-scope="{ row }">
              <span class="gpa-badge">{{ row.gpa }}</span>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { listScore } from "@/api/system/score";
import store from "@/store";

export default {
  created() {
    // 获取路由参数中的学期名称
    const semesterName = this.$route.query.semester || '';
    this.activeSemester = semesterName;
    // 获取当前学期
    this.semester=this.activeSemester.includes("上")? "第一学期" : "第二学期"
    // 从store获取学生ID
    const studentId = this.$store.state.user.name || '';
    // 调用计算函数
    this.academicYear = this.calculateAcademicYear(semesterName, studentId);
    //获取成绩数据
    this.getList();
  },
  data() {
    return {
      activeSemester: "",
      // 加载状态
      loading: true,
      // 总条数
      total: 0,
      // 当前GPA
      currentGPA: 0,
      // 平均成绩
      averageScore: 0,
      // 最高成绩
      maxScore: 0,
      // 总学分
      totalCredits: 0,
      // 成绩表格数据
      scoreList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: store.state.user.name,
        courseName: "",
        courseCategory:"",
        semester: "",
        academicYear:"",
      },
      // 课程类型选项
      courseCategorys: [
        { value: "必修", label: "必修" },
        { value: "选修", label: "选修" },
        { value: "通识", label: "通识" },
      ],
    };
  },
  methods: {
    calculateAcademicYear(semesterName, studentId) {
      // 1. 提取基准年份
      const baseYear = this.extractBaseYear(studentId);
      if (baseYear === null) {
        console.error('学号格式错误，无法计算学年');
        return '未知年份';
      }

      // 2. 计算年级增量
      const gradeIncrement = this.getGradeIncrement(semesterName);

      // 3. 返回最终年份（可根据需求返回数字或字符串）
      return baseYear + gradeIncrement;
      // 或 return `${baseYear + gradeIncrement}级`;
    },

    extractBaseYear(studentId) {
      const yearMatch = studentId.match(/^\d{4}/);
      return yearMatch ? parseInt(yearMatch[0], 10) : null;
    },

    getGradeIncrement(semesterName) {
      const GRADE_MAP = {
        '大一': 0,
        '大二': 1,
        '大三': 2,
        '大四': 3
      };

      // 匹配第一个符合的年级关键词
      const matchedGrade = Object.keys(GRADE_MAP).find(grade =>
        semesterName.includes(grade)
      );

      return matchedGrade ? GRADE_MAP[matchedGrade] : 0;
    },
    parseScore(score) {
      if (score === "通过") return { value: 100, isValid: false }; // 通过不计入统计
      if (score === "不通过") return { value: 0, isValid: false };  // 不通过不计入统计
      const numeric = Number(score);
      return {
        value: isNaN(numeric) ? 0 : numeric,
        isValid: !isNaN(numeric) // 标记有效成绩
      };
    },
    /** 查询成绩列表 */
    getList() {
      this.loading = true;
      this.queryParams.semester=this.semester;
      this.queryParams.academicYear=this.academicYear;
      console.log(this.queryParams)
      listScore(this.queryParams).then(response => {
        this.scoreList = response.rows;
        this.total = response.total;
        this.calcSummaryData();
        this.loading = false;
      });
    },
    /** 计算统计指标 */
    calcSummaryData() {
      if (this.scoreList.length === 0) {
        this.currentGPA = 0;
        this.averageScore = 0;
        this.maxScore = 0;
        this.totalCredits = 0;
        return;
      }

      let validScores = [];
      let totalValidGPA = 0;
      let totalValidCredits = 0;
      let allCredits = 0;

      this.scoreList.forEach(item => {
        // 解析成绩
        const scoreInfo = this.parseScore(item.scoreValue);
        const credit = item.credit || 0;

        // 累计总学分（所有课程）
        allCredits += credit;

        // 仅处理有效成绩
        if (scoreInfo.isValid) {
          validScores.push(scoreInfo.value);
          totalValidGPA += (item.gpa || 0) * credit;
          totalValidCredits += credit;
        }
      });

      // 平均成绩计算（仅有效成绩）
      this.averageScore = validScores.length > 0
        ? (validScores.reduce((a, b) => a + b, 0) / validScores.length).toFixed(1)
        : 0;

      // GPA计算（有效成绩加权）
      this.currentGPA = totalValidCredits > 0
        ? (totalValidGPA / totalValidCredits).toFixed(2)
        : 0;

      // 最高成绩（仅有效成绩）
      this.maxScore = validScores.length > 0
        ? Math.max(...validScores)
        : 0;

      // 总学分（所有课程）
      this.totalCredits = allCredits;
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 根据成绩值获取颜色 */
    scoreColor(score) {
      if (score >= 90) return "#48BB78";
      if (score >= 80) return "#4299E1";
      if (score >= 60) return "#ECC94B";
      return "#F56565";
    },
    /** 课程类型标签样式 */
    categoryTagType(category) {
      const types = {
        专业课: "success",
        公共课: "",
        实践课: "warning",
        选修课: "info",
      };
      return types[category] || "";
    },
    /** 表头样式 */
    headerStyle() {
      return {
        backgroundColor: "#EBF4FF",
        color: "#2B6CB0",
        fontWeight: "600",
      };
    }
  }
};
</script>

<style scoped>
/* 继承主界面的设计变量 */
:root {
  --primary: #2b6cb0;
  --secondary: #4299e1;
  --success: #48bb78;
  --warning: #ecc94b;
  --info: #718096;
  --surface: #f7fafc;
}

.container {
  width: 100%;
  margin: 0 auto;
  padding: 2rem;
  background: linear-gradient(160deg, #EBF4FF 0%, #EBF8FF 100%);
  min-height: 100vh;
  display: flex;
  justify-content: center; /* 水平居中 */
}

.main-container {
  background: #ffffff;
  border-radius: 1.5rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  position: relative;
  overflow: hidden;
  width: 100%;
  max-width: 1400px; /* 添加最大宽度限制 */
  margin: 0 auto;     /* 自动水平居中 */
}

.nav {
  background: linear-gradient(135deg, #2B6CB0 0%, #4299E1 100%);
  border-radius: 1rem;
  margin: -2rem -2rem 2rem;
  position: relative;
  overflow: hidden;
}

.nav::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg,
  rgba(255, 255, 255, 0.1) 25%,
  transparent 50%,
  rgba(255, 255, 255, 0.1) 75%
  );
  opacity: 0.3;
}

.nav-content {
  padding: 1.5rem 2rem;
  position: relative;
  z-index: 1;
}

.nav h2 {
  color: white;
  font-size: 1.8rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 0;
}

.current-semester {
  font-size: 1.2rem;
  opacity: 0.9;
  margin-left: 1rem;
}

.filter-card {
  background: var(--surface);
  border-radius: 1rem;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: var(--surface);
  border-radius: 1rem;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
}

.stat-icon {
  font-size: 2.5rem;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2d3748;
  line-height: 1.2;
}

.stat-label {
  color: #718096;
  font-size: 0.9rem;
}

.score-table-card {
  background: var(--surface);
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.custom-table {
  border-radius: 0.8rem;
  overflow: hidden;
}

.course-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.course-icon {
  font-size: 1.5rem;
}

.credit-badge {
  background: var(--secondary);
  color: #000000;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.9rem;
}

.gpa-badge {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: #000000;
  padding: 4px 12px;
  border-radius: 12px;
  display: inline-block;
}

.score-progress {
  background: #ebf8ff;
  height: 28px;
  border-radius: 14px;
  overflow: hidden;
  position: relative;
}

.progress-bar {
  height: 100%;
  transition: width 0.6s ease;
  display: flex;
  align-items: center;
  padding-right: 12px;
  justify-content: flex-end;
  position: relative;
}

.progress-bar::after {
  content: "";
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: rgba(255, 255, 255, 0.3);
}

.score-text {
  color: white;
  font-size: 0.9rem;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .main-container {
    padding: 1rem;
    border-radius: 1rem;
  }

  .nav {
    margin: -1rem -1rem 1.5rem;
    border-radius: 1rem 1rem 0 0;
  }

  .stats-overview {
    grid-template-columns: 1fr;
  }

  .stat-card {
    padding: 1rem;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 2rem;
  }

  .stat-value {
    font-size: 1.5rem;
  }

  .nav h2 {
    font-size: 1.4rem;
  }
}
</style>
