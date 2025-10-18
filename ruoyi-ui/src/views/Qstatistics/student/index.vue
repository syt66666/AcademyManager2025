<template>
  <div class="student-container">
    <el-card class="table-bar">
      <!-- 操作栏 -->
      <div class="operation-bar">
        <div class="operation-left">
          <h3 class="table-title">
            <i class="el-icon-user"></i>
            学生分流信息
          </h3>
        </div>
        <div class="operation-right">
          <el-button type="primary" icon="el-icon-download" @click="exportExcel" class="export-btn">
            导出数据
          </el-button>
        </div>
      </div>

      <!-- 表格展示 -->
      <div class="table-container">
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
          :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
          :row-style="{ height: '50px' }"
          :cell-style="{ whiteSpace: 'nowrap' }"
        >
        <el-table-column label="序号" align="center" width="80" type="index" :index="getIndex" />
        <el-table-column label="学号" align="center" prop="userName" min-width="150" />
        <el-table-column label="转前书院" align="center" prop="academy" min-width="180" />
        <el-table-column label="转前专业" align="center" prop="originalSystemMajor" min-width="180" />
        <el-table-column label="分流后书院" align="center" prop="changeAdress" min-width="180" />
        <el-table-column label="分流后专业" align="center" prop="changeMajor" min-width="180" />
        <el-table-column label="一阶段分流类型" align="center" :prop="'changeMajorType'" :formatter="formatDivertType" min-width="200">
          <template slot-scope="scope">
            <el-tag :type="getDivertTypeTagType(scope.row.changeMajorType)" size="small">
              {{ formatDivertType(scope.row, null, scope.row.changeMajorType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="转专业后书院" align="center" prop="afterChangeAdress" min-width="180" />
        <el-table-column label="转专业后专业" align="center" prop="afterChangeMajor" min-width="180" />
        <el-table-column label="二阶段分流类型" align="center" :prop="'afterChangeMajorType'" :formatter="formatDivertType" min-width="200">
          <template slot-scope="scope">
            <el-tag :type="getDivertTypeTagType(scope.row.afterChangeMajorType)" size="small">
              {{ formatDivertType(scope.row, null, scope.row.afterChangeMajorType) }}
            </el-tag>
          </template>
        </el-table-column>
        </el-table>
      </div>

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
import { getAllStudentQuestionnaieData2, getStudentQuestionnaieData2 } from "@/api/system/questionnaire";
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
        originalSystemMajor: null,
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
    // 计算序号
    getIndex(index) {
      return (this.queryParams.pageNum - 1) * this.queryParams.pageSize + index + 1;
    },
    formatDivertType(row, column, cellValue) {
      return this.divertTypeMap[cellValue] || cellValue;
    },
    // 获取分流类型标签颜色
    getDivertTypeTagType(type) {
      const typeMap = {
        1: 'success',  // 保持当前专业 - 绿色
        2: 'primary',  // 域内任选专业 - 蓝色
        3: 'warning',  // 类内任选专业 - 橙色
        4: 'info',     // 创新班政策内任选专业 - 灰色
        5: 'danger'    // 转专业 - 红色
      };
      return typeMap[type] || 'info';
    },
    getList() {
      this.loading = true;
      getStudentQuestionnaieData2(this.queryParams)
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
      getAllStudentQuestionnaieData2(this.queryParams)
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
        originalSystemMajor: null,
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
      const headers = ['学号', '转前书院', '转前专业', '分流后书院', '分流后专业', '一阶段分流类型', '转专业后书院', '转专业后专业', '二阶段分流类型'];
      const rows = this.allStudentList.map(item => [
        item.userName,
        item.academy,
        item.originalSystemMajor,
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
  background: transparent;
  min-height: fit-content;
  padding: 0;
}

.table-bar {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e4e7ed;
  overflow: hidden;
  width: 100%;
  margin-top: 20px;
  min-height: 300px; /* 改为最小高度，允许内容扩展 */
  display: flex;
  flex-direction: column;
}

/* 操作栏样式 */
.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
  margin: 0;
}

/* 表格容器样式 */
.table-container {
  width: 100%;
  flex: 1;
  overflow: visible; /* 允许内容显示 */
  display: flex;
  flex-direction: column;
}

.operation-left .table-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

.operation-left .table-title i {
  font-size: 18px;
  color: #409eff;
}

.export-btn {
  background: #409eff;
  border: 1px solid #409eff;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
}

.export-btn:hover {
  background: #66b1ff;
  border-color: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.el-table {
  border-radius: 8px;
  overflow: visible; /* 允许表格内容完整显示 */
  border: none;
  flex: 1;
}

.el-table .cell {
  font-size: 14px;
  padding: 12px 8px;
  white-space: nowrap !important;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

/* 表格行样式 */
.el-table tbody tr:hover {
  background-color: #f0f9ff !important;
  transform: scale(1.01);
  transition: all 0.3s ease;
}

.el-table tbody tr {
  transition: all 0.3s ease;
}

/* 表格边框优化 */
.el-table--border::after,
.el-table--group::after,
.el-table::before {
  background-color: #e4e7ed;
}

.el-table--border th,
.el-table--border td {
  border-right: 1px solid #e4e7ed;
}

/* 序号列样式 */
.el-table .el-table__body tr td:first-child {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #409eff;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 16px;
  padding: 12px 0;
  flex-shrink: 0;
}

.pagination .el-pagination {
  background: #fff;
  padding: 12px 20px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.pagination .el-pagination__pager li {
  background: #fff;
  border: 1px solid #e4e7ed;
  color: #606266;
  margin: 0 2px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.pagination .el-pagination__pager li:hover {
  background: #f5f7fa;
  border-color: #409eff;
  color: #409eff;
}

.pagination .el-pagination__pager li.active {
  background: #409eff;
  border-color: #409eff;
  color: white;
  font-weight: 600;
}

.pagination .el-pagination__total,
.pagination .el-pagination__jump {
  color: #606266;
  font-weight: 500;
}

/* 表格不换行样式 */
.nowrap-table .el-table__body tr td {
  white-space: nowrap !important;
}

.nowrap-table .el-table__body tr td .cell {
  white-space: nowrap !important;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 标签样式优化 */
.el-tag {
  border-radius: 12px;
  font-weight: 500;
  font-size: 12px;
  padding: 4px 12px;
}

.el-tag--success {
  background-color: #f0f9ff;
  border-color: #67c23a;
  color: #67c23a;
}

.el-tag--primary {
  background-color: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

.el-tag--warning {
  background-color: #fdf6ec;
  border-color: #e6a23c;
  color: #e6a23c;
}

.el-tag--info {
  background-color: #f4f4f5;
  border-color: #909399;
  color: #909399;
}

.el-tag--danger {
  background-color: #fef0f0;
  border-color: #f56c6c;
  color: #f56c6c;
}
</style>
