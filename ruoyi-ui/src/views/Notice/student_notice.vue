<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="card-header">
        <i class="el-icon-search"></i>
        <span>搜索条件</span>
      </div>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <div class="search-row">
          <el-form-item label="关键字" prop="notiTitle">
            <el-input
              v-model="queryParams.notiTitle"
              placeholder="搜索标题或内容"
              clearable
              prefix-icon="el-icon-search"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button-group>
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="handleQuery"
                class="search-button"
              >搜索</el-button>
              <el-button
                icon="el-icon-refresh"
                @click="resetQuery"
                class="refresh-button"
              >重置</el-button>
            </el-button-group>
          </el-form-item>
        </div>
      </el-form>
    </div>

    <!-- 通知列表 -->
    <div class="notifications-container">
      <div class="notifications-header">
        <div class="header-left">
          <i class="el-icon-bell"></i>
          <span>通知列表</span>
        </div>
      </div>

      <!-- 通知内容 -->
      <div class="notifications-content">
        <div v-if="notifications.length === 0" class="no-notification">
          <i class="el-icon-info"></i>
          <span>暂无通知</span>
        </div>
        <div v-else class="notification-list">
          <div
            v-for="(notification, index) in notifications"
            :key="notification.notiId"
            class="notification-item"
          >
            <div class="notification-main" @click="viewNotification(notification)">
              <div class="notification-date">
                {{ formatNotificationDate(notification.createdAt) }}
              </div>
              <div class="notification-info">
                <div class="notification-title">{{ notification.notiTitle }}</div>
                <div class="notification-meta">
                  <span class="notification-type">{{ notification.notiType }}</span>
                  <span class="notification-time">{{ formatNotificationTime(notification.createdAt) }}</span>
                </div>
                <div class="notification-content">{{ notification.notiContent }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页组件 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit="5"
        :page-sizes="[5]"
        :page-size="5"
        @pagination="getList"
        class="custom-pagination"
      />
    </div>

    <!-- 通知详情弹窗 -->
    <el-dialog
      title="通知详情"
      :visible.sync="detailDialogVisible"
      :width="dialogWidth"
      :close-on-click-modal="false"
      custom-class="notification-dialog"
    >
      <div v-if="currentNotification" class="notification-detail">
        <div class="detail-header">
          <h3>{{ currentNotification.notiTitle }}</h3>
          <div class="detail-meta">
            <el-tag :type="getNotificationTypeColor(currentNotification.notiType)">
              {{ currentNotification.notiType }}
            </el-tag>
            <span class="detail-time">{{ formatNotificationTime(currentNotification.createdAt) }}</span>
          </div>
        </div>
        <div class="detail-content">
          <p>{{ currentNotification.notiContent }}</p>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 发布通知弹窗 -->
    <el-dialog
      title="发布通知"
      :visible.sync="publishDialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="publishForm" :rules="publishRules" ref="publishForm" label-width="100px">
        <el-form-item label="通知标题" prop="notiTitle">
          <el-input
            v-model="publishForm.notiTitle"
            placeholder="请输入通知标题"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>


        <el-form-item label="通知内容" prop="notiContent">
          <el-input
            v-model="publishForm.notiContent"
            type="textarea"
            :rows="6"
            placeholder="请输入通知内容"
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
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="100px">
        <el-form-item label="通知标题" prop="notiTitle">
          <el-input
            v-model="editForm.notiTitle"
            placeholder="请输入通知标题"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>


        <el-form-item label="通知内容" prop="notiContent">
          <el-input
            v-model="editForm.notiContent"
            type="textarea"
            :rows="6"
            placeholder="请输入通知内容"
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
import { listNotificationsPublic, addNotifications, updateNotifications, delNotifications } from "@/api/system/notifications";
import { getStudent, getNickName } from "@/api/system/student";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "NoticeIndex",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 当前用户书院
      currentUserCollege: '',
      // 通知列表
      notifications: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5, // 固定每页显示5条数据
        notiTitle: null
      },
      // 通知详情弹窗
      detailDialogVisible: false,
      currentNotification: null,

      // 发布通知数据
      publishDialogVisible: false,
      publishSubmitting: false,
      publishForm: {
        notiTitle: '',
        notiContent: '',
        creatorId: '',
        createdAt: null
      },
      publishRules: {
        notiTitle: [
          { required: true, message: '请输入通知标题', trigger: 'blur' },
          { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
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
        notiContent: '',
        creatorId: '',
        createdAt: null
      },
      editRules: {
        notiTitle: [
          { required: true, message: '请输入通知标题', trigger: 'blur' },
          { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        notiContent: [
          { required: true, message: '请输入通知内容', trigger: 'blur' },
          { min: 10, max: 1000, message: '内容长度在 10 到 1000 个字符', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    // 参照首页书院通知弹窗宽度：根据屏幕宽度与侧边栏动态计算，最小 800px
    dialogWidth() {
      const screenWidth = window.innerWidth;
      const sidebarWidth = screenWidth > 1200 ? 200 : (screenWidth > 768 ? 64 : 0);
      const availableWidth = screenWidth - sidebarWidth - 40; // 预留边距
      return Math.max(availableWidth, 800) + 'px';
    }
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询通知列表 */
    async getList() {
      this.loading = true;
      const startTime = Date.now();
      
      try {
        // 获取当前学生用户的书院信息（仅学生可访问）
        let userCollege = '';
        try {
          // 1) 优先：从学生信息API获取 academy
          const studentResponse = await getStudent(this.$store.state.user.name);
          if (studentResponse && studentResponse.studentInfo && studentResponse.studentInfo.academy) {
            userCollege = studentResponse.studentInfo.academy;
            console.log('当前用户书院（student.academy）:', userCollege);
          }

          // 2) 其次：从 store 获取 academy
          if (!userCollege) {
            userCollege = this.$store.state.user.academy || '';
            if (userCollege) {
              console.log('当前用户书院（store.academy）:', userCollege);
            }
          }

          // 如果获取不到学生书院信息，提示并返回
          if (!userCollege) {
            console.warn('无法获取学生书院信息，该页面仅限学生访问');
            this.$message.warning('无法获取您的书院信息，请确保您已登录学生账号');
            this.notifications = [];
            this.total = 0;
            return;
          }

          this.currentUserCollege = userCollege;
        } catch (error) {
          console.error('获取用户书院信息失败:', error);
          this.$message.error('获取书院信息失败，该页面仅限学生访问');
          this.currentUserCollege = '';
          this.notifications = [];
          this.total = 0;
          return;
        }
        
        // 构建查询参数，包含书院筛选（学生只能看到自己书院的通知）
        const queryParams = {
          ...this.queryParams,
          notiType: userCollege // 添加书院筛选条件
        };
        
        console.log('开始加载通知列表，查询参数:', queryParams);
        
        // 添加超时处理
        const timeoutPromise = new Promise((_, reject) => {
          setTimeout(() => reject(new Error('请求超时')), 10000); // 10秒超时
        });
        
        const apiPromise = listNotificationsPublic(queryParams);
        const response = await Promise.race([apiPromise, timeoutPromise]);
        
        const loadTime = Date.now() - startTime;
        console.log(`通知列表API响应 (耗时: ${loadTime}ms):`, response);
        
        if (response && response.code === 200) {
          // 直接使用后端返回的分页数据
          this.notifications = response.rows || [];
          this.total = response.total || 0;
          console.log('后端返回的通知数量:', this.notifications.length);
          console.log('后端返回的总数:', this.total);
        } else {
          console.log('API返回非200状态码:', response?.code, response?.msg);
          this.$message.error('获取通知列表失败: ' + (response?.msg || '服务器返回错误'));
          this.notifications = [];
          this.total = 0;
        }
        
        if (loadTime > 3000) {
          console.warn('通知列表加载较慢，耗时:', loadTime + 'ms');
        }
        
      } catch (error) {
        const loadTime = Date.now() - startTime;
        console.error(`获取通知列表失败 (耗时: ${loadTime}ms):`, error);
        
        if (error.message === '请求超时') {
          this.$message.error('请求超时，请检查网络连接');
        } else {
          this.$message.error('获取通知列表失败');
        }
        
        this.notifications = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams.notiTitle = null;
      this.queryParams.pageNum = 1;
      this.$refs.queryForm.resetFields();
      this.getList();
    },

    /** 查看通知详情 */
    viewNotification(notification) {
      this.currentNotification = notification;
      this.detailDialogVisible = true;
    },

    /** 格式化通知日期 */
    formatNotificationDate(date) {
      if (!date) return '';
      const d = new Date(date);
      const day = d.getDate();
      const month = d.getMonth() + 1;
      return `${month}月 ${day}`;
    },

    /** 格式化通知时间 */
    formatNotificationTime(date) {
      if (!date) return '';
      return parseTime(date, '{y}-{m}-{d} {h}:{i}');
    },

    /** 获取通知类型颜色 */
    getNotificationTypeColor(type) {
      const colorMap = {
        'system': 'primary',
        'activity': 'success',
        'course': 'warning'
      };
      return colorMap[type] || 'primary';
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
        notiContent: notification.notiContent,
        creatorId: notification.creatorId,
        createdAt: notification.createdAt
      };
    },

    // 删除通知
    deleteNotification(notification) {
      this.$confirm(`确定要删除通知"${notification.notiTitle}"吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await delNotifications(notification.notiId);
          if (response.code === 200) {
            this.$message.success('通知删除成功');
            this.getList();
          } else {
            this.$message.error('通知删除失败: ' + (response.msg || '未知错误'));
          }
        } catch (error) {
          this.$message.error('通知删除失败: ' + error.message);
        }
      }).catch(() => {
        // 用户取消删除
      });
    },

    // 提交发布通知
    submitPublish() {
      this.$refs.publishForm.validate(async (valid) => {
        if (valid) {
          this.publishSubmitting = true;
          try {
            const now = new Date();
            // 格式化为后端期望的格式: yyyy-MM-dd HH:mm:ss
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');
            const seconds = String(now.getSeconds()).padStart(2, '0');
            const localTimeString = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
            
            // 获取当前学生用户的书院信息（仅学生可发布通知）
            let userCollege = '';
            console.log('开始获取学生书院信息，当前用户名:', this.$store.state.user.name);
            
            try {
              // 优先从学生信息API获取
              console.log('尝试从getStudent API获取书院信息...');
              const studentResponse = await getStudent(this.$store.state.user.name);
              console.log('getStudent API响应:', studentResponse);
              
              if (studentResponse && studentResponse.studentInfo && studentResponse.studentInfo.academy) {
                userCollege = studentResponse.studentInfo.academy;
                console.log('从student.academy获取到书院:', userCollege);
              } else {
                // 从Vuex store获取
                userCollege = this.$store.state.user.academy || '';
                console.log('从Vuex store获取书院:', userCollege);
              }
            } catch (error) {
              console.error('获取用户书院信息失败:', error);
            }
            
            // 如果获取不到学生书院信息，不允许发布
            if (!userCollege) {
              this.$message.warning('无法获取您的书院信息，该功能仅限学生使用');
              return;
            }
            
            console.log('最终确定的学生书院:', userCollege);
            
            const publishData = {
              ...this.publishForm,
              creatorId: this.$store.state.user.name || '',
              createdAt: localTimeString,
              notiType: userCollege || 'system' // 设置通知类型为当前用户的书院
            };
            
            console.log('发布数据:', publishData);

            const response = await addNotifications(publishData);
            if (response.code === 200) {
              this.$message.success('通知发布成功');
              this.publishDialogVisible = false;
              this.resetPublishForm();
              // 重置到第一页并刷新列表
              this.queryParams.pageNum = 1;
              this.getList();
            } else {
              this.$message.error('通知发布失败: ' + (response.msg || '未知错误'));
            }
          } catch (error) {
            this.$message.error('通知发布失败: ' + error.message);
          } finally {
            this.publishSubmitting = false;
          }
        }
      });
    },

    // 提交编辑通知
    submitEdit() {
      this.$refs.editForm.validate(async (valid) => {
        if (valid) {
          this.editSubmitting = true;
          try {
            const response = await updateNotifications(this.editForm);
            if (response.code === 200) {
              this.$message.success('通知编辑成功');
              this.editDialogVisible = false;
              // 刷新当前页列表
              this.getList();
            } else {
              this.$message.error('通知编辑失败: ' + (response.msg || '未知错误'));
            }
          } catch (error) {
            this.$message.error('通知编辑失败: ' + error.message);
          } finally {
            this.editSubmitting = false;
          }
        }
      });
    },

    // 重置发布表单
    resetPublishForm() {
      this.publishForm = {
        notiTitle: '',
        notiContent: '',
        creatorId: this.$store.state.user.name || '',
        createdAt: null
      };
    }

  }
};
</script>

<style scoped>
.notification-dialog {
  border-radius: 8px !important;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e4e7ed;
}

.notification-dialog .el-dialog__header {
  background: linear-gradient(135deg, #42A5F5 0%, #64B5F6 100%) !important;
  color: white !important;
  padding: 20px 24px !important;
  border-bottom: none !important;
  border-radius: 8px 8px 0 0 !important;
}

.notification-dialog .el-dialog__title {
  color: white !important;
  font-size: 18px !important;
  font-weight: 700 !important;
}

.notification-detail {
  padding: 0;
  min-height: 400px;
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

.detail-content {
  padding: 24px;
  flex: 1;
  overflow-y: auto;
  background: #ffffff;
}

@media (max-width: 768px) {
  .notification-dialog {
    width: 95% !important;
    margin: 0 auto;
  }
}
.app-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 搜索卡片 */
.search-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #e4e7ed;
  border-radius: 12px 12px 0 0;
}

.card-header i {
  font-size: 16px;
  color: #409EFF;
  margin-right: 8px;
}

.card-header span {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  flex-wrap: wrap;
}

.search-input {
  width: 200px;
}

.search-button, .refresh-button {
  margin-left: 10px;
}

/* 通知容器 */
.notifications-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

.notifications-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left i {
  font-size: 18px;
}

.header-left span {
  font-size: 16px;
  font-weight: 600;
}

.refresh-btn {
  color: white !important;
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

/* 通知内容 */
.notifications-content {
  padding: 20px;
}

.no-notification {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #909399;
}

.no-notification i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #c0c4cc;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: all 0.3s ease;
  gap: 16px;
}

.notification-item:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.notification-date {
  flex-shrink: 0;
  width: 60px;
  text-align: center;
  background: #f0f9ff;
  color: #409EFF;
  padding: 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.notification-info {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.4;
}

.notification-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}

.notification-type {
  background: #f0f9ff;
  color: #409EFF;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.notification-time {
  color: #909399;
  font-size: 12px;
}

.notification-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}


/* 分页样式 */
.custom-pagination {
  display: flex;
  justify-content: center !important;
  margin: 10px auto 0;
  padding: 8px 0;
  width: 100%;
  position: relative;
  top: -20px; /* 向上移动20px避免被覆盖 */
  z-index: 10; /* 确保分页组件在最上层 */
}

.custom-pagination /deep/ .el-pagination {
  display: inline-flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.custom-pagination /deep/ .el-pager li:hover {
  border-color: #409EFF;
  color: #409EFF;
}

/* 通知项布局 */
.notification-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 12px;
  background: #fff;
  transition: all 0.3s ease;
}

.notification-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.notification-main {
  flex: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-actions {
  display: flex;
  align-items: center;
  gap: 8px;
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

/* 添加按钮样式 */
.add-button {
  background: linear-gradient(135deg, #409EFF 0%, #36a3f7 100%);
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.add-button:hover {
  background: linear-gradient(135deg, #36a3f7 0%, #2b8ce6 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 通知详情弹窗 */
.notification-detail {
  padding: 20px 0;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.detail-header h3 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.detail-time {
  color: #909399;
  font-size: 14px;
}

.detail-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.detail-content p {
  margin: 0;
  white-space: pre-wrap;
}
</style>
