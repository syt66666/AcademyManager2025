<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学号" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入学号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程编码" prop="courseCode">
        <el-input
          v-model="queryParams.courseCode"
          placeholder="请输入课程编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="修读学年" prop="academicYear">
        <el-input
          v-model="queryParams.academicYear"
          placeholder="请输入修读学年"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="修读学期" prop="semester">
        <el-input
          v-model="queryParams.semester"
          placeholder="请输入修读学期"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成绩值" prop="scoreValue">
        <el-input
          v-model="queryParams.scoreValue"
          placeholder="请输入成绩值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程绩点" prop="gpa">
        <el-input
          v-model="queryParams.gpa"
          placeholder="请输入课程绩点"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="录入人姓名" prop="nickName">
        <el-input
          v-model="queryParams.nickName"
          placeholder="请输入录入人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数据插入时间" prop="uploadTime">
        <el-date-picker clearable
                        v-model="queryParams.uploadTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择数据插入时间">
        </el-date-picker>
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
          v-hasPermi="['system:score:add']"
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
          v-hasPermi="['system:score:edit']"
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
          v-hasPermi="['system:score:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:score:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="scoreList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="scoreId" />
      <el-table-column label="学号" align="center" prop="studentId" />
      <el-table-column label="课程编码" align="center" prop="courseCode" />
      <el-table-column label="修读学年" align="center" prop="academicYear" />
      <el-table-column label="修读学期" align="center" prop="semester" />
      <el-table-column label="成绩值" align="center" prop="scoreValue" />
      <el-table-column label="课程绩点" align="center" prop="gpa" />
      <el-table-column label="成绩类型：正考，补考，重修，免修" align="center" prop="scoreType" />
      <el-table-column label="录入人姓名" align="center" prop="nickName" />
      <el-table-column label="数据插入时间" align="center" prop="uploadTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.uploadTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:score:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:score:remove']"
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

    <!-- 添加或修改【请填写功能名称】对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="课程编码" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程编码" />
        </el-form-item>
        <el-form-item label="修读学年" prop="academicYear">
          <el-input v-model="form.academicYear" placeholder="请输入修读学年" />
        </el-form-item>
        <el-form-item label="修读学期" prop="semester">
          <el-input v-model="form.semester" placeholder="请输入修读学期" />
        </el-form-item>
        <el-form-item label="成绩值" prop="scoreValue">
          <el-input v-model="form.scoreValue" placeholder="请输入成绩值" />
        </el-form-item>
        <el-form-item label="课程绩点" prop="gpa">
          <el-input v-model="form.gpa" placeholder="请输入课程绩点" />
        </el-form-item>
        <el-form-item label="录入人姓名" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入录入人姓名" />
        </el-form-item>
        <el-form-item label="数据插入时间" prop="uploadTime">
          <el-date-picker clearable
                          v-model="form.uploadTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择数据插入时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listScore, getScore, delScore, addScore, updateScore } from "@/api/system/score";

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
      // 【请填写功能名称】表格数据
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
        academicYear: null,
        semester: null,
        scoreValue: null,
        gpa: null,
        scoreType: null,
        nickName: null,
        uploadTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        studentId: [
          { required: true, message: "学号不能为空", trigger: "blur" }
        ],
        courseCode: [
          { required: true, message: "课程编码不能为空", trigger: "blur" }
        ],
        academicYear: [
          { required: true, message: "修读学年不能为空", trigger: "blur" }
        ],
        semester: [
          { required: true, message: "修读学期不能为空", trigger: "blur" }
        ],
        scoreValue: [
          { required: true, message: "成绩值不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询【请填写功能名称】列表 */
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加【请填写功能名称】";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const scoreId = row.scoreId || this.ids
      getScore(scoreId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改【请填写功能名称】";
      });
    },
    /** 提交按钮 */
    submitForm() {
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
      this.$modal.confirm('是否确认删除【请填写功能名称】编号为"' + scoreIds + '"的数据项？').then(function() {
        return delScore(scoreIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/score/export', {
        ...this.queryParams
      }, `score_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
