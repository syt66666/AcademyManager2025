<template>
  <div class="student-list">
    <div class="student-list-header">
      <h4>学生列表</h4>
    </div>
    <div class="student-list-content">
      <el-table
        :data="studentList"
        v-loading="loading"
        element-loading-text="加载中..."
        class="student-table"
        stripe
        height="400"
      >
        <el-table-column
          type="index"
          label="序号"
          width="60"
          align="center"
        />
        <el-table-column
          prop="studentName"
          label="学生姓名"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="studentId"
          label="学号"
          width="120"
          align="center"
        />
        <el-table-column
          prop="college"
          label="所属书院"
          width="120"
          align="center"
        />
        <el-table-column
          prop="major"
          label="专业"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column
          prop="transferType"
          label="分流类型"
          width="120"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag :type="getTransferTypeTagType(scope.row.transferType)" size="small">
              {{ getTransferTypeText(scope.row.transferType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          width="100"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StudentList',
  data() {
    return {
      studentList: [],
      loading: false,
      academy: null,
      major: null,
      type: null
    }
  },
  methods: {
    // 获取分流类型文本
    getTransferTypeText(transferType) {
      const typeMap = {
        '1': '保持当前专业',
        '2': '域内任选专业',
        '3': '类内任选专业',
        '4': '拔尖/创新班政策内任选专业',
        '5': '转专业'
      }
      return typeMap[transferType] || transferType || '未知'
    },
    
    // 获取分流类型标签类型
    getTransferTypeTagType(transferType) {
      const typeMap = {
        '1': 'success',
        '2': 'primary',
        '3': 'warning',
        '4': 'info',
        '5': 'danger'
      }
      return typeMap[transferType] || 'info'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'completed': '已完成',
        'processing': '处理中',
        'pending': '待审核',
        'rejected': '已拒绝'
      }
      return statusMap[status] || status || '未知'
    },
    
    // 获取状态标签类型
    getStatusTagType(status) {
      const typeMap = {
        'completed': 'success',
        'processing': 'warning',
        'pending': 'info',
        'rejected': 'danger'
      }
      return typeMap[status] || 'info'
    },
    
    // 加载学生列表
    async loadStudentList() {
      try {
        this.loading = true
        // 这里应该调用实际的学生列表API
        // const response = await getStudentList({
        //   academy: this.academy,
        //   major: this.major,
        //   type: this.type
        // })
        // this.studentList = response.rows || []
        
        // 临时模拟数据
        this.studentList = [
          {
            studentName: '张三',
            studentId: '2021001',
            college: '求实书院',
            major: '软件工程',
            transferType: '1',
            status: 'completed'
          },
          {
            studentName: '李四',
            studentId: '2021002',
            college: '求实书院',
            major: '网络工程',
            transferType: '2',
            status: 'processing'
          }
        ]
      } catch (error) {
        console.error('加载学生列表失败:', error)
        this.$message.error('加载学生列表失败')
      } finally {
        this.loading = false
      }
    }
  },
  mounted() {
    this.loadStudentList()
  }
}
</script>

<style scoped>
.student-list {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.student-list-header {
  padding: 16px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

.student-list-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.student-list-content {
  padding: 0;
}

.student-table {
  border-radius: 0;
}

.student-table .el-table__header {
  background: #f8fafc;
}

.student-table .el-table__header th {
  background: #f8fafc;
  color: #374151;
  font-weight: 600;
  border-bottom: 1px solid #e5e7eb;
}

.student-table .el-table__body tr:hover {
  background: #f9fafb;
}
</style>
