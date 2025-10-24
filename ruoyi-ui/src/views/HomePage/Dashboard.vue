<template>
  <div class="dashboard-container">
    <!-- 加载动画 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner">
        <i class="el-icon-loading"></i>
        <p>加载中...</p>
      </div>
    </div>

    <!-- 主内容 -->
    <div v-show="!loading" class="dashboard-content">
      <div class="dashboard-header fade-in">
        <h1 class="dashboard-title">书院学生管理系统</h1>
        <p class="dashboard-subtitle">实时显示各书院运营数据</p>
      </div>

      <!-- 快速统计卡片 -->
      <div class="quick-stats fade-in" style="animation-delay: 0.1s">
      <div class="stat-card activity-card clickable" @click="navigateTo('activity')">
        <div class="stat-icon">
          <i class="el-icon-s-promotion"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">书院活动</div>
          <div class="stat-value">{{ activityStats.totalActivities }}</div>
          <div class="stat-detail">
            <span class="ongoing">{{ activityStats.ongoingActivities }} 进行中</span>
            <span class="completed">{{ activityStats.completedActivities }} 已完成</span>
          </div>
        </div>
        <div class="stat-arrow">
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>

      <div class="stat-card course-card clickable" @click="navigateTo('course')">
        <div class="stat-icon">
          <i class="el-icon-reading"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">书院课程</div>
          <div class="stat-value">{{ courseStats.totalCourses }}</div>
          <div class="stat-detail">
            <span class="enrolling">{{ courseStats.enrollingCourses }} 选课中</span>
            <span class="completed">{{ courseStats.completedCourses }} 已完成</span>
          </div>
        </div>
        <div class="stat-arrow">
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>

      <div class="stat-card major-card clickable" @click="navigateTo('major')">
        <div class="stat-icon">
          <i class="el-icon-s-grid"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">专业分流</div>
          <div class="stat-value">{{ majorStats.statusText }}</div>
          <div class="stat-detail">
            <span class="info">{{ majorStats.description }}</span>
          </div>
        </div>
        <div class="stat-arrow">
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>
    </div>

      <!-- 各书院概览 -->
      <div class="colleges-overview fade-in" style="animation-delay: 0.2s">
      <h2 class="section-title">各书院数据一览</h2>
      <div class="colleges-grid">
        <div
          v-for="college in collegeList"
          :key="college.key"
          class="college-overview-card"
        >
          <div class="college-header">
            <i :class="college.icon" class="college-icon"></i>
            <span class="college-name">{{ college.name }}</span>
          </div>
          <div class="college-stats">
            <div class="stat-row">
              <span class="label">活动</span>
              <span class="value">{{ getCollegeActivityCount(college.key) }}</span>
            </div>
            <div class="stat-row">
              <span class="label">课程</span>
              <span class="value">{{ getCollegeCourseCount(college.key) }}</span>
            </div>
            <div class="stat-row">
              <span class="label">参与人数</span>
              <span class="value highlight">{{ getCollegeParticipantCount(college.key) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

      <!-- 最近动态 -->
      <div class="recent-updates fade-in" style="animation-delay: 0.3s">
      <div class="updates-section">
        <div class="section-header">
          <h2 class="section-title">最近活动</h2>
        </div>
        <div class="updates-list">
          <div
            v-for="activity in recentActivities"
            :key="activity.id"
            class="update-item"
          >
            <div class="update-icon activity">
              <i class="el-icon-s-promotion"></i>
            </div>
            <div class="update-content">
              <div class="update-title">{{ activity.name }}</div>
              <div class="update-meta">
                <span>{{ activity.college }}</span>
                <span>{{ formatActivityTime(activity.startTime, activity.endTime) }}</span>
              </div>
            </div>
            <div class="update-status" :class="getActivityStatusClass(activity.status)">
              {{ getStatusText(activity.status) }}
            </div>
          </div>
        </div>
      </div>

      <div class="updates-section">
        <div class="section-header">
          <h2 class="section-title">最近课程</h2>
        </div>
        <div class="updates-list">
          <div
            v-for="course in recentCourses"
            :key="course.courseId"
            class="update-item"
          >
            <div class="update-icon course">
              <i class="el-icon-reading"></i>
            </div>
            <div class="update-content">
              <div class="update-title">{{ course.courseName }}</div>
              <div class="update-meta">
                <span>{{ course.organizer }}</span>
                <span>{{ formatDate(course.startTime) }} - {{ formatDate(course.endTime) }}</span>
              </div>
            </div>
            <div class="update-status" :class="getCourseStatusClass(course.status)">
              {{ computeCourseStatus(course) }}
            </div>
          </div>
        </div>
      </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getActivityOverview,
  getCollegeActivityStats,
  getRecentActivities
} from '@/api/system/activityStatistics'
import {
  getCourseOverview,
  getCollegeCourseStats
} from '@/api/system/courseStatistics'
import { listCourses } from '@/api/system/courses'

export default {
  name: 'Dashboard',
  data() {
    return {
      loading: true,
      collegeList: [
        { key: 'dayu', name: '大煜书院', icon: 'el-icon-school' },
        { key: 'bochuan', name: '伯川书院', icon: 'el-icon-school' },
        { key: 'lingxi', name: '令希书院', icon: 'el-icon-school' },
        { key: 'houde', name: '厚德书院', icon: 'el-icon-school' },
        { key: 'zhixing', name: '知行书院', icon: 'el-icon-school' },
        { key: 'duxue', name: '笃学书院', icon: 'el-icon-school' },
        { key: 'qiushi', name: '求实书院', icon: 'el-icon-school' }
      ],
      activityStats: {
        totalActivities: 0,
        ongoingActivities: 0,
        completedActivities: 0,
        totalParticipants: 0
      },
      courseStats: {
        totalCourses: 0,
        enrollingCourses: 0,
        completedCourses: 0
      },
      majorStats: {
        statusText: '已完成',
        description: '2024-2025学年'
      },
      collegeActivityData: {},
      collegeCourseData: {},
      recentActivities: [],
      recentCourses: []
    }
  },
  methods: {
    navigateTo(view) {
      // 根据 view 跳转到对应的菜单路由
      const routeMap = {
        'activity': '/HomePage/CollegeActivity',
        'course': '/HomePage/CollegeCourse',
        'major': '/HomePage/MajorDiversion'
      }
      const path = routeMap[view]
      if (path) {
        this.$router.push(path)
      }
    },
    async loadData() {
      this.loading = true
      try {
        // 加载活动数据
        const activityOverview = await getActivityOverview()
        if (activityOverview.code === 200) {
          this.activityStats = activityOverview.data
        }

        const collegeActivityStats = await getCollegeActivityStats()
        if (collegeActivityStats.code === 200) {
          const backendCollegeData = collegeActivityStats.data.collegeData
          this.collegeActivityData = {}

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
              this.collegeActivityData[collegeKey] = stats
            }
          }
        }

        const recentActivitiesResponse = await getRecentActivities(5)
        if (recentActivitiesResponse.code === 200) {
          this.recentActivities = (recentActivitiesResponse.data.recentActivities || []).slice(0, 5)
        }

        // 加载课程数据
        const courseOverview = await getCourseOverview()
        if (courseOverview.code === 200) {
          this.courseStats = courseOverview.data
        }

        const collegeCourseStats = await getCollegeCourseStats()
        if (collegeCourseStats.code === 200) {
          const backendCollegeData = collegeCourseStats.data.collegeData
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

        const recentCoursesResponse = await listCourses({ pageNum: 1, pageSize: 5 })
        if (recentCoursesResponse.code === 200) {
          this.recentCourses = (recentCoursesResponse.rows || []).slice(0, 5)
        }

        // 模拟专业分流数据（如果有真实API，替换这里）
        // 可以根据实际情况设置状态，比如：
        // this.majorStats.statusText = '未开始' / '进行中' / '已完成'
        // this.majorStats.description = '截止时间：12月31日'

      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('数据加载失败，请刷新重试')
      } finally {
        // 延迟隐藏加载动画，让过渡更自然
        setTimeout(() => {
          this.loading = false
        }, 300)
      }
    },
    getCollegeActivityCount(collegeKey) {
      return this.collegeActivityData[collegeKey]?.totalActivities || 0
    },
    getCollegeCourseCount(collegeKey) {
      return this.collegeCourseData[collegeKey]?.totalCourses || 0
    },
    getCollegeParticipantCount(collegeKey) {
      return this.collegeActivityData[collegeKey]?.participants || 0
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
      const dateStr = date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
      const timeStr = date.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      })
      return `${dateStr} ${timeStr}`
    },
    formatActivityTime(startTime, endTime) {
      // 如果都没有，返回待定
      if (!startTime && !endTime) {
        return '时间待定'
      }

      // 如果有完整的开始和结束时间
      if (startTime && endTime) {
        const startDate = new Date(startTime)
        const endDate = new Date(endTime)

        const dateStr = startDate.toLocaleDateString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit'
        })
        const startTimeStr = startDate.toLocaleTimeString('zh-CN', {
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hour12: false
        })
        const endTimeStr = endDate.toLocaleTimeString('zh-CN', {
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hour12: false
        })

        return `${dateStr} ${startTimeStr}-${endTimeStr}`
      }

      // 如果只有开始时间
      if (startTime) {
        return this.formatDateTime(startTime)
      }

      return '时间待定'
    },
    getStatusText(status) {
      const statusMap = {
        // 数据库存储值 -> 前端显示文本
        '未开始': '报名未开始',
        '可报名': '活动报名中',
        '已截止': '报名已截止',
        '进行中': '活动进行中',
        '已结束': '活动已结束',
        // 兼容旧值
        'not-started': '报名未开始',
        'enrolling': '活动报名中',
        'enroll-ended': '报名已截止',
        'ongoing': '活动进行中',
        'completed': '活动已结束',
        'cancelled': '已取消'
      }
      return statusMap[status] || status
    },
    getActivityStatusClass(status) {
      // 将数据库状态值映射到 CSS class
      const classMap = {
        '未开始': 'status-not-started',
        '可报名': 'status-enrolling',
        '已截止': 'status-deadline',
        '进行中': 'status-ongoing',
        '已结束': 'status-ended',
        // 兼容旧值
        'not-started': 'status-not-started',
        'enrolling': 'status-enrolling',
        'enroll-ended': 'status-deadline',
        'ongoing': 'status-ongoing',
        'completed': 'status-ended'
      }
      return classMap[status] || 'status-not-started'
    },
    computeCourseStatus(course) {
      // 状态映射：数据库状态 -> 前端显示
      const statusMap = {
        '未开始': '选课未开始',
        '选课中': '选课进行中',
        '已截止': '选课已截止',
        '进行中': '课程进行中',
        '已结束': '课程已结束'
      }
      return statusMap[course.status] || course.status || '选课未开始'
    },
    getCourseStatusClass(status) {
      // 根据课程状态返回对应的CSS类名
      const classMap = {
        '未开始': 'status-not-started',
        '选课中': 'status-enrolling',
        '已截止': 'status-deadline',
        '进行中': 'status-ongoing',
        '已结束': 'status-ended'
      }
      return classMap[status] || 'status-not-started'
    }
  },
  mounted() {
    this.loadData()
  }
}
</script>

<style scoped src="./styles/common.css"></style>

<style scoped>
* {
  box-sizing: border-box;
}

.dashboard-container {
  background: #f8fafc;
  min-height: 100vh;
  padding: 20px 12px;
  max-width: 100%;
  width: 100%;
  box-sizing: border-box;
  overflow-x: hidden;
  position: relative;
}

/* 主内容区域 */
.dashboard-content {
  animation: contentFadeIn 0.4s ease;
}

@keyframes contentFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.dashboard-header {
  text-align: center;
  margin-bottom: 40px;
}

.dashboard-title {
  font-size: 32px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 12px 0;
}

.dashboard-subtitle {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
}

/* 快速统计卡片 */
.quick-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(min(220px, 100%), 1fr));
  gap: 16px;
  margin-bottom: 32px;
  width: 100%;
  max-width: 100%;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 14px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
  min-width: 0;
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden;
  position: relative;
  will-change: transform;
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
  border-color: currentColor;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.stat-arrow {
  font-size: 20px;
  color: #d1d5db;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.stat-card.clickable:hover .stat-arrow {
  color: currentColor;
  transform: translateX(4px);
}

.activity-card.clickable:hover {
  border-color: #667eea;
}

.activity-card.clickable:hover .stat-arrow {
  color: #667eea;
}

.course-card.clickable:hover {
  border-color: #10b981;
}

.course-card.clickable:hover .stat-arrow {
  color: #10b981;
}

.major-card.clickable:hover {
  border-color: #f59e0b;
}

.major-card.clickable:hover .stat-arrow {
  color: #f59e0b;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.activity-card .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.course-card .stat-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.major-card .stat-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.participants-card .stat-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.stat-content {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
  line-height: 1.2;
}

.stat-detail {
  display: flex;
  gap: 12px;
  font-size: 12px;
  flex-wrap: wrap;
}

.stat-detail span.ongoing {
  color: #f59e0b;
}

.stat-detail span.completed {
  color: #10b981;
}

.stat-detail span.enrolling {
  color: #3b82f6;
}

.stat-detail span.info {
  color: #6b7280;
}

/* 各书院概览 */
.colleges-overview {
  margin-bottom: 40px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 24px;
}

.colleges-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(min(130px, 100%), 1fr));
  gap: 12px;
  width: 100%;
  max-width: 100%;
}

.college-overview-card {
  background: white;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 0;
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden;
  will-change: transform;
}

.college-overview-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.college-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f3f4f6;
}

.college-icon {
  font-size: 24px;
  color: #3b82f6;
}

.college-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.college-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.stat-row .label {
  color: #6b7280;
}

.stat-row .value {
  font-weight: 600;
  color: #111827;
}

.stat-row .value.highlight {
  color: #3b82f6;
}

/* 最近动态 */
.recent-updates {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(min(320px, 100%), 1fr));
  gap: 16px;
  width: 100%;
  max-width: 100%;
}

.updates-section {
  background: white;
  border-radius: 10px;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  min-width: 0;
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header .section-title {
  font-size: 18px;
  margin: 0;
}

.updates-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.update-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.update-item:hover {
  background-color: #f8fafc;
  transform: translateX(4px);
}

.update-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.update-icon.activity {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.update-icon.course {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.update-content {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.update-title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.update-meta {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  gap: 12px;
}

.update-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.update-status.ongoing {
  background: #fef3c7;
  color: #d97706;
}

.update-status.completed {
  background: #d1fae5;
  color: #059669;
}

.update-status.enrolling {
  background: #dbeafe;
  color: #1d4ed8;
}

/* 课程状态颜色 - 与活动状态颜色对应 */
.update-status.status-not-started {
  background: #f3f4f6;
  color: #6b7280;
}

.update-status.status-enrolling {
  background: #dbeafe;
  color: #1d4ed8;
}

.update-status.status-deadline {
  background: #fef3c7;
  color: #d97706;
}

.update-status.status-ongoing {
  background: #fef3c7;
  color: #d97706;
}

.update-status.status-ended {
  background: #d1fae5;
  color: #059669;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 15px 10px;
  }

  .quick-stats {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .stat-card {
    padding: 16px;
    gap: 12px;
  }

  .colleges-grid {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 12px;
  }

  .college-overview-card {
    padding: 12px;
  }

  .recent-updates {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .updates-section {
    padding: 16px;
  }
}
</style>

