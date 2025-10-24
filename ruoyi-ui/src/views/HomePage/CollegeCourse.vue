<template>
  <div class="app-container">
    <!-- 加载动画 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner">
        <i class="el-icon-loading"></i>
        <p>加载中...</p>
      </div>
    </div>

    <!-- 主内容 -->
    <div v-show="!loading" class="content-wrapper fade-in-content">
      <!-- 课程统计图表 -->
      <div class="course-charts-section fade-in" style="animation-delay: 0.1s">
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

      <!-- 各书院课程统计 -->
      <div class="college-stats-section fade-in" style="animation-delay: 0.2s">
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

      <!-- 课程类型分布和最近课程 - 左右布局 -->
      <div class="course-content-row fade-in" style="animation-delay: 0.3s">
        <!-- 课程类型分布 -->
        <div class="course-type-section">
          <div class="section-header">
            <h3 class="section-title">课程类型分布</h3>
          </div>
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
                  <span class="course-type">{{ getCourseTypeName(course.courseType) }}</span>
                  <span class="course-date">{{ formatDate(course.startTime) }} - {{ formatDate(course.endTime) }}</span>
                </div>
              </div>
              <div class="course-status" :class="getCourseStatusClass(computeCourseStatus(course))">
                {{ computeCourseStatus(course) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 所有课程列表弹窗 -->
    <el-dialog
      :title="getCourseDialogTitle()"
      :visible.sync="allCoursesDialogVisible"
      width="80%"
      :before-close="handleAllCoursesDialogClose"
      class="all-courses-dialog"
    >
      <div class="dialog-content">
        <!-- 课程统计信息 -->
        <div class="all-courses-stats">
          <div class="all-courses-stats-card">
            <div class="stat-item">
              <div class="stat-number">{{ allCoursesStats.total }}</div>
              <div class="stat-label">课程总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ allCoursesStats.ongoing }}</div>
              <div class="stat-label">课程进行中</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ allCoursesStats.ended }}</div>
              <div class="stat-label">课程已结束</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ allCoursesStats.enrollments }}</div>
              <div class="stat-label">参与人数</div>
            </div>
          </div>
        </div>

        <!-- 课程列表表格 -->
        <div class="all-courses-table-container">
          <el-table
            :data="allCoursesList"
            v-loading="allCoursesLoading"
            element-loading-text="加载中..."
            class="all-courses-table"
            stripe
            :header-cell-style="{
              'background': '#f8fafc',
              'color': '#2d3540',
              'font-weight': '600',
              'border-bottom': '2px solid #e2e8f0'
            }"
          >
            <el-table-column
              type="index"
              label="序号"
              width="60"
              align="center"
            />
            <el-table-column
              prop="courseName"
              label="课程名称"
              min-width="200"
              show-overflow-tooltip
            />
            <el-table-column
              prop="organizer"
              label="组织单位"
              min-width="120"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag size="small" type="primary" effect="plain">
                  {{ scope.row.organizer }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="courseCategory"
              label="课程类型"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag size="small" type="success" effect="plain">
                  {{ getCourseTypeName(scope.row.courseType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="courseLocation"
              label="课程地点"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column
              prop="startTime"
              label="课程开始"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column
              prop="endTime"
              label="课程结束"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column
              label="选课情况"
              min-width="120"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.courseCapacity || 0 }}/{{ scope.row.courseTotalCapacity || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="status"
              label="状态"
              min-width="120"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag
                  :type="getCourseStatusTagType(scope.row.status)"
                  size="small"
                  effect="dark"
                >
                  {{ computeCourseStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              min-width="100"
              align="center"
              fixed="right"
            >
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  @click="handleViewStudents(scope.row)"
                  style="color: #3b82f6; font-weight: 500;"
                >
                  已选学生
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页组件 -->
        <el-pagination
          @size-change="handleAllCoursesSizeChange"
          @current-change="handleAllCoursesCurrentChange"
          :current-page="allCoursesQueryParams.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="allCoursesQueryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="allCoursesTotal"
          class="custom-pagination"
        />
      </div>
    </el-dialog>

    <!-- 学生列表对话框 -->
    <el-dialog
      title="预约课程学生列表"
      :visible.sync="studentDialogVisible"
      width="1200px"
      append-to-body
      class="student-dialog">

      <!-- 统计信息卡片 -->
      <div class="student-stats">
        <div class="stats-card">
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.total }}</div>
            <div class="stat-label">总报名人数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.notSubmitted }}</div>
            <div class="stat-label">未提交</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.pending }}</div>
            <div class="stat-label">未考核</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.approved }}</div>
            <div class="stat-label">已通过</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.rejected }}</div>
            <div class="stat-label">未通过</div>
          </div>
        </div>
      </div>

      <!-- 学生列表表格 -->
      <div class="student-table-container">
        <el-table
          :data="filteredStudentList"
          class="enhanced-student-table full-width-table"
          :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}"
          style="width: 100%">
          <el-table-column label="序号" width="80" align="center">
            <template v-slot="scope">
              <span class="index-badge">
                {{ scope.$index + 1 }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="学号" prop="studentId" min-width="140" align="center">
            <template slot-scope="scope">
              <div class="student-id-container">
                <span class="student-id">{{ scope.row.studentId }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="姓名" prop="studentName" min-width="120" align="center">
            <template slot-scope="scope">
              <div class="student-name-container">
                <span class="student-name">{{ scope.row.studentName || '未知' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属书院" prop="college" min-width="140" align="center">
            <template slot-scope="scope">
              <div class="major-info">
                <span class="major-name">{{ scope.row.college || '未知' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="课程成绩" prop="score" min-width="100" align="center">
            <template slot-scope="scope">
              <div class="score-container">
                <span class="score-text" :class="getScoreClass(scope.row.score)">
                  {{ scope.row.score !== null && scope.row.score !== undefined ? scope.row.score : '-' }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="选课时间" prop="bookAt" min-width="160" align="center">
            <template slot-scope="scope">
              <div class="booked_at">
                <i class="el-icon-time"></i>
                <span class="time-text">{{ parseTime(scope.row.bookAt, '{y}-{m}-{d} {h}:{i}') }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="考核状态" prop="status" min-width="120" align="center">
            <template slot-scope="scope">
              <el-tag
                :type="getStudentStatusTagType(scope.row.status)"
                effect="dark"
                class="status-tag">
                {{ getStudentStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 对话框底部 -->
      <div slot="footer" class="dialog-footer">
        <div class="footer-left">
          <div class="total-info">共{{ filteredStudentList.length }}名学生</div>
        </div>
        <div class="footer-right">
          <el-button @click="studentDialogVisible = false" class="close-btn">
            关闭
          </el-button>
        </div>
      </div>
    </el-dialog>
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
      loading: true,
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
      enrollmentCountChart: null,
      allCoursesDialogVisible: false,
      allCoursesDialogCollege: 'all', // 弹窗中显示的书院
      allCoursesDialogType: null, // 弹窗中显示的课程类型
      allCoursesList: [],
      allCoursesLoading: false,
      allCoursesTotal: 0,
      allCoursesQueryParams: {
        pageNum: 1,
        pageSize: 10,
        organizer: null,
        courseType: null
      },
      allCoursesStats: {
        total: 0,
        ongoing: 0,
        ended: 0,
        enrollments: 0
      },
      // 学生列表对话框
      studentDialogVisible: false,
      currentCourse: null,
      studentList: [],
      filteredStudentList: [],
      studentFilterParams: {
        studentId: '',
        studentName: '',
        college: '',
        status: ''
      },
      // 缓存全部书院的初始数据
      cachedAllData: {
        courseStats: null,
        collegeCourseData: null,
        courseTypes: null,
        recentCourses: null
      },
      studentStats: {
        total: 0,
        notSubmitted: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      }
    }
  },
  methods: {
    async handleCollegeFilter(collegeKey) {
      // 如果点击的是已选中的书院，则恢复为显示全部
      if (this.selectedCollege === collegeKey) {
        this.selectedCollege = 'all'
        // 直接使用缓存的数据，不重新加载
        this.restoreCachedAllData()
      } else {
        this.selectedCollege = collegeKey
        await this.loadCourseStatsByCollege(collegeKey)
      }
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
    formatDateTime(dateTime) {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      })
    },
    getCourseTypeName(category) {
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
    getCourseStatusClass(status) {
      // 将课程状态映射到CSS class
      const classMap = {
        '选课未开始': 'status-not-started',
        '选课进行中': 'status-enrolling',
        '选课已截止': 'status-deadline',
        '课程进行中': 'status-ongoing',
        '课程已结束': 'status-completed'
      }
      return classMap[status] || 'status-not-started'
    },
    async loadCourseStats() {
      this.loading = true
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
            .slice(0, 5)
        }

        // 缓存全部书院的数据
        this.cacheAllData()

      } catch (error) {
        console.error('加载课程统计数据失败:', error)
        this.$message.error('加载课程统计数据失败')
      } finally {
        setTimeout(() => {
          this.loading = false
          // 等待 DOM 更新后再初始化图表
          this.$nextTick(() => {
            this.initCourseCharts()
          })
        }, 300)
      }
    },
    async loadCourseStatsByCollege(collegeKey) {
      try {
        if (collegeKey === 'all') {
          // 如果有缓存，使用缓存数据；否则重新加载
          if (this.cachedAllData.courseStats) {
            this.restoreCachedAllData()
          } else {
            await this.loadCourseStats()
          }
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

          // 加载该书院的最近课程
          const recentCoursesResponse = await listCourses({ pageNum: 1, pageSize: 10, organizer: collegeName })
          if (recentCoursesResponse.code === 200) {
            const allCourses = recentCoursesResponse.rows || []
            this.recentCourses = allCourses
              .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
              .slice(0, 5)
          }

          // 更新图表
          this.$nextTick(() => {
            this.initCourseCharts()
          })
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

      const totalCourses = this.courseStats.totalCourses || 0

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
        graphic: [
          {
            type: 'text',
            left: 'center',
            top: '42%',
            style: {
              text: '总课程数',
              textAlign: 'center',
              fill: '#6b7280',
              fontSize: 14,
              fontWeight: 'normal'
            }
          },
          {
            type: 'text',
            left: 'center',
            top: '52%',
            style: {
              text: totalCourses.toString(),
              textAlign: 'center',
              fill: '#111827',
              fontSize: 28,
              fontWeight: 'bold'
            }
          }
        ],
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

      const totalEnrollments = this.courseStats.totalEnrollments || 0

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
        graphic: [
          {
            type: 'text',
            left: 'center',
            top: '42%',
            style: {
              text: '总选课人数',
              textAlign: 'center',
              fill: '#6b7280',
              fontSize: 14,
              fontWeight: 'normal'
            }
          },
          {
            type: 'text',
            left: 'center',
            top: '52%',
            style: {
              text: totalEnrollments.toString(),
              textAlign: 'center',
              fill: '#111827',
              fontSize: 28,
              fontWeight: 'bold'
            }
          }
        ],
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
      // 打开该书院的课程列表弹窗
      this.allCoursesDialogVisible = true
      this.allCoursesDialogCollege = collegeKey
      this.allCoursesDialogType = null
      this.allCoursesQueryParams.pageNum = 1
      this.allCoursesQueryParams.pageSize = 10
      this.allCoursesQueryParams.courseType = null
      // 设置为指定书院的课程
      if (collegeKey !== 'all') {
        this.allCoursesQueryParams.organizer = this.getCollegeName(collegeKey)
      } else {
        this.allCoursesQueryParams.organizer = null
      }
      this.loadAllCoursesList()
    },
    handleCourseTypeClick(type) {
      // 打开该课程类型的课程列表弹窗
      this.allCoursesDialogVisible = true
      this.allCoursesDialogCollege = 'all'
      this.allCoursesDialogType = type
      this.allCoursesQueryParams.pageNum = 1
      this.allCoursesQueryParams.pageSize = 10
      this.allCoursesQueryParams.organizer = this.selectedCollege !== 'all' ? this.getCollegeName(this.selectedCollege) : null
      this.allCoursesQueryParams.courseType = type.key
      this.loadAllCoursesList()
    },
    showAllCourses() {
      this.allCoursesDialogVisible = true
      this.allCoursesDialogCollege = this.selectedCollege
      this.allCoursesDialogType = null
      this.allCoursesQueryParams.pageNum = 1
      this.allCoursesQueryParams.pageSize = 10
      this.allCoursesQueryParams.courseType = null
      // 根据当前筛选的书院设置organizer
      if (this.selectedCollege !== 'all') {
        this.allCoursesQueryParams.organizer = this.getCollegeName(this.selectedCollege)
      } else {
        this.allCoursesQueryParams.organizer = null
      }
      this.loadAllCoursesList()
    },
    async loadAllCoursesList() {
      try {
        this.allCoursesLoading = true
        const response = await this.getAllCourses(this.allCoursesQueryParams)
        if (response.code === 200) {
          this.allCoursesList = response.rows || []
          this.allCoursesTotal = response.total || 0
          this.calculateAllCoursesStats(this.allCoursesList)
        } else {
          this.$message.error('获取课程列表失败')
          this.allCoursesList = []
          this.allCoursesTotal = 0
        }
      } catch (error) {
        console.error('加载所有课程列表失败:', error)
        this.$message.error('加载所有课程列表失败')
        this.allCoursesList = []
        this.allCoursesTotal = 0
      } finally {
        this.allCoursesLoading = false
      }
    },
    async getAllCourses(params) {
      try {
        const queryParams = {
          pageNum: params.pageNum,
          pageSize: params.pageSize
        }

        // 如果有指定书院，添加organizer参数
        if (params.organizer) {
          queryParams.organizer = params.organizer
        }

        // 如果有指定课程类型，添加courseType参数
        if (params.courseType) {
          queryParams.courseType = params.courseType
        }

        const response = await listCourses(queryParams)
        return response
      } catch (error) {
        console.error('获取课程列表失败:', error)
        throw error
      }
    },
    calculateAllCoursesStats(courses) {
      this.allCoursesStats = {
        total: courses.length,
        ongoing: 0,
        ended: 0,
        enrollments: 0
      }

      courses.forEach(course => {
        // 根据status统计各种状态
        const statusText = this.computeCourseStatusText(course.status)
        
        // 课程进行中（包含未开始选课、选课进行中、选课已截止、课程进行中）
        if (statusText === '选课未开始' || statusText === '选课进行中' || 
            statusText === '选课已截止' || statusText === '课程进行中') {
          this.allCoursesStats.ongoing++
        } else if (statusText === '课程已结束') {
          this.allCoursesStats.ended++
        }
        
        // 累加参与人数（courseCapacity 是已选人数）
        this.allCoursesStats.enrollments += (course.courseCapacity || 0)
      })
    },
    handleAllCoursesSizeChange(val) {
      this.allCoursesQueryParams.pageSize = val
      this.allCoursesQueryParams.pageNum = 1
      this.loadAllCoursesList()
    },
    handleAllCoursesCurrentChange(val) {
      this.allCoursesQueryParams.pageNum = val
      this.loadAllCoursesList()
    },
    handleAllCoursesDialogClose(done) {
      this.allCoursesDialogVisible = false
      this.allCoursesDialogCollege = 'all'
      this.allCoursesDialogType = null
      done()
    },
    getCourseDialogTitle() {
      if (this.allCoursesDialogType) {
        // 如果是按课程类型筛选
        return this.allCoursesDialogType.name + ' - 课程列表'
      } else if (this.allCoursesDialogCollege === 'all') {
        // 如果是所有书院
        return '所有课程列表'
      } else {
        // 如果是特定书院
        return this.getCollegeName(this.allCoursesDialogCollege) + ' - 课程列表'
      }
    },
    computeCourseStatusText(status) {
      const statusMap = {
        '未开始': '选课未开始',
        '选课中': '选课进行中',
        '已截止': '选课已截止',
        '进行中': '课程进行中',
        '已结束': '课程已结束'
      }
      return statusMap[status] || status || '未知'
    },
    getCourseStatusTagType(status) {
      const typeMap = {
        '未开始': 'info',
        '选课中': 'primary',
        '已截止': 'warning',
        '进行中': 'success',
        '已结束': 'info'
      }
      return typeMap[status] || 'info'
    },
    // 查看学生列表
    handleViewStudents(row) {
      this.currentCourse = row
      this.studentDialogVisible = true
      this.loadStudentList(row.courseId)
    },
    // 加载学生列表数据
    loadStudentList(courseId) {
      getCourseBookings(courseId).then(response => {
        this.studentList = response.rows || []
        this.filteredStudentList = [...this.studentList]
        this.calculateStudentStats()
      }).catch(error => {
        console.error('获取学生列表失败:', error)
        this.$message.error('获取学生列表失败')
        this.studentList = []
        this.filteredStudentList = []
        this.calculateStudentStats()
      })
    },
    // 计算学生统计信息
    calculateStudentStats() {
      this.studentStats.total = this.studentList.length
      this.studentStats.notSubmitted = this.studentList.filter(s => s.status === 'notSubmitted' || s.status === '未提交').length
      this.studentStats.pending = this.studentList.filter(s => s.status === 'pending' || s.status === '未考核' || s.status === '未审核').length
      this.studentStats.approved = this.studentList.filter(s => s.status === 'approved' || s.status === '已通过').length
      this.studentStats.rejected = this.studentList.filter(s => s.status === 'rejected' || s.status === '未通过').length
    },
    // 学生筛选
    handleStudentFilter() {
      this.filteredStudentList = this.studentList.filter(student => {
        const matchStudentId = !this.studentFilterParams.studentId ||
          (student.studentId && student.studentId.includes(this.studentFilterParams.studentId))

        const matchStudentName = !this.studentFilterParams.studentName ||
          (student.studentName && student.studentName.includes(this.studentFilterParams.studentName))

        const matchCollege = !this.studentFilterParams.college ||
          (student.college && student.college.includes(this.studentFilterParams.college))

        const matchStatus = !this.studentFilterParams.status ||
          this.getStudentStatusText(student.status) === this.getStudentStatusText(this.studentFilterParams.status)

        return matchStudentId && matchStudentName && matchCollege && matchStatus
      })
    },
    // 重置学生筛选
    resetStudentFilter() {
      this.studentFilterParams = {
        studentId: '',
        studentName: '',
        college: '',
        status: ''
      }
      this.filteredStudentList = [...this.studentList]
    },
    // 获取学生状态标签类型
    getStudentStatusTagType(status) {
      const statusMap = {
        'notSubmitted': 'info',
        'pending': 'warning',
        'approved': 'success',
        'rejected': 'danger',
        '未提交': 'info',
        '未审核': 'warning',
        '未考核': 'warning',
        '已通过': 'success',
        '未通过': 'danger'
      }
      return statusMap[status] || 'info'
    },
    // 获取学生状态文本
    getStudentStatusText(status) {
      const statusMap = {
        'notSubmitted': '未提交',
        'pending': '未考核',
        'approved': '已通过',
        'rejected': '未通过',
        '未提交': '未提交',
        '未审核': '未考核',
        '未考核': '未考核',
        '已通过': '已通过',
        '未通过': '未通过'
      }
      return statusMap[status] || '未知'
    },
    // 获取成绩样式类
    getScoreClass(score) {
      if (score === null || score === undefined) return ''
      if (score >= 90) return 'score-excellent'
      if (score >= 80) return 'score-good'
      if (score >= 70) return 'score-medium'
      if (score >= 60) return 'score-pass'
      return 'score-fail'
    },
    // 时间格式化（parseTime方法）
    parseTime(time, pattern) {
      if (!time) return ''
      const date = new Date(time)
      const formatObj = {
        y: date.getFullYear(),
        m: (date.getMonth() + 1).toString().padStart(2, '0'),
        d: date.getDate().toString().padStart(2, '0'),
        h: date.getHours().toString().padStart(2, '0'),
        i: date.getMinutes().toString().padStart(2, '0'),
        s: date.getSeconds().toString().padStart(2, '0')
      }
      return pattern.replace(/{([ymdhis])+}/g, (result, key) => {
        return formatObj[key]
      })
    },
    cacheAllData() {
      // 深拷贝当前数据到缓存
      this.cachedAllData = {
        courseStats: JSON.parse(JSON.stringify(this.courseStats)),
        collegeCourseData: JSON.parse(JSON.stringify(this.collegeCourseData)),
        courseTypes: JSON.parse(JSON.stringify(this.courseTypes)),
        recentCourses: JSON.parse(JSON.stringify(this.recentCourses))
      }
    },
    restoreCachedAllData() {
      // 从缓存恢复数据
      if (this.cachedAllData.courseStats) {
        this.courseStats = JSON.parse(JSON.stringify(this.cachedAllData.courseStats))
        this.collegeCourseData = JSON.parse(JSON.stringify(this.cachedAllData.collegeCourseData))
        this.courseTypes = JSON.parse(JSON.stringify(this.cachedAllData.courseTypes))
        this.recentCourses = JSON.parse(JSON.stringify(this.cachedAllData.recentCourses))
        // 重新初始化图表
        this.$nextTick(() => {
          this.initCourseCharts()
        })
      } else {
        // 如果没有缓存，重新加载
        this.loadCourseStats()
      }
    }
  },
  mounted() {
    this.loadCourseStats()
  }
}
</script>

<style scoped src="./styles/common.css"></style>
<style scoped src="./styles/course.css"></style>

<style scoped>
/* 学生弹窗样式 */
.student-dialog .el-dialog {
  border-radius: 12px;
}

.student-stats {
  margin-bottom: 20px;
}

.student-stats .stats-card {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  background: #f8fafc;
  padding: 20px;
  border-radius: 8px;
}

.student-stats .stat-item {
  text-align: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.student-stats .stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #10b981;
  margin-bottom: 8px;
}

.student-stats .stat-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.student-table-container {
  margin-bottom: 20px;
}

.enhanced-student-table {
  width: 100%;
}

.index-badge {
  display: inline-block;
  width: 30px;
  height: 30px;
  line-height: 30px;
  background: #3b82f6;
  color: white;
  border-radius: 50%;
  font-weight: 600;
}

.student-id-container,
.student-name-container,
.major-info,
.booked_at,
.score-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.student-id,
.student-name,
.major-name {
  font-weight: 500;
  color: #374151;
}

.time-text {
  color: #6b7280;
  font-size: 13px;
}

.score-text {
  font-size: 15px;
  font-weight: 600;
  font-family: 'Monaco', 'Menlo', monospace;
}

.score-excellent {
  color: #67C23A;
}

.score-good {
  color: #409EFF;
}

.score-medium {
  color: #E6A23C;
}

.score-pass {
  color: #909399;
}

.score-fail {
  color: #F56C6C;
}

.status-tag {
  font-weight: 500;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.footer-left .total-info {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.close-btn {
  background: #6b7280;
  color: white;
}
</style>

