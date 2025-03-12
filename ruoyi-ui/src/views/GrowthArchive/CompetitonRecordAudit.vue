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
      <el-form-item label="竞赛名称" prop="competitionName">
        <el-input
          v-model="queryParams.competitionName"
          placeholder="请输入竞赛名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="获奖级别" prop="competitionLevel">
        <el-select
          v-model="queryParams.competitionLevel"
          placeholder="请选择活动级别"
          clearable
          @keyup.enter.native="handleQuery"
          style="width: 100%"
        >
          <el-option
            v-for="item in competitionLevelOptions"
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
          v-hasPermi="['system:record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" width="50" align="center">
        <template slot-scope="scope">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="学号" align="center" prop="studentId" />
      <el-table-column label="姓名" align="center" prop="studentName" />
      <el-table-column label="竞赛名称" align="center" prop="competitionName" />
      <el-table-column label="竞赛级别" align="center" prop="competitionLevel" />
      <el-table-column label="竞赛奖项" align="center" prop="awardLevel" />
      <el-table-column label="获奖日期" align="center" prop="awardDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.awardDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="证明材料" width="120">
        <template v-slot:default="scope">
          <div class="proof-material-cell">
            <el-link
              v-if="scope.row.proofMaterial"
              type="primary"
              :underline="false"
              @click="handlePreview(scope.row.proofMaterial)"
              style="margin-right: 10px;"
            >
              <i class="el-icon-view"></i> 预览
            </el-link>
            <el-button
              v-if="scope.row.proofMaterial"
              type="primary"
              icon="el-icon-download"
              size="mini"
              @click="downloadFiles(scope.row.proofMaterial)"
              :disabled="!scope.row.proofMaterial"
            >下载
            </el-button>
            <span v-else>无材料</span>
          </div>
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
            v-hasPermi="['system:competition:audit']"
            v-if="scope.row.auditStatus !== '已通过'"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleAudit(scope.row, '拒绝')"
            v-hasPermi="['system:competition:audit']"
            v-if="scope.row.auditStatus !== '未通过'"
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
import { listAuditCompetition,auditRecord } from "@/api/student/competition";
import axios from "axios";

export default {
  name: "Record",
  data() {
    return {
      dateRange:[],
      auditStatusOptions: [
        { value: '未审核', label: '未审核' },
        { value: '已通过', label: '已通过' },
        { value: '未通过', label: '未通过' }
      ],
      competitionLevelOptions: [
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
      // 学生科创竞赛记录表格数据
      recordList: [],
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
        competitionName: null,
        competitionLevel: null,
        awardLevel: null,
        scholarshipPoints: null,
        awardDate: null,
        proofMaterial: null,
        auditStatus: null,
        applyTime: null,
        nickName: null,
        auditTime: null,
        auditRemark: null,
        semester: null,
        studentName: null,
        awardDateBegin: null,
        awardDateEnd: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
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
    /** 查询学生科创竞赛记录列表 */
    getList() {
      this.loading = true;
      listAuditCompetition(this.queryParams).then(response => {
        this.recordList = response.rows;
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
        competitionId: null,
        studentId: null,
        competitionName: null,
        competitionLevel: null,
        awardLevel: null,
        scholarshipPoints: null,
        awardDate: null,
        proofMaterial: null,
        auditStatus: null,
        applyTime: null,
        nickName: null,
        auditTime: null,
        auditRemark: null,
        semester: null,
        studentName: null,
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
      this.ids = selection.map(item => item.competitionId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('system/record/export', {
        ...this.queryParams
      }, `record_${new Date().getTime()}.xlsx`)
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
        console.log('审核操作:', row.competitionId, status, value);
        const auditData = {
          competitionId: row.competitionId,
          auditStatus: statusMapping[status],
          auditRemark: isApproved ? '系统审核通过' : value
        };

        // 调用专用审核接口
        auditRecord(auditData).then(response => {
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

  },

};
</script>
