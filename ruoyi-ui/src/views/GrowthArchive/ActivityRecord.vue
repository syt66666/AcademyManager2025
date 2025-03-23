<template>
  <div class="container">
    <div class="main-container">
      <!-- 顶部标题栏 -->
      <div class="nav">
        <div class="nav-content">
          <h2>
            <span class="activity-icon">🎨</span>
            文体活动记录
            <span class="current-semester">{{ activeSemester }} 活动成果</span>
          </h2>
          <el-button
            type="primary"
            class="add-button"
            @click="openDialog"
            icon="el-icon-plus"
          >新增记录
          </el-button>
        </div>
      </div>

      <!-- 活动表格 -->
      <div class="activity-table-card">
        <el-table
          :data="activityRecords"
          style="width: 100%"
          class="optimized-table"
          :header-cell-style="headerStyle"
          v-loading="loading"
          :row-class-name="tableRowClassName"
        >
          <!-- 序号列 -->
          <el-table-column label="序号" width="80" align="center">
            <template v-slot="scope">
              <span class="index-badge">
                {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
              </span>
            </template>
          </el-table-column>

          <!-- 活动名称 -->
          <el-table-column prop="activityName" label="活动名称" min-width="120">
            <template v-slot="scope">
              <div class="activity-name">
                <i class="el-icon-star-on name-icon"></i>
                <span class="name-text">{{ scope.row.activityName }}</span>
              </div>
            </template>
          </el-table-column>

          <!-- 活动级别 -->
          <el-table-column prop="activityLevel" label="活动级别" width="120" align="center">
            <template v-slot="scope">
              <el-tag
                :type="getLevelTagType(scope.row.activityLevel)"
                effect="light"
                class="level-tag"
              >
                {{ scope.row.activityLevel }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 获奖等级 -->
          <el-table-column prop="awardLevel" label="获奖等级" width="120" align="center">
            <template v-slot="scope">
              <el-tag
                :type="getAwardTagType(scope.row.awardLevel)"
                effect="light"
                class="award-tag"
              >
                {{ scope.row.awardLevel }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 获奖日期 -->
          <el-table-column prop="awardDate" label="获奖日期" width="120" align="center">
            <template v-slot="scope">
              <span class="time-display">
                {{ formatDate(scope.row.awardDate) }}
              </span>
            </template>
          </el-table-column>

          <!-- 证明材料 -->
          <el-table-column label="证明材料" width="140" align="center">
            <template v-slot="scope">
              <el-dropdown trigger="click" @command="handleFileCommand" @click.native.stop :disabled="!scope.row.proofMaterial || scope.row.proofMaterial.length === 0">
                <el-button type="primary" size="mini" plain @click.stop :disabled="!scope.row.proofMaterial || scope.row.proofMaterial.length === 0">
                  <i class="el-icon-document"></i> 图片操作
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

          <!-- 审核状态 -->
          <el-table-column prop="auditStatus" label="审核状态" width="120" align="center">
            <template v-slot="scope">
              <el-tag
                :type="getStatusTagType(scope.row.auditStatus)"
                effect="light"
                class="status-tag"
              >
                <i :class="getStatusIcon(scope.row.auditStatus)"></i>
                {{ scope.row.auditStatus }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 操作列 -->
          <el-table-column label="操作" width="100" align="center">
            <template v-slot="scope">
              <template v-if="scope.row.auditStatus === '未通过'">
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="handleEditDraft(scope.row)"
                >重新提交
                </el-button>
              </template>

              <template v-if="scope.row.auditStatus === '未提交'">
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="handleEditDraft(scope.row)"
                >编辑
                </el-button>
                <el-button
                  type="text"
                  size="mini"
                  style="color: #F56C6C;"
                  @click.stop="handleDelete(scope.row)"
                >删除
                </el-button>
              </template>

              <el-tag
                v-if="['未审核', '已通过'].includes(scope.row.auditStatus)"
                type="info"
                size="mini"
                class="no-edit-tag"
              >不可修改
              </el-tag>
            </template>
          </el-table-column>

          <!-- 审核时间 -->
          <el-table-column prop="auditTime" label="审核时间" width="140" align="center">
            <template v-slot="scope">
              <span class="time-display">
                {{ formatDateTime(scope.row.auditTime) }}
              </span>
            </template>
          </el-table-column>

          <!-- 审核意见 -->
          <el-table-column prop="auditRemark" label="审核意见" min-width="160" align="center">
            <template v-slot="scope">
              <div class="remark-text">
                {{ scope.row.auditRemark || '-' }}
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <pagination
          v-show="totalRecords>0"
          :total="totalRecords"
          :page.sync="currentPage"
          :limit.sync="pageSize"
          @pagination="fetchActivityRecords"
          class="custom-pagination"
        />
      </div>
    </div>

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

    <!-- 活动新增对话框 -->
    <el-dialog
      :visible.sync="showDialog"
      title="活动信息填报"
      width="580px"
      class="activity-dialog"
      @close="closeDialog"
    >
      <div class="dialog-header">
        <h3 class="form-title">{{ isEdit ? '编辑活动记录' : '新增活动记录' }}</h3>
        <p class="form-tips">请填写本学期参与的活动信息（带<span class="required">*</span>为必填项）</p>
      </div>

      <el-form
        ref="form"
        :model="formData"
        :rules="rules"
        label-width="110px"
        class="activity-form"
      >
        <!-- 活动名称 -->
        <el-form-item label="活动名称" prop="activityName">
          <el-input
            v-model="formData.activityName"
            placeholder="请输入完整活动名称"
            class="custom-input"
          >
            <i slot="prefix" class="el-icon-star-on name-icon"></i>
          </el-input>
        </el-form-item>

        <!-- 活动级别 -->
        <el-form-item label="活动级别" prop="activityLevel">
          <el-select
            v-model="formData.activityLevel"
            placeholder="请选择级别"
            class="custom-select"
          >
            <el-option
              v-for="item in levelOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span class="option-icon">{{ levelIcons[item.value] }}</span>
              {{ item.label }}
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 获奖等级 -->
        <el-form-item label="获奖等级" prop="awardLevel">
          <el-select
            v-model="formData.awardLevel"
            placeholder="请选择奖项"
            class="custom-select"
          >
            <el-option
              v-for="item in awardOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span class="option-icon">{{ awardIcons[item.value] }}</span>
              {{ item.label }}
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 获奖日期 -->
        <el-form-item label="获奖日期" prop="awardDate">
          <el-date-picker
            v-model="formData.awardDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="选择日期"
            class="custom-date"
            :picker-options="pickerOptions"
          >
            <i slot="suffix" class="el-icon-date date-icon"></i>
          </el-date-picker>
        </el-form-item>

        <!-- 证明材料 -->
        <el-form-item label="证明材料" prop="proofMaterial">
          <el-upload
            multiple
            :limit="5"
            :file-list="fileList"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :on-preview="handlePreviewFile"
            list-type="picture-card"
            class="custom-upload"
          >
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">支持格式：JPG/PNG 单文件≤10MB 最多5个文件</div>
          </el-upload>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item class="form-actions">
          <el-button
            type="info"
            class="save-btn"
            @click="handleSave"
          >
            <i class="el-icon-document"></i> 保存草稿
          </el-button>
          <el-button
            type="primary"
            class="submit-btn"
            @click="handleSubmit"
          >
            <i class="el-icon-check"></i> 正式提交
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>

</template>

<script>
import {listActivity, addActivity, updateActivity, delActivity, checkActivityUnique} from "@/api/system/activity";
import ImageUpload from '@/components/ImageUpload';
import axios from "axios";
import store from "@/store";

export default {
  mounted() {
    // 获取学期参数
    this.activeSemester = this.$route.query.semester;
    //得到列表数据
    this.fetchActivityRecords();
  },
  components: {
    ImageUpload
  },
  data() {
    return {
      allowedImageTypes: ['image/jpg', 'image/png','image/jpeg'], // 允许的文件类型
      maxImageSize: 5 * 1024 * 1024, // 5MB限制
      levelOptions: [
        {value: '院级', label: '院级'},
        {value: '校级', label: '校级'},
        {value: '省级', label: '省级'},
        {value: '国家级', label: '国家级'},
        {value: '国际级', label: '国际级'}
      ],
      awardOptions: [
        {value: '特等奖', label: '特等奖'},
        {value: '一等奖', label: '一等奖'},
        {value: '二等奖', label: '二等奖'},
        {value: '三等奖', label: '三等奖'},
        {value: '优秀奖', label: '优秀奖'}
      ],
      levelIcons: {
        '院级': '🏛️',
        '校级': '🏫',
        '省级': '🌉',
        '国家级': '🇨🇳',
        '国际级': '🌍'
      },
      awardIcons: {
        '特等奖': '🏆',
        '一等奖': '🥇',
        '二等奖': '🥈',
        '三等奖': '🥉',
        '优秀奖': '🎖️'
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        }
      },
      fileList: [], // 已上传的文件列表
      previewVisible: false,
      currentDownloadFile: '',
      previewImages: [],
      currentPreviewIndex: 0,
      isEdit: false,
      currentActivityId: null,
      baseUrl: process.env.VUE_APP_BASE_API,
      activeSemester: "",
      dialogVisible: false,
      currentImage: '',
      activityRecords: [],
      queryParams: {},
      currentPage: 1,
      pageSize: 10,
      totalRecords: 0,
      proofMaterial: [],
      showDialog: false,
      isLoading: false,
      formData: {
        activityName: '',
        activityLevel: '',
        awardLevel: '',
        proofMaterial: '',
        semester: '',
        awardDate: '',
        auditStatus: '未提交',
        auditTime: null,
        auditRemark: '',
      },
      rules: {
        activityName: [
          {required: true, message: '活动名称不能为空', trigger: 'blur'}
        ],
        activityLevel: [
          {required: true, message: '请选择活动级别', trigger: 'change'}
        ],
        awardLevel: [
          {required: true, message: '请选择奖项', trigger: 'change'}
        ],
        awardDate: [
          {required: true, message: '请选择获奖日期', trigger: 'change'}
        ]
      }
    };
  },

  methods: {
    headerStyle() {
      return {
        backgroundColor: '#EBF4FF',
        color: '#2B6CB0',
        fontWeight: "600",
      };
    },

    // 新增表格相关方法
    getLevelTagType(level) {
      const typeMap = {
        '院级': 'info',
        '校级': 'primary',
        '省级': 'success',
        '国家级': 'warning',
        '国际级': 'danger'
      }
      return typeMap[level] || 'info'
    },

    getAwardTagType(award) {
      const typeMap = {
        '特等奖': 'danger',
        '一等奖': 'warning',
        '二等奖': 'success',
        '三等奖': 'primary',
        '优秀奖': 'info'
      }
      return typeMap[award] || ''
    },

    getStatusTagType(status) {
      const typeMap = {
        '已通过': 'success',
        '未审核': 'warning',
        '未通过': 'danger',
        '未提交': 'info'
      }
      return typeMap[status] || 'info'
    },

    getStatusIcon(status) {
      const iconMap = {
        '已通过': 'el-icon-circle-check',
        '未审核': 'el-icon-time',
        '未通过': 'el-icon-circle-close',
        '未提交': 'el-icon-edit'
      }
      return iconMap[status] || 'el-icon-question'
    },

    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
    },

    formatDateTime(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return `${this.formatDate(dateString)} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },

    handleFileCommand(command) {
      if (command.action === 'preview') {
        this.handlePreview(command.files)
      } else if (command.action === 'download') {
        this.downloadFiles(command.files)
      }
    },

    tableRowClassName({rowIndex}) {
      return rowIndex % 2 === 1 ? 'stripe-row' : ''
    },


    // 文件预览处理
    handlePreviewFile(file) {
      if (file.isOld) {
        // 旧文件直接使用存储的URL
        window.open(file.url);
      } else {
        // 新上传文件使用本地预览
        const reader = new FileReader();
        reader.onload = (e) => {
          window.open(e.target.result);
        };
        reader.readAsDataURL(file.raw);
      }
    },
    handleFileRemove(file, fileList) {
      this.fileList = fileList;
    },
    handleFileChange(file, fileList) {
      // 额外参数用于显示错误提示
      const done = (condition, message) => {
        if (!condition) {
          this.$message.error(message)
          // 移除非法的最后一个文件
          const newFiles = fileList.slice(0, fileList.length - 1)
          this.fileList = newFiles.slice(-5)
          return false
        }
        // 保留合法文件并限制最多5个
        this.fileList = fileList.slice(-5)
        return true
      }
      console.log("file.raw.type:", file.raw.type)
      // 类型验证
      const isValidType = this.allowedImageTypes.includes(file.raw.type)
      if (!isValidType) {
        return done(
          false,
          `不支持 ${file.name} 的文件类型，请上传 PNG/JPG 格式的图片`
        )
      }

      // 大小验证
      const isValidSize = file.size <= this.maxImageSize
      if (!isValidSize) {
        return done(false, `文件 ${file.name} 超过5MB大小限制`)
      }

      // 扩展名二次验证（防止伪装扩展名）
      const fileExt = file.name.split('.').pop().toLowerCase()
      const isValidExt = ['jpg', 'png'].includes(fileExt)
      if (!isValidExt) {
        return done(false, `文件 ${file.name} 的扩展名不合法`)
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
        if (paths.length >= 1) {
          this.$confirm(`本次下载包含${paths.length}个文件，是否继续？`, '批量下载提示', {
            confirmButtonText: '立即下载',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            paths.forEach(path => {
              const url = this.getFullUrl(path); // 使用 getFullUrl 方法生成完整 URL
              this.downloadSingleFile(url);
            });
          });
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
    // 新增获取完整URL的方法
    getFullUrl(filePath) {
      return `${process.env.VUE_APP_BASE_API}/profile/${filePath}`;
    },


    // 关闭对话框
    closeDialog() {
      this.showDialog = false;
      this.fileList = []; // 清空已上传的文件列表
    },
    // 删除处理方法
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该草稿记录吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        const response = await delActivity(row.activityId); // 根据实际ID字段调整
        if (response.code === 200) {
          this.$message.success('删除成功');
          // 删除后重新加载数据
          await this.fetchActivityRecords();
          // 清理本地草稿
          if (!row.id) { // 假设id存在表示已保存到服务端
            localStorage.removeItem(this.getDraftKey());
          }
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`删除失败: ${error.message || '服务器错误'}`);
        }
      }
    },
    // 获取完整图片路径
    getImageUrl(path) {
      if (!path) return '';
      // 处理两种路径情况：
      // 1. 完整路径直接返回
      // 2. 相对路径拼接基础URL
      return path.startsWith('http')
        ? `${path}?t=${Date.now()}`  // 添加时间戳防止缓存
        : `${this.baseUrl}${path}?t=${Date.now()}`;
    },

    handleImageClick(imageUrl) {
      this.currentImage = imageUrl;
      this.dialogVisible = true;
    },

    // 下载图片
    handleDownload() {
      if (!this.currentImage) {
        this.$message.warning('没有可下载的图片');
        return;
      }

      const link = document.createElement('a');
      link.href = this.getImageUrl(this.currentImage);
      link.download = this.generateFileName();
      link.style.display = 'none';

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
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

    // 数据获取方法
    async fetchActivityRecords() {
      this.isLoading = true;
      try {
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          studentId: this.$store.state.user.name,
          semester: this.activeSemester,
          ...this.queryParams
        };

        const response = await listActivity(params);
        if (response.code === 200) {
          // 处理证明材料路径
          this.activityRecords = response.rows.map(item => ({
            ...item,
            id: item.activityId,
            auditStatus: item.auditStatus,
            proofMaterial: this.parseMaterial(item.proofMaterial)
          }));
          this.totalRecords = response.total;
        }
      } catch (error) {
        console.error("获取活动记录失败:", error);
      } finally {
        this.isLoading = false;
      }
    },

    // 分页处理
    handleSizeChange(size) {
      this.pageSize = size;
      this.fetchActivityRecords();
    },

    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchActivityRecords();
    },
    initFormData() {
      return {
        activityName: '',
        activityLevel: '',
        awardLevel: '',
        proofMaterial: '',
        semester: this.activeSemester,
        awardDate: '',
        auditTime: null,
        auditRemark: '',
        auditStatus: '未提交'
      };
    },
    // 对话框控制
    openDialog() {
      this.isEdit = false;
      this.currentActivityId = null;
      this.formData = this.initFormData(); // 使用初始化方法
      this.showDialog = true;
    },

// 修改后的parseMaterial方法
    parseMaterial(material) {
      try {
        // 情况1：已经是数组直接返回
        if (Array.isArray(material)) {
          console.log('[DEBUG] 已解析为数组:', material);
          return [...material]; // 解除响应式绑定
        }

        // 情况2：字符串类型尝试解析
        if (typeof material === 'string') {
          // 处理Vue响应式对象字符串的特殊情况
          const cleaned = material.replace(/__ob__:.*?($$|$$)/gs, '');
          console.log('[DEBUG] 已解析为字符串:', cleaned);
          return JSON.parse(cleaned);
        }

        // 情况3：其他类型返回空数组
        return [];
      } catch (e) {
        console.error('材料解析失败:', {
          input: material,
          error: e.stack
        });
        return [];
      }
    },
    handleEdit(row) {
      // 创建深拷贝避免响应式数据问题
      const rawData = JSON.parse(JSON.stringify(row));
      this.formData = {...rawData};
      this.isEdit = true;
      this.currentActivityId = rawData.activityId;
      this.showDialog = true;

      // 解析文件路径
      const proofMaterial = this.parseMaterial(rawData.proofMaterial);

      // 生成符合el-upload要求的文件列表
      this.fileList = proofMaterial.map((path, index) => {
        // 路径有效性验证
        if (!path || typeof path !== 'string') {
          console.warn(`无效文件路径[${index}]:`, path);
          return null;
        }

        // 生成完整访问URL
        const fullUrl = `${process.env.VUE_APP_BASE_API}/profile/${encodeURIComponent(path)}`;

        return {
          uid: Date.now() + index, // 唯一标识
          name: path.split('/').pop(),
          url: fullUrl,
          status: 'success',
          isOld: true,
          path: path
        };
      }).filter(Boolean);

      console.log('[DEBUG] 生成的文件列表:', this.fileList);
    },

    // 处理草稿修改
    handleEditDraft(row) {
      this.handleEdit(row);
      localStorage.removeItem(this.getDraftKey());
    },
    // 保存草稿
    async handleSave() {
      await this.submitData('未提交');
    },

    // 正式提交
    async handleSubmit() {
      await this.submitData('未审核');
    },

    // 统一提交方法
    async submitData(status) {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          // 获取原始记录数据（编辑时）
          const originalRecord = this.activityRecords.find(
            item => item.id === this.currentActivityId
          );
          console.log("this.currentActivityId:" + this.currentActivityId)
          // 检测关键字段是否修改
          const isKeyFieldChanged = !originalRecord ||
            this.formData.activityName !== originalRecord.activityName ||
            this.formData.activityLevel !== originalRecord.activityLevel ||
            this.formData.awardLevel !== originalRecord.awardLevel;
          console.log("isKeyFieldChanged:" + isKeyFieldChanged)
          console.log("this.currentActivityId:" + this.currentActivityId)
          const shouldCheckUnique = !this.currentActivityId || isKeyFieldChanged;

          // 编辑时排除自身
          if (shouldCheckUnique) {
            // 唯一性校验参数
            const checkParams = {
              studentId: this.$store.state.user.name,
              activityName: this.formData.activityName,
              activityLevel: this.formData.activityLevel,
              awardLevel: this.formData.awardLevel,
              semester: this.activeSemester
            };
            // 执行唯一性校验
            const checkRes = await checkActivityUnique(checkParams);
            if (checkRes.code !== 200) {
              return this.$message.error('已存在相同活动记录，不可重复添加');
            }
          }

          // 获取保留的旧文件路径
          const existingPaths = this.fileList
            .filter(file => file.isOld)
            .map(file => file.path);

          // 获取新上传的文件
          const newFiles = this.fileList
            .filter(file => !file.isOld)
            .map(file => file.raw);

          const formData = new FormData();
          // 构建核心数据对象
          const recordData = {
            activityId: null,
            activityName: this.formData.activityName,
            activityLevel: this.formData.activityLevel,
            awardLevel: this.formData.awardLevel,
            semester: this.activeSemester,
            studentId: store.state.user.name,
            auditStatus: status,
            auditTime: null,
            auditRemark: '',
            awardDate: this.formData.awardDate,
            existingProofMaterial: existingPaths, // 旧文件路径
          };

          // 如果是编辑操作，添加ID字段
          if (this.currentActivityId) {
            recordData.activityId = this.currentActivityId;
          }
          // 构建 JSON 部分（指定类型为 application/json）
          const recordBlob = new Blob(
            [JSON.stringify(recordData)],
            {type: "application/json"}
          );
          formData.append("stuActivityRecord", recordBlob);

          // 添加文件
          this.fileList.forEach((file) => {
            formData.append("proofMaterial", file.raw);
          });

          // 配置headers
          const config = {
            headers: {
              "Authorization": "Bearer " + localStorage.getItem("token"),
              "Content-Type": "multipart/form-data"
            }
          };
          // 根据模式选择API方法
          const apiMethod = this.currentActivityId ? updateActivity : addActivity;
          apiMethod(formData, config)
            .then(() => {
              this.$message.success(this.currentActivityId ? "更新成功！" : "提交成功！");
              this.fetchActivityRecords();
              this.closeDialog();
            })
            .catch(error => {
              this.$message.error(`操作失败：${error.message}`);
            });
        }
      });
    },
    // 获取本地存储的key
    getDraftKey() {
      return `activity_draft_${this.$store.state.user.name}_${this.activeSemester}`;
    },
  }
};
</script>

<style scoped>
/* ================= 全局容器样式 ================= */
.container {
  width: 100%;
  margin: 0 auto;
  padding: 2rem;
  background: linear-gradient(160deg, #EBF4FF 0%, #EBF8FF 100%);
  min-height: 100vh;
  display: flex;
  justify-content: center; /* 水平居中 */
}

.main-container {
  background: #ffffff;
  border-radius: 1.5rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  position: relative;
  overflow: hidden;
  width: 100%;
  max-width: 1400px; /* 添加最大宽度限制 */
  margin: 0 auto;     /* 自动水平居中 */
}

/* ================= 导航栏样式 ================= */
.nav {
  background: linear-gradient(135deg, #2B6CB0 0%, #4299E1 100%);
  border-radius: 1rem;
  margin: -2rem -2rem 2rem;
  position: relative;
  overflow: hidden;
}

.nav::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg,
  rgba(255, 255, 255, 0.1) 25%,
  transparent 50%,
  rgba(255, 255, 255, 0.1) 75%
  );
  opacity: 0.3;
}

.nav-content {
  padding: 1.5rem 2rem;
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav h2 {
  color: white;
  font-size: 1.8rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 0;
}

.activity-icon {
  font-size: 1.5em;
  margin-right: 0.5rem;
}

.current-semester {
  font-size: 1.2rem;
  opacity: 0.9;
}
/* ================= 表格相关样式 ================= */
.activity-table-card {
  background: #fff;
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  margin-top: 1.5rem;
}

/* 表格基础样式 */
.optimized-table {
  --table-header-bg: #f8fafc;
  --table-hover-bg: #f7fafc;
  --table-stripe-bg: #f8fafc;
  --table-border-color: #e2e8f0;
  --table-text-primary: #2d3748;
  border-radius: 8px;
  overflow: hidden;
}

/* 表头样式 */
.optimized-table /deep/ .el-table__header th {
  background: var(--table-header-bg) !important;
  color: #2b6cb0;
  font-weight: 600;
  font-size: 0.95rem;
}

/* 表格行样式 */
.optimized-table /deep/ .el-table__body td {
  color: var(--table-text-primary);
  transition: background 0.2s;
  border-color: var(--table-border-color);
}

.optimized-table /deep/ .el-table__body tr:hover td {
  background: var(--table-hover-bg) !important;
  cursor: pointer;
}

.optimized-table /deep/ .stripe-row td {
  background-color: var(--table-stripe-bg);
}

/* 表格元素样式 */
.index-badge {
  display: inline-flex;
  width: 28px;
  height: 28px;
  background: #ebf4ff;
  border-radius: 50%;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  color: #2b6cb0;
}

.activity-name {
  display: flex;
  align-items: center;
  padding: 8px 0;
  gap: 8px;
}

/* 核心样式 */
.name-icon {
  /* 尺寸放大 */
  font-size: 16px !important;  /* 原始尺寸放大50% */
  transform: scale(1);         /* 移除缩放补偿 */

  /* 高级渐变色 */
  background: linear-gradient(45deg, #FFD700 20%, #FFA500 80%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent !important;

  /* 光影细节 */
  filter:
    drop-shadow(0 1px 1px rgba(255,215,0,0.15))
    drop-shadow(0 0 2px rgba(255,255,255,0.6));

  /* 动画优化 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 悬浮特效 */
.name-icon:hover {
  filter:
    drop-shadow(0 2px 3px rgba(255,229,92,0.25))
    drop-shadow(0 0 3px rgba(255,255,255,0.8));
  transform: scale(1.1);
}

/* 动态边框 (匹配星星形状) */
.name-icon::after {
  content: "";
  position: absolute;
  left: -3px;
  top: -3px;
  width: calc(100% + 6px);
  height: calc(100% + 6px);
  border: 1px solid rgba(255,255,255,0.4);
  clip-path: polygon(
    50% 0%,
    61% 35%,
    98% 35%,
    68% 57%,
    79% 91%,
    50% 70%,
    21% 91%,
    32% 57%,
    2% 35%,
    39% 35%
  ); /* 五角星剪裁路径 */
}
/* 标签统一样式 */
.level-tag,
.award-tag,
.status-tag {
  border-radius: 12px;
  padding: 0 10px;
  font-weight: 500;
}

/* ================= 对话框样式 ================= */
.activity-dialog {
  border-radius: 12px;
}

.activity-dialog /deep/ .el-dialog__header {
  display: none; /* 隐藏原生标题 */
}

.dialog-header {
  text-align: center;
  padding: 20px 0 15px;
  background: linear-gradient(135deg, #2B6CB0 0%, #4299E1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 表单元素样式 */
.activity-form {
  padding: 0 30px 20px;
}

/* 操作按钮样式 */
.form-actions {
  margin-top: 25px;
  text-align: center;
}

/* ================= 分页样式 ================= */
.custom-pagination {
  display: flex;
  justify-content: center !important; /* 强制居中 */
  margin: 20px auto 0;
  padding: 12px 0;
  width: 100%;
}

/* 调整分页组件内部布局 */
.custom-pagination /deep/ .el-pagination {
  display: inline-flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}
/* 悬停效果 */
.custom-pagination /deep/ .el-pager li:hover {
  border-color: #4299e1;
  color: #4299e1;
}


/* 响应式调整 */
@media (max-width: 768px) {
  .custom-pagination {
    padding: 8px;
    justify-content: center;
  }
}

/* ================= 响应式设计 ================= */
@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .nav h2 {
    font-size: 1.4rem;
  }
}

</style>
