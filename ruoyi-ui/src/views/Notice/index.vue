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
          <el-form-item label="通知标题" prop="notiTitle">
            <el-input
              v-model="queryParams.notiTitle"
              placeholder="请输入通知标题"
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
          <el-tag type="info" size="small">{{ total }} 条通知</el-tag>
        </div>
        <div class="header-right">
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="showPublishDialog"
            class="add-button"
          >
            发布通知
          </el-button>
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
            <div class="notification-actions">
              <el-button
                type="text"
                size="small"
                icon="el-icon-edit"
                @click.stop="editNotification(notification)"
                title="编辑通知"
                class="action-btn edit-btn"
              ></el-button>
              <el-button
                type="text"
                size="small"
                icon="el-icon-delete"
                @click.stop="deleteNotification(notification)"
                title="删除通知"
                class="action-btn delete-btn"
              ></el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页组件 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
        class="custom-pagination"
      />
    </div>

    <!-- 通知详情弹窗 -->
    <el-dialog
      title="通知详情"
      :visible.sync="detailDialogVisible"
      width="600px"
      :close-on-click-modal="false"
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
      // 通知列表
      notifications: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
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
  created() {
    this.getList();
  },
  methods: {
    /** 查询通知列表 */
    async getList() {
      this.loading = true;
      try {
        const response = await listNotificationsPublic(this.queryParams);
        this.notifications = response.rows || [];
        this.total = response.total || 0;
      } catch (error) {
        console.error('获取通知列表失败:', error);
        this.$message.error('获取通知列表失败');
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
      return `${day} ${month}月`;
    },

    /** 格式化通知时间 */
    formatNotificationTime(date) {
      if (!date) return '';
      return parseTime(date, '{y}-{m}-{d} {h}:{i}');
    },

    /** 获取通知类型标签 */
    getNotificationTypeLabel(type) {
      const typeMap = {
        'system': '系统通知',
        'activity': '活动通知',
        'course': '课程通知'
      };
      return typeMap[type] || '系统通知';
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
            const localTimeString = now.toISOString();

            const publishData = {
              ...this.publishForm,
              creatorId: this.$store.state.user.name || '',
              createdAt: localTimeString
            };

            const response = await addNotifications(publishData);
            if (response.code === 200) {
              this.$message.success('通知发布成功');
              this.publishDialogVisible = false;
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
        createdAt: new Date()
      };
    }

  }
};
</script>

<style scoped>
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
  margin: 20px auto 0;
  padding: 12px 0;
  width: 100%;
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
