<template>
  <el-row type="flex" justify="center">

    <!-- 讲座报告卡片 -->
    <el-card id="reportCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">讲座报告</h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="addNewCard"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <el-table :data="competitionRecords" style="width: 100%" border stripe highlight-current-row>
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column type="studentId" label="学生学号" width="80"></el-table-column>
        <el-table-column prop="studentName" label="学生姓名" min-width="120"></el-table-column>
        <el-table-column prop="reportTitle" label="题目" min-width="180"></el-table-column>
        <el-table-column prop="reporter" label="报告人" min-width="150"></el-table-column>
        <el-table-column prop="reportDate" label="报告时间" min-width="150"></el-table-column>
        <!--        <el-table-column prop="proofMaterial" label="证明材料" min-width="150"></el-table-column>-->
        <el-table-column prop="reportAdmitTime" label="报告提交时间" min-width="150"></el-table-column>
        <el-table-column prop="reportContent" label="内容简介" min-width="150"></el-table-column>
        <el-table-column prop="reportLink" label="链接" min-width="150"></el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" min-width="150"></el-table-column>
        <el-table-column prop="auditTime" label="审核时间" min-width="150"></el-table-column>
        <el-table-column prop="auditRemark" label="审核意见" min-width="150"></el-table-column>
        <el-table-column prop="nickName" label="审核人姓名" min-width="150"></el-table-column>
        <el-table-column label="报告文件照片下载" width="120">
          <template slot-scope="scope">
            <el-button type="text" @click="showDetails(scope.row)" style="color: #42b983;">下载报告</el-button>
            <el-button type="text" @click="showDetails(scope.row)" style="color: #42b983;">下载图片</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
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

    <transition name="fade">
      <el-dialog :visible.sync="showSecondCard" id="newCard" style="width: 100%; margin-top: 2vh;margin-left: 1%" @close="closeCard" >
        <div style="display: flex; align-items: center; justify-content: center;">
          <h1>报告填写</h1>
        </div>
        <div style="display: flex; flex-direction: column;">
          <el-form ref="form" :model="formData" label-width="120px" label-position="right">
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">题目</span>
                <el-input v-model="formData.reportTitle" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">报告人</span>
                <el-input v-model="formData.reporter" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">时间</span>
                <el-date-picker v-model="formData.reportDate" type="datetime" style="width: 100%; flex: 1;"></el-date-picker>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">讲座简介</span>
                <el-input v-model="formData.reportContent" type="textarea" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">链接</span>
                <el-input v-model="formData.reportLink" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;height: auto">报告心得体会上传</span>
                <input type="file" @change="onFileChange" accept="*/*" />
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;width:70px">图片上传</span>
                <input type="file" @change="onImageChange" accept="image/*" />
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center; justify-content: right;">
                <el-button type="primary" @click="submitForm" style="margin-right: 3vh">提交</el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </el-dialog>
    </transition>
  </el-row>
</template>

<script>
import axios from "axios";
import {upLoad, fetchLectureReportRecords} from "@/api/student/letcure";
import {formatDate} from "@/utils";
export default {
  data() {
    return {
      competitionRecords: [],// 存储后端返回的讲座报告记录数据
      showSecondCard: false,
      newCardInfo: '',
      selectedFile: null,
      uploadMessage: null,
      reportFeeling: null,
      picture: null,
      formData: {
        reportTitle: '',
        reporter: '',
        reportDate: '',
        reportContent: '',
        reportLink: '',
        //学期
        semester: '2',
      },
    };
  },
  mounted() {
    this.fetchLectureReportRecords();  // 在页面加载时获取数据
  },
  methods: {
    addNewCard() {
      this.showSecondCard = true;
    },
    closeCard() {
      this.showSecondCard = false;
    },
    onFileChange(e) {
      // 当用户选择文件时，更新file变量
      this.reportFeeling = e.target.files[0];
    },
    onImageChange(e) {
      // 当用户选择图片时，更新images数组
      this.picture = Array.from(e.target.files);
    },
    async fetchLectureReportRecords(queryParams = {}, currentPage = 1, pageSize = 10) {
      this.isLoading = true; // 设置为加载状态
      try {
        const data = await fetchLectureReportRecords({
          ...queryParams,
          pageNum: currentPage,
          pageSize: pageSize
        });
        console.log(data);
        console.log(data.data);
        this.competitionRecords = data.data || []; // 假设后端返回的数据格式包含 rows
        this.totalRecords = data.total || 0;       // 假设返回总记录数 total
      } catch (error) {
        console.error("Error fetching competition records:", error);
      } finally {
        this.isLoading = false; // 无论成功还是失败，结束加载状态
      }
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          console.log('表单数据:', this.formData.reportDate);
          //将本地时间转为 ISO 格式的 UTC 时间
          // if (this.formData.reportDate && !isNaN(Date.parse(this.formData.reportDate))) {
          //   this.formData.reportDate = new Date(`${this.formData.reportDate} UTC`).toISOString();
          // }
          const formData = new FormData();
          const json = JSON.stringify(this.formData);
          formData.append('studentLectureReport', json);
          formData.append('reportFeeling', this.reportFeeling);
          formData.append('picture', this.picture[0]);
          // 在这里编写提交表单的逻辑，例如将表单数据发送到后端
          console.log('表单数据:', this.formData);
          console.log('传递后端数据:', formData);
          // 可以使用 axios 或 fetch 发送请求
          // 例如：
          upLoad(formData).then(response => {
            console.log("+++++++++", response);
          })
            .catch(error => {
              console.error(error);
            });
        } else {
          this.$message.error('请填写完整表单信息');
        }
      });
    },
  }
};
</script>

<style>
h1 {
  color: #333;
}
input, button {
  margin: 10px;
}
.el-form-item__label {
  text-align: right;
}
.form-item-label {
  display: inline-block;
  height: 32px;
  line-height: 32px;
  margin-right: 10px;
}
</style>
