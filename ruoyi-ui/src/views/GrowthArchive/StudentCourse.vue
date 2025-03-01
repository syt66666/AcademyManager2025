<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
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
          style="width: 100%"
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
          style="width: 100%"
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
          v-hasPermi="['system:course:add']"
        >新增</el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:course:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:course:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:course:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['system:course:import']"
        >导入</el-button>
      </el-col>

      <!--      <el-col :span="1.5">-->
<!--      &lt;!&ndash; 模板下载按钮 &ndash;&gt;-->
<!--      <el-tooltip content="下载导入模板" placement="top">-->
<!--        <el-button-->
<!--          type="info"-->
<!--          plain-->
<!--          icon="el-icon-download"-->
<!--          size="mini"-->
<!--          @click="handleDownloadTemplate"-->
<!--          v-hasPermi="['system:course:import']">-->
<!--          模板-->
<!--        </el-button>-->
<!--      </el-tooltip>-->
<!--    </el-col>-->

<!--      <el-col :span="1.5">-->
        <!-- 数据导入按钮 -->
<!--        <el-tooltip content="执行数据导入" placement="top">-->
<!--          <el-button-->
<!--            type="success"-->
<!--            plain-->
<!--            icon="el-icon-upload2"-->
<!--            size="mini"-->
<!--            @click="handleImport"-->
<!--            v-hasPermi="['system:course:import']">-->
<!--            导入-->
<!--          </el-button>-->
<!--        </el-tooltip>-->
<!--      </el-col>-->

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="courseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="课程代码" align="center" prop="courseCode" />
      <el-table-column label="课程名称" align="center" prop="courseName" />
      <el-table-column label="开课院系" align="center" prop="academy" />
      <el-table-column label="授课教师" align="center" prop="teacherName" />
      <el-table-column label="学分值" align="center" prop="credit" />
      <el-table-column label="总课时数" align="center" prop="courseHours" />
      <el-table-column label="课程类型" align="center" prop="courseCategory" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:course:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:course:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
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

        <!-- 移除总课时表单项，保留学分输入 -->
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

        <!-- 隐藏存储字段（如果需要保存数据） -->
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
