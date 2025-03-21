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
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学号" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入学生学号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="讲座题目" prop="reportTitle">
        <el-input
          v-model="queryParams.reportTitle"
          placeholder="请输入题目"
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
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reportList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" width="50" align="center">
        <template slot-scope="scope">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="学号" align="center" prop="studentId" width="120"/>
      <el-table-column label="姓名" align="center" prop="studentName"/>
      <el-table-column label="讲座题目" align="center" prop="reportTitle"/>
      <el-table-column label="讲师姓名" align="center" prop="reporter"/>
      <el-table-column label="讲座地点" align="center" prop="reportLocation"/>
      <el-table-column label="讲座日期" align="center" prop="reportDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reportDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="reportLink" label="讲座链接" width="120" align="center">
        <template v-slot="scope">
          <el-tag
            :effect="scope.row.reportLink ? 'light' : 'plain'"
            :class="!scope.row.reportLink ? 'disabled-tag' : ''"
            class="location-tag"
          >
            <a
              :href="scope.row.reportLink || 'javascript:void(0)'"
              :class="{ 'disabled-link': !scope.row.reportLink }"
              class="link-style"
              target="_blank"
              rel="noopener noreferrer"
            >
              点击查看
            </a>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总结文档" width="140" align="center">
        <template v-slot="scope">
          <el-dropdown
            trigger="click"
            @command="handleDocCommand"
            :disabled="!scope.row.reportFeeling"
          >
            <el-button
              type="primary"
              size="mini"
              plain
              :disabled="!scope.row.reportFeeling"
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
      <el-table-column label="现场图片" width="140" align="center">
        <template v-slot="scope">
          <el-dropdown trigger="click" @command="handleFileCommand"
                       :disabled="!scope.row.reportPicture || scope.row.reportPicture === '[]'">
            <el-button type="primary" size="mini" plain
                       :disabled="!scope.row.reportPicture || scope.row.reportPicture === '[]'">
              <i class="el-icon-picture"></i> 图片操作
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                :command="{ action: 'preview', files: scope.row.reportPicture }"
                :disabled="!scope.row.reportPicture"
              >预览
              </el-dropdown-item>
              <el-dropdown-item
                :command="{ action: 'download', files: scope.row.reportPicture }"
                :disabled="!scope.row.reportPicture"
              >下载
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="reportAdmitTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reportAdmitTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="auditStatus" label="审核状态" min-width="100">
        <template slot-scope="scope">
            <span>
              <el-tag v-if="formatAuditStatus(scope.row.auditStatus) === '未审核'"
                      type="warning">{{ formatAuditStatus(scope.row.auditStatus) }}</el-tag>
              <el-tag v-else-if="formatAuditStatus(scope.row.auditStatus) === '已通过'"
                      type="success">{{ formatAuditStatus(scope.row.auditStatus) }}</el-tag>
              <el-tag v-else-if="formatAuditStatus(scope.row.auditStatus) === '未通过'"
                      type="danger">{{ formatAuditStatus(scope.row.auditStatus) }}</el-tag>
              <el-tag v-else>未知状态</el-tag>
            </span>
        </template>
      </el-table-column>
      <el-table-column label="审核时间" align="center" prop="auditTime" width="140">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核意见" align="center" prop="auditRemark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleAudit(scope.row,'通过')"
            v-hasPermi="['system:report:audit']"
          >通过
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleAudit(scope.row,'拒绝')"
            v-hasPermi="['system:report:audit']"
          >拒绝
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
  </div>
</template>

<script>
import {listAuditReport, auditReport, getAuditCount} from "@/api/student/lecture";
import axios from "axios";

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
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      currentDownloadFile: '',
      dialogVisible: false,
      currentLecturePoster: '',
      // ...其他变量保持不变
      docPreviewVisible: false,
      currentDocument: {
        url: '',
        type: '',
        name: ''
      },
      docxContent: '',
      // 统计数字
      auditStats: {
        approved: 0,
        rejected: 0,
        pending: 0
      },
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
      rules: {}
    };
  },
  created() {
    this.getList();
    this.fetchAuditCount();
  },
  methods: {
    // 获取统计数据
    async fetchAuditCount() {
      try {
        const { code, data } = await getAuditCount();
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
        const filePath = command.row.reportFeeling;
        if (!filePath) {
          this.$message.warning('无可用文档');
          return;
        }
        console.log("我进来了0" + command.row.reportFeeling)
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
          this.downloadReportFeeling(command.row);
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

    // 下载文档
    downloadDocument(file) {
      axios.get(file.url, {
        responseType: 'blob',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      }).then(response => {
        const blob = new Blob([response.data]);
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = file.name;
        document.body.appendChild(link);
        link.click();
        URL.revokeObjectURL(link.href);
        document.body.removeChild(link);
      }).catch(error => {
        this.$message.error('下载失败: ' + error.message);
      });
    },
    // 在methods中添加以下方法
    handleFileCommand(command) {
      console.log(command.files);
      if (command.action === 'preview') {
        this.handlePreview(command.files)
      } else if (command.action === 'download') {
        this.downloadReportPicture(command.files)
      }
    },
    async downloadReportPicture(filePaths) {
      try {
        // 解析图片路径
        const paths = typeof filePaths === 'string'
          ? JSON.parse(filePaths)
          : filePaths;
        if (!Array.isArray(paths)) {
          throw new Error("无效的图片路径格式");
        }
        // 处理多个文件下载
        if (paths.length >= 1) {
          this.$confirm(`本次下载包含${paths.length}个图片，是否继续？`, '批量下载提示', {
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
        console.error("下载错误详情:", error);
      }
    },
// 获取完整图片路径
    getImageUrl(path) {
      return `${process.env.VUE_APP_BASE_API}/${path}`;
    },
    //总结文档下载
    async downloadReportFeeling(row) {
      try {
        const filePaths = row.reportFeeling;
        const fileName = row.reportFeelingName || this.getFileName(filePaths);
        const link = document.createElement('a');
        link.href = `${process.env.VUE_APP_BASE_API}/profile/${filePaths}`;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
        console.error("下载错误详情:", error);
      }
    },
    async downloadLecturePoster(filePath) {
      const url = `${process.env.VUE_APP_BASE_API}/${filePath}`;
      await this.downloadSingleFile(url);
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
    // 辅助方法：从路径获取文件名
    getFileName(path) {
      return path.split('/').pop().replace(/\?.*/, '');
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
    // 格式化审核状态
    formatAuditStatus(status) {
      switch (status) {
        case 0:
          return "未审核";
        case 1:
          return "已通过";
        case 2:
          return "未通过";
        case 3:
          return "未提交";
        default:
          return "未审核";
      }
    },
    /** 查询学生参与报告信息列表 */
    getList() {
      this.loading = true;
      listAuditReport(this.queryParams).then(response => {
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
      this.single = selection.length !== 1
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
      this.download('system/lecture/export', {
        ...this.queryParams
      }, `report_${new Date().getTime()}.xlsx`)
    },

    handleAudit(row, status) {
      const isApproved = status === '通过';
      const statusMapping = {
        '通过': 1,
        '拒绝': 2,
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
        console.log('审核操作:', row.reportId, statusMapping[status], value);
        const auditData = {
          reportId: row.reportId,
          auditStatus: statusMapping[status],
          auditRemark: isApproved ? '系统审核通过' : value
        };

        // 调用专用审核接口
        auditReport(auditData).then(response => {
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


<style lang="scss" scoped>
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

/* 禁用状态的链接样式 */
.disabled-link {
  color: #c0c4cc !important; /* Element UI 禁用文本色 */
  cursor: not-allowed !important;
  text-decoration: none !important;
  pointer-events: none; /* 彻底禁用交互 */
  opacity: 0.7; /* 统一降低透明度 */
}

/* 禁用状态的标签容器 */
.disabled-tag {
  background-color: #f5f7fa !important; /* Element 禁用背景色 */
  border-color: #e4e7ed !important; /* 边框颜色与背景协调 */
}

/* 正常状态的链接样式 */
.location-tag .link-style {
  color: var(--el-color-primary); /* 使用 Element 主题色 */
  text-decoration: none;
  transition: all 0.2s; /* 添加悬流动画 */
}

/* 悬停状态增强体验 */
.location-tag .link-style:hover {
  text-decoration: underline;
  opacity: 0.8;
}

</style>
