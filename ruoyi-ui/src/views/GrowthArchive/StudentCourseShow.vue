<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form
      class="query-form"
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
    >
      <el-form-item label="课程代码" prop="courseCode">
        <el-input
          v-model="queryParams.courseCode"
          placeholder="请输入课程代码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程名称" prop="courseName">
        <el-input
          v-model="queryParams.courseName"
          placeholder="请输入课程名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开课院系" prop="academy">
        <el-select
          v-model="queryParams.academy"
          placeholder="请选择开课院系"
          clearable
          filterable
          style="width: 160px"
        >
          <el-option
            v-for="item in 7"
            :key="item"
            :label="`书院${item}`"
            :value="`书院${item}`"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="授课教师" prop="teacherName">
        <el-input
          v-model="queryParams.teacherName"
          placeholder="请输入授课教师"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button-group>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-button-group>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-card class="table-container">
      <div slot="header" class="table-header">
        <span class="table-title">课程数据列表</span>
      </div>

      <el-table
        v-loading="loading"
        :data="courseList"
        class="enhanced-table"
        style="width: 100%"
      >
        <el-table-column label="序号" width="80" align="center" class-name="index-column">
          <template slot-scope="scope">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="课程代码" prop="courseCode" align="center" min-width="160" />
        <el-table-column label="课程名称" prop="courseName" align="center" min-width="200" />
        <el-table-column label="开课院系" prop="academy" align="center" min-width="120"/>
        <el-table-column label="授课教师" prop="teacherName" align="center" min-width="140" />
        <el-table-column label="学分值" prop="credit" align="center" min-width="80">
          <template slot-scope="{row}">
            <span class="credit-badge">{{ row.credit }}</span>
          </template>
        </el-table-column>
        <el-table-column label="总课时数" prop="courseHours" align="center" min-width="90">
          <template slot-scope="{row}">
            <span class="credit-badge">{{ row.courseHours }}</span>
          </template>
        </el-table-column>
        <el-table-column label="课程容量" prop="courseCapacity" align="center" min-width="90" >
          <template slot-scope="{row}">
            <span class="credit-badge">{{ row.enrolledStudent }}/{{ row.courseCapacity }}</span>
          </template>
        </el-table-column>
        <el-table-column label="课程类型" align="center" min-width="120">
          <template slot-scope="{row}">
            <el-tag
              :type="row.courseCategory === '必修' ? 'success' : 'warning'"
              size="small"
              effect="light"
              class="type-tag"
            >
              {{ row.courseCategory }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="success"
              plain
              :disabled="scope.row.isEnrolled || scope.row.enrolledStudent >= scope.row.courseCapacity"
              @click="handleEnroll(scope.row)"
            >
              {{ scope.row.isEnrolled ? '已选' : '选课' }}
            </el-button>

            <el-button
              size="mini"
              type="danger"
              plain
              :disabled="!scope.row.isEnrolled"
              @click="handleDrop(scope.row)"
            >
              退课
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
        class="custom-pagination"
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
    async getList() {
      this.loading = true;
      try {
        const res = await listCourse(this.queryParams);
        this.courseList = res.rows.map(course => ({
          ...course,
          courseCode: String(course.courseCode).trim(),
          // enrolledStudent: Number(course.enrolledStudent) || 0
        }));
        this.total = res.total;
        this.markEnrolledCourses();
      } catch (error) {
        console.error("课程加载失败:", error);
        this.$message.error("课程数据加载失败");
      } finally {
        this.loading = false;
      }
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

<style scoped>
/* ================= 全局样式变量 ================= */
.app-container {
  --primary-color: #409eff;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --info-color: #909399;
  --border-radius: 8px;
  --box-shadow: 0 3px 10px rgba(0, 0, 0, 0.06);
  margin-left: 100px;
}

/* ================= 查询表单区域 ================= */
.query-form {
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 表单项通用样式 */
.query-form .el-form-item {
  margin-bottom: 16px;
  margin-right: 20px;
}

.query-form .el-form-item__label {
  font-size: 14px;
  color: #2d3540;
  font-weight: 600;
  letter-spacing: 0.3px;
}

/* 输入控件 */
.query-form .el-input__inner,
.query-form .el-select .el-input__inner {
  height: 40px;
  width: 220px;
  border-radius: 8px;
  border: 1px solid rgba(228, 231, 237, 0.6);
  background: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  box-shadow: inset 0 1px 2px rgba(255, 255, 255, 0.3), 0 2px 4px rgba(0, 0, 0, 0.05);
}

.query-form .el-input__inner:focus,
.query-form .el-select .el-input__inner:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(var(--primary-color), 0.2), inset 0 1px 2px rgba(255, 255, 255, 0.4);
}

/* 按钮组 */
.query-form .el-button-group {
  gap: 12px; /* 按钮之间的间距 */
  margin-left: 8px;
}
.query-form .el-button-group .el-button:not(:last-child) {
  margin-right: 12px; /* 除最后一个按钮外添加右边距 */
}
.query-form .el-button {
  border: none;
  padding: 8px 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.query-form .el-button--primary {
  background: linear-gradient(145deg, #409eff, #3375ff);
  box-shadow: 0 3px 8px rgba(64, 158, 255, 0.3);
}

/* ================= 表格容器样式 ================= */
.table-container {
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.table-container:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(145deg, #f8fafc, #f0f4f8);
}

.table-title {
  font-size: 18px;
  font-weight: 600;
  color: #2d3540;
  letter-spacing: 0.5px;
}

/* ================= 增强表格样式 ================= */
.enhanced-table {
  --table-border-color: rgba(228, 231, 237, 0.6);
  --header-bg: linear-gradient(165deg, #f8fafc 0%, #f1f5f9 100%);
  --hover-bg: rgba(241, 245, 249, 0.6);
  font-family: 'Segoe UI', system-ui, sans-serif;
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow);
  overflow: hidden;
}

/* 表头样式 */
.enhanced-table ::v-deep .el-table__header th {
  background: var(--header-bg) !important;
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--table-border-color) !important;
  font-weight: 600;
  letter-spacing: 0.3px;
  text-transform: capitalize;
  position: relative;
}

.enhanced-table ::v-deep .el-table__header th::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 1px;
  background: linear-gradient(90deg,
  rgba(255,255,255,0) 0%,
  rgba(224, 232, 255, 0.6) 50%,
  rgba(255,255,255,0) 100%
  );
}

/* 表体样式 */
.enhanced-table ::v-deep .el-table__body td {
  transition:
    transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.3s ease;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(228, 231, 237, 0.5) !important;
}

/* 行交互效果 */
.enhanced-table ::v-deep .el-table__body tr:hover td {
  background: var(--hover-bg) !important;
  transform: perspective(100px) translateZ(2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* 斑马纹效果 */
.enhanced-table ::v-deep .el-table__row.striped-row td {
  background: linear-gradient(
    to right,
    rgba(251, 253, 255, 0.6),
    rgba(245, 247, 250, 0.6)
  );
}

/* ================= 特殊元素样式 ================= */
.credit-badge {
  display: inline-block;
  min-width: 28px;
  padding: 4px 8px;
  border-radius: 14px;
  background: rgba(var(--primary-color), 0.1);
  color: var(--primary-color);
  font-weight: 600;
  font-family: 'Roboto Mono', monospace;
}

.type-tag {
  border-radius: 6px;
  padding: 4px 10px;
  border-width: 1px;
  font-weight: 500;
}

.type-tag.必修 {
  background: rgba(101, 163, 13, 0.1);
  border-color: rgba(101, 163, 13, 0.3);
  color: #3f6212;
}

.type-tag.选修 {
  background: rgba(245, 158, 11, 0.1);
  border-color: rgba(245, 158, 11, 0.3);
  color: #854d0e;
}

/* ================= 响应式布局 ================= */
@media (max-width: 1200px) {
  .query-form .el-form-item {
    width: calc(50% - 12px);
  }

  .query-form .el-select {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .query-form .el-form-item {
    width: 100%;
    margin-right: 0;
  }

  .action-bar {
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1 1 45%;
    margin-bottom: 8px;
  }

  .enhanced-table {
    .course-code,
    .academy {
      display: none;
    }
  }
}

/* ================= 对话框样式 ================= */
/* 对话框基础美化 */
::v-deep .el-dialog {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

/* 标题栏样式 */
::v-deep .el-dialog__header {
  background: #f8fafc;
  border-bottom: 1px solid #e4e7ed;
  padding: 16px 24px;
}

::v-deep .el-dialog__title {
  font-size: 16px;
  color: #2d3540;
  font-weight: 600;
}

/* 表单内容区域 */
::v-deep .el-dialog__body {
  padding: 20px 24px;
}

/* 表单项优化 */
::v-deep .el-form-item {
  margin-bottom: 18px;
}

::v-deep .el-form-item__label {
  color: #5a6376;
  font-weight: 500;
}

/* 输入控件样式 */
::v-deep .el-input__inner,
::v-deep .el-select {
  border-radius: 8px;
  transition: border-color 0.3s ease;
}

::v-deep .el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

/* 底部按钮区域 */
::v-deep .el-dialog__footer {
  padding: 16px 24px;
  background: #f8fafc;
  border-top: 1px solid #e4e7ed;
}

::v-deep .el-button {
  border-radius: 8px;
  padding: 10px 20px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  ::v-deep .el-dialog {
    width: 90% !important;
  }
}

/* ================= 行交互效果 ================= */
.enhanced-table::v-deep .el-table__row {
  transition: all 0.3s ease;
  cursor: pointer;
}

.enhanced-table::v-deep .el-table__row--striped {
  background: linear-gradient(
    to right,
    rgba(251, 253, 255, 0.6),
    rgba(245, 247, 250, 0.6)
  );
}

/* ================= 导入导出相关 ================= */
.upload-tip {
  font-size: 12px;
  color: var(--info-color);
  margin-top: 8px;
}

.import-template-link {
  font-size: 12px;
  margin-left: 8px;
}


/* 统一按钮基础样式 */
.action-btn,
.query-form .el-button {
  min-width: 100px;
  height: 36px;
  padding: 0 20px;
  border-radius: 8px;
  border: none;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
}

/* 图标间距统一 */
.action-btn i,
.query-form .el-button i {
  margin-right: 8px;
  font-size: 16px;
}

/* 查询/导出按钮 - 主色调 */
.el-button--primary,
.export-btn {
  background: linear-gradient(135deg, #409EFF 0%, #3375ff 100%);
  color: white !important;
}

/* 导入按钮 - 辅助色 */
.import-btn {
  background: linear-gradient(135deg, #67C23A 0%, #5BAF2D 100%);
  color: white !important;
}

/* 重置按钮 - 中性色 */
.query-form .el-button:not(.el-button--primary) {
  background: linear-gradient(135deg, #f0f2f5 0%, #e5e9ef 100%);
  color: #606266;
  border: 1px solid #e4e7ed;
}

/* 悬停效果 */
.action-btn:hover,
.query-form .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(0, 0, 0, 0.15);
}

/* 点击效果 */
.action-btn:active,
.query-form .el-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

/* 禁用状态 */
.action-btn.is-disabled,
.query-form .el-button.is-disabled {
  opacity: 0.7;
  transform: none !important;
  box-shadow: none !important;
}

/* 按钮组间距调整 */
.el-button-group {
  gap: 12px;
}

/* 导入导出按钮特殊间距 */
.action-bar {
  gap: 12px;
}
</style>
