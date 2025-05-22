<template>
  <div class="app-container">
    <!-- 优化后的选择对话框 -->
    <el-dialog
      title="📚 选择修读方式"
      :visible.sync="dialogVisible"
      width="400px"
      class="mode-dialog"
      :close-on-click-modal="false"
    >
      <div class="mode-selection">
        <el-radio-group v-model="selectedStudyMode">
          <el-radio
            label="standard"
            class="mode-card"
            :class="{active: selectedStudyMode === 'standard'}"
          >
            <div class="mode-content">
              <i class="el-icon-notebook-2 card-icon"></i>
              <h3>标准修读</h3>
              <p>参加完整课程学习<br>完成课业要求</p>
              <div class="highlight-line"></div>
            </div>
          </el-radio>
          <el-radio
            label="activity"
            class="mode-card"
            :class="{active: selectedStudyMode === 'activity'}"
          >
            <div class="mode-content">
              <i class="el-icon-medal-1 card-icon"></i>
              <h3>活动置换</h3>
              <p>通过实践活动置换<br>获得相应学分</p>
              <div class="highlight-line"></div>
            </div>
          </el-radio>
        </el-radio-group>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
        <el-button
          type="primary"
          @click="confirmEnroll"
          class="confirm-btn"
        >
          <i class="el-icon-check"></i> 确认选择
        </el-button>
      </div>
    </el-dialog>

    <!-- 优化后的查询表单 -->
    <el-card class="search-card">
      <el-form
        :model="queryParams"
        ref="queryForm"
        class="advanced-search-form"
      >
        <div class="form-row">
          <!-- 课程代码 -->
          <el-form-item label="课程代码" prop="courseCode" class="form-item">
            <el-input
              v-model="queryParams.courseCode"
              placeholder="输入课程代码"
              suffix-icon="el-icon-search"
              class="compact-input"
            />
          </el-form-item>

          <!-- 课程名称 -->
          <el-form-item label="课程名称" prop="courseName" class="form-item">
            <el-input
              v-model="queryParams.courseName"
              placeholder="输入课程名称"
              class="compact-input"
            />
          </el-form-item>

          <!-- 开课院系 -->
          <el-form-item label="开课院系" prop="academy" class="form-item">
            <el-select
              v-model="queryParams.academy"
              placeholder="选择院系"
              class="compact-select"
              popper-class="rounded-dropdown"
            >
              <el-option
                v-for="item in 7"
                :key="item"
                :label="`书院${item}`"
                :value="`书院${item}`"
              />
            </el-select>
          </el-form-item>

        <!-- 授课教师 -->
        <el-form-item label="授课教师" prop="teacherName" class="form-item">
          <el-input
            v-model="queryParams.teacherName"
            placeholder="输入教师姓名"
            class="compact-input"
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button
            type="primary"
            icon="el-icon-search"
            class="search-btn"
            @click="handleQuery"
          >
            搜索
          </el-button>
          <el-button
            class="reset-btn"
            @click="resetQuery"
          >
            <i class="el-icon-refresh-right"></i> 重置
          </el-button>
        </div>
  </div>
  </el-form>
    </el-card>

    <!-- 优化后的数据表格 -->
    <el-card class="data-card">
      <div slot="header" class="table-header">
        <h2 class="table-title">📖 课程列表</h2>
        <div class="header-extra">
          <span class="total-count">共 {{ total }} 门课程</span>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="courseList"
        class="modern-table"
        style="width: 100%"
        :row-class-name="tableRowClassName"
      >
        <!-- 表格列优化 -->
        <el-table-column label="序号" width="80" align="center">
          <template slot-scope="scope">
            <span class="index-badge">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="课程信息" min-width="240">
          <template slot-scope="{row}">
            <div class="course-info">
              <span class="course-code">{{ row.courseCode }}</span>
              <h3 class="course-name">{{ row.courseName }}</h3>
              <div class="meta-info">
                <span class="academy-tag">{{ row.academy }}</span>
                <span class="teacher">{{ row.teacherName }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="课程详情" width="180" align="center">
          <template slot-scope="{row}">
            <div class="stats-box">
              <div class="stat-item">
                <span class="stat-label">学分</span>
                <span class="stat-value credit">{{ row.credit }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">课时</span>
                <span class="stat-value">{{ row.courseHours }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">容量</span>
                <span class="stat-value capacity">
                  {{ row.enrolledStudent }}/{{ row.courseCapacity }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="类型" width="120" align="center">
          <template slot-scope="{row}">
            <el-tag
              :type="row.courseCategory === '必修' ? 'success' : 'warning'"
              effect="dark"
              class="category-tag"
            >
              {{ row.courseCategory }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button
                size="mini"
                :type="scope.row.isEnrolled ? 'info' : 'success'"
                :disabled="scope.row.isEnrolled || scope.row.enrolledStudent >= scope.row.courseCapacity"
                @click="showModeDialog(scope.row)"
                class="action-btn enroll-btn"
              >
                {{ scope.row.isEnrolled ? '已选 ✓' : '选课' }}
              </el-button>
              <el-button
                size="mini"
                type="danger"
                :disabled="!scope.row.isEnrolled"
                @click="handleDrop(scope.row)"
                class="action-btn drop-btn"
              >
                退课
              </el-button>
            </div>
          </template>
        </el-table-column>

        <!-- 展开行优化 -->
        <el-table-column type="expand" width="60">
          <template slot-scope="{row}">
            <div v-if="row.replacementConditions && row.replacementConditions.length" class="condition-panel">
              <div class="panel-header">
                <i class="el-icon-document-checked"></i>
                <h3>学分置换要求</h3>
              </div>
              <ul class="condition-list">
                <li
                  v-for="(item,index) in row.replacementConditions"
                  :key="index"
                  class="condition-item"
                >
                  <div class="condition-index">{{ index+1 }}</div>
                  <div class="condition-content">{{ item }}</div>
                </li>
              </ul>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页优化 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
        class="styled-pagination"
      />
    </el-card>
  </div>
</template>

<script>
import { listCourse } from "@/api/system/course";
import { addEnrollments, delEnrollments, listEnrollments } from "@/api/system/enrollment";
import store from "@/store";

export default {
  name: "Course",
  data() {
    return {
      dialogVisible: false,
      selectedStudyMode: 'standard',
      currentCourse: null,
      loading: true,
      total: 0,
      courseList: [],
      enrolledCourseCodes: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseCode: null,
        courseName: null,
        credit: null,
        courseCategory: '特色课',
        teacherName: null,
        academy: null,
        courseHours: null,
        courseCapacity: null
      }
    };
  },
  async created() {
    await this.loadEnrollments();
    await this.getList();
  },
  methods: {
    // 新增模式选择对话框控制
    showModeDialog(row) {
      this.currentCourse = row;
      this.dialogVisible = true;
    },
    // 新增确认选课方法
    confirmEnroll() {
      if (!this.selectedStudyMode) {
        this.$message.warning('请选择修读方式');
        return;
      }
      this.handleEnroll(this.currentCourse, this.selectedStudyMode);
    },
    // 全量加载选课记录（自动分页）
    async loadEnrollments() {
      try {
        let allRecords = [];
        let currentPage = 1;
        let totalPages = 1;

        do {
          const res = await listEnrollments({
            studentId: store.state.user.name,
            pageNum: currentPage,
            pageSize: 100
          });

          allRecords = [...allRecords, ...res.rows];
          totalPages = Math.ceil(res.total / res.pageSize);
        } while (currentPage++ < totalPages);

        this.enrolledCourseCodes = allRecords.map(item =>
          String(item.courseCode).trim()
        );
      } catch (error) {
        console.error("选课记录加载失败:", error);
        this.$message.error("选课记录加载失败，请刷新重试");
      }
    },

    // 精确标记已选状态
    markEnrolledCourses() {
      this.courseList = this.courseList.map(course => {
        const isEnrolled = this.enrolledCourseCodes.some(code =>
          code === String(course.courseCode).trim()
        );
        return { ...course, isEnrolled };
      });
    },

    // 获取课程列表（自动同步状态）
    // 修改后的getList方法
    async getList() {
      this.loading = true;
      try {
        const res = await listCourse(this.queryParams);

        // 添加固定置换规则
        this.courseList = res.rows.map(course => {
          // 为指定课程添加置换规则
          const replacementRules = this.getStaticReplacementRules(course.courseCode);
          console.log(replacementRules)
          return {
            ...course,
            courseCode: String(course.courseCode).trim(),
            replacementConditions: replacementRules
          };
        });
        this.total = res.total;
        this.markEnrolledCourses();
      } catch (error) {
        console.error("课程加载失败:", error);
        this.$message.error("课程数据加载失败");
      } finally {
        this.loading = false;
      }
    },

    // 新增静态规则配置方法
    getStaticReplacementRules(courseCode) {
      // 定义课程置换规则映射表
      const replacementRules = {
        'CS102': [
          '完成至少3个科创项目实践',
          '获得校级以上竞赛奖项',
          '提交5000字实践报告'
        ],
        'CS101': [
          '参加3次以上艺术工作坊',
          '完成艺术作品创作',
          '通过专家评审'
        ]
      }

      // 返回对应课程的规则，没有则返回空数组
      return replacementRules[courseCode] || [];
    },
    // 强化版选课操作
    async handleEnroll(row) {
      try {
        // 状态预检（双重验证）
        if (row.isEnrolled) {
          return this.$message.warning("该课程已选，不可重复操作");
        }
        if (this.enrolledCourseCodes.includes(row.courseCode)) {
          return this.$message.warning("系统检测到重复选课，请刷新页面");
        }

        await this.$confirm(`确定选择【${row.courseName}】课程吗？`, "操作确认", {
          type: "warning",
          closeOnClickModal: false,
          lockScroll: false
        });

        // 乐观更新
        this.enrolledCourseCodes = [...this.enrolledCourseCodes, row.courseCode];
        this.markEnrolledCourses();
        // row.enrolledStudent += 1;

        // 提交请求
        await addEnrollments({
          studentId: store.state.user.name,
          courseCode: row.courseCode
        });
        await this.getList();
        this.$message.success("选课成功");
      } catch (error) {
        // 状态回滚
        this.enrolledCourseCodes = this.enrolledCourseCodes.filter(
          code => code !== row.courseCode
        );
        this.markEnrolledCourses();
        // row.enrolledStudent = Math.max(0, row.enrolledStudent - 1);

        if (error !== "cancel") {
          if (error.message.includes("Duplicate entry")) {
            await this.loadEnrollments();
            this.markEnrolledCourses();
            this.$message.warning("系统状态已同步，请勿重复操作");
          } else {
            this.$message.error(`选课失败: ${error.message}`);
          }
        }
      }
    },

    // 强化版退课操作
    async handleDrop(row) {
      try {
        await this.$confirm(`确定要退选【${row.courseName}】课程吗？`, "退课操作", {
          type: "error",
          confirmButtonClass: "el-button--danger",
          closeOnClickModal: false
        });

        // 查找选课ID（支持多记录校验）
        const enrollments = await listEnrollments({
          studentId: store.state.user.name,
          courseCode: row.courseCode
        });

        if (enrollments.rows.length === 0) {
          throw new Error("未找到选课记录，可能已被删除");
        }

        // 批量删除（防止冗余记录）
        await Promise.all(
          enrollments.rows.map(item =>
            delEnrollments(item.enrollmentId)
          )
        );

        // 状态更新
        this.enrolledCourseCodes = this.enrolledCourseCodes.filter(
          code => code !== row.courseCode
        );
        this.markEnrolledCourses();
        // row.enrolledStudent = Math.max(0, row.enrolledStudent - 1);
        await this.getList();
        this.$message.success("退课成功");
      } catch (error) {
        if (error !== "cancel") {
          this.$message.error(`退课失败: ${error.message}`);
          await this.loadEnrollments();
          this.markEnrolledCourses();
        }
      }
    },

    // 其他方法保持不变
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    }
  }
};
</script>
<style lang="scss" scoped>
/* 主容器 */
.app-container {
  padding: 20px;
  background: #f5f7fa;
  margin-left: 100px;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);

  ::v-deep .el-card__body {
    padding: 20px;
  }
}

/* 高级搜索表单 */
.advanced-search-form {
  .form-row {
    display: flex;
    gap: 15px;
    margin-bottom: 15px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .round-input {
    ::v-deep .el-input__inner {
      border-radius: 20px;
      padding: 0 15px;
      height: 36px;
      transition: all 0.3s ease;

      &:focus {
        box-shadow: 0 0 8px rgba(64, 158, 255, 0.2);
      }
    }
  }

  .form-actions {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-left: auto;
  }

  .search-btn {
    border-radius: 20px;
    padding: 10px 25px;
    background: linear-gradient(135deg, #409EFF, #3375ff);
    border: none;
    transition: transform 0.2s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    }
  }

  .reset-btn {
    border-radius: 20px;
    padding: 10px 20px;
    border-color: #ddd;
    color: #666;

    &:hover {
      color: #409EFF;
      border-color: #409EFF;
    }
  }
}

/* 数据卡片 */
.data-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);

  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;

    .table-title {
      font-size: 18px;
      color: #2c3e50;
      margin: 0;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .total-count {
      font-size: 14px;
      color: #909399;
    }
  }
}

/* 现代表格样式 */
.modern-table {
  ::v-deep th {
    background: #f8fafd !important;
    color: #2c3e50;
    font-weight: 600;
  }

  ::v-deep tr {
    transition: all 0.2s ease;

    &:hover {
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
  }

  .course-info {
    .course-code {
      font-size: 12px;
      color: #909399;
    }

    .course-name {
      margin: 6px 0;
      font-size: 15px;
      color: #2c3e50;
    }

    .meta-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .academy-tag {
        background: #e8f4ff;
        color: #409EFF;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;
      }

      .teacher {
        color: #666;
        font-size: 13px;
      }
    }
  }

  .stats-box {
    display: flex;
    justify-content: space-around;
    padding: 8px;

    .stat-item {
      text-align: center;

      .stat-label {
        display: block;
        font-size: 12px;
        color: #909399;
        margin-bottom: 4px;
      }

      .stat-value {
        font-weight: 600;
        color: #2c3e50;

        &.credit {
          color: #67C23A;
        }

        &.capacity {
          color: #E6A23C;
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .action-btn {
      width: 100%;
      border-radius: 16px;
      transition: all 0.2s ease;

      &.enroll-btn:not([disabled]) {
        background: linear-gradient(135deg, #67C23A, #5daf34);
        border: none;

        &:hover {
          box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
        }
      }

      &.drop-btn:not([disabled]) {
        background: linear-gradient(135deg, #F56C6C, #e65050);
        border: none;
      }
    }
  }
}

/* 条件面板 */
.condition-panel {
  padding: 16px;
  background: #f9fafc;
  margin: 8px;
  border-radius: 8px;

  .panel-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;

    h3 {
      margin: 0;
      color: #2c3e50;
      font-size: 15px;
    }

    i {
      color: #409EFF;
      font-size: 18px;
    }
  }

  .condition-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px;
    background: white;
    margin-bottom: 8px;
    border-radius: 6px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);

    .condition-index {
      width: 24px;
      height: 24px;
      background: #409EFF;
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
    }

    .condition-content {
      color: #606266;
      font-size: 14px;
    }
  }
}

/* 分页样式 */
.styled-pagination {
  ::v-deep .btn-prev,
  ::v-deep .btn-next {
    border-radius: 8px;
    padding: 0 12px;
  }

  ::v-deep .el-pager li {
    border-radius: 8px;
    margin: 0 4px;
    transition: all 0.2s ease;

    &.active {
      background: #409EFF;
      color: white;
      box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
    }
  }
}

/* 模式选择对话框 */
.mode-dialog {
  ::v-deep .el-dialog {
    border-radius: 16px;
    overflow: hidden;

    &__header {
      background: linear-gradient(135deg, #409EFF, #3375ff);
      padding: 20px;

      .el-dialog__title {
        color: white;
        font-size: 18px;
      }
    }

    &__body {
      padding: 30px 40px;
    }
  }

  .mode-selection {
    display: grid;
    gap: 20px;
    grid-template-columns: 1fr 1fr;
  }

  .mode-card {
    border: 2px solid #ebeef5;
    border-radius: 12px;
    padding: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
    background: white;
    position: relative;
    overflow: hidden;

    &.active {
      border-color: #409EFF;
      box-shadow: 0 4px 16px rgba(64, 158, 255, 0.2);

      .highlight-line {
        width: 100%;
      }
    }

    .mode-content {
      text-align: center;

      .card-icon {
        font-size: 32px;
        color: #409EFF;
        margin-bottom: 12px;
      }

      h3 {
        margin: 8px 0;
        color: #2c3e50;
        font-size: 16px;
      }

      p {
        color: #909399;
        font-size: 13px;
        line-height: 1.5;
        margin: 8px 0;
      }

      .highlight-line {
        position: absolute;
        bottom: 0;
        left: 0;
        height: 3px;
        width: 0;
        background: #409EFF;
        transition: width 0.3s ease;
      }
    }

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
    }
  }

  .dialog-footer {
    padding: 20px;
    text-align: right;

    .cancel-btn {
      border-radius: 20px;
      padding: 10px 25px;
    }

    .confirm-btn {
      border-radius: 20px;
      padding: 10px 30px;
      background: linear-gradient(135deg, #67C23A, #5daf34);
      border: none;
      transition: transform 0.2s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
      }
    }
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.table-enter-active {
  animation: fadeIn 0.3s ease;
}
</style>
<!--<style scoped>-->
<!--.app-container {-->
<!--  /* 基础字体放大 */-->
<!--  font-size: 1.25rem; /* 默认16px → 20px */-->

<!--  /* 调整ElementUI组件字体 */-->
<!--  ::v-deep {-->
<!--    /* 表单项标签 */-->
<!--    .el-form-item__label {-->
<!--      font-size: 1.125rem !important; /* 18px */-->
<!--    }-->

<!--    /* 输入框文字 */-->
<!--    .el-input__inner,-->
<!--    .el-select .el-input__inner {-->
<!--      font-size: 1rem; /* 16px */-->
<!--    }-->

<!--    /* 表格文字 */-->
<!--    .el-table {-->
<!--      font-size: 1.125rem; /* 18px */-->
<!--      th > .cell { font-size: 1.125rem; }-->
<!--    }-->

<!--    /* 按钮文字 */-->
<!--    .el-button {-->
<!--      font-size: 1.125rem;-->
<!--      [class*=" el-icon-"], [class^=el-icon-] {-->
<!--        font-size: 1.25rem;-->
<!--      }-->
<!--    }-->

<!--    /* 对话框标题 */-->
<!--    .el-dialog__title {-->
<!--      font-size: 1.5rem !important; /* 24px */-->
<!--    }-->
<!--  }-->
<!--}-->

<!--/* 调整特殊元素 */-->
<!--.table-title {-->
<!--  font-size: 1.75rem !important; /* 28px */-->
<!--}-->

<!--.credit-badge {-->
<!--  font-size: 1.125rem; /* 18px */-->
<!--}-->

<!--/* 展开面板文字调整 */-->
<!--.condition-panel {-->
<!--  font-size: 1.125rem;-->
<!--  .panel-header span {-->
<!--    font-size: 1.25rem;-->
<!--  }-->
<!--}-->
<!--/* ================= 全局样式变量 ================= */-->
<!--.app-container {-->
<!--  &#45;&#45;primary-color: #409eff;-->
<!--  &#45;&#45;success-color: #67c23a;-->
<!--  &#45;&#45;warning-color: #e6a23c;-->
<!--  &#45;&#45;danger-color: #f56c6c;-->
<!--  &#45;&#45;info-color: #909399;-->
<!--  &#45;&#45;border-radius: 8px;-->
<!--  &#45;&#45;box-shadow: 0 3px 10px rgba(0, 0, 0, 0.06);-->
<!--  margin-left: 100px;-->
<!--}-->

<!--/* ================= 查询表单区域 ================= */-->
<!--.query-form {-->
<!--  padding: 20px;-->
<!--  background: rgba(255, 255, 255, 0.95);-->
<!--  border-radius: 16px;-->
<!--  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);-->
<!--  margin-bottom: 24px;-->
<!--  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);-->
<!--}-->

<!--/* 表单项通用样式 */-->
<!--.query-form .el-form-item {-->
<!--  margin-bottom: 16px;-->
<!--  margin-right: 20px;-->
<!--}-->

<!--.query-form .el-form-item__label {-->
<!--  font-size: 14px;-->
<!--  color: #2d3540;-->
<!--  font-weight: 600;-->
<!--  letter-spacing: 0.3px;-->
<!--}-->

<!--/* 输入控件 */-->
<!--.query-form .el-input__inner,-->
<!--.query-form .el-select .el-input__inner {-->
<!--  height: 40px;-->
<!--  width: 220px;-->
<!--  border-radius: 8px;-->
<!--  border: 1px solid rgba(228, 231, 237, 0.6);-->
<!--  background: rgba(255, 255, 255, 0.8);-->
<!--  transition: all 0.3s ease;-->
<!--  box-shadow: inset 0 1px 2px rgba(255, 255, 255, 0.3), 0 2px 4px rgba(0, 0, 0, 0.05);-->
<!--}-->

<!--.query-form .el-input__inner:focus,-->
<!--.query-form .el-select .el-input__inner:focus {-->
<!--  border-color: var(&#45;&#45;primary-color);-->
<!--  box-shadow: 0 0 0 2px rgba(var(&#45;&#45;primary-color), 0.2), inset 0 1px 2px rgba(255, 255, 255, 0.4);-->
<!--}-->

<!--/* 按钮组 */-->
<!--.query-form .el-button-group {-->
<!--  gap: 12px; /* 按钮之间的间距 */-->
<!--  margin-left: 8px;-->
<!--}-->
<!--.query-form .el-button-group .el-button:not(:last-child) {-->
<!--  margin-right: 12px; /* 除最后一个按钮外添加右边距 */-->
<!--}-->
<!--.query-form .el-button {-->
<!--  border: none;-->
<!--  padding: 8px 20px;-->
<!--  border-radius: 8px;-->
<!--  transition: all 0.3s ease;-->
<!--}-->

<!--.query-form .el-button&#45;&#45;primary {-->
<!--  background: linear-gradient(145deg, #409eff, #3375ff);-->
<!--  box-shadow: 0 3px 8px rgba(64, 158, 255, 0.3);-->
<!--}-->

<!--/* ================= 表格容器样式 ================= */-->
<!--.table-container {-->
<!--  border-radius: 12px;-->
<!--  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);-->
<!--  transition: all 0.3s ease;-->
<!--}-->

<!--.table-container:hover {-->
<!--  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);-->
<!--}-->

<!--.table-header {-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--  align-items: center;-->
<!--  padding: 16px 24px;-->
<!--  background: linear-gradient(145deg, #f8fafc, #f0f4f8);-->
<!--}-->

<!--.table-title {-->
<!--  font-size: 18px;-->
<!--  font-weight: 600;-->
<!--  color: #2d3540;-->
<!--  letter-spacing: 0.5px;-->
<!--}-->

<!--/* ================= 增强表格样式 ================= */-->
<!--.enhanced-table {-->
<!--  &#45;&#45;table-border-color: rgba(228, 231, 237, 0.6);-->
<!--  &#45;&#45;header-bg: linear-gradient(165deg, #f8fafc 0%, #f1f5f9 100%);-->
<!--  &#45;&#45;hover-bg: rgba(241, 245, 249, 0.6);-->
<!--  font-family: 'Segoe UI', system-ui, sans-serif;-->
<!--  border-radius: var(&#45;&#45;border-radius);-->
<!--  box-shadow: var(&#45;&#45;box-shadow);-->
<!--  overflow: hidden;-->
<!--}-->

<!--/* 表头样式 */-->
<!--.enhanced-table ::v-deep .el-table__header th {-->
<!--  background: var(&#45;&#45;header-bg) !important;-->
<!--  backdrop-filter: blur(8px);-->
<!--  border-bottom: 1px solid var(&#45;&#45;table-border-color) !important;-->
<!--  font-weight: 600;-->
<!--  letter-spacing: 0.3px;-->
<!--  text-transform: capitalize;-->
<!--  position: relative;-->
<!--}-->

<!--.enhanced-table ::v-deep .el-table__header th::after {-->
<!--  content: '';-->
<!--  position: absolute;-->
<!--  left: 0;-->
<!--  right: 0;-->
<!--  bottom: -1px;-->
<!--  height: 1px;-->
<!--  background: linear-gradient(90deg,-->
<!--  rgba(255,255,255,0) 0%,-->
<!--  rgba(224, 232, 255, 0.6) 50%,-->
<!--  rgba(255,255,255,0) 100%-->
<!--  );-->
<!--}-->

<!--/* 表体样式 */-->
<!--.enhanced-table ::v-deep .el-table__body td {-->
<!--  transition:-->
<!--    transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),-->
<!--    box-shadow 0.3s ease;-->
<!--  padding: 14px 16px;-->
<!--  border-bottom: 1px solid rgba(228, 231, 237, 0.5) !important;-->
<!--}-->

<!--/* 行交互效果 */-->
<!--.enhanced-table ::v-deep .el-table__body tr:hover td {-->
<!--  background: var(&#45;&#45;hover-bg) !important;-->
<!--  transform: perspective(100px) translateZ(2px);-->
<!--  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);-->
<!--}-->

<!--/* 斑马纹效果 */-->
<!--.enhanced-table ::v-deep .el-table__row.striped-row td {-->
<!--  background: linear-gradient(-->
<!--    to right,-->
<!--    rgba(251, 253, 255, 0.6),-->
<!--    rgba(245, 247, 250, 0.6)-->
<!--  );-->
<!--}-->

<!--/* ================= 特殊元素样式 ================= */-->
<!--.credit-badge {-->
<!--  display: inline-block;-->
<!--  min-width: 28px;-->
<!--  padding: 4px 8px;-->
<!--  border-radius: 14px;-->
<!--  background: rgba(var(&#45;&#45;primary-color), 0.1);-->
<!--  color: var(&#45;&#45;primary-color);-->
<!--  font-weight: 600;-->
<!--  font-family: 'Roboto Mono', monospace;-->
<!--}-->

<!--.type-tag {-->
<!--  border-radius: 6px;-->
<!--  padding: 4px 10px;-->
<!--  border-width: 1px;-->
<!--  font-weight: 500;-->
<!--}-->

<!--.type-tag.必修 {-->
<!--  background: rgba(101, 163, 13, 0.1);-->
<!--  border-color: rgba(101, 163, 13, 0.3);-->
<!--  color: #3f6212;-->
<!--}-->

<!--.type-tag.选修 {-->
<!--  background: rgba(245, 158, 11, 0.1);-->
<!--  border-color: rgba(245, 158, 11, 0.3);-->
<!--  color: #854d0e;-->
<!--}-->

<!--/* ================= 响应式布局 ================= */-->
<!--@media (max-width: 1200px) {-->
<!--  .query-form .el-form-item {-->
<!--    width: calc(50% - 12px);-->
<!--  }-->

<!--  .query-form .el-select {-->
<!--    width: 100%;-->
<!--  }-->
<!--}-->

<!--@media (max-width: 768px) {-->
<!--  .query-form .el-form-item {-->
<!--    width: 100%;-->
<!--    margin-right: 0;-->
<!--  }-->

<!--  .action-bar {-->
<!--    flex-wrap: wrap;-->
<!--  }-->

<!--  .action-btn {-->
<!--    flex: 1 1 45%;-->
<!--    margin-bottom: 8px;-->
<!--  }-->

<!--  .enhanced-table {-->
<!--    .course-code,-->
<!--    .academy {-->
<!--      display: none;-->
<!--    }-->
<!--  }-->
<!--}-->

<!--/* ================= 对话框样式 ================= */-->
<!--/* 对话框基础美化 */-->
<!--::v-deep .el-dialog {-->
<!--  border-radius: 12px;-->
<!--  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);-->
<!--}-->

<!--/* 标题栏样式 */-->
<!--::v-deep .el-dialog__header {-->
<!--  background: #f8fafc;-->
<!--  border-bottom: 1px solid #e4e7ed;-->
<!--  padding: 16px 24px;-->
<!--}-->

<!--::v-deep .el-dialog__title {-->
<!--  font-size: 16px;-->
<!--  color: #2d3540;-->
<!--  font-weight: 600;-->
<!--}-->

<!--/* 表单内容区域 */-->
<!--::v-deep .el-dialog__body {-->
<!--  padding: 20px 24px;-->
<!--}-->

<!--/* 表单项优化 */-->
<!--::v-deep .el-form-item {-->
<!--  margin-bottom: 18px;-->
<!--}-->

<!--::v-deep .el-form-item__label {-->
<!--  color: #5a6376;-->
<!--  font-weight: 500;-->
<!--}-->

<!--/* 输入控件样式 */-->
<!--::v-deep .el-input__inner,-->
<!--::v-deep .el-select {-->
<!--  border-radius: 8px;-->
<!--  transition: border-color 0.3s ease;-->
<!--}-->

<!--::v-deep .el-input__inner:focus {-->
<!--  border-color: #409EFF;-->
<!--  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);-->
<!--}-->

<!--/* 底部按钮区域 */-->
<!--::v-deep .el-dialog__footer {-->
<!--  padding: 16px 24px;-->
<!--  background: #f8fafc;-->
<!--  border-top: 1px solid #e4e7ed;-->
<!--}-->

<!--::v-deep .el-button {-->
<!--  border-radius: 8px;-->
<!--  padding: 10px 20px;-->
<!--}-->

<!--/* 响应式调整 */-->
<!--@media (max-width: 768px) {-->
<!--  ::v-deep .el-dialog {-->
<!--    width: 90% !important;-->
<!--  }-->
<!--}-->

<!--/* ================= 行交互效果 ================= */-->
<!--.enhanced-table::v-deep .el-table__row {-->
<!--  transition: all 0.3s ease;-->
<!--  cursor: pointer;-->
<!--}-->

<!--.enhanced-table::v-deep .el-table__row&#45;&#45;striped {-->
<!--  background: linear-gradient(-->
<!--    to right,-->
<!--    rgba(251, 253, 255, 0.6),-->
<!--    rgba(245, 247, 250, 0.6)-->
<!--  );-->
<!--}-->

<!--/* ================= 导入导出相关 ================= */-->
<!--.upload-tip {-->
<!--  font-size: 12px;-->
<!--  color: var(&#45;&#45;info-color);-->
<!--  margin-top: 8px;-->
<!--}-->

<!--.import-template-link {-->
<!--  font-size: 12px;-->
<!--  margin-left: 8px;-->
<!--}-->


<!--/* 统一按钮基础样式 */-->
<!--.action-btn,-->
<!--.query-form .el-button {-->
<!--  min-width: 100px;-->
<!--  height: 36px;-->
<!--  padding: 0 20px;-->
<!--  border-radius: 8px;-->
<!--  border: none;-->
<!--  font-weight: 500;-->
<!--  letter-spacing: 0.5px;-->
<!--  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);-->
<!--  display: inline-flex;-->
<!--  align-items: center;-->
<!--  justify-content: center;-->
<!--  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);-->
<!--}-->

<!--/* 图标间距统一 */-->
<!--.action-btn i,-->
<!--.query-form .el-button i {-->
<!--  margin-right: 8px;-->
<!--  font-size: 16px;-->
<!--}-->

<!--/* 查询/导出按钮 - 主色调 */-->
<!--.el-button&#45;&#45;primary,-->
<!--.export-btn {-->
<!--  background: linear-gradient(135deg, #409EFF 0%, #3375ff 100%);-->
<!--  color: white !important;-->
<!--}-->

<!--/* 导入按钮 - 辅助色 */-->
<!--.import-btn {-->
<!--  background: linear-gradient(135deg, #67C23A 0%, #5BAF2D 100%);-->
<!--  color: white !important;-->
<!--}-->

<!--/* 重置按钮 - 中性色 */-->
<!--.query-form .el-button:not(.el-button&#45;&#45;primary) {-->
<!--  background: linear-gradient(135deg, #f0f2f5 0%, #e5e9ef 100%);-->
<!--  color: #606266;-->
<!--  border: 1px solid #e4e7ed;-->
<!--}-->

<!--/* 悬停效果 */-->
<!--.action-btn:hover,-->
<!--.query-form .el-button:hover {-->
<!--  transform: translateY(-2px);-->
<!--  box-shadow: 0 5px 12px rgba(0, 0, 0, 0.15);-->
<!--}-->

<!--/* 点击效果 */-->
<!--.action-btn:active,-->
<!--.query-form .el-button:active {-->
<!--  transform: translateY(0);-->
<!--  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);-->
<!--}-->

<!--/* 禁用状态 */-->
<!--.action-btn.is-disabled,-->
<!--.query-form .el-button.is-disabled {-->
<!--  opacity: 0.7;-->
<!--  transform: none !important;-->
<!--  box-shadow: none !important;-->
<!--}-->

<!--/* 按钮组间距调整 */-->
<!--.el-button-group {-->
<!--  gap: 12px;-->
<!--}-->

<!--/* 导入导出按钮特殊间距 */-->
<!--.action-bar {-->
<!--  gap: 12px;-->
<!--}-->
<!--/* 新增模式选择样式 */-->
<!--.mode-radio {-->
<!--  width: 100%;-->
<!--  margin: 10px 0;-->
<!--  padding: 15px;-->
<!--  transition: all 0.3s ease;-->
<!--}-->

<!--.mode-option {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  margin-left: 10px;-->
<!--}-->

<!--.mode-title {-->
<!--  font-weight: 600;-->
<!--  color: #2d3540;-->
<!--  margin-bottom: 5px;-->
<!--}-->

<!--.mode-desc {-->
<!--  font-size: 12px;-->
<!--  color: #909399;-->
<!--  line-height: 1.4;-->
<!--}-->

<!--::v-deep .el-radio__input {-->
<!--  align-self: flex-start;-->
<!--  margin-top: 8px;-->
<!--}-->

<!--::v-deep .el-radio.is-bordered.is-checked {-->
<!--  border-color: #409EFF;-->
<!--  background: rgba(64, 158, 255, 0.05);-->
<!--}-->
<!--</style>-->
