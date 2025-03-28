<template>
  <div class="api-test-container">
    <!-- 参数输入区 -->
    <el-form :model="form" label-width="120px">
      <el-form-item label="专业名称">
        <el-input v-model="form.major" placeholder="请输入专业名称" clearable />
      </el-form-item>

      <el-form-item label="所属学院">
        <el-input v-model="form.academy" placeholder="请输入学院名称" clearable />
      </el-form-item>

      <el-form-item label="创新状态">
        <el-select v-model="form.innovationStatus" clearable>
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="政策状态">
        <el-select v-model="form.policyStatus" clearable>
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <!-- 操作区 -->
    <div class="action-area">
      <el-button
        type="primary"
        :loading="loading"
        @click="handleSubmit"
      >
        发送测试请求
      </el-button>
      <el-button @click="handleReset">重置参数</el-button>
    </div>

    <!-- 请求信息 -->
    <div v-if="requestUrl" class="request-info">
      <el-tag type="info">请求地址：{{ requestUrl }}</el-tag>
    </div>

    <!-- 结果展示 -->
    <div class="result-area">
      <el-alert
        v-if="errorMsg"
        :title="errorMsg"
        type="error"
        show-icon
        class="error-alert"
      />

      <!-- 新增子专业展示 -->
      <el-collapse v-if="responseData">
        <el-collapse-item title="子专业列表">
          <div class="child-majors">
            <el-tag
              v-for="(name, index) in childMajors"
              :key="index"
              type="info"
              class="major-tag"
            >
              {{ name }}
            </el-tag>
            <div v-if="childMajors.length === 0" class="empty-tip">
              暂无子专业数据
            </div>
          </div>
        </el-collapse-item>

        <el-collapse-item title="原始响应数据">
          <pre>{{ formattedResponse }}</pre>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script>
import { getMajorTree } from '@/api/system/student' // 请确认实际路径

export default {
  data() {
    return {
      form: {
        major: '',
        academy: '',
        innovationStatus: null,
        policyStatus: null
      },
      childMajors: [], // 子专业存储
      loading: false,
      responseData: null,
      errorMsg: '',
      requestUrl: '',
      statusOptions: [
        { value: 0, label: '未启动' },
        { value: 1, label: '已启动' }
      ]
    }
  },
  computed: {
    formattedResponse() {
      return JSON.stringify(this.responseData, null, 2)
    }
  },
  methods: {
    async handleSubmit() {
      this.loading = true
      this.errorMsg = ''
      this.responseData = null

      try {
        // 构造请求参数（过滤空值）
        const params = Object.fromEntries(
          Object.entries(this.form).filter(([_, v]) => v !== '' && v !== null)
        )

        // 生成请求URL
        this.requestUrl = `/system/major/tree?${new URLSearchParams(params)}`

        // 发送请求
        const { data } = await getMajorTree(params)

        if (data && Array.isArray(data)) {
          this.responseData = data
          this.childMajors = this.extractChildMajors(data) // 提取子专业
        } else {
          throw new Error('返回数据格式异常')
        }
      } catch (error) {
        this.errorMsg = `请求失败：${error.message}`
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    // 新增方法：递归提取子专业名称
    extractChildMajors(data) {
      return data.flatMap(item => {
        const childrenNames = item.children?.length
          ? item.children.map(child => child.majorName)
          : []
        // 递归处理子节点
        const grandchildrenNames = item.children?.length
          ? this.extractChildMajors(item.children)
          : []
        return [...childrenNames, ...grandchildrenNames]
      })
    },
    handleReset() {
      this.form = {
        major: '',
        academy: '',
        innovationStatus: null,
        policyStatus: null
      }
      this.responseData = null
      this.errorMsg = ''
      this.requestUrl = ''
    }
  }
}
</script>

<style scoped>
.api-test-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.action-area {
  margin-top: 20px;
  text-align: center;
}

.request-info {
  margin: 15px 0;
}

.result-area {
  margin-top: 20px;
}

pre {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
}

.error-alert {
  margin-bottom: 15px;
}

.el-form-item {
  margin-bottom: 18px;
}

.el-select {
  width: 100%;
}
</style>
