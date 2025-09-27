<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="card-header">
        <i class="el-icon-search"></i>
        <span>搜索条件</span>
      </div>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <div class="search-row">
          <el-form-item label="学生学号" prop="studentId">
            <el-input
              v-model="queryParams.studentId"
              placeholder="请输入学生学号"
              clearable
              prefix-icon="el-icon-user"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="学生姓名" prop="studentName">
            <el-input
              v-model="queryParams.studentName"
              placeholder="请输入学生姓名"
              clearable
              prefix-icon="el-icon-user-solid"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="行政班" prop="studentClass">
            <el-input
              v-model="queryParams.studentClass"
              placeholder="请输入行政班"
              clearable
              prefix-icon="el-icon-school"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="入学年份" prop="enrollmentYear">
            <el-select
              v-model="queryParams.enrollmentYear"
              placeholder="请选择入学年份"
              clearable
              class="search-input"
              @change="handleQuery">
              <el-option
                v-for="year in enrollmentYearOptions"
                :key="year"
                :label="year"
                :value="year">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="search-actions">
            <el-button-group class="action-buttons">
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="handleQuery"
                class="search-button"
              >搜索</el-button>
              <el-button
                icon="el-icon-refresh"
                @click="resetQuery"
                class="refresh-button"
              >重置</el-button>
            </el-button-group>
          </el-form-item>
        </div>
      </el-form>
    </div>

    <!-- 按钮区域 -->
    <div class="button-bar">
      <el-button-group>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
        <el-button
          type="success"
          plain
          icon="el-icon-upload"
          size="mini"
          @click="handleImport"
        >导入</el-button>
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-button-group>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <div class="card-header">
        <i class="el-icon-s-grid"></i>
        <span>学生列表</span>
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>

      <!-- 表格美化 -->
      <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange" class="modern-table" :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}">
        <el-table-column type="selection" width="45" align="center"/>
        <el-table-column label="序号" width="80" align="center">
          <template slot-scope="scope">
            <span class="index-badge">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="学生学号" align="center" prop="studentId" min-width="160">
          <template slot-scope="scope">
            <div class="student-id-container">
              <span class="student-id">{{ scope.row.studentId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="学生姓名" align="center" prop="studentName" min-width="120">
          <template slot-scope="scope">
            <div class="student-name">{{ scope.row.studentName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="入学年份" align="center" prop="enrollmentYear" min-width="100">
          <template slot-scope="scope">
            <el-tag size="small" type="success" effect="plain" class="enrollment-year-tag">
              {{ scope.row.enrollmentYear }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所属书院" align="center" prop="academy" min-width="120">
          <template slot-scope="scope">
            <div class="academy-text">{{ scope.row.academy }}</div>
          </template>
        </el-table-column>
        <el-table-column label="所属学域" align="center" prop="originalSystemMajor" min-width="200">
          <template slot-scope="scope">
            <div class="major-info">{{ scope.row.originalSystemMajor }}</div>
          </template>
        </el-table-column>
        <el-table-column label="录取专业" align="center" prop="major" min-width="180">
          <template slot-scope="scope">
            <div class="major-info">{{ scope.row.major }}</div>
          </template>
        </el-table-column>
        <el-table-column label="行政班" align="center" prop="studentClass" min-width="120">
          <template slot-scope="scope">
            <el-tag size="small" type="primary" effect="plain" class="class-tag">
              {{ scope.row.studentClass }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分流形式" align="center" prop="divertForm" min-width="180">
          <template slot-scope="scope">
            <div class="divert-form">{{ scope.row.divertForm }}</div>
          </template>
        </el-table-column>
        <el-table-column label="创新班/拔尖班" align="center" prop="innovationClass" min-width="140">
          <template slot-scope="scope">
            <el-tag :type="scope.row.innovationClass === 1 ? 'success' : 'info'" effect="dark" class="innovation-tag">
              {{ scope.row.innovationClass === 1 ? '是' : scope.row.innovationClass === 0 ? '否' : '未知' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" min-width="180">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                class="action-button edit-button"
              >修改信息</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-refresh"
                @click="handleResetPassword(scope.row)"
                class="action-button reset-button"
              >重置密码</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="handlePagination"
        class="custom-pagination"
      />
    </div>

    <!-- 添加或修改学生信息对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="600px"
      append-to-body
      class="student-form-dialog"
      :before-close="handleDialogClose">

      <!-- 对话框头部 -->
      <div slot="title" class="dialog-header">
        <div class="header-content">
          <div class="header-icon">
            <i :class="form.id ? 'el-icon-edit' : 'el-icon-plus'"></i>
          </div>
          <div class="header-text">
            <h3>{{ title }}</h3>
            <p>{{ form.id ? '修改学生信息' : '添加新的学生' }}</p>
          </div>
        </div>
      </div>

      <!-- 表单内容 -->
      <div class="dialog-body">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="student-form">

          <!-- 基本信息区域 -->
          <div class="form-section">
            <div class="section-header">
              <i class="el-icon-user"></i>
              <span>基本信息</span>
            </div>
            <div class="section-content">
              <el-form-item label="学生学号" prop="studentId">
                <el-input
                  v-model="form.studentId"
                  placeholder="请输入学生学号"
                  prefix-icon="el-icon-user"
                  class="form-input">
                </el-input>
                <div class="form-tip">
                  <i class="el-icon-info"></i>
                  学生学号必须唯一
                </div>
              </el-form-item>

              <el-form-item label="学生姓名" prop="studentName">
                <el-input
                  v-model="form.studentName"
                  placeholder="请输入学生姓名"
                  prefix-icon="el-icon-user-solid"
                  class="form-input">
                </el-input>
              </el-form-item>

              <el-form-item label="入学年份" prop="enrollmentYear">
                <el-input
                  v-model="form.enrollmentYear"
                  placeholder="请输入入学年份"
                  prefix-icon="el-icon-date"
                  class="form-input">
                </el-input>
                <div class="form-tip">
                  <i class="el-icon-info"></i>
                  请输入4位数字年份，如：2024
                </div>
              </el-form-item>

              <el-form-item label="所属书院" prop="academy">
                <el-input
                  v-model="form.academy"
                  placeholder="请输入所属书院"
                  prefix-icon="el-icon-office-building"
                  class="form-input">
                </el-input>
              </el-form-item>

              <el-form-item label="所在学域" prop="originalSystemMajor">
                <el-input
                  v-model="form.originalSystemMajor"
                  placeholder="请输入所在学域"
                  prefix-icon="el-icon-school"
                  class="form-input">
                </el-input>
              </el-form-item>
            </div>
          </div>

          <!-- 班级信息区域 -->
          <div class="form-section">
            <div class="section-header">
              <i class="el-icon-s-grid"></i>
              <span>班级信息</span>
            </div>
            <div class="section-content">
              <el-form-item label="行政班" prop="studentClass">
                <el-input
                  v-model="form.studentClass"
                  placeholder="请输入行政班"
                  prefix-icon="el-icon-s-grid"
                  class="form-input">
                </el-input>
              </el-form-item>

              <el-form-item label="分流形式" prop="divertForm">
                <el-input
                  v-model="form.divertForm"
                  placeholder="请输入分流形式"
                  prefix-icon="el-icon-connection"
                  class="form-input">
                </el-input>
              </el-form-item>

              <el-form-item label="创新班/拔尖班" prop="innovationClass">
                <el-select v-model="form.innovationClass" placeholder="请选择是否创新班或拔尖班" clearable class="form-select">
                  <el-option label="是" :value="1"></el-option>
                  <el-option label="否" :value="0"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </div>
        </el-form>
      </div>

      <!-- 对话框底部 -->
      <div slot="footer" class="dialog-footer">
        <div class="footer-right">
          <el-button
            @click="cancel"
            class="cancel-btn">
            <i class="el-icon-close"></i>
            取消
          </el-button>
          <el-button
            type="primary"
            @click="submitForm"
            class="submit-btn">
            <i :class="form.id ? 'el-icon-check' : 'el-icon-plus'"></i>
            {{ form.id ? '保存修改' : '添加学生' }}
          </el-button>
        </div>
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
        :on-error="handleFileError"
        :before-upload="beforeUpload"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
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
import { listStudent, getStudent, delStudent, addStudent, updateStudent, getNickName, resetStudentPassword } from "@/api/system/student";
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
      // 入学年份选项
      enrollmentYearOptions: [],
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
        changeMajorType: null,
        enrollmentYear: null
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
  mounted() {
    // 页面加载完成后，再次尝试获取入学年份选项
    this.$nextTick(() => {
      setTimeout(() => {
        if (this.enrollmentYearOptions.length === 0) {
          this.getEnrollmentYearOptions();
        }
      }, 1000);
    });
  },
  methods: {
    /** 获取组织者名称并查询学生信息列表 */
    getNickName() {
      getNickName()
        .then(nickName => {
          // 合并查询参数与组织者信息
          const params = { ...this.queryParams, academy: nickName.msg };
          this.currentFilterParams = params; // 保存筛选参数
          this.getList(params);
          // 获取入学年份选项
          this.getEnrollmentYearOptions();
        })
        .catch(error => {
          // 失败时使用原始查询参数
          this.currentFilterParams = this.queryParams;
          this.getList(this.queryParams);
          // 获取入学年份选项
          this.getEnrollmentYearOptions();
        });
    },
    /** 获取入学年份选项 */
    getEnrollmentYearOptions() {
      // 获取所有学生数据来提取入学年份
      const params = { pageNum: 1, pageSize: 10000 }; // 获取大量数据
      
      // 如果有当前筛选参数，使用它；否则等待获取书院信息
      if (this.currentFilterParams && this.currentFilterParams.academy) {
        params.academy = this.currentFilterParams.academy;
      } else {
        return;
      }
      
      listStudent(params).then(response => {
        if (response.rows && response.rows.length > 0) {
          // 提取所有入学年份并去重
          const years = response.rows
            .map(student => student.enrollmentYear)
            .filter(year => year && year.trim() !== '')
            .map(year => year.trim())
            .filter((year, index, arr) => arr.indexOf(year) === index) // 去重
            .sort((a, b) => b.localeCompare(a)); // 按年份降序排列
          
          this.enrollmentYearOptions = years;
        }
      }).catch(error => {
        // 静默处理错误
      });
    },
    /** 从当前学生列表更新入学年份选项 */
    updateEnrollmentYearOptionsFromCurrentList() {
      if (this.infoList && this.infoList.length > 0) {
        // 从当前学生列表中提取入学年份并去重
        const years = this.infoList
          .map(student => student.enrollmentYear)
          .filter(year => year && year.trim() !== '')
          .map(year => year.trim())
          .filter((year, index, arr) => arr.indexOf(year) === index) // 去重
          .sort((a, b) => b.localeCompare(a)); // 按年份降序排列
        
        // 合并到现有选项中，避免重复
        const existingYears = this.enrollmentYearOptions || [];
        const allYears = [...new Set([...existingYears, ...years])]
          .sort((a, b) => b.localeCompare(a));
        
        this.enrollmentYearOptions = allYears;
      }
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
        
        // 从当前学生列表中提取入学年份选项
        this.updateEnrollmentYearOptionsFromCurrentList();
      }).catch(error => {
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
        changeMajorType: null,
        enrollmentYear: null
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
    /** 重置密码按钮操作 */
    handleResetPassword(row) {
      const studentName = row.studentName;
      const studentId = row.studentId;
      this.$modal.confirm('是否确认重置学生"' + studentName + '"（学号：' + studentId + '）的密码为123456？').then(function() {
        return resetStudentPassword(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("密码重置成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/student/exportSelected', {
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
      // 显示上传进度
      this.$message({
        message: `正在上传文件：${file.name}，进度：${Math.round(event.percent)}%`,
        type: 'info',
        duration: 2000
      });
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      
      // 检查响应状态
      if (response.code === 200) {
        // 成功情况
        this.$alert(
          "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + 
          response.msg + 
          "</div>", 
          "✅ 导入成功", 
          { 
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定',
            type: 'success'
          }
        );
        this.$message.success('数据导入成功！');
      } else {
        // 失败情况
        this.$alert(
          "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + 
          (response.msg || '导入过程中发生未知错误') + 
          "</div>", 
          "❌ 导入失败", 
          { 
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定',
            type: 'error'
          }
        );
        this.$message.error('数据导入失败，请检查错误信息！');
      }
      
      this.getList();
    },
    // 文件上传失败处理
    handleFileError(error, file, fileList) {
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      
      let errorMessage = '文件上传失败';
      if (error.message) {
        errorMessage = error.message;
      } else if (error.response && error.response.data && error.response.data.msg) {
        errorMessage = error.response.data.msg;
      }
      
      this.$alert(
        "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + 
        errorMessage + 
        "</div>", 
        "❌ 上传失败", 
        { 
          dangerouslyUseHTMLString: true,
          confirmButtonText: '确定',
          type: 'error'
        }
      );
      this.$message.error('文件上传失败！');
    },
    // 文件上传前验证
    beforeUpload(file) {
      // 检查文件类型
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                     file.type === 'application/vnd.ms-excel' ||
                     file.name.endsWith('.xlsx') || 
                     file.name.endsWith('.xls');
      
      if (!isExcel) {
        this.$message.error('只能上传 Excel 文件（.xlsx 或 .xls 格式）！');
        return false;
      }
      
      // 检查文件大小（限制为10MB）
      const isLt10M = file.size / 1024 / 1024 < 10;
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过 10MB！');
        return false;
      }
      
      return true;
    },
    // 提交上传文件
    submitFileForm() {
      if (this.$refs.upload.uploadFiles.length === 0) {
        this.$message.warning('请先选择要上传的文件！');
        return;
      }
      
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
    },
    // 获取书院标签类型
    getAcademyTagType(academy) {
      const academyColors = {
        '知行书院': 'primary',
        '明德书院': 'success',
        '博雅书院': 'warning',
        '至善书院': 'info',
        '未知': ''
      };
      return academyColors[academy] || 'info';
    },
    // 对话框关闭处理
    handleDialogClose(done) {
      this.cancel();
      done();
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

/* 统一卡片样式 */
.search-card,
.table-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
  width: 100%;
}

.search-card:hover,
.table-card:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 卡片头部 */
.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f2f5;
}

.card-header i {
  font-size: 20px;
  color: #409EFF;
  margin-right: 12px;
}

.card-header span {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.record-count {
  margin-left: auto;
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}

/* 搜索表单 */
.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.search-input {
  min-width: 220px;
  transition: all 0.3s ease;
}

.search-input:hover {
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.search-actions {
  margin-left: auto;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

/* 按钮样式 */
.search-button {
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.refresh-button {
  background: #f0f2f5;
  border: none;
  padding: 10px 20px;
  color: #606266;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.refresh-button:hover {
  background: #e4e7ed;
  color: #333;
  transform: translateY(-2px);
}

/* 按钮区域 */
.button-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 5px;
}

.el-button-group {
  display: flex;
  gap: 8px;
}

/* 现代化表格 */
.modern-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
  width: 100%;
  table-layout: auto;
}

.modern-table th {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9) !important;
  font-weight: 600;
  color: #1e293b;
  border-bottom: 2px solid #e2e8f0;
  padding: 16px 12px;
}

.modern-table td {
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 12px;
}

.modern-table tr:hover td {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9) !important;
}

.index-badge {
  display: inline-block;
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  color: white;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.student-id-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
}

.student-id {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  color: #409EFF;
  font-weight: 500;
  text-align: center;
}

.student-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.academy-text {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.enrollment-year-tag {
  font-weight: 600;
  padding: 0 10px;
  height: 26px;
  line-height: 26px;
  font-size: 13px;
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(103, 194, 58, 0.2);
}

.class-tag {
  font-weight: 500;
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
  font-size: 12px;
  border-radius: 4px;
}

.innovation-tag {
  font-weight: 600;
  padding: 0 10px;
  height: 28px;
  line-height: 28px;
  font-size: 12px;
}

.major-info {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.divert-form {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 6px;
}

.action-button {
  padding: 5px 8px;
  font-size: 12px;
  border-radius: 4px;
}

.edit-button { 
  background: #f4f4f5; 
  border-color: #d3d4d6; 
  color: #909399; 
}

.reset-button { 
  background: #f0f9eb; 
  border-color: #e1f3d8; 
  color: #67c23a; 
}

/* 分页样式 */
.custom-pagination {
  display: flex;
  justify-content: center !important;
  margin: 20px auto 0;
  padding: 12px 0;
  width: 100%;
}

.custom-pagination /deep/ .el-pagination {
  display: inline-flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.custom-pagination /deep/ .el-pager li:hover {
  border-color: #409EFF;
  color: #409EFF;
}

/* 学生表单对话框美化样式 */
.student-form-dialog {
  .el-dialog {
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    overflow: hidden;
  }

  .el-dialog__header {
    padding: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px 16px 0 0;
  }

  .el-dialog__body {
    padding: 0;
    background: #f8f9fa;
  }

  .el-dialog__footer {
    padding: 0;
    background: #f8f9fa;
    border-radius: 0 0 16px 16px;
  }
}

/* 对话框头部 */
.dialog-header {
  padding: 24px 32px;
  color: white;

  .header-content {
    display: flex;
    align-items: center;
    gap: 16px;

    .header-icon {
      width: 48px;
      height: 48px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      backdrop-filter: blur(10px);

      i {
        font-size: 24px;
        color: white;
      }
    }

    .header-text {
      h3 {
        margin: 0 0 4px 0;
        font-size: 20px;
        font-weight: 600;
        color: white;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

/* 对话框主体 */
.dialog-body {
  padding: 32px;
  max-height: 70vh;
  overflow-y: auto;
}

/* 学生表单 */
.student-form {
  .form-section {
    background: white;
    border-radius: 12px;
    margin-bottom: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border: 1px solid #e4e7ed;
    overflow: hidden;

    .section-header {
      display: flex;
      align-items: center;
      padding: 16px 20px;
      background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
      border-bottom: 1px solid #e4e7ed;

      i {
        font-size: 16px;
        color: #409EFF;
        margin-right: 8px;
      }

      span {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .section-content {
      padding: 24px;
    }
  }
}

/* 表单输入框样式 */
.form-input {
  .el-input__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;

    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }
}

.form-select {
  .el-input__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;

    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }
}

/* 表单提示样式 */
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 6px;
  border-left: 3px solid #409EFF;

  i {
    margin-right: 6px;
    color: #409EFF;
    font-size: 14px;
  }
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 20px 32px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;

  .footer-right {
    display: flex;
    gap: 12px;

    .cancel-btn {
      background: #f5f7fa;
      border: 1px solid #dcdfe6;
      color: #606266;
      border-radius: 8px;
      padding: 10px 20px;
      font-weight: 500;
      transition: all 0.3s ease;

      &:hover {
        background: #ecf5ff;
        border-color: #b3d8ff;
        color: #409EFF;
      }

      i {
        margin-right: 6px;
      }
    }

    .submit-btn {
      background: linear-gradient(135deg, #409EFF 0%, #64b5ff 100%);
      border: none;
      color: white;
      border-radius: 8px;
      padding: 10px 24px;
      font-weight: 600;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
        background: linear-gradient(135deg, #3a8ee6 0%, #5a9fd8 100%);
      }

      &:active {
        transform: translateY(0);
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
      }

      i {
        margin-right: 6px;
      }
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .student-form-dialog {
    .el-dialog {
      width: 95% !important;
      margin: 0 auto;
    }
  }

  .dialog-body {
    padding: 20px;
  }

  .dialog-footer {
    flex-direction: column;
    gap: 16px;

    .footer-right {
      width: 100%;
      justify-content: center;
    }
  }
}
</style>