<template>
  <div class="booking-container">
    <div class="search-card">
      <div class="card-header">
        <i class="el-icon-search"></i>
        <span>搜索条件</span>
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
        <el-table-column label="序号" width="80" align="center">
          <template v-slot="scope">
            <span class="index-badge">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="活动名称" align="center" prop="activityName" />
        <el-table-column label="活动类型" align="center" prop="activityType" width="200">
          <template slot-scope="scope">
            <el-tag :type="getActivityTypeTagType(scope.row.activityType)" effect="plain" class="activity-type-tag">
              {{ getActivityTypeName(scope.row.activityType) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="活动地点" align="center" prop="activityLocation" />
        <el-table-column label="组织单位" align="center" prop="organizer"  width="90"/>
        <el-table-column label="活动开始时间" align="center" prop="startTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="活动结束时间" align="center" prop="endTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="活动状态" align="center" width="90">
          <template slot-scope="scope">
            <el-tag :type="getActivityStatusTag(scope.row)" effect="dark" class="status-tag">
              {{ getActivityStatusText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" fixed="right" width="120">
          <template slot-scope="scope">
            <el-button
              v-if="getSignStatusText(scope.row) === '可报名'"
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
          <div class="section-content">
            <!-- 使用 v-html 渲染富文本内容 -->
            <div class="rich-text-content" v-html="selectedActivity.activityDescription || '暂无描述信息'"></div>
          </div>
        </div>
        <!-- 报名/取消按钮 -->
        <div class="signup-status">
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
      // 详情弹窗相关
      detailDialogVisible: false,
      selectedActivity: null,
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
    };
  },
  created() {
    this.getList();
  },
  async mounted() {
    // 检查报名状态
    await this.checkBookingStatus();
  },
  computed: {
    // 显示报名按钮的条件
    showSignUpButton() {
      if (!this.selectedActivity) return false;
      return this.getActivityStatusText(this.selectedActivity) === "报名进行中" &&
        !this.selectedActivity.isBooked &&
        this.selectedActivity.activityCapacity > 0;
    },

    // 显示报名已满提示的条件
    showFullCapacityAlert() {
      if (!this.selectedActivity) return false;
      return this.getActivityStatusText(this.selectedActivity) === "报名进行中" &&
        !this.selectedActivity.isBooked &&
        this.selectedActivity.activityCapacity <= 0;
    },

    // 显示取消报名按钮的条件
    showCancelButton() {
      if (!this.selectedActivity) return false;
      const status = this.getActivityStatusText(this.selectedActivity);
      return this.selectedActivity.isBooked &&
        (status === "报名进行中" || status === "报名未开始");
    },
  },
  methods: {
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
        '4': 'info',      // 社会责任与领军意识活动类 - 灰色
        '其他': ''        // 默认蓝色
      }
      return map[activityType] || 'info';
    },

    // 获取活动列表
    getList() {
      this.loading = true;
      // 构建查询参数，获取所有活动数据
      const allParams = {
        pageNum: 1,
        pageSize: 1000, // 获取足够大的数量
        activityName: this.queryParams.activityName,
        activityType: this.queryParams.activityType,
        activityLocation: this.queryParams.activityLocation,
        organizer: this.queryParams.organizer,
      };

      listActivities(allParams).then(response => {
        // 过滤出上个月、本月、下个月的活动
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
        // 获取活动列表后检查报名状态
        this.checkBookingStatus();
      });
    },

    // 过滤活动：只显示活动开始时间在本月的活动
    filterActivitiesByMonth(activities) {
      const now = new Date();
      const currentYear = now.getFullYear();
      const currentMonth = now.getMonth(); // 0-11

      console.log('当前时间范围过滤:', {
        currentMonth: `${currentYear}年${currentMonth + 1}月`
      });

      const filteredActivities = activities.filter(activity => {
        if (!activity.startTime) {
          console.log('活动缺少开始时间:', activity.activityName);
          return false;
        }

        const activityDate = new Date(activity.startTime);
        const activityYear = activityDate.getFullYear();
        const activityMonth = activityDate.getMonth();

        const isInRange = (activityYear === currentYear && activityMonth === currentMonth); // 只显示本月

        if (isInRange) {
          console.log('活动在范围内:', {
            name: activity.activityName,
            startTime: activity.startTime,
            month: `${activityYear}年${activityMonth + 1}月`
          });
        }

        return isInRange;
      });

      console.log(`过滤结果: 总共${activities.length}个活动，过滤后${filteredActivities.length}个活动`);
      return filteredActivities;
    },

    // 过滤可报名活动：在可报名时间内且人数未满
    filterAvailableActivities(activities) {
      const availableActivities = activities.filter(activity => {
        // 检查活动状态是否为"报名进行中"
        const status = this.getActivityStatusText(activity);
        const isSignUpActive = status === "报名进行中";

        // 检查人数是否未满
        const hasCapacity = activity.activityCapacity > 0;

        const isAvailable = isSignUpActive && hasCapacity;

        if (isAvailable) {
          console.log('可报名活动:', {
            name: activity.activityName,
            status: status,
            capacity: `${activity.activityTotalCapacity - activity.activityCapacity}/${activity.activityTotalCapacity}`,
            remaining: activity.activityCapacity
          });
        }

        return isAvailable;
      });

      console.log(`可报名过滤结果: 总共${activities.length}个活动，可报名${availableActivities.length}个活动`);
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
      if (!this.activitiesList || this.activitiesList.length === 0) return;

      const checkPromises = this.activitiesList.map(activity =>
        checkBookingSimple(activity.activityId, this.$store.state.user.name).then(res => {
          // 使用 Vue.set 确保响应式更新
          this.$set(activity, 'isBooked', res.data.isBooked);
          console.log(`活动 ${activity.activityName} 报名状态更新为: ${res.data.isBooked}`);
        }).catch(error => {
          console.error('检查报名状态失败:', error);
          this.$set(activity, 'isBooked', false);
        })
      );

      await Promise.all(checkPromises);
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

    // 获取报名状态文本
    getSignStatusText(row) {
      if (row.isBooked) return "已报名";

      const status = this.getActivityStatusText(row);
      const hasCapacity = row.activityCapacity > 0;

      if (["报名进行中"].includes(status) && hasCapacity) {
        return "可报名";
      }

      return "不可报名";
    },

    // 判断活动是否已满
    isActivityFull(row) {
      const status = this.getActivityStatusText(row);
      // 只有在报名进行中且容量为0时才显示已满
      return status === "报名进行中" && row.activityCapacity <= 0;
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
      const percentage = (activity.activityTotalCapacity - activity.activityCapacity) / activity.activityTotalCapacity;
      if (percentage >= 0.8) return 'capacity-high';
      if (percentage >= 0.5) return 'capacity-medium';
      return 'capacity-low';
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

    // 处理详情
    handleDetail(row) {
      this.selectedActivity = { ...row };
      this.detailDialogVisible = true;
    },

    // 处理详情弹窗关闭
    handleDetailClose(done) {
      this.detailDialogVisible = false;
      this.selectedActivity = null;
      done();
    },

    // 处理报名
    handleSignUp(row) {
      this.$confirm('确定要报名该活动吗？', '报名确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.submitSignUp(row);
      }).catch(() => {
        this.$message.info('已取消报名');
      });
    },

    // 处理取消报名
    handleCancel(row) {
      this.$confirm('确定要取消该活动报名吗？', '确认取消', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.submitCancelSignUp(row);
      }).catch(() => {
        this.$message.info('已取消取消报名操作');
      });
    },

    // 提交报名
    async submitSignUp(activity) {
      try {
        // 1. 更新活动容量
        await signUpCapacity(activity.activityId, activity.version);

        // 2. 添加报名记录
        await addBooking({
          activityId: activity.activityId,
          studentId: this.$store.state.user.name,
        });

        // 3. 更新活动状态
        const updatedActivity = {
          ...activity,
          activityCapacity: Math.max(activity.activityCapacity - 1, 0),
          version: activity.version + 1,
          isBooked: true // 标记为已报名
        };

        // 4. 更新活动列表
        const index = this.activitiesList.findIndex(a => a.activityId === activity.activityId);
        if (index !== -1) {
          // 使用 Vue.set 确保响应式更新
          this.$set(this.activitiesList, index, updatedActivity);
          console.log(`活动 ${activity.activityName} 报名状态已更新为已报名`);
        }

        this.$message.success("报名成功！");
        
        // 报名成功后关闭详情弹窗
        this.detailDialogVisible = false;
        this.selectedActivity = null;
        
        // 重新检查报名状态以确保数据同步
        await this.checkBookingStatus();
      } catch (error) {
        console.error("报名失败:", error);
        this.$message.error("报名失败: " + (error.msg || "请稍后重试"));
      }
    },

    // 提交取消报名
    async submitCancelSignUp(activity) {
      try {
        // 1. 删除报名记录
        await deleteBookingsByActivityAndStudent(
          activity.activityId,
          this.$store.state.user.name
        );

        // 2. 恢复活动容量
        await cancelSignUpCapacity(
          activity.activityId,
          activity.version
        );

        // 3. 更新活动状态
        const updatedActivity = {
          ...activity,
          activityCapacity: Math.min(activity.activityCapacity + 1, activity.activityTotalCapacity),
          version: activity.version + 1,
          isBooked: false // 标记为未报名
        };

        // 4. 更新活动列表
        const index = this.activitiesList.findIndex(a => a.activityId === activity.activityId);
        if (index !== -1) {
          // 使用 Vue.set 确保响应式更新
          this.$set(this.activitiesList, index, updatedActivity);
          console.log(`活动 ${activity.activityName} 报名状态已更新为未报名`);
        }

        this.$message.success("取消报名成功！");
        
        // 取消报名成功后关闭详情弹窗
        this.detailDialogVisible = false;
        this.selectedActivity = null;
        
        // 重新检查报名状态以确保数据同步
        await this.checkBookingStatus();
      } catch (error) {
        console.error("取消报名失败:", error);
        this.$message.error("取消报名失败: " + (error.msg || "请稍后重试"));
      }
    }
  }
};
</script>

<style scoped>
.booking-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: auto;
  height: auto;
  overflow: visible;
  position: relative;
  width: 100%;
  box-sizing: border-box;
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

.time-range-info {
  margin-left: auto;
  margin-right: 20px;
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
</style>
