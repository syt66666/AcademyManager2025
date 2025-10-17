<template>
  <div class="app-container">
    <!-- 主要内容区域 -->
    <div class="main-content">
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
                @click="loadNotifications"
                :loading="notificationsLoading"
                class="refresh-btn"
              >
                <i class="el-icon-refresh"></i>
                刷新
              </el-button>
              <el-button
                type="text"
                size="small"
                @click="showPublishDialog"
                v-if="isAdmin"
                class="add-notification-btn"
              >
                <i class="el-icon-plus"></i>
                发布通知
              </el-button>
            </div>
          </div>
          <div class="news-content">
            <!-- 通知类型标签页 -->
            <div class="notification-tabs">
              <div 
                v-for="tab in notificationTabs" 
                :key="tab.value"
                class="notification-tab"
                :class="{ active: selectedNotificationType === tab.value }"
                @click="filterNotificationsByType(tab.value)"
              >
                {{ tab.label }}
              </div>
            </div>
            
            <div v-if="filteredNotifications.length === 0" class="no-notification">
              <i class="el-icon-info"></i>
              <span>暂无通知</span>
            </div>
            <div v-else class="notification-list" ref="notificationList">
              <div
                v-for="notification in filteredNotifications"
                :key="notification.notiId"
                class="notification-item"
                @click="showNotificationDetail(notification)"
              >
                <div class="notification-date">
                  {{ formatNotificationDate(notification.createdAt) }}
                </div>
                <div class="notification-info">
                  <div class="notification-title">{{ notification.notiTitle }}</div>
                </div>
                <div v-if="isAdmin" class="notification-actions">
                  <el-button
                    type="text"
                    size="medium"
                    icon="el-icon-edit"
                    @click.stop="editNotification(notification)"
                    title="编辑通知"
                    class="action-btn edit-btn"
                  ></el-button>
                  <el-button
                    type="text"
                    size="medium"
                    icon="el-icon-delete"
                    @click.stop="deleteNotification(notification)"
                    title="删除通知"
                    class="action-btn delete-btn"
                  ></el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 左侧下方：统计图表 -->
        <div class="charts-container">
          <!-- 活动统计环形图 -->
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span class="card-title">
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

    <!-- 通知详情弹窗 -->
    <el-dialog
      title="通知详情"
      :visible.sync="notificationDialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentNotification" class="notification-detail">
        <div class="detail-header">
          <h3 class="detail-title">{{ currentNotification.notiTitle }}</h3>
          <div class="detail-meta">
            <span class="detail-type">
              <el-tag size="small" type="info">{{ currentNotification.notiType || '系统通知' }}</el-tag>
            </span>
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
        <el-button @click="notificationDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 发布通知弹窗 -->
    <el-dialog
      title="发布通知"
      :visible.sync="publishDialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="publishForm"
        :rules="publishRules"
        ref="publishForm"
        label-width="80px"
      >
        <el-form-item label="通知标题" prop="notiTitle">
          <el-input
            v-model="publishForm.notiTitle"
            placeholder="请输入通知标题"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item label="通知类型" prop="notiType">
          <el-select v-model="publishForm.notiType" placeholder="请选择通知类型" style="width: 100%">
            <el-option label="系统通知" value="system"></el-option>
            <el-option label="活动公告" value="activity"></el-option>
            <el-option label="课程通知" value="course"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="通知内容" prop="notiContent">
          <el-input
            type="textarea"
            v-model="publishForm.notiContent"
            placeholder="请输入通知内容"
            :rows="6"
            maxlength="1000"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPublish" :loading="publishSubmitting">发布</el-button>
      </div>
    </el-dialog>

    <!-- 编辑通知弹窗 -->
    <el-dialog
      title="编辑通知"
      :visible.sync="editDialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="editForm"
        :rules="editRules"
        ref="editForm"
        label-width="80px"
      >
        <el-form-item label="通知标题" prop="notiTitle">
          <el-input
            v-model="editForm.notiTitle"
            placeholder="请输入通知标题"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item label="通知类型" prop="notiType">
          <el-select v-model="editForm.notiType" placeholder="请选择通知类型" style="width: 100%">
            <el-option label="系统通知" value="system"></el-option>
            <el-option label="活动公告" value="activity"></el-option>
            <el-option label="课程通知" value="course"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="通知内容" prop="notiContent">
          <el-input
            type="textarea"
            v-model="editForm.notiContent"
            placeholder="请输入通知内容"
            :rows="6"
            maxlength="1000"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="editSubmitting">保存</el-button>
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
import { listNotificationsPublic, addNotifications, updateNotifications, delNotifications, getNotifications } from "@/api/system/notifications";
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

      // 通知类型筛选
      selectedNotificationType: 'all',
      notificationTabs: [
        { value: 'all', label: '全部' },
        { value: 'system', label: '系统通知' },
        { value: 'activity', label: '活动通知' },
        { value: 'course', label: '课程通知' }
      ],

      // 发布通知数据
      publishDialogVisible: false,
      publishSubmitting: false,
      publishForm: {
        notiTitle: '',
        notiType: '',
        notiContent: '',
        fileAttachments: null,
        pictureAttachments: null,
        creatorId: '',
        createdAt: null
      },
      publishRules: {
        notiTitle: [
          { required: true, message: '请输入通知标题', trigger: 'blur' },
          { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        notiType: [
          { required: true, message: '请选择通知类型', trigger: 'change' }
        ],
        notiContent: [
          { required: true, message: '请输入通知内容', trigger: 'blur' },
          { min: 10, max: 1000, message: '内容长度在 10 到 1000 个字符', trigger: 'blur' }
        ]
      },

      // 编辑通知数据
      editDialogVisible: false,
      editSubmitting: false,
      editForm: {
        notiId: null,
        notiTitle: '',
        notiType: '',
        notiContent: '',
        fileAttachments: null,
        pictureAttachments: null,
        creatorId: '',
        createdAt: null
      },
      editRules: {
        notiTitle: [
          { required: true, message: '请输入通知标题', trigger: 'blur' },
          { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        notiType: [
          { required: true, message: '请选择通知类型', trigger: 'change' }
        ],
        notiContent: [
          { required: true, message: '请输入通知内容', trigger: 'blur' },
          { min: 10, max: 1000, message: '内容长度在 10 到 1000 个字符', trigger: 'blur' }
        ]
      },

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

    // 筛选后的通知列表
    filteredNotifications() {
      if (this.selectedNotificationType === 'all') {
        return this.notifications;
      }
      return this.notifications.filter(notification => {
        const notiType = notification.notiType || 'system';
        return notiType === this.selectedNotificationType;
      });
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

    // 显示发布通知弹窗
    showPublishDialog() {
      this.publishDialogVisible = true;
      this.resetPublishForm();
    },

    // 编辑通知
    editNotification(notification) {
      this.editDialogVisible = true;
      this.editForm = {
        notiId: notification.notiId,
        notiTitle: notification.notiTitle,
        notiType: notification.notiType,
        notiContent: notification.notiContent,
        fileAttachments: notification.fileAttachments,
        pictureAttachments: notification.pictureAttachments,
        creatorId: notification.creatorId,
        createdAt: notification.createdAt
      };
      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs.editForm) {
          this.$refs.editForm.clearValidate();
        }
      });
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
      this.initActivityChart();
      this.initCourseChart();
    },

    // 初始化活动统计图表
    initActivityChart() {
      const chart = echarts.init(this.$refs.activityChart);
      const total = this.activityStats.pending + this.activityStats.approved + this.activityStats.rejected;
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          show: false // 完全隐藏图例
        },
        series: [
          {
            name: '活动统计',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: true,
              position: 'center',
              formatter: function() {
                return `活动总数\n${total}`;
              },
              fontSize: 14,
              fontWeight: 'bold',
              color: '#303133'
            },
            emphasis: {
              label: {
                show: false
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

      // 添加悬浮事件来动态更新中心标签
      chart.on('mouseover', (params) => {
        // 根据状态名称获取对应的颜色
        let statusColor = '#303133';
        switch(params.name) {
          case '未审核':
            statusColor = '#409EFF';
            break;
          case '已通过':
            statusColor = '#67C23A';
            break;
          case '未通过':
            statusColor = '#F56C6C';
            break;
        }

        const option = {
          series: [{
            label: {
              show: false,
              position: 'center',
              formatter: function() {
                return `${params.name}\n${params.value}`;
              },
              fontSize: 14,
              fontWeight: 'bold',
              color: statusColor,
              textStyle: {
                color: statusColor,
                fontSize: 14,
                fontWeight: 'bold'
              },
              emphasis: {
                color: statusColor,
                textStyle: {
                  color: statusColor
                }
              }
            }
          }]
        };
        chart.setOption(option);
      });

      // 添加鼠标离开事件恢复原始标签
      chart.on('mouseout', () => {
        const option = {
          series: [{
            label: {
              show: true,
              position: 'center',
              formatter: function() {
                return `活动总数\n${total}`;
              },
              fontSize: 14,
              fontWeight: 'bold',
              color: '#303133'
            }
          }]
        };
        chart.setOption(option);
      });
    },

    // 初始化课程统计图表
    initCourseChart() {
      const chart = echarts.init(this.$refs.courseChart);
      const total = this.courseStats.pending + this.courseStats.approved + this.courseStats.rejected;
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          show: false // 完全隐藏图例
        },
        series: [
          {
            name: '课程统计',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: true,
              position: 'center',
              formatter: function() {
                return `课程总数\n${total}`;
              },
              fontSize: 14,
              fontWeight: 'bold',
              color: '#303133'
            },
            emphasis: {
              label: {
                show: false
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

      // 添加悬浮事件来动态更新中心标签
      chart.on('mouseover', (params) => {
        // 根据状态名称获取对应的颜色
        let statusColor = '#303133';
        switch(params.name) {
          case '未考核':
            statusColor = '#409EFF';
            break;
          case '已通过':
            statusColor = '#67C23A';
            break;
          case '未通过':
            statusColor = '#F56C6C';
            break;
        }

        const option = {
          series: [{
            label: {
              show: false,
              position: 'center',
              formatter: function() {
                return `${params.name}\n${params.value}`;
              },
              fontSize: 14,
              fontWeight: 'bold',
              color: statusColor,
              textStyle: {
                color: statusColor,
                fontSize: 14,
                fontWeight: 'bold'
              },
              emphasis: {
                color: statusColor,
                textStyle: {
                  color: statusColor
                }
              }
            }
          }]
        };
        chart.setOption(option);
      });

      // 添加鼠标离开事件恢复原始标签
      chart.on('mouseout', () => {
        const option = {
          series: [{
            label: {
              show: true,
              position: 'center',
              formatter: function() {
                return `课程总数\n${total}`;
              },
              fontSize: 14,
              fontWeight: 'bold',
              color: '#303133'
            }
          }]
        };
        chart.setOption(option);
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
          this.updateActivityChart();
        }
      } catch (error) {
        console.error('获取活动统计数据失败:', error);
        // 如果获取失败，使用默认数据
        this.activityStats = {
          pending: 0,
          approved: 180,
          rejected: 0
        };
        this.updateActivityChart();
      }
    },

    // 更新活动统计图表
    updateActivityChart() {
      if (this.$refs.activityChart) {
        const chart = echarts.getInstanceByDom(this.$refs.activityChart);
        if (chart) {
          const total = this.activityStats.pending + this.activityStats.approved + this.activityStats.rejected;
          const option = {
            series: [{
              data: [
                { value: this.activityStats.pending, name: '未审核', itemStyle: { color: '#409EFF' } },
                { value: this.activityStats.approved, name: '已通过', itemStyle: { color: '#67C23A' } },
                { value: this.activityStats.rejected, name: '未通过', itemStyle: { color: '#F56C6C' } }
              ],
              label: {
                show: true,
                position: 'center',
                formatter: function() {
                  return `活动总数\n${total}`;
                },
                fontSize: 14,
                fontWeight: 'bold',
                color: '#303133'
              },
              emphasis: {
                label: {
                  show: true,
                  position: 'center',
                  formatter: function(params) {
                    return `${params.name}\n${params.value}`;
                  },
                  fontSize: 14,
                  fontWeight: 'bold',
                  color: '#303133'
                }
              }
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
          const total = this.courseStats.pending + this.courseStats.approved + this.courseStats.rejected;
          const option = {
            series: [{
              data: [
                { value: this.courseStats.pending, name: '未考核', itemStyle: { color: '#409EFF' } },
                { value: this.courseStats.approved, name: '已通过', itemStyle: { color: '#67C23A' } },
                { value: this.courseStats.rejected, name: '未通过', itemStyle: { color: '#F56C6C' } }
              ],
              label: {
                show: true,
                position: 'center',
                formatter: function() {
                  return `课程总数\n${total}`;
                },
                fontSize: 14,
                fontWeight: 'bold',
                color: '#303133'
              },
              emphasis: {
                label: {
                  show: true,
                  position: 'center',
                  formatter: function(params) {
                    return `${params.name}\n${params.value}`;
                  },
                  fontSize: 14,
                  fontWeight: 'bold',
                  color: '#303133'
                }
              }
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

    // 根据类型筛选通知
    filterNotificationsByType(type) {
      this.selectedNotificationType = type;
    },

    // 格式化通知日期（用于显示在日期标签中）
    formatNotificationDate(date) {
      if (!date) return '';
      const d = new Date(date);
      const day = d.getDate();
      const month = d.getMonth() + 1;
      return `${day} ${month}月`;
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
  height: calc(100vh - 120px);
  min-height: 600px;
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
  flex: 1;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
  height: 400px;
  display: flex;
  flex-direction: column;
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

/* 通知类型标签页 */
.notification-tabs {
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 12px;
}

.notification-tab {
  flex: 1;
  padding: 6px 12px;
  text-align: center;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;
}

.notification-tab:hover {
  color: #409EFF;
  background: #f0f9ff;
}

.notification-tab.active {
  color: #409EFF;
  background: #f0f9ff;
  border-bottom-color: #409EFF;
  font-weight: 600;
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

.notification-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
  flex-shrink: 0;
  margin-left: auto;
}

.notification-item:hover .notification-actions {
  opacity: 1;
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

/* 通知类型标签 */
.notification-type-tag {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
}

.type-system {
  background: #e3f2fd;
  color: #1976d2;
}

.type-activity {
  background: #f3e5f5;
  color: #7b1fa2;
}

.type-course {
  background: #e8f5e8;
  color: #388e3c;
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
  opacity: 0;
  transition: opacity 0.3s ease;
  flex-shrink: 0;
}

.notification-item:hover .notification-actions {
  opacity: 1;
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
.notification-detail {
  padding: 0;
}

.detail-header {
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 16px;
  margin-bottom: 20px;
}

.detail-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #909399;
}

.detail-type {
  display: flex;
  align-items: center;
}

.detail-time {
  display: flex;
  align-items: center;
  gap: 4px;
}


.detail-content {
  margin-bottom: 24px;
}

.content-text {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-attachments {
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.detail-attachments h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.detail-attachments h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
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
  gap: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  transition: all 0.2s;
}

.attachment-item:hover {
  background: #e9ecef;
  border-color: #dee2e6;
}

.attachment-item i {
  color: #409EFF;
  font-size: 16px;
}

.file-name {
  flex: 1;
  font-size: 14px;
  color: #606266;
  word-break: break-all;
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
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  flex: 1;
}

.chart-card {
  height: 500px;
}

.chart-content {
  height: 450px;
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
  flex: 1;
}

.course-list {
  max-height: 400px;
  overflow-y: auto;
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
