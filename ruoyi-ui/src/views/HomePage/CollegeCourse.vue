<template>
  <div class="app-container">
    <div class="content-wrapper">
      <!-- 课程统计图表 -->
      <div class="course-charts-section">
        <div class="charts-container">
          <div class="chart-card">
            <h4 class="chart-title">各书院课程数统计</h4>
            <div class="chart-container">
              <div ref="courseCountChart" class="chart"></div>
            </div>
          </div>
          <div class="chart-card">
            <h4 class="chart-title">各书院选课人数统计</h4>
            <div class="chart-container">
              <div ref="enrollmentCountChart" class="chart"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 课程状态统计 -->
      <div class="course-status-section">
        <div class="status-stats-grid">
          <div class="status-stat-card not-started">
            <div class="status-icon">
              <i class="el-icon-time"></i>
            </div>
            <div class="status-content">
              <div class="status-title">未开始选课</div>
              <div class="status-count">{{ courseStats.notStartedCourses }}</div>
            </div>
          </div>
          <div class="status-stat-card enrolling">
            <div class="status-icon">
              <i class="el-icon-loading"></i>
            </div>
            <div class="status-content">
              <div class="status-title">选课进行中</div>
              <div class="status-count">{{ courseStats.enrollingCourses }}</div>
            </div>
          </div>
          <div class="status-stat-card ended">
            <div class="status-icon">
              <i class="el-icon-check"></i>
            </div>
            <div class="status-content">
              <div class="status-title">选课已结束</div>
              <div class="status-count">{{ courseStats.enrollmentEndedCourses }}</div>
            </div>
          </div>
          <div class="status-stat-card ongoing">
            <div class="status-icon">
              <i class="el-icon-video-play"></i>
            </div>
            <div class="status-content">
              <div class="status-title">课程进行中</div>
              <div class="status-count">{{ courseStats.ongoingCourses }}</div>
            </div>
          </div>
          <div class="status-stat-card completed">
            <div class="status-icon">
              <i class="el-icon-finished"></i>
            </div>
            <div class="status-content">
              <div class="status-title">课程已完成</div>
              <div class="status-count">{{ courseStats.completedCourses }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 各书院课程统计 -->
      <div class="college-stats-section">
        <h3 class="section-title">各书院课程统计</h3>
        <div class="college-stats-grid">
          <div
            v-for="college in collegeList"
            :key="college.key"
            class="college-stat-card"
            :class="{ 'active': selectedCollege === college.key }"
            @click="handleCollegeFilter(college.key)"
          >
            <div class="college-header">
              <i :class="college.icon" class="college-stat-icon"></i>
              <span class="college-name">{{ college.name }}</span>
              <button
                class="detail-button"
                @click.stop="handleCourseDetail(college.key)"
                title="查看详细"
              >
                <i class="el-icon-view"></i>
              </button>
            </div>
            <div class="college-numbers">
              <div class="stat-item">
                <span class="stat-label">课程总数</span>
                <span class="stat-value">{{ getCollegeCourseCount(college.key) }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">选课中</span>
                <span class="stat-value enrolling">{{ getCollegeEnrollingCount(college.key) }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">选课人数</span>
                <span class="stat-value enrollments">{{ getCollegeEnrollmentCount(college.key) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 课程类型分布 -->
      <div class="course-type-section">
        <h3 class="section-title">课程类型分布</h3>
        <div class="type-distribution">
          <div
            v-for="(type, index) in courseTypes"
            :key="type.key"
            class="type-item"
            :style="{ '--progress': type.percentage + '%' }"
            @click="handleCourseTypeClick(type)"
          >
            <div class="type-header">
              <span class="type-name">{{ type.name }}</span>
              <span class="type-count">{{ type.count }}门</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: type.percentage + '%' }"></div>
            </div>
            <div class="type-percentage">{{ type.percentage }}%</div>
          </div>
        </div>
      </div>

      <!-- 最近课程列表 -->
      <div class="recent-courses-section">
        <div class="section-header">
          <h3 class="section-title">最近课程</h3>
          <el-button
            type="text"
            size="small"
            @click="showAllCourses"
            class="view-all-btn"
          >
            全部
            <i class="el-icon-arrow-right"></i>
          </el-button>
        </div>
        <div class="courses-list">
          <div
            v-for="course in recentCourses"
            :key="course.courseId"
            class="course-item"
          >
            <div class="course-info">
              <div class="course-name">{{ course.courseName }}</div>
              <div class="course-meta">
                <span class="course-college">{{ course.organizer }}</span>
                <span class="course-type">{{ getCourseCategoryName(course.courseType) }}</span>
                <span class="course-date">{{ formatDate(course.startTime) }}</span>
              </div>
            </div>
            <div class="course-status" :class="'status-' + computeCourseStatus(course).replace('选课', '').replace('进行中', 'ongoing').replace('未开始', 'not-started').replace('已截止', 'ended')">
              {{ computeCourseStatus(course) }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 其他弹窗组件省略,与原文件保持一致 -->
  </div>
</template>

<script>
import {
  getCourseOverview,
  getCollegeCourseStats,
  getCourseTypeDistribution,
  getCourseStatsByCollege,
  getCollegeCourses
} from '@/api/system/courseStatistics'
import { listCourses, getCourseBookings } from '@/api/system/courses'
import * as echarts from 'echarts'

export default {
  name: 'CollegeCourse',
  data() {
    return {
      selectedCollege: 'all',
      collegeList: [
        { key: 'dayu', name: '大煜书院', icon: 'el-icon-school' },
        { key: 'bochuan', name: '伯川书院', icon: 'el-icon-school' },
        { key: 'lingxi', name: '令希书院', icon: 'el-icon-school' },
        { key: 'houde', name: '厚德书院', icon: 'el-icon-school' },
        { key: 'zhixing', name: '知行书院', icon: 'el-icon-school' },
        { key: 'duxue', name: '笃学书院', icon: 'el-icon-school' },
        { key: 'qiushi', name: '求实书院', icon: 'el-icon-school' }
      ],
      courseStats: {
        totalCourses: 0,
        notStartedCourses: 0,
        enrollingCourses: 0,
        enrollmentEndedCourses: 0,
        ongoingCourses: 0,
        completedCourses: 0,
        totalEnrollments: 0
      },
      collegeCourseData: {},
      courseTypes: [
        { key: '1', name: '人格塑造与价值引领课程类', count: 0, percentage: 0 },
        { key: '2', name: '知识融合与思维进阶课程类', count: 0, percentage: 0 },
        { key: '3', name: '能力锻造与实践创新课程类', count: 0, percentage: 0 },
        { key: '4', name: '社会责任与领军意识课程类', count: 0, percentage: 0 }
      ],
      recentCourses: [],
      courseCountChart: null,
      enrollmentCountChart: null
    }
  },
  methods: {
    async handleCollegeFilter(collegeKey) {
      this.selectedCollege = collegeKey
      await this.loadCourseStatsByCollege(collegeKey)
    },
    getCollegeName(collegeKey) {
      if (collegeKey === 'all') return '全部书院'
      const college = this.collegeList.find(item => item.key === collegeKey)
      return college ? college.name : ''
    },
    getCollegeCourseCount(collegeKey) {
      if (collegeKey === 'all') return this.courseStats.totalCourses
      return this.collegeCourseData[collegeKey]?.totalCourses || 0
    },
    getCollegeEnrollingCount(collegeKey) {
      if (collegeKey === 'all') return this.courseStats.enrollingCourses
      return this.collegeCourseData[collegeKey]?.enrollingCourses || 0
    },
    getCollegeEnrollmentCount(collegeKey) {
      if (collegeKey === 'all') return this.courseStats.totalEnrollments
      return this.collegeCourseData[collegeKey]?.enrollments || 0
    },
    formatDate(dateTime) {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },
    getCourseCategoryName(category) {
      const categoryValue = String(category)
      const categoryMap = {
        '1': '人格塑造与价值引领课程类',
        '2': '知识融合与思维进阶课程类',
        '3': '能力锻造与实践创新课程类',
        '4': '社会责任与领军意识课程类'
      }
      return categoryMap[categoryValue] || '其他'
    },
    computeCourseStatus(course) {
      const now = new Date()
      const start = course.courseStart ? new Date(course.courseStart) : null
      const deadline = course.courseDeadline ? new Date(course.courseDeadline) : null

      if (start && now < start) {
        return '选课未开始'
      }

      if (start && deadline && now >= start && now <= deadline) {
        return '选课进行中'
      }

      if (deadline && now > deadline) {
        return '选课已截止'
      }

      return '选课未开始'
    },
    async loadCourseStats() {
      try {
        const overviewResponse = await getCourseOverview()
        if (overviewResponse.code === 200) {
          this.courseStats = {
            totalCourses: overviewResponse.data.totalCourses || 0,
            notStartedCourses: overviewResponse.data.notStartedCourses || 0,
            enrollingCourses: overviewResponse.data.enrollingCourses || 0,
            enrollmentEndedCourses: overviewResponse.data.enrollmentEndedCourses || 0,
            ongoingCourses: overviewResponse.data.ongoingCourses || 0,
            completedCourses: overviewResponse.data.completedCourses || 0,
            totalEnrollments: overviewResponse.data.totalEnrollments || 0
          }
        }

        const collegeStatsResponse = await getCollegeCourseStats()
        if (collegeStatsResponse.code === 200) {
          const backendCollegeData = collegeStatsResponse.data.collegeData
          this.collegeCourseData = {}

          const collegeNameToKey = {
            '大煜书院': 'dayu',
            '伯川书院': 'bochuan',
            '令希书院': 'lingxi',
            '厚德书院': 'houde',
            '知行书院': 'zhixing',
            '笃学书院': 'duxue',
            '求实书院': 'qiushi'
          }

          for (const [collegeName, stats] of Object.entries(backendCollegeData)) {
            const collegeKey = collegeNameToKey[collegeName]
            if (collegeKey) {
              this.collegeCourseData[collegeKey] = stats
            }
          }
        }

        const typeDistributionResponse = await getCourseTypeDistribution()
        if (typeDistributionResponse.code === 200) {
          const typeDistribution = typeDistributionResponse.data.typeDistribution
          this.courseTypes = [
            { key: '1', name: '人格塑造与价值引领课程类', count: typeDistribution['人格塑造与价值引领课程类'] || 0, percentage: 0 },
            { key: '2', name: '知识融合与思维进阶课程类', count: typeDistribution['知识融合与思维进阶课程类'] || 0, percentage: 0 },
            { key: '3', name: '能力锻造与实践创新课程类', count: typeDistribution['能力锻造与实践创新课程类'] || 0, percentage: 0 },
            { key: '4', name: '社会责任与领军意识课程类', count: typeDistribution['社会责任与领军意识课程类'] || 0, percentage: 0 }
          ]

          const total = this.courseTypes.reduce((sum, type) => sum + type.count, 0)
          this.courseTypes.forEach(type => {
            type.percentage = total > 0 ? Math.round((type.count / total) * 100 * 10) / 10 : 0
          })
        }

        const recentCoursesResponse = await listCourses({ pageNum: 1, pageSize: 10 })
        if (recentCoursesResponse.code === 200) {
          const allCourses = recentCoursesResponse.rows || []
          this.recentCourses = allCourses
            .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
            .slice(0, 3)
        }

        this.initCourseCharts()
      } catch (error) {
        console.error('加载课程统计数据失败:', error)
        this.$message.error('加载课程统计数据失败')
      }
    },
    async loadCourseStatsByCollege(collegeKey) {
      try {
        if (collegeKey === 'all') {
          await this.loadCourseStats()
          return
        }

        const collegeName = this.getCollegeName(collegeKey)
        const response = await getCourseStatsByCollege(collegeName)
        if (response.code === 200) {
          this.courseStats = {
            totalCourses: response.data.totalCourses || 0,
            notStartedCourses: response.data.notStartedCourses || 0,
            enrollingCourses: response.data.enrollingCourses || 0,
            enrollmentEndedCourses: response.data.enrollmentEndedCourses || 0,
            ongoingCourses: response.data.ongoingCourses || 0,
            completedCourses: response.data.completedCourses || 0,
            totalEnrollments: response.data.totalEnrollments || 0
          }

          if (response.data.typeDistribution) {
            const typeDistribution = response.data.typeDistribution
            this.courseTypes = [
              { key: '1', name: '人格塑造与价值引领课程类', count: typeDistribution['人格塑造与价值引领课程类'] || 0, percentage: 0 },
              { key: '2', name: '知识融合与思维进阶课程类', count: typeDistribution['知识融合与思维进阶课程类'] || 0, percentage: 0 },
              { key: '3', name: '能力锻造与实践创新课程类', count: typeDistribution['能力锻造与实践创新课程类'] || 0, percentage: 0 },
              { key: '4', name: '社会责任与领军意识课程类', count: typeDistribution['社会责任与领军意识课程类'] || 0, percentage: 0 }
            ]

            const total = this.courseTypes.reduce((sum, type) => sum + type.count, 0)
            this.courseTypes.forEach(type => {
              type.percentage = total > 0 ? Math.round((type.count / total) * 100 * 10) / 10 : 0
            })
          }
        }
      } catch (error) {
        console.error('加载书院课程统计数据失败:', error)
        this.$message.error('加载书院课程统计数据失败')
      }
    },
    initCourseCharts() {
      this.$nextTick(() => {
        this.initCourseCountChart()
        this.initEnrollmentCountChart()
      })
    },
    initCourseCountChart() {
      if (this.courseCountChart) {
        this.courseCountChart.dispose()
      }

      const chartDom = this.$refs.courseCountChart
      if (!chartDom) return

      this.courseCountChart = echarts.init(chartDom)

      const allColleges = [
        { key: 'dayu', name: '大煜书院' },
        { key: 'bochuan', name: '伯川书院' },
        { key: 'lingxi', name: '令希书院' },
        { key: 'houde', name: '厚德书院' },
        { key: 'zhixing', name: '知行书院' },
        { key: 'duxue', name: '笃学书院' },
        { key: 'qiushi', name: '求实书院' }
      ]

      const collegeData = this.collegeCourseData || {}
      const data = allColleges.map(college => ({
        name: college.name,
        value: collegeData[college.key]?.totalCourses || 0
      }))

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: '5%',
          top: 'middle',
          width: '20%',
          textStyle: {
            fontSize: 12,
            fontWeight: 'bold',
            color: '#374151'
          }
        },
        series: [
          {
            name: '课程数',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            color: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16'],
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}：{c}',
              fontSize: 12,
              color: '#374151',
              fontWeight: 'bold'
            },
            labelLine: {
              show: true,
              length: 15,
              length2: 10,
              smooth: true,
              lineStyle: {
                color: '#999'
              }
            },
            data: data
          }
        ]
      }

      this.courseCountChart.setOption(option)
    },
    initEnrollmentCountChart() {
      if (this.enrollmentCountChart) {
        this.enrollmentCountChart.dispose()
      }

      const chartDom = this.$refs.enrollmentCountChart
      if (!chartDom) return

      this.enrollmentCountChart = echarts.init(chartDom)

      const allColleges = [
        { key: 'dayu', name: '大煜书院' },
        { key: 'bochuan', name: '伯川书院' },
        { key: 'lingxi', name: '令希书院' },
        { key: 'houde', name: '厚德书院' },
        { key: 'zhixing', name: '知行书院' },
        { key: 'duxue', name: '笃学书院' },
        { key: 'qiushi', name: '求实书院' }
      ]

      const collegeData = this.collegeCourseData || {}
      const data = allColleges.map(college => ({
        name: college.name,
        value: collegeData[college.key]?.enrollments || 0
      }))

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: '5%',
          top: 'middle',
          width: '20%',
          textStyle: {
            fontSize: 12,
            fontWeight: 'bold',
            color: '#374151'
          }
        },
        series: [
          {
            name: '选课人数',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            color: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16'],
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}：{c}',
              fontSize: 12,
              color: '#374151',
              fontWeight: 'bold'
            },
            labelLine: {
              show: true,
              length: 15,
              length2: 10,
              smooth: true,
              lineStyle: {
                color: '#999'
              }
            },
            data: data
          }
        ]
      }

      this.enrollmentCountChart.setOption(option)
    },
    handleCourseDetail(collegeKey) {
      // 实现课程详情逻辑
    },
    handleCourseTypeClick(type) {
      // 实现课程类型点击逻辑
    },
    showAllCourses() {
      // 实现显示所有课程逻辑
    }
  },
  mounted() {
    this.loadCourseStats()
  }
}
</script>

<style scoped src="./styles/common.css"></style>
<style scoped src="./styles/course.css"></style>

