<template>
  <div class="student-container">
<!--&lt;!&ndash;     搜索栏 &ndash;&gt;-->
<!--    <div v-show="showSearch" class="search-bar">-->
<!--      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">-->
<!--        <el-form-item label="问卷 ID" prop="questionnaireId">-->
<!--          <el-input-->
<!--            v-model="queryParams.questionnaireId"-->
<!--            placeholder="请输入问卷 ID"-->
<!--            clearable-->
<!--            @keyup.enter.native="handleQuery"-->
<!--          />-->
<!--        </el-form-item>-->
<!--        <el-form-item>-->
<!--          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
<!--          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery" class="reset-btn">重置</el-button>-->
<!--        </el-form-item>-->
<!--      </el-form>-->
<!--    </div>-->

    <!-- 表格栏 -->
    <el-card class="table-bar">
      <el-table
        style="width: 100%;"
        ref="table"
        v-loading="loading"
        :data="studentList"
        @selection-change="handleSelectionChange"
        border
        max-height="none"
      >
<!--        <el-table-column label="ID" align="center" prop="id" />-->
        <el-table-column label="学号" align="center" prop="userName" />
        <el-table-column label="转前书院" align="center" prop="academy" />
        <el-table-column label="转前专业" align="center" prop="systemMajor" />
        <el-table-column v-if="!time" label="分流后书院" align="center" prop="changeAdress" />
        <el-table-column v-if="!time" label="分流后专业" align="center" prop="changeMajor" />
        <el-table-column v-if="!time" label="一阶段分流类型" align="center" :prop="'changeMajorType'" :formatter="formatDivertType"/>
        <el-table-column v-if="time" label="转专业后书院" align="center" prop="afterChangeAdress" />
        <el-table-column v-if="time" label="转专业后专业" align="center" prop="afterChangeMajor" />
        <el-table-column v-if="time"
                         label="二阶段分流类型"
                         align="center"
                         :prop="'afterChangeMajorType'"
                         :formatter="formatDivertType" />


      </el-table>
      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script>
import { getStudentQuestionnaieData } from "@/api/system/questionnaire";

export default {
  name: "Student",
  data() {
    return {
      divertTypeMap: {
        1: "保持当前专业",
        2: "域内任选专业",
        3: "类内任选专业",
        4: "创新班政策内任选专业",
        5: "转专业",
      },
      academy: null,
      major: null,
      type:null,
      time:false,
      loading: false,
      total: 0,
      studentList: [],
      showSearch: true,
      queryParams: {
        pageNum: 1, // 当前页码
        pageSize: 5, // 每页条数
        id:null,
        userName:null,
        academy:null,
        systemMajor:null,
        changeAdress:null,
        changeMajor:null,
        changeMajorType:null,
        afterChangeAdress:null,
        afterChangeMajor:null,
        afterChangeMajorType:null,
      },
      form: {},
    };
  },
  created() {
    this.getList(); // 初始加载数据
  },
  methods: {
    // 格式化分流类型
    formatDivertType(row, column, cellValue) {
      return this.divertTypeMap[cellValue] || cellValue;
    },
    // 获取数据
    getList() {
      console.log('我进来啦')
      console.log('this.queryParams：'+this.queryParams)
      //this.queryParams.academy='求实书院'
      this.loading = true;
      getStudentQuestionnaieData(this.queryParams)
        .then((response) => {
          const { rows, total } = response;
          this.studentList = rows;
          this.total = total;
          this.loading = false;
        })
        .catch(() => {
          this.$message.error("获取数据失败，请稍后重试");
          this.loading = false;
        });
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 重置
    resetQuery() {
      this.handleQuery();
      this.queryParams = {
        id:null,
        userName:null,
        academy:null,
        systemMajor:null,
        changeAdress:null,
        changeMajor:null,
        changeMajorType:null,
        afterChangeAdress:null,
        afterChangeMajor:null,
        afterChangeMajorType:null,
      };
      this.getList();
    },
  },

  watch: {
    time(newVal){
      this.queryParams.changeAdress=null;
      this.queryParams.afterChangeAdress=null;
      this.queryParams.changeMajor=null;
      this.queryParams.afterChangeMajor=null;
      this.queryParams.changeMajorType=null;
      this.queryParams.afterChangeMajorType=null;
    },
    academy(newVal) {
      if(this.time===false){
        this.queryParams.changeAdress=newVal;
      }
      else {
        this.queryParams.afterChangeAdress=newVal;
      }
      this.getList();
    },
    major(newVal) {
      if(this.time===false){
        this.queryParams.changeMajor=newVal;
      }
      else {
        this.queryParams.afterChangeMajor=newVal;
      }
      this.getList();
    },
    type(newVal) {
      if(this.time===false){
        this.queryParams.changeMajorType=newVal;
      }
      else {
        this.queryParams.afterChangeMajorType=newVal;
      }
      this.getList();
    },
    columns: {
      deep: true,
      handler() {
        this.$nextTick(() => {
          this.$refs.table.doLayout();
        });
      }
    },
  },
};
</script>

<style scoped>
.student-container {
  width: 100%;
  padding: 15px;
}

.search-bar {
  margin-bottom: 10px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.reset-btn {
  background-color: #f56c6c;
  color: white;
}

.reset-btn:hover {
  background-color: #f44336;
}

.table-bar {
  margin-top: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}
</style>
