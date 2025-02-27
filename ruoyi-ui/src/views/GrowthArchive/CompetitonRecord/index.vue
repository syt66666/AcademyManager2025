<template>
  <el-row type="flex" justify="center" style="margin-top: 4vh;">
    <!-- 竞赛列表卡片 -->
    <el-card id="reportCard" style="width: 48%; margin-top: 2vh; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);">
      <div style="display: flex; align-items: center; justify-content: center;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">科创竞赛</h1>
        <el-button
          type="primary"
          icon="el-icon-plus"
          circle
          size="medium"
          style="position: absolute; margin-left: 45%; background-color: #42b983; border-color: #42b983; box-shadow: 0 4px 8px rgba(66, 185, 131, 0.2);"
          @click="addNewCard">
        </el-button>
      </div>
      <div style="display: flex; justify-content: center;">
        <el-table :data="tableData" style="width: 100%" border>
          <el-table-column type="index" label="序号" width="80"></el-table-column>
          <el-table-column prop="title" label="标题" min-width="200"></el-table-column>
          <el-table-column label="详情" width="120">
            <template slot-scope="scope">
              <el-button type="text" @click="showDetails(scope.row)" style="color: #42b983;">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 竞赛填写卡片 -->
    <transition name="fade">
      <el-card v-if="showSecondCard" id="newCard" style="width: 48%; margin-top: 2vh; margin-left: 1%; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);">
        <div style="display: flex; align-items: center; justify-content: center;">
          <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">竞赛填写</h1>
          <el-button
            icon="el-icon-d-arrow-left"
            size="medium"
            style="position: absolute; margin-right: 40%; color: #42b983;"
            @click="closeCard">
            收回
          </el-button>
        </div>
        <div style="display: flex; flex-direction: column; padding: 20px;">
          <el-form ref="form" :model="formData" label-width="120px" label-position="right" style="font-size: 16px;">

            <el-form-item label="竞赛名称">
              <el-input v-model="formData.competitionName" placeholder="请输入竞赛名称" style="width: 100%;"></el-input>
            </el-form-item>

            <el-form-item label="竞赛级别">
              <el-select v-model="formData.competitionLevel" placeholder="请选择竞赛级别" style="width: 100%;">
                <el-option label="院级" value="院级"></el-option>
                <el-option label="校级" value="校级"></el-option>
                <el-option label="省级" value="省级"></el-option>
                <el-option label="国家级" value="国家级"></el-option>
                <el-option label="国际级" value="国际级"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="奖项">
              <el-select v-model="formData.awardLevel" placeholder="请选择奖项" style="width: 100%;">
                <el-option label="特等奖" value="特等奖"></el-option>
                <el-option label="一等奖" value="一等奖"></el-option>
                <el-option label="二等奖" value="二等奖"></el-option>
                <el-option label="三等奖" value="三等奖"></el-option>
                <el-option label="优秀奖" value="优秀奖"></el-option>
                <el-option label="未获奖" value="未获奖"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="折合分数">
              <el-input v-model="formData.scholarshipPoints" placeholder="请输入折合分数" style="width: 100%;"></el-input>
            </el-form-item>

            <el-form-item label="图片上传">
              <input type="file" @change="onImageChange" accept="image/*" style="width: 100%;"/>
            </el-form-item>

            <el-form-item>
              <div style="display: flex; justify-content: flex-end;">
                <el-button type="primary" @click="submitForm" style="margin-right: 3vh; background-color: #42b983; border-color: #42b983;">提交</el-button>
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
import { addCompetitionRecord } from "@/api/student/competition"; // 确保你有正确的 API 文件

export default {
  data() {
    return {
      showSecondCard: false,
      picture: null,
      formData: {
        competitionName: '', //名称
        competitionLevel: '', //级别
        awardLevel: '', //几等奖
        scholarshipPoints: '', //折合分数
        proofMaterial: '', //文件
        semester: '2',
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
    onImageChange(e) {
      this.picture = Array.from(e.target.files);  // 保存上传的图片
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const formData = new FormData();

          // 构建表单数据对象
          const competitionData = {
            competitionName: this.formData.competitionName,
            competitionLevel: this.formData.competitionLevel,
            awardLevel: this.formData.awardLevel,
            scholarshipPoints: this.formData.scholarshipPoints,
            semester: this.formData.semester,
          };

          // 将表单数据对象转换为 JSON 字符串
          const stuCompetitionRecord = JSON.stringify(competitionData);

          // 将 JSON 字符串作为表单数据的一部分添加到 FormData
          formData.append('StuCompetitionRecord', stuCompetitionRecord);

          // 处理图片上传
          if (this.picture && this.picture.length > 0) {
            formData.append('proofMaterial', this.picture[0]);
          }

          // 调用 API 提交数据
          addCompetitionRecord(formData).then(response => {
            console.log("提交成功:", response);
            // 成功后清空表单
            this.$message.success("提交成功！");
            this.formData = { // 清空表单
              competitionName: '',
              competitionLevel: '',
              awardLevel: '',
              scholarshipPoints: '',
              proofMaterial: '',
              semester: '2',
            };
          }).catch(error => {
            console.error("提交失败:", error);
            this.$message.error("提交失败，请重试！");
          });
        } else {
          this.$message.error('请填写完整表单信息');
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

.el-form-item__label {
  text-align: right;
}

.form-item-label {
  display: inline-block;
  height: 32px;
  line-height: 32px;
  margin-right: 10px;
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

.el-button--primary {
  background-color: #42b983;
  border-color: #42b983;
}

.el-input,
.el-select {
  font-size: 14px;
  border-radius: 4px;
}
</style>
