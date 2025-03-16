<template>
  <div class="container">
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <div class="nav">
        <div class="nav-content">
          <h2>
            <span class="score-icon">👨🏫</span>
            导师指导记录
            <span class="current-semester">{{ activeSemester }} 指导成果</span>
          </h2>
          <el-button
            type="primary"
            class="add-button"
            @click="addNewCard"
            icon="el-icon-plus"
          >新增记录</el-button>
        </div>
      </div>

      <!-- 数据表格区域 -->
      <div class="score-table-card">
        <el-table
          :data="meetingRecords"
          class="optimized-table"
          :header-cell-style="headerStyle"
          highlight-current-row
          @row-click="handleRowClick"
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
          <el-table-column prop="guidanceTopic" label="指导主题" min-width="180">
            <template v-slot="scope">
              <div class="competition-name">
                <i class="el-icon-notebook-2 name-icon"></i>
                <span class="name-text">{{ scope.row.guidanceTopic }}</span>
              </div>
            </template>
          </el-table-column>

          <!-- 指导地点 -->
          <el-table-column prop="guidanceLocation" label="地点" width="120" align="center">
            <template v-slot="scope">
              <el-tag effect="light" class="level-tag">
                {{ scope.row.guidanceLocation }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 指导时间 -->
          <el-table-column prop="guidanceTime" label="时间" width="140" align="center">
            <template v-slot="scope">
              <span class="time-display">
                {{ formatDate(scope.row.guidanceTime) }}
              </span>
            </template>
          </el-table-column>

          <!-- 总结文档 -->
          <el-table-column label="总结文档" width="140" align="center">
            <template v-slot="scope">
              <el-button
                v-if="scope.row.summaryFilePath"
                type="primary"
                size="mini"
                @click.stop="downloadSummaryDocument(scope.row.summaryFilePath)"
                class="document-btn"
              >下载</el-button>
              <span v-else>-</span>
            </template>
          </el-table-column>

          <!-- 现场图片 -->
          <el-table-column label="现场图片" width="180" align="center">
            <template v-slot="scope">
              <el-dropdown trigger="click" @command="handleFileCommand">
                <el-button type="primary" size="mini" plain>
                  <i class="el-icon-picture"></i> 文件操作
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    :command="{ action: 'preview', files: scope.row.photoPaths }"
                    :disabled="!scope.row.photoPaths || scope.row.photoPaths === '[]'"
                  >预览</el-dropdown-item>
                  <el-dropdown-item
                    :command="{ action: 'download', files: scope.row.photoPaths }"
                    :disabled="!scope.row.photoPaths || scope.row.photoPaths === '[]'"
                  >下载</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </template>
          </el-table-column>

          <!-- 审核状态 -->
          <el-table-column prop="auditStatus" label="状态" width="140" align="center">
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
          <el-table-column label="操作" width="180" align="center">
            <template v-slot="scope">
              <template v-if="scope.row.auditStatus === '未通过'">
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="handleEditDraft(scope.row)"
                >重新提交</el-button>
              </template>

              <template v-if="scope.row.auditStatus === '未提交'">
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="handleEditDraft(scope.row)"
                >编辑</el-button>
                <el-button
                  type="text"
                  size="mini"
                  style="color: #F56C6C;"
                  @click.stop="handleDelete(scope.row)"
                >删除</el-button>
              </template>

              <el-tag
                v-if="['未审核', '已通过'].includes(scope.row.auditStatus)"
                type="info"
                size="mini"
                class="no-edit-tag"
              >不可修改</el-tag>
            </template>
          </el-table-column>

          <!-- 审核时间 -->
          <el-table-column prop="auditTime" label="审核时间" width="160" align="center">
            <template v-slot="scope">
              <span class="time-display">
                {{ formatDateTime(scope.row.auditTime) }}
              </span>
            </template>
          </el-table-column>

          <!-- 审核意见 -->
          <el-table-column prop="auditRemark" label="审核意见" min-width="180">
            <template v-slot="scope">
              <div class="remark-text">
                {{ scope.row.auditRemark || '暂无备注' }}
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
            alt="现场图片预览"
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
        class="competition-dialog"
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
          class="competition-form"
        >
          <!-- 指导主题 -->
          <el-form-item label="指导主题" prop="guidanceTopic">
            <el-input
              v-model="formData.guidanceTopic"
              placeholder="请输入指导主题"
              class="custom-input"
            >
              <i slot="prefix" class="el-icon-notebook-2 input-icon"></i>
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

          <!-- 总结文档 -->
          <el-form-item label="总结文档" prop="summaryFilePath">
            <el-upload
              :auto-upload="false"
              :limit="1"
              :on-change="handleSummaryChange"
              :on-remove="handleSummaryRemove"
              :file-list="summaryFileList"
            >
              <el-button type="primary" size="mini">选择文件</el-button>
              <template #tip>
                <div class="el-upload__tip">仅支持单个文件上传 (PDF/DOC/DOCX)</div>
              </template>
            </el-upload>
          </el-form-item>

          <!-- 现场图片 -->
          <el-form-item label="现场图片" prop="photoPaths">
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
            >保存草稿</el-button>
            <el-button
              type="primary"
              class="submit-btn"
              @click="handleSubmit"
            >正式提交</el-button>
          </el-form-item>
        </el-form>
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
      currentRecordId: null, // 新增当前记录ID
      isEdit: false,//判断修改还是插入
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      currentDownloadFile: '',
      meetingRecords: [],// 存储后端返回的导师会议记录数据
      queryParams: {}, // 查询条件
      currentPage: 1, // 当前页
      pageSize: 10, // 每页显示的条数
      totalRecords: 0, // 总记录数
      showDialog: false,
      selectedFile: null,
      uploadMessage: null,
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
      }
    };
  },
  mounted() {
    // 获取学期数据
    this.activeSemester = this.$route.query.semester || '未知学期';
    this.formData.semester = this.activeSemester;
    this.fetchMeetingRecords();  // 在页面加载时获取数据
  },
  methods: {
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

    // 处理草稿修改
    handleEditDraft(row) {
      this.handleEdit(row);
    },

    handleEdit(row) {
      const rawData = JSON.parse(JSON.stringify(row));
      this.formData = {...rawData};
      this.isEdit = true;
      this.currentRecordId = rawData.id;
      this.showDialog = true;

      const proofMaterial = this.parseMaterial(rawData.photoPaths);
      this.fileList = proofMaterial.map((path, index) => ({
        uid: Date.now() + index,
        name: path.split('/').pop(),
        url: `${process.env.VUE_APP_BASE_API}/profile/${encodeURIComponent(path)}`,
        status: 'success',
        isOld: true,
        path: path
      })).filter(Boolean);
    },

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
    //现场图片下载
    async downloadMeetingPictures(filePaths) {
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
    // 给现场图片生成带时间戳的文件名
    generateFileName1(filePath) {
      const originalName = filePath.split('/').pop() || '现场图片';
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

        if (paths.length === 0) {
          this.$message.error('预览失败,当前没有添加过现场图片');
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

    // 分页大小变化
    handleSizeChange(size) {
      this.pageSize = size;
      this.fetchMeetingRecords(this.queryParams, this.currentPage, this.pageSize);
    },

    // 当前页变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchMeetingRecords(this.queryParams, this.currentPage, this.pageSize);
    },


    addNewCard() {
      this.showDialog = true;
      this.isEdit = false;
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
    onFileChange(e) {
      // 当用户选择文件时，更新file变量
      this.summaryFilePath = e.target.files[0];
      this.formData.summaryFilePath = this.summaryFilePath ? this.summaryFilePath.name : '';
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
            guidanceTopic: this.formData.guidanceTopic,
            guidanceLocation: this.formData.guidanceLocation,
            guidanceTime: this.formData.guidanceTime,
            semester: this.activeSemester,
            auditStatus: status,
            studentComment: this.formData.studentComment,
            studentId: this.$store.state.user.name,
            summaryFilePath: this.formData.summaryFilePath,
            photoPaths: JSON.stringify(this.fileList.map(file => file.url)),
            auditTime: null,
            auditRemark: "",
            existingProofMaterial: existingPaths,
          };

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

          formData.append('summaryFile', this.selectedFile);

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
  max-width: 1440px;
  margin: 0 auto;
  padding: 2rem;
  background: linear-gradient(160deg, #f0f4ff 0%, #f8fbff 100%);
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
  background: linear-gradient(135deg, #4a5568 0%, #718096 100%);
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

/* ================= 表格相关样式 ================= */
.score-table-card {
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

.competition-name {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.name-icon {
  color: #ecc94b;
  margin-right: 8px;
  font-size: 1.2rem;
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
.competition-dialog {
  border-radius: 12px;
}

.dialog-header {
  text-align: center;
  padding: 20px 0 15px;
  background: linear-gradient(135deg, #4a5568 0%, #718096 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.form-title {
  font-size: 1.5rem;
  margin: 0;
}

.form-tips {
  color: #718096;
  font-size: 0.9rem;
  margin: 8px 0 0;
}

.required {
  color: #f56565;
}

/* 表单元素样式 */
.competition-form {
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

/* ================= 分页样式 ================= */
.custom-pagination {
  margin-top: 20px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: flex-end;
}

/* 页码按钮样式 */
.custom-pagination /deep/ .el-pager li {
  background: transparent;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  margin: 0 4px;
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  transition: all 0.2s;
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

  .competition-form {
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
</style>
