<template>
  <div class="app-container">
    <div class="main-layout">
      <!-- 左侧书院筛选区域 -->
      <div class="college-filter-panel">
        <div class="panel-header">
          <h3 class="panel-title">书院筛选</h3>
        </div>
        <div class="panel-content">
          <div class="college-buttons">
            <button
              v-for="college in collegeList"
              :key="college.key"
              @click="handleCollegeFilter(college.key)"
              :class="[
                'college-button',
                selectedCollege === college.key ? 'college-button-active' : 'college-button-normal'
              ]"
            >
              <i :class="college.icon" class="college-icon"></i>
              {{ college.name }}
            </button>
          </div>
          
          <!-- 全部书院按钮 -->
          <div class="all-college-section">
            <button
              @click="handleCollegeFilter('all')"
              :class="[
                'college-button all-college-button',
                selectedCollege === 'all' ? 'all-college-button-active' : 'all-college-button-normal'
              ]"
            >
              <i class="el-icon-s-grid college-icon"></i>
              全部书院
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧主内容区域 -->
      <div class="main-content-area">
        <!-- 顶部菜单栏 -->
        <div class="top-menu-bar">
          <div class="menu-container">
            <div class="menu-buttons">
              <button
                v-for="menuItem in menuItems"
                :key="menuItem.key"
                @click="handleMenuClick(menuItem.key)"
                :class="[
                  'menu-button',
                  activeMenu === menuItem.key ? 'menu-button-active' : 'menu-button-normal'
                ]"
              >
                <i :class="menuItem.icon" class="menu-icon"></i>
                {{ menuItem.label }}
              </button>
            </div>
          </div>
        </div>

        <!-- 内容区域 -->
        <div class="content-area">
          <!-- 书院活动页面 -->
          <div v-if="activeMenu === 'activity'" class="page-content">
            <div class="content-wrapper">
              <!-- 统计概览卡片 -->
              <div class="stats-overview">
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-s-promotion"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">总活动数</div>
                    <div class="card-value">{{ activityStats.totalActivities }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-time"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">进行中</div>
                    <div class="card-value">{{ activityStats.ongoingActivities }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-check"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">已完成</div>
                    <div class="card-value">{{ activityStats.completedActivities }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-user"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">总参与人数</div>
                    <div class="card-value">{{ activityStats.totalParticipants }}</div>
                  </div>
                </div>
              </div>

              <!-- 各书院活动统计 -->
              <div class="college-stats-section">
                <h3 class="section-title">各书院活动统计</h3>
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
                    </div>
                    <div class="college-numbers">
                      <div class="stat-item">
                        <span class="stat-label">活动总数</span>
                        <span class="stat-value">{{ getCollegeActivityCount(college.key) }}</span>
                      </div>
                      <div class="stat-item">
                        <span class="stat-label">进行中</span>
                        <span class="stat-value ongoing">{{ getCollegeOngoingCount(college.key) }}</span>
                      </div>
                      <div class="stat-item">
                        <span class="stat-label">参与人数</span>
                        <span class="stat-value participants">{{ getCollegeParticipantCount(college.key) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 活动类型分布 -->
              <div class="activity-type-section">
                <h3 class="section-title">活动类型分布</h3>
                <div class="type-distribution">
                  <div 
                    v-for="(type, index) in activityTypes" 
                    :key="type.key"
                    class="type-item"
                    :style="{ '--progress': type.percentage + '%' }"
                  >
                    <div class="type-header">
                      <span class="type-name">{{ type.name }}</span>
                      <span class="type-count">{{ type.count }}个</span>
                    </div>
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: type.percentage + '%' }"></div>
                    </div>
                    <div class="type-percentage">{{ type.percentage }}%</div>
                  </div>
                </div>
              </div>

              <!-- 最近活动列表 -->
              <div class="recent-activities-section">
                <h3 class="section-title">最近活动</h3>
                <div class="activities-list">
                  <div 
                    v-for="activity in recentActivities" 
                    :key="activity.id"
                    class="activity-item"
                  >
                    <div class="activity-info">
                      <div class="activity-name">{{ activity.name }}</div>
                      <div class="activity-meta">
                        <span class="activity-college">{{ activity.college }}</span>
                        <span class="activity-type">{{ activity.type }}</span>
                        <span class="activity-date">{{ activity.date }}</span>
                      </div>
                    </div>
                    <div class="activity-status" :class="'status-' + activity.status">
                      {{ getStatusText(activity.status) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 书院课程页面 -->
          <div v-if="activeMenu === 'course'" class="page-content">
            <div class="content-wrapper">
              <!-- 课程统计概览卡片 -->
              <div class="stats-overview">
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-reading"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">总课程数</div>
                    <div class="card-value">{{ courseStats.totalCourses }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-time"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">选课中</div>
                    <div class="card-value">{{ courseStats.enrollingCourses }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-check"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">已结束选课</div>
                    <div class="card-value">{{ courseStats.enrollmentEndedCourses }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-user"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">总选课人数</div>
                    <div class="card-value">{{ courseStats.totalEnrollments }}</div>
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

              <!-- 课程状态统计 -->
              <div class="course-status-section">
                <h3 class="section-title">课程状态统计</h3>
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

              <!-- 最近课程列表 -->
              <div class="recent-courses-section">
                <h3 class="section-title">最近课程</h3>
                <div class="courses-list">
                  <div 
                    v-for="course in recentCourses" 
                    :key="course.id"
                    class="course-item"
                  >
                    <div class="course-info">
                      <div class="course-name">{{ course.name }}</div>
                      <div class="course-meta">
                        <span class="course-college">{{ course.college }}</span>
                        <span class="course-type">{{ course.type }}</span>
                        <span class="course-date">{{ course.date }}</span>
                        <span class="course-enrollments">{{ course.enrollments }}人已选</span>
                      </div>
                    </div>
                    <div class="course-status" :class="'status-' + course.status">
                      {{ getCourseStatusText(course.status) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 专业分流页面 -->
          <div v-if="activeMenu === 'major'" class="page-content">
            <div class="content-wrapper">
              <!-- 分流统计概览卡片 -->
              <div class="stats-overview">
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-s-grid"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">总分流人数</div>
                    <div class="card-value">{{ majorStats.totalStudents }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-refresh"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">跨书院分流</div>
                    <div class="card-value">{{ majorStats.crossCollegeTransfers }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-check"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">保持原专业</div>
                    <div class="card-value">{{ majorStats.keepOriginalMajor }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-edit"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">转专业人数</div>
                    <div class="card-value">{{ majorStats.majorChanges }}</div>
                  </div>
                </div>
              </div>

              <!-- 各书院分流统计 -->
              <div class="college-stats-section">
                <h3 class="section-title">各书院分流统计</h3>
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
                    </div>
                    <div class="college-numbers">
                      <div class="stat-item">
                        <span class="stat-label">分流人数</span>
                        <span class="stat-value">{{ getCollegeTransferCount(college.key) }}</span>
                      </div>
                      <div class="stat-item">
                        <span class="stat-label">转入人数</span>
                        <span class="stat-value incoming">{{ getCollegeIncomingCount(college.key) }}</span>
                      </div>
                      <div class="stat-item">
                        <span class="stat-label">转出人数</span>
                        <span class="stat-value outgoing">{{ getCollegeOutgoingCount(college.key) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 分流类型分布 -->
              <div class="transfer-type-section">
                <h3 class="section-title">分流类型分布</h3>
                <div class="type-distribution">
                  <div 
                    v-for="(type, index) in transferTypes" 
                    :key="type.key"
                    class="type-item"
                    :style="{ '--progress': type.percentage + '%' }"
                  >
                    <div class="type-header">
                      <span class="type-name">{{ type.name }}</span>
                      <span class="type-count">{{ type.count }}人</span>
                    </div>
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: type.percentage + '%' }"></div>
                    </div>
                    <div class="type-percentage">{{ type.percentage }}%</div>
                  </div>
                </div>
              </div>

              <!-- 分流流向统计 -->
              <div class="transfer-flow-section">
                <h3 class="section-title">分流流向统计</h3>
                <div class="flow-stats-grid">
                  <div class="flow-stat-card">
                    <div class="flow-header">
                      <i class="el-icon-s-promotion flow-icon"></i>
                      <span class="flow-title">热门转入书院</span>
                    </div>
                    <div class="flow-list">
                      <div 
                        v-for="(college, index) in popularIncomingColleges" 
                        :key="college.name"
                        class="flow-item"
                      >
                        <span class="flow-rank">{{ index + 1 }}</span>
                        <span class="flow-name">{{ college.name }}</span>
                        <span class="flow-count">{{ college.count }}人</span>
                      </div>
                    </div>
                  </div>
                  <div class="flow-stat-card">
                    <div class="flow-header">
                      <i class="el-icon-s-promotion flow-icon"></i>
                      <span class="flow-title">热门转出书院</span>
                    </div>
                    <div class="flow-list">
                      <div 
                        v-for="(college, index) in popularOutgoingColleges" 
                        :key="college.name"
                        class="flow-item"
                      >
                        <span class="flow-rank">{{ index + 1 }}</span>
                        <span class="flow-name">{{ college.name }}</span>
                        <span class="flow-count">{{ college.count }}人</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 最近分流记录 -->
              <div class="recent-transfers-section">
                <h3 class="section-title">最近分流记录</h3>
                <div class="transfers-list">
                  <div 
                    v-for="transfer in recentTransfers" 
                    :key="transfer.id"
                    class="transfer-item"
                  >
                    <div class="transfer-info">
                      <div class="transfer-student">{{ transfer.studentName }}</div>
                      <div class="transfer-meta">
                        <span class="transfer-from">{{ transfer.fromCollege }}</span>
                        <i class="el-icon-right transfer-arrow"></i>
                        <span class="transfer-to">{{ transfer.toCollege }}</span>
                        <span class="transfer-type">{{ transfer.transferType }}</span>
                        <span class="transfer-date">{{ transfer.date }}</span>
                      </div>
                    </div>
                    <div class="transfer-status" :class="'status-' + transfer.status">
                      {{ getTransferStatusText(transfer.status) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminTeacherHome',
  data() {
    return {
      activeMenu: 'activity', // 默认选中书院活动
      selectedCollege: 'all', // 默认选中全部书院
      menuItems: [
        {
          key: 'activity',
          label: '书院活动',
          icon: 'el-icon-s-promotion'
        },
        {
          key: 'course',
          label: '书院课程',
          icon: 'el-icon-reading'
        },
        {
          key: 'major',
          label: '专业分流',
          icon: 'el-icon-s-grid'
        }
      ],
      collegeList: [
        {
          key: 'dayu',
          name: '大煜书院',
          icon: 'el-icon-school'
        },
        {
          key: 'bochuan',
          name: '伯川书院',
          icon: 'el-icon-school'
        },
        {
          key: 'lingxi',
          name: '令希书院',
          icon: 'el-icon-school'
        },
        {
          key: 'houde',
          name: '厚德书院',
          icon: 'el-icon-school'
        },
        {
          key: 'zhixing',
          name: '知行书院',
          icon: 'el-icon-school'
        },
        {
          key: 'duxue',
          name: '笃学书院',
          icon: 'el-icon-school'
        },
        {
          key: 'qiushi',
          name: '求实书院',
          icon: 'el-icon-school'
        }
      ],
      // 活动统计数据
      activityStats: {
        totalActivities: 0,
        ongoingActivities: 0,
        completedActivities: 0,
        totalParticipants: 0
      },
      // 各书院活动数据
      collegeActivityData: {},
      // 活动类型分布
      activityTypes: [
        { key: '1', name: '人格塑造与价值引领活动类', count: 0, percentage: 0 },
        { key: '2', name: '知识融合与思维进阶活动类', count: 0, percentage: 0 },
        { key: '3', name: '能力锻造与实践创新活动类', count: 0, percentage: 0 },
        { key: '4', name: '社会责任与领军意识活动类', count: 0, percentage: 0 }
      ],
      // 最近活动列表
      recentActivities: [],
      // 课程统计数据
      courseStats: {
        totalCourses: 0,
        notStartedCourses: 0,
        enrollingCourses: 0,
        enrollmentEndedCourses: 0,
        completedCourses: 0,
        totalEnrollments: 0
      },
      // 各书院课程数据
      collegeCourseData: {},
      // 课程类型分布
      courseTypes: [
        { key: '1', name: '人格塑造与价值引领课程类', count: 0, percentage: 0 },
        { key: '2', name: '知识融合与思维进阶课程类', count: 0, percentage: 0 },
        { key: '3', name: '能力锻造与实践创新课程类', count: 0, percentage: 0 },
        { key: '4', name: '社会责任与领军意识课程类', count: 0, percentage: 0 }
      ],
      // 最近课程列表
      recentCourses: [],
      // 专业分流统计数据
      majorStats: {
        totalStudents: 0,
        crossCollegeTransfers: 0,
        keepOriginalMajor: 0,
        majorChanges: 0
      },
      // 各书院分流数据
      collegeTransferData: {},
      // 分流类型分布
      transferTypes: [
        { key: '1', name: '保持当前专业', count: 0, percentage: 0 },
        { key: '2', name: '域内任选专业', count: 0, percentage: 0 },
        { key: '3', name: '类内任选专业', count: 0, percentage: 0 },
        { key: '4', name: '创新班政策内任选专业', count: 0, percentage: 0 },
        { key: '5', name: '转专业', count: 0, percentage: 0 }
      ],
      // 热门转入书院
      popularIncomingColleges: [],
      // 热门转出书院
      popularOutgoingColleges: [],
      // 最近分流记录
      recentTransfers: []
    }
  },
  methods: {
    handleMenuClick(menuKey) {
      this.activeMenu = menuKey
      // 当切换到活动页面时，加载活动统计数据
      if (menuKey === 'activity') {
        this.loadActivityStats()
      }
      // 当切换到课程页面时，加载课程统计数据
      if (menuKey === 'course') {
        this.loadCourseStats()
      }
      // 当切换到专业分流页面时，加载分流统计数据
      if (menuKey === 'major') {
        this.loadMajorStats()
      }
    },
    handleCollegeFilter(collegeKey) {
      this.selectedCollege = collegeKey
      // 这里可以添加筛选逻辑，比如重新加载数据
      console.log('筛选书院:', collegeKey)
    },
    getCollegeName(collegeKey) {
      if (collegeKey === 'all') return '全部书院'
      const college = this.collegeList.find(item => item.key === collegeKey)
      return college ? college.name : ''
    },
    // 获取书院活动数量
    getCollegeActivityCount(collegeKey) {
      if (collegeKey === 'all') return this.activityStats.totalActivities
      return this.collegeActivityData[collegeKey]?.totalActivities || 0
    },
    // 获取书院进行中活动数量
    getCollegeOngoingCount(collegeKey) {
      if (collegeKey === 'all') return this.activityStats.ongoingActivities
      return this.collegeActivityData[collegeKey]?.ongoingActivities || 0
    },
    // 获取书院参与人数
    getCollegeParticipantCount(collegeKey) {
      if (collegeKey === 'all') return this.activityStats.totalParticipants
      return this.collegeActivityData[collegeKey]?.participants || 0
    },
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'not-started': '未开始',
        'ongoing': '进行中',
        'completed': '已完成',
        'cancelled': '已取消'
      }
      return statusMap[status] || status
    },
    // 获取课程状态文本
    getCourseStatusText(status) {
      const statusMap = {
        'not-started': '未开始选课',
        'enrolling': '选课中',
        'enrollment-ended': '选课已结束',
        'completed': '课程已完成'
      }
      return statusMap[status] || status
    },
    // 获取书院课程数量
    getCollegeCourseCount(collegeKey) {
      if (collegeKey === 'all') return this.courseStats.totalCourses
      return this.collegeCourseData[collegeKey]?.totalCourses || 0
    },
    // 获取书院选课中课程数量
    getCollegeEnrollingCount(collegeKey) {
      if (collegeKey === 'all') return this.courseStats.enrollingCourses
      return this.collegeCourseData[collegeKey]?.enrollingCourses || 0
    },
    // 获取书院选课人数
    getCollegeEnrollmentCount(collegeKey) {
      if (collegeKey === 'all') return this.courseStats.totalEnrollments
      return this.collegeCourseData[collegeKey]?.enrollments || 0
    },
    // 获取书院分流人数
    getCollegeTransferCount(collegeKey) {
      if (collegeKey === 'all') return this.majorStats.totalStudents
      return this.collegeTransferData[collegeKey]?.totalTransfers || 0
    },
    // 获取书院转入人数
    getCollegeIncomingCount(collegeKey) {
      if (collegeKey === 'all') return 0
      return this.collegeTransferData[collegeKey]?.incomingTransfers || 0
    },
    // 获取书院转出人数
    getCollegeOutgoingCount(collegeKey) {
      if (collegeKey === 'all') return 0
      return this.collegeTransferData[collegeKey]?.outgoingTransfers || 0
    },
    // 获取分流状态文本
    getTransferStatusText(status) {
      const statusMap = {
        'completed': '已完成',
        'processing': '处理中',
        'pending': '待审核',
        'rejected': '已拒绝'
      }
      return statusMap[status] || status
    },
    // 加载活动统计数据
    loadActivityStats() {
      // 模拟数据，实际应该调用API
      this.activityStats = {
        totalActivities: 156,
        ongoingActivities: 23,
        completedActivities: 128,
        totalParticipants: 2847
      }
      
      // 模拟各书院数据
      this.collegeActivityData = {
        dayu: { totalActivities: 22, ongoingActivities: 3, participants: 456 },
        bochuan: { totalActivities: 18, ongoingActivities: 2, participants: 389 },
        lingxi: { totalActivities: 25, ongoingActivities: 4, participants: 567 },
        houde: { totalActivities: 20, ongoingActivities: 3, participants: 423 },
        zhixing: { totalActivities: 24, ongoingActivities: 5, participants: 512 },
        duxue: { totalActivities: 19, ongoingActivities: 2, participants: 378 },
        qiushi: { totalActivities: 28, ongoingActivities: 4, participants: 522 }
      }
      
      // 模拟活动类型分布
      this.activityTypes = [
        { key: '1', name: '人格塑造与价值引领活动类', count: 45, percentage: 29 },
        { key: '2', name: '知识融合与思维进阶活动类', count: 38, percentage: 24 },
        { key: '3', name: '能力锻造与实践创新活动类', count: 42, percentage: 27 },
        { key: '4', name: '社会责任与领军意识活动类', count: 31, percentage: 20 }
      ]
      
      // 模拟最近活动
      this.recentActivities = [
        {
          id: 1,
          name: '科技创新大赛',
          college: '大煜书院',
          type: '能力锻造与实践创新活动类',
          date: '2024-01-15',
          status: 'ongoing'
        },
        {
          id: 2,
          name: '志愿服务项目',
          college: '知行书院',
          type: '社会责任与领军意识活动类',
          date: '2024-01-12',
          status: 'completed'
        },
        {
          id: 3,
          name: '学术讲座',
          college: '令希书院',
          type: '知识融合与思维进阶活动类',
          date: '2024-01-10',
          status: 'ongoing'
        }
      ]
    },
    // 加载课程统计数据
    loadCourseStats() {
      // 模拟数据，实际应该调用API
      this.courseStats = {
        totalCourses: 89,
        notStartedCourses: 12,
        enrollingCourses: 18,
        enrollmentEndedCourses: 45,
        completedCourses: 14,
        totalEnrollments: 3247
      }
      
      // 模拟各书院课程数据
      this.collegeCourseData = {
        dayu: { totalCourses: 13, enrollingCourses: 3, enrollments: 456 },
        bochuan: { totalCourses: 11, enrollingCourses: 2, enrollments: 389 },
        lingxi: { totalCourses: 15, enrollingCourses: 4, enrollments: 567 },
        houde: { totalCourses: 12, enrollingCourses: 3, enrollments: 423 },
        zhixing: { totalCourses: 14, enrollingCourses: 5, enrollments: 512 },
        duxue: { totalCourses: 10, enrollingCourses: 2, enrollments: 378 },
        qiushi: { totalCourses: 14, enrollingCourses: 4, enrollments: 522 }
      }
      
      // 模拟课程类型分布
      this.courseTypes = [
        { key: '1', name: '人格塑造与价值引领课程类', count: 28, percentage: 31 },
        { key: '2', name: '知识融合与思维进阶课程类', count: 22, percentage: 25 },
        { key: '3', name: '能力锻造与实践创新课程类', count: 25, percentage: 28 },
        { key: '4', name: '社会责任与领军意识课程类', count: 14, percentage: 16 }
      ]
      
      // 模拟最近课程
      this.recentCourses = [
        {
          id: 1,
          name: '创新创业实践课程',
          college: '大煜书院',
          type: '能力锻造与实践创新课程类',
          date: '2024-01-15',
          enrollments: 156,
          status: 'enrolling'
        },
        {
          id: 2,
          name: '领导力培养课程',
          college: '知行书院',
          type: '社会责任与领军意识课程类',
          date: '2024-01-12',
          enrollments: 89,
          status: 'enrollment-ended'
        },
        {
          id: 3,
          name: '学术研究方法论',
          college: '令希书院',
          type: '知识融合与思维进阶课程类',
          date: '2024-01-10',
          enrollments: 234,
          status: 'enrolling'
        }
      ]
    },
    // 加载专业分流统计数据
    loadMajorStats() {
      // 模拟数据，实际应该调用API
      this.majorStats = {
        totalStudents: 1247,
        crossCollegeTransfers: 234,
        keepOriginalMajor: 856,
        majorChanges: 157
      }
      
      // 模拟各书院分流数据
      this.collegeTransferData = {
        dayu: { totalTransfers: 156, incomingTransfers: 23, outgoingTransfers: 18 },
        bochuan: { totalTransfers: 134, incomingTransfers: 19, outgoingTransfers: 22 },
        lingxi: { totalTransfers: 178, incomingTransfers: 28, outgoingTransfers: 15 },
        houde: { totalTransfers: 145, incomingTransfers: 21, outgoingTransfers: 19 },
        zhixing: { totalTransfers: 167, incomingTransfers: 25, outgoingTransfers: 17 },
        duxue: { totalTransfers: 123, incomingTransfers: 16, outgoingTransfers: 24 },
        qiushi: { totalTransfers: 189, incomingTransfers: 31, outgoingTransfers: 12 }
      }
      
      // 模拟分流类型分布
      this.transferTypes = [
        { key: '1', name: '保持当前专业', count: 856, percentage: 69 },
        { key: '2', name: '域内任选专业', count: 234, percentage: 19 },
        { key: '3', name: '类内任选专业', count: 89, percentage: 7 },
        { key: '4', name: '创新班政策内任选专业', count: 45, percentage: 4 },
        { key: '5', name: '转专业', count: 23, percentage: 1 }
      ]
      
      // 模拟热门转入书院
      this.popularIncomingColleges = [
        { name: '令希书院', count: 89 },
        { name: '求实书院', count: 76 },
        { name: '知行书院', count: 65 },
        { name: '大煜书院', count: 54 },
        { name: '厚德书院', count: 43 }
      ]
      
      // 模拟热门转出书院
      this.popularOutgoingColleges = [
        { name: '笃学书院', count: 67 },
        { name: '伯川书院', count: 54 },
        { name: '厚德书院', count: 43 },
        { name: '大煜书院', count: 38 },
        { name: '知行书院', count: 32 }
      ]
      
      // 模拟最近分流记录
      this.recentTransfers = [
        {
          id: 1,
          studentName: '张三',
          fromCollege: '笃学书院',
          toCollege: '令希书院',
          transferType: '域内任选专业',
          date: '2024-01-15',
          status: 'completed'
        },
        {
          id: 2,
          studentName: '李四',
          fromCollege: '伯川书院',
          toCollege: '求实书院',
          transferType: '类内任选专业',
          date: '2024-01-14',
          status: 'processing'
        },
        {
          id: 3,
          studentName: '王五',
          fromCollege: '厚德书院',
          toCollege: '知行书院',
          transferType: '转专业',
          date: '2024-01-13',
          status: 'completed'
        }
      ]
    }
  },
  mounted() {
    // 页面加载时初始化数据
    this.loadActivityStats()
    this.loadCourseStats()
    this.loadMajorStats()
  }
}
</script>

<style scoped>
/* 主容器 */
.app-container {
  margin-left: 100px;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 主布局 */
.main-layout {
  display: flex;
  height: 100vh;
  gap: 0;
}

/* 左侧书院筛选面板 */
.college-filter-panel {
  width: 256px;
  background: white;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.panel-header {
  padding: 20px 20px 10px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.panel-content {
  padding: 20px;
  flex: 1;
}

.college-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.college-button {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.college-button:hover {
  border-color: #3b82f6;
  background: #f8fafc;
  transform: translateX(2px);
}

.college-button-active {
  border-color: #3b82f6;
  background: #eff6ff;
  color: #1d4ed8;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}

.college-icon {
  margin-right: 12px;
  font-size: 16px;
}

.all-college-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.all-college-button {
  border-color: #10b981;
  color: #059669;
}

.all-college-button:hover {
  border-color: #059669;
  background: #f0fdf4;
}

.all-college-button-active {
  border-color: #10b981;
  background: #f0fdf4;
  color: #047857;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
}

/* 右侧主内容区域 */
.main-content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  min-width: 0;
}

/* 顶部菜单栏 */
.top-menu-bar {
  background: white;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.menu-container {
  padding: 0 24px;
}

.menu-buttons {
  display: flex;
  gap: 32px;
}

.menu-button {
  padding: 16px 0;
  border: none;
  background: none;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.menu-button:hover {
  color: #333;
  border-bottom-color: #d1d5db;
}

.menu-button-active {
  color: #3b82f6;
  border-bottom-color: #3b82f6;
}

.menu-icon {
  margin-right: 8px;
  font-size: 16px;
}

/* 内容区域 */
.content-area {
  flex: 1;
  overflow-y: auto;
  background: #f8fafc;
}

.content-wrapper {
  padding: 24px;
}

.content-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.content-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px 0;
}

.college-indicator {
  font-size: 14px;
  font-weight: 400;
  color: #3b82f6;
}

.content-description {
  color: #6b7280;
  font-size: 16px;
  margin: 0 0 16px 0;
}

.debug-info {
  background: #f3f4f6;
  padding: 12px;
  border-radius: 6px;
  margin-top: 16px;
}

.debug-info p {
  margin: 4px 0;
  font-size: 12px;
  color: #6b7280;
}

.page-content {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 活动统计样式 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.overview-card:nth-child(1) .card-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.overview-card:nth-child(2) .card-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.overview-card:nth-child(3) .card-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.overview-card:nth-child(4) .card-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
}

/* 各书院统计样式 */
.college-stats-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 20px;
}

.college-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.college-stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid transparent;
}

.college-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.college-stat-card.active {
  border-color: #3b82f6;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.2);
}

.college-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.college-stat-icon {
  font-size: 20px;
  color: #3b82f6;
  margin-right: 12px;
}

.college-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.college-numbers {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.stat-value.ongoing {
  color: #f59e0b;
}

.stat-value.participants {
  color: #10b981;
}

/* 活动类型分布样式 */
.activity-type-section {
  margin-bottom: 32px;
}

.type-distribution {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.type-item {
  margin-bottom: 20px;
}

.type-item:last-child {
  margin-bottom: 0;
}

.type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.type-name {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.type-count {
  font-size: 14px;
  color: #6b7280;
}

.progress-bar {
  height: 8px;
  background: #f3f4f6;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 4px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #1d4ed8);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.type-percentage {
  font-size: 12px;
  color: #6b7280;
  text-align: right;
}

/* 最近活动样式 */
.recent-activities-section {
  margin-bottom: 32px;
}

.activities-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.2s ease;
}

.activity-item:hover {
  background-color: #f8fafc;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-info {
  flex: 1;
}

.activity-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 8px;
}

.activity-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #6b7280;
}

.activity-college {
  color: #3b82f6;
  font-weight: 500;
}

.activity-type {
  color: #10b981;
}

.activity-date {
  color: #6b7280;
}

.activity-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-ongoing {
  background: #fef3c7;
  color: #d97706;
}

.status-completed {
  background: #d1fae5;
  color: #059669;
}

.status-not-started {
  background: #e5e7eb;
  color: #6b7280;
}

.status-cancelled {
  background: #fee2e2;
  color: #dc2626;
}

/* 课程统计样式 */
.course-type-section {
  margin-bottom: 32px;
}

.course-status-section {
  margin-bottom: 32px;
}

.status-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.status-stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.status-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.status-stat-card.not-started {
  border-left: 4px solid #6b7280;
}

.status-stat-card.enrolling {
  border-left: 4px solid #3b82f6;
}

.status-stat-card.ended {
  border-left: 4px solid #10b981;
}

.status-stat-card.completed {
  border-left: 4px solid #8b5cf6;
}

.status-stat-card .status-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.status-stat-card.not-started .status-icon {
  background: linear-gradient(135deg, #6b7280 0%, #9ca3af 100%);
}

.status-stat-card.enrolling .status-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.status-stat-card.ended .status-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.status-stat-card.completed .status-icon {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.status-content {
  flex: 1;
}

.status-title {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.status-count {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

/* 最近课程样式 */
.recent-courses-section {
  margin-bottom: 32px;
}

.courses-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.course-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.2s ease;
}

.course-item:hover {
  background-color: #f8fafc;
}

.course-item:last-child {
  border-bottom: none;
}

.course-info {
  flex: 1;
}

.course-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 8px;
}

.course-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #6b7280;
}

.course-college {
  color: #3b82f6;
  font-weight: 500;
}

.course-type {
  color: #10b981;
}

.course-date {
  color: #6b7280;
}

.course-enrollments {
  color: #8b5cf6;
  font-weight: 500;
}

.course-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-enrolling {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-enrollment-ended {
  background: #d1fae5;
  color: #059669;
}

.status-not-started {
  background: #e5e7eb;
  color: #6b7280;
}

.status-completed {
  background: #ede9fe;
  color: #7c3aed;
}

.stat-value.enrolling {
  color: #3b82f6;
}

.stat-value.enrollments {
  color: #8b5cf6;
}

/* 专业分流样式 */
.transfer-type-section {
  margin-bottom: 32px;
}

.transfer-flow-section {
  margin-bottom: 32px;
}

.flow-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.flow-stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.flow-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.flow-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f3f4f6;
}

.flow-icon {
  font-size: 20px;
  color: #3b82f6;
  margin-right: 12px;
}

.flow-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.flow-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.flow-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.flow-item:hover {
  background: #e2e8f0;
}

.flow-rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #3b82f6;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  margin-right: 12px;
}

.flow-name {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.flow-count {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

/* 最近分流记录样式 */
.recent-transfers-section {
  margin-bottom: 32px;
}

.transfers-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.transfer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.2s ease;
}

.transfer-item:hover {
  background-color: #f8fafc;
}

.transfer-item:last-child {
  border-bottom: none;
}

.transfer-info {
  flex: 1;
}

.transfer-student {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 8px;
}

.transfer-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #6b7280;
}

.transfer-from {
  color: #ef4444;
  font-weight: 500;
}

.transfer-arrow {
  color: #3b82f6;
  font-size: 16px;
}

.transfer-to {
  color: #10b981;
  font-weight: 500;
}

.transfer-type {
  color: #8b5cf6;
  font-weight: 500;
}

.transfer-date {
  color: #6b7280;
}

.transfer-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-completed {
  background: #d1fae5;
  color: #059669;
}

.status-processing {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-pending {
  background: #fef3c7;
  color: #d97706;
}

.status-rejected {
  background: #fee2e2;
  color: #dc2626;
}

.stat-value.incoming {
  color: #10b981;
}

.stat-value.outgoing {
  color: #ef4444;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .college-filter-panel {
    width: 200px;
  }
  
  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .college-stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .app-container {
    margin-left: 0;
    padding: 10px;
  }
  
  .main-layout {
    flex-direction: column;
  }
  
  .college-filter-panel {
    width: 100%;
    height: auto;
    order: 2;
  }
  
  .main-content-area {
    order: 1;
  }
  
  .menu-buttons {
    flex-direction: column;
    gap: 0;
  }
  
  .menu-button {
    width: 100%;
    text-align: left;
    padding: 12px 16px;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .college-stats-grid {
    grid-template-columns: 1fr;
  }
  
  .activity-meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>