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
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      class="custom-query-form"
    >
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作栏 -->
    <el-row class="action-bar">
      <el-col :span="24" class="action-container">
        <el-button
          type="success"
          icon="el-icon-download"
          class="action-btn export-btn"
          @click="handleExport"
        >数据导出</el-button>
      </el-col>
    </el-row>

    <!-- 表格区域 -->
    <el-card class="table-container">
      <el-table
        v-loading="loading"
        :data="activityList"
        class="enhanced-table"
        :header-cell-style="headerStyle"
        :row-class-name="tableRowClassName"
      >
        <el-table-column label="序号" width="70" align="center">
          <template slot-scope="scope">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="学生学号" align="center" prop="studentId"/>
        <el-table-column label="学生姓名" align="center" prop="studentName"/>
        <el-table-column label="活动名称" align="center" prop="activityName"/>
        <el-table-column label="活动地点" align="center" prop="activityLocation"/>
        <!-- 证明材料 -->
        <el-table-column label="证明材料" width="160" align="center">
          <template v-slot="scope">
            <el-dropdown
              trigger="click"
              @command="handleProofCommand"
              :disabled="!scope.row.proof || scope.row.proof.length === 0"
            >
              <el-button
                type="primary"
                size="mini"
                plain
                :disabled="!scope.row.proof || scope.row.proof.length === 0"
              >
                <i class="el-icon-picture"></i> 图片操作
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  :command="{ action: 'preview', files: scope.row.proof }"
                >预览</el-dropdown-item>
                <el-dropdown-item
                  :command="{ action: 'download', files: scope.row.proof }"
                >下载</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>

        <!-- 学生总结 -->
        <el-table-column label="学生总结" width="160" align="center">
          <template v-slot="scope">
            <el-dropdown
              trigger="click"
              @command="handleSummaryCommand"
              :disabled="!scope.row.summary"
            >
              <el-button
                type="primary"
                size="mini"
                plain
                :disabled="!scope.row.summary"
              >
                <i class="el-icon-document"></i> 文档操作
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  :command="{ action: 'preview', file: scope.row.summary }"
                >预览</el-dropdown-item>
                <el-dropdown-item
                  :command="{ action: 'download', file: scope.row.summary }"
                >下载</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>

        <el-table-column label="审核状态" prop="status" align="center" width="100">
          <template slot-scope="scope">
            <el-tag
              v-if="scope.row.status === '未审核'"
              type="warning"
            >未审核</el-tag>
            <el-tag
              v-else-if="scope.row.status === '已通过'"
              type="success"
            >已通过</el-tag>
            <el-tag
              v-else-if="scope.row.status === '未通过'"
              type="danger"
            >未通过</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="success"
              icon="el-icon-check"
              @click="handleAudit(scope.row, '通过')"
              v-if="scope.row.status !== '已通过'"
              circle
            ></el-button>
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-close"
              @click="handleAudit(scope.row, '拒绝')"
              v-if="scope.row.status !== '未通过'"
              circle
            ></el-button>
            <el-button
              size="mini"
              type="info"
              icon="el-icon-notebook-2"
              @click="showAuditHistory(scope.row)"
              circle
            ></el-button>
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
    <el-dialog :visible.sync="previewVisible" title="证明材料预览" width="60%">
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
        >
          <i class="el-icon-download"></i> 下载当前图片
        </el-button>
      </div>
    </el-dialog>

    <!-- 文档预览对话框 -->
    <el-dialog
      :visible.sync="docPreviewVisible"
      title="学生总结预览"
      width="80%"
      class="native-pdf-preview"
    >
      <div v-if="currentDocument.type === 'pdf'" class="preview-container">
        <iframe
          :src="`${currentDocument.url}#toolbar=0&navpanes=0&scrollbar=0`"
          style="width: 100%; height: 75vh; border: none;"
          @load="disablePdfInteractions"
        ></iframe>
      </div>
      <div v-else-if="currentDocument.type === 'docx'" class="preview-container docx-preview">
        <div v-html="docxContent" class="docx-content"></div>
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
              <span>IP: {{ row.ipAddress }}</span>
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
import { listBookingsAudit, updateBooking } from "@/api/system/bookings";
import { getToken } from "@/utils/auth";
import { listAuditHistory } from "@/api/student/audit";
import axios from "axios";

export default {
  name: "ActivityAudit",
  data() {
    return {
      // 数据加载状态
      loading: true,
      historyLoading: false,

      // 审核状态统计
      auditStats: {
        approved: 0,
        rejected: 0,
        pending: 0
      },

      // 审核历史相关
      historyVisible: false,
      auditHistoryList: [],
      historyTotal: 0,
      historyQueryParams: {
        pageNum: 1,
        pageSize: 5,
        moduleType: 5,
        moduleId: null
      },

      // 预览相关
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      docPreviewVisible: false,
      currentDocument: {
        url: '',
        type: '',
        name: ''
      },
      docxContent: '',

      // 表格数据
      activityList: [],
      total: 0,

      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        studentName: null,
        activityName: null,
        status: null
      }
    };
  },
  created() {
    this.getList();
    this.fetchAuditCount();
  },
  methods: {
    // 获取活动列表
    getList() {
      this.loading = true;
      listBookingsAudit(this.queryParams).then(response => {
        this.activityList = response.rows; // 直接使用后端返回的状态字符串
        this.total = response.total;
        this.loading = false;
      });
    },

    // 获取审核统计
    async fetchAuditCount() {
      try {
        // 这里应该是调用API获取统计数据
        // 模拟数据
        this.auditStats = {
          approved: 12,
          rejected: 5,
          pending: 8
        };
      } catch (error) {
        console.error("获取统计数据失败:", error);
      }
    },

    // 点击状态卡片进行筛选
    handleStatusClick(type) {
      const statusMap = {
        pending: '未审核',
        approved: '已通过',
        rejected: '未通过'
      };
      this.queryParams.status = statusMap[type];
      this.getList();
    },

    // 处理证明材料操作
    handleProofCommand(command) {
      if (command.action === 'preview') {
        this.handleProofPreview(command.files);
      } else if (command.action === 'download') {
        this.downloadProofFiles(command.files);
      }
    },

    // 处理总结文档操作
    handleSummaryCommand(command) {
      if (command.action === 'preview') {
        this.handleDocumentPreview(command.file);
      } else if (command.action === 'download') {
        this.downloadSummaryFile(command.file);
      }
    },

    // 预览证明材料
    handleProofPreview(files) {
      if (!files || files.length === 0) {
        this.$message.warning('无可用证明材料');
        return;
      }

      this.previewImages = files.map(file =>
        `${process.env.VUE_APP_BASE_API}${file}`
      );
      this.currentPreviewIndex = 0;
      this.previewVisible = true;
    },

    // 下载证明材料
    downloadProofFiles(files) {
      if (!files || files.length === 0) {
        this.$message.warning('无可用证明材料');
        return;
      }

      files.forEach(file => {
        const url = `${process.env.VUE_APP_BASE_API}${file}`;
        this.downloadSingleFile(url);
      });
    },

    // 下载学生总结
    downloadSummaryFile(filePath) {
      if (!filePath) {
        this.$message.warning('无可用总结文档');
        return;
      }

      const url = `${process.env.VUE_APP_BASE_API}${filePath}`;
      this.downloadSingleFile(url);
    },

    // 处理文档预览
    async handleDocumentPreview(filePath) {
      const loading = this.$loading({
        lock: true,
        text: '正在加载文档...',
        spinner: 'el-icon-loading',
      });

      try {
        const fileType = this.getFileType(filePath);
        this.currentDocument = {
          url: `${process.env.VUE_APP_BASE_API}${filePath}`,
          type: fileType,
          name: filePath.split('/').pop()
        };

        if (fileType === 'docx') {
          const response = await axios.get(this.currentDocument.url, {
            responseType: 'arraybuffer',
            headers: {
              Authorization: `Bearer ${getToken()}`
            }
          });
          const result = await this.parseDocx(response.data);
          this.docxContent = result.html;
        }
        this.docPreviewVisible = true;
      } catch (error) {
        this.$message.error(`预览失败: ${error.message}`);
      } finally {
        loading.close();
      }
    },

    // 获取文件类型
    getFileType(filePath) {
      const extension = filePath.split('.').pop().toLowerCase();
      return {
        pdf: 'pdf',
        docx: 'docx',
        doc: 'doc'
      }[extension] || 'other';
    },

    // 解析DOCX文件
    async parseDocx(arrayBuffer) {
      try {
        const mammoth = await import('mammoth');
        const result = await mammoth.convertToHtml({arrayBuffer});
        return {html: result.value};
      } catch (error) {
        console.error('DOCX解析失败:', error);
        return {html: '<p>文档解析失败，请下载后查看</p>'};
      }
    },

    // 下载单个文件
    downloadSingleFile(url) {
      const a = document.createElement('a');
      a.href = url;
      a.download = url.split('/').pop();
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    },

    // 禁用PDF交互功能
    disablePdfInteractions(event) {
      try {
        const iframeDoc = event.target.contentDocument || event.target.contentWindow.document;
        iframeDoc.addEventListener('contextmenu', e => e.preventDefault());
        iframeDoc.body.style.userSelect = 'none';
      } catch (error) {
        console.log('安全策略限制，部分交互禁用失败');
      }
    },

    // 显示审核历史
    async showAuditHistory(row) {
      this.historyQueryParams.moduleId = row.bookingId;
      this.historyVisible = true;
      await this.getAuditHistory();
    },

    // 获取审核历史记录
    async getAuditHistory() {
      this.historyLoading = true;
      try {
        const response = await listAuditHistory(this.historyQueryParams);
        this.auditHistoryList = response.rows;
        this.historyTotal = response.total;
      } finally {
        this.historyLoading = false;
      }
    },

    // 审核操作
    handleAudit(row, status) {
      const isApproved = status === '通过';
      const statusMapping = {
        '通过': '已通过',
        '拒绝': '未通过',
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
        // 构建审核数据
        const auditData = {
          bookingId: row.bookingId,
          status: statusMapping[status],
          reviewComment: isApproved ? '系统审核通过' : value
        };

        // 调用审核接口
        updateBooking(auditData).then(response => {
          this.$message.success(`已${status}审核`);
          this.getList(); // 刷新列表
        }).catch(error => {
          this.$message.error(`${status}审核失败: ${error.message || ''}`);
        });
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    },

    // 搜索操作
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    // 重置查询条件
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        studentName: null,
        activityName: null,
        status: null
      };
      this.getList();
    },

    // 导出数据
    handleExport() {
      this.download('system/bookings/export', {
        ...this.queryParams
      }, `activity${new Date().getTime()}.xlsx`);
    },

    // 表格行样式
    tableRowClassName({rowIndex}) {
      return rowIndex % 2 === 0 ? 'striped-row' : '';
    },

    // 表头样式
    headerStyle() {
      return {
        color: '#2d3540',
        fontWeight: 600,
        fontSize: '14px',
        backgroundColor: '#f5f7fa'
      };
    }
  }
};
</script>
<style scoped>
.app-container {
  margin-left: 100px;
}
</style>
