<template>
  <el-row type="flex" justify="center" style="margin-top: 4vh;">
    <!-- 搜索表单 -->
    <el-form inline style="margin-top: 2vh; width: 70%;">
      <el-form-item label="竞赛名称">
        <el-input v-model="queryParams.competitionName" placeholder="请输入竞赛名称"
                  style="border-radius: 4px;"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search" icon="el-icon-search"
                   style="background-color: #42b983; border-color: #42b983;">查询</el-button>
      </el-form-item>
    </el-form>

    <!-- 竞赛列表卡片 -->
    <el-card id="reportCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">科创竞赛</h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="openDialog"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <el-table :data="competitionRecords" style="width: 100%" border stripe highlight-current-row>
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="competitionId" label="竞赛ID" min-width="120"></el-table-column>
        <el-table-column prop="studentId" label="学号" min-width="120"></el-table-column>
        <el-table-column prop="competitionName" label="竞赛名称" min-width="180"></el-table-column>
        <el-table-column prop="competitionLevel" label="竞赛级别" min-width="150"></el-table-column>
        <el-table-column prop="awardLevel" label="竞赛奖项" min-width="150"></el-table-column>
        <el-table-column prop="scholarshipPoints" label="折合分数" min-width="120"></el-table-column>
        <el-table-column prop="competitionDate" label="竞赛日期" min-width="150"></el-table-column>
        <el-table-column prop="awardDate" label="获奖日期" min-width="150"></el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" min-width="150"></el-table-column>
        <el-table-column label="详情" width="120">
          <template slot-scope="scope">
            <el-button type="text" @click="showDetails(scope.row)" style="color: #42b983;">详情</el-button>
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

    <!-- 竞赛填写弹出对话框 -->
    <el-dialog :visible.sync="showDialog" title="竞赛填写" width="50%" @close="closeDialog">
      <el-form ref="form" :model="formData" :rules="rules" label-width="120px" style="padding: 20px;">
        <!-- 表单项 -->
        <el-form-item label="竞赛名称" prop="competitionName">
          <el-input v-model="formData.competitionName" placeholder="请输入竞赛名称" style="width: 100%;"></el-input>
        </el-form-item>
        <el-form-item label="竞赛级别" prop="competitionLevel">
          <el-select v-model="formData.competitionLevel" placeholder="请选择竞赛级别" style="width: 100%;">
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
            <el-option label="优秀奖" value="优秀奖"></el-option>
            <el-option label="未获奖" value="未获奖"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="折合分数" prop="scholarshipPoints">
          <el-input v-model="formData.scholarshipPoints" placeholder="请输入折合分数" style="width: 100%;"></el-input>
        </el-form-item>
        <el-form-item label="图片上传">
          <input type="file" @change="onImageChange" accept="image/*" style="width: 100%;"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" style="float: right; background-color: #42b983; border-color: #42b983;">提交</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-row>
</template>
<script>
import axios from "axios";
import {addCompetitionRecord, fetchCompetitionRecords} from "@/api/student/competition"; // 确保你有正确的 API 文件

export default {
  data() {
    return {
      competitionRecords: [],// 存储后端返回的竞赛记录数据
      queryParams: {},         // 用于存储查询条件
      currentPage: 1,          // 当前页
      pageSize: 10,            // 每页显示的条数
      totalRecords: 0,         // 总记录数
      showDialog: false, // 控制对话框显示
      isLoading: false,        // 加载状态/
      showSecondCard: false,
      picture: null,
      formData: {
        competitionName: '', //名称
        competitionLevel: '', //级别
        awardLevel: '', //奖项
        scholarshipPoints: '', //折合分数
        proofMaterial: '', //文件
        semester: '2',
      },
      rules: {
        competitionName: [
          {required: true, message: '竞赛名称不能为空', trigger: 'blur'}
        ],
        competitionLevel: [
          {required: true, message: '请选择竞赛级别', trigger: 'change'}
        ],
        awardLevel: [
          {required: true, message: '请选择奖项', trigger: 'change'}
        ],
        scholarshipPoints: [
          {required: true, message: '折合分数不能为空', trigger: 'blur'}
        ]
      }
    };
  },
  mounted() {
    this.fetchCompetitionRecords();  // 在页面加载时获取数据
  },
  methods: {
    openDialog() {
      this.showDialog = true;
    },
    // 关闭对话框
    closeDialog() {
      this.showDialog = false;
    },
    async fetchCompetitionRecords(queryParams = {}, currentPage = 1, pageSize = 10) {
      this.isLoading = true; // 设置为加载状态
      try {
        const data = await fetchCompetitionRecords({
          ...queryParams,
          pageNum: currentPage,
          pageSize: pageSize
        });
        this.competitionRecords = data.rows || []; // 假设后端返回的数据格式包含 rows
        this.totalRecords = data.total || 0;       // 假设返回总记录数 total
      } catch (error) {
        console.error("Error fetching competition records:", error);
      } finally {
        this.isLoading = false; // 无论成功还是失败，结束加载状态
      }
    },

    addNewCard() {
      this.showSecondCard = true;
    },
    closeCard() {
      this.showSecondCard = false;
    },
    onImageChange(e) {
      this.picture = Array.from(e.target.files);  // 保存上传的图片
    },
    showDetails(record) {
      // 显示竞赛详情的逻辑
      console.log("查看竞赛详情:", record);
    },
    andleCurrentChange(page) { // 处理页码变化
      this.currentPage = page;
      this.fetchCompetitionRecords(this.queryParams, this.currentPage, this.pageSize);
    },

    handleSizeChange(size) { // 处理每页显示条数变化
      this.pageSize = size;
      this.fetchCompetitionRecords(this.queryParams, this.currentPage, this.pageSize);
    },

    // 添加一个方法用于触发查询
    search() {
      this.currentPage = 1; // 每次查询时重置当前页为第一页
      this.fetchCompetitionRecords(this.queryParams, this.currentPage, this.pageSize);
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
          this.closeDialog(); // 关闭对话框
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
.el-input__inner {
  border-radius: 4px;
}

.el-button--primary {
  background-color: #42b983;
  border-color: #42b983;
}
</style>
