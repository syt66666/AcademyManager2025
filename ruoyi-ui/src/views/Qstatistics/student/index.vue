<template>
  <div class="student-container">
    <!-- 搜索栏 -->
    <div v-show="showSearch" class="search-bar">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
<!--      <el-form-item label="" prop="id">
        <el-input
          v-model="queryParams.id"
          placeholder="请输入"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
      <el-form-item label="学号" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入学号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="姓名" prop="studentName">
        <el-input
          v-model="queryParams.studentName"
          placeholder="请输入姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
<!--      <el-form-item label="转前学院" prop="academy">
        <el-input
          v-model="queryParams.academy"
          placeholder="请输入转前学院"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
<!--      <el-form-item label="系统内专业" prop="systemMajor">
        <el-input
          v-model="queryParams.systemMajor"
          placeholder="请输入系统内专业"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
<!--      <el-form-item label="转前专业" prop="major">
        <el-input
          v-model="queryParams.major"
          placeholder="请输入转前专业"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
<!--      <el-form-item label="行政班" prop="studentClass">
        <el-input
          v-model="queryParams.studentClass"
          placeholder="请输入行政班"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分流形式" prop="divertForm">
        <el-input
          v-model="queryParams.divertForm"
          placeholder="请输入分流形式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创新班或拔尖班" prop="innovationClass">
        <el-input
          v-model="queryParams.innovationClass"
          placeholder="请输入创新班或拔尖班"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
<!--      <el-form-item label="转后专业" prop="afterMajor">
        <el-input
          v-model="queryParams.afterMajor"
          placeholder="请输入转后专业"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="转后学院" prop="afterAcademy">
        <el-input
          v-model="queryParams.afterAcademy"
          placeholder="请输入转后学院"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    </div>
    <!-- 表格栏 -->
    <el-card class="table-bar">
      <el-row :gutter="10" class="mb8">
  <!--      <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['system:student:add']"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['system:student:edit']"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['system:student:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['system:student:export']"
          >导出</el-button>
        </el-col>-->
        <right-toolbar :showSearch.sync="showSearch" :columns.sync="columns"/>
      </el-row>

      <el-table style="width: 100%" ref="table" height="550" v-loading="loading" :data="studentList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="columns[0].visible" label="学号" align="center" prop="studentId" width="120" fixed/>
        <el-table-column v-if="columns[1].visible" label="姓名" align="center" prop="studentName" width="80" show-overflow-tooltip/>
        <el-table-column v-if="columns[2].visible" label="性别" align="center" prop="studentSex" width="50"/>
        <el-table-column v-if="columns[3].visible" label="转前学院" align="center" prop="academy" >
          <template slot-scope="scope">
            <el-tag size="mini" type="primary">{{ scope.row.academy }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="columns[4].visible" label="系统内专业" align="center" prop="systemMajor" />
        <el-table-column v-if="columns[5].visible" label="转前专业" align="center" prop="major">
          <template slot-scope="scope">
            <el-tag size="mini" type="primary">{{ scope.row.major }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="columns[6].visible" label="行政班" align="center" prop="studentClass" />
        <el-table-column v-if="columns[7].visible" label="分流形式" align="center" prop="divertForm" show-overflow-tooltip/>
        <el-table-column v-if="columns[8].visible" label="创新班或拔尖班" align="center" prop="innovationClass" />
        <el-table-column v-if="columns[9].visible" label="转后学院" align="center" prop="afterAcademy">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.afterAcademy" size="mini" type="success">{{ scope.row.afterAcademy }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="columns[10].visible" label="转后专业" align="center" prop="afterMajor">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.afterMajor" size="mini" type="success">{{ scope.row.afterMajor }}</el-tag>
          </template>
        </el-table-column>

  <!--      <el-table-column label="操作" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:student:edit']"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:student:remove']"
            >删除</el-button>
          </template>
        </el-table-column>-->
      </el-table>
      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
    <!-- 添加或修改学生管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="转前学院" prop="academy">
          <el-input v-model="form.academy" placeholder="请输入转前学院" />
        </el-form-item>
        <el-form-item label="系统内专业" prop="systemMajor">
          <el-input v-model="form.systemMajor" placeholder="请输入系统内专业" />
        </el-form-item>
        <el-form-item label="转前专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入转前专业" />
        </el-form-item>
        <el-form-item label="行政班" prop="studentClass">
          <el-input v-model="form.studentClass" placeholder="请输入行政班" />
        </el-form-item>
        <el-form-item label="分流形式" prop="divertForm">
          <el-input v-model="form.divertForm" placeholder="请输入分流形式" />
        </el-form-item>
        <el-form-item label="创新班或拔尖班" prop="innovationClass">
          <el-input v-model="form.innovationClass" placeholder="请输入创新班或拔尖班" />
        </el-form-item>
        <el-form-item label="转后专业" prop="afterMajor">
          <el-input v-model="form.afterMajor" placeholder="请输入转后专业" />
        </el-form-item>
        <el-form-item label="转后学院" prop="afterAcademy">
          <el-input v-model="form.afterAcademy" placeholder="请输入转后学院" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStudent, getStudent, delStudent, addStudent, updateStudent } from "@/api/system/student";

export default {
  name: "Student",
  data() {
    return {
      academy: null,
      major: null,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 学生管理表格数据
      studentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
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
        afterAcademy: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        studentId: [
          { required: true, message: "学号不能为空", trigger: "blur" }
        ],
        studentName: [
          { required: true, message: "姓名不能为空", trigger: "blur" }
        ],
        academy: [
          { required: true, message: "管理部门不能为空", trigger: "blur" }
        ],
      },
      // 显隐列
      columns: [
        {key: 1, label: '学号', visible: true},
        {key: 2, label: '姓名', visible: true},
        {key: 3, label: '性别', visible: true},
        {key: 4, label: '转前学院', visible: true},
        {key: 5, label: '系统内专业', visible: false},
        {key: 5, label: '转前专业', visible: true},
        {key: 6, label: '行政班', visible: false},
        {key: 7, label: '分流形式', visible: false},
        {key: 8, label: '创新班或拔尖班', visible: false},
        {key: 9, label: '转后学院', visible: true},
        {key: 10, label: '转后专业', visible: true},
      ]
    };
  },
  created() {
    this.getList();
  },
  watch: {
    academy(newVal, oldVal) {
      // 当 academy 发生变化时，刷新数据
      this.queryParams.afterAcademy = newVal;
      this.getList();
    },
    major(newVal, oldVal) {
      // 当 major 发生变化时，刷新数据
      this.queryParams.afterMajor = newVal;
      this.getList();
    },
    columns: {
      deep: true,
      handler() {
        this.$nextTick(() => {
          this.$refs.table.doLayout();
        })
      }
    },
  },
  methods: {
    /** 查询学生管理列表 */
    getList() {
      this.loading = true;
      listStudent(this.queryParams).then(response => {
        this.studentList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
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
        afterAcademy: null
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
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加学生管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getStudent(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改学生管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateStudent(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addStudent(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除学生管理编号为"' + ids + '"的数据项？').then(function() {
        return delStudent(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/student/export', {
        ...this.queryParams
      }, `student_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
.student-container {
  width: 100%;
  height: 89vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-content: center;
}

.search-bar {
  width: 99%;
  height: 8vh;
  margin: 1vh;
  padding-left: 2vw;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid rgba(235, 239, 245, 0.4);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, .1);
  background: #c6e2ff;
}

.search-bar .el-form-item--small {
  margin: 0 25px 0 0;
}

.table-bar {
  width: 99%;
  height: 80vh;
  margin: 0 1vh;
  display: flex;
  flex-direction: column;
  align-content: center;
}
</style>
