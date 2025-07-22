<template>
  <div class="app-container">
    <!-- 视图切换标签 -->
    <el-tabs v-model="activeView" class="view-tabs">
      <!-- 日历视图 -->
      <el-tab-pane label="日历视图" name="calendar">
        <el-calendar v-model="calendarDate" class="calendar-view">
          <template #dateCell="{ data }">
            <div class="calendar-cell">
              <div class="date-header">
                <span>{{ data.day.split('-')[2] }}</span>
              </div>
              <div class="events-container">
                <div
                  v-for="(event, index) in getDateEvents(data.day)"
                  :key="index"
                  class="calendar-event"
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
          </template>
        </el-calendar>
      </el-tab-pane>

      <!-- 甘特图视图 -->
      <el-tab-pane label="甘特图视图" name="gantt">
        <div class="gantt-container">
          <div class="gantt-header">
            <div class="gantt-timeline">
              <div
                v-for="day in ganttDays"
                :key="day.date"
                :class="['gantt-date-cell', { 'today': day.isToday }]"
              >
                <div class="date">{{ day.day }}</div>
                <div class="weekday">{{ day.weekday }}</div>
              </div>
            </div>
          </div>

          <div class="gantt-body">
            <div
              v-for="(activity, index) in filteredActivities"
              :key="index"
              class="gantt-activity"
            >
              <div class="activity-label">
                <span class="activity-name">{{ activity.activityName }}</span>
                <span class="activity-org">({{ activity.organizer }})</span>
              </div>

              <div class="activity-bars">
                <div
                  v-for="day in ganttDays"
                  :key="day.date"
                  class="gantt-cell"
                >
                  <div
                    v-if="isActivityOnDate(activity, day.date)"
                    class="activity-bar"
                    :class="{
                      'not-started': !isActivityStarted(activity, day.date),
                      'in-progress': isActivityInProgress(activity, day.date)
                    }"
                    @click="handleEventClick(activity)"
                  >
                    <el-button
                      v-if="isBeforeToday(activity.activityDeadline)"
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
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 详情/报名区域 -->
    <div class="detail-section">
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
                {{ selectedActivity.activityCapacity }}/{{ selectedActivity.activityTotalCapacity }}人
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

        <!-- 报名按钮 -->
        <!-- 报名按钮 -->
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

          <el-alert
            v-if="selectedActivity.isBooked"
            title="您已成功报名该活动"
            type="success"
            :closable="false"
            class="signup-alert"
          >
            报名信息已提交，无法取消报名
          </el-alert>
        </div>
      </div>

      <div v-else class="no-activity-selected">
        <i class="el-icon-info"></i>
        <p>请从左侧选择活动查看详情</p>
      </div>
    </div>
  </div>
</template>


<script>
import { listActivities, signUpCapacity } from "@/api/system/activities";
import {addBooking, checkBookingSimple} from "@/api/system/bookings";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "ActivityDashboard",
  data() {
    return {
      activeView: 'calendar', // 当前视图: calendar/gantt
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

      // 甘特图相关
      ganttDays: [],
      ganttDaysCount: 7 // 甘特图显示7天
    };
  },
  computed: {
    // 计算过滤后的活动列表
    filteredActivities() {
      return this.activityList.filter(activity =>
        this.isActivityInRange(activity, this.ganttDays[0]?.date, this.ganttDays[this.ganttDays.length - 1]?.date)
      );
    },

    // 显示报名按钮的条件
    showSignUpButton() {
      if (!this.selectedActivity) return false;
      return this.getActivityStatusText(this.selectedActivity) === "报名进行中" &&
        !this.selectedActivity.isBooked && // 使用活动对象的isBooked属性
        this.selectedActivity.activityCapacity > 0;
    },

  },
  created() {
    this.fetchActivities();
    this.generateGanttDays();
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

    // 获取容量样式
    getCapacityClass(activity) {
      const percentage = activity.activityCapacity / activity.activityTotalCapacity;
      if (percentage >= 0.8) return 'capacity-high';
      if (percentage >= 0.5) return 'capacity-medium';
      return 'capacity-low';
    },

    // 字符串截断
    truncate(str, maxLength) {
      if (!str) return '';
      return str.length > maxLength ? str.substring(0, maxLength) + '...' : str;
    },

    // 生成甘特图日期范围
    generateGanttDays() {
      const days = [];
      const today = new Date();

      for (let i = 0; i < this.ganttDaysCount; i++) {
        const date = new Date();
        date.setDate(today.getDate() + i);

        days.push({
          date: date.toISOString().split('T')[0],
          day: date.getDate(),
          weekday: ['日', '一', '二', '三', '四', '五', '六'][date.getDay()],
          isToday: i === 0
        });
      }

      this.ganttDays = days;
    },

    // 检查活动是否在日期范围内
    isActivityInRange(activity, startDate, endDate) {
      const activityStart = new Date(activity.startTime);
      const activityEnd = new Date(activity.endTime);
      const rangeStart = new Date(startDate);
      const rangeEnd = new Date(endDate);
      rangeEnd.setHours(23, 59, 59, 999);

      return (activityStart >= rangeStart && activityStart <= rangeEnd) ||
        (activityEnd >= rangeStart && activityEnd <= rangeEnd) ||
        (activityStart <= rangeStart && activityEnd >= rangeEnd);
    },

    // 检查活动是否在指定日期
    isActivityOnDate(activity, dateString) {
      const date = new Date(dateString);
      const start = new Date(activity.startTime);
      const end = new Date(activity.endTime);

      start.setHours(0, 0, 0, 0);
      end.setHours(23, 59, 59, 999);

      return date >= start && date <= end;
    },

    // 活动是否已开始
    isActivityStarted(activity, dateString) {
      const date = new Date(dateString);
      const activityDate = new Date(activity.startTime);
      return date >= activityDate;
    },

    // 活动是否进行中
    isActivityInProgress(activity, dateString) {
      const date = new Date(dateString);
      const start = new Date(activity.startTime);
      const end = new Date(activity.endTime);
      return date >= start && date <= end;
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

      } catch (error) {
        console.error("报名失败:", error);
        this.$message.error("报名失败: " + (error.msg || "请稍后重试"));
      }
    },

    // 处理事件点击
    handleEventClick(activity) {
      this.selectedActivity = { ...activity };
    },
  }
};
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  margin-left: 200px;
}

.view-tabs {
  flex: 1;
  margin-bottom: 20px;
}

.calendar-view {
  height: 600px;

  .calendar-cell {
    height: 100px;
    overflow-y: auto;
    padding: 5px;
    border: 1px solid #eee;
    border-radius: 4px;

    .date-header {
      font-weight: bold;
      margin-bottom: 5px;
    }

    .events-container {
      max-height: 80px;
      overflow-y: auto;
    }

    .calendar-event {
      padding: 4px;
      margin-bottom: 4px;
      background-color: #f5f7fa;
      border-radius: 4px;
      font-size: 12px;
      cursor: pointer;
      transition: background-color 0.3s;

      &:hover {
        background-color: #e4e7ed;
      }

      .event-summary {
        display: flex;
        justify-content: space-between;

        .event-name {
          font-weight: bold;
        }

        .event-org {
          flex: 1;
          margin: 0 5px;
          color: #666;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .detail-btn {
          padding: 2px 5px;
          font-size: 12px;

          &.disabled-btn {
            color: #999;
            cursor: not-allowed;
          }
        }
      }

      .event-time-range {
        font-size: 11px;
        color: #409eff;
        margin-top: 2px;
      }
    }
  }
}

.gantt-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;

  .gantt-header {
    background: #f5f7fa;

    .gantt-timeline {
      display: flex;
      border-bottom: 1px solid #ebeef5;

      .gantt-date-cell {
        flex: 1;
        text-align: center;
        padding: 8px 0;
        border-right: 1px solid #ebeef5;

        &:last-child {
          border-right: none;
        }

        &.today {
          background-color: #ecf5ff;
          border-bottom: 2px solid #409eff;
        }

        .date {
          font-weight: bold;
          font-size: 18px;
        }

        .weekday {
          color: #606266;
          font-size: 12px;
        }
      }
    }
  }

  .gantt-body {
    .gantt-activity {
      display: flex;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .activity-label {
        width: 200px;
        padding: 10px;
        background: #f9fafc;
        border-right: 1px solid #ebeef5;
        display: flex;
        flex-direction: column;
        justify-content: center;

        .activity-name {
          font-weight: bold;
          margin-bottom: 4px;
        }

        .activity-org {
          font-size: 12px;
          color: #666;
        }
      }

      .activity-bars {
        flex: 1;
        display: flex;

        .gantt-cell {
          flex: 1;
          border-right: 1px solid #ebeef5;
          position: relative;

          &:last-child {
            border-right: none;
          }

          .activity-bar {
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #ecf5ff;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;

            &:hover {
              background-color: #d9ecff;
            }

            &.in-progress {
              background-color: #f0f9eb;
              border-top: 2px solid #67c23a;
              border-bottom: 2px solid #67c23a;

              &:hover {
                background-color: #e1f3d8;
              }
            }

            &.not-started {
              background-color: #f5f5f5;
              opacity: 0.7;

              &:hover {
                background-color: #e6e6e6;
              }
            }

            .detail-btn {
              font-size: 12px;
              padding: 4px 8px;

              &.disabled-btn {
                color: #999;
                cursor: not-allowed;
              }
            }
          }
        }
      }
    }
  }
}

.detail-section {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 20px;
  background: #fff;
  margin-top: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .activity-detail {
    .detail-header {
      display: flex;
      align-items: center;
      margin-bottom: 20px;

      h2 {
        margin: 0 15px 0 0;
        font-size: 22px;
      }
    }

    .detail-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 15px;

      .detail-item {
        display: flex;
        align-items: center;

        .detail-label {
          font-weight: bold;
          width: 120px;
          color: #606266;
        }

        .detail-value {
          flex: 1;

          .capacity-high {
            color: #f56c6c;
            font-weight: bold;
          }

          .capacity-medium {
            color: #e6a23c;
          }

          .capacity-low {
            color: #67c23a;
          }
        }
      }
    }

    .detail-section-content {
      margin: 20px 0;

      .section-title {
        display: flex;
        align-items: center;
        color: #303133;
        margin-bottom: 10px;

        i {
          margin-right: 8px;
        }
      }

      .section-content {
        line-height: 1.6;
        padding: 10px 15px;
        background: #f9f9f9;
        border-radius: 4px;
      }
    }

    .signup-form {
      margin-top: 30px;
      padding: 20px;
      border: 1px solid #ebeef5;
      border-radius: 4px;

      .form-title {
        border-left: 4px solid #409eff;
        padding-left: 10px;
        margin: 0 0 20px 0;
      }
    }

    .signup-status {
      margin-top: 30px;
      display: flex;
      justify-content: center;

      .signup-button {
        width: 200px;
        height: 40px;
        font-size: 16px;
      }

      .signup-alert {
        margin-top: 20px;
      }
    }
  }

  .no-activity-selected {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 200px;
    color: #909399;

    i {
      font-size: 50px;
      margin-bottom: 15px;
    }

    p {
      font-size: 16px;
    }
  }
}
</style>
