<template>
  <div class="app-container">
    <div class="main-layout">
      <!-- 左侧书院筛选区域 -->
      <div class="college-filter-panel">
        <div class="panel-header">
          <h3 class="panel-title">书院筛选</h3>
        </div>
        <div class="panel-content">
          <div class="college-buttons">
            <button
              v-for="college in collegeList"
              :key="college.key"
              @click="handleCollegeFilter(college.key)"
              :class="[
                'college-button',
                selectedCollege === college.key ? 'college-button-active' : 'college-button-normal'
              ]"
            >
              <i :class="college.icon" class="college-icon"></i>
              {{ college.name }}
            </button>
          </div>
          
          <!-- 全部书院按钮 -->
          <div class="all-college-section">
            <button
              @click="handleCollegeFilter('all')"
              :class="[
                'college-button all-college-button',
                selectedCollege === 'all' ? 'all-college-button-active' : 'all-college-button-normal'
              ]"
            >
              <i class="el-icon-s-grid college-icon"></i>
              全部书院
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧主内容区域 -->
      <div class="main-content-area">
        <!-- 顶部菜单栏 -->
        <div class="top-menu-bar">
          <div class="menu-container">
            <div class="menu-buttons">
              <button
                v-for="menuItem in menuItems"
                :key="menuItem.key"
                @click="handleMenuClick(menuItem.key)"
                :class="[
                  'menu-button',
                  activeMenu === menuItem.key ? 'menu-button-active' : 'menu-button-normal'
                ]"
              >
                <i :class="menuItem.icon" class="menu-icon"></i>
                {{ menuItem.label }}
              </button>
            </div>
          </div>
        </div>

        <!-- 内容区域 -->
        <div class="content-area">
          <!-- 书院活动页面 -->
          <div v-if="activeMenu === 'activity'" class="page-content">
            <div class="content-wrapper">
              <!-- 统计概览卡片 -->
              <div class="stats-overview">
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
                    <div class="card-title">进行中</div>
                    <div class="card-value">{{ activityStats.ongoingActivities }}</div>
                  </div>
                </div>
                <div class="overview-card">
                  <div class="card-icon">
                    <i class="el-icon-check"></i>
                  </div>
                  <div class="card-content">
                    <div class="card-title">已完成</div>
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
              </div>

              <!-- 各书院活动统计 -->
              <div class="college-stats-section">
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
                        <span class="stat-label">进行中</span>
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

              <!-- 活动类型分布 -->
              <div class="activity-type-section">
                <h3 class="section-title">活动类型分布</h3>
                <div class="type-distribution">
                  <div 
                    v-for="(type, index) in activityTypes" 
                    :key="type.key"
                    class="type-item"
                    :style="{ '--progress': type.percentage + '%' }"
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
                <h3 class="section-title">最近活动</h3>
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
                        <span class="activity-date">{{ activity.date }}</span>
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

          <!-- 书院课程页面 -->
          <div v-if="activeMenu === 'course'" class="page-content">
            <div class="content-wrapper">
              <!-- 课程统计图表 -->
              <div class="course-charts-section">
                <div class="charts-container">
                  <div class="chart-card">
                    <h4 class="chart-title">各书院课程数统计</h4>
                    <div class="chart-container">
                      <div ref="courseCountChart" class="chart"></div>
                    </div>
                  </div>
                  <div class="chart-card">
                    <h4 class="chart-title">各书院选课人数统计</h4>
                    <div class="chart-container">
                      <div ref="enrollmentCountChart" class="chart"></div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 课程状态统计 -->
              <div class="course-status-section">
               <div class="status-stats-grid">
                  <div class="status-stat-card not-started">
                    <div class="status-icon">
                      <i class="el-icon-time"></i>
                    </div>
                    <div class="status-content">
                      <div class="status-title">未开始选课</div>
                      <div class="status-count">{{ courseStats.notStartedCourses }}</div>
                    </div>
                  </div>
                  <div class="status-stat-card enrolling">
                    <div class="status-icon">
                      <i class="el-icon-loading"></i>
                    </div>
                    <div class="status-content">
                      <div class="status-title">选课进行中</div>
                      <div class="status-count">{{ courseStats.enrollingCourses }}</div>
                    </div>
                  </div>
                  <div class="status-stat-card ended">
                    <div class="status-icon">
                      <i class="el-icon-check"></i>
                    </div>
                    <div class="status-content">
                      <div class="status-title">选课已结束</div>
                      <div class="status-count">{{ courseStats.enrollmentEndedCourses }}</div>
                    </div>
                  </div>
                  <div class="status-stat-card ongoing">
                    <div class="status-icon">
                      <i class="el-icon-video-play"></i>
                    </div>
                    <div class="status-content">
                      <div class="status-title">课程进行中</div>
                      <div class="status-count">{{ courseStats.ongoingCourses }}</div>
                    </div>
                  </div>
                  <div class="status-stat-card completed">
                    <div class="status-icon">
                      <i class="el-icon-finished"></i>
                    </div>
                    <div class="status-content">
                      <div class="status-title">课程已完成</div>
                      <div class="status-count">{{ courseStats.completedCourses }}</div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 各书院课程统计 -->
              <div class="college-stats-section">
                <h3 class="section-title">各书院课程统计</h3>
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
                        class="detail-button"
                        @click.stop="handleCourseDetail(college.key)"
                        title="查看详细"
                      >
                        <i class="el-icon-view"></i>
                      </button>
                    </div>
                    <div class="college-numbers">
                      <div class="stat-item">
                        <span class="stat-label">课程总数</span>
                        <span class="stat-value">{{ getCollegeCourseCount(college.key) }}</span>
                      </div>
                      <div class="stat-item">
                        <span class="stat-label">选课中</span>
                        <span class="stat-value enrolling">{{ getCollegeEnrollingCount(college.key) }}</span>
                      </div>
                      <div class="stat-item">
                        <span class="stat-label">选课人数</span>
                        <span class="stat-value enrollments">{{ getCollegeEnrollmentCount(college.key) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 课程类型分布 -->
              <div class="course-type-section">
                <h3 class="section-title">课程类型分布</h3>
                <div class="type-distribution">
                  <div 
                    v-for="(type, index) in courseTypes" 
                    :key="type.key"
                    class="type-item"
                    :style="{ '--progress': type.percentage + '%' }"
                  >
                    <div class="type-header">
                      <span class="type-name">{{ type.name }}</span>
                      <span class="type-count">{{ type.count }}门</span>
                    </div>
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: type.percentage + '%' }"></div>
                    </div>
                    <div class="type-percentage">{{ type.percentage }}%</div>
                  </div>
                </div>
              </div>

            </div>
          </div>

          <!-- 专业分流页面 -->
          <div v-if="activeMenu === 'major'" class="page-content">
            <Qstatistics />
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
            <span class="stat-label">进行中</span>
            <span class="stat-value ongoing">{{ getOngoingCount() }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">已完成</span>
            <span class="stat-value completed">{{ getCompletedCount() }}</span>
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
            <el-table-column label="参与情况" min-width="120">
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

    <!-- 书院课程详情弹窗 -->
    <el-dialog
      :title="collegeNameForCourseDetail + ' - 课程详情'"
      :visible.sync="courseDialogVisible"
      width="80%"
      :before-close="handleCourseClose"
      class="college-courses-dialog"
    >
      <div class="dialog-content">
        <!-- 课程统计信息 -->
        <div class="course-stats">
          <div class="course-stats-card">
            <div class="stat-item">
              <div class="stat-number">{{ courseStats.total }}</div>
              <div class="stat-label">课程总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ courseStats.ongoing }}</div>
              <div class="stat-label">进行中</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ courseStats.completed }}</div>
              <div class="stat-label">已结课</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ courseStats.enrollments }}</div>
              <div class="stat-label">总选课人数</div>
            </div>
          </div>
        </div>

        <el-table
          :data="courseList"
          v-loading="courseLoading"
          element-loading-text="加载中..."
          class="course-table"
          stripe
        >
          <el-table-column
            type="index"
            label="序号"
            width="60"
            align="center"
          />
          <el-table-column
            prop="courseName"
            label="课程名称"
            min-width="200"
            show-overflow-tooltip
          />
          <el-table-column
            prop="courseCategory"
            label="课程分类"
            min-width="250"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag :type="getCourseCategoryTagType(scope.row.courseType)">
                {{ getCourseCategoryName(scope.row.courseType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="courseLocation"
            label="课程地点"
            width="180"
            show-overflow-tooltip
          />
          <el-table-column
            label="选课人数"
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              {{ scope.row.courseCapacity - scope.row.courseTotalCapacity }}/{{ scope.row.courseCapacity }}
            </template>
          </el-table-column>
          <el-table-column
            label="课程状态"
            width="150"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag :type="getCourseStatusTagType(computeCourseStatus(scope.row))">
                {{ computeCourseStatus(scope.row) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="startTime"
            label="开始时间"
            width="150"
            align="center"
          >
            <template slot-scope="scope">
              {{ formatDate(scope.row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column
            prop="endTime"
            label="结束时间"
            width="150"
            align="center"
          >
            <template slot-scope="scope">
              {{ formatDate(scope.row.endTime) }}
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
                @click="handleViewCourseStudents(scope.row)"
                class="action-button view-button">
                已选学生
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页组件 -->
        <el-pagination
          @size-change="handleCourseSizeChange"
          @current-change="handleCourseCurrentChange"
          :current-page="courseQueryParams.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="courseQueryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="courseTotal"
          class="custom-pagination"
        />
      </div>
    </el-dialog>

    <!-- 课程学生列表弹窗 -->
    <el-dialog
      :title="'已选学生 - ' + (selectedCourseForStudents && selectedCourseForStudents.courseName) || '课程名称'"
      :visible.sync="courseStudentDialogVisible"
      width="80%"
      :before-close="handleCourseStudentDialogClose"
      class="course-student-dialog">
      
      <!-- 课程学生统计信息 -->
      <div class="course-student-stats">
        <div class="course-student-stats-card">
          <div class="stat-item">
            <div class="stat-number">{{ courseStudentStats.total }}</div>
            <div class="stat-label">课程总数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ courseStudentStats.ongoing }}</div>
            <div class="stat-label">进行中</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ courseStudentStats.completed }}</div>
            <div class="stat-label">已结课</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ courseStudentStats.approved }}</div>
            <div class="stat-label">已通过</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ courseStudentStats.rejected }}</div>
            <div class="stat-label">未通过</div>
          </div>
        </div>
      </div>

      <!-- 学生表格 -->
      <div class="course-student-table-container">
        <el-table
          :data="courseStudentList"
          border
          v-loading="false"
          class="enhanced-student-table"
          :header-cell-style="{
            'background': '#f8fafc',
            'color': '#2d3540',
            'font-weight': '600',
            'border-bottom': '2px solid #e2e8f0'
          }"
        >
          <el-table-column label="序号" width="60" align="center">
            <template v-slot="scope">
              <span class="index-badge">
                {{ (courseStudentQueryParams.pageNum - 1) * courseStudentQueryParams.pageSize + scope.$index + 1 }}
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
                <span class="student-name">{{ row.studentName || '未知' }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="college" label="所属书院" align="center" min-width="120">
            <template slot-scope="{row}">
              <el-tag size="small" :type="getAcademyTagType(row.college)" effect="plain">
                {{ row.college || '未知' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="studentClass" label="班级" align="center" min-width="100">
            <template slot-scope="{row}">
              {{ row.studentClass || '-' }}
            </template>
          </el-table-column>

          <el-table-column prop="scoreValue" label="成绩" align="center" min-width="100">
            <template slot-scope="{row}">
              {{ row.scoreValue || '-' }}
            </template>
          </el-table-column>

          <el-table-column prop="status" label="考核状态" align="center" min-width="100">
            <template slot-scope="{row}">
              <el-tag 
                size="small" 
                :type="getCourseBookingStatusTagType(row.status)" 
                effect="plain">
                {{ getCourseBookingStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="bookAt" label="选课时间" min-width="160" align="center">
            <template slot-scope="{row}">
              <div class="booked_at">
                <i class="el-icon-time"></i>
                <span class="time-text">{{ formatDate(row.bookAt) }}</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页组件 -->
      <div class="course-student-pagination">
        <el-pagination
          v-show="courseStudentTotal > 0"
          @size-change="handleCourseStudentSizeChange"
          @current-change="handleCourseStudentCurrentChange"
          :current-page="courseStudentQueryParams.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="courseStudentQueryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="courseStudentTotal"
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
import { 
  getCourseOverview, 
  getCollegeCourseStats, 
  getCourseTypeDistribution, 
  getCourseStatsByCollege,
  getCollegeCourses
} from '@/api/system/courseStatistics'
import { listCourses, getCourseBookings } from '@/api/system/courses'
import { listBookingsWithActivity } from '@/api/system/bookings'
import * as echarts from 'echarts'
import Qstatistics from '@/views/Qstatistics/index.vue'

export default {
  name: 'AdminTeacherHome',
  components: {
    Qstatistics
  },
  data() {
    return {
      activeMenu: 'activity', // 默认选中书院活动
      selectedCollege: 'all', // 默认选中全部书院
      menuItems: [
        {
          key: 'activity',
          label: '书院活动',
          icon: 'el-icon-s-promotion'
        },
        {
          key: 'course',
          label: '书院课程',
          icon: 'el-icon-reading'
        },
        {
          key: 'major',
          label: '专业分流',
          icon: 'el-icon-s-grid'
        }
      ],
      collegeList: [
        {
          key: 'dayu',
          name: '大煜书院',
          icon: 'el-icon-school'
        },
        {
          key: 'bochuan',
          name: '伯川书院',
          icon: 'el-icon-school'
        },
        {
          key: 'lingxi',
          name: '令希书院',
          icon: 'el-icon-school'
        },
        {
          key: 'houde',
          name: '厚德书院',
          icon: 'el-icon-school'
        },
        {
          key: 'zhixing',
          name: '知行书院',
          icon: 'el-icon-school'
        },
        {
          key: 'duxue',
          name: '笃学书院',
          icon: 'el-icon-school'
        },
        {
          key: 'qiushi',
          name: '求实书院',
          icon: 'el-icon-school'
        }
      ],
      // 活动统计数据
      activityStats: {
        totalActivities: 0,
        ongoingActivities: 0,
        completedActivities: 0,
        totalParticipants: 0
      },
      // 各书院活动数据
      collegeActivityData: {},
      // 活动类型分布
      activityTypes: [
        { key: '1', name: '人格塑造与价值引领活动类', count: 0, percentage: 0 },
        { key: '2', name: '知识融合与思维进阶活动类', count: 0, percentage: 0 },
        { key: '3', name: '能力锻造与实践创新活动类', count: 0, percentage: 0 },
        { key: '4', name: '社会责任与领军意识活动类', count: 0, percentage: 0 }
      ],
      // 最近活动列表
      recentActivities: [],
      // 课程统计数据
      courseStats: {
        totalCourses: 0,
        notStartedCourses: 0,
        enrollingCourses: 0,
        enrollmentEndedCourses: 0,
        ongoingCourses: 0,
        completedCourses: 0,
        totalEnrollments: 0
      },
      // 各书院课程数据
      collegeCourseData: {},
      // 课程类型分布
      courseTypes: [
        { key: '1', name: '人格塑造与价值引领课程类', count: 0, percentage: 0 },
        { key: '2', name: '知识融合与思维进阶课程类', count: 0, percentage: 0 },
        { key: '3', name: '能力锻造与实践创新课程类', count: 0, percentage: 0 },
        { key: '4', name: '社会责任与领军意识课程类', count: 0, percentage: 0 }
      ],
        
      // 弹窗相关数据
      dialogVisible: false,
      selectedCollegeForDetail: '',
      collegeNameForDetail: '',
      collegeActivities: [],
      loading: false,
      // 课程详情弹窗相关数据
      courseDialogVisible: false,
      selectedCollegeForCourseDetail: '',
      collegeNameForCourseDetail: '',
      courseList: [],
      courseLoading: false,
      courseTotal: 0,
      // 课程统计数据
      courseStats: {
        total: 0,
        ongoing: 0,
        completed: 0,
        enrollments: 0
      },
        courseQueryParams: {
          pageNum: 1,
          pageSize: 10,
          organizer: ''
        },
        // 课程学生列表弹窗
        courseStudentDialogVisible: false,
        selectedCourseForStudents: null,
        courseStudentList: [],
        courseStudentTotal: 0,
        courseStudentQueryParams: {
          pageNum: 1,
          pageSize: 10,
          courseId: null
        },
        
        // 图表相关
        courseCountChart: null,
        enrollmentCountChart: null,
        
        // 课程学生统计数据
        courseStudentStats: {
          total: 0,
          ongoing: 0,
          completed: 0,
          approved: 0,
          rejected: 0
        },
      // 分页相关数据
      currentPage: 1,
      pageSize: 10,
      totalActivities: 0,
      // 学生列表相关数据
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
      // 学生统计数据
      studentStats: {
        total: 0,
        submitted: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      }
    }
  },
  computed: {
    // 分页后的活动列表
    paginatedActivities() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.collegeActivities.slice(start, end)
    }
  },
  methods: {
    handleMenuClick(menuKey) {
      this.activeMenu = menuKey
      // 当切换到活动页面时，加载活动统计数据
      if (menuKey === 'activity') {
        this.loadActivityStats()
      }
      // 当切换到课程页面时，加载课程统计数据
      if (menuKey === 'course') {
        this.loadCourseStats()
      }
    },
    async handleCollegeFilter(collegeKey) {
      this.selectedCollege = collegeKey
      // 根据选中的书院重新加载数据
      if (this.activeMenu === 'activity') {
        await this.loadActivityStatsByCollege(collegeKey)
      } else if (this.activeMenu === 'course') {
        await this.loadCourseStatsByCollege(collegeKey)
      }
      console.log('筛选书院:', collegeKey)
    },
    getCollegeName(collegeKey) {
      if (collegeKey === 'all') return '全部书院'
      const college = this.collegeList.find(item => item.key === collegeKey)
      return college ? college.name : ''
    },
    // 获取书院活动数量
    getCollegeActivityCount(collegeKey) {
      if (collegeKey === 'all') return this.activityStats.totalActivities
      return this.collegeActivityData[collegeKey]?.totalActivities || 0
    },
    // 获取书院进行中活动数量
    getCollegeOngoingCount(collegeKey) {
      if (collegeKey === 'all') return this.activityStats.ongoingActivities
      return this.collegeActivityData[collegeKey]?.ongoingActivities || 0
    },
    // 获取书院参与人数
    getCollegeParticipantCount(collegeKey) {
      if (collegeKey === 'all') return this.activityStats.totalParticipants
      return this.collegeActivityData[collegeKey]?.participants || 0
    },
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'not-started': '未开始',
        'ongoing': '进行中',
        'completed': '已完成',
        'cancelled': '已取消'
      }
      return statusMap[status] || status
    },
    // 获取课程状态文本
    getCourseStatusText(status) {
      const statusMap = {
        'not-started': '未开始选课',
        'enrolling': '选课中',
        'enrollment-ended': '选课已结束',
        'completed': '课程已完成'
      }
      return statusMap[status] || status
    },
    // 获取书院课程数量
    getCollegeCourseCount(collegeKey) {
      if (collegeKey === 'all') return this.courseStats.totalCourses
      return this.collegeCourseData[collegeKey]?.totalCourses || 0
    },
    // 获取书院选课中课程数量
    getCollegeEnrollingCount(collegeKey) {
      if (collegeKey === 'all') return this.courseStats.enrollingCourses
      return this.collegeCourseData[collegeKey]?.enrollingCourses || 0
    },
    // 获取书院选课人数
    getCollegeEnrollmentCount(collegeKey) {
      if (collegeKey === 'all') return this.courseStats.totalEnrollments
      return this.collegeCourseData[collegeKey]?.enrollments || 0
    },
    // 获取书院分流人数
    getCollegeTransferCount(collegeKey) {
      if (collegeKey === 'all') return this.majorStats.totalStudents
      return this.collegeTransferData[collegeKey]?.totalTransfers || 0
    },
    // 获取书院转入人数
    getCollegeIncomingCount(collegeKey) {
      if (collegeKey === 'all') return 0
      return this.collegeTransferData[collegeKey]?.incomingTransfers || 0
    },
    // 获取书院转出人数
    getCollegeOutgoingCount(collegeKey) {
      if (collegeKey === 'all') return 0
      return this.collegeTransferData[collegeKey]?.outgoingTransfers || 0
    },
    // 获取分流状态文本
    getTransferStatusText(status) {
      const statusMap = {
        'completed': '已完成',
        'processing': '处理中',
        'pending': '待审核',
        'rejected': '已拒绝'
      }
      return statusMap[status] || status
    },
    // 加载活动统计数据
    async loadActivityStats() {
      try {
        // 获取活动统计概览
        const overviewResponse = await getActivityOverview()
        if (overviewResponse.code === 200) {
          this.activityStats = overviewResponse.data
        }

        // 获取各书院活动统计
        const collegeStatsResponse = await getCollegeActivityStats()
        if (collegeStatsResponse.code === 200) {
          // 将后端返回的书院名称映射到前端的书院key
          const backendCollegeData = collegeStatsResponse.data.collegeData
          this.collegeActivityData = {}
          
          // 书院名称到key的映射
          const collegeNameToKey = {
            '大煜书院': 'dayu',
            '伯川书院': 'bochuan', 
            '令希书院': 'lingxi',
            '厚德书院': 'houde',
            '知行书院': 'zhixing',
            '笃学书院': 'duxue',
            '求实书院': 'qiushi'
          }
          
          // 转换数据格式
          for (const [collegeName, stats] of Object.entries(backendCollegeData)) {
            const collegeKey = collegeNameToKey[collegeName]
            if (collegeKey) {
              this.collegeActivityData[collegeKey] = stats
            }
          }
        }

        // 获取活动类型分布
        const typeDistributionResponse = await getActivityTypeDistribution()
        if (typeDistributionResponse.code === 200) {
          this.activityTypes = typeDistributionResponse.data.typeDistribution
        }

        // 获取最近活动列表
        const recentActivitiesResponse = await getRecentActivities(10)
        if (recentActivitiesResponse.code === 200) {
          // 过滤出进行中和已结束的活动，按开始时间降序排序，最多显示3条
          const allActivities = recentActivitiesResponse.data.recentActivities || []
          this.recentActivities = allActivities
            .filter(activity => activity.status === 'ongoing' || activity.status === 'completed')
            .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
            .slice(0, 3)
        }
      } catch (error) {
        console.error('加载活动统计数据失败:', error)
        this.$message.error('加载活动统计数据失败')
      }
    },
    // 根据书院筛选活动统计数据
    async loadActivityStatsByCollege(collegeKey) {
      try {
        if (collegeKey === 'all') {
          // 如果选择全部书院，重新加载完整数据
          await this.loadActivityStats()
          return
        }

        // 获取书院名称
        const collegeName = this.getCollegeName(collegeKey)
        
        // 根据书院筛选活动统计
        const response = await getActivityStatsByCollege(collegeName)
        if (response.code === 200) {
          // 更新活动统计概览
          this.activityStats = response.data
          
          // 保持各书院数据不变，不更新 collegeActivityData
          // 这样各书院活动统计始终显示所有书院的数据
          
          // 更新活动类型分布（根据选中书院筛选）
          if (response.data.typeDistribution) {
            this.activityTypes = response.data.typeDistribution
          }
          
          // 更新最近活动列表（根据选中书院筛选）
          if (response.data.recentActivities) {
            // 过滤出进行中和已结束的活动，按开始时间降序排序，最多显示3条
            const allActivities = response.data.recentActivities || []
            this.recentActivities = allActivities
              .filter(activity => activity.status === 'ongoing' || activity.status === 'completed')
              .sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
              .slice(0, 3)
          }
        }
      } catch (error) {
        console.error('加载书院活动统计数据失败:', error)
        this.$message.error('加载书院活动统计数据失败')
      }
    },
    // 查看书院活动详情
    viewCollegeActivities(collegeKey, collegeName) {
      this.selectedCollegeForDetail = collegeKey
      this.collegeNameForDetail = collegeName
      this.dialogVisible = true
      this.loadCollegeActivities(collegeName)
    },
    // 加载书院活动列表
    async loadCollegeActivities(collegeName) {
      try {
        this.loading = true
        const response = await getCollegeActivities(collegeName)
        if (response.code === 200) {
          this.collegeActivities = response.data.activities || []
          this.totalActivities = this.collegeActivities.length
          // 重置分页
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
    // 获取进行中活动数量
    getOngoingCount() {
      return this.collegeActivities.filter(activity => activity.status === 'ongoing').length
    },
    // 获取已完成活动数量
    getCompletedCount() {
      return this.collegeActivities.filter(activity => activity.status === 'completed').length
    },
    // 获取状态标签类型
    getStatusTagType(status) {
      const typeMap = {
        'ongoing': 'warning',
        'completed': 'success',
        'not-started': 'info'
      }
      return typeMap[status] || 'info'
    },
    // 查看活动详情
    viewActivityDetail(activity) {
      this.currentActivityId = activity.id
      this.currentActivityName = activity.name
      this.handleViewStudents(activity)
    },
    // 跳转到活动管理页面
    goToActivityManager() {
      this.$router.push({
        path: '/activity/manager',
        query: {
          organizer: this.collegeNameForDetail
        }
      })
      this.dialogVisible = false
    },
    // 关闭弹窗
    handleClose(done) {
      this.dialogVisible = false
      done()
    },
    // 处理课程详情
    handleCourseDetail(collegeKey) {
      const collegeName = this.getCollegeName(collegeKey)
      this.selectedCollegeForCourseDetail = collegeKey
      this.collegeNameForCourseDetail = collegeName
      this.courseDialogVisible = true
      this.loadCollegeCourses(collegeName)
    },
    // 加载书院课程列表
    async loadCollegeCourses(collegeName) {
      try {
        this.courseLoading = true
        this.courseQueryParams.organizer = collegeName
        
        const response = await listCourses(this.courseQueryParams)
        if (response.code === 200) {
          this.courseList = response.rows || []
          this.courseTotal = response.total || 0
          
          // 计算课程统计数据
          this.calculateCourseStats(this.courseList)
        }
      } catch (error) {
        console.error('加载书院课程列表失败:', error)
        this.$message.error('加载书院课程列表失败')
      } finally {
        this.courseLoading = false
      }
    },
    
    // 计算课程统计数据
    calculateCourseStats(courses) {
      this.courseStats = {
        total: courses.length,
        ongoing: 0,
        completed: 0,
        enrollments: 0
      }
      
      courses.forEach(course => {
        const courseStatus = this.computeCourseStatus(course)
        
        // 根据课程状态统计
        if (courseStatus === '课程进行中') {
          this.courseStats.ongoing++
        } else if (courseStatus === '课程已完成') {
          this.courseStats.completed++
        }
        
        // 计算选课人数
        const courseCapacity = course.courseCapacity || 0
        const courseTotalCapacity = course.courseTotalCapacity || 0
        const enrollments = Math.max(0, courseCapacity - courseTotalCapacity)
        this.courseStats.enrollments += enrollments
      })
    },
    
    // 课程分页大小改变
    handleCourseSizeChange(val) {
      this.courseQueryParams.pageSize = val
      this.courseQueryParams.pageNum = 1
      this.loadCollegeCourses(this.courseQueryParams.organizer)
    },
    // 课程当前页改变
    handleCourseCurrentChange(val) {
      this.courseQueryParams.pageNum = val
      this.loadCollegeCourses(this.courseQueryParams.organizer)
    },
    // 关闭课程弹窗
    handleCourseClose(done) {
      this.courseDialogVisible = false
      done()
    },
    // 格式化日期时间
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
    // 格式化日期
    formatDate(dateTime) {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },
    // 获取课程分类名称
    getCourseCategoryName(category) {
      // 处理数字类型和字符串类型
      const categoryValue = String(category)
      const categoryMap = {
        '1': '人格塑造与价值引领课程类',
        '2': '知识融合与思维进阶课程类',
        '3': '能力锻造与实践创新课程类',
        '4': '社会责任与领军意识课程类'
      }
      return categoryMap[categoryValue] || '其他'
    },
    // 获取课程分类标签类型
    getCourseCategoryTagType(category) {
      // 处理数字类型和字符串类型
      const categoryValue = String(category)
      const typeMap = {
        '1': 'success',
        '2': 'primary',
        '3': 'warning',
        '4': 'danger'
      }
      return typeMap[categoryValue] || 'info'
    },
    // 获取课程状态名称
    getCourseStatusName(status) {
      const statusMap = {
        'not-started': '未开始',
        'enrolling': '选课中',
        'enrollment-ended': '选课已结束',
        'ongoing': '进行中',
        'completed': '已结束'
      }
      return statusMap[status] || '未知'
    },
    // 计算课程状态（基于时间）
    computeCourseStatus(course) {
      const now = new Date()
      const start = course.courseStart ? new Date(course.courseStart) : null
      const deadline = course.courseDeadline ? new Date(course.courseDeadline) : null

      // 如果当前时间在选课开始时间之前，显示"选课未开始"
      if (start && now < start) {
        return '选课未开始'
      }

      // 如果当前时间在选课开始时间和截止时间之间，显示"选课进行中"
      if (start && deadline && now >= start && now <= deadline) {
        return '选课进行中'
      }

      // 如果当前时间超过选课截止时间，显示"选课已截止"
      if (deadline && now > deadline) {
        return '选课已截止'
      }

      // 默认状态
      return '选课未开始'
    },
    // 获取课程状态标签类型
    getCourseStatusTagType(status) {
      const typeMap = {
        '选课未开始': 'info',
        '选课进行中': 'success',
        '选课已截止': 'danger',
        'not-started': 'info',
        'enrolling': 'success',
        'enrollment-ended': 'warning',
        'ongoing': 'primary',
        'completed': 'danger'
      }
      return typeMap[status] || 'info'
    },
    // 查看课程学生列表
    handleViewCourseStudents(course) {
      this.selectedCourseForStudents = course
      this.courseStudentDialogVisible = true
      this.courseStudentQueryParams.courseId = course.courseId
      this.courseStudentQueryParams.pageNum = 1
      this.courseStudentQueryParams.pageSize = 10
      this.loadCourseStudentList()
    },
    // 加载课程学生列表
    async loadCourseStudentList() {
      try {
        const response = await getCourseBookings(this.courseStudentQueryParams.courseId)
        console.log('课程学生列表API响应:', response)
        if (response.code === 200) {
          const allStudents = response.rows || []
          console.log('课程学生数据:', allStudents)
          this.courseStudentTotal = allStudents.length
          
          // 计算课程学生统计数据
          this.calculateCourseStudentStats(allStudents)
          console.log('课程学生统计数据:', this.courseStudentStats)
          
          // 前端分页处理
          const startIndex = (this.courseStudentQueryParams.pageNum - 1) * this.courseStudentQueryParams.pageSize
          const endIndex = startIndex + this.courseStudentQueryParams.pageSize
          this.courseStudentList = allStudents.slice(startIndex, endIndex)
        } else {
          // 重置统计数据
          this.courseStudentStats = {
            total: 0,
            ongoing: 0,
            completed: 0,
            approved: 0,
            rejected: 0
          }
        }
      } catch (error) {
        console.error('加载课程学生列表失败:', error)
        this.$message.error('加载课程学生列表失败')
      }
    },
    // 课程学生分页大小改变
    handleCourseStudentSizeChange(val) {
      this.courseStudentQueryParams.pageSize = val
      this.courseStudentQueryParams.pageNum = 1
      this.loadCourseStudentList()
    },
    // 课程学生当前页改变
    handleCourseStudentCurrentChange(val) {
      this.courseStudentQueryParams.pageNum = val
      this.loadCourseStudentList()
    },
    
    // 计算课程学生统计数据
    calculateCourseStudentStats(allStudents) {
      this.courseStudentStats = {
        total: allStudents.length,
        ongoing: 0,
        completed: 0,
        approved: 0,
        rejected: 0
      }
      
      allStudents.forEach(student => {
        const status = student.status || '未提交'
        const courseStatus = this.computeCourseStatus(student)
        
        // 根据课程状态统计
        if (courseStatus === '课程进行中') {
          this.courseStudentStats.ongoing++
        } else if (courseStatus === '课程已完成') {
          this.courseStudentStats.completed++
        }
        
        // 根据审核状态统计
        switch (status) {
          case '已通过':
            this.courseStudentStats.approved++
            break
          case '未通过':
            this.courseStudentStats.rejected++
            break
        }
      })
    },
    
    // 关闭课程学生弹窗
    handleCourseStudentDialogClose(done) {
      this.courseStudentDialogVisible = false
      done()
    },
    // 分页大小改变
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
    },
    // 当前页改变
    handleCurrentChange(val) {
      this.currentPage = val
    },
    // 查看已选学生
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
        // 获取所有学生数据（用于排序和统计）
        const allStudentsRes = await listBookingsWithActivity({
          activityId: activity.id,
          pageNum: 1,
          pageSize: 1000 // 获取所有学生数据
        })
        
        // 保存所有学生数据
        this.allStudents = allStudentsRes.rows || []
        this.studentTotal = allStudentsRes.total || 0
        
        // 计算统计数据
        this.calculateStudentStats(this.allStudents)
        
        // 获取当前页数据
        this.getCurrentPageStudents()
        
        this.studentDialogVisible = true
        
      } catch (e) {
        // 即使出错也显示弹框，但显示错误信息
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
    // 计算学生统计数据
    calculateStudentStats(allStudents) {
      this.studentStats.total = allStudents.length
      this.studentStats.submitted = allStudents.filter(s => s.status === 'submitted').length
      this.studentStats.pending = allStudents.filter(s => s.status === 'pending').length
      this.studentStats.approved = allStudents.filter(s => s.status === 'approved').length
      this.studentStats.rejected = allStudents.filter(s => s.status === 'rejected').length
    },
    // 重置学生统计数据
    resetStudentStats() {
      this.studentStats = {
        total: 0,
        submitted: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      }
    },
    // 获取当前页学生数据（从所有学生数据中分页）
    getCurrentPageStudents() {
      if (!this.allStudents || this.allStudents.length === 0) {
        this.selectedStudents = []
        return
      }
      
      const startIndex = (this.studentQueryParams.pageNum - 1) * this.studentQueryParams.pageSize
      const endIndex = startIndex + this.studentQueryParams.pageSize
      this.selectedStudents = this.allStudents.slice(startIndex, endIndex)
    },
    // 学生列表分页大小改变
    handleStudentSizeChange(val) {
      this.studentQueryParams.pageSize = val
      this.studentQueryParams.pageNum = 1
      this.getCurrentPageStudents()
    },
    // 学生列表当前页改变
    handleStudentCurrentChange(val) {
      this.studentQueryParams.pageNum = val
      this.getCurrentPageStudents()
    },
    // 关闭学生弹窗
    handleStudentDialogClose(done) {
      this.studentDialogVisible = false
      done()
    },
    // 复制到剪贴板
    copyToClipboard(text) {
      if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(() => {
          this.$message.success('已复制到剪贴板')
        }).catch(() => {
          this.$message.error('复制失败')
        })
      } else {
        // 兼容旧浏览器
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
    // 获取书院标签类型
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
    // 获取预约状态标签类型
    getBookingStatusTag(status) {
      const typeMap = {
        'pending': 'warning',
        'approved': 'success',
        'rejected': 'danger',
        'submitted': 'info'
      }
      return typeMap[status] || 'info'
    },
    // 获取预约状态文本
    getBookingStatusText(status) {
      const statusMap = {
        'pending': '待审核',
        'approved': '已通过',
        'rejected': '未通过',
        'submitted': '已提交'
      }
      return statusMap[status] || status
    },
    
    // 获取课程选课状态文本
    getCourseBookingStatusText(status) {
      const statusMap = {
        '未提交': '未提交',
        '未审核': '未审核',
        '已通过': '已通过',
        '未通过': '未通过'
      }
      return statusMap[status] || status || '未知'
    },
    
    // 获取课程选课状态标签类型
    getCourseBookingStatusTagType(status) {
      const typeMap = {
        '未提交': 'info',
        '未审核': 'warning',
        '已通过': 'success',
        '未通过': 'danger'
      }
      return typeMap[status] || 'info'
    },
    // 获取学生行样式
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
    // 导出学生名单
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

      // 使用系统下载方法
      this.download('/system/bookings/exportStudents', {
        activityId: activityId
      }, `预约学生名单_${activityName}_${this.parseTime(new Date(), '{y}{m}{d}')}.xlsx`)

      this.studentDialogVisible = false // 导出后自动关闭对话框
    },
    // 加载课程统计数据
    async loadCourseStats() {
      try {
        // 获取课程统计概览
        const overviewResponse = await getCourseOverview()
        if (overviewResponse.code === 200) {
          this.courseStats = overviewResponse.data
        }

        // 获取各书院课程统计
        const collegeStatsResponse = await getCollegeCourseStats()
        if (collegeStatsResponse.code === 200) {
          // 将后端返回的书院名称映射到前端的书院key
          const backendCollegeData = collegeStatsResponse.data.collegeData
          this.collegeCourseData = {}
          
          // 书院名称到key的映射
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
              this.collegeCourseData[collegeKey] = stats
            }
          }
        }

        // 获取课程类型分布
        const typeDistributionResponse = await getCourseTypeDistribution()
        if (typeDistributionResponse.code === 200) {
          const typeDistribution = typeDistributionResponse.data.typeDistribution
          this.courseTypes = [
            { key: '1', name: '人格塑造与价值引领课程类', count: typeDistribution['人格塑造与价值引领课程类'] || 0, percentage: 0 },
            { key: '2', name: '知识融合与思维进阶课程类', count: typeDistribution['知识融合与思维进阶课程类'] || 0, percentage: 0 },
            { key: '3', name: '能力锻造与实践创新课程类', count: typeDistribution['能力锻造与实践创新课程类'] || 0, percentage: 0 },
            { key: '4', name: '社会责任与领军意识课程类', count: typeDistribution['社会责任与领军意识课程类'] || 0, percentage: 0 }
          ]
          
          // 计算百分比
          const total = this.courseTypes.reduce((sum, type) => sum + type.count, 0)
          this.courseTypes.forEach(type => {
            type.percentage = total > 0 ? Math.round((type.count / total) * 100 * 10) / 10 : 0
          })
        }

        // 初始化图表
        this.initCourseCharts()
      } catch (error) {
        console.error('加载课程统计数据失败:', error)
        this.$message.error('加载课程统计数据失败')
      }
    },
    // 根据书院筛选课程统计数据
    async loadCourseStatsByCollege(collegeKey) {
      try {
        if (collegeKey === 'all') {
          // 如果选择全部书院，重新加载完整数据
          await this.loadCourseStats()
          return
        }

        // 获取书院名称
        const collegeName = this.getCollegeName(collegeKey)
        
        // 根据书院筛选课程统计
        const response = await getCourseStatsByCollege(collegeName)
        if (response.code === 200) {
          // 更新课程统计概览
          this.courseStats = response.data
          
          // 保持各书院数据不变，不更新 collegeCourseData
          // 这样各书院课程统计始终显示所有书院的数据
          
          // 更新课程类型分布（根据选中书院筛选）
          if (response.data.typeDistribution) {
            const typeDistribution = response.data.typeDistribution
            this.courseTypes = [
              { key: '1', name: '人格塑造与价值引领课程类', count: typeDistribution['人格塑造与价值引领课程类'] || 0, percentage: 0 },
              { key: '2', name: '知识融合与思维进阶课程类', count: typeDistribution['知识融合与思维进阶课程类'] || 0, percentage: 0 },
              { key: '3', name: '能力锻造与实践创新课程类', count: typeDistribution['能力锻造与实践创新课程类'] || 0, percentage: 0 },
              { key: '4', name: '社会责任与领军意识课程类', count: typeDistribution['社会责任与领军意识课程类'] || 0, percentage: 0 }
            ]
            
            // 计算百分比
            const total = this.courseTypes.reduce((sum, type) => sum + type.count, 0)
            this.courseTypes.forEach(type => {
              type.percentage = total > 0 ? Math.round((type.count / total) * 100 * 10) / 10 : 0
            })
          }
          
        }
      } catch (error) {
        console.error('加载书院课程统计数据失败:', error)
        this.$message.error('加载书院课程统计数据失败')
      }
    },
    // 加载专业分流统计数据
    loadMajorStats() {
      // 模拟数据，实际应该调用API
      this.majorStats = {
        totalStudents: 1247,
        crossCollegeTransfers: 234,
        keepOriginalMajor: 856,
        majorChanges: 157
      }
      
      // 模拟各书院分流数据
      this.collegeTransferData = {
        dayu: { totalTransfers: 156, incomingTransfers: 23, outgoingTransfers: 18 },
        bochuan: { totalTransfers: 134, incomingTransfers: 19, outgoingTransfers: 22 },
        lingxi: { totalTransfers: 178, incomingTransfers: 28, outgoingTransfers: 15 },
        houde: { totalTransfers: 145, incomingTransfers: 21, outgoingTransfers: 19 },
        zhixing: { totalTransfers: 167, incomingTransfers: 25, outgoingTransfers: 17 },
        duxue: { totalTransfers: 123, incomingTransfers: 16, outgoingTransfers: 24 },
        qiushi: { totalTransfers: 189, incomingTransfers: 31, outgoingTransfers: 12 }
      }
      
      // 模拟分流类型分布
      this.transferTypes = [
        { key: '1', name: '保持当前专业', count: 856, percentage: 69 },
        { key: '2', name: '域内任选专业', count: 234, percentage: 19 },
        { key: '3', name: '类内任选专业', count: 89, percentage: 7 },
        { key: '4', name: '创新班政策内任选专业', count: 45, percentage: 4 },
        { key: '5', name: '转专业', count: 23, percentage: 1 }
      ]
      
      // 模拟热门转入书院
      this.popularIncomingColleges = [
        { name: '令希书院', count: 89 },
        { name: '求实书院', count: 76 },
        { name: '知行书院', count: 65 },
        { name: '大煜书院', count: 54 },
        { name: '厚德书院', count: 43 }
      ]
      
      // 模拟热门转出书院
      this.popularOutgoingColleges = [
        { name: '笃学书院', count: 67 },
        { name: '伯川书院', count: 54 },
        { name: '厚德书院', count: 43 },
        { name: '大煜书院', count: 38 },
        { name: '知行书院', count: 32 }
      ]
      
      // 模拟最近分流记录
      this.recentTransfers = [
        {
          id: 1,
          studentName: '张三',
          fromCollege: '笃学书院',
          toCollege: '令希书院',
          transferType: '域内任选专业',
          date: '2024-01-15',
          status: 'completed'
        },
        {
          id: 2,
          studentName: '李四',
          fromCollege: '伯川书院',
          toCollege: '求实书院',
          transferType: '类内任选专业',
          date: '2024-01-14',
          status: 'processing'
        },
        {
          id: 3,
          studentName: '王五',
          fromCollege: '厚德书院',
          toCollege: '知行书院',
          transferType: '转专业',
          date: '2024-01-13',
          status: 'completed'
        }
      ]
    },
    
    // 初始化课程统计图表
    initCourseCharts() {
      this.$nextTick(() => {
        this.initCourseCountChart()
        this.initEnrollmentCountChart()
      })
    },
    
    // 初始化课程数统计图表
    initCourseCountChart() {
      if (this.courseCountChart) {
        this.courseCountChart.dispose()
      }
      
      const chartDom = this.$refs.courseCountChart
      if (!chartDom) return
      
      this.courseCountChart = echarts.init(chartDom)
      
      // 确保所有七个书院都显示
      const allColleges = [
        { key: 'dayu', name: '大煜书院' },
        { key: 'bochuan', name: '伯川书院' },
        { key: 'lingxi', name: '令希书院' },
        { key: 'houde', name: '厚德书院' },
        { key: 'zhixing', name: '知行书院' },
        { key: 'duxue', name: '笃学书院' },
        { key: 'qiushi', name: '求实书院' }
      ]
      
      const collegeData = this.collegeCourseData || {}
      const data = allColleges.map(college => ({
        name: college.name,
        value: collegeData[college.key]?.totalCourses || 0
      }))
      
      const option = {
        title: {
          left: 'center',
          textStyle: {
            fontSize: 14,
            fontWeight: 'normal'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: '5%',
          top: 'middle',
          width: '20%',
          formatter: (name) => {
            return name
          },
          textStyle: {
            fontSize: 12,
            fontWeight: 'bold',
            color: '#374151'
          }
        },
        series: [
          {
            name: '课程数',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: false,
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
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          },
          {
            name: '中心文字',
            type: 'pie',
            radius: ['0%', '30%'],
            center: ['50%', '50%'],
            label: {
              show: true,
              position: 'center',
              formatter: () => {
                const total = data.reduce((sum, item) => sum + item.value, 0)
                return `{total|${total}}\n{label|总课程数}`
              },
              rich: {
                total: {
                  fontSize: 24,
                  fontWeight: 'bold',
                  color: '#374151'
                },
                label: {
                  fontSize: 14,
                  color: '#6b7280'
                }
              }
            },
            data: [{ value: 1, itemStyle: { color: 'transparent' } }]
          }
        ]
      }
      
      this.courseCountChart.setOption(option)
    },
    
    // 初始化选课人数统计图表
    initEnrollmentCountChart() {
      if (this.enrollmentCountChart) {
        this.enrollmentCountChart.dispose()
      }
      
      const chartDom = this.$refs.enrollmentCountChart
      if (!chartDom) return
      
      this.enrollmentCountChart = echarts.init(chartDom)
      
      // 确保所有七个书院都显示
      const allColleges = [
        { key: 'dayu', name: '大煜书院' },
        { key: 'bochuan', name: '伯川书院' },
        { key: 'lingxi', name: '令希书院' },
        { key: 'houde', name: '厚德书院' },
        { key: 'zhixing', name: '知行书院' },
        { key: 'duxue', name: '笃学书院' },
        { key: 'qiushi', name: '求实书院' }
      ]
      
      const collegeData = this.collegeCourseData || {}
      const data = allColleges.map(college => ({
        name: college.name,
        value: collegeData[college.key]?.enrollments || 0
      }))
      
      const option = {
        title: {
          left: 'center',
          textStyle: {
            fontSize: 14,
            fontWeight: 'normal'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: '5%',
          top: 'middle',
          width: '20%',
          formatter: (name) => {
            return name
          },
          textStyle: {
            fontSize: 12,
            fontWeight: 'bold',
            color: '#374151'
          }
        },
        series: [
          {
            name: '选课人数',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: false,
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
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          },
          {
            name: '中心文字',
            type: 'pie',
            radius: ['0%', '30%'],
            center: ['50%', '50%'],
            label: {
              show: true,
              position: 'center',
              formatter: () => {
                const total = data.reduce((sum, item) => sum + item.value, 0)
                return `{total|${total}}\n{label|总选课人数}`
              },
              rich: {
                total: {
                  fontSize: 24,
                  fontWeight: 'bold',
                  color: '#374151'
                },
                label: {
                  fontSize: 14,
                  color: '#6b7280'
                }
              }
            },
            data: [{ value: 1, itemStyle: { color: 'transparent' } }]
          }
        ]
      }
      
      this.enrollmentCountChart.setOption(option)
    }
  },
  mounted() {
    // 页面加载时初始化数据
    this.loadActivityStats()
    this.loadCourseStats()
    this.loadMajorStats()
  }
}
</script>

<style scoped>
/* 主容器 */
.app-container {
  margin-left: 100px;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 主布局 */
.main-layout {
  display: flex;
  gap: 0;
}

/* 左侧书院筛选面板 */
.college-filter-panel {
  width: 256px;
  background: white;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.panel-header {
  padding: 20px 20px 10px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.panel-content {
  padding: 20px;
  flex: 1;
}

.college-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.college-button {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.college-button:hover {
  border-color: #3b82f6;
  background: #f8fafc;
  transform: translateX(2px);
}

.college-button-active {
  border-color: #3b82f6;
  background: #eff6ff;
  color: #1d4ed8;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}

.college-icon {
  margin-right: 12px;
  font-size: 16px;
}

.all-college-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.all-college-button {
  border-color: #10b981;
  color: #059669;
}

.all-college-button:hover {
  border-color: #059669;
  background: #f0fdf4;
}

.all-college-button-active {
  border-color: #10b981;
  background: #f0fdf4;
  color: #047857;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
}

/* 右侧主内容区域 */
.main-content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  min-width: 0;
}

/* 顶部菜单栏 */
.top-menu-bar {
  background: white;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.menu-container {
  padding: 0 24px;
}

.menu-buttons {
  display: flex;
  gap: 32px;
}

.menu-button {
  padding: 16px 0;
  border: none;
  background: none;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.menu-button:hover {
  color: #333;
  border-bottom-color: #d1d5db;
}

.menu-button-active {
  color: #3b82f6;
  border-bottom-color: #3b82f6;
}

.menu-icon {
  margin-right: 8px;
  font-size: 16px;
}

/* 内容区域 */
.content-area {
  flex: 1;
  background: #f8fafc;
}

.content-wrapper {
  padding: 24px;
}

.content-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.content-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px 0;
}

.college-indicator {
  font-size: 14px;
  font-weight: 400;
  color: #3b82f6;
}

.content-description {
  color: #6b7280;
  font-size: 16px;
  margin: 0 0 16px 0;
}

.debug-info {
  background: #f3f4f6;
  padding: 12px;
  border-radius: 6px;
  margin-top: 16px;
}

.debug-info p {
  margin: 4px 0;
  font-size: 12px;
  color: #6b7280;
}

.page-content {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 活动统计样式 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.overview-card:nth-child(1) .card-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.overview-card:nth-child(2) .card-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.overview-card:nth-child(3) .card-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.overview-card:nth-child(4) .card-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
}

/* 各书院统计样式 */
.college-stats-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 20px;
}

.college-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.college-stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid transparent;
}

.college-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.college-stat-card.active {
  border-color: #3b82f6;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.2);
}

.college-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  position: relative;
}

.college-stat-icon {
  font-size: 20px;
  color: #3b82f6;
  margin-right: 12px;
}

.college-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  flex: 1;
}

.detail-button {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: #3b82f6;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.detail-button:hover {
  background: #2563eb;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.detail-button:active {
  transform: scale(0.95);
}

.view-detail-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: #3b82f6;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
  z-index: 10;
}

.view-detail-btn:hover {
  background: #1d4ed8;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.view-detail-btn:active {
  transform: scale(0.95);
}

.college-numbers {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.stat-value.ongoing {
  color: #f59e0b;
}

.stat-value.participants {
  color: #10b981;
}

/* 活动类型分布样式 */
.activity-type-section {
  margin-bottom: 32px;
}

.type-distribution {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.type-item {
  margin-bottom: 20px;
}

.type-item:last-child {
  margin-bottom: 0;
}

.type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.type-name {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.type-count {
  font-size: 14px;
  color: #6b7280;
}

.progress-bar {
  height: 8px;
  background: #f3f4f6;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 4px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #1d4ed8);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.type-percentage {
  font-size: 12px;
  color: #6b7280;
  text-align: right;
}

/* 最近活动样式 */
.recent-activities-section {
  margin-bottom: 32px;
}

.activities-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.2s ease;
}

.activity-item:hover {
  background-color: #f8fafc;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-info {
  flex: 1;
}

.activity-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 8px;
}

.activity-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #6b7280;
}

.activity-college {
  color: #3b82f6;
  font-weight: 500;
}

.activity-type {
  color: #10b981;
}

.activity-date {
  color: #6b7280;
}

.activity-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-ongoing {
  background: #fef3c7;
  color: #d97706;
}

.status-completed {
  background: #d1fae5;
  color: #059669;
}

.status-not-started {
  background: #e5e7eb;
  color: #6b7280;
}

.status-cancelled {
  background: #fee2e2;
  color: #dc2626;
}

/* 课程统计样式 */
.course-type-section {
  margin-bottom: 32px;
}

/* 课程统计图表 */
.course-charts-section {
  margin-top: 24px;
  margin-bottom: 32px;
}

.charts-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 20px 0;
  text-align: center;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart {
  width: 100%;
  height: 100%;
}

.course-status-section {
  margin-bottom: 32px;
}

.status-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.status-stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.status-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.status-stat-card.not-started {
  border-left: 4px solid #6b7280;
}

.status-stat-card.enrolling {
  border-left: 4px solid #3b82f6;
}

.status-stat-card.ended {
  border-left: 4px solid #10b981;
}

.status-stat-card.ongoing {
  border-left: 4px solid #f59e0b;
}

.status-stat-card.completed {
  border-left: 4px solid #8b5cf6;
}

.status-stat-card .status-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.status-stat-card.not-started .status-icon {
  background: linear-gradient(135deg, #6b7280 0%, #9ca3af 100%);
}

.status-stat-card.enrolling .status-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.status-stat-card.ended .status-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.status-stat-card.ongoing .status-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.status-stat-card.completed .status-icon {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.status-content {
  flex: 1;
}

.status-title {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.status-count {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}


.stat-value.enrolling {
  color: #3b82f6;
}

.stat-value.enrollments {
  color: #8b5cf6;
}


.flow-stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.flow-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.flow-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f3f4f6;
}

.flow-icon {
  font-size: 20px;
  color: #3b82f6;
  margin-right: 12px;
}

.flow-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.flow-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.flow-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.flow-item:hover {
  background: #e2e8f0;
}

.flow-rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #3b82f6;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  margin-right: 12px;
}

.flow-name {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.flow-count {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

/* 最近分流记录样式 */
.recent-transfers-section {
  margin-bottom: 32px;
}

.transfers-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.transfer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.2s ease;
}

.transfer-item:hover {
  background-color: #f8fafc;
}

.transfer-item:last-child {
  border-bottom: none;
}

.transfer-info {
  flex: 1;
}

.transfer-student {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 8px;
}

.transfer-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #6b7280;
}

.transfer-from {
  color: #ef4444;
  font-weight: 500;
}

.transfer-arrow {
  color: #3b82f6;
  font-size: 16px;
}

.transfer-to {
  color: #10b981;
  font-weight: 500;
}

.transfer-type {
  color: #8b5cf6;
  font-weight: 500;
}

.transfer-date {
  color: #6b7280;
}

.transfer-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-completed {
  background: #d1fae5;
  color: #059669;
}

.status-processing {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-pending {
  background: #fef3c7;
  color: #d97706;
}

.status-rejected {
  background: #fee2e2;
  color: #dc2626;
}

.stat-value.incoming {
  color: #10b981;
}

.stat-value.outgoing {
  color: #ef4444;
}

/* 弹窗样式 */
.college-activities-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.college-activities-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
  border-bottom: none;
}

.college-activities-dialog .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.college-activities-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.college-activities-dialog .el-dialog__headerbtn .el-dialog__close:hover {
  color: #f0f0f0;
}

.dialog-content {
  padding: 0;
}

.dialog-stats {
  display: flex;
  gap: 24px;
  padding: 20px 24px;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

.dialog-stats .stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  min-width: 100px;
}

.dialog-stats .stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.dialog-stats .stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

.dialog-stats .stat-value.ongoing {
  color: #f59e0b;
}

.dialog-stats .stat-value.completed {
  color: #10b981;
}

.activities-table {
  padding: 24px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  background: #f8fafc;
  border-top: 1px solid #e5e7eb;
}

.dialog-footer {
  padding: 16px 24px;
  background: #f8fafc;
  border-top: 1px solid #e5e7eb;
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 12px;
}

/* 课程学生列表弹窗样式 */
.course-student-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.course-student-dialog .el-dialog__header {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  padding: 20px 24px;
  border-bottom: none;
}

.course-student-dialog .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.course-student-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.course-student-dialog .el-dialog__headerbtn .el-dialog__close:hover {
  color: #f0f0f0;
}

.course-student-dialog .el-dialog__body {
  padding: 0;
  background: #f8fafc;
}

/* 课程学生统计信息 */
/* 课程统计信息样式 */
.course-stats {
  padding: 20px 24px;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

.course-stats-card {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.course-stats-card .stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  min-width: 80px;
}

.course-stats-card .stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.course-stats-card .stat-label {
  font-size: 12px;
  color: #6b7280;
  text-align: center;
}

.course-student-stats {
  padding: 20px 24px;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

.course-student-stats-card {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.course-student-stats-card .stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  min-width: 80px;
}

.course-student-stats-card .stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.course-student-stats-card .stat-label {
  font-size: 12px;
  color: #6b7280;
  text-align: center;
}

/* 课程学生表格容器 */
.course-student-table-container {
  padding: 24px;
}

.course-student-table-container .enhanced-student-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 课程学生分页样式 */
.course-student-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 24px;
  background: #f8fafc;
  border-top: 1px solid #e5e7eb;
}

.course-student-pagination .custom-pagination {
  margin: 0;
}

/* 课程学生表格相关样式 */
.course-student-table-container .student-id-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.course-student-table-container .student-id {
  font-weight: 500;
  color: #374151;
}

.course-student-table-container .copy-btn {
  padding: 2px 4px;
  font-size: 12px;
}

.course-student-table-container .student-name-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.course-student-table-container .student-name {
  font-weight: 500;
  color: #111827;
}

.course-student-table-container .booked_at {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.course-student-table-container .time-text {
  font-size: 14px;
  color: #6b7280;
}

.course-student-table-container .index-badge {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background: #3b82f6;
  color: white;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
}

/* 学生弹窗样式 */
.student-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.student-dialog .el-dialog__header {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  padding: 20px 24px;
  border-bottom: none;
}

.student-dialog .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.student-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.student-dialog .el-dialog__headerbtn .el-dialog__close:hover {
  color: #f0f0f0;
}

.student-stats {
  padding: 20px 24px;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

.stats-card {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.stats-card .stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  min-width: 80px;
}

.stats-card .stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.stats-card .stat-label {
  font-size: 12px;
  color: #6b7280;
  text-align: center;
}

.student-table-container {
  padding: 24px;
}

.enhanced-student-table {
  border-radius: 8px;
  overflow: hidden;
}

.student-id-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.student-id {
  font-weight: 500;
  color: #374151;
}

.copy-btn {
  padding: 2px 4px;
  font-size: 12px;
}

.student-name-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.student-name {
  font-weight: 500;
  color: #111827;
}

.student-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: #f8fafc;
  border-top: 1px solid #e5e7eb;
}

.export-btn {
  background: #10b981;
  border-color: #10b981;
}

.export-btn:hover {
  background: #059669;
  border-color: #059669;
}

.custom-pagination {
  margin: 0;
}

/* 学生表格行样式 */
.success-row {
  background-color: #f0f9ff;
}

.danger-row {
  background-color: #fef2f2;
}

.warning-row {
  background-color: #fffbeb;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .college-filter-panel {
    width: 200px;
  }
  
  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .college-stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .app-container {
    margin-left: 0;
    padding: 10px;
  }
  
  .main-layout {
    flex-direction: column;
  }
  
  .college-filter-panel {
    width: 100%;
    height: auto;
    order: 2;
  }
  
  .main-content-area {
    order: 1;
  }
  
  .menu-buttons {
    flex-direction: column;
    gap: 0;
  }
  
  .menu-button {
    width: 100%;
    text-align: left;
    padding: 12px 16px;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .college-stats-grid {
    grid-template-columns: 1fr;
  }
  
  .activity-meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>