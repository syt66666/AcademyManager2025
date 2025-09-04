<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="auto" class="query-form">
      <div class="query-row">
        <el-form-item label="活动名称" prop="activityName" class="search-item">
          <el-input
            v-model="queryParams.activityName"
            placeholder="请输入活动名称"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="handleQuery"
            class="search-input"
          />
        </el-form-item>
        <el-form-item label="组织单位" prop="organizer" class="search-item">
          <el-input
            v-model="queryParams.organizer"
            placeholder="请输入组织单位"
            clearable
            prefix-icon="el-icon-office-building"
            @keyup.enter.native="handleQuery"
            class="search-input"
          />
        </el-form-item>
        <el-form-item label="活动类型" prop="activityType" class="search-item">
          <el-select v-model="queryParams.activityType" clearable placeholder="请选择活动类型" class="search-input">
            <el-option 
              v-for="type in availableActivityTypes" 
              :key="type" 
              :label="type" 
              :value="type"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="活动状态" prop="status" class="search-item">
          <el-select v-model="queryParams.status" clearable class="status-select" placeholder="全部状态">
            <el-option label="报名未开始" value="未开始"/>
            <el-option label="报名进行中" value="可报名"/>
            <el-option label="报名已截止" value="已截止"/>
            <el-option label="活动进行中" value="进行中"/>
            <el-option label="活动已结束" value="已结束"/>
          </el-select>
        </el-form-item>
        <el-form-item class="search-item">
          <el-checkbox v-model="queryParams.onlyAvailable" @change="handleOnlyAvailableChange">
            <span class="checkbox-label">只显示可报名活动</span>
          </el-checkbox>
        </el-form-item>
        <el-form-item class="search-item">
          <el-button-group class="action-buttons">
            <el-button type="primary" icon="el-icon-search" @click="handleQuery" class="search-button">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery" class="refresh-button">重置</el-button>
          </el-button-group>
        </el-form-item>
      </div>
    </el-form>

    <!-- 活动列表 -->
    <el-table v-loading="loading" :data="activitiesList" style="width: 100%" class="enhanced-table">
      <el-table-column label="序号" width="70" align="center">
        <template v-slot="scope">
          <span class="index-badge">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="活动名称" align="center" prop="activityName" width="180">
        <template slot-scope="scope">
          <div class="activity-name">{{ scope.row.activityName }}</div>
        </template>
      </el-table-column>
      <el-table-column label="活动类型" align="center" prop="activityType" width="180">
        <template slot-scope="scope">
          <div class="activity-type">{{ scope.row.activityType }}</div>
        </template>
      </el-table-column>
      <el-table-column label="活动地点" align="center" prop="activityLocation" width="120" />
      <el-table-column label="组织单位" align="center" prop="organizer" width="120"/>
      <el-table-column label="报名时间" align="center" >
        <template slot-scope="scope">
          <div class="time-range">
            <i class="el-icon-time"></i> {{ formatDateTime(scope.row.activityStart) }} 至 {{ formatDateTime(scope.row.activityDeadline) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="活动时间" align="center" >
        <template slot-scope="scope">
          <div class="time-range">
            <i class="el-icon-date"></i> {{ formatDateTime(scope.row.startTime) }} 至 {{ formatDateTime(scope.row.endTime) }}
          </div>
        </template>
      </el-table-column>

      <!-- 活动状态列 -->
      <el-table-column label="活动状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="getActivityStatusTag(scope.row)" effect="dark" class="status-tag">
            {{ getActivityStatusText(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="报名人数" align="center" width="100">
        <template #default="scope">
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
                  {{ scope.row.activityTotalCapacity - scope.row.activityCapacity }}/{{ scope.row.activityTotalCapacity }}
                </span>
            </div>
          </div>
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column label="操作" align="center" fixed="right" width="150">
        <template slot-scope="scope">
          <div class="action-buttons">
            <!-- 详情按钮 -->
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleDetail(scope.row)"
              class="action-button detail-button"
            >详情</el-button>

            <!-- 报名按钮 - 三种状态 -->
            <el-button
              v-if="showSignUpButton(scope.row)"
              size="mini"
              type="text"
              icon="el-icon-check"
              @click="handleSignUp(scope.row)"
              :disabled="isSignUpDisabled(scope.row)"
              :class="[
          'action-button',
          'signup-button',
          {
            'disabled-button': isSignUpDisabled(scope.row),
            'full-button': isCapacityFull(scope.row) && isSignUpAllowed(scope.row)
          }
        ]"
            >
              {{ getSignUpButtonText(scope.row) }}
            </el-button>

            <!-- 取消按钮 -->
            <el-button
              v-if="showCancelButton(scope.row)"
              size="mini"
              type="text"
              icon="el-icon-close"
              @click="handleCancel(scope.row)"
              class="action-button cancel-button"
            >取消</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
      class="custom-pagination"
    />

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
          <el-tag :type="getActivityStatusTag(currentActivity)" size="medium" class="status-tag">
            {{ getActivityStatusText(currentActivity) }}
          </el-tag>
          <el-tag :type="getSignStatusTag(currentActivity)" size="medium" effect="light" class="sign-tag">
            {{ getSignStatusText(currentActivity) }}
          </el-tag>
        </div>

        <el-divider></el-divider>


        <div class="detail-grid">
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-location"></i> 活动类型：</div>
            <div class="detail-value">{{ currentActivity.activityType }}</div>
          </div>
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
                'capacity-high': (currentActivity.activityTotalCapacity - currentActivity.activityCapacity) / currentActivity.activityTotalCapacity > 0.8,
                'capacity-medium': (currentActivity.activityTotalCapacity - currentActivity.activityCapacity) / currentActivity.activityTotalCapacity > 0.5 && (currentActivity.activityTotalCapacity - currentActivity.activityCapacity) / currentActivity.activityTotalCapacity <= 0.8,
                'capacity-low': (currentActivity.activityTotalCapacity - currentActivity.activityCapacity) / currentActivity.activityTotalCapacity <= 0.5
              }">
                {{ currentActivity.activityTotalCapacity - currentActivity.activityCapacity }}/{{ currentActivity.activityTotalCapacity }}人
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

      <!-- 底部按钮 -->
      <div slot="footer" class="dialog-footer">
        <el-button
          v-if="showSignUpButton(currentActivity)"
          type="success"
          icon="el-icon-circle-check"
          @click="handleSignUp(currentActivity)"
          class="action-button"
        >立即报名</el-button>
        <el-button
          v-if="showCancelButton(currentActivity)"
          type="danger"
          icon="el-icon-circle-close"
          @click="handleCancel(currentActivity)"
          class="action-button"
        >取消报名</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listActivities, signUpCapacity, cancelSignUpCapacity } from "@/api/system/activities";
import { addBooking, deleteBookingsByActivityAndStudent } from "@/api/system/bookings";
import { parseTime } from "@/utils/ruoyi";
import { checkBookingSimple } from "@/api/system/bookings";

export default {
  name: "ActivitiesSignUp",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      activitiesList: [],
      // 可用的活动类型列表
      availableActivityTypes: [],
      detailVisible: false,
      currentActivity: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        activityName: null,
        activityLocation: null,
        organizer: null,
        activityType: null,
        status: null,
        onlyAvailable: false,
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 判断报名按钮是否禁用 */
    isSignUpDisabled(row) {
      // 如果容量已满或活动状态不允许报名，则禁用按钮
      return this.isCapacityFull(row) || !this.isSignUpAllowed(row);
    },

    /** 检查容量是否已满 */
    isCapacityFull(row) {
      return row.activityCapacity <= 0;
    },

    /** 检查活动状态是否允许报名 */
    isSignUpAllowed(row) {
      return this.getActivityStatusText(row) === "报名进行中";
    },

    /** 获取报名按钮文本 */
    getSignUpButtonText(row) {
      if (this.isSignUpAllowed(row) && this.isCapacityFull(row)) {
        return "已满";
      }
      return "报名";
    },
    /** 新增方法：计算容量百分比 */
    calculateCapacityPercentage(row) {
      if (!row.activityTotalCapacity || row.activityTotalCapacity <= 0) return 0;
      const used = row.activityTotalCapacity - row.activityCapacity;
      return Math.round((used / row.activityTotalCapacity) * 100);
    },

    /** 新增方法：获取进度条颜色 */
    getProgressColor(percentage) {
      if (percentage >= 80) return '#f87171';
      if (percentage >= 50) return '#fbbf24';
      return '#4ade80';
    },

    /** 新增方法：获取容量文字样式类 */
    getCapacityClass(row) {
      const percentage = this.calculateCapacityPercentage(row);
      if (percentage >= 80) return 'capacity-high';
      if (percentage >= 50) return 'capacity-medium';
      return 'capacity-low';
    },
    /** 查询活动列表和预约列表 */
    async getList() {
      this.loading = true;
      try {
        // 1. 获取活动列表
        const response = await listActivities(this.queryParams);
        let activityList = response.rows.map(activity => ({
          ...activity,
          isBooked: false
        }));

        this.total = response.total;

        // 2. 查询每个活动是否已报名
        const checkPromises = activityList.map(activity =>
          checkBookingSimple(activity.activityId, this.$store.state.user.name).then(res => {
            activity.isBooked = res.data.isBooked;
          })
        );
        await Promise.all(checkPromises);

        // ✅ 3. 若勾选了只显示可报名活动，进行筛选：
        if (this.queryParams.onlyAvailable) {
          activityList = activityList.filter(activity => {
            const notBooked = !activity.isBooked;
            const hasCapacity = activity.activityCapacity > 0;
            const isOngoing = this.getActivityStatusText(activity) === "报名进行中";
            return notBooked && hasCapacity && isOngoing;
          });
        }

        this.activitiesList = activityList;
        
        // 更新可用的活动类型列表
        this.updateAvailableActivityTypes();

      } catch (error) {
        console.error("获取数据失败:", error);
      } finally {
        this.loading = false;
      }
    },

    /** 处理搜索 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 搜索报名状态 */
    handleStatusFilterChange() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 处理只显示可报名活动 */
    handleOnlyAvailableChange() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 重置搜索 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },

    /** 格式化日期时间 */
    formatDateTime(time) {
      return parseTime(time, "{y}-{m}-{d} {h}:{i}");
    },

    /** 获取活动状态文本 */
    getActivityStatusText(row) {
      const now = new Date();
      const start = new Date(row.startTime);
      const end = new Date(row.endTime);
      const deadline = new Date(row.activityDeadline);
      const activityStart = new Date(row.activityStart);

      if (now < activityStart) return "报名未开始";
      if (now < deadline && now >= activityStart) return "报名进行中";
      if (now >= deadline && now < start) return "报名已截止";
      if (now >= start && now <= end) return "活动进行中";
      if (now > end) return "活动已结束";
      return row.status || "未知";
    },

    /** 获取活动状态标签类型 */
    getActivityStatusTag(row) {
      const status = this.getActivityStatusText(row);
      switch (status) {
        case "报名未开始": return "info";
        case "报名进行中": return "success";
        case "报名已截止": return "warning";
        case "活动进行中": return "primary";
        case "活动已结束": return "";
        default: return "danger";
      }
    },

    /** 更新可用的活动类型列表 */
    updateAvailableActivityTypes() {
      const types = new Set();
      this.activitiesList.forEach(item => {
        if (item.activityType) {
          types.add(item.activityType);
        }
      });
      
      // 如果没有活动类型数据，提供默认选项
      if (types.size === 0) {
        types.add('学术讲座');
        types.add('实践活动');
        types.add('文体活动');
        types.add('志愿服务');
        types.add('竞赛活动');
        types.add('其他');
      }
      
      // 转换为数组并排序
      this.availableActivityTypes = Array.from(types).sort();
    },

    /** 获取报名状态文本 */
    getSignStatusText(row) {
      if (row.isBooked) return "已报名";

      const status = this.getActivityStatusText(row);
      const hasCapacity = row.activityCapacity > 0;

      if (["报名进行中"].includes(status) && hasCapacity) {
        return "可报名";
      }

      return "不可报名";
    },

    /** 获取报名状态标签类型 */
    getSignStatusTag(row) {
      const status = this.getSignStatusText(row);
      switch (status) {
        case "已报名": return "success";
        case "可报名": return "warning";
        default: return "info";
      }
    },

    /** 是否显示报名按钮 */
    showSignUpButton(row) {
      return this.getSignStatusText(row) !== "已报名";
    },

    /** 是否显示取消按钮 */
    showCancelButton(row) {
      return this.getSignStatusText(row) === "已报名" &&
        ["报名进行中"].includes(this.getActivityStatusText(row));
    },

    /** 查看详情 */
    handleDetail(row) {
      this.currentActivity = Object.assign({}, row);
      this.detailVisible = true;
    },

    /** 报名活动 */
    handleSignUp(row) {
      this.$confirm("确定要报名吗？", "确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        signUpCapacity(row.activityId, row.version)
          .then(() => {
            this.$message.success("报名成功！");
            row.isBooked = true;
            return addBooking({
              activityId: row.activityId,
              studentId: this.$store.state.user.name
            });
          })
          .then(() => this.getList())
          .catch(error => {
            this.$message.error(error.msg || "报名失败,请刷新界面重试");
          });
      });
    },

    /** 取消报名 */
    handleCancel(row) {
      this.$confirm("确定要取消报名吗？", "确认取消", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        cancelSignUpCapacity(row.activityId, row.version)
          .then(() => {
            this.$message.info("已取消报名");
            row.isBooked = false;
            return deleteBookingsByActivityAndStudent(
              row.activityId,
              this.$store.state.user.name
            );
          })
          .then(() => this.getList())
          .catch(error => {
            this.$message.error(error.msg || "取消失败");
          });
      });
    }
  }
};
</script>

<style scoped>
/* 禁用状态按钮样式 */
.disabled-button {
  opacity: 0.6;
  cursor: not-allowed !important;
}

/* 已满状态按钮样式 */
.full-button {
  color: #F56C6C !important;
  background-color: rgba(245, 108, 108, 0.1) !important;
}

/* 不可报名状态按钮样式 */
.disabled-button.signup-button:not(.full-button) {
  color: #909399 !important;
  background-color: rgba(144, 147, 153, 0.1) !important;
}
/* 查询表单 - 紧凑布局 */
.query-form {
  padding: 16px;
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
}

.query-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.search-item {
  margin-bottom: 0;
  flex-grow: 1;
}

.search-input {
  transition: all 0.3s ease;
}

.search-input:hover {
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.status-select {
  min-width: 140px;
}

.checkbox-label {
  font-size: 14px;
  color: #606266;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.search-button {
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  border: none;
  padding: 9px 18px;
  border-radius: 4px;
  transition: all 0.3s;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.4);
}

.refresh-button {
  background-color: #f0f2f5;
  border: none;
  padding: 9px 18px;
  color: #606266;
  border-radius: 4px;
  transition: all 0.3s;
}

.refresh-button:hover {
  background-color: #e4e7ed;
  color: #333;
}
/* 全局样式 */
.app-container {
  margin-left: 100px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 查询表单 */
.query-form {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 6px;
  margin-bottom: 15px;
}

.query-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 10px;
}

.query-row .el-form-item {
  margin-bottom: 0;
  margin-right: 15px;
}

/* 表格样式优化 */
.enhanced-table {
  width: 100%;
  margin-top: 15px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.activity-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.time-range {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #606266;
}

.time-range i {
  margin-right: 5px;
  color: #909399;
}

.status-tag {
  font-weight: 600;
  padding: 0 10px;
  height: 28px;
  line-height: 28px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 6px;
}

.action-button {
  padding: 5px 8px;
  font-size: 12px;
  border-radius: 4px;
}

.detail-button { color: #409EFF; }
.signup-button { color: #67C23A; }
.cancel-button { color: #F56C6C; }

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

/* 活动详情弹窗 */
.activity-detail-dialog ::v-deep .el-dialog {
  border-radius: 8px;
  overflow: hidden;
}

.activity-detail-dialog ::v-deep .el-dialog__header {
  background: linear-gradient(120deg, #409EFF, #64b5ff);
  padding: 15px 20px;
}

.activity-detail-dialog ::v-deep .el-dialog__title {
  color: white;
  font-weight: 600;
}

.activity-detail-dialog ::v-deep .el-dialog__headerbtn {
  top: 15px;
}

.activity-detail-dialog ::v-deep .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.detail-header {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
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
}

.detail-label {
  width: 100px;
  flex-shrink: 0;
  font-weight: 500;
  color: #606266;
  display: flex;
  align-items: center;
}

.detail-label i {
  margin-right: 6px;
  color: #409EFF;
}

.detail-value {
  flex-grow: 1;
  color: #303133;
}

.detail-section {
  margin-top: 20px;
}

.section-title {
  margin-bottom: 10px;
  color: #409EFF;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
}

.section-content {
  padding: 12px;
  background-color: #f8fafc;
  border-radius: 4px;
  line-height: 1.7;
  color: #606266;
  border-left: 3px solid #409EFF;
}

.dialog-footer {
  text-align: center;
  padding: 15px 0 5px;
}

.close-btn {
  width: 120px;
  padding: 10px 0;
}
</style>
