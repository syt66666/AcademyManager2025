<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
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
      <el-form-item label="行政班" prop="studentClass">
        <el-input
          v-model="queryParams.studentClass"
          placeholder="请输入行政班"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="是否创新班或拔尖班" prop="innovationClass">
        <el-select
          v-model="queryParams.innovationClass"
          placeholder="请选择是否创新班或拔尖班"
          clearable
          @change="handleQuery"
        >
          <el-option label="是" :value="1"></el-option>
          <el-option label="否" :value="0"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-upload"
          size="mini"
          @click="handleImport"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="学生学号" align="center" prop="studentId" />
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="所属书院" align="center" prop="academy" />
      <el-table-column label="所属学域" align="center" prop="originalSystemMajor" />
      <el-table-column label="行政班" align="center" prop="studentClass" />
      <el-table-column label="分流形式" align="center" prop="divertForm" />
      <el-table-column label="是否创新班或拔尖班" align="center" prop="innovationClass">
        <template slot-scope="scope">
          {{ scope.row.innovationClass === 1 ? '是' : scope.row.innovationClass === 0 ? '否' : '' }}
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="handlePagination"
    />

    <!-- 添加或修改学生信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生学号" />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入学生姓名" />
        </el-form-item>
        <el-form-item label="所属书院" prop="academy">
          <el-input v-model="form.academy" placeholder="请输入所属书院" />
        </el-form-item>
        <el-form-item label="所在学域" prop="originalSystemMajor">
          <el-input v-model="form.originalSystemMajor" placeholder="请输入所在学域" />
        </el-form-item>
        <el-form-item label="行政班" prop="studentClass">
          <el-input v-model="form.studentClass" placeholder="请输入行政班" />
        </el-form-item>
        <el-form-item label="分流形式" prop="divertForm">
          <el-input v-model="form.divertForm" placeholder="请输入分流形式" />
        </el-form-item>
        <el-form-item label="是否创新班或拔尖班" prop="innovationClass">
          <el-select v-model="form.innovationClass" placeholder="请选择是否创新班或拔尖班" clearable>
            <el-option label="是" :value="1"></el-option>
            <el-option label="否" :value="0"></el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
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
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的学生数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
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
import { listStudent, getStudent, delStudent, addStudent, updateStudent, getNickName } from "@/api/system/student";
export default {
  name: "Info",
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
      // 学生信息表格数据
      infoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        studentName: null,
        academy: null,
        originalSystemMajor: null,
        major: null,
        systemMajor: null,
        studentClass: null,
        divertForm: null,
        ranking: null,
        innovationClass: null,
        policyStatus: null,
        studentSex: null,
        gradeLevel: null,
        changeMajorType: null
      },
      // 表单参数
      form: {},
      // 当前筛选状态
      currentFilterParams: null,
      // 表单校验
      rules: {
        studentId: [
          { required: true, message: "学生学号不能为空", trigger: "blur" }
        ],
        studentName: [
          { required: true, message: "学生姓名不能为空", trigger: "blur" }
        ],
        academy: [
          { required: true, message: "所属书院不能为空", trigger: "blur" }
        ],
        originalSystemMajor: [
          { required: true, message: "所属学域不能为空", trigger: "blur" }
        ],
        studentClass: [
          { required: true, message: "行政班不能为空", trigger: "blur" }
        ],
        divertForm: [
          { required: true, message: "分流形式不能为空", trigger: "blur" }
        ],
      },
      // 上传参数
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
        headers: { Authorization: "Bearer " + this.$store.state.user.token },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/system/student/importData"
      }
    };
  },
  created() {
    this.getNickName();
  },
  methods: {
    /** 获取组织者名称并查询学生信息列表 */
    getNickName() {
      getNickName()
        .then(nickName => {
          console.log("获取到组织者名称:", nickName.msg);
          // 合并查询参数与组织者信息
          const params = { ...this.queryParams, academy: nickName.msg };
          console.log("查询参数:", params);
          this.currentFilterParams = params; // 保存筛选参数
          this.getList(params);
        })
        .catch(error => {
          console.error("获取组织者名称失败:", error);
          // 失败时使用原始查询参数
          console.log("使用原始查询参数:", this.queryParams);
          this.currentFilterParams = this.queryParams;
          this.getList(this.queryParams);
        });
    },
    /** 查询学生信息列表 */
    getList(params = null) {
      this.loading = true;
      const queryParams = params || this.queryParams;
      
      // 自动添加academy限制，确保只查询当前书院的数据
      if (this.currentFilterParams && this.currentFilterParams.academy) {
        queryParams.academy = this.currentFilterParams.academy;
      }
      
      listStudent(queryParams).then(response => {
        this.infoList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(error => {
        console.error("查询学生信息失败:", error);
        this.loading = false;
        this.$modal.msgError("查询学生信息失败，请检查网络连接或联系管理员");
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
        id: null,
        studentId: null,
        studentName: null,
        academy: null,
        originalSystemMajor: null,
        major: null,
        systemMajor: null,
        studentClass: null,
        divertForm: null,
        ranking: null,
        innovationClass: null,
        policyStatus: null,
        studentSex: null,
        gradeLevel: null,
        changeMajorType: null
      };
      this.resetForm("form");
    },

    /** 分页处理 */
    handlePagination() {
      this.getList();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加学生信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.studentId || this.ids
      getStudent(id).then(response => {
        this.form = response.studentInfo;
        this.open = true;
        this.title = "修改学生信息";
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
      this.$modal.confirm('是否确认删除学生信息编号为"' + ids + '"的数据项？').then(function() {
        return delStudent(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/student/export', {
        ...this.queryParams
      }, `student_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "学生信息导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    importTemplate() {
      this.downloadTemplate('system/student/importTemplate', `student_template_${new Date().getTime()}.xlsx`)
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
    // 下载模板文件（使用GET请求）
    downloadTemplate(url, filename) {
      const link = document.createElement('a');
      link.href = process.env.VUE_APP_BASE_API + '/' + url;
      link.download = filename;
      link.style.display = 'none';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
};
</script>
<style scoped>
/* 整体布局 */
.app-container {
  margin-left: 100px;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
</style>