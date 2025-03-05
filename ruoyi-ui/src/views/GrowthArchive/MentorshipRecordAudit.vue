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
      <el-form-item label="导师工号" prop="tutorId">
        <el-input
          v-model="queryParams.tutorId"
          placeholder="请输入导师工号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="指导主题" prop="guidanceTopic">
        <el-input
          v-model="queryParams.guidanceTopic"
          placeholder="请输入指导主题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="指导地点" prop="guidanceLocation">
        <el-input
          v-model="queryParams.guidanceLocation"
          placeholder="请输入指导地点"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="指导时间" prop="guidanceTime">
        <el-date-picker clearable
                        v-model="queryParams.guidanceTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择指导时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="总结文档路径" prop="summaryFilePath">
        <el-input
          v-model="queryParams.summaryFilePath"
          placeholder="请输入总结文档路径"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提交时间" prop="submitTime">
        <el-date-picker clearable
                        v-model="queryParams.submitTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择提交时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker clearable
                        v-model="queryParams.auditTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择审核时间">
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
          v-hasPermi="['system:mentorship:add']"
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
          v-hasPermi="['system:mentorship:edit']"
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
          v-hasPermi="['system:mentorship:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:mentorship:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="mentorshipList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录ID" align="center" prop="recordId" />
      <el-table-column label="学生学号" align="center" prop="studentId" />
      <el-table-column label="导师工号" align="center" prop="tutorId" />
      <el-table-column label="指导主题" align="center" prop="guidanceTopic" />
      <el-table-column label="指导地点" align="center" prop="guidanceLocation" />
      <el-table-column label="指导时间" align="center" prop="guidanceTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.guidanceTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总结文档路径" align="center" prop="summaryFilePath" />
      <el-table-column label="照片路径数组" align="center" prop="photoPaths" />
      <el-table-column label="学期" align="center" prop="semester" />
      <el-table-column label="提交时间" align="center" prop="submitTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.submitTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="学生评价" align="center" prop="studentComment" />
      <el-table-column label="审核状态" align="center" prop="auditStatus" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核意见" align="center" prop="auditRemark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:mentorship:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:mentorship:remove']"
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

    <!-- 添加或修改导师指导记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生学号" />
        </el-form-item>
        <el-form-item label="导师工号" prop="tutorId">
          <el-input v-model="form.tutorId" placeholder="请输入导师工号" />
        </el-form-item>
        <el-form-item label="指导主题" prop="guidanceTopic">
          <el-input v-model="form.guidanceTopic" placeholder="请输入指导主题" />
        </el-form-item>
        <el-form-item label="指导地点" prop="guidanceLocation">
          <el-input v-model="form.guidanceLocation" placeholder="请输入指导地点" />
        </el-form-item>
        <el-form-item label="指导时间" prop="guidanceTime">
          <el-date-picker clearable
                          v-model="form.guidanceTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择指导时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="总结文档路径" prop="summaryFilePath">
          <el-input v-model="form.summaryFilePath" placeholder="请输入总结文档路径" />
        </el-form-item>
        <el-form-item label="提交时间" prop="submitTime">
          <el-date-picker clearable
                          v-model="form.submitTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择提交时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="学生评价" prop="studentComment">
          <el-input v-model="form.studentComment" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker clearable
                          v-model="form.auditTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择审核时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核意见" prop="auditRemark">
          <el-input v-model="form.auditRemark" type="textarea" placeholder="请输入内容" />
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
import { listMentorship, getMentorship, delMentorship, addMentorship, updateMentorship } from "@/api/system/mentorship";

export default {
  name: "Mentorship",
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
      // 导师指导记录表格数据
      mentorshipList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        tutorId: null,
        guidanceTopic: null,
        guidanceLocation: null,
        guidanceTime: null,
        summaryFilePath: null,
        photoPaths: null,
        semester: null,
        submitTime: null,
        studentComment: null,
        auditStatus: null,
        auditTime: null,
        auditRemark: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        studentId: [
          { required: true, message: "学生学号不能为空", trigger: "blur" }
        ],
        tutorId: [
          { required: true, message: "导师工号不能为空", trigger: "blur" }
        ],
        guidanceTopic: [
          { required: true, message: "指导主题不能为空", trigger: "blur" }
        ],
        guidanceTime: [
          { required: true, message: "指导时间不能为空", trigger: "blur" }
        ],
        semester: [
          { required: true, message: "学期不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询导师指导记录列表 */
    getList() {
      this.loading = true;
      listMentorship(this.queryParams).then(response => {
        this.mentorshipList = response.rows;
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
        recordId: null,
        studentId: null,
        tutorId: null,
        guidanceTopic: null,
        guidanceLocation: null,
        guidanceTime: null,
        summaryFilePath: null,
        photoPaths: null,
        semester: null,
        submitTime: null,
        studentComment: null,
        auditStatus: null,
        auditTime: null,
        auditRemark: null
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
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加导师指导记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const recordId = row.recordId || this.ids
      getMentorship(recordId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改导师指导记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.recordId != null) {
            updateMentorship(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMentorship(this.form).then(response => {
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
      const recordIds = row.recordId || this.ids;
      this.$modal.confirm('是否确认删除导师指导记录编号为"' + recordIds + '"的数据项？').then(function() {
        return delMentorship(recordIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/mentorship/export', {
        ...this.queryParams
      }, `mentorship_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
