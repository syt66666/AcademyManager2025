<template>
  <el-row type="flex" justify="center" style="margin-top: 4vh;">
    <el-card id="reportCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <!-- 头部区域保持不变 -->
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">文体活动</h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="openDialog"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="activityRecords" style="width: 100%" border stripe highlight-current-row>
        <!-- 表格列定义保持不变 -->
        <el-table-column type="index" label="序号" width="50"></el-table-column>
<!--        <el-table-column prop="studentId" label="学号" min-width="120"></el-table-column>-->
        <el-table-column prop="activityName" label="活动名称" min-width="150"></el-table-column>
        <el-table-column prop="activityLevel" label="活动级别" min-width="80"></el-table-column>
        <el-table-column prop="awardLevel" label="活动奖项" min-width="80"></el-table-column>
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
            <el-tag v-if="scope.row.auditStatus === '未审核'" type="warning">{{ scope.row.auditStatus }}</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '已通过'" type="success">{{ scope.row.auditStatus }}</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '未通过'" type="danger">{{ scope.row.auditStatus }}</el-tag>
            <el-tag v-else>未知状态</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" min-width="150"></el-table-column>
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
<!--        <el-form-item label="折合分数" prop="scholarshipPoints">-->
<!--          <el-input-number-->
<!--            v-model="formData.scholarshipPoints"-->
<!--            :min="0"-->
<!--            :max="100"-->
<!--            style="width: 100%;"-->
<!--          ></el-input-number>-->
<!--        </el-form-item>-->
        <el-form-item label="图片上传" prop="proofMaterial">
          <imageUpload
            v-model="formData.proofMaterial"
            :limit="1"
            :fileSize="5"
            :fileType="['png','jpg','jpeg']"
            :isShowTip="true"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm"
                     style="float: right; background-color: #42b983; border-color: #42b983;">提交
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-row>
</template>

<script>
import { listActivity, addActivity } from "@/api/system/activity";
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
        // scholarshipPoints: 0,
        proofMaterial: '',
        semester: '',
        awardDate: '',
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
        // scholarshipPoints: [
        //   { type: 'number', required: true, message: '请输入有效分数', trigger: 'blur' }
        // ]
      }
    };
  },

  methods: {
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

    // 对话框控制
    openDialog() {
      this.showDialog = true;
    },

    closeDialog() {
      this.showDialog = false;
    },

    // 表单提交
    async submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          try {
            // 用户信息验证
            if (!this.$store.state.user?.name) {
              this.$message.error('用户信息获取失败');
              return;
            }
            // 构造提交参数
            const params = {
              ...this.formData,
              // scholarshipPoints: Number(this.formData.scholarshipPoints),
              studentId: this.$store.state.user.name,
              semester:this.activeSemester,
            };
            const response = await addActivity(params);
            if (response.code === 200) {
              this.$message.success('提交成功');
              this.fetchActivityRecords();
              this.closeDialog();
              Object.assign(this.formData, this.$options.data().formData);
            }
          } catch (error) {
            console.error('提交失败:', error);
            this.$message.error('提交失败: ' + (error.message || '服务器错误'));
          }
        }
      });
    }
  }
};
</script>

<style scoped>

h1 {
  color: #333;
}

input, button {
  margin: 10px;
}

</style>
