<template>
  <div class="app-container">
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 审核统计card - 横跨左右两列 -->
      <div class="audit-card-full">
        <el-card class="audit-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">
              <i class="el-icon-s-check"></i>
              审核统计
            </span>
          </div>
          <div class="audit-content">
            <!-- 活动审核和课程考核并排显示 -->
            <div class="audit-row">
              <!-- 活动审核 -->
              <div class="audit-section">
                <div class="audit-title">活动审核</div>
                <div class="audit-stats">
                  <div class="stat-item pending" @click="goToActivityAudit('pending')">
                    <div class="stat-icon">
                      <i class="el-icon-view"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-label">未审核</div>
                      <div class="stat-value">{{ activityAuditStats.pending }}</div>
                    </div>
                  </div>
                  <div class="stat-item approved" @click="goToActivityAudit('approved')">
                    <div class="stat-icon">
                      <i class="el-icon-check"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-label">已通过</div>
                      <div class="stat-value">{{ activityAuditStats.approved }}</div>
                    </div>
                  </div>
                  <div class="stat-item rejected" @click="goToActivityAudit('rejected')">
                    <div class="stat-icon">
                      <i class="el-icon-close"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-label">未通过</div>
                      <div class="stat-value">{{ activityAuditStats.rejected }}</div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 课程考核 -->
              <div class="audit-section">
                <div class="audit-title">课程考核</div>
                <div class="audit-stats">
                  <div class="stat-item pending" @click="goToCourseAudit('pending')">
                    <div class="stat-icon">
                      <i class="el-icon-view"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-label">未审核</div>
                      <div class="stat-value">{{ courseAuditStats.pending }}</div>
                    </div>
                  </div>
                  <div class="stat-item approved" @click="goToCourseAudit('approved')">
                    <div class="stat-icon">
                      <i class="el-icon-check"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-label">已通过</div>
                      <div class="stat-value">{{ courseAuditStats.approved }}</div>
                    </div>
                  </div>
                  <div class="stat-item rejected" @click="goToCourseAudit('rejected')">
                    <div class="stat-icon">
                      <i class="el-icon-close"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-label">未通过</div>
                      <div class="stat-value">{{ courseAuditStats.rejected }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 左右两列容器 -->
      <div class="columns-container">
        <!-- 左侧列 -->
        <div class="left-column">
          <!-- 左侧上方：书院通知 -->
          <div class="news-panel">
          <div class="panel-header">
            <i class="el-icon-bell"></i>
            <span>书院通知</span>
            <div class="card-actions">
              <el-button
                type="text"
                size="small"
                @click="goToNoticeManager"
                class="refresh-btn"
              >
                <i class="el-icon-view"></i>
                查看全部
              </el-button>
            </div>
          </div>
          <div class="news-content">
            <div v-if="notifications.length === 0" class="no-notification">
              <i class="el-icon-info"></i>
              <span>暂无通知</span>
            </div>
            <div v-else class="notification-list" ref="notificationList">
              <div
                v-for="notification in notifications.slice(0, 5)"
                :key="notification.notiId"
                class="notification-item"
                @click="showNotificationDetail(notification)"
              >
                <div class="notification-date">
                  {{ formatNotificationDate(notification.createdAt) }}
                </div>
                <div class="notification-info">
                  <div class="notification-title">{{ notification.notiTitle }}</div>
                  <div class="notification-badges">
                    <span v-if="isNewNotification(notification)" class="badge new">NEW</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 左侧下方：统计图表 -->
        <div class="charts-container">
          <!-- 统计图表 -->
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span class="card-title">
                活动与课程统计
              </span>
            </div>
            <div class="chart-content">
              <div class="chart-row">
                <div class="chart-item">
                  <div class="chart-subtitle">活动统计</div>
                  <div ref="statsChart" class="chart"></div>
                </div>
                <div class="chart-item">
                  <div class="chart-subtitle">课程统计</div>
                  <div ref="courseStatsChart" class="chart"></div>
                </div>
              </div>
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
            <div v-for="(activity, index) in recentActivities.slice(0, 5)" :key="index" class="activity-item" @click="goToActivityManager(activity)">
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
            <div v-for="(course, index) in currentCourses.slice(0, 5)" :key="index" class="course-item" @click="goToCourseManager(course)">
              <div class="course-time">
                <div class="time-day">{{ getCourseDay(course) }}</div>
                <div class="time-month">{{ getCourseMonth(course) }}</div>
              </div>
              <div class="course-content">
                <h4 class="course-title">{{ course.name }}</h4>
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
              </div>
              <div class="course-status" :class="getCourseStatusClass(course.status)">
                {{ course.status }}
              </div>
            </div>
          </div>
        </el-card>
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

    </el-dialog>

  </div>
</template>

<script>
import { getStudent, getNickName } from "@/api/system/student";
import { listActivities } from "@/api/system/activities";
import { listCourses } from "@/api/system/courses";
import { getAuditCount } from "@/api/system/bookings";
import { getAuditCount as getCourseAuditCount } from "@/api/system/courseBookings";
import { listNotificationsPublic } from "@/api/system/notifications";
import { parseTime } from "@/utils/ruoyi";
import * as echarts from 'echarts';

export default {
  name: "AcademicTeacherHome",
  data() {
    return {
      // 当前学生书院信息
      currentAcademy: '',

      // 通知数据
      notifications: [],
      notificationsLoading: false,
      notificationDialogVisible: false,
      currentNotification: null,



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
      },

      // 课程统计图表数据
      courseChartData: [],
      courseStatsChart: null,

      // 活动审核统计数据
      activityAuditStats: {
        pending: 0,
        approved: 0,
        rejected: 0
      },

      // 课程考核统计数据
      courseAuditStats: {
        pending: 0,
        approved: 0,
        rejected: 0
      },

      // 月份活动数据
      monthlyActivityData: []
    };
  },
  computed: {
    // 计算弹窗宽度
    dialogWidth() {
      // 根据屏幕宽度和侧边栏宽度计算弹窗宽度
      const screenWidth = window.innerWidth;
      const sidebarWidth = screenWidth > 1200 ? 200 : (screenWidth > 768 ? 64 : 0);
      const availableWidth = screenWidth - sidebarWidth - 40; // 减去40px的边距
      return Math.max(availableWidth, 800) + 'px'; // 最小宽度800px，比StudentHome更大
    },

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
      this.initCourseStatsChart();
    });
  },
  created() {
    // 初始化学生信息
    this.getCurrentStudentInfo();
    // 获取通知数据
    this.loadNotifications();
    // 获取近期活动数据
    this.getRecentActivities();
    // 获取本学期课程数据
    this.getCurrentCourses();
    // 获取活动统计数据
    this.getActivityStats();
    // 获取课程统计数据
    this.getCourseStats();
    // 获取活动审核统计数据
    this.getActivityAuditStats();
    // 获取课程考核统计数据
    this.getCourseAuditStats();
    // 获取月度活动数据
    this.getMonthlyActivityData();
  },
  methods: {
    // 加载通知数据
    async loadNotifications() {
      this.notificationsLoading = true;
      try {
        console.log('=== 开始加载通知数据 ===');
        console.log('当前用户信息:', this.$store.state.user);
        console.log('API基础URL:', process.env.VUE_APP_BASE_API);
        console.log('请求URL:', '/system/notifications/public/list');

        // 使用公开接口
        const response = await listNotificationsPublic({
          pageNum: 1,
          pageSize: 20  // 增加页面大小以显示更多数据
        });

        console.log('通知API响应:', response);
        console.log('响应类型:', typeof response);
        console.log('响应码:', response?.code);
        console.log('响应数据:', response?.rows);
        console.log('数据长度:', response?.rows?.length);
        console.log('总记录数:', response?.total);

        if (response && response.code === 200) {
          // 确保数据格式正确
          const notifications = response.rows || response.data || [];
          this.notifications = Array.isArray(notifications) ? notifications : [];

          // 确保按创建时间降序排序（最新的在前）
          console.log('排序前的通知时间:', this.notifications.map(n => ({
            id: n.notiId,
            title: n.notiTitle,
            createdAt: n.createdAt
          })));

          this.notifications.sort((a, b) => {
            const timeA = new Date(a.createdAt).getTime();
            const timeB = new Date(b.createdAt).getTime();
            return timeB - timeA; // 降序排序
          });

          console.log('排序后的通知时间:', this.notifications.map(n => ({
            id: n.notiId,
            title: n.notiTitle,
            createdAt: n.createdAt
          })));

          console.log('成功加载通知数据:', this.notifications);
          console.log('通知数量:', this.notifications.length);

          // 检查每个通知的字段
          this.notifications.forEach((notification, index) => {
            console.log(`通知${index + 1}:`, {
              id: notification.notiId,
              title: notification.notiTitle,
              type: notification.notiType,
              content: notification.notiContent?.substring(0, 50) + '...',
              creator: notification.creatorId,
              createdAt: notification.createdAt
            });
          });

          // 如果没有数据，显示提示
          if (this.notifications.length === 0) {
            console.log('没有找到通知数据');
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
      // 这里可以实现图片预览功能
      window.open(imagePath, '_blank');
    },

    // 格式化日期
    formatDate(date) {
      console.log('formatDate 输入:', date, '类型:', typeof date);
      if (!date) return '';

      // 如果已经是字符串格式，直接返回
      if (typeof date === 'string') {
        return date.substring(0, 10); // 只返回日期部分
      }

      return parseTime(date, '{y}-{m}-{d}');
    },

    // 格式化日期时间
    formatDateTime(date) {
      console.log('formatDateTime 输入:', date, '类型:', typeof date);
      if (!date) return '';

      // 如果已经是字符串格式，直接返回
      if (typeof date === 'string') {
        return date; // 直接返回完整的时间字符串
      }

      return parseTime(date, '{y}-{m}-{d} {h}:{i}:{s}');
    },

    // 获取通知类型文本
    getNotificationTypeText(type) {
      const typeMap = {
        'system': '系统',
        'activity': '活动',
        'course': '课程'
      };
      return typeMap[type] || '通知';
    },

    // 获取通知类型样式类
    getNotificationTypeClass(type) {
      const classMap = {
        'system': 'type-system',
        'activity': 'type-activity',
        'course': 'type-course'
      };
      return classMap[type] || 'type-default';
    },

    // 获取通知内容预览
    getNotificationPreview(content) {
      if (!content) return '暂无内容';
      // 移除HTML标签并截取前50个字符
      const plainText = content.replace(/<[^>]*>/g, '');
      return plainText.length > 50 ? plainText.substring(0, 50) + '...' : plainText;
    },



    // 提交编辑通知
    submitEdit() {
      this.$refs.editForm.validate(async (valid) => {
        if (valid) {
          this.editSubmitting = true;
          try {
            console.log('=== 开始编辑通知 ===');
            console.log('编辑的通知数据:', this.editForm);

            const response = await updateNotifications(this.editForm);

            console.log('通知编辑响应:', response);

            if (response.code === 200) {
              this.$message.success('通知编辑成功');
              this.editDialogVisible = false;
              // 重新加载通知列表
              this.loadNotifications();
            } else {
              console.error('通知编辑失败，响应码:', response.code, '错误信息:', response.msg);
              this.$message.error('通知编辑失败: ' + (response.msg || '未知错误'));
            }
          } catch (error) {
            console.error('=== 编辑通知失败 ===');
            console.error('错误对象:', error);

            if (error.response) {
              console.error('HTTP响应错误:');
              console.error('状态码:', error.response.status);
              console.error('响应数据:', error.response.data);
              this.$message.error(`编辑通知失败: HTTP ${error.response.status}`);
            } else if (error.request) {
              console.error('网络请求错误:', error.request);
              this.$message.error('编辑通知失败: 网络请求失败');
            } else {
              console.error('其他错误:', error.message);
              this.$message.error('编辑通知失败: ' + error.message);
            }
          } finally {
            this.editSubmitting = false;
          }
        } else {
          console.warn('表单验证失败');
          this.$message.warning('请检查表单填写是否完整');
        }
      });
    },

    // 删除通知
    deleteNotification(notification) {
      this.$confirm(`确定要删除通知"${notification.notiTitle}"吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          console.log('=== 开始删除通知 ===');
          console.log('删除的通知ID:', notification.notiId);

          const response = await delNotifications(notification.notiId);

          console.log('通知删除响应:', response);

          if (response.code === 200) {
            this.$message.success('通知删除成功');
            // 重新加载通知列表
            this.loadNotifications();
          } else {
            console.error('通知删除失败，响应码:', response.code, '错误信息:', response.msg);
            this.$message.error('通知删除失败: ' + (response.msg || '未知错误'));
          }
        } catch (error) {
          console.error('=== 删除通知失败 ===');
          console.error('错误对象:', error);

          if (error.response) {
            console.error('HTTP响应错误:');
            console.error('状态码:', error.response.status);
            console.error('响应数据:', error.response.data);
            this.$message.error(`删除通知失败: HTTP ${error.response.status}`);
          } else if (error.request) {
            console.error('网络请求错误:', error.request);
            this.$message.error('删除通知失败: 网络请求失败');
          } else {
            console.error('其他错误:', error.message);
            this.$message.error('删除通知失败: ' + error.message);
          }
        }
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },

    // 重置发布表单
    resetPublishForm() {
      this.publishForm = {
        notiTitle: '',
        notiType: '',
        notiContent: '',
        fileAttachments: null,
        pictureAttachments: null,
        creatorId: this.$store.state.user.name || '',
        createdAt: new Date()
      };
      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs.publishForm) {
          this.$refs.publishForm.clearValidate();
        }
      });
    },

    // 提交发布通知
    submitPublish() {
      this.$refs.publishForm.validate(async (valid) => {
        if (valid) {
          this.publishSubmitting = true;
          try {
            // 确保使用当前时间，格式化为后端期望的格式（使用本地时间）
            const now = new Date();
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');
            const seconds = String(now.getSeconds()).padStart(2, '0');

            const localTimeString = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;

            const publishData = {
              ...this.publishForm,
              createdAt: localTimeString
            };

            console.log('=== 开始发布通知 ===');
            console.log('当前时间对象:', now);
            console.log('UTC时间:', now.toISOString());
            console.log('本地时间字符串:', localTimeString);
            console.log('发布的通知数据:', publishData);
            console.log('发布时间:', publishData.createdAt);

            const response = await addNotifications(publishData);

            console.log('通知发布响应:', response);

            if (response.code === 200) {
              this.$message.success('通知发布成功');
              this.publishDialogVisible = false;
              // 重新加载通知列表
              this.loadNotifications();
            } else {
              console.error('通知发布失败，响应码:', response.code, '错误信息:', response.msg);
              this.$message.error('通知发布失败: ' + (response.msg || '未知错误'));
            }
          } catch (error) {
            console.error('=== 发布通知失败 ===');
            console.error('错误对象:', error);

            if (error.response) {
              console.error('HTTP响应错误:');
              console.error('状态码:', error.response.status);
              console.error('响应数据:', error.response.data);
              this.$message.error(`发布通知失败: HTTP ${error.response.status}`);
            } else if (error.request) {
              console.error('网络请求错误:', error.request);
              this.$message.error('发布通知失败: 网络请求失败');
            } else {
              console.error('其他错误:', error.message);
              this.$message.error('发布通知失败: ' + error.message);
            }
          } finally {
            this.publishSubmitting = false;
          }
        } else {
          console.warn('表单验证失败');
          this.$message.warning('请检查表单填写是否完整');
        }
      });
    },

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
      this.initStatsChart();
    },

    // 初始化统计柱形图
    initStatsChart() {
      const chart = echarts.init(this.$refs.statsChart);

      // 准备数据 - 按月份统计活动数量
      const data = [];
      const colors = ['#409EFF', '#67C23A', '#F56C6C', '#E6A23C', '#909399'];

      // 检查是否有活动数据
      if (this.monthlyActivityData && this.monthlyActivityData.length > 0) {
        // 按月份统计活动数量
        for (let month = 1; month <= 12; month++) {
          const monthData = this.monthlyActivityData.find(m => parseInt(m.month.replace('月', '')) === month);
          let activityCount = 0;
          let totalParticipants = 0;
          let activityNames = [];

          if (monthData && monthData.activities) {
            activityCount = monthData.activities.length;
            monthData.activities.forEach(activity => {
              totalParticipants += activity.participants || 0;
              activityNames.push(activity.name);
            });
          }

          data.push({
            value: activityCount,
            name: `${month}月`,
            month: month,
            activityCount: activityCount,
            totalParticipants: totalParticipants,
            activityNames: activityNames,
            itemStyle: {
              color: '#409EFF'
            }
          });
        }
      } else {
        // 如果没有数据，生成空数据
        for (let month = 1; month <= 12; month++) {
          data.push({
            value: 0,
            name: `${month}月`,
            month: month,
            activityCount: 0,
            totalParticipants: 0,
            activityNames: [],
            itemStyle: {
              color: '#409EFF'
            }
          });
        }
      }

      const option = {
        backgroundColor: '#ffffff',
        tooltip: {
          show: false
        },
        grid: {
          left: '15%',
          right: '5%',
          bottom: '10%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          name: '活动数量',
          nameLocation: 'middle',
          nameGap: 30,
          minInterval: 1,
          axisLabel: {
            fontSize: 12,
            color: '#666',
            formatter: function(value) {
              return Math.floor(value);
            }
          },
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          }
        },
        yAxis: {
          type: 'category',
          data: Array.from({length: 12}, (_, i) => (i + 1) + '月'),
          name: '月份',
          nameLocation: 'middle',
          nameGap: 30,
          axisLabel: {
            fontSize: 11,
            color: '#666',
            formatter: function(value) {
              return value;
            }
          },
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          }
        },
        series: [
          {
            name: '活动数量',
            type: 'bar',
            data: data,
            barWidth: '60%',
            label: {
              show: true,
              position: 'right',
              formatter: function(params) {
                return params.data.activityCount > 0 ? params.data.activityCount : '';
              },
              fontSize: 11,
              color: '#333',
              fontWeight: 'bold'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowOffsetY: 0,
                shadowColor: 'rgba(0, 0, 0, 0.3)'
              }
            }
          }
        ]
      };

      chart.setOption(option);

      // 添加点击事件
      chart.on('click', (params) => {
        if (params.data && params.data.month) {
          console.log('点击月份:', params.data);
          // 跳转到活动管理页面，按月份筛选
          this.$router.push({
            path: '/Activity/ActivityManager',
            query: {
              filterMode: 'month',
              month: params.data.month,
              year: new Date().getFullYear()
            }
          });
        }
      });
    },

    // 初始化课程统计图表
    initCourseStatsChart() {
      if (!this.$refs.courseStatsChart) return;

      this.courseStatsChart = echarts.init(this.$refs.courseStatsChart);

      // 准备数据 - 按月份统计课程数量
      const data = [];

      // 检查是否有课程数据
      if (this.courseChartData && this.courseChartData.length > 0) {
        // 使用处理好的课程数据
        this.courseChartData.forEach(item => {
          data.push({
            value: item.courseCount,
            name: `${item.month}月`,
            month: item.month,
            courseCount: item.courseCount,
            totalCapacity: item.totalCapacity,
            courseNames: item.courseNames,
            itemStyle: {
              color: '#67C23A' // 绿色表示课程
            }
          });
        });
      } else {
        // 如果没有数据，生成空数据
        for (let month = 1; month <= 12; month++) {
          data.push({
            value: 0,
            name: `${month}月`,
            month: month,
            courseCount: 0,
            totalCapacity: 0,
            courseNames: [],
            itemStyle: {
              color: '#67C23A'
            }
          });
        }
      }

      const option = {
        backgroundColor: '#ffffff',
        tooltip: {
          show: false
        },
        grid: {
          left: '15%',
          right: '5%',
          bottom: '10%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          name: '课程数量',
          nameLocation: 'middle',
          nameGap: 30,
          minInterval: 1,
          axisLabel: {
            fontSize: 12,
            color: '#666',
            formatter: function(value) {
              return Math.floor(value);
            }
          },
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          }
        },
        yAxis: {
          type: 'category',
          data: Array.from({length: 12}, (_, i) => (i + 1) + '月'),
          name: '月份',
          nameLocation: 'middle',
          nameGap: 30,
          axisLabel: {
            fontSize: 11,
            color: '#666',
            formatter: function(value) {
              return value;
            }
          },
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          }
        },
        series: [
          {
            name: '课程数量',
            type: 'bar',
            data: data,
            barWidth: '60%',
            label: {
              show: true,
              position: 'right',
              formatter: function(params) {
                return params.data.courseCount > 0 ? params.data.courseCount : '';
              },
              fontSize: 11,
              color: '#333',
              fontWeight: 'bold'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowOffsetY: 0,
                shadowColor: 'rgba(0, 0, 0, 0.3)'
              }
            }
          }
        ]
      };

      this.courseStatsChart.setOption(option);

      // 添加点击事件
      this.courseStatsChart.on('click', (params) => {
        if (params.data && params.data.month) {
          console.log('点击课程月份:', params.data);
          // 跳转到课程管理页面，按月份筛选
          this.$router.push({
            path: '/Course/CourseManager',
            query: {
              filterMode: 'month',
              month: params.data.month,
              year: new Date().getFullYear()
            }
          });
        }
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
          // 按活动开始时间降序排序，获取最近的5个活动
          const sortedActivities = response.rows
            .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
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
        // 如果点击了具体活动，传递活动信息进行筛选
        this.$router.push({
          path: '/Activity/ActivityManager',
          query: {
            activityId: activity.activityId,
            activityName: activity.title,
            activityType: activity.activityType,
            filterMode: 'single' // 标记为单个活动筛选模式
          }
        });
      } else {
        // 如果点击了"查看全部"按钮，显示近期活动（未结束的活动）
        this.$router.push({
          path: '/Activity/ActivityManager',
          query: {
            filterMode: 'recent' // 标记为近期活动筛选模式
          }
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
          // 按课程开始时间降序排序，获取最近的4个课程
          const sortedCourses = response.rows
            .sort((a, b) => new Date(b.courseStart) - new Date(a.courseStart))
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
              status: this.computeCourseStatus(course),
              // 保留原始时间字段用于日期显示
              courseStart: course.courseStart,
              courseDeadline: course.courseDeadline
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
            status: '选课进行中',
            courseStart: new Date().toISOString(),
            courseDeadline: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString()
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
        // 如果点击了具体课程，传递课程信息进行筛选
        this.$router.push({
          path: '/Course/CourseManager',
          query: {
            courseId: course.courseId,
            courseName: course.name,
            courseCategory: course.courseCategory,
            status: course.status,
            filterMode: 'single' // 标记为单个课程筛选模式
          }
        });
      } else {
        // 如果点击了"课程管理"按钮，显示本学期课程（未结束的课程）
        this.$router.push({
          path: '/Course/CourseManager',
          query: {
            filterMode: 'current' // 标记为本学期课程筛选模式
          }
        });
      }
    },

    // 跳转到通知管理页面
    goToNoticeManager() {
      this.$router.push('/Notice/index');
    },

    // 跳转到活动审核页面
    goToActivityAudit(status) {
      console.log('跳转到活动审核页面，状态:', status);
      this.$router.push({
        path: '/Activity/ActivityAudit',
        query: {
          status: status
        }
      });
    },

    // 跳转到课程审核页面
    goToCourseAudit(status) {
      console.log('跳转到课程审核页面，状态:', status);
      this.$router.push({
        path: '/Course/CourseAudit',
        query: {
          status: status
        }
      });
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
        // 先获取组织者名称，与活动审核界面保持一致
        const nickName = await getNickName();
        const organizer = nickName.msg;

        const response = await getAuditCount({ organizer });
        if (response.code === 200 && response.data) {
          this.activityStats = response.data;
          // 更新图表数据
          this.updateStatsChart();
        }
      } catch (error) {
        console.error('获取活动统计数据失败:', error);
        // 如果获取失败，使用默认数据
        this.activityStats = {
          pending: 0,
          approved: 180,
          rejected: 0
        };
        this.updateStatsChart();
      }
    },

    // 获取课程统计数据
    async getCourseStats() {
      try {
        // 先获取组织者名称
        const nickName = await getNickName();
        const organizer = nickName.msg;

        // 获取课程数据，与课程管理界面保持一致
        const response = await listCourses({
          organizer: organizer,
          pageSize: 10000 // 获取所有数据
        });

        if (response.code === 200) {
          const courses = response.rows || [];
          this.processCourseStats(courses);
        }
      } catch (error) {
        console.error('获取课程统计数据失败:', error);
        this.$message.error('获取课程统计数据失败');
      }
    },

    // 处理课程统计数据
    processCourseStats(courses) {
      // 按月份分组统计课程数量
      const monthlyData = {};

      // 初始化12个月的数据
      for (let i = 1; i <= 12; i++) {
        monthlyData[i] = {
          month: i,
          courseCount: 0,
          totalCapacity: 0,
          courseNames: []
        };
      }

      // 处理课程数据
      courses.forEach(course => {
        if (course.startTime) {
          const startTime = new Date(course.startTime);
          const month = startTime.getMonth() + 1; // getMonth() 返回 0-11，需要 +1

          if (monthlyData[month]) {
            monthlyData[month].courseCount++;
            monthlyData[month].totalCapacity += (course.courseCapacity || 0);
            monthlyData[month].courseNames.push(course.courseName || '未命名课程');
          }
        }
      });

      // 转换为数组格式
      this.courseChartData = Object.values(monthlyData);

      // 初始化课程统计图表
      this.$nextTick(() => {
        this.initCourseStatsChart();
      });
    },

    // 更新统计图表
    updateStatsChart() {
      if (this.$refs.statsChart) {
        const chart = echarts.getInstanceByDom(this.$refs.statsChart);
        if (chart) {
          // 重新初始化图表以更新数据
          this.initStatsChart();
        }
      }
    },


    // 获取活动审核统计数据
    async getActivityAuditStats() {
      try {
        // 先获取组织者名称
        const nickName = await getNickName();
        const organizer = nickName.msg;

        const response = await getAuditCount({ organizer });
        if (response.code === 200 && response.data) {
          this.activityAuditStats = response.data;
        }
      } catch (error) {
        console.error('获取活动审核统计数据失败:', error);
        // 如果获取失败，使用默认数据
        this.activityAuditStats = {
          pending: 1,
          approved: 181,
          rejected: 0
        };
      }
    },

    // 获取课程考核统计数据
    async getCourseAuditStats() {
      try {
        const response = await getCourseAuditCount();
        if (response.code === 200 && response.data) {
          this.courseAuditStats = response.data;
        }
      } catch (error) {
        console.error('获取课程考核统计数据失败:', error);
        // 如果获取失败，使用默认数据
        this.courseAuditStats = {
          pending: 0,
          approved: 0,
          rejected: 0
        };
      }
    },

    // 获取月度活动数据
    async getMonthlyActivityData() {
      try {
        // 先获取组织者名称，与活动管理保持一致
        const nickName = await getNickName();
        const organizer = nickName.msg;

        // 获取所有活动数据，与活动管理使用相同的查询方式
        const response = await listActivities({
          pageNum: 1,
          pageSize: 10000, // 获取所有数据
          organizer: organizer
        });

        if (response.code === 200 && response.rows) {
          // 按月份分组活动数据
          const monthlyGroups = {};

          response.rows.forEach(activity => {
            const startTime = new Date(activity.startTime);
            const month = (startTime.getMonth() + 1) + '月';
            const day = startTime.getDate() + '日';

            if (!monthlyGroups[month]) {
              monthlyGroups[month] = [];
            }

            // 获取报名人数，使用实际数据
            const participants = activity.registrationCount || activity.participantCount || 0;

            monthlyGroups[month].push({
              name: activity.activityName,
              date: day,
              participants: participants,
              activityId: activity.activityId,
              location: activity.activityLocation,
              organizer: activity.organizer,
              startTime: activity.startTime,
              endTime: activity.endTime,
              activityType: activity.activityType,
              activityDescription: activity.activityDescription
            });
          });

          // 转换为数组格式并按月份排序
          this.monthlyActivityData = Object.keys(monthlyGroups)
            .sort((a, b) => {
              const monthA = parseInt(a.replace('月', ''));
              const monthB = parseInt(b.replace('月', ''));
              return monthA - monthB;
            })
            .map(month => ({
              month: month,
              activities: monthlyGroups[month].sort((a, b) => {
                const dayA = parseInt(a.date.replace('日', ''));
                const dayB = parseInt(b.date.replace('日', ''));
                return dayA - dayB;
              })
            }));

          // 更新图表
          this.updateStatsChart();
        }
      } catch (error) {
        console.error('获取月度活动数据失败:', error);
        // 如果获取失败，使用空数据
        this.monthlyActivityData = [];
        this.updateStatsChart();
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
    },

    // 获取课程日期（用于显示在时间区域）
    getCourseDay(course) {
      if (course.courseStart) {
        const startDate = new Date(course.courseStart);
        return startDate.getDate().toString();
      }
      return '01';
    },

    // 获取课程月份（用于显示在时间区域）
    getCourseMonth(course) {
      if (course.courseStart) {
        const startDate = new Date(course.courseStart);
        return (startDate.getMonth() + 1) + '月';
      }
      return '1月';
    },

    // 获取通知日期（用于显示在时间区域）
    getNotificationDay(dateTime) {
      if (dateTime) {
        const date = new Date(dateTime);
        return date.getDate().toString();
      }
      return '01';
    },

    // 获取通知月份（用于显示在时间区域）
    getNotificationMonth(dateTime) {
      if (dateTime) {
        const date = new Date(dateTime);
        return (date.getMonth() + 1) + '月';
      }
      return '1月';
    },

    // 格式化时间（只显示时分）
    formatTime(dateTime) {
      if (!dateTime) return '';

      const date = new Date(dateTime);
      return date.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      });
    },


    // 格式化通知日期（用于显示在日期标签中）
    formatNotificationDate(date) {
      if (!date) return '';
      const d = new Date(date);
      const day = d.getDate();
      const month = d.getMonth() + 1;
      return `${day} ${month}月`;
    },

    // 判断是否为新通知（3天内，精确到天数）
    isNewNotification(notification) {
      if (!notification.createdAt) return false;

      const now = new Date();
      const notificationDate = new Date(notification.createdAt);

      // 将时间设置为当天的开始时间（00:00:00）进行比较
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      const notificationDay = new Date(notificationDate.getFullYear(), notificationDate.getMonth(), notificationDate.getDate());

      // 计算天数差
      const diffTime = today - notificationDay;
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

      // 3天内（包括今天）
      return diffDays >= 0 && diffDays <= 3;
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
  overflow-x: hidden;
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
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: calc(100vh - 120px);
  max-height: none;
}

/* 审核统计card全宽容器 */
.audit-card-full {
  width: 100%;
  margin-bottom: 15px;
}

/* 左右两列容器 */
.columns-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  flex: 1;
  min-height: 0;
}

/* 左侧列 */
.left-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 0;
  overflow: hidden;
  height: 100%;
}

/* 右侧列 */
.right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 0;
  overflow: hidden;
  height: 100%;
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
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  color: white;
  font-weight: 600;
  font-size: 16px;
  padding: 16px 20px;
  margin: -20px -20px 0 -20px;
  border-radius: 12px 12px 0 0;
}

/* 卡片头部按钮样式 */
.card-header .el-button {
  color: white !important;
  font-size: 12px;
  padding: 4px 8px;
  border: none;
  background: transparent;
  transition: all 0.3s ease;
}

.card-header .el-button:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-radius: 4px;
  transform: translateY(-1px);
}

.card-header .el-button:active {
  transform: translateY(0);
}

.card-header .el-button i {
  margin-right: 4px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title i {
  color: white;
  font-size: 18px;
}

/* 卡片操作按钮组 */
.card-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-actions .el-button {
  color: white;
  font-size: 12px;
  padding: 4px 8px;
  border: none;
  background: transparent;
  transition: all 0.3s ease;
}

.card-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  transform: translateY(-1px);
}

.card-actions .el-button:active {
  transform: translateY(0);
}

.card-actions .el-button i {
  margin-right: 4px;
}

/* 书院通知面板 */
.news-panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  height: 400px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 面板头部 */
.panel-header {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.panel-header i {
  margin-right: 8px;
  font-size: 18px;
}

.panel-header .card-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.refresh-btn,
.add-notification-btn {
  color: white !important;
  font-size: 12px;
  padding: 4px 8px;
}

.refresh-btn:hover,
.add-notification-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

/* 面板内容 */
.news-content {
  padding: 12px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}


.notification-content {
  padding: 16px 20px 20px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止内容溢出 */
  min-height: 0; /* 允许flex子元素收缩 */
}

/* 无数据状态 */
.no-notification {
  text-align: center;
  color: #909399;
  padding: 60px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.no-notification i {
  font-size: 48px;
  margin-bottom: 12px;
  display: block;
  color: #c0c4cc;
}

.no-notification span {
  font-size: 14px;
  color: #909399;
}

/* 通知列表 */
.notification-list {
  max-height: 300px;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
  /* 隐藏滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.notification-list::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
}



.notification-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: all 0.2s;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background: #f5f7fa;
  transform: translateX(4px);
}

.notification-date {
  min-width: 60px;
  background: #409EFF;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.notification-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.notification-title {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
  line-height: 1.4;
  margin: 0;
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

.notification-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  opacity: 1;
  transition: opacity 0.3s ease;
  flex-shrink: 0;
  margin-left: auto;
}

.action-btn {
  padding: 10px 14px !important;
  font-size: 18px !important;
  border: none !important;
  background: transparent !important;
  transition: all 0.3s ease !important;
  border-radius: 8px !important;
  min-width: 44px !important;
  height: 44px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1) !important;
}

.edit-btn {
  color: #409EFF !important;
  background: rgba(64, 158, 255, 0.1) !important;
}

.edit-btn:hover {
  background: rgba(64, 158, 255, 0.2) !important;
  color: #409EFF !important;
  transform: scale(1.1) !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4) !important;
}

.delete-btn {
  color: #F56C6C !important;
  background: rgba(245, 108, 108, 0.1) !important;
}

.delete-btn:hover {
  background: rgba(245, 108, 108, 0.2) !important;
  color: #F56C6C !important;
  transform: scale(1.1) !important;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4) !important;
}

.action-btn:active {
  transform: scale(0.95) !important;
}

.notification-content {
  flex: 1;
}

/* 通知头部 */
.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
  gap: 12px;
}

.notification-title {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}


.type-default {
  background: #f5f5f5;
  color: #757575;
}

/* 通知元信息 */
.notification-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.notification-content-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.notification-time {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}


.notification-time-text {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.notification-preview {
  font-size: 12px;
  color: #606266;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  opacity: 1;
  transition: opacity 0.3s ease;
  flex-shrink: 0;
}

.action-btn {
  padding: 8px 12px !important;
  font-size: 16px !important;
  border: none !important;
  background: transparent !important;
  transition: all 0.3s ease !important;
  border-radius: 6px !important;
  min-width: 36px !important;
  height: 36px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.edit-btn {
  color: #409EFF !important;
}

.edit-btn:hover {
  background: rgba(64, 158, 255, 0.1) !important;
  color: #409EFF !important;
  transform: scale(1.1) !important;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3) !important;
}

.delete-btn {
  color: #F56C6C !important;
}

.delete-btn:hover {
  background: rgba(245, 108, 108, 0.1) !important;
  color: #F56C6C !important;
  transform: scale(1.1) !important;
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3) !important;
}

.action-btn:active {
  transform: scale(0.95) !important;
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
  background: linear-gradient(135deg, #42A5F5 0%, #64B5F6 100%) !important;
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
}

.detail-header {
  border-bottom: 1px solid #e5e7eb;
  padding: 20px 24px;
  margin-bottom: 0;
  background: #ffffff;
}

.detail-title {
  margin: 0 0 12px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  line-height: 1.4;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 14px;
  color: #6b7280;
  font-weight: 400;
}


.detail-time {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-time::before {
  content: "🕒";
  font-size: 12px;
}

.detail-content {
  margin-bottom: 24px;
  padding: 24px;
  flex: 1;
  overflow-y: auto;
  background: #ffffff;
}

.content-text {
  font-size: 15px;
  line-height: 1.7;
  color: #374151;
  white-space: pre-wrap;
  word-break: break-word;
  font-weight: 400;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.detail-attachments {
  border-top: 1px solid #e5e7eb;
  padding: 24px;
  background: #ffffff;
  margin-top: auto;
}

.detail-attachments h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.detail-attachments h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.attachment-section {
  margin-bottom: 20px;
}

.attachment-section:last-child {
  margin-bottom: 0;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
  margin-bottom: 8px;
}

.attachment-item:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.attachment-item i {
  color: #6b7280;
  font-size: 16px;
}

.file-name {
  flex: 1;
  font-size: 14px;
  color: #374151;
  word-break: break-all;
  font-weight: 400;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}

.image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;
}

.image-item:hover {
  transform: scale(1.05);
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.image-item:hover .image-overlay {
  opacity: 1;
}

.image-overlay i {
  color: white;
  font-size: 24px;
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
  display: flex;
  flex: 1;
}

.chart-card {
  height: 400px;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.chart-content {
  height: 350px;
  flex: 1;
}


.chart-subtitle {
  font-size: 14px;
  font-weight: bold;
  color: #666;
  text-align: center;
  margin-bottom: 8px;
  padding: 6px;
  background: #f0f2f5;
  border-radius: 4px;
}

.chart {
  width: 100%;
  height: 100%;
}

.chart-row {
  display: flex;
  gap: 20px;
  width: 100%;
  height: 100%;
}

.chart-item {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chart-item .chart {
  flex: 1;
  height: 100%;
}

/* 审核统计卡片 */
.audit-card {
  width: 100%;
  margin-bottom: 0;
  padding: 15px 20px;
}

.audit-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.audit-row {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.audit-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.audit-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  padding-bottom: 6px;
  border-bottom: 1px solid #f0f0f0;
}

.audit-stats {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.stat-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-item:hover::after {
  content: '点击查看详情';
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 1000;
}

.stat-item.pending {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border: 1px solid #90caf9;
}

.stat-item.approved {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  border: 1px solid #a5d6a7;
}

.stat-item.rejected {
  background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
  border: 1px solid #ef9a9a;
}

.stat-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.stat-item.pending .stat-icon {
  background: #409EFF;
  color: white;
}

.stat-item.approved .stat-icon {
  background: #67C23A;
  color: white;
}

.stat-item.rejected .stat-icon {
  background: #F56C6C;
  color: white;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 11px;
  color: #909399;
  font-weight: 500;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

/* 近期活动卡片 */
.activity-card {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.activity-list {
  max-height: 320px;
  overflow-y: auto;
  flex: 1;
  /* 隐藏滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.activity-list::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
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
  height: 400px;
  display: flex;
  flex-direction: column;
}

.course-list {
  max-height: 320px;
  overflow-y: auto;
  flex: 1;
  /* 隐藏滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.course-list::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
}

.course-item {
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

.course-item:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.course-item:last-child {
  border-bottom: none;
}

.course-time {
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

.course-content {
  flex: 1;
}

.course-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.course-teacher,
.course-schedule,
.course-location {
  font-size: 12px;
  color: #606266;
  margin: 0 0 4px 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
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
