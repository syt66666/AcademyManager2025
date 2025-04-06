<template>
  <div class="container">
    <div>
      <time-countdown
        :start-time="startTimestamp"
        :end-time="endTimestamp"
      />
    </div>
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
            <!-- 在专业选择的下拉框和确认按钮之间添加 -->
            <div class="policy-switch" v-if="form.innovationStatus === 1">
              <el-checkbox
                v-model="form.policyStatus"
                :true-label="1"
                :false-label="0"
                class="policy-checkbox"
                @change="handleCheckboxChange"
              >
                <span class="policy-text">放弃创新班政策</span>
                <el-tooltip
                  effect="light"
                  content="选择此项将放弃创新班特殊政策"
                  placement="top"
                >
                  <i class="el-icon-warning-outline"></i>
                </el-tooltip>
              </el-checkbox>
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
              <div class="status-positioner">
                <div class="status-container">
                  <div v-if="systemMajor" class="text-status success-text">
                    <i class="el-icon-success"></i>
                    {{ systemMajor }}
                  </div>
                  <div v-else class="text-status info-text">
                    <i class="el-icon-info"></i>
                    尚未选择
                  </div>
                </div>
              </div>
              <div class="action-row">
                <el-button
                  type="primary"
                  class="confirm-btn gradient-btn"
                  @click="handleConfirm"
                  :disabled="!selectedMajor || !isValidTime"
                >
                  <i class="el-icon-check"></i>
                  <span class="btn-text">立即确认</span>
                  <div class="hover-effect"></div>
                </el-button>
              </div>
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
import {getMajorCount, getMajorTree, getStudent, updateStudent} from "@/api/system/student";
import store from "@/store";
import TimeCountdown from '@/components/FlipCountdown/index'

const COLOR_SCHEME = [
  {start: '#6A81E0', end: '#8E37D7'},
  {start: '#7BCFA6', end: '#4CAF50'},
  {start: '#FFC107', end: '#FF9800'}
]

export default {
  components: {
    TimeCountdown
  },
  computed: {
    // 将时间字符串转换为时间戳
    startTimestamp() {
      return Date.parse('2023-09-01 08:00:00')
    },
    endTimestamp() {
      return Date.parse('2025-05-20 08:00:00')
    },
    // 新增时间有效性计算
    isValidTime() {
      const now = Date.now()
      return now >= this.startTimestamp && now <= this.endTimestamp
    }
  },
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
      previousPolicyStatus: null,
      userName: store.state.user.name,
      innovationClass: '',
      childMajors: [], // 处理后的子专业数据
      responseData: null,
      errorMsg: '',
      requestUrl: '',
      selectedMajor: '',
      loading: false,
      gradeCharts: [
        {title: 'A级人数分布', type: 'A'},
        {title: 'B级人数分布', type: 'B'},
        {title: 'C级人数分布', type: 'C'}
      ],
      charts: {
        main: null,
        pie: null,
        grade: []
      },
      ws: null,
      reconnectAttempts: 0,
      isConnected: false,
    }
  },
  async mounted() {
    await this.getData()   // 先获取数据
    this.initCharts()           // 再初始化图表
    this.connectWebSocket() //
    // window.addEventListener('data-update', this.handleDataUpdate)
  },
  watch: {
    childMajors: {
      deep: true,
      handler(newVal) {
        if (newVal.length > 0) {
          this.$nextTick(() => {
            if (!this.charts.main || this.charts.main.isDisposed()) {
              this.initCharts()
            } else {
              this.updateAllCharts()
            }
          })
        }
      }
    }
  },
  beforeDestroy() {
    if (this.ws) this.ws.close()
    window.removeEventListener('resize', this.handleResize)
    this.disposeCharts()
  },
  methods: {
    // 修改后的处理方法
    handleCheckboxChange(newValue) {
      this.previousPolicyStatus = this.form.policyStatus === 0 ? 1 : 0
      this.$confirm(
        `确定要${newValue === 1 ? '放弃' : '保留'}创新班政策吗？`,
        '操作确认',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // 用户确认后执行更新
        this.updatePolicyStatus()
      }).catch(() => {
        // 用户取消时恢复旧值
        this.form.policyStatus = this.previousPolicyStatus
        this.$message.info('已取消操作')
      })
    },

    // 原有的更新方法保持不变
    async updatePolicyStatus() {
      try {
        await updateStudent({
          studentId: this.userName,
          policyStatus: this.form.policyStatus
        })
        await this.getData()
        this.$message.success('政策状态更新成功')
      } catch (error) {
        this.$message.error('政策状态更新失败')
        console.error(error)
      }
    },
    async fetchMajorCounts(parentId, isTell, divertForm) {
      try {
        const {data: countData} = await getMajorCount({
          parentId: parentId,
          divertForm: divertForm,
          isTell: isTell
        })
        return countData
      } catch (error) {
        console.error('获取人数失败:', error)
        return []
      }
    },
    handleMajorChange(majorId) {
      this.selectedMajor = majorId
    },
    async handleConfirm() {
      // 新增时间有效性验证
      if (!this.isValidTime) {
        return this.$message.error('当前不在可选时间段内，无法提交')
      }
      if (!this.selectedMajor) return

      try {
        // 添加policyStatus验证
        // if (this.form.innovationStatus === 1 && this.form.policyStatus === null) {
        //   return this.$message.warning("请选择是否放弃创新班政策")
        // }
        const selectedMajor = this.childMajors.find(
          m => m.majorId === this.selectedMajor
        )
        //调用接口更新学生信息
        await updateStudent({
          studentId: this.userName,
          systemMajor: selectedMajor.majorName,
          policyStatus: this.form.innovationStatus === 1 ? this.form.policyStatus : 0
        })
        console.log(1111)
        // 3. 重新获取最新专业数据（关键步骤）
        await this.getData(true)
        // 4. 清空已选专业并提示成功
        this.selectedMajor = ''
        this.$message.success('专业选择成功')
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async getData(isTell = false) {
      this.loading = true
      this.errorMsg = ''
      this.responseData = null
      try {
        // 1. 先获取学生信息
        const studentResponse = await getStudent(this.userName)
        const studentInfo = studentResponse.studentInfo
        this.divertForm = studentInfo.divertForm
        this.innovationClass = studentInfo.innovationClass
        this.systemMajor = studentInfo.systemMajor
        console.log('this.systemMajor:' + this.systemMajor)
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
          divertForm:this.divertForm,
          ...(this.form.innovationStatus && {innovationStatus: this.form.innovationStatus}),
          ...(this.form.policyStatus && {policyStatus: this.form.policyStatus})
        }
        console.log('params:')
        console.log(params)

        // 调用接口得到专业数据
        const {data} = await getMajorTree(params)

        // 获取顶层专业ID（新增）
        const topLevelMajorIds = data
          .filter(item => item.parentId === null)
          .map(item => item.majorId)
        const topMajorId = topLevelMajorIds[0] || 0
        // 获取人数统计数据（新增关键步骤）
        const countsData = await this.fetchMajorCounts(topMajorId, isTell, this.divertForm)
        if (countsData.length !== 0) {
          // 处理专业数据时合并人数（修改 extractChildMajors 调用方式）
          this.childMajors = this.extractChildMajors(data, countsData)
        }
      } catch (error) {
        this.errorMsg = `请求失败：${error.message}`
        console.error(error)
      } finally {
        this.loading = false
      }
    },

    // 数据提取方法
    extractChildMajors(data, countsData) {
      return data.flatMap(item => {
        // 查找当前专业的人数数据（新增匹配逻辑）
        const majorCounts = countsData.find(c => c.majorName === item.majorName) || {}
        console.log(majorCounts)
        const current = item.children?.length === 0 ? [{
          majorId: item.majorId,
          majorName: item.majorName,
          total: majorCounts.count || 0,  // 使用接口返回的数据
          grades: {
            A: majorCounts.countA || 0,
            B: majorCounts.countB || 0,
            C: majorCounts.countC || 0
          }
        }] : []

        // 递归处理子级时传递 countsData（重要修改）
        const children = item.children?.flatMap(child =>
          this.extractChildMajors([child], countsData)
        ) || []

        return [...current, ...children]
      })
    },
    // 图表初始化
    initCharts() {
      // 销毁已存在的实例
      this.disposeCharts()

      // 重新初始化
      this.charts.main = echarts.init(this.$refs.mainChart)
      this.charts.pie = echarts.init(this.$refs.pieChart)
      this.gradeCharts.forEach((_, index) => {
        this.charts.grade[index] = echarts.init(this.$refs[`gradeChart${index}`][0])
      })

      this.updateAllCharts()

      // 添加窗口resize监听
      // window.addEventListener('resize', this.handleResize)
    },
    //更新所有表格数据
    updateAllCharts() {
      if (!this.charts.main || this.charts.main.isDisposed()) return

      try {
        this.updateMainChart()
        this.updateGradeCharts()
        this.updatePieChart()

        // 添加重绘逻辑
        this.charts.main.resize()
        this.charts.pie.resize()
        this.charts.grade.forEach(chart => chart.resize())
      } catch (e) {
        console.error('图表更新失败:', e)
      }
    },

    updateMainChart() {
      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255,255,255,0.98)',
          borderWidth: 0,
          borderRadius: 8,
          shadowBlur: 12,
          shadowColor: 'rgba(0,0,0,0.1)',
          padding: [12, 18],
          textStyle: {
            color: '#666',
            fontSize: 14
          },
          formatter: params => {
            const total = this.childMajors.find(m => m.majorName === params[0].name)?.total || 1;
            return `<div style="font-weight:500;margin-bottom:8px">${params[0].name}</div>` +
              params.map(p => `
                        <div style="display:flex;align-items:center;margin:6px 0">
                            <span style="display:inline-block;margin-right:10px;border-radius:4px;width:12px;height:12px;background:${p.color}"></span>
                            <span style="flex:1">${p.seriesName}</span>
                            <span style="font-weight:500;color:#333">${p.value}人</span>
                            <span style="color:#999;margin-left:8px">(${((p.value / total) * 100).toFixed(1)}%)</span>
                        </div>
                    `).join('')
          }
        },
        legend: {
          data: ['A级人数', 'B级人数', 'C级人数'],
          bottom: 20,
          itemGap: 20,
          itemWidth: 14,
          itemHeight: 8,
          itemStyle: {
            borderRadius: 4
          },
          textStyle: {
            fontSize: 12,
            color: '#666'
          }
        },
        grid: {
          left: '2%',
          right: '5%',
          bottom: '18%',
          top: '10%',
          containLabel: true
        },
        yAxis: {
          type: 'category',
          axisLine: {show: false},
          axisTick: {show: false},
          axisLabel: {
            color: '#666',
            fontSize: 12,
            margin: 12
          },
          data: this.childMajors.map(m => m.majorName)
        },
        xAxis: {
          type: 'value',
          splitLine: {
            lineStyle: {
              color: '#f0f0f0',
              type: 'dashed'
            }
          },
          axisLabel: {
            color: '#999',
            fontSize: 12
          }
        },
        animationDuration: 800,  // 增加动画总时长
        animationDurationUpdate: 1000,  // 数据更新动画时长
        animationEasingUpdate: 'cubicOut',  // 更新动画曲线
        series: ['A', 'B', 'C'].map((type, index) => ({
          name: `${type}级人数`,
          type: 'bar',
          stack: 'total',
          data: this.childMajors.map(m => m.grades[type]),
          barWidth: 30,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              {offset: 0, color: COLOR_SCHEME[index].start},
              {offset: 1, color: COLOR_SCHEME[index].end}
            ]),
            borderRadius: [6, 6, 0, 0],
            shadowColor: 'rgba(134, 65, 244, 0.1)',  // 更柔和的阴影颜色
            shadowBlur: 12,  // 增加阴影模糊度
            shadowOffsetY: 4  // 调整阴影偏移
          },
          emphasis: {
            itemStyle: {
              shadowColor: 'rgba(134, 65, 244, 0.2)',  // 悬停时加强阴影
              shadowBlur: 16
            }
          },
            animationDelay: function (params) {
              // 动态延迟算法（更丝滑的波浪效果）
              return params.dataIndex * 30 + index * 60;
            },
        })),
        // 新增视觉过渡效果
        graphic: {
          elements: [{
            type: 'rect',
            left: 'center',
            top: 'middle',
            silent: true,
            invisible: true,
            z: 100,
            shape: { width: 0, height: 0 },
            style: { fill: 'rgba(134, 65, 244, 0.1)' }
          }]
        }
      }

      this.charts.main.setOption(option);
    },

    updateGradeCharts() {
      this.gradeCharts.forEach((chart, index) => {
        const option = {
          xAxis: {
            type: 'category',
            data: this.childMajors.map(m => m.majorName),
            axisLabel: {rotate: 30, color: '#666'}
          },
          yAxis: {type: 'value', axisLabel: {color: '#666'}},
          series: [{
            type: 'bar',
            data: this.childMajors.map(m => m.grades[chart.type]),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {offset: 0, color: COLOR_SCHEME[index].start},
                {offset: 1, color: COLOR_SCHEME[index].end}
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
        tooltip: {trigger: 'item'},
        legend: {show: false},
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: this.childMajors.map((m, i) => ({
            name: m.majorName,
            value: m.total,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                {offset: 0, color: COLOR_SCHEME[i % 3].start},
                {offset: 1, color: COLOR_SCHEME[i % 3].end}
              ])
            }
          }))
        }]
      }
      this.charts.pie.setOption(option)
    },

    tableRowClassName({row}) {
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
      // 增加实例状态判断
      const safeDispose = (instance) => {
        if (instance && !instance.isDisposed()) {
          instance.dispose()
        }
      }

      safeDispose(this.charts.main)
      safeDispose(this.charts.pie)
      this.charts.grade.forEach(safeDispose)
      this.charts.grade = []
    },

    // 新增 WebSocket 连接方法
    connectWebSocket() {
      // const wsUrl = `ws://localhost:8080/websocket/message`
      const host = window.location.hostname
      const baseUrl = process.env.VUE_APP_BASE_API;  // http://localhost:8080
      const wsPath = process.env.VUE_APP_WEBSOCKET_PATH;  // /websocket/message

// 提取主机和端口
      const {hostname, port} = new URL(baseUrl);
      const wsUrl = `ws://${hostname}:${port}${wsPath}`;

      // 生成完整 URL
      // const wsUrl = `ws://${host}:${process.env.VUE_APP_WS_BACKEND_PORT}/websocket/message`   // 生产环境路径
      console.log('host:', wsUrl)

      this.ws = new WebSocket(wsUrl)

      this.ws.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data)
          console.log(message)
          this.handleWebSocketMessage(message) // 调用处理方法
        } catch (e) {
          console.error('消息解析失败', e)
        }
      }
    },
    // 新增 WebSocket 数据更新方法
    handleWebSocketMessage(message) {
      if (message.type === 'student_update') {
        // 查找匹配的专业
        const index = this.childMajors.findIndex(
          m => m.majorId === message.majorId
        )
        if (index > -1) {
          // 使用 Vue.set 确保响应式更新
          this.$set(this.childMajors, index, {
            ...this.childMajors[index],
            total: message.total,
            grades: {
              A: message.gradeA,
              B: message.gradeB,
              C: message.gradeC
            }
          })

          // 可选：触发图表更新
          this.updateAllCharts()
        }
      }
    }
  }
}
</script>

<style lang="scss">
/* ==================== 基础布局 ==================== */
.container {
  padding: 30px;
  background: #f8faff;
  min-height: 100vh;
  margin-left: 100px;
}

/* ==================== 卡片系统 ==================== */
.neo-card {
  border: none !important;
  border-radius: 20px !important;
  box-shadow: 0 10px 30px rgba(106, 129, 224, 0.1) !important;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 40px rgba(106, 129, 224, 0.2) !important;
  }
}

.glow-card {
  background: linear-gradient(145deg, #ffffff, #f8faff) !important;
  box-shadow: 0 4px 20px rgba(106, 129, 224, 0.15) !important;
}

/* ==================== 头部区域 ==================== */
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

/* ==================== 表单元素 ==================== */
.major-select {
  width: 100%;
  margin-bottom: 20px !important;

  .el-input__inner {
    height: 50px !important;
    border-radius: 12px !important;
    border: 2px solid #e6e9f4 !important;
    background: rgba(255, 255, 255, 0.9) !important;
    font-size: 16px;
    color: #2c3e50;
    transition: all 0.3s;

    &:hover {
      border-color: #6A81E0 !important;
      box-shadow: 0 4px 12px rgba(106, 129, 224, 0.15);
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
        rgba(255, 255, 255, 0) 0%,
        rgba(255, 255, 255, 0.2) 50%,
        rgba(255, 255, 255, 0) 100%
    );
    transition: left 0.6s;
  }

  &:hover {
    box-shadow: 0 8px 20px rgba(106, 129, 224, 0.3) !important;

    .hover-effect {
      left: 100%;
    }
  }
}

/* ==================== 数据表格 ==================== */
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

/* ==================== 下拉选择 ==================== */
.beautiful-select {
  border: none !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1) !important;
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

/* ==================== 图表区域 ==================== */
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

.main-chart {
  border-radius: 16px;
}

.policy-switch {
  transition: all 0.3s;

  &:hover {
    transform: scale(1.02);
    box-shadow: 0 2px 8px rgba(106, 129, 224, 0.1);
  }

  .el-checkbox__input.is-checked .el-checkbox__inner {
    background-color: #6A81E0;
    border-color: #6A81E0;
  }
}

.select-container {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .action-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20px;

    .confirm-btn {
      flex-shrink: 0; // 防止按钮被压缩
      margin-right: auto; // 按钮靠左
    }

    .status-container {
      flex-grow: 1;
      max-width: 60%;
      min-width: 280px; // 最小宽度防止内容挤压

      .el-alert {
        margin: 0;
        padding: 8px 16px !important;
      }
    }
  }
}

/* 修改政策切换部分样式 */
.policy-switch {
  // 移除背景和边框
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  padding: 8px 0 !important; // 调整间距
  margin: 12px 0 !important;
  border-radius: 12px;
  backdrop-filter: blur(4px);
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);

  .el-checkbox {
    display: flex;
    align-items: center;

    &__input {
      margin-left: 8px; // 增加左边距
      margin-right: 0; // 清除默认右边距

      // 选中状态颜色保持
      &.is-checked .el-checkbox__inner {
        background-color: #6A81E0;
        border-color: #6A81E0;
      }
    }

    &__label {
      display: flex;
      flex-direction: row-reverse; // 文字和图标的排列顺序
      align-items: center;
    }

    .el-icon-warning-outline {
      order: -1; // 图标保持在文字左侧
      margin-right: 6px;
      margin-left: 0;
    }
  }

  // 悬停状态调整
  &:hover {
    transform: none !important;
    box-shadow: none !important;

    .policy-text::after {
      width: 100%; // 悬停时显示下划线动画
    }
  }

  .el-checkbox {
    &__label {
      display: flex;
      align-items: center;
      font-size: 15px;
      color: #2c3e50;
      letter-spacing: 0.5px;
      flex-direction: row-reverse;
    }

    &__input {
      margin-left: 8px;
      margin-right: 0;

      &.is-checked .el-checkbox__inner {
        background-color: #6A81E0;
        border-color: #6A81E0;
      }
    }

    .el-icon-warning-outline {
      order: -1;
      margin-right: 6px;
      color: #FFC107 !important;
      font-size: 18px;
      transition: all 0.3s;

      &:hover {
        transform: rotate(15deg) scale(1.1);
      }
    }
  }

  .policy-text {
    font-weight: 600;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      width: 0;
      height: 2px;
      background: #6A81E0;
      transition: width 0.3s;
    }
  }
}

/* 优化状态提示样式 */
.status-container {
  .text-status {
    font-size: 14px;
    line-height: 1.5;
    border-radius: 6px;
    display: flex;
    align-items: center;
    transition: all 0.3s;
    // 移除background属性
    padding: 0 !important;

    i {
      margin-right: 8px;
      font-size: 16px;
    }

    &.success-text {
      color: #67C23A;
      background: rgba(103, 194, 58, 0.08);
    }

    &.info-text {
      color: #909399;
      background: rgba(144, 147, 153, 0.08);
    }
  }
}

/* 新增动画 */
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.08);
  }
  100% {
    transform: scale(1);
  }
}

/* 调整按钮和状态的布局 */
.action-row {
  display: flex;
  gap: 20px;
  align-items: stretch; // 保持等高

  .confirm-btn {
    min-width: 140px;
    height: 50px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      transform: translateY(-2px) scale(1.02);
    }
  }
}

/* 新增样式 */
.select-container {
  position: relative;

  .status-positioner {
    position: absolute;
    bottom: 60px;
    right: 12px;
    z-index: 2;
  }

  .status-container {
    .text-status {
      font-size: 13px;
      color: #666;
      display: flex;
      align-items: center;
      padding: 2px 8px;
      background: transparent !important;

      i {
        font-size: 14px;
        margin-right: 4px;
      }

      &.success-text {
        color: #67C23A;

        i {
          color: #67C23A;
        }
      }

      &.info-text {
        color: #909399;

        i {
          color: #909399;
        }
      }
    }
  }

}

/* 响应式调整 */
@media (max-width: 768px) {
  .select-container {
    .action-row {
      flex-direction: column;

      .status-container {
        max-width: 100%;
        min-width: auto;
        width: 100%;
      }

      .confirm-btn {
        width: 100%;
        margin-right: 0;
      }
    }

    .status-positioner {
      position: static;
      margin-top: 8px;
      text-align: right;
    }

    .confirm-btn {
      margin-top: 15px;
    }
  }

  .policy-switch {
    background: transparent !important;
    box-shadow: none !important;
  }
}
</style>
