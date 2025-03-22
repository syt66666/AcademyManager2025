<template>
  <div class="container">
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <div class="nav">
        <div class="nav-content">
          <h2>
            <span class="score-icon">📝</span>
            讲座报告管理
            <span class="current-semester">{{ activeSemester }} 讲座记录</span>
          </h2>
          <el-button
            type="primary"
            class="add-button"
            @click="addNewCard"
            icon="el-icon-plus"
          >新增报告
          </el-button>
        </div>
      </div>

      <!-- 表格区域 -->
      <div class="report-table-card">
        <el-table
          :data="records"
          class="optimized-table"
          :header-cell-style="headerStyle"
          highlight-current-row
        >
          <!-- 序号列 -->
          <el-table-column type="index" label="序号" width="80" align="center">
            <template v-slot="scope">
              <span class="index-badge">
                {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
              </span>
            </template>
          </el-table-column>

          <!-- 讲座题目 -->
          <el-table-column prop="reportTitle" label="讲座题目" width="120">
            <template v-slot="scope">
              <div class="lecture-name">
                <i class="el-icon-notebook-2 name-icon"></i>
                <span class="name-text">{{ scope.row.reportTitle }}</span>
              </div>
            </template>
          </el-table-column>

          <!-- 讲师姓名 -->
          <el-table-column prop="reporter" label="讲师姓名" width="120" align="center">
            <template v-slot="scope">
              <el-tag effect="light" class="reporter-tag">
                {{ scope.row.reporter }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 讲座地点 -->
          <el-table-column prop="reportLocation" label="讲座地点" width="120" align="center">
            <template v-slot="scope">
              <el-tag effect="light" class="level-tag">
                {{ scope.row.reportLocation }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 讲座链接 -->
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


          <!-- 讲座时间 -->
          <el-table-column prop="reportDate" label="讲座时间" width="120" align="center">
            <template v-slot="scope">
              <span class="time-display">
                {{ formatDate(scope.row.reportDate) }}
              </span>
            </template>
          </el-table-column>

          <!-- 总结文档 -->
          <el-table-column label="总结文档" width="120" align="center">
            <template v-slot="scope">
              <el-button
                type="primary"
                size="mini"
                @click.stop="downloadReportFeeling(scope.row)"
                class="document-btn"
                :disabled="!scope.row.reportFeeling || scope.row.reportFeeling === '[]'"
              >下载</el-button>
            </template>
          </el-table-column>

          <!-- 现场图片 -->
          <el-table-column label="现场图片" width="140" align="center">
            <template v-slot="scope">
              <el-dropdown trigger="click" @command="handleFileCommand" :disabled="!scope.row.reportPicture || scope.row.reportPicture === '[]'">
                <el-button type="primary" size="mini" plain :disabled="!scope.row.reportPicture || scope.row.reportPicture === '[]'">
                  <i class="el-icon-picture"></i> 文件操作
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    :command="{ action: 'preview', files: scope.row.reportPicture }"
                    :disabled="!scope.row.reportPicture"
                  >预览</el-dropdown-item>
                  <el-dropdown-item
                    :command="{ action: 'download', files: scope.row.reportPicture }"
                    :disabled="!scope.row.reportPicture"
                  >下载</el-dropdown-item>
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
                {{ formatAuditStatus(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120" align="center">
            <template v-slot="scope">
              <template v-if="formatAuditStatus(scope.row.auditStatus) === '未通过'">
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="handleEdit(scope.row)"
                >重新提交
                </el-button>
              </template>

              <template v-if="formatAuditStatus(scope.row.auditStatus) === '未提交'">
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="handleEdit(scope.row)"
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
                v-if="['未审核', '已通过'].includes(formatAuditStatus(scope.row.auditStatus))"
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

        <!-- 新增/编辑对话框 -->
        <el-dialog
          :visible.sync="showDialog"
          :title="isEdit ? '编辑讲座记录' : '新增讲座记录'"
          class="lecture-dialog"
          width="580px"
          @close="closeCard"
        >
          <div class="dialog-header">
            <h3 class="form-title">{{ isEdit ? '编辑讲座记录' : '新增讲座记录' }}</h3>
            <p class="form-tips">请填写本学期讲座指导信息（带<span class="required">*</span>为必填项）</p>
          </div>
          <el-form
            ref="form"
            :model="formData"
            :rules="rules"
            label-width="110px"
            class="lecture-form"
          >
            <!-- 讲座题目 -->
            <el-form-item label="讲座题目" prop="reportTitle">
              <el-input
                v-model="formData.reportTitle"
                placeholder="请输入讲座题目"
                class="lecture-input"
              >
                <i slot="prefix" class="el-icon-notebook-2 input-icon"></i>
              </el-input>
            </el-form-item>

            <!-- 讲师姓名 -->
            <el-form-item label="讲师姓名" prop="reporter">
              <el-input
                v-model="formData.reporter"
                placeholder="请输入讲师姓名"
                class="lecture-input"
              >
                <i slot="prefix" class="el-icon-s-custom input-icon"></i>
              </el-input>
            </el-form-item>

            <!-- 讲座地点 -->
            <el-form-item label="讲座地点" prop="reportLocation">
              <el-input
                v-model="formData.reportLocation"
                placeholder="请输入讲座地点"
                class="location-input"
              >
                <i slot="prefix" class="el-icon-location input-icon"></i>
              </el-input>
            </el-form-item>

            <!-- 讲座日期 -->
            <el-form-item label="讲座日期" prop="reportDate">
              <el-date-picker
                v-model="formData.reportDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="选择日期"
                class="time-picker"
                :picker-options="datePickerOptions"
              >
                <i slot="suffix" class="el-icon-date input-icon"></i>
              </el-date-picker>
            </el-form-item>

            <!-- 讲座链接 -->
            <el-form-item label="讲座链接" prop="reportLink">
              <el-input v-model="formData.reportLink" placeholder="请输入讲座链接" style="width: 100%;"></el-input>
            </el-form-item>

            <!-- 总结文档上传组件 -->
            <el-form-item label="总结文档" prop="reportFeeling">
              <el-upload
                :auto-upload="false"
                :limit="1"
                :on-change="handleSummaryChange"
                :on-remove="handleSummaryRemove"
                :file-list="summaryFileList"
                class="enhanced-upload"
              >
                <!-- 自定义上传按钮 -->
                <el-button
                  type="primary"
                  size="small"
                  class="custom-upload-btn"
                >
                  <i class="el-icon-upload"></i>
                  选择文件
                </el-button>

                <!-- 提示信息 -->
                <template #tip>
                  <div class="custom-upload-tip">
                    <i class="el-icon-info"></i>
                    支持格式：PDF/DOC/DOCX，单个文件 ≤10MB
                  </div>
                </template>

                <!-- 自定义文件列表 -->
                <template #file="{ file }">
                  <div class="custom-file-item">
                    <div class="file-icon-wrapper">
                      <i class="el-icon-document" :class="getFileIcon(file)"></i>
                    </div>
                    <div class="file-details">
                      <span class="file-name">{{ file.name }}</span>
                      <span class="file-size">{{ formatFileSize(file.size) }}</span>
                    </div>
                    <el-button
                      type="danger"
                      class="delete-btn"
                      icon="el-icon-delete"
                      circle
                      @click.stop="handleSummaryRemove(file)"
                    ></el-button>
                  </div>
                </template>
              </el-upload>
            </el-form-item>
            <!-- 现场图片 -->
            <el-form-item label="现场图片" prop="reportPicture">

              <el-upload
                multiple
                :limit="5"
                :file-list="fileList"
                :auto-upload="false"
                :on-change="handleFileChange"
                :on-remove="handleFileRemove"
                :on-preview="handlePreviewFile"
                list-type="picture-card"
                class="photo-upload"
              >
                <i class="el-icon-plus"></i>
                <div slot="tip" class="el-upload__tip">支持JPG/PNG格式，单文件≤10MB</div>
              </el-upload>
            </el-form-item>

            <!-- 操作按钮 -->
            <el-form-item class="form-actions">
              <el-button
                type="info"
                class="save-btn"
                @click="handleSave"
              >保存草稿
              </el-button>
              <el-button type="primary"
                         class="submit-btn"
                         @click="handleSubmit"
              >正式提交</el-button>
            </el-form-item>
          </el-form>
        </el-dialog>

        <!-- 分页组件 -->
        <pagination
          v-show="totalRecords > 0"
          :total="totalRecords"
          :page.sync="currentPage"
          :limit.sync="pageSize"
          @pagination="listReport"
          class="custom-pagination"
        />
      </div>

      <!-- 现场图片预览对话框 -->
      <el-dialog :visible.sync="previewVisible" title="图片预览" width="60%">
        <div style="text-align: center; margin-bottom: 20px;">
          <img
            :src="fileList[currentPreviewIndex]"
            style="max-width: 100%; display: block; margin: 0 auto;"
            alt="现场照片预览"
          />
          <el-button
            icon="el-icon-arrow-left"
            :disabled="currentPreviewIndex === 0"
            @click="currentPreviewIndex--"
          ></el-button>
          <span style="margin: 0 20px;">{{ currentPreviewIndex + 1 }} / {{ fileList.length }}</span>
          <el-button
            icon="el-icon-arrow-right"
            :disabled="currentPreviewIndex === fileList.length - 1"
            @click="currentPreviewIndex++"
          ></el-button>
        </div>

        <div slot="footer">
          <el-button
            type="primary"
            @click="downloadSingleFile(fileList[currentPreviewIndex])"
            style="background-color: #42b983; border-color: #42b983;"
          >
            <i class="el-icon-download"></i> 下载当前图片
          </el-button>
        </div>
      </el-dialog>

    </div>
  </div>
</template>

<script>
import axios from "axios";
import {addReport, listReport, updateReport, delReport, checkLectureUnique} from "@/api/student/lecture";
import store from "@/store";
import {checkCompetitionUnique} from "@/api/student/competition";

export default {
  data() {
    return {
      originalFeelingName: '', // 保存原始文件名
      reportFeelingList: [], // 总结文档上传列表
      isEdit: false,//判断修改还是插入
      previewVisible: false,
      fileList: [],
      existingFiles: [],
      currentRecordId: null,
      currentPreviewIndex: 0,
      currentDownloadFile: '',
      dialogVisible: false,
      currentLecturePoster: '',
      baseUrl: process.env.VUE_APP_BASE_API,
      records: [],// 存储后端返回的讲座报告记录数据
      queryParams: {}, // 查询条件
      currentPage: 1, // 当前页
      pageSize: 10, // 每页显示的条数
      totalRecords: 0, // 总记录数
      showDialog: false,
      newCardInfo: '',
      uploadMessage: null,
      reportFeeling: null,
      currentImage: '',
      formData: {
        reportTitle: '',
        reporter: '',
        reportDate: '',
        reportContent: '',
        reportLink: '',
        lecturePoster: '',
        reportPicture: [],
        auditStatus: '',
        reportLocation:'',
        semester: '',
      },
      activeSemester: '', // 当前学期
      datePickerOptions: {
        disabledDate(time) {
          // 禁止选择今天之后的日期
          return time.getTime() > Date.now();
        }
      },
      rules: {
        reportTitle: [{required: true, message: '讲座题目不为空', trigger: 'blur'}],
        reporter: [{required: true, message: '讲师姓名不为空', trigger: 'blur'}],
        reportLocation: [{required: true, message: '讲座地点不为空', trigger: 'blur'}],
        reportDate: [{required: true, message: '请选择讲座日期', trigger: 'change'}]
      },
    };
  },
  mounted() {
    // 获取学期数据
    this.activeSemester = this.$route.query.semester || '未知学期';
    this.formData.semester = this.findSemester(this.activeSemester);
    this.listReport();  // 在页面加载时获取数据
  },
  methods: {
    // 获取文件图标类型
    getFileIcon(file) {
      const ext = file.name.split('.').pop().toLowerCase()
      if (ext === 'pdf') return 'pdf'
      if (['doc', 'docx'].includes(ext)) return 'doc'
      return 'default'
    },

    // 格式化文件大小
    formatFileSize(bytes) {
      if (!bytes) return '0 B'
      const units = ['B', 'KB', 'MB', 'GB']
      const exp = Math.floor(Math.log(bytes) / Math.log(1024))
      return `${(bytes / Math.pow(1024, exp)).toFixed(1)} ${units[exp]}`
    },
    // 表头样式方法
    headerStyle() {
      return {
        backgroundColor: '#f8fafc',
        color: '#2b6cb0',
        fontWeight: '600'
      };
    },

    handlePreview(filePath) {
      try {
        const paths = typeof filePath === 'string'
          ? JSON.parse(filePath)
          : filePath;

        if (paths.length === 0) {
          this.$message.error('预览失败,当前没有添加过现场图片');
        }
        if (paths.length > 0) {
          this.fileList = paths.map(path => this.getFullUrl(path));
          this.currentPreviewIndex = 0;
          this.currentDownloadFile = paths[0];
          this.previewVisible = true;
        }
      } catch (error) {
        this.$message.error('预览失败：文件路径格式不正确');
      }
    },


    //现场报告多张图片下载
    async downloadReportPicture(filePaths) {
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
        link.setAttribute('download', this.generateFileName1(filePath));
        document.body.appendChild(link);
        link.click();
        URL.revokeObjectURL(url);
        link.remove();
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
      }
    },
    // 给现场报告图片生成带时间戳的文件名
    generateFileName1(filePath) {
      const originalName = filePath.split('/').pop() || '现场报告图片';
      const timestamp = new Date().getTime();
      const ext = originalName.split('.').pop() || 'jpg';
      return `${originalName.split('.')[0]}_${timestamp}.${ext}`;
    },

    // 在methods中添加以下方法
    handleFileCommand(command) {
      console.log(2415346554657);
      console.log(command.files);
      if (command.action === 'preview') {
        this.handlePreview(command.files)
      } else if (command.action === 'download') {
        this.downloadReportPicture(command.files)
      }
    },
    // // 修正后的文件处理方法
    // async handlePreview(files) {
    //   try {
    //     const filePaths = Array.isArray(files) ? files : JSON.parse(files)
    //     this.fileList = filePaths.map(path => ({
    //       url: `${process.env.VUE_APP_BASE_API}/profile/${path}`,
    //       name: path.split('/').pop()
    //     }))
    //     this.previewVisible = true
    //   } catch (error) {
    //     this.$message.error('预览失败：' + error.message)
    //   }
    // },

// 修正后的文件上传处理
    handleFileChange(file, fileList) {
      // 限制图片类型和大小
      const isImage = ['image/jpeg', 'image/png'].includes(file.raw.type)
      const isLt10M = file.raw.size / 1024 / 1024 < 10

      if (!isImage) {
        this.$message.error('只能上传JPG/PNG格式图片!')
        return false
      }

      if (!isLt10M) {
        this.$message.error('图片大小不能超过10MB!')
        return false
      }

      // 压缩大图片
      if (file.raw.size > 2 * 1024 * 1024) {
        this.compressImage(file.raw).then(compressed => {
          file.raw = compressed
          this.fileList = [...fileList.slice(-5)]
        })
      } else {
        this.fileList = [...fileList.slice(-5)]
      }
    },
    // 状态标签样式
    getStatusTagType(status) {
      const typeMap = {
        '1': 'success',
        '0': 'warning',
        '2': 'danger',
        '3': 'info'
      }
      return typeMap[status] || 'info'
    },

    // 状态图标
    getStatusIcon(status) {
      const iconMap = {
        '1': 'el-icon-circle-check',
        '0': 'el-icon-time',
        '2': 'el-icon-circle-close',
        '3': 'el-icon-edit'
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
    async handleSave() {
      this.formData.auditStatus = 3;
      this.submitForm();
    },
    async handleSubmit() {
      this.formData.auditStatus = 0;
      this.submitForm();
    },

    // 删除未提交记录
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该记录吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        const response = await delReport(row.reportId);
        if (response.code === 200) {
          this.$message.success('删除成功');
          this.initData();
          // localStorage.removeItem(this.getDraftKey());
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`删除失败: ${error.message}`);
        }
      }
    },
// 修改后的handleEdit方法
    handleEdit(row) {
      try {
        // 初始化基础表单数据
        this.formData = {
          reportId: row.reportId,
          reportTitle: row.reportTitle,
          reporter: row.reporter,
          reportDate: row.reportDate,
          reportContent: row.reportContent,
          reportLocation: row.reportLocation,
          reportLink: row.reportLink,
          lecturePoster: row.lecturePoster,
          semester: this.formData.semester,
          auditStatus: row.auditStatus,
          reportFeeling: row.reportFeeling || ''
        };
        this.currentRecordId = row.reportId;
        console.log("this.currentRecordId:"+this.currentRecordId)
        // 处理总结文档回显
        this.reportFeelingList = [];
        if (row.reportFeeling) {
          // 将数据库中的路径字符串转换为上传组件需要的格式
          const fileName = row.reportFeelingName || this.getFileName(row.reportFeeling);
          this.reportFeelingList = [{
            name: fileName,
            url: this.getFullUrl(row.reportFeeling)
          }];

          // 保持原始文件引用
          this.reportFeeling = row.reportFeeling;
        }

        //已有照片列表
        this.existingFiles = row.reportPicture;

        // 解析图片路径
        const paths = typeof row.reportPicture === 'string'
          ? JSON.parse(row.reportPicture)
          : row.reportPicture || [];

        // 生成符合要求的文件列表
        this.fileList = paths.map((path, index) => ({
          uid: `existing-${Date.now()}-${index}`,
          name: path.split('/').pop(),
          url: this.getFullUrl(path),
          status: 'success',
          isOld: true,
          path: path
        }));

        // 显示对话框
        this.showDialog = true;
        this.isEdit = true;

        console.log('初始化编辑数据:', {
          formData: this.formData,
          fileList: this.fileList
        });

      } catch (error) {
        console.error('编辑初始化失败:', error);
        this.$message.error('数据加载失败，请检查控制台');
      }
    },
    // 辅助方法：从路径获取文件名
    getFileName(path) {
      return path.split('/').pop().replace(/\?.*/, '');
    },
// 修正后的文件路径方法
    getFullUrl(filePath) {
      // 处理可能存在的重复profile目录
      const cleanPath = filePath.replace(/^profile\//, '');
      return `${process.env.VUE_APP_BASE_API}/profile/${cleanPath}`;
    },

// 文件移除处理
    handleFileRemove(file, fileList) {
      // 如果是已存在文件，记录需要删除
      if(file.isOld) {
        this.$set(file, 'deleteFlag', true)
      }
      this.fileList = fileList
    },
    handlePreviewFile(file) {
      if (file.isOld) {
        window.open(file.url);
      } else {
        const reader = new FileReader();
        reader.onload = (e) => {
          window.open(e.target.result);
        };
        reader.readAsDataURL(file.raw);
      }
    },
// 图片压缩方法
    compressImage(file) {
      return new Promise(resolve => {
        const reader = new FileReader()
        reader.onload = e => {
          const img = new Image()
          img.onload = () => {
            const canvas = document.createElement('canvas')
            // 保持宽高比压缩到800px以内
            const MAX_SIZE = 800
            let width = img.width
            let height = img.height

            if (width > height) {
              if (width > MAX_SIZE) {
                height *= MAX_SIZE / width
                width = MAX_SIZE
              }
            } else {
              if (height > MAX_SIZE) {
                width *= MAX_SIZE / height
                height = MAX_SIZE
              }
            }

            canvas.width = width
            canvas.height = height
            const ctx = canvas.getContext('2d')
            ctx.drawImage(img, 0, 0, width, height)

            canvas.toBlob(blob => {
              resolve(new File([blob], file.name, {
                type: 'image/jpeg',
                lastModified: Date.now()
              }))
            }, 'image/jpeg', 0.8)
          }
          img.src = e.target.result
        }
        reader.readAsDataURL(file)
      })
    },
    // 生成带时间戳的文件名
    generateFeelingFileName() {
      const date = new Date().toISOString().slice(0, 10);
      const ext = this.getFeelingFileExtension();
      return `reportFeeling_${date}_${Math.random().toString(36).substr(2, 5)}.${ext}`;
    },

    // 获取文件扩展名
    getFeelingFileExtension() {
      if (!this.reportFeeling) return '';
      const match = this.reportFeeling.name.match(/\.([a-zA-Z0-9]+)(\?.*)?$/);
      return match ? match[1].toLowerCase() : '';
    },

    //总结文档下载
    async downloadReportFeeling(row) {
      try {
        const filePaths = row.reportFeeling;
        const fileName = row.reportFeelingName || this.getFileName(filePaths);
        const link = document.createElement('a');
        link.href = `${process.env.VUE_APP_BASE_API}/profile/${filePaths}`;
        // link.download = this.generateFeelingFileName();
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
        console.error("下载错误详情:", error);
      }
    },

    //转化学期
    findSemester(semester) {
      const semesterTrimmed = semester.trim();  // 去除前后空格
      switch (semester) {
        case '大一上':
          return 1;
        case '大一下':
          return 2;
        case '大二上':
          return 3;
        case '大二下':
          return 4;
        case '大三上':
          return 5;
        case '大三下':
          return 6;
        case '大四上':
          return 7;
        case '大四下':
          return 8;
        default:
          return 0;
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
    addNewCard() {
      this.showDialog = true;
      this.isEdit = false;
      this.currentRecordId = null;
      // 重置文档相关状态
      this.reportFeelingList = [];
      this.reportFeeling = null;
    },
    closeCard() {
      this.showDialog = false;
      this.reportFeeling = null;
      this.existingFiles = [];
      this.showDialog = false;
      this.fileList = [];
      this.formData = {
        reportTitle: '',
        reporter: '',
        reportDate: '',
        reportContent: '',
        reportLink: '',
        reportLocation: '',
        lecturePoster: '',
        reportFeeling:'',
        reportFeelingName: '',
        reportPicture: [],
        //审核状态
        auditStatus: '',
        //学期
        semester: this.findSemester(this.activeSemester),
        auditTime: '',
        auditRemark: '',
      };
    },
    // 修改后的总结文档处理方法
    handleSummaryChange(file, fileList) {
      if (fileList.length > 1) {
        this.$message.warning('只能上传一个文件')
        fileList.splice(0, 1)
      }
      // 保存原始文件名（新增）
      this.originalFeelingName = file.name
      // 关键修改：获取原生文件对象
      this.reportFeelingList = fileList
      this.reportFeeling = file.raw // 使用 raw 属性获取原生 File
      console.log(this.reportFeeling)
    },

    // 文件移除回调
    handleSummaryRemove() {
      this.reportFeelingList = []
      this.originalFeelingName = ''//清空文件名
    },

    async listReport() {
      this.isLoading = true; // 设置为加载状态
      try {
        const data = await listReport({
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          semester: this.formData.semester,
          studentId: store.state.user.name,
          // ...this.queryParams,
        });
        console.log(data);
        this.records = data.rows || []; // 假设后端返回的数据格式包含 rows
        this.totalRecords = data.total || 0;       // 假设返回总记录数 total
      } catch (error) {
        console.error("Error fetching competition records:", error);
      } finally {
        this.isLoading = false; // 无论成功还是失败，结束加载状态
      }
    },


    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          console.log("this.currentRecordId:"+this.currentRecordId)
          console.log(this.records)
          //唯一性检验
          const originalReport = this.records.find(
            item => item.reportId === this.currentRecordId
          );
          console.log('原始数据:', originalReport);
          const isKeyFieldChanged = !originalReport ||
            this.formData.reportTitle !== originalReport.reportTitle ||
            this.formData.reportDate !== originalReport.reportDate ||
            this.formData.reporter !== originalReport.reporter ||
            this.formData.reportLocation !== originalReport.reportLocation;

          if (isKeyFieldChanged) {
            try {
              const checkRes = await checkLectureUnique({
                studentId: this.$store.state.user.name,
                reportTitle: this.formData.reportTitle,
                reporter: this.formData.reporter,
                reportDate: this.formData.reportDate,
                reportLocation: this.formData.reportLocation,
                semester: this.findSemester(this.activeSemester)
              });
              if (checkRes.code !== 200) return this.$message.error('已存在相同活动记录,不可重复添加');
            } catch (error) {
              return this.$message.error(`校验失败: ${error.message}`);
            }
          }


          this.formData.reportFeelingName = this.originalFeelingName
          console.log('表单数据:', this.formData)
          const formData = new FormData();
          const json = JSON.stringify(this.formData);
          formData.append('studentLectureReport', json);
          formData.append('reportFeeling', this.reportFeeling);
          console.log('表单数据formData.reportFeeling:', this.reportFeeling);

          // 构建文件数据
          const keepFiles = this.fileList
            .filter(file => !file.deleteFlag)
            .map(file => file.isOld ? file.path : file.raw)

          // 分离新旧文件
          const oldFiles = keepFiles.filter(f => typeof f === 'string')
          const newFiles = keepFiles.filter(f => f instanceof File)

          // 添加数据
          formData.append('previewImages', JSON.stringify(oldFiles))
          newFiles.forEach(file => {
            formData.append('reportPicture', file)
          })


          console.log('表单数据formData.reportPicture:', this.formData.reportPicture);
          console.log('传递后端数据:', formData);
          // 可以使用 axios 或 fetch 发送请求
          // 例如：
          if (this.isEdit) {
            //修改信息
            updateReport(formData).then(response => {
              console.log("+++++++++", response);
              this.$message.success('保存成功');
              this.initData();
            })
              .catch(error => {
                console.error(error);
                this.initData();
              });
          } else {
            //第一次添加信息
            addReport(formData).then(response => {
              console.log("+++++++++", response);
              this.$message.success('提交成功');
              this.initData();
            })
              .catch(error => {
                console.error(error);
                this.initData();
              });
          }
        } else {
          this.$message.error('请填写完整表单信息');
        }
      });
    },

    initData() {
      this.reportFeeling = null;
      this.existingFiles = [];
      this.showDialog = false;
      this.records = [];
      this.formData = {
        reportTitle: '',
        reporter: '',
        reportDate: '',
        reportContent: '',
        reportLink: '',
        reportLocation: '',
        lecturePoster: '',
        reportPicture: [],
        auditStatus: '',
        semester: this.findSemester(this.activeSemester),
      };
      this.fileList = [];
      this.listReport();  // 在页面加载时获取数据
    },
  }
};
</script>

<style scoped>
/* ================= 全局容器样式 ================= */
.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 2rem;
  background: linear-gradient(160deg, #EBF4FF 0%, #EBF8FF 100%);
  min-height: 100vh;
}

.main-container {
  background: #ffffff;
  border-radius: 1.5rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  position: relative;
  overflow: hidden;
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

.score-icon {
  font-size: 1.5em;
  margin-right: 0.5rem;
}

.current-semester {
  font-size: 1.2rem;
  opacity: 0.9;
}

/* ================= 表格样式 ================= */
.report-table-card {
  background: #fff;
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  margin-top: 1.5rem;
}

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

/* 特殊行样式 */
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
/* 讲座题目列 - 整体样式 */
.lecture-name {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
}

/* 图标美化 */

.name-icon {
  /* 核心样式 */
  font-size: 18px;
  background: linear-gradient(45deg, #673AB7 30%, #9C27B0 70%); /* 学术紫渐变 */
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent !important;
  filter:
    drop-shadow(0 1px 1px rgba(103,58,183,0.1))
    drop-shadow(0 0 1px rgba(255,255,255,0.5));

  /* 布局调整 */
  transform: scale(1.05);
  margin-right: 2px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 悬停互动效果 */
.name-icon :hover{
  filter:
    drop-shadow(0 1.5px 2px rgba(126,87,194,0.15))
    drop-shadow(0 0 1.2px rgba(255,255,255,0.8));
  transform: scale(1.1);
}

/* 标签统一样式 */
.level-tag,
.status-tag,
.reporter-tag,
.location-tag{
  border-radius: 12px;
  padding: 0 10px;
  font-weight: 500;
}

.level-tag {
  background: #f0fff4;
  border-color: #c6f6d5;
  color: #38a169;
}
/* 讲师姓名标签 */
.reporter-tag {
  background: #fff9db;
  border-color: #ffe082;
  color: #b38f00;
}
/* 上传组件样式 */
.enhanced-upload {
  width: 100%;
}

/* 上传按钮样式 */
.custom-upload-btn {
  background: linear-gradient(135deg, #409EFF, #3375ff);
  border: none;
  padding: 10px 20px;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }

  i {
    margin-right: 8px;
  }
}

/* 提示信息样式 */
.custom-upload-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
  display: flex;
  align-items: center;

  .el-icon-info {
    margin-right: 6px;
    font-size: 14px;
  }
}

/* 文件项样式 */
.custom-file-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-top: 10px;
  background: #f8faff;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s;

  &:hover {
    transform: translateX(4px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
}

.file-icon-wrapper {
  width: 36px;
  height: 36px;
  background: #409EFF10;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;

  .el-icon-document {
    font-size: 20px;

    &.pdf { color: #FF5252; }
    &.doc, &.docx { color: #2B579A; }
    &.default { color: #409EFF; }
  }
}

.file-details {
  flex: 1;
  margin: 0 16px;

  .file-name {
    display: block;
    color: #303133;
    font-weight: 500;
    max-width: 300px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .file-size {
    color: #909399;
    font-size: 12px;
  }
}

.delete-btn {
  opacity: 0.7;
  transition: all 0.3s;

  &:hover {
    opacity: 1;
    transform: scale(1.1);
  }

  /deep/ i {
    vertical-align: baseline;
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
  text-decoration: none;
  transition: all 0.2s; /* 添加悬流动画 */
}

/* 悬停状态增强体验 */
.location-tag .link-style:hover {
  text-decoration: underline;
  opacity: 0.8;
}
/* 禁用状态样式 */
.document-btn.el-button--primary.is-disabled {
  background-color: #f0f4ff !important;
  border-color: #c6d9ff !important;
  color: #a3c3ff !important;
  cursor: not-allowed;
}
/* 悬停状态保持禁用样式 */
.document-btn.el-button--primary.is-disabled:hover {
  background-color: #f0f4ff !important;
  border-color: #c6d9ff !important;
  color: #a3c3ff !important;
}

/* 禁用图标样式 */
.document-btn.is-disabled .el-icon-download {
  opacity: 0.5;
}

/* 链接样式 */
.link-style {
  color: #409EFF; /* Element主蓝色 */
  text-decoration: none;
  transition: all 0.3s;
}

.link-style:hover {
  text-decoration: underline;
  color: #79bbff;
}

/* 调整标签内边距 */
.location-tag .el-tag__content {
  padding: 2px 6px;
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

/* ================= 对话框样式 ================= */
.lecture-dialog {
  border-radius: 12px;
}

.lecture-dialog /deep/ .el-dialog__header {
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
.lecture-form {
  padding: 0 30px 20px;
}

.input-icon {
  color: #a0aec0;
  margin-left: 8px;
}


.el-upload__tip {
  color: #718096;
  font-size: 0.85rem;
}

/* 操作按钮 */
.form-actions {
  margin-top: 25px;
  text-align: center;
}

.save-btn {
  background: #edf2f7;
  border-color: #cbd5e0;
  color: #4a5568;
}

.submit-btn {
  background: #48bb78;
  border-color: #48bb78;
}

.submit-btn:hover {
  background: #38a169;
  border-color: #38a169;
}
/* ================= 响应式设计 ================= */
@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .nav h2 {
    font-size: 1.4rem;
  }

  .optimized-table /deep/ .el-table__header th {
    font-size: 0.8rem;
  }
}
</style>
