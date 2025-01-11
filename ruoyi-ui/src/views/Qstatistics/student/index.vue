<template>
  <div class="student-container">
    <!-- 搜索栏 -->
    <div v-show="showSearch" class="search-bar">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
<!--        <el-form-item label="专业分流类型" prop="change_major_type">-->
<!--          <el-select-->
<!--            v-model="queryParams.change_major_type"-->
<!--            placeholder="请选择专业分流类型"-->
<!--            clearable-->
<!--            @change="handleQuery"-->
<!--            filterable-->
<!--            class="select-field"-->
<!--          >-->
<!--            <el-option-->
<!--              v-for="(label, value) in divertTypeMap"-->
<!--              :key="value"-->
<!--              :label="label"-->
<!--              :value="value"-->
<!--            />-->
<!--          </el-select>-->
<!--        </el-form-item>-->
        <el-form-item label="学号" prop="studentId">
          <el-input
            v-model="queryParams.studentId"
            placeholder="请输入学号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 表格栏 -->
    <el-card class="table-bar">
      <el-table
        style="width: 100%;"
        ref="table"
        v-loading="loading"
        :data="studentList"
        @selection-change="handleSelectionChange"
        border
        max-height="none">
        <el-table-column v-if="columns[1].visible" label="姓名" align="center" prop="studentName" show-overflow-tooltip/>
        <el-table-column v-if="columns[2].visible" label="学号" align="center" prop="studentId" show-overflow-tooltip/>
        <el-table-column v-if="columns[3].visible" label="转前书院" align="center" prop="academy">
          <template slot-scope="scope">
            <el-tag size="mini" type="primary">{{ scope.row.academy }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="columns[5].visible" label="转前专业" align="center" prop="major">
          <template slot-scope="scope">
            <el-tag size="mini" type="primary">{{ scope.row.major }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="columns[9].visible" label="转后书院" align="center" prop="afterAcademy">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.afterAcademy" size="mini" type="success">{{ scope.row.afterAcademy }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="columns[10].visible" label="转后专业" align="center" prop="afterMajor">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.afterMajor" size="mini" type="success">{{ scope.row.afterMajor }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="columns[11].visible" label="分流类型" align="center" prop="change_major_type">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.change_major_type" size="mini" type="primary">{{ divertTypeMap[scope.row.change_major_type] || ' ' }}</el-tag>
          </template>
        </el-table-column>
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
import { listStudent, getStudent, delStudent, addStudent, updateStudent } from "@/api/system/student";

export default {
  name: "Student",
  data() {
    return {
      divertTypeMap: {
        '1': '保持当前专业',
        '2': '域内任选专业',
        '3': '类内任选专业',
        '4': '创新班政策内任选专业',
        '5': '转专业'
      },
      academy: null,
      major: null,
      type: null,
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      studentList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        id: null,
        studentId: null,
        studentName: null,
        academy: null,
        systemMajor: null,
        major: null,
        studentClass: null,
        divertForm: null,
        innovationClass: null,
        studentSex: null,
        afterMajor: null,
        afterAcademy: null,
        change_major_type: null
      },
      form: {},
      rules: {
        studentId: [{ required: true, message: "学号不能为空", trigger: "blur" }],
        studentName: [{ required: true, message: "姓名不能为空", trigger: "blur" }],
        academy: [{ required: true, message: "书院不能为空", trigger: "blur" }],
      },
      columns: [
        { key: 1, label: '学号', visible: true },
        { key: 2, label: '姓名', visible: true },
        { key: 3, label: '性别', visible: true },
        { key: 4, label: '转前书院', visible: true },
        { key: 5, label: '系统内专业', visible: false },
        { key: 5, label: '转前专业', visible: true },
        { key: 6, label: '行政班', visible: false },
        { key: 7, label: '分流形式', visible: false },
        { key: 8, label: '创新班或拔尖班', visible: false },
        { key: 9, label: '转后书院', visible: true },
        { key: 10, label: '转后专业', visible: true },
        { key: 11, label: '专业分流类型', visible: true },
      ]
    };
  },
  created() {
    this.getList();
  },
  watch: {
    academy(newVal, oldVal) {
      this.queryParams.afterAcademy = newVal;
      this.getList();
    },
    major(newVal, oldVal) {
      this.queryParams.afterMajor = newVal;
      this.getList();
    },
    type(newVal, oldVal) {
      this.queryParams.change_major_type = newVal;
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
  methods: {
    getList() {
      this.loading = true;
      listStudent(this.queryParams).then(response => {
        this.studentList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        id: null,
        studentId: null,
        studentName: null,
        academy: null,
        systemMajor: null,
        major: null,
        studentClass: null,
        divertForm: null,
        innovationClass: null,
        studentSex: null,
        afterMajor: null,
        afterAcademy: null,
        change_major_type: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
      this.major=null;
      this.academy=null;
      this.type=null;
    },
  }
};
</script>

<style scoped>
.student-container {
  width: 100%;
  padding: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.search-bar {
  width: 100%;
  margin-bottom: 10px;
  background-color: #ffffff;
  //border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.select-field {
  width: 150px;
}

.reset-btn {
  background-color: #f56c6c;
  color: white;
  border-color: #f56c6c;
}

.reset-btn:hover {
  background-color: #f44336;
}

.table-bar {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  max-height: calc(100vh - 180px);
  overflow-y: auto;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}
</style>
