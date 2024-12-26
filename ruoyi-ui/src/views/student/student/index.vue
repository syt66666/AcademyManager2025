<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学号" prop="学号">
        <el-input v-model="queryParams.学号" placeholder="请输入学号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="姓名" prop="姓名">
        <el-input v-model="queryParams.姓名" placeholder="请输入姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="管理部门" prop="管理部门">
        <el-input v-model="queryParams.管理部门" placeholder="请输入管理部门" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!--   <el-form-item label="系统内专业" prop="系统内专业">
        <el-input v-model="queryParams.系统内专业" placeholder="请输入系统内专业" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="招生录取专业" prop="招生录取专业">
        <el-input v-model="queryParams.招生录取专业" placeholder="请输入招生录取专业" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <!--  <el-form-item label="行政班" prop="行政班">
        <el-input
          v-model="queryParams.行政班"
          placeholder="请输入行政班"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="备注" prop="备注">
        <el-input
          v-model="queryParams.备注"
          placeholder="请输入备注"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <el-form-item label="分流形式" prop="分流形式">
        <el-input v-model="queryParams.分流形式" placeholder="请输入分流形式" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!--   <el-form-item label="国家和高校专项计划学生标志" prop="国家和高校专项计划学生标志">
        <el-input v-model="queryParams.国家和高校专项计划学生标志" placeholder="请输入国家和高校专项计划学生标志" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="英文姓名" prop="英文姓名">
        <el-input v-model="queryParams.英文姓名" placeholder="请输入英文姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="性别" prop="性别">
        <el-input v-model="queryParams.性别" placeholder="请输入性别" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="queryParams.password" placeholder="请输入密码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item> -->
      <el-form-item class="right-buttons">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['student:student:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['student:student:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['student:student:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['student:student:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImport"
          v-hasPermi="['system:user:import']">导入学生数据</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="studentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="序号" align="center" prop="id" /> -->
      <el-table-column label="学号" align="center" prop="学号" />
      <el-table-column label="姓名" align="center" prop="姓名" />
      <el-table-column label="管理部门" align="center" prop="管理部门" />
      <el-table-column label="系统内专业" align="center" prop="系统内专业" />
      <el-table-column label="招生录取专业" align="center" prop="招生录取专业" />
      <el-table-column label="行政班" align="center" prop="行政班" />
      <el-table-column label="分流形式" align="center" prop="分流形式" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['student:student:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['student:student:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改学生管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <form-item label="姓名" prop="name" v-model="form.name" placeholder="请输入姓名" />
        <form-item label="管理部门" prop="department" v-model="form.department" placeholder="请输入管理部门" />
        <form-item label="系统内专业" prop="majorInSystem" v-model="form.majorInSystem" placeholder="请输入系统内专业" />
        <form-item label="招生录取专业" prop="admissionMajor" v-model="form.admissionMajor" placeholder="请输入招生录取专业" />
        <form-item label="行政班" prop="administrativeClass" v-model="form.administrativeClass" placeholder="请输入行政班" />
        <form-item label="备注" prop="remark" v-model="form.remark" placeholder="请输入备注" />
        <form-item label="分流形式" prop="diversionForm" v-model="form.diversionForm" placeholder="请输入分流形式" />
        <form-item label="国家和高校专项计划学生标志" prop="specialPlanFlag" v-model="form.specialPlanFlag"
          placeholder="请输入国家和高校专项计划学生标志" />
        <form-item label="英文姓名" prop="englishName" v-model="form.englishName" placeholder="请输入英文姓名" />
        <form-item label="性别" prop="gender" v-model="form.gender" placeholder="请输入性别" />
        <!-- <form-item label="密码" prop="password" v-model="form.password" placeholder="请输入密码" /> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload ref="upload" :limit="1" accept=".xlsx, .xls" :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport" :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :auto-upload="false" drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline"
            @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStudent, getStudent, delStudent, addStudent, updateStudent } from "@/api/student/student";
import { getToken } from "@/utils/auth";
export default {
  name: "Student",
  data() {
    return {
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
        url: process.env.VUE_APP_BASE_API + "/student/excel/upload"
      },
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
      // 学生管理表格数据
      studentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        学号: null,
        姓名: null,
        管理部门: null,
        系统内专业: null,
        招生录取专业: null,
        行政班: null,
        备注: null,
        分流形式: null,
        国家和高校专项计划学生标志: null,
        英文姓名: null,
        性别: null,
        password: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        学号: [
          { required: true, message: "学号不能为空", trigger: "blur" }
        ],
        姓名: [
          { required: true, message: "姓名不能为空", trigger: "blur" }
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询学生管理列表 */
    getList() {
      this.loading = true;
      listStudent(this.queryParams).then(response => {
        this.studentList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "用户导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('system/user/importTemplate', {
      }, `user_template_${new Date().getTime()}.xlsx`)
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
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        学号: null,
        姓名: null,
        管理部门: null,
        系统内专业: null,
        招生录取专业: null,
        行政班: null,
        备注: null,
        分流形式: null,
        国家和高校专项计划学生标志: null,
        英文姓名: null,
        性别: null,
        password: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加学生管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getStudent(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改学生管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateStudent(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addStudent(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除学生管理编号为"' + ids + '"的数据项？').then(function () {
        return delStudent(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('student/student/export', {
        ...this.queryParams
      }, `student_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style scoped>
/* 如果需要进一步调整样式 */
.el-form-item--right {
  text-align: right;
}
</style>
<style scoped>
.right-buttons {
  text-align: right;
}

/* 添加样式以优化布局 */
.el-form-item {
  margin-bottom: 15px;
}
</style>