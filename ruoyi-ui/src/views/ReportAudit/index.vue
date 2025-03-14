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
      <el-form-item label="题目" prop="reportTitle">
        <el-input
          v-model="queryParams.reportTitle"
          placeholder="请输入题目"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报告人" prop="reporter">
        <el-input
          v-model="queryParams.reporter"
          placeholder="请输入报告人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报告时间" prop="reportDate">
        <el-date-picker clearable
                        v-model="queryParams.reportDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择报告时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="报告链接" prop="reportLink">
        <el-input
          v-model="queryParams.reportLink"
          placeholder="请输入报告链接"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报告心得体会" prop="reportFeeling">
        <el-input
          v-model="queryParams.reportFeeling"
          placeholder="请输入报告心得体会"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报告提交时间" prop="reportAdmitTime">
        <el-date-picker clearable
                        v-model="queryParams.reportAdmitTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择报告提交时间">
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
      <el-form-item label="审核人姓名" prop="nickName">
        <el-input
          v-model="queryParams.nickName"
          placeholder="请输入审核人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上传学期" prop="semester">
        <el-input
          v-model="queryParams.semester"
          placeholder="请输入上传学期"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:report:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reportList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报告唯一编号" align="center" prop="reportId" />
      <el-table-column label="学生学号" align="center" prop="studentId" />
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="题目" align="center" prop="reportTitle" />
      <el-table-column label="报告人" align="center" prop="reporter" />
      <el-table-column label="报告时间" align="center" prop="reportDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reportDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报告内容简介" align="center" prop="reportContent" />
      <el-table-column label="报告链接" align="center" prop="reportLink" />
      <el-table-column label="报告心得体会" align="center" prop="reportFeeling" />
      <el-table-column label="演讲海报" align="center" prop="lecturePoster" />
      <el-table-column label="报告现场图片" align="center" prop="reportPicture" />
      <el-table-column label="报告提交时间" align="center" prop="reportAdmitTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reportAdmitTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核意见" align="center" prop="auditRemark" />
      <el-table-column label="审核人姓名" align="center" prop="nickName" />
      <el-table-column label="上传学期" align="center" prop="semester" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:report:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:report:remove']"
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

    <!-- 添加或修改学生参与报告信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生学号" />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入学生姓名" />
        </el-form-item>
        <el-form-item label="题目" prop="reportTitle">
          <el-input v-model="form.reportTitle" placeholder="请输入题目" />
        </el-form-item>
        <el-form-item label="报告人" prop="reporter">
          <el-input v-model="form.reporter" placeholder="请输入报告人" />
        </el-form-item>
        <el-form-item label="报告时间" prop="reportDate">
          <el-date-picker clearable
                          v-model="form.reportDate"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择报告时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="报告内容简介">
          <editor v-model="form.reportContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="报告链接" prop="reportLink">
          <el-input v-model="form.reportLink" placeholder="请输入报告链接" />
        </el-form-item>
        <el-form-item label="报告心得体会" prop="reportFeeling">
          <el-input v-model="form.reportFeeling" placeholder="请输入报告心得体会" />
        </el-form-item>
        <el-form-item label="演讲海报" prop="lecturePoster">
          <el-input v-model="form.lecturePoster" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="报告现场图片" prop="reportPicture">
          <el-input v-model="form.reportPicture" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="报告提交时间" prop="reportAdmitTime">
          <el-date-picker clearable
                          v-model="form.reportAdmitTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择报告提交时间">
          </el-date-picker>
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
        <el-form-item label="审核人姓名" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入审核人姓名" />
        </el-form-item>
        <el-form-item label="上传学期" prop="semester">
          <el-input v-model="form.semester" placeholder="请输入上传学期" />
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
import { listReport } from "@/api/student/letcure";

export default {
  name: "Report",
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
      // 学生参与报告信息表格数据
      reportList: [],
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
        reportTitle: null,
        reporter: null,
        reportDate: null,
        reportContent: null,
        reportLink: null,
        reportFeeling: null,
        lecturePoster: null,
        reportPicture: null,
        reportAdmitTime: null,
        auditStatus: null,
        auditTime: null,
        auditRemark: null,
        nickName: null,
        semester: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询学生参与报告信息列表 */
    getList() {
      this.loading = true;
      listReport(this.queryParams).then(response => {
        this.reportList = response.rows;
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
        reportId: null,
        studentId: null,
        studentName: null,
        reportTitle: null,
        reporter: null,
        reportDate: null,
        reportContent: null,
        reportLink: null,
        reportFeeling: null,
        lecturePoster: null,
        reportPicture: null,
        reportAdmitTime: null,
        auditStatus: null,
        auditTime: null,
        auditRemark: null,
        nickName: null,
        semester: null
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
      this.ids = selection.map(item => item.reportId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加学生参与报告信息";
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('system/report/export', {
        ...this.queryParams
      }, `report_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
