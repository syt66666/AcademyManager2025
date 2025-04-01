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
      <el-form
        :model="queryParams"
        ref="queryForm"
        size="small"
        :inline="true"
        v-show="showSearch"
        label-width="100px"
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
        <el-form-item label="导师工号" prop="tutorId">
          <el-input
            v-model="queryParams.tutorId"
            placeholder="请输入导师工号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="导师姓名" prop="tutorName">
          <el-input
            v-model="queryParams.tutorName"
            placeholder="请输入导师姓名"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-container">
      <div slot="header" class="table-header">
        <span class="table-title">导师指导审核</span>
        <div class="action-bar">
          <el-button
            type="success"
            icon="el-icon-download"
            class="action-btn export-btn"
            @click="handleExport"
            v-hasPermi="['system:mentorship:export']"
          >数据导出
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="mentorshipList"
        class="enhanced-table"
        :header-cell-style="headerStyle"
        :row-class-name="tableRowClassName"
      >
        <!-- 展开行 -->
        <el-table-column type="expand" width="60">
          <template #default="{row}">
            <div class="expanded-content">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="导师工号">{{ row.tutorId }}</el-descriptions-item>
                <el-descriptions-item label="导师姓名">{{ row.tutorName }}</el-descriptions-item>
                <el-descriptions-item label="指导地点">{{ row.guidanceLocation }}</el-descriptions-item>
                <el-descriptions-item label="提交时间" :span="2">
                  {{ parseTime(row.submitTime, '{y}-{m}-{d}') }}
                </el-descriptions-item>
                <el-descriptions-item label="学生评价">{{ row.studentComment }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </template>
        </el-table-column>
        <!-- 默认显示的列 -->
        <el-table-column label="序号" width="80" align="center">
          <template slot-scope="scope">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="学生学号" align="center" prop="studentId" min-width="120"/>
        <el-table-column label="学生姓名" align="center" prop="studentName" width="120"/>
        <el-table-column label="指导主题" align="center" prop="guidanceTopic" width="180"/>
        <el-table-column label="指导日期" align="center" prop="guidanceTime" width="150">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.guidanceTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="总结文档" width="160" align="center">
          <template v-slot="scope">
            <el-dropdown
              trigger="click"
              @command="handleDocCommand"
              :disabled="!scope.row.summaryFilePath"
            >
              <el-button
                type="primary"
                size="mini"
                plain
                class="file-btn"
                :disabled="!scope.row.summaryFilePath"
              >
                <i class="el-icon-document"></i> 文档操作
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  :command="{ action: 'preview', row: scope.row }"
                >预览
                </el-dropdown-item>
                <el-dropdown-item
                  :command="{ action: 'download', row: scope.row }"
                >下载
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
        <el-table-column label="指导图片" width="160" align="center">
          <template v-slot="scope">
            <el-dropdown trigger="click"
                         @command="handleFileCommand"
                         :disabled="!scope.row.photoPaths || scope.row.photoPaths === '[]'">
              <el-button type="primary"
                         size="mini"
                         plain
                         class="file-btn"
                         :disabled="!scope.row.photoPaths || scope.row.photoPaths === '[]'">
                <i class="el-icon-picture"></i> 图片操作
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  :command="{ action: 'preview', files: scope.row.photoPaths }"
                >预览
                </el-dropdown-item>
                <el-dropdown-item
                  :command="{ action: 'download', files: scope.row.photoPaths }"
                >下载
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
              @click="handleAudit(scope.row,'通过')"
              v-hasPermi="['system:mentorship:audit']"
              v-if="scope.row.auditStatus !== '已通过'"
            >通过
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-close"
              @click="handleAudit(scope.row,'拒绝')"
              v-hasPermi="['system:mentorship:audit']"
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
      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
    <!-- 文档预览对话框 -->
    <el-dialog
      :visible.sync="docPreviewVisible"
      title="文档预览"
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
import {listAuditMentorship, auditMentorship, getAuditCount} from "@/api/system/mentorship";
import {listAuditHistory} from "@/api/student/audit";
import axios from "axios";

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
        moduleType: 4, // 固定查询导师指导类型
        moduleId: null
      },
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      currentDownloadFile: '',
      docPreviewVisible: false,
      currentDocument: {
        url: '',
        type: '',
        name: ''
      },
      docxContent: '',
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
          {required: true, message: "学生学号不能为空", trigger: "blur"}
        ],
        tutorId: [
          {required: true, message: "导师工号不能为空", trigger: "blur"}
        ],
        guidanceTopic: [
          {required: true, message: "指导主题不能为空", trigger: "blur"}
        ],
        guidanceTime: [
          {required: true, message: "指导时间不能为空", trigger: "blur"}
        ],
        semester: [
          {required: true, message: "学期不能为空", trigger: "blur"}
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
      console.log(row.recordId)
      this.historyQueryParams.moduleId = row.recordId
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
        console.log(response)
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
    disablePdfInteractions(event) {
      try {
        const iframeDoc = event.target.contentDocument || event.target.contentWindow.document;
        // 禁用右键菜单
        iframeDoc.addEventListener('contextmenu', e => e.preventDefault());
        // 禁用文本选择
        iframeDoc.body.style.userSelect = 'none';
      } catch (error) {
        console.log('安全策略限制，部分交互禁用失败');
      }
    },
    // 处理文档操作命令
    handleDocCommand(command) {
      console.log(command.action)
      try {
        const filePath = command.row.summaryFilePath;
        if (!filePath) {
          this.$message.warning('无可用文档');
          return;
        }
        console.log("我进来了0" + command.row.summaryFilePath)
        const fileData = {
          url: `${process.env.VUE_APP_BASE_API}/profile/${filePath}`,
          type: this.getFileType(filePath),
          name: filePath.split('/').pop()
        };
        console.log(fileData)
        if (command.action === 'preview') {
          this.handleDocumentPreview(fileData);
          console.log("我进来了1")
        } else if (command.action === 'download') {
          console.log("我进来了2")
          this.downloadSummaryDocument(command.row);
        }
      } catch (error) {
        this.$message.error(`操作失败: ${error.message}`);
      }
    },

    // 处理文档预览
    async handleDocumentPreview(file) {
      const loading = this.$loading({
        lock: true,
        text: '正在加载文档...',
        spinner: 'el-icon-loading',
      });

      try {
        this.currentDocument = file;
        console.log("文件类型：" + file.type)
        if (file.type === 'pdf') {
          this.docPreviewVisible = true; // 直接显示iframe
        } else if (file.type === 'docx') {
          const response = await axios.get(file.url, {
            responseType: 'arraybuffer',
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`
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

    // 文件类型判断
    getFileType(filePath) {
      const extension = filePath.split('.').pop().toLowerCase();
      return {
        pdf: 'pdf',
        docx: 'docx',
        doc: 'doc'
      }[extension] || 'other';
    },

    // 下载当前文档
    downloadCurrentDocument() {
      this.downloadDocument(this.currentDocument);
    },
    handleFileCommand(command) {
      if (command.action === 'preview') {
        this.handlePreview(command.files)
      } else if (command.action === 'download') {
        this.downloadFiles(command.files)
      }
    },
    // 生成带时间戳的文件名
    generateSummaryFileName() {
      const date = new Date().toISOString().slice(0, 10);
      const ext = this.getSummaryFileExtension();
      return `summaryFilePath_${date}_${Math.random().toString(36).substr(2, 5)}.${ext}`;
    },

    // 获取文件扩展名
    getSummaryFileExtension() {
      if (!this.selectedFile) return '';
      const match = this.selectedFile.name.match(/\.([a-zA-Z0-9]+)(\?.*)?$/);
      return match ? match[1].toLowerCase() : '';
    },
    //总结文档下载
    async downloadSummaryDocument(row) {
      try {
        const filePaths = row.summaryFilePath;
        const fileName = row.summaryFileName || this.getFileName(filePaths);
        const link = document.createElement('a');
        link.href = `${process.env.VUE_APP_BASE_API}/profile/${filePaths}`;
        // link.download = this.generateSummaryFileName();
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
        console.error("下载错误详情:", error);
      }
    },
    async downloadFiles(filePaths) {
      try {
        // 解析文件路径
        const paths = typeof filePaths === 'string'
          ? JSON.parse(filePaths)
          : filePaths;
        if (!Array.isArray(paths)) {
          throw new Error("无效的文件路径格式");
        }
        // 处理多个文件下载
        if (paths.length > 1 || paths.length === 1) {
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
        } else if (paths.length === 1) {
          this.previewImage = this.getFullUrl(paths[0]);
          this.currentDownloadFile = paths[0];
          this.previewVisible = true;
        }
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
        console.error("下载错误详情:", error);
      }
    },
    // 下载单个文件
    async downloadSingleFile(filePath) {
      try {
        const response = await axios.get(
          filePath,
          {
            responseType: 'blob',
            headers: {
              Authorization: "Bearer " + localStorage.getItem("token")
            }
          }
        );
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
    generateFileName(filePath) {
      const originalName = filePath.split('/').pop() || '证明材料';
      const timestamp = new Date().getTime();
      const ext = originalName.split('.').pop() || 'jpg';
      return `${originalName.split('.')[0]}_${timestamp}.${ext}`;
    },

    // 获取完整URL（带缓存清除）
    getFullUrl(filePath) {
      return `${process.env.VUE_APP_BASE_API}/profile/${filePath}`;
    },
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
        this.$message.error('预览失败：文件路径格式不正确');
      }
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
    /** 查询导师指导记录列表 */
    getList() {
      this.loading = true;
      listAuditMentorship(this.queryParams).then(response => {
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
        studentName: null,
        tutorId: null,
        tutorName: null,
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
      this.queryParams.auditStatus = null;
      this.handleQuery();
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/mentorship/export', {
        ...this.queryParams
      }, `mentorship_${new Date().getTime()}.xlsx`)
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
        console.log('审核操作:', row.recordId, status, value);
        const auditData = {
          recordId: row.recordId,
          auditStatus: statusMapping[status],
          auditRemark: isApproved ? '系统审核通过' : value
        };

        // 调用专用审核接口
        auditMentorship(auditData).then(response => {
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
    tableRowClassName({rowIndex}) {
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
    },
  }
};
</script>
<style scoped>
/* ================= 文档样式美化 ================= */
.docx-preview {
  /* 容器样式 */
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  max-height: 70vh;
  overflow-y: auto;

  /* 滚动条美化 */

  &::-webkit-scrollbar {
    width: 8px;
    background: #f5f5f5;
  }

  &::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 4px;
  }

  .docx-content {
    /* 基础排版 */
    font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", Arial, sans-serif;
    line-height: 1.8;
    color: #333;
    max-width: 800px;
    margin: 0 auto;

    /* 标题层级 */

    h1, h2, h3, h4, h5, h6 {
      color: #2c3e50;
      margin: 1.5em 0 1em;
      font-weight: 600;
      position: relative;
      padding-left: 1rem;

      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        height: 60%;
        width: 4px;
        background: #42b983;
      }
    }

    h1 {
      font-size: 24px;
    }

    h2 {
      font-size: 22px;
    }

    h3 {
      font-size: 20px;
    }

    h4 {
      font-size: 18px;
    }

    /* 段落样式 */

    p {
      margin: 1em 0;
      text-indent: 2em;
    }

    /* 列表增强 */

    ul, ol {
      padding-left: 2em;
      margin: 1em 0;

      li {
        margin: 0.5em 0;
        padding-left: 0.5em;

        &::marker {
          color: #42b983;
        }
      }
    }

    /* 表格美化 */

    table {
      width: 100%;
      margin: 1.5em 0;
      border-collapse: collapse;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

      th {
        background: #f8f9fa;
        padding: 12px;
        font-weight: 600;
        border-bottom: 2px solid #dee2e6;
      }

      td {
        padding: 12px;
        border-bottom: 1px solid #dee2e6;
      }

      tr:nth-child(even) {
        background-color: #f8f9fa;
      }
    }

    /* 代码块样式 */

    pre {
      background: #f8f9fa;
      border-radius: 6px;
      padding: 1rem;
      margin: 1.5em 0;
      overflow-x: auto;

      code {
        font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
        color: #e83e8c;
      }
    }

    /* 引用样式 */

    blockquote {
      border-left: 4px solid #42b983;
      background: #f8f9fa;
      margin: 1.5em 0;
      padding: 1em 1.5em;
      color: #6c757d;

      p {
        margin: 0;
        text-indent: 0;
      }
    }

    /* 图片适配 */

    img {
      max-width: 80%;
      height: auto;
      display: block;
      margin: 1.5em auto;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    /* 页眉页脚隐藏 */

    .Header, .Footer {
      display: none;
    }
  }
}

/* 对话框过渡动画 */
.el-dialog__wrapper {
  ::v-deep .el-dialog {
    transition: all 0.3s ease;

    &__header {
      border-bottom: 1px solid #eee;
      padding: 20px;
    }

    &__body {
      padding: 0;
    }
  }
}

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

    i {
      color: #ffc107;
    }
  }

  .wave-effect {
    background: linear-gradient(to right,
    rgba(255, 193, 7, 0.05),
    rgba(255, 193, 7, 0.1)
    );
  }
}

.approved {
  .card-icon {
    background: rgba(76, 175, 80, 0.12);

    i {
      color: #4caf50;
    }
  }

  .wave-effect {
    background: linear-gradient(to right,
    rgba(76, 175, 80, 0.05),
    rgba(76, 175, 80, 0.1)
    );
  }
}

.rejected {
  .card-icon {
    background: rgba(244, 67, 54, 0.12);

    i {
      color: #f44336;
    }
  }

  .wave-effect {
    background: linear-gradient(to right,
    rgba(244, 67, 54, 0.05),
    rgba(244, 67, 54, 0.1)
    );
  }
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
  rgba(255, 255, 255, 0) 0%,
  rgba(224, 232, 255, 0.6) 50%,
  rgba(255, 255, 255, 0) 100%
  );
}

/* 表体样式 */
.enhanced-table ::v-deep .el-table__body td {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
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
/* 展开行样式优化 */
/* 展开箭头 */
.el-table__expand-icon {
  color: #409EFF;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 16px;
  position: relative;
}

.el-table__expand-icon:hover {
  color: #3375ff;
}

.el-table__expand-icon--expanded {
  transform: rotate(90deg) scale(1.1);
}

/* 内容容器 */
.expanded-content {
  padding: 16px 24px;
  background: #f8fafc;
  border-radius: 8px;
  margin: 8px 12px;
  box-shadow:
    inset 0 2px 4px rgba(0, 0, 0, 0.02),
    0 3px 6px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}

/* 渐变装饰线 */
.expanded-content::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(
    180deg,
    #409EFF 0%,
    rgba(64, 158, 255, 0.4) 100%
  );
}

/* 描述列表优化 */
.expanded-content ::v-deep .el-descriptions {
  margin: 8px 0;
}

.expanded-content ::v-deep .el-descriptions__label {
  width: 100px;
  color: #5a5e66;
  font-weight: 500;
  position: relative;
  padding-left: 8px;
}

.expanded-content ::v-deep .el-descriptions__label::after {
  content: ':';
  position: absolute;
  right: -12px;
  color: #909399;
}

.expanded-content ::v-deep .el-descriptions__content {
  color: #303133;
  padding-left: 12px;
}

/* 响应式处理 */
@media (max-width: 768px) {
  .expanded-content {
    padding: 12px;
    margin: 6px 8px;
  }

  .expanded-content ::v-deep .el-descriptions {
    --el-descriptions-table-border: 0;
  }

  .expanded-content ::v-deep .el-descriptions-item {
    display: flex;
    flex-direction: column;
    padding: 6px 0;
  }

  .expanded-content ::v-deep .el-descriptions__label {
    width: auto;
    font-size: 13px;
    padding-left: 0;
    margin-bottom: 4px;
  }

  .expanded-content ::v-deep .el-descriptions__label::after {
    content: none;
  }

  .expanded-content ::v-deep .el-descriptions__content {
    padding-left: 0;
    font-size: 14px;
  }
}

/* 入场动画 */
.el-table__expanded-cell {
  transition:
    background-color 0.3s ease,
    opacity 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-table__expanded-cell-enter-active {
  animation: expandSlide 0.3s ease-out;
}

@keyframes expandSlide {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
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
