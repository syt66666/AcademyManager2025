<template>
  <div class="student-container">
    <el-card class="table-bar">
      <!-- 导出按钮 -->
      <div class="export-btn-container">
        <el-button type="primary" icon="el-icon-download" @click="exportExcel">导出数据</el-button>
      </div>

      <!-- 表格展示 -->
      <el-table
        style="width: 100%;"
        ref="table"
        v-loading="loading"
        :data="studentList"
        @selection-change="handleSelectionChange"
        border
        max-height="none"
        size="medium"
        class="nowrap-table"
      >
        <el-table-column label="学号" align="center" prop="userName" min-width="150" />
        <el-table-column label="转前书院" align="center" prop="academy" min-width="180" />
        <el-table-column label="转前专业" align="center" prop="systemMajor" min-width="180" />
        <el-table-column label="分流后书院" align="center" prop="changeAdress" min-width="180" />
        <el-table-column label="分流后专业" align="center" prop="changeMajor" min-width="180" />
        <el-table-column label="一阶段分流类型" align="center" :prop="'changeMajorType'" :formatter="formatDivertType" min-width="200"/>
        <el-table-column label="转专业后书院" align="center" prop="afterChangeAdress" min-width="180" />
        <el-table-column label="转专业后专业" align="center" prop="afterChangeMajor" min-width="180" />
        <el-table-column label="二阶段分流类型" align="center" :prop="'afterChangeMajorType'" :formatter="formatDivertType" min-width="200"/>
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
import { getAllStudentQuestionnaieData, getStudentQuestionnaieData } from "@/api/system/questionnaire";
import * as XLSX from 'xlsx';

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
      type: null,
      time: false,
      loading: false,
      total: 0,
      studentList: [],
      allStudentList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        id: null,
        userName: null,
        academy: null,
        systemMajor: null,
        changeAdress: null,
        changeMajor: null,
        changeMajorType: null,
        afterChangeAdress: null,
        afterChangeMajor: null,
        afterChangeMajorType: null,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    formatDivertType(row, column, cellValue) {
      return this.divertTypeMap[cellValue] || cellValue;
    },
    getList() {
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
      getAllStudentQuestionnaieData(this.queryParams)
        .then((response) => {
          const { rows, total } = response;
          this.allStudentList = rows;
          this.loading = false;
        })
        .catch(() => {
          this.$message.error("获取数据失败，请稍后重试");
          this.loading = false;
        });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.queryParams = {
        id: null,
        userName: null,
        academy: null,
        systemMajor: null,
        changeAdress: null,
        changeMajor: null,
        changeMajorType: null,
        afterChangeAdress: null,
        afterChangeMajor: null,
        afterChangeMajorType: null,
      };
      this.getList();
    },
    // 导出 Excel 文件
    exportExcel() {
        const headers = ['学号', '转前书院', '转前专业', '分流后书院', '分流后专业', '一阶段分流类型','转专业后书院', '转专业后专业', '二阶段分流类型'];
        const rows = this.allStudentList.map(item => [
          item.userName,
          item.academy,
          item.systemMajor,
          item.changeAdress,
          item.changeMajor,
          this.formatDivertType(item, null, item.changeMajorType),
          item.afterChangeAdress,
          item.afterChangeMajor,
          this.formatDivertType(item, null, item.afterChangeMajorType)
        ]);
      const worksheet = XLSX.utils.aoa_to_sheet([headers, ...rows]);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, '学生数据');

      // 导出 Excel 文件
      XLSX.writeFile(workbook, '学生数据.xlsx');
    }
  },
  watch: {
    time(newVal) {
      this.queryParams.changeAdress = null;
      this.queryParams.afterChangeAdress = null;
      this.queryParams.changeMajor = null;
      this.queryParams.afterChangeMajor = null;
      this.queryParams.changeMajorType = null;
      this.queryParams.afterChangeMajorType = null;
    },
    academy(newVal) {
      if (this.time === false) {
        this.queryParams.changeAdress = newVal;
      } else {
        this.queryParams.afterChangeAdress = newVal;
      }
      this.getList();
    },
    major(newVal) {
      if (this.time === false) {
        this.queryParams.changeMajor = newVal;
      } else {
        this.queryParams.afterChangeMajor = newVal;
      }
      this.getList();
    },
    type(newVal) {
      if (this.time === false) {
        this.queryParams.changeMajorType = newVal;
      } else {
        this.queryParams.afterChangeMajorType = newVal;
      }
      this.getList();
    },
  },
};
</script>

<style scoped>
.student-container {
  background-color: #f9f9f9;
}

.table-bar {
  margin-top: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table .cell {
  font-size: 14px;
  padding: 10px;
  white-space: nowrap; /* Prevent text wrapping */
  overflow: hidden;
  text-overflow: ellipsis; /* Add ellipsis if text overflows */
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.pagination .el-pagination {
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
}

.pagination .el-pagination__pager li.active {
  background-color: #409eff;
  color: white;
}

.pagination .el-pagination__pager li:hover {
  background-color: #f1f1f1;
}

.export-btn-container {
  text-align: right;
  margin-bottom: 10px;
}

.export-btn-container .el-button {
  margin-left: 10px;
}

.reset-btn {
  background-color: #f56c6c;
  color: white;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.reset-btn:hover {
  background-color: #f44336;
}

.el-table-column {
  font-weight: bold;
}
</style>
