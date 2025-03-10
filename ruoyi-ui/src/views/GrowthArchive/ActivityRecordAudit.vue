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
      <el-form-item label="活动名称" prop="activityName">
        <el-input
          v-model="queryParams.activityName"
          placeholder="请输入活动名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="获奖级别" prop="activityLevel">
        <el-select
          v-model="queryParams.activityLevel"
          placeholder="请选择活动级别"
          clearable
          @keyup.enter.native="handleQuery"
          style="width: 100%"
        >
          <el-option
            v-for="item in activityLevelOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select
          v-model="queryParams.auditStatus"
          placeholder="请选择审核状态"
          clearable
          @keyup.enter.native="handleQuery"
          style="width: 100%"
        >
          <el-option
            v-for="item in auditStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
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
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:activity:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="activityList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="index" label="序号" width="50"></el-table-column>-->
      <el-table-column label="序号" width="50" align="center">
        <template slot-scope="scope">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="学号" align="center" prop="studentId" />
      <el-table-column label="活动名称" align="center" prop="activityName" />
      <el-table-column label="获奖级别" align="center" prop="activityLevel" />
      <el-table-column label="获奖奖项" align="center" prop="awardLevel" />
      <el-table-column label="获奖日期" align="center" prop="awardDate" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.awardDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="证明材料" align="center" prop="proofMaterial">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.proofMaterial"
            type="primary"
            size="mini"
            @click="handleDownload(scope.row.proofMaterial)">
            <i class="el-icon-download"></i> 下载
          </el-button>
          <span v-else>无材料</span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="applyTime" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>

        <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.auditStatus)">
            {{ scope.row.auditStatus || '未审核' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核时间" align="center" prop="auditTime" width="150">
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
            icon="el-icon-check"
            @click="handleAudit(scope.row, '通过')"
            v-hasPermi="['system:activity:audit']"
            v-if="scope.row.auditStatus !== '已通过'"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleAudit(scope.row, '拒绝')"
            v-hasPermi="['system:activity:audit']"
            v-if="scope.row.auditStatus !== '未通过'"
          >拒绝</el-button>
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
  </div>
</template>

<script>
import { listAuditActivity, getActivity, auditActivity  } from "@/api/system/activity";

export default {
  name: "Activity",
    data() {
    return {
      auditStatusOptions: [
        { value: '未审核', label: '未审核' },
        { value: '已通过', label: '已通过' },
        { value: '未通过', label: '未通过' }
      ],
      activityLevelOptions: [
        { value: '院级', label: '院级' },
        { value: '校级', label: '校级' },
        { value: '省级', label: '省级' },
        { value: '国家级', label: '国家级' },
        { value: '国际级', label: '国际级' }
      ],
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
      // 学生文体活动记录表格数据
      activityList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        activityName: null,
        activityLevel: null,
        awardLevel: null,
        scholarshipPoints: null,
        awardDate: null,
        proofMaterial: null,
        semester: null,
        applyTime: null,
        nickName: null,
        auditStatus: null,
        auditTime: null,
        auditRemark: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        studentId: [
          { required: true, message: "学号不能为空", trigger: "blur" }
        ],
        activityName: [
          { required: true, message: "活动名称不能为空", trigger: "blur" }
        ],
        activityLevel: [
          { required: true, message: "活动级别不能为空", trigger: "blur" }
        ],
        awardLevel: [
          { required: true, message: "活动奖项不能为空", trigger: "blur" }
        ],
        scholarshipPoints: [
          { required: true, message: "折合奖学金分数不能为空", trigger: "blur" }
        ],
        awardDate: [
          { required: true, message: "获奖日期不能为空", trigger: "blur" }
        ],
        proofMaterial: [
          { required: true, message: "证明材料路径不能为空", trigger: "blur" }
        ],
        semester: [
          { required: true, message: "修读学期不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询学生文体活动记录列表 */
    getList() {
      this.loading = true;
      listAuditActivity(this.queryParams).then(response => {
        this.activityList = response.rows;
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
        activityId: null,
        studentId: null,
        activityName: null,
        activityLevel: null,
        awardLevel: null,
        scholarshipPoints: null,
        awardDate: null,
        proofMaterial: null,
        semester: null,
        applyTime: null,
        nickName: null,
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
      this.ids = selection.map(item => item.activityId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加学生文体活动记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const activityId = row.activityId || this.ids
      getActivity(activityId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改学生文体活动记录";
      });
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('system/activity/export', {
        ...this.queryParams
      }, `activity_${new Date().getTime()}.xlsx`)
    },

    // 文件下载处理
    handleDownload(filePath) {
      if (!filePath) {
        this.$message.warning('无可下载文件');
        return;
      }

      // 处理文件路径
      const fullPath = filePath.startsWith('http')
        ? filePath
        : `${process.env.VUE_APP_BASE_API}${filePath}`;

      // 创建隐藏下载链接
      const link = document.createElement('a');
      link.href = fullPath + `?t=${Date.now()}`; // 防止缓存
      link.download = this.getFileName(filePath);
      link.style.display = 'none';

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    // 获取文件名
    getFileName(path) {
      return path.split('/').pop() || 'proof-material';
    },

    // 审核状态标签样式
    getStatusTagType(status) {
      const statusMap = {
        '已通过': 'success',
        '未通过': 'danger',
        '未审核': 'warning'
      };
      return statusMap[status] || 'info';
    },

    // 审核操作
    handleAudit(row, status) {
      const isApproved = status === '通过';
      const statusMapping = {
        '通过': '已通过',
        '拒绝': '未通过'
      };

      this.$prompt(
        isApproved ? '确认通过审核吗？' : '请输入拒绝原因',
        '审核确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: isApproved ? null : /.+/,
          inputErrorMessage: '拒绝原因不能为空'
        }
      ).then(({ value }) => {
        // 构建符合API要求的参数
        const auditData = {
          activityId: row.activityId,
          auditStatus: statusMapping[status],
          auditRemark: isApproved ? '系统审核通过' : value
        };

        // 调用专用审核接口
        auditActivity(auditData).then(response => {
          this.$modal.msgSuccess(`已${status}审核`);
          this.getList(); // 刷新列表
        }).catch(error => {
          console.error('审核操作失败:', error);
          this.$modal.msgError(`${status}审核失败: ${error.message || ''}`);
        });
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    }
  }
};
</script>
