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
        <p class="dashboard-subtitle">实时显示各书院数据</p>
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
            @click="showActivityDetail(activity)"
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

    <!-- 活动详情弹窗 -->
    <el-dialog
      title="活动详情"
      :visible.sync="activityDetailVisible"
      width="800px"
      :before-close="handleDetailClose"
      class="activity-detail-dialog"
    >
      <div class="activity-detail" v-if="selectedActivity">
        <!-- 活动详情展示 -->
        <div class="detail-header">
          <h2>{{ selectedActivity.name || selectedActivity.activityName }}</h2>
          <div class="status-tags">
            <el-tag :type="getActivityDetailStatusTag(selectedActivity)" size="medium" class="status-tag">
              {{ getStatusText(selectedActivity.status) }}
            </el-tag>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="detail-grid">
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-location"></i> 活动地点：</div>
            <div class="detail-value">{{ selectedActivity.location || selectedActivity.activityLocation }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-office-building"></i> 活动类型：</div>
            <div class="detail-value">{{ getActivityTypeName(selectedActivity.type || selectedActivity.activityType) }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-office-building"></i> 组织单位：</div>
            <div class="detail-value">{{ selectedActivity.college || selectedActivity.organizer }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-user"></i> 活动容量：</div>
            <div class="detail-value">
              <span class="capacity-info">
                {{ selectedActivity.capacity || selectedActivity.activityCapacity || 0 }}/{{ selectedActivity.totalCapacity || selectedActivity.activityTotalCapacity }}人
              </span>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-alarm-clock"></i> 报名开始：</div>
            <div class="detail-value">{{ formatDateTime(selectedActivity.signupStartTime || selectedActivity.activityStart) }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-alarm-clock"></i> 报名截止：</div>
            <div class="detail-value">{{ formatDateTime(selectedActivity.signupDeadline || selectedActivity.activityDeadline) }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-time"></i> 活动开始：</div>
            <div class="detail-value">{{ formatDateTime(selectedActivity.startTime) }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-time"></i> 活动结束：</div>
            <div class="detail-value">{{ formatDateTime(selectedActivity.endTime) }}</div>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="detail-section-content">
          <h4 class="section-title"><i class="el-icon-document"></i> 活动描述</h4>
          <div class="section-content">
            <!-- 使用 v-html 渲染富文本内容 -->
            <div class="rich-text-content" v-html="selectedActivity.description || selectedActivity.activityDescription || '暂无描述信息'"></div>
          </div>
        </div>

        <!-- 活动图片展示 -->
        <div class="detail-section-content" v-if="selectedActivity.imageUrl || selectedActivity.pictureUrl">
          <h4 class="section-title"><i class="el-icon-picture"></i> 活动图片</h4>
          <div class="section-content">
            <div class="activity-image-container">
              <el-image
                :src="getActivityImageUrl(selectedActivity.imageUrl || selectedActivity.pictureUrl)"
                :preview-src-list="[getActivityImageUrl(selectedActivity.imageUrl || selectedActivity.pictureUrl)]"
                fit="cover"
                class="activity-image"
              />
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
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
import { getActivities } from '@/api/system/activities'

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
      recentCourses: [],
      // 活动详情弹窗相关
      activityDetailVisible: false,
      selectedActivity: null
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
          console.log('=== 活动统计数据 ===')
          console.log('后端返回的书院活动数据:', backendCollegeData)
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
              console.log(`${collegeName} (${collegeKey}):`, stats)
            }
          }
          console.log('处理后的 collegeActivityData:', this.collegeActivityData)
        }

        const recentActivitiesResponse = await getRecentActivities(5)
        if (recentActivitiesResponse.code === 200) {
          this.recentActivities = (recentActivitiesResponse.data.recentActivities || []).slice(0, 5)
          console.log('=== 最近活动数据 ===')
          console.log('最近活动列表:', this.recentActivities)
          if (this.recentActivities.length > 0) {
            console.log('第一个活动的完整数据:', this.recentActivities[0])
          }
        }

        // 加载课程数据
        const courseOverview = await getCourseOverview()
        if (courseOverview.code === 200) {
          this.courseStats = courseOverview.data
        }

        const collegeCourseStats = await getCollegeCourseStats()
        if (collegeCourseStats.code === 200) {
          const backendCollegeData = collegeCourseStats.data.collegeData
          console.log('=== 课程统计数据 ===')
          console.log('后端返回的书院课程数据:', backendCollegeData)
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
              console.log(`${collegeName} (${collegeKey}):`, stats)
            }
          }
          console.log('处理后的 collegeCourseData:', this.collegeCourseData)
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
      // 活动参与人数（后端返回的字段名是 totalParticipants）
      const activityParticipants = this.collegeActivityData[collegeKey]?.totalParticipants || 0
      // 课程参与人数（后端返回的字段名是 enrollments）
      const courseParticipants = this.collegeCourseData[collegeKey]?.enrollments || 0
      // 返回总参与人数（活动 + 课程）
      const total = activityParticipants + courseParticipants
      
      // 调试日志
      if (total > 0) {
        console.log(`${collegeKey} 参与人数: 活动=${activityParticipants}, 课程=${courseParticipants}, 总计=${total}`)
      }
      
      return total
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
    },
    
    // 显示活动详情
    async showActivityDetail(activity) {
      try {
        console.log('=== 点击的活动对象 ===')
        console.log('活动数据:', activity)
        
        // 通过活动ID获取完整的活动详情
        const activityId = activity.id || activity.activityId
        console.log('活动ID:', activityId)
        
        if (!activityId) {
          this.$message.error('活动ID不存在')
          return
        }
        
        // 显示加载提示
        const loading = this.$loading({
          lock: true,
          text: '加载活动详情...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        
        const response = await getActivities(activityId)
        console.log('=== API返回的活动详情 ===')
        console.log('完整响应:', response)
        console.log('活动详情数据:', response.data)
        
        loading.close()
        
        if (response && response.code === 200 && response.data) {
          this.selectedActivity = response.data
          this.activityDetailVisible = true
        } else {
          this.$message.error('获取活动详情失败')
        }
      } catch (error) {
        console.error('获取活动详情失败:', error)
        this.$message.error('获取活动详情失败，请重试')
      }
    },
    
    // 关闭活动详情弹窗
    handleDetailClose(done) {
      this.activityDetailVisible = false
      this.selectedActivity = null
      if (done) done()
    },
    
    // 获取活动状态标签类型
    getActivityDetailStatusTag(activity) {
      const status = activity.status || ''
      const statusMap = {
        '未开始': 'info',
        '报名未开始': 'info',
        'not-started': 'info',
        '可报名': 'success',
        '报名进行中': 'success',
        'enrolling': 'success',
        '已截止': 'danger',
        '报名已截止': 'danger',
        'enroll-ended': 'danger',
        '进行中': 'warning',
        '活动进行中': 'warning',
        'ongoing': 'warning',
        '已结束': '',
        '活动已结束': '',
        'completed': ''
      }
      return statusMap[status] || 'danger'
    },
    
    // 获取活动类型名称
    getActivityTypeName(activityType) {
      const typeMap = {
        '1': '人格塑造与价值引领活动类',
        '2': '知识融合与思维进阶活动类',
        '3': '能力锻造与实践创新活动类',
        '4': '社会责任与领军意识活动类'
      }
      return typeMap[activityType] || activityType || '未分类'
    },
    
    // 获取活动图片完整URL
    getActivityImageUrl(pictureUrl) {
      if (!pictureUrl) return ''

      // 如果已经是完整URL，直接返回
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        return pictureUrl
      }

      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径
      if (pictureUrl.startsWith('/profile/')) {
        return `${process.env.VUE_APP_BASE_API}${pictureUrl}`
      }

      return pictureUrl
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

/* 活动详情弹窗样式 */
.activity-detail-dialog >>> .el-dialog {
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.activity-detail-dialog >>> .el-dialog__header {
  background: linear-gradient(to right, #409EFF, #3b82f6);
  color: white;
  border-radius: 12px 12px 0 0;
  padding: 20px 24px;
}

.activity-detail-dialog >>> .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: white;
}

.activity-detail-dialog >>> .el-dialog__close {
  color: white;
  font-size: 20px;
}

.activity-detail-dialog >>> .el-dialog__close:hover {
  color: rgba(255, 255, 255, 0.8);
}

.activity-detail-dialog >>> .el-dialog__body {
  padding: 24px;
  background: #f8f9fa;
}

.activity-detail {
  .detail-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid rgba(64, 158, 255, 0.1);
  }

  .detail-header h2 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
    color: #2c3e50;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  }

  .status-tags {
    display: flex;
    gap: 8px;
  }

  .status-tags .status-tag {
    border-radius: 20px;
    padding: 4px 12px;
    font-weight: 500;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }

  .detail-item {
    display: flex;
    align-items: center;
    padding: 12px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 8px;
    border: 1px solid rgba(255, 255, 255, 0.3);
    transition: all 0.3s ease;
  }

  .detail-item:hover {
    background: rgba(255, 255, 255, 0.9);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .detail-label {
    font-weight: 600;
    width: 120px;
    color: #495057;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .detail-label i {
    color: #409EFF;
    font-size: 16px;
  }

  .detail-value {
    flex: 1;
    font-weight: 500;
    color: #2c3e50;
  }

  .capacity-info {
    color: #409EFF;
    font-weight: 600;
  }

  .detail-section-content {
    margin: 20px 0;
  }

  .detail-section-content .section-title {
    display: flex;
    align-items: center;
    color: #2c3e50;
    margin-bottom: 12px;
    font-weight: 600;
    font-size: 16px;
  }

  .detail-section-content .section-title i {
    margin-right: 8px;
    color: #409EFF;
    font-size: 18px;
  }

  .detail-section-content .section-content {
    line-height: 1.6;
    padding: 16px 20px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 8px;
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: #495057;
    font-weight: 500;
  }
}

/* 活动图片展示样式 */
.activity-image-container {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.activity-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.activity-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
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
  
  .activity-detail .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .activity-detail-dialog >>> .el-dialog {
    width: 95% !important;
    margin: 0 auto;
  }
  
  .activity-detail .detail-header h2 {
    font-size: 20px;
  }
}
</style>

