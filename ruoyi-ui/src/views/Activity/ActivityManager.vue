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
          <el-form-item label="活动地点" prop="activityLocation">
            <el-input
              v-model="queryParams.activityLocation"
              placeholder="请输入活动地点"
              clearable
              prefix-icon="el-icon-location-outline"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <!-- <el-form-item label="组织单位" prop="organizer">
            <el-input
              v-model="queryParams.organizer"
              placeholder="请输入组织单位"
              clearable
              prefix-icon="el-icon-office-building"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item> -->
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
        <el-button
          type="success"
          plain
          icon="el-icon-upload"
          size="mini"
          @click="handleImport"
        >导入</el-button>
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-button-group>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <div class="card-header">
        <i class="el-icon-s-grid"></i>
        <span>活动列表</span>
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>
      
      <!-- 表格美化 -->
      <el-table v-loading="loading" :data="activitiesList" @selection-change="handleSelectionChange" class="modern-table" :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}">
      <el-table-column type="selection" width="45" align="center"/>
      <el-table-column label="序号" width="80" align="center">
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
      <el-table-column label="活动类型" align="center" prop="activityType" width="200">
        <template slot-scope="scope">
          <el-tag :type="getActivityTypeTagType(scope.row.activityType)" effect="plain" class="activity-type-tag">
            {{ getActivityTypeName(scope.row.activityType) || '未分类' }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- <el-table-column label="组织单位" align="center" prop="organizer" width="120"/> -->
      <el-table-column label="时间安排" align="center" min-width="280">
        <template slot-scope="scope">
          <div class="time-schedule">
            <!-- 报名时间 -->
            <div class="time-section signup-time">
              <div class="time-header">
                <i class="el-icon-user"></i>
                <span class="time-label">报名时间</span>
              </div>
              <div class="time-content">
                <div class="time-item">
                  <span class="time-start">{{ formatDateTime(scope.row.activityStart) }}</span>
                  <span class="time-separator">至</span>
                  <span class="time-end">{{ formatDateTime(scope.row.activityDeadline) }}</span>
                </div>
              </div>
            </div>
            
            <!-- 活动时间 -->
            <div class="time-section activity-time">
              <div class="time-header">
                <i class="el-icon-date"></i>
                <span class="time-label">活动时间</span>
              </div>
              <div class="time-content">
                <div class="time-item">
                  <span class="time-start">{{ formatDateTime(scope.row.startTime) }}</span>
                  <span class="time-separator">至</span>
                  <span class="time-end">{{ formatDateTime(scope.row.endTime) }}</span>
                </div>
              </div>
            </div>
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

      <el-table-column label="操作" width="150">
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
              编辑活动
            </el-button>
          </div>
        </template>
      </el-table-column>

      <!-- 活动详细信息 -->
      <el-table-column type="expand" width="60" align="center">
        <template slot-scope="props">
          <div class="expand-card">
            <div class="expand-row">
              <div class="expand-label"><i class="el-icon-location"></i> 活动地点:</div>
              <div class="expand-content">{{ props.row.activityLocation || "未设置地点" }}</div>
            </div>
            <div class="expand-row">
              <div class="expand-label"><i class="el-icon-document"></i> 活动描述:</div>
              <div class="expand-content">{{ props.row.activityDescription || "无描述信息" }}</div>
            </div>
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


    <!-- 添加或修改活动对话框 -->
    <el-dialog 
      :title="title" 
      :visible.sync="open" 
      width="600px" 
      append-to-body
      class="activity-form-dialog"
      :before-close="handleDialogClose">
      
      <!-- 对话框头部 -->
      <div slot="title" class="dialog-header">
        <div class="header-content">
          <div class="header-icon">
            <i :class="form.activityId ? 'el-icon-edit' : 'el-icon-plus'"></i>
          </div>
          <div class="header-text">
            <h3>{{ title }}</h3>
            <p>{{ form.activityId ? '修改活动信息' : '创建新的活动' }}</p>
          </div>
        </div>
      </div>

      <!-- 表单内容 -->
      <div class="dialog-body">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="activity-form">
          
          <!-- 基本信息区域 -->
          <div class="form-section">
            <div class="section-header">
              <i class="el-icon-info"></i>
              <span>基本信息</span>
            </div>
            <div class="section-content">
              <el-form-item label="活动名称" prop="activityName">
                <el-input 
                  v-model="form.activityName" 
                  placeholder="请输入活动名称"
                  prefix-icon="el-icon-trophy"
                  class="form-input">
                </el-input>
                <div class="form-tip">
                  <i class="el-icon-info"></i>
                  活动名称在当前组织单位下必须唯一
                </div>
              </el-form-item>
              
              <el-form-item label="活动类型" prop="activityType">
                <el-select v-model="form.activityType" placeholder="请选择活动类型" class="form-select">
                  <el-option 
                    v-for="type in predefinedActivityTypes" 
                    :key="type.value" 
                    :label="type.label" 
                    :value="type.value">
                    <span style="float: left">{{ type.label }}</span>
                    <span style="float: right; color: #8492a6; font-size: 13px">{{ type.value }}</span>
                  </el-option>
                </el-select>
              </el-form-item>
              
              <el-form-item label="活动地点" prop="activityLocation">
                <el-input 
                  v-model="form.activityLocation" 
                  placeholder="请输入活动地点"
                  prefix-icon="el-icon-location"
                  class="form-input">
                </el-input>
              </el-form-item>
              
              <el-form-item label="活动容量" prop="activityTotalCapacity">
                <el-input-number
                  v-model="form.activityTotalCapacity"
                  :min="1"
                  :max="1000"
                  controls-position="right"
                  class="form-number">
                </el-input-number>
                <span class="capacity-tip">人</span>
              </el-form-item>
            </div>
          </div>

          <!-- 时间安排区域 -->
          <div class="form-section">
            <div class="section-header">
              <i class="el-icon-time"></i>
              <span>时间安排</span>
            </div>
            <div class="section-content">
              <div class="time-grid">
                <el-form-item label="报名开始" prop="activityStart" class="time-item">
                  <el-date-picker 
                    v-model="form.activityStart"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="选择报名开始时间"
                    class="form-datetime">
                  </el-date-picker>
                </el-form-item>
                
                <el-form-item label="报名截止" prop="activityDeadline" class="time-item">
                  <el-date-picker 
                    v-model="form.activityDeadline"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="选择报名截止时间"
                    class="form-datetime">
                  </el-date-picker>
                </el-form-item>
                
                <el-form-item label="活动开始" prop="startTime" class="time-item">
                  <el-date-picker 
                    v-model="form.startTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="选择活动开始时间"
                    class="form-datetime">
                  </el-date-picker>
                </el-form-item>
                
                <el-form-item label="活动结束" prop="endTime" class="time-item">
                  <el-date-picker 
                    v-model="form.endTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="选择活动结束时间"
                    class="form-datetime">
                  </el-date-picker>
                </el-form-item>
              </div>
            </div>
          </div>

          <!-- 详细信息区域 -->
          <div class="form-section">
            <div class="section-header">
              <i class="el-icon-document"></i>
              <span>详细信息</span>
            </div>
            <div class="section-content">
              <el-form-item label="活动描述" prop="activityDescription">
                <el-input 
                  v-model="form.activityDescription" 
                  type="textarea" 
                  :rows="4"
                  placeholder="请详细描述活动内容、目的和要求..."
                  class="form-textarea">
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
            <span>正在{{ form.activityId ? '保存修改' : '创建活动' }}...</span>
          </div>
        </div>
        <div class="footer-right">
          <el-button 
            @click="cancel" 
            :disabled="isSubmitting"
            class="cancel-btn">
            <i class="el-icon-close"></i>
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="submitForm"
            :loading="isSubmitting"
            :disabled="isSubmitting"
            class="submit-btn">
            <i :class="form.activityId ? 'el-icon-check' : 'el-icon-plus'"></i>
            {{ form.activityId ? '保存修改' : '创建活动' }}
          </el-button>
        </div>
      </div>
    </el-dialog>
    <!-- 学生预约活动对话框 -->
    <el-dialog
      title="预约活动学生列表"
      :visible.sync="dialogVisibleStudents"
      width="95%"
      append-to-body
      class="student-dialog"
      :before-close="handleStudentDialogClose">
      
      <!-- 学生统计信息 -->
      <div class="student-stats" v-if="selectedStudents.length > 0">
        <div class="stats-card">
          <div class="stat-item">
            <div class="stat-number">{{ selectedStudents.length }}</div>
            <div class="stat-label">总报名人数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ getStatusCount('approved') }}</div>
            <div class="stat-label">已通过</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ getStatusCount('submitted') }}</div>
            <div class="stat-label">待审核</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ getStatusCount('rejected') }}</div>
            <div class="stat-label">未通过</div>
          </div>
        </div>
      </div>


      <!-- 学生表格 -->
      <div class="student-table-container">
        <el-table
          :data="filteredStudents"
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
          @selection-change="handleStudentSelectionChange">
          
          <el-table-column type="selection" width="50" align="center"/>
          
          <el-table-column label="序号" width="70" align="center">
            <template v-slot="scope">
              <span class="index-badge">
                {{ scope.$index + 1 }}
              </span>
            </template>
          </el-table-column>
          
          <el-table-column prop="studentId" label="学号" min-width="140" sortable>
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

          <el-table-column prop="studentName" label="姓名" min-width="100" sortable>
            <template slot-scope="{row}">
              <div class="student-name-container">
                <span class="student-name">{{ row.studentName }}</span>
                <el-tag v-if="row.isBooked" type="success" size="mini" effect="plain">已报名</el-tag>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="academy" label="所属书院" min-width="120" sortable>
            <template slot-scope="{row}">
              <el-tag size="small" :type="getAcademyTagType(row.academy)" effect="plain">
                {{ row.academy || '未知' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="major" label="专业" min-width="160" sortable>
            <template slot-scope="{row}">
              <div class="major-info">
                <span class="major-name">{{ row.major || row.systemMajor || '未知' }}</span>
                <div class="class-info" v-if="row.studentClass">
                  <i class="el-icon-office-building"></i>
                  {{ row.studentClass }}
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="审核状态" min-width="110" align="center">
            <template slot-scope="{row}">
              <el-tag :type="getBookingStatusTag(row.status)" size="small" effect="dark">
                {{ getBookingStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="bookAt" label="预约时间" min-width="160" sortable>
            <template slot-scope="{row}">
              <div class="booked_at">
                <i class="el-icon-time"></i>
                <span class="time-text">{{ parseTime(row.bookAt) || '无时间' }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100" align="center">
            <template slot-scope="{row}">
              <el-button-group>
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="viewStudentDetail(row)"
                  title="查看详情">
                </el-button>
                <el-button
                  v-if="row.status === 'submitted'"
                  size="mini"
                  type="text"
                  icon="el-icon-check"
                  @click="approveStudent(row)"
                  title="通过审核"
                  class="approve-btn">
                </el-button>
                <el-button
                  v-if="row.status === 'submitted'"
                  size="mini"
                  type="text"
                  icon="el-icon-close"
                  @click="rejectStudent(row)"
                  title="拒绝审核"
                  class="reject-btn">
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 批量操作区域 -->
      <div class="batch-actions" v-if="selectedStudentIds.length > 0">
        <div class="batch-info">
          <span>已选择 {{ selectedStudentIds.length }} 名学生</span>
        </div>
        <div class="batch-buttons">
          <el-button
            type="success"
            size="small"
            icon="el-icon-check"
            @click="batchApproveStudents">
            批量通过
          </el-button>
          <el-button
            type="danger"
            size="small"
            icon="el-icon-close"
            @click="batchRejectStudents">
            批量拒绝
          </el-button>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <div class="footer-left">
          <span class="total-info">共 {{ filteredStudents.length }} 名学生</span>
        </div>
        <div class="footer-right">
          <el-button
            type="primary"
            icon="el-icon-download"
            @click="handleExportStudents"
            class="export-btn">
            导出名单
          </el-button>
          <el-button @click="dialogVisibleStudents = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>
    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px">
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
        <div class="el-upload__tip" slot="tip">
          <el-checkbox v-model="upload.updateSupport"/>
          是否更新已经存在的用户数据
          <el-link type="info" style="font-size:12px" @click="importTemplate">下载模板</el-link>
        </div>
        <div class="el-upload__tip" style="color:red" slot="tip">提示：仅允许导入“xls”或“xlsx”格式文件！</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listActivities, getActivities, delActivities, addActivities, updateActivities2, checkActivityUnique} from "@/api/system/activities";
import {getToken} from "@/utils/auth";
import {listBookingsWithActivity} from "@/api/system/bookings";
import {getNickName} from "@/api/system/student";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "Activities",
  data() {
    return {
      // 新增状态
      dialogVisibleStudents: false,
      selectedStudents: [],
      studentLoading: false,
      // 选中的学生ID列表
      selectedStudentIds: [],
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 选中数组
      names: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 活动表格数据
      activitiesList: [],
      // 可用的活动类型列表
      availableActivityTypes: [],
      // 预定义的活动类型
      predefinedActivityTypes: [
        { value: '1', label: '人格塑造与价值引领活动类' },
        { value: '2', label: '知识融合与思维进阶活动类' },
        { value: '3', label: '能力锻造与实践创新活动类' },
        { value: '4', label: '社会责任与领军意识活动类' }
      ],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        activityId: null,
        activityName: null,
        startTime: null,
        endTime: null,
        activityLocation: null,
        activityCapacity: null,
        activityTotalCapacity: null,
        activityStart:null,
        activityDeadline: null,
        activityDescription: null,
        activityType: null,
        createdAt: null,
        organizer: null,
        notes: null,
      },
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: {Authorization: "Bearer " + getToken()},
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/system/activities/importData"
      },
      // 表单参数
      form: {},
      // 提交状态控制
      isSubmitting: false,

      // 表单校验
      rules: {
        activityName: [
          {required: true, message: "活动名称不能为空", trigger: "blur"}
        ],
        activityLocation: [
          {required: true, message: "活动地点不能为空", trigger: "blur"}
        ],
        activityTotalCapacity: [
          { required: true, message: "活动容量不能为空", trigger: "blur" }
          ],
        activityType: [
          { required: true, message: "请选择活动类型", trigger: "change" }
        ],
        activityStart: [
          {required: true, message: "报名开始时间不能为空", trigger: "blur"}
        ],
        activityDeadline: [
          {required: true, message: "报名截止时间不能为空", trigger: "blur"},
          {
            validator: (rule, value, callback) => {
              if (this.form.activityStart && value) {
                const start = new Date(this.form.activityStart).getTime();
                const deadline = new Date(value).getTime();
                // 检查截止时间是否在开始时间后（至少1秒）
                if (deadline - start < 1000) {
                  callback(new Error("报名截止时间必须晚于报名开始时间"));
                } else {
                  callback();
                }
              } else {
                callback();
              }
            },
            trigger: "blur"
          }
        ],
        organizer: [
          {required: true, message: "组织单位不能为空", trigger: "blur"}
        ],
        startTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (this.form.activityDeadline && value) {
                const deadline = new Date(this.form.activityDeadline).getTime();
                const start = new Date(value).getTime();
                if (start - deadline < 1000) {
                  callback(new Error("开始时间必须晚于报名截止时间"));
                } else {
                  callback();
                }
              } else {
                callback();
              }
            },
            trigger: "blur"
          }
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (this.form.startTime && value) {
                const start = new Date(this.form.startTime).getTime();
                const end = new Date(value).getTime();
                if (end - start < 1000) {
                  callback(new Error("结束时间必须晚于开始时间"));
                } else {
                  callback();
                }
              } else {
                callback();
              }
            },
            trigger: "blur"
          }
        ]
      }
    };
  },
  computed: {
    // 学生列表（直接显示所有学生，不进行过滤）
    filteredStudents() {
      return this.selectedStudents;
    }
  },
  created() {
    this.getList();
  },
  methods: {
    statusTagType(status) {
      const map = {
        '未开始': '',         // 默认蓝色（适合未开始状态）
        '可报名': 'success',  // 绿色（表示可以报名）
        '已截止': 'danger',   // 红色（表示已截止不可报名）
        '进行中': 'warning',  // 黄色（表示活动正在进行中）
        '已结束': 'info'      // 灰色（表示活动已结束）
      }
      return map[status] || 'info'; // 默认使用灰色
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

    /** 计算容量百分比 */
    calculateCapacityPercentage(row) {
      if (!row.activityTotalCapacity || row.activityTotalCapacity <= 0) return 0;
      const used = row.activityTotalCapacity - row.activityCapacity;
      return Math.round((used / row.activityTotalCapacity) * 100);
    },

    /** 获取进度条颜色 */
    getProgressColor(percentage) {
      if (percentage >= 80) return '#f87171';
      if (percentage >= 50) return '#fbbf24';
      return '#4ade80';
    },

    /** 获取容量文字样式类 */
    getCapacityClass(row) {
      const percentage = this.calculateCapacityPercentage(row);
      if (percentage >= 80) return 'capacity-high';
      if (percentage >= 50) return 'capacity-medium';
      return 'capacity-low';
    },

    /** 获取预约状态文本 */
    getBookingStatusText(status) {
      console.log('审核状态原始值:', status, '类型:', typeof status);
      const statusMap = {
        'pending': '未提交',
        'submitted': '未审核', 
        'approved': '已通过',
        'rejected': '未通过',
        '未提交': '未提交',
        '未审核': '未审核',
        '已通过': '已通过',
        '未通过': '未通过'
      };
      return statusMap[status] || '未知';
    },

    /** 获取预约状态标签类型 */
    getBookingStatusTag(status) {
      const tagMap = {
        'pending': 'info',      // 未提交 - 灰色
        'submitted': 'warning', // 未审核 - 橙色
        'approved': 'success',  // 已通过 - 绿色
        'rejected': 'danger',   // 未通过 - 红色
        '未提交': 'info',       // 未提交 - 灰色
        '未审核': 'warning',    // 未审核 - 橙色
        '已通过': 'success',    // 已通过 - 绿色
        '未通过': 'danger'      // 未通过 - 红色
      };
      return tagMap[status] || 'info';
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
        '4': 'info',      // 社会责任与领军意识活动类 - 灰色
        '其他': ''        // 默认蓝色
      }
      return map[activityType] || 'info';
    },
    // 导出选课学生
    handleExportStudents() {
      if (this.selectedStudents.length === 0) {
        this.$message.warning("没有可导出的数据");
        return;
      }

      const activityId = this.selectedStudents[0]?.activityId;
      const activityName = this.selectedStudents[0]?.activityName;

      if (!activityId) {
        this.$message.error("缺少活动ID参数");
        return;
      }

      this.download('/system/bookings/exportStudents', {
        activityId: activityId
      }, `预约学生名单_${activityName}_${this.parseTime(new Date(), '{y}{m}{d}')}.xlsx`)

      this.dialogVisibleStudents = false; // 导出后自动关闭对话框
    },
    // 查看选课学生
    async handleViewStudents(row) {
      this.studentLoading = true;
      try {
        const res = await listBookingsWithActivity({
          activityId: row.activityId // 使用当前行的活动ID，而不是硬编码的6
        });

        if (res.rows && res.rows.length) {
          this.selectedStudents = res.rows;
          this.selectedStudentIds = [];
          this.dialogVisibleStudents = true;
        } else {
          // 使用正确的消息提示方法
          this.$message.info("当前活动暂无学生预约");
        }
      } catch (e) {
        console.error("获取学生预约活动数据失败", e);
        // 添加错误提示
        this.$message.error("获取学生预约活动数据失败，请稍后再试");
      } finally {
        this.studentLoading = false;
      }
    },
    /** 计算活动状态 */
    calculateStatus() {
      // 获取当前时间（使用服务器时间更准确，这里先用客户端时间）
      const now = new Date().getTime();
      const startSign = new Date(this.form.activityStart).getTime();
      const deadline = new Date(this.form.activityDeadline).getTime();
      const startActivity = new Date(this.form.startTime).getTime();
      const endActivity = new Date(this.form.endTime).getTime();

      // 检查时间点是否有效
      if (!isNaN(startSign) && !isNaN(deadline) &&
        !isNaN(startActivity) && !isNaN(endActivity)) {

        if (now < startSign) {
          this.form.status = '未开始';
        } else if (now <= deadline) {
          this.form.status = '可报名';
        } else if (now < startActivity) {
          this.form.status = '已截止';
        } else if (now <= endActivity) {
          this.form.status = '进行中';
        } else {
          this.form.status = '已结束';
        }
      } else {
        this.form.status = '未开始';
      }
    },
    /** 导入模板操作 */
    importTemplate() {
      fetch(process.env.VUE_APP_BASE_API + '/system/activities/importTemplate', {
        mode: 'cors', // 明确跨域模式
        headers: new Headers({'Content-Type': 'application/json'})
      })
        .then(response => response.blob())
        .then(blob => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = '书院活动信息.xlsx'; // 指定下载文件名
          a.click();
          window.URL.revokeObjectURL(url);
        });
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "用户导入";
      this.upload.open = true;
    },
// 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
// 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert(response.msg, "导入结果", {dangerouslyUseHTMLString: true});
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    /** 查询活动列表 */
    getList() {
      this.loading = true;
      console.log("开始获取活动列表，queryParams:", this.queryParams);
      getNickName().then(nickName => {
        this.queryParams.organizer = nickName.msg; // 更新组织者
        console.log("获取到组织者名称:", nickName.msg);
        // 🔽 确保在 organizer 更新后调用列表接口
        listActivities(this.queryParams).then(response => {
          console.log("获取活动列表成功:", response);
          this.activitiesList = response.rows;
          this.total = response.total;
          this.loading = false;
          // 更新可用的活动类型列表
          this.updateAvailableActivityTypes();
        }).catch(error => {
          console.error("获取活动列表失败:", error);
          this.loading = false;
          this.$message.error("获取活动列表失败");
        });
      }).catch(error => {
        console.error("获取组织者名称失败:", error);
        // 即使获取组织者名称失败，也尝试获取活动列表
        listActivities(this.queryParams).then(response => {
          console.log("获取活动列表成功（无组织者）:", response);
          this.activitiesList = response.rows;
          this.total = response.total;
          this.loading = false;
          // 更新可用的活动类型列表
          this.updateAvailableActivityTypes();
        }).catch(listError => {
          console.error("获取活动列表失败:", listError);
          this.loading = false;
          this.$message.error("获取活动列表失败");
        });
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.isSubmitting = false;
      this.reset();
    },

    // 对话框关闭处理
    handleDialogClose(done) {
      if (this.isSubmitting) {
        this.$message.warning('正在处理中，请稍候...');
        return;
      }
      this.cancel();
      done();
    },
    // 表单重置
    reset() {
      this.form = {
        activityId: null,
        activityName: null,
        startTime: null,
        endTime: null,
        activityLocation: null,
        activityCapacity: null,
        activityTotalCapacity: null,
        activityStart:null,
        activityDeadline: null,
        activityDescription: null,
        activityType: null,
        status: null,
        createdAt: null,
        organizer: null,
        notes: null,
        version: 0
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.activityId)
      this.names = selection.map(item => item.activityName)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加活动信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const activityId = row.activityId || this.ids
      getActivities(activityId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改活动信息";
      });
    },
    /** 提交按钮 */
    /** 提交按钮 */
    async submitForm() {
      // 防止重复提交
      if (this.isSubmitting) {
        this.$message.warning("数据正在处理，请勿重复提交");
        return;
      }

      this.isSubmitting = true;

      try {
        // 1. 先确保获取组织者名称
        const result = await getNickName(); // 等待异步操作完成
        this.form.organizer = result.msg;

        console.log("提交表单数据：", JSON.parse(JSON.stringify(this.form)));

        // 2. 验证表单
        const valid = await new Promise((resolve) => {
          this.$refs.form.validate(resolve);
        });

        if (!valid) {
          this.isSubmitting = false;
          return; // 验证不通过则停止
        }

        // 3. 计算活动状态
        this.calculateStatus();

        // 4. 根据情况执行新增/修改（唯一性检查在后端进行）
        if (this.form.activityId != null) {
          await updateActivities2(this.form);  // 等待更新完成
          this.$message.success("修改成功");
        } else {
          await addActivities(this.form);  // 等待新增完成
          this.$message.success("新增成功");
        }

        // 5. 关闭弹窗并刷新列表
        this.open = false;
        await this.getList();  // 如果需要等待刷新完成

      } catch (error) {
        console.error("表单提交失败:", error);
        // 检查是否是唯一性错误
        if (error.message && error.message.includes("活动名称和组织单位组合已存在")) {
          this.$message.error("活动名称和组织单位组合已存在，不能重复添加！");
        } else {
          this.$message.error(`操作失败: ${error.message || '未知错误'}`);
        }
      } finally {
        this.isSubmitting = false;
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const activityIds = row.activityId || this.ids;
      const activityNames = row.activityName || this.names;
      this.$modal.confirm('是否确认删除活动名称为"' + activityNames + '"的数据项？').then(function () {
        return delActivities(activityIds);
      }).then(() => {
        this.getList();
        this.$message.success("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/activities/export', {
        ...this.queryParams
      }, `activities_${new Date().getTime()}.xlsx`)
    },

    /** 更新可用的活动类型列表 */
    updateAvailableActivityTypes() {
      // 使用预定义的活动类型
      this.availableActivityTypes = ['1', '2', '3', '4'];
    },

    // ========== 学生列表相关方法 ==========
    
    /** 获取状态统计数量 */
    getStatusCount(status) {
      return this.selectedStudents.filter(student => student.status === status).length;
    },

    /** 获取书院标签类型 */
    getAcademyTagType(academy) {
      const academyColors = {
        '知行书院': 'primary',
        '明德书院': 'success', 
        '博雅书院': 'warning',
        '至善书院': 'info',
        '未知': ''
      };
      return academyColors[academy] || 'info';
    },

    /** 获取学生行样式类名 */
    getStudentRowClassName({row}) {
      if (row.status === 'approved') return 'approved-row';
      if (row.status === 'rejected') return 'rejected-row';
      if (row.status === 'submitted') return 'pending-row';
      return '';
    },


    /** 学生选择变化 */
    handleStudentSelectionChange(selection) {
      this.selectedStudentIds = selection.map(student => student.studentId);
    },

    /** 复制到剪贴板 */
    copyToClipboard(text) {
      navigator.clipboard.writeText(text).then(() => {
        this.$message.success('已复制到剪贴板');
      }).catch(() => {
        this.$message.error('复制失败');
      });
    },

    /** 查看学生详情 */
    viewStudentDetail(student) {
      this.$alert(`
        <div style="text-align: left;">
          <p><strong>学号：</strong>${student.studentId}</p>
          <p><strong>姓名：</strong>${student.studentName}</p>
          <p><strong>所属书院：</strong>${student.academy || '未知'}</p>
          <p><strong>专业：</strong>${student.major || student.systemMajor || '未知'}</p>
          <p><strong>行政班：</strong>${student.studentClass || '未知'}</p>
          <p><strong>审核状态：</strong>${this.getBookingStatusText(student.status)}</p>
          <p><strong>预约时间：</strong>${this.parseTime(student.bookAt) || '无时间'}</p>
        </div>
      `, '学生详情', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定'
      });
    },

    /** 通过学生审核 */
    approveStudent(student) {
      this.$confirm(`确定要通过学生 ${student.studentName} 的审核吗？`, '确认通过', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        // 这里应该调用API更新学生状态
        student.status = 'approved';
        this.$message.success('审核通过成功');
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    },

    /** 拒绝学生审核 */
    rejectStudent(student) {
      this.$confirm(`确定要拒绝学生 ${student.studentName} 的审核吗？`, '确认拒绝', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 这里应该调用API更新学生状态
        student.status = 'rejected';
        this.$message.success('审核拒绝成功');
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    },

    /** 批量通过学生审核 */
    batchApproveStudents() {
      if (this.selectedStudentIds.length === 0) {
        this.$message.warning('请先选择要操作的学生');
        return;
      }
      
      this.$confirm(`确定要批量通过 ${this.selectedStudentIds.length} 名学生的审核吗？`, '批量通过', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        // 这里应该调用API批量更新学生状态
        this.selectedStudents.forEach(student => {
          if (this.selectedStudentIds.includes(student.studentId)) {
            student.status = 'approved';
          }
        });
        this.selectedStudentIds = [];
        this.$message.success('批量审核通过成功');
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    },

    /** 批量拒绝学生审核 */
    batchRejectStudents() {
      if (this.selectedStudentIds.length === 0) {
        this.$message.warning('请先选择要操作的学生');
        return;
      }
      
      this.$confirm(`确定要批量拒绝 ${this.selectedStudentIds.length} 名学生的审核吗？`, '批量拒绝', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 这里应该调用API批量更新学生状态
        this.selectedStudents.forEach(student => {
          if (this.selectedStudentIds.includes(student.studentId)) {
            student.status = 'rejected';
          }
        });
        this.selectedStudentIds = [];
        this.$message.success('批量审核拒绝成功');
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    },

    /** 学生对话框关闭处理 */
    handleStudentDialogClose(done) {
      this.selectedStudentIds = [];
      done();
    },

  },
  watch: {
    'form.activityTotalCapacity'(newVal) {
      this.form.activityCapacity = newVal;
    }
  }
};
</script>

<style scoped>
/* 扩展卡片 */
.expand-card {
  background: #f9fafc;
  border-radius: 8px;
  padding: 15px;
  margin: 5px;
  border-left: 3px solid #409EFF;
}

.expand-row {
  display: flex;
  margin-bottom: 12px;
}

.expand-label {
  font-weight: 600;
  min-width: 100px;
  color: #409EFF;
  display: flex;
  align-items: center;
}

.expand-label i {
  margin-right: 8px;
}

.expand-content {
  flex: 1;
  color: #606266;
  line-height: 1.6;
}
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

.activity-type-tag {
  font-weight: 500;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.activity-name {
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
  
  &.activity-time {
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


/* 学生表格容器 */
.student-table-container {
  margin: 20px 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

/* 增强的学生表格 */
.enhanced-student-table {
  .student-id-container {
    display: flex;
    align-items: center;
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
    gap: 8px;
    
    .student-name {
      font-weight: 600;
      color: #303133;
    }
  }
  
  .major-info {
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
  
  .booked_at {
    display: flex;
    align-items: center;
    color: #606266;
    font-size: 13px;
    
    i {
      margin-right: 6px;
      color: #909399;
    }
    
    .time-text {
      font-family: monospace;
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



/* 活动表单对话框美化样式 */
.activity-form-dialog {
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

/* 活动表单 */
.activity-form {
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

/* 时间网格布局 */
.time-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  
  .time-item {
    margin-bottom: 0;
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
  width: 100%;
  
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

.capacity-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
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
  .activity-form-dialog {
    .el-dialog {
      width: 95% !important;
      margin: 0 auto;
    }
  }
  
  .dialog-body {
    padding: 20px;
  }
  
  .time-grid {
    grid-template-columns: 1fr;
    gap: 16px;
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

/* 响应式调整 */
@media (max-width: 768px) {
  .custom-pagination {
    padding: 8px;
    justify-content: center;
  }
}
</style>
