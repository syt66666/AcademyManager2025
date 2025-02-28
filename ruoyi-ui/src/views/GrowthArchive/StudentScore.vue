<template>
  <div class="app-container">
    <!-- 课程选择区域 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="8">
        <el-select
          v-model="selectedCode"
          filterable
          clearable
          placeholder="输入/选择课程代码"
          @change="handleCodeChange"
          @input="searchByCode"
        >
          <el-option
            v-for="item in courseOptions"
            :key="item.courseCode"
            :label="item.courseCode"
            :value="item.courseCode"
          >
            <span>{{ item.courseCode }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">
              {{ item.courseName }}
            </span>
          </el-option>
        </el-select>
      </el-col>

      <el-col :span="8">
        <el-select
          v-model="selectedName"
          filterable
          clearable
          placeholder="输入/选择课程名称"
          @change="handleNameChange"
          @input="searchByName"
        >
          <el-option
            v-for="item in courseOptions"
            :key="item.courseCode"
            :label="item.courseName"
            :value="item.courseCode"
          >
            <span>{{ item.courseName }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">
              {{ item.courseCode }}
            </span>
          </el-option>
        </el-select>
      </el-col>

      <el-col :span="6">
        <el-button
          type="primary"
          @click="handleConfirm"
          :disabled="!selectedCode || !selectedName"
        >确认选择</el-button>
      </el-col>

      <el-col :span="4">
        <el-button type="primary" @click="showAddDialog">添加课程</el-button>
      </el-col>
    </el-row>

    <!-- 添加课程对话框 -->
    <el-dialog title="新增课程" :visible.sync="dialogVisible" width="30%">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="form.courseCode" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" />
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherName">
          <el-input v-model="form.teacherName" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="form.credit" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="课程类别" prop="courseCategory">
          <el-select v-model="form.courseCategory">
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { fetchCourseList, createCourse } from '@/api/student/course'

export default {
  data() {
    return {
      selectedCode: '',
      selectedName: '',
      courseOptions: [],
      dialogVisible: false,
      form: {
        courseCode: '',
        courseName: '',
        credit: 2,
        courseCategory: '',
        teacherName:'',
        chooseCourseCode: '',
        chooseCourseName: ''
      },
      rules: {
        courseCode: [
          { required: true, message: '请输入课程代码', trigger: 'blur' }
        ],
        courseName: [
          { required: true, message: '请输入课程名称', trigger: 'blur' }
        ],
        teacherName: [
          { required: true, message: '请输入授课教师', trigger: 'blur' }
        ],
        credit: [
          { required: true, message: '请选择学分', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.loadCourseData()
  },
  methods: {
    // 加载课程数据
    async loadCourseData() {
      try {
        const res = await fetchCourseList()
        this.courseOptions = res.rows || []
      } catch (error) {
        console.error('加载课程数据失败:', error)
      }
    },

    // 代码选择变化
    handleCodeChange(code) {
      const course = this.courseOptions.find(item => item.courseCode === code)
      if (course) {
        this.selectedName = course.courseName
      }
    },

    // 名称选择变化
    handleNameChange(code) {
      const course = this.courseOptions.find(item => item.courseCode === code)
      if (course) {
        this.selectedCode = course.courseCode
      }
    },

    // 代码搜索
    searchByCode(query) {
      this.loadCourseData({ courseCode: query })
    },

    // 名称搜索
    searchByName(query) {
      this.loadCourseData({ courseName: query })
    },

    handleConfirm() {
      this.chooseCourseCode = this.selectedCode
      this.chooseCourseName = this.selectedName
      this.$message.success(`已选择课程：${this.selectedCode} - ${this.selectedName}`)
    },

    // 显示添加对话框
    showAddDialog() {
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form.resetFields()
      })
    },

    // 提交表单
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            await createCourse(this.form)
            this.$message.success('课程添加成功')
            this.dialogVisible = false
            this.loadCourseData() // 刷新课程列表
          } catch (error) {
            console.error('添加课程失败:', error)
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.mb-4 {
  margin-bottom: 1.5rem;
}
</style>
