<template>
  <div class="app-container">
    <!-- 统计看板 -->
    <el-row :gutter="12" class="mb-4">
      <el-col :span="8">
        <el-card
          class="status-card pending"
          shadow="hover"
          @click.native="handleStatusClick('pending')"
        >
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-warning-outline"></i>
            </div>
            <div class="card-info">
              <div class="title">未审核</div>
              <div class="count">{{ auditStats.pending }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card
          class="status-card approved"
          shadow="hover"
          @click.native="handleStatusClick('approved')"
        >
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-success"></i>
            </div>
            <div class="card-info">
              <div class="title">已通过</div>
              <div class="count">{{ auditStats.approved }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card
          class="status-card rejected"
          shadow="hover"
          @click.native="handleStatusClick('rejected')"
        >
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-error"></i>
            </div>
            <div class="card-info">
              <div class="title">未通过</div>
              <div class="count">{{ auditStats.rejected }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 查询表单 -->
    <el-card class="query-form">
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

        <el-form-item label="获奖时间" prop="awardDate">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            @change="handleDateChange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 350px"
          ></el-date-picker>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-container">
      <div slot="header" class="table-header">
        <span class="table-title">文体活动审核</span>
        <div class="action-bar">
          <el-button
            type="success"
            icon="el-icon-download"
            class="action-btn export-btn"
            @click="handleExport"
            v-hasPermi="['system:activity:export']"
          >数据导出
          </el-button>
        </div>
      </div>
      <el-table
        :data="activityList"
        style="width: 100%"
        class="enhanced-table"
        :header-cell-style="headerStyle"
        :row-class-name="tableRowClassName"
        @row-click="handleRowClick"
      >
        <el-table-column label="序号" width="80" align="center">
          <template slot-scope="scope">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="学生学号" align="center" prop="studentId"/>
        <el-table-column label="学生姓名" align="center" prop="studentName" width="100"/>
        <el-table-column label="活动名称" align="center" prop="activityName"/>
        <el-table-column label="活动级别" align="center" prop="activityLevel" width="100"/>
        <el-table-column label="获奖奖项" align="center" prop="awardLevel" width="100"/>
        <el-table-column label="获奖日期" align="center" prop="awardDate" width="140">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.awardDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="证明材料" width="160" align="center">
          <template v-slot="scope">
            <el-dropdown trigger="click" @command="handleFileCommand" @click.native.stop
                         :disabled="!scope.row.proofMaterial || scope.row.proofMaterial.length === 0">
              <el-button type="primary" size="mini" plain @click.stop
                         class="file-btn"
                         :disabled="!scope.row.proofMaterial || scope.row.proofMaterial.length === 0">
                <i class="el-icon-document"></i> 图片操作
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  :command="{ action: 'preview', files: scope.row.proofMaterial }"
                  :disabled="!scope.row.proofMaterial"
                >
                  <i class="el-icon-view"></i>预览
                </el-dropdown-item>
                <el-dropdown-item
                  :command="{ action: 'download', files: scope.row.proofMaterial }"
                  :disabled="!scope.row.proofMaterial"
                >
                  <i class="el-icon-download"></i>下载
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" align="center" prop="applyTime" width="140">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" align="center" prop="auditStatus" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.auditStatus)">
              {{ scope.row.auditStatus || '未审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width op-column">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-check"
              @click="handleAudit(scope.row, '通过')"
              v-hasPermi="['system:activity:audit']"
              v-if="scope.row.auditStatus !== '已通过'"
            >通过
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-close"
              @click="handleAudit(scope.row, '拒绝')"
              v-hasPermi="['system:activity:audit']"
              v-if="scope.row.auditStatus !== '未通过'"
            >拒绝
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-notebook-2"
              @click="showAuditHistory(scope.row)"
              v-hasPermi="['system:history:list']"
            >日志
            </el-button>
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
    </el-card>
    <!-- 图片预览对话框 -->
    <el-dialog :visible.sync="previewVisible" title="图片预览" width="60%">
      <div style="text-align: center; margin-bottom: 20px;">
        <img
          :src="previewImages[currentPreviewIndex]"
          style="max-width: 100%; display: block; margin: 0 auto;"
          alt="证明材料预览"
        />
        <el-button
          icon="el-icon-arrow-left"
          :disabled="currentPreviewIndex === 0"
          @click="currentPreviewIndex--"
        ></el-button>
        <span style="margin: 0 20px;">{{ currentPreviewIndex + 1 }} / {{ previewImages.length }}</span>
        <el-button
          icon="el-icon-arrow-right"
          :disabled="currentPreviewIndex === previewImages.length - 1"
          @click="currentPreviewIndex++"
        ></el-button>
      </div>

      <div slot="footer">
        <el-button
          type="primary"
          @click="downloadSingleFile(previewImages[currentPreviewIndex])"
          style="background-color: #42b983; border-color: #42b983;"
        >
          <i class="el-icon-download"></i> 下载当前图片
        </el-button>
      </div>
    </el-dialog>

    <!-- 审核历史对话框-->
    <el-dialog
      title="审核历史"
      :visible.sync="historyVisible"
      width="70%"
      append-to-body
    >
      <el-table
        v-loading="historyLoading"
        :data="auditHistoryList"
        border
        style="margin-top: 15px;"
      >
        <el-table-column label="操作类型" align="center" prop="auditAction" width="120"/>
        <el-table-column label="审核前状态" align="center" prop="auditStatusBefore">
          <template slot-scope="{row}">
            <el-tag :type="getStatusTagType(row.auditStatusBefore)" size="mini">
              {{ row.auditStatusBefore || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核后状态" align="center" prop="auditStatusAfter">
          <template slot-scope="{row}">
            <el-tag :type="getStatusTagType(row.auditStatusAfter)" size="mini">
              {{ row.auditStatusAfter }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核人" align="center" prop="auditorId" width="120"/>
        <el-table-column label="审核时间" align="center" prop="auditTime" width="160">
          <template slot-scope="{row}">
            {{ parseTime(row.auditTime) }}
          </template>
        </el-table-column>
        <el-table-column label="审核意见" align="center" prop="auditRemark" show-overflow-tooltip/>
        <el-table-column label="操作信息" align="center" width="200">
          <template slot-scope="{row}">
            <div v-if="row.ipAddress" class="operation-info">
              <span>IP: {{ row.ipAddress }}</span><br>
              <span>设备: {{ row.deviceInfo || '未知' }}</span>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="historyTotal > 0"
        :total="historyTotal"
        :page.sync="historyQueryParams.pageNum"
        :limit.sync="historyQueryParams.pageSize"
        @pagination="getAuditHistory"
      />
    </el-dialog>

  </div>
</template>

<script>
import {listAuditActivity, getActivity, auditActivity, getAuditCount} from "@/api/system/activity";
import {listAuditHistory} from "@/api/student/audit";
import axios from "axios";

export default {
  name: "Activity",
  data() {
    return {
      // 统计数字
      auditStats: {
        approved: 0,
        rejected: 0,
        pending: 0
      },
      // 审核历史相关
      historyVisible: false,
      historyLoading: false,
      auditHistoryList: [],
      historyTotal: 0,
      historyQueryParams: {
        pageNum: 1,
        pageSize: 10,
        moduleType: 2, // 固定查询文体活动类型
        moduleId: null,
      },
      previewVisible: false,
      currentDownloadFile: '',
      previewImages: [],
      currentPreviewIndex: 0,
      dateRange: [],
      auditStatusOptions: [
        {value: '未审核', label: '未审核'},
        {value: '已通过', label: '已通过'},
        {value: '未通过', label: '未通过'}
      ],
      activityLevelOptions: [
        {value: '院级', label: '院级'},
        {value: '校级', label: '校级'},
        {value: '省级', label: '省级'},
        {value: '国家级', label: '国家级'},
        {value: '国际级', label: '国际级'}
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
        awardDateBegin: null,
        awardDateEnd: null,
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
          {required: true, message: "学号不能为空", trigger: "blur"}
        ],
        activityName: [
          {required: true, message: "活动名称不能为空", trigger: "blur"}
        ],
        activityLevel: [
          {required: true, message: "活动级别不能为空", trigger: "blur"}
        ],
        awardLevel: [
          {required: true, message: "活动奖项不能为空", trigger: "blur"}
        ],
        awardDate: [
          {required: true, message: "获奖日期不能为空", trigger: "blur"}
        ],
        proofMaterial: [
          {required: true, message: "证明材料路径不能为空", trigger: "blur"}
        ],
        semester: [
          {required: true, message: "修读学期不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
    this.fetchAuditCount();
  },
  methods: {

    // 显示审核历史
    async showAuditHistory(row) {
      this.historyQueryParams.moduleId = row.activityId
      this.historyVisible = true
      await this.getAuditHistory()
    },

    // 获取审核历史记录
    async getAuditHistory() {
      this.historyLoading = true
      try {
        const response = await listAuditHistory(this.historyQueryParams)
        this.auditHistoryList = response.rows
        this.historyTotal = response.total
      } finally {
        this.historyLoading = false
      }
    },
    // 获取统计数据
    async fetchAuditCount() {
      try {
        const {code, data} = await getAuditCount();
        if (code === 200) {
          this.auditStats = data;
        }
      } catch (error) {
        console.error("获取统计数据失败:", error);
      }
    },

    // 点击统计卡片筛选数据
    handleStatusClick(type) {
      const statusMap = {
        pending: '未审核',
        approved: '已通过',
        rejected: '未通过'
      };

      this.queryParams.auditStatus = statusMap[type];
      this.handleQuery(); // 调用现有的查询方法
    },
    handleFileCommand(command) {
      if (command.action === 'preview') {
        this.handlePreview(command.files)
      } else if (command.action === 'download') {
        this.downloadFiles(command.files)
      }
    },
    // 预览图片
    handlePreview(filePath) {
      try {
        const paths = typeof filePath === 'string'
          ? JSON.parse(filePath)
          : filePath;

        if (paths.length > 0) {
          this.previewImages = paths.map(path => this.getFullUrl(path));
          this.currentPreviewIndex = 0;
          this.currentDownloadFile = paths[0];
          this.previewVisible = true;
        }
      } catch (error) {
        console.error('预览失败:', error);
        this.$message.error('预览失败：文件路径格式不正确');
      }
    },
    async downloadFiles(filePaths) {
      try {
        const paths = typeof filePaths === 'string' ? JSON.parse(filePaths) : filePaths;
        if (paths.length >= 1) {
          this.$confirm(`本次下载包含${paths.length}个文件，是否继续？`, '批量下载提示', {
            confirmButtonText: '立即下载',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            paths.forEach(path => {
              const url = `${process.env.VUE_APP_BASE_API}/profile/${path}`;
              this.downloadSingleFile(url);
            });
          });
        }
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
      }
    },
    // 下载单个文件
    async downloadSingleFile(filePath) {
      try {
        const response = await axios.get(filePath, {
          responseType: 'blob',
          headers: {Authorization: "Bearer " + localStorage.getItem("token")}
        });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', this.generateFileName(filePath));
        document.body.appendChild(link);
        link.click();
        URL.revokeObjectURL(url);
        link.remove();
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
      }
    },
    // 生成带时间戳的文件名
    generateFileName() {
      const date = new Date().toISOString().slice(0, 10);
      const ext = this.getFileExtension();
      return `activity_${date}_${Math.random().toString(36).substr(2, 5)}.${ext}`;
    },

    // 获取文件扩展名
    getFileExtension() {
      try {
        return this.currentImage.split('.').pop().split(/[#?]/)[0] || 'jpg';
      } catch {
        return 'jpg';
      }
    },
    // 新增获取完整URL的方法
    getFullUrl(filePath) {
      return `${process.env.VUE_APP_BASE_API}/profile/${filePath}`;
    },
    // 日期选择变化事件
    handleDateChange(range) {
      // 处理空值情况
      if (range && range.length === 2) {
        this.queryParams.awardDateBegin = range[0];
        this.queryParams.awardDateEnd = range[1];
      } else {
        this.queryParams.awardDateBegin = null;
        this.queryParams.awardDateEnd = null;
      }
      console.log('时间参数:', this.queryParams.awardDateBegin, this.queryParams.awardDateEnd);
    },
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
        auditRemark: null,
        awardDateBegin: null,
        awardDateEnd: null
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
      // 清空日期选择器
      this.dateRange = [];
      // 重置查询参数
      this.queryParams = {
        ...this.queryParams,
        auditStatus: null,
        awardDateBegin: null,
        awardDateEnd: null
      };
      // 触发查询
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.activityId)
      this.single = selection.length !== 1
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
      // 确保导出参数包含时间范围
      const exportParams = {
        ...this.queryParams,
        awardDateBegin: this.queryParams.awardDateBegin,
        awardDateEnd: this.queryParams.awardDateEnd
      };
      this.download('system/activity/export', exportParams, `activity_${new Date().getTime()}.xlsx`);
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
      ).then(({value}) => {
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
    },
    submitFileForm() {
      this.$refs.upload.submit();
    },
    tableRowClassName({ rowIndex }) {
      return rowIndex % 2 === 0 ? 'striped-row' : '';
    },

    // 自定义表头样式
    headerStyle() {
      return {
        color: '#2d3540',
        fontWeight: 600,
        fontSize: '14px'
      }
    },
    // 行点击处理
    handleRowClick(row) {
      this.$emit('row-click', row)
    }
  }
};
</script>

<style scoped>
/* ================= 全局样式变量 ================= */
.app-container {
  --primary-color: #409eff;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --info-color: #909399;
  --border-radius: 8px;
  --box-shadow: 0 3px 10px rgba(0, 0, 0, 0.06);
  margin-left: 100px;
}

/* ================= 统计卡片 ================= */
.status-card {
  position: relative;
  overflow: hidden;
  border: none;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  cursor: pointer;
  background: rgba(255, 255, 255, 0.95) !important;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  border-radius: 12px;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    padding: 1px;
    background: linear-gradient(
      135deg,
      rgba(200, 200, 200, 0.3),
      rgba(250, 250, 250, 0.2) 50%,
      rgba(200, 200, 200, 0.3)
    );
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);

    &::before {
      opacity: 0.8;
      background: linear-gradient(
        135deg,
        rgba(180, 180, 180, 0.4),
        rgba(220, 220, 220, 0.3) 50%,
        rgba(180, 180, 180, 0.4)
      );
    }

    .card-icon {
      transform: rotate(8deg) scale(1.05);
    }

    .wave-effect {
      transform: translateX(100%);
    }
  }
}

.wave-effect {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    to right,
    rgba(255, 255, 255, 0.1) 20%,
    rgba(255, 255, 255, 0.2) 50%,
    rgba(255, 255, 255, 0.1) 80%
  );
  transform: translateX(-30%);
  transition: transform 1.2s ease-in-out;
  mix-blend-mode: overlay;
}

.card-content {
  position: relative;
  padding: 20px;
  z-index: 2;
  display: flex;
  align-items: center;
}

.card-icon {
  width: 64px;
  height: 64px;
  margin-right: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.6s cubic-bezier(0.68, -0.55, 0.27, 1.55);

  i {
    font-size: 28px;
  }
}

.card-info {
  .title {
    color: #666;
    font-size: 14px;
    letter-spacing: 0.5px;
    margin-bottom: 6px;
  }

  .count {
    font-size: 28px;
    font-weight: 600;
    background: linear-gradient(135deg, #666, #444);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    transition: all 0.4s ease;
  }
}

.pending {
  .card-icon {
    background: rgba(255, 193, 7, 0.12);
    i { color: #ffc107; }
  }
  .wave-effect { background: linear-gradient(to right,
  rgba(255, 193, 7, 0.05),
  rgba(255, 193, 7, 0.1)
  );}
}

.approved {
  .card-icon {
    background: rgba(76, 175, 80, 0.12);
    i { color: #4caf50; }
  }
  .wave-effect { background: linear-gradient(to right,
  rgba(76, 175, 80, 0.05),
  rgba(76, 175, 80, 0.1)
  );}
}

.rejected {
  .card-icon {
    background: rgba(244, 67, 54, 0.12);
    i { color: #f44336; }
  }
  .wave-effect { background: linear-gradient(to right,
  rgba(244, 67, 54, 0.05),
  rgba(244, 67, 54, 0.1)
  );}
}

/* ================= 查询表单 ================= */
.query-form {
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  margin-top: 20px;

  .el-form-item {
    margin-bottom: 16px;
    margin-right: 20px;

    &__label {
      font-size: 14px;
      color: #2d3540;
      font-weight: 600;
      letter-spacing: 0.3px;
    }
  }

  .el-input__inner,
  .el-select .el-input__inner {
    height: 40px;
    width: 220px;
    border-radius: 8px;
    border: 1px solid rgba(228, 231, 237, 0.6);
    background: rgba(255, 255, 255, 0.8);
    transition: all 0.3s ease;
    box-shadow: inset 0 1px 2px rgba(255, 255, 255, 0.3), 0 2px 4px rgba(0, 0, 0, 0.05);

    &:focus {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(var(--primary-color), 0.2), inset 0 1px 2px rgba(255, 255, 255, 0.4);
    }
  }

  .el-button-group {
    gap: 12px;
    margin-left: 8px;

    .el-button:not(:last-child) {
      margin-right: 12px;
    }
  }
}

/* ================= 数据表格 ================= */
.table-container {
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.table-container:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 表格标题栏 */
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(145deg, #f8fafc, #f0f4f8);
}

.table-title {
  font-size: 18px;
  font-weight: 600;
  color: #2d3540;
  letter-spacing: 0.5px;
}

/* ================= 增强表格样式 ================= */
.enhanced-table {
  --table-border-color: rgba(228, 231, 237, 0.6);
  --header-bg: linear-gradient(165deg, #f8fafc 0%, #f1f5f9 100%);
  --hover-bg: rgba(241, 245, 249, 0.6);
  font-family: 'Segoe UI', system-ui, sans-serif;
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow);
  overflow: hidden;
}

/* 表头样式 */
.enhanced-table ::v-deep .el-table__header th {
  background: var(--header-bg) !important;
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--table-border-color) !important;
  font-weight: 600;
  letter-spacing: 0.3px;
  text-transform: capitalize;
  position: relative;
}

.enhanced-table ::v-deep .el-table__header th::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 1px;
  background: linear-gradient(90deg,
  rgba(255,255,255,0) 0%,
  rgba(224, 232, 255, 0.6) 50%,
  rgba(255,255,255,0) 100%
  );
}

/* 表体样式 */
.enhanced-table ::v-deep .el-table__body td {
  transition:
    transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.3s ease;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(228, 231, 237, 0.5) !important;
}

/* 行交互效果 */
.enhanced-table ::v-deep .el-table__body tr:hover td {
  background: var(--hover-bg) !important;
  transform: perspective(100px) translateZ(2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* 斑马纹效果 */
.enhanced-table ::v-deep .el-table__row.striped-row td {
  background: linear-gradient(
    to right,
    rgba(251, 253, 255, 0.6),
    rgba(245, 247, 250, 0.6)
  );
}

/* ================= 操作列 ================= */
.op-column {
  min-width: 240px;
}

.op-btn-group {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.op-btn {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;

  &__icon {
    margin-right: 6px;
    font-size: 15px;
    transition: transform 0.2s ease;
  }

  &__text {
    position: relative;
    top: 1px;
  }

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);

    .op-btn__icon {
      transform: scale(1.15);
    }
  }

  &:active {
    transform: translateY(1px);
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  }
}

.op-btn--approve {
  color: #22c55e;
  border-color: rgba(34, 197, 94, 0.2);

  &:hover {
    background: rgba(34, 197, 94, 0.06);
    border-color: rgba(34, 197, 94, 0.3);
  }
}

.op-btn--reject {
  color: #ef4444;
  border-color: rgba(239, 68, 68, 0.2);

  &:hover {
    background: rgba(239, 68, 68, 0.06);
    border-color: rgba(239, 68, 68, 0.3);
  }
}

.op-btn--log {
  color: #6366f1;
  border-color: rgba(99, 102, 241, 0.2);

  &:hover {
    background: rgba(99, 102, 241, 0.06);
    border-color: rgba(99, 102, 241, 0.3);
  }
}

/* ================= 通用按钮 ================= */
.action-btn,
.query-form .el-button {
  min-width: 100px;
  height: 36px;
  padding: 0 20px;
  border-radius: 8px;
  border: none;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);

  i {
    margin-right: 8px;
    font-size: 16px;
  }
}

.el-button--primary,
.export-btn {
  background: linear-gradient(135deg, #409EFF 0%, #3375ff 100%);
  color: white !important;
}

.query-form .el-button:not(.el-button--primary) {
  background: linear-gradient(135deg, #f0f2f5 0%, #e5e9ef 100%);
  color: #606266;
  border: 1px solid #e4e7ed;
}
/* 文件操作按钮 - 禁用状态美化 */
.file-btn[disabled] {
  /* 颜色 */
  background-color: #f5f7fa !important;
  border-color: #e4e7ed !important;
  color: #c0c4cc !important;

  /* 视觉效果 */
  opacity: 0.8;
  cursor: not-allowed;

  /* 禁用悬停效果 */
  &:hover, &:focus {
    background-color: #f5f7fa !important;
    border-color: #e4e7ed !important;
    color: #c0c4cc !important;
    transform: none;
    box-shadow: none;
  }

  /* 图标颜色 */
  i {
    color: inherit !important;
  }
}

/* 正常状态保持原有样式 */
.file-btn:not([disabled]) {
  transition: all 0.3s ease;
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
  }
}
/* ================= 响应式调整 ================= */
@media (max-width: 768px) {
  /* 统计卡片 */
  .status-card {
    margin-bottom: 8px;

    .card-content {
      padding: 12px;
    }

    .card-icon {
      width: 48px;
      height: 48px;
      margin-right: 16px;

      i {
        font-size: 20px;
      }
    }

    .count {
      font-size: 24px;
    }
  }

  /* 查询表单 */
  .query-form {
    padding: 16px;

    .el-form-item {
      margin-bottom: 8px !important;
    }

    .el-date-editor {
      width: 100% !important;
    }
  }

  /* 操作按钮 */
  .op-btn-group {
    gap: 6px;
  }

  .op-btn {
    padding: 5px 10px;
    font-size: 12px;

    &__icon {
      font-size: 14px;
      margin-right: 4px;
    }

    &__text {
      display: none;
    }
  }
}
</style>

