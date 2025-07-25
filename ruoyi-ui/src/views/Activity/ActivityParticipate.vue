<template>
  <div class="app-container">
    <!-- 搜索区域 - 美化 -->
    <div class="search-card">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <div class="search-row">
          <el-form-item label="活动名称" prop="activityName">
            <el-input
              v-model="queryParams.activityName"
              placeholder="请输入活动名称"
              clearable
              prefix-icon="el-icon-search"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="活动地点" prop="activityLocation">
            <el-input
              v-model="queryParams.activityLocation"
              placeholder="请输入活动地点"
              clearable
              prefix-icon="el-icon-location-outline"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="组织单位" prop="organizer">
            <el-input
              v-model="queryParams.organizer"
              placeholder="请输入组织单位"
              clearable
              prefix-icon="el-icon-office-building"
              class="search-input"
            />
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

    <!-- 活动列表 -->
    <div class="table-card">
      <el-table
        v-loading="loading"
        :data="activitiesList"
        style="width: 100%"
        class="modern-table"
        :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}"
      >
        <!-- 序号列 -->
        <el-table-column label="序号" width="80" align="center">
          <template v-slot="scope">
            <span class="index-badge">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="活动名称" align="center" prop="activityName" />
        <el-table-column label="活动地点" align="center" prop="activityLocation" />
        <el-table-column label="组织单位" align="center" prop="organizer" />
        <el-table-column label="开始时间" align="center" prop="startTime" >
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结束时间" align="center" prop="endTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>

        <el-table-column label="材料提交" align="center" class-name="small-padding fixed-width" width="120">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === '未提交'"
              type="primary"
              size="mini"
              class="upload-button"
              @click="openUploadDialog(scope.row)"
            ><i class="el-icon-upload"></i> 提交</el-button>
            <el-button
              v-if="scope.row.status === '未通过'"
              type="warning"
              size="mini"
              class="reupload-button"
              @click="openUploadDialog(scope.row)"
            ><i class="el-icon-refresh-left"></i> 重新提交</el-button>
            <el-tag
              v-if="scope.row.status === '未审核'"
              type="warning"
              effect="light"
              class="status-tag"
            >待审核</el-tag>
            <el-tag
              v-if="scope.row.status === '已通过'"
              type="success"
              effect="light"
              class="status-tag"
            >已通过</el-tag>
          </template>
        </el-table-column>
        <el-table-column type="expand" width="60" align="center">
          <template slot-scope="props">
            <div class="expand-card">
              <div class="expand-row">
                <div class="expand-label"><i class="el-icon-document"></i> 活动描述:</div>
                <div class="expand-content">{{ props.row.activityDescription }}</div>
              </div>
              <div class="expand-row">
                <div class="expand-label"><i class="el-icon-warning"></i> 注意事项:</div>
                <div class="expand-content">{{ props.row.notes }}</div>
              </div>
            </div>
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
        class="custom-pagination"
      />
    </div>

    <!-- 文件上传对话框 -->
    <el-dialog
      :title="uploadTitle"
      :visible.sync="uploadVisible"
      width="60%"
      :close-on-click-modal="false"
    >
      <!-- 图片上传区域 -->
      <div class="section">
        <h3>上传图片证明材料</h3>
        <el-upload
          :action="uploadUrl"
          list-type="picture-card"
          :file-list="imageFiles"
          :on-preview="handlePicturePreview"
          :on-remove="handleRemoveImage"
          :on-success="handleImageSuccess"
          :limit="5"
          multiple
          accept=".jpg,.jpeg,.png,.gif"
          :before-upload="beforeImageUpload"
          :headers="headers"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
        <div class="el-upload__tip">最多可上传5张图片，每张大小不超过2MB</div>
      </div>

      <!-- 文档上传区域 -->
      <div class="section">
        <h3>上传文档材料</h3>
        <el-upload
          :action="uploadUrl"
          :file-list="docFiles"
          :on-remove="handleRemoveDoc"
          :on-success="handleDocSuccess"
          :limit="1"
          accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx"
          :before-upload="beforeDocUpload"
          :headers="headers"
          :data="{ filePath: 'bookingImages' }"
        >
          <el-button size="small" type="primary">上传文件</el-button>
          <div slot="tip" class="el-upload__tip">
            支持上传PDF、Word、Excel、PPT等文档，大小不超过10MB
          </div>
        </el-upload>
      </div>

      <!-- 图片预览对话框 -->
      <el-dialog :visible.sync="imagePreviewVisible" width="60%">
        <img width="100%" :src="previewImageUrl" alt />
      </el-dialog>

      <span slot="footer" class="dialog-footer">
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProof">确认提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listBookingsWithActivity, updateBooking, getBooking } from "@/api/system/bookings";
import { getToken } from "@/utils/auth";

export default {
  name: "Activities",
  data() {
    return {
      // 上传相关数据
      uploadVisible: false,
      uploadTitle: "提交材料",
      currentBooking: null,

      // 上传URL和headers
      uploadUrl: process.env.VUE_APP_BASE_API + "/common/upload",
      headers: { Authorization: "Bearer " + getToken() },

      // 图片上传
      imageFiles: [],
      previewImageUrl: "",
      imagePreviewVisible: false,

      // 文档上传
      docFiles: [],

      // 已有文件
      existingFiles: [],

      // 遮罩层
      loading: true,
      showSearch: true,
      total: 0,
      activitiesList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: this.$store.state.user.name,
        activityName: null,
        startTime: null,
        endTime: null,
        activityLocation: null,
        organizer: null
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取文件的完整URL（用于回显）
    getFileFullUrl(fileName) {
      return process.env.VUE_APP_BASE_API  + fileName;
    },

    // 从URL中提取文件名的帮助函数
    extractFileName(url) {
      if (!url) return '';
      return url.substring(url.lastIndexOf('/') + 1);
    },

    // 修改初始化文件列表 - 修复回显问题
    initFileLists(data) {
      this.imageFiles = [];
      this.docFiles = [];

      // 初始化图片文件 - 使用文件名并拼接完整URL
      if (data.proof && Array.isArray(data.proof)) {
        data.proof.forEach(fileName => {
          this.imageFiles.push({
            name: fileName,
            url: this.getFileFullUrl(fileName),
            isOld: true // 标记为已有文件
          });
        });
      }

      // 初始化文档文件
      if (data.summary) {
        this.docFiles.push({
          name: data.summary,
          url: this.getFileFullUrl(data.summary),
          isOld: true // 标记为已有文件
        });
      }
    },

    // 修复图片上传成功处理
    handleImageSuccess(response, file, fileList) {
      if (response.code === 200) {
        // 获取上传的文件名
        const fileName = response.fileName || this.extractFileName(response.url);

        // 更新文件列表：只更新当前上传的文件
        this.imageFiles = fileList.map(f => {
          if (f.uid === file.uid) {
            return {
              name: fileName,
              url: this.getFileFullUrl(fileName),
              isOld: false // 标记为新上传文件
            };
          }
          return f;
        });
        this.$message.success('图片上传成功');
      } else {
        this.$message.error('图片上传失败: ' + response.msg);
      }
    },
    // 修复文档上传成功处理
    handleDocSuccess(response, file, fileList) {
      if (response.code === 200) {
        const fileName = response.fileName || this.extractFileName(response.url);

        this.docFiles = fileList.map(f => {
          if (f.uid === file.uid) {
            return {
              name: fileName,
              url: this.getFileFullUrl(fileName),
              isOld: false // 标记为新上传文件
            };
          }
          return f;
        });
        this.$message.success('文档上传成功');
      } else {
        this.$message.error('文档上传失败: ' + response.msg);
      }
    },

    // 图片预览
    handlePicturePreview(file) {
      if (file.url) {
        this.previewImageUrl = file.url;
        this.imagePreviewVisible = true;
      }
    },

    // 打开上传对话框
    async openUploadDialog(booking) {
      this.currentBooking = booking;
      this.uploadTitle = booking.status === '未提交' ? '提交材料' : '重新提交';
      this.uploadVisible = true;

      // 重置上传状态
      this.resetUploadState();

      try {
        // 获取已有的材料信息
        const res = await getBooking(booking.bookingId);
        if (res.code === 200) {
          const data = res.data || {};
          // 初始化文件列表
          this.initFileLists(data);
        }
      } catch (error) {
        console.error("获取材料信息失败:", error);
        this.$message.error("获取材料信息失败");
      }
    },

    // 提交证明材料
    async submitProof() {
      if (!this.currentBooking) return;

      try {
        // 准备proof字段 - 只提交文件名数组
        const proofFileNames = this.imageFiles.map(file => file.name);

        // 准备summary字段 - 只提交文件名（取第一个文档）
        const summaryFileName = this.docFiles.length > 0 ? this.docFiles[0].name : null;

        // 提交到后端
        const res = await updateBooking({
          bookingId: this.currentBooking.bookingId,
          proof: proofFileNames,
          summary: summaryFileName,
          status: "未审核"
        });

        if (res.code === 200) {
          this.$message.success("材料提交成功");
          this.uploadVisible = false;
          this.getList(); // 刷新列表
        } else {
          this.$message.error("材料提交失败: " + res.msg);
        }
      } catch (error) {
        this.$message.error("材料提交失败: " + error.message);
      }
    },

    // 重置上传状态
    resetUploadState() {
      this.imageFiles = [];
      this.docFiles = [];
      this.imagePreviewVisible = false;
    },

    // 图片上传前校验
    beforeImageUpload(file) {
      const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type);
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error('请上传图片格式文件!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!');
        return false;
      }
      return true;
    },

    // 文档上传前校验
    beforeDocUpload(file) {
      const allowedTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-powerpoint',
        'application/vnd.openxmlformats-officedocument.presentationml.presentation'
      ];

      const isDoc = allowedTypes.includes(file.type);
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isDoc) {
        this.$message.error('请上传PDF、Word、Excel或PPT文件!');
        return false;
      }
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过 10MB!');
        return false;
      }
      return true;
    },

    // 移除图片
    handleRemoveImage(file, fileList) {
      this.imageFiles = fileList;
    },

    // 移除文档
    handleRemoveDoc(file, fileList) {
      this.docFiles = fileList;
    },

    /** 查询活动列表 */
    getList() {
      this.loading = true;
      listBookingsWithActivity(this.queryParams).then(response => {
        this.activitiesList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
  }
};
</script>

<style scoped>
/* 全局样式 */
.app-container {
  margin-left: 100px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 搜索卡片样式 */
.search-card {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.search-input {
  width: 200px;
}

.search-input:hover {
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
}

/* 表格卡片样式 */
.table-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.modern-table {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.modern-table th {
  background-color: #f5f7fa !important;
  font-weight: 600;
  color: #303133;
}

/* 按钮样式 */
.search-button {
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  transition: all 0.3s;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 8px rgba(64, 158, 255, 0.4);
}

.refresh-button {
  background-color: #f0f2f5;
  border: none;
  padding: 8px 15px;
  color: #606266;
  border-radius: 4px;
  transition: all 0.3s;
}

.refresh-button:hover {
  background-color: #e4e7ed;
  color: #333;
}

/* 材料提交按钮样式 */
.upload-button {
  background-color: #409EFF;
  color: #fff;
  border: none;
  padding: 5px 8px;
  border-radius: 4px;
  font-size: 12px;
  transition: all 0.2s;
}

.upload-button:hover {
  background-color: #64b5ff;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
}

.reupload-button {
  background-color: #f39c12;
  color: #fff;
  border: none;
  padding: 5px 8px;
  border-radius: 4px;
  font-size: 12px;
  transition: all 0.2s;
}

.reupload-button:hover {
  background-color: #f4a726;
  box-shadow: 0 2px 4px rgba(243, 156, 18, 0.3);
}

.status-tag {
  padding: 5px 8px;
  font-size: 12px;
}

/* 扩展卡片 */
.expand-card {
  background: #f9fafc;
  border-radius: 8px;
  padding: 15px;
  margin: 5px;
  border-left: 3px solid #409EFF;
}

.expand-row {
  display: flex;
  margin-bottom: 12px;
}

.expand-label {
  font-weight: 600;
  min-width: 100px;
  color: #409EFF;
  display: flex;
  align-items: center;
}

.expand-label i {
  margin-right: 8px;
}

.expand-content {
  flex: 1;
  color: #606266;
  line-height: 1.6;
}

/* 分页样式 */
.custom-pagination {
  padding: 15px 0 0;
  display: flex;
  justify-content: center;
}

/* 上传对话框样式 */
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

.dialog-footer {
  text-align: right;
}
</style>
