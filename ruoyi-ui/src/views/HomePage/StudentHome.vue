<template>
  <div class="student-home-container" v-loading="loading">
    <!-- 顶部通知栏和近期活动 -->
    <div class="top-section">
      <!-- 左上角通知栏 -->
      <div class="notification-panel">
        <div class="panel-header">
          <i class="el-icon-bell"></i>
          <span>系统通知</span>
          <el-button 
            type="text" 
            size="mini" 
            @click="loadNotifications"
            class="refresh-btn"
            :loading="notificationsLoading"
          >
            <i class="el-icon-refresh"></i>
          </el-button>
        </div>
        <div class="notification-content">
          <div v-if="notifications.length === 0" class="no-notification">
            <i class="el-icon-info"></i>
            <span>暂无通知</span>
          </div>
          <div v-else class="notification-list">
            <div 
              v-for="notification in notifications" 
              :key="notification.notiId"
              class="notification-item"
              @click="showNotificationDetail(notification)"
            >
              <div class="notification-title">{{ notification.notiTitle }}</div>
              <div class="notification-time">{{ formatDate(notification.createdAt) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右上角近期活动通知 -->
      <div class="recent-activities-panel">
        <div class="panel-header">
          <i class="el-icon-calendar"></i>
          <span>近期活动</span>
          <el-button 
            type="text" 
            size="mini" 
            @click="loadRecentActivities"
            class="refresh-btn"
            :loading="activitiesLoading"
          >
            <i class="el-icon-refresh"></i>
          </el-button>
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
        </div>
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
          this.recentActivities = allActivities
            .filter(activity => {
              if (!activity.startTime) return false;
              const activityTime = new Date(activity.startTime);
              console.log('活动时间:', activityTime, '是否在一个月内:', activityTime >= oneMonthAgo);
              return activityTime >= oneMonthAgo;
            })
            .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
            .slice(0, 5);
            
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
          pageNum: 1,
          pageSize: 10,
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

/* 通知列表 */
.notification-list {
  max-height: 200px;
  overflow-y: auto;
}

.notification-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

/* 活动列表 */
.activity-list {
  max-height: 200px;
  overflow-y: auto;
}

.activity-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: all 0.3s ease;
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
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
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

.detail-time::before {
  content: "🕒";
  font-size: 12px;
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
</style>