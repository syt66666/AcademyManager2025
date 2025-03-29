<template>
  <div class="container">
    <!-- 顶部统计图表 -->
    <el-row :gutter="20" class="top-charts">
      <el-col
        v-for="(chart, index) in gradeCharts"
        :key="index"
        :xs="24"
        :sm="12"
        :md="6"
      >
        <el-card class="chart-card glow-card">
          <h4 class="chart-title">{{ chart.title }}</h4>
          <div
            :ref="'gradeChart' + index"
            class="chart-content"
            style="height: 200px"
          ></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="6">
        <el-card class="chart-card glow-card">
          <h4 class="chart-title">专业人数占比</h4>
          <div
            ref="pieChart"
            class="chart-content"
            style="height: 200px"
          ></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主体内容 -->
    <el-row :gutter="20" class="main-content">
      <!-- 左侧布局 -->
      <el-col :xs="24" :md="12">
        <el-card class="selection-card neo-card">
          <div class="selection-header">
            <div class="title-container">
              <h3 class="section-title">
                <i class="el-icon-s-management"></i>
                专业选择
              </h3>
              <div class="decorative-line"></div>
            </div>
            <div class="select-container">
              <el-select
                v-model="selectedMajor"
                placeholder="请选择你的专业"
                class="major-select"
                filterable
                @change="handleMajorChange"
                popper-class="beautiful-select"
              >
                <el-option
                  v-for="major in childMajors"
                  :key="major.majorId"
                  :label="major.majorName"
                  :value="major.majorId"
                >
                  <span class="option-text">{{ major.majorName }}</span>
                  <span class="option-count">{{ major.total || 0 }}人已选</span>
                </el-option>
              </el-select>
              <el-button
                type="primary"
                class="confirm-btn gradient-btn"
                @click="handleConfirm"
                :disabled="!selectedMajor"
              >
                <i class="el-icon-check"></i>
                <span class="btn-text">立即确认</span>
                <div class="hover-effect"></div>
              </el-button>
            </div>
          </div>

          <el-card class="stats-card neo-card">
            <h3 class="section-title">
              <i class="el-icon-s-data"></i>
              实时人数统计
            </h3>
            <el-table
              :data="childMajors"
              style="width: 100%"
              :row-class-name="tableRowClassName"
              v-loading="loading"
              class="beautiful-table"
            >
              <el-table-column
                type="index"
                label="序号"
                width="80"
                align="center"
                header-align="center"
              >
                <template #default="scope">
                  <div class="index-badge">{{ scope.$index + 1 }}</div>
                </template>
              </el-table-column>
              <el-table-column label="专业名称" min-width="120">
                <template #default="{row}">
                  <span class="major-name">
                    <i class="el-icon-notebook-2"></i>
                    {{ row.majorName }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="总人数" align="right" header-align="right">
                <template #default="{row}">
                  <el-tag
                    :type="getCountType(row.total)"
                    class="count-tag"
                  >
                    {{ row.total || 0 }} 人
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-card>
      </el-col>

      <!-- 右侧图表 -->
      <el-col :xs="24" :md="12">
        <el-card class="main-chart-card neo-card">
          <h3 class="section-title">
            <i class="el-icon-data-analysis"></i>
            成绩分布实时视图
          </h3>
          <div ref="mainChart" class="main-chart" style="height: 600px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import {getMajorTree, getStudent, updateStudent} from "@/api/system/student";
import store from "@/store";

const COLOR_SCHEME = [
  { start: '#6A81E0', end: '#8E37D7' },
  { start: '#7BCFA6', end: '#4CAF50' },
  { start: '#FFC107', end: '#FF9800' }
]

export default {
  data() {
    return {
      selectKey: 0, // 新增
      form: {
        major: '',
        academy: '',
        innovationStatus: null,
        policyStatus: null
      },
      //学生信息
      userName: store.state.user.name,
      childMajors: [], // 处理后的子专业数据
      responseData: null,
      errorMsg: '',
      requestUrl: '',
      selectedMajor: '',
      loading: false,
      gradeCharts: [
        { title: 'A级人数分布', type: 'A' },
        { title: 'B级人数分布', type: 'B' },
        { title: 'C级人数分布', type: 'C' }
      ],
      charts: {
        main: null,
        pie: null,
        grade: []
      }
    }
  },
  async mounted() {
    await this.handleSubmit()   // 先获取数据
    this.initCharts()           // 再初始化图表
    // this.startSimulation()  // 已注释
  },
  watch: {
    childMajors: {
      deep: true,
      handler() {
        if (this.charts.main) {
          this.updateAllCharts()
        }
      }
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.disposeCharts()
  },
  methods: {
    handleMajorChange(majorId) {
      this.selectedMajor = majorId
      console.log('选择的专业ID:', majorId)
    },
    async handleConfirm() {
      if (!this.selectedMajor) return

      try {
        const selectedMajor = this.childMajors.find(
          m => m.majorId === this.selectedMajor
        )

        await updateStudent({
          studentId: this.userName,
          systemMajor: selectedMajor.majorName
        })

        await this.handleSubmit()
        this.selectKey++
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async handleSubmit() {
      this.loading = true
      this.errorMsg = ''
      this.responseData = null
      try {
        // 1. 先获取学生信息
        const studentResponse = await getStudent(this.userName)
        const studentInfo = studentResponse.studentInfo

        // 2. 正确映射字段
        this.form = {
          major: studentInfo.divertForm.includes("类内任选") ? studentInfo.major : studentInfo.originalSystemMajor, // 关键字段映射
          academy: studentInfo.academy,
          innovationStatus: studentInfo.innovationClass,
          policyStatus: studentInfo.policyStatus
        }

        // 3. 确保参数有效性
        const params = {
          major: this.form.major,
          academy: this.form.academy,
          ...(this.form.innovationStatus && { innovationStatus: this.form.innovationStatus }),
          ...(this.form.policyStatus && { policyStatus: this.form.policyStatus })
        }

        // 4. 打印验证参数
        console.log('Request params:', params)

        // 5. 调用接口
        const { data } = await getMajorTree(params)

        if (data && Array.isArray(data)) {
          // 临时添加模拟数据（实际开发中应删除）
          // this.addMockData(data)
          this.childMajors = this.extractChildMajors(data)
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

    // 数据提取方法
    extractChildMajors(data) {
      return data.flatMap(item => {
        const current = item.children?.length === 0 ? [{
          majorId: item.majorId,
          majorName: item.majorName,
          total: item.studentNum || 0,
          grades: {
            A: item.gradeA || 0,
            B: item.gradeB || 0,
            C: item.gradeC || 0
          }
        }] : []

        const children = item.children?.flatMap(child =>
          this.extractChildMajors([child])
        ) || []

        return [...current, ...children]
      })
    },

    // 临时模拟数据方法（实际接口有数据后应删除）
    addMockData(data) {
      const mock = (node) => {
        if (node.children) {
          node.children.forEach(mock)
        } else {
          node.studentNum = Math.floor(Math.random() * 100) + 50
          node.gradeA = Math.floor(node.studentNum * 0.2)
          node.gradeB = Math.floor(node.studentNum * 0.5)
          node.gradeC = node.studentNum - node.gradeA - node.gradeB
        }
      }
      data.forEach(mock)
    },

    // 图表初始化
    initCharts() {
      this.charts.main = echarts.init(this.$refs.mainChart)
      this.charts.pie = echarts.init(this.$refs.pieChart)
      this.gradeCharts.forEach((_, index) => {
        this.charts.grade[index] = echarts.init(this.$refs[`gradeChart${index}`][0])
      })
      this.updateAllCharts()
    },

    updateAllCharts() {
      this.updateMainChart()
      this.updateGradeCharts()
      this.updatePieChart()
    },

    updateMainChart() {
      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255,255,255,0.95)',
          formatter: params => {
            const total = this.childMajors.find(m => m.majorName === params[0].name)?.total || 1
            return params.map(p => `
              <span style="display:inline-block;margin-right:5px;border-radius:50%;width:10px;height:10px;background:${p.color}"></span>
              ${p.seriesName}: ${p.value}人 (${((p.value/total)*100).toFixed(1)}%)
            `).join('<br>')
          }
        },
        legend: { data: ['A级人数', 'B级人数', 'C级人数'], bottom: 20 },
        grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
        yAxis: { type: 'category', data: this.childMajors.map(m => m.majorName) },
        xAxis: { type: 'value' },
        series: ['A', 'B', 'C'].map((type, index) => ({
          name: `${type}级人数`,
          type: 'bar',
          stack: 'total',
          data: this.childMajors.map(m => m.grades[type]),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: COLOR_SCHEME[index].start },
              { offset: 1, color: COLOR_SCHEME[index].end }
            ]),
            borderRadius: [0, 8, 8, 0]
          },
          barWidth: 36
        }))
      }
      this.charts.main.setOption(option)
    },

    updateGradeCharts() {
      this.gradeCharts.forEach((chart, index) => {
        const option = {
          xAxis: {
            type: 'category',
            data: this.childMajors.map(m => m.majorName),
            axisLabel: { rotate: 30, color: '#666' }
          },
          yAxis: { type: 'value', axisLabel: { color: '#666' } },
          series: [{
            type: 'bar',
            data: this.childMajors.map(m => m.grades[chart.type]),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: COLOR_SCHEME[index].start },
                { offset: 1, color: COLOR_SCHEME[index].end }
              ]),
              borderRadius: [8, 8, 0, 0]
            },
            barWidth: 24
          }]
        }
        this.charts.grade[index].setOption(option)
      })
    },

    updatePieChart() {
      const option = {
        tooltip: { trigger: 'item' },
        legend: { show: false },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: this.childMajors.map((m, i) => ({
            name: m.majorName,
            value: m.total,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: COLOR_SCHEME[i % 3].start },
                { offset: 1, color: COLOR_SCHEME[i % 3].end }
              ])
            }
          }))
        }]
      }
      this.charts.pie.setOption(option)
    },



    startSimulation() {
      setInterval(() => {
        this.childMajors.forEach(major => {
          const delta = Math.floor(Math.random() * 3) - 1
          major.total = Math.max(0, major.total + delta)

          const ratios = {
            A: major.grades.A / (major.total - delta || 1),
            B: major.grades.B / (major.total - delta || 1),
            C: major.grades.C / (major.total - delta || 1)
          }

          major.grades.A = Math.round(major.total * ratios.A)
          major.grades.B = Math.round(major.total * ratios.B)
          major.grades.C = major.total - major.grades.A - major.grades.B
        })
        this.updateAllCharts()
      }, 2000)
    },

    tableRowClassName({ row }) {
      return row.total > 50 ? 'warning-row' : ''
    },

    getCountType(count) {
      count = count || 0
      return count > 60 ? 'danger' : count > 40 ? 'warning' : 'success'
    },

    handleResize() {
      this.charts.main.resize()
      this.charts.pie.resize()
      this.charts.grade.forEach(chart => chart.resize())
    },

    disposeCharts() {
      this.charts.main.dispose()
      this.charts.pie.dispose()
      this.charts.grade.forEach(chart => chart.dispose())
    }
  }
}
</script>

<style lang="scss">
.container {
  padding: 30px;
  background: #f8faff;
  min-height: 100vh;
}

.neo-card {
  border: none !important;
  border-radius: 20px !important;
  box-shadow: 0 10px 30px rgba(106,129,224,0.1) !important;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 40px rgba(106,129,224,0.2) !important;
  }
}

.glow-card {
  background: linear-gradient(145deg, #ffffff, #f8faff) !important;
  box-shadow: 0 4px 20px rgba(106,129,224,0.15) !important;
}

.selection-header {
  padding: 30px !important;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 20px !important;
  margin: -20px -20px 30px !important;

  .title-container {
    margin-bottom: 25px;
    position: relative;

    .decorative-line {
      width: 60px;
      height: 3px;
      background: #6A81E0;
      margin-top: 10px;
      border-radius: 2px;
    }
  }
}

.major-select {
  width: 100%;
  margin-bottom: 20px !important;

  .el-input__inner {
    height: 50px !important;
    border-radius: 12px !important;
    border: 2px solid #e6e9f4 !important;
    background: rgba(255,255,255,0.9) !important;
    font-size: 16px;
    color: #2c3e50;
    transition: all 0.3s;

    &:hover {
      border-color: #6A81E0 !important;
      box-shadow: 0 4px 12px rgba(106,129,224,0.15);
    }

    &:focus {
      border-color: #6A81E0 !important;
      background: white !important;
    }
  }
}

.gradient-btn {
  position: relative;
  overflow: hidden;
  border: none !important;
  background: linear-gradient(135deg, #6A81E0 0%, #8E37D7 100%) !important;
  height: 50px !important;
  border-radius: 12px !important;
  font-size: 16px !important;
  letter-spacing: 1px;
  transition: all 0.4s !important;

  .btn-text {
    position: relative;
    z-index: 1;
  }

  .hover-effect {
    position: absolute;
    top: 0;
    left: -100%;
    width: 200%;
    height: 100%;
    background: linear-gradient(
        90deg,
        rgba(255,255,255,0) 0%,
        rgba(255,255,255,0.2) 50%,
        rgba(255,255,255,0) 100%
    );
    transition: left 0.6s;
  }

  &:hover {
    box-shadow: 0 8px 20px rgba(106,129,224,0.3) !important;

    .hover-effect {
      left: 100%;
    }
  }
}

.beautiful-table {
  th {
    background: #f8f9fa !important;
    font-weight: 600 !important;
    color: #2c3e50 !important;
  }

  .index-badge {
    width: 28px;
    height: 28px;
    background: #6A81E0;
    color: white;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
  }

  .major-name {
    display: flex;
    align-items: center;

    i {
      margin-right: 8px;
      color: #6A81E0;
      font-size: 18px;
    }
  }

  .count-tag {
    border-radius: 8px !important;
    padding: 0 12px !important;
    height: 28px !important;
    line-height: 28px !important;
    font-weight: bold;
    border: none !important;
  }
}

.beautiful-select {
  border: none !important;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1) !important;
  border-radius: 12px !important;

  .el-select-dropdown__item {
    padding: 12px 20px !important;
    display: flex;
    justify-content: space-between;
    transition: all 0.3s;

    &:hover {
      background: #f8f9fa !important;
    }

    &.selected {
      background: #f0f3ff !important;
    }

    .option-text {
      font-weight: 500;
      color: #2c3e50;
    }

    .option-count {
      font-size: 12px;
      color: #909399;
    }
  }
}

.section-title {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 25px;
  display: flex;
  align-items: center;

  i {
    margin-right: 10px;
    font-size: 24px;
    color: #6A81E0;
  }
}

.top-charts {
  height: 25vh;
  margin-bottom: 30px;

  .chart-card {
    height: 100%;
    border-radius: 16px !important;
    margin-bottom: 20px;

    .chart-title {
      font-size: 16px;
      color: #2c3e50;
      margin-bottom: 15px;
    }
  }
}

.main-content {
  height: calc(75vh - 90px);
}

.main-chart {
  border-radius: 16px;
}
</style>
