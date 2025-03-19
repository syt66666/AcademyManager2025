<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学号" prop="studentId">
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
          v-hasPermi="['system:mentorship:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="mentorshipList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" width="80" align="center">
        <template slot-scope="scope">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="学生学号" align="center" prop="studentId" min-width="120"/>
      <el-table-column label="学生姓名" align="center" prop="studentName" min-width="120"/>
      <el-table-column label="导师工号" align="center" prop="tutorId" min-width="120"/>
      <el-table-column label="导师姓名" align="center" prop="tutorName" min-width="120"/>
      <el-table-column label="指导主题" align="center" prop="guidanceTopic" min-width="120"/>
      <el-table-column label="指导地点" align="center" prop="guidanceLocation" min-width="120"/>
      <el-table-column label="指导日期" align="center" prop="guidanceTime" min-width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.guidanceTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总结文档" width="120" align="center">
        <template v-slot="scope">
          <el-button
            type="primary"
            size="mini"
            @click.stop="downloadSummaryDocument(scope.row)"
            class="document-btn"
            :disabled="!scope.row.summaryFilePath || scope.row.summaryFilePath === '[]'"
          >下载
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="指导图片" width="140" align="center">
        <template v-slot="scope">
          <el-dropdown trigger="click"
                       @command="handleFileCommand"
                       :disabled="!scope.row.photoPaths || scope.row.photoPaths === '[]'">
            <el-button type="primary"
                       size="mini"
                       plain
                       :disabled="!scope.row.photoPaths ||                                                                                        scope.row.photoPaths === '[]'">
              <i class="el-icon-picture"></i> 文件操作
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
      <el-table-column label="提交时间" align="center" prop="submitTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.submitTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="学生评价" align="center" prop="studentComment" min-width="120"/>
      <el-table-column label="审核状态" align="center" prop="auditStatus" min-width="120">
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
            @click="handleAudit(scope.row,'通过')"
            v-hasPermi="['system:mentorship:audit']"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleAudit(scope.row,'拒绝')"
            v-hasPermi="['system:mentorship:audit']"
          >拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>

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
import { listAuditMentorship, auditMentorship } from "@/api/system/mentorship";
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
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      currentDownloadFile: '',
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
    async downloadSummaryDocument(filePath) {
      try {
        const link = document.createElement('a');
        link.href = `${process.env.VUE_APP_BASE_API}/profile/${filePath}`;
        link.download = this.generateSummaryFileName();
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
        if (paths.length > 1|| paths.length === 1) {
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
      ).then(({ value }) => {
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
    }
  }
};
</script>
