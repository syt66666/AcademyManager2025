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
<!--      <el-form-item label="折合奖学金分数" prop="scholarshipPoints">-->
<!--        <el-input-->
<!--          v-model="queryParams.scholarshipPoints"-->
<!--          placeholder="请输入折合奖学金分数"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="获奖日期" prop="awardDate">-->
<!--        <el-date-picker clearable-->
<!--                        v-model="queryParams.awardDate"-->
<!--                        type="date"-->
<!--                        value-format="yyyy-MM-dd"-->
<!--                        placeholder="请选择获奖日期">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="证明材料路径" prop="proofMaterial">-->
<!--        <el-input-->
<!--          v-model="queryParams.proofMaterial"-->
<!--          placeholder="请输入证明材料路径"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item label="修读学期" prop="semester">
        <el-input
          v-model="queryParams.semester"
          placeholder="请输入修读学期"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提交时间" prop="applyTime">
        <el-date-picker clearable
                        v-model="queryParams.applyTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择提交时间">
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
          v-hasPermi="['system:activity:add']"
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
          v-hasPermi="['system:activity:edit']"
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
          v-hasPermi="['system:activity:remove']"
        >删除</el-button>
      </el-col>
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
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="activityId" />
      <el-table-column label="学号" align="center" prop="studentId" />
      <el-table-column label="活动名称" align="center" prop="activityName" />
      <el-table-column label="活动级别" align="center" prop="activityLevel" />
      <el-table-column label="活动奖项" align="center" prop="awardLevel" />
      <el-table-column label="折合奖学金分数" align="center" prop="scholarshipPoints" />
      <el-table-column label="获奖日期" align="center" prop="awardDate" width="180">
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
      <el-table-column label="修读学期" align="center" prop="semester" />
      <el-table-column label="提交时间" align="center" prop="applyTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核人姓名" align="center" prop="nickName" />
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.auditStatus)">
            {{ scope.row.auditStatus || '未审核' }}
          </el-tag>
        </template>
      </el-table-column>
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
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:activity:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:activity:remove']"
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

    <!-- 添加或修改学生文体活动记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="活动名称" prop="activityName">
          <el-input v-model="form.activityName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="折合奖学金分数" prop="scholarshipPoints">
          <el-input v-model="form.scholarshipPoints" placeholder="请输入折合奖学金分数" />
        </el-form-item>
        <el-form-item label="获奖日期" prop="awardDate">
          <el-date-picker clearable
                          v-model="form.awardDate"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择获奖日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="证明材料" prop="proofMaterial">
          <el-input v-model="form.proofMaterial" placeholder="请输入证明材料" />
        </el-form-item>
        <el-form-item label="修读学期" prop="semester">
          <el-input v-model="form.semester" placeholder="请输入修读学期" />
        </el-form-item>
        <el-form-item label="提交时间" prop="applyTime">
          <el-date-picker clearable
                          v-model="form.applyTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择提交时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核人姓名" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入审核人姓名" />
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
import { listActivity, getActivity, delActivity, addActivity, updateActivity } from "@/api/system/activity";

export default {
  name: "Activity",
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
      listActivity(this.queryParams).then(response => {
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
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.activityId != null) {
            updateActivity(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addActivity(this.form).then(response => {
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
      const activityIds = row.activityId || this.ids;
      this.$modal.confirm('是否确认删除学生文体活动记录编号为"' + activityIds + '"的数据项？').then(function() {
        return delActivity(activityIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
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
    handleAudit(row, action) {
      const isApprove = action === '通过';
      const promptConfig = {
        title: `审核${action}`,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: isApprove ? null : /.+/,
        inputErrorMessage: '必须填写拒绝原因'
      };

      this.$prompt(
        isApprove ? '确认通过审核吗？' : '请输入拒绝原因：',
        promptConfig
      ).then(({ value }) => {
        const params = {
          activityId: row.activityId,
          auditStatus: isApprove ? '已通过' : '未通过',
          auditRemark: value || (isApprove ? '审核通过' : '')
        };

        return auditActivity(params).then(() => {
          this.$modal.msgSuccess(`已${action}审核`);
          this.getList(); // 刷新列表
        });
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    },

  }
};
</script>
