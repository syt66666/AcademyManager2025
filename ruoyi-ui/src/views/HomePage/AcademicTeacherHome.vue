<template>
  <div class="app-container">
    <!-- 欢迎信息 -->
    <div class="welcome-header">
      <div class="welcome-content">
        <h1 class="welcome-title">{{ welcomeMessage }}</h1>
        <p class="welcome-subtitle">{{ currentAcademy ? `欢迎来到${currentAcademy}` : '欢迎使用书院管理系统' }}</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧列 -->
      <div class="left-column">
        <!-- 左侧上方：书院新闻 -->
        <el-card class="news-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">
              <i class="el-icon-news"></i>
              书院新闻
            </span>
          </div>
          <div class="news-empty">
            <i class="el-icon-document"></i>
            <p>敬请期待</p>
          </div>
        </el-card>

        <!-- 左侧下方：统计图表 -->
        <div class="charts-container">
          <!-- 活动统计环形图 -->
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span class="card-title">
                <i class="el-icon-pie-chart"></i>
                活动统计
              </span>
            </div>
            <div class="chart-content">
              <div ref="activityChart" class="chart"></div>
            </div>
          </el-card>

          <!-- 课程统计环形图 -->
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span class="card-title">
                <i class="el-icon-pie-chart"></i>
                课程统计
              </span>
            </div>
            <div class="chart-content">
              <div ref="courseChart" class="chart"></div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 右侧列 -->
      <div class="right-column">
        <!-- 右侧上方：近期活动 -->
        <el-card class="activity-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">
              <i class="el-icon-calendar"></i>
              近期活动
            </span>
            <el-button type="text" size="small" @click="goToActivityManager()">查看全部</el-button>
          </div>
          <div class="activity-list">
            <div v-for="(activity, index) in recentActivities" :key="index" class="activity-item" @click="goToActivityManager(activity)">
              <div class="activity-time">
                <div class="time-day">{{ activity.day }}</div>
                <div class="time-month">{{ activity.month }}</div>
              </div>
              <div class="activity-content">
                <h4 class="activity-title">{{ activity.title }}</h4>
                <p class="activity-location">
                  <i class="el-icon-location"></i>
                  {{ activity.location }}
                </p>
                <p class="activity-time-text">
                  <i class="el-icon-time"></i>
                  {{ activity.time }}
                </p>
              </div>
              <div class="activity-status" :class="activity.status">
                {{ activity.statusText }}
              </div>
            </div>
          </div>
        </el-card>

        <!-- 右侧下方：本学期课程 -->
        <el-card class="course-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">
              <i class="el-icon-reading"></i>
              本学期课程
            </span>
            <el-button type="text" size="small" @click="goToCourseManager()">课程管理</el-button>
          </div>
          <div class="course-list">
            <div v-for="(course, index) in currentCourses" :key="index" class="course-item" @click="goToCourseManager(course)">
              <div class="course-info">
                <h4 class="course-name">{{ course.name }}</h4>
                <p class="course-code">{{ course.courseCode }}</p>
                <p class="course-teacher">
                  <i class="el-icon-user"></i>
                  {{ course.teacher }}
                </p>
                <p class="course-schedule">
                  <i class="el-icon-time"></i>
                  {{ course.schedule }}
                </p>
                <p class="course-location">
                  <i class="el-icon-location"></i>
                  {{ course.location }}
                </p>
                <p class="course-category">
                  <i class="el-icon-tag"></i>
                  {{ course.courseCategory }} | {{ course.credit }}学分
                </p>
              </div>
              <div class="course-progress">
                <el-progress 
                  :percentage="course.progress" 
                  :color="getProgressColor(course.progress)"
                  :show-text="false">
                </el-progress>
                <span class="progress-text">{{ course.enrolledStudent || 0 }}/{{ course.courseCapacity || 0 }}</span>
                <div class="course-status" :class="getCourseStatusClass(course.status)">
                  {{ course.status }}
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import { getStudent } from "@/api/system/student";
import { listActivities } from "@/api/system/activities";
import { listCourses } from "@/api/system/courses";
import { getAuditCount } from "@/api/system/bookings";
import { getAuditCount as getCourseAuditCount } from "@/api/system/courseBookings";
import * as echarts from 'echarts';

export default {
  name: "AcademicTeacherHome",
  data() {
    return {
      // 当前学生书院信息
      currentAcademy: '',
      
      // 近期活动数据
      recentActivities: [],

      // 本学期课程数据
      currentCourses: [],

      // 活动统计数据
      activityStats: {
        pending: 0,
        approved: 0,
        rejected: 0
      },

      // 课程统计数据
      courseStats: {
        pending: 0,
        approved: 0,
        rejected: 0
      }
    };
  },
  computed: {
    // 判断用户角色
    userRole() {
      const userName = this.$store.state.user.name;
      if (userName === 'admin') return 'system-admin';
      if (userName === '10000') return 'super-admin';
      if (userName >= '10001' && userName <= '10007') return 'academy-admin';
      return 'student';
    },

    // 判断是否为管理员（包括所有管理员类型）
    isAdmin() {
      return ['system-admin', 'super-admin', 'academy-admin'].includes(this.userRole);
    },

    // 欢迎信息
    welcomeMessage() {
      switch (this.userRole) {
        case 'system-admin':
          return '你好，系统管理员';
        case 'super-admin':
          return '你好，总管理员';
        case 'academy-admin':
          return '你好，书院教务员';
        default:
          return '你好，学生';
      }
    },
  },
  mounted() {
    // 初始化图表
    this.$nextTick(() => {
      this.initCharts();
    });
  },
  created() {
    // 初始化学生信息
    this.getCurrentStudentInfo();
    // 获取近期活动数据
    this.getRecentActivities();
    // 获取本学期课程数据
    this.getCurrentCourses();
    // 获取活动统计数据
    this.getActivityStats();
    // 获取课程统计数据
    this.getCourseStats();
  },
  methods: {
    // 获取当前学生信息
    async getCurrentStudentInfo() {
      try {
        const response = await getStudent(this.$store.state.user.name);
        console.log('学生信息API响应:', response);

        if (response && response.studentInfo) {
          this.currentAcademy = response.studentInfo.academy;
          console.log('当前学生书院:', this.currentAcademy);
        } else {
          console.error('获取学生信息失败，响应中没有studentInfo:', response);
          this.currentAcademy = '未知';
        }
      } catch (error) {
        console.error('获取学生信息异常:', error);
        this.currentAcademy = '未知';
      }
    },

    // 初始化图表
    initCharts() {
      this.initActivityChart();
      this.initCourseChart();
    },

    // 初始化活动统计图表
    initActivityChart() {
      const chart = echarts.init(this.$refs.activityChart);
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['未审核', '已通过', '未通过']
        },
        series: [
          {
            name: '活动统计',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['60%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: this.activityStats.pending, name: '未审核', itemStyle: { color: '#409EFF' } },
              { value: this.activityStats.approved, name: '已通过', itemStyle: { color: '#67C23A' } },
              { value: this.activityStats.rejected, name: '未通过', itemStyle: { color: '#F56C6C' } }
            ]
          }
        ]
      };
      chart.setOption(option);
      
      // 添加点击事件
      chart.on('click', (params) => {
        this.handleActivityChartClick(params.name);
      });
    },

    // 初始化课程统计图表
    initCourseChart() {
      const chart = echarts.init(this.$refs.courseChart);
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['未考核', '已通过', '未通过']
        },
        series: [
          {
            name: '课程统计',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['60%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: this.courseStats.pending, name: '未考核', itemStyle: { color: '#409EFF' } },
              { value: this.courseStats.approved, name: '已通过', itemStyle: { color: '#67C23A' } },
              { value: this.courseStats.rejected, name: '未通过', itemStyle: { color: '#F56C6C' } }
            ]
          }
        ]
      };
      chart.setOption(option);
      
      // 添加点击事件
      chart.on('click', (params) => {
        this.handleCourseChartClick(params.name);
      });
    },

    // 获取进度条颜色
    getProgressColor(percentage) {
      if (percentage >= 80) return '#67C23A';
      if (percentage >= 60) return '#E6A23C';
      return '#F56C6C';
    },

    // 获取近期活动数据
    async getRecentActivities() {
      try {
        const response = await listActivities({
          pageNum: 1,
          pageSize: 10
        });
        
        if (response.code === 200 && response.rows) {
          // 按活动开始时间排序，获取最近的5个活动
          const sortedActivities = response.rows
            .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
            .slice(0, 5);
          
          this.recentActivities = sortedActivities.map(activity => {
            const startTime = new Date(activity.startTime);
            const endTime = new Date(activity.endTime);
            
            return {
              activityId: activity.activityId,
              title: activity.activityName,
              location: activity.activityLocation,
              time: this.formatTimeRange(startTime, endTime),
              day: startTime.getDate().toString(),
              month: (startTime.getMonth() + 1) + '月',
              status: this.getActivityStatus(activity),
              statusText: this.getActivityStatusText(activity),
              organizer: activity.organizer,
              activityType: activity.activityType,
              description: activity.activityDescription
            };
          });
        }
      } catch (error) {
        console.error('获取近期活动失败:', error);
        // 如果获取失败，使用默认数据
        this.recentActivities = [
          {
            activityId: 1,
            title: '学术讲座：机器学习前沿技术',
            location: '学术报告厅',
            time: '14:00-16:00',
            day: '20',
            month: '1月',
            status: 'upcoming',
            statusText: '报名进行中',
            organizer: '计算机学院',
            activityType: '2',
            description: '介绍机器学习的最新发展'
          }
        ];
      }
    },

    // 格式化时间范围
    formatTimeRange(startTime, endTime) {
      const start = startTime.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: false 
      });
      const end = endTime.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: false 
      });
      return `${start}-${end}`;
    },

    // 获取活动状态（用于CSS类名）
    getActivityStatus(activity) {
      const status = this.getActivityStatusText(activity);
      switch (status) {
        case '报名未开始': return 'upcoming';
        case '报名进行中': return 'upcoming';
        case '报名已截止': return 'upcoming';
        case '活动进行中': return 'ongoing';
        case '活动已结束': return 'ended';
        default: return 'upcoming';
      }
    },

    // 获取活动状态文本（与活动管理保持一致）
    getActivityStatusText(activity) {
      const now = new Date();
      const start = new Date(activity.startTime);
      const end = new Date(activity.endTime);
      const deadline = new Date(activity.activityDeadline);
      const activityStart = new Date(activity.activityStart);

      if (now < activityStart) return "报名未开始";
      if (now < deadline && now >= activityStart) return "报名进行中";
      if (now >= deadline && now < start) return "报名已截止";
      if (now >= start && now <= end) return "活动进行中";
      if (now > end) return "活动已结束";
      return activity.status || "未知";
    },

    // 跳转到活动管理页面
    goToActivityManager(activity) {
      // 跳转到活动管理页面
      if (activity && activity.activityId) {
        // 如果点击了具体活动，传递活动ID进行筛选
        this.$router.push({
          path: '/Activity/ActivityManager',
          query: {
            activityId: activity.activityId,
            activityName: activity.title
          }
        });
      } else {
        // 如果点击了"查看全部"按钮，直接跳转
        this.$router.push({
          path: '/Activity/ActivityManager'
        });
      }
    },

    // 获取本学期课程数据
    async getCurrentCourses() {
      try {
        const response = await listCourses({
          pageNum: 1,
          pageSize: 10
        });
        
        if (response.code === 200 && response.rows) {
          // 按课程开始时间排序，获取最近的4个课程
          const sortedCourses = response.rows
            .sort((a, b) => new Date(a.courseStart) - new Date(b.courseStart))
            .slice(0, 4);
          
          this.currentCourses = sortedCourses.map(course => {
            return {
              courseId: course.courseId,
              name: course.courseName,
              teacher: course.organizer || '待定',
              schedule: this.formatCourseSchedule(course),
              location: course.courseLocation || '待定',
              progress: this.calculateCourseProgress(course),
              courseCode: course.courseId ? `C${course.courseId}` : '待定',
              courseCategory: course.courseCategory || '待定',
              credit: course.courseCredit || 0,
              courseHours: 0, // Courses实体中没有此字段
              courseCapacity: course.courseTotalCapacity || 0,
              enrolledStudent: course.courseTotalCapacity && course.courseCapacity ? 
                (course.courseTotalCapacity - course.courseCapacity) : 0,
              status: this.computeCourseStatus(course)
            };
          });
        }
      } catch (error) {
        console.error('获取本学期课程失败:', error);
        // 如果获取失败，使用默认数据
        this.currentCourses = [
          {
            courseId: 1,
            name: '高等数学',
            teacher: '张教授',
            schedule: '周一 8:00-10:00',
            location: '数学学院',
            progress: 75,
            courseCode: 'MATH101',
            courseCategory: '必修',
            credit: 4,
            courseHours: 64,
            courseCapacity: 100,
            enrolledStudent: 75,
            status: '选课进行中'
          }
        ];
      }
    },

    // 格式化课程时间安排
    formatCourseSchedule(course) {
      if (course.courseStart && course.courseDeadline) {
        const start = new Date(course.courseStart);
        const deadline = new Date(course.courseDeadline);
        const startStr = start.toLocaleDateString('zh-CN', { 
          month: 'numeric', 
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        });
        const deadlineStr = deadline.toLocaleDateString('zh-CN', { 
          month: 'numeric', 
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        });
        return `${startStr} - ${deadlineStr}`;
      }
      return '时间待定';
    },

    // 计算课程进度（选课人数/总容量）
    calculateCourseProgress(course) {
      if (!course.courseTotalCapacity || course.courseTotalCapacity <= 0) return 0;
      if (!course.courseCapacity) return 0;
      const enrolledStudent = course.courseTotalCapacity - course.courseCapacity;
      const progress = Math.round((enrolledStudent / course.courseTotalCapacity) * 100);
      return Math.min(progress, 100); // 确保不超过100%
    },

    // 计算课程状态（与课程管理保持一致）
    computeCourseStatus(course) {
      const now = new Date();
      const start = course.courseStart ? new Date(course.courseStart) : null;
      const deadline = course.courseDeadline ? new Date(course.courseDeadline) : null;

      // 如果当前时间在选课开始时间之前，显示"选课未开始"
      if (start && now < start) {
        return '选课未开始';
      }

      // 如果当前时间在选课开始时间和截止时间之间，显示"选课进行中"
      if (start && deadline && now >= start && now <= deadline) {
        return '选课进行中';
      }

      // 如果当前时间超过选课截止时间，显示"选课已截止"
      if (deadline && now > deadline) {
        return '选课已截止';
      }

      // 默认状态
      return course.status || '选课未开始';
    },

    // 获取课程状态样式类
    getCourseStatusClass(status) {
      switch (status) {
        case '选课未开始': return 'status-not-started';
        case '选课进行中': return 'status-in-progress';
        case '选课已截止': return 'status-ended';
        default: return 'status-not-started';
      }
    },

    // 跳转到课程管理页面
    goToCourseManager(course) {
      // 跳转到课程管理页面
      if (course && course.courseId) {
        // 如果点击了具体课程，传递课程ID进行筛选
        this.$router.push({
          path: '/Course/CourseManager',
          query: {
            courseId: course.courseId,
            courseName: course.name
          }
        });
      } else {
        // 如果点击了"课程管理"按钮，直接跳转
        this.$router.push({
          path: '/Course/CourseManager'
        });
      }
    },

    // 处理活动统计图表点击事件
    handleActivityChartClick(statusName) {
      // 根据状态名称映射到活动审核页面的状态
      const statusMap = {
        '未审核': 'pending',
        '已通过': 'approved',
        '未通过': 'rejected'
      };
      
      const status = statusMap[statusName];
      if (status) {
        // 跳转到活动审核页面并传递状态参数
        this.$router.push({
          path: '/Activity/ActivityAudit',
          query: {
            status: status
          }
        });
      }
    },

    // 获取活动统计数据
    async getActivityStats() {
      try {
        const response = await getAuditCount();
        if (response.code === 200 && response.data) {
          this.activityStats = response.data;
          // 更新图表数据
          this.updateActivityChart();
        }
      } catch (error) {
        console.error('获取活动统计数据失败:', error);
        // 如果获取失败，使用默认数据
        this.activityStats = {
          pending: 35,
          approved: 25,
          rejected: 40
        };
        this.updateActivityChart();
      }
    },

    // 更新活动统计图表
    updateActivityChart() {
      if (this.$refs.activityChart) {
        const chart = echarts.getInstanceByDom(this.$refs.activityChart);
        if (chart) {
          const option = {
            series: [{
              data: [
                { value: this.activityStats.pending, name: '未审核', itemStyle: { color: '#409EFF' } },
                { value: this.activityStats.approved, name: '已通过', itemStyle: { color: '#67C23A' } },
                { value: this.activityStats.rejected, name: '未通过', itemStyle: { color: '#F56C6C' } }
              ]
            }]
          };
          chart.setOption(option);
        }
      }
    },

    // 获取课程统计数据
    async getCourseStats() {
      try {
        const response = await getCourseAuditCount();
        if (response.code === 200 && response.data) {
          this.courseStats = response.data;
          // 更新图表数据
          this.updateCourseChart();
        }
      } catch (error) {
        console.error('获取课程统计数据失败:', error);
        // 如果获取失败，使用默认数据
        this.courseStats = {
          pending: 30,
          approved: 20,
          rejected: 10
        };
        this.updateCourseChart();
      }
    },

    // 更新课程统计图表
    updateCourseChart() {
      if (this.$refs.courseChart) {
        const chart = echarts.getInstanceByDom(this.$refs.courseChart);
        if (chart) {
          const option = {
            series: [{
              data: [
                { value: this.courseStats.pending, name: '未考核', itemStyle: { color: '#409EFF' } },
                { value: this.courseStats.approved, name: '已通过', itemStyle: { color: '#67C23A' } },
                { value: this.courseStats.rejected, name: '未通过', itemStyle: { color: '#F56C6C' } }
              ]
            }]
          };
          chart.setOption(option);
        }
      }
    },

    // 处理课程统计图表点击事件
    handleCourseChartClick(statusName) {
      // 根据状态名称映射到课程审核页面的状态
      const statusMap = {
        '未考核': 'pending',
        '已通过': 'approved',
        '未通过': 'rejected'
      };
      
      const status = statusMap[statusName];
      if (status) {
        // 跳转到课程审核页面并传递状态参数
        this.$router.push({
          path: '/Course/CourseAudit',
          query: {
            status: status
          }
        });
      }
    }
  }
};
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  margin-left: 200px;
  background: #f5f7fa;
  padding: 20px;
  position: relative;
  width: calc(100% - 200px);
  overflow: visible;
  box-sizing: border-box;
}

/* 欢迎头部 */
.welcome-header {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e4e7ed;
}

.welcome-content {
  text-align: center;
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 10px 0;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
  font-weight: 500;
}

/* 主要内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  height: calc(100vh - 200px);
}

/* 左侧列 */
.left-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 右侧列 */
.right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 卡片通用样式 */
.el-card {
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title i {
  color: #409EFF;
}

/* 书院新闻卡片 */
.news-card {
  flex: 1;
}

.news-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #909399;
}

.news-empty i {
  font-size: 64px;
  margin-bottom: 20px;
  color: #c0c4cc;
}

.news-empty p {
  font-size: 16px;
  margin: 0;
  color: #909399;
}

.news-list {
  max-height: 400px;
  overflow-y: auto;
}

.news-item {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 15px;
}

.news-item:last-child {
  border-bottom: none;
}

.news-content {
  flex: 1;
}

.news-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.news-summary {
  font-size: 12px;
  color: #606266;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-date {
  font-size: 11px;
  color: #909399;
}

.news-image {
  width: 80px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 图表容器 */
.charts-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  flex: 1;
}

.chart-card {
  height: 300px;
}

.chart-content {
  height: 250px;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 近期活动卡片 */
.activity-card {
  flex: 1;
}

.activity-list {
  max-height: 400px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 15px;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  margin: 0 -10px;
  padding-left: 15px;
  padding-right: 15px;
}

.activity-item:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-time {
  text-align: center;
  min-width: 60px;
}

.time-day {
  font-size: 20px;
  font-weight: 700;
  color: #409EFF;
  line-height: 1;
}

.time-month {
  font-size: 12px;
  color: #909399;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.activity-location,
.activity-time-text {
  font-size: 12px;
  color: #606266;
  margin: 0 0 4px 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.activity-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.activity-status.upcoming {
  background: #f0f9ff;
  color: #409EFF;
}

.activity-status.ongoing {
  background: #f0f9ff;
  color: #67C23A;
}

.activity-status.ended {
  background: #fef0f0;
  color: #F56C6C;
}

/* 本学期课程卡片 */
.course-card {
  flex: 1;
}

.course-list {
  max-height: 400px;
  overflow-y: auto;
}

.course-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  margin: 0 -10px;
  padding-left: 15px;
  padding-right: 15px;
}

.course-item:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.course-item:last-child {
  border-bottom: none;
}

.course-info {
  margin-bottom: 10px;
}

.course-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.course-code {
  font-size: 11px;
  color: #909399;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.course-teacher,
.course-schedule,
.course-location,
.course-category {
  font-size: 12px;
  color: #606266;
  margin: 0 0 4px 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-progress {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-text {
  font-size: 12px;
  color: #606266;
  min-width: 60px;
}

.course-status {
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 500;
  margin-top: 4px;
}

.course-status.status-not-started {
  background: #f0f9ff;
  color: #409EFF;
}

.course-status.status-in-progress {
  background: #f0f9ff;
  color: #67C23A;
}

.course-status.status-ended {
  background: #fef0f0;
  color: #F56C6C;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .charts-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .app-container {
    margin-left: 0;
    width: 100%;
    padding: 10px;
  }
  
  .welcome-title {
    font-size: 24px;
  }
  
  .welcome-subtitle {
    font-size: 14px;
  }
}
</style>
