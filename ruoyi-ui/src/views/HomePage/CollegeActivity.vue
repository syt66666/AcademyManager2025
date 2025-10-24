<template>
  <div class="app-container">
    <!-- 加载动画 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner">
        <i class="el-icon-loading"></i>
        <p>加载中...</p>
      </div>
    </div>

    <!-- 主内容 -->
    <div v-show="!loading" class="content-wrapper fade-in-content">
      <!-- 统计概览卡片 -->
      <!-- <div class="stats-overview fade-in" style="animation-delay: 0.1s">
        <div class="overview-card">
          <div class="card-icon">
            <i class="el-icon-s-promotion"></i>
          </div>
          <div class="card-content">
            <div class="card-title">总活动数</div>
            <div class="card-value">{{ activityStats.totalActivities }}</div>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon">
            <i class="el-icon-time"></i>
          </div>
          <div class="card-content">
            <div class="card-title">活动进行中</div>
            <div class="card-value">{{ activityStats.ongoingActivities }}</div>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon">
            <i class="el-icon-check"></i>
          </div>
          <div class="card-content">
            <div class="card-title">活动已完成</div>
            <div class="card-value">{{ activityStats.completedActivities }}</div>
          </div>
        </div>
        <div class="overview-card">
          <div class="card-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="card-content">
            <div class="card-title">总参与人数</div>
            <div class="card-value">{{ activityStats.totalParticipants }}</div>
          </div>
        </div>
      </div> -->

      <!-- 活动统计图表 -->
      <div class="activity-charts-section fade-in" style="animation-delay: 0.15s">
        <div class="charts-container">
          <div class="chart-card">
            <h4 class="chart-title">各书院活动数统计</h4>
            <div class="chart-container">
              <div ref="activityCountChart" class="chart"></div>
            </div>
          </div>
          <div class="chart-card">
            <h4 class="chart-title">各书院参与人数统计</h4>
            <div class="chart-container">
              <div ref="participantCountChart" class="chart"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 各书院活动统计 -->
      <div class="college-stats-section fade-in" style="animation-delay: 0.2s">
        <h3 class="section-title">各书院活动统计</h3>
        <div class="college-stats-grid">
          <div
            v-for="college in collegeList"
            :key="college.key"
            class="college-stat-card"
            :class="{ 'active': selectedCollege === college.key }"
            @click="handleCollegeFilter(college.key)"
          >
            <div class="college-header">
              <i :class="college.icon" class="college-stat-icon"></i>
              <span class="college-name">{{ college.name }}</span>
              <button
                class="view-detail-btn"
                @click.stop="viewCollegeActivities(college.key, college.name)"
                title="已选学生"
              >
                <i class="el-icon-view"></i>
              </button>
            </div>
            <div class="college-numbers">
              <div class="stat-item">
                <span class="stat-label">活动总数</span>
                <span class="stat-value">{{ getCollegeActivityCount(college.key) }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">活动进行中</span>
                <span class="stat-value ongoing">{{ getCollegeOngoingCount(college.key) }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">参与人数</span>
                <span class="stat-value participants">{{ getCollegeParticipantCount(college.key) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 活动类型分布和最近活动 - 左右布局 -->
      <div class="activity-content-row fade-in" style="animation-delay: 0.3s">
        <!-- 活动类型分布 -->
        <div class="activity-type-section">
          <div class="section-header">
            <h3 class="section-title">活动类型分布</h3>
          </div>
          <div class="type-distribution">
            <div
              v-for="(type, index) in activityTypes"
              :key="type.key"
              class="type-item"
              :style="{ '--progress': type.percentage + '%' }"
              @click="handleActivityTypeClick(type)"
            >
              <div class="type-header">
                <span class="type-name">{{ type.name }}</span>
                <span class="type-count">{{ type.count }}个</span>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: type.percentage + '%' }"></div>
              </div>
              <div class="type-percentage">{{ type.percentage }}%</div>
            </div>
          </div>
        </div>

        <!-- 最近活动列表 -->
        <div class="recent-activities-section">
          <div class="section-header">
            <h3 class="section-title">最近活动</h3>
            <el-button
              type="text"
              size="small"
              @click="showAllActivities"
              class="view-all-btn"
            >
              全部
              <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
          <div class="activities-list">
            <div
              v-for="activity in recentActivities"
              :key="activity.id"
              class="activity-item"
            >
              <div class="activity-info">
                <div class="activity-name">{{ activity.name }}</div>
                <div class="activity-meta">
                  <span class="activity-college">{{ activity.college }}</span>
                  <span class="activity-type">{{ activity.type }}</span>
                  <span class="activity-date">{{ formatDate(activity.startTime) }}</span>
                </div>
              </div>
              <div class="activity-status" :class="'status-' + activity.status">
                {{ getStatusText(activity.status) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 书院活动详情弹窗 -->
    <el-dialog
      :title="collegeNameForDetail + ' - 活动详情'"
      :visible.sync="dialogVisible"
      width="80%"
      :before-close="handleClose"
      class="college-activities-dialog"
    >
      <div class="dialog-content">
        <!-- 活动统计概览 -->
        <div class="dialog-stats">
          <div class="stat-item">
            <span class="stat-label">活动总数</span>
            <span class="stat-value">{{ collegeActivities.length }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">活动进行中</span>
            <span class="stat-value ongoing">{{ getOngoingCount() }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">活动已结束</span>
            <span class="stat-value completed">{{ getCompletedCount() }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">参与人数</span>
            <span class="stat-value">{{ getTotalParticipants() }}</span>
          </div>
        </div>

        <!-- 活动列表 -->
        <div class="activities-table">
          <el-table
            :data="paginatedActivities"
            v-loading="loading"
            style="width: 100%"
            :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
          >
            <el-table-column label="序号" width="80" align="center">
              <template slot-scope="scope">
                {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="name" label="活动名称" min-width="200" show-overflow-tooltip />
            <el-table-column prop="type" label="活动类型" min-width="180" show-overflow-tooltip />
            <el-table-column prop="startTime" label="开始时间" min-width="160">
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="endTime" label="结束时间" min-width="160">
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="location" label="活动地点" min-width="150" show-overflow-tooltip />
            <el-table-column label="参与情况" min-width="140">
              <template slot-scope="scope">
                <span>{{ scope.row.participants }}/{{ scope.row.capacity }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" min-width="100">
              <template slot-scope="scope">
                <el-tag
                  :type="getStatusTagType(scope.row.status)"
                  size="small"
                >
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="120" fixed="right">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  @click="viewActivityDetail(scope.row)"
                >
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页组件 -->
          <div class="pagination-container">
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="currentPage"
              :page-sizes="[10, 20, 50, 100]"
              :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalActivities"
            >
            </el-pagination>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 学生列表弹窗 -->
    <el-dialog
      :title="'已选学生 - ' + currentActivityName"
      :visible.sync="studentDialogVisible"
      width="80%"
      :before-close="handleStudentDialogClose"
      class="student-dialog"
    >
      <!-- 学生统计信息 -->
      <div class="student-stats">
        <div class="stats-card">
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.total }}</div>
            <div class="stat-label">总报名人数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.submitted }}</div>
            <div class="stat-label">已提交</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.pending }}</div>
            <div class="stat-label">未审核</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.approved }}</div>
            <div class="stat-label">已通过</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.rejected }}</div>
            <div class="stat-label">未通过</div>
          </div>
        </div>
      </div>

      <!-- 学生表格 -->
      <div class="student-table-container">
        <el-table
          :data="selectedStudents"
          border
          v-loading="studentLoading"
          class="enhanced-student-table"
          :header-cell-style="{
            'background': '#f8fafc',
            'color': '#2d3540',
            'font-weight': '600',
            'border-bottom': '2px solid #e2e8f0'
          }"
          :row-class-name="getStudentRowClassName"
        >
          <el-table-column label="序号" width="60" align="center">
            <template v-slot="scope">
              <span class="index-badge">
                {{ (studentQueryParams.pageNum - 1) * studentQueryParams.pageSize + scope.$index + 1 }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="studentId" label="学号" align="center" min-width="140">
            <template slot-scope="{row}">
              <div class="student-id-container">
                <span class="student-id">{{ row.studentId }}</span>
                <el-button
                  v-if="row.studentId"
                  type="text"
                  size="mini"
                  icon="el-icon-copy-document"
                  @click="copyToClipboard(row.studentId)"
                  class="copy-btn">
                </el-button>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="studentName" label="姓名" align="center" min-width="100">
            <template slot-scope="{row}">
              <div class="student-name-container">
                <span class="student-name">{{ row.studentName }}</span>
                <el-tag v-if="row.isBooked" type="success" size="mini" effect="plain">已报名</el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="studentClass" label="学生班级" align="center" min-width="120">
            <template slot-scope="{row}">
              <el-tag size="small" type="primary" effect="plain">
                {{ row.studentClass || '未知' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="academy" label="所属书院" align="center" min-width="120">
            <template slot-scope="{row}">
              <el-tag size="small" :type="getAcademyTagType(row.academy)" effect="plain">
                {{ row.academy || '未知' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="审核状态" min-width="110" align="center">
            <template slot-scope="{row}">
              <el-tag :type="getBookingStatusTag(row.status)" size="small" effect="dark">
                {{ getBookingStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 学生列表分页及导出按钮 -->
      <div class="student-pagination">
        <el-button
          type="primary"
          icon="el-icon-download"
          @click="handleExportStudents"
          class="export-btn">
          导出名单
        </el-button>
        <el-pagination
          v-show="studentTotal > 0"
          @size-change="handleStudentSizeChange"
          @current-change="handleStudentCurrentChange"
          :current-page="studentQueryParams.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="studentQueryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="studentTotal"
          class="custom-pagination"
        />
      </div>
    </el-dialog>

    <!-- 活动类型筛选弹窗 -->
    <el-dialog
      :title="selectedActivityTypeName + ' - 活动列表'"
      :visible.sync="activityTypeDialogVisible"
      width="80%"
      :before-close="handleActivityTypeDialogClose"
      class="activity-type-dialog"
    >
      <div class="dialog-content">
        <!-- 活动类型统计信息 -->
        <div class="activity-type-stats">
          <div class="activity-type-stats-card">
            <div class="stat-item">
              <div class="stat-number">{{ activityTypeStats.total }}</div>
              <div class="stat-label">活动总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ activityTypeStats.ongoing }}</div>
              <div class="stat-label">活动进行中</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ activityTypeStats.completed }}</div>
              <div class="stat-label">活动已结束</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ activityTypeStats.participants }}</div>
              <div class="stat-label">参与人数</div>
            </div>
          </div>
        </div>

        <!-- 活动列表表格 -->
        <div class="activity-type-table-container">
          <el-table
            :data="activityTypeList"
            v-loading="activityTypeLoading"
            element-loading-text="加载中..."
            class="activity-type-table"
            stripe
            :header-cell-style="{
              'background': '#f8fafc',
              'color': '#2d3540',
              'font-weight': '600',
              'border-bottom': '2px solid #e2e8f0'
            }"
          >
            <el-table-column
              type="index"
              label="序号"
              width="60"
              align="center"
            />
            <el-table-column
              prop="activityName"
              label="活动名称"
              min-width="200"
              show-overflow-tooltip
            />
            <el-table-column
              prop="organizer"
              label="组织单位"
              min-width="120"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag size="small" :type="getAcademyTagType(scope.row.organizer)" effect="plain">
                  {{ scope.row.organizer }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="activityLocation"
              label="活动地点"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column
              prop="startTime"
              label="开始时间"
              min-width="160"
              align="center"
            >
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column
              prop="endTime"
              label="结束时间"
              min-width="160"
              align="center"
            >
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column
              label="参与情况"
              min-width="140"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.activityCapacity || 0 }}/{{ scope.row.activityTotalCapacity || 0 }}</span>

              </template>
            </el-table-column>
            <el-table-column
              prop="status"
              label="状态"
              min-width="100"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag
                  :type="getStatusTagType(scope.row.status)"
                  size="small"
                  effect="dark"
                >
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="120"
              align="center"
            >
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  @click="viewActivityDetail(scope.row)"
                  class="action-button view-button">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页组件 -->
        <el-pagination
          @size-change="handleActivityTypeSizeChange"
          @current-change="handleActivityTypeCurrentChange"
          :current-page="activityTypeQueryParams.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="activityTypeQueryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="activityTypeTotal"
          class="custom-pagination"
        />
      </div>
    </el-dialog>

    <!-- 所有活动列表弹窗 -->
    <el-dialog
      :title="getAllActivitiesDialogTitle()"
      :visible.sync="allActivitiesDialogVisible"
      width="80%"
      :before-close="handleAllActivitiesDialogClose"
      class="all-activities-dialog"
    >
      <div class="dialog-content">
        <!-- 活动统计信息 -->
        <div class="all-activities-stats">
          <div class="all-activities-stats-card">
            <div class="stat-item">
              <div class="stat-number">{{ allActivitiesStats.total }}</div>
              <div class="stat-label">活动总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ allActivitiesStats.ongoing }}</div>
              <div class="stat-label">活动进行中</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ allActivitiesStats.completed }}</div>
              <div class="stat-label">活动已结束</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ allActivitiesStats.participants }}</div>
              <div class="stat-label">参与人数</div>
            </div>
          </div>
        </div>

        <!-- 活动列表表格 -->
        <div class="all-activities-table-container">
          <el-table
            :data="allActivitiesList"
            v-loading="allActivitiesLoading"
            element-loading-text="加载中..."
            class="all-activities-table"
            stripe
            :header-cell-style="{
              'background': '#f8fafc',
              'color': '#2d3540',
              'font-weight': '600',
              'border-bottom': '2px solid #e2e8f0'
            }"
          >
            <el-table-column
              type="index"
              label="序号"
              width="60"
              align="center"
            />
            <el-table-column
              prop="activityName"
              label="活动名称"
              min-width="200"
              show-overflow-tooltip
            />
            <el-table-column
              prop="organizer"
              label="组织单位"
              min-width="120"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag size="small" :type="getAcademyTagType(scope.row.organizer)" effect="plain">
                  {{ scope.row.organizer }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="activityType"
              label="活动类型"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag size="small" :type="getActivityTypeTagType(scope.row.activityType)" effect="plain">
                  {{ getActivityTypeName(scope.row.activityType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="activityLocation"
              label="活动地点"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column
              prop="startTime"
              label="开始时间"
              min-width="160"
              align="center"
            >
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column
              prop="endTime"
              label="结束时间"
              min-width="160"
              align="center"
            >
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column
              label="参与情况"
              min-width="140"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.activityCapacity || 0 }}/{{ scope.row.activityTotalCapacity || 0 }}</span>
          
              </template>
            </el-table-column>
            <el-table-column
              prop="status"
              label="状态"
              min-width="100"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag
                  :type="getStatusTagType(scope.row.status)"
                  size="small"
                  effect="dark"
                >
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="120"
              align="center"
            >
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  @click="viewActivityDetail(scope.row)"
                  class="action-button view-button">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页组件 -->
        <el-pagination
          @size-change="handleAllActivitiesSizeChange"
          @current-change="handleAllActivitiesCurrentChange"
          :current-page="allActivitiesQueryParams.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="allActivitiesQueryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="allActivitiesTotal"
          class="custom-pagination"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getActivityOverview,
  getCollegeActivityStats,
  getActivityTypeDistribution,
  getRecentActivities,
  getActivityStatsByCollege,
  getCollegeActivities
} from '@/api/system/activityStatistics'
import { listActivities } from '@/api/system/activities'
import { listBookingsWithActivity } from '@/api/system/bookings'
import * as echarts from 'echarts'

export default {
  name: 'CollegeActivity',
  data() {
    return {
      loading: true,
      selectedCollege: 'all',
      collegeList: [
        { key: 'dayu', name: '大煜书院', icon: 'el-icon-school' },
        { key: 'bochuan', name: '伯川书院', icon: 'el-icon-school' },
        { key: 'lingxi', name: '令希书院', icon: 'el-icon-school' },
        { key: 'houde', name: '厚德书院', icon: 'el-icon-school' },
        { key: 'zhixing', name: '知行书院', icon: 'el-icon-school' },
        { key: 'duxue', name: '笃学书院', icon: 'el-icon-school' },
        { key: 'qiushi', name: '求实书院', icon: 'el-icon-school' }
      ],
      activityStats: {
        totalActivities: 0,
        ongoingActivities: 0,
        completedActivities: 0,
        totalParticipants: 0
      },
      collegeActivityData: {},
      activityTypes: [
        { key: '1', name: '人格塑造与价值引领活动类', count: 0, percentage: 0 },
        { key: '2', name: '知识融合与思维进阶活动类', count: 0, percentage: 0 },
        { key: '3', name: '能力锻造与实践创新活动类', count: 0, percentage: 0 },
        { key: '4', name: '社会责任与领军意识活动类', count: 0, percentage: 0 }
      ],
      recentActivities: [],
      activityCountChart: null,
      participantCountChart: null,
      dialogVisible: false,
      selectedCollegeForDetail: '',
      collegeNameForDetail: '',
      collegeActivities: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      totalActivities: 0,
      studentDialogVisible: false,
      selectedStudents: [],
      allStudents: [],
      studentLoading: false,
      studentTotal: 0,
      currentActivityId: null,
      currentActivityName: '',
      studentQueryParams: {
        pageNum: 1,
        pageSize: 10,
        activityId: null
      },
      studentStats: {
        total: 0,
        submitted: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      },
      activityTypeDialogVisible: false,
      selectedActivityType: null,
      selectedActivityTypeName: '',
      activityTypeList: [],
      activityTypeLoading: false,
      activityTypeTotal: 0,
      activityTypeQueryParams: {
        pageNum: 1,
        pageSize: 10,
        activityType: null
      },
      activityTypeStats: {
        total: 0,
        ongoing: 0,
        completed: 0,
        participants: 0
      },
      allActivitiesDialogVisible: false,
      allActivitiesList: [],
      allActivitiesLoading: false,
      allActivitiesTotal: 0,
      allActivitiesQueryParams: {
        pageNum: 1,
        pageSize: 10,
        organizer: null  // 书院筛选参数
      },
      allActivitiesStats: {
        total: 0,
        ongoing: 0,
        completed: 0,
        participants: 0
      },
      // 缓存全部书院的初始数据
      cachedAllData: {
        activityStats: null,
        collegeActivityData: null,
        activityTypes: null,
        recentActivities: null
      }
    }
  },
  computed: {
    paginatedActivities() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.collegeActivities.slice(start, end)
    }
  },
  methods: {
    async handleCollegeFilter(collegeKey) {
      // 如果点击的是已选中的书院，则恢复为显示全部
      if (this.selectedCollege === collegeKey) {
        this.selectedCollege = 'all'
        // 直接使用缓存的数据，不重新加载
        this.restoreCachedAllData()
      } else {
        this.selectedCollege = collegeKey
        await this.loadActivityStatsByCollege(collegeKey)
      }
    },
    getCollegeName(collegeKey) {
      if (collegeKey === 'all') return '全部书院'
      const college = this.collegeList.find(item => item.key === collegeKey)
      return college ? college.name : ''
    },
    getCollegeActivityCount(collegeKey) {
      if (collegeKey === 'all') return this.activityStats.totalActivities
      return this.collegeActivityData[collegeKey]?.totalActivities || 0
    },
    getCollegeOngoingCount(collegeKey) {
      if (collegeKey === 'all') return this.activityStats.ongoingActivities
      return this.collegeActivityData[collegeKey]?.ongoingActivities || 0
    },
    getCollegeParticipantCount(collegeKey) {
      if (collegeKey === 'all') return this.activityStats.totalParticipants
      return this.collegeActivityData[collegeKey]?.totalParticipants || 0
    },
    getStatusText(status) {
      const statusMap = {
        // 数据库存储值 -> 前端显示文本
        '未开始': '报名未开始',
        '可报名': '活动报名中',
        '已截止': '报名已截止',
        '进行中': '活动进行中',
        '已结束': '活动已结束',
        // 兼容旧值
        'not-started': '报名未开始',
        'enrolling': '活动报名中',
        'enroll-ended': '报名已截止',
        'ongoing': '活动进行中',
        'completed': '活动已结束',
        'cancelled': '已取消'
      }
      return statusMap[status] || status
    },
    async loadActivityStats() {
      this.loading = true
      try {
        const overviewResponse = await getActivityOverview()
        if (overviewResponse.code === 200) {
          this.activityStats = overviewResponse.data
        }

        const collegeStatsResponse = await getCollegeActivityStats()
        if (collegeStatsResponse.code === 200) {
          const backendCollegeData = collegeStatsResponse.data.collegeData
          this.collegeActivityData = {}

          const collegeNameToKey = {
            '大煜书院': 'dayu',
            '伯川书院': 'bochuan',
            '令希书院': 'lingxi',
            '厚德书院': 'houde',
            '知行书院': 'zhixing',
            '笃学书院': 'duxue',
            '求实书院': 'qiushi'
          }

          for (const [collegeName, stats] of Object.entries(backendCollegeData)) {
            const collegeKey = collegeNameToKey[collegeName]
            if (collegeKey) {
              this.collegeActivityData[collegeKey] = stats
            }
          }
        }

        const typeDistributionResponse = await getActivityTypeDistribution()
        if (typeDistributionResponse.code === 200) {
          this.activityTypes = typeDistributionResponse.data.typeDistribution
        }

        const recentActivitiesResponse = await getRecentActivities(10)
        if (recentActivitiesResponse.code === 200) {
          const allActivities = recentActivitiesResponse.data.recentActivities || []
          this.recentActivities = allActivities
            .filter(activity => activity.status === 'ongoing' || activity.status === 'completed')
            .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
            .slice(0, 5)
        }

        // 缓存全部数据
        this.cacheAllData()
      } catch (error) {
        console.error('加载活动统计数据失败:', error)
        this.$message.error('加载活动统计数据失败')
      } finally {
        setTimeout(() => {
          this.loading = false
          // 等待 DOM 更新后再初始化图表
          this.$nextTick(() => {
            this.initActivityCharts()
          })
        }, 300)
      }
    },
    async loadActivityStatsByCollege(collegeKey) {
      try {
        if (collegeKey === 'all') {
          // 如果有缓存，使用缓存数据；否则重新加载
          if (this.cachedAllData.activityStats) {
            this.restoreCachedAllData()
          } else {
            await this.loadActivityStats()
          }
          return
        }

        const collegeName = this.getCollegeName(collegeKey)
        const response = await getActivityStatsByCollege(collegeName)
        if (response.code === 200) {
          this.activityStats = response.data

          if (response.data.typeDistribution) {
            this.activityTypes = response.data.typeDistribution
          }

          if (response.data.recentActivities) {
            const allActivities = response.data.recentActivities || []
            this.recentActivities = allActivities
              .filter(activity => activity.status === 'ongoing' || activity.status === 'completed')
              .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
              .slice(0, 5)
          }
          // 更新图表
          this.$nextTick(() => {
            this.initActivityCharts()
          })
        }
      } catch (error) {
        console.error('加载书院活动统计数据失败:', error)
        this.$message.error('加载书院活动统计数据失败')
      }
    },
    // 缓存全部书院数据
    cacheAllData() {
      this.cachedAllData = {
        activityStats: JSON.parse(JSON.stringify(this.activityStats)),
        collegeActivityData: JSON.parse(JSON.stringify(this.collegeActivityData)),
        activityTypes: JSON.parse(JSON.stringify(this.activityTypes)),
        recentActivities: JSON.parse(JSON.stringify(this.recentActivities))
      }
    },
    // 恢复缓存的全部书院数据
    restoreCachedAllData() {
      if (this.cachedAllData.activityStats) {
        this.activityStats = JSON.parse(JSON.stringify(this.cachedAllData.activityStats))
        this.collegeActivityData = JSON.parse(JSON.stringify(this.cachedAllData.collegeActivityData))
        this.activityTypes = JSON.parse(JSON.stringify(this.cachedAllData.activityTypes))
        this.recentActivities = JSON.parse(JSON.stringify(this.cachedAllData.recentActivities))
        
        // 更新图表
        this.$nextTick(() => {
          this.initActivityCharts()
        })
      }
    },
    initActivityCharts() {
      this.$nextTick(() => {
        this.initActivityCountChart()
        this.initParticipantCountChart()
      })
    },
    initActivityCountChart() {
      if (this.activityCountChart) {
        this.activityCountChart.dispose()
      }

      const chartDom = this.$refs.activityCountChart
      if (!chartDom) return

      this.activityCountChart = echarts.init(chartDom)

      const allColleges = [
        { key: 'dayu', name: '大煜书院' },
        { key: 'bochuan', name: '伯川书院' },
        { key: 'lingxi', name: '令希书院' },
        { key: 'houde', name: '厚德书院' },
        { key: 'zhixing', name: '知行书院' },
        { key: 'duxue', name: '笃学书院' },
        { key: 'qiushi', name: '求实书院' }
      ]

      const collegeData = this.collegeActivityData || {}
      const data = allColleges.map(college => ({
        name: college.name,
        value: collegeData[college.key]?.totalActivities || 0
      }))

      const totalActivities = this.activityStats.totalActivities || 0

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: '5%',
          top: 'middle',
          width: '20%',
          textStyle: {
            fontSize: 12,
            fontWeight: 'bold',
            color: '#374151'
          }
        },
        graphic: [
          {
            type: 'text',
            left: 'center',
            top: '42%',
            style: {
              text: '总活动数',
              textAlign: 'center',
              fill: '#6b7280',
              fontSize: 14,
              fontWeight: 'normal'
            }
          },
          {
            type: 'text',
            left: 'center',
            top: '52%',
            style: {
              text: totalActivities.toString(),
              textAlign: 'center',
              fill: '#111827',
              fontSize: 28,
              fontWeight: 'bold'
            }
          }
        ],
        series: [
          {
            name: '活动数',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            color: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16'],
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}：{c}',
              fontSize: 12,
              color: '#374151',
              fontWeight: 'bold'
            },
            labelLine: {
              show: true,
              length: 15,
              length2: 10,
              smooth: true,
              lineStyle: {
                color: '#999'
              }
            },
            data: data
          }
        ]
      }

      this.activityCountChart.setOption(option)
    },
    initParticipantCountChart() {
      if (this.participantCountChart) {
        this.participantCountChart.dispose()
      }

      const chartDom = this.$refs.participantCountChart
      if (!chartDom) return

      this.participantCountChart = echarts.init(chartDom)

      const allColleges = [
        { key: 'dayu', name: '大煜书院' },
        { key: 'bochuan', name: '伯川书院' },
        { key: 'lingxi', name: '令希书院' },
        { key: 'houde', name: '厚德书院' },
        { key: 'zhixing', name: '知行书院' },
        { key: 'duxue', name: '笃学书院' },
        { key: 'qiushi', name: '求实书院' }
      ]

      const collegeData = this.collegeActivityData || {}
      const data = allColleges.map(college => ({
        name: college.name,
        value: collegeData[college.key]?.totalParticipants || 0
      }))

      const totalParticipants = this.activityStats.totalParticipants || 0

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: '5%',
          top: 'middle',
          width: '20%',
          textStyle: {
            fontSize: 12,
            fontWeight: 'bold',
            color: '#374151'
          }
        },
        graphic: [
          {
            type: 'text',
            left: 'center',
            top: '42%',
            style: {
              text: '总参与人数',
              textAlign: 'center',
              fill: '#6b7280',
              fontSize: 14,
              fontWeight: 'normal'
            }
          },
          {
            type: 'text',
            left: 'center',
            top: '52%',
            style: {
              text: totalParticipants.toString(),
              textAlign: 'center',
              fill: '#111827',
              fontSize: 28,
              fontWeight: 'bold'
            }
          }
        ],
        series: [
          {
            name: '参与人数',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            color: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16'],
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}：{c}',
              fontSize: 12,
              color: '#374151',
              fontWeight: 'bold'
            },
            labelLine: {
              show: true,
              length: 15,
              length2: 10,
              smooth: true,
              lineStyle: {
                color: '#999'
              }
            },
            data: data
          }
        ]
      }

      this.participantCountChart.setOption(option)
    },
    viewCollegeActivities(collegeKey, collegeName) {
      this.selectedCollegeForDetail = collegeKey
      this.collegeNameForDetail = collegeName
      this.dialogVisible = true
      this.loadCollegeActivities(collegeName)
    },
    async loadCollegeActivities(collegeName) {
      try {
        this.loading = true
        const response = await getCollegeActivities(collegeName)
        if (response.code === 200) {
          this.collegeActivities = response.data.activities || []
          this.totalActivities = this.collegeActivities.length
          this.currentPage = 1
        } else {
          this.$message.error('获取活动列表失败')
          this.collegeActivities = []
          this.totalActivities = 0
        }
      } catch (error) {
        console.error('加载书院活动列表失败:', error)
        this.$message.error('加载书院活动列表失败')
        this.collegeActivities = []
        this.totalActivities = 0
      } finally {
        this.loading = false
      }
    },
    // 获取活动进行中的数量（包含报名未开始、报名进行中、报名已截止、活动进行中）
    getOngoingCount() {
      return this.collegeActivities.filter(activity => {
        const status = activity.status
        return status === 'not-started' || status === '未开始' || status === '报名未开始' ||
               status === 'enrolling' || status === '报名中' || status === '报名进行中' || status === '可报名' ||
               status === 'enroll-ended' || status === '报名已截止' || status === '已截止' ||
               status === 'ongoing' || status === '进行中' || status === '活动进行中'
      }).length
    },
    // 获取活动已结束的数量
    getCompletedCount() {
      return this.collegeActivities.filter(activity => {
        const status = activity.status
        return status === 'completed' || status === '已完成' || status === '活动已结束' || status === '已结束'
      }).length
    },
    // 获取总参与人数
    getTotalParticipants() {
      return this.collegeActivities.reduce((total, activity) => {
        return total + (activity.participants || 0)
      }, 0)
    },
    getStatusTagType(status) {
      const typeMap = {
        // 数据库存储值 -> 标签颜色
        '未开始': 'info',          // 报名未开始 - 灰色
        '可报名': 'success',       // 活动报名中 - 绿色
        '已截止': 'warning',       // 报名已截止 - 黄色
        '进行中': 'warning',       // 活动进行中 - 黄色
        '已结束': 'info',          // 活动已结束 - 灰色
        // 兼容旧值
        'not-started': 'info',
        'enrolling': 'success',
        'enroll-ended': 'warning',
        'ongoing': 'warning',
        'completed': 'info',
        '报名未开始': 'info',
        '报名中': 'success',
        '报名进行中': 'success',
        '活动报名中': 'success',
        '报名已截止': 'warning',
        '活动进行中': 'warning',
        '已完成': 'info',
        '活动已结束': 'info'
      }
      return typeMap[status] || 'info'
    },
    viewActivityDetail(activity) {
      this.currentActivityId = activity.id
      this.currentActivityName = activity.name
      this.handleViewStudents(activity)
    },
    handleClose(done) {
      this.dialogVisible = false
      done()
    },
    formatDateTime(dateTime) {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    formatDate(dateTime) {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
    },
    handleCurrentChange(val) {
      this.currentPage = val
    },
    async handleViewStudents(activity) {
      this.studentLoading = true
      this.currentActivityId = activity.id
      this.currentActivityName = activity.name
      this.studentQueryParams = {
        pageNum: 1,
        pageSize: 10,
        activityId: activity.id
      }

      try {
        const allStudentsRes = await listBookingsWithActivity({
          activityId: activity.id,
          pageNum: 1,
          pageSize: 1000
        })

        this.allStudents = allStudentsRes.rows || []
        this.studentTotal = allStudentsRes.total || 0
        this.calculateStudentStats(this.allStudents)
        this.getCurrentPageStudents()
        this.studentDialogVisible = true

      } catch (e) {
        this.selectedStudents = []
        this.allStudents = []
        this.studentTotal = 0
        this.resetStudentStats()
        this.studentDialogVisible = true
        this.$message.error('获取学生预约活动数据失败，请稍后再试')
      } finally {
        this.studentLoading = false
      }
    },
    calculateStudentStats(allStudents) {
      this.studentStats.total = allStudents.length
      this.studentStats.submitted = allStudents.filter(s => s.status === 'submitted').length
      this.studentStats.pending = allStudents.filter(s => s.status === 'pending').length
      this.studentStats.approved = allStudents.filter(s => s.status === 'approved').length
      this.studentStats.rejected = allStudents.filter(s => s.status === 'rejected').length
    },
    resetStudentStats() {
      this.studentStats = {
        total: 0,
        submitted: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      }
    },
    getCurrentPageStudents() {
      if (!this.allStudents || this.allStudents.length === 0) {
        this.selectedStudents = []
        return
      }

      const startIndex = (this.studentQueryParams.pageNum - 1) * this.studentQueryParams.pageSize
      const endIndex = startIndex + this.studentQueryParams.pageSize
      this.selectedStudents = this.allStudents.slice(startIndex, endIndex)
    },
    handleStudentSizeChange(val) {
      this.studentQueryParams.pageSize = val
      this.studentQueryParams.pageNum = 1
      this.getCurrentPageStudents()
    },
    handleStudentCurrentChange(val) {
      this.studentQueryParams.pageNum = val
      this.getCurrentPageStudents()
    },
    handleStudentDialogClose(done) {
      this.studentDialogVisible = false
      done()
    },
    copyToClipboard(text) {
      if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(() => {
          this.$message.success('已复制到剪贴板')
        }).catch(() => {
          this.$message.error('复制失败')
        })
      } else {
        const textArea = document.createElement('textarea')
        textArea.value = text
        document.body.appendChild(textArea)
        textArea.select()
        try {
          document.execCommand('copy')
          this.$message.success('已复制到剪贴板')
        } catch (err) {
          this.$message.error('复制失败')
        }
        document.body.removeChild(textArea)
      }
    },
    getAcademyTagType(academy) {
      const typeMap = {
        '大煜书院': 'success',
        '伯川书院': 'primary',
        '令希书院': 'warning',
        '厚德书院': 'info',
        '知行书院': 'danger',
        '笃学书院': 'success',
        '求实书院': 'primary'
      }
      return typeMap[academy] || 'info'
    },
    getBookingStatusTag(status) {
      const typeMap = {
        'pending': 'warning',
        'approved': 'success',
        'rejected': 'danger',
        'submitted': 'info'
      }
      return typeMap[status] || 'info'
    },
    getBookingStatusText(status) {
      const statusMap = {
        'pending': '待审核',
        'approved': '已通过',
        'rejected': '未通过',
        'submitted': '已提交'
      }
      return statusMap[status] || status
    },
    getStudentRowClassName({ row, rowIndex }) {
      if (row.status === 'approved') {
        return 'success-row'
      } else if (row.status === 'rejected') {
        return 'danger-row'
      } else if (row.status === 'pending') {
        return 'warning-row'
      }
      return ''
    },
    handleExportStudents() {
      if (!this.selectedStudents || this.selectedStudents.length === 0) {
        this.$message.warning('没有可导出的数据')
        return
      }

      const activityId = this.selectedStudents[0]?.activityId
      const activityName = this.selectedStudents[0]?.activityName

      if (!activityId) {
        this.$message.error('缺少活动ID参数')
        return
      }

      this.download('/system/bookings/exportStudents', {
        activityId: activityId
      }, `预约学生名单_${activityName}_${this.parseTime(new Date(), '{y}{m}{d}')}.xlsx`)

      this.studentDialogVisible = false
    },
    handleActivityTypeClick(type) {
      this.selectedActivityType = type
      this.selectedActivityTypeName = type.name
      this.activityTypeDialogVisible = true
      this.activityTypeQueryParams.activityType = type.key
      this.activityTypeQueryParams.pageNum = 1
      this.activityTypeQueryParams.pageSize = 10
      this.loadActivityTypeList()
    },
    async loadActivityTypeList() {
      try {
        this.activityTypeLoading = true
        const response = await this.getActivitiesByType(this.activityTypeQueryParams)
        if (response.code === 200) {
          this.activityTypeList = response.rows || []
          this.activityTypeTotal = response.total || 0
          this.calculateActivityTypeStats(this.activityTypeList)
        } else {
          this.$message.error('获取活动列表失败')
          this.activityTypeList = []
          this.activityTypeTotal = 0
        }
      } catch (error) {
        console.error('加载活动类型列表失败:', error)
        this.$message.error('加载活动类型列表失败')
        this.activityTypeList = []
        this.activityTypeTotal = 0
      } finally {
        this.activityTypeLoading = false
      }
    },
    async getActivitiesByType(params) {
      try {
        const queryParams = {
          pageNum: params.pageNum,
          pageSize: params.pageSize,
          activityType: params.activityType
        }

        const response = await listActivities(queryParams)
        return response
      } catch (error) {
        console.error('获取活动列表失败:', error)
        throw error
      }
    },
    calculateActivityTypeStats(activities) {
      this.activityTypeStats = {
        total: activities.length,
        ongoing: 0,
        completed: 0,
        participants: 0
      }

      activities.forEach(activity => {
        const status = activity.status
        // 活动进行中（包含报名未开始、报名进行中、报名已截止、活动进行中）
        if (status === 'not-started' || status === '未开始' || status === '报名未开始' ||
            status === 'enrolling' || status === '报名中' || status === '报名进行中' || status === '可报名' ||
            status === 'enroll-ended' || status === '报名已截止' || status === '已截止' ||
            status === 'ongoing' || status === '进行中' || status === '活动进行中') {
          this.activityTypeStats.ongoing++
        } else if (status === 'completed' || status === '已完成' || status === '活动已结束' || status === '已结束') {
          this.activityTypeStats.completed++
        }
        const participants = activity.activityCapacity || 0
        this.activityTypeStats.participants += participants
      })
    },
    handleActivityTypeSizeChange(val) {
      this.activityTypeQueryParams.pageSize = val
      this.activityTypeQueryParams.pageNum = 1
      this.loadActivityTypeList()
    },
    handleActivityTypeCurrentChange(val) {
      this.activityTypeQueryParams.pageNum = val
      this.loadActivityTypeList()
    },
    handleActivityTypeDialogClose(done) {
      this.activityTypeDialogVisible = false
      done()
    },
    showAllActivities() {
      this.allActivitiesDialogVisible = true
      this.allActivitiesQueryParams.pageNum = 1
      this.allActivitiesQueryParams.pageSize = 10
      // 根据当前筛选的书院设置书院参数
      if (this.selectedCollege === 'all') {
        this.allActivitiesQueryParams.organizer = null  // 显示所有书院的活动
      } else {
        this.allActivitiesQueryParams.organizer = this.getCollegeName(this.selectedCollege)
      }
      this.loadAllActivitiesList()
    },
    async loadAllActivitiesList() {
      try {
        this.allActivitiesLoading = true
        const response = await this.getAllActivities(this.allActivitiesQueryParams)
        if (response.code === 200) {
          this.allActivitiesList = response.rows || []
          this.allActivitiesTotal = response.total || 0
          this.calculateAllActivitiesStats(this.allActivitiesList)
        } else {
          this.$message.error('获取活动列表失败')
          this.allActivitiesList = []
          this.allActivitiesTotal = 0
        }
      } catch (error) {
        console.error('加载所有活动列表失败:', error)
        this.$message.error('加载所有活动列表失败')
        this.allActivitiesList = []
        this.allActivitiesTotal = 0
      } finally {
        this.allActivitiesLoading = false
      }
    },
    async getAllActivities(params) {
      try {
        const queryParams = {
          pageNum: params.pageNum,
          pageSize: params.pageSize
        }
        
        // 如果有书院筛选，添加 organizer 参数
        if (params.organizer) {
          queryParams.organizer = params.organizer
        }

        const response = await listActivities(queryParams)
        return response
      } catch (error) {
        console.error('获取活动列表失败:', error)
        throw error
      }
    },
    calculateAllActivitiesStats(activities) {
      this.allActivitiesStats = {
        total: activities.length,
        ongoing: 0,
        completed: 0,
        participants: 0
      }

      activities.forEach(activity => {
        const status = activity.status
        // 活动进行中（包含报名未开始、报名进行中、报名已截止、活动进行中）
        if (status === 'not-started' || status === '未开始' || status === '报名未开始' ||
            status === 'enrolling' || status === '报名中' || status === '报名进行中' || status === '可报名' ||
            status === 'enroll-ended' || status === '报名已截止' || status === '已截止' ||
            status === 'ongoing' || status === '进行中' || status === '活动进行中') {
          this.allActivitiesStats.ongoing++
        } else if (status === 'completed' || status === '已完成' || status === '活动已结束' || status === '已结束') {
          this.allActivitiesStats.completed++
        }
        const participants = activity.activityCapacity || 0
        this.allActivitiesStats.participants += participants
      })
    },
    handleAllActivitiesSizeChange(val) {
      this.allActivitiesQueryParams.pageSize = val
      this.allActivitiesQueryParams.pageNum = 1
      this.loadAllActivitiesList()
    },
    handleAllActivitiesCurrentChange(val) {
      this.allActivitiesQueryParams.pageNum = val
      this.loadAllActivitiesList()
    },
    handleAllActivitiesDialogClose(done) {
      this.allActivitiesDialogVisible = false
      done()
    },
    getAllActivitiesDialogTitle() {
      if (this.selectedCollege === 'all') {
        return '所有活动列表'
      }
      return `${this.getCollegeName(this.selectedCollege)}活动列表`
    },
    getActivityTypeName(type) {
      const typeMap = {
        '1': '人格塑造与价值引领活动类',
        '2': '知识融合与思维进阶活动类',
        '3': '能力锻造与实践创新活动类',
        '4': '社会责任与领军意识活动类'
      }
      return typeMap[type] || '其他'
    },
    getActivityTypeTagType(type) {
      const typeMap = {
        '1': 'success',
        '2': 'primary',
        '3': 'warning',
        '4': 'danger'
      }
      return typeMap[type] || 'info'
    }
  },
  mounted() {
    this.loadActivityStats()
  }
}
</script>

<style scoped src="./styles/common.css"></style>
<style scoped src="./styles/activity.css"></style>

<style scoped>
/* 本页面特殊样式 */
</style>

