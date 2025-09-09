<template>
  <div class="app-container">
    <!-- 视图切换标签 -->
    <el-tabs v-model="activeView" class="view-tabs">
      <!-- 日历视图 -->
      <el-tab-pane label="日历视图" name="calendar">
        <el-calendar v-model="calendarDate" class="calendar-view" style="--calendar-day-height: 120px;">
          <template #dateCell="{ data }">
            <div 
              v-if="isCurrentMonth(data.day)" 
              class="calendar-cell"
            >
              <div class="date-header">
                <span>{{ data.day.split('-')[2] }}</span>
              </div>
              <div class="events-container">
                <div
                  v-for="(event, index) in getDateEvents(data.day)"
                  :key="index"
                  :class="['calendar-event', 'status-' + getActivityStatusType(event)]"
                  @click="handleEventClick(event)"
                >
                  <div class="event-summary">
                    <span class="event-name">{{ truncate(event.activityName, 10) }}</span>
                    <span class="event-org">({{ truncate(event.organizer, 5) }})</span>
                    <el-button
                      v-if="isBeforeToday(event.activityDeadline)"
                      type="text"
                      size="mini"
                      class="detail-btn"
                    >
                      详细
                    </el-button>
                    <el-button
                      v-else
                      type="text"
                      size="mini"
                      disabled
                      class="detail-btn disabled-btn"
                    >
                      已截止
                    </el-button>
                  </div>
                  <div class="event-time-range">
                    {{ formatTimeRange(event.startTime, event.endTime) }}
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="calendar-cell empty-cell">
              <!-- 非当月日期显示为空 -->
            </div>
          </template>
        </el-calendar>
        
        <!-- 活动状态图例 -->
        <div class="activity-legend">
          <h4 class="legend-title">活动状态图例</h4>
          <div class="legend-items">
            <div class="legend-item">
              <div class="legend-color status-not-started"></div>
              <span class="legend-text">报名未开始</span>
            </div>
            <div class="legend-item">
              <div class="legend-color status-signup-active"></div>
              <span class="legend-text">报名进行中</span>
            </div>
            <div class="legend-item">
              <div class="legend-color status-signup-ended"></div>
              <span class="legend-text">报名已截止</span>
            </div>
            <div class="legend-item">
              <div class="legend-color status-activity-active"></div>
              <span class="legend-text">活动进行中</span>
            </div>
            <div class="legend-item">
              <div class="legend-color status-activity-ended"></div>
              <span class="legend-text">活动已结束</span>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 活动报名视图 -->
      <el-tab-pane label="活动报名" name="booking">
        <ActivityBooking />
      </el-tab-pane>
    </el-tabs>

    <!-- 活动详情弹窗 -->
    <el-dialog
      title="活动详情"
      :visible.sync="dialogVisible"
      width="800px"
      :before-close="handleClose"
      class="activity-dialog"
    >
      <div class="activity-detail" v-if="selectedActivity">
        <!-- 活动详情展示 -->
        <div class="detail-header">
          <h2>{{ selectedActivity.activityName }}</h2>
          <el-tag :type="getActivityStatusTag(selectedActivity)" size="medium" class="status-tag">
            {{ getActivityStatusText(selectedActivity) }}
          </el-tag>
        </div>

        <el-divider></el-divider>

        <div class="detail-grid">
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-location"></i> 活动地点：</div>
            <div class="detail-value">{{ selectedActivity.activityLocation }}</div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-office-building"></i> 组织单位：</div>
            <div class="detail-value">{{ selectedActivity.organizer }}</div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-user"></i> 活动容量：</div>
            <div class="detail-value">
              <span :class="getCapacityClass(selectedActivity)">
                {{ selectedActivity.activityTotalCapacity - selectedActivity.activityCapacity }}/{{ selectedActivity.activityTotalCapacity }}人
              </span>
            </div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-alarm-clock"></i> 报名开始：</div>
            <div class="detail-value">{{ formatDateTime(selectedActivity.activityStart) }}</div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-alarm-clock"></i> 报名截止：</div>
            <div class="detail-value">{{ formatDateTime(selectedActivity.activityDeadline) }}</div>
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
          <div class="section-content">{{ selectedActivity.activityDescription }}</div>
        </div>

        <div class="detail-section-content">
          <h4 class="section-title"><i class="el-icon-warning"></i> 注意事项</h4>
          <div class="section-content">{{ selectedActivity.notes }}</div>
        </div>

        <!-- 报名/退掉按钮 -->
        <div class="signup-status">
          <el-button
            type="primary"
            :disabled="!showSignUpButton"
            @click="handleSignUp"
            v-if="showSignUpButton"
            class="signup-button"
          >
            立即报名
          </el-button>

          <el-button
            type="danger"
            :disabled="!showCancelButton"
            @click="handleCancelSignUp"
            v-if="showCancelButton"
            class="cancel-button"
          >
            取消报名
          </el-button>

          <el-alert
            v-if="showFullCapacityAlert"
            title="报名已满"
            type="warning"
            :closable="false"
            class="signup-alert"
          >
            该活动报名人数已满，无法继续报名
          </el-alert>

          <el-alert
            v-if="selectedActivity.isBooked && !showCancelButton"
            title="您已成功报名该活动"
            type="success"
            :closable="false"
            class="signup-alert"
          >
            报名信息已提交，无法取消报名
          </el-alert>
        </div>
      </div>


    </el-dialog>
  </div>
</template>


<script>
import { listActivities, signUpCapacity, cancelSignUpCapacity } from "@/api/system/activities";
import {addBooking, checkBookingSimple, deleteBookingsByActivityAndStudent} from "@/api/system/bookings";
import { parseTime } from "@/utils/ruoyi";
import ActivityBooking from "./Activity/ActivityBooking.vue";

export default {
  name: "ActivityDashboard",
  components: {
    ActivityBooking
  },
  data() {
    return {
      activeView: 'calendar', // 当前视图: calendar/booking
      calendarDate: new Date(), // 日历当前日期
      filterDateRange: null,
      activityList: [],
      loading: false,
      queryParams: {
        pageNum: 1,
        pageSize: 100, // 获取全部活动
        activityId: null,
        activityName: null,
        activityLocation: null,
        activityCapacity: null,
        activityTotalCapacity: null,
        activityStart: null,
        activityDeadline: null,
        activityDescription: null,
        status: null,
        createdAt: null,
        organizer: null,
        notes: null,
      },

      // 当前选中的活动
      selectedActivity: null,
      
      // 弹窗控制
      dialogVisible: false,

      // 报名表单相关
      showSignUpForm: false,
      signupForm: {
        name: '',
        studentId: '',
        phone: '',
        email: '',
        remarks: ''
      },
      signupRules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
      },

    };
  },
  computed: {
    // 显示报名按钮的条件
    showSignUpButton() {
      if (!this.selectedActivity) return false;
      return this.getActivityStatusText(this.selectedActivity) === "报名进行中" &&
        !this.selectedActivity.isBooked && // 使用活动对象的isBooked属性
        this.selectedActivity.activityCapacity > 0;
    },

    // 显示报名已满提示的条件
    showFullCapacityAlert() {
      if (!this.selectedActivity) return false;
      return this.getActivityStatusText(this.selectedActivity) === "报名进行中" &&
        !this.selectedActivity.isBooked &&
        this.selectedActivity.activityCapacity <= 0;
    },

    // 显示退掉按钮的条件
    showCancelButton() {
      if (!this.selectedActivity) return false;
      const status = this.getActivityStatusText(this.selectedActivity);
      return this.selectedActivity.isBooked && 
        (status === "报名进行中" || status === "报名未开始");
    },

  },
  created() {
    this.fetchActivities();
  },
  mounted() {
    this.hideEmptyCalendarRows();
    this.forceCalendarDayHeight();
  },
  watch: {
    calendarDate() {
      this.$nextTick(() => {
        this.hideEmptyCalendarRows();
        this.forceCalendarDayHeight();
      });
    },
    activeView(newView) {
      // 当切换到活动报名视图时，清除选中的活动
      if (newView === 'booking') {
        this.selectedActivity = null;
      }
    }
  },
  methods: {
    // 获取活动列表
    async fetchActivities() {
      this.loading = true;
      try {
        const response = await listActivities(this.queryParams);
        if (response.code === 200) {
          this.activityList = response.rows;

          // 计算活动状态并初始化isBooked属性
          const now = new Date();
          this.activityList = this.activityList.map(activity => {
            return {
              ...activity,
              status: this.calculateStatus(activity, now),
              isBooked: false // 初始化为未报名状态
            };
          });
        } else {
          this.$message.error("获取活动列表失败: " + response.msg);
        }
      } catch (error) {
        console.error("获取活动列表异常:", error);
        this.$message.error("获取活动列表异常");
      } finally {
        this.loading = false;
      }
      const checkPromises = this.activityList.map(activity =>
        checkBookingSimple(activity.activityId, this.$store.state.user.name).then(res => {
          activity.isBooked = res.data.isBooked;
        })
      );
      await Promise.all(checkPromises);
    },
    // 计算活动状态
    calculateStatus(activity, now) {
      const startTime = new Date(activity.startTime);
      const endTime = new Date(activity.endTime);
      const deadline = activity.activityDeadline ? new Date(activity.activityDeadline) : null;

      if (now > endTime) return '已结束';
      if (now >= startTime && now <= endTime) return '进行中';
      if (deadline && now > deadline) return '已截止';
      return activity.status || '未开始';
    },

    // 获取日期内的事件
    getDateEvents(dateString) {
      return this.activityList.filter(activity => {
        // 只显示活动开始日期当天的活动
        const activityStartDate = this.formatDate(activity.startTime);
        return activityStartDate === dateString;
      });
    },
    // 添加日期格式化方法
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;
    },
    // 检查日期是否在活动时间范围内
    isDateInRange(dateString, startTime, endTime) {
      const date = new Date(dateString);
      const start = new Date(startTime);
      const end = new Date(endTime);

      start.setHours(0, 0, 0, 0);
      end.setHours(23, 59, 59, 999);

      return date >= start && date <= end;
    },

    // 格式化日期时间
    formatDateTime(datetime) {
      return parseTime(datetime, "{y}-{m}-{d} {h}:{i}");
    },

    // 格式化时间范围
    formatTimeRange(startTime, endTime) {
      return `${parseTime(startTime, "{h}:{i}")} - ${parseTime(endTime, "{h}:{i}")}`;
    },

    // 检查是否在截止日期前
    isBeforeToday(deadline) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const deadlineDate = new Date(deadline);
      return deadlineDate >= today;
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

      if (now < activityStart) return "报名未开始";
      if (now < deadline && now >= activityStart) return "报名进行中";
      if (now >= deadline && now < start) return "报名已截止";
      if (now >= start && now <= end) return "活动进行中";
      if (now > end) return "活动已结束";
      return activity.status || "未知";
    },

    // 获取活动状态类型（用于颜色判断）
    getActivityStatusType(activity) {
      const now = new Date();
      const start = new Date(activity.startTime);
      const end = new Date(activity.endTime);
      const deadline = new Date(activity.activityDeadline);
      const activityStart = new Date(activity.activityStart);

      if (now < activityStart) return "not-started";
      if (now < deadline && now >= activityStart) return "signup-active";
      if (now >= deadline && now < start) return "signup-ended";
      if (now >= start && now <= end) return "activity-active";
      if (now > end) return "activity-ended";
      return "unknown";
    },

    // 获取容量样式
    getCapacityClass(activity) {
      const percentage = (activity.activityTotalCapacity - activity.activityCapacity) / activity.activityTotalCapacity;
      if (percentage >= 0.8) return 'capacity-high';
      if (percentage >= 0.5) return 'capacity-medium';
      return 'capacity-low';
    },

    // 字符串截断
    truncate(str, maxLength) {
      if (!str) return '';
      return str.length > maxLength ? str.substring(0, maxLength) + '...' : str;
    },

    // 修改报名处理方法
    handleSignUp() {
      this.$confirm('确定要报名该活动吗？', '报名确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.submitSignUp();
      }).catch(() => {
        this.$message.info('已取消报名');
      });
    },

    // 提交报名
    async submitSignUp() {
      try {
        // 1. 更新活动容量
        await signUpCapacity(this.selectedActivity.activityId, this.selectedActivity.version);

        // 2. 添加报名记录
        await addBooking({
          activityId: this.selectedActivity.activityId,
          studentId: this.$store.state.user.name,
        });

        // 3. 更新活动状态
        const updatedActivity = {
          ...this.selectedActivity,
          activityCapacity: Math.max(this.selectedActivity.activityCapacity - 1, 0),
          version: this.selectedActivity.version + 1,
          isBooked: true // 标记为已报名
        };

        this.selectedActivity = updatedActivity;

        // 4. 更新活动列表
        const index = this.activityList.findIndex(a => a.activityId === this.selectedActivity.activityId);
        if (index !== -1) {
          this.activityList.splice(index, 1, updatedActivity);
        }

        this.$message.success("报名成功！");
        // 报名成功后关闭弹窗
        this.dialogVisible = false;

      } catch (error) {
        console.error("报名失败:", error);
        this.$message.error("报名失败: " + (error.msg || "请稍后重试"));
      }
    },

    // 处理事件点击
    handleEventClick(activity) {
      this.selectedActivity = { ...activity };
      this.dialogVisible = true;
    },

    // 处理弹窗关闭
    handleClose(done) {
      this.dialogVisible = false;
      this.selectedActivity = null;
      done();
    },

    // 处理退掉活动
    handleCancelSignUp() {
      this.$confirm('确定要取消该活动吗？', '确认取消', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.submitCancelSignUp();
      }).catch(() => {
        this.$message.info('已取消取消报名操作');
      });
    },

    // 提交退掉活动
    async submitCancelSignUp() {
      try {
        // 1. 删除报名记录
        await deleteBookingsByActivityAndStudent(
          this.selectedActivity.activityId, 
          this.$store.state.user.name
        );

        // 2. 恢复活动容量
        await cancelSignUpCapacity(
          this.selectedActivity.activityId,
          this.selectedActivity.version
        );

        // 3. 更新活动状态
        const updatedActivity = {
          ...this.selectedActivity,
          activityCapacity: Math.min(this.selectedActivity.activityCapacity + 1, this.selectedActivity.activityTotalCapacity),
          version: this.selectedActivity.version + 1,
          isBooked: false // 标记为未报名
        };

        this.selectedActivity = updatedActivity;

        // 4. 更新活动列表
        const index = this.activityList.findIndex(a => a.activityId === this.selectedActivity.activityId);
        if (index !== -1) {
          this.activityList.splice(index, 1, updatedActivity);
        }

        this.$message.success("退掉活动成功！");
        // 退掉成功后关闭弹窗
        this.dialogVisible = false;

      } catch (error) {
        console.error("退掉活动失败:", error);
        this.$message.error("退掉活动失败: " + (error.msg || "请稍后重试"));
      }
    },

    // 判断日期是否属于当前月份
    isCurrentMonth(dateString) {
      const date = new Date(dateString);
      const currentDate = new Date(this.calendarDate);
      
      return date.getMonth() === currentDate.getMonth() && 
             date.getFullYear() === currentDate.getFullYear();
    },

    // 隐藏多余的空行
    hideEmptyCalendarRows() {
      this.$nextTick(() => {
        const calendarTable = document.querySelector('.calendar-view .el-calendar-table tbody');
        if (calendarTable) {
          const rows = calendarTable.querySelectorAll('tr');
          rows.forEach(row => {
            const cells = row.querySelectorAll('td');
            const hasCurrentMonthCell = Array.from(cells).some(cell => 
              cell.classList.contains('is-current-month')
            );
            
            if (!hasCurrentMonthCell) {
              row.classList.add('hide-row');
            } else {
              row.classList.remove('hide-row');
            }
          });
        }
      });
    },

    // 强制设置日历格子高度
    forceCalendarDayHeight() {
      this.$nextTick(() => {
        const calendarDays = document.querySelectorAll('.el-calendar-table .el-calendar-day');
        calendarDays.forEach(day => {
          day.style.height = '120px';
          day.style.minHeight = '120px';
          day.style.maxHeight = '120px';
        });
      });
    }
  }
};
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  margin-left: 200px;
  background: #f5f7fa;
  padding: 0;
  min-height: 100vh;
  position: relative;
  width: calc(100% - 200px);
  overflow: hidden;
  box-sizing: border-box;
}

.view-tabs {
  flex: 1;
  margin: 0;
  background: transparent;
  backdrop-filter: none;
  border-radius: 0;
  padding: 0;
  box-shadow: none;
  border: none;
  overflow: visible;
  display: flex;
  flex-direction: column;
  width: 100%;
  box-sizing: border-box;
  height: auto;

  .el-tabs__header {
    margin: 0 0 15px 0;
  }

  .el-tabs__nav-wrap {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 8px;
    padding: 4px;
  }

  .el-tabs__nav {
    border: none;
  }

  .el-tabs__item {
    color: rgba(255, 255, 255, 0.8);
    border: none;
    background: transparent;
    border-radius: 6px;
    margin: 0 2px;
    transition: all 0.3s ease;

    &:hover {
      color: white;
      background: rgba(255, 255, 255, 0.1);
    }

    &.is-active {
      color: white;
      background: rgba(255, 255, 255, 0.2);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }

  .el-tabs__active-bar {
    display: none;
  }

  .el-tabs__content {
    flex: 1;
    overflow: visible;
    padding: 0;
    width: 100%;
    box-sizing: border-box;
    height: auto;
  }

}

.calendar-view {
  height: calc(100vh - 100px);
  overflow: visible;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 0;
  padding: 15px;
  box-shadow: none;
}

  .calendar-cell {
    height: calc(100% - 2px);
    width: calc(100% - 2px);
    max-height: 150px;
    overflow: hidden;
    padding: 2px; /* 减少内边距 */
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 8px;
    display: flex;
    flex-direction: column; /* 改为垂直布局 */
    box-sizing: border-box;
    position: relative;
    margin: 1px;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
      background: rgba(255, 255, 255, 0.95);
    }

    .date-header {
      font-weight: bold;
      margin: 0 0 2px 0; /* 添加底部间距 */
      width: 100%; /* 占满宽度 */
      text-align: center;
      padding: 2px 4px; /* 减少内边距 */
      line-height: 1.2;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 6px; /* 减少圆角 */
      font-size: 10px; /* 减少字体大小 */
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
      flex-shrink: 0; /* 防止被压缩 */
    }

    .events-container {
      position: relative;
      display: flex;
      flex-direction: column;
      gap: 1px; /* 减少间距 */
      flex: 1; /* 占据剩余空间 */
      min-width: 0;
      overflow-y: auto;
      width: 100%; /* 确保容器占满可用宽度 */
      box-sizing: border-box; /* 包含内边距在宽度计算中 */
      padding: 0; /* 移除内边距 */
      padding-right: 2px; /* 为滚动条留出空间 */
    }

    .calendar-event {
      display: flex;
      align-items: center;
      padding: 4px 8px; /* 增加内边距 */
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); /* 默认颜色 */
      border: none;
      border-radius: 6px; /* 增加圆角 */
      font-size: 11px; /* 稍微增加字体大小 */
      cursor: pointer;
      transition: all 0.3s ease;
      min-width: 0;
      height: 24px; /* 增加高度从16px到24px */
      overflow: hidden;
      color: white;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
      position: relative;
      width: 100%; /* 确保所有活动条都是100%宽度 */
      box-sizing: border-box; /* 包含内边距在宽度计算中 */
      margin-bottom: 2px; /* 增加底部间距 */
      flex-shrink: 0; /* 防止活动条被压缩 */

      /* 报名未开始 - 灰色 */
      &.status-not-started {
        background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
        box-shadow: 0 2px 8px rgba(149, 165, 166, 0.2);
      }

      /* 报名进行中 - 蓝色 */
      &.status-signup-active {
        background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
        box-shadow: 0 2px 8px rgba(52, 152, 219, 0.2);
      }

      /* 报名已截止 - 橙色 */
      &.status-signup-ended {
        background: linear-gradient(135deg, #e67e22 0%, #d35400 100%);
        box-shadow: 0 2px 8px rgba(230, 126, 34, 0.2);
      }

      /* 活动进行中 - 绿色 */
      &.status-activity-active {
        background: linear-gradient(135deg, #27ae60 0%, #229954 100%);
        box-shadow: 0 2px 8px rgba(39, 174, 96, 0.2);
      }

      /* 活动已结束 - 深灰色 */
      &.status-activity-ended {
        background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
        box-shadow: 0 2px 8px rgba(52, 73, 94, 0.2);
      }

      /* 未知状态 - 紫色（默认） */
      &.status-unknown {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
      }

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 100%);
        border-radius: 6px;
        pointer-events: none;
      }

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
        background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
      }

      /* 不同状态的hover效果 */
      &.status-not-started:hover {
        background: linear-gradient(135deg, #7f8c8d 0%, #6c7b7d 100%);
        box-shadow: 0 4px 16px rgba(149, 165, 166, 0.4);
      }

      &.status-signup-active:hover {
        background: linear-gradient(135deg, #2980b9 0%, #21618c 100%);
        box-shadow: 0 4px 16px rgba(52, 152, 219, 0.4);
      }

      &.status-signup-ended:hover {
        background: linear-gradient(135deg, #d35400 0%, #ba4a00 100%);
        box-shadow: 0 4px 16px rgba(230, 126, 34, 0.4);
      }

      &.status-activity-active:hover {
        background: linear-gradient(135deg, #229954 0%, #1e8449 100%);
        box-shadow: 0 4px 16px rgba(39, 174, 96, 0.4);
      }

      &.status-activity-ended:hover {
        background: linear-gradient(135deg, #2c3e50 0%, #1b2631 100%);
        box-shadow: 0 4px 16px rgba(52, 73, 94, 0.4);
      }

      &:active {
        transform: translateY(0);
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
      }

      .event-summary {
        display: flex;
        align-items: center;
        gap: 4px;
        min-width: 0;
        flex: 1;
        z-index: 1;
        width: 100%; /* 确保占满整个活动条宽度 */

        .event-name {
          font-weight: 600;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          flex: 1; /* 允许弹性增长，占据剩余空间 */
          font-size: 10px; /* 增加字体大小 */
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
          min-width: 0; /* 允许收缩 */
          margin-right: 6px; /* 增加右边距 */
        }

        .event-org {
          color: rgba(255, 255, 255, 0.8);
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          flex: 0 0 auto; /* 固定宽度，不伸缩 */
          font-size: 9px; /* 增加字体大小 */
          max-width: 45px; /* 增加最大宽度 */
          margin-right: 6px; /* 增加右边距 */
        }

        .detail-btn {
          padding: 2px 6px; /* 增加内边距 */
          font-size: 9px; /* 增加字体大小 */
          margin-left: auto;
          background: rgba(255, 255, 255, 0.2);
          border: 1px solid rgba(255, 255, 255, 0.3);
          color: white;
          border-radius: 4px; /* 增加圆角 */
          transition: all 0.2s ease;
          flex: 0 0 auto; /* 固定宽度，不伸缩 */
          white-space: nowrap; /* 防止按钮文字换行 */

          &:hover {
            background: rgba(255, 255, 255, 0.3);
            border-color: rgba(255, 255, 255, 0.5);
          }

          &.disabled-btn {
            background: rgba(255, 255, 255, 0.1);
            color: rgba(255, 255, 255, 0.5);
            cursor: not-allowed;
            border-color: rgba(255, 255, 255, 0.1);
          }
        }
      }

      .event-time-range {
        display: none; /* 隐藏时间范围以保持一行紧凑显示 */
      }
    }
  }

  /* 覆盖 Element UI 的日历格子高度 - 使用更高优先级 */
  .calendar-view .el-calendar-table .el-calendar-day {
    height: var(--calendar-day-height, 120px) !important;
    padding: 8px !important;
    min-height: var(--calendar-day-height, 120px) !important;
    max-height: var(--calendar-day-height, 120px) !important;
  }

  /* 强制覆盖所有可能的样式 */
  .calendar-view .el-calendar-table td .el-calendar-day {
    height: 120px !important;
    min-height: 120px !important;
    max-height: 120px !important;
  }

/* 日历视图内显示"详细/已截止"按钮 */
.calendar-view .detail-btn {
  display: inline-block;
}

/* 美化今天的日期 */
.calendar-view .el-calendar__day.is-today {
  .calendar-cell {
    background: linear-gradient(135deg, rgba(255, 193, 7, 0.1) 0%, rgba(255, 193, 7, 0.05) 100%);
    border: 2px solid #ffc107;
    box-shadow: 0 4px 16px rgba(255, 193, 7, 0.2);
    
    .date-header {
      background: linear-gradient(135deg, #ffc107 0%, #ff8f00 100%);
      box-shadow: 0 2px 8px rgba(255, 193, 7, 0.4);
    }
  }
}

/* 美化周末日期 */
.calendar-view .el-calendar__day.is-weekend {
  .calendar-cell {
    background: linear-gradient(135deg, rgba(255, 87, 34, 0.05) 0%, rgba(255, 87, 34, 0.02) 100%);
  }
}

/* 美化月份标题 */
.calendar-view .el-calendar__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
  padding: 15px 20px;
  margin-bottom: 0;
  
  .el-calendar__title {
    font-size: 18px;
    font-weight: 600;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  }
  
  .el-calendar__button-group {
    .el-button {
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: white;
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
        border-color: rgba(255, 255, 255, 0.5);
      }
    }
  }
}

/* 美化星期标题 */
.calendar-view .el-calendar__body {
  .el-calendar-table {
    thead {
      th {
        background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
        color: #495057;
        font-weight: 600;
        border: none;
        padding: 12px 0;
      }
    }
  }
}

/* 隐藏非当月日期的单元格 */
.calendar-view .empty-cell {
  visibility: hidden;
  pointer-events: none;
}

/* 隐藏非当月日期 */
.calendar-view .el-calendar-table td:not(.is-current-month) {
  display: none;
}

/* 确保当月日期正常显示 */
.calendar-view .el-calendar-table td.is-current-month {
  display: table-cell;
}

/* 隐藏多余的空行 - 通过JavaScript动态处理 */
.calendar-view .el-calendar-table tbody tr.hide-row {
  display: none !important;
}

/* 确保日历表格完整显示 */
.calendar-view .el-calendar__body {
  overflow: visible;
  height: 600px;
  padding: 0;
}

.calendar-view .el-calendar-table {
  table-layout: fixed;
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

.calendar-view .el-calendar-table tbody {
  display: table-row-group;
}

.calendar-view .el-calendar-table tr {
  display: table-row;
  height: 155px;
}

.calendar-view .el-calendar-table td {
  display: table-cell;
  vertical-align: top;
  height: 155px;
  padding: 0;
  position: relative;
  box-sizing: border-box;
  overflow: hidden;
}

.calendar-view .el-calendar-table td .cell {
  height: 100%;
  width: 100%;
  padding: 0;
  margin: 0;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

/* 确保日历单元格完全包含在表格单元格内 */
.calendar-view .el-calendar-table td * {
  box-sizing: border-box;
}

/* 活动状态图例样式 */
.activity-legend {
  position: absolute;
  bottom: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  min-width: 200px;
  z-index: 10;

  .legend-title {
    margin: 0 0 12px 0;
    font-size: 14px;
    font-weight: 600;
    color: #2c3e50;
    text-align: center;
  }

  .legend-items {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .legend-item {
    display: flex;
    align-items: center;
    gap: 10px;

    .legend-color {
      width: 16px;
      height: 16px;
      border-radius: 4px;
      flex-shrink: 0;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

      /* 报名未开始 - 灰色 */
      &.status-not-started {
        background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
      }

      /* 报名进行中 - 蓝色 */
      &.status-signup-active {
        background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
      }

      /* 报名已截止 - 橙色 */
      &.status-signup-ended {
        background: linear-gradient(135deg, #e67e22 0%, #d35400 100%);
      }

      /* 活动进行中 - 绿色 */
      &.status-activity-active {
        background: linear-gradient(135deg, #27ae60 0%, #229954 100%);
      }

      /* 活动已结束 - 深灰色 */
      &.status-activity-ended {
        background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%);
      }
    }

    .legend-text {
      font-size: 12px;
      color: #495057;
      font-weight: 500;
    }
  }
}


/* 活动详情弹窗样式 */
.activity-dialog {
  .el-dialog {
    border-radius: 12px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  }

  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 12px 12px 0 0;
    padding: 20px 24px;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__close {
      color: white;
      font-size: 20px;

      &:hover {
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
    background: #f8f9fa;
  }

  .el-dialog__footer {
    background: #f8f9fa;
    border-radius: 0 0 12px 12px;
    padding: 16px 24px;
    text-align: right;
  }
}

.activity-detail {
  .detail-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid rgba(102, 126, 234, 0.1);

    h2 {
      margin: 0 15px 0 0;
      font-size: 24px;
      font-weight: 600;
      color: #2c3e50;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    }

    .status-tag {
      border-radius: 20px;
      padding: 4px 12px;
      font-weight: 500;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }

  .detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;

    .detail-item {
      display: flex;
      align-items: center;
      padding: 12px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 8px;
      border: 1px solid rgba(255, 255, 255, 0.3);
      transition: all 0.3s ease;

      &:hover {
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

        i {
          color: #667eea;
          font-size: 16px;
        }
      }

      .detail-value {
        flex: 1;
        font-weight: 500;

        .capacity-high {
          color: #e74c3c;
          font-weight: 600;
          background: rgba(231, 76, 60, 0.1);
          padding: 2px 8px;
          border-radius: 12px;
        }

        .capacity-medium {
          color: #f39c12;
          font-weight: 600;
          background: rgba(243, 156, 18, 0.1);
          padding: 2px 8px;
          border-radius: 12px;
        }

        .capacity-low {
          color: #27ae60;
          font-weight: 600;
          background: rgba(39, 174, 96, 0.1);
          padding: 2px 8px;
          border-radius: 12px;
        }
      }
    }
  }

  .detail-section-content {
    margin: 20px 0;

    .section-title {
      display: flex;
      align-items: center;
      color: #2c3e50;
      margin-bottom: 12px;
      font-weight: 600;
      font-size: 16px;

      i {
        margin-right: 8px;
        color: #667eea;
        font-size: 18px;
      }
    }

    .section-content {
      line-height: 1.6;
      padding: 16px 20px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 8px;
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: #495057;
      font-weight: 500;
    }
  }

  .signup-status {
    margin-top: 24px;
    display: flex;
    justify-content: center;

    .signup-button {
      width: 200px;
      height: 44px;
      font-size: 16px;
      font-weight: 600;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 22px;
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
      }

      &:active {
        transform: translateY(0);
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
      }
    }

    .cancel-button {
      width: 200px;
      height: 44px;
      font-size: 16px;
      font-weight: 600;
      background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
      border: none;
      border-radius: 22px;
      box-shadow: 0 4px 16px rgba(231, 76, 60, 0.3);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(231, 76, 60, 0.4);
        background: linear-gradient(135deg, #c0392b 0%, #a93226 100%);
      }

      &:active {
        transform: translateY(0);
        box-shadow: 0 2px 8px rgba(231, 76, 60, 0.3);
      }
    }

    .signup-alert {
      margin-top: 20px;
      border-radius: 8px;
      border: none;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }
}

/* 细节优化：滚动条美化、过渡动画、响应式布局与可达性增强 */
/* 滚动条美化（仅影响事件列表，不影响全局） */
.calendar-view .events-container::-webkit-scrollbar {
  width: 4px;
}
.calendar-view .events-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}
.calendar-view .events-container::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.4);
  border-radius: 2px;
  transition: background 0.3s ease;
}
.calendar-view .events-container:hover::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.6);
}

/* 入场动画，使元素更灵动 */
@keyframes fadeSlideIn {
  from { opacity: 0; transform: translateY(4px); }
  to   { opacity: 1; transform: translateY(0); }
}
.calendar-view .calendar-event {
  animation: fadeSlideIn 0.2s ease both;
}

/* 焦点可达性优化（键盘导航时有清晰的可见焦点） */
.calendar-view .detail-btn:focus,
.detail-section .signup-button:focus {
  outline: 2px solid rgba(102, 126, 234, 0.6);
  outline-offset: 2px;
}

/* 标签激活时的轻微放大反馈 */
.view-tabs .el-tabs__item.is-active {
  transform: translateY(-1px);
}

/* 响应式：较小屏幕下信息布局更紧凑 */
@media (max-width: 1200px) {
  .activity-detail .detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .app-container {
    margin-left: 0;
    width: 100%;
  }
  .view-tabs { padding: 10px; }
  .calendar-view { 
    padding: 10px;
    height: calc(100vh - 150px);
  }
  
  .activity-dialog .el-dialog {
    width: 95% !important;
    margin: 0 auto;
  }
  
  .activity-detail .detail-header h2 { 
    font-size: 20px; 
  }
  
  .activity-detail .signup-status .signup-button,
  .activity-detail .signup-status .cancel-button {
    width: 100%;
  }
  
  /* 移动端图例样式调整 */
  .activity-legend {
    position: relative;
    bottom: auto;
    right: auto;
    margin: 20px auto 0;
    width: 90%;
    max-width: 300px;
  }
}

/* 覆盖 ActivityBooking 组件的表格高度和滚动 */
::v-deep .booking-container .modern-table {
  max-height: calc(100vh - 400px) !important;
  height: auto !important;
  overflow: hidden !important;
  overflow-y: auto !important;
}

/* 确保分页组件正常显示 */
::v-deep .booking-container .custom-pagination {
  display: flex !important;
  visibility: visible !important;
  opacity: 1 !important;
  position: relative !important;
  z-index: 999 !important;
  margin-top: 20px !important;
  padding: 20px 0 !important;
  background: white !important;
  border-top: 1px solid #e4e7ed !important;
}

/* 强制显示分页组件内部容器 */
::v-deep .booking-container .custom-pagination .pagination-container {
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
  margin: 0 !important;
}
</style>



