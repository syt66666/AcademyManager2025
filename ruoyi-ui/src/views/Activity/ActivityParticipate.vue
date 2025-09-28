<template>
  <div class="app-container">
    <!-- 统计大盒子 -->
    <div class="stats-card">
      <div class="card-header">
        <i class="el-icon-data-analysis"></i>
        <span>活动统计</span>
      </div>
      
      <div class="stats-content">
        <!-- 左侧状态统计 -->
        <div class="status-stats">
          <h3>活动状态筛选</h3>
          <div class="status-items">
            <div 
              class="status-item" 
              :class="{ active: selectedStatus === '未提交' }"
              @click="filterByStatus('未提交')"
            >
              <div class="status-icon unsubmitted">
                <i class="el-icon-upload2"></i>
              </div>
              <div class="status-info">
                <div class="status-count">{{ statusCounts.unsubmitted }}</div>
                <div class="status-label">未提交</div>
              </div>
            </div>
            
            <div 
              class="status-item" 
              :class="{ active: selectedStatus === '未通过' }"
              @click="filterByStatus('未通过')"
            >
              <div class="status-icon rejected">
                <i class="el-icon-close"></i>
              </div>
              <div class="status-info">
                <div class="status-count">{{ statusCounts.rejected }}</div>
                <div class="status-label">未通过</div>
              </div>
            </div>
            
            <div 
              class="status-item" 
              :class="{ active: selectedStatus === '已通过' }"
              @click="filterByStatus('已通过')"
            >
              <div class="status-icon approved">
                <i class="el-icon-check"></i>
              </div>
              <div class="status-info">
                <div class="status-count">{{ statusCounts.approved }}</div>
                <div class="status-label">已通过</div>
              </div>
            </div>
            
            <div 
              class="status-item clear-filter" 
              :class="{ active: selectedStatus === null }"
              @click="clearStatusFilter"
            >
              <div class="status-icon all">
                <i class="el-icon-view"></i>
              </div>
              <div class="status-info">
                <div class="status-count">{{ allActivitiesList.length }}</div>
                <div class="status-label">全部</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 右侧进度条 -->
        <div class="progress-stats">
          <h3>活动完成进度</h3>
          <div class="progress-items">
            <div 
              v-for="(progress, type) in activityProgress" 
              :key="type"
              class="progress-item"
              :class="{ active: selectedActivityType === type }"
              @click="filterByActivityType(type)"
            >
              <div class="progress-label">{{ getActivityTypeName(type) }}</div>
              <div class="progress-bar-container">
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ width: progress.percentage + '%' }"
                    :class="getProgressBarClass(progress.percentage, type)"
                  ></div>
                </div>
                <div class="progress-text">{{ progress.completed }}/8</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 活动列表 -->
    <div class="table-card">
      <div class="card-header">
        <i class="el-icon-tickets"></i>
        <span>我的活动记录</span>
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>
      <el-table
        v-loading="loading"
        :data="activitiesList"
        style="width: 100%"
        class="modern-table"
        :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}"
        :row-class-name="getRowClassName"
      >
        <!-- 序号列 -->
        <el-table-column label="序号" width="80" align="center">
          <template v-slot="scope">
            <span 
              class="index-badge" 
              :class="{ 'rejected-badge': scope.row.status === '未通过' }"
            >
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="活动名称" align="center" prop="activityName" width="200">
          <template slot-scope="scope">
            <div class="activity-name" :title="scope.row.activityName">
              {{ truncateText(scope.row.activityName, 15) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="活动类型" align="center" prop="activityType" width="200">
          <template slot-scope="scope">
            <el-tag :type="getActivityTypeTagType(scope.row.activityType)" effect="plain" class="activity-type-tag">
              {{ getActivityTypeName(scope.row.activityType) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="活动地点" align="center" prop="activityLocation" />
        <el-table-column label="组织单位" align="center" prop="organizer" />
        <el-table-column label="活动开始时间" align="center" prop="startTime" >
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="活动结束时间" align="center" prop="endTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>

        <el-table-column label="材料提交" align="center" width="120">
          <template slot-scope="scope">
            <!-- 活动未结束时显示提示信息 -->
            <el-tag
              v-if="!isActivityEnded(scope.row.endTime)"
              type="info"
              effect="light"
              class="status-tag"
            >活动未结束</el-tag>
            <!-- 活动结束后显示上传按钮或状态标签 -->
            <template v-else>
              <el-button
                v-if="scope.row.status === '未提交'"
                type="text"
                size="mini"
                class="action-button upload-button"
                @click="openUploadDialog(scope.row)"
              >提交</el-button>
              <!-- 未通过状态 -->
              <el-tag
                v-if="scope.row.status === '未通过'"
                type="danger"
                effect="light"
                class="status-tag clickable-tag"
                @click="openUploadDialog(scope.row)"
                :title="scope.row.reviewComment || '点击查看审核意见'"
              >
                重新提交
              </el-tag>
              <!-- 未审核状态 - 添加点击功能 -->
              <el-tag
                v-if="scope.row.status === '未审核'"
                type="warning"
                effect="light"
                class="clickable-tag"
                @click="openUploadDialog(scope.row)"
              >
                未审核
              </el-tag>
              <!-- 已通过状态 - 添加点击功能 -->
              <el-tag
                v-if="scope.row.status === '已通过'"
                type="success"
                effect="light"
                class="status-tag clickable-tag"
                @click="openUploadDialog(scope.row)"
              >
                已通过
              </el-tag>
            </template>
          </template>
        </el-table-column>

        <el-table-column type="expand" width="60" align="center">
          <template slot-scope="props">
            <div class="expand-card">
              <div class="expand-row">
                <div class="expand-label"><i class="el-icon-document"></i> 活动描述:</div>
                <div class="expand-content">{{ props.row.activityDescription }}</div>
              </div>
              <div class="expand-row" v-if="props.row.pictureUrl">
                <div class="expand-label"><i class="el-icon-picture"></i> 活动图片:</div>
                <div class="expand-content">
        <div class="activity-image-container">
          <el-image
            :src="getActivityImageUrl(props.row.pictureUrl)"
            :preview-src-list="[getActivityImageUrl(props.row.pictureUrl)]"
            fit="cover"
            class="activity-image"
          />
        </div>
                </div>
              </div>
              <!-- <div class="expand-row">
                <div class="expand-label"><i class="el-icon-warning"></i> 注意事项:</div>
                <div class="expand-content">{{ props.row.notes }}</div>
              </div> -->
              <div class="expand-row" v-if="!isActivityEnded(props.row.endTime)">
                <div class="expand-label"><i class="el-icon-time"></i> 材料提交提示:</div>
                <div class="expand-content upload-tip">
                  <i class="el-icon-info"></i>
                  活动尚未结束，请等待活动结束后再提交证明材料。活动结束时间：{{ parseTime(props.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
                </div>
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
      <!-- 审核意见显示区域 -->
      <div v-if="currentBooking && currentBooking.status === '未通过' && currentBooking.reviewComment" class="audit-feedback-section">
        <h3><i class="el-icon-warning-outline"></i> 审核意见</h3>
        <div class="audit-feedback-content">
          <div class="audit-feedback-text">{{ currentBooking.reviewComment }}</div>
          <div class="audit-feedback-meta">
            <span class="audit-time">
              <i class="el-icon-time"></i>
              审核时间：{{ parseTime(currentBooking.reviewTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
            </span>
            <span v-if="currentBooking.reviewer" class="audit-reviewer">
              <i class="el-icon-user"></i>
              审核人：{{ currentBooking.reviewer }}
            </span>
          </div>
        </div>
      </div>

      <!-- 状态提示 -->
      <div v-if="isViewOnly" class="view-only-notice">
        <el-alert
          title="当前状态下不可添加或修改材料"
          type="info"
          :closable="false"
          show-icon>
        </el-alert>
      </div>

      <!-- 图片上传区域 - 只在非只读模式下显示 -->
      <div v-if="!isViewOnly" class="section">
        <h3>上传图片证明材料</h3>
        <div class="image-upload-container">
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
            :auto-upload="true"
            class="image-upload"
        >
            <div class="upload-add-card">
              <i class="el-icon-plus upload-add-icon"></i>
              <div class="upload-add-text">添加图片</div>
            </div>
        </el-upload>
          <div class="upload-tip">
            <i class="el-icon-info"></i>
            最多可上传5张图片，每张大小不超过2MB，支持JPG、PNG、GIF格式
          </div>
        </div>
      </div>

      <!-- 图片展示区域 - 只在只读模式下显示 -->
      <div v-if="isViewOnly" class="section">
        <h3>图片材料</h3>
        <div v-if="imageFiles && imageFiles.length" class="proof-grid">
          <div
            v-for="(file, idx) in imageFiles"
            :key="idx"
            class="proof-card"
          >
            <el-image
              :src="getPreviewImageUrl(file)"
              fit="cover"
              class="proof-thumb"
              @click="handlePicturePreview(file)"
            />
            <div class="proof-overlay">
              <el-button
                size="mini"
                circle
                icon="el-icon-view"
                @click.stop="handlePicturePreview(file)"
              />
              <el-button
                size="mini"
                circle
                icon="el-icon-download"
                @click.stop="downloadFileReliably(file)"
                :loading="file.downloading"
              />
            </div>
          </div>
          <div class="proof-actions">
            <el-button size="small" icon="el-icon-view" @click="handlePicturePreview(imageFiles[0])">预览全部</el-button>
            <el-button size="small" icon="el-icon-download" type="primary" @click="downloadAllImages">全部下载</el-button>
          </div>
        </div>
        <div v-else class="empty-tip">暂无图片材料</div>
      </div>


      <!-- 文档上传区域 - 只在非只读模式下显示 -->
      <div v-if="!isViewOnly" class="section">
        <h3>上传文档材料</h3>
        <div class="upload-container">
        <el-upload
          :action="uploadUrl"
          :file-list="docFiles"
          :on-remove="handleRemoveDoc"
          :on-success="handleDocSuccess"
          :limit="1"
          accept=".pdf"
          :before-upload="beforeDocUpload"
          :headers="headers"
          :data="{ filePath: 'bookingImages' }"
            :auto-upload="true"
            drag
            class="document-upload"
            :show-file-list="false"
          >
            <div class="upload-drag-area">
              <i class="el-icon-upload upload-icon"></i>
              <div class="upload-text">
                <p class="upload-main-text">将PDF文件拖拽到此处，或</p>
                <p class="upload-sub-text">点击选择文件</p>
              </div>
            </div>
            <div slot="tip" class="upload-tip">
              <i class="el-icon-info"></i>
            仅支持上传PDF格式文档，大小不超过10MB
          </div>
        </el-upload>
          
          <!-- 自定义文件列表显示 -->
          <div v-if="docFiles && docFiles.length > 0" class="custom-file-list">
            <div v-for="(file, index) in docFiles" :key="index" class="custom-file-item">
              <i class="el-icon-document file-icon"></i>
              <span class="file-name">{{ getFileNameOnly(file.name) }}</span>
              <el-button 
                type="text" 
                icon="el-icon-delete" 
                class="remove-btn"
                @click="handleRemoveDoc(file)"
              ></el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 文档展示区域 - 只在只读模式下显示 -->
      <div v-if="isViewOnly" class="section">
        <h3>文档材料</h3>
        <div v-if="docFiles && docFiles.length" class="doc-card">
          <div class="doc-left">
            <i :class="['file-icon', getDocIconClass(docFiles[0].name)]"></i>
            <div class="file-meta">
              <div class="file-name" :title="docFiles[0].name">{{ docFiles[0].name }}</div>
              <el-tag size="mini" type="info">{{ getFileType(docFiles[0].name).toUpperCase() }}</el-tag>
            </div>
          </div>
          <div class="doc-actions">
            <el-button size="mini" icon="el-icon-view" @click="handleDocumentPreview(docFiles[0])">预览</el-button>
            <el-button size="mini" icon="el-icon-download" type="primary" @click="downloadFileReliably(docFiles[0])" :loading="docFiles[0].downloading">下载</el-button>
          </div>
        </div>
        <div v-else class="empty-tip">暂无文档材料</div>
      </div>


      <!-- 图片预览对话框 -->
      <el-dialog 
        :visible.sync="previewVisible" 
        title="图片预览" 
        width="70%" 
        top="5vh" 
        class="preview-dialog"
        :modal="true"
        :close-on-click-modal="true"
        :close-on-press-escape="true"
        append-to-body
      >
        <div class="preview-content">
          <div class="preview-image-container">
            <img
              :src="currentPreviewImage"
              :alt="currentPreviewName"
              class="preview-large-image"
            />
          </div>

          <div class="preview-controls">
            <el-button
              icon="el-icon-arrow-left"
              :disabled="currentPreviewIndex === 0"
              @click="prevImage"
              size="small"
            >上一张</el-button>

            <span class="preview-count">
          {{ currentPreviewIndex + 1 }} / {{ previewImages.length }}
        </span>

            <el-button
              icon="el-icon-arrow-right"
              :disabled="currentPreviewIndex === previewImages.length - 1"
              @click="nextImage"
              size="small"
            >下一张</el-button>
          </div>
          <!-- 缩略图导航 -->
          <div v-if="previewImages.length > 1" class="thumbnail-navigation">
            <div
              v-for="(img, index) in previewImages"
              :key="index"
              :class="['thumbnail-item', { active: index === currentPreviewIndex }]"
              @click="switchToImage(index)"
            >
              <img :src="img.url" :alt="img.name" class="thumbnail-image" />
            </div>
          </div>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="previewVisible = false">关闭</el-button>
          <el-button
            type="primary"
            @click="downloadCurrentPreviewImage"
            :loading="currentPreviewDownloading"
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
        :modal="true"
        :close-on-click-modal="true"
        :close-on-press-escape="true"
        append-to-body
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

      <!-- 只在未提交和未通过状态下显示底部按钮 -->
      <span slot="footer" class="dialog-footer" v-if="showFooterButtons">
        <el-button @click="uploadVisible = false">取消</el-button>
    <el-button type="primary" @click="submitProof">
      {{ currentBooking && currentBooking.status === '未通过' ? '重新提交' : '确认提交' }}
    </el-button>
      </span>
    </el-dialog>

    <!-- 活动图片预览对话框 -->
    <el-dialog
      title="活动图片预览"
      :visible.sync="activityImagePreviewVisible"
      width="60%"
      append-to-body
      class="image-preview-dialog">
      <div class="preview-container">
        <img :src="previewActivityImageUrl" class="preview-image" />
      </div>
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
      total: 0,
      activitiesList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: this.$store.state.user.name
      },
      
      // 统计相关数据
      selectedStatus: null, // 当前选中的状态筛选
      allActivitiesList: [], // 所有活动数据，用于统计计算
      statusCounts: {
        unsubmitted: 0,  // 未提交
        rejected: 0,     // 已拒绝
        approved: 0      // 已通过
      },
      activityProgress: {
        '1': { completed: 0, percentage: 0 }, // 人格塑造与价值引领活动类
        '2': { completed: 0, percentage: 0 }, // 知识融合与思维进阶活动类
        '3': { completed: 0, percentage: 0 }, // 能力锻造与实践创新活动类
        '4': { completed: 0, percentage: 0 }  // 社会责任与领军意识活动类
      },
          // 进度条筛选：当前选中的活动类型
          selectedActivityType: null,
      isViewOnly: false, // 是否为只查看模式
      showFooterButtons: false, // 是否显示底部按钮

      // 预览相关
      // 活动图片预览
      activityImagePreviewVisible: false,
      previewActivityImageUrl: '',
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      currentPreviewImage: '',
      currentPreviewName: '',
      docPreviewVisible: false,
      currentDocument: {
        url: '',
        type: '',
        name: ''
      },
      docxContent: '',
      pdfError: '',
      downloading: false,
      currentPreviewDownloading: false,

    };
  },
  created() {
    this.getAllActivitiesForStats();
    this.getList();
  },
  methods: {
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

    // 检查活动是否已结束
    isActivityEnded(endTime) {
      if (!endTime) return false;
      const now = new Date();
      const activityEndTime = new Date(endTime);
      return now > activityEndTime;
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
    // 获取文件的完整URL（用于显示）
    getFileFullUrl(fileName) {
      if (!fileName) return '';

      // 如果已经是完整URL，直接返回
      if (fileName.startsWith('http://') || fileName.startsWith('https://') || fileName.startsWith('data:')) {
        return fileName;
      }

      // 确保路径格式正确
      let normalizedPath = fileName;
      if (!normalizedPath.startsWith('/')) {
        normalizedPath = '/' + normalizedPath;
      }

      const baseUrl = process.env.VUE_APP_BASE_API || '';
      return `${baseUrl}${normalizedPath}`;
    },

    // 获取预览图片URL
    getPreviewImageUrl(file) {
      if (!file) return '';

      // 新上传的文件
      if (file.url && (file.url.startsWith('http') || file.url.startsWith('data:'))) {
        return file.url;
      }

      // 服务器返回的文件
      if (file.url) {
        return this.getFileFullUrl(file.url);
      }

      // response中的文件
      if (file.response && file.response.fileName) {
        return this.getFileFullUrl(file.response.fileName);
      }

      return '';
    },

    // 获取下载URL
    getDownloadUrl(file) {
      const url = this.getPreviewImageUrl(file);
      return url;
    },


    // 从URL中提取文件名
    extractFileName(url) {
      if (!url) return 'unknown';

      try {
        // 移除URL参数和查询字符串
        const cleanUrl = url.split('?')[0].split('#')[0];
        return cleanUrl.substring(cleanUrl.lastIndexOf('/') + 1);
      } catch (e) {
        return 'file';
      }
    },


    // 初始化文件列表
    initFileLists(data) {
      this.imageFiles = [];
      this.docFiles = [];
      // 初始化图片文件
      if (data.proof && Array.isArray(data.proof)) {
        data.proof.forEach(fileName => {
          if (fileName) {
            const fullUrl = this.getFileFullUrl(fileName);
          this.imageFiles.push({
            name: this.extractFileName(fileName),
              url: fullUrl,
              isOld: true,
              downloading: false // 添加下载状态
          });
          }
        });
      }
      // 初始化文档文件
      if (data.summary) {
        const fullUrl = this.getFileFullUrl(data.summary);
        this.docFiles.push({
          name: this.extractFileName(data.summary),
          url: fullUrl,
          isOld: true,
          downloading: false
        });
      }
    },

    // 修复图片上传成功处理
    handleImageSuccess(response, file, fileList) {
      if (response.code === 200) {
        const fileName = response.fileName || this.extractFileName(response.url);

        this.imageFiles = fileList.map(f => {
          if (f.uid === file.uid) {
            return {
              name: fileName,
              url: this.getFileFullUrl(fileName),
              isOld: false,
              downloading: false // 添加下载状态
            };
          }
          return {
            ...f,
            downloading: f.downloading || false // 确保有下载状态
          };
        });
        this.$message.success('图片上传成功');
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
              isOld: false,
              downloading: false // 添加下载状态
            };
          }
          return {
            ...f,
            downloading: f.downloading || false // 确保有下载状态
          };
        });
        this.$message.success('文档上传成功');
      }
    },

// 处理图片预览
    handlePicturePreview(file) {

      // 构建预览图片数组，包含URL和文件名
      this.previewImages = this.imageFiles
        .filter(f => {
          const url = this.getPreviewImageUrl(f);
          return url && url.length > 0;
        })
        .map(f => ({
          url: this.getPreviewImageUrl(f),
          name: f.name || this.extractFileNameFromUrl(this.getPreviewImageUrl(f))
        }));


      if (this.previewImages.length === 0) {
        this.$message.warning('没有可预览的图片');
        return;
      }

      // 找到当前图片的索引
      const currentUrl = this.getPreviewImageUrl(file);
      const currentIndex = this.previewImages.findIndex(img => img.url === currentUrl);

      this.currentPreviewIndex = currentIndex >= 0 ? currentIndex : 0;
      this.updatePreviewImage();


      this.previewVisible = true;
      
    },

    // 更新当前预览图片
    updatePreviewImage() {
      if (this.previewImages.length > 0 && this.currentPreviewIndex >= 0) {
        const currentImage = this.previewImages[this.currentPreviewIndex];
        this.currentPreviewImage = currentImage.url;
        this.currentPreviewName = currentImage.name;
      }
    },

    // 切换到上一张图片
    prevImage() {
      if (this.currentPreviewIndex > 0) {
        this.currentPreviewIndex--;
        this.updatePreviewImage();
      }
    },

    // 切换到下一张图片
    nextImage() {
      if (this.currentPreviewIndex < this.previewImages.length - 1) {
        this.currentPreviewIndex++;
        this.updatePreviewImage();
      }
    },

    // 切换到指定图片
    switchToImage(index) {
      if (index >= 0 && index < this.previewImages.length) {
        this.currentPreviewIndex = index;
        this.updatePreviewImage();
      }
    },

    // 获取文件类型
    getFileType(fileName) {
      if (!fileName) return 'other';
      const extension = fileName.split('.').pop().toLowerCase();
      return {
        pdf: 'pdf',
        docx: 'docx',
        doc: 'doc'
      }[extension] || 'other';
    },

    // 获取文档图标类
    getDocIconClass(fileName) {
      const type = this.getFileType(fileName);
      const map = { pdf: 'icon-pdf', docx: 'icon-doc', doc: 'icon-doc', other: 'icon-file' };
      return map[type] || 'icon-file';
    },

    // 下载所有图片
    downloadAllImages() {
      if (!this.imageFiles || this.imageFiles.length === 0) {
        this.$message.warning('没有可下载的图片');
        return;
      }
      
      this.imageFiles.forEach(file => {
        this.downloadFileReliably(file);
      });
    },

    // 获取文件名（不包含路径）
    getFileNameOnly(fileName) {
      if (!fileName) return '未知文件';
      
      // 如果包含路径分隔符，取最后一部分
      const parts = fileName.split('/');
      return parts[parts.length - 1];
    },

    // 从URL中提取文件名
    extractFileNameFromUrl(url) {
      if (!url) return '未知文件';

      try {
        const urlObj = new URL(url);
        const pathname = urlObj.pathname;
        const filename = pathname.split('/').pop();
        return filename || '未知文件';
      } catch (e) {
        // 如果不是有效的URL，尝试直接处理
        const parts = url.split('/');
        return parts[parts.length - 1] || '未知文件';
      }
    },

// 下载单个文件
    downloadSingleFile(url, fileName = null) {
      const downloadFileName = fileName || url.split('/').pop();

      // 使用新的可靠下载方法
      this.downloadFileReliably({
        url: url,
        name: downloadFileName
      });
    },

    // 打开上传对话框
    async openUploadDialog(booking) {
      // 检查活动是否已结束
      if (!this.isActivityEnded(booking.endTime)) {
        this.$message.warning("活动尚未结束，无法提交材料。请等待活动结束后再提交。");
        return;
      }

      this.currentBooking = booking;
      // 根据状态设置不同的对话框标题、模式和按钮显示
      let title = "提交材料";
      let viewOnly = false;
      let showButtons = true;

      if (booking.status === '未通过') {
        title = '重新提交';
        viewOnly = false;
        showButtons = true;
      } else if (booking.status === '未审核') {
        title = '查看材料';
        viewOnly = true;
        showButtons = false;
      } else if (booking.status === '已通过') {
        title = '查看材料';
        viewOnly = true;
        showButtons = false;
      } else if (booking.status === '未提交') {
        title = '提交材料';
        viewOnly = false;
        showButtons = true;
      }

      this.uploadTitle = title;
      this.isViewOnly = viewOnly; // 设置是否为只查看模式
      this.showFooterButtons = showButtons; // 设置是否显示底部按钮
      this.uploadVisible = true;
      // 重置上传状态
      this.resetUploadState();

      try {
        // 获取已有的材料信息
        const res = await getBooking(booking.bookingId);
        if (res.code === 200) {
          const data = res.data || {};
          // 更新当前预约信息，包含审核意见等详细字段
          this.currentBooking = {
            ...this.currentBooking,
            ...data
          };
          // 初始化文件列表
          this.initFileLists(data);
        }
      } catch (error) {
        this.$message.error("获取材料信息失败");
      }
    },

    // 提交证明材料
    async submitProof() {
      if (!this.currentBooking) return;

      // 再次检查活动是否已结束（防止在对话框打开期间活动状态变化）
      if (!this.isActivityEnded(this.currentBooking.endTime)) {
        this.$message.warning("活动尚未结束，无法提交材料。请等待活动结束后再提交。");
        this.uploadVisible = false;
        return;
      }

      try {
        // 准备proof字段 - 区分已有文件和新上传文件
        const proofFileNames = this.imageFiles.map(file => {
          if (file.isOld) {
            // 对于已有文件，从URL中提取完整路径
            return file.url.replace(process.env.VUE_APP_BASE_API, '');
          } else {
            // 对于新上传的文件，使用文件名
            return file.name;
          }
        });

        // 准备summary字段 - 区分已有文件和新上传文件
        let summaryFileName = null;
        if (this.docFiles.length > 0) {
          const docFile = this.docFiles[0];
          if (docFile.isOld) {
            // 对于已有文件，从URL中提取完整路径
            summaryFileName = docFile.url.replace(process.env.VUE_APP_BASE_API, '');
          } else {
            // 对于新上传的文件，使用文件名
            summaryFileName = docFile.name;
          }
        }

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
          this.getAllActivitiesForStats(); // 刷新统计数据
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
      const isPdf = file.type === 'application/pdf';
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isPdf) {
        this.$message.error('请上传PDF格式文件!');
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
      if (fileList) {
        // Element UI 上传组件调用时，fileList 参数存在
        this.imageFiles = [...fileList];
      } else {
        // 手动删除时，从数组中移除文件
        const index = this.imageFiles.findIndex(f => f.uid === file.uid || f.name === file.name);
        if (index > -1) {
          this.imageFiles.splice(index, 1);
        }
      }
      this.$forceUpdate(); // 强制更新视图
    },

    // 移除文档
    handleRemoveDoc(file, fileList) {
      if (fileList) {
        // Element UI 上传组件调用时，fileList 参数存在
        this.docFiles = [...fileList];
      } else {
        // 自定义删除按钮调用时，手动从数组中移除文件
        const index = this.docFiles.findIndex(f => f.uid === file.uid || f.name === file.name);
        if (index > -1) {
          this.docFiles.splice(index, 1);
        }
      }
      this.$forceUpdate(); // 强制更新视图
    },

    /** 获取所有活动数据用于统计 */
    getAllActivitiesForStats() {
      const statsParams = {
        pageNum: 1,
        pageSize: 1000, // 获取足够多的数据
        studentId: this.$store.state.user.name
      };
      
      listBookingsWithActivity(statsParams).then(response => {
        this.allActivitiesList = response.rows || [];
        // 更新统计数据
        this.updateStatistics();
      }).catch(error => {
        this.allActivitiesList = [];
      });
    },

    /** 查询活动列表 */
    getList() {
      this.loading = true;
      listBookingsWithActivity(this.queryParams).then(response => {
        let rows = response.rows || [];

        // 本地筛选：如果选择了进度条类型，则只显示该类型且状态为“已通过”的记录
        if (this.selectedActivityType) {
          rows = rows.filter(item => String(item.activityType) === String(this.selectedActivityType) && item.status === '已通过');
        }

        // 对活动列表按照材料提交状态进行排序（同优先级下按开始时间由晚到早）
        const sortedRows = this.sortActivitiesByStatus(rows);
        this.activitiesList = sortedRows;
        this.total = rows.length;
        this.loading = false;
      });
    },

    /** 通过进度条筛选：点击某类型，筛选出已通过的该类型活动；再次点击同类型则清除筛选 */
    filterByActivityType(type) {
      // 切换选中状态
      if (this.selectedActivityType === type) {
        this.selectedActivityType = null;
      } else {
        this.selectedActivityType = type;
        // 同时将状态筛选切到“已通过”以匹配需求
        this.selectedStatus = '已通过';
        this.queryParams.pageNum = 1;
        this.queryParams.status = '已通过';
      }

      // 当清空类型筛选时，还原状态筛选（仅当之前因类型筛选设置为已通过时）
      if (this.selectedActivityType === null && this.selectedStatus === '已通过') {
        this.selectedStatus = null;
        delete this.queryParams.status;
      }

      // 重新拉取数据并应用本地筛选
      this.getList();
    },

    /** 按照材料提交状态排序活动列表 */
    sortActivitiesByStatus(activities) {
      // 定义状态优先级：未通过 > 活动未结束 > 未审核 > 已通过
      const statusPriority = {
        '未通过': 1,
        '未提交': 2,  // 活动未结束对应未提交状态
        '未审核': 3,
        '已通过': 4
      };

      return activities.sort((a, b) => {
        // 获取状态优先级
        const priorityA = statusPriority[a.status] || 999;
        const priorityB = statusPriority[b.status] || 999;
        
        // 如果优先级相同，按活动开始时间排序（最新的在前）
        if (priorityA === priorityB) {
          return new Date(b.startTime) - new Date(a.startTime);
        }
        
        // 按优先级排序
        return priorityA - priorityB;
      });
    },

    /** 更新统计数据 */
    updateStatistics() {
      this.calculateStatusCounts();
      this.calculateActivityProgress();
    },

    /** 计算状态统计 */
    calculateStatusCounts() {
      this.statusCounts = {
        unsubmitted: 0,
        rejected: 0,
        approved: 0
      };

      // 使用所有活动数据计算统计
      this.allActivitiesList.forEach(activity => {
        switch(activity.status) {
          case '未提交':
            this.statusCounts.unsubmitted++;
            break;
          case '未通过':
            this.statusCounts.rejected++;
            break;
          case '已通过':
            this.statusCounts.approved++;
            break;
        }
      });
    },

    /** 计算活动类型进度 */
    calculateActivityProgress() {
      // 重置进度
      Object.keys(this.activityProgress).forEach(type => {
        this.activityProgress[type] = { completed: 0, percentage: 0 };
      });

      // 统计已通过的活动（使用所有活动数据）
      this.allActivitiesList.forEach(activity => {
        if (activity.status === '已通过' && activity.activityType) {
          const type = activity.activityType.toString();
          if (this.activityProgress[type]) {
            this.activityProgress[type].completed++;
          }
        }
      });

      // 计算百分比（每个类型完成8次满进度）
      Object.keys(this.activityProgress).forEach(type => {
        const completed = this.activityProgress[type].completed;
        this.activityProgress[type].percentage = Math.min((completed / 8) * 100, 100);
      });
    },

    /** 根据状态筛选 */
    filterByStatus(status) {
      this.selectedStatus = status;
      this.queryParams.pageNum = 1;
      
      // 添加状态筛选参数
      if (status) {
        this.queryParams.status = status;
      } else {
        delete this.queryParams.status;
      }
      
      this.getList();
    },

    /** 清除状态筛选 */
    clearStatusFilter() {
      this.selectedStatus = null;
      this.queryParams.pageNum = 1;
      delete this.queryParams.status;
      this.getList();
    },

    /** 获取进度条样式类 */
    getProgressBarClass(percentage, activityType) {
      const baseClass = this.getActivityTypeBaseClass(activityType);
      if (percentage >= 100) return `${baseClass}-full`;
      if (percentage >= 75) return `${baseClass}-high`;
      if (percentage >= 50) return `${baseClass}-medium`;
      if (percentage >= 25) return `${baseClass}-low`;
      return `${baseClass}-empty`;
    },

    /** 获取活动类型对应的基础样式类 */
    getActivityTypeBaseClass(activityType) {
      const map = {
        '1': 'progress-primary',   // 人格塑造与价值引领活动类 - 蓝色
        '2': 'progress-success',   // 知识融合与思维进阶活动类 - 绿色
        '3': 'progress-warning',   // 能力锻造与实践创新活动类 - 橙色
        '4': 'progress-danger'     // 社会责任与领军意识活动类 - 红色
      };
      return map[activityType] || 'progress-info';
    },

    /** 获取表格行的CSS类名 */
    getRowClassName({ row, rowIndex }) {
      if (row.status === '未通过') {
        return 'rejected-row';
      }
      return '';
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

    downloadSummaryFile(filePath) {
      if (!filePath) {
        this.$message.warning('无可用总结文档');
        return;
      }

      const url = `${process.env.VUE_APP_BASE_API}${filePath}`;
      this.downloadSingleFile(url);
    },

    // 处理文档预览
    handleDocumentPreview(file) {
      if (file.url) {
        // 判断文件类型
        const ext = file.name.split('.').pop().toLowerCase();

        if (ext === 'pdf') {
          // PDF文件预览
          this.currentDocument = {
            url: file.url,
            type: 'pdf',
            name: file.name
          };
          this.pdfError = '';
          this.docPreviewVisible = true;
        } else if (['doc', 'docx'].includes(ext)) {
          // Word文档提示下载
          this.$message.info('Word文档暂不支持在线预览，请下载后查看');
          this.downloadSingleFile(file.url);
        } else {
          // 其他文件类型直接下载
          this.downloadSingleFile(file.url);
        }
      }
    },

    // 可靠的下载方法
    async downloadFileReliably(file) {
      try {
        // 设置下载状态
        this.$set(file, 'downloading', true);

        let downloadUrl = '';
        let fileName = '';

        // 确定下载URL和文件名
        if (file.url && (file.url.startsWith('http') || file.url.startsWith('data:'))) {
          downloadUrl = file.url;
          fileName = file.name || this.extractFileName(file.url);
        } else if (file.response && file.response.fileName) {
          downloadUrl = this.getFileFullUrl(file.response.fileName);
          fileName = file.name || this.extractFileName(file.response.fileName);
        } else if (file.url) {
          downloadUrl = this.getFileFullUrl(file.url);
          fileName = file.name || this.extractFileName(file.url);
        } else {
          throw new Error('无法获取文件下载地址');
        }


        // 方法1: 使用fetch API下载
        try {
          const response = await fetch(downloadUrl);
          if (!response.ok) throw new Error('网络响应不正常');

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
        }

        // 方法2: 直接创建链接下载（备用）
        const a = document.createElement('a');
        a.href = downloadUrl;
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
        if (file.url) {
          window.open(file.url, '_blank');
        }
      } finally {
        this.$set(file, 'downloading', false);
      }
    },



    // 统一的文件下载方法
    async downloadFile(file) {
      try {
        // 设置下载状态
        this.$set(file, 'downloading', true);

        let filePath = '';
        let fileName = '';

        // 确定文件路径和名称
        if (file.isOld) {
          // 已有文件：从URL中提取路径
          filePath = file.url.replace(process.env.VUE_APP_BASE_API, '');
          fileName = file.name;
        } else if (file.response && file.response.fileName) {
          // 新上传的文件：使用response中的文件名
          filePath = file.response.fileName;
          fileName = file.name || this.extractFileName(filePath);
        } else if (file.url) {
          // 其他情况：尝试从URL提取
          filePath = file.url.replace(process.env.VUE_APP_BASE_API, '');
          fileName = file.name || this.extractFileName(filePath);
        } else {
          throw new Error('无法获取文件路径');
        }


        // 使用API下载文件
        const response = await downloadFile(filePath, fileName);

        if (response.code === 200) {
          this.$message.success('文件下载成功');
        } else {
          throw new Error(response.msg || '下载失败');
        }
      } catch (error) {
        this.$message.error('文件下载失败: ' + (error.message || '请稍后重试'));

        // 降级方案：尝试直接下载
        this.fallbackDownload(file);
      } finally {
        this.$set(file, 'downloading', false);
      }
    },

    // 降级下载方案
    fallbackDownload(file) {
      try {
        let downloadUrl = '';

        if (file.url) {
          downloadUrl = file.url;
        } else if (file.response && file.response.fileName) {
          downloadUrl = this.getFileFullUrl(file.response.fileName);
        }

        if (downloadUrl) {
          // 创建隐藏的iframe进行下载
          const iframe = document.createElement('iframe');
          iframe.style.display = 'none';
          iframe.src = downloadUrl;
          document.body.appendChild(iframe);

          setTimeout(() => {
            document.body.removeChild(iframe);
          }, 5000);

          this.$message.info('正在尝试下载文件...');
        }
      } catch (fallbackError) {
      }
    },

    // 下载当前预览的图片
// 下载当前预览的图片
    async downloadCurrentPreviewImage() {
      if (!this.currentPreviewImage || this.previewImages.length === 0) {
        this.$message.warning('没有可下载的图片');
        return;
      }

      try {
        this.currentPreviewDownloading = true;

        const currentImage = this.previewImages[this.currentPreviewIndex];
        const downloadUrl = currentImage.url;
        const fileName = currentImage.name || this.extractFileName(downloadUrl);

        // 使用统一的可靠下载方法
        await this.downloadFileReliably({
          url: downloadUrl,
          name: fileName
        });

      } catch (error) {
        this.$message.error('图片下载失败');
      } finally {
        this.currentPreviewDownloading = false;
      }
    },

    /** 预览活动图片 */
    previewActivityImage(imageUrl) {
      this.previewActivityImageUrl = imageUrl;
      this.activityImagePreviewVisible = true;
    },

    /** 获取活动图片完整URL（仿照审核界面实现） */
    getActivityImageUrl(pictureUrl) {
      if (!pictureUrl) return '';
      
      // 如果已经是完整URL，直接返回
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        return pictureUrl;
      }
      
      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径（仿照审核界面）
      if (pictureUrl.startsWith('/profile/')) {
        return `${process.env.VUE_APP_BASE_API}${pictureUrl}`;
      }
      
      return pictureUrl;
    },

    /** 截断文本显示 */
    truncateText(text, maxLength) {
      if (!text) return '';
      if (text.length <= maxLength) return text;
      return text.substring(0, maxLength) + '...';
    }

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

/* 统一卡片样式 */
.stats-card,
.table-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.stats-card:hover,
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

/* 统计大盒子样式 */
.stats-content {
  display: flex;
  gap: 40px;
  margin-top: 20px;
}

.status-stats,
.progress-stats {
  flex: 1;
}

.status-stats h3,
.progress-stats h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #f0f2f5;
  padding-bottom: 10px;
}

/* 状态统计样式 */
.status-items {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.status-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
}

.status-item:hover {
  background: #f0f9ff;
  border-color: #bae6fd;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.status-item.active {
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  border-color: #409EFF;
  color: white;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.status-item.active .status-count,
.status-item.active .status-label {
  color: white;
}

.status-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 18px;
}

.status-icon.unsubmitted {
  background: linear-gradient(135deg, #f39c12, #f1c40f);
  color: white;
}

.status-icon.rejected {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: white;
}

.status-icon.approved {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
}

.status-icon.all {
  background: linear-gradient(135deg, #6c757d, #495057);
  color: white;
}

.status-info {
  display: flex;
  flex-direction: column;
}

.status-count {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.status-label {
  font-size: 14px;
  color: #606266;
  margin-top: 4px;
}

/* 进度条样式 */
.progress-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.progress-label {
  min-width: 200px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.progress-item.active .progress-label {
  color: #303133;
  font-weight: 700;
}

.progress-item:hover {
  transform: translateY(-1px);
}

.progress-bar-container {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar {
  flex: 1;
  height: 12px;
  background: #f0f2f5;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
}

.progress-fill {
  height: 100%;
  border-radius: 6px;
  transition: all 0.6s ease;
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* 人格塑造与价值引领活动类 - 蓝色系 */
.progress-primary-empty {
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
}

.progress-primary-low {
  background: linear-gradient(135deg, #90caf9, #64b5f6);
}

.progress-primary-medium {
  background: linear-gradient(135deg, #42a5f5, #2196f3);
}

.progress-primary-high {
  background: linear-gradient(135deg, #1e88e5, #1976d2);
}

.progress-primary-full {
  background: linear-gradient(135deg, #1565c0, #0d47a1);
}

/* 知识融合与思维进阶活动类 - 绿色系 */
.progress-success-empty {
  background: linear-gradient(135deg, #e8f5e8, #c8e6c9);
}

.progress-success-low {
  background: linear-gradient(135deg, #a5d6a7, #81c784);
}

.progress-success-medium {
  background: linear-gradient(135deg, #66bb6a, #4caf50);
}

.progress-success-high {
  background: linear-gradient(135deg, #43a047, #388e3c);
}

.progress-success-full {
  background: linear-gradient(135deg, #2e7d32, #1b5e20);
}

/* 能力锻造与实践创新活动类 - 橙色系 */
.progress-warning-empty {
  background: linear-gradient(135deg, #fff3e0, #ffcc80);
}

.progress-warning-low {
  background: linear-gradient(135deg, #ffb74d, #ffa726);
}

.progress-warning-medium {
  background: linear-gradient(135deg, #ff9800, #fb8c00);
}

.progress-warning-high {
  background: linear-gradient(135deg, #f57c00, #ef6c00);
}

.progress-warning-full {
  background: linear-gradient(135deg, #e65100, #d84315);
}

/* 社会责任与领军意识活动类 - 红色系 */
.progress-danger-empty {
  background: linear-gradient(135deg, #ffebee, #ffcdd2);
}

.progress-danger-low {
  background: linear-gradient(135deg, #ef9a9a, #e57373);
}

.progress-danger-medium {
  background: linear-gradient(135deg, #f44336, #e53935);
}

.progress-danger-high {
  background: linear-gradient(135deg, #d32f2f, #c62828);
}

.progress-danger-full {
  background: linear-gradient(135deg, #b71c1c, #8d1a1a);
}

.progress-text {
  min-width: 40px;
  font-size: 12px;
  color: #606266;
  font-weight: 600;
  text-align: center;
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


/* 材料提交按钮样式 - 统一文字按钮风格 */
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

.upload-button {
  color: #409EFF;
}

.upload-button:hover {
  background-color: rgba(64, 158, 255, 0.1);
}

.reupload-button {
  color: #ef4444;
}

.reupload-button:hover {
  background-color: rgba(239, 68, 68, 0.1);
}

.status-tag {
  padding: 5px 8px;
  font-size: 12px;
}

/* 重新提交标签样式 */
.status-tag.reupload-tag {
  cursor: pointer;
  transition: all 0.3s ease;
}

.status-tag.reupload-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 活动未结束标签特殊样式 */
.status-tag[type="info"] {
  background-color: #f4f4f5;
  border-color: #d3d4d6;
  color: #909399;
  font-weight: 500;
}

/* 序号徽章 */
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

/* 未通过状态的红色序号徽章 */
.index-badge.rejected-badge {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
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

/* 活动类型标签 */
.activity-type-tag {
  font-weight: 500;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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

/* 上传提示样式 */
.upload-tip {
  background-color: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 6px;
  padding: 12px;
  color: #0369a1;
  font-weight: 500;
}

.upload-tip i {
  margin-right: 6px;
  color: #0284c7;
}

/* 分页样式 */
.custom-pagination {
  margin-top: 24px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-content {
    flex-direction: column;
    gap: 30px;
  }
  
  .status-items {
    justify-content: center;
  }
  
  .progress-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .progress-label {
    min-width: auto;
    width: 100%;
  }
  
  .progress-bar-container {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .status-items {
    flex-direction: column;
    align-items: stretch;
  }
  
  .status-item {
    min-width: auto;
    justify-content: center;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .record-count {
    margin-left: 0;
  }
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

/* 审核意见显示区域样式 */
.audit-feedback-section {
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #fff5f5, #fef2f2);
  border: 1px solid #fecaca;
  border-radius: 12px;
  border-left: 4px solid #f87171;
}

.audit-feedback-section h3 {
  margin: 0 0 15px 0;
  color: #dc2626;
  font-weight: 600;
  font-size: 16px;
  display: flex;
  align-items: center;
}

.audit-feedback-section h3 i {
  margin-right: 8px;
  font-size: 18px;
}

.audit-feedback-content {
  background: white;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #f3f4f6;
}

.audit-feedback-text {
  color: #374151;
  line-height: 1.6;
  font-size: 14px;
  margin-bottom: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 6px;
  border-left: 3px solid #e5e7eb;
}

.audit-feedback-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #6b7280;
}

.audit-time,
.audit-reviewer {
  display: flex;
  align-items: center;
}

.audit-time i,
.audit-reviewer i {
  margin-right: 4px;
  color: #9ca3af;
}

.dialog-footer {
  text-align: right;
}

/* 图片卡片容器 */
.image-card-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: 6px;
}

/* 悬浮层样式 */
.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 6px;
}

/* 鼠标悬停时显示悬浮层 */
.image-card-container:hover .image-overlay {
  opacity: 1;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 10px;
}
.action-buttons .el-button {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  color: #606266;
  transition: all 0.3s ease;
}
.action-buttons .el-button:hover {
  background: #409EFF;
  color: white;
  transform: scale(1.1);
}
/* 调整预览对话框样式 */
.preview-dialog {
  z-index: 4000 !important;
}

.preview-dialog .el-dialog {
  z-index: 4001 !important;
}

.preview-dialog .el-dialog__wrapper {
  z-index: 4000 !important;
}

.preview-dialog .el-dialog__body {
  padding: 20px;
}

/* 审核界面样式 - 与ActivityAudit.vue保持一致 */
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
  text-align: center;
  padding: 20px;
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

/* 美化上传区域样式 */
.upload-container {
  margin-top: 15px;
}

.document-upload {
  width: 100%;
}

.upload-drag-area {
  padding: 20px;
  text-align: center;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s ease;
  cursor: pointer;
}

.upload-drag-area:hover {
  border-color: #409EFF;
  background: #f0f9ff;
}

.upload-icon {
  font-size: 32px;
  color: #c0c4cc;
  margin-bottom: 12px;
  display: block;
}

.upload-text {
  color: #606266;
}

.upload-main-text {
  font-size: 14px;
  margin: 0 0 6px 0;
  font-weight: 500;
}

.upload-sub-text {
  font-size: 12px;
  margin: 0;
  color: #409EFF;
}

.upload-tip {
  margin-top: 15px;
  padding: 12px 16px;
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 6px;
  color: #409EFF;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.upload-tip i {
  font-size: 16px;
}

/* 图片上传美化 */
.image-upload-container {
  margin-top: 15px;
}

.image-upload .el-upload--picture-card {
  width: 120px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s ease;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  position: relative !important;
  padding: 0 !important;
  margin: 0 !important;
}

.image-upload .el-upload--picture-card:hover {
  border-color: #409EFF;
  background: #f0f9ff;
}

/* 移除Element UI默认的padding和margin */
.image-upload .el-upload--picture-card * {
  margin: 0 !important;
  padding: 0 !important;
}

.upload-add-card {
  display: flex !important;
  flex-direction: column !important;
  align-items: center !important;
  justify-content: center !important;
  height: 100% !important;
  width: 100% !important;
  color: #606266;
  position: relative;
  margin: 0 !important;
  padding: 0 !important;
}

.upload-add-icon {
  font-size: 28px;
  margin-bottom: 6px !important;
  color: #c0c4cc;
  display: block;
  line-height: 1;
  margin-top: 0 !important;
  margin-left: 0 !important;
  margin-right: 0 !important;
}

.upload-add-text {
  font-size: 12px;
  color: #909399;
  display: block;
  line-height: 1;
  margin-top: 0 !important;
  margin-bottom: 0 !important;
  margin-left: 0 !important;
  margin-right: 0 !important;
}

/* 已上传文件卡片美化 */
.image-upload .el-upload-list--picture-card .el-upload-list__item {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  overflow: hidden;
}

.image-upload .el-upload-list--picture-card .el-upload-list__item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 文档上传列表美化 */
.document-upload .el-upload-list__item {
  margin-top: 15px;
  padding: 12px 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.document-upload .el-upload-list__item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.document-upload .el-upload-list__item .el-icon-document {
  color: #f56c6c;
  font-size: 20px;
  margin-right: 12px;
}

.document-upload .el-upload-list__item .el-upload-list__item-name {
  color: #303133;
  font-weight: 500;
}

.document-upload .el-upload-list__item .el-upload-list__item-status-label {
  background: #67c23a;
  border-radius: 50%;
}

/* 自定义文件列表样式 */
.custom-file-list {
  margin-top: 15px;
}

.custom-file-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s ease;
  margin-bottom: 8px;
}

.custom-file-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.custom-file-item .file-icon {
  color: #f56c6c;
  font-size: 20px;
  margin-right: 12px;
  flex-shrink: 0;
}

.custom-file-item .file-name {
  flex: 1;
  color: #303133;
  font-weight: 500;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.custom-file-item .remove-btn {
  color: #f56c6c;
  padding: 0;
  margin-left: 8px;
  flex-shrink: 0;
}

.custom-file-item .remove-btn:hover {
  color: #f78989;
}
.preview-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 15px;
  gap: 15px;
}
.preview-count {
  margin: 0 15px;
  font-weight: 500;
  color: #606266;
}

/* 只读模式通知 */
.view-only-notice {
  margin-bottom: 20px;
}
/* 上传区域禁用状态 */
.upload-disabled {
  opacity: 0.6;
  pointer-events: none;
}
/* 只读模式下的文件列表样式 */
.readonly-file-list {
  margin-top: 15px;
}
.readonly-file-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  margin-bottom: 8px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}
.readonly-file-icon {
  margin-right: 8px;
  color: #409EFF;
}
.readonly-file-name {
  flex: 1;
  font-size: 14px;
  color: #606266;
}
.readonly-file-actions {
  display: flex;
  gap: 8px;
}

/* 只读模式通知 */
.view-only-notice {
  margin-bottom: 20px;
}

/* 只读模式下的文件列表样式 */
.readonly-file-list {
  margin-top: 15px;
}

.readonly-file-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  margin-bottom: 8px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.readonly-file-icon {
  margin-right: 8px;
  color: #409EFF;
}

.readonly-file-name {
  flex: 1;
  font-size: 14px;
  color: #606266;
}

.readonly-file-actions {
  display: flex;
  gap: 8px;
}

/* 无数据提示 */
.no-data {
  text-align: center;
  color: #909399;
  padding: 30px 0;
}

.no-data i {
  font-size: 50px;
  margin-bottom: 10px;
  color: #dcdfe6;
}

.no-data p {
  margin: 0;
}
/* 图片预览列表 */
.image-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-top: 15px;
}

.image-preview-item {
  width: 150px;
  margin-bottom: 15px;
}

.image-container {
  position: relative;
  width: 150px;
  height: 120px;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.image-container:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-container:hover .image-overlay {
  opacity: 1;
}

.image-actions {
  display: flex;
  gap: 8px;
}

.image-name {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
  text-align: center;
  word-break: break-all;
  line-height: 1.4;
}

/* 预览对话框样式 */
.preview-dialog .el-dialog__body {
  padding: 20px;
}

.preview-controls {
  margin-top: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.preview-count {
  font-size: 14px;
  color: #606266;
  margin: 0 10px;
}
/* 预览对话框样式 */
.preview-dialog .el-dialog__body {
  padding: 20px;
}

.preview-content {
  text-align: center;
}

.preview-image-container {
  margin-bottom: 20px;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-large-image {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.preview-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.preview-count {
  font-size: 14px;
  color: #606266;
  min-width: 120px;
  text-align: center;
}

.preview-name {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 缩略图导航 */
.thumbnail-navigation {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 15px;
  padding: 10px;
  background: #f9f9f9;
  border-radius: 4px;
}

.thumbnail-item {
  width: 60px;
  height: 60px;
  border: 2px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.thumbnail-item:hover {
  border-color: #409EFF;
  transform: scale(1.05);
}

.thumbnail-item.active {
  border-color: #409EFF;
  border-width: 3px;
}

.thumbnail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .preview-controls {
    flex-direction: column;
    gap: 10px;
  }

  .thumbnail-item {
    width: 50px;
    height: 50px;
  }
}
/* 下载按钮加载状态样式 */
.el-button [class*="el-icon-loading"] {
  margin-right: 5px;
}

/* 文件项悬停效果 */
.readonly-file-item:hover {
  background-color: #f0f9ff;
  border-color: #c6e2ff;
}

.image-preview-item:hover {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}

/* 活动图片展示样式 */
.activity-image-container {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.activity-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  }
}

/* 图片预览对话框样式 */
.image-preview-dialog {
  .el-dialog {
    border-radius: 12px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  }

  .el-dialog__header {
    background: linear-gradient(to right, #409EFF, #64b5ff);
    color: white;
    border-radius: 12px 12px 0 0;
    padding: 20px 24px;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__close {
      color: white;
      font-size: 20px;

      &:hover {
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }

  .el-dialog__body {
    padding: 0;
    background: #f8f9fa;
  }
}

.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 0 0 12px 12px;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}


</style>
