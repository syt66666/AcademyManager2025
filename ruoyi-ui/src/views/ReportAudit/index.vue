<template>
  <el-row type="flex" justify="center">
    <el-card id="reportCard" style="width: 48%; margin-top: 2vh;">
      <div style="display: flex; align-items: center; justify-content: center;">
        <h1>报告审核</h1>
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
          <el-table-column prop="name" label="申请人"></el-table-column>
          <el-table-column prop="studentId" label="学号"></el-table-column>
          <el-table-column prop="status" label="状态"></el-table-column>
          <el-table-column label="审核">
            <template slot-scope="scope">
              <el-button type="text" @click="showDetails(scope.row)">审核</el-button>
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
                <span class="form-item-label" style="font-size: 16px;">题目</span>
                <el-input v-model="formData.title" style="width: 100%; flex: 1;"></el-input>
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
                <el-date-picker v-model="formData.time" type="datetime" style="width: 100%; flex: 1;"></el-date-picker>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">讲座简介</span>
                <el-input v-model="formData.introduction" type="textarea" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">连接</span>
                <el-input v-model="formData.link" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">报告心得体会上传</span>
                <el-upload
                  action="/api/upload-experience"
                  :on-success="handleExperienceUploadSuccess"
                  :show-file-list="false"
                  :before-upload="beforeExperienceUpload"
                  :auto-upload="true"
                >
                  <el-button size="small" type="primary">点击上传</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">图片上传</span>
                <el-upload
                  action="/api/upload-image"
                  :on-success="handleImageUploadSuccess"
                  :show-file-list="true"
                  :before-upload="beforeImageUpload"
                  :auto-upload="true"
                  list-type="picture-card"
                >
                  <i class="el-icon-plus"></i>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center; justify-content: right;">
                <el-button type="primary" @click="refuse" style="margin-right: 3vh">拒绝</el-button>
                <el-button type="primary" @click="pass" style="margin-right: 3vh">通过</el-button>
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

export default {
  data() {
    return {
      showSecondCard: false,
      newCardInfo: '',
      selectedFile: null,
      uploadMessage: null,
      formData: {
        title: '',
        reporter: '',
        time: '',
        introduction: '',
        link: '',
        experienceFile: null,
        imageFiles: []
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
    handleExperienceUploadSuccess(response, file, fileList) {
      // 处理报告心得体会上传成功后的逻辑
      console.log('报告心得体会上传成功', response);
    },
    beforeExperienceUpload(file) {
      // 报告心得体会上传前的检查逻辑，例如文件大小、类型检查
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error('文件大小不能超过 2MB!');
        return false;
      }
      this.formData.experienceFile = file;
      return true;
    },
    handleImageUploadSuccess(response, file, fileList) {
      // 处理图片上传成功后的逻辑
      console.log('图片上传成功', response);
    },
    beforeImageUpload(file) {
      // 图片上传前的检查逻辑，例如文件大小、类型检查
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error('文件大小不能超过 2MB!');
        return false;
      }
      this.formData.imageFiles.push(file);
      return true;
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
