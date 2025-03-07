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
        <el-table-column prop="proofMaterial" label="证明材料" min-width="120">
          <template v-slot:default="scope">
            <img
              :src="getImageUrl(scope.row.proofMaterial)"
              alt="活动证明"
              style="width: 50px; height: 50px; cursor: pointer;"
              v-if="scope.row.proofMaterial"
              @click="handleImageClick(scope.row.proofMaterial)"
            />
            <span v-else>无图片</span>
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
            <!-- 原有按钮保持不变 -->
            <el-button
              v-if="scope.row.auditStatus === '未通过'"
              type="text"
              size="mini"
              @click="handleEditDraft(scope.row)"
            >重新提交</el-button>

            <!-- 未提交状态新增删除按钮 -->
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
      <el-dialog :visible.sync="dialogVisible" title="查看图片" width="50%">
        <div style="position: relative;">
          <img :src="getImageUrl(currentImage)" alt="活动证明大图" style="width: 100%; height: auto;" />
          <div style="position: absolute; bottom: 20px; right: 20px;">
            <el-button
              type="primary"
              icon="el-icon-download"
              @click="handleDownload"
              style="background-color: #42b983; border-color: #42b983;">
              下载图片
            </el-button>
          </div>
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
        <!-- 表单字段保持不变 -->
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
          <imageUpload
            v-model="formData.proofMaterial"
            :limit="1"
            :fileSize="5"
            :fileType="['png','jpg','jpeg']"
            :isShowTip="true"
          />
        </el-form-item>
        <!-- 在表单底部添加双按钮 -->
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
      auditStatus: '',
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
      }
    };
  },

  methods: {

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
          semester:this.activeSemester,
          ...this.queryParams
        };

        const response = await listActivity(params);
        if (response.code === 200) {
          this.activityRecords = response.rows || [];
          this.totalRecords = response.total || 0;

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
    // 对话框关闭
    closeDialog() {
      if (!this.isEdit) {
        // 自动保存草稿
        const draftData = {
          ...this.formData,
          proofMaterial: typeof this.formData.proofMaterial === 'object'
            ? this.formData.proofMaterial.url
            : this.formData.proofMaterial
        };
        localStorage.setItem(this.getDraftKey(), JSON.stringify(draftData));
      }
      this.showDialog = false;
      this.isEdit = false;
      this.currentActivityId = null;
      this.$refs.form.resetFields();
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
      this.currentActivityId = row.id;
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
      try {
        // 唯一性校验参数
        const checkParams = {
          studentId: this.$store.state.user.name,
          activityName: this.formData.activityName,
          activityLevel: this.formData.activityLevel,
          awardLevel: this.formData.awardLevel,
          semester: this.activeSemester
        };
        // 编辑时排除自身
        if (this.isEdit) {
          checkParams.excludeId = this.currentActivityId;
        }

        // 执行唯一性校验
        const checkRes = await checkActivityUnique(checkParams);
        if (checkRes.code !== 200) {
          return this.$message.error('已存在相同活动记录，不可重复添加');
        }

        const params = {
          ...this.formData,
          auditTime:null,
          auditRemark:"",

          auditStatus: status,
          studentId: this.$store.state.user.name,
          semester: this.activeSemester,
        };

        // 文件路径处理
        if (typeof params.proofMaterial === 'object') {
          params.proofMaterial = params.proofMaterial.url;
        }

        // API调用逻辑
        const response = this.isEdit
          ? await updateActivity({ ...params, id: this.currentActivityId })
          : await addActivity(params);
        if (response.code === 200) {
          this.$message.success(status === '未提交' ? '保存成功' : '提交成功');
          this.fetchActivityRecords();
          this.closeDialog();
        }
      } catch (error) {
        console.error('操作失败:', error);
        this.$message.error(`操作失败: ${error.message || '服务器错误'}`);
      }
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
