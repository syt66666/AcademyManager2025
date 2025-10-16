<template>
  <div class="student-home-container" v-loading="loading">
    <!-- 顶部通知栏和近期活动 -->
    <div class="top-section">
      <!-- 左上角通知栏 -->
      <div class="notification-panel">
        <div class="panel-header">
          <i class="el-icon-bell"></i>
          <span>书院通知</span>
        </div>
        <div class="notification-content">
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
          
          <!-- 通知列表 -->
          <div v-if="filteredNotifications.length === 0" class="no-notification">
            <i class="el-icon-info"></i>
            <span>暂无通知</span>
          </div>
          <div v-else class="notification-list">
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
                <div class="notification-badges">
                  <span v-if="isNewNotification(notification)" class="badge new">NEW</span>
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
          <i class="el-icon-calendar"></i>
          <span>近期活动</span>
        </div>
        <div class="activities-content">
          <div v-if="recentActivities.length === 0" class="no-activity">
            <i class="el-icon-info"></i>
            <span>暂无活动</span>
          </div>
          <div v-else class="activity-list">
            <div 
              v-for="activity in recentActivities" 
              :key="activity.activityId"
              class="activity-item"
              @click="goToActivityBooking(activity)"
            >
              <div class="activity-name">{{ activity.activityName }}</div>
              <div class="activity-time">{{ formatDateTime(activity.startTime) }}</div>
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
          <i class="el-icon-pie-chart"></i>
          <span>活动完成进度</span>
        </div>
        <div class="completion-content">
          <div class="progress-categories">
            <div 
              v-for="category in activityCategories" 
              :key="category.type"
              class="progress-category"
            >
              <div class="category-label">{{ category.name }}</div>
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

      <!-- 右边活动状态筛选 -->
      <div class="activity-status-panel">
        <div class="panel-header">
          <i class="el-icon-filter"></i>
          <span>活动状态筛选</span>
        </div>
        <div class="status-content">
          <div class="status-filters">
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

    <!-- 底部已选课程 -->
    <div class="bottom-section">
      <div class="selected-courses-panel">
        <div class="panel-header">
          <i class="el-icon-reading"></i>
          <span>我的选课记录</span>
          <span class="record-count">共 {{ totalCourses }} 条记录</span>
        </div>
        <div class="courses-content">
          <el-table
            :data="selectedCourses"
            style="width: 100%"
            class="course-table"
            :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}"
            v-loading="coursesLoading"
            empty-text="暂无数据"
          >
            <el-table-column label="序号" width="80" align="center">
              <template v-slot="scope">
                <span class="index-badge">
                  {{ scope.$index + 1 }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="courseName" label="课程名称" min-width="150" />
            <el-table-column label="课程性质" width="100" align="center">
              <template v-slot="scope">
                <el-tag 
                  :type="getCourseCategoryTagType(scope.row.courseCategory)" 
                  size="mini"
                  effect="plain"
                >
                  {{ getCourseCategoryName(scope.row.courseCategory) || '未分类' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="课程类型" width="120" align="center">
              <template v-slot="scope">
                <el-tag 
                  :type="getCourseTypeTagType(scope.row.courseType)" 
                  size="mini"
                  effect="plain"
                >
                  {{ getCourseTypeName(scope.row.courseType) || '未分类' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="学分" width="80" align="center">
              <template v-slot="scope">
                <span class="credit-value">{{ scope.row.courseCredit || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="courseLocation" label="课程地点" min-width="120" />
            <el-table-column prop="organizer" label="组织单位" min-width="120" />
            <el-table-column label="成绩来源于" width="120" align="center">
              <template v-slot="scope">
                <el-tag 
                  v-if="scope.row.scoreType" 
                  :type="getScoreTypeTagType(scope.row.scoreType)" 
                  size="mini"
                  effect="plain"
                >
                  {{ scope.row.scoreType }}
                </el-tag>
                <span v-else class="no-score-type">暂无成绩</span>
              </template>
            </el-table-column>
            <el-table-column label="课程开始时间" width="140" align="center">
              <template v-slot="scope">
                <span>{{ formatDateTime(scope.row.startTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="课程结束时间" width="140" align="center">
              <template v-slot="scope">
                <span>{{ formatDateTime(scope.row.endTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="材料提交" width="100" align="center">
              <template v-slot="scope">
                <el-tag
                  v-if="!isCourseEnded(scope.row.endTime)"
                  type="info"
                  size="mini"
                  effect="light"
                >课程未结束</el-tag>
                <template v-else>
                  <el-button
                    v-if="scope.row.status === '未提交'"
                    type="text"
                    size="mini"
                    @click="openUploadDialog(scope.row)"
                  >提交</el-button>
                  <el-tag
                    v-if="scope.row.status === '未通过'"
                    type="danger"
                    size="mini"
                    effect="light"
                    @click="openUploadDialog(scope.row)"
                    class="clickable-tag"
                  >重新提交</el-tag>
                  <el-tag
                    v-if="scope.row.status === '未考核' || scope.row.status === '未审核'"
                    type="warning"
                    size="mini"
                    effect="light"
                  >审核中</el-tag>
                  <el-tag
                    v-if="scope.row.status === '已通过'"
                    type="success"
                    size="mini"
                    effect="light"
                  >已通过</el-tag>
                </template>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页组件 -->
          <div class="pagination-container">
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="currentPage"
              :page-sizes="[10, 20, 50, 100]"
              :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalCourses"
              background
            >
            </el-pagination>
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
        <el-button type="primary" @click="notificationDialogVisible = false" size="medium">
          <i class="el-icon-check"></i> 确定
        </el-button>
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
import { listBookingsWithCourse } from "@/api/system/courseBookings";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "StudentHome",
  data() {
    return {
      // 通知数据
      notifications: [],
      
      // 通知类型筛选
      selectedNotificationType: 'all',
      notificationTabs: [
        { value: 'all', label: '全部' },
        { value: 'system', label: '系统通知' },
        { value: 'activity', label: '活动通知' },
        { value: 'course', label: '课程通知' }
      ],
      
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
      
      // 已选课程数据
      selectedCourses: [],
      totalCourses: 0,
      
      // 分页数据
      currentPage: 1,
      pageSize: 10,
      
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
    
    // 计算弹窗宽度
    dialogWidth() {
      // 根据屏幕宽度和侧边栏宽度计算弹窗宽度
      const screenWidth = window.innerWidth;
      const sidebarWidth = screenWidth > 1200 ? 200 : (screenWidth > 768 ? 64 : 0);
      const availableWidth = screenWidth - sidebarWidth - 40; // 减去40px的边距
      return Math.max(availableWidth, 600) + 'px'; // 最小宽度600px
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
    }
  },
  created() {
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
        console.log('API基础URL:', process.env.VUE_APP_BASE_API);
        console.log('请求URL:', '/system/notifications/public/list');
        
        // 使用公开接口
        const response = await listNotificationsPublic({
          pageNum: 1,
          pageSize: 5
        });
        
        console.log('通知API响应:', response);
        
        if (response && response.code === 200) {
          this.notifications = response.rows || [];
          console.log('成功加载通知数据:', this.notifications);
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

    // 加载近期活动数据
    async loadRecentActivities() {
      this.activitiesLoading = true;
      try {
        // 不限制组织者，获取所有活动
        const response = await listActivities({
          pageNum: 1,
          pageSize: 100 // 获取更多数据以便过滤
        });
        
        console.log('活动API响应:', response);
        
        if (response.code === 200) {
          const allActivities = response.rows || [];
          console.log('所有活动数据:', allActivities);
          
          // 计算一个月前的时间
          const oneMonthAgo = new Date();
          oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);
          console.log('一个月前时间:', oneMonthAgo);
          
          // 过滤出一个月内的活动并按时间倒序排列
          let filteredActivities = allActivities
            .filter(activity => {
              if (!activity.startTime) return false;
              const activityTime = new Date(activity.startTime);
              console.log('活动时间:', activityTime, '是否在一个月内:', activityTime >= oneMonthAgo);
              return activityTime >= oneMonthAgo;
            })
            .sort((a, b) => new Date(b.startTime) - new Date(a.startTime));
          
          // 如果一个月内的活动不足3个，则获取最近的活动
          if (filteredActivities.length < 3) {
            console.log('一个月内活动不足3个，获取最近的活动');
            filteredActivities = allActivities
              .filter(activity => activity.startTime)
              .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
              .slice(0, 5);
          }
          
          this.recentActivities = filteredActivities.slice(0, 5);
            
          console.log('过滤后的近期活动:', this.recentActivities);
        } else {
          console.error('API响应错误:', response);
          this.$message.error('加载近期活动失败');
        }
      } catch (error) {
        console.error('加载近期活动失败:', error);
        this.$message.error('加载近期活动失败');
      } finally {
        this.activitiesLoading = false;
      }
    },

    // 加载活动完成进度数据
    async loadActivityProgress() {
      try {
        // 获取当前学期
        const currentDate = new Date();
        const currentYear = currentDate.getFullYear();
        const currentMonth = currentDate.getMonth() + 1;
        let currentSemester = '';
        
        if (currentMonth >= 9 || currentMonth <= 1) {
          currentSemester = `${currentYear}-${currentYear + 1}-1`;
        } else if (currentMonth >= 3 && currentMonth <= 7) {
          currentSemester = `${currentYear - 1}-${currentYear}-2`;
        }
        
        // 获取活动记录数据
        const response = await listActivity({
          pageNum: 1,
          pageSize: 1000,
          studentId: this.$store.state.user.name,
          semester: currentSemester
        });
        
        if (response.code === 200) {
          const activityRecords = response.rows || [];
          
          // 按活动类型分类统计
          this.activityCategories.forEach(category => {
            const categoryRecords = activityRecords.filter(record => 
              this.getActivityType(record.activityType) === category.type
            );
            category.completed = categoryRecords.filter(record => 
              record.auditStatus === '已通过'
            ).length;
            category.total = Math.max(categoryRecords.length, 8); // 至少显示8个
            category.progress = category.total > 0 ? Math.round((category.completed / category.total) * 100) : 0;
          });
          
          // 更新活动状态筛选的计数
          this.updateActivityStatusCounts(activityRecords);
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
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          studentId: this.$store.state.user.name
        });
        if (response.code === 200) {
          this.selectedCourses = response.rows || [];
          this.totalCourses = response.total || 0;
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

    // 获取活动状态文本
    getActivityStatusText(activity) {
      const now = new Date();
      const start = new Date(activity.startTime);
      const end = new Date(activity.endTime);
      const deadline = new Date(activity.activityDeadline);
      const activityStart = new Date(activity.activityStart);

      console.log('活动状态判断:', {
        activityName: activity.activityName,
        now: now,
        activityStart: activityStart,
        deadline: deadline,
        start: start,
        end: end
      });

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
      this.selectedStatus = status;
      this.$message.info(`筛选状态: ${status}`);
    },

    // 清除状态筛选
    clearStatusFilter() {
      this.selectedStatus = null;
      this.$message.info('显示全部活动');
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
    updateActivityStatusCounts(activityRecords) {
      this.activityStatusFilters.forEach(filter => {
        filter.count = activityRecords.filter(record => 
          record.auditStatus === filter.value
        ).length;
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
    
    // 根据类型筛选通知
    filterNotificationsByType(type) {
      this.selectedNotificationType = type;
    },
    
    // 格式化通知日期
    formatNotificationDate(date) {
      if (!date) return '';
      const d = new Date(date);
      const day = d.getDate();
      const month = d.getMonth() + 1;
      return `${day} ${month}月`;
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
    
    // 分页大小改变
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1; // 重置到第一页
      this.loadSelectedCourses();
    },
    
    // 当前页改变
    handleCurrentChange(val) {
      this.currentPage = val;
      this.loadSelectedCourses();
    }
  }
};
</script>

<style scoped>
.student-home-container {
  margin-left: 100px;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
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

.activity-completion-panel,
.activity-status-panel {
  flex: 1;
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
  padding: 16px 20px;
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.panel-header i {
  margin-right: 8px;
  font-size: 18px;
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

.record-count {
  margin-left: auto;
  margin-right: 10px;
  font-size: 12px;
  opacity: 0.9;
}

/* 面板内容 */
.notification-content,
.activities-content,
.completion-content,
.status-content,
.courses-content {
  padding: 20px;
}

/* 通知类型标签页 */
.notification-tabs {
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 16px;
}

.notification-tab {
  flex: 1;
  padding: 8px 12px;
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

/* 通知列表 */
.notification-list {
  max-height: 200px;
  overflow-y: auto;
}

/* 自定义滚动条样式 - 类似近期活动 */
.notification-list::-webkit-scrollbar {
  width: 6px;
}

.notification-list::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 3px;
}

.notification-list::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.notification-list::-webkit-scrollbar-thumb:hover {
  background: #a8abb2;
}

.notification-item {
  display: flex;
  align-items: flex-start;
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

.badge.top {
  background: #409EFF;
  color: white;
}

/* 活动列表 */
.activity-list {
  max-height: 240px;
  overflow-y: auto;
  min-height: 120px;
}

.activity-item {
  padding: 14px 0;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background: #f8f9fa;
  padding-left: 8px;
  border-radius: 6px;
}

.activity-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
  font-size: 14px;
  line-height: 1.4;
}

.activity-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

/* 活动完成进度 */
.progress-categories {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-category {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-label {
  min-width: 200px;
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.progress-bar-container {
  flex: 1;
  display: flex;
  align-items: center;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #f0f2f5;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #64b5ff);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-text {
  min-width: 40px;
  text-align: right;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

/* 状态筛选 */
.status-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.status-filter-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
}

.status-filter-item:hover {
  background: #f0f9ff;
  border-color: #bae6fd;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.status-filter-item.active {
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  border-color: #409EFF;
  color: white;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.status-filter-item.clear-filter {
  background: #f0f9ff;
  border-color: #bae6fd;
}

.status-filter-item.clear-filter.active {
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  border-color: #409EFF;
  color: white;
}

.status-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 14px;
}

.status-icon.unsubmitted {
  background: linear-gradient(135deg, #f39c12, #f1c40f);
  color: white;
}

.status-icon.rejected {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: white;
}

.status-icon.pending {
  background: linear-gradient(135deg, #f39c12, #e67e22);
  color: white;
}

.status-icon.approved {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
}

.status-icon.all {
  background: linear-gradient(135deg, #6c757d, #495057);
  color: white;
}

.status-info {
  display: flex;
  flex-direction: column;
}

.status-count {
  font-size: 18px;
  font-weight: 700;
  line-height: 1;
}

.status-label {
  font-size: 12px;
  margin-top: 2px;
}

.status-filter-item.active .status-count,
.status-filter-item.active .status-label {
  color: white;
}

/* 课程表格样式 */
.course-table {
  border-radius: 8px;
  overflow: hidden;
}

.course-table /deep/ .el-table__header th {
  background: #f8fafc !important;
  color: #303133;
  font-weight: 600;
  font-size: 0.9rem;
}

.course-table /deep/ .el-table__body td {
  color: #606266;
  transition: background 0.2s;
}

.course-table /deep/ .el-table__body tr:hover td {
  background: #f5f7fa !important;
}

.credit-value {
  font-weight: 600;
  color: #409EFF;
}

.index-badge {
  display: inline-flex;
  width: 24px;
  height: 24px;
  background: #ebf4ff;
  border-radius: 50%;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  color: #2b6cb0;
  font-size: 12px;
}

.no-score-type {
  color: #c0c4cc;
  font-size: 12px;
}

.clickable-tag {
  cursor: pointer;
}

.clickable-tag:hover {
  opacity: 0.8;
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
  border-color: #409EFF;
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
.no-activity {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.no-notification i,
.no-activity i {
  font-size: 32px;
  margin-bottom: 8px;
  display: block;
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
  
  .status-filters {
    flex-direction: column;
  }
  
  .status-filter-item {
    min-width: auto;
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

.detail-type {
  display: flex;
  align-items: center;
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

/* 通知项点击效果 */
.notification-item {
  cursor: pointer;
  transition: all 0.2s;
}

.notification-item:hover {
  background: #f5f7fa;
  transform: translateX(4px);
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
</style>