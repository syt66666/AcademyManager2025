<template>
  <div class="app-container">
    <!-- 主体布局 -->
    <el-row :gutter="20">
      <!-- 左侧面板 -->
      <el-col :span="8">
        <!-- 日历区域 - 调整高度确保完整显示 -->
        <el-card class="calendar-card">
          <div slot="header">
            <span>日历视图</span>
          </div>
          <el-calendar v-model="calendarDate" class="compact-calendar">
            <template #dateCell="{ data }">
              <div @click="handleDateClick(data)">
                {{ data.day.split('-').slice(2).join('-') }}
                <div v-if="hasEventOnDate(data.day)" class="event-indicator"></div>
              </div>
            </template>
          </el-calendar>
        </el-card>

        <!-- 已订阅活动 - 更多空间 -->
        <el-card class="subscribed-card expanded-subscription">
          <div slot="header">
            <span>我的订阅</span>
          </div>
          <div v-if="subscribedActivities.length === 0" class="empty-subscription">
            暂无订阅活动
          </div>
          <el-timeline v-else>
            <el-timeline-item
              v-for="activity in subscribedActivities"
              :key="activity.activityId"
              :timestamp="formatDate(activity.startTime)"
              placement="top"
            >
              <el-card shadow="hover">
                <h4>{{ activity.activityName }}</h4>
                <p>{{ activity.activityDescription }}</p>
                <el-tag type="success" size="mini">已订阅</el-tag>
                <el-button
                  size="mini"
                  class="unsubscribe-btn"
                  @click="unsubscribe(activity.activityId)"
                >取消订阅</el-button>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <!-- 右侧活动列表 -->
      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span>活动列表</span>
            <div class="filter-container">
              <el-date-picker
                v-model="filterDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd"
                @change="fetchActivities"
              />
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="fetchActivities"
              >
                搜索
              </el-button>
            </div>
          </div>

          <el-table
            :data="activityList"
            style="width: 100%"
            v-loading="loading"
          >
            <el-table-column label="活动名称" prop="activityName" width="180" />
            <el-table-column label="活动地点" prop="activityLocation"  />
            <el-table-column label="报名时间" width="200">
              <template slot-scope="{ row }">
                {{ formatDate(row.activityStart) }} 至 {{ formatDate(row.activityDeadline) }}
              </template>
            </el-table-column>
            <el-table-column label="活动时间" width="200">
              <template slot-scope="{ row }">
                {{ formatDate(row.startTime) }} 至 {{ formatDate(row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="{ row }">
                <el-tag :type="statusTagMap[row.status]">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="{ row }">
                <el-button
                  v-if="!row.subscribed"
                  type="text"
                  @click="subscribeActivity(row.activityId)"
                >
                  订阅
                </el-button>
                <el-button
                  v-else
                  type="text"
                  disabled
                >
                  已订阅
                </el-button>
                <el-button type="text" @click="viewDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="fetchActivities"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 活动详情弹窗 -->
    <el-dialog
      :title="currentActivity.activityName"
      :visible.sync="detailVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="activity-detail">
        <div class="detail-item">
          <span class="detail-label">活动状态：</span>
          <el-tag :type="statusTagMap[currentActivity.status]" size="medium">
            {{ currentActivity.status }}
          </el-tag>
        </div>

        <el-divider></el-divider>

        <div class="detail-item">
          <span class="detail-label">活动地点：</span>
          <span class="detail-value">{{ currentActivity.activityLocation }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">组织单位：</span>
          <span class="detail-value">{{ currentActivity.organizer }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">活动容量：</span>
          <span class="detail-value">{{ currentActivity.activityCapacity }}/{{ currentActivity.activityTotalCapacity }}人</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">报名开始：</span>
          <span class="detail-value">{{ formatDateTime(currentActivity.activityStart) }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">报名截止：</span>
          <span class="detail-value">{{ formatDateTime(currentActivity.activityDeadline) }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">活动开始：</span>
          <span class="detail-value">{{ formatDateTime(currentActivity.startTime) }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">活动结束：</span>
          <span class="detail-value">{{ formatDateTime(currentActivity.endTime) }}</span>
        </div>

        <el-divider></el-divider>

        <div class="detail-section">
          <h4 class="section-title">活动描述</h4>
          <div class="section-content">{{ currentActivity.activityDescription }}</div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">注意事项</h4>
          <div class="section-content">{{ currentActivity.notes }}</div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listActivities } from "@/api/system/activities";
import Pagination from "@/components/Pagination";

export default {
  name: "ActivityDashboard",
  components: { Pagination },
  data() {
    return {
      calendarDate: new Date(),
      filterDateRange: null,
      activityList: [],
      total: 0,
      loading: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
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
      subscribedActivities: [],
      statusTagMap: {
        '未开始': 'info',
        '可报名': 'success',
        '已截止': 'danger',
        '进行中': 'warning',
        '已结束': ''
      },
      // 活动详情弹窗相关数据
      detailVisible: false,
      currentActivity: {
        activityId: null,
        activityName: '',
        activityLocation: '',
        activityDescription: '',
        activityStart: null,
        activityDeadline: null,
        startTime: null,
        endTime: null,
        activityTotalCapacity: 0,
        activityCapacity: 0,
        status: '',
        organizer: '',
        notes: ''
      }
    };
  },
  mounted() {
    this.fetchActivities();
    this.fetchSubscribed();
  },
  methods: {
    fetchActivities() {
      if (this.filterDateRange && this.filterDateRange.length === 2) {
        this.queryParams.startTime = this.filterDateRange[0];
        this.queryParams.endTime = this.filterDateRange[1];
      } else {
        this.queryParams.startTime = null;
        this.queryParams.endTime = null;
      }

      this.loading = true;
      listActivities(this.queryParams).then(response => {
        if (response.code === 200) {
          this.activityList = response.rows;
          this.total = response.total;

          const now = new Date();
          this.activityList = this.activityList.map(activity => {
            return {
              ...activity,
              status: this.calculateStatus(activity, now)
            };
          });
        } else {
          this.$message.error("获取活动列表失败: " + response.msg);
        }
      }).catch(error => {
        console.error("获取活动列表异常:", error);
        this.$message.error("获取活动列表异常");
      }).finally(() => {
        this.loading = false;
      });
    },

    calculateStatus(activity, now) {
      const startTime = new Date(activity.startTime);
      const endTime = new Date(activity.endTime);
      const deadline = activity.activityDeadline ? new Date(activity.activityDeadline) : null;

      if (now > endTime) return '已结束';
      if (now >= startTime && now <= endTime) return '进行中';
      if (deadline && now > deadline) return '已截止';
      return activity.status || '未开始';
    },

    fetchSubscribed() {
      const subscriptions = JSON.parse(localStorage.getItem('activitySubscriptions') || '[]');

      this.subscribedActivities = this.activityList
        .filter(a => subscriptions.includes(a.activityId))
        .map(activity => {
          return {
            activityId: activity.activityId,
            activityName: activity.activityName,
            activityDescription: activity.activityDescription,
            startTime: activity.startTime
          };
        });
    },

    subscribeActivity(id) {
      const subscriptions = JSON.parse(localStorage.getItem('activitySubscriptions') || '[]');

      if (!subscriptions.includes(id)) {
        subscriptions.push(id);
        localStorage.setItem('activitySubscriptions', JSON.stringify(subscriptions));

        const activity = this.activityList.find(a => a.activityId === id);
        if (activity) {
          activity.subscribed = true;
          this.subscribedActivities = [...this.subscribedActivities, {
            activityId: activity.activityId,
            activityName: activity.activityName,
            activityDescription: activity.activityDescription,
            startTime: activity.startTime
          }];

          this.$message.success("订阅成功");
        }
      } else {
        this.$message.warning("您已订阅此活动");
      }
    },

    unsubscribe(id) {
      let subscriptions = JSON.parse(localStorage.getItem('activitySubscriptions') || '[]');
      subscriptions = subscriptions.filter(subId => subId !== id);
      localStorage.setItem('activitySubscriptions', JSON.stringify(subscriptions));

      this.subscribedActivities = this.subscribedActivities.filter(a => a.activityId !== id);
      const activity = this.activityList.find(a => a.activityId === id);
      if (activity) activity.subscribed = false;

      this.$message.success("已取消订阅");
    },

    // 查看详情 - 打开弹窗
    viewDetail(row) {
      this.currentActivity = { ...row };
      this.detailVisible = true;
    },

    handleDateClick(dateData) {
      const date = dateData.day;
      this.filterDateRange = [date, date];
      this.fetchActivities();
    },

    hasEventOnDate(dateString) {
      return this.activityList.some(activity => {
        return this.formatDate(activity.startTime) === dateString;
      });
    },

    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;
    },

    formatDateTime(datetime) {
      if (!datetime) return '';
      const d = new Date(datetime);
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
/* 调整日历样式确保完整显示 */
.compact-calendar {
  height: 420px; /* 确保能显示完整月份 */
}

.compact-calendar ::v-deep .el-calendar__header {
  padding: 5px 10px; /* 减小头部内边距 */
}

.compact-calendar ::v-deep .el-calendar__body {
  padding: 5px; /* 减小主体内边距 */
}

.compact-calendar ::v-deep .el-calendar-table .el-calendar-day {
  height: 35px; /* 确保日期单元格高度合适 */
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 调整订阅区域 */
.expanded-subscription {
  height: calc(100% - 440px); /* 增加订阅区域高度 */
  min-height: 300px; /* 最小高度保证 */
}

.subscription-scroll {
  height: calc(100% - 60px); /* 减去标题高度 */
}

.subscription-item {
  margin-bottom: 10px;
}

.subscription-item .description {
  margin: 8px 0;
  font-size: 13px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 限制描述为2行 */
  -webkit-box-orient: vertical;
}

.subscription-item .actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 其他样式保持不变 */
.app-container {
  padding: 20px;
  margin-left: 100px;
}

.calendar-card, .subscribed-card {
  margin-bottom: 20px;
}

.filter-container {
  float: right;
  margin-top: -5px;
  .el-date-editor {
    width: 280px;
    margin-right: 10px;
  }
}

.event-indicator {
  width: 6px;
  height: 6px;
  background-color: #F56C6C;
  border-radius: 50%;
  display: inline-block;
  margin-left: 2px;
}

.subscribed-card {
  max-height: 500px;
  overflow-y: auto;
}

.unsubscribe-btn {
  margin-left: 10px;
}

.empty-subscription {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}

/* 活动详情弹窗样式 */
.activity-detail {
  .detail-item {
    margin-bottom: 12px;
    display: flex;
    align-items: center;

    .detail-label {
      font-weight: bold;
      width: 100px;
      flex-shrink: 0;
    }

    .detail-value {
      flex-grow: 1;
    }
  }

  .detail-section {
    margin-top: 20px;

    .section-title {
      margin-bottom: 8px;
      color: #409EFF;
    }

    .section-content {
      padding: 10px;
      background-color: #f5f7fa;
      border-radius: 4px;
      line-height: 1.6;
    }
  }
}

.dialog-footer {
  text-align: right;
}
</style>
