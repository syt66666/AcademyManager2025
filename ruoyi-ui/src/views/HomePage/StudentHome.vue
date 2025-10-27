<template>
  <div class="student-home-container" v-loading="loading">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1 class="welcome-title">
            <i class="el-icon-sunny"></i>
            {{ getGreeting() }}，{{ $store.state.user.nickName || $store.state.user.name }}！
          </h1>
          <p class="welcome-subtitle">{{ getCurrentDate() }}</p>
        </div>
        <div class="quick-actions">
          <div class="action-item" @click="goToActivityBookingPage">
            <div class="action-icon activity">
              <i class="el-icon-tickets"></i>
            </div>
            <span>活动预约</span>
          </div>
          <div class="action-item" @click="goToCourseBookingPage">
            <div class="action-icon course">
              <i class="el-icon-notebook-2"></i>
            </div>
            <span>课程选课</span>
          </div>
          <div class="action-item" @click="goToActivityParticipatePage">
            <div class="action-icon participate">
              <i class="el-icon-folder-opened"></i>
            </div>
            <span>我的活动</span>
          </div>
          <div class="action-item" @click="goToCourseParticipatePage">
            <div class="action-icon my-course">
              <i class="el-icon-document"></i>
            </div>
            <span>我的课程</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 顶部通知栏和近期活动 -->
    <div class="top-section">
      <!-- 左上角通知栏 -->
      <div class="notification-panel">
        <div class="panel-header">
          <div class="header-left">
            <i class="el-icon-bell"></i>
            <span>书院通知</span>
          </div>
          <el-button type="text" size="small" @click="goToAllNotifications" class="all-button">
            全部
          </el-button>
        </div>
        <div class="notification-content">
          <!-- 通知列表 -->
          <div v-if="notifications.length === 0" class="no-notification">
            <i class="el-icon-info"></i>
            <span>暂无通知</span>
          </div>
          <div v-else class="notification-list">
            <div
              v-for="notification in notifications.slice(0, 3)"
              :key="notification.notiId"
              class="notification-item"
              @click="showNotificationDetail(notification)"
            >
              <div class="notification-date">
                {{ formatNotificationDate(notification.createdAt) }}
              </div>
              <div class="notification-info">
                <div class="notification-title">
                  <span v-if="isNewNotification(notification)" class="badge new">NEW</span>
                  {{ notification.notiTitle }}
                </div>
                <div class="notification-badges">
                  <span v-if="isTopNotification(notification)" class="badge top">TOP</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右上角近期活动通知 -->
      <div class="recent-activities-panel">
        <div class="panel-header">
          <div class="header-left">
            <i class="el-icon-s-promotion"></i>
            <span>近期活动</span>
          </div>
          <el-button type="text" size="small" @click="goToAllActivities" class="all-button">
            全部
          </el-button>
        </div>
        <div class="activities-content">
          <div v-if="recentActivities.length === 0" class="no-activity">
            <i class="el-icon-info"></i>
            <span>暂无活动</span>
          </div>
          <div v-else class="activity-list">
            <div
              v-for="activity in recentActivities.slice(0, 3)"
              :key="activity.activityId"
              class="activity-item"
              @click="goToActivityBooking(activity)"
            >
              <div class="activity-date">
                {{ formatActivityDate(activity.activityStart) }}
              </div>
              <div class="activity-content">
                <div class="activity-name">
                  {{ activity.activityName }}
                </div>
              </div>
              <div class="activity-time-section">
                <span class="activity-time-label">活动时间：</span>
                <span class="time-info">
                  <i class="el-icon-time"></i>
                  {{ formatActivityTimeRange(activity.startTime, activity.endTime) }}
                </span>
              </div>
              <div class="activity-status">
                <el-tag :type="getActivityStatusTag(activity)" size="mini">
                  {{ getActivityStatusText(activity) }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 中间部分 -->
    <div class="middle-section">
      <!-- 左边活动完成情况 -->
      <div class="activity-completion-panel">
        <div class="panel-header">
          <div class="header-left">
            <i class="el-icon-data-line"></i>
            <span>活动完成进度</span>
          </div>
        </div>
        <div class="completion-content">
          <div class="progress-categories">
            <div
              v-for="category in activityCategories"
              :key="category.type"
              class="progress-category"
              @click="goToActivityParticipateByType(category.type)"
            >
              <el-tooltip
                :content="getCompletedActivitiesText(category)"
                placement="top"
                effect="dark"
                :disabled="category.completed === 0"
              >
                <div class="category-label">{{ category.name }}</div>
              </el-tooltip>
              <div class="progress-bar-container">
                <div class="progress-bar">
                  <div
                    class="progress-fill"
                    :style="{ width: category.progress + '%' }"
                  ></div>
                </div>
              </div>
              <div class="progress-text">{{ category.completed }}/{{ category.total }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 状态筛选区域 -->
      <div class="status-filters-section">
        <!-- 活动状态筛选 -->
        <div class="activity-status-panel">
          <div class="panel-header">
            <div class="header-left">
              <i class="el-icon-s-operation"></i>
              <span>活动状态筛选</span>
            </div>
          </div>
          <div class="status-content">
            <div class="status-filters">
              <!-- 所有状态在一行显示 -->
              <div class="status-filters-row">
                <div
                  v-for="status in activityStatusFilters"
                  :key="status.value"
                  class="status-filter-item"
                  :class="{ active: selectedStatus === status.value }"
                  @click="filterByStatus(status.value)"
                >
                  <div class="status-icon" :class="status.iconClass">
                    <i :class="status.icon"></i>
                  </div>
                  <div class="status-info">
                    <div class="status-count">{{ status.count }}</div>
                    <div class="status-label">{{ status.label }}</div>
                  </div>
                </div>
                <div
                  class="status-filter-item clear-filter"
                  :class="{ active: selectedStatus === null }"
                  @click="clearStatusFilter"
                >
                  <div class="status-icon all">
                    <i class="el-icon-view"></i>
                  </div>
                  <div class="status-info">
                    <div class="status-count">{{ totalAll }}</div>
                    <div class="status-label">全部</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 课程状态筛选 -->
        <div class="course-status-panel">
          <div class="panel-header">
            <div class="header-left">
              <i class="el-icon-s-grid"></i>
              <span>课程状态筛选</span>
            </div>
          </div>
          <div class="status-content">
            <div class="status-filters">
              <!-- 所有状态在一行显示 -->
              <div class="status-filters-row">
                <div
                  v-for="status in courseStatusFilters"
                  :key="status.value"
                  class="status-filter-item"
                  :class="{ active: selectedCourseStatus === status.value }"
                  @click="filterByCourseStatus(status.value)"
                >
                  <div class="status-icon" :class="status.iconClass">
                    <i :class="status.icon"></i>
                  </div>
                  <div class="status-info">
                    <div class="status-count">{{ status.count }}</div>
                    <div class="status-label">{{ status.label }}</div>
                  </div>
                </div>
                <div
                  class="status-filter-item clear-filter"
                  :class="{ active: selectedCourseStatus === null }"
                  @click="clearCourseStatusFilter"
                >
                  <div class="status-icon all">
                    <i class="el-icon-view"></i>
                  </div>
                  <div class="status-info">
                    <div class="status-count">{{ totalCourseAll }}</div>
                    <div class="status-label">全部</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部已选课程 -->
    <div class="bottom-section">
      <div class="selected-courses-panel">
        <div class="panel-header">
          <div class="header-left">
            <i class="el-icon-reading"></i>
            <span>我的选课记录</span>
          </div>
          <div class="header-right">
            <el-button type="text" size="small" @click="goToAllCourses" class="all-button">
              全部
            </el-button>
          </div>
        </div>
        <div class="courses-content">
          <div v-if="selectedCourses.length === 0" class="no-course">
            <i class="el-icon-info"></i>
            <span>暂无选课记录</span>
          </div>
          <div v-else class="course-list">
            <div
              v-for="course in selectedCourses"
              :key="course.id"
              class="course-item"
              @click="goToCourseParticipate(course)"
            >
              <div class="course-date">{{ formatCourseDate(course.courseStart) }}</div>
              <div class="course-content">
                <div class="course-title-row">
                  <h4 class="course-title">{{ course.courseName }}</h4>
                </div>
              </div>
              <div class="course-time-section">
                <span class="course-time-label">课程时间：</span>
                <span class="course-time-inline">
                  <i class="el-icon-time"></i>
                  {{ formatCourseTimeRange(course.courseStart, course.courseDeadline) }}
                </span>
              </div>
              <div class="course-status" :class="getCourseStatusClass(course.status)">
                {{ course.status }}
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>

    <!-- 通知详情弹窗 -->
    <el-dialog
      title="通知详情"
      :visible.sync="notificationDialogVisible"
      :width="dialogWidth"
      :close-on-click-modal="false"
      :modal="true"
      :lock-scroll="true"
      custom-class="notification-dialog"
      :before-close="handleDialogClose"
    >
      <div v-if="currentNotification" class="notification-detail">
        <div class="detail-header">
          <h3 class="detail-title">{{ currentNotification.notiTitle }}</h3>
          <div class="detail-meta">
            <span class="detail-time">{{ formatDateTime(currentNotification.createdAt) }}</span>
          </div>
        </div>

        <div class="detail-content">
          <div class="content-text" v-html="formatNotificationContent(currentNotification.notiContent)"></div>
        </div>

        <!-- 附件部分 -->
        <div v-if="hasAttachments(currentNotification)" class="detail-attachments">
          <h4>附件</h4>

          <!-- 文档附件 -->
          <div v-if="currentNotification.fileAttachments && currentNotification.fileAttachments.length > 0" class="attachment-section">
            <h5>文档附件</h5>
            <div class="attachment-list">
              <div
                v-for="(file, index) in currentNotification.fileAttachments"
                :key="index"
                class="attachment-item"
              >
                <i class="el-icon-document"></i>
                <span class="file-name">{{ getFileName(file) }}</span>
                <el-button type="text" size="mini" @click="downloadFile(file)">
                  <i class="el-icon-download"></i> 下载
                </el-button>
              </div>
            </div>
          </div>

          <!-- 图片附件 -->
          <div v-if="currentNotification.pictureAttachments && currentNotification.pictureAttachments.length > 0" class="attachment-section">
            <h5>图片附件</h5>
            <div class="image-gallery">
              <div
                v-for="(image, index) in currentNotification.pictureAttachments"
                :key="index"
                class="image-item"
                @click="previewImage(image)"
              >
                <img :src="image" :alt="'图片' + (index + 1)" />
                <div class="image-overlay">
                  <i class="el-icon-zoom-in"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="notificationDialogVisible = false" size="medium">
          <i class="el-icon-close"></i> 关闭
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNotificationsPublic } from "@/api/system/notifications";
import { listActivities } from "@/api/system/activities";
import { listActivity } from "@/api/system/activity";
import { listBookingsWithActivity } from "@/api/system/bookings";
import { listBookingsWithCourse } from "@/api/system/courseBookings";
import { getStudent } from "@/api/system/student";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "StudentHome",
  data() {
    return {
       // 通知数据
       notifications: [],
       userCollege: null, // 用户所属书院（来自stu_info表的academy字段）

      // 通知详情弹窗
      notificationDialogVisible: false,
      currentNotification: null,

      // 近期活动数据
      recentActivities: [],

      // 活动分类进度数据
      activityCategories: [
        {
          type: 'personality',
          name: '人格塑造与价值引领活动类',
          completed: 0,
          total: 8,
          progress: 0
        },
        {
          type: 'knowledge',
          name: '知识融合与思维进阶活动类',
          completed: 0,
          total: 8,
          progress: 0
        },
        {
          type: 'ability',
          name: '能力锻造与实践创新活动类',
          completed: 0,
          total: 8,
          progress: 0
        },
        {
          type: 'social',
          name: '社会责任与领军意识活动类',
          completed: 0,
          total: 8,
          progress: 0
        }
      ],

      // 活动状态筛选
      selectedStatus: null,
      activityStatusFilters: [
        { value: '未提交', label: '未提交', icon: 'el-icon-upload2', iconClass: 'unsubmitted', count: 0 },
        { value: '未通过', label: '未通过', icon: 'el-icon-close', iconClass: 'rejected', count: 0 },
        { value: '未审核', label: '未审核', icon: 'el-icon-time', iconClass: 'pending', count: 0 },
        { value: '已通过', label: '已通过', icon: 'el-icon-check', iconClass: 'approved', count: 0 }
      ],

      // 课程状态筛选
      selectedCourseStatus: null,
      courseStatusFilters: [
        { value: '未提交', label: '未提交', icon: 'el-icon-upload2', iconClass: 'unsubmitted', count: 0 },
        { value: '未通过', label: '未通过', icon: 'el-icon-close', iconClass: 'rejected', count: 0 },
        { value: '未考核', label: '未考核', icon: 'el-icon-time', iconClass: 'pending', count: 0 },
        { value: '已通过', label: '已通过', icon: 'el-icon-check', iconClass: 'approved', count: 0 }
      ],

      // 已选课程数据
      selectedCourses: [],
      totalCourses: 0,


      // 加载状态
      loading: false,
      notificationsLoading: false,
      activitiesLoading: false,
      coursesLoading: false,

      // 自动刷新定时器
      refreshTimer: null
    };
  },
  computed: {
    // 计算总活动数
    totalAll() {
      return this.activityStatusFilters.reduce((sum, filter) => sum + filter.count, 0);
    },

    // 计算总课程数
    totalCourseAll() {
      return this.courseStatusFilters.reduce((sum, filter) => sum + filter.count, 0);
    },

    // 计算弹窗宽度
    dialogWidth() {
      // 根据屏幕宽度和侧边栏宽度计算弹窗宽度
      const screenWidth = window.innerWidth;
      const sidebarWidth = screenWidth > 1200 ? 200 : (screenWidth > 768 ? 64 : 0);
      const availableWidth = screenWidth - sidebarWidth - 40; // 减去40px的边距
      return Math.max(availableWidth, 600) + 'px'; // 最小宽度600px
    }
  },
      async created() {
     // 先获取学生书院信息，然后加载数据
     await this.initUserCollege();
     this.loadData();
     // 设置自动刷新，每5分钟刷新一次数据
     this.refreshTimer = setInterval(() => {
       this.loadData();
     }, 5 * 60 * 1000);
   },
  beforeDestroy() {
    // 清理定时器
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer);
    }
  },
  methods: {

     // 初始化用户书院信息
     async initUserCollege() {
       try {
         console.log('=== 开始获取学生书院信息 ===');
         console.log('当前登录用户:', this.$store.state.user.name);

         // 直接调用获取学生信息的API
         const response = await getStudent(this.$store.state.user.name);
         console.log('学生信息API响应:', response);

         if (response && response.studentInfo && response.studentInfo.academy) {
           this.userCollege = response.studentInfo.academy;
           console.log('✅ 学生书院信息获取成功:', this.userCollege);
         } else {
           console.warn('⚠️ 未找到学生书院信息:', response);
           this.userCollege = null;
         }
       } catch (error) {
         console.error('❌ 获取学生书院信息失败:', error);
         this.userCollege = null;
         this.$message.warning('获取书院信息失败，请联系管理员');
       }
     },

     // 显示通知详情
    showNotificationDetail(notification) {
      this.currentNotification = notification;
      this.notificationDialogVisible = true;
    },

    // 格式化通知内容
    formatNotificationContent(content) {
      if (!content) return '暂无内容';
      // 将换行符转换为HTML换行
      return content.replace(/\n/g, '<br>');
    },

    // 检查是否有附件
    hasAttachments(notification) {
      const hasFiles = notification.fileAttachments && notification.fileAttachments.length > 0;
      const hasPictures = notification.pictureAttachments && notification.pictureAttachments.length > 0;
      return hasFiles || hasPictures;
    },

    // 获取文件名
    getFileName(filePath) {
      if (!filePath) return '';
      return filePath.split('/').pop() || filePath;
    },

    // 下载文件
    downloadFile(filePath) {
      if (!filePath) return;
      // 创建下载链接
      const link = document.createElement('a');
      link.href = filePath;
      link.download = this.getFileName(filePath);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    // 预览图片
    previewImage(imagePath) {
      if (!imagePath) return;
      // 使用Element UI的图片预览功能
      this.$alert(`<img src="${imagePath}" style="max-width: 100%; height: auto;" />`, '图片预览', {
        dangerouslyUseHTMLString: true,
        showConfirmButton: false,
        customClass: 'image-preview-dialog'
      });
    },

    // 加载所有数据
    async loadData() {
      this.loading = true;
      try {
        await Promise.all([
          this.loadNotifications(),
          this.loadRecentActivities(),
          this.loadActivityProgress(),
          this.loadSelectedCourses()
        ]);
      } catch (error) {
        console.error('加载数据失败:', error);
        this.$message.error('加载数据失败');
      } finally {
        this.loading = false;
      }
    },

     // 加载通知数据
     async loadNotifications() {
       this.notificationsLoading = true;
       try {
         console.log('=== 开始加载通知数据 ===');
         console.log('当前用户信息:', this.$store.state.user);
         console.log('当前用户书院（已获取）:', this.userCollege);

         // 使用公开接口，获取所有通知
         const response = await listNotificationsPublic({
           pageNum: 1,
           pageSize: 10  // 增加页面大小以显示所有通知
         });

         console.log('通知API响应:', response);

         if (response && response.code === 200) {
           // 获取所有通知数据
           const allNotifications = response.rows || [];
           console.log('所有通知数据:', allNotifications);

           // 根据用户书院过滤通知（匹配noti_type字段）
           if (this.userCollege) {
             const filteredNotifications = allNotifications.filter(notification => {
               // 检查通知的noti_type字段
               const notificationCollege = notification.noti_type || notification.notiType;
               const isMatch = notificationCollege === this.userCollege;

               console.log('通知过滤检查:', {
                 title: notification.notiTitle,
                 noti_type: notification.noti_type,
                 notiType: notification.notiType,
                 userCollege: this.userCollege,
                 isMatch: isMatch
               });

               return isMatch;
             });

             // 限制显示数量为5条，与近期活动保持一致
             this.notifications = filteredNotifications.slice(0, 5);
             console.log(`✅ 过滤后的书院通知 (${this.userCollege}):`, this.notifications);
             console.log(`原始通知数: ${allNotifications.length}, 过滤后通知数: ${filteredNotifications.length}, 显示数量: ${this.notifications.length}`);
           } else {
             // 如果没有书院信息，不显示任何通知（安全考虑）
             this.notifications = [];
             console.warn('⚠️ 未找到用户书院信息，不显示任何通知');
             this.$message.warning('未找到您的书院信息，请联系管理员');
           }
         } else {
           console.log('API返回非200状态码:', response?.code, response?.msg);
           this.$message.error('加载通知失败: ' + (response?.msg || '服务器返回错误'));
         }

       } catch (error) {
         console.error('=== 通知加载失败 ===');
         console.error('错误对象:', error);
         console.error('错误类型:', typeof error);
         console.error('错误值:', error);

         if (error.response) {
           console.error('HTTP响应错误:');
           console.error('状态码:', error.response.status);
           console.error('状态文本:', error.response.statusText);
           console.error('响应数据:', error.response.data);
           console.error('响应头:', error.response.headers);

           this.$message.error(`加载通知失败: HTTP ${error.response.status} - ${error.response.statusText}`);
         } else if (error.request) {
           console.error('网络请求错误:');
           console.error('请求对象:', error.request);
           this.$message.error('加载通知失败: 网络请求失败，请检查网络连接');
         } else {
           console.error('其他错误:', error.message);
           this.$message.error('加载通知失败: ' + error.message);
         }
       } finally {
         this.notificationsLoading = false;
       }
     },

     // 加载近期活动数据（保持原来的书院筛选方法，但使用与AcademicTeacherHome.vue一致的其他逻辑）
     async loadRecentActivities() {
       this.activitiesLoading = true;
       try {
         console.log('=== 开始加载近期活动数据 ===');
         console.log('当前用户书院（已获取）:', this.userCollege);

         // 获取所有活动
         const response = await listActivities({
           pageNum: 1,
           pageSize: 1000 // 获取所有数据
         });

         console.log('活动API响应:', response);

         if (response.code === 200) {
           const allActivities = response.rows || [];
           console.log('所有活动数据:', allActivities);

           // 根据用户书院过滤活动（匹配organizer字段）
           let filteredActivities = allActivities;
           if (this.userCollege) {
             filteredActivities = allActivities.filter(activity => {
               // 检查活动的organizer字段
               const activityOrganizer = activity.organizer;
               const isMatch = activityOrganizer === this.userCollege;

               console.log('活动过滤检查:', {
                 activityName: activity.activityName,
                 organizer: activity.organizer,
                 userCollege: this.userCollege,
                 isMatch: isMatch
               });

               return isMatch;
             });

             console.log(`✅ 过滤后的书院活动 (${this.userCollege}):`, filteredActivities);
             console.log(`原始活动数: ${allActivities.length}, 过滤后活动数: ${filteredActivities.length}`);
           } else {
             console.warn('⚠️ 未找到用户书院信息，显示所有活动');
           }

           // 按活动开始时间降序排序，获取最近的5个活动（与AcademicTeacherHome.vue保持一致）
           const sortedActivities = filteredActivities
             .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
             .slice(0, 5);

           this.recentActivities = sortedActivities.map(activity => {
             const startTime = new Date(activity.startTime);
             const endTime = new Date(activity.endTime);

             return {
               activityId: activity.activityId,
               activityName: activity.activityName,
               activityLocation: activity.activityLocation,
               startTime: activity.startTime,
               endTime: activity.endTime,
               activityStart: activity.activityStart,
               activityDeadline: activity.activityDeadline,
               organizer: activity.organizer,
               activityType: activity.activityType,
               activityDescription: activity.activityDescription,
               status: this.getActivityStatus(activity),
               statusText: this.getActivityStatusText(activity)
             };
           });
           
           console.log('筛选后的活动数据:', this.recentActivities);
         } else {
           console.error('API响应错误:', response);
           this.$message.error('加载近期活动失败');
         }
       } catch (error) {
         console.error('获取近期活动失败:', error);
         this.$message.error('加载近期活动失败');
       } finally {
         this.activitiesLoading = false;
       }
     },

    // 加载活动完成进度数据
    async loadActivityProgress() {
      try {
        // 获取活动预约记录数据（包含活动信息）
        const response = await listBookingsWithActivity({
          pageNum: 1,
          pageSize: 1000,
          studentId: this.$store.state.user.name
        });

        if (response.code === 200) {
          const bookingRecords = response.rows || [];
          console.log('=== 活动预约记录数据 ===');
          console.log('API响应:', response);
          console.log('总记录数:', bookingRecords.length);
          console.log('预约记录详情:', bookingRecords);
          console.log('当前用户:', this.$store.state.user.name);

          if (bookingRecords.length === 0) {
            console.warn('⚠️ bookings表中没有数据！');
            console.warn('请检查：');
            console.warn('1. 学生是否已预约活动');
            console.warn('2. bookings表中是否有该学生的记录');
            console.warn('3. API接口是否正确');
          } else {
            console.log('✅ 找到预约记录，开始处理数据...');
            bookingRecords.forEach((record, index) => {
              console.log(`记录 ${index + 1}:`, {
                activityId: record.activityId,
                activityName: record.activityName,
                activityType: record.activityType,
                status: record.status,
                studentId: record.studentId
              });
            });
          }

          // 按活动类型分类统计
          this.activityCategories.forEach(category => {
            const categoryRecords = bookingRecords.filter(record =>
              this.getActivityType(record.activityType) === category.type
            );
            // 获取已完成的活动
            const completedRecords = categoryRecords.filter(record =>
              record.status === '已通过'
            );

            category.completed = completedRecords.length;
            category.total = Math.max(categoryRecords.length, 8); // 至少显示8个
            category.progress = category.total > 0 ? Math.round((category.completed / category.total) * 100) : 0;

            // 添加已完成活动的详细信息
            category.completedActivities = completedRecords.map(record => ({
              name: record.activityName,
              activityId: record.activityId,
              completedTime: record.updatedAt || record.createdAt
            }));

            console.log(`分类 ${category.name} 统计:`, {
              completed: category.completed,
              total: category.total,
              progress: category.progress,
              completedActivities: category.completedActivities
            });
          });

          // 更新活动状态筛选的计数
          this.updateActivityStatusCounts(bookingRecords);
        } else {
          this.$message.error('加载活动进度失败');
        }
      } catch (error) {
        console.error('加载活动进度失败:', error);
        this.$message.error('加载活动进度失败');
      }
    },

    // 加载已选课程数据
    async loadSelectedCourses() {
      this.coursesLoading = true;
      try {
        const response = await listBookingsWithCourse({
          pageNum: 1,
          pageSize: 1000, // 获取所有数据
          studentId: this.$store.state.user.name
        });
        if (response.code === 200) {
          const allCourses = response.rows || [];

          // 按course_start时间倒序排列选课记录
          this.selectedCourses = allCourses
            .filter(course => course.courseStart) // 过滤掉没有course_start时间的课程
            .sort((a, b) => new Date(b.courseStart) - new Date(a.courseStart));

          this.totalCourses = this.selectedCourses.length;

          // 更新课程状态筛选的计数
          this.updateCourseStatusCounts(allCourses);

          console.log('按course_start时间倒序排列的选课记录:', this.selectedCourses);
        } else {
          this.$message.error('加载选课记录失败');
        }
      } catch (error) {
        console.error('加载选课记录失败:', error);
        this.$message.error('加载选课记录失败');
      } finally {
        this.coursesLoading = false;
      }
    },

    // 格式化日期
    formatDate(date) {
      return parseTime(date, '{y}-{m}-{d}');
    },

    // 格式化日期时间
    formatDateTime(date) {
      return parseTime(date, '{y}-{m}-{d} {h}:{i}:{s}');
    },

    // 格式化活动日期（显示为"月 日"格式）
    formatActivityDate(date) {
      if (!date) return '';
      const d = new Date(date);
      const day = d.getDate();
      const month = d.getMonth() + 1;
      return `${month}月${day}`;
    },

    // 格式化活动时间范围（显示为"HH:MM-HH:MM"格式）
    formatActivityTimeRange(startTime, endTime) {
      if (!startTime || !endTime) return '00:00-00:00';
      
      const start = new Date(startTime);
      const end = new Date(endTime);
      
      const startHour = start.getHours().toString().padStart(2, '0');
      const startMinute = start.getMinutes().toString().padStart(2, '0');
      const endHour = end.getHours().toString().padStart(2, '0');
      const endMinute = end.getMinutes().toString().padStart(2, '0');
      
      return `${startHour}:${startMinute}-${endHour}:${endMinute}`;
    },

    // 格式化课程日期（显示为"月 日"格式）
    formatCourseDate(date) {
      if (!date) return '';
      const d = new Date(date);
      const day = d.getDate();
      const month = d.getMonth() + 1;
      return `${month}月${day}`;
    },

    // 格式化课程时间范围（显示为"MM/DD HH:MM - MM/DD HH:MM"格式）
    formatCourseTimeRange(startTime, endTime) {
      if (!startTime || !endTime) return '00/00 00:00 - 00/00 00:00';
      
      const start = new Date(startTime);
      const end = new Date(endTime);
      
      const startMonth = (start.getMonth() + 1).toString().padStart(2, '0');
      const startDay = start.getDate().toString().padStart(2, '0');
      const startHour = start.getHours().toString().padStart(2, '0');
      const startMinute = start.getMinutes().toString().padStart(2, '0');
      
      const endMonth = (end.getMonth() + 1).toString().padStart(2, '0');
      const endDay = end.getDate().toString().padStart(2, '0');
      const endHour = end.getHours().toString().padStart(2, '0');
      const endMinute = end.getMinutes().toString().padStart(2, '0');
      
      return `${startMonth}/${startDay} ${startHour}:${startMinute} - ${endMonth}/${endDay} ${endHour}:${endMinute}`;
    },

    // 获取课程状态样式类
    getCourseStatusClass(status) {
      switch (status) {
        case '已通过': return 'status-approved';
        case '未审核': return 'status-pending';
        case '未通过': return 'status-rejected';
        case '未提交': return 'status-not-submitted';
        default: return 'status-pending';
      }
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

     // 获取活动状态标签类型
     getActivityStatusTag(activity) {
       const status = this.getActivityStatusText(activity);
       switch (status) {
         case "报名未开始": return "info";
         case "报名进行中": return "success";
         case "报名已截止": return "danger";
         case "活动进行中": return "warning";
         case "活动已结束": return "";
         default: return "danger";
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

    // 获取活动类型（基于activityType字段）
    getActivityType(activityType) {
      const typeMap = {
        '1': 'personality',  // 人格塑造与价值引领活动类
        '2': 'knowledge',    // 知识融合与思维进阶活动类
        '3': 'ability',      // 能力锻造与实践创新活动类
        '4': 'social'        // 社会责任与领军意识活动类
      };
      return typeMap[activityType] || 'personality';
    },

    // 获取活动类型名称
    getActivityTypeName(activityType) {
      const typeMap = {
        '1': '人格塑造与价值引领活动类',
        '2': '知识融合与思维进阶活动类',
        '3': '能力锻造与实践创新活动类',
        '4': '社会责任与领军意识活动类'
      };
      return typeMap[activityType] || activityType;
    },

    // 获取活动类型标签类型
    getActivityTypeTagType(activityType) {
      const map = {
        '1': 'primary',   // 人格塑造与价值引领活动类 - 蓝色
        '2': 'success',   // 知识融合与思维进阶活动类 - 绿色
        '3': 'warning',   // 能力锻造与实践创新活动类 - 橙色
        '4': 'danger',    // 社会责任与领军意识活动类 - 红色
        '其他': ''        // 默认蓝色
      }
      return map[activityType] || 'info';
    },

    // 获取课程状态标签类型
    getCourseStatusTag(status) {
      switch (status) {
        case "已通过": return "success";
        case "未审核": return "warning";
        case "未通过": return "danger";
        case "未提交": return "info";
        default: return "info";
      }
    },

    // 获取课程分类标签类型
    getCourseCategoryTagType(category) {
      const map = {
        '必修': 'danger',    // 必修 - 红色
        '选修': 'success',   // 选修 - 绿色
        '实践': 'warning',   // 实践 - 橙色
        '其他': 'info'       // 其他 - 蓝色
      }
      return map[category] || 'info';
    },

    // 获取课程分类名称
    getCourseCategoryName(category) {
      const categoryMap = {
        '必修': '必修',
        '选修': '选修',
        '实践': '实践',
        '其他': '其他'
      };
      return categoryMap[category] || category;
    },

    // 获取课程类型标签类型
    getCourseTypeTagType(courseType) {
      const map = {
        '1': 'primary',   // 人格塑造与价值引领活动类 - 蓝色
        '2': 'success',   // 知识融合与思维进阶活动类 - 绿色
        '3': 'warning',   // 能力锻造与实践创新活动类 - 橙色
        '4': 'danger',    // 社会责任与领军意识活动类 - 红色
        '其他': ''        // 默认蓝色
      }
      return map[courseType] || 'info';
    },

    // 获取课程类型名称
    getCourseTypeName(courseType) {
      const typeMap = {
        '1': '人格塑造与价值引领活动类',
        '2': '知识融合与思维进阶活动类',
        '3': '能力锻造与实践创新活动类',
        '4': '社会责任与领军意识活动类'
      };
      return typeMap[courseType] || courseType;
    },

    // 获取成绩来源标签类型
    getScoreTypeTagType(scoreType) {
      const typeMap = {
        '考试': 'danger',
        '考查': 'warning',
        '实践': 'success',
        '论文': 'info'
      };
      return typeMap[scoreType] || 'info';
    },

    // 判断课程是否已结束
    isCourseEnded(endTime) {
      if (!endTime) return false;
      return new Date(endTime) < new Date();
    },

    // 根据状态筛选
    filterByStatus(status) {
      this.$router.push({
        path: '/Activity/ActivityParticipate',
        query: {
          status: status,
          autoFilter: 'true'
        }
      }).catch(error => {
        console.error('跳转到活动参与页面失败:', error);
        this.$message.error('跳转到活动参与页面失败，请检查路由配置');
      });
    },

    // 清除状态筛选
    clearStatusFilter() {
      this.$router.push({
        path: '/Activity/ActivityParticipate'
      }).catch(error => {
        console.error('跳转到活动参与页面失败:', error);
        this.$message.error('跳转到活动参与页面失败，请检查路由配置');
      });
    },

    // 根据课程状态筛选
    filterByCourseStatus(status) {
      this.$router.push({
        path: '/Course/CourseParticipate',
        query: {
          status: status,
          autoFilter: 'true'
        }
      }).catch(error => {
        console.error('跳转到课程参与页面失败:', error);
        this.$message.error('跳转到课程参与页面失败，请检查路由配置');
      });
    },

    // 清除课程状态筛选
    clearCourseStatusFilter() {
      this.$router.push({
        path: '/Course/CourseParticipate'
      }).catch(error => {
        console.error('跳转到课程参与页面失败:', error);
        this.$message.error('跳转到课程参与页面失败，请检查路由配置');
      });
    },

    // 跳转到活动预约界面
    goToActivityBooking(activity) {
      console.log('准备跳转到活动预约界面，活动ID:', activity.activityId);
      console.log('活动信息:', activity);

      this.$router.push({
        path: '/Activity/ActivityBooking',
        query: { activityId: activity.activityId }
      }).then(() => {
        console.log('成功跳转到活动预约界面');
      }).catch(error => {
        console.error('跳转失败:', error);
        this.$message.error('跳转到活动预约界面失败，请检查路由配置');
      });
    },


    // 更新活动状态筛选的计数
    updateActivityStatusCounts(bookingRecords) {
      this.activityStatusFilters.forEach(filter => {
        filter.count = bookingRecords.filter(record =>
          record.status === filter.value
        ).length;
      });
    },

    // 更新课程状态筛选的计数
    updateCourseStatusCounts(courseRecords) {
      this.courseStatusFilters.forEach(filter => {
        filter.count = courseRecords.filter(record => {
          // "未考核"和"未审核"视为同一状态
          if (filter.value === '未考核') {
            return record.status === '未考核' || record.status === '未审核';
          }
          return record.status === filter.value;
        }).length;
      });
    },

    // 打开上传对话框
    openUploadDialog(course) {
      this.$message.info(`打开上传对话框: ${course.courseName}`);
    },

    // 处理弹窗关闭
    handleDialogClose(done) {
      this.notificationDialogVisible = false;
      done();
    },

    // 格式化通知日期
    formatNotificationDate(date) {
      if (!date) return '';
      const d = new Date(date);
      const day = d.getDate();
      const month = d.getMonth() + 1;
      return `${month}月${day}`;
    },

    // 判断是否为新通知（3天内）
    isNewNotification(notification) {
      if (!notification.createdAt) return false;
      const now = new Date();
      const notificationDate = new Date(notification.createdAt);
      const diffTime = Math.abs(now - notificationDate);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return diffDays <= 3;
    },

    // 判断是否为置顶通知
    isTopNotification(notification) {
      // 可以根据实际业务逻辑判断，比如优先级字段
      return notification.priority === 'high' || notification.isTop === true;
    },


    // 获取已完成活动的显示文本
    getCompletedActivitiesText(category) {
      if (!category.completedActivities || category.completedActivities.length === 0) {
        return '暂无已完成的活动';
      }

      const activityNames = category.completedActivities.map(activity => activity.name);
      return `已完成的活动：\n${activityNames.join('\n')}`;
    },

    // 获取问候语
    getGreeting() {
      const hour = new Date().getHours();
      if (hour < 6) return '凌晨好';
      if (hour < 9) return '早上好';
      if (hour < 12) return '上午好';
      if (hour < 14) return '中午好';
      if (hour < 17) return '下午好';
      if (hour < 19) return '傍晚好';
      if (hour < 22) return '晚上好';
      return '夜深了';
    },

    // 获取当前日期
    getCurrentDate() {
      const date = new Date();
      const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
      return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${weekDays[date.getDay()]}`;
    },

    // 快捷入口 - 活动预约
    goToActivityBookingPage() {
      this.$router.push('/Activity/ActivityBooking');
    },

    // 快捷入口 - 课程选课
    goToCourseBookingPage() {
      this.$router.push('/Course/CourseBooking');
    },

    // 快捷入口 - 我的活动
    goToActivityParticipatePage() {
      this.$router.push('/Activity/ActivityParticipate');
    },

    // 快捷入口 - 我的课程
    goToCourseParticipatePage() {
      this.$router.push('/Course/CourseParticipate');
    },

    // 跳转到所有通知页面
    goToAllNotifications() {
      this.goToNoticeManager();
    },

    goToNoticeManager() {
      this.$router.push('/Notice/student_notice');
    },

    // 跳转到所有活动页面（活动预约界面）
    goToAllActivities() {
      console.log('跳转到活动预约界面 - 活动列表视图');
      this.$router.push({
        path: '/Activity/ActivityBooking',
        query: {
          view: 'list',  // 指定跳转到活动列表视图
          tab: 'activity-list'  // 指定活动列表标签页
        }
      }).then(() => {
        console.log('成功跳转到活动预约界面 - 活动列表视图');
      }).catch(error => {
        console.error('跳转到活动预约界面失败:', error);
        this.$message.error('跳转到活动预约界面失败，请检查路由配置');
      });
    },

    // 根据活动类型跳转到活动参与界面
    goToActivityParticipateByType(activityType) {
      console.log('准备跳转到活动参与界面，活动类型:', activityType);

      // 将类型映射为数字
      const typeMap = {
        'personality': '1',  // 人格塑造与价值引领活动类
        'knowledge': '2',    // 知识融合与思维进阶活动类
        'ability': '3',      // 能力锻造与实践创新活动类
        'social': '4'        // 社会责任与领军意识活动类
      };

      const typeValue = typeMap[activityType] || activityType;

      this.$router.push({
        path: '/Activity/ActivityParticipate',
        query: {
          activityType: typeValue,
          autoFilter: 'true'
        }
      }).then(() => {
        console.log('成功跳转到活动参与界面，类型筛选:', typeValue);
      }).catch(error => {
        console.error('跳转到活动参与界面失败:', error);
        this.$message.error('跳转到活动参与界面失败，请检查路由配置');
      });
    },

    // 跳转到课程参与界面
    goToCourseParticipate(course) {
      console.log('准备跳转到课程参与界面，课程信息:', course);

      this.$router.push({
        path: '/Course/CourseParticipate',
        query: {
          courseId: course.id || course.courseId,
          courseName: course.courseName,
          autoFilter: 'true'
        }
      }).then(() => {
        console.log('成功跳转到课程参与界面，课程ID:', course.id || course.courseId);
      }).catch(error => {
        console.error('跳转到课程参与界面失败:', error);
        this.$message.error('跳转到课程参与界面失败，请检查路由配置');
      });
    },

    // 跳转到所有课程页面
    goToAllCourses() {
      console.log('跳转到所有课程页面');
      this.$router.push({
        path: '/Course/CourseParticipate'
      }).then(() => {
        console.log('成功跳转到所有课程页面');
      }).catch(error => {
        console.error('跳转到所有课程页面失败:', error);
        this.$message.error('跳转到所有课程页面失败，请检查路由配置');
      });
    }
  }
};
</script>

<style scoped>
.student-home-container {
  margin-left: 100px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  min-height: 100vh;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(96, 165, 250, 0.3);
  animation: fadeInDown 0.6s ease-out;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 32px;
}

.welcome-text {
  flex: 1;
}

.welcome-title {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: white;
  display: flex;
  align-items: center;
  gap: 12px;
  animation: fadeInLeft 0.8s ease-out;
}

.welcome-title i {
  font-size: 36px;
  animation: rotate 3s linear infinite;
}

.welcome-subtitle {
  margin: 8px 0 0 0;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  animation: fadeInLeft 1s ease-out;
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  gap: 16px;
  animation: fadeInRight 0.8s ease-out;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 100px;
}

.action-item:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.4);
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  transition: all 0.3s ease;
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-icon.activity {
  color: #60a5fa;
}

.action-icon.course {
  color: #8b5cf6;
}

.action-icon.participate {
  color: #10b981;
}

.action-icon.my-course {
  color: #f59e0b;
}

.action-item:hover .action-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.action-item span {
  font-size: 14px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
}

/* 顶部区域 */
.top-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.notification-panel,
.recent-activities-panel {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

/* 中间区域 */
.middle-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.status-filters-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
  flex: 1;
}

.activity-completion-panel {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

.activity-status-panel,
.course-status-panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

/* 底部区域 */
.bottom-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

/* 面板头部 */
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left i {
  font-size: 16px;
  color: white;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.college-name {
  font-size: 14px;
  font-weight: 400;
  color: #6b7280;
  margin-left: 4px;
}

.all-button {
  color: white !important;
  font-size: 14px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
  margin-left: auto;
}

.all-button:hover {
  background-color: rgba(255, 255, 255, 0.2) !important;
  color: white !important;
}


.view-all-btn {
  margin-left: auto;
  color: white;
  font-size: 12px;
}

.view-all-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.refresh-btn {
  margin-left: auto;
  color: white;
  font-size: 12px;
  padding: 4px 8px;
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.record-count {
  font-size: 12px;
  opacity: 0.9;
}

/* 面板内容 */
.notification-content,
.activities-content,
.courses-content {
  padding: 20px;
}

.completion-content,
.status-content {
  padding: 8px 12px; /* 减少内边距，让面板更紧凑 */
}

/* 通知类型标签页 */
.notification-tabs {
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 16px;
}


/* 通知列表 */
.notification-list {
  /* 移除高度限制和滚动条，让内容自适应 */
  min-height: auto;
}

.notification-item {
  display: flex;
  align-items: center;
  padding: 16px;
  margin-bottom: 8px;
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  gap: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.notification-item:last-child {
  margin-bottom: 0;
}

.notification-item:hover {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-color: #60a5fa;
  transform: translateX(4px);
  box-shadow: 0 8px 16px rgba(96, 165, 250, 0.15);
}

.notification-date {
  min-width: 60px;
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  flex-shrink: 0;
}

.notification-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.notification-title {
  font-weight: 700;
  color: #303133;
  font-size: 14px;
  line-height: 1.4;
  margin: 0;
  word-wrap: break-word;  /* 允许长文本换行，避免水平滚动 */
  overflow-wrap: break-word;  /* 兼容性更好的换行 */
  display: flex;
  align-items: center;
  gap: 8px;
}

.notification-badges {
  display: flex;
  gap: 6px;
  align-items: center;
}

.badge {
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
}

.badge.new {
  background: #ff6b6b;
  color: white;
}

.badge.top {
  background: #409EFF;
  color: white;
}

/* 活动列表 */
.activity-list {
  /* 移除高度限制，让内容自适应 */
  min-height: auto;
}

.activity-item {
  padding: 16px;
  margin-bottom: 8px;
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.activity-item:last-child {
  margin-bottom: 0;
}

.activity-item:hover {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-color: #f59e0b;
  transform: translateX(4px);
  box-shadow: 0 8px 16px rgba(245, 158, 11, 0.15);
}

.activity-date {
  min-width: 60px;
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  flex-shrink: 0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background: #f8f9fa;
  padding-left: 8px;
  border-radius: 6px;
}

.activity-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.activity-status {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.activity-name {
  font-weight: 700;
  color: #303133;
  margin-bottom: 6px;
  font-size: 14px;
  line-height: 1.4;
  display: flex;
  align-items: center;
  gap: 8px;
}

.activity-times {
  margin-bottom: 8px;
}

.time-info {
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  gap: 4px;
}

.time-info i {
  font-size: 12px;
  color: #909399;
}

.time-info:last-child {
  margin-bottom: 0;
}

.activity-time-section {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.activity-time-label {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.activity-status {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

/* 活动完成进度 */
.progress-categories {
  display: flex;
  flex-direction: column;
  gap: 8px; /* 减少类别间距 */
}

.progress-category {
  display: flex;
  align-items: center;
  gap: 8px; /* 减少元素间距 */
  cursor: pointer;
  padding: 8px 12px; /* 减少内边距 */
  border-radius: 8px; /* 减少圆角 */
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.progress-category:hover {
  background: #f0f9ff;
  border-color: #bae6fd;
  transform: translateY(-1px); /* 减少悬停移动距离 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08); /* 减少阴影 */
}

.category-label {
  min-width: 180px; /* 减少最小宽度 */
  font-size: 14px; /* 减少字体大小 */
  color: #303133;
  font-weight: 700;
}

.progress-bar-container {
  flex: 1;
  display: flex;
  align-items: center;
}

.progress-bar {
  width: 100%;
  height: 6px; /* 减少进度条高度 */
  background: #f0f2f5;
  border-radius: 3px; /* 减少圆角 */
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #60a5fa, #3b82f6);
  border-radius: 3px; /* 与进度条圆角保持一致 */
  transition: width 0.3s ease;
}

.progress-text {
  min-width: 35px; /* 减少最小宽度 */
  text-align: right;
  font-size: 14px; /* 减少字体大小 */
  color: #606266;
  font-weight: 500;
}

/* 状态筛选 */
.status-filters {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
  justify-content: center;
}

.status-filters-row {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
  width: 100%;
}

.status-filter-item {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  background: #f8fafc;
  border-radius: 6px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 70px;
  min-height: 50px;
  white-space: nowrap;
  flex-shrink: 0;
}

.status-filter-item:hover {
  background: #f0f9ff;
  border-color: #bae6fd;
  transform: translateY(-1px); /* 减少悬停移动距离 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08); /* 减少阴影 */
}

.status-filter-item.active {
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  border-color: #60a5fa;
  color: white;
  box-shadow: 0 4px 12px rgba(96, 165, 250, 0.3);
}

.status-filter-item.clear-filter {
  background: #f0f9ff;
  border-color: #bae6fd;
}

.status-filter-item.clear-filter.active {
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  border-color: #60a5fa;
  color: white;
}

.status-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 6px;
  font-size: 12px;
}

.status-icon.unsubmitted {
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  color: white;
}

.status-icon.pending {
  background: linear-gradient(135deg, #06b6d4, #0891b2);
  color: white;
}

.status-icon.approved {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}

.status-icon.rejected {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
}

.status-icon.all {
  background: linear-gradient(135deg, #64748b, #475569);
  color: white;
}

.status-info {
  display: flex;
  flex-direction: column;
  min-width: 0; /* 允许收缩 */
  flex-shrink: 0; /* 防止收缩 */
}

.status-count {
  font-size: 14px;
  font-weight: 700;
  line-height: 1;
}

.status-label {
  font-size: 11px;
  margin-top: 2px;
  font-weight: 600;
  white-space: nowrap;
}

.status-filter-item.active .status-count,
.status-filter-item.active .status-label {
  color: white;
}

/* 现代化表格 */
.modern-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.modern-table th {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9) !important;
  font-weight: 600;
  color: #1e293b;
  border-bottom: 2px solid #e2e8f0;
  padding: 16px 12px;
}

.modern-table td {
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 12px;
}

.modern-table tr:hover td {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9) !important;
}

/* 材料提交按钮样式 - 统一文字按钮风格 */
.action-button {
  padding: 5px 8px;
  font-size: 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.upload-button {
  color: #409EFF;
}

.upload-button:hover {
  background-color: rgba(64, 158, 255, 0.1);
}

.reupload-button {
  color: #ef4444;
}

.reupload-button:hover {
  background-color: rgba(239, 68, 68, 0.1);
}

.status-tag {
  padding: 5px 8px;
  font-size: 12px;
}

/* 重新提交标签样式 */
.status-tag.reupload-tag {
  cursor: pointer;
  transition: all 0.3s ease;
}

.status-tag.reupload-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 活动未结束标签特殊样式 */
.status-tag[type="info"] {
  background-color: #f4f4f5;
  border-color: #d3d4d6;
  color: #909399;
  font-weight: 500;
}

/* 序号徽章 */
.index-badge {
  display: inline-block;
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  color: white;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(96, 165, 250, 0.3);
}

/* 未通过状态的红色序号徽章 */
.index-badge.rejected-badge {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

/* 课程类型标签 */
.course-type-tag {
  font-weight: 500;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 学分显示样式 */
.credit-value {
  font-weight: 600;
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 13px;
}

/* 课程分类标签样式 */
.category-tag {
  font-weight: 500;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  font-size: 12px;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 成绩类型标签样式 */
.score-type-tag {
  font-weight: 500;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  font-size: 12px;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 无成绩样式 */
.no-score-type {
  color: #c0c4cc;
  font-size: 12px;
}

/* 可点击标签样式 */
.clickable-tag {
  cursor: pointer;
  transition: all 0.3s ease;
}

.clickable-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 分页组件样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding: 16px 0;
  border-top: 1px solid #ebeef5;
  background: #fafbfc;
}

.pagination-container .el-pagination {
  text-align: center;
}

.pagination-container .el-pagination .el-pagination__total {
  color: #606266;
  font-weight: 500;
}

.pagination-container .el-pagination .el-pagination__sizes {
  color: #606266;
}

.pagination-container .el-pagination .el-pager li {
  background: #ffffff;
  border: 1px solid #dcdfe6;
  color: #606266;
  font-weight: 500;
}

.pagination-container .el-pagination .el-pager li:hover {
  color: #409EFF;
}

.pagination-container .el-pagination .el-pager li.active {
  background: #409EFF;
  border-color: #60a5fa;
  color: #ffffff;
}

.pagination-container .el-pagination .btn-prev,
.pagination-container .el-pagination .btn-next {
  background: #ffffff;
  border: 1px solid #dcdfe6;
  color: #606266;
}

.pagination-container .el-pagination .btn-prev:hover,
.pagination-container .el-pagination .btn-next:hover {
  color: #409EFF;
}

.pagination-container .el-pagination .el-pagination__jump {
  color: #606266;
}

/* 无数据状态 */
.no-notification,
.no-activity,
.no-course {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #909399;
  min-height: 200px;
  padding: 40px 20px;
}

.no-notification i,
.no-activity i,
.no-course i {
  font-size: 48px;
  margin-bottom: 12px;
  color: #c0c4cc;
}

.no-notification span,
.no-activity span,
.no-course span {
  font-size: 14px;
  color: #909399;
}

/* 课程列表样式 */
.course-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  max-height: none;
  overflow-y: visible;
  flex: 1;
}

.course-item {
  display: flex;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #fafbfc 100%);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  gap: 16px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
}

.course-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.course-item:hover {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-color: #60a5fa;
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(96, 165, 250, 0.2);
}

.course-item:hover::before {
  transform: scaleY(1);
  border-color: #60a5fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.course-item:last-child {
  border-bottom: 1px solid #e4e7ed;
}

.course-date {
  min-width: 60px;
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  flex-shrink: 0;
}

.course-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  flex-direction: column;
}

.course-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
  line-height: 1.3;
}

.course-time-section {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.course-time-label {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.course-time-inline {
  font-size: 12px;
  color: #606266;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.course-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.course-status.status-approved {
  background: #f0f9ff;
  color: #67C23A;
}

.course-status.status-pending {
  background: #f0f9ff;
  color: #409EFF;
}

.course-status.status-rejected {
  background: #fef0f0;
  color: #F56C6C;
}

.course-status.status-not-submitted {
  background: #f4f4f5;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .top-section,
  .middle-section {
    flex-direction: column;
  }

  .progress-category {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .category-label {
    min-width: auto;
  }

  .status-filters {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .student-home-container {
    margin-left: 0;
    padding: 10px;
  }

  /* 欢迎横幅响应式 */
  .welcome-banner {
    padding: 20px;
  }

  .welcome-content {
    flex-direction: column;
    gap: 20px;
  }

  .welcome-title {
    font-size: 24px;
  }

  .welcome-title i {
    font-size: 28px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }

  .quick-actions {
    width: 100%;
    justify-content: space-around;
  }

  .action-item {
    min-width: 70px;
    padding: 12px 8px;
  }

  .action-icon {
    width: 48px;
    height: 48px;
    font-size: 24px;
  }

  .action-item span {
    font-size: 12px;
  }

  /* 顶部区域响应式 */
  .top-section {
    flex-direction: column;
  }

  .middle-section {
    flex-direction: column;
  }

  .status-filters {
    flex-direction: column;
  }

  .status-filter-item {
    min-width: auto;
  }

  /* 移动端课程列表改为单列 */
  .course-list {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .course-item {
    padding: 16px;
    flex-direction: column;
    align-items: stretch;
  }

  .course-title-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

/* 通知详情弹窗样式 */
.notification-dialog {
  border-radius: 8px !important;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e4e7ed;
}

.notification-dialog.el-dialog {
  border-radius: 8px !important;
}

.notification-dialog .el-dialog__header,
.notification-dialog.el-dialog .el-dialog__header {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%) !important;
  color: white !important;
  padding: 20px 24px !important;
  border-bottom: none !important;
  border-radius: 8px 8px 0 0 !important;
  position: relative !important;
  overflow: hidden !important;
  box-shadow: 0 2px 8px rgba(66, 165, 245, 0.3) !important;
}

.notification-dialog .el-dialog__header::before,
.notification-dialog.el-dialog .el-dialog__header::before {
  content: '' !important;
  position: absolute !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%) !important;
  pointer-events: none !important;
}

.notification-dialog .el-dialog__title,
.notification-dialog.el-dialog .el-dialog__title {
  color: white !important;
  font-size: 18px !important;
  font-weight: 700 !important;
  margin: 0 !important;
  letter-spacing: 0.5px !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2) !important;
  position: relative !important;
  z-index: 1 !important;
  display: flex !important;
  align-items: center !important;
  gap: 8px !important;
}

.notification-dialog .el-dialog__title::before,
.notification-dialog.el-dialog .el-dialog__title::before {
  content: '📢' !important;
  font-size: 16px !important;
  opacity: 0.9 !important;
}

.notification-dialog .el-dialog__headerbtn,
.notification-dialog.el-dialog .el-dialog__headerbtn {
  top: 20px !important;
  right: 24px !important;
  width: 32px !important;
  height: 32px !important;
  border-radius: 50% !important;
  background: rgba(255, 255, 255, 0.2) !important;
  transition: all 0.3s ease !important;
  z-index: 2 !important;
}

.notification-dialog .el-dialog__headerbtn:hover,
.notification-dialog.el-dialog .el-dialog__headerbtn:hover {
  background: rgba(255, 255, 255, 0.3) !important;
  transform: scale(1.1) !important;
}

.notification-dialog .el-dialog__headerbtn .el-dialog__close,
.notification-dialog.el-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white !important;
  font-size: 16px !important;
  font-weight: bold !important;
}

.notification-dialog .el-dialog__body,
.notification-dialog.el-dialog .el-dialog__body {
  padding: 0 !important;
  max-height: 80vh;
  overflow-y: auto;
  background: #ffffff !important;
  border-radius: 0 0 8px 8px !important;
}

.notification-dialog .el-dialog__footer {
  padding: 16px 24px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
  text-align: right;
  border-radius: 0 0 8px 8px;
}

.notification-detail {
  padding: 0;
  min-height: 500px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  position: relative;
}

/* 装饰元素 */
.notification-detail::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #60a5fa, #3b82f6, #2563eb);
  opacity: 0.8;
}

.detail-header {
  border-bottom: 2px solid transparent;
  background: linear-gradient(to bottom, #ffffff, #f9fafb);
  padding: 28px 32px;
  margin-bottom: 0;
  position: relative;
  overflow: hidden;
}

/* 标题区域装饰背景 */
.detail-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    linear-gradient(135deg, rgba(96, 165, 250, 0.03) 0%, transparent 50%),
    linear-gradient(-45deg, rgba(59, 130, 246, 0.02) 0%, transparent 50%);
  pointer-events: none;
}

.detail-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 32px;
  right: 32px;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    #e0e7ff 20%, 
    #93c5fd 50%, 
    #e0e7ff 80%, 
    transparent 100%
  );
}

.detail-title {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.4;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  position: relative;
  padding-left: 16px;
  letter-spacing: 0.3px;
}

/* 标题左侧装饰条 */
.detail-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 70%;
  background: linear-gradient(180deg, #60a5fa, #3b82f6);
  border-radius: 3px;
  box-shadow: 0 0 10px rgba(59, 130, 246, 0.3);
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 24px;
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  justify-content: flex-end;
  position: relative;
  padding: 12px 0 0 16px;
}

.detail-type {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  padding: 6px 14px;
  border-radius: 20px;
  border: 1px solid #bfdbfe;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.1);
}

.detail-time {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f8fafc;
  padding: 6px 14px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
}

.detail-time::before {
  content: "🕒";
  font-size: 14px;
}

.detail-content {
  margin-bottom: 24px;
  padding: 32px 36px;
  flex: 1;
  overflow-y: auto;
  background: #ffffff;
  position: relative;
}

/* 内容区域微妙的背景纹理 */
.detail-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(96, 165, 250, 0.02) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(59, 130, 246, 0.02) 0%, transparent 50%);
  pointer-events: none;
}

.content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #334155;
  white-space: pre-wrap;
  word-break: break-word;
  font-weight: 400;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  position: relative;
  padding: 20px 24px;
  background: #fafbfc;
  border-radius: 12px;
  border: 1px solid #e8ecf1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  min-height: 200px;
}

/* 内容文字区左侧装饰 */
.content-text::before {
  content: '"';
  position: absolute;
  left: 8px;
  top: 8px;
  font-size: 48px;
  color: #93c5fd;
  opacity: 0.3;
  font-family: Georgia, serif;
  line-height: 1;
}

.detail-attachments {
  border-top: 2px solid #f0f4f8;
  padding: 28px 32px;
  background: linear-gradient(to bottom, #fafbfc, #ffffff);
  margin-top: auto;
  position: relative;
}

/* 附件区域顶部装饰条 */
.detail-attachments::before {
  content: '';
  position: absolute;
  top: 0;
  left: 32px;
  right: 32px;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    #93c5fd 20%, 
    #60a5fa 50%, 
    #93c5fd 80%, 
    transparent 100%
  );
}

.detail-attachments h4 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  position: relative;
  padding-left: 16px;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 附件标题前的图标 */
.detail-attachments h4::before {
  content: '📎';
  position: absolute;
  left: 0;
  font-size: 16px;
}

.detail-attachments h5 {
  margin: 0 0 16px 0;
  font-size: 15px;
  font-weight: 600;
  color: #475569;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  padding: 8px 12px;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border-left: 3px solid #60a5fa;
  border-radius: 4px;
}

.attachment-section {
  margin-bottom: 24px;
}

.attachment-section:last-child {
  margin-bottom: 0;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #ffffff, #fafbfc);
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 0;
  position: relative;
  overflow: hidden;
}

/* 附件项悬停效果 */
.attachment-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, #60a5fa, #3b82f6);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.attachment-item:hover {
  background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
  border-color: #93c5fd;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(96, 165, 250, 0.15);
}

.attachment-item:hover::before {
  transform: scaleY(1);
}

.attachment-item i {
  color: #60a5fa;
  font-size: 20px;
  background: #eff6ff;
  padding: 10px;
  border-radius: 8px;
  flex-shrink: 0;
}

.file-name {
  flex: 1;
  font-size: 15px;
  color: #334155;
  word-break: break-all;
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* 附件下载按钮美化 */
.attachment-item .el-button {
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.25);
}

.attachment-item .el-button:hover {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.attachment-item .el-button i {
  background: transparent;
  color: white;
  padding: 0;
  margin-right: 4px;
  font-size: 14px;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
}

.image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.image-item:hover {
  transform: scale(1.08) translateY(-4px);
  border-color: #60a5fa;
  box-shadow: 0 8px 20px rgba(96, 165, 250, 0.25);
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-item:hover img {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(96, 165, 250, 0.85), rgba(59, 130, 246, 0.75));
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  backdrop-filter: blur(2px);
}

.image-item:hover .image-overlay {
  opacity: 1;
}

.image-overlay i {
  color: white;
  font-size: 28px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
  animation: iconPulse 2s ease-in-out infinite;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* 图片预览弹窗样式 */
.image-preview-dialog {
  text-align: center;
}

.image-preview-dialog .el-message-box__content {
  padding: 20px;
}

/* 通知弹窗底部按钮样式 */
.notification-dialog .dialog-footer {
  margin: 0;
}

.notification-dialog .dialog-footer .el-button {
  margin-left: 12px;
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.2s ease;
}

.notification-dialog .dialog-footer .el-button--primary {
  background: #3b82f6;
  border: none;
  color: white;
}

.notification-dialog .dialog-footer .el-button--primary:hover {
  background: #2563eb;
}

.notification-dialog .dialog-footer .el-button:not(.el-button--primary) {
  background: #ffffff;
  border: 1px solid #d1d5db;
  color: #6b7280;
}

.notification-dialog .dialog-footer .el-button:not(.el-button--primary):hover {
  background: #f9fafb;
  border-color: #9ca3af;
}


/* 响应式设计 - 通知弹窗 */
@media (max-width: 768px) {
  .notification-dialog {
    width: 95% !important;
    margin: 0 auto;
  }

  .notification-dialog .el-dialog__body {
    max-height: 70vh;
  }

  .detail-header,
  .detail-content,
  .detail-attachments {
    padding: 16px;
  }

  .detail-title {
    font-size: 16px;
  }
}

/* 动画定义 */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
