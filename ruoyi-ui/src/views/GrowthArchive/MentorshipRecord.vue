<template>
  <div class="container">
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <div class="nav">
        <div class="nav-content">
          <h2>
            <span class="mentorship-icon">👥</span>
            导师指导记录
            <span class="current-semester">{{ activeSemester }} 指导成果</span>
          </h2>
          <el-button
            type="primary"
            class="add-button"
            @click="addNewCard"
            icon="el-icon-plus"
          >新增记录
          </el-button>
        </div>
      </div>

      <!-- 表格区域 -->
      <div class="mentorship-table-card">
        <el-table
          :data="meetingRecords"
          class="optimized-table"
          :header-cell-style="headerStyle"
          highlight-current-row
        >
          <!-- 序号列 -->
          <el-table-column label="序号" width="80" align="center">
            <template v-slot="scope">
              <span class="index-badge">
                {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
              </span>
            </template>
          </el-table-column>
          <!-- 指导主题 -->
          <el-table-column prop="guidanceTopic" label="指导主题" min-width="120">
            <template v-slot="scope">
              <div class="mentorship-name">
                <i class="el-icon-s-opportunity name-icon"></i>
                <span class="name-text">{{ scope.row.guidanceTopic }}</span>
              </div>
            </template>
          </el-table-column>
          <!-- 导师姓名 -->
          <el-table-column prop="tutorName" label="导师姓名" min-width="120">
            <template v-slot="scope">
              <div class="mentorship-name">
                <i class="el-icon-s-custom mentor-icon"></i>
                <span class="name-text">{{ scope.row.tutorName }}</span>
              </div>
            </template>
          </el-table-column>
          <!-- 指导地点 -->
          <el-table-column prop="guidanceLocation" label="指导地点" width="120" align="center">
            <template v-slot="scope">
              <el-tag effect="light" class="level-tag">
                {{ scope.row.guidanceLocation }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 指导日期 -->
          <el-table-column prop="guidanceTime" label="指导日期" width="120" align="center">
            <template v-slot="scope">
              <span class="time-display">
                {{ formatDate(scope.row.guidanceTime) }}
              </span>
            </template>
          </el-table-column>

          <!-- 总结文档 -->
          <el-table-column label="总结文档" width="160" align="center">
            <template v-slot="scope">
              <el-dropdown
                trigger="click"
                @command="handleDocCommand"
                :disabled="!scope.row.summaryFilePath"
              >
                <el-button
                  type="primary"
                  size="mini"
                  plain
                  class="file-btn"
                  :disabled="!scope.row.summaryFilePath"
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

          <!-- 指导图片 -->
          <el-table-column label="指导图片" width="140" align="center">
            <template v-slot="scope">
              <el-dropdown trigger="click"
                           @command="handleFileCommand"
                           :disabled="!scope.row.photoPaths || scope.row.photoPaths === '[]'">
                <el-button type="primary"
                           size="mini"
                           plain
                           :disabled="!scope.row.photoPaths ||                                                                                        scope.row.photoPaths === '[]'">
                  <i class="el-icon-picture"></i> 图片操作
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
          <el-table-column label="操作" width="120" align="center">
            <template v-slot="scope">
              <template v-if="scope.row.auditStatus === '未通过'">
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="handleEdit(scope.row)"
                >重新提交
                </el-button>
              </template>

              <template v-if="scope.row.auditStatus === '未提交'">
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

        <!-- 分页组件 -->
        <pagination
          v-show="totalRecords > 0"
          :total="totalRecords"
          :page.sync="currentPage"
          :limit.sync="pageSize"
          @pagination="fetchMeetingRecords"
          class="custom-pagination"
        />
      </div>

      <!-- 图片预览对话框 -->
      <el-dialog
        :visible.sync="previewVisible"
        title="图片预览"
        class="preview-dialog"
        width="60%"
      >
        <div style="text-align: center; margin-bottom: 20px;">
          <img
            :src="previewImages[currentPreviewIndex]"
            style="max-width: 100%; display: block; margin: 0 auto;"
            alt="指导图片预览"
          />
          <el-button
            icon="el-icon-arrow-left"
            :disabled="currentPreviewIndex === 0"
            @click="currentPreviewIndex--"
          ></el-button>
          <span style="margin: 0 20px;">
            {{ currentPreviewIndex + 1 }} / {{ previewImages.length }}
          </span>
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

      <!-- 新增/编辑对话框 -->
      <el-dialog
        :visible.sync="showDialog"
        :title="isEdit ? '编辑指导记录' : '新增指导记录'"
        class="mentorship-dialog"
        width="580px"
        @close="closeCard"
      >
        <div class="dialog-header">
          <h3 class="form-title">{{ isEdit ? '编辑指导记录' : '新增指导记录' }}</h3>
          <p class="form-tips">请填写本学期导师指导信息（带<span class="required">*</span>为必填项）</p>
        </div>

        <el-form
          ref="form"
          :model="formData"
          :rules="rules"
          label-width="110px"
          class="mentorship-form"
        >
          <!-- 指导主题 -->
          <el-form-item label="指导主题" prop="guidanceTopic">
            <el-input
              v-model="formData.guidanceTopic"
              placeholder="请输入指导主题"
              class="custom-input"
            >
              <i slot="prefix" class="el-icon-s-opportunity input-icon"></i>
            </el-input>
          </el-form-item>
          <!-- 指导导师 -->
          <el-form-item label="导师工号" prop="tutorId">
            <el-input
              v-model="formData.tutorId"
              placeholder="请输入导师工号"
              class="custom-input"
            >
              <i slot="prefix" class="el-icon-s-opportunity input-icon"></i>
            </el-input>
          </el-form-item>
          <!-- 指导地点 -->
          <el-form-item label="指导地点" prop="guidanceLocation">
            <el-input
              v-model="formData.guidanceLocation"
              placeholder="请输入指导地点"
              class="custom-input"
            >
              <i slot="prefix" class="el-icon-location input-icon"></i>
            </el-input>
          </el-form-item>

          <!-- 指导时间 -->
          <el-form-item label="指导时间" prop="guidanceTime">
            <el-date-picker
              v-model="formData.guidanceTime"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期"
              class="custom-date"
              :picker-options="pickerOptions"
            >
              <i slot="suffix" class="el-icon-date date-icon"></i>
            </el-date-picker>
          </el-form-item>
          <!-- 学生评价 -->
          <el-form-item label="学生评价" prop="studentComment">
            <el-input
              type="textarea"
              v-model="formData.studentComment"
              :rows="3"
              placeholder="请输入对导师本次指导的评价"
              class="custom-comment"
              maxlength="50"
              show-word-limit
            >
              <i slot="prefix" class="el-icon-edit input-icon"></i>
            </el-input>
          </el-form-item>
          <!-- 总结文档上传组件 -->
          <el-form-item label="总结文档" prop="summaryFilePath">
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

          <!-- 指导图片 -->
          <el-form-item label="指导图片" prop="photoPaths">
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
              <div slot="tip" class="el-upload__tip">支持格式：JPG/PNG 单文件≤10MB 最多5张</div>
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
            <el-button
              type="primary"
              class="submit-btn"
              @click="handleSubmit"
            >正式提交
            </el-button>
          </el-form-item>
        </el-form>
      </el-dialog>

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
    </div>
  </div>
</template>

<script>
import axios from "axios";
import {
  listMentorship,
  addMentorship,
  delMentorship,
  updateMentorship,
  checkMentorshipUnique,
} from "@/api/system/mentorship";
import {Properties as $download} from "svg-sprite-loader/examples/custom-runtime-generator/build/main";

export default {
  data() {
    return {
      currentPreviewIndex: 0,
      currentDownloadFile: '',
      docPreviewVisible: false,
      currentDocument: {
        url: '',
        type: '',
        name: ''
      },
      docxContent: '',
      originalSummaryFileName: '', // 保存原始文件名
      currentRecordId: null, // 新增当前记录ID
      isEdit: false,//判断修改还是插入
      previewVisible: false,
      previewImages: [],
      meetingRecords: [],// 存储后端返回的导师会议记录数据
      queryParams: {}, // 查询条件
      currentPage: 1, // 当前页
      pageSize: 10, // 每页显示的条数
      totalRecords: 0, // 总记录数
      showDialog: false,
      selectedFile: null,
      uploadMessage: null,
      summaryFile: null,
      summaryFilePath: null,
      summaryFileList: [],
      fileList: [],
      activeSemester: '', // 当前学期
      formData: {
        guidanceTopic: '',
        guidanceLocation: '',
        guidanceTime: '',
        studentComment: '',
        summaryFilePath: '',
        photoPaths: [],
        auditStatus: '未提交',
        semester: '',
        auditTime: null,
        auditRemark: '',
      },
      rules: {
        guidanceTopic: [
          {required: true, message: '指导主题不能为空', trigger: 'blur'}
        ],
        guidanceLocation: [
          {required: true, message: '指导地点不能为空', trigger: 'blur'}
        ],
        guidanceTime: [
          {required: true, message: '请选择指导时间', trigger: 'change'}
        ]
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        }
      },
    };
  },
  mounted() {
    // 获取学期数据
    this.activeSemester = this.$route.query.semester || '未知学期';
    this.formData.semester = this.activeSemester;
    this.fetchMeetingRecords();  // 在页面加载时获取数据
  },
  methods: {
    // 处理文档操作命令
    handleDocCommand(command) {
      console.log(command.action)
      try {
        const filePath = command.row.summaryFilePath;
        if (!filePath) {
          this.$message.warning('无可用文档');
          return;
        }
        console.log("我进来了0" + command.row.summaryFilePath)
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
          this.downloadSummaryDocument(command.row);
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
        console.log("文件类型：" + file.type)
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
    // 修改后的总结文档处理方法
    handleSummaryChange(file, fileList) {
      if (fileList.length > 1) {
        this.$message.warning('只能上传一个文件')
        fileList.splice(0, 1)
      }
      // 保存原始文件名（新增）
      this.originalSummaryFileName = file.name
      // 关键修改：获取原生文件对象
      this.summaryFileList = fileList
      this.summaryFile = file.raw
    },

    // 文件移除回调
    handleSummaryRemove() {
      this.summaryFileList = []
      this.summaryFilePath = ''
      this.formData.summaryFilePath = ''
      this.originalSummaryFileName = ''//清空文件名
    },

    handleFileCommand(command) {
      if (command.action === 'preview') {
        this.handlePreview(command.files)
      } else if (command.action === 'download') {
        this.downloadFiles(command.files)
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

    getFullUrl(filePath) {
      return `${process.env.VUE_APP_BASE_API}/profile/${filePath}`;
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

    // 状态标签样式
    getStatusTagType(status) {
      const typeMap = {
        '已通过': 'success',
        '未审核': 'warning',
        '未通过': 'danger',
        '未提交': 'info'
      }
      return typeMap[status] || 'info'
    },

    // 状态图标
    getStatusIcon(status) {
      const iconMap = {
        '已通过': 'el-icon-circle-check',
        '未审核': 'el-icon-time',
        '未通过': 'el-icon-circle-close',
        '未提交': 'el-icon-edit'
      }
      return iconMap[status] || 'el-icon-question'
    },

    // 表头样式
    headerStyle() {
      return {
        backgroundColor: '#EBF4FF',
        color: '#2B6CB0',
        fontWeight: "600",
      };
    },

    $download() {
      return $download
    },
    //保存草稿
    async handleSave() {
      await this.submitData("未提交");
    },
    //正式提交
    async handleSubmit() {
      await this.submitData("未审核");
    },

    // 删除未提交记录
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该记录吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        const response = await delMentorship(row.recordId);
        if (response.code === 200) {
          this.$message.success('删除成功');
          this.initData();
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`删除失败: ${error.message}`);
        }
      }
    },

    //处理草稿修改
    handleEdit(row) {
      const rawData = JSON.parse(JSON.stringify(row));
      this.formData = {...rawData};
      this.isEdit = true;
      this.currentRecordId = rawData.recordId;
      this.showDialog = true;
      // 正确初始化路径字段
      this.formData.summaryFilePath = row.summaryFilePath || '';
      // 处理总结文档回显
      this.summaryFileList = [];
      this.summaryFile = null;
      if (row.summaryFilePath) {
        // 将数据库中的路径字符串转换为上传组件需要的格式
        const fileName = row.summaryFileName || this.getFileName(row.summaryFilePath);
        this.summaryFileList = [{
          name: fileName,
          url: this.getFullUrl(row.summaryFilePath)
        }];
        console.log("66666666:")
        console.log(this.summaryFileList)
        // 保持原始文件引用
        this.summaryFilePath = row.summaryFilePath;
      }

      // 解析文件路径
      const proofMaterial = this.parseMaterial(rawData.photoPaths);

      // 生成符合el-upload要求的文件列表
      this.fileList = proofMaterial.map((path, index) => ({
        uid: Date.now() + index,
        name: path.split('/').pop(),
        url: `${process.env.VUE_APP_BASE_API}/profile/${encodeURIComponent(path)}`,
        status: 'success',
        isOld: true,
        path: path
      })).filter(Boolean);
    },

    // 解析文件路径
    parseMaterial(material) {
      try {
        if (Array.isArray(material)) return [...material];
        if (typeof material === 'string') {
          const cleaned = material.replace(/__ob__:.*?($$|$$)/gs, '');
          return JSON.parse(cleaned);
        }
        return [];
      } catch (e) {
        console.error('材料解析失败:', e);
        return [];
      }
    },

    //总结文档下载
    async downloadSummaryDocument(row) {
      try {
        const filePaths = row.summaryFilePath;
        const fileName = row.summaryFileName || this.getFileName(filePaths);
        const link = document.createElement('a');
        link.href = `${process.env.VUE_APP_BASE_API}/profile/${filePaths}`;
        // link.download = this.generateSummaryFileName();
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
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
    // 给指导图片生成带时间戳的文件名
    generateFileName1(filePath) {
      const originalName = filePath.split('/').pop() || '指导图片';
      const timestamp = new Date().getTime();
      const ext = originalName.split('.').pop() || 'jpg';
      return `${originalName.split('.')[0]}_${timestamp}.${ext}`;
    },

    handlePreview(filePath) {
      try {
        const paths = typeof filePath === 'string'
          ? JSON.parse(filePath)
          : filePath;

        if (paths.length === 0) {
          this.$message.error('预览失败,当前没有添加过指导图片');
        }
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
    handleFileChange(file, fileList) {
      this.fileList = fileList.slice(-5); // 保持最多5个文件
    },

    handleFileRemove(file, fileList) {
      this.fileList = fileList;
    },

    addNewCard() {
      this.showDialog = true;
      this.isEdit = false;
      // 重置文档相关状态
      this.summaryFileList = [];
      this.summaryFilePath = null;
    },
    closeCard() {
      this.summaryFilePath = null;
      this.fileList = [];
      this.currentRecordId = null;
      this.showDialog = false;
      this.formData = {
        guidanceTopic: '',
        guidanceLocation: '',
        guidanceTime: '',
        studentComment: '',
        summaryFilePath: '',
        photoPaths: [],
        //审核状态
        auditStatus: '',
        //学期
        semester: this.activeSemester,
      };
    },
// 数据获取方法
    async fetchMeetingRecords() {
      this.isLoading = true;
      try {
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          studentId: this.$store.state.user.name,
          semester: this.activeSemester,
          fileList: this.fileList,
          ...this.queryParams
        };

        const response = await listMentorship(params);
        if (response.code === 200) {
          this.meetingRecords = response.rows || [];
          this.totalRecords = response.total || 0;

        }
      } catch (error) {
        console.error("获取会议记录失败:", error);
      } finally {
        this.isLoading = false;
      }
    },
    // 统一提交方法
    async submitData(status) {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          // 获取原始记录数据（编辑时）
          const originalRecord = this.meetingRecords.find(
            item => item.recordId === this.currentRecordId
          );
          // 检测关键字段是否修改
          const isKeyFieldChanged = !originalRecord ||
            this.formData.guidanceTopic !== originalRecord.guidanceTopic ||
            this.formData.guidanceLocation !== originalRecord.guidanceLocation ||
            this.formData.guidanceTime !== originalRecord.guidanceTime;
          console.log("isKeyFieldChanged:" + isKeyFieldChanged);
          // 编辑时排除自身
          if (isKeyFieldChanged) {
            // 唯一性校验参数
            const checkParams = {
              studentId: this.$store.state.user.name,
              guidanceTopic: this.formData.guidanceTopic,
              guidanceLocation: this.formData.guidanceLocation,
              guidanceTime: this.formData.guidanceTime,
              semester: this.activeSemester,
            };
            const checkRes = await checkMentorshipUnique(checkParams);
            if (checkRes.code !== 200) {
              return this.$message.error('已存在相同活动记录，不可重复添加');
            }
          }

          const existingPaths = this.fileList
            .filter(file => file.isOld)
            .map(file => file.path);
          const newFiles = this.fileList
            .filter(file => !file.isOld)
            .map(file => file.raw);
          const formData = new FormData();

          // 构建核心数据对象
          const recordData = {
            recordId: this.currentRecordId,
            tutorId: this.formData.tutorId,
            guidanceTopic: this.formData.guidanceTopic,
            guidanceLocation: this.formData.guidanceLocation,
            guidanceTime: this.formData.guidanceTime,
            semester: this.activeSemester,
            auditStatus: status,
            studentComment: this.formData.studentComment,
            studentId: this.$store.state.user.name,
            photoPaths: JSON.stringify(this.fileList.map(file => file.url)),
            auditTime: null,
            auditRemark: "",
            existingProofMaterial: existingPaths,
            summaryFilePath: this.formData.summaryFilePath || '',
            summaryFileName: this.originalSummaryFileName,
          };
          console.log("recordData:");
          console.log(recordData);
          // 如果是编辑操作，添加ID字段
          if (this.currentRecordId) {
            recordData.recordId = this.currentRecordId;
          }

          // 构建 JSON 部分（指定类型为 application/json）
          const recordBlob = new Blob(
            [JSON.stringify(recordData)],
            {type: "application/json"}
          );
          formData.append("record", recordBlob);

          //添加文件
          this.fileList.forEach((file) => {
            formData.append("photoPaths", file.raw);
          });
          console.log("this.summaryFile:", this.summaryFile)
          formData.append('summaryFile', this.summaryFile);

          // 配置 headers
          const config = {
            headers: {
              "Authorization": "Bearer " + localStorage.getItem("token"),
              "Content-Type": "multipart/form-data"
            }
          };

          // 根据模式选择 API 方法
          const apiMethod = this.currentRecordId ? updateMentorship : addMentorship;

          apiMethod(formData, config)
            .then(() => {
              this.$message.success(this.currentRecordId ? "更新成功！" : "提交成功！");
              this.fetchMeetingRecords();
              this.closeCard();
              this.currentRecordId = null; // 清空当前记录ID
            })
            .catch(error => {
              this.$message.error(`操作失败：${error.message}`);
            });
        }
      });
    },
    initData() {
      this.fetchMeetingRecords();  // 在页面加载时获取数据
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

.mentorship-icon {
  font-size: 1.5em;
  margin-right: 0.5rem;
}

.current-semester {
  font-size: 1.2rem;
  opacity: 0.9;
}

/* ================= 表格相关样式 ================= */
.mentorship-table-card {
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

.mentorship-name {
  display: flex;
  align-items: center;
  padding: 8px 0;
  gap: 8px;
}

.name-icon {
  /* 核心样式 */
  font-size: 18px;
  background: linear-gradient(45deg, #FF9800 20%, #FF5722 80%); /* 活力橙红渐变 */
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent !important;
  filter: drop-shadow(0 1px 1px rgba(255, 152, 0, 0.15)) drop-shadow(0 0 1px rgba(255, 255, 255, 0.6));

  /* 动态效果 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: center;
}

/* 悬停互动 */
.name-icon:hover {
  filter: drop-shadow(0 1.5px 2px rgba(255, 183, 77, 0.25)) drop-shadow(0 0 1.2px rgba(255, 255, 255, 0.8));
  transform: scale(1.1);
}

/* 标签统一样式 */
.level-tag,
.status-tag {
  border-radius: 12px;
  padding: 0 10px;
  font-weight: 500;
}

/* 指导地点标签 */
.level-tag {
  background: #f0fff4;
  border-color: #c6f6d5;
  color: #38a169;
}

/* 审核状态标签 */
.status-tag {
  min-width: 70px;
  text-align: center;
}

/* ================= 对话框样式 ================= */
.mentorship-dialog {
  border-radius: 12px;
}

.mentorship-dialog /deep/ .el-dialog__header {
  display: none; /* 隐藏原生标题 */
}

.dialog-header {
  text-align: center;
  padding: 20px 0 15px;
  background: linear-gradient(135deg, #2B6CB0 0%, #4299E1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
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

    &.pdf {
      color: #FF5252;
    }

    &.doc, &.docx {
      color: #2B579A;
    }

    &.default {
      color: #409EFF;
    }
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

/* 表单元素样式 */
.mentorship-form {
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
  .container {
    padding: 1rem;
  }

  .nav h2 {
    font-size: 1.4rem;
  }

  .mentorship-form {
    padding: 0 15px;
  }
}

@media (max-width: 480px) {
  .nav-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .form-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .save-btn,
  .submit-btn {
    width: 100%;
  }
}

/* 指导页面特殊样式 */
.document-btn {
  padding: 6px 12px;
  font-size: 12px;
  background: #4299e1;
  border-color: #4299e1;
}

.time-display {
  font-family: monospace;
  color: #4a5568;
}

.remark-text {
  color: #718096;
  font-size: 0.9rem;
  line-height: 1.4;
}
/* ================= 文档样式美化 ================= */
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

</style>
