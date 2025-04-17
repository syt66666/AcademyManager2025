<template>
  <div class="container">
    <div class="main-container">
      <!-- 顶部标题栏 -->
      <div class="nav">
        <div class="nav-content">
          <h2>
            <span class="competition-icon">🏆</span>
            科创竞赛记录
            <span class="current-semester">{{ activeSemester }} 竞赛成果</span>
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

      <!-- 竞赛表格 -->
      <div class="competition-table-card">
        <el-table
          :data="competitionRecords"
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

          <!-- 竞赛名称 -->
          <el-table-column prop="competitionName" label="竞赛名称" min-width="120" >
            <template v-slot="scope">
              <div class="competition-name">
                <i class="el-icon-trophy name-icon"></i>
                <span class="name-text">{{ scope.row.competitionName }}</span>
              </div>
            </template>
          </el-table-column>

          <!-- 竞赛级别 -->
          <el-table-column prop="competitionLevel" label="竞赛级别" width="120" align="center">
            <template v-slot="scope">
              <el-tag
                :type="getLevelTagType(scope.row.competitionLevel)"
                effect="light"
                class="level-tag"
              >
                {{ scope.row.competitionLevel }}
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
              <el-dropdown trigger="click"
                           @command="handleFileCommand"
                           @click.native.stop
                           :disabled="!scope.row.proofMaterial || scope.row.proofMaterial.length === 0">
                <el-button type="primary"
                           size="mini"
                           plain
                           @click.stop
                           :disabled="!scope.row.proofMaterial || scope.row.proofMaterial.length === 0">
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
          @pagination="fetchCompetitionRecords"
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

    <!-- 竞赛新增对话框 -->
    <el-dialog
      :visible.sync="showDialog"
      title="竞赛信息填报"
      width="580px"
      class="competition-dialog"
      @close="closeDialog"
    >
      <div class="dialog-header">
        <h3 class="form-title">{{ isEdit ? '编辑竞赛记录' : '新增竞赛记录' }}</h3>
        <p class="form-tips">请填写本学期参与的竞赛信息（带<span class="required">*</span>为必填项）</p>
      </div>

      <el-form
        ref="form"
        :model="formData"
        :rules="rules"
        label-width="110px"
        class="competition-form"
      >
        <!-- 竞赛名称 -->
        <el-form-item label="竞赛名称" prop="competitionName">
          <el-input
            v-model="formData.competitionName"
            placeholder="请输入完整竞赛名称"
            class="custom-input"
          >
            <i slot="prefix" class="el-icon-trophy name-icon"></i>
          </el-input>
        </el-form-item>

        <!-- 竞赛级别 -->
        <el-form-item label="竞赛级别" prop="competitionLevel">
          <el-select
            v-model="formData.competitionLevel"
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
import axios from "axios";
import {addRecord, listRecord, delRecord, updateRecord, checkCompetitionUnique} from "@/api/student/competition";
import store from "@/store";

export default {
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
      isEdit: false,
      currentCompetitionId: null,
      fileList: [],
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      loading: false,
      previewImage: '',
      currentDownloadFile: '',
      competitionRecords: [],
      queryParams: {},
      currentPage: 1,
      pageSize: 10,
      totalRecords: 0,
      showDialog: false,
      activeSemester: '',
      formData: {
        competitionName: '',
        competitionLevel: '',
        awardLevel: '',
        awardDate: '',
        proofMaterial: '',
      },
      rules: {
        competitionName: [{required: true, message: '竞赛名称不能为空', trigger: 'blur'}],
        competitionLevel: [{required: true, message: '请选择竞赛级别', trigger: 'change'}],
        awardLevel: [{required: true, message: '请选择奖项', trigger: 'change'}],
        awardDate: [{required: true, message: '请选择获奖日期', trigger: 'change'}]
      },
    };
  },
  mounted() {
    this.activeSemester = this.$route.query.semester || '未知学期';
    this.fetchCompetitionRecords();
  },
  methods: {
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
    handleEditDraft(row) {
      this.handleEdit(row);
      localStorage.removeItem(this.getDraftKey());
    },
    handleEdit(row) {
      const rawData = JSON.parse(JSON.stringify(row));
      this.formData = {...rawData};
      this.isEdit = true;
      this.currentCompetitionId = rawData.competitionId;
      this.showDialog = true;

      const proofMaterial = this.parseMaterial(rawData.proofMaterial);
      this.fileList = proofMaterial.map((path, index) => ({
        uid: Date.now() + index,
        name: path.split('/').pop(),
        url: `${process.env.VUE_APP_BASE_API}/profile/${encodeURIComponent(path)}`,
        status: 'success',
        isOld: true,
        path: path
      })).filter(Boolean);

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
      return done(true); // 新增此行
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该记录吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        const response = await delRecord(row.id);
        if (response.code === 200) {
          this.$message.success('删除成功');
          await this.fetchCompetitionRecords();
          localStorage.removeItem(this.getDraftKey());
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`删除失败: ${error.message}`);
        }
      }
    },
    async handleSave() {
      await this.submitData('未提交');
    },
    async handleSubmit() {
      await this.submitData('未审核');
    },
    getDraftKey() {
      return `competition_draft_${this.$store.state.user.name}_${this.activeSemester}`;
    },
    openDialog() {
      this.isEdit = false;
      this.currentCompetitionId = null;
      this.formData = this.initFormData();
      this.showDialog = true;
      const draft = localStorage.getItem(this.getDraftKey());
      if (draft) this.formData = JSON.parse(draft);
    },
    initFormData() {
      return {
        competitionName: '',
        competitionLevel: '',
        awardLevel: '',
        awardDate: '',
        proofMaterial: [],
        auditStatus: '未提交',
        auditTime: null,
        auditRemark: '',
        semester: this.activeSemester
      };
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
    generateFileName(filePath) {
      const originalName = filePath.split('/').pop() || '证明材料';
      const timestamp = new Date().getTime();
      const ext = originalName.split('.').pop() || 'jpg';
      return `${originalName.split('.')[0]}_${timestamp}.${ext}`;
    },
    getFullUrl(filePath) {
      return `${process.env.VUE_APP_BASE_API}/profile/${filePath}`;
    },
    handlePreview(filePath) {
      try {
        const paths = typeof filePath === 'string' ? JSON.parse(filePath) : filePath;
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
    closeDialog() {
      this.showDialog = false;
      this.fileList = [];
    },
    async submitData(status) {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          const originalRecord = this.competitionRecords.find(
            item => item.id === this.currentCompetitionId
          );
          const isKeyFieldChanged = !originalRecord ||
            this.formData.competitionName !== originalRecord.competitionName ||
            this.formData.competitionLevel !== originalRecord.competitionLevel ||
            this.formData.awardLevel !== originalRecord.awardLevel;

          if (isKeyFieldChanged) {
            try {
              const checkRes = await checkCompetitionUnique({
                studentId: this.$store.state.user.name,
                competitionName: this.formData.competitionName,
                competitionLevel: this.formData.competitionLevel,
                awardLevel: this.formData.awardLevel,
                semester: this.activeSemester
              });
              if (checkRes.code !== 200) return this.$message.error('已存在相同活动记录,不可重复添加');
            } catch (error) {
              return this.$message.error(`校验失败: ${error.message}`);
            }
          }

          const existingPaths = this.fileList
            .filter(file => file.isOld)
            .map(file => file.path);
          const newFiles = this.fileList
            .filter(file => !file.isOld)
            .map(file => file.raw);
          const formData = new FormData();

          const recordData = {
            competitionId: null,
            competitionName: this.formData.competitionName,
            competitionLevel: this.formData.competitionLevel,
            awardLevel: this.formData.awardLevel,
            semester: this.activeSemester,
            studentId: store.state.user.name,
            auditStatus: status,
            auditTime: null,
            auditRemark: '',
            awardDate: this.formData.awardDate,
            existingProofMaterial: existingPaths,
          };

          if (this.currentCompetitionId) {
            recordData.competitionId = this.currentCompetitionId
          }

          const recordBlob = new Blob([JSON.stringify(recordData)], {type: "application/json"});
          formData.append("record", recordBlob);
          // 调试点：打印当前 fileList
          console.log('[DEBUG] 提交时的 fileList:',
            this.fileList.map(file => ({
              name: file.name,
              size: file.size,
              isOld: file.isOld,
              raw: !!file.raw // 检查是否有 raw 属性
            })))

          this.fileList.forEach((file) => formData.append("proofMaterial", file.raw));

          const config = {
            headers: {
              "Authorization": "Bearer " + localStorage.getItem("token"),
              "Content-Type": "multipart/form-data"
            }
          };
          const apiMethod = this.currentCompetitionId ? updateRecord : addRecord;

          apiMethod(formData, config)
            .then(() => {
              this.$message.success(this.currentCompetitionId ? "更新成功！" : "提交成功！");
              this.fetchCompetitionRecords();
              this.closeDialog();
            })
            .catch(error => {
              this.$message.error(`操作失败：${error.message}`);
            });
        }
      });
    },
    async fetchCompetitionRecords() {
      try {
        const response = await listRecord({
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          semester: this.activeSemester,
          studentId: store.state.user.name
        });
        if (response && response.code === 200) {
          this.competitionRecords = response.rows.map(item => ({
            ...item,
            id: item.competitionId,
            auditStatus: item.auditStatus,
            proofMaterial: this.parseMaterial(item.proofMaterial)
          }));
          this.totalRecords = response.total || 0;
        } else {
          this.$message.error(response.msg || '数据加载失败');
        }
      } catch (error) {
        console.error("数据加载失败:", error);
        this.$message.error('数据加载异常');
      }
    },
    headerStyle() {
      return {
        backgroundColor: '#EBF4FF',
        color: '#2B6CB0',
        fontWeight: "600",
      };
    }
  }
};
</script>

<style scoped>
/* ================= 全局容器样式 ================= */
.container {
  display: flex;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); /* 柔和的渐变背景 */
  padding: 20px;
}

.main-container {
  background: #ffffff;
  border-radius: 1.5rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  max-width: 1440px;
  overflow: hidden;
  padding: 2rem;
  position: relative;
  width: 100%;
  margin-left: 100px;
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

.competition-icon {
  font-size: 1.5em;
  margin-right: 0.5rem;
}

.current-semester {
  font-size: 1.2rem;
  opacity: 0.9;
}

/* ================= 表格相关样式 ================= */
.competition-table-card {
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

.competition-name {
  display: flex;
  align-items: center;
  padding: 8px 0;
  gap: 8px; /* 与 notebook 相同的图标-文字间距 */
}

.name-icon {
  /* 移除原颜色，替换为渐变 */
  background: linear-gradient(135deg, #409EFF 20%, #67C23A 80%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent !important; /* 重要：覆盖 Element 默认颜色 */

  /* 调整阴影匹配渐变风格 */
  filter:
    drop-shadow(0 0.5px 0.5px rgba(64,158,255,0.15)) /* 蓝色投影 */
    drop-shadow(0 0.5px 0.5px rgba(103,194,58,0.15)) /* 绿色投影 */
    drop-shadow(0 0 0.5px rgba(255,255,255,0.3)); /* 保留内白边 */

  /* 其他原有属性保持 */
  font-size: 16px;
  transform: scale(0.95);
  transition: all 0.2s;
  position: relative; /* 为伪元素定位准备 */
}

.name-icon:hover {
  /* 悬浮时增强渐变和阴影 */
  filter:
    drop-shadow(0 1px 1px rgba(64,158,255,0.2))
    drop-shadow(0 1px 1px rgba(103,194,58,0.2))
    drop-shadow(0 0 1px rgba(255,255,255,0.4));
  transform: scale(1);
}

/* 调整伪元素边框为渐变色 */
.name-icon::after {
  border: 0.5px solid rgba(64,158,255,0.15); /* 与主色协调 */
  mix-blend-mode: overlay; /* 混合模式增强质感 */
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
.competition-dialog {
  border-radius: 12px;
}

.competition-dialog /deep/ .el-dialog__header {
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
.competition-form {
  padding: 0 30px 20px;
}

/* 操作按钮样式 */
.form-actions {
  margin-top: 25px;
  text-align: center;
}

/* ================= 分页样式 ================= */
/* 修改分页容器样式 */
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
