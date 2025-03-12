<template>
  <el-row type="flex" justify="center" style="margin-top: 4vh;">
    <el-card id="reportCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <!-- 头部区域 -->
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">文体活动</h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="openDialog"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="activityRecords" style="width: 100%" border stripe highlight-current-row>
        <el-table-column type="index" label="序号"></el-table-column>
        <el-table-column prop="activityName" label="活动名称" min-width="100"></el-table-column>
        <el-table-column prop="activityLevel" label="活动级别" ></el-table-column>
        <el-table-column prop="awardLevel" label="活动奖项" ></el-table-column>
        <el-table-column prop="awardDate" label="获奖日期" min-width="100"></el-table-column>
        <el-table-column label="证明材料" width="120">
          <template v-slot:default="scope">
            <div class="proof-material-cell">
              <el-link
                type="primary"
                :underline="false"
                @click="handlePreview(scope.row.proofMaterial)"
                style="margin-right: 10px;"
              >
                <i class="el-icon-view"></i> 预览
              </el-link>
              <el-button
                type="primary"
                icon="el-icon-download"
                size="mini"
                @click="downloadFiles(scope.row.proofMaterial)"
                :disabled="!scope.row.proofMaterial"
              >下载
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" min-width="80">
          <template v-slot:default="scope">
            <el-tag v-if="scope.row.auditStatus === '未审核'" type="warning">未审核</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '已通过'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '未通过'" type="danger">未通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '未提交'" type="info">未提交</el-tag>
            <el-tag v-else type="info">未知状态</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button
              v-if="scope.row.auditStatus === '未通过'"
              type="text"
              size="mini"
              @click="handleEditDraft(scope.row)"
            >重新提交</el-button>
            <template v-if="scope.row.auditStatus === '未提交'">
              <el-button
                type="text"
                size="mini"
                @click="handleEditDraft(scope.row)"
              >编辑草稿</el-button>
              <el-button
                type="text"
                size="mini"
                style="color: #F56C6C;"
                @click="handleDelete(scope.row)"
              >删除</el-button>
            </template>

            <el-tag
              v-if="['未审核', '已通过'].includes(scope.row.auditStatus)"
              type="info"
              size="mini"
            >不可修改</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" min-width="100"></el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" min-width="150"></el-table-column>

      </el-table>

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

      <!-- 分页组件 -->
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalRecords"
        :page-sizes="[10, 20, 30, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="text-align: center; margin-top: 10px;"
      />
    </el-card>

    <!-- 新增活动对话框 -->
    <el-dialog :visible.sync="showDialog" title="活动填写" width="50%" @close="closeDialog">
      <el-form ref="form" :model="formData" :rules="rules" label-width="120px" style="padding: 20px;">
        <el-form-item label="活动名称" prop="activityName">
          <el-input v-model="formData.activityName" placeholder="请输入活动名称" style="width: 100%;"></el-input>
        </el-form-item>
        <el-form-item label="活动级别" prop="activityLevel">
          <el-select v-model="formData.activityLevel" placeholder="请选择活动级别" style="width: 100%;">
            <el-option label="院级" value="院级"></el-option>
            <el-option label="校级" value="校级"></el-option>
            <el-option label="省级" value="省级"></el-option>
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="国际级" value="国际级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="奖项" prop="awardLevel">
          <el-select v-model="formData.awardLevel" placeholder="请选择奖项" style="width: 100%;">
            <el-option label="特等奖" value="特等奖"></el-option>
            <el-option label="一等奖" value="一等奖"></el-option>
            <el-option label="二等奖" value="二等奖"></el-option>
            <el-option label="三等奖" value="三等奖"></el-option>
            <el-option label="参与奖" value="优秀奖"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="获奖日期" prop="awardDate">
          <el-date-picker
            clearable
            v-model="formData.awardDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择获奖日期"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="图片上传" prop="proofMaterial">
          <el-upload
            multiple
            :limit="5"
            :file-list="fileList"
            :auto-upload="false"
            :on-change="handleFileChange"
          >
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">最多上传5个文件，单个不超过10MB</div>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button
            type="info"
            @click="handleSave"
            style="margin-right: 10px;"
          >保存草稿</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            style="background-color: #42b983; border-color: #42b983;"
          >正式提交</el-button>
        </el-form-item>

      </el-form>
    </el-dialog>
  </el-row>
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
      fileList: [], // 已上传的文件列表
      previewVisible: false,
      currentDownloadFile: '',
      previewImages: [],
      currentPreviewIndex: 0,
      isEdit: false,
      currentActivityId: null,
      baseUrl: process.env.VUE_APP_BASE_API,
      activeSemester:"",
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
          { required: true, message: '活动名称不能为空', trigger: 'blur' }
        ],
        activityLevel: [
          { required: true, message: '请选择活动级别', trigger: 'change' }
        ],
        awardLevel: [
          { required: true, message: '请选择奖项', trigger: 'change' }
        ],
        awardDate: [
          { required: true, message: '请选择获奖日期', trigger: 'change' }
        ]
      }
    };
  },

  methods: {
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
        if (paths.length >=1) {
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
    handleFileChange(file, fileList) {
      this.fileList = fileList.slice(-5); // 保持最多5个文件
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
          orderByColumn: 'apply_time', // 新增排序字段
          isAsc: 'desc',                // 降序排列
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

// 新增材料解析方法
    parseMaterial(material) {
      try {
        // 尝试解析为 JSON 数组
        return typeof material === 'string' ? JSON.parse(material) : material;
      } catch (e) {
        // 如果解析失败，假设是单个文件路径
        return material ? [material] : [];
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

    // 处理编辑未通过记录
    handleEdit(row) {
      this.formData = {
        ...row,
        auditTime:null,
        auditRemark:"",
        awardDate: row.awardDate ? new Date(row.awardDate) : null
      };
      this.isEdit = true;
      this.currentActivityId = row.activityId;
      this.showDialog = true;
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
          awardDate: this.formData.awardDate
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
.el-button--text:hover {
  background-color: rgba(245, 108, 108, 0.1);
}

h1 {
  color: #333;
}

input, button {
  margin: 10px;
}

</style>
