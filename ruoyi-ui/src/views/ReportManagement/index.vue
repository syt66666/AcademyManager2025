<template>
  <el-row type="flex" justify="center">
    <el-card id="reportCard" style="width: 48%; margin-top: 2vh;">
      <div style="display: flex; align-items: center; justify-content: center;">
        <h1>讲座报告</h1>
        <el-button
          type="primary"
          icon="el-icon-plus"
          circle
          size="mini"
          style="position: absolute;margin-left: 45%"
          @click="addNewCard"></el-button>
      </div>
      <div style="display: flex; justify-content: center;">
        <el-table :data="tableData" style="width: 100%">
          <el-table-column type="index" label="序号"></el-table-column>
          <el-table-column prop="title" label="标题"></el-table-column>
          <el-table-column label="详情">
            <template slot-scope="scope">
              <el-button type="text" @click="showDetails(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    <transition name="fade">
      <el-card v-if="showSecondCard" id="newCard" style="width: 48%; margin-top: 2vh;margin-left: 1%">
        <div style="display: flex; align-items: center; justify-content: center;">
          <h1>报告填写</h1>
        <el-button
          icon="el-icon-d-arrow-left"
          size="mini"
          style="position: absolute;margin-right: 40%"
          @click="closeCard">收回</el-button>
        </div>
        <div style="display: flex; flex-direction: column;">
          <el-form ref="form" :model="formData" label-width="120px" label-position="right">
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;width:70px">题目</span>
                <el-input v-model="formData.title" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;width:70px">报告人</span>
                <el-input v-model="formData.reporter" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;width:70px">时间</span>
                <el-date-picker v-model="formData.reportDate" type="datetime" style="width: 100%; flex: 1;"></el-date-picker>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;width:70px">讲座简介</span>
                <el-input v-model="formData.reportContent" type="textarea" style="width: 100%; flex: 1;margin-left: 10px"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;width:70px">连接</span>
                <el-input v-model="formData.link" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;width:70px;height: auto">报告心得体会上传</span>
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
      </el-card>
    </transition>
  </el-row>
</template>

<script>
import axios from "axios";
import {upLoad} from "@/api/student/letcure";
import {formatDate} from "@/utils";
export default {
  data() {
    return {
      showSecondCard: false,
      newCardInfo: '',
      selectedFile: null,
      uploadMessage: null,
      reportFeeling: null,
      picture: null,
      formData: {
        title: '',
        reporter: '',
        reportDate: '',
        reportContent: '',
        link: '',
      },
    };
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
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          //将本地时间转为 ISO 格式的 UTC 时间
          this.formData.reportDate = new Date(`${this.formData.reportDate} UTC`).toISOString();
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
