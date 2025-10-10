<template>
  <div class="app-container">
    <!-- 管理员界面：显示活动管理 -->
    <div v-if="isAdmin" class="admin-home-container">
      <ActivityManager :isHomeMode="true" />
    </div>

    <!-- 学生界面：显示日历和活动报名 -->
    <div v-else>
      <!-- 视图切换标签 -->
      <el-tabs v-model="activeView" class="view-tabs">
        <!-- 日历视图 -->
        <el-tab-pane label="日历视图" name="calendar">
          <!-- 自定义日历头部 -->
          <div class="custom-calendar-header">
            <div class="header-left">
              <span class="calendar-title">{{ currentMonthTitle }} - {{ currentAcademy || '加载中...' }}</span>
            </div>
            <div class="header-right">
              <el-button-group>
                <el-button size="mini" @click="prevMonth">上个月</el-button>
                <el-button size="mini" @click="goCurrentMonth">本月</el-button>
                <el-button size="mini" @click="nextMonth">下个月</el-button>
              </el-button-group>
            </div>
          </div>

          <el-calendar
            v-model="calendarDate"
            class="calendar-view"
            style="--calendar-day-height: 120px;"
            :range="null"
          >
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
                    :class="['calendar-event', 'status-' + getActivityStatusType(event), { 'booked': event.isBooked }]"
                    @click="handleEventClick(event)"
                  >
                    <div class="event-summary">
                      <span class="event-name">{{ truncate(event.activityName, 10) }}</span>
                      <span class="event-org">({{ truncate(event.organizer, 5) }})</span>
                      <el-button
                        v-if="getActivityStatusType(event) === 'signup-active'"
                        type="text"
                        size="mini"
                        class="detail-btn"
                      >
                        详细
                      </el-button>
                      <el-button
                        v-else-if="getActivityStatusType(event) === 'not-started'"
                        type="text"
                        size="mini"
                        disabled
                        class="detail-btn disabled-btn"
                      >
                        未开始
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
                <div class="legend-color status-booked"></div>
                <span class="legend-text">已报名</span>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 活动报名视图 -->
        <el-tab-pane label="活动报名" name="booking">
          <div v-if="activeView === 'booking'">
            <ActivityBooking ref="activityBooking" @booking-updated="handleBookingUpdated" />
          </div>
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
            <div class="status-tags">
              <el-tag :type="getActivityStatusTag(selectedActivity)" size="medium" class="status-tag">
                {{ getActivityStatusText(selectedActivity) }}
              </el-tag>
              <el-tag :type="getSignStatusTag(selectedActivity)" size="medium" effect="light" class="sign-tag">
                {{ getSignStatusText(selectedActivity) }}
              </el-tag>
            </div>
          </div>

          <el-divider></el-divider>

          <div class="detail-grid">
            <div class="detail-item">
              <div class="detail-label"><i class="el-icon-location"></i> 活动地点：</div>
              <div class="detail-value">{{ selectedActivity.activityLocation }}</div>
            </div>
            <div class="detail-item">
              <div class="detail-label"><i class="el-icon-office-building"></i> 活动类型：</div>
              <div class="detail-value">{{ getActivityTypeName(selectedActivity.activityType) }}</div>
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
            <div class="section-content rich-text-content" v-html="selectedActivity.activityDescription || '暂无描述信息'"></div>
          </div>

          <!-- 活动图片展示 -->
          <div class="detail-section-content" v-if="selectedActivity.pictureUrl">
            <h4 class="section-title"><i class="el-icon-picture"></i> 活动图片</h4>
            <div class="section-content">
              <div class="activity-image-container">
                <el-image
                  :src="getActivityImageUrl(selectedActivity.pictureUrl)"
                  :preview-src-list="[getActivityImageUrl(selectedActivity.pictureUrl)]"
                  fit="cover"
                  class="activity-image"
                />
              </div>
            </div>
          </div>

          <!-- 报名/取消按钮 -->
          <div class="signup-status">
            <!-- 取消限制提示 -->
            <div v-if="exceedCancelLimit && getSignStatusText(selectedActivity) === '可报名'" class="cancel-limit-info">
              <el-alert
                title="报名限制"
                type="warning"
                :closable="false"
                description="本月取消次数已达上限（3次），无法报名新活动，但仍可取消已报名的活动"
                show-icon
                class="signup-alert"
              />
            </div>

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

            <!-- 剩余取消次数提示 -->
            <div v-if="!exceedCancelLimit && remainingCancels < 3" class="limit-tip">
              <i class="el-icon-info"></i>
              本月还可取消 {{ remainingCancels }} 次（每月最多3次，取消次数限制只影响新活动报名）
            </div>

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

      <!-- 图片预览对话框 -->
      <el-dialog
        title="图片预览"
        :visible.sync="imagePreviewVisible"
        width="60%"
        append-to-body
        class="image-preview-dialog">
        <div class="preview-container">
          <img :src="previewImageUrl" class="preview-image" />
        </div>
      </el-dialog>
    </div> <!-- 学生界面结束 -->
  </div>
</template>

<script>
import { listActivities, signUpCapacity, cancelSignUpCapacity, getActivities } from "@/api/system/activities";
import {addBooking, checkBookingSimple, deleteBookingsByActivityAndStudent} from "@/api/system/bookings";
import ActivityManager from "@/views/Activity/ActivityManager.vue";
import { getStudent } from "@/api/system/student";
import { parseTime } from "@/utils/ruoyi";
import ActivityBooking from "./Activity/ActivityBooking.vue";
import { recordCancel, checkCancelLimit, getCancelCount } from "@/api/system/userLimit";
import { getServerTime } from "@/api/common/time";

export default {
  name: "ActivityDashboard",
  components: {
    ActivityBooking,
    ActivityManager
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
      // 图片预览相关
      imagePreviewVisible: false,
      previewImageUrl: '',

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
      // 当前学生书院信息
      currentAcademy: '',
      // 实时同步相关
      isSyncing: false, // 是否正在同步数据

      // 取消限制相关
      remainingCancels: 3, // 剩余可取消次数
      // 服务器时间
      serverTime: null,
    };
  },
  computed: {
    // 判断是否为管理员
    isAdmin() {
      const userName = this.$store.state.user.name;
      return userName && userName >= '10000' && userName <= '10007';
    },

    // 动态页面标题
    pageTitle() {
      return this.isAdmin ? '活动管理' : '活动预约';
    },
    currentMonthTitle() {
      const d = new Date(this.calendarDate);
      return `${d.getFullYear()}年${(d.getMonth() + 1).toString().padStart(2, '0')}月`;
    },

    // 是否超过取消限制
    exceedCancelLimit() {
      return this.remainingCancels <= 0;
    },

    // 显示报名按钮的条件
    showSignUpButton() {
      if (!this.selectedActivity) return false;

      // 检查是否超过取消限制
      if (this.exceedCancelLimit) {
        return false;
      }

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

    // 显示取消报名按钮的条件 - 不受取消次数限制
    showCancelButton() {
      if (!this.selectedActivity) return false;
      const status = this.getActivityStatusText(this.selectedActivity);
      // 只要已报名且状态是"报名进行中"或"报名未开始"，就可以取消，不受取消次数限制
      return this.selectedActivity.isBooked &&
        (status === "报名进行中" || status === "报名未开始");
    },
  },
  created() {
    this.getServerTime();
    this.getCurrentStudentInfo();
    // 初始加载取消限制信息
    this.loadCancelLimitInfo();
  },
  mounted() {
    this.hideEmptyCalendarRows();
    this.forceCalendarDayHeight();
    this.hideTodayButton();
  },
  watch: {

    calendarDate() {
      this.$nextTick(() => {
        this.hideEmptyCalendarRows();
        this.forceCalendarDayHeight();
        this.hideTodayButton();
      });
    },
    activeView(newView) {
      // 当切换到活动报名视图时，清除选中的活动并刷新数据
      if (newView === 'booking') {
        this.selectedActivity = null;
        // 延迟执行，确保组件已经渲染
        this.$nextTick(() => {
          if (this.$refs.activityBooking) {
            this.$refs.activityBooking.getList();
          }
        });
      }
    }
  },
  methods: {
    /** 获取服务器时间 */
    async getServerTime() {
      try {
        const response = await getServerTime();
        if (response.code === 200) {
          this.serverTime = new Date(response.data);
        } else {
          // 如果获取服务器时间失败，使用本地时间作为备用
          this.serverTime = new Date();
        }
      } catch (error) {
        // 如果获取服务器时间失败，使用本地时间作为备用
        this.serverTime = new Date();
      }
    },

    // 加载取消限制信息
    async loadCancelLimitInfo() {
      try {
        const response = await getCancelCount(this.$store.state.user.name);
        console.log('获取取消次数响应:', response);

        // 检查响应数据结构
        if (response && response.code === 200) {
          // 后端返回的数据直接在根对象上，不在data属性中
          if (response.data !== undefined) {
            const cancelCount = response.data;
            this.remainingCancels = Math.max(0, 3 - cancelCount);
            console.log(`更新取消限制信息: 已取消${cancelCount}次，剩余${this.remainingCancels}次`);
          } else {
            console.warn('获取取消次数响应缺少data字段:', response);
            this.remainingCancels = 3;
          }
        } else {
          console.warn('获取取消次数响应格式异常:', response);
          this.remainingCancels = 3;
        }
      } catch (error) {
        console.error('加载取消限制信息失败:', error);
        this.remainingCancels = 3;
      }
    },

    // 获取当前学生信息
    async getCurrentStudentInfo() {
      try {
        const response = await getStudent(this.$store.state.user.name);
        console.log('学生信息API响应:', response);

        if (response && response.studentInfo) {
          this.currentAcademy = response.studentInfo.academy;
          console.log('当前学生书院:', this.currentAcademy);
          // 获取学生信息后，根据书院获取活动
          this.fetchActivities();
        } else {
          console.error('获取学生信息失败，响应中没有studentInfo:', response);
          this.currentAcademy = '未知';
          // 即使获取失败，也尝试获取活动
          this.fetchActivities();
        }
      } catch (error) {
        console.error('获取学生信息异常:', error);
        this.currentAcademy = '未知';
        // 即使获取失败，也尝试获取活动
        this.fetchActivities();
      }
    },

    // 检查取消限制
    async checkCancelLimit() {
      try {
        const response = await checkCancelLimit(this.$store.state.user.name);
        console.log('检查取消限制响应:', response);

        // 检查响应数据结构
        if (response && response.code === 200) {
          // 后端返回的数据直接在根对象上，不在data属性中
          if (response.cancelCount !== undefined && response.canCancel !== undefined) {
            return response.canCancel;
          } else {
            console.warn('检查取消限制响应缺少必要字段:', response);
            return true; // 默认允许取消
          }
        } else {
          console.warn('检查取消限制响应格式异常:', response);
          return true; // 默认允许取消
        }
      } catch (error) {
        console.error('检查取消限制失败:', error);
        return true; // 默认允许取消，避免影响正常流程
      }
    },

    // 获取是否可以报名
    async getCanSignUp(row) {
      // 如果不是可报名状态，直接返回true（不检查限制）
      if (this.getSignStatusText(row) !== '可报名') {
        return true;
      }

      try {
        const response = await getCancelCount(this.$store.state.user.name);
        console.log('获取取消次数响应(报名检查):', response);

        // 检查响应数据结构
        if (response && response.code === 200) {
          // 后端返回的数据直接在根对象上，不在data属性中
          if (response.data !== undefined) {
            const cancelCount = response.data;
            return cancelCount < 3; // 取消次数小于3次才允许报名
          } else {
            console.warn('获取取消次数响应缺少data字段:', response);
            return true; // 默认允许报名
          }
        } else {
          console.warn('获取取消次数响应格式异常:', response);
          return true; // 默认允许报名
        }
      } catch (error) {
        console.error('检查取消限制失败:', error);
        return true; // 默认允许报名，避免影响正常流程
      }
    },

    prevMonth() {
      const d = new Date(this.calendarDate);
      d.setMonth(d.getMonth() - 1);
      this.calendarDate = d;
    },
    nextMonth() {
      const d = new Date(this.calendarDate);
      d.setMonth(d.getMonth() + 1);
      this.calendarDate = d;
    },
    goToday() {
      this.calendarDate = new Date();
    },
    goCurrentMonth() {
      this.calendarDate = new Date();
    },
    formatCalendarTitle(date) {
      const d = new Date(date);
      return `${d.getFullYear()}年${(d.getMonth() + 1).toString().padStart(2, '0')}月`;
    },
    getActivityTypeName(activityType) {
      const typeMap = {
        '1': '人格塑造与价值引领活动类',
        '2': '知识融合与思维进阶活动类',
        '3': '能力锻造与实践创新活动类',
        '4': '社会责任与领军意识活动类'
      };
      return typeMap[activityType] || activityType;
    },

    // 获取活动列表
    async fetchActivities() {
      this.loading = true;
      try {
        // 根据当前学生的书院过滤活动
        const params = {
          ...this.queryParams,
          organizer: this.currentAcademy // 只获取当前学生书院的活动
        };

        const response = await listActivities(params);
        if (response.code === 200) {
          this.activityList = response.rows;

          // 计算活动状态并初始化isBooked属性
          // 使用服务器时间，如果服务器时间不可用则使用本地时间
          const now = this.serverTime || new Date();
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

      // 检查报名状态
      const checkPromises = this.activityList.map(activity =>
        checkBookingSimple(activity.activityId, this.$store.state.user.name).then(res => {
          activity.isBooked = res.data.isBooked;
        })
      );
      await Promise.all(checkPromises);
    },

    // 计算活动状态
    calculateStatus(activity, now) {
      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const currentTime = this.serverTime || now;
      const startTime = new Date(activity.startTime);
      const endTime = new Date(activity.endTime);
      const deadline = activity.activityDeadline ? new Date(activity.activityDeadline) : null;

      if (currentTime > endTime) return '已结束';
      if (currentTime >= startTime && currentTime <= endTime) return '进行中';
      if (deadline && currentTime > deadline) return '已截止';
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
      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const now = this.serverTime || new Date();
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

    // 获取报名状态文本
    getSignStatusText(row) {
      // 简化逻辑，只检查是否已报名
      if (row.isBooked) {
        return "已报名";
      }

      const status = this.getActivityStatusText(row);
      const hasCapacity = row.activityCapacity > 0;

      if (["报名进行中"].includes(status) && hasCapacity) {
        return "可报名";
      }

      return "不可报名";
    },

    // 获取报名状态标签类型
    getSignStatusTag(activity) {
      const status = this.getSignStatusText(activity);
      switch (status) {
        case "可报名": return "success";
        case "已报名": return "warning";
        case "不可报名": return "danger";
        default: return "info";
      }
    },

    // 获取活动状态类型（用于颜色判断）
    getActivityStatusType(activity) {
      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const now = this.serverTime || new Date();
      const deadline = new Date(activity.activityDeadline);
      const activityStart = new Date(activity.activityStart);

      if (now < activityStart) return "not-started"; // 报名未开始
      if (now < deadline && now >= activityStart) return "signup-active"; // 报名进行中
      if (now >= deadline) return "signup-ended"; // 报名已截止
      return "signup-ended"; // 默认显示为报名已截止
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
    async handleSignUp() {
      try {
        // 先检查取消限制
        const canSignUp = await this.getCanSignUp(this.selectedActivity);
        if (!canSignUp) {
          this.$message.warning('本月取消次数已达上限，无法报名新活动');
          return;
        }

        this.$confirm('确定要报名该活动吗？', '报名确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.submitSignUp();
        }).catch(() => {
          this.$message.info('已取消报名');
        });
      } catch (error) {
        console.error('检查报名限制失败:', error);
        this.$message.error('检查报名限制失败，请稍后重试');
      }
    },

    // 获取最新活动信息
    async getLatestActivityInfo(activityId) {
      try {
        const response = await getActivities(activityId);
        console.log('获取最新活动信息响应:', response);
        console.log('活动组织者:', response.data?.organizer);
        return response.data;
      } catch (error) {
        console.error("获取活动信息失败:", error);
        return null;
      }
    },

    // 提交报名
    async submitSignUp() {
      try {
        // 0. 先检查是否已经报名过
        const checkResponse = await checkBookingSimple(this.selectedActivity.activityId, this.$store.state.user.name);
        if (checkResponse.data.isBooked) {
          this.$message.warning("您已经报名过该活动，不能重复报名");
          // 更新活动状态为已报名
          this.selectedActivity.isBooked = true;
          return;
        }

        // 0.5. 先获取最新的活动信息，确保版本号是最新的
        const latestActivity = await this.getLatestActivityInfo(this.selectedActivity.activityId);
        if (!latestActivity) {
          this.$message.error("获取活动信息失败，请刷新重试");
          return;
        }

        // 1. 更新活动容量（使用最新的版本号）
        await signUpCapacity(this.selectedActivity.activityId, latestActivity.version);

        // 2. 添加报名记录
        const bookingData = {
          activityId: this.selectedActivity.activityId,
          studentId: this.$store.state.user.name,
          bookAt: new Date().toISOString(),
          status: "未提交"
        };
        console.log('报名数据:', bookingData);
        console.log('当前活动信息:', this.selectedActivity);
        console.log('最新活动信息:', latestActivity);
        await addBooking(bookingData);

        // 3. 更新活动状态
        const updatedActivity = {
          ...latestActivity,
          activityCapacity: Math.max(latestActivity.activityCapacity - 1, 0),
          version: latestActivity.version + 1,
          isBooked: true // 标记为已报名
        };

        this.selectedActivity = updatedActivity;

        // 4. 更新活动列表
        const index = this.activityList.findIndex(a => a.activityId === this.selectedActivity.activityId);
        if (index !== -1) {
          this.activityList.splice(index, 1, updatedActivity);
        }

        this.$message.success("报名成功！");
        console.log('日历视图报名成功，活动ID:', this.selectedActivity.activityId, '用户:', this.$store.state.user.name);

        // 报名成功后关闭弹窗
        this.dialogVisible = false;

        // 如果当前在活动报名视图，刷新数据
        if (this.activeView === 'booking' && this.$refs.activityBooking) {
          this.$refs.activityBooking.getList();
        }

        // 额外同步一次数据，确保其他用户能看到最新状态
        await this.syncActivityData();

      } catch (error) {
        console.error("报名失败:", error);
        this.$message.error("报名失败: " + (error.msg || "请稍后重试"));
      }
    },

    // 处理事件点击
    async handleEventClick(activity) {
      this.selectedActivity = { ...activity };

      // 加载最新的取消限制信息
      await this.loadCancelLimitInfo();

      this.dialogVisible = true;
    },

    // 处理弹窗关闭
    handleClose(done) {
      this.dialogVisible = false;
      this.selectedActivity = null;
      done();
    },

    // 处理活动报名状态更新
    handleBookingUpdated(bookingData) {
      console.log('收到报名状态更新事件:', bookingData);

      // 更新日历视图中的活动数据
      const index = this.activityList.findIndex(a => a.activityId === bookingData.activityId);
      if (index !== -1) {
        // 使用Vue.set确保响应式更新
        this.$set(this.activityList[index], 'isBooked', bookingData.isBooked);
        this.$set(this.activityList[index], 'activityCapacity', bookingData.activityCapacity);

        console.log('日历视图活动状态已更新:', this.activityList[index]);
      }
    },

    // 处理取消活动
    async handleCancelSignUp() {
      try {
        // 先检查取消限制
        const canCancel = await this.checkCancelLimit();

        this.$confirm('确定要取消该活动吗？', '确认取消', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.submitCancelSignUp();
        }).catch(() => {
          // 用户取消操作，不显示提示
        });
      } catch (error) {
        console.error('检查取消限制失败:', error);
        this.$message.error('检查取消限制失败，请稍后重试');
      }
    },

    // 提交取消活动
    async submitCancelSignUp() {
      try {
        console.log('开始取消报名流程，活动ID:', this.selectedActivity.activityId);

        // 0. 先刷新活动数据以确保使用最新版本号
        await this.fetchActivities();
        const latestActivity = this.activityList.find(a => a.activityId === this.selectedActivity.activityId);
        if (latestActivity) {
          this.selectedActivity.version = latestActivity.version;
          this.selectedActivity.activityCapacity = latestActivity.activityCapacity;
          console.log('更新为最新版本号和容量:', this.selectedActivity.version, this.selectedActivity.activityCapacity);
        }

        // 1. 先检查报名记录是否存在
        console.log('检查报名记录是否存在...');
        let bookingExists = false;
        try {
          const bookingStatus = await checkBookingSimple(this.selectedActivity.activityId, this.$store.state.user.name);
          console.log('报名状态检查结果:', bookingStatus);
          bookingExists = bookingStatus.data.isBooked;
        } catch (checkError) {
          console.error('检查报名状态失败:', checkError);
          // 如果检查失败，假设记录不存在
          bookingExists = false;
        }

        // 2. 如果报名记录存在，则删除
        if (bookingExists) {
          console.log('报名记录存在，执行删除...');
          try {
            const deleteResult = await deleteBookingsByActivityAndStudent(
              this.selectedActivity.activityId,
              this.$store.state.user.name
            );
            console.log('删除报名记录结果:', deleteResult);

            // 检查删除是否成功
            if (!deleteResult || deleteResult.code !== 200) {
              console.warn('删除报名记录返回非成功状态:', deleteResult);
              // 不抛出异常，继续执行，但记录警告
            }
          } catch (deleteError) {
            console.error('删除报名记录异常:', deleteError);
            // 如果删除失败，尝试继续执行，但记录警告
            console.warn('删除报名记录失败，但继续执行取消流程');
          }
        } else {
          console.log('报名记录不存在，跳过删除步骤');
        }

        // 3. 恢复活动容量
        console.log('恢复活动容量...');
        const capacityResult = await cancelSignUpCapacity(
          this.selectedActivity.activityId,
          this.selectedActivity.version
        );
        console.log('恢复容量结果:', capacityResult);

        if (capacityResult && capacityResult.code !== 200) {
          throw new Error(capacityResult.msg || '恢复活动容量失败');
        }

        // 4. 记录取消信息到数据库
        console.log('记录取消信息...');
        const cancelData = {
          studentId: this.$store.state.user.name,
          activityId: this.selectedActivity.activityId,
          cancelTime: new Date().toISOString()
        };
        console.log('取消记录数据:', cancelData);

        const cancelResult = await recordCancel(cancelData);
        console.log('recordCancel响应:', cancelResult);

        if (cancelResult && cancelResult.code !== 200) {
          throw new Error(cancelResult.msg || '取消记录保存失败');
        }

        // 5. 立即查询数据库验证取消记录
        console.log('验证取消记录是否保存...');
        const verifyResponse = await getCancelCount(this.$store.state.user.name);
        console.log('验证取消次数响应:', verifyResponse);

        if (verifyResponse && verifyResponse.code === 200) {
          const actualCancelCount = verifyResponse.data;
          console.log(`实际取消次数: ${actualCancelCount}`);

          // 如果实际取消次数没有增加，说明有问题
          if (actualCancelCount === 0) {
            console.warn('警告：取消记录可能没有正确保存到数据库');
            // 不抛出异常，继续执行，但记录警告
          }
        }

        // 6. 重新获取活动列表以同步最新数据（包括版本号和容量）
        await this.fetchActivities();

        // 7. 重新加载取消限制信息
        console.log('重新加载取消限制信息...');
        await this.loadCancelLimitInfo();
        console.log(`重新加载后剩余取消次数: ${this.remainingCancels}`);

        if (this.remainingCancels > 0) {
          this.$message.success(`取消报名成功！本月还可取消 ${this.remainingCancels} 次`);
        } else {
          this.$message.warning('取消报名成功！本月取消次数已用完，将无法报名新活动，但仍可取消已报名的活动');
        }

        // 取消成功后关闭弹窗
        this.dialogVisible = false;

        // 如果当前在活动报名视图，刷新数据
        if (this.activeView === 'booking' && this.$refs.activityBooking) {
          this.$refs.activityBooking.getList();
        }

        // 额外同步一次数据，确保其他用户能看到最新状态
        await this.syncActivityData();

      } catch (error) {
        console.error("取消活动失败:", error);

        // 更详细的错误信息
        if (error.response) {
          console.error('错误响应:', error.response);
          this.$message.error(`取消活动失败: ${error.response.data?.msg || error.response.statusText}`);
        } else if (error.request) {
          console.error('请求错误:', error.request);
          this.$message.error("取消活动失败: 网络连接错误");
        } else {
          this.$message.error("取消活动失败: " + (error.message || "请稍后重试"));
        }
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
    },

    // 隐藏"今天"按钮
    hideTodayButton() {
      this.$nextTick(() => {
        // 隐藏Element UI默认的日历头部
        const defaultHeader = document.querySelector('.calendar-view .el-calendar__header');
        if (defaultHeader) {
          defaultHeader.style.display = 'none';
        }

        // 隐藏所有包含"今天"文字的按钮，但排除自定义头部的按钮
        const buttons = document.querySelectorAll('.calendar-view .el-button');
        buttons.forEach(button => {
          if (button.textContent && button.textContent.includes('今天') &&
            !button.closest('.custom-calendar-header')) {
            button.style.display = 'none';
          }
        });

        // 隐藏默认的按钮组，但保留自定义的按钮组
        const defaultButtonGroup = document.querySelector('.calendar-view .el-calendar__header .el-calendar__button-group');
        if (defaultButtonGroup) {
          defaultButtonGroup.style.display = 'none';
        }
      });

      // 使用定时器重复检查，确保"今天"按钮被隐藏
      setTimeout(() => {
        const buttons = document.querySelectorAll('.calendar-view .el-button');
        buttons.forEach(button => {
          if (button.textContent && button.textContent.includes('今天') &&
            !button.closest('.custom-calendar-header')) {
            button.style.display = 'none';
            button.style.visibility = 'hidden';
          }
        });
      }, 100);
    },

    /** 预览活动图片 */
    previewActivityImage(imageUrl) {
      this.previewImageUrl = imageUrl;
      this.imagePreviewVisible = true;
    },

    /** 获取活动图片完整URL */
    getActivityImageUrl(pictureUrl) {
      if (!pictureUrl) return '';

      // 如果已经是完整URL，直接返回
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        return pictureUrl;
      }

      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径
      if (pictureUrl.startsWith('/profile/')) {
        return `${process.env.VUE_APP_BASE_API}${pictureUrl}`;
      }

      return pictureUrl;
    },

    // 同步活动数据（在报名操作后调用）
    async syncActivityData() {
      if (this.isSyncing) {
        console.log('正在同步中，跳过本次同步');
        return;
      }

      try {
        this.isSyncing = true;
        console.log('同步活动数据...');

        // 静默获取最新数据，不显示loading状态
        const response = await listActivities(this.queryParams);
        if (response && response.rows) {
          // 使用服务器时间，如果服务器时间不可用则使用本地时间
          const now = this.serverTime || new Date();
          this.activityList = response.rows.map(activity => {
            return {
              ...activity,
              status: this.calculateStatus(activity, now),
              isBooked: false // 初始化为未报名状态
            };
          });

          // 检查报名状态
          const checkPromises = this.activityList.map(activity =>
            checkBookingSimple(activity.activityId, this.$store.state.user.name).then(res => {
              activity.isBooked = res.data.isBooked;
            }).catch(() => {
              // 如果检查失败，保持默认状态
            })
          );
          await Promise.all(checkPromises);

          console.log('活动数据已同步，当前活动数量:', this.activityList.length);
        }
      } catch (error) {
        console.error('同步活动数据失败:', error);
        // 同步失败，不显示错误信息给用户
      } finally {
        this.isSyncing = false;
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
  padding: 0;
  position: relative;
  width: calc(100% - 200px);
  overflow: visible;
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
  min-height: auto;

  .el-tabs__header {
    margin: 0 0 15px 0;
    flex-shrink: 0;
  }

  .el-tabs__nav-wrap {
    background: linear-gradient(to right, rgb(69, 127, 202), rgb(86, 145, 200));
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
    min-height: auto;
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
    background: linear-gradient(to right, rgb(69, 127, 202), rgb(86, 145, 200));
    color: white;
    border-radius: 6px; /* 减少圆角 */
    font-size: 10px; /* 减少字体大小 */
    box-shadow: 0 2px 8px rgba(69, 127, 202, 0.3);
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
    background: linear-gradient(to right, rgb(69, 127, 202), rgb(86, 145, 200)); /* 默认颜色 */
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

    /* 已报名状态 - 绿色边框和特殊标识 */
    &.booked {
      border: 2px solid #27ae60;
      box-shadow: 0 2px 8px rgba(39, 174, 96, 0.3);
      position: relative;

      &::after {
        content: '✓';
        position: absolute;
        top: -2px;
        right: -2px;
        width: 16px;
        height: 16px;
        background: #27ae60;
        color: white;
        border-radius: 50%;
        font-size: 10px;
        font-weight: bold;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 2px 4px rgba(39, 174, 96, 0.4);
      }
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
      box-shadow: 0 4px 16px rgba(69, 127, 202, 0.4);
      background: linear-gradient(to right, rgb(59, 107, 182), rgb(76, 125, 180));
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

/* 自定义日历头部样式 */
.custom-calendar-header {
  display: flex !important;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(to right, rgb(69, 127, 202), rgb(86, 145, 200));
  color: white;
  border-radius: 8px 8px 0 0;
  padding: 15px 20px;
  margin-bottom: 0;
  width: 100%;
  box-sizing: border-box;

  .header-left {
    display: flex !important;
    align-items: center;

    .calendar-title {
      font-size: 18px;
      font-weight: 600;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      white-space: nowrap;
    }
  }

  .header-right {
    display: flex !important;
    align-items: center;

    .el-button-group {
      display: flex !important;

      .el-button {
        background: rgba(255, 255, 255, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: white;
        font-size: 12px;
        padding: 6px 12px;
        display: inline-block !important;

        &:hover {
          background: rgba(255, 255, 255, 0.3);
          border-color: rgba(255, 255, 255, 0.5);
        }

        &:active {
          background: rgba(255, 255, 255, 0.4);
        }
      }
    }
  }
}

/* 隐藏Element UI默认的日历头部 */
.calendar-view .el-calendar__header {
  display: none !important;
}

/* 隐藏Element UI默认的按钮组，但保留自定义的按钮组 */
.calendar-view .el-calendar__header .el-calendar__button-group {
  display: none !important;
}

/* 隐藏所有包含"今天"文字的按钮 */
.calendar-view .el-button:contains("今天") {
  display: none !important;
}

/* 更强制性地隐藏"今天"按钮 */
.calendar-view .el-calendar__header .el-calendar__button-group .el-button:nth-child(2) {
  display: none !important;
}

/* 隐藏所有可能的"今天"按钮 */
.calendar-view .el-button[title*="今天"],
.calendar-view .el-button[aria-label*="今天"] {
  display: none !important;
}

/* 确保自定义日历头部的按钮组正常显示 */
.custom-calendar-header .el-button-group {
  display: flex !important;
}

.custom-calendar-header .el-button-group .el-button {
  display: inline-block !important;
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
  bottom: 80px;
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

      /* 已报名 - 绿色 */
      &.status-booked {
        background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
        border: 2px solid #27ae60;
        position: relative;

        &::after {
          content: '✓';
          position: absolute;
          top: -2px;
          right: -2px;
          width: 12px;
          height: 12px;
          background: #27ae60;
          color: white;
          border-radius: 50%;
          font-size: 8px;
          font-weight: bold;
          display: flex;
          align-items: center;
          justify-content: center;
        }
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
    background: linear-gradient(to right, rgb(69, 127, 202), rgb(86, 145, 200));
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
    justify-content: space-between;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid rgba(102, 126, 234, 0.1);

    h2 {
      margin: 0;
      font-size: 24px;
      font-weight: 600;
      color: #2c3e50;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    }

    .status-tags {
      display: flex;
      gap: 8px;

      .status-tag, .sign-tag {
        border-radius: 20px;
        padding: 4px 12px;
        font-weight: 500;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
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
    flex-direction: column;
    align-items: center;
    gap: 16px;

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

/* 取消限制信息样式 */
.cancel-limit-info {
  margin-bottom: 16px;
  width: 100%;
}

.limit-tip {
  padding: 8px 12px;
  background: #f0f9ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
  color: #1890ff;
  font-size: 12px;
  margin-top: 8px;
  width: 100%;
  text-align: center;
}

.limit-tip i {
  margin-right: 4px;
}

.disabled-button {
  color: #c0c4cc !important;
  cursor: not-allowed !important;
  background-color: #f5f7fa !important;
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

/* 书院信息样式 */
.academy-info {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  padding: 8px 16px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
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
  max-height: none !important;
  height: auto !important;
  overflow: visible !important;
}

/* 确保页面可以正常滚动 */
::v-deep body, ::v-deep html {
  height: auto !important;
  overflow: visible !important;
}

/* 确保分页组件正常显示 */
::v-deep .booking-container .custom-pagination {
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
  position: relative !important;
  z-index: 999 !important;
  margin-top: 20px !important;
  padding: 20px 0 !important;
  background: white !important;
  border-top: 1px solid #e4e7ed !important;
  border-radius: 0 0 16px 16px !important;
}

/* 强制显示分页组件内部容器 */
::v-deep .booking-container .custom-pagination .pagination-container {
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
  margin: 0 !important;
}

/* 活动图片展示样式 */
.activity-image-container {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.activity-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  }
}

/* 图片预览对话框样式 */
.image-preview-dialog {
  .el-dialog {
    border-radius: 12px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  }

  .el-dialog__header {
    background: linear-gradient(to right, #409EFF, #64b5ff);
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
    padding: 0;
    background: #f8f9fa;
  }
}

.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 0 0 12px 12px;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 管理员首页容器样式 */
.admin-home-container {
  width: 100%;
  max-width: 100%;
  padding: 0;
  margin: 0;
  min-height: calc(100vh - 84px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

/* 专门针对管理员首页的ActivityManager样式 */
.admin-home-container .admin-activity-manager {
  width: 100% !important;
  max-width: 100% !important;
}

.admin-home-container .admin-activity-manager ::v-deep .app-container {
  width: 100% !important;
  max-width: 100% !important;
  padding: 20px !important;
  margin: 0 !important;
  margin-left: 0 !important;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.admin-home-container .admin-activity-manager ::v-deep .search-card,
.admin-home-container .admin-activity-manager ::v-deep .table-card {
  width: 100%;
  margin: 0;
}

.admin-home-container .admin-activity-manager ::v-deep .el-form {
  width: 100%;
  text-align: left;
}

.admin-home-container .admin-activity-manager ::v-deep .table-container {
  width: 100%;
  text-align: left;
}

.admin-home-container .admin-activity-manager ::v-deep .el-form-item__content {
  justify-content: flex-start;
}

/* 富文本内容样式 */
.rich-text-content {
  line-height: 1.6;
}

.rich-text-content img {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 8px 0;
}

.rich-text-content p {
  margin: 8px 0;
}

.rich-text-content h1, .rich-text-content h2, .rich-text-content h3 {
  margin: 12px 0 8px;
  font-weight: 600;
}

.rich-text-content ul, .rich-text-content ol {
  padding-left: 20px;
  margin: 8px 0;
}

.rich-text-content li {
  margin: 4px 0;
}

.rich-text-content blockquote {
  border-left: 4px solid #e4e7ed;
  padding-left: 12px;
  margin: 8px 0;
  color: #606266;
  background-color: #f5f7fa;
  border-radius: 0 4px 4px 0;
}

.rich-text-content pre {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 12px;
  overflow-x: auto;
  margin: 8px 0;
}

.rich-text-content code {
  background-color: #f5f7fa;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: monospace;
  margin: 0 2px;
}

.rich-text-content table {
  border-collapse: collapse;
  width: 100%;
  margin: 8px 0;
}

.rich-text-content th, .rich-text-content td {
  border: 1px solid #e4e7ed;
  padding: 8px 12px;
  text-align: left;
}

.rich-text-content th {
  background-color: #f5f7fa;
  font-weight: 600;
}
</style>
