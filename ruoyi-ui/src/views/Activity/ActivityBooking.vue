<template>
  <div class="app-container">
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
                    <span class="event-name" :title="event.activityName">{{ truncate(event.activityName, 18) }}</span>
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

      <!-- 活动列表视图 -->
      <el-tab-pane label="活动列表" name="list">
        <div class="search-card">
      <div class="card-header">
        <i class="el-icon-search"></i>
        <span>搜索条件</span>
        <span class="academy-info" v-if="currentAcademy">当前书院：{{ currentAcademy }}</span>
        <span class="time-range-info">{{ getTimeRangeText() }}</span>
      </div>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <div class="search-row">
          <el-form-item label="活动名称" prop="activityName">
            <el-input
              v-model="queryParams.activityName"
              placeholder="请输入活动名称"
              clearable
              prefix-icon="el-icon-search"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="活动类型" prop="activityType">
            <el-select v-model="queryParams.activityType" clearable placeholder="请选择活动类型" class="search-input">
              <el-option
                v-for="type in predefinedActivityTypes"
                :key="type.value"
                :label="type.label"
                :value="type.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item prop="availableOnly">
            <el-checkbox v-model="queryParams.availableOnly" @change="handleQuery">
              只显示可报名活动
            </el-checkbox>
          </el-form-item>

          <el-form-item class="search-actions">
            <el-button-group class="action-buttons">
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

    <!-- 活动列表 -->
    <div class="table-card">
      <div class="card-header">
        <i class="el-icon-tickets"></i>
        <span>活动列表</span>
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>
      <el-table
        v-loading="loading"
        :data="activitiesList"
        style="width: 100%"
        class="modern-table"
        :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}"
        :row-class-name="getRowClassName"
      >
        <el-table-column label="序号" width="100" align="center">
          <template v-slot="scope">
            <span class="index-badge">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="活动名称" align="center" prop="activityName" width="300">
          <template slot-scope="scope">
            <div class="activity-name" :title="scope.row.activityName">
              <span :title="scope.row.activityName">{{ truncateText(scope.row.activityName, 18) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="活动类型" align="center" prop="activityType" width="250">
          <template slot-scope="scope">
            <el-tag :type="getActivityTypeTagType(scope.row.activityType)" effect="plain" class="activity-type-tag">
              {{ getActivityTypeName(scope.row.activityType) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="活动地点" align="center" prop="activityLocation" />

        <!-- 时间安排列 -->
        <el-table-column label="时间安排" align="center" min-width="250">
          <template slot-scope="scope">
            <div class="time-schedule-inline">
              <!-- 报名时间 -->
              <div class="time-inline-item signup-time">
                <i class="el-icon-user"></i>
                <span class="time-inline-label">报名时间</span>
                <span class="time-inline-content">
                  {{ parseTime(scope.row.activityStart, '{y}-{m}-{d} {h}:{i}') }} 至 {{ parseTime(scope.row.activityDeadline, '{y}-{m}-{d} {h}:{i}') }}
                </span>
              </div>
              <!-- 活动时间 -->
              <div class="time-inline-item activity-time">
                <i class="el-icon-date"></i>
                <span class="time-inline-label">活动时间</span>
                <span class="time-inline-content">
                  {{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}') }} 至 {{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}') }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="活动状态" align="center" width="150">
          <template slot-scope="scope">
            <el-tag :type="getActivityStatusTag(scope.row)" effect="dark" class="status-tag">
              {{ getActivityStatusText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报名人数" align="center" width="120">
          <template slot-scope="scope">
            <div class="participants">
              <el-progress
                :percentage="calculateCapacityPercentage(scope.row)"
                :color="getProgressColor(calculateCapacityPercentage(scope.row))"
                :show-text="false"
                :stroke-width="10"
                class="progress-bar"
              />
              <div class="count">
                <span :class="getCapacityClass(scope.row)">
                  {{ scope.row.activityCapacity || 0 }}/{{ scope.row.activityTotalCapacity }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" fixed="right" width="120">
          <template slot-scope="scope">
            <!-- 添加取消限制检查 -->
            <el-tooltip
              v-if="!getCanSignUp(scope.row)"
              content="本月取消次数已达上限（3次），无法报名"
              placement="top"
            >
              <el-button
                type="text"
                size="mini"
                class="action-button disabled-button"
                disabled
              >无法报名</el-button>
            </el-tooltip>
            <el-button
              v-else-if="getSignStatusText(scope.row) === '可报名'"
              type="text"
              size="mini"
              class="action-button signup-button"
              @click="handleDetail(scope.row)"
            >报名</el-button>
            <el-button
              v-else-if="getSignStatusText(scope.row) === '已报名'"
              type="text"
              size="mini"
              class="action-button cancel-button"
              @click="handleDetail(scope.row)"
            >取消报名</el-button>
            <el-button
              v-else-if="isActivityFull(scope.row)"
              type="text"
              size="mini"
              class="action-button detail-button"
              @click="handleDetail(scope.row)"
            >已满</el-button>
            <el-button
              v-else
              type="text"
              size="mini"
              class="action-button detail-button"
              @click="handleDetail(scope.row)"
            >不可报名</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="custom-pagination">
        <el-pagination
          v-show="total>0"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 活动详情弹窗 -->
    <el-dialog
      title="活动详情"
      :visible.sync="detailDialogVisible"
      width="800px"
      :before-close="handleDetailClose"
      class="activity-detail-dialog"
    >
      <div class="activity-detail" v-if="selectedActivity">
        <!-- 活动详情展示 -->
        <div class="detail-header">
          <h2>{{ selectedActivity.activityName }}</h2>
          <div class="status-tags">
            <el-tag :type="getActivityStatusTag(selectedActivity)" size="medium" class="status-tag">
              {{ getActivityStatusText(selectedActivity) }}
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
              {{ selectedActivity.activityCapacity || 0 }}/{{ selectedActivity.activityTotalCapacity }}人
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
          <div class="section-content">
            <!-- 使用 v-html 渲染富文本内容 -->
            <div class="rich-text-content" v-html="selectedActivity.activityDescription || '暂无描述信息'"></div>
          </div>
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
            @click="handleSignUp(selectedActivity)"
            v-if="showSignUpButton"
            class="signup-button"
          >
            立即报名
          </el-button>
          <el-button
            type="danger"
            :disabled="!showCancelButton"
            @click="handleCancel(selectedActivity)"
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
            <div v-if="expandRequestCount > 0" style="margin-top: 8px; font-size: 14px;">
              已有 <strong style="color: #409EFF;">{{ expandRequestCount }}</strong> 人申请扩容
            </div>
          </el-alert>
          
          <!-- 申请扩容按钮 -->
          <el-button
            v-if="showFullCapacityAlert"
            type="warning"
            :loading="requestingExpand"
            :disabled="hasRequestedExpand"
            @click="handleRequestExpand"
            class="expand-button"
          >
            <i class="el-icon-plus"></i>
            {{ hasRequestedExpand ? '已申请扩容' : '申请扩容' }}
          </el-button>
          <div v-if="hasRequestedExpand && showFullCapacityAlert" style="margin-top: 8px; font-size: 13px; color: #909399;">
            <i class="el-icon-success"></i> 您已申请过扩容，每个活动只能申请一次
          </div>
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
  </div>
</template>

<script>
import { listActivities, signUpCapacity, cancelSignUpCapacity, getActivities, requestExpandCapacity, getExpandRequestCount, checkExpandRequest } from "@/api/system/activities";
import { addBooking, deleteBookingsByActivityAndStudent, signUpActivity, getBookedActivities, cancelSignUpActivity } from "@/api/system/bookings";
import { parseTime } from "@/utils/ruoyi";
import { checkBookingSimple } from "@/api/system/bookings";
import { getStudent } from "@/api/system/student";
import { recordCancel, checkCancelLimit, getCancelCount } from "@/api/system/userLimit";
import { getServerTime } from "@/api/common/time";

export default {
  name: "ActivitiesSignUp",
  data() {
    return {
      activeView: 'calendar', // 当前视图: list/calendar，默认显示日历视图
      calendarDate: new Date(), // 日历当前日期
      remainingCancels: 3,
      loading: true,
      showSearch: true,
      // 服务器时间
      serverTime: null,
      total: 0,
      activitiesList: [], // 活动列表（仅当月）
      allActivitiesList: [], // 所有活动（用于日历视图）
      // 详情弹窗相关
      detailDialogVisible: false,
      selectedActivity: null,
      expandRequestCount: 0, // 扩容申请数量
      requestingExpand: false, // 是否正在申请扩容
      hasRequestedExpand: false, // 当前用户是否已申请扩容
      // 图片预览相关
      imagePreviewVisible: false,
      previewImageUrl: '',
      // 学生书院信息
      currentAcademy: null,
      // 预定义的活动类型
      predefinedActivityTypes: [
        { value: '1', label: '人格塑造与价值引领活动类' },
        { value: '2', label: '知识融合与思维进阶活动类' },
        { value: '3', label: '能力锻造与实践创新活动类' },
        { value: '4', label: '社会责任与领军意识活动类' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        activityName: null,
        activityLocation: null,
        organizer: null,
        activityType: null,
        availableOnly: false, // 只显示可报名活动
      },
      // 实时同步相关
    };
  },
  computed: {
    // 动态页面标题
    pageTitle() {
      return '活动预约';
    },
    currentMonthTitle() {
      const d = new Date(this.calendarDate);
      return `${d.getFullYear()}年${(d.getMonth() + 1).toString().padStart(2, '0')}月`;
    },

    // 修复计算属性
    exceedCancelLimit() {
      // 从数据中获取实际的取消限制状态
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
        !this.selectedActivity.isBooked &&
        (this.selectedActivity.activityCapacity || 0) < this.selectedActivity.activityTotalCapacity;
    },

    // 显示报名已满提示的条件
    showFullCapacityAlert() {
      if (!this.selectedActivity) return false;
      return this.getActivityStatusText(this.selectedActivity) === "报名进行中" &&
        !this.selectedActivity.isBooked &&
        (this.selectedActivity.activityCapacity || 0) >= this.selectedActivity.activityTotalCapacity;
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
  async created() {
    // 获取服务器时间
    await this.getServerTime();
    // 先获取学生信息
    await this.getCurrentStudentInfo();
    // 初始加载取消限制信息
    await this.loadCancelLimitInfo();
    // 检查路由参数，处理从首页跳转过来的活动详情请求
    await this.checkRouteParams();

  },
  async mounted() {
    // 检查报名状态
    await this.checkBookingStatus();
    // 初始化日历相关功能
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
      // 当切换到日历视图时，刷新数据
      if (newView === 'calendar') {
        this.$nextTick(() => {
          this.hideEmptyCalendarRows();
          this.forceCalendarDayHeight();
          this.hideTodayButton();
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

    /** 检查路由参数，处理从首页跳转过来的活动详情请求 */
    async checkRouteParams() {
      const { activityId, view, tab } = this.$route.query;

      // 处理视图切换参数
      if (view === 'list' || tab === 'activity-list') {
        console.log('检测到活动列表视图参数，切换到活动列表标签页');
        this.activeView = 'list';
      }

      if (activityId) {
        console.log('=== 开始处理路由参数 ===');
        console.log('检测到路由参数 activityId:', activityId, '类型:', typeof activityId);

        try {
          // 直接通过API获取指定活动，不依赖当前页面的活动列表
          console.log('通过API直接获取活动详情...');
          const response = await this.getActivityById(activityId);

          if (response && response.activityId) {
            console.log('通过API找到活动:', response.activityName);

            // 等待一小段时间确保页面完全加载
            await new Promise(resolve => setTimeout(resolve, 1000));

            // 自动打开活动详情弹窗
            this.handleDetail(response);
            console.log('成功打开活动详情弹窗');
          } else {
            console.error('=== API未返回活动数据 ===');
            console.error('查找的活动ID:', activityId);

            // 如果API方式失败，尝试在所有活动列表中查找
            console.log('尝试在所有活动列表中查找...');
            if (this.allActivitiesList.length === 0) {
              await this.getList();
            }

            // 先在所有活动列表中查找
            let targetActivity = this.allActivitiesList.find(activity =>
              activity.activityId == activityId
            );

            // 如果在所有活动列表中也没找到，再在当月活动列表中查找
            if (!targetActivity) {
              targetActivity = this.activitiesList.find(activity =>
                activity.activityId == activityId
              );
            }

            if (targetActivity) {
              console.log('在列表中找到活动:', targetActivity.activityName);
              await new Promise(resolve => setTimeout(resolve, 1000));
              this.handleDetail(targetActivity);
            } else {
              this.$message.warning(`未找到ID为 ${activityId} 的活动，请检查活动是否存在`);
            }
          }

        } catch (error) {
          console.error('处理路由参数时出错:', error);
          this.$message.error('处理活动详情请求时出错，请刷新页面重试');
        }

        // 清除路由参数，避免刷新页面时重复触发
        this.$router.replace({
          path: this.$route.path,
          query: {}
        });

        console.log('=== 路由参数处理完成 ===');
      }
    },

    /** 通过活动ID直接获取活动详情 */
    async getActivityById(activityId) {
      try {
        // 使用getActivities API直接获取活动详情
        const response = await getActivities(activityId);
        if (response && response.code === 200) {
          return response.data;
        } else {
          console.error('API返回错误:', response);
          return null;
        }
      } catch (error) {
        console.error('获取活动详情失败:', error);
        return null;
      }
    },

    // 加载取消限制信息
    // 修复loadCancelLimitInfo方法
    // 修复：加载取消限制信息
    // 修复：加载取消限制信息
    async loadCancelLimitInfo() {
      try {
        const response = await getCancelCount(this.$store.state.user.name);

        // 检查响应数据结构
        if (response && response.code === 200) {
          // 后端返回的数据直接在根对象上，不在data属性中
          if (response.data !== undefined) {
            const cancelCount = response.data;
            this.remainingCancels = Math.max(0, 3 - cancelCount);
          } else {
            this.remainingCancels = 3;
          }
        } else {
          this.remainingCancels = 3;
        }
      } catch (error) {
        this.remainingCancels = 3;
      }
    },

    // 获取当前学生信息
    async getCurrentStudentInfo() {
      try {
        const response = await getStudent(this.$store.state.user.name);

        if (response && response.studentInfo) {
          this.currentAcademy = response.studentInfo.academy;
          // 获取学生信息后，根据书院获取活动
          this.getList();
        } else {
          this.currentAcademy = '未知';
          // 即使获取失败，也尝试获取活动
          this.getList();
        }
      } catch (error) {
        this.currentAcademy = '未知';
        // 即使获取失败，也尝试获取活动
        this.getList();
      }
    },

    // 活动类型映射函数：将数字转换为对应的类型名称
    getActivityTypeName(activityType) {
      const typeMap = {
        '1': '人格塑造与价值引领活动类',
        '2': '知识融合与思维进阶活动类',
        '3': '能力锻造与实践创新活动类',
        '4': '社会责任与领军意识活动类'
      };
      return typeMap[activityType] || activityType;
    },

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

    // 获取活动列表
    getList() {
      this.loading = true;
      // 构建查询参数，只获取学生所在书院的活动
      const allParams = {
        pageNum: 1,
        pageSize: 1000, // 获取足够大的数量
        activityName: this.queryParams.activityName,
        activityType: this.queryParams.activityType,
        activityLocation: this.queryParams.activityLocation,
        organizer: this.currentAcademy, // 只获取学生所在书院的活动
      };

      listActivities(allParams).then(response => {
        // 保存所有活动（用于日历视图）
        this.allActivitiesList = response.rows || [];

        // 过滤出本月的活动（用于列表视图）
        let filteredActivities = this.filterActivitiesByMonth(response.rows);

        // 如果选择了"只显示可报名活动"，进一步过滤
        if (this.queryParams.availableOnly) {
          filteredActivities = this.filterAvailableActivities(filteredActivities);
        }

        // 手动分页
        const startIndex = (this.queryParams.pageNum - 1) * this.queryParams.pageSize;
        const endIndex = startIndex + this.queryParams.pageSize;
        this.activitiesList = filteredActivities.slice(startIndex, endIndex);
        this.total = filteredActivities.length;

        this.loading = false;
        // 获取活动列表后检查报名状态（包括所有活动）
        this.checkBookingStatus();
      });
    },

    // 过滤活动：只显示活动开始时间在本月的活动
    filterActivitiesByMonth(activities) {
      const now = new Date();
      const currentYear = now.getFullYear();
      const currentMonth = now.getMonth(); // 0-11

      const filteredActivities = activities.filter(activity => {
        if (!activity.startTime) {
          return false;
        }

        const activityDate = new Date(activity.startTime);
        const activityYear = activityDate.getFullYear();
        const activityMonth = activityDate.getMonth();

        const isInRange = (activityYear === currentYear && activityMonth === currentMonth); // 只显示本月

        return isInRange;
      });

      return filteredActivities;
    },

    // 过滤可报名活动：在可报名时间内且人数未满
    filterAvailableActivities(activities) {
      const availableActivities = activities.filter(activity => {
        // 检查活动状态是否为"报名进行中"
        const status = this.getActivityStatusText(activity);
        const isSignUpActive = status === "报名进行中";

        // 检查人数是否未满
        const hasCapacity = (activity.activityCapacity || 0) < activity.activityTotalCapacity;

        const isAvailable = isSignUpActive && hasCapacity;

        return isAvailable;
      });
      return availableActivities;
    },

    // 分页事件处理
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
      this.queryParams.pageNum = 1;
      this.getList();
    },

    handleCurrentChange(val) {
      this.queryParams.pageNum = val;
      this.getList();
    },

    // 检查报名状态
    async checkBookingStatus() {
      // 检查列表视图的活动
      if (this.activitiesList && this.activitiesList.length > 0) {
        const listPromises = this.activitiesList.map(activity =>
          checkBookingSimple(activity.activityId, this.$store.state.user.name).then(res => {
            // 使用 Vue.set 确保响应式更新
            this.$set(activity, 'isBooked', res.data.isBooked);
          }).catch(error => {
            console.error(`检查活动 ${activity.activityId} 报名状态失败:`, error);
            this.$set(activity, 'isBooked', false);
          })
        );
        await Promise.all(listPromises);
      }

      // 检查日历视图的所有活动
      if (this.allActivitiesList && this.allActivitiesList.length > 0) {
        const allPromises = this.allActivitiesList.map(activity =>
          checkBookingSimple(activity.activityId, this.$store.state.user.name).then(res => {
            // 使用 Vue.set 确保响应式更新
            this.$set(activity, 'isBooked', res.data.isBooked);
          }).catch(error => {
            console.error(`检查活动 ${activity.activityId} 报名状态失败:`, error);
            this.$set(activity, 'isBooked', false);
          })
        );
        await Promise.all(allPromises);
      }
    },

    // 根据数据库中的实际报名记录更新所有活动状态
    async updateBookingStatusFromDatabase() {
      try {
        const response = await getBookedActivities(this.$store.state.user.name);

        if (response.code === 200) {
          const bookedActivityIds = response.data || [];

          // 更新列表视图的活动报名状态
          this.activitiesList.forEach(activity => {
            const isBooked = bookedActivityIds.includes(activity.activityId);
            this.$set(activity, 'isBooked', isBooked);
          });

          // 更新日历视图的所有活动报名状态
          this.allActivitiesList.forEach(activity => {
            const isBooked = bookedActivityIds.includes(activity.activityId);
            this.$set(activity, 'isBooked', isBooked);
          });

          // 如果当前有选中的活动，也更新其状态
          if (this.selectedActivity) {
            const isSelectedBooked = bookedActivityIds.includes(this.selectedActivity.activityId);
            this.$set(this.selectedActivity, 'isBooked', isSelectedBooked);
          }
        }
      } catch (error) {
        console.error('从数据库更新报名状态失败:', error);
      }
    },

    // 搜索按钮操作
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    // 重置按钮操作
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.availableOnly = false; // 重置可报名活动筛选
      this.handleQuery();
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
      const hasCapacity = (row.activityCapacity || 0) < row.activityTotalCapacity;

      if (["报名进行中"].includes(status) && hasCapacity) {
        return "可报名";
      }

      return "不可报名";
    },

    // 判断活动是否已满
    isActivityFull(row) {
      const status = this.getActivityStatusText(row);
      // 只有在报名进行中且已选人数>=总容量时才显示已满
      return status === "报名进行中" && (row.activityCapacity || 0) >= row.activityTotalCapacity;
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

    // 获取表格行样式类名
    getRowClassName({row}) {
      if (row.isBooked) {
        return 'booked-row';
      }
      return '';
    },
    // 获取容量样式
    getCapacityClass(activity) {
      const percentage = (activity.activityCapacity || 0) / activity.activityTotalCapacity;
      if (percentage >= 0.8) return 'capacity-high';
      if (percentage >= 0.5) return 'capacity-medium';
      return 'capacity-low';
    },

    // 计算容量百分比
    calculateCapacityPercentage(row) {
      if (!row.activityTotalCapacity || row.activityTotalCapacity <= 0) return 0;
      const used = row.activityCapacity || 0; // activityCapacity 现在是已选人数
      return Math.round((used / row.activityTotalCapacity) * 100);
    },

    // 获取进度条颜色
    getProgressColor(percentage) {
      if (percentage >= 80) return '#f87171';
      if (percentage >= 50) return '#fbbf24';
      return '#4ade80';
    },

    // 格式化日期时间
    formatDateTime(datetime) {
      return parseTime(datetime, "{y}-{m}-{d} {h}:{i}");
    },

    // 获取时间范围文本
    getTimeRangeText() {
      const now = new Date();
      const currentYear = now.getFullYear();
      const currentMonth = now.getMonth();

      const monthNames = [
        '1月', '2月', '3月', '4月', '5月', '6月',
        '7月', '8月', '9月', '10月', '11月', '12月'
      ];

      const currentMonthText = `${currentYear}年${monthNames[currentMonth]}`;

      return `${currentMonthText}`;
    },
// 修复：处理详情
    async handleDetail(row) {
      this.selectedActivity = { ...row };
      this.detailDialogVisible = true;

      // 加载最新的取消限制信息
      await this.loadCancelLimitInfo();
      
      // 获取扩容申请数量
      await this.loadExpandRequestCount(row.activityId);
      
      // 检查当前用户是否已申请扩容
      await this.checkUserExpandRequest(row.activityId);
    },
    
    // 加载扩容申请数量
    async loadExpandRequestCount(activityId) {
      if (!activityId) return;
      try {
        const response = await getExpandRequestCount(activityId);
        if (response && response.code === 200) {
          this.expandRequestCount = response.data || 0;
        }
      } catch (error) {
        console.error('获取扩容申请数量失败:', error);
        this.expandRequestCount = 0;
      }
    },
    
    // 检查当前用户是否已申请扩容
    async checkUserExpandRequest(activityId) {
      if (!activityId) return;
      try {
        const response = await checkExpandRequest(activityId);
        if (response && response.code === 200) {
          this.hasRequestedExpand = response.data === true;
        } else {
          this.hasRequestedExpand = false;
        }
      } catch (error) {
        console.error('检查申请状态失败:', error);
        this.hasRequestedExpand = false;
      }
    },
    
    // 处理申请扩容
    async handleRequestExpand() {
      if (!this.selectedActivity || !this.selectedActivity.activityId) {
        this.$message.error('活动信息不完整');
        return;
      }
      
      try {
        this.requestingExpand = true;
        const response = await requestExpandCapacity(this.selectedActivity.activityId);
        
        if (response && response.code === 200) {
          this.$message.success('申请扩容成功！');
          // 更新扩容申请数量
          await this.loadExpandRequestCount(this.selectedActivity.activityId);
          // 更新用户申请状态
          this.hasRequestedExpand = true;
        } else {
          this.$message.error(response?.msg || '申请扩容失败');
          // 如果是重复申请，更新状态
          if (response?.msg && response.msg.includes('已经申请过')) {
            this.hasRequestedExpand = true;
          }
        }
      } catch (error) {
        console.error('申请扩容失败:', error);
        this.$message.error('申请扩容失败，请稍后重试');
      } finally {
        this.requestingExpand = false;
      }
    },

    // 处理详情弹窗关闭
    handleDetailClose(done) {
      this.detailDialogVisible = false;
      this.selectedActivity = null;
      this.expandRequestCount = 0; // 重置扩容申请数量
      this.hasRequestedExpand = false; // 重置申请状态
      if (done) {
        done();
      }
    },

    // // 处理报名
    // handleSignUp(row) {
    //   this.$confirm('确定要报名该活动吗？', '报名确认', {
    //     confirmButtonText: '确定',
    //     cancelButtonText: '取消',
    //     type: 'warning'
    //   }).then(() => {
    //     this.submitSignUp(row);
    //   }).catch(() => {
    //     this.$message.info('已取消报名');
    //   });
    // },
    // 修复：处理取消报名
    async handleCancel(row) {
      try {
        // 先检查取消限制
        const canCancel = await this.checkCancelLimit();

        this.$confirm('确定要取消该活动报名吗？', '确认取消', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.submitCancelSignUp(row);
        }).catch(() => {
        });
      } catch (error) {

      }
    },
    // 修复：检查取消限制
    async checkCancelLimit() {
      try {
        const response = await checkCancelLimit(this.$store.state.user.name);

        // 检查响应数据结构
        if (response && response.code === 200) {
          // 后端返回的数据直接在根对象上，不在data属性中
          if (response.cancelCount !== undefined && response.canCancel !== undefined) {
            return response.canCancel;
          } else {
            return true; // 默认允许取消
          }
        } else {
          return true; // 默认允许取消
        }
      } catch (error) {
        return true; // 默认允许取消，避免影响正常流程
      }
    },

    // 修复：处理报名
    async handleSignUp(row) {
      try {
        // 先检查取消限制
        const canSignUp = await this.getCanSignUp(row);
        if (!canSignUp) {
          this.$message.warning('本月取消次数已达上限，无法报名新活动');
          return;
        }

        this.$confirm('确定要报名该活动吗？', '报名确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.submitSignUp(row);
        }).catch(() => {
          this.$message.info('已取消报名');
        });
      } catch (error) {
        this.$message.error('检查报名限制失败，请稍后重试');
      }
    },

// 修复：获取是否可以报名
    async getCanSignUp(row) {
      // 如果不是可报名状态，直接返回true（不检查限制）
      if (this.getSignStatusText(row) !== '可报名') {
        return true;
      }

      try {
        const response = await getCancelCount(this.$store.state.user.name);

        // 检查响应数据结构
        if (response && response.code === 200) {
          // 后端返回的数据直接在根对象上，不在data属性中
          if (response.data !== undefined) {
            const cancelCount = response.data;
            return cancelCount < 3; // 取消次数小于3次才允许报名
          } else {
            return true; // 默认允许报名
          }
        } else {
          return true; // 默认允许报名
        }
      } catch (error) {
        return true; // 默认允许报名，避免影响正常流程
      }
    },

    // 提交报名 - 使用原子性报名接口
    async submitSignUp(activity) {
      try {
        // 0. 先检查是否已经报名过
        const checkResponse = await checkBookingSimple(activity.activityId, this.$store.state.user.name);
        if (checkResponse.data.isBooked) {
          this.$message.warning("您已经报名过该活动，不能重复报名");
          // 更新活动状态为已报名
          activity.isBooked = true;
          return;
        }

        // 0.5. 先获取最新的活动信息，确保版本号是最新的
        const latestActivity = await this.getLatestActivityInfo(activity.activityId);
        if (!latestActivity) {
          this.$message.error("获取活动信息失败，请刷新重试");
          return;
        }

        // 1. 使用原子性报名接口
        const signUpData = {
          activityId: activity.activityId,
          studentId: this.$store.state.user.name,
          version: latestActivity.version
        };


        const response = await signUpActivity(signUpData);

        if (response.code === 200) {
          this.$message.success("报名成功！");

          // 报名成功后关闭详情弹窗
          this.detailDialogVisible = false;
          this.selectedActivity = null;

          // 等待数据库事务完成
          await new Promise(resolve => setTimeout(resolve, 300));

          // 重新获取活动列表以更新已选人数
          await this.getList();

        } else {
          this.$message.error(response.msg || "报名失败");
        }
      } catch (error) {
        console.error("报名失败:", error);
        this.$message.error("报名失败: " + (error.msg || "请稍后重试"));
      }
    },

    // 修复：提交取消报名
    async submitCancelSignUp(activity) {
      try {
        // 0. 先刷新活动数据以确保使用最新版本号
        await this.getList();
        // 先在所有活动列表中查找
        let latestActivity = this.allActivitiesList.find(a => a.activityId === activity.activityId);
        // 如果没找到，再在当月活动列表中查找
        if (!latestActivity) {
          latestActivity = this.activitiesList.find(a => a.activityId === activity.activityId);
        }
        if (latestActivity) {
          activity.version = latestActivity.version;
          activity.activityCapacity = latestActivity.activityCapacity; // 更新已选人数
        }

        // 使用原子性取消报名接口
        const cancelData = {
          activityId: activity.activityId,
          studentId: this.$store.state.user.name,
          version: activity.version
        };

        const response = await cancelSignUpActivity(cancelData);

        if (response.code === 200) {
          this.$message.success("取消报名成功！");

          // 取消报名成功后关闭详情弹窗
          this.detailDialogVisible = false;
          this.selectedActivity = null;

          // 等待数据库事务完成
          await new Promise(resolve => setTimeout(resolve, 300));

          // 重新获取活动列表以更新已选人数
          await this.getList();

          // 异步重新加载取消限制信息，但不影响当前UI状态
          setTimeout(async () => {
            try {
              await this.loadCancelLimitInfo();
            } catch (error) {
              // 后台更新失败，不影响用户操作
            }
          }, 1000);
        } else {
          this.$message.error(response.msg || "取消报名失败");
        }
      } catch (error) {
        console.error("取消报名失败:", error);
        this.$message.error("取消报名失败: " + (error.msg || "请稍后重试"));
      }
    },

    /** 预览活动图片 */
    previewActivityImage(imageUrl) {
      this.previewImageUrl = imageUrl;
      this.imagePreviewVisible = true;
    },

    /** 获取活动图片完整URL（仿照审核界面实现） */
    getActivityImageUrl(pictureUrl) {
      if (!pictureUrl) return '';

      // 如果已经是完整URL，直接返回
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        return pictureUrl;
      }

      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径（仿照审核界面）
      if (pictureUrl.startsWith('/profile/')) {
        return `${process.env.VUE_APP_BASE_API}${pictureUrl}`;
      }

      return pictureUrl;
    },

    /** 截断文本显示 */
    truncateText(text, maxLength) {
      if (!text) return '';
      if (text.length <= maxLength) return text;
      return text.substring(0, maxLength) + '...';
    },


    // 日历相关方法
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
    goCurrentMonth() {
      this.calendarDate = new Date();
    },

    // 获取日期内的事件（日历视图使用所有活动）
    getDateEvents(dateString) {
      return this.allActivitiesList.filter(activity => {
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

    // 格式化时间范围
    formatTimeRange(startTime, endTime) {
      return `${parseTime(startTime, "{h}:{i}")} - ${parseTime(endTime, "{h}:{i}")}`;
    },

    // 判断日期是否属于当前月份
    isCurrentMonth(dateString) {
      const date = new Date(dateString);
      const currentDate = new Date(this.calendarDate);

      return date.getMonth() === currentDate.getMonth() &&
        date.getFullYear() === currentDate.getFullYear();
    },

    // 获取活动状态类型（用于颜色判断）
    getActivityStatusType(activity) {
      // 已报名的活动优先显示为booked状态
      if (activity.isBooked) {
        return "booked";
      }

      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const now = this.serverTime || new Date();
      const deadline = new Date(activity.activityDeadline);
      const activityStart = new Date(activity.activityStart);

      if (now < activityStart) return "not-started"; // 报名未开始
      if (now < deadline && now >= activityStart) return "signup-active"; // 报名进行中
      if (now >= deadline) return "signup-ended"; // 报名已截止
      return "signup-ended"; // 默认显示为报名已截止
    },

    // 字符串截断
    truncate(str, maxLength) {
      if (!str) return '';
      return str.length > maxLength ? str.substring(0, maxLength) + '...' : str;
    },

    // 处理事件点击
    async handleEventClick(activity) {
      this.selectedActivity = { ...activity };

      // 加载最新的取消限制信息
      await this.loadCancelLimitInfo();
      
      // 获取扩容申请数量
      await this.loadExpandRequestCount(activity.activityId);
      
      // 检查当前用户是否已申请扩容
      await this.checkUserExpandRequest(activity.activityId);

      this.detailDialogVisible = true;
    },

    // 获取最新活动信息
    async getLatestActivityInfo(activityId) {
      try {
        const response = await getActivities(activityId);
        return response.data;
      } catch (error) {
        console.error("获取活动信息失败:", error);
        return null;
      }
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
    }
  }
};
</script>

<style scoped>
/* 整体布局 */
.app-container {
  margin-left: 100px;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.search-card, .table-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: visible;
  position: relative;
}

.table-card {
  padding-bottom: 0;
  margin-bottom: 40px;
  overflow: visible;
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f2f5;
}

.card-header i {
  font-size: 20px;
  color: #409EFF;
  margin-right: 12px;
}

.card-header span {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.record-count {
  margin-left: auto;
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}

.academy-info {
  margin-left: auto;
  margin-right: 20px;
  font-size: 13px;
  color: #67C23A;
  font-weight: 500;
  background: rgba(103, 194, 58, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid rgba(103, 194, 58, 0.2);
}

.time-range-info {
  margin-left: 20px;
  font-size: 13px;
  color: #409EFF;
  font-weight: 500;
  background: rgba(64, 158, 255, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.search-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.search-input {
  width: 200px;
}

.search-actions {
  margin-left: auto;
}

/* 可报名活动单选框样式 */
.el-form-item:has(.el-checkbox) {
  margin-bottom: 0;
}

.el-checkbox {
  font-weight: 500;
}

.el-checkbox__label {
  color: #409EFF;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.modern-table {
  border-radius: 12px;
  overflow: visible;
  border: 1px solid #e4e7ed;
  margin-bottom: 0;
  height: auto;
  max-height: none;
}

.index-badge {
  display: inline-block;
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  color: white;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

/* 活动名称样式 */
.activity-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  cursor: help;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.activity-name:hover {
  color: #409EFF;
}

.activity-type-tag {
  font-weight: 500;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.status-tag {
  padding: 5px 8px;
  font-size: 12px;
}

/* 已报名活动行样式 */
.booked-row {
  background: linear-gradient(135deg, #f0fff4 0%, #e6fff1 100%) !important;
  border-left: 4px solid #27ae60 !important;
  position: relative;
}

.booked-row:hover {
  background: linear-gradient(135deg, #e6fff1 0%, #d4f4dd 100%) !important;
}

.booked-row::before {
  content: '✓';
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  background: #27ae60;
  color: white;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

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

.detail-button {
  color: #409EFF;
}

.detail-button:hover {
  background-color: rgba(64, 158, 255, 0.1);
}

.signup-button {
  color: #67C23A;
}

.signup-button:hover {
  background-color: rgba(103, 194, 58, 0.1);
}

.cancel-button {
  color: #F56C6C;
}

.cancel-button:hover {
  background-color: rgba(245, 108, 108, 0.1);
}

.full-button {
  color: #F56C6C;
  cursor: not-allowed;
}

.full-button:hover {
  background-color: rgba(245, 108, 108, 0.1);
}

.disabled-button {
  color: #C0C4CC;
  cursor: not-allowed;
}

.disabled-button:hover {
  background-color: rgba(192, 196, 204, 0.1);
}

.custom-pagination {
  margin-top: 24px;
  text-align: center;
  padding: 20px 0;
  background: white;
  border-top: 1px solid #e4e7ed;
  border-radius: 0 0 16px 16px;
  min-height: 60px;
  width: 100%;
}

/* 活动详情弹窗样式 */
.activity-detail-dialog {
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

    .expand-button {
      width: 200px;
      height: 44px;
      font-size: 16px;
      font-weight: 600;
      background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
      border: none;
      border-radius: 22px;
      color: white;
      box-shadow: 0 4px 16px rgba(243, 156, 18, 0.3);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(243, 156, 18, 0.4);
        background: linear-gradient(135deg, #e67e22 0%, #d35400 100%);
      }

      &:active {
        transform: translateY(0);
        box-shadow: 0 2px 8px rgba(243, 156, 18, 0.3);
      }

      i {
        margin-right: 6px;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .activity-detail .detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .activity-detail-dialog .el-dialog {
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
/* 取消限制信息样式 */
.cancel-limit-info {
  margin-bottom: 16px;
}

.limit-tip {
  padding: 8px 12px;
  background: #f0f9ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
  color: #1890ff;
  font-size: 12px;
}

.limit-tip i {
  margin-right: 4px;
}

.disabled-button {
  color: #c0c4cc !important;
  cursor: not-allowed !important;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
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

/* 时间安排样式 */
.time-schedule-inline {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 6px 0;
}

.time-inline-item {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  line-height: 1.4;
  white-space: nowrap;
}

.time-inline-item.signup-time {
  i {
    color: #409EFF;
    margin-right: 4px;
  }
  .time-inline-label {
    color: #409EFF;
    font-weight: 600;
    margin-right: 8px;
  }
}

.time-inline-item.activity-time {
  i {
    color: #67C23A;
    margin-right: 4px;
  }
  .time-inline-label {
    color: #67C23A;
    font-weight: 600;
    margin-right: 8px;
  }
}

.time-inline-item i {
  font-size: 14px;
}

.time-inline-label {
  font-size: 12px;
  font-weight: 600;
}

.time-inline-content {
  color: #606266;
  font-weight: 500;
}

/* 报名人数列样式 */
.participants {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.progress-bar {
  width: 100%;
}

.count {
  font-size: 12px;
  font-weight: 500;
}

.capacity-high {
  color: #F56C6C;
  font-weight: 500;
}

.capacity-medium {
  color: #E6A23C;
  font-weight: 500;
}

.capacity-low {
  color: #67C23A;
  font-weight: 500;
}

/* 视图切换标签样式 */
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

/* 日历视图样式 */
.calendar-view {
  height: calc(100vh - 200px);
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
  padding: 2px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
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
    margin: 0 0 2px 0;
    width: 100%;
    text-align: center;
    padding: 2px 4px;
    line-height: 1.2;
    background: linear-gradient(to right, rgb(69, 127, 202), rgb(86, 145, 200));
    color: white;
    border-radius: 6px;
    font-size: 10px;
    box-shadow: 0 2px 8px rgba(69, 127, 202, 0.3);
    flex-shrink: 0;
  }

  .events-container {
    position: relative;
    display: flex;
    flex-direction: column;
    gap: 1px;
    flex: 1;
    min-width: 0;
    overflow-y: auto;
    width: 100%;
    box-sizing: border-box;
    padding: 0;
    padding-right: 2px;
  }

  .calendar-event {
    display: flex;
    align-items: center;
    padding: 4px 8px;
    background: linear-gradient(to right, rgb(69, 127, 202), rgb(86, 145, 200));
    border: none;
    border-radius: 6px;
    font-size: 11px;
    cursor: pointer;
    transition: all 0.3s ease;
    min-width: 0;
    height: 24px;
    overflow: hidden;
    color: white;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
    position: relative;
    width: 100%;
    box-sizing: border-box;
    margin-bottom: 2px;
    flex-shrink: 0;

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

    /* 已报名状态 - 绿色条和特殊标识 */
    &.booked {
      background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%) !important;
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

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 16px rgba(69, 127, 202, 0.4);
      background: linear-gradient(to right, rgb(59, 107, 182), rgb(76, 125, 180));
    }

    /* 已报名状态的hover效果 */
    &.booked:hover {
      background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%) !important;
      box-shadow: 0 4px 16px rgba(39, 174, 96, 0.4);
    }

    .event-summary {
      display: flex;
      align-items: center;
      gap: 4px;
      min-width: 0;
      flex: 1;
      z-index: 1;
      width: 100%;

      .event-name {
        font-weight: 600;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex: 1;
        font-size: 10px;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
        min-width: 0;
        margin-right: 6px;
      }

      .event-org {
        color: rgba(255, 255, 255, 0.8);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex: 0 0 auto;
        font-size: 9px;
        max-width: 45px;
        margin-right: 6px;
      }

      .detail-btn {
        padding: 2px 6px;
        font-size: 9px;
        margin-left: auto;
        background: rgba(255, 255, 255, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: white;
        border-radius: 4px;
        transition: all 0.2s ease;
        flex: 0 0 auto;
        white-space: nowrap;

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
      display: none;
    }
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

      &.status-not-started {
        background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
      }

      &.status-signup-active {
        background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
      }

      &.status-signup-ended {
        background: linear-gradient(135deg, #e67e22 0%, #d35400 100%);
      }

      &.status-booked {
        background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%) !important;
        border: 2px solid #27ae60 !important;
        position: relative;
        box-shadow: 0 2px 8px rgba(39, 174, 96, 0.3) !important;
        z-index: 10;

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

/* 覆盖 Element UI 的日历格子高度 */
.calendar-view .el-calendar-table .el-calendar-day {
  height: var(--calendar-day-height, 120px) !important;
  padding: 8px !important;
  min-height: var(--calendar-day-height, 120px) !important;
  max-height: var(--calendar-day-height, 120px) !important;
}

/* 隐藏非当月日期 */
.calendar-view .el-calendar-table td:not(.is-current-month) {
  display: none;
}

/* 确保当月日期正常显示 */
.calendar-view .el-calendar-table td.is-current-month {
  display: table-cell;
}

/* 隐藏多余的空行 */
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

/* 滚动条美化 */
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

/* 入场动画 */
@keyframes fadeSlideIn {
  from { opacity: 0; transform: translateY(4px); }
  to   { opacity: 1; transform: translateY(0); }
}
.calendar-view .calendar-event {
  animation: fadeSlideIn 0.2s ease both;
}

/* 焦点可达性优化 */
.calendar-view .detail-btn:focus {
  outline: 2px solid rgba(102, 126, 234, 0.6);
  outline-offset: 2px;
}

/* 标签激活时的轻微放大反馈 */
.view-tabs .el-tabs__item.is-active {
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .activity-legend {
    position: relative;
    bottom: auto;
    right: auto;
    margin: 20px auto 0;
    width: 90%;
    max-width: 300px;
  }

  .calendar-view {
    height: calc(100vh - 250px);
    padding: 10px;
  }
}


</style>
