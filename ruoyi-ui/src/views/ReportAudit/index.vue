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
      <el-table-column label="学号" align="center" prop="studentId"/>
      <el-table-column label="姓名" align="center" prop="studentName"/>
      <el-table-column label="讲座题目" align="center" prop="reportTitle"/>
      <el-table-column label="讲师姓名" align="center" prop="reporter"/>
      <el-table-column label="讲座时间" align="center" prop="reportDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reportDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="心得体会" width="120">
        <template v-slot:default="scope">
          <div class="proof-material-cell">
            <el-button
              type="primary"
              icon="el-icon-download"
              size="mini"
              v-if="scope.row.reportFeeling"
              @click="downloadReportFeeling(scope.row.reportFeeling)"
            >下载
            </el-button>
            <span v-else> </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="讲座内容简介" align="center" prop="reportContent"/>
      <el-table-column label="讲座链接" align="center" prop="reportLink"/>
      <el-table-column label="讲座海报" align="center" prop="lecturePoster"/>
      <el-table-column label="讲座海报">
        <template v-slot:default="scope">
          <img
            :src="getImageUrl(scope.row.lecturePoster)"
            alt="讲座海报"
            style="width: 50px; height: 50px; cursor: pointer;"
            v-if="scope.row.lecturePoster"
            @click="handleImageClick(scope.row.lecturePoster)"
          />
          <span v-else> </span>
        </template>
      </el-table-column>
      <el-table-column label="讲座现场照片" width="120">
        <template v-slot:default="scope">
          <div class="proof-material-cell">
            <el-link
              v-if="scope.row.reportPicture"
              type="primary"
              :underline="false"
              @click="handlePreview(scope.row.reportPicture)"
              style="margin-right: 10px;"
            >
              <i class="el-icon-view"></i> 预览
            </el-link>
            <el-button
              v-if="scope.row.reportPicture"
              type="primary"
              icon="el-icon-download"
              size="mini"
              @click="downloadFiles(scope.row.reportPicture)"
              :disabled="!scope.row.reportPicture"
            >下载
            </el-button>
            <span v-else> </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="reportAdmitTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reportAdmitTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="auditStatus" label="审核状态" min-width="80">
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

      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
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
    <!-- 讲座海报图片预览对话框 -->
    <el-dialog :visible.sync="dialogVisible" title="图片预览" width="50%">
      <div style="position: relative;">
        <img :src="getImageUrl(currentLecturePoster)" alt="报告海报大图" style="width: 100%; height: auto;"/>
        <div style="position: absolute; bottom: 20px; right: 20px;">
          <el-button
            type="primary"
            icon="el-icon-download"
            @click="downloadLecturePoster(currentLecturePoster)"
            style="background-color: #42b983; border-color: #42b983;">
            下载图片
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listAuditReport, auditReport} from "@/api/student/lecture";
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
  },
  methods: {
    //展开报告海报大图
    handleImageClick(imageUrl) {
      this.currentLecturePoster = imageUrl;
      this.dialogVisible = true;
    },
    // 获取完整图片路径
    getImageUrl(path) {
      return `${process.env.VUE_APP_BASE_API}/${path}`;
    },
    // 生成带时间戳的文件名
    generateReportFeeling() {
      const date = new Date().toISOString().slice(0, 10);
      const ext = this.getReportFeelingExtension();
      return `reportFeeling_${date}_${Math.random().toString(36).substr(2, 5)}.${ext}`;
    },

    // 获取文件扩展名
    getReportFeelingExtension() {
      if (!this.selectedFile) return '';
      const match = this.selectedFile.name.match(/\.([a-zA-Z0-9]+)(\?.*)?$/);
      return match ? match[1].toLowerCase() : '';
    },
    //总结文档下载
    async downloadReportFeeling(filePath) {
      try {
        const link = document.createElement('a');
        link.href = `${process.env.VUE_APP_BASE_API}/profile/${filePath}`;
        link.download = this.generateReportFeeling();
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
