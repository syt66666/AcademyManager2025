<template>
  <el-row type="flex" justify="center" style="margin-top: 4vh;">
    <!-- 活动列表卡片 -->
    <el-card id="reportCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">文体活动</h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="openDialog"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <el-table :data="activityRecords" style="width: 100%" border stripe highlight-current-row>
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="studentId" label="学号" min-width="120"></el-table-column>
        <el-table-column prop="activityName" label="活动名称" min-width="180"></el-table-column>
        <el-table-column prop="activityLevel" label="活动级别" min-width="150"></el-table-column>
        <el-table-column prop="awardLevel" label="活动奖项" min-width="150"></el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" min-width="150">
          <template v-slot:default="scope">
            <el-tag v-if="scope.row.auditStatus === '未审核'" type="warning">{{ scope.row.auditStatus }}</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '已通过'" type="success">{{ scope.row.auditStatus }}</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '未通过'" type="danger">{{ scope.row.auditStatus }}</el-tag>
            <el-tag v-else>未知状态</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" min-width="150"></el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" min-width="150"></el-table-column>
        <el-table-column prop="proofMaterial" label="图片" min-width="150">
          <template v-slot:default="scope">
            <img
              :src="scope.row.proofMaterial"
              alt="图片"
              style="width: 50px; height: 50px; cursor: pointer;"
              v-if="scope.row.proofMaterial"
              @click="handleImageClick(scope.row.proofMaterial)"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="dialogVisible" title="查看图片" width="50%">
        <img :src="currentImage" alt="放大图片" style="width: 100%; height: auto;" />
      </el-dialog>

      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        :total="totalRecords"
        :page-sizes="[10, 20, 30, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="text-align: center; margin-top: 10px;"
      />
    </el-card>

    <!-- 活动填写对话框 -->
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
          </el-select>
        </el-form-item>
        <el-form-item label="奖项" prop="awardLevel">
          <el-select v-model="formData.awardLevel" placeholder="请选择奖项" style="width: 100%;">
            <el-option label="一等奖" value="一等奖"></el-option>
            <el-option label="二等奖" value="二等奖"></el-option>
            <el-option label="三等奖" value="三等奖"></el-option>
            <el-option label="参与奖" value="参与奖"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="折合分数" prop="scholarshipPoints">
          <el-input-number
            v-model="formData.scholarshipPoints"
            :min="0"
            :max="100"
            style="width: 100%;"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="图片上传">
          <input type="file" @change="onImageChange" accept="image/*" style="width: 100%;"/>
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

export default {
  data() {
    return {
      dialogVisible: false,
      currentImage: '',
      activityRecords: [],
      queryParams: {},
      currentPage: 1,
      pageSize: 10,
      totalRecords: 0,
      showDialog: false,
      isLoading: false,
      picture: null,
      formData: {
        activityName: '',
        activityLevel: '',
        awardLevel: '',
        scholarshipPoints: 0,
        proofMaterial: '',
        semester: '2'
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
        scholarshipPoints: [
          { type: 'number', required: true, message: '请输入有效分数', trigger: 'blur' }
        ]
      }
    };
  },
  mounted() {
    this.fetchActivityRecords();
  },
  methods: {
    handleImageClick(imageUrl) {
      this.currentImage = imageUrl;
      this.dialogVisible = true;
    },

    async fetchActivityRecords() {
      this.isLoading = true;
      try {
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          ...this.queryParams
        };

        const response = await listActivity(params);
        if (response.code === 200) {
          this.activityRecords = response.rows;
          this.totalRecords = response.total;
        }
      } catch (error) {
        console.error("获取活动记录失败:", error);
      } finally {
        this.isLoading = false;
      }
    },

    handleSizeChange(size) {
      this.pageSize = size;
      this.fetchActivityRecords();
    },

    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchActivityRecords();
    },

    openDialog() {
      this.showDialog = true;
    },

    closeDialog() {
      this.showDialog = false;
    },

    onImageChange(e) {
      this.picture = e.target.files[0];
    },

    async submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          const formData = new FormData();
          formData.append('activityName', this.formData.activityName);
          formData.append('activityLevel', this.formData.activityLevel);
          formData.append('awardLevel', this.formData.awardLevel);
          formData.append('scholarshipPoints', this.formData.scholarshipPoints);
          formData.append('semester', this.formData.semester);

          if (this.picture) {
            formData.append('proofMaterial', this.picture);
          }

          try {
            const response = await addActivity(formData);
            if (response.code === 200) {
              this.$message.success('提交成功');
              this.fetchActivityRecords();
              this.closeDialog();
              this.formData = this.$options.data().formData; // 重置表单
            }
          } catch (error) {
            console.error('提交失败:', error);
            this.$message.error('提交失败');
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


/* 背景和卡片的阴影效果 */
#reportCard,
#newCard {
  transition: all 0.3s ease;
}

#reportCard:hover,
#newCard:hover {
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.2);
}


</style>>
