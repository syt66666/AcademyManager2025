<template>

  <div class="app-container">
    <div class="search-card">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <div class="card-header">
          <i class="el-icon-search"></i>
          <span>搜索条件</span>
        </div>
        <el-form-item label="课程名称" prop="courseName">
          <el-input
            v-model="queryParams.courseName"
            placeholder="请输入课程名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="课程状态" prop="status">
          <el-select v-model="queryParams.status" clearable placeholder="请选择课程状态" class="search-input" @change="handleQuery">
            <el-option
              v-for="status in courseStatusOptions"
              :key="status.value"
              :label="status.label"
              :value="status.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 表格卡片 -->
    <div class="table-card">
      <div class="card-header">
        <i class="el-icon-s-grid"></i>
        <span>课程列表</span>
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
          >导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="coursesList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" width="80" align="center">
          <template v-slot="scope">
          <span class="index-badge">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </span>
          </template>
        </el-table-column>
        <el-table-column label="课程名称" align="center" prop="courseName">
          <template slot-scope="scope">
            <span class="course-name">{{ scope.row.courseName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="课程类型" align="center" prop="courseType" width="200">
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
        <el-table-column label="课程地点" align="center" prop="courseLocation" width="150">
          <template slot-scope="scope">
            <span class="location-text">{{ scope.row.courseLocation || '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="选课开始时间" align="center" prop="courseStart" width="200">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.courseStart, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="选课截止时间" align="center" prop="courseDeadline" width="200">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.courseDeadline, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <!-- 课程状态列 -->
        <el-table-column label="课程状态" align="center" prop="status" width="100">
          <template slot-scope="scope">
            <!-- 1. 绑定标签内容：显示当前课程的状态文本（scope.row.status） -->
            <!-- 2. 绑定标签类型：通过计算属性将业务状态映射为el-tag支持的类型 -->
            <el-tag
              :type="getStatusTagType(scope.row.status)"
              effect="dark"
              class="status-tag"
            >
              {{ scope.row.status }} <!-- 关键：渲染状态文本 -->
            </el-tag>
          </template>
        </el-table-column>
        <!-- 报名人数列：放在“课程状态”列之后 -->
        <el-table-column label="报名人数" align="center" width="160"> <!-- 宽度调整为160px，适配进度条 -->
          <template #default="scope">
            <!-- 容器：垂直排列进度条和人数文本 -->
            <div class="participants">
              <!-- 进度条：展示报名占比 -->
              <el-progress
                :percentage="calculateCapacityPercentage(scope.row)"
                :color="getProgressColor(calculateCapacityPercentage(scope.row))"
                :show-text="false"
                :stroke-width="8"
                class="progress-bar"
              ></el-progress>
              <!-- 人数文本：显示“已报名/总容量” -->
              <div class="count">
        <span :class="getCapacityClass(scope.row)">
          {{ scope.row.courseTotalCapacity - scope.row.courseCapacity }}/{{ scope.row.courseTotalCapacity }}
        </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />

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
                <el-form-item label="课程名称" prop="courseName">
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

                <el-form-item label="课程类型" prop="courseType">
                  <el-select v-model="form.courseType" placeholder="请选择课程类型" class="form-select">
                    <el-option
                      v-for="type in courseTypeOptions"
                      :key="type.value"
                      :label="type.label"
                      :value="type.value">
                      <span style="float: left">{{ type.label }}</span>
                      <span style="float: right; color: #8492a6; font-size: 13px">{{ type.value }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>

                <el-form-item label="课程地点" prop="courseLocation">
                  <el-input
                    v-model="form.courseLocation"
                    placeholder="请输入课程地点"
                    prefix-icon="el-icon-location"
                    class="form-input">
                  </el-input>
                </el-form-item>

                <div class="form-row">
                  <el-form-item label="课程学分" prop="courseCredit" class="form-item-half">
                    <el-input-number
                      v-model="form.courseCredit"
                      :min="0"
                      :max="10"
                      :precision="1"
                      :step="0.5"
                      controls-position="right"
                      class="form-number">
                    </el-input-number>
                    <span class="credit-tip">学分</span>
                  </el-form-item>

                  <el-form-item label="课程容量" prop="courseTotalCapacity" class="form-item-half">
                    <el-input-number
                      v-model="form.courseTotalCapacity"
                      :min="1"
                      :max="1000"
                      controls-position="right"
                      class="form-number"
                      @change="handleCapacityChange">
                    </el-input-number>
                    <span class="capacity-tip">人</span>
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
                <div class="time-grid">
                  <el-form-item label="选课开始" prop="courseStart" class="time-item">
                    <el-date-picker
                      v-model="form.courseStart"
                      type="datetime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      placeholder="选择选课开始时间"
                      class="form-datetime">
                    </el-date-picker>
                  </el-form-item>

                  <el-form-item label="选课截止" prop="courseDeadline" class="time-item">
                    <el-date-picker
                      v-model="form.courseDeadline"
                      type="datetime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      placeholder="选择选课截止时间"
                      class="form-datetime">
                    </el-date-picker>
                  </el-form-item>

                  <el-form-item label="课程开始" prop="startTime" class="time-item">
                    <el-date-picker
                      v-model="form.startTime"
                      type="datetime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      placeholder="选择课程开始时间"
                      class="form-datetime">
                    </el-date-picker>
                  </el-form-item>

                  <el-form-item label="课程结束" prop="endTime" class="time-item">
                    <el-date-picker
                      v-model="form.endTime"
                      type="datetime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      placeholder="选择课程结束时间"
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
                <el-form-item label="课程描述" prop="courseDescription">
                  <el-input
                    type="textarea"
                    v-model="form.courseDescription"
                    :rows="4"
                    placeholder="请输入课程描述"
                    class="form-textarea"
                  />
                </el-form-item>

                <el-form-item label="组织者" prop="organizer">
                  <el-input
                    v-model="form.organizer"
                    placeholder="请输入组织者"
                    prefix-icon="el-icon-user"
                    class="form-input">
                  </el-input>
                </el-form-item>

                <el-form-item label="注意事项" prop="notes">
                  <el-input
                    type="textarea"
                    v-model="form.notes"
                    :rows="3"
                    placeholder="请输入注意事项"
                    class="form-textarea"
                  />
                </el-form-item>

                <el-form-item label="课程图片" prop="pictureUrl">
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
                          :src="getCourseImageUrl(form.pictureUrl)"
                          :preview-src-list="[getCourseImageUrl(form.pictureUrl)]"
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
                        <div class="upload-tip">支持 JPG、PNG 格式，大小不超过 2MB</div>
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
              class="submit-btn">
              {{ form.courseId ? '保存修改' : '创建课程' }}
            </el-button>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { listCourses, getCourses, delCourses, addCourses, updateCourses } from "@/api/system/courses";
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
        courseName: null,
        courseLocation: null,
        courseType: null,
        organizer: null,

      },
      // 课程状态选项
      courseStatusOptions: [
        { value: '未开始', label: '未开始' },
        { value: '选课中', label: '选课中' },
        { value: '已截止', label: '已截止' }
      ],
      // 课程类型选项
      courseTypeOptions: [
        { value: '1', label: '人格塑造与价值引领活动类' },
        { value: '2', label: '知识融合与思维进阶活动类' },
        { value: '3', label: '能力锻造与实践创新活动类' },
        { value: '4', label: '社会责任与领军意识活动类' }
      ],
      // 表单提交状态
      isSubmitting: false,
      // 图片上传相关
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload',
      uploadHeaders: {
        Authorization: "Bearer " + this.$store.getters.token
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        courseName: [
          { required: true, message: "课程名称不能为空", trigger: "blur" }
        ],
        courseLocation: [
          { required: true, message: "课程地点不能为空", trigger: "blur" }
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
          { required: true, message: "组织者不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 序号计算（跨页连续）
    indexMethod(index) {
      return (this.queryParams.pageNum - 1) * this.queryParams.pageSize + index + 1;
    },
    /** 查询书院选课列表 */
    getList() {
      this.loading = true;
      listCourses(this.queryParams).then(response => {
        const rows = response.rows || [];
        // 若后端未按状态过滤，则在前端按需过滤
        const selected = (this.queryParams.status || '').trim();
        const withStatus = rows.map(item => ({
          ...item,
          status: item.status || this.computeCourseStatus(item)
        }));
        this.coursesList = selected ? withStatus.filter(r => r.status === selected) : withStatus;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 兜底：根据时间推导课程状态
    computeCourseStatus(item) {
      const now = new Date();
      const start = item.courseStart ? new Date(item.courseStart) : null;
      const deadline = item.courseDeadline ? new Date(item.courseDeadline) : null;
      if (start && now < start) return '未开始';
      if (start && deadline && now >= start && now <= deadline) return '选课中';
      if (deadline && now > deadline) return '已截止';
      return item.status || '未开始';
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
        organizer: null,
        notes: null,
        version: null
      };
      this.resetForm("form");
    },
    // 状态与标签类型的映射：返回el-tag支持的type值
    getStatusTagType(status) {
      switch (status) {
        case '未开始':
          return 'info'; // 蓝色标签
        case '选课中':
          return 'success'; // 绿色标签
        case '已截止':
          return 'danger'; // 红色标签
        default:
          return 'warning'; // 橙色标签（默认，应对异常状态）
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
      // 计算已报名人数 = 总容量 - 剩余容量
      const enrolled = row.courseTotalCapacity - row.courseCapacity;
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
        '1': '人格塑造与价值引领活动类',
        '2': '知识融合与思维进阶活动类',
        '3': '能力锻造与实践创新活动类',
        '4': '社会责任与领军意识活动类'
      };
      return typeMap[coursetype] || coursetype;
    },

    getCourseTypeTagType(coursetype) {
      const map = {
        '1': 'primary',   // 人格塑造与价值引领活动类 - 蓝色
        '2': 'success',   // 知识融合与思维进阶活动类 - 绿色
        '3': 'warning',   // 能力锻造与实践创新活动类 - 橙色
        '4': 'danger',    // 社会责任与领军意识活动类 - 红色
        '其他': ''        // 默认蓝色
      }
      return map[coursetype] || 'info';
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
      this.ids = selection.map(item => item.courseId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加书院选课";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const courseId = row.courseId || this.ids
      getCourses(courseId).then(response => {
        this.form = response.data;
        // 保持原有的已报名人数，不重置剩余容量
        // 如果剩余容量为空或无效，则根据已报名人数计算
        if (!this.form.courseCapacity || this.form.courseCapacity < 0) {
          // 如果剩余容量无效，假设没有已报名人数
          this.form.courseCapacity = this.form.courseTotalCapacity || 0;
        }
        this.open = true;
        this.title = "修改书院选课";
      });
    },
    /** 提交按钮 */
    submitForm() {
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
      this.$modal.confirm('是否确认删除书院选课编号为"' + courseIds + '"的数据项？').then(function() {
        return delCourses(courseIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
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
    /** 图片上传成功 */
    handleImageSuccess(response, file) {
      if (response.code === 200) {
        this.form.pictureUrl = response.url;
        this.$message.success('图片上传成功');
      } else {
        this.$message.error(response.msg || '图片上传失败');
      }
    },
    /** 图片上传失败 */
    handleImageError(error, file) {
      this.$message.error('图片上传失败，请重试');
    },
    /** 图片上传前验证 */
    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error('只能上传图片文件!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!');
        return false;
      }
      return true;
    },
    /** 获取课程图片URL */
    getCourseImageUrl(url) {
      if (!url) return '';
      if (url.startsWith('http')) return url;
      return process.env.VUE_APP_BASE_API + url;
    },
    /** 预览图片 */
    previewImage() {
      // 图片预览功能由 el-image 组件自动处理
    },
    /** 删除图片 */
    removeImage() {
      this.form.pictureUrl = '';
      this.$message.success('图片已删除');
    },
    /** 处理容量变化 */
    handleCapacityChange(value) {
      // 当总容量变化时，根据当前已报名人数计算剩余容量
      if (value && value > 0) {
        // 计算当前已报名人数
        const currentEnrolled = this.form.courseTotalCapacity && this.form.courseCapacity 
          ? this.form.courseTotalCapacity - this.form.courseCapacity 
          : 0;
        
        // 新的剩余容量 = 新总容量 - 当前已报名人数
        const newRemainingCapacity = value - currentEnrolled;
        
        // 确保剩余容量不为负数
        this.form.courseCapacity = Math.max(0, newRemainingCapacity);
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
  .course-form-dialog {
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

      .credit-tip,
      .capacity-tip {
        margin-left: 8px;
        color: #909399;
        font-size: 12px;
      }

      .time-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px;

        .time-item {
          margin-bottom: 0;
        }
      }
    }
  }
}

/* 图片上传容器 */
.image-upload-container {
  .image-uploader {
    width: 100%;

    .image-preview {
      position: relative;
      width: 200px;
      height: 120px;
      border-radius: 8px;
      overflow: hidden;
      border: 2px dashed #d9d9d9;

      .uploaded-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
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
        opacity: 0;
        transition: opacity 0.3s;

        i {
          color: white;
          font-size: 20px;
          margin: 0 8px;
          cursor: pointer;
          padding: 8px;
          border-radius: 4px;
          background: rgba(255, 255, 255, 0.2);

          &:hover {
            background: rgba(255, 255, 255, 0.3);
          }
        }
      }

      &:hover .image-overlay {
        opacity: 1;
      }
    }

    .upload-placeholder {
      width: 200px;
      height: 120px;
      border: 2px dashed #d9d9d9;
      border-radius: 8px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: border-color 0.3s;

      &:hover {
        border-color: #409EFF;
      }

      i {
        font-size: 32px;
        color: #c0c4cc;
        margin-bottom: 8px;
      }

      .upload-text {
        font-size: 14px;
        color: #606266;
        margin-bottom: 4px;
      }

      .upload-tip {
        font-size: 12px;
        color: #909399;
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

  .course-form .form-section .section-content .time-grid {
    grid-template-columns: 1fr;
  }

  .course-form .form-section .section-content .form-row {
    flex-direction: column;
    gap: 0;
  }
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

  .course-image {
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
