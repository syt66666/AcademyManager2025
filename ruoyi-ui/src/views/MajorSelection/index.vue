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
                popper-class="beautiful-select"
              >
                <el-option
                  v-for="major in majors"
                  :key="major.name"
                  :label="major.name"
                  :value="major.name"
                >
                  <span class="option-text">{{ major.name }}</span>
                  <span class="option-count">{{ major.current }}人已选</span>
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
              :data="majors"
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
                    {{ row.name }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="已选人数" align="right" header-align="right">
                <template #default="{row}">
                  <el-tag
                    :type="getCountType(row.current)"
                    class="count-tag"
                  >
                    {{ row.current }} 人
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
import { debounce } from 'lodash'

const COLOR_SCHEME = [
  { start: '#6A81E0', end: '#8E37D7' },
  { start: '#7BCFA6', end: '#4CAF50' },
  { start: '#FFC107', end: '#FF9800' }
]

export default {
  data() {
    return {
      selectedMajor: '',
      loading: false,
      gradeCharts: [
        { title: 'A级成绩分布', type: 'A' },
        { title: 'B级成绩分布', type: 'B' },
        { title: 'C级成绩分布', type: 'C' }
      ],
      majors: [
        {
          name: '计算机科学',
          current: 45,
          grades: { A: 20, B: 50, C: 30 }
        },
        {
          name: '电子信息工程',
          current: 33,
          grades: { A: 15, B: 60, C: 25 }
        }
      ],
      charts: {
        main: null,
        pie: null,
        grade: []
      }
    }
  },

  mounted() {
    this.$nextTick(() => {
      this.initCharts()
      this.startSimulation()
    })
    window.addEventListener('resize', debounce(this.handleResize, 300))
  },

  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.disposeCharts()
  },

  methods: {
    initCharts() {
      this.charts.main = echarts.init(this.$refs.mainChart)
      this.charts.pie = echarts.init(this.$refs.pieChart)
      this.gradeCharts.forEach((_, index) => {
        const refName = 'gradeChart' + index
        this.charts.grade[index] = echarts.init(this.$refs[refName][0])
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
          formatter: params => `
            <b>${params[0].name}</b><br>
            ${params.map(p => `
              <span style="display:inline-block;margin-right:5px;border-radius:50%;width:10px;height:10px;background:${p.color}"></span>
              ${p.seriesName}: ${Math.round(p.value)}人 (${this.majors.find(m => m.name === p.name).grades[p.seriesName[0]]}%)
            `).join('<br>')}`
        },
        legend: {
          data: ['A级成绩', 'B级成绩', 'C级成绩'],
          bottom: 20
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        yAxis: {
          type: 'category',
          data: this.majors.map(m => m.name)
        },
        xAxis: { type: 'value' },
        series: ['A', 'B', 'C'].map((type, index) => ({
          name: `${type}级成绩`,
          type: 'bar',
          stack: 'total',
          data: this.majors.map(m => m.current * (m.grades[type] / 100)),
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
            data: this.majors.map(m => m.name),
            axisLabel: {
              rotate: 30,
              color: '#666'
            }
          },
          yAxis: {
            type: 'value',
            axisLabel: { color: '#666' }
          },
          series: [{
            type: 'bar',
            data: this.majors.map(m => m.current * (m.grades[chart.type] / 100)),
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
        legend: {
          orient: 'vertical',
          left: 20,
          top: 'middle'
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: this.majors.map((m, i) => ({
            name: m.name,
            value: m.current,
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

    handleConfirm() {
      if (this.selectedMajor) {
        const major = this.majors.find(m => m.name === this.selectedMajor)
        if (major) {
          major.current++
          this.selectedMajor = ''
          this.updateAllCharts()
        }
      }
    },

    tableRowClassName({ row }) {
      return row.current > 50 ? 'warning-row' : ''
    },

    getCountType(count) {
      return count > 60 ? 'danger' : count > 40 ? 'warning' : 'success'
    },

    startSimulation() {
      setInterval(() => {
        this.majors.forEach(major => {
          major.current = Math.max(0, major.current + Math.floor(Math.random() * 3 - 1))
          major.grades = {
            A: Math.min(100, major.grades.A + Math.random() * 2 - 1),
            B: Math.min(100, major.grades.B + Math.random() * 2 - 1),
            C: Math.min(100, major.grades.C + Math.random() * 2 - 1)
          }
        })
        this.updateAllCharts()
      }, 2000)
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
