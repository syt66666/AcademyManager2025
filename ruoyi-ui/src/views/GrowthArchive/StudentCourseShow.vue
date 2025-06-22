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
    async confirmEnroll() {
      if (!this.selectedStudyMode) {
        this.$message.warning('请选择修读方式');
        return;
      }
      try {
        await this.handleEnroll(this.currentCourse, this.selectedStudyMode);
      } finally {
        this.dialogVisible = false; // 无论成功与否都关闭对话框
      }
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
          '参加3次以上艺术工作坊',
          '完成艺术作品创作',
          '通过专家评审'
        ],
        'CS101': [
          '完成至少3个科创项目实践',
          '获得校级以上竞赛奖项',
          '提交5000字实践报告'
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
console.log(this.selectedStudyMode)
        // 提交请求
        await addEnrollments({
          studentId: store.state.user.name,
          courseCode: row.courseCode,
          enrollmentType: this.selectedStudyMode==='standard'?0:1,
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
