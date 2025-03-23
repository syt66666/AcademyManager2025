<template>
  <div class="app-container">
    <el-form class="query-form" :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="学生学号" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入学生学号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学生姓名" prop="studentName">
        <el-input
          v-model="queryParams.studentName"
          placeholder="请输入学生姓名"
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
      <el-form-item label="修读学年" prop="academicYear">
        <el-date-picker
          v-model="queryParams.academicYear"
          type="year"
          value-format="yyyy"
          format="yyyy"
          placeholder="选择修读学年"
          :editable="false"
          clearable
          @keyup.enter.native="handleQuery">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="修读学期" prop="semester">
        <el-select
          v-model="queryParams.semester"
          placeholder="请选择修读学期"
          clearable
          filterable
          @keyup.enter.native="handleQuery">
          <el-option
            v-for="item in [{label: '第一学期', value: '第一学期'}, {label: '第二学期', value: '第二学期'}, {label: '第三学期', value: '第三学期'}]"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="成绩类型" prop="scoreType">
        <el-select
          v-model="queryParams.scoreType"
          placeholder="请选择成绩类型"
          clearable
          filterable
          @keyup.enter.native="handleQuery">
          <el-option
            v-for="item in [{label: '正考', value: '正考'}, {label: '补考', value: '补考'}, {label: '重修', value: '重修'}, {label: '免修', value: '免修'}]"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button-group>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-button-group>
      </el-form-item>
    </el-form>
    <!-- 数据操作区域 -->
    <el-card class="table-container">
      <!-- 操作工具栏 -->
      <div slot="header" class="table-header">
        <span class="table-title">成绩数据列表</span>
        <div class="action-bar">
          <el-button
            type="primary"
            icon="el-icon-upload2"
            class="action-btn import-btn"
            @click="handleImport"
            v-hasPermi="['system:score:import']"
          >
            成绩导入
          </el-button>
          <el-button
            type="success"
            icon="el-icon-download"
            class="action-btn export-btn"
            @click="handleExport"
            v-hasPermi="['system:score:export']"
          >
            数据导出
          </el-button>
        </div>
      </div>
    <el-table
      :data="scoreList"
      style="width: 100%"
      class="enhanced-table"
      :header-cell-style="headerStyle"
      :row-class-name="tableRowClassName"
      @row-click="handleRowClick"
    >
      <el-table-column label="序号" width="80" align="center">
        <template slot-scope="scope">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="学生学号" align="center" prop="studentId" width="150"/>
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="课程代码" align="center" prop="courseCode" />
      <el-table-column label="课程名称" align="center" prop="courseName" width="150"/>
      <el-table-column label="修读学年" align="center" prop="academicYear" />
      <el-table-column label="修读学期" align="center" prop="semester" />
      <el-table-column
        label="学生成绩"
        align="center"
        prop="scoreValue"
        width="150"
      >
        <template slot-scope="{row}">
        <span :class="['score-value', scoreClass(row.scoreValue)]">
          {{ row.scoreValue }}
          <i v-if="row.scoreValue >= 90" class="el-icon-star-on"></i>
        </span>
        </template>
      </el-table-column>
      <el-table-column
        label="课程类型"
        align="center"
        width="150"
      >
        <template slot-scope="{row}">
          <el-tag
            :type="scoreTypeTag(row.scoreType)"
            size="small"
            effect="light"
            :class="['type-tag', row.scoreType]"
          >
            {{ row.scoreType }}
          </el-tag>
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
    <!-- 添加或修改成绩对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程代码" />
        </el-form-item>
        <el-form-item label="修读学年" prop="academicYear">
          <el-date-picker
            v-model="form.academicYear"
            type="year"
            value-format="yyyy"
            placeholder="选择学年"
            :editable="false"
            :clearable="false">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="修读学期" prop="semester">
          <el-select v-model="form.semester" placeholder="请选择修读学期">
            <el-option
              v-for="item in [{label: '第一学期', value: '第一学期'}, {label: '第二学期', value: '第二学期'}, {label: '第三学期', value: '第三学期'}]"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="成绩值" prop="scoreValue">
          <el-input v-model="form.scoreValue" placeholder="请输入成绩值" />
        </el-form-item>
        <el-form-item label="成绩类型" prop="scoreType">
          <el-select
            v-model="form.scoreType"
            placeholder="请选择成绩类型"
            clearable>
            <el-option
              v-for="item in [{label: '正考', value: '正考'}, {label: '补考', value: '补考'}, {label: '重修', value: '重修'}, {label: '免修', value: '免修'}]"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { listScore, getScore, delScore, addScore, updateScore } from "@/api/system/score";
import store from "@/store";
import { getToken } from "@/utils/auth";

export default {
  name: "Score",
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
      // 表格数据
      scoreList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        courseCode: null,
        courseName: null,
        academicYear: null,
        semester: null,
        scoreValue: null,
        gpa: null,
        scoreType: null,
        nickName: null,
        uploadTime: null,
      },
      // 表单参数
      form: {},
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
        url: process.env.VUE_APP_BASE_API + "/system/score/importData"
      },
      // 表单校验
      rules: {
        studentId: [
          {required: true, message: "学号不能为空", trigger: "blur"}
        ],
        courseCode: [
          {required: true, message: "课程编码不能为空", trigger: "blur"}
        ],
        academicYear: [
          {required: true, message: "请选择修读学年", trigger: "change"}
        ],
        semester: [
          {required: true, message: "请选择修读学期", trigger: "change"}
        ],
        scoreValue: [
          {required: true, message: "成绩值不能为空", trigger: "blur"}
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询成绩列表 */
    getList() {
      this.loading = true;
      listScore(this.queryParams).then(response => {
        this.scoreList = response.rows;
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
        scoreId: null,
        studentId: null,
        courseCode: null,
        academicYear: null,
        semester: null,
        scoreValue: null,
        gpa: null,
        scoreType: null,
        nickName: null,
        uploadTime: null
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
      this.ids = selection.map(item => item.scoreId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加成绩";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const scoreId = row.scoreId || this.ids
      getScore(scoreId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改成绩";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.form.nickName = store.state.user.name;
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.scoreId != null) {
            updateScore(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addScore(this.form).then(response => {
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
      const scoreIds = row.scoreId || this.ids;
      this.$modal.confirm('是否确认删除成绩？').then(function () {
        return delScore(scoreIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/score/export', {
        ...this.queryParams
      }, `score_${new Date().getTime()}.xlsx`)
    },
    /** 导出模板操作 */
    importTemplate() {
      fetch(process.env.VUE_APP_BASE_API+'/system/score/importTemplate', {
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
    scoreClass(score) {
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 60) return 'pass'
      return 'fail'
    },

    scoreTypeTag(type) {
      const map = {
        '正考': 'success',
        '补考': 'warning',
        '重修': 'danger',
        '免修': 'info'
      }
      return map[type] || 'info'
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
/* ================= 全局变量 ================= */
.app-container {
  --primary-color: #409eff;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --info-color: #909399;
  --border-radius: 8px;
  --box-shadow: 0 3px 10px rgba(0, 0, 0, 0.06);
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

/* ================= 数据表格容器 ================= */
.table-container {
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.table-container:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 表格标题栏 */
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
/* 成绩显示 */
.score-value {
  font-family: 'Roboto Mono', monospace;
  font-weight: 600;
  position: relative;
  padding-right: 20px;
}

.score-value.excellent { color: #16a34a; }
.score-value.good { color: #2563eb; }
.score-value.pass { color: #ca8a04; }
.score-value.fail { color: #dc2626; }

.score-value i {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  color: #f59e0b;
  filter: drop-shadow(0 2px 4px rgba(245, 158, 11, 0.3));
}

/* 类型标签 */
.type-tag {
  border-radius: 6px;
  padding: 4px 10px;
  border-width: 1px;
  font-weight: 500;
}

.type-tag.正考 {
  background: rgba(101, 163, 13, 0.1);
  border-color: rgba(101, 163, 13, 0.3);
  color: #3f6212;
}

.type-tag.补考 {
  background: rgba(245, 158, 11, 0.1);
  border-color: rgba(245, 158, 11, 0.3);
  color: #854d0e;
}

.type-tag.重修 {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.3);
  color: #991b1b;
}

.type-tag.免修 {
  background: rgba(99, 102, 241, 0.1);
  border-color: rgba(99, 102, 241, 0.3);
  color: #3730a3;
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

/* ================= 响应式布局 ================= */
@media (max-width: 768px) {
  /* 查询表单适配 */
  .query-form {
    padding: 16px;
    border-radius: 12px;
  }

  .query-form .el-form-item {
    width: 100%;
    margin-right: 0;
  }

  .query-form .el-input__inner,
  .query-form .el-select {
    width: 100%;
  }

  .query-form .el-button-group {
    width: 100%;
    display: flex;
  }

  .query-form .el-button {
    flex: 1;
  }

  /* 表格适配 */
  .table-container .table-header {
    flex-direction: column;
    gap: 12px;
  }

  .enhanced-table ::v-deep .el-table__body td {
    padding: 10px 12px;
    font-size: 13px;
  }

  .enhanced-table .course-code,
  .enhanced-table .academic-year {
    display: none;
  }

  .type-tag {
    font-size: 12px;
    padding: 2px 8px;
  }
}

/* ================= 工具类样式 ================= */
.striped-row td {
  background: linear-gradient(to right,
  rgba(251, 253, 255, 0.6),
  rgba(245, 247, 250, 0.6)
  );
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
