<template>
  <div class="app-container" :class="{ 'home-mode': isHomeMode }">
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
          <!-- <el-form-item label="活动地点" prop="activityLocation">
            <el-input
              v-model="queryParams.activityLocation"
              placeholder="请输入活动地点"
              clearable
              prefix-icon="el-icon-location-outline"
              class="search-input"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item> -->
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
          <el-form-item label="活动状态" prop="activityStatus">
            <el-select v-model="queryParams.activityStatus" clearable placeholder="请选择活动状态" class="search-input">
              <el-option
                v-for="status in activityStatusOptions"
                :key="status.value"
                :label="status.label"
                :value="status.value"
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
        <el-tooltip v-if="hasEndedActivities" content="选中的活动中包含已结束的活动，点击删除时会提示无法删除" placement="top">
          <i class="el-icon-warning" style="color: #E6A23C; margin-left: 8px; font-size: 16px;"></i>
        </el-tooltip>
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
      <el-table v-loading="loading" :data="activitiesList" @selection-change="handleSelectionChange" class="modern-table" :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}" :row-class-name="getRowClassName">
        <el-table-column type="selection" width="45" align="center"/>
        <el-table-column label="序号" width="80" align="center">
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
        <el-table-column label="活动类型" align="center" prop="activityType" width="200">
          <template slot-scope="scope">
            <el-tag :type="getActivityTypeTagType(scope.row.activityType)" effect="plain" class="activity-type-tag">
              {{ getActivityTypeName(scope.row.activityType) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column label="组织单位" align="center" prop="organizer" width="120"/> -->
        <el-table-column label="时间安排" align="center" min-width="250">
          <template slot-scope="scope">
            <div class="time-schedule-inline">
              <!-- 报名时间 -->
              <div class="time-inline-item signup-time">
                <i class="el-icon-user"></i>
                <span class="time-inline-label">报名时间</span>
                <span class="time-inline-content">
                  {{ formatDateTime(scope.row.activityStart) }} 至 {{ formatDateTime(scope.row.activityDeadline) }}
                </span>
              </div>

              <!-- 活动时间 -->
              <div class="time-inline-item activity-time">
                <i class="el-icon-date"></i>
                <span class="time-inline-label">活动时间</span>
                <span class="time-inline-content">
                  {{ formatDateTime(scope.row.startTime) }} 至 {{ formatDateTime(scope.row.endTime) }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 活动状态列 -->
        <el-table-column label="活动状态" align="center" width="150">
          <template slot-scope="scope">
            <el-tag :type="getActivityStatusTag(scope.row)" effect="dark" class="status-tag">
              {{ getActivityStatusText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="报名人数" align="center" width="120">
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
                :disabled="isActivityEnded(scope.row)"
                @click="handleUpdate(scope.row)"
                class="action-button edit-button"
                :class="{ 'disabled-button': isActivityEnded(scope.row) }">
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
              <div class="expand-row" v-if="props.row.pictureUrl">
                <div class="expand-label"><i class="el-icon-picture"></i> 活动图片:</div>
                <div class="expand-content">
                  <div class="activity-image-container">
                    <el-image
                      :src="getActivityImageUrl(props.row.pictureUrl)"
                      :preview-src-list="[getActivityImageUrl(props.row.pictureUrl)]"
                      fit="cover"
                      class="activity-image"
                    />
                  </div>
                </div>
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
                  type="textarea"
                  v-model="form.activityDescription"
                  :rows="6"
                  placeholder="请输入活动描述"
                  class="form-textarea"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="活动图片" prop="pictureUrl">
                <div class="image-upload-container">
                  <el-upload
                    class="image-uploader"
                    :action="uploadUrl"
                    :headers="uploadHeaders"
                    :show-file-list="false"
                    :on-success="handleImageSuccess"
                    :on-error="handleImageError"
                    :before-upload="beforeImageUpload"
                    accept="image/*"
                    :disabled="isSubmitting">
                    <div v-if="form.pictureUrl" class="image-preview">
                      <el-image
                        :src="getActivityImageUrl(form.pictureUrl)"
                        :preview-src-list="[getActivityImageUrl(form.pictureUrl)]"
                        fit="cover"
                        class="uploaded-image"
                      />
                      <div class="image-overlay">
                        <i class="el-icon-zoom-in" @click.stop="previewImage"></i>
                        <i class="el-icon-delete" @click.stop="removeImage"></i>
                      </div>
                    </div>
                    <div v-else class="upload-placeholder">
                      <i class="el-icon-plus"></i>
                      <div class="upload-text">点击上传图片</div>
                      <div class="upload-tip">支持 JPG、PNG 格式，大小不超过 5MB</div>
                    </div>
                  </el-upload>
                </div>
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
      width="50%"
      append-to-body
      class="student-dialog"
      :before-close="handleStudentDialogClose">

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
          @sort-change="handleSortChange">

          <el-table-column label="序号" width="60" align="center">
            <template v-slot="scope">
              <span class="index-badge">
                {{ scope.$index + 1 }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="studentId" label="学号" align="center" min-width="140" sortable>
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

          <el-table-column prop="studentName" label="姓名" align="center" min-width="100" sortable>
            <template slot-scope="{row}">
              <div class="student-name-container">
                <span class="student-name">{{ row.studentName }}</span>
                <el-tag v-if="row.isBooked" type="success" size="mini" effect="plain">已报名</el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="studentClass" label="学生班级" align="center" min-width="120" sortable>
            <template slot-scope="{row}">
              <el-tag size="small" type="primary" effect="plain">
                {{ row.studentClass || '未知' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="academy" label="所属书院" align="center" min-width="120" >
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
        <pagination
          v-show="studentTotal > 0"
          :total="studentTotal"
          :page.sync="studentQueryParams.pageNum"
          :limit.sync="studentQueryParams.pageSize"
          @pagination="handleStudentPagination"
          class="custom-pagination"
        />
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
import {listActivities, getActivities, delActivities, addActivities, updateActivities2, checkActivityUnique} from "@/api/system/activities";
import {getToken} from "@/utils/auth";
import {listBookingsWithActivity} from "@/api/system/bookings";
import {getNickName} from "@/api/system/student";
import { parseTime } from "@/utils/ruoyi";
import { getServerTime } from "@/api/common/time";

export default {
  name: "Activities",
  props: {
    isHomeMode: {
      type: Boolean,
      default: false
    }
  },
  components: {
  },
  data() {
    return {

      // 新增状态
      dialogVisibleStudents: false,
      selectedStudents: [],
      allStudents: [], // 所有学生数据（用于排序）
      studentLoading: false,
      studentTotal: 0,
      currentActivityId: null,
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
      },
      // 服务器时间
      serverTime: null,
      // 图片上传相关
      imagePreviewVisible: false,
      previewImageUrl: '',
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload', // 上传接口地址
      uploadHeaders: {
        'Authorization': 'Bearer ' + getToken()
      },
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
      // 活动状态选项
      activityStatusOptions: [
        { value: '报名未开始', label: '报名未开始' },
        { value: '报名进行中', label: '报名进行中' },
        { value: '报名已截止', label: '报名已截止' },
        { value: '活动进行中', label: '活动进行中' },
        { value: '活动已结束', label: '活动已结束' }
      ],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10, // 恢复合理的分页大小
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
        activityStatus: null,
        createdAt: null,
        organizer: null,
        notes: null,
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
        activityDescription: [
          {
            validator: (rule, value, callback) => {
              if (value && value.length > 100) {
                callback(new Error("活动描述不能超过100字"));
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
    // 学生列表（直接显示当前页的学生）
    filteredStudents() {
      return this.selectedStudents;
    },

    // 检查选中的活动中是否有已结束的活动
    hasEndedActivities() {
      if (!this.ids || this.ids.length === 0) {
        return false;
      }
      return this.activitiesList.some(activity =>
        this.ids.includes(activity.activityId) && this.isActivityEnded(activity)
      );
    }
  },
  created() {
    this.getServerTime();
    this.getList().then(() => {
      // 数据加载完成后再检查路由参数
      this.checkRouteParams();
    });
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

    /** 检查路由参数，处理从首页跳转过来的编辑请求 */
    async checkRouteParams() {
      const { activityId, filterMode } = this.$route.query;
      
      if (activityId && filterMode === 'single') {
        // 查找对应的活动
        const targetActivity = this.activitiesList.find(activity => 
          activity.activityId == activityId
        );
        
        if (targetActivity) {
          // 自动打开编辑弹窗
          await this.handleUpdate(targetActivity);
        } else {
          // 如果列表中没有找到，显示错误信息
          this.$message.error('未找到指定的活动，请刷新页面重试');
        }
        
        // 清除路由参数，避免刷新页面时重复触发
        this.$router.replace({
          path: this.$route.path,
          query: {}
        });
      }
    },

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
      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const now = this.serverTime || new Date();
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

    /** 判断活动是否已结束 */
    isActivityEnded(row) {
      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const now = this.serverTime || new Date();
      const end = new Date(row.endTime);
      return now > end;
    },


    /** 获取表格行的样式类名 */
    getRowClassName({row}) {
      if (this.isActivityEnded(row)) {
        return 'ended-activity-row';
      }
      return '';
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
        '4': 'danger',    // 社会责任与领军意识活动类 - 红色
        '其他': ''        // 默认蓝色
      }
      return map[activityType] || 'info';
    },

    // 按活动开始时间排序
    sortActivitiesByStartTime(activities) {
      return activities.sort((a, b) => {
        const timeA = new Date(a.startTime);
        const timeB = new Date(b.startTime);
        return timeB - timeA; // 从晚到早排序
      });
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
      this.currentActivityId = row.activityId; // 保存当前活动ID
      this.studentQueryParams = {
        pageNum: 1,
        pageSize: 10,
        activityId: row.activityId
      };

      try {
        // 获取所有学生数据（用于排序和统计）
        const allStudentsRes = await listBookingsWithActivity({
          activityId: row.activityId,
          pageNum: 1,
          pageSize: 1000 // 获取所有学生数据
        });

        // 保存所有学生数据
        this.allStudents = allStudentsRes.rows || [];
        this.studentTotal = allStudentsRes.total || 0;

        // 计算统计数据
        this.calculateStudentStats(this.allStudents);

        // 获取当前页数据
        this.getCurrentPageStudents();

        this.dialogVisibleStudents = true;

      } catch (e) {
        // 即使出错也显示弹框，但显示错误信息
        this.selectedStudents = [];
        this.allStudents = [];
        this.studentTotal = 0;
        this.resetStudentStats();
        this.dialogVisibleStudents = true;
        this.$message.error("获取学生预约活动数据失败，请稍后再试");
      } finally {
        this.studentLoading = false;
      }
    },
    /** 计算活动状态 */
    calculateStatus() {
      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const now = (this.serverTime || new Date()).getTime();
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
    /** 查询活动列表 */
    getList() {
      this.loading = true;
      return getNickName().then(nickName => {
        this.queryParams.organizer = nickName.msg; // 更新组织者
        // 🔽 确保在 organizer 更新后调用列表接口
        // 先获取所有活动数据（不分页）
        const allDataParams = { ...this.queryParams, pageNum: 1, pageSize: 10000 };
        return listActivities(allDataParams).then(response => {
          let allActivities = response.rows;

          // 如果有活动状态筛选条件，进行前端筛选
          if (this.queryParams.activityStatus) {
            allActivities = allActivities.filter(activity => {
              const status = this.getActivityStatusText(activity);
              return status === this.queryParams.activityStatus;
            });
          }

          // 按活动开始时间排序（从晚到早）
          allActivities = this.sortActivitiesByStartTime(allActivities);

          // 对筛选后的数据进行分页
          const startIndex = (this.queryParams.pageNum - 1) * this.queryParams.pageSize;
          const endIndex = startIndex + this.queryParams.pageSize;
          const paginatedList = allActivities.slice(startIndex, endIndex);

          this.activitiesList = paginatedList;
          this.total = allActivities.length; // 使用筛选后的总数量
          this.loading = false;
          // 更新可用的活动类型列表
          this.updateAvailableActivityTypes();
        }).catch(error => {
          this.loading = false;
          this.$message.error("获取活动列表失败");
          throw error;
        });
      }).catch(error => {
        // 即使获取组织者名称失败，也尝试获取活动列表
        // 先获取所有活动数据（不分页）
        const allDataParams = { ...this.queryParams, pageNum: 1, pageSize: 10000 };
        return listActivities(allDataParams).then(response => {
          let allActivities = response.rows;

          // 如果有活动状态筛选条件，进行前端筛选
          if (this.queryParams.activityStatus) {
            allActivities = allActivities.filter(activity => {
              const status = this.getActivityStatusText(activity);
              return status === this.queryParams.activityStatus;
            });
          }

          // 按活动开始时间排序（从晚到早）
          allActivities = this.sortActivitiesByStartTime(allActivities);

          // 对筛选后的数据进行分页
          const startIndex = (this.queryParams.pageNum - 1) * this.queryParams.pageSize;
          const endIndex = startIndex + this.queryParams.pageSize;
          const paginatedList = allActivities.slice(startIndex, endIndex);

          this.activitiesList = paginatedList;
          this.total = allActivities.length; // 使用筛选后的总数量
          this.loading = false;
          // 更新可用的活动类型列表
          this.updateAvailableActivityTypes();
        }).catch(listError => {
          this.loading = false;
          this.$message.error("获取活动列表失败");
          throw listError;
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
        pictureUrl: null,
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
      // 手动重置查询参数，确保活动状态被清空
      this.queryParams.activityStatus = null;
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
    // /** 修改按钮操作 */
    // handleUpdate(row) {
    //   this.reset();
    //   const activityId = row.activityId || this.ids
    //   getActivities(activityId).then(response => {
    //     this.form = response.data;
    //     this.open = true;
    //     this.title = "修改活动信息";
    //   });
    // },

    /** 修改按钮操作 */
    async handleUpdate(row) {
      // 检查活动是否已结束
      if (this.isActivityEnded(row)) {
        this.$message.warning('活动已结束，不允许编辑活动信息');
        return;
      }
      
      const activityId = row.activityId || this.ids;
      this.loading = true;
      
      try {
        const response = await getActivities(activityId);
        const activityData = response.data;
        
        // 直接将获取的数据设置到 form
        this.form = activityData;
        
        // 确保活动描述字段有值
        if (!this.form.activityDescription) {
          this.form.activityDescription = '';
        }
        
        this.open = true;
        this.title = "修改活动信息";
        
        // 等待对话框和编辑器完全渲染
        await this.$nextTick();
        
      } catch (error) {
        this.$message.error('获取活动详情失败');
      } finally {
        this.loading = false;
      }
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
        const result = await getNickName();
        this.form.organizer = result.msg;

        // 普通文本内容无需特殊处理

        // 3. 验证表单
        const valid = await new Promise((resolve) => {
          this.$refs.form.validate(resolve);
        });

        if (!valid) {
          this.isSubmitting = false;
          return; // 验证不通过则停止
        }

        // 4. 计算活动状态
        this.calculateStatus();


        // 5. 根据情况执行新增/修改
        if (this.form.activityId != null) {
          await updateActivities2(this.form);
          this.$message.success("修改成功");
        } else {
          await addActivities(this.form);
          this.$message.success("新增成功");
        }

        // 6. 关闭弹窗并刷新列表
        this.open = false;
        await this.getList();
      } catch (error) {
        if (error.message && error.message.includes("活动名称和组织单位组合已存在")) {
          this.$message.error("活动名称和组织单位组合已存在，不能重复添加！");
        } else {
          this.$message.error(`操作失败: ${error.message || '未知错误'}`);
        }
      } finally {
        this.isSubmitting = false;
      }
    },

// 处理富文本中的图片，将Base64图片上传到服务器并替换为URL


    /** 删除按钮操作 */
    handleDelete(row) {
      const activityIds = row.activityId || this.ids;
      const activityNames = row.activityName || this.names;

      // 检查是否有已结束的活动
      let endedActivities = [];
      if (row && this.isActivityEnded(row)) {
        endedActivities.push(row.activityName);
      } else if (this.ids && this.ids.length > 0) {
        // 批量删除时，找出所有已结束的活动
        endedActivities = this.activitiesList
          .filter(activity =>
            this.ids.includes(activity.activityId) && this.isActivityEnded(activity)
          )
          .map(activity => activity.activityName);
      }

      // 如果有已结束的活动，显示详细的提示信息
      if (endedActivities.length > 0) {
        let message = "以下活动已结束，无法删除：\n";
        endedActivities.forEach(name => {
          message += "• " + name + "\n";
        });
        message += "\n请取消选择已结束的活动后重试。";
        this.$message.warning(message);
        return;
      }

      // 构建更详细的确认信息
      let confirmMessage = '是否确认删除活动名称为"' + activityNames + '"的数据项？\n\n';
      confirmMessage += '⚠️ 注意：删除活动将同时删除该活动的所有学生报名记录！\n';
      confirmMessage += '此操作不可撤销，请谨慎操作。';

      this.$modal.confirm(confirmMessage).then(function () {
        return delActivities(activityIds);
      }).then(() => {
        this.getList();
        this.$message.success("删除成功，相关学生报名记录已一并删除");
      }).catch((error) => {
        // 处理后端返回的错误信息
        if (error && error.message && error.message.includes("已结束")) {
          this.$message.error(error.message);
        }
      });
    },

    /** 更新可用的活动类型列表 */
    updateAvailableActivityTypes() {
      // 使用预定义的活动类型
      this.availableActivityTypes = ['1', '2', '3', '4'];
    },

    /** 截断文本显示 */
    truncateText(text, maxLength) {
      if (!text) return '';
      if (text.length <= maxLength) return text;
      return text.substring(0, maxLength) + '...';
    },

    // ========== 学生列表相关方法 ==========

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


    /** 复制到剪贴板 */
    copyToClipboard(text) {
      navigator.clipboard.writeText(text).then(() => {
        this.$message.success('已复制到剪贴板');
      }).catch(() => {
        this.$message.error('复制失败');
      });
    },

    /** 学生对话框关闭处理 */
    handleStudentDialogClose(done) {
      done();
    },

    /** 获取学生列表（分页） */
    async getStudentList() {
      if (!this.currentActivityId) return;

      this.studentLoading = true;
      try {
        const res = await listBookingsWithActivity(this.studentQueryParams);
        this.selectedStudents = res.rows || [];
        this.studentTotal = res.total || 0;
      } catch (e) {
        this.selectedStudents = [];
        this.studentTotal = 0;
        this.$message.error("获取学生列表失败");
      } finally {
        this.studentLoading = false;
      }
    },

    /** 获取当前页学生数据（从所有学生数据中分页） */
    getCurrentPageStudents() {
      if (!this.allStudents || this.allStudents.length === 0) {
        this.selectedStudents = [];
        return;
      }

      const startIndex = (this.studentQueryParams.pageNum - 1) * this.studentQueryParams.pageSize;
      const endIndex = startIndex + this.studentQueryParams.pageSize;
      this.selectedStudents = this.allStudents.slice(startIndex, endIndex);
    },

    /** 学生列表分页处理 */
    handleStudentPagination(pagination) {
      this.studentQueryParams.pageNum = pagination.page;
      this.studentQueryParams.pageSize = pagination.limit;
      this.getCurrentPageStudents();
    },

    /** 计算学生统计数据 */
    calculateStudentStats(allStudents) {
      this.studentStats.total = allStudents.length;

      // 计算各状态人数
      this.studentStats.pending = allStudents.filter(student =>
        student.status === 'pending' || student.status === '未审核'
      ).length;
      this.studentStats.approved = allStudents.filter(student =>
        student.status === 'approved' || student.status === '已通过'
      ).length;
      this.studentStats.rejected = allStudents.filter(student =>
        student.status === 'rejected' || student.status === '未通过'
      ).length;

      // 已提交 = 未审核 + 已通过 + 未通过
      this.studentStats.submitted = this.studentStats.pending + this.studentStats.approved + this.studentStats.rejected;
    },

    /** 重置学生统计数据 */
    resetStudentStats() {
      this.studentStats = {
        total: 0,
        submitted: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      };
    },


    /** 处理表格排序变化 */
    handleSortChange({ column, prop, order }) {
      if (!this.allStudents || this.allStudents.length === 0) return;

      // 根据排序字段和顺序对所有数据进行排序
      let sortedStudents = [...this.allStudents];

      if (order === 'ascending') {
        // 升序排序
        if (prop === 'studentId') {
          sortedStudents.sort((a, b) => {
            const idA = parseInt(a.studentId) || 0;
            const idB = parseInt(b.studentId) || 0;
            return idA - idB;
          });
        } else if (prop === 'studentName') {
          sortedStudents.sort((a, b) => {
            const nameA = a.studentName || '';
            const nameB = b.studentName || '';
            return nameA.localeCompare(nameB, 'zh-CN');
          });
        } else if (prop === 'studentClass') {
          sortedStudents.sort((a, b) => {
            const classA = a.studentClass || '未知';
            const classB = b.studentClass || '未知';
            return classA.localeCompare(classB, 'zh-CN');
          });
        }
      } else if (order === 'descending') {
        // 降序排序
        if (prop === 'studentId') {
          sortedStudents.sort((a, b) => {
            const idA = parseInt(a.studentId) || 0;
            const idB = parseInt(b.studentId) || 0;
            return idB - idA;
          });
        } else if (prop === 'studentName') {
          sortedStudents.sort((a, b) => {
            const nameA = a.studentName || '';
            const nameB = b.studentName || '';
            return nameB.localeCompare(nameA, 'zh-CN');
          });
        } else if (prop === 'studentClass') {
          sortedStudents.sort((a, b) => {
            const classA = a.studentClass || '未知';
            const classB = b.studentClass || '未知';
            return classB.localeCompare(classA, 'zh-CN');
          });
        }
      }

      // 更新所有学生数据
      this.allStudents = sortedStudents;

      // 重置到第一页
      this.studentQueryParams.pageNum = 1;

      // 获取当前页数据
      this.getCurrentPageStudents();
    },

    // ========== 图片上传相关方法 ==========

    /** 图片上传前验证 */
    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isImage) {
        this.$message.error('只能上传图片文件!');
        return false;
      }
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!');
        return false;
      }
      return true;
    },

    /** 图片上传成功回调 */
    handleImageSuccess(response, file) {

      if (response.code === 200 && response.url) {
        // 提取相对路径部分（如：/profile/upload/...）
        const relativePath = response.fileName || response.url.replace(/^https?:\/\/[^\/]+/, '');

        // 使用Vue.set确保响应式更新，存储相对路径
        this.$set(this.form, 'pictureUrl', relativePath);
        this.$message.success('图片上传成功');

        // 强制更新视图
        this.$forceUpdate();

        // 测试URL处理
      } else {
        this.$message.error(response.msg || '图片上传失败');
      }
    },

    /** 图片上传失败回调 */
    handleImageError(error) {
      this.$message.error('图片上传失败，请重试');
    },

    /** 图片加载成功处理 */
    handleImageLoadSuccess(event) {
    },

    /** 图片开始加载处理 */
    handleImageLoadStart(event) {
    },

    /** 图片加载中断处理 */
    handleImageAbort(event) {
    },

    /** 图片加载错误处理 */
    handleImageLoadError(event) {

      // 尝试获取更多错误信息
      const img = event.target;
      if (img) {

        // 检查是否是网络问题
        if (img.error) {
        }
      }

      // 尝试直接访问图片URL来测试
      this.testImageUrl(event.target.src);

      this.$message.error('图片加载失败，请检查图片URL或网络连接');
    },

    /** 测试图片URL是否可访问 */
    testImageUrl(url) {

      // 创建一个新的图片元素来测试
      const testImg = new Image();
      testImg.onload = () => {
      };
      testImg.onerror = (error) => {

        // 尝试使用fetch测试
        this.testImageWithFetch(url);
      };
      testImg.src = url;
    },

    /** 使用fetch测试图片URL */
    async testImageWithFetch(url) {
      try {
        const response = await fetch(url, { method: 'HEAD' });

        if (response.ok) {
        } else {
        }
      } catch (error) {
      }
    },

    /** 预览图片 */
    previewImage() {
      this.previewImageUrl = this.getActivityImageUrl(this.form.pictureUrl);
      this.imagePreviewVisible = true;
    },

    /** 删除图片 */
    removeImage() {
      this.$confirm('确定要删除这张图片吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$set(this.form, 'pictureUrl', null);
        this.$message.success('图片已删除');
      }).catch(() => {
        // 用户取消删除
      });
    },

    /** 预览活动图片 */
    previewActivityImage(imageUrl) {
      this.previewImageUrl = imageUrl;
      this.imagePreviewVisible = true;
    },

    /** 获取活动图片完整URL */
    getActivityImageUrl(pictureUrl) {

      if (!pictureUrl) {
        return '';
      }

      // 如果已经是完整URL，直接返回
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        return pictureUrl;
      }

      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径
      if (pictureUrl.startsWith('/profile/')) {
        const fullUrl = `${process.env.VUE_APP_BASE_API}${pictureUrl}`;
        return fullUrl;
      }

      // 其他情况直接返回
      return pictureUrl;
    },

    /** 获取活动图片完整URL（处理中文编码问题） */
    getActivityImageUrlFixed(pictureUrl) {
      if (!pictureUrl) return '';

      // 如果已经是完整URL，直接返回
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        return pictureUrl;
      }

      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径
      if (pictureUrl.startsWith('/profile/')) {
        const fullUrl = `${process.env.VUE_APP_BASE_API}${pictureUrl}`;
        return this.getActivityImageUrlFixed(fullUrl);
      }

      return pictureUrl;
    },

    /** 获取活动图片完整URL（尝试编码版本） */
    getActivityImageUrlEncoded(pictureUrl) {
      if (!pictureUrl) return '';

      // 如果已经是完整URL，尝试编码处理
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        try {
          // 解析URL
          const url = new URL(pictureUrl);
          // 重新构建URL，确保路径部分正确编码
          const encodedPath = url.pathname.split('/').map(segment => {
            // 对每个路径段进行编码，但保持已编码的部分不变
            return encodeURIComponent(decodeURIComponent(segment));
          }).join('/');

          const encodedUrl = `${url.protocol}//${url.host}${encodedPath}`;
          return encodedUrl;
        } catch (error) {
          return pictureUrl;
        }
      }

      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径
      if (pictureUrl.startsWith('/profile/')) {
        const fullUrl = `${process.env.VUE_APP_BASE_API}${pictureUrl}`;
        return this.getActivityImageUrlEncoded(fullUrl);
      }

      return pictureUrl;
    },

    /** 智能获取活动图片URL（仿照审核界面实现） */
    getActivityImageUrlSmart(pictureUrl) {
      if (!pictureUrl) return '';

      // 如果已经是完整URL，直接返回
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        return pictureUrl;
      }

      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径（仿照审核界面）
      if (pictureUrl.startsWith('/profile/')) {
        const fullUrl = `${process.env.VUE_APP_BASE_API}${pictureUrl}`;
        return fullUrl;
      }

      return pictureUrl;
    },

  },
  watch: {
    'form.activityTotalCapacity'(newVal, oldVal) {
      if (!this.form.activityId) {
        // 新增活动模式：剩余容量 = 总容量
        this.form.activityCapacity = newVal;
      } else {
        // 编辑活动模式：需要重新计算剩余容量
        if (oldVal && newVal && this.form.activityCapacity !== null) {
          // 计算已选人数（基于旧的总容量）
          const selectedCount = oldVal - this.form.activityCapacity;
          
          // 计算新的剩余容量
          const newRemainingCapacity = newVal - selectedCount;
          
          // 确保剩余容量不为负数
          if (newRemainingCapacity >= 0) {
            this.form.activityCapacity = newRemainingCapacity;
          } else {
            // 如果新总容量小于已选人数，剩余容量设为0
            this.form.activityCapacity = 0;
            this.$message.warning(`新总容量(${newVal})小于已选人数(${selectedCount})，剩余容量已设为0`);
          }
        }
      }
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
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #64b5ff);
  color: white;
  font-weight: 600;
  font-size: 12px;
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.3);
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
  cursor: help;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.activity-name:hover {
  color: #409EFF;
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
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px 8px 0 0;

  .stats-card {
    display: flex;
    justify-content: space-around;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 8px;
    padding: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .stat-item {
      text-align: center;

      .stat-number {
        font-size: 20px;
        font-weight: 700;
        color: #409EFF;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 12px;
        color: #606266;
        font-weight: 500;
      }
    }
  }
}


/* 学生表格容器 */
.student-table-container {
  margin: 8px 12px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

/* 增强的学生表格 */
.enhanced-student-table {
  width: 100% !important;

  .el-table {
    width: 100% !important;
  }

  .el-table td {
    padding: 8px 12px !important;
  }

  .el-table th {
    padding: 8px 12px !important;
  }

  .student-id-container {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;

    .student-id {
      font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
      font-size: 14px;
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
    gap: 6px;

    .student-name {
      font-weight: 600;
      color: #303133;
      font-size: 13px;
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

/* 已结束活动行样式 */
.ended-activity-row {
  background-color: #f5f5f5 !important;
  color: #999 !important;
  opacity: 0.6;
}

.ended-activity-row:hover {
  background-color: #f0f0f0 !important;
}

.ended-activity-row td {
  color: #999 !important;
}

/* 已结束活动的选择框样式 - 保持正常可点击状态 */
.ended-activity-row .el-checkbox {
  opacity: 1;
  cursor: pointer;
}

.ended-activity-row .el-checkbox__input {
  cursor: pointer;
}

/* 学生分页样式 */
.student-pagination {
  padding: 8px 12px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.student-pagination .custom-pagination {
  margin: 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .custom-pagination {
    padding: 8px;
    justify-content: center;
  }

  .student-pagination {
    padding: 12px 16px;
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

/* 活动图片展示 */
.activity-image-container {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.activity-image {
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

/* 响应式调整 */
@media (max-width: 768px) {
  .upload-placeholder {
    height: 150px;

    i {
      font-size: 36px;
    }

    .upload-text {
      font-size: 14px;
    }

    .upload-tip {
      font-size: 11px;
    }
  }

  .image-preview {
    height: 150px;
  }

  .activity-image {
    max-width: 150px;
    max-height: 100px;
  }

  .image-preview-dialog {
    .el-dialog {
      width: 95% !important;
      margin: 0 auto;
    }
  }

}
</style>
