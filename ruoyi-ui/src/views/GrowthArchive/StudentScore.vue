<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="学号">
        <el-input v-model="searchForm.studentId" placeholder="输入学号"></el-input>
      </el-form-item>
      <el-form-item label="学期">
        <el-select v-model="searchForm.semester" clearable placeholder="选择学期">
          <el-option
            v-for="item in semesterOptions"
            :key="item"
            :label="item"
            :value="item"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="课程代码">
        <el-input v-model="searchForm.courseCode" placeholder="输入课程名称"></el-input>
      </el-form-item>
      <el-form-item label="课程名称">
        <el-input v-model="searchForm.courseName" placeholder="输入课程名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮组 -->
    <div class="mb-4">
      <el-button type="primary" @click="handleCreate">新增成绩</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table
      :data="tableData"
      border
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column prop="scoreId" label="ID" width="150"></el-table-column>
      <el-table-column prop="studentId" label="学号" width="150"></el-table-column>
      <el-table-column prop="courseCode" label="课程代码" width="150"></el-table-column>
      <el-table-column prop="courseName" label="课程名称"></el-table-column>
      <el-table-column prop="semester" label="学期" width="200"></el-table-column>
      <el-table-column prop="scoreValue" label="成绩" width="120"></el-table-column>
      <el-table-column prop="scoreType" label="类型" width="120">
        <template slot-scope="{row}">
          <el-tag :type="scoreTypeMap[row.scoreType]">{{ row.scoreType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="{row}">
          <el-button size="mini" @click="handleEdit(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogStatus==='create'?'新增成绩':'编辑成绩'"
      :visible.sync="dialogFormVisible"
      width="600px"
    >
      <el-form
        ref="dataForm"
        :model="temp"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="temp.studentId"></el-input>
        </el-form-item>

        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="temp.courseCode"></el-input>
        </el-form-item>

        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="temp.courseName"></el-input>
        </el-form-item>

        <el-form-item label="课程类型" prop="courseCategory">
          <el-select v-model="temp.courseCategory" class="w-full">
            <el-option
              v-for="item in courseTypeOptions"
              :key="item"
              :label="item"
              :value="item"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="学分" prop="credit">
          <el-input-number
            v-model="temp.credit"
            :min="1"
            :max="10"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="学期" prop="semester">
          <el-date-picker
            v-model="temp.semester"
            type="month"
            value-format="yyyy-MM"
            placeholder="选择学期"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="成绩值" prop="scoreValue">
          <el-input v-model="temp.scoreValue"></el-input>
        </el-form-item>

        <el-form-item label="考试类型" prop="scoreType">
          <el-radio-group v-model="temp.scoreType">
            <el-radio
              v-for="item in scoreTypeOptions"
              :key="item"
              :label="item"
            ></el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="dialogStatus==='create'?createData():updateData()"
        >确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList, createScore, updateScore, deleteScore } from '@/api/student/score'
import Pagination from '@/components/Pagination'


export default {
  name: 'ScoreManagement',
  components: { Pagination },
  data() {
    return {
      // 搜索相关
      searchForm: {
        studentId: '',
        semester: ''
      },
      semesterOptions: ['2023-春', '2023-秋', '2024-春'],

      // 表格相关
      tableData: [],
      loading: false,
      total: 0,
      listQuery: {
        page: 1,
        limit: 20
      },

      // 弹窗相关
      dialogFormVisible: false,
      dialogStatus: '',
      temp: {
        scoreId: undefined,
        studentId: '',
        courseCode: '',
        courseName: '',
        courseCategory: '必修',
        credit: 2,
        academy: '',
        semester: '',
        scoreValue: '',
        gpa: null,
        scoreType: '正考'
      },
      rules: {
        studentId: [
          { required: true, message: '学号不能为空', trigger: 'blur' }
        ],
        courseName: [
          { required: true, message: '课程名称不能为空', trigger: 'blur' }
        ],
        courseCode: [
          { required: true, message: '课程代码不能为空', trigger: 'blur' }
        ],
        scoreValue: [
          { required: true, message: '成绩不能为空', trigger: 'blur' }
        ],
        semester: [
          { required: true, message: '请选择学期', trigger: 'change' }
        ]
      },

      // 类型映射
      courseTypeOptions: ['必修', '选修'],
      scoreTypeOptions: ['正考', '补考', '重修', '免修'],
      scoreTypeMap: {
        '正考': 'success',
        '补考': 'warning',
        '重修': 'danger',
        '免修': 'info'
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取成绩列表
    getList() {
      this.loading = true
      const params = {
        ...this.searchForm,
        page: this.listQuery.page,
        size: this.listQuery.limit
      }
      fetchList(params).then(response => {
        this.tableData = response.rows
        this.total = response.total
        this.loading = false
      })
    },

    // 搜索处理
    handleSearch() {
      this.listQuery.page = 1
      this.getList()
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        studentId: '',
        semester: ''
      }
      this.handleSearch()
    },

    // 打开新建弹窗
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },

    // 提交新建
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          createScore(this.temp).then(() => {
            this.dialogFormVisible = false
            this.$message.success('创建成功')
            this.getList()
          })
        }
      })
    },

    // 打开编辑弹窗
    handleEdit(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },

    // 提交更新
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          updateScore(this.temp).then(() => {
            this.dialogFormVisible = false
            this.$message.success('更新成功')
            this.getList()
          })
        }
      })
    },

    // 删除记录
    handleDelete(row) {
      this.$confirm('确认删除该成绩记录?', '警告', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteScore(row.scoreId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>
