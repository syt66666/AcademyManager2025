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
      <el-form-item label="学分值" prop="credit">
        <el-input
          v-model="queryParams.credit"
          placeholder="请输入学分值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
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
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            class="action-btn"
            @click="handleAdd"
            v-hasPermi="['system:course:add']"
          >新增</el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="courseList"
        class="enhanced-table"
        :header-cell-style="headerStyle"
        :row-class-name="tableRowClassName"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" width="80" align="center">
          <template slot-scope="scope">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="课程代码" align="center" prop="courseCode" width="150" />
        <el-table-column label="课程名称" align="center" prop="courseName" width="180" />
        <el-table-column label="开课院系" align="center" prop="academy" />
        <el-table-column label="授课教师" align="center" prop="teacherName" width="120" />
        <el-table-column label="学分值" align="center" prop="credit" width="100">
          <template slot-scope="{row}">
            <span class="credit-badge">{{ row.credit }}</span>
          </template>
        </el-table-column>
        <el-table-column label="总课时数" align="center" prop="courseHours" width="120" />
        <el-table-column label="课程类型" align="center" width="120">
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
        <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
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

export default {
  name: "Course",
  data() {
    return {
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
        courseHours: null
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
          { required: true, message: "课程类型不能为空", trigger: "blur" }
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
        courseHours: null
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
  --table-header-bg: #f8fafc;
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

.query-form .el-input__inner,
.query-form .el-select .el-input__inner {
  height: 40px;
  width: 220px;
  border-radius: 8px;
  border: 1px solid rgba(228, 231, 237, 0.6);
  background: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  box-shadow:
    inset 0 1px 2px rgba(255, 255, 255, 0.3),
    0 2px 4px rgba(0, 0, 0, 0.05);
}

/* ================= 表格容器样式 ================= */
.table-container {
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
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
  --hover-bg: rgba(241, 245, 249, 0.6);
  font-family: 'Segoe UI', system-ui, sans-serif;
}

.enhanced-table::v-deep .el-table__header th {
  background: var(--table-header-bg) !important;
  border-bottom: 1px solid var(--table-border-color) !important;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.enhanced-table::v-deep .el-table__body td {
  transition:
    transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.3s ease;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(228, 231, 237, 0.5) !important;
}

.enhanced-table::v-deep .el-table__body tr:hover td {
  background: var(--hover-bg) !important;
  transform: perspective(100px) translateZ(2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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

/* ================= 操作按钮样式 ================= */
.action-bar {
  display: flex;
  gap: 12px;
}

.action-btn {
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  border: none;
}

.action-btn.export-btn {
  background: linear-gradient(145deg, #e6a23c, #cf9234);
  color: white;
}

.action-btn.import-btn {
  background: linear-gradient(145deg, #909399, #7a7d85);
  color: white;
}

.action-icon {
  font-size: 16px;
  padding: 6px;
  transition: all 0.3s ease;
}

.action-icon.edit-icon {
  color: var(--primary-color);
}

.action-icon.edit-icon:hover {
  color: lighten(var(--primary-color), 10%);
}

.action-icon.delete-icon {
  color: var(--danger-color);
}

.action-icon.delete-icon:hover {
  color: lighten(var(--danger-color), 10%);
}

/* ================= 分页样式 ================= */
.custom-pagination {
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-top: 1px solid rgba(228, 231, 237, 0.6);
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
.el-dialog {
  border-radius: var(--border-radius) !important;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(12px);
}

.el-dialog__header {
  border-bottom: 1px solid rgba(228, 231, 237, 0.5);
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
</style>
