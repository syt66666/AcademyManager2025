<template>
  <div class="app-container">
    <!-- 统计大盒子 -->
    <div class="stats-card">
      <div class="card-header">
        <i class="el-icon-data-analysis"></i>
        <span>选课状态筛选</span>
      </div>

      <div class="stats-content">
        <!-- 状态筛选 -->
        <div class="status-stats">

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
              :class="{ active: selectedStatus === '未考核' }"
              @click="filterByStatus('未考核')"
            >
              <div class="status-icon pending">
                <i class="el-icon-time"></i>
              </div>
              <div class="status-info">
                <div class="status-count">{{ statusCounts.pending }}</div>
                <div class="status-label">未考核</div>
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
                <div class="status-count">{{ totalAll }}</div>
                <div class="status-label">全部</div>
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
        <span>我的选课记录</span>
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>
      <el-table
        v-loading="loading"
        :data="coursesList"
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
        <el-table-column label="课程名称" align="center" prop="courseName" />
        <!-- 类型列 -->
        <el-table-column label="课程性质" align="center" width="120">
          <template slot-scope="scope">
            <el-tag :type="getCourseCategoryTagType(scope.row.courseCategory)" effect="plain" class="category-tag">
              {{ getCourseCategoryName(scope.row.courseCategory) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="课程类型" align="center" prop="courseType" width="200">
          <template slot-scope="scope">
            <el-tag :type="getCourseTypeTagType(scope.row.courseType)" effect="plain" class="course-type-tag">
              {{ getCourseTypeName(scope.row.courseType) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 学分列 -->
        <el-table-column label="学分" align="center" width="80">
          <template slot-scope="scope">
            <span class="credit-value">{{ scope.row.courseCredit || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="课程地点" align="center" prop="courseLocation" />
        <el-table-column label="组织单位" align="center" prop="organizer" />
        <el-table-column label="课程成绩" align="center" prop="scoreValue" width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.scoreValue" class="score-text">{{ scope.row.scoreValue }}</span>
            <span v-else class="no-score">-</span>
          </template>
        </el-table-column>
        <el-table-column label="课程开始时间" align="center" prop="startTime" >
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="课程结束时间" align="center" prop="endTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>



        <el-table-column label="材料提交" align="center" width="120">
          <template slot-scope="scope">
            <!-- 课程未结束时显示提示信息 -->
            <el-tag
              v-if="!isCourseEnded(scope.row.endTime)"
              type="info"
              effect="light"
              class="status-tag"
            >课程未结束</el-tag>
            <!-- 课程结束后显示上传按钮或状态标签 -->
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
              >
                重新提交
              </el-tag>
              <!-- 未考核状态 - 添加点击功能 -->
              <el-tag
                v-if="scope.row.status === '未考核' || scope.row.status === '未审核'"
                type="warning"
                effect="light"
                class="clickable-tag"
                @click="openUploadDialog(scope.row)"
              >
                未考核
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
                <div class="expand-label"><i class="el-icon-document"></i> 课程描述:</div>
                <div class="expand-content">{{ props.row.courseDescription || '暂无描述' }}</div>
              </div>
              <div class="expand-row" v-if="!isCourseEnded(props.row.endTime)">
                <div class="expand-label"><i class="el-icon-time"></i> 材料提交提示:</div>
                <div class="expand-content upload-tip">
                  <i class="el-icon-info"></i>
                  课程尚未结束，请等待课程结束后再提交证明材料。课程结束时间：{{ parseTime(props.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
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
      width="40%"
      :close-on-click-modal="false"
    >
      <!-- 考核意见显示区域 -->
      <div v-if="currentBooking && currentBooking.status === '未通过' && currentBooking.reviewComment" class="audit-feedback-section">
        <h3><i class="el-icon-warning-outline"></i> 考核意见</h3>
        <div class="audit-feedback-content">
          <div class="audit-feedback-text">{{ currentBooking.reviewComment }}</div>
          <div class="audit-feedback-meta">
            <span class="audit-time">
              <i class="el-icon-time"></i>
              考核时间：{{ parseTime(currentBooking.reviewTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
            </span>
            <span v-if="currentBooking.reviewer" class="audit-reviewer">
              <i class="el-icon-user"></i>
              考核人：{{ currentBooking.reviewer }}
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

      <!-- 压缩包上传区域 - 只在非只读模式下显示 -->
      <div v-if="!isViewOnly" class="section compact-section">
        <h3>上传证明材料压缩包</h3>
        <div class="zip-upload-container">
          <el-upload
            :action="uploadUrl"
            :file-list="zipFiles"
            :on-remove="handleRemoveZip"
            :on-success="handleZipSuccess"
            :on-progress="handleZipProgress"
            :limit="1"
            accept=".zip,.rar,.7z"
            :before-upload="beforeZipUpload"
            :headers="headers"
            :data="{ filePath: 'courseBookings' }"
            :auto-upload="true"
            drag
            class="zip-upload"
            :show-file-list="false"
          >
            <div class="upload-drag-area">
              <i class="el-icon-upload upload-icon"></i>
              <div class="upload-text">
                <p class="upload-main-text">将压缩包文件拖拽到此处，或</p>
                <p class="upload-sub-text">点击选择文件</p>
              </div>
            </div>
            <div slot="tip" class="upload-tip">
              <i class="el-icon-info"></i>
              仅支持上传ZIP、RAR、7Z格式压缩包，大小不超过50MB
            </div>
          </el-upload>

          <!-- 自定义文件列表显示 -->
          <div v-if="zipFiles && zipFiles.length > 0" class="custom-file-list">
            <div v-for="(file, index) in zipFiles" :key="index" class="custom-file-item">
              <i class="el-icon-folder file-icon"></i>
              <span class="file-name">{{ getFileNameOnly(file.name) }}</span>
              <div class="file-progress" v-if="file.uploading">
                <el-progress
                  :percentage="file.progress"
                  :status="file.progress === 100 ? 'success' : ''"
                  :stroke-width="6"
                  :show-text="true"
                ></el-progress>
              </div>
              <el-button
                type="text"
                icon="el-icon-delete"
                class="remove-btn"
                @click="handleRemoveZip(file)"
                :disabled="file.uploading"
              ></el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 压缩包展示区域 - 只在只读模式下显示 -->
      <div v-if="isViewOnly" class="section">
        <h3>证明材料压缩包</h3>
        <div v-if="zipFiles && zipFiles.length" class="zip-card">
          <div class="zip-left">
            <i class="el-icon-folder file-icon"></i>
            <div class="file-meta">
              <div class="file-name" :title="zipFiles[0].name">{{ zipFiles[0].name }}</div>
              <el-tag size="mini" type="info">{{ getFileType(zipFiles[0].name).toUpperCase() }}</el-tag>
            </div>
          </div>
          <div class="zip-actions">
            <el-button size="mini" icon="el-icon-download" type="primary" @click="downloadFileReliably(zipFiles[0])" :loading="zipFiles[0].downloading">下载</el-button>
          </div>
        </div>
        <div v-else class="empty-tip">暂无压缩包材料</div>
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
import { listBookingsWithCourse, updateBookings, getBookings } from "@/api/system/courseBookings";
import { getToken } from "@/utils/auth";

export default {
  name: "CourseParticipate",
  data() {
    return {
      // 上传相关数据
      uploadVisible: false,
      uploadTitle: "提交材料",
      currentBooking: null,

      // 上传URL和headers
      uploadUrl: process.env.VUE_APP_BASE_API + "/common/upload",
      headers: { Authorization: "Bearer " + getToken() },

      // 压缩包上传
      zipFiles: [],

      // 已有文件
      existingFiles: [],

      // 遮罩层
      loading: true,
      total: 0,
      totalAll: 0, // 所有数据的数量，不受筛选影响
      coursesList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: this.$store.state.user.name
      },

      // 统计相关数据
      selectedStatus: null, // 当前选中的状态筛选
      allCoursesList: [], // 所有选课数据，用于统计计算
      statusCounts: {
        unsubmitted: 0,  // 未提交
        rejected: 0,     // 未通过
        pending: 0,      // 未考核
        approved: 0      // 已通过
      },
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
    console.log('当前用户信息:', this.$store.state.user);
    console.log('用户ID:', this.$store.state.user.id);
    console.log('用户名:', this.$store.state.user.name);
    
    // 检查URL参数,如果有状态筛选则自动应用
    if (this.$route.query.status && this.$route.query.autoFilter === 'true') {
      const status = this.$route.query.status;
      console.log('从URL参数获取状态筛选:', status);
      this.selectedStatus = status;
      // 如果选择"未考核"或"未审核",则查询"未审核"状态
      if (status === '未考核' || status === '未审核') {
        this.queryParams.status = '未审核';
      } else {
        this.queryParams.status = status;
      }
    }
    
    this.getAllCoursesForStats();
    this.getList();
  },
  methods: {
    // 课程类型映射函数：将数字转换为对应的类型名称
    getCourseTypeName(courseType) {
      const typeMap = {
        '1': '人格塑造与价值引领课程类',
        '2': '知识融合与思维进阶课程类',
        '3': '能力锻造与实践创新课程类',
        '4': '社会责任与领军意识课程类'
      };
      return typeMap[courseType] || courseType;
    },

    // 检查课程是否已结束
    isCourseEnded(endTime) {
      if (!endTime) return false;
      const now = new Date();
      const courseEndTime = new Date(endTime);
      return now > courseEndTime;
    },

    getCourseTypeTagType(courseType) {
      const map = {
        '1': 'primary',   // 必修课程 - 蓝色
        '2': 'success',   // 选修课程 - 绿色
        '3': 'warning',   // 实践课程 - 橙色
        '4': 'danger',    // 其他课程 - 红色
        '其他': ''        // 默认蓝色
      }
      return map[courseType] || 'info';
    },

    // 获取课程分类名称
    getCourseCategoryName(courseCategory) {
      const categoryMap = {
        '必修': '必修',
        '选修': '选修',
        '实践': '实践',
        '其他': '其他'
      };
      return categoryMap[courseCategory] || courseCategory;
    },

    // 获取课程分类标签颜色
    getCourseCategoryTagType(courseCategory) {
      const map = {
        '必修': 'danger',    // 必修 - 红色
        '选修': 'success',   // 选修 - 绿色
        '实践': 'warning',   // 实践 - 橙色
        '其他': 'info'       // 其他 - 蓝色
      }
      return map[courseCategory] || 'info';
    },

    // 获取成绩来源标签颜色
    getScoreTypeTagType(scoreType) {
      const map = {
        '正考': 'success',     // 正考 - 绿色
        '补考': 'warning',     // 补考 - 橙色
        '重修': 'danger',      // 重修 - 红色
        '免修': 'info',        // 免修 - 蓝色
        '缓考': 'primary'      // 缓考 - 紫色
      }
      return map[scoreType] || 'info';
    },
    // 获取文件的完整URL（用于显示）
    getFileFullUrl(fileName) {
      if (!fileName) return '';

      // 如果已经是完整URL，直接返回
      if (fileName.startsWith('http://') || fileName.startsWith('https://') || fileName.startsWith('data:')) {
        return fileName;
      }

      const baseUrl = process.env.VUE_APP_BASE_API || '';

      // 检查文件名是否已经包含了baseUrl前缀
      if (fileName.startsWith(baseUrl)) {
        // 如果已经包含了baseUrl，直接返回
        return fileName;
      }

      // 确保路径格式正确
      let normalizedPath = fileName;
      if (!normalizedPath.startsWith('/')) {
        normalizedPath = '/' + normalizedPath;
      }

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
      this.zipFiles = [];
      // 初始化压缩包文件 - 课程预约只支持单个文件
      if (data.proof) {
        const fullUrl = this.getFileFullUrl(data.proof);
        this.zipFiles.push({
          name: this.extractFileName(data.proof),
          url: fullUrl,
          isOld: true,
          downloading: false, // 添加下载状态
          uploading: false,
          progress: 100
        });
      }
      console.log('初始化文件列表完成:', {
        zipFiles: this.zipFiles
      });
    },

    // 压缩包上传进度处理
    handleZipProgress(event, file, fileList) {
      console.log('压缩包上传进度:', event.percent);
      // 更新文件的上传进度
      const targetFile = this.zipFiles.find(f => f.uid === file.uid);
      if (targetFile) {
        this.$set(targetFile, 'progress', Math.round(event.percent));
        this.$set(targetFile, 'uploading', true);
      }
    },

    // 修复压缩包上传成功处理
    handleZipSuccess(response, file, fileList) {
      if (response.code === 200) {
        const fileName = response.fileName || this.extractFileName(response.url);

        this.zipFiles = fileList.map(f => {
          if (f.uid === file.uid) {
            return {
              name: fileName,
              url: this.getFileFullUrl(fileName),
              isOld: false,
              downloading: false, // 添加下载状态
              uploading: false,
              progress: 100
            };
          }
          return {
            ...f,
            downloading: f.downloading || false, // 确保有下载状态
            uploading: false,
            progress: 100
          };
        });
        this.$message.success('压缩包上传成功');
      }
    },

// 处理图片预览
    handlePicturePreview(file) {
      console.log('=== 开始预览图片 ===');
      console.log('预览图片:', file);

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

      console.log('预览图片数组:', this.previewImages);

      if (this.previewImages.length === 0) {
        this.$message.warning('没有可预览的图片');
        return;
      }

      // 找到当前图片的索引
      const currentUrl = this.getPreviewImageUrl(file);
      const currentIndex = this.previewImages.findIndex(img => img.url === currentUrl);

      this.currentPreviewIndex = currentIndex >= 0 ? currentIndex : 0;
      this.updatePreviewImage();

      console.log('设置预览状态:', {
        previewVisible: this.previewVisible,
        currentPreviewIndex: this.currentPreviewIndex,
        currentPreviewImage: this.currentPreviewImage,
        currentPreviewName: this.currentPreviewName
      });

      this.previewVisible = true;

      console.log('预览对话框应该已显示');
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
    async openUploadDialog(enrollment) {
      // 检查课程是否已结束
      if (!this.isCourseEnded(enrollment.endTime)) {
        this.$message.warning("课程尚未结束，无法提交材料。请等待课程结束后再提交。");
        return;
      }

      this.currentBooking = enrollment;
      // 根据状态设置不同的对话框标题、模式和按钮显示
      let title = "提交材料";
      let viewOnly = false;
      let showButtons = true;

      if (enrollment.status === '未通过') {
        title = '重新提交';
        viewOnly = false;
        showButtons = true;
      } else if (enrollment.status === '未考核') {
        title = '查看材料';
        viewOnly = true;
        showButtons = false;
      } else if (enrollment.status === '已通过') {
        title = '查看材料';
        viewOnly = true;
        showButtons = false;
      } else if (enrollment.status === '未提交') {
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
        const res = await getBookings(enrollment.bookingId);
        if (res.code === 200) {
          const data = res.data || {};
          // 更新当前选课信息，包含考核意见等详细字段
          this.currentBooking = {
            ...this.currentBooking,
            ...data
          };
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

      // 再次检查课程是否已结束（防止在对话框打开期间课程状态变化）
      if (!this.isCourseEnded(this.currentBooking.endTime)) {
        this.$message.warning("课程尚未结束，无法提交材料。请等待课程结束后再提交。");
        this.uploadVisible = false;
        return;
      }

      try {
        // 准备proof字段 - 课程预约只支持单个文件（与活动预约不同）
        let proofFileName = null;
        if (this.zipFiles.length > 0) {
          const zipFile = this.zipFiles[0]; // 只取第一个文件
          if (zipFile.isOld) {
            // 对于已有文件，从URL中提取完整路径
            proofFileName = zipFile.url.replace(process.env.VUE_APP_BASE_API, '');
          } else {
            // 对于新上传的文件，使用服务器文件名（包含时间戳）
            proofFileName = zipFile.name;
          }
        }

        // 提交到后端
        const res = await updateBookings({
          bookingId: this.currentBooking.bookingId,
          proof: proofFileName,
          status: "未考核"
        });

        if (res.code === 200) {
          this.$message.success("材料提交成功");
          this.uploadVisible = false;
          this.getList(); // 刷新列表
          this.getAllCoursesForStats(); // 刷新统计数据
        } else {
          this.$message.error("材料提交失败: " + res.msg);
        }
      } catch (error) {
        this.$message.error("材料提交失败: " + error.message);
      }
    },

    // 重置上传状态
    resetUploadState() {
      this.zipFiles = [];
    },

    // 压缩包上传前校验
    beforeZipUpload(file) {
      console.log('压缩包上传前验证:', {
        name: file.name,
        type: file.type,
        size: file.size
      });

      const isZip = ['application/zip', 'application/x-rar-compressed', 'application/x-7z-compressed'].includes(file.type) ||
        file.name.toLowerCase().endsWith('.zip') ||
        file.name.toLowerCase().endsWith('.rar') ||
        file.name.toLowerCase().endsWith('.7z');
      const isLt50M = file.size / 1024 / 1024 < 50;

      console.log('文件验证结果:', { isZip, isLt50M, sizeMB: (file.size / 1024 / 1024).toFixed(2) });

      if (!isZip) {
        this.$message.error('请上传压缩包格式文件(ZIP/RAR/7Z)!');
        return false;
      }
      if (!isLt50M) {
        this.$message.error('上传文件大小不能超过 50MB!');
        return false;
      }

      console.log('文件验证通过，开始上传');
      return true;
    },


    // 移除压缩包
    handleRemoveZip(file, fileList) {
      if (fileList) {
        // Element UI 上传组件调用时，fileList 参数存在
        this.zipFiles = [...fileList];
      } else {
        // 手动删除时，从数组中移除文件
        const index = this.zipFiles.findIndex(f => f.uid === file.uid || f.name === file.name);
        if (index > -1) {
          this.zipFiles.splice(index, 1);
        }
      }
      this.$forceUpdate(); // 强制更新视图
    },


    /** 获取所有选课数据用于统计 */
    getAllCoursesForStats() {
      const statsParams = {
        pageNum: 1,
        pageSize: 1000, // 获取足够多的数据
        studentId: this.$store.state.user.name
      };

      console.log('获取统计数据参数:', statsParams);

      listBookingsWithCourse(statsParams).then(response => {
        console.log('统计数据响应:', response);
        this.allCoursesList = response.rows || [];
        this.totalAll = this.allCoursesList.length; // 设置所有数据的数量
        // 更新统计数据
        this.updateStatistics();
      }).catch(error => {
        console.error('获取统计数据失败:', error);
        this.allCoursesList = [];
        this.totalAll = 0;
      });
    },

    /** 查询选课列表 */
    getList() {
      this.loading = true;
      console.log('查询参数:', this.queryParams);

      listBookingsWithCourse(this.queryParams).then(response => {
        console.log('查询响应:', response);
        let rows = response.rows || [];


        // 对选课列表按照材料提交状态进行排序（同优先级下按开始时间由晚到早）
        const sortedRows = this.sortCoursesByStatus(rows);
        this.coursesList = sortedRows;
        this.total = rows.length;

        this.loading = false;
      }).catch(error => {
        console.error('获取选课列表失败:', error);
        this.loading = false;
        this.$message.error('获取选课列表失败');
      });
    },


    /** 按照材料提交状态排序选课列表 */
    sortCoursesByStatus(courses) {
      // 定义状态优先级：未通过 > 课程未结束 > 未考核 > 已通过
      const statusPriority = {
        '未通过': 1,
        '未提交': 2,  // 课程未结束对应未提交状态
        '未考核': 3,
        '已通过': 4
      };

      return courses.sort((a, b) => {
        // 获取状态优先级
        const priorityA = statusPriority[a.status] || 999;
        const priorityB = statusPriority[b.status] || 999;

        // 如果优先级相同，按课程开始时间排序（最新的在前）
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
    },

    /** 计算状态统计 */
    calculateStatusCounts() {
      this.statusCounts = {
        unsubmitted: 0,
        rejected: 0,
        pending: 0,
        approved: 0
      };

      // 使用所有数据计算统计，不受筛选影响
      this.allCoursesList.forEach(course => {
        switch(course.status) {
          case '未提交':
            this.statusCounts.unsubmitted++;
            break;
          case '未通过':
            this.statusCounts.rejected++;
            break;
          case '未考核':
          case '未审核':
            this.statusCounts.pending++;
            break;
          case '已通过':
            this.statusCounts.approved++;
            break;
        }
      });
    },


    /** 根据状态筛选 */
    filterByStatus(status) {
      this.selectedStatus = status;
      this.queryParams.pageNum = 1;

      // 添加状态筛选参数
      if (status) {
        // 如果选择"未考核"，则查询"未审核"状态
        if (status === '未考核') {
          this.queryParams.status = '未审核';
        } else {
          this.queryParams.status = status;
        }
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

      console.log('环境检测:', {
        hostname: window.location.hostname,
        isProduction: isProduction,
        hasToken: !!token
      });

      if (isProduction && token) {
        // 生产环境：使用文件访问接口
        try {
          const filePath = url.replace(process.env.VUE_APP_BASE_API, '');
          const accessUrl = `${process.env.VUE_APP_BASE_API}/file/access?path=${encodeURIComponent(filePath)}&token=${token}#toolbar=0&navpanes=0&scrollbar=0`;
          console.log('使用文件访问接口:', accessUrl);
          return accessUrl;
        } catch (error) {
          console.warn('文件访问接口构建失败，回退到原始方式:', error);
        }
      }

      // 本地开发环境或回退方案：使用原始URL
      const fallbackUrl = `${url}#toolbar=0&navpanes=0&scrollbar=0`;
      console.log('使用原始URL:', fallbackUrl);
      return fallbackUrl;
    },

    // 处理PDF预览错误
    handlePdfError(event) {
      console.error('PDF预览错误:', event);
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
        console.log('安全策略限制，部分交互禁用失败');
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

        console.log('开始下载文件:', { downloadUrl, fileName });

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
          console.log('Fetch下载失败，尝试备用方法:', fetchError);
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
        console.error('文件下载失败:', error);
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

        console.log('下载文件:', { filePath, fileName, file });

        // 使用API下载文件
        const response = await downloadFile(filePath, fileName);

        if (response.code === 200) {
          this.$message.success('文件下载成功');
        } else {
          throw new Error(response.msg || '下载失败');
        }
      } catch (error) {
        console.error('文件下载失败:', error);
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
        console.error('降级下载也失败了:', fallbackError);
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
        console.error('图片下载失败:', error);
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

    /** 获取课程图片完整URL（仿照考核界面实现） */
    getCourseImageUrl(pictureUrl) {
      if (!pictureUrl) return '';

      // 如果已经是完整URL，直接返回
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        return pictureUrl;
      }

      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径（仿照考核界面）
      if (pictureUrl.startsWith('/profile/')) {
        return `${process.env.VUE_APP_BASE_API}${pictureUrl}`;
      }

      return pictureUrl;
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
  justify-content: center;
  margin-top: 20px;
}

.status-stats {
  flex: 1;
  max-width: 800px;
}

.status-stats h3 {
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

.status-icon.pending {
  background: linear-gradient(135deg, #f39c12, #e67e22);
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

/* 可点击标签样式 */
.clickable-tag {
  cursor: pointer;
  transition: all 0.3s ease;
}

.clickable-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  opacity: 0.8;
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

/* 课程类型标签 */
.course-type-tag {
  font-weight: 500;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 学分显示样式 */
.credit-value {
  font-weight: 600;
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 13px;
}

/* 课程分类标签样式 */
.category-tag {
  font-weight: 500;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  font-size: 12px;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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
    justify-content: center;
  }

  .status-items {
    justify-content: center;
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

/* 考核意见显示区域样式 */
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

/* 考核界面样式 - 与ActivityAudit.vue保持一致 */
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

/* 课程图片展示样式 */
.course-image-container {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.course-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.course-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

/* 压缩包上传样式 */
.zip-upload-container {
  margin-top: 15px;
}

.zip-upload {
  width: 100%;
}

.zip-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
  margin-top: 15px;
  transition: all 0.3s ease;
}

.zip-card:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.zip-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.zip-actions {
  display: flex;
  gap: 8px;
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
  background: #f56c6c;
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

/* 紧凑样式 */
.compact-section {
  padding: 10px;
  margin-bottom: 15px;
}

.compact-section h3 {
  margin-bottom: 10px;
  font-size: 14px;
}

.upload-drag-area {
  padding: 15px;
  min-height: 80px;
}

.upload-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.upload-main-text {
  font-size: 13px;
  margin: 0 0 4px 0;
}

.upload-sub-text {
  font-size: 11px;
  margin: 0;
}

.upload-tip {
  margin-top: 10px;
  padding: 8px 12px;
  font-size: 12px;
}

.custom-file-item {
  padding: 8px 12px;
  margin-bottom: 6px;
}

.custom-file-item .file-icon {
  font-size: 16px;
  margin-right: 8px;
}

.custom-file-item .file-name {
  font-size: 13px;
}

/* 文件上传进度样式 */
.file-progress {
  flex: 1;
  margin: 8px 0;
  padding: 0 8px;
}

.file-progress .el-progress {
  margin: 0;
}

.file-progress .el-progress__text {
  font-size: 12px;
  color: #409EFF;
  font-weight: 500;
}

/* 上传中状态的文件项样式 */
.custom-file-item:has(.file-progress) {
  background: #f0f9ff;
  border-color: #b3d8ff;
}

.custom-file-item:has(.file-progress) .file-icon {
  color: #409EFF;
}

.custom-file-item:has(.file-progress) .file-name {
  color: #409EFF;
  font-weight: 500;
}

/* 成绩显示样式 */
.score-text {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.no-score {
  color: #c0c4cc;
  font-size: 14px;
}

</style>
