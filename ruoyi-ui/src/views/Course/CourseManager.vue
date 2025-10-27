<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="card-header">
        <i class="el-icon-search"></i>
        <span>搜索条件</span>
      </div>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <div class="search-row">
          <el-form-item label="课程名称" prop="courseName">
            <el-input
              v-model="queryParams.courseName"
              placeholder="请输入课程名称"
              clearable
              prefix-icon="el-icon-search"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>

          <el-form-item label="课程种类" prop="courseType">
            <el-select
              v-model="queryParams.courseType"
              clearable
              placeholder="请选择课程种类"
              class="search-input"
              @change="handleQuery"
              filterable>
              <el-option
                v-for="type in courseTypeOptions"
                :key="type.value"
                :label="type.label"
                :value="type.value">
                {{ type.label }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="课程状态" prop="status">
            <el-select
              v-model="queryParams.status"
              clearable
              placeholder="请选择课程状态"
              class="search-input"
              @change="handleQuery">
              <el-option
                v-for="status in courseStatusOptions"
                :key="status.value"
                :label="status.label"
                :value="status.value"
              />
            </el-select>
          </el-form-item>
          <!-- <el-form-item label="课程类型" prop="courseCategory">
            <el-select v-model="queryParams.courseCategory" clearable placeholder="请选择课程类型" class="search-input" @change="handleQuery">
              <el-option
                v-for="category in courseCategoryOptions"
                :key="category.value"
                :label="category.label"
                :value="category.value"
              />
            </el-select>
          </el-form-item> -->
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
    <!-- 按钮区域 -->
    <div class="button-bar">
      <el-button-group>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
        <!-- 注释掉导出按钮：因需求暂时隐藏导出功能，后续可恢复 -->
        <!-- <el-button
                  type="warning"
                  plain
                  icon="el-icon-download"
                  size="mini"
                  @click="handleExport"
                >导出</el-button>-->
      </el-button-group>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <div class="card-header">
        <i class="el-icon-s-grid"></i>
        <span>课程列表</span>
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>

      <!-- 表格美化 -->
      <el-table v-loading="loading" :data="coursesList" @selection-change="handleSelectionChange" class="modern-table" :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}" :row-class-name="getRowClassName">
        <el-table-column type="selection" width="45" align="center"/>
        <el-table-column label="序号" width="80" align="center">
          <template v-slot="scope">
          <span class="index-badge">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </span>
          </template>
        </el-table-column>
        <el-table-column label="课程名称" align="center" prop="courseName" width="180">
          <template slot-scope="scope">
            <div class="course-name" :title="scope.row.courseName">
              {{ truncateText(scope.row.courseName, 7) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="课程种类" align="center" prop="courseType" width="200">
          <template slot-scope="scope">
            <el-tag :type="getCourseTypeTagType(scope.row.courseType)" effect="plain" class="course-type-tag">
              {{ getCourseTypeName(scope.row.courseType) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="学分" align="center" prop="courseCredit" width="80">
          <template slot-scope="scope">
            <span class="credit-text">{{ (scope.row.courseCredit || 0).toFixed(1) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="课程类型" align="center" prop="courseCategory" width="80">
          <template slot-scope="scope">
            <el-tag :type="getCourseCategoryTagType(scope.row.courseCategory)" effect="plain" class="category-tag">
              {{ getCourseCategoryName(scope.row.courseCategory) || '未设置' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="课程地点" align="center" prop="courseLocation" width="100">
          <template slot-scope="scope">
            <span class="location-text">{{ scope.row.courseLocation || '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="时间安排" align="center" min-width="300">
          <template slot-scope="scope">
            <div class="time-schedule-inline">
              <!-- 选课时间 -->
              <div class="time-inline-item signup-time">
                <i class="el-icon-user"></i>
                <span class="time-inline-label">选课时间</span>
                <span class="time-inline-content">
                  {{ parseTime(scope.row.courseStart, '{y}-{m}-{d} {h}:{i}') }} 至 {{ parseTime(scope.row.courseDeadline, '{y}-{m}-{d} {h}:{i}') }}
                </span>
              </div>

              <!-- 课程时间 -->
              <div class="time-inline-item activity-time">
                <i class="el-icon-date"></i>
                <span class="time-inline-label">课程时间</span>
                <span class="time-inline-content">
                  {{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}') }} 至 {{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}') }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <!-- 课程状态列 -->
        <el-table-column label="课程状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" effect="dark" class="status-tag">
              {{ scope.row.status }}
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
                  {{ scope.row.courseCapacity || 0 }}/{{ scope.row.courseTotalCapacity }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button
                size="mini"
                type="text"
                @click="handleViewStudents(scope.row)"
                class="action-button view-button">
                已选学生
              </el-button>
              <el-button
                size="mini"
                type="text"
                @click="handleUpdate(scope.row)"
                class="action-button edit-button">
                修改信息
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
        class="custom-pagination"
      />
    </div>

    <!-- 添加或修改课程对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="700px"
      append-to-body
      class="course-form-dialog"
      :before-close="handleDialogClose">

      <!-- 对话框头部 -->
      <div slot="title" class="dialog-header">
        <div class="header-content">
          <div class="header-icon">
            <i :class="form.courseId ? 'el-icon-edit' : 'el-icon-plus'"></i>
          </div>
          <div class="header-text">
            <h3>{{ title }}</h3>
            <p>{{ form.courseId ? '修改课程信息' : '创建新的课程' }}</p>
          </div>
        </div>
      </div>

      <!-- 表单内容 -->
      <div class="dialog-body">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="course-form">

          <!-- 基本信息区域 -->
          <div class="form-section">
            <div class="section-header">
              <i class="el-icon-info"></i>
              <span>基本信息</span>
            </div>
            <div class="section-content">
              <el-form-item prop="courseName">
                <template slot="label">
                    <span class="required-label">
                      课程名称
                    </span>
                </template>
                <el-input
                  v-model="form.courseName"
                  placeholder="请输入课程名称"
                  prefix-icon="el-icon-reading"
                  class="form-input">
                </el-input>
                <div class="form-tip">
                  <i class="el-icon-info"></i>
                  课程名称在当前组织单位下必须唯一
                </div>
              </el-form-item>

              <el-form-item prop="courseType">
                <template slot="label">
                    <span class="required-label">
                      <span class="required-asterisk">*</span>课程种类
                    </span>
                </template>
                <el-select
                  v-model="form.courseType"
                  placeholder="请选择课程种类"
                  class="form-select"
                  clearable
                  filterable>
                  <el-option
                    v-for="type in courseTypeOptions"
                    :key="type.value"
                    :label="type.label"
                    :value="type.value">
                    {{ type.label }}
                  </el-option>
                </el-select>
              </el-form-item>

              <el-form-item prop="courseCategory">
                <template slot="label">
                    <span class="required-label">
                      <span class="required-asterisk">*</span>课程类型
                    </span>
                </template>
                <el-select v-model="form.courseCategory" placeholder="请选择课程类型" class="form-select">
                  <el-option
                    v-for="category in courseCategoryOptions"
                    :key="category.value"
                    :label="category.label"
                    :value="category.value">
                  </el-option>
                </el-select>
              </el-form-item>

              <el-form-item prop="courseLocation">
                <template slot="label">
                    <span class="required-label">
                      课程地点
                    </span>
                </template>
                <el-input
                  v-model="form.courseLocation"
                  placeholder="请输入课程地点"
                  prefix-icon="el-icon-location"
                  class="form-input">
                </el-input>
              </el-form-item>

              <div class="form-row">
                <el-form-item prop="courseCredit" class="form-item-half">
                  <template slot="label">
                      <span class="required-label">
                        <span class="required-asterisk">*</span>课程学分
                      </span>
                  </template>
                  <div class="input-with-unit">
                    <el-input-number
                      v-model="form.courseCredit"
                      :min="0"
                      :max="10"
                      :precision="1"
                      :step="0.5"
                      controls-position="right"
                      class="form-number">
                    </el-input-number>
                  </div>
                </el-form-item>

                <el-form-item prop="courseTotalCapacity" class="form-item-half">
                  <template slot="label">
                      <span class="required-label">
                        课程容量
                      </span>
                  </template>
                  <div class="input-with-unit">
                    <el-input-number
                      v-model="form.courseTotalCapacity"
                      :min="1"
                      :max="1000"
                      controls-position="right"
                      class="form-number"
                      @change="handleCapacityChange">
                    </el-input-number>
                  </div>
                </el-form-item>
              </div>
            </div>
          </div>

          <!-- 时间安排区域 -->
          <div class="form-section">
            <div class="section-header">
              <i class="el-icon-time"></i>
              <span>时间安排</span>
            </div>
            <div class="section-content">
              <el-form-item prop="courseStart">
                <template slot="label">
                  <span class="required-label">选课开始</span>
                </template>
                <el-date-picker
                  v-model="form.courseStart"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="选择选课开始时间"
                  prefix-icon="el-icon-time"
                  class="form-datetime">
                </el-date-picker>
              </el-form-item>

              <el-form-item prop="courseDeadline">
                <template slot="label">
                  <span class="required-label">选课截止</span>
                </template>
                <el-date-picker
                  v-model="form.courseDeadline"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="选择选课截止时间"
                  prefix-icon="el-icon-time"
                  class="form-datetime">
                </el-date-picker>
              </el-form-item>

              <el-form-item prop="startTime">
                <template slot="label">
                  <span class="required-label">课程开始</span>
                </template>
                <el-date-picker
                  v-model="form.startTime"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="选择课程开始时间"
                  prefix-icon="el-icon-time"
                  class="form-datetime">
                </el-date-picker>
              </el-form-item>

              <el-form-item prop="endTime">
                <template slot="label">
                  <span class="required-label">课程结束</span>
                </template>
                <el-date-picker
                  v-model="form.endTime"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="选择课程结束时间"
                  prefix-icon="el-icon-time"
                  class="form-datetime">
                </el-date-picker>
              </el-form-item>
            </div>
          </div>

          <!-- 详细信息区域 -->
          <div class="form-section">
            <div class="section-header">
              <i class="el-icon-document"></i>
              <span>详细信息</span>
            </div>
            <div class="section-content">
              <el-form-item label="课程描述" prop="courseDescription">
                <el-input
                  type="textarea"
                  v-model="form.courseDescription"
                  :rows="4"
                  placeholder="请输入课程描述"
                  class="form-textarea"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item prop="organizer">
                <template slot="label">
                    <span class="required-label">
                      组织单位
                    </span>
                </template>
                <el-input
                  v-model="form.organizer"
                  placeholder="组织单位"
                  prefix-icon="el-icon-user"
                  class="form-input"
                  readonly
                  disabled>
                </el-input>
              </el-form-item>
            </div>
          </div>
        </el-form>
      </div>

      <!-- 对话框底部 -->
      <div slot="footer" class="dialog-footer">
        <div class="footer-left">
          <div class="form-status" v-if="isSubmitting">
            <i class="el-icon-loading"></i>
            <span>正在{{ form.courseId ? '保存修改' : '创建课程' }}...</span>
          </div>
        </div>
        <div class="footer-right">
          <el-button
            @click="cancel"
            :disabled="isSubmitting"
            class="cancel-btn">
            取消
          </el-button>
          <el-button
            type="primary"
            @click="submitForm"
            :loading="isSubmitting"
            :disabled="!timeValidationPassed"
            class="submit-btn">
            {{ form.courseId ? '保存修改' : '创建课程' }}
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 学生列表对话框 -->
    <el-dialog
      title="预约课程学生列表"
      :visible.sync="studentDialogVisible"
      width="1200px"
      append-to-body
      class="student-dialog">

      <!-- 统计信息卡片 -->
      <div class="student-stats">
        <div class="stats-card">
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.total }}</div>
            <div class="stat-label">总报名人数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.notSubmitted }}</div>
            <div class="stat-label">未提交</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ studentStats.pending }}</div>
            <div class="stat-label">未考核</div>
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

      <!-- 学生筛选区域 -->
      <div class="student-filter-container">
        <el-form :model="studentFilterParams" ref="studentFilterForm" size="small" :inline="true">
          <el-form-item label="学号" prop="studentId">
            <el-input
              v-model="studentFilterParams.studentId"
              placeholder="请输入学号"
              clearable
              prefix-icon="el-icon-search"
              class="filter-input"
              @keyup.enter.native="handleStudentFilter"
            />
          </el-form-item>
          <el-form-item label="姓名" prop="studentName">
            <el-input
              v-model="studentFilterParams.studentName"
              placeholder="请输入姓名"
              clearable
              prefix-icon="el-icon-search"
              class="filter-input"
              @keyup.enter.native="handleStudentFilter"
            />
          </el-form-item>

          <el-form-item label="考核状态" prop="status">
            <el-select v-model="studentFilterParams.status" clearable placeholder="请选择考核状态" class="filter-input">
              <el-option label="未提交" value="notSubmitted"></el-option>
              <el-option label="未考核" value="pending"></el-option>
              <el-option label="已通过" value="approved"></el-option>
              <el-option label="未通过" value="rejected"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button-group>
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="handleStudentFilter"
                class="filter-button"
              >筛选</el-button>
              <el-button
                icon="el-icon-refresh"
                @click="resetStudentFilter"
                class="reset-button"
              >重置</el-button>
            </el-button-group>
          </el-form-item>
        </el-form>
      </div>

      <!-- 学生列表表格 -->
      <div class="student-table-container">
        <el-table
          :data="filteredStudentList"
          class="enhanced-student-table full-width-table"
          :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}"
          style="width: 100%">
          <el-table-column label="序号" width="80" align="center">
            <template v-slot="scope">
              <span class="index-badge">
                {{ scope.$index + 1 }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="学号" prop="studentId" min-width="140" align="center">
            <template slot-scope="scope">
              <div class="student-id-container">
                <span class="student-id">{{ scope.row.studentId }}</span>
                <i class="el-icon-copy-document copy-btn" @click="copyStudentId(scope.row.studentId)"></i>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="姓名" prop="studentName" min-width="120" align="center">
            <template slot-scope="scope">
              <div class="student-name-container">
                <span class="student-name">{{ scope.row.studentName || '未知' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属书院" prop="college" min-width="140" align="center">
            <template slot-scope="scope">
              <div class="major-info">
                <span class="major-name">{{ scope.row.college || '未知' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="课程成绩" prop="score" min-width="100" align="center">
            <template slot-scope="scope">
              <div class="score-container">
                <span class="score-text" :class="getScoreClass(scope.row.score)">
                  {{ scope.row.score !== null && scope.row.score !== undefined ? scope.row.score : '-' }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="考核状态" prop="status" min-width="120" align="center">
            <template slot-scope="scope">
              <el-tag
                :type="getStudentStatusTagType(scope.row.status)"
                effect="dark"
                class="status-tag">
                {{ getStudentStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 对话框底部 -->
      <div slot="footer" class="dialog-footer">
        <div class="footer-left">
          <div class="total-info">共{{ filteredStudentList.length }}名学生</div>
        </div>
        <div class="footer-right">
          <el-button
            type="primary"
            icon="el-icon-download"
            @click="exportStudentList"
            class="export-btn">
            导出名单
          </el-button>
          <el-button @click="studentDialogVisible = false" class="close-btn">
            关闭
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCourses, getCourses, delCourses, addCourses, updateCourses, getCourseBookings, exportCourseStudents, checkCourseUnique } from "@/api/system/courses";
import { getNickName } from "@/api/system/student";
import { getServerTime } from "@/api/common/time";
import RightToolbar from '@/components/RightToolbar';
import Pagination from '@/components/Pagination';

export default {
  name: "Courses",
  components: {
    RightToolbar,
    Pagination
  },
  data() {
    return {
      initialBookedCount: 0, // 🔥 新增：保存初始已选人数
      // 服务器时间
      serverTime: null,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 当前用户所属书院
      currentUserAcademy: '',
      // 书院选课表格数据
      coursesList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseId: null,
        courseName: null,
        courseLocation: null,
        courseType: null,
        courseCategory: null,
        organizer: null,
        status: null
      },
      // 课程状态选项
      courseStatusOptions: [
        { value: '选课未开始', label: '选课未开始' },
        { value: '选课进行中', label: '选课进行中' },
        { value: '选课已截止', label: '选课已截止' }
      ],
      // 课程类型选项
      courseTypeOptions: [
        { value: 1, label: '人格塑造与价值引领课程类' },
        { value: 2, label: '知识融合与思维进阶课程类' },
        { value: 3, label: '能力锻造与实践创新课程类' },
        { value: 4, label: '社会责任与领军意识课程类' }
      ],
      // 课程种类选项
      courseCategoryOptions: [
        { value: '必修', label: '必修' },
        { value: '选修', label: '选修' }
      ],
      // 表单提交状态
      isSubmitting: false,
      // 时间验证状态
      timeValidationPassed: true,
      // 表单参数
      form: {},
      // 学生列表对话框
      studentDialogVisible: false,
      studentList: [],
      filteredStudentList: [],
      studentFilterParams: {
        studentId: '',
        studentName: '',
        college: '',
        status: ''
      },
      studentStats: {
        total: 0,
        notSubmitted: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      },
      // 表单校验
      rules: {
        courseName: [
          { required: true, message: "课程名称不能为空", trigger: "blur" }
        ],
        courseLocation: [
          { required: true, message: "课程地点不能为空", trigger: "blur" }
        ],
        courseStart: [
          { required: true, message: "选课开始时间不能为空", trigger: "blur" }
        ],
        courseDeadline: [
          { required: true, message: "选课截止时间不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "课程开始时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "课程结束时间不能为空", trigger: "blur" }
        ],
        courseTotalCapacity: [
          { required: true, message: "课程容量不能为空", trigger: "blur" }
        ],
        organizer: [
          { required: true, message: "组织单位不能为空", trigger: "blur" }
        ],
        courseDescription: [
          {
            validator: (rule, value, callback) => {
              if (value && value.length > 200) {
                callback(new Error("课程描述不能超过200字"));
              } else {
                callback();
              }
            },
            trigger: "blur"
          }
        ],
      }
    };
  },
  computed: {
    // 检查选中的课程中是否包含已开始选课的课程
    hasStartedCourses() {
      if (!this.ids || this.ids.length === 0) {
        return false;
      }
      return this.coursesList.some(course =>
        this.ids.includes(course.courseId) && this.isCourseSignUpStarted(course)
      );
    }
  },
  async created() {
    // 获取服务器时间
    await this.getServerTime();
    // 获取当前用户所属书院（必须等待完成，确保organizer被设置）
    await this.getCurrentUserAcademy();
    // 测试状态计算逻辑
    this.testStatusComputation();
    // 在获取书院信息后再查询课程列表，确保只显示当前书院的课程
    this.getList().then(() => {
      // 数据加载完成后再检查路由参数
      this.checkRouteParams();
    });
  },
  watch: {
    // 🔥 新增：监听总容量变化
    'form.courseTotalCapacity'(newVal, oldVal) {
      // 当修改总容量时，检查是否小于已选人数
      if (this.form.courseId && newVal && this.initialBookedCount >= 0) {
        // courseCapacity 是已选人数，保持不变
        // 只需要确保总容量不小于已选人数
        if (newVal < this.initialBookedCount) {
          this.$nextTick(() => {
            this.form.courseTotalCapacity = oldVal;
          });
          this.$message.warning(`新总容量(${newVal})不能小于已选人数(${this.initialBookedCount})人`);
        }
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

    // 获取当前用户所属书院
    async getCurrentUserAcademy() {
      try {
        const response = await getNickName();
        if (response && response.msg) {
          this.currentUserAcademy = response.msg;
          // 设置查询参数中的organizer字段，只显示当前书院的课程
          this.queryParams.organizer = this.currentUserAcademy;
          console.log('当前用户书院:', this.currentUserAcademy);
          console.log('已设置课程过滤条件 - organizer:', this.queryParams.organizer);
        } else {
          console.error('获取用户书院信息失败:', response);
          this.currentUserAcademy = '';
          this.queryParams.organizer = '';
        }
      } catch (error) {
        console.error('获取用户书院信息异常:', error);
        this.currentUserAcademy = '';
        this.queryParams.organizer = '';
      }
    },
    // 测试状态计算逻辑
    testStatusComputation() {
      const testData = {
        courseName: '测试课程',
        courseStart: '2025-09-27 00:00:00',
        courseDeadline: '2025-09-28 11:15:00'
      };
      const status = this.computeCourseStatus(testData);
      return status;
    },
    // 检查课程是否已开始选课
    isCourseSignUpStarted(course) {
      if (!course || !course.courseStart) {
        return false;
      }
      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const now = this.serverTime || new Date();
      const courseStart = new Date(course.courseStart);
      return now >= courseStart;
    },
    // 获取当前用户昵称
    getCurrentUserNickName() {
      return this.$store.state.user.nickName || this.$store.state.user.name || '当前用户';
    },
    // 序号计算（跨页连续）
    indexMethod(index) {
      return (this.queryParams.pageNum - 1) * this.queryParams.pageSize + index + 1;
    },
    // 验证课程名称唯一性（同一组织单位的同一课程只能有一个）
    async validateCourseUniqueness(rule, value, callback) {
      if (!value || !this.form.organizer) {
        callback();
        return;
      }

      try {
        // 调用后端API检查唯一性
        const response = await checkCourseUnique({
          courseName: value,
          organizer: this.form.organizer,
          courseId: this.form.courseId || null
        });

        if (response.code === 200 && response.data === false) {
          // 不唯一，显示错误
          this.$message({
            message: `⚠️ 课程重复！该组织单位已存在名为"${value}"的课程，请使用不同的课程名称`,
            type: 'error',
            duration: 5000,
            showClose: true,
            customClass: 'course-duplicate-error'
          });
          callback(new Error(`该组织单位已存在名为"${value}"的课程，请使用不同的课程名称`));
        } else {
          callback();
        }
      } catch (error) {
        // API调用失败时，回退到前端检查
        const existingCourse = this.coursesList.find(course =>
          course.courseName === value &&
          course.organizer === this.form.organizer &&
          course.courseId !== this.form.courseId
        );

        if (existingCourse) {
          callback(new Error(`该组织单位已存在名为"${value}"的课程，请使用不同的课程名称`));
        } else {
          callback();
        }
      }
    },
    // 时间顺序验证
    validateTimeOrder() {
      const { courseStart, courseDeadline, startTime, endTime } = this.form;

      // 检查是否有空值
      if (!courseStart || !courseDeadline || !startTime || !endTime) {
        this.timeValidationPassed = false;
        return false;
      }

      const courseStartTime = new Date(courseStart);
      const courseDeadlineTime = new Date(courseDeadline);
      const startTimeDate = new Date(startTime);
      const endTimeDate = new Date(endTime);

      // 验证时间顺序：选课开始 < 选课结束 < 课程开始 < 课程结束
      if (courseStartTime >= courseDeadlineTime) {
        this.timeValidationPassed = false;
        this.$message({
          message: '选课开始时间必须早于选课截止时间',
          type: 'error',
          duration: 3000,
          customClass: 'time-validation-error'
        });
        return false;
      }

      if (courseDeadlineTime >= startTimeDate) {
        this.timeValidationPassed = false;
        this.$message({
          message: '选课截止时间必须早于课程开始时间',
          type: 'error',
          duration: 3000,
          customClass: 'time-validation-error'
        });
        return false;
      }

      if (startTimeDate >= endTimeDate) {
        this.timeValidationPassed = false;
        this.$message({
          message: '课程开始时间必须早于课程结束时间',
          type: 'error',
          duration: 3000,
          customClass: 'time-validation-error'
        });
        return false;
      }

      // 验证通过
      this.timeValidationPassed = true;
      return true;
    },
    // 必填项验证
    validateRequiredFields() {
      const { courseName, courseType, courseCategory, courseLocation, courseCredit, courseTotalCapacity, courseStart, courseDeadline, startTime, endTime, organizer } = this.form;

      let hasError = false;

      // 清除之前的错误样式
      this.clearFieldErrors();

      // 验证课程名称
      if (!courseName || courseName.trim() === '') {
        this.setFieldError('courseName');
        hasError = true;
      }

      // 验证课程类型
      if (!courseType) {
        this.setFieldError('courseType');
        hasError = true;
      }

      // 验证课程种类
      if (!courseCategory) {
        this.setFieldError('courseCategory');
        hasError = true;
      }

      // 验证课程地点
      if (!courseLocation || courseLocation.trim() === '') {
        this.setFieldError('courseLocation');
        hasError = true;
      }

      // 验证课程学分
      if (!courseCredit || courseCredit <= 0) {
        this.setFieldError('courseCredit');
        hasError = true;
      }

      // 验证课程容量
      if (!courseTotalCapacity || courseTotalCapacity <= 0) {
        this.setFieldError('courseTotalCapacity');
        hasError = true;
      }

      // 验证选课开始时间
      if (!courseStart) {
        this.setFieldError('courseStart');
        hasError = true;
      }

      // 验证选课截止时间
      if (!courseDeadline) {
        this.setFieldError('courseDeadline');
        hasError = true;
      }

      // 验证课程开始时间
      if (!startTime) {
        this.setFieldError('startTime');
        hasError = true;
      }

      // 验证课程结束时间
      if (!endTime) {
        this.setFieldError('endTime');
        hasError = true;
      }

      // 验证组织单位
      if (!organizer || organizer.trim() === '') {
        this.setFieldError('organizer');
        hasError = true;
      }

      return !hasError;
    },
    // 设置字段错误样式
    setFieldError(fieldName) {
      this.$nextTick(() => {
        const field = this.$refs.form.$el.querySelector(`[prop="${fieldName}"]`);
        if (field) {
          field.classList.add('field-error');
        }
      });
    },
    // 清除字段错误样式
    clearFieldErrors() {
      this.$nextTick(() => {
        const errorFields = this.$refs.form.$el.querySelectorAll('.field-error');
        errorFields.forEach(field => {
          field.classList.remove('field-error');
        });
      });
    },
    // 查看学生列表
    handleViewStudents(row) {
      this.currentCourse = row;
      this.studentDialogVisible = true;
      this.loadStudentList(row.courseId);
    },
    // 加载学生列表数据
    loadStudentList(courseId) {
      this.getCourseBookings(courseId).then(response => {
        this.studentList = response.rows || [];
        this.filteredStudentList = [...this.studentList];
        this.calculateStudentStats();
      }).catch(error => {
        console.error('获取学生列表失败:', error);
        this.$message.error('获取学生列表失败');
        this.studentList = [];
        this.filteredStudentList = [];
        this.calculateStudentStats();
      });
    },
    // 获取课程预约数据
    getCourseBookings(courseId) {
      return getCourseBookings(courseId);
    },
    // 计算学生统计信息
    calculateStudentStats() {
      this.studentStats.total = this.studentList.length;
      this.studentStats.notSubmitted = this.studentList.filter(s => s.status === 'notSubmitted' || s.status === '未提交').length;
      this.studentStats.pending = this.studentList.filter(s => s.status === 'pending' || s.status === '未考核' || s.status === '未审核').length;
      this.studentStats.approved = this.studentList.filter(s => s.status === 'approved' || s.status === '已通过').length;
      this.studentStats.rejected = this.studentList.filter(s => s.status === 'rejected' || s.status === '未通过').length;
    },
    // 学生筛选
    handleStudentFilter() {
      this.filteredStudentList = this.studentList.filter(student => {
        const matchStudentId = !this.studentFilterParams.studentId ||
          student.studentId.toLowerCase().includes(this.studentFilterParams.studentId.toLowerCase());
        const matchStudentName = !this.studentFilterParams.studentName ||
          (student.studentName && student.studentName.toLowerCase().includes(this.studentFilterParams.studentName.toLowerCase()));
        const matchCollege = !this.studentFilterParams.college ||
          (student.college && student.college.toLowerCase().includes(this.studentFilterParams.college.toLowerCase()));
        const matchStatus = !this.studentFilterParams.status ||
          this.getStudentStatusText(student.status) === this.getStudentStatusText(this.studentFilterParams.status);

        return matchStudentId && matchStudentName && matchCollege && matchStatus;
      });
    },
    // 重置学生筛选
    resetStudentFilter() {
      this.studentFilterParams = {
        studentId: '',
        studentName: '',
        college: '',
        status: ''
      };
      this.filteredStudentList = [...this.studentList];
    },
    // 获取学生状态标签类型
    getStudentStatusTagType(status) {
      const statusMap = {
        'notSubmitted': 'info',
        'pending': 'warning',
        'approved': 'success',
        'rejected': 'danger',
        '未提交': 'info',
        '未审核': 'warning',
        '未考核': 'warning',
        '已通过': 'success',
        '未通过': 'danger'
      };
      return statusMap[status] || 'info';
    },
    // 获取学生状态文本
    getStudentStatusText(status) {
      const statusMap = {
        'notSubmitted': '未提交',
        'pending': '未考核',
        'approved': '已通过',
        'rejected': '未通过',
        '未提交': '未提交',
        '未审核': '未考核',
        '未考核': '未考核',
        '已通过': '已通过',
        '未通过': '未通过'
      };
      return statusMap[status] || '未知';
    },
    // 获取成绩样式类
    getScoreClass(score) {
      if (score === null || score === undefined) return '';
      if (score >= 90) return 'score-excellent';
      if (score >= 80) return 'score-good';
      if (score >= 70) return 'score-medium';
      if (score >= 60) return 'score-pass';
      return 'score-fail';
    },
    // 复制学号
    copyStudentId(studentId) {
      navigator.clipboard.writeText(studentId).then(() => {
        this.$message.success('学号已复制到剪贴板');
      }).catch(() => {
        this.$message.error('复制失败');
      });
    },
    // 导出学生名单
    exportStudentList() {
      if (!this.currentCourse) {
        this.$message.error('请先选择课程');
        return;
      }

      if (this.filteredStudentList.length === 0) {
        this.$message.warning('没有可导出的学生数据');
        return;
      }

      // 创建Excel数据
      const excelData = this.filteredStudentList.map((student, index) => ({
        '序号': index + 1,
        '学号': student.studentId,
        '姓名': student.studentName || '未知',
        '所属书院': student.college || '未知',
        '预约时间': this.parseTime(student.bookAt, '{y}-{m}-{d} {h}:{i}'),
        '考核状态': this.getStudentStatusText(student.status),
        '考核人': student.reviewer || '',
        '考核意见': student.reviewerComment || '',
        '考核时间': student.reviewTime ? this.parseTime(student.reviewTime, '{y}-{m}-{d} {h}:{i}') : '',
        '学习总结': student.summary || ''
      }));

      // 使用XLSX库导出
      this.exportToExcel(excelData, `课程学生名单_${this.currentCourse.courseName}_${new Date().getTime()}.xlsx`);
    },
    // 导出到Excel
    exportToExcel(data, filename) {
      // 动态导入XLSX库
      import('xlsx').then(XLSX => {
        const ws = XLSX.utils.json_to_sheet(data);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, '学生名单');

        // 设置列宽
        const colWidths = [
          { wch: 8 },   // 序号
          { wch: 15 },  // 学号
          { wch: 12 },  // 姓名
          { wch: 15 },  // 所属书院
          { wch: 20 },  // 预约时间
          { wch: 12 },  // 考核状态
          { wch: 12 },  // 考核人
          { wch: 20 },  // 考核意见
          { wch: 20 },  // 考核时间
          { wch: 30 }   // 学习总结
        ];
        ws['!cols'] = colWidths;

        XLSX.writeFile(wb, filename);
        this.$message.success('导出成功');
      }).catch(error => {
        console.error('导出失败:', error);
        this.$message.error('导出失败，请检查是否安装了xlsx库');
      });
    },
    /** 查询书院选课列表 */
    getList() {
      this.loading = true;
      // 确保organizer过滤条件始终存在（只显示当前书院的课程）
      if (this.currentUserAcademy && !this.queryParams.organizer) {
        this.queryParams.organizer = this.currentUserAcademy;
      }
      console.log('查询课程列表，过滤条件:', this.queryParams);
      return listCourses(this.queryParams).then(response => {
        const rows = response.rows || [];
        // 若后端未按状态过滤，则在前端按需过滤
        const selectedStatus = (this.queryParams.status || '').trim();
        const selectedType = this.queryParams.courseType; // courseType 是数字类型，不需要 trim()
        const selectedCategory = (this.queryParams.courseCategory || '').trim();

        const withStatus = rows.map(item => {
          const computedStatus = this.computeCourseStatus(item);
          return {
            ...item,
            status: computedStatus // 强制使用计算的状态
          };
        });

        // 按状态过滤
        let filteredList = selectedStatus ? withStatus.filter(r => r.status === selectedStatus) : withStatus;

        // 按课程类型过滤（courseType 是数字类型）
        if (selectedType != null && selectedType !== '') {
          filteredList = filteredList.filter(r => r.courseType === selectedType);
        }

        // 按课程种类过滤
        if (selectedCategory) {
          filteredList = filteredList.filter(r => r.courseCategory === selectedCategory);
        }

        // 按课程ID过滤
        if (this.queryParams.courseId) {
          filteredList = filteredList.filter(r => r.courseId == this.queryParams.courseId);
        }

        // 按照课程开始时间排序（从早到晚）
        this.coursesList = filteredList.sort((a, b) => {
          const timeA = a.startTime ? new Date(a.startTime).getTime() : 0;
          const timeB = b.startTime ? new Date(b.startTime).getTime() : 0;
          return timeA - timeB;
        });

        this.total = response.total;
        this.loading = false;

        // 强制更新视图
        this.$forceUpdate();
      }).catch(error => {
        this.loading = false;
        this.$message.error("获取课程列表失败");
        throw error;
      });
    },
    // 兜底：根据时间推导课程状态
    computeCourseStatus(item) {
      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const now = this.serverTime || new Date();
      const start = item.courseStart ? new Date(item.courseStart) : null;
      const deadline = item.courseDeadline ? new Date(item.courseDeadline) : null;

      // 如果当前时间在选课开始时间之前，显示"选课未开始"
      if (start && now < start) {
        return '选课未开始';
      }

      // 如果当前时间在选课开始时间和截止时间之间，显示"选课进行中"
      if (start && deadline && now >= start && now <= deadline) {
        return '选课进行中';
      }

      // 如果当前时间超过选课截止时间，显示"选课已截止"
      if (deadline && now > deadline) {
        return '选课已截止';
      }

      // 默认状态
      return item.status || '选课未开始';
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        courseId: null,
        courseName: null,
        courseType: null,
        courseLocation: null,
        courseStart: null,
        courseDeadline: null,
        startTime: null,
        endTime: null,
        courseTotalCapacity: null,
        courseCapacity: null,
        courseDescription: null,
        pictureUrl: null,
        status: null,
        createdAt: null,
        organizer: this.getCurrentUserNickName(),
        notes: null,
        version: null
      };
      // 重置时间验证状态
      this.timeValidationPassed = true;
      this.resetForm("form");
    },
    // 状态与标签类型的映射：返回el-tag支持的type值
    getStatusTagType(status) {
      switch (status) {
        case '选课未开始':
          return 'info'; // 蓝色标签
        case '选课进行中':
          return 'success'; // 绿色标签
        case '选课已截止':
          return 'danger'; // 红色标签
        default:
          return 'info'; // 蓝色标签（默认）
      }
    },
    /**
     * 1. 计算报名占比（百分比）
     * @param {Object} row - 当前课程行数据
     * @returns {Number} 占比（0-100，避免负数或超过100）
     */
    calculateCapacityPercentage(row) {
      // 处理异常值（如总容量为0或null，避免除以0）
      if (!row.courseTotalCapacity || row.courseTotalCapacity <= 0) {
        return 0;
      }
      // courseCapacity 就是已选人数
      const enrolled = row.courseCapacity || 0;
      // 计算占比并取整（确保在0-100之间）
      const percentage = Math.round((enrolled / row.courseTotalCapacity) * 100);
      return Math.max(0, Math.min(100, percentage));
    },

    /**
     * 2. 根据占比获取进度条颜色（分3档：低/中/高）
     * @param {Number} percentage - 报名占比
     * @returns {String} 进度条颜色（Element UI支持的颜色值）
     */
    getProgressColor(percentage) {
      if (percentage < 50) {
        return '#67C23A'; // 绿色：报名率低（<50%）
      } else if (percentage < 90) {
        return '#E6A23C'; // 橙色：报名率中（50%-90%）
      } else {
        return '#F56C6C'; // 红色：报名率高（≥90%）
      }
    },

    /**
     * 3. 根据占比获取人数文本样式（和进度条颜色对应）
     * @param {Object} row - 当前课程行数据
     * @returns {String} 样式类名（对应CSS中的颜色类）
     */
    getCapacityClass(row) {
      const percentage = this.calculateCapacityPercentage(row);
      if (percentage < 50) {
        return 'capacity-low'; // 绿色文本
      } else if (percentage < 90) {
        return 'capacity-medium'; // 橙色文本
      } else {
        return 'capacity-high'; // 红色文本
      }
    },

    // 课程类型映射函数：将数字转换为对应的类型名称
    getCourseTypeName(coursetype) {
      const typeMap = {
        1: '人格塑造与价值引领课程类',
        2: '知识融合与思维进阶课程类',
        3: '能力锻造与实践创新课程类',
        4: '社会责任与领军意识课程类'
      };
      return typeMap[coursetype] || coursetype;
    },
    // 课程种类映射函数：获取种类名称
    getCourseCategoryName(category) {
      const categoryMap = {
        '必修': '必修',
        '选修': '选修'
      };
      return categoryMap[category] || category;
    },

    /** 截断文本显示 */
    truncateText(text, maxLength) {
      if (!text) return '';
      if (text.length <= maxLength) return text;
      return text.substring(0, maxLength) + '...';
    },

    getCourseTypeTagType(coursetype) {
      const map = {
        1: 'primary',   // 人格塑造与价值引领课程类 - 蓝色
        2: 'success',   // 知识融合与思维进阶课程类 - 绿色
        3: 'warning',   // 能力锻造与实践创新课程类 - 橙色
        4: 'danger',    // 社会责任与领军意识课程类 - 红色
        '其他': ''        // 默认蓝色
      }
      return map[coursetype] || 'info';
    },
    // 课程种类标签类型映射
    getCourseCategoryTagType(category) {
      const map = {
        '必修': 'danger',    // 必修 - 红色
        '选修': 'success'    // 选修 - 绿色
      };
      return map[category] || 'info';
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      // 手动重置所有查询参数，确保所有筛选条件被清空
      this.queryParams.courseId = null;
      this.queryParams.courseName = null;
      this.queryParams.courseLocation = null;
      this.queryParams.courseType = null;
      this.queryParams.courseCategory = null;
      this.queryParams.status = null;
      // 注意：不重置organizer，因为需要保持当前用户的组织者身份

      // 重置表单显示
      if (this.$refs.queryForm) {
        this.$refs.queryForm.resetFields();
      }

      // 重置页码到第一页
      this.queryParams.pageNum = 1;

      // 重新获取列表
      this.getList();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.courseId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    /** 检查路由参数，处理从首页跳转过来的筛选请求 */
    async checkRouteParams() {
      const { courseId, filterMode } = this.$route.query;

      if (courseId && filterMode === 'single') {
        // 根据课程ID筛选课程
        this.queryParams.courseId = courseId;
        this.handleQuery();

        // 显示筛选成功消息
        this.$message.success('已筛选出指定课程');

        // 清除路由参数，避免刷新页面时重复触发
        this.$router.replace({
          path: this.$route.path,
          query: {}
        });
      } else if (filterMode === 'current') {
        // 处理本学期课程筛选
        this.queryParams.status = '选课进行中'; // 只显示进行中的课程
        this.handleQuery();

        // 显示筛选成功消息
        this.$message.success('已筛选出本学期课程');

        // 清除路由参数，避免刷新页面时重复触发
        this.$router.replace({
          path: this.$route.path,
          query: {}
        });
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加书院课程";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const courseId = row.courseId || this.ids
      getCourses(courseId).then(response => {
        this.form = response.data;
        // 🔥 保存初始已选人数（courseCapacity 就是已选人数）
        this.initialBookedCount = this.form.courseCapacity || 0;
        // 确保课程类型是字符串格式，以匹配选项值
        if (this.form.courseType) {
          this.form.courseType = String(this.form.courseType);
        }
        // 设置组织单位为当前用户昵称（不可修改）
        this.form.organizer = this.getCurrentUserNickName();
        // 新增课程时，初始已选人数为0
        if (!this.form.courseCapacity || this.form.courseCapacity < 0) {
          this.form.courseCapacity = 0;
        }
        this.open = true;
        this.title = "修改书院课程";
      });
    },
    /** 提交按钮 */
    submitForm() {
      // 首先进行必填项验证
      if (!this.validateRequiredFields()) {
        this.$message({
          message: '有必填项未填',
          type: 'error',
          duration: 3000,
          customClass: 'required-validation-error'
        });
        return;
      }

      // 然后进行时间验证
      if (!this.validateTimeOrder()) {
        this.$message({
          message: '请修正时间设置后再提交',
          type: 'error',
          duration: 3000,
          customClass: 'time-validation-error'
        });
        return;
      }

      // 最后进行表单验证
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.isSubmitting = true;
          if (this.form.courseId != null) {
            updateCourses(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).catch(() => {
              // 错误处理
            }).finally(() => {
              this.isSubmitting = false;
            });
          } else {
            addCourses(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            }).catch(() => {
              // 错误处理
            }).finally(() => {
              this.isSubmitting = false;
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const courseIds = row.courseId || this.ids;
      const courseNames = row.courseName || this.names; // 假设有names字段用于批量删除

      // 检查是否有已开始选课的课程
      let startedCourses = [];
      if (row && this.isCourseSignUpStarted(row)) {
        startedCourses.push(row.courseName);
      } else if (this.ids && this.ids.length > 0) {
        startedCourses = this.coursesList
          .filter(course =>
            this.ids.includes(course.courseId) && this.isCourseSignUpStarted(course)
          )
          .map(course => course.courseName);
      }

      if (startedCourses.length > 0) {
        let message = "以下课程已开始选课，无法删除：\n";
        startedCourses.forEach(name => {
          message += "• " + name + "\n";
        });
        message += "\n请取消选择已开始选课的课程后重试。";
        this.$message.warning(message);
        return;
      }

      let confirmMessage = '是否确认删除课程名称为"' + courseNames + '"的数据项？\n\n';
      confirmMessage += '⚠️ 注意：删除课程将同时删除该课程的所有选课记录！\n';
      confirmMessage += '此操作不可撤销，请谨慎操作。';

      this.$modal.confirm(confirmMessage).then(function() {
        return delCourses(courseIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功，相关选课记录已一并删除");
      }).catch((error) => {
        if (error && error.message && error.message.includes("已开始选课")) {
          this.$message.error(error.message);
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/courses/export', {
        ...this.queryParams
      }, `courses_${new Date().getTime()}.xlsx`)
    },
    /** 对话框关闭处理 */
    handleDialogClose(done) {
      if (this.isSubmitting) {
        this.$message.warning('正在提交中，请稍候...');
        return;
      }
      done();
    },
    /** 处理容量变化 */
    handleCapacityChange(value) {
      // 当修改总容量时，保持已选人数不变
      // courseCapacity 是已选人数，不需要重新计算
      // 只需要确保总容量不小于已选人数
      if (value && value > 0 && this.form.courseCapacity && value < this.form.courseCapacity) {
        this.$message.warning(`课程总容量不能小于已选人数(${this.form.courseCapacity})！`);
        this.form.courseTotalCapacity = this.form.courseCapacity;
      }
    },
    // 获取表格行样式类名
    getRowClassName({row}) {
      if (this.isCourseSignUpStarted(row)) {
        return 'started-course-row';
      }
      return '';
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

/* 统一卡片样式 */
.search-card,
.table-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.search-card:hover,
.table-card:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 卡片头部 */
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

.card-header .academy-tag {
  margin-left: 16px;
  padding: 6px 12px;
  font-weight: 500;
  border-radius: 20px;
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
  border: 1px solid #90caf9;
  color: #1976d2;
}

.card-header .academy-tag i {
  margin-right: 6px;
  font-size: 14px;
  color: #1976d2;
}

.record-count {
  margin-left: auto;
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}

/* 搜索表单 */
.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.search-input {
  min-width: 220px;
  transition: all 0.3s ease;
}

.search-input:hover {
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.search-actions {
  margin-left: auto;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

/* 按钮样式 */
.search-button {
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.refresh-button {
  background: #f0f2f5;
  border: none;
  padding: 10px 20px;
  color: #606266;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.refresh-button:hover {
  background: #e4e7ed;
  color: #333;
  transform: translateY(-2px);
}

/* 按钮区域 */
.button-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 5px;
}

.el-button-group {
  display: flex;
  gap: 8px;
}

/* 现代化表格 */
.modern-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.modern-table th {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9) !important;
  font-weight: 600;
  color: #1e293b;
  border-bottom: 2px solid #e2e8f0;
  padding: 16px 12px;
}

.modern-table td {
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 12px;
}

.modern-table tr:hover td {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9) !important;
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

.course-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.status-tag {
  font-weight: 600;
  padding: 0 10px;
  height: 28px;
  line-height: 28px;
}

.course-type-tag {
  font-weight: 500;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 内联时间安排样式 */
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

  &.signup-time {
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

  &.activity-time {
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

  i {
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
}

/* 报名人数容器：垂直排列，居中对齐 */
.participants {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px; /* 进度条和文本的间距 */
  padding: 2px 0;
}

/* 进度条：控制宽度，适配表格列 */
.progress-bar {
  width: 100%;
  margin: 2px 0;
}

/* 人数文本：统一字体大小和权重 */
.count {
  font-size: 13px;
  font-weight: 500;
}

/* 报名率颜色类（和进度条颜色对应） */
.capacity-low {
  color: #67C23A; /* 绿色 */
}
.capacity-medium {
  color: #E6A23C; /* 橙色 */
}
.capacity-high {
  color: #F56C6C; /* 红色 */
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

.edit-button {
  background: #f4f4f5;
  border-color: #d3d4d6;
  color: #909399;
}

.delete-button {
  background: #fef0f0;
  border-color: #fde2e2;
  color: #f56c6c;
}

.view-button {
  background: #f0f9ff;
  border-color: #d9ecff;
  color: #409eff;
}

/* 分页样式 */
.custom-pagination {
  display: flex;
  justify-content: center !important;
  margin: 20px auto 0;
  padding: 12px 0;
  width: 100%;
}

.custom-pagination /deep/ .el-pagination {
  display: inline-flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.custom-pagination /deep/ .el-pager li:hover {
  border-color: #409EFF;
  color: #409EFF;
}

/* 课程表单对话框美化样式 */
.course-form-dialog {
  .el-dialog {
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    overflow: hidden;
  }

  .el-dialog__header {
    padding: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px 16px 0 0;
  }

  .el-dialog__body {
    padding: 0;
    background: #f8f9fa;
  }

  .el-dialog__footer {
    padding: 0;
    background: #f8f9fa;
    border-radius: 0 0 16px 16px;
  }
}

/* 对话框头部 */
.dialog-header {
  padding: 24px 32px;
  color: white;

  .header-content {
    display: flex;
    align-items: center;
    gap: 16px;

    .header-icon {
      width: 48px;
      height: 48px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      backdrop-filter: blur(10px);

      i {
        font-size: 24px;
        color: white;
      }
    }

    .header-text {
      h3 {
        margin: 0 0 4px 0;
        font-size: 20px;
        font-weight: 600;
        color: white;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

/* 对话框主体 */
.dialog-body {
  padding: 32px;
  max-height: 70vh;
  overflow-y: auto;
}

/* 课程表单 */
.course-form {
  .form-section {
    background: white;
    border-radius: 12px;
    margin-bottom: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border: 1px solid #e4e7ed;
    overflow: hidden;

    .section-header {
      display: flex;
      align-items: center;
      padding: 16px 20px;
      background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
      border-bottom: 1px solid #e4e7ed;

      i {
        font-size: 16px;
        color: #409EFF;
        margin-right: 8px;
      }

      span {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .section-content {
      padding: 24px;
    }
  }
}

/* 表单输入框样式 */
.form-input {
  .el-input__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;

    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }
}

.form-select {
  .el-input__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;

    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }
}

/* 表单提示样式 */
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 6px;
  border-left: 3px solid #409EFF;

  i {
    margin-right: 6px;
    color: #409EFF;
    font-size: 14px;
  }
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;

  .footer-left {
    .form-status {
      display: flex;
      align-items: center;
      color: #409EFF;
      font-size: 14px;

      i {
        margin-right: 8px;
        animation: rotating 2s linear infinite;
      }
    }
  }

  .footer-right {
    display: flex;
    gap: 12px;

    .cancel-btn {
      background: #f5f7fa;
      border: 1px solid #dcdfe6;
      color: #606266;
      border-radius: 8px;
      padding: 10px 20px;
      font-weight: 500;
      transition: all 0.3s ease;

      &:hover {
        background: #ecf5ff;
        border-color: #b3d8ff;
        color: #409EFF;
      }

      i {
        margin-right: 6px;
      }
    }

    .submit-btn {
      background: linear-gradient(135deg, #409EFF 0%, #64b5ff 100%);
      border: none;
      color: white;
      border-radius: 8px;
      padding: 10px 24px;
      font-weight: 600;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
        background: linear-gradient(135deg, #3a8ee6 0%, #5a9fd8 100%);
      }

      &:active {
        transform: translateY(0);
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
      }

      i {
        margin-right: 6px;
      }
    }
  }
}

/* 旋转动画 */
@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .course-form-dialog {
    .el-dialog {
      width: 95% !important;
      margin: 0 auto;
    }
  }

  .dialog-body {
    padding: 20px;
  }

  .dialog-footer {
    flex-direction: column;
    gap: 16px;

    .footer-right {
      width: 100%;
      justify-content: center;
    }
  }
}

/* 报名人数容器：垂直排列，居中对齐 */
.participants {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px; /* 进度条和文本的间距 */
  padding: 2px 0;
}

/* 进度条：控制宽度，适配表格列 */
.progress-bar {
  width: 100%;
  margin: 2px 0;
}

/* 人数文本：统一字体大小和权重 */
.count {
  font-size: 13px;
  font-weight: 500;
}

/* 报名率颜色类（和进度条颜色对应） */
.capacity-low {
  color: #67C23A; /* 绿色 */
}
.capacity-medium {
  color: #E6A23C; /* 橙色 */
}
.capacity-high {
  color: #F56C6C; /* 红色 */
}

/* 首页模式样式 */
.app-container.home-mode {
  margin-left: 0 !important;
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
}

/* 首页模式下的表格样式 */
.app-container.home-mode .table-card {
  width: 100%;
  max-width: 100%;
  overflow-x: auto;
}

.app-container.home-mode .el-table {
  width: 100%;
  min-width: 1200px; /* 确保表格有足够的最小宽度 */
}

.app-container.home-mode .el-table__body-wrapper {
  overflow-x: auto;
}

/* 统一卡片样式 */
.search-card,
.table-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.search-card:hover,
.table-card:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 卡片头部 */
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

.card-header .academy-tag {
  margin-left: 16px;
  padding: 6px 12px;
  font-weight: 500;
  border-radius: 20px;
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
  border: 1px solid #90caf9;
  color: #1976d2;
}

.card-header .academy-tag i {
  margin-right: 6px;
  font-size: 14px;
  color: #1976d2;
}

.record-count {
  margin-left: auto;
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}

/* 搜索表单 */
.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.search-input {
  min-width: 220px;
  transition: all 0.3s ease;
}

.search-input:hover {
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.search-actions {
  margin-left: auto;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

/* 按钮样式 */
.search-button {
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.refresh-button {
  background: #f0f2f5;
  border: none;
  padding: 10px 20px;
  color: #606266;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.refresh-button:hover {
  background: #e4e7ed;
  color: #333;
  transform: translateY(-2px);
}

/* 按钮区域 */
.button-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 5px;
}

.el-button-group {
  display: flex;
  gap: 8px;
}


/* 现代化表格 */
.modern-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.modern-table th {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9) !important;
  font-weight: 600;
  color: #1e293b;
  border-bottom: 2px solid #e2e8f0;
  padding: 16px 12px;
}

.modern-table td {
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 12px;
}

.modern-table tr:hover td {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9) !important;
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


.status-tag {
  font-weight: 600;
  padding: 0 10px;
  height: 28px;
  line-height: 28px;
}

.course-type-tag {
  font-weight: 500;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.course-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

/* 时间安排样式 */
.time-schedule {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 8px 0;
}

/* 内联时间安排样式 */
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

  &.signup-time {
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

  &.course-time {
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

  i {
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
}

.time-section {
  padding: 8px 0;
  text-align: center;
  transition: all 0.3s ease;

  &:hover {
    transform: translateX(2px);
  }

  &.signup-time {
    .time-header i {
      color: #409EFF;
    }
  }

  &.course-time {
    .time-header i {
      color: #67C23A;
    }
  }
}

.time-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6px;

  i {
    font-size: 14px;
    margin-right: 6px;
  }

  .time-label {
    font-size: 12px;
    font-weight: 600;
    color: #303133;
  }
}

.time-content {
  .time-item {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    color: #606266;
    line-height: 1.4;

    .time-start {
      font-weight: 500;
      color: #303133;
    }

    .time-separator {
      margin: 0 8px;
      color: #909399;
      font-size: 11px;
    }

    .time-end {
      font-weight: 500;
      color: #303133;
    }
  }
}

/* 保持原有的time-range样式作为备用 */
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

.view-button { background: #f0f9eb; border-color: #e1f3d8; color: #67c23a; }
.export-button { background: #f0f7ff; border-color: #d9ecff; color: #409eff; }
.edit-button { background: #f4f4f5; border-color: #d3d4d6; color: #909399; }
.delete-button { background: #fef0f0; border-color: #fde2e2; color: #f56c6c; }

/* 禁用按钮样式 */
.disabled-button {
  background: #f5f7fa !important;
  border-color: #e4e7ed !important;
  color: #c0c4cc !important;
  cursor: not-allowed !important;
  opacity: 0.6;
}

.disabled-button:hover {
  background: #f5f7fa !important;
  border-color: #e4e7ed !important;
  color: #c0c4cc !important;
  transform: none !important;
  box-shadow: none !important;
}


/* 学生对话框优化样式 */
.student-dialog {
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

/* 学生统计信息 */
.student-stats {
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px 12px 0 0;

  .stats-card {
    display: flex;
    justify-content: space-around;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

    .stat-item {
      text-align: center;

      .stat-number {
        font-size: 28px;
        font-weight: 700;
        color: #409EFF;
        margin-bottom: 8px;
      }

      .stat-label {
        font-size: 14px;
        color: #606266;
        font-weight: 500;
      }
    }
  }
}

/* 学生筛选区域 */
.student-filter-container {
  margin: 20px 24px;
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;

  .filter-input {
    min-width: 180px;
    transition: all 0.3s ease;
  }

  .filter-input:hover {
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
  }

  .filter-button {
    background: linear-gradient(135deg, #409EFF, #64b5ff);
    border: none;
    padding: 8px 16px;
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.3s;
  }

  .filter-button:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  }

  .reset-button {
    background: #f0f2f5;
    border: none;
    padding: 8px 16px;
    color: #606266;
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.3s;
  }

  .reset-button:hover {
    background: #e4e7ed;
    color: #333;
    transform: translateY(-1px);
  }
}


/* 学生表格容器 */
.student-table-container {
  margin: 20px 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
  width: calc(100% - 48px);
}

/* 全宽表格样式 */
.full-width-table {
  width: 100% !important;
}

.full-width-table .el-table__body-wrapper {
  width: 100% !important;
}

.full-width-table .el-table__header-wrapper {
  width: 100% !important;
}

/* 增强的学生表格 */
.enhanced-student-table {
  .student-id-container {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;

    .student-id {
      font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
      font-size: 13px;
      color: #409EFF;
      font-weight: 500;
    }

    .copy-btn {
      opacity: 0;
      transition: opacity 0.3s;
      padding: 2px;

      &:hover {
        color: #409EFF;
      }
    }
  }

  .student-name-container {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;

    .student-name {
      font-weight: 600;
      color: #303133;
    }
  }

  .major-info {
    display: flex;
    justify-content: center;
    align-items: center;

    .major-name {
      display: block;
      font-size: 14px;
      color: #303133;
      font-weight: 500;
      margin-bottom: 4px;
    }

    .class-info {
      display: flex;
      align-items: center;
      font-size: 12px;
      color: #909399;

      i {
        margin-right: 4px;
        font-size: 12px;
      }
    }
  }

  .score-container {
    display: flex;
    justify-content: center;
    align-items: center;

    .score-text {
      font-size: 15px;
      font-weight: 600;
      font-family: 'Monaco', 'Menlo', monospace;
    }

    .score-excellent {
      color: #67C23A;
    }

    .score-good {
      color: #409EFF;
    }

    .score-medium {
      color: #E6A23C;
    }

    .score-pass {
      color: #909399;
    }

    .score-fail {
      color: #F56C6C;
    }
  }

  /* 行状态样式 */
  .approved-row {
    background-color: rgba(103, 194, 58, 0.05) !important;
  }

  .rejected-row {
    background-color: rgba(245, 108, 108, 0.05) !important;
  }

  .pending-row {
    background-color: rgba(230, 162, 60, 0.05) !important;
  }

  /* 悬停效果 */
  tr:hover {
    .copy-btn {
      opacity: 1;
    }
  }

  /* 操作按钮样式 */
  .approve-btn {
    color: #67C23A;

    &:hover {
      color: #5daf34;
      background-color: rgba(103, 194, 58, 0.1);
    }
  }

  .reject-btn {
    color: #F56C6C;

    &:hover {
      color: #f56c6c;
      background-color: rgba(245, 108, 108, 0.1);
    }
  }
}

/* 批量操作区域 */
.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-top: 1px solid #e4e7ed;
  margin: 0 24px;
  border-radius: 0 0 12px 12px;

  .batch-info {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
  }
  /* 文本域容器样式 */
  .editor-container {
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    line-height: normal;
    padding: 4px;
  }

  .batch-buttons {
    display: flex;
    gap: 12px;
  }
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #f8f9fa;
  border-radius: 0 0 12px 12px;

  .footer-left {
    .total-info {
      font-size: 14px;
      color: #606266;
      font-weight: 500;
    }
  }

  .footer-right {
    display: flex;
    gap: 12px;
  }
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .student-dialog .el-dialog {
    width: 95% !important;
    margin: 0 auto;
  }

  .student-table-container {
    margin: 15px 12px;
    width: calc(100% - 24px);
  }
}

@media (max-width: 768px) {
  .student-dialog {
    .el-dialog {
      width: 95% !important;
      margin: 0 auto;
    }
  }

  .stats-card {
    flex-direction: column;
    gap: 16px;

    .stat-item {
      .stat-number {
        font-size: 24px;
      }
    }
  }

  .student-table-container {
    margin: 10px 8px;
    width: calc(100% - 16px);
  }

  .full-width-table {
    font-size: 12px;
  }

  .full-width-table .el-table__header th {
    padding: 8px 4px;
  }

  .full-width-table .el-table__body td {
    padding: 8px 4px;
  }
}

/* 分页样式 */
.custom-pagination {
  display: flex;
  justify-content: center !important;
  margin: 20px auto 0;
  padding: 12px 0;
  width: 100%;
}

.custom-pagination /deep/ .el-pagination {
  display: inline-flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.custom-pagination /deep/ .el-pager li:hover {
  border-color: #409EFF;
  color: #409EFF;
}



/* 课程表单对话框美化样式 */
.course-form-dialog {
  .el-dialog {
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    overflow: hidden;
  }

  .el-dialog__header {
    padding: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px 16px 0 0;
  }

  .el-dialog__body {
    padding: 0;
    background: #f8f9fa;
  }

  .el-dialog__footer {
    padding: 0;
    background: #f8f9fa;
    border-radius: 0 0 16px 16px;
  }
}

/* 对话框头部 */
.dialog-header {
  padding: 24px 32px;
  color: white;

  .header-content {
    display: flex;
    align-items: center;
    gap: 16px;

    .header-icon {
      width: 48px;
      height: 48px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      backdrop-filter: blur(10px);

      i {
        font-size: 24px;
        color: white;
      }
    }

    .header-text {
      h3 {
        margin: 0 0 4px 0;
        font-size: 20px;
        font-weight: 600;
        color: white;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

/* 表单输入框样式 */
.form-input {
  .el-input__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;

    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }
}

.form-select {
  .el-input__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;

    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }
}

.form-datetime {
  .el-input__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;

    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }
}

.form-number {
  .el-input__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;

    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }
}

.input-with-unit {
  display: flex;
  align-items: center;
  gap: 8px;
}

.unit-text {
  color: #909399;
  font-size: 14px;
  white-space: nowrap;
  flex-shrink: 0;
}

.form-textarea {
  .el-textarea__inner {
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;
    resize: vertical;

    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
    }
  }
}

/* 表单提示样式 */
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 6px;
  border-left: 3px solid #409EFF;

  i {
    margin-right: 6px;
    color: #409EFF;
    font-size: 14px;
  }
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;

  .footer-left {
    .form-status {
      display: flex;
      align-items: center;
      color: #409EFF;
      font-size: 14px;

      i {
        margin-right: 8px;
        animation: rotating 2s linear infinite;
      }
    }
  }

  .footer-right {
    display: flex;
    gap: 12px;

    .cancel-btn {
      background: #f5f7fa;
      border: 1px solid #dcdfe6;
      color: #606266;
      border-radius: 8px;
      padding: 10px 20px;
      font-weight: 500;
      transition: all 0.3s ease;

      &:hover {
        background: #ecf5ff;
        border-color: #b3d8ff;
        color: #409EFF;
      }

      i {
        margin-right: 6px;
      }
    }

    .submit-btn {
      background: linear-gradient(135deg, #409EFF 0%, #64b5ff 100%);
      border: none;
      color: white;
      border-radius: 8px;
      padding: 10px 24px;
      font-weight: 600;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
        background: linear-gradient(135deg, #3a8ee6 0%, #5a9fd8 100%);
      }

      &:active {
        transform: translateY(0);
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
      }

      i {
        margin-right: 6px;
      }
    }
  }
}

/* 旋转动画 */
@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .course-form-dialog {
    .el-dialog {
      width: 95% !important;
      margin: 0 auto;
    }
  }

  .dialog-body {
    padding: 20px;
  }

  .dialog-footer {
    flex-direction: column;
    gap: 16px;

    .footer-right {
      width: 100%;
      justify-content: center;
    }
  }
}


/* 课程重复错误提示样式 */
.course-duplicate-error {
  background: linear-gradient(135deg, #ff4757, #ff3742) !important;
  border: 2px solid #ff3742 !important;
  box-shadow: 0 8px 32px rgba(255, 71, 87, 0.4) !important;
  border-radius: 12px !important;
  color: white !important;
  font-weight: 600 !important;
  font-size: 14px !important;
  padding: 16px 20px !important;
  max-width: 400px !important;
  animation: shake 0.5s ease-in-out !important;
}

.course-duplicate-error .el-message__content {
  color: white !important;
  font-weight: 600 !important;
}

.course-duplicate-error .el-message__closeBtn {
  color: white !important;
  font-size: 16px !important;
}

.course-duplicate-error .el-message__closeBtn:hover {
  color: #ffebee !important;
}

/* 时间验证错误提示样式 */
.time-validation-error {
  background: linear-gradient(135deg, #ff4757, #ff3742) !important;
  border: 2px solid #ff3742 !important;
  box-shadow: 0 8px 32px rgba(255, 71, 87, 0.4) !important;
  border-radius: 12px !important;
  color: white !important;
  font-weight: 600 !important;
  font-size: 14px !important;
  padding: 16px 20px !important;
  max-width: 400px !important;
  animation: shake 0.5s ease-in-out !important;
}

.time-validation-error .el-message__content {
  color: white !important;
  font-weight: 600 !important;
}

.time-validation-error .el-message__closeBtn {
  color: white !important;
  font-size: 16px !important;
}

.time-validation-error .el-message__closeBtn:hover {
  color: #ffebee !important;
}

/* 必填项验证错误提示样式 */
.required-validation-error {
  background: linear-gradient(135deg, #ff4757, #ff3742) !important;
  border: 2px solid #ff3742 !important;
  box-shadow: 0 8px 32px rgba(255, 71, 87, 0.4) !important;
  border-radius: 12px !important;
  color: white !important;
  font-weight: 600 !important;
  font-size: 14px !important;
  padding: 16px 20px !important;
  max-width: 400px !important;
  animation: shake 0.5s ease-in-out !important;
}

.required-validation-error .el-message__content {
  color: white !important;
  font-weight: 600 !important;
}

.required-validation-error .el-message__closeBtn {
  color: white !important;
  font-size: 16px !important;
}

.required-validation-error .el-message__closeBtn:hover {
  color: #ffebee !important;
}

/* 字段错误样式 */
.field-error .el-input__inner,
.field-error .el-textarea__inner,
.field-error .el-input-number__input,
.field-error .el-date-editor {
  border-color: #ff4757 !important;
  box-shadow: 0 0 0 2px rgba(255, 71, 87, 0.2) !important;
}

.field-error .el-select .el-input__inner {
  border-color: #ff4757 !important;
  box-shadow: 0 0 0 2px rgba(255, 71, 87, 0.2) !important;
}

.field-error .el-form-item__label {
  color: #ff4757 !important;
}

/* 必填字段星号样式 */
.required-asterisk {
  color: #ff4757 !important;
  font-weight: bold;
  margin-right: 4px;
}

/* 只读组织单位字段样式 */
.form-input[readonly] {
  background-color: #f5f7fa;
  color: #606266;
  cursor: not-allowed;
}

.form-input[disabled] {
  background-color: #f5f7fa;
  color: #606266;
  cursor: not-allowed;
}

.required-label {
  color: #606266;
}

/* 状态标签样式优化 */
.status-tag {
  font-weight: 600;
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 4px;
  min-width: 80px;
  text-align: center;
  transition: all 0.3s ease;
  border: none;
  color: white !important;
  display: inline-block;
  line-height: 1.2;
}

.status-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* 不同状态的颜色优化 */
.status-tag.el-tag--info {
  background: #909399; /* 灰色 - 选课未开始 */
  border-radius: 4px;
}

.status-tag.el-tag--success {
  background: #67C23A; /* 绿色 - 选课进行中 */
  border-radius: 4px;
}

.status-tag.el-tag--danger {
  background: #F56C6C; /* 红色 - 选课已截止 */
  border-radius: 4px;
}

/* 错误提示动画 */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
  20%, 40%, 60%, 80% { transform: translateX(5px); }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .custom-pagination {
    padding: 8px;
    justify-content: center;
  }

  .course-duplicate-error {
    max-width: 90% !important;
    font-size: 13px !important;
    padding: 12px 16px !important;
  }
}

/* ========== 图片上传相关样式 ========== */

/* 图片上传容器 */
.image-upload-container {
  width: 100%;
}

.image-uploader {
  width: 100%;
}

/* 上传占位符 */
.upload-placeholder {
  width: 100%;
  height: 200px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    border-color: #409EFF;
    background: #f0f7ff;
  }

  i {
    font-size: 48px;
    color: #c0c4cc;
    margin-bottom: 16px;
  }

  .upload-text {
    font-size: 16px;
    color: #606266;
    margin-bottom: 8px;
    font-weight: 500;
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
  }
}

/* 图片预览 */
.image-preview {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e7ed;

  .uploaded-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  .image-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
    opacity: 0;
    transition: opacity 0.3s ease;

    i {
      color: white;
      font-size: 24px;
      cursor: pointer;
      padding: 8px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      transition: all 0.3s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: scale(1.1);
      }
    }
  }

  &:hover .image-overlay {
    opacity: 1;
  }
}

/*课程图片展示 */
.course-image-container {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.course-image {
  max-width: 200px;
  max-height: 150px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  }
}

/* 图片预览对话框 */
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

/* 学分和地点列样式 */
.credit-text {
  font-weight: 600;
  color: #409EFF;
  font-size: 14px;
}

.location-text {
  color: #606266;
  font-size: 13px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
}

/* 课程种类标签样式 */
.category-tag {
  font-size: 12px;
  padding: 4px 8px;
}

/* 时间安排样式 */
.time-schedule {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 4px 0;

  .time-item {
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.time-icon {
  font-size: 14px;
  flex-shrink: 0;
}

.time-icon.registration {
  color: #409EFF;
}

.time-icon.activity {
  color: #67C23A;
}

.time-content {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}

.time-label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
  white-space: nowrap;
}

.time-range {
  font-size: 14px;
  color: #606266;
  line-height: 1.3;
  word-break: break-all;
}

/* 课程表单对话框美化样式 */
.course-form-dialog {
  .el-dialog {
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    overflow: hidden;
  }

  .el-dialog__header {
    padding: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px 16px 0 0;
  }

  .el-dialog__body {
    padding: 0;
    background: #f8f9fa;
  }

  .el-dialog__footer {
    padding: 20px 24px;
    background: white;
    border-top: 1px solid #e4e7ed;
  }
}

/* 对话框头部 */
.dialog-header {
  padding: 24px;
  color: white;

  .header-content {
    display: flex;
    align-items: center;

    .header-icon {
      width: 48px;
      height: 48px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;

      i {
        font-size: 24px;
        color: white;
      }
    }

    .header-text {
      h3 {
        margin: 0 0 4px 0;
        font-size: 20px;
        font-weight: 600;
        color: white;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

/* 对话框主体 */
.dialog-body {
  padding: 24px;
  max-height: 75vh;
  overflow-y: auto;
}

/* 课程表单 */
.course-form {
  .form-section {
    background: white;
    border-radius: 12px;
    margin-bottom: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border: 1px solid #e4e7ed;
    overflow: hidden;

    .section-header {
      display: flex;
      align-items: center;
      padding: 16px 20px;
      background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
      border-bottom: 1px solid #e4e7ed;

      i {
        font-size: 16px;
        color: #409EFF;
        margin-right: 8px;
      }

      span {
        font-weight: 600;
        color: #2c3e50;
        font-size: 14px;
      }
    }

    .section-content {
      padding: 20px;

      .form-input {
        width: 100%;
      }

      .form-select {
        width: 100%;
      }

      .form-textarea {
        width: 100%;
      }

      .form-datetime {
        width: 100%;
      }

      .form-number {
        width: 100%;
      }

      .form-tip {
        display: flex;
        align-items: center;
        margin-top: 8px;
        font-size: 12px;
        color: #909399;

        i {
          margin-right: 4px;
        }
      }

      .form-row {
        display: flex;
        gap: 16px;

        .form-item-half {
          flex: 1;
        }
      }

      .input-with-unit {
        display: flex;
        align-items: center;
        gap: 6px;
      }

      .unit-text {
        color: #909399;
        font-size: 12px;
        white-space: nowrap;
        flex-shrink: 0;
      }
    }
  }
}


/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .footer-left {
    .form-status {
      display: flex;
      align-items: center;
      color: #409EFF;
      font-size: 14px;

      i {
        margin-right: 8px;
        animation: spin 1s linear infinite;
      }
    }
  }

  .footer-right {
    display: flex;
    gap: 12px;

    .cancel-btn {
      padding: 10px 20px;
      border-radius: 6px;
    }

    .submit-btn {
      padding: 10px 20px;
      border-radius: 6px;
    }
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 已开始选课的课程行样式 */
.started-course-row {
  background-color: #f0f9ff !important;
  border-left: 4px solid #3b82f6 !important;
}

.started-course-row:hover {
  background-color: #e0f2fe !important;
}

/* 已开始选课的课程选择框样式 - 保持正常可点击状态 */
.started-course-row .el-checkbox {
  opacity: 1;
  cursor: pointer;
}

.started-course-row .el-checkbox__input {
  cursor: pointer;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .course-form-dialog {
    .el-dialog {
      width: 95% !important;
      margin: 0 auto;
    }
  }

  .dialog-body {
    padding: 20px;
  }

  .course-form .form-section .section-content .form-row {
    flex-direction: column;
    gap: 0;
  }
}

</style>
