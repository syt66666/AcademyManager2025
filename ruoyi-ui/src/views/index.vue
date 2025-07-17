<template>
  <div class="app-container">
    <!-- 主体布局 -->
    <el-row :gutter="24">
      <!-- 左侧面板 -->
      <el-col :span="8">
        <!-- 日历区域 -->
        <el-card class="calendar-card shadow-card">
          <div slot="header" class="card-header">
            <i class="el-icon-date"></i>
            <span>日历视图</span>
          </div>
          <el-calendar v-model="calendarDate" class="compact-calendar">
            <template #dateCell="{ data }">
              <div class="calendar-day" @click="handleDateClick(data)">
                <div class="day-number">{{ data.day.split('-').slice(2).join('-') }}</div>
                <div v-if="hasEventOnDate(data.day)" class="event-indicator"></div>
              </div>
            </template>
          </el-calendar>
        </el-card>

        <!-- 已订阅活动 -->
        <el-card class="subscribed-card expanded-subscription shadow-card">
          <div slot="header" class="card-header">
            <i class="el-icon-star-on"></i>
            <span>我的订阅</span>
          </div>
          <div class="subscription-container">
            <div v-if="subscribedActivities.length === 0" class="empty-subscription">
              <i class="el-icon-info"></i>
              <p>暂无订阅活动</p>
            </div>
            <el-scrollbar class="subscription-scroll">
              <el-timeline v-if="subscribedActivities.length > 0">
                <el-timeline-item
                  v-for="activity in subscribedActivities"
                  :key="activity.subscriptionId"
                  :timestamp="formatDate(activity.startTime)"
                  placement="top"
                >
                  <el-card shadow="hover" class="activity-card">
                    <h4 class="activity-title">{{ activity.activityName }}</h4>
                    <div class="activity-info">
                      <div><i class="el-icon-time"></i> {{ formatDate(activity.startTime) }} -
                        {{ formatDate(activity.endTime) }}
                      </div>
                      <div><i class="el-icon-tickets"></i> {{ formatDate(activity.activityStart) }} -
                        {{ formatDate(activity.activityDeadline) }}
                      </div>
                    </div>
                    <div class="activity-actions">
                      <el-tag type="success" size="mini" class="tag-subscribed">已订阅</el-tag>
                      <el-button
                        size="mini"
                        class="unsubscribe-btn"
                        @click="unsubscribe(activity.subscriptionId)"
                      >取消订阅
                      </el-button>
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </el-scrollbar>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧活动列表 -->
      <el-col :span="16">
        <el-card class="shadow-card">
          <div slot="header" class="card-header">
            <i class="el-icon-menu"></i>
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
                class="date-picker"
              />
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="fetchActivities"
                class="search-btn"
              >
                搜索
              </el-button>
            </div>
          </div>

          <el-table
            :data="activityList"
            style="width: 100%"
            v-loading="loading"
            class="activity-table"
            :header-cell-style="{background: '#f5f7fa', color: '#606266'}"
          >
            <el-table-column label="活动名称" prop="activityName" width="180">
              <template slot-scope="{ row }">
                <div class="activity-name">{{ row.activityName }}</div>
              </template>
            </el-table-column>
            <el-table-column label="活动地点" prop="activityLocation">
              <template slot-scope="{ row }">
                <div class="activity-location">
                  <i class="el-icon-location"></i> {{ row.activityLocation }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="报名时间" width="200">
              <template slot-scope="{ row }">
                <div class="time-range">
                  <i class="el-icon-time"></i> {{ formatDate(row.activityStart) }} 至
                  {{ formatDate(row.activityDeadline) }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="活动时间" width="200">
              <template slot-scope="{ row }">
                <div class="time-range">
                  <i class="el-icon-date"></i> {{ formatDate(row.startTime) }} 至 {{ formatDate(row.endTime) }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="{ row }">
                <el-tag :type="statusTagMap[row.status]" class="status-tag">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="{ row }">
                <div class="action-buttons">
                  <el-button
                    v-if="!row.subscribed"
                    type="text"
                    class="subscribe-btn"
                    @click="subscribeActivity(row.activityId)"
                  >
                    <i class="el-icon-star-off"></i> 订阅
                  </el-button>
                  <el-button
                    v-else
                    type="text"
                    disabled
                    class="subscribed-btn"
                  >
                    <i class="el-icon-star-on"></i> 已订阅
                  </el-button>
                  <el-button type="text" class="detail-btn" @click="viewDetail(row)">
                    <i class="el-icon-view"></i> 详情
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="fetchActivities"
            class="custom-pagination"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 活动详情弹窗 -->
    <el-dialog
      :title="currentActivity.activityName"
      :visible.sync="detailVisible"
      width="700px"
      :close-on-click-modal="false"
      class="activity-detail-dialog"
    >
      <div class="activity-detail">
        <div class="detail-header">
          <el-tag :type="statusTagMap[currentActivity.status]" size="medium" class="status-tag">
            {{ currentActivity.status }}
          </el-tag>
        </div>

        <el-divider></el-divider>

        <div class="detail-grid">
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-location"></i> 活动地点：</div>
            <div class="detail-value">{{ currentActivity.activityLocation }}</div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-office-building"></i> 组织单位：</div>
            <div class="detail-value">{{ currentActivity.organizer }}</div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-user"></i> 活动容量：</div>
            <div class="detail-value">
              <span :class="{
                'capacity-high': currentActivity.activityCapacity / currentActivity.activityTotalCapacity > 0.8,
                'capacity-medium': currentActivity.activityCapacity / currentActivity.activityTotalCapacity > 0.5 && currentActivity.activityCapacity / currentActivity.activityTotalCapacity <= 0.8,
                'capacity-low': currentActivity.activityCapacity / currentActivity.activityTotalCapacity <= 0.5
              }">
                {{ currentActivity.activityCapacity }}/{{ currentActivity.activityTotalCapacity }}人
              </span>
            </div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-alarm-clock"></i> 报名开始：</div>
            <div class="detail-value">{{ formatDateTime(currentActivity.activityStart) }}</div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-alarm-clock"></i> 报名截止：</div>
            <div class="detail-value">{{ formatDateTime(currentActivity.activityDeadline) }}</div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-time"></i> 活动开始：</div>
            <div class="detail-value">{{ formatDateTime(currentActivity.startTime) }}</div>
          </div>

          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-time"></i> 活动结束：</div>
            <div class="detail-value">{{ formatDateTime(currentActivity.endTime) }}</div>
          </div>
        </div>

        <el-divider></el-divider>

        <div class="detail-section">
          <h4 class="section-title"><i class="el-icon-document"></i> 活动描述</h4>
          <div class="section-content">{{ currentActivity.activityDescription }}</div>
        </div>

        <div class="detail-section">
          <h4 class="section-title"><i class="el-icon-warning"></i> 注意事项</h4>
          <div class="section-content">{{ currentActivity.notes }}</div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false" class="close-btn">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listActivities} from "@/api/system/activities";
import {addSubscription, delSubscription, listSubscription} from "@/api/system/subscription";
import Pagination from "@/components/Pagination";

export default {
  name: "ActivityDashboard",
  components: {Pagination},
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

          // 更新订阅状态
          this.updateSubscriptionStatus();
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

    // 获取已订阅活动
    fetchSubscribed() {
      const studentId = this.$store.state.user.name; // 从Vuex获取当前用户学号

      listSubscription({studentId}).then(response => {
        if (response.code === 200) {
          this.subscribedActivities = response.rows;
          console.log("已订阅活动:", this.subscribedActivities)
          // 更新活动列表中的订阅状态
          this.updateSubscriptionStatus();
        }
      }).catch(error => {
        console.error("获取订阅活动失败:", error);
      });
    },

    // 更新活动列表中的订阅状态
    updateSubscriptionStatus() {
      this.activityList.forEach(activity => {
        // 检查活动是否在订阅列表中
        activity.subscribed = this.subscribedActivities.some(
          sub => sub.activityId === activity.activityId
        );
      });
    },

// 在subscribeActivity方法中添加
    subscribeActivity(activityId) {
      const studentId = this.$store.state.user.name;
      // 先更新本地状态（乐观更新）
      const activityIndex = this.activityList.findIndex(a => a.activityId === activityId);
      if (activityIndex !== -1) {
        this.$set(this.activityList, activityIndex, {
          ...this.activityList[activityIndex],
          subscribed: true // 立即更新订阅状态
        });
      }

      addSubscription({studentId, activityId})
        .then(response => {
          if (response.code === 200) {
            this.$message.success("订阅成功");
            // 重新获取订阅列表（保持数据准确）
            this.fetchSubscribed();
          } else {
            // 失败时回退状态
            if (activityIndex !== -1) {
              this.$set(this.activityList, activityIndex, {
                ...this.activityList[activityIndex],
                subscribed: false
              });
            }
            this.$message.error("订阅失败: " + response.msg);
          }
        })
        .catch(error => {
          // 失败时回退状态
          if (activityIndex !== -1) {
            this.$set(this.activityList, activityIndex, {
              ...this.activityList[activityIndex],
              subscribed: false
            });
          }
          this.$message.error("订阅失败");
        });
    },

// 在unsubscribe方法中添加
    unsubscribe(subscriptionId) {
      // 先找到对应的活动ID
      const subscription = this.subscribedActivities.find(s => s.subscriptionId === subscriptionId);
      if (!subscription) return;

      const activityId = subscription.activityId;
      // 更新本地状态
      const activityIndex = this.activityList.findIndex(a => a.activityId === activityId);
      if (activityIndex !== -1) {
        this.$set(this.activityList, activityIndex, {
          ...this.activityList[activityIndex],
          subscribed: false // 立即更新为未订阅
        });
      }

      delSubscription(subscriptionId)
        .then(response => {
          if (response.code === 200) {
            this.$message.success("已取消订阅");
            this.fetchSubscribed(); // 更新订阅列表
          } else {
            // 失败时恢复状态
            if (activityIndex !== -1) {
              this.$set(this.activityList, activityIndex, {
                ...this.activityList[activityIndex],
                subscribed: true
              });
            }
            this.$message.error("取消订阅失败: " + response.msg);
          }
        })
        .catch(error => {
          // 失败时恢复状态
          if (activityIndex !== -1) {
            this.$set(this.activityList, activityIndex, {
              ...this.activityList[activityIndex],
              subscribed: true
            });
          }
          this.$message.error("取消订阅失败");
        });
    },

    // 查看详情 - 打开弹窗
    viewDetail(row) {
      this.currentActivity = {...row};
      this.detailVisible = true;
    },

    handleDateClick(dateData) {
      const date = dateData.day;
      this.filterDateRange = [date, null];
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
/* 修改订阅区域样式 */
.subscribed-card {
  .expanded-subscription {
    min-height: 250px; /* 最小高度保持 */
  }

  /* 新增：固定高度的容器 */
  .subscription-container {
    height: 180px; /* 固定高度 */
    display: flex;
    flex-direction: column;
  }

  .subscription-scroll {
    flex: 1; /* 占据剩余空间 */
    overflow: hidden; /* 隐藏原生滚动条 */
  }

  /* 无订阅内容时的样式 */
  .empty-subscription {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 30px 0;
    color: #909399;

    i {
      font-size: 40px;
      margin-bottom: 10px;
      color: #dcdfe6;
    }

    p {
      margin: 0;
      font-size: 14px;
    }
  }

  .activity-card {
    margin-bottom: 12px;
    border-radius: 6px;
    border: 1px solid #ebeef5;
  }
}
/* 全局样式 */
.app-container {
  padding: 20px;
  margin-top: 50px;
  margin-left: 100px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 50px);
}

/* 卡片样式 */
.shadow-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  border: none;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #303133;

  i {
    margin-right: 8px;
    font-size: 18px;
    color: #409EFF;
  }
}

/* 日历区域 */
/* 日历区域优化 */
.calendar-card {
  margin-bottom: 24px;
}

.compact-calendar {
  height: 380px;
}

/* 关键修改：调整日历内部样式 */
::v-deep .el-calendar__header {
  padding: 8px 12px;
  height: 40px; /* 减小头部高度 */
}

::v-deep .el-calendar__body {
  padding: 0;
  height: calc(100% - 40px); /* 增加主体高度 */
}

::v-deep .el-calendar-table {
  height: 100%;
}

::v-deep .el-calendar-table thead {
  height: 30px; /* 减小表头高度 */
}

::v-deep .el-calendar-table thead th {
  padding: 5px 0;
}

::v-deep .el-calendar-table tr {
  height: calc((100% - 30px) / 6); /* 平均分配6行高度 */
}

::v-deep .el-calendar-table .el-calendar-day {
  height: 100%; /* 单元格占满行高 */
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.calendar-day {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.day-number {
  font-size: 14px;
  font-weight: 500;
}

.event-indicator {
  width: 6px;
  height: 6px;
  background-color: #F56C6C;
  border-radius: 50%;
  margin-top: 2px;
}

/* 订阅区域 */
.subscribed-card {
  .expanded-subscription {
    min-height: 300px;
  }

  .subscription-scroll {
    height: calc(100% - 60px);
    max-height: 420px;
  }

  .activity-card {
    margin-bottom: 12px;
    border-radius: 6px;
    border: 1px solid #ebeef5;

    .activity-title {
      margin: 0 0 10px 0;
      font-size: 15px;
      color: #303133;
      font-weight: 600;
    }

    .activity-info {
      font-size: 13px;
      color: #606266;
      line-height: 1.6;

      i {
        margin-right: 5px;
        color: #909399;
      }
    }

    .activity-actions {
      margin-top: 10px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .tag-subscribed {
        font-weight: 500;
      }

      .unsubscribe-btn {
        margin-left: 10px;
        padding: 5px 8px;
        font-size: 12px;
      }
    }
  }

  .empty-subscription {
    text-align: center;
    padding: 30px 0;
    color: #909399;

    i {
      font-size: 40px;
      margin-bottom: 10px;
      color: #dcdfe6;
    }

    p {
      margin: 0;
      font-size: 14px;
    }
  }
}

/* 活动列表区域 */
.filter-container {
  margin-left: 200px;
  float: right;
  display: flex;
  align-items: center;

  .date-picker {
    width: 300px;
    margin-right: 12px;
  }

  .search-btn {
    padding: 9px 15px;
  }
}

.activity-table {
  ::v-deep th {
    font-weight: 600;
  }

  .activity-name {
    font-weight: 500;
    color: #303133;
  }

  .activity-location {
    display: flex;
    align-items: center;

    i {
      margin-right: 5px;
      color: #409EFF;
    }
  }

  .time-range {
    display: flex;
    align-items: center;
    font-size: 13px;

    i {
      margin-right: 5px;
      color: #909399;
    }
  }

  .status-tag {
    font-weight: 500;
    padding: 0 8px;
    height: 26px;
    line-height: 26px;
  }

  .action-buttons {
    display: flex;

    .subscribe-btn {
      color: #409EFF;

      i {
        margin-right: 3px;
      }
    }

    .subscribed-btn {
      color: #67C23A;

      i {
        margin-right: 3px;
      }
    }

    .detail-btn {
      color: #909399;

      i {
        margin-right: 3px;
      }
    }
  }
}

.custom-pagination {
  margin-top: 20px;
}

/* 活动详情弹窗 */
.activity-detail-dialog {
  ::v-deep .el-dialog {
    border-radius: 8px;
    overflow: hidden;
  }

  ::v-deep .el-dialog__header {
    background: linear-gradient(120deg, #409EFF, #64b5ff);
    padding: 15px 20px;

    .el-dialog__title {
      color: white;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      top: 15px;

      .el-dialog__close {
        color: white;
      }
    }
  }

  .detail-header {
    margin-bottom: 15px;
  }

  .detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
    margin-bottom: 20px;
  }

  .detail-item {
    display: flex;
    align-items: flex-start;

    .detail-label {
      width: 100px;
      flex-shrink: 0;
      font-weight: 500;
      color: #606266;
      display: flex;
      align-items: center;

      i {
        margin-right: 6px;
        color: #409EFF;
      }
    }

    .detail-value {
      flex-grow: 1;
      color: #303133;

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
    }
  }

  .detail-section {
    margin-top: 20px;

    .section-title {
      margin-bottom: 10px;
      color: #409EFF;
      font-weight: 500;
      display: flex;
      align-items: center;

      i {
        margin-right: 8px;
      }
    }

    .section-content {
      padding: 12px;
      background-color: #f8fafc;
      border-radius: 4px;
      line-height: 1.7;
      color: #606266;
      border-left: 3px solid #409EFF;
    }
  }

  .dialog-footer {
    text-align: center;
    padding: 15px 0 5px;

    .close-btn {
      width: 120px;
      padding: 10px 0;
    }
  }
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .el-col {
    width: 100%;
    max-width: 100%;
  }

  .el-col-8, .el-col-16 {
    flex: 0 0 100%;
    max-width: 100%;
  }

  .calendar-card, .subscribed-card {
    margin-bottom: 20px;
  }
}
</style>
