<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学号" prop="studentId">
        <el-input v-model="queryParams.studentId" placeholder="请输入学号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="姓名" prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
        <el-form-item label="书院" prop="academy">
          <el-select v-model="queryParams.academy" placeholder="请选择书院" clearable @change="handleQuery">
            <el-option
              v-for="(academy, index) in academyList"
              :key="index"
              :label="academy"
              :value="academy"
            />
          </el-select>
        </el-form-item>

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
      <el-table-column label="学号" align="center" prop="studentId" sortable />
      <el-table-column label="姓名" align="center" prop="studentName" sortable />
      <el-table-column label="书院" align="center" prop="academy" sortable/>
      <el-table-column label="系统内专业" align="center" prop="systemMajor" sortable/>
      <el-table-column label="招生录取专业" align="center" prop="major" sortable/>
      <el-table-column label="行政班" align="center" prop="studentClass" sortable/>
      <el-table-column label="分流形式" align="center" prop="divertForm" sortable/>
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

    <!-- 修改学生管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="书院" prop="academy">
          <el-input v-model="form.academy" placeholder="请输入所属书院" />
        </el-form-item>
        <el-form-item label="系统内专业" prop="systemMajor">
          <el-input v-model="form.systemMajor" placeholder="请输入系统内专业" />
        </el-form-item>
        <el-form-item label="招生录取专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入招生录取专业" />
        </el-form-item>
        <el-form-item label="行政班" prop="studentClass">
          <el-input v-model="form.studentClass" placeholder="请输入行政班" />
        </el-form-item>
        <el-form-item label="分流形式" prop="divertForm">
          <el-input v-model="form.divertForm" placeholder="请输入分流形式" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 添加学生管理对话框 -->
    <el-dialog :title="title" :visible.sync="openInsert" width="500px" append-to-body>
      <el-form ref="form" :model="studentForm" :rules="insertRules" label-width="80px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="studentForm.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="studentForm.studentName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="书院" prop="academy">
          <el-input v-model="studentForm.studentAddress" placeholder="请输入所属书院" />
        </el-form-item>
        <el-form-item label="系统内专业" prop="systemMajor">
          <el-input v-model="studentForm.systemMajor" placeholder="请输入系统内专业" />
        </el-form-item>
        <el-form-item label="招生录取专业" prop="major">
          <el-input v-model="studentForm.enrollmentMajor" placeholder="请输入招生录取专业" />
        </el-form-item>
        <el-form-item label="行政班" prop="studentClass">
          <el-input v-model="studentForm.studentClass" placeholder="请输入行政班" />
        </el-form-item>
        <el-form-item label="分流形式" prop="divertForm">
          <el-input v-model="studentForm.studentDiversionForm" placeholder="请输入分流形式" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="studentCancel">取 消</el-button>
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
      //书院选择下拉框
      academyList: [
        '伯川书院',
        '笃学书院',
        '令希书院',
        '厚德书院',
        '知行书院',
        '求实书院',
        '厚德书院'
      ],
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
      // 是否显示更新弹出层
      open: false,
      // 是否显示插入弹出层
      openInsert: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        studentName: null,
        academy: null,
        systemMajor: null,
        major: null,
        studentClass: null,
        divertForm: null,
        sex: null,

      },
      // 表单参数
      form: {},
      // 新增一个学生的学生信息参数
      studentForm: {
        studentId : null,
        studentName : null,
        studentAddress : null,
        systemMajor : null,
        enrollmentMajor : null,
        studentClass : null,
        studentNote : null,
        studentDiversionForm : null,
        haveQualification : null,
        englishName : null,
        studentSex : null,
      },
      // 修改表单校验
      rules: {
        studentId: [
          { required: true, message: "学号不能为空", trigger: "blur" }
        ],
        studentName: [
          { required: true, message: "姓名不能为空", trigger: "blur" }
        ],
        academy: [
          { required: true, message: '请输入管理部门', trigger: 'blur' }
        ],

      },
      // 插入表单校验
      insertRules: {
        studentId: [
          { required: true, message: "学号不能为空", trigger: "blur" }
        ],
        studentName: [
          { required: true, message: "姓名不能为空", trigger: "blur" }
        ],

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
        studentId: null,
        studentName: null,
        academy: null,
        systemMajor: null,
        major: null,
        studentClass: null,
        divertForm: null,
      };

      this.resetForm("form");
    },
    // 取消增加用户表单按钮
    studentCancel() {
      this.openInsert = false;
      this.studentReset();
    },
    // 增加用户表单重置
    studentReset() {
      this.studentForm = {
        id: null,
        studentId : '',
        studentName : '',
        studentAddress : '',
        systemMajor : '',
        enrollmentMajor : '',
        studentClass : '',
        studentNote : '',
        studentDiversionForm : '',
        haveQualification : '',
        englishName : '',
        studentSex : '',

      };

      this.resetForm("studentForm");
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
      this.studentReset();
      this.openInsert = true;
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
            this.studentForm.haveQualification = this.studentForm.haveQualification === "是" ? true : false;
            addStudent(this.studentForm).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.openInsert = false;
              this.getList();
            }).catch(error => {
              // 处理addStudent函数中可能抛出的异常
              console.error("新增学生失败：", error);
              this.$modal.msgError("新增学生失败");
              this.openInsert = false;
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
