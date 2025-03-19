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
      <el-table-column label="姓名" align="center" prop="studentName" />
      <el-table-column label="活动名称" align="center" prop="activityName" />
      <el-table-column label="获奖级别" align="center" prop="activityLevel" />
      <el-table-column label="获奖奖项" align="center" prop="awardLevel" />
      <el-table-column label="获奖日期" align="center" prop="awardDate" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.awardDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="证明材料" width="140" align="center">
        <template v-slot="scope">
          <el-dropdown trigger="click" @command="handleFileCommand" @click.native.stop :disabled="!scope.row.proofMaterial || scope.row.proofMaterial.length === 0">
            <el-button type="primary" size="mini" plain @click.stop :disabled="!scope.row.proofMaterial || scope.row.proofMaterial.length === 0">
              <i class="el-icon-document"></i> 文件操作
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
import { listAuditActivity, getActivity, auditActivity  } from "@/api/system/activity";
import {parseTime} from "@/utils/ruoyi";
import axios from "axios";

export default {
  name: "Activity",
    data() {
    return {
      previewVisible: false,
      currentDownloadFile: '',
      previewImages: [],
      currentPreviewIndex: 0,
      dateRange:[],
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
        awardDateBegin:null,
        awardDateEnd:null,
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
        awardDateBegin: null,
        awardDateEnd: null
      };
      // 触发查询
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
