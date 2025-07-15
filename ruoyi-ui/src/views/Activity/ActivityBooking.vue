<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="活动名称" prop="activityName">
        <el-input
          v-model="queryParams.activityName"
          placeholder="请输入活动名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="活动地点" prop="activityLocation">
        <el-input
          v-model="queryParams.activityLocation"
          placeholder="请输入活动地点"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="组织者" prop="organizer">
        <el-input
          v-model="queryParams.organizer"
          placeholder="请输入组织者"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="活动状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择活动状态" clearable>
          <el-option label="未开始" value="0" />
          <el-option label="已截止" value="1" />
          <el-option label="进行中" value="2" />
          <el-option label="已结束" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 活动列表 -->
    <el-table v-loading="loading" :data="activitiesList" style="width: 100%">
      <el-table-column label="活动名称" align="center" prop="activityName" width="180" />
      <el-table-column label="活动地点" align="center" prop="activityLocation" width="120" />

      <!-- 活动状态列 -->
      <el-table-column label="活动状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="getActivityStatusTag(scope.row)">
            {{ getActivityStatusText(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 报名状态列 -->
      <el-table-column label="报名状态" align="center" >
        <template slot-scope="scope">
          <el-tag :type="getSignStatusTag(scope.row)" effect="plain">
            {{ getSignStatusText(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="活动容量剩余情况" align="center">
        <template #default="scope">
          {{ scope.row.activityTotalCapacity-scope.row.activityCapacity }}/{{ scope.row.activityTotalCapacity }}
        </template>
      </el-table-column>

      <el-table-column label="开始时间" align="center" prop="startTime" >
        <template slot-scope="scope">
          <span>{{ formatDateTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" >
        <template slot-scope="scope">
          <span>{{ formatDateTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报名截止" align="center" prop="activityDeadline" >
        <template slot-scope="scope">
          <span>{{ formatDateTime(scope.row.activityDeadline) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="组织单位" align="center" prop="organizer"  />

      <!-- 操作列 -->
      <el-table-column label="操作" align="center"  fixed="right">
        <template slot-scope="scope">
          <!-- 详情按钮 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>

          <!-- 报名/取消按钮 -->
          <el-button
            v-if="showSignUpButton(scope.row)"
            size="mini"
            type="text"
            icon="el-icon-check"
            style="color: #67C23A"
            @click="handleSignUp(scope.row)"
          >报名</el-button>

          <el-button
            v-if="showCancelButton(scope.row)"
            size="mini"
            type="text"
            icon="el-icon-close"
            style="color: #F56C6C"
            @click="handleCancel(scope.row)"
          >取消</el-button>
        </template>
      </el-table-column>

      <!-- 展开详情 -->
      <el-table-column type="expand" width="60" align="center">
        <template slot-scope="props">
          <div class="expand-container">
            <div class="expand-row">
              <div class="expand-label">活动描述：</div>
              <div class="expand-content">{{ props.row.activityDescription }}</div>
            </div>
            <div class="expand-row">
              <div class="expand-label">注意事项：</div>
              <div class="expand-content">{{ props.row.notes }}</div>
            </div>
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
    />

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
          <el-tag :type="getActivityStatusTag(currentActivity)" size="medium">
            {{ getActivityStatusText(currentActivity) }}
          </el-tag>
        </div>

        <div class="detail-item">
          <span class="detail-label">报名状态：</span>
          <el-tag :type="getSignStatusTag(currentActivity)" size="medium" effect="light">
            {{ getSignStatusText(currentActivity) }}
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
          <span class="detail-value">{{ currentActivity.activityCapacity }}人</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">开始时间：</span>
          <span class="detail-value">{{ formatDateTime(currentActivity.startTime) }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">结束时间：</span>
          <span class="detail-value">{{ formatDateTime(currentActivity.endTime) }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">报名截止：</span>
          <span class="detail-value">{{ formatDateTime(currentActivity.activityDeadline) }}</span>
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

        <div v-if="showSignUpButton(currentActivity)" class="detail-actions">
          <el-button
            type="success"
            icon="el-icon-check"
            @click="handleSignUp(currentActivity)"
          >立即报名</el-button>
        </div>

        <div v-if="showCancelButton(currentActivity)" class="detail-actions">
          <el-button
            type="danger"
            icon="el-icon-close"
            @click="handleCancel(currentActivity)"
          >取消报名</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listActivities, signUpActivity, cancelSignUp,signUpCapacity,cancelSignUpCapacity } from "@/api/system/activities";
import {addBooking, deleteBookings, deleteBookingsByActivityAndStudent} from "@/api/system/bookings";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "Activities",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      activitiesList: [],
      detailVisible: false,
      currentActivity: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        activityName: null,
        activityLocation: null,
        organizer: null,
        status: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询活动列表 */
    getList() {
      this.loading = true;
      // 后端API：获取活动列表
      listActivities(this.queryParams).then(response => {
        this.activitiesList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    /** 处理搜索 */
    handleQuery() {
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

      if (now < deadline && now < start) return "未开始";
      if (now >= deadline && now < start) return "已截止";
      if (now >= start && now <= end) return "进行中";
      if (now > end) return "已结束";
      return row.status || "未知";
    },

    /** 获取活动状态标签类型 */
    getActivityStatusTag(row) {
      const status = this.getActivityStatusText(row);
      switch (status) {
        case "未开始":
          return "info";
        case "进行中":
          return "success";
        case "已截止":
          return "warning";
        case "已结束":
          return "";
        default:
          return "danger";
      }
    },

    /** 获取报名状态文本 */
    getSignStatusText(row) {
      // 已报名的情况优先
      if (row.signedUp) return "已报名";

      // 当前时间状态（未开始/进行中才可能报名）
      const status = this.getActivityStatusText(row);

      // 判断是否还有剩余容量
      const hasCapacity = row.activityCapacity > 0;

      // 只有同时满足时间 + 容量 才能报名
      if (["未开始", "进行中"].includes(status) && hasCapacity) {
        return "可报名";
      }

      return "不可报名";
    },


    /** 获取报名状态标签类型 */
    getSignStatusTag(row) {
      const status = this.getSignStatusText(row);
      switch (status) {
        case "已报名":
          return "success";
        case "可报名":
          return "warning";
        default:
          return "info";
      }
    },

    /** 是否显示报名按钮 */
    showSignUpButton(row) {
      return this.getSignStatusText(row) === "可报名";
    },

    /** 是否显示取消按钮 */
    showCancelButton(row) {
      return this.getSignStatusText(row) === "已报名" &&
        ["未开始", "进行中"].includes(this.getActivityStatusText(row));
    },

    /** 查看详情 */
    handleDetail(row) {
      this.currentActivity = Object.assign({}, row);
      this.detailVisible = true;
    },

    /** 报名活动 */
    handleSignUp(row) {
      // 后端API：用户报名活动
      this.$confirm("确定要报名吗？", "确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
      signUpCapacity(row.activityId)

        .then(response => {
          this.$message.success("报名成功！");
          row.activityCapacity--
          row.signedUp = true; // 更新报名状态
          addBooking({activityId: row.activityId,studentId:this.$store.state.user.id})
          //this.getList();
        })
        .catch(error => {
          this.$message.error(error.msg || "报名失败");
        });
      }).catch(() => {
      });
    },

    /** 取消报名 */
    handleCancel(row) {
      this.$confirm("确定要取消报名吗？取消后如需参加需重新报名。", "确认取消", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        // 后端API：取消活动报名
        cancelSignUpCapacity(row.activityId)

          .then(response => {
            this.$message.info("已取消报名");
            row.activityCapacity++
            row.signedUp = false; // 更新报名状态

            // 使用新方法删除预约记录
            deleteBookingsByActivityAndStudent(row.activityId, this.$store.state.user.id)
            //this.getList();
          })
          .catch(error => {
            this.$message.error(error.msg || "取消失败");
          });
      }).catch(() => {
      });
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
  margin-left: 100px;
}

/* 详情弹窗样式 */
.activity-detail {
  padding: 10px 20px;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  line-height: 32px;
}

.detail-label {
  width: 80px;
  font-weight: bold;
  color: #606266;
}

.detail-value {
  flex: 1;
  color: #303133;
}

.detail-section {
  margin: 20px 0;
}

.section-title {
  color: #409EFF;
  margin-bottom: 10px;
  padding-bottom: 5px;
  border-bottom: 1px dashed #EBEEF5;
}

.section-content {
  line-height: 1.8;
  color: #606266;
  padding: 0 10px;
}

.detail-actions {
  margin-top: 30px;
  text-align: center;
}

/* 表格展开详情样式 */
.expand-container {
  padding: 10px 40px;
}

.expand-row {
  display: flex;
  margin-bottom: 15px;
}

.expand-label {
  width: 80px;
  font-weight: bold;
  color: #606266;
}

.expand-content {
  flex: 1;
  line-height: 1.6;
  color: #303133;
  white-space: pre-line;
}
</style>
