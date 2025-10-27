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

    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="card-header">
        <i class="el-icon-search"></i>
        <span>搜索条件</span>
      </div>
      <el-form
        :model="queryParams"
        ref="queryForm"
        size="small"
        :inline="true"
        v-show="showSearch"
      >
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
              prefix-icon="el-icon-user"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="活动名称" prop="activityName">
            <el-input
              v-model="queryParams.activityName"
              placeholder="请输入活动名称"
              clearable
              prefix-icon="el-icon-search"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="活动类型" prop="activityType">
            <el-select v-model="queryParams.activityType" clearable placeholder="请选择活动类型" class="search-input">
              <el-option
                v-for="type in predefinedActivityTypes"
                :key="type.value"
                :label="type.label"
                :value="type.value"
              />
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

    <!-- 操作栏 -->
    <div class="button-bar">
      <el-button
        type="primary"
        icon="el-icon-check"
        class="action-btn"
        :disabled="selectedRows.length === 0"
        @click="handleBatchAudit('通过')"
      >批量通过</el-button>
      <el-button
        type="danger"
        icon="el-icon-close"
        class="action-btn"
        :disabled="selectedRows.length === 0"
        @click="handleBatchAudit('未通过')"
      >批量未通过</el-button>
      <el-button
        type="success"
        icon="el-icon-download"
        class="action-btn export-btn"
        @click="handleExport"
      >数据导出</el-button>
    </div>

    <!-- 表格区域 -->
    <div class="table-card">
      <div class="card-header">
        <i class="el-icon-s-grid"></i>
        <span>审核列表</span>
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>
      <el-table
        v-loading="loading"
        :data="activityList"
        ref="activityTable"
        class="modern-table"
        :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}"
        :row-class-name="tableRowClassName"
        row-key="bookingId"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <!-- 序号列 -->
        <el-table-column label="序号" width="80" align="center">
          <template v-slot="scope">
              <span class="index-badge">
                {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
              </span>
          </template>
        </el-table-column>
        <el-table-column label="学生学号" align="center" prop="studentId" min-width="100"/>
        <el-table-column label="学生姓名" align="center" prop="studentName" min-width="100"/>
        <el-table-column label="活动名称" align="center" prop="activityName" min-width="250">
          <template slot-scope="scope">
            <div class="activity-name" :title="scope.row.activityName">
              <span :title="scope.row.activityName">{{ truncateText(scope.row.activityName, 18) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="活动类型" align="center" prop="activityType" min-width="200">
          <template slot-scope="scope">
            <el-tag :type="getActivityTypeTagType(scope.row.activityType)" effect="plain" class="activity-type-tag">
              {{ getActivityTypeName(scope.row.activityType) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="活动地点" align="center" prop="activityLocation"/>

        <el-table-column label="审核状态" prop="status" align="center" width="120">
          <template slot-scope="scope">
            <el-tag
              v-if="scope.row.status === '未审核'"
              type="warning"
              effect="dark"
            >未审核</el-tag>
            <el-tag
              v-else-if="scope.row.status === '已通过'"
              type="success"
              effect="dark"
            >已通过</el-tag>
            <el-tag
              v-else-if="scope.row.status === '未通过'"
              type="danger"
              effect="dark"
            >未通过</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" width="150">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button
                size="mini"
                type="text"
                @click="openAuditDialog(scope.row)"
                class="action-button audit-button">
                审核
              </el-button>
              <el-button
                size="mini"
                type="text"
                @click="showAuditHistory(scope.row)"
                class="action-button history-button">
                历史
              </el-button>
            </div>
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

    <!-- 审核详情对话框 -->
    <el-dialog
      :visible.sync="auditDialogVisible"
      :title="auditDialogTitle"
      width="70%"
      append-to-body
    >
      <div v-if="currentBooking" class="audit-content">
        <div class="audit-header">
          <div class="header-left">
            <div class="title-row">
              <span class="student-name">{{ currentBooking.studentName || '-' }}</span>
              <el-tag size="mini" :type="getStatusTagType(currentBooking.status)">{{ currentBooking.status || '-' }}</el-tag>
            </div>
            <div class="sub-row">
              <span>学号：{{ currentBooking.studentId || '-' }}</span>
              <span class="divider">|</span>
              <span>活动：{{ currentBooking.activityName || '-' }}</span>
              <span class="divider">|</span>
              <span>类型：{{ getActivityTypeName(currentBooking.activityType) || '-' }}</span>
            </div>
          </div>
          <div class="header-right">
            <el-button size="mini" @click="showAuditHistory(currentBooking)"><i class="el-icon-notebook-2"></i> 审核历史</el-button>
          </div>
        </div>

        <el-skeleton :loading="auditLoading" animated :rows="3">
          <template slot="template">
            <el-skeleton-item variant="h3" style="width: 30%"></el-skeleton-item>
            <el-skeleton-item variant="image" style="width: 100%; height: 120px; margin-top: 10px"/>
            <el-skeleton-item variant="text" style="width: 50%; margin-top: 10px"/>
          </template>
          <template>
            <div class="audit-grid">
              <div class="section">
                <h3>图片材料</h3>
                <div v-if="auditImages && auditImages.length" class="proof-grid">
                  <div
                    v-for="(img, idx) in auditImages"
                    :key="idx"
                    class="proof-card"
                  >
                    <el-image
                      :src="img"
                      :preview-src-list="auditImages"
                      fit="cover"
                      class="proof-thumb"
                    />
                    <div class="proof-overlay">
                      <el-button
                        size="mini"
                        circle
                        icon="el-icon-view"
                        @click.stop="handleProofPreview([auditImagePaths[idx]])"
                      />
                      <el-button
                        size="mini"
                        circle
                        icon="el-icon-download"
                        @click.stop="downloadSingleFile(img)"
                      />
                    </div>
                  </div>
                  <div class="proof-actions">
                    <el-button size="small" icon="el-icon-view" @click="handleProofPreview(auditImagePaths)">预览全部</el-button>
                    <el-button size="small" icon="el-icon-download" type="primary" @click="downloadProofFiles(auditImagePaths)">全部下载</el-button>
                  </div>
                </div>
                <div v-else class="empty-tip">暂无图片材料</div>
              </div>

              <div class="section">
                <h3>文档材料</h3>
                <div v-if="auditSummary" class="doc-card">
                  <div class="doc-left">
                    <i :class="['file-icon', getDocIconClass(auditSummary)]"></i>
                    <div class="file-meta">
                      <div class="file-name" :title="getFileName(auditSummary)">{{ getFileName(auditSummary) }}</div>
                      <el-tag size="mini" type="info">{{ getFileType(auditSummary).toUpperCase() }}</el-tag>
                    </div>
                  </div>
                  <div class="doc-actions">
                    <el-button size="mini" icon="el-icon-view" @click="handleSummaryCommand({ action: 'preview', file: auditSummary })">预览</el-button>
                    <el-button size="mini" icon="el-icon-download" type="primary" @click="downloadSummaryFile(auditSummary)">下载</el-button>
                  </div>
                </div>
                <div v-else class="empty-tip">暂无文档材料</div>
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button :loading="actionLoading" type="primary" @click="approveCurrent">通 过</el-button>
        <el-button :loading="actionLoading" type="danger" @click="rejectCurrent">不通过</el-button>
      </span>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog :visible.sync="previewVisible" title="证明材料预览" width="60%" class="preview-dialog">
      <div style="text-align: center; margin-bottom: 20px;">
        <img
          :src="previewImages[currentPreviewIndex]"
          style="max-width: 100%; display: block; margin: 0 auto;"
          alt="证明材料预览"
        />
        <div class="preview-controls">
          <el-button
            icon="el-icon-arrow-left"
            :disabled="currentPreviewIndex === 0"
            @click="currentPreviewIndex--"
          ></el-button>
          <span class="preview-count">{{ currentPreviewIndex + 1 }} / {{ previewImages.length }}</span>
          <el-button
            icon="el-icon-arrow-right"
            :disabled="currentPreviewIndex === previewImages.length - 1"
            @click="currentPreviewIndex++"
          ></el-button>
        </div>
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
      class="preview-dialog native-pdf-preview"
    >
      <div v-if="currentDocument.type === 'pdf'" class="preview-container">
        <iframe
          :src="getPdfUrlWithAuth(currentDocument.url)"
          style="width: 100%; height: 75vh; border: none;"
          @load="disablePdfInteractions"
          @error="handlePdfError"
        ></iframe>
        <div v-if="pdfError" class="pdf-error">
          <p>PDF预览失败: {{ pdfError }}</p>
          <el-button @click="retryPdfPreview">重试</el-button>
        </div>
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
      class="history-dialog"
    >
      <el-table
        v-loading="historyLoading"
        :data="auditHistoryList"
        border
        style="margin-top: 15px;"
        class="history-table"
      >
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
import { listBookingsAudit, updateBooking, auditBooking, getAuditCount, getBooking } from "@/api/system/bookings";
import { getToken } from "@/utils/auth";
import { listAuditHistory } from "@/api/student/audit";
import {getNickName} from "@/api/system/student";
import axios from "axios";


export default {
  name: "ActivityAudit",
  data() {
    return {
      // 数据加载状态
      loading: true,
      historyLoading: false,
      // 显示搜索条件
      showSearch: true,
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
      pdfError: '',

      // 表格数据
      activityList: [],
      total: 0,
      selectedRows: [],
      // 可用的活动类型列表
      availableActivityTypes: [],
      // 预定义的活动类型
      predefinedActivityTypes: [
        { value: '1', label: '人格塑造与价值引领活动类' },
        { value: '2', label: '知识融合与思维进阶活动类' },
        { value: '3', label: '能力锻造与实践创新活动类' },
        { value: '4', label: '社会责任与领军意识活动类' }
      ],

      // 审核弹窗
      auditDialogVisible: false,
      auditDialogTitle: '审核材料',
      currentBooking: null,
      auditImagePaths: [],
      auditImages: [],
      auditSummary: null,
      auditLoading: false,
      actionLoading: false,

      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        studentName: null,
        activityName: null,
        activityType: null,
        status: null,
        organizer: null
      }
    };
  },
  created() {
    this.checkRouteParams();
    this.getList();
    this.fetchAuditCount();
  },
  methods: {
    /** 检查路由参数，处理从首页跳转过来的筛选请求 */
    checkRouteParams() {
      const { status } = this.$route.query;

      if (status) {
        // 根据状态参数设置筛选条件
        const statusMap = {
          'pending': '未审核',
          'approved': '已通过',
          'rejected': '未通过'
        };

        this.queryParams.status = statusMap[status] || status;

        // 显示筛选提示
        this.$message.success(`已筛选${this.queryParams.status}的审核记录`);

        // 清除路由参数，避免刷新页面时重复触发
        this.$router.replace({
          path: this.$route.path,
          query: {}
        });
      }
    },

    async openAuditDialog(row) {
      this.currentBooking = row;
      this.auditDialogTitle = `审核材料`;
      this.auditDialogVisible = true;
      // 拉取材料详情（与学生端一致接口）
      try {
        this.auditLoading = true;
        const res = await this.$options.methods._getBookingDetail.call(this, row.bookingId);
        const data = res && res.data ? res.data : {};
        this.auditImagePaths = Array.isArray(data.proof) ? data.proof : [];
        this.auditImages = this.auditImagePaths.map(p => `${process.env.VUE_APP_BASE_API}${p}`);
        this.auditSummary = data.summary || null;
      } catch (e) {
        this.auditImagePaths = [];
        this.auditImages = [];
        this.auditSummary = null;
      } finally {
        this.auditLoading = false;
      }
    },

    async approveCurrent() {
      if (!this.currentBooking) return;

      try {
        await this.$confirm('确认通过该活动的审核吗？', '审核确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        const payload = {
          bookingId: this.currentBooking.bookingId,
          status: '已通过',
          reviewComment: '系统审核通过',
          reviewTime: new Date().toISOString(),
          reviewer: this.$store.state.user.name
        };

        this.actionLoading = true;
        await this.$options.methods._updateSingleBooking.call(this, payload);
        this.$message.success('审核通过');
        this.auditDialogVisible = false;
        this.getList();
        this.fetchAuditCount();
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('审核通过失败');
        }
      } finally {
        this.actionLoading = false;
      }
    },

    async rejectCurrent() {
      if (!this.currentBooking) return;
      try {
        const { value } = await this.$prompt('请输入未通过原因', '审核未通过', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: /.+/,
          inputErrorMessage: '未通过原因不能为空'
        });
        const payload = {
          bookingId: this.currentBooking.bookingId,
          status: '未通过',
          reviewComment: value,
          reviewTime: new Date().toISOString(),
          reviewer: this.$store.state.user.name
        };
        this.actionLoading = true;
        await this.$options.methods._updateSingleBooking.call(this, payload);
        this.$message.success('未通过');
        this.auditDialogVisible = false;
        this.getList();
        this.fetchAuditCount();
      } catch (e) {
        if (e !== 'cancel') this.$message.error('未通过失败');
      } finally {
        this.actionLoading = false;
      }
    },

    // 分离出获取详情，便于单测与复用
    _getBookingDetail(bookingId) {
      return getBooking(bookingId);
    },
    onSelectionChange(selection) {
      this.selectedRows = selection || [];
    },

    async handleBatchAudit(actionLabel) {
      const isApproved = actionLabel === '通过';
      const statusMapping = {
        '通过': '已通过',
        '未通过': '未通过'
      };

      if (!this.selectedRows || this.selectedRows.length === 0) {
        this.$message.warning('请先选择需要审核的记录');
        return;
      }

      try {
        const promptOptions = {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        };
        if (!isApproved) {
          promptOptions.inputPattern = /.+/;
          promptOptions.inputErrorMessage = '未通过原因不能为空';
          promptOptions.inputPlaceholder = '请输入未通过原因';
        }

        const result = await this.$prompt(
          isApproved ? '确认批量通过审核吗？' : '请输入未通过原因（将应用于所有选中记录）',
          '批量审核确认',
          promptOptions
        );

        const reviewComment = isApproved ? '系统批量审核通过' : result.value;

        const loading = this.$loading({
          lock: true,
          text: '正在批量处理...',
          spinner: 'el-icon-loading'
        });

        let successCount = 0;
        let failCount = 0;

        for (const row of this.selectedRows) {
          const payload = {
            bookingId: row.bookingId,
            status: statusMapping[actionLabel],
            reviewComment,
            reviewTime: new Date().toISOString(),
            reviewer: this.$store.state.user.name
          };
          try {
            await this.$options.methods._updateSingleBooking.call(this, payload);
            successCount++;
          } catch (e) {
            failCount++;
          }
        }

        loading.close();
        this.$message.success(`批量完成：成功 ${successCount} 条，失败 ${failCount} 条`);
        this.getList();
        this.fetchAuditCount();
        this.$refs.activityTable && this.$refs.activityTable.clearSelection();
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('批量审核失败');
        } else {
          this.$message.info('已取消操作');
        }
      }
    },

    async _updateSingleBooking(payload) {
      return auditBooking(payload);
    },
    getStatusTagType(status) {
      const statusMap = {
        '已通过': 'success',
        '未通过': 'danger',
        '未审核': 'warning'
      };
      return statusMap[status] || 'info';
    },

    // 活动类型映射函数：将数字转换为对应的类型名称
    getActivityTypeName(activityType) {
      const typeMap = {
        '1': '人格塑造与价值引领活动类',
        '2': '知识融合与思维进阶活动类',
        '3': '能力锻造与实践创新活动类',
        '4': '社会责任与领军意识活动类'
      };
      return typeMap[activityType] || activityType;
    },

    getActivityTypeTagType(activityType) {
      const map = {
        '1': 'primary',   // 人格塑造与价值引领活动类 - 蓝色
        '2': 'success',   // 知识融合与思维进阶活动类 - 绿色
        '3': 'warning',   // 能力锻造与实践创新活动类 - 橙色
        '4': 'danger',    // 社会责任与领军意识活动类 - 红色
        '其他': ''        // 默认蓝色
      }
      return map[activityType] || 'info';
    },

    /** 更新可用的活动类型列表 */
    updateAvailableActivityTypes() {
      const types = new Set();
      this.activityList.forEach(item => {
        if (item.activityType) {
          types.add(item.activityType);
        }
      });

      // 如果没有活动类型数据，提供默认选项
      if (types.size === 0) {
        types.add('1');
        types.add('2');
        types.add('3');
        types.add('4');
        types.add('其他');
      }

      // 转换为数组并排序
      this.availableActivityTypes = Array.from(types).sort();
    },
    // 获取活动列表
    getList() {
      this.loading = true;

      // 封装获取审核列表的逻辑
      const fetchAuditList = (params) => {
        listBookingsAudit(params).then(response => {
          this.activityList = response.rows;
          this.total = response.total;
          this.updateAvailableActivityTypes();
        }).catch(error => {
          this.$message.error("获取审核列表失败");
        }).finally(() => {
          this.loading = false;
        });
      };

      // 先获取组织者名称，作为默认筛选条件
      getNickName()
        .then(nickName => {
          // 合并查询参数与组织者信息
          const params = { ...this.queryParams, organizer: nickName.msg };
          fetchAuditList(params);
        })
        .catch(error => {
          // 失败时使用原始查询参数
          fetchAuditList(this.queryParams);
        });
    },



      // 获取审核统计
    async fetchAuditCount() {
      try {
        // 先获取组织者名称
        const nickNameResponse = await getNickName();
        const organizer = nickNameResponse.msg;

        console.log("🔍 获取审核统计，组织者:", organizer);
        const {code, data} = await getAuditCount(organizer);
        if (code === 200) {
          this.auditStats = data;
          console.log("📊 审核统计数据:", data);
        }
      } catch (error) {
        console.error("获取统计数据失败:", error);
        // 如果获取组织者失败，尝试不传参数
        try {
          const {code, data} = await getAuditCount();
          if (code === 200) {
            this.auditStats = data;
          }
        } catch (fallbackError) {
          console.error("获取统计数据失败（回退方案）:", fallbackError);
        }
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
          // 尝试多种方式获取文档
          let response;
          try {
            // 方式1：带认证头请求
            response = await axios.get(this.currentDocument.url, {
              responseType: 'arraybuffer',
              headers: {
                Authorization: `Bearer ${getToken()}`,
                'Cache-Control': 'no-cache'
              },
              timeout: 30000
            });
          } catch (authError) {
            // 方式2：匿名访问（如果服务器配置了permitAll）
            response = await axios.get(this.currentDocument.url, {
              responseType: 'arraybuffer',
              headers: {
                'Cache-Control': 'no-cache'
              },
              timeout: 30000
            });
          }

          const result = await this.parseDocx(response.data);
          this.docxContent = result.html;
        }
        this.docPreviewVisible = true;
        } catch (error) {
          if (error.response && error.response.status === 401) {
            this.$message.error('文档访问权限不足，请重新登录后重试');
          } else if (error.response && error.response.status === 403) {
            this.$message.error('文档访问被拒绝，请联系管理员');
          } else if (error.code === 'ECONNABORTED') {
            this.$message.error('文档加载超时，请检查网络连接');
          } else {
            this.$message.error(`预览失败: ${error.message || '未知错误'}`);
          }
        } finally {
        loading.close();
      }
    },

    // 文档辅助：获取文件名
    getFileName(filePath) {
      if (!filePath) return '';
      const parts = (filePath + '').split('/');
      return parts[parts.length - 1];
    },
    // 文档辅助：获取图标类
    getDocIconClass(filePath) {
      const type = this.getFileType(filePath);
      const map = { pdf: 'icon-pdf', docx: 'icon-doc', doc: 'icon-doc', other: 'icon-file' };
      return map[type] || 'icon-file';
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
        return {html: '<p>文档解析失败，请下载后查看</p>'};
      }
    },

    // 下载单个文件
    async downloadSingleFile(url) {
      try {
        // 从URL中提取文件名
        const fileName = url.split('/').pop() || 'download';

        // 方法1: 使用fetch API下载
        try {
          const response = await fetch(url, {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${getToken()}`
            }
          });

          if (!response.ok) {
            throw new Error('网络响应不正常');
          }

          const blob = await response.blob();
          const blobUrl = URL.createObjectURL(blob);

          const a = document.createElement('a');
          a.href = blobUrl;
          a.download = fileName;
          a.style.display = 'none';
          document.body.appendChild(a);
          a.click();

          // 清理
          setTimeout(() => {
            document.body.removeChild(a);
            URL.revokeObjectURL(blobUrl);
          }, 100);

          this.$message.success('文件下载成功');
          return;
        } catch (fetchError) {
          // Fetch下载失败，尝试备用方法
        }

        // 方法2: 直接创建链接下载（备用）
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();

        setTimeout(() => {
          document.body.removeChild(a);
        }, 100);

        this.$message.info('文件下载已开始');

      } catch (error) {
        this.$message.error(`下载失败: ${error.message || '请稍后重试'}`);

        // 最终备用方案：在新窗口打开
        window.open(url, '_blank');
      }
    },

    // 获取带认证的PDF URL
    getPdfUrlWithAuth(url) {
      const token = getToken();

      // 检测是否为生产环境（服务器部署）
      const isProduction = window.location.hostname !== 'localhost' &&
                          window.location.hostname !== '127.0.0.1' &&
                          !window.location.hostname.includes('192.168.');

      if (isProduction && token) {
        // 生产环境：使用文件访问接口
        try {
          const filePath = url.replace(process.env.VUE_APP_BASE_API, '');
          const accessUrl = `${process.env.VUE_APP_BASE_API}/file/access?path=${encodeURIComponent(filePath)}&token=${token}#toolbar=0&navpanes=0&scrollbar=0`;
          return accessUrl;
        } catch (error) {
          // 文件访问接口构建失败，回退到原始方式
        }
      }

      // 本地开发环境或回退方案：使用原始URL
      const fallbackUrl = `${url}#toolbar=0&navpanes=0&scrollbar=0`;
      return fallbackUrl;
    },

    // 处理PDF预览错误
    handlePdfError(event) {
      this.pdfError = 'PDF文件加载失败，请检查文件是否存在或网络连接';
    },

    // 重试PDF预览
    retryPdfPreview() {
      this.pdfError = '';
      // 强制刷新iframe
      this.$nextTick(() => {
        const iframe = document.querySelector('.preview-container iframe');
        if (iframe) {
          iframe.src = iframe.src;
        }
      });
    },

    // 禁用PDF交互功能
    disablePdfInteractions(event) {
      try {
        const iframeDoc = event.target.contentDocument || event.target.contentWindow.document;
        iframeDoc.addEventListener('contextmenu', e => e.preventDefault());
        iframeDoc.body.style.userSelect = 'none';
      } catch (error) {
        // 安全策略限制，部分交互禁用失败
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
        '未通过': '未通过',
      };

      this.$prompt(
        isApproved ? '确认通过审核吗？' : '请输入未通过原因',
        '审核确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: isApproved ? null : /.+/,
          inputErrorMessage: '未通过原因不能为空'
        }
      ).then(({value}) => {
        // 构建审核数据
        const auditData = {
          bookingId: row.bookingId,
          status: statusMapping[status],
          reviewComment: isApproved ? '系统审核通过' : value,
          reviewTime: new Date().toISOString(),
          reviewer: this.$store.state.user.name
        };

        // 调用审核接口
        auditBooking(auditData).then(response => {
          this.$message.success(`已${status}审核`);
          this.getList(); // 刷新列表
          this.fetchAuditCount();
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
        activityType: null,
        status: null,
        organizer: null
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

    /** 截断文本显示 */
    truncateText(text, maxLength) {
      if (!text) return '';
      if (text.length <= maxLength) return text;
      return text.substring(0, maxLength) + '...';
    },

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

/* 统计看板样式 */
.status-card {
  border-radius: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
}

.status-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.15);
}

.pending {
  background: linear-gradient(145deg, #f0f9ff, #e6f7ff);
  border-top: 4px solid #409EFF;
}

.approved {
  background: linear-gradient(145deg, #f0fff4, #e6fff1);
  border-top: 4px solid #67C23A;
}

.rejected {
  background: linear-gradient(145deg, #fff6f6, #ffeded);
  border-top: 4px solid #F56C6C;
}

.card-content {
  display: flex;
  align-items: center;
  padding: 15px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 28px;
}

.pending .card-icon {
  background: rgba(64, 158, 255, 0.15);
  color: #409EFF;
}

.approved .card-icon {
  background: rgba(103, 194, 58, 0.15);
  color: #67C23A;
}

.rejected .card-icon {
  background: rgba(245, 108, 108, 0.15);
  color: #F56C6C;
}

.card-info .title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 5px;
}

.card-info .count {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
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
  justify-content: flex-end;
  margin-bottom: 24px;
  gap: 12px;
}

.action-btn {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.export-btn {
  background: #f0f9eb;
  border-color: #e1f3d8;
  color: #67c23a;
}

.export-btn:hover {
  background: #e1f3d8;
  border-color: #67c23a;
  color: #67c23a;
}

/* 现代化表格 */
.modern-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
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

.striped-row {
  background-color: #fafafa;
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

/* 操作按钮 - 统一文字按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-button {
  padding: 5px 8px;
  font-size: 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.audit-button {
  color: #409EFF;
}

.audit-button:hover {
  background-color: rgba(64, 158, 255, 0.1);
}

.history-button {
  color: #909399;
}

.history-button:hover {
  background-color: rgba(144, 147, 153, 0.1);
}

/* 对话框样式 */
.preview-dialog {
  border-radius: 8px;
}

.preview-controls {
  margin-top: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-count {
  margin: 0 15px;
  font-weight: 500;
}

.history-dialog {
  border-radius: 8px;
}

.history-table {
  border-radius: 6px;
  overflow: hidden;
}

.history-table th {
  background-color: #f5f7fa !important;
}

.operation-info {
  font-size: 13px;
  color: #909399;
}

/* 活动名称样式 */
.activity-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  cursor: help;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.activity-name:hover {
  color: #409EFF;
}

/* 活动类型标签样式 */
.activity-type-tag {
  font-weight: 500;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 审核对话框材料区域样式，保持与学生端一致 */
.section {
  margin-bottom: 30px;
  padding: 15px;
  background: #f9fafc;
  border-radius: 8px;
}
.section h3 {
  margin-bottom: 15px;
  color: #409EFF;
  font-weight: 600;
}
.audit-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
@media (max-width: 1200px) {
  .audit-grid {
    grid-template-columns: 1fr;
  }
}
.proof-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: flex-start;
}
.proof-card {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  width: 150px;
  height: 150px;
  background: #fff;
}
.proof-thumb {
  width: 100%;
  height: 100%;
}
.proof-overlay {
  position: absolute;
  right: 6px;
  bottom: 6px;
  display: flex;
  gap: 6px;
}
.proof-actions {
  width: 100%;
  margin-top: 10px;
}
.empty-tip {
  color: #909399;
}

/* 审核弹窗头部信息区 */
.audit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px 0;
}
.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.sub-row {
  margin-top: 6px;
  color: #606266;
  font-size: 13px;
  display: flex;
  gap: 8px;
}
.divider {
  color: #dcdfe6;
}
.student-name {
  color: #409EFF;
}
.hover-zoom:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

/* 文档卡片样式 */
.doc-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
}
.doc-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.file-icon {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
}
.icon-pdf { background: #f56c6c; }
.icon-doc { background: #409EFF; }
.icon-file { background: #909399; }
.file-meta .file-name {
  max-width: 320px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.doc-actions {
  display: flex;
  gap: 8px;
}

/* PDF错误样式 */
.pdf-error {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.pdf-error p {
  color: #f56c6c;
  margin-bottom: 15px;
  font-size: 14px;
}

.preview-container {
  position: relative;
}
</style>
