<template>
  <div class="app-container">
    <el-form class="query-form" :model="queryParams" ref="queryForm" size="small" :inline="true">
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
<!--      <el-form-item label="学分值" prop="credit">-->
<!--        <el-input-->
<!--          v-model="queryParams.credit"-->
<!--          placeholder="请输入学分值"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item label="课程类型" prop="courseCategory">
        <el-select
          v-model="queryParams.courseCategory"
          placeholder="请选择课程类型"
          clearable
          filterable
          style="width: 140px"
        >
          <el-option label="必修" value="必修" />
          <el-option label="选修" value="选修" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button-group>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-button-group>
      </el-form-item>
    </el-form>

    <el-card class="table-container">
      <div slot="header" class="table-header">
        <span class="table-title">课程数据列表</span>
        <div class="action-bar">
          <el-button
            type="info"
            icon="el-icon-upload2"
            class="action-btn import-btn"
            @click="handleImport"
            v-hasPermi="['system:course:import']"
          >
            课程导入
          </el-button>
          <el-button
            type="warning"
            icon="el-icon-download"
            class="action-btn export-btn"
            @click="handleExport"
            v-hasPermi="['system:course:export']"
          >
            数据导出
          </el-button>
<!--          <el-button-->
<!--            type="primary"-->
<!--            plain-->
<!--            icon="el-icon-plus"-->
<!--            class="action-btn"-->
<!--            @click="handleAdd"-->
<!--            v-hasPermi="['system:course:add']"-->
<!--          >新增</el-button>-->
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="courseList"
        class="enhanced-table"
        :header-cell-style="headerStyle"
        :row-class-name="tableRowClassName"
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column label="序号" width="80" align="center" class-name="index-column">
          <template slot-scope="scope">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="课程代码" align="center" prop="courseCode" min-width="160" />
        <el-table-column label="课程名称" align="center" prop="courseName" min-width="150" />
        <el-table-column label="开课院系" align="center" prop="academy" min-width="120"/>
        <el-table-column label="授课教师" align="center" prop="teacherName" min-width="140" />
        <el-table-column label="学分值" align="center" prop="credit" min-width="80">
          <template slot-scope="{row}">
            <span class="credit-badge">{{ row.credit }}</span>
          </template>
        </el-table-column>
        <el-table-column label="总课时数" align="center" prop="courseHours" min-width="90">
          <template slot-scope="{row}">
            <span class="credit-badge">{{ row.courseHours }}</span>
          </template>
        </el-table-column>
        <el-table-column label="课程容量" align="center" prop="courseCapacity" min-width="90" >
        <template slot-scope="{row}">
          <span class="credit-badge">{{ row.courseCapacity }}</span>
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
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              class="action-icon edit-icon"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:course:edit']"
            />
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              class="action-icon delete-icon"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:course:remove']"
            />
            <el-button
              v-if="scope.row.courseCategory === '特色课'"
              size="mini"
              type="text"
              icon="el-icon-user"
              class="action-icon view-icon"
              @click="handleViewStudents(scope.row)">
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
        class="custom-pagination"
      />
    </el-card>

    <!-- 学生选课信息对话框 -->
    <el-dialog
      title="选课学生列表"
      :visible.sync="dialogVisibleStudents"
      width="60%"
      append-to-body
      class="student-dialog">
      <el-table
        :data="selectedStudents"
        border
        v-loading="studentLoading"
        class="enhanced-student-table"
        :header-cell-style="{
      'background': '#f5f7fa',
      'color': '#2d3540',
      'font-weight': '600'
    }">
        <el-table-column prop="studentId" label="学号" width="180">
          <template slot-scope="{row}">
            <span class="student-id">{{ row.studentId }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="studentName" label="姓名">
          <template slot-scope="{row}">
            <span class="student-name">{{ row.studentName }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="academy" label="所属院系">
          <template slot-scope="{row}">
            <el-tag type="info" size="mini">{{ row.academy }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enrollmentTime" label="修读类型" width="180">
          <template slot-scope="{row}">
            <div class="enrollment-time">
            活动置换
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="enrollmentTime" label="选课时间" width="180">
          <template slot-scope="{row}">
            <div class="enrollment-time">
              <i class="el-icon-time"></i>
              {{ parseTime(row.enrollmentTime) }}
            </div>
          </template>
        </el-table-column>

      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          icon="el-icon-download"
          @click="handleExportStudents"
          class="export-btn"
          v-hasPermi="['system:course:export']">
          导出名单
        </el-button>
      </div>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px">
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
        <div class="el-upload__tip" slot="tip">
          <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据
          <el-link type="info" style="font-size:12px" @click="importTemplate">下载模板</el-link>
        </div>
        <div class="el-upload__tip" style="color:red" slot="tip">提示：仅允许导入“xls”或“xlsx”格式文件！</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改课程信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程唯一编号" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程全称" />
        </el-form-item>
        <el-form-item label="开课院系" prop="academy">
          <el-select
            v-model="form.academy"
            placeholder="请选择开课院系"
            clearable
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
          <el-input v-model="form.teacherName" placeholder="请输入授课教师" />
        </el-form-item>
        <el-form-item label="学分值" prop="credit">
          <el-input-number
            v-model="form.credit"
            :min="1"
            :step="1"
            :precision="0"
            @change="calculateCourseHours"
            placeholder="请输入学分值"
            controls-position="right"
            style="width: 30%"
          />
        </el-form-item>
        <el-form-item label="课程类型" prop="courseCategory">
          <el-select
            v-model="form.courseCategory"
            placeholder="请选择课程类型"
            clearable
          >
            <el-option
              label="必修"
              value="必修"
            />
            <el-option
              label="选修"
              value="选修"
            />
          </el-select>
        </el-form-item>
        <input type="hidden" v-model="form.courseHours">

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCourse, getCourse, delCourse, addCourse, updateCourse } from "@/api/system/course";
import { getToken } from "@/utils/auth";
import {listEnrollments, listEnrollments2} from "../../api/system/enrollment";

export default {
  name: "Course",
  data() {
    return {
      // 新增状态
      dialogVisibleStudents: false,
      selectedStudents: [],
      studentLoading: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 课程信息表格数据
      courseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseCode: null,
        courseName: null,
        credit: null,
        courseCategory: null,
        teacherName: null,
        academy: null,
        courseHours: null,
        courseCapacity:null
      },
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/system/course/importData"
      },

      // 表单参数
      form: {},
      // 表单校验
      rules: {
        courseCode: [
          { required: true, message: "课程代码不能为空", trigger: "blur" }
        ],
        courseName: [
          { required: true, message: "课程名称不能为空", trigger: "blur" }
        ],
        credit: [
          { required: true, message: "学分值不能为空", trigger: "blur" }
        ],
        courseCategory: [
          { required: true, message: "请选择课程类型", trigger: "change" }
        ],
        academy: [
          { required: true, message: "请选择开课书院", trigger: "change" }
        ],
        teacherName: [
          { required: true, message: "授课教师不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 导出选课学生
    handleExportStudents() {
      if (this.selectedStudents.length === 0) {
        this.$message.warning("没有可导出的数据");
        return;
      }

      const courseCode = this.selectedStudents[0]?.courseCode;
      if (!courseCode) {
        this.$message.error("缺少课程代码参数");
        return;
      }

      this.download('/system/enrollments/export', {
        courseCode: courseCode
      }, `选课名单_${courseCode}_${this.parseTime(new Date(), '{y}{m}{d}')}.xlsx`)

      this.dialogVisibleStudents = false; // 导出后自动关闭对话框
    },
    // 查看选课学生
    async handleViewStudents(row) {
      this.studentLoading = true;
      try {
        const res = await listEnrollments2({
          courseCode: row.courseCode
        });

        if (res.rows && res.rows.length) {
          this.selectedStudents = res.rows;
          this.dialogVisibleStudents = true;
        } else {
          this.$modal.msgInfo("当前课程暂无学生选课");
        }
      } catch (e) {
        console.error("获取选课数据失败", e);
      } finally {
        this.studentLoading = false;
      }
    },
    calculateCourseHours() {
      if (typeof this.form.credit === 'number') {
        this.form.courseHours = this.form.credit * 12
      } else {
        this.form.courseHours = null
      }
    },
    /** 查询课程信息列表 */
    getList() {
      this.loading = true;
      listCourse(this.queryParams).then(response => {
        this.courseList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        courseId: null,
        courseCode: null,
        courseName: null,
        credit: null,
        courseCategory: null,
        teacherName: null,
        academy: null,
        courseHours: null,
        courseCapacity:null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.courseId)
      this.codes = selection.map(item => item.courseCode)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加课程信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const courseId = row.courseId || this.ids
      getCourse(courseId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改课程信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.courseId != null) {
            updateCourse(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCourse(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const courseIds = row.courseId || this.ids;
      const courseCodes = row.courseCode || this.codes;
      this.$modal.confirm('是否确认删除课程代码为"' + courseCodes + '"的数据项？').then(function() {
        return delCourse(courseIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/course/export', {
        ...this.queryParams
      }, `course_${new Date().getTime()}.xlsx`)
    },

    /** 导出模板操作 */
    importTemplate() {
  fetch(process.env.VUE_APP_BASE_API+'/system/course/importTemplate', {
    mode: 'cors', // 明确跨域模式
    headers: new Headers({'Content-Type': 'application/json'})
  })
    .then(response => response.blob())
    .then(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'template.xlsx'; // 指定下载文件名
      a.click();
      window.URL.revokeObjectURL(url);
    });
},
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "用户导入";
      this.upload.open = true;
    },

// 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
// 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
// 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    tableRowClassName({ rowIndex }) {
      return rowIndex % 2 === 0 ? 'striped-row' : '';
    },

    // 自定义表头样式
    headerStyle() {
      return {
        color: '#2d3540',
        fontWeight: 600,
        fontSize: '14px'
      }
    },
    // 行点击处理
    handleRowClick(row) {
      this.$emit('row-click', row)
    }
  }
};
</script>

<style scoped>
/* 对话框整体样式 */
.student-dialog {
  ::v-deep .el-dialog {
    border-radius: 8px;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);

    &__header {
      border-bottom: 1px solid #ebeef5;
      padding: 16px 24px;

      .el-dialog__title {
        font-size: 16px;
        color: #2d3540;
        font-weight: 600;
      }
    }
  }
}

/* 表格美化 */
.enhanced-student-table {
  ::v-deep .el-table {
    border-radius: 6px;

    th {
      background: #f5f7fa !important;
      font-size: 14px;
    }

    td {
      padding: 12px 0;
      font-size: 14px;
    }

    .cell {
      padding: 0 16px;
    }
  }
}

/* 学号样式 */
.student-id {
  font-family: Monaco, Consolas, monospace;
  color: #409EFF;
  font-weight: 500;
}

/* 姓名样式 */
.student-name {
  font-weight: 500;
  color: #2d3540;
}

/* 选课时间 */
.enrollment-time {
  display: flex;
  align-items: center;
  color: #606266;

  .el-icon-time {
    margin-right: 6px;
    color: #909399;
    font-size: 14px;
  }
}

/* 关闭按钮 */
.close-btn {
  padding: 9px 24px;
  border-radius: 6px;
  background-color: #409EFF;
  color: white;
  transition: all 0.2s;

  &:hover {
    background-color: #66b1ff;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  }
}

/* 加载状态 */
::v-deep .el-loading-mask {
  background-color: rgba(255, 255, 255, 0.8);

  .el-loading-spinner i {
    color: #409EFF;
  }

  .el-loading-text {
    color: #909399;
    margin-top: 8px;
  }
}
.app-container {
  /* 基础字体放大 */
  font-size: 1.25rem; /* 默认16px → 20px */

  /* 调整ElementUI组件字体 */
  ::v-deep {
    /* 表单项标签 */
    .el-form-item__label {
      font-size: 1.125rem !important; /* 18px */
    }

    /* 输入框文字 */
    .el-input__inner,
    .el-select .el-input__inner {
      font-size: 1rem; /* 16px */
    }

    /* 表格文字 */
    .el-table {
      font-size: 1.125rem; /* 18px */
      th > .cell { font-size: 1.125rem; }
    }

    /* 按钮文字 */
    .el-button {
      font-size: 1.125rem;
      [class*=" el-icon-"], [class^=el-icon-] {
        font-size: 1.25rem;
      }
    }

    /* 对话框标题 */
    .el-dialog__title {
      font-size: 1.5rem !important; /* 24px */
    }
  }
}

/* 调整特殊元素 */
.table-title {
  font-size: 1.75rem !important; /* 28px */
}

.credit-badge {
  font-size: 1.125rem; /* 18px */
}

/* 展开面板文字调整 */
.condition-panel {
  font-size: 1.125rem;
  .panel-header span {
    font-size: 1.25rem;
  }
}
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
