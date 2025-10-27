<template>
  <div class="app-container">

    <!-- 搜索区域（保留原有功能） -->
    <div class="search-card">
      <div class="card-header">
        <i class="el-icon-search"></i>
        <span>搜索条件</span>
        <div class="academy-info" v-if="currentAcademy && currentAcademy !== '未知'">
          <i class="el-icon-school"></i>
          <span>当前书院：{{ currentAcademy }}</span>
        </div>
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
          <el-form-item label="课程类型" prop="courseType">
            <el-select
              v-model="queryParams.courseType"
              placeholder="请选择课程类型"
              clearable
              class="search-select"
              @change="handleQuery"
            >
              <el-option label="人格塑造与价值引领课程类" value="1" />
              <el-option label="知识融合与思维进阶课程类" value="2" />
              <el-option label="能力锻造与实践创新课程类" value="3" />
              <el-option label="社会责任与领军意识课程类" value="4" />
            </el-select>
          </el-form-item>
          <el-form-item prop="availableOnly">
            <el-checkbox v-model="queryParams.availableOnly" @change="handleQuery">
              只显示可选课课程
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


    <!-- 课程列表 -->
    <div class="table-card">
      <div class="card-header">
        <i class="el-icon-tickets"></i>
        <span>课程列表</span>
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>
      <!-- 课程表格（核心修改：缩小课程名称列宽度） -->
      <el-table
        v-loading="loading"
        :data="coursesList"
        style="width: 100%"
        class="modern-table"
        :header-cell-style="{backgroundColor: '#f8fafc', color: '#303133'}"
        :row-class-name="getRowClassName"
      >
        <!-- 多选框列 -->
        <el-table-column type="selection" width="55" align="center" />
        <!-- 序号列 -->
        <el-table-column label="序号" align="center" width="80">
          <template slot-scope="scope">
            <span class="index-badge">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </span>
          </template>
        </el-table-column>
        <!-- 课程名称列 -->
        <el-table-column label="课程名称" align="center" prop="courseName" show-overflow-tooltip />
        <el-table-column label="课程性质" align="center" width="120">
          <template slot-scope="scope">
            <el-tag :type="getCourseCategoryTagType(scope.row.courseCategory)" effect="plain" class="category-tag">
              {{ getCourseCategoryName(scope.row.courseCategory) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 课程类型列 -->
        <el-table-column label="课程类型" align="center" prop="courseType" width="200">
          <template slot-scope="scope">
            <el-tag :type="getCourseTypeTagType(scope.row.courseType)" effect="plain" class="course-type-tag">
              {{ getCourseTypeName(scope.row.courseType) || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 学分列 -->
        <el-table-column label="学分" align="center" width="80">
          <template slot-scope="scope">
            <span class="credit-value">{{ scope.row.courseCredit || 0 }}</span>
          </template>
        </el-table-column>
        <!-- 上课地点列 -->
        <el-table-column label="上课地点" align="center" prop="courseLocation" show-overflow-tooltip />
        <!-- 组织单位列 -->

        <!-- 时间安排列 -->
        <el-table-column label="时间安排" align="center" min-width="250">
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
        <el-table-column label="课程状态" align="center" prop="status" >
          <template slot-scope="scope">
            <el-tag :type="getCourseStatusTagType(scope.row)" effect="dark" class="status-tag">
              {{ getCourseStatusText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 选课人数列 -->
        <el-table-column label="选课人数" align="center" width="120">
          <template slot-scope="scope">
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


        <!-- 操作列（保留原有功能） -->
        <el-table-column label="操作" align="center" fixed="right" width="120">
          <template slot-scope="scope">
            <el-button
              v-if="getSignStatusText(scope.row) === '可选课'"
              type="text"
              size="mini"
              class="action-button signup-button"
              @click="handleDetail(scope.row)"
            >选课</el-button>
            <el-button
              v-else-if="getSignStatusText(scope.row) === '未提交'"
              type="text"
              size="mini"
              class="action-button cancel-button"
              @click="handleDetail(scope.row)"
            >取消选课</el-button>
            <el-button
              v-else-if="isCourseFull(scope.row)"
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
            >不可选课</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件（保留原有） -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
        class="custom-pagination"
      />
    </div>



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


    <!-- 课程详情弹窗 -->
    <el-dialog
      title="课程详情"
      :visible.sync="detailDialogVisible"
      width="800px"
      :before-close="handleDetailClose"
      class="course-detail-dialog"
    >
      <div class="course-detail" v-if="selectedCourse">
        <!-- 课程详情展示 -->
        <div class="detail-header">
          <h2>{{ selectedCourse.courseName }}</h2>
          <div class="status-tags">
            <el-tag :type="getCourseStatusTagType(selectedCourse)" size="medium" class="status-tag">
              {{ getCourseStatusText(selectedCourse) }}
            </el-tag>

          </div>
        </div>
        <el-divider></el-divider>
        <div class="detail-grid">
          <!-- 基本信息 -->
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-location"></i> 上课地点：</div>
            <div class="detail-value">{{ selectedCourse.courseLocation }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-collection-tag"></i> 课程类型：</div>
            <div class="detail-value">
              <el-tag :type="getCourseTypeTagType(selectedCourse.courseType)" effect="plain" class="course-type-tag">
                {{ getCourseTypeName(selectedCourse.courseType) }}
              </el-tag>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-office-building"></i> 组织单位：</div>
            <div class="detail-value">{{ selectedCourse.organizer }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-user"></i> 课程容量：</div>
            <div class="detail-value">
            <span :class="getCapacityClass(selectedCourse)">
              {{ selectedCourse.courseCapacity || 0 }}/{{ selectedCourse.courseTotalCapacity }}人
            </span>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-coin"></i> 课程学分：</div>
            <div class="detail-value">
              <span class="credit-value">{{ selectedCourse.courseCredit || 0 }}学分</span>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-collection-tag"></i> 课程性质：</div>
            <div class="detail-value">
              <el-tag :type="getCourseCategoryTagType(selectedCourse.courseCategory)" effect="plain" class="category-tag">
                {{ getCourseCategoryName(selectedCourse.courseCategory) || '未分类' }}
              </el-tag>
            </div>
          </div>
          <!-- 课程时间 -->
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-time"></i> 选课开始：</div>
            <div class="detail-value">{{ formatDateTime(selectedCourse.courseStart) }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-time"></i> 选课结束：</div>
            <div class="detail-value">{{ formatDateTime(selectedCourse.courseDeadline) }}</div>
          </div>
          <!-- 选课时间 -->
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-alarm-clock"></i> 课程开始：</div>
            <div class="detail-value">{{ formatDateTime(selectedCourse.startTime) }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label"><i class="el-icon-alarm-clock"></i> 课程结束：</div>
            <div class="detail-value">{{ formatDateTime(selectedCourse.endTime) }}</div>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="detail-section-content">
          <h4 class="section-title"><i class="el-icon-document"></i> 课程描述</h4>
          <div class="section-content">
            <!-- 使用 v-html 渲染富文本内容 -->
            <div class="rich-text-content" v-html="selectedCourse.courseDescription || '暂无描述信息'"></div>
          </div>
        </div>

        <!-- 课程图片展示 -->
        <div class="detail-section-content" v-if="selectedCourse.pictureUrl">
          <h4 class="section-title"><i class="el-icon-picture"></i> 课程图片</h4>
          <div class="section-content">
            <div class="course-image-container">
              <el-image
                :src="getCourseImageUrl(selectedCourse.pictureUrl)"
                :preview-src-list="[getCourseImageUrl(selectedCourse.pictureUrl)]"
                fit="cover"
                class="course-image"
              />
            </div>
          </div>
        </div>
        <!-- 选课/取消按钮 -->
        <div class="signup-status">
          <el-button
            type="primary"
            :disabled="!showSignUpButton"
            @click="handleSignUp(selectedCourse)"
            v-if="showSignUpButton"
            class="signup-button"
          >
            立即选课
          </el-button>
          <el-button
            type="danger"
            :disabled="!showCancelButton"
            @click="handleCancel(selectedCourse)"
            v-if="showCancelButton"
            class="cancel-button"
          >
            取消选课
          </el-button>

          <el-alert
            v-if="showFullCapacityAlert"
            title="选课已满"
            type="warning"
            :closable="false"
            class="signup-alert"
          >
            该课程选课人数已满，无法继续选课
          </el-alert>
          <el-alert
            v-if="selectedCourse.isBooked && !showCancelButton"
            title="您已成功选课该课程"
            type="success"
            :closable="false"
            class="signup-alert"
          >
            选课信息已提交，无法取消选课
          </el-alert>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listCourses, getCourses, delCourses, addCourses, updateCourses, signUpCourse, cancelSignUpCourse } from "@/api/system/courses";
import {parseTime} from "@/utils/ruoyi";
import {addBooking, checkCourseBookingSimple, deleteBookingsByCourseAndStudent} from "@/api/system/courseBookings";
import {getStudent} from "@/api/system/student";
import { getServerTime } from "@/api/common/time";

export default {
  name: "Courses",
  data() {
    return {
      selectedCourse: null,
      // 服务器时间
      serverTime: null,
      // 详情弹窗相关
      detailDialogVisible: false,
      // 遮罩层
      loading: true,
      // 选中数组（管理员批量操作）
      ids: [],
      // 非单个禁用（修改按钮）
      single: true,
      // 非多个禁用（删除按钮）
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 课程表格数据
      coursesList: [],
      // 弹窗标题
      title: "",
      // 弹窗显示状态
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseName: null,
        courseLocation: null,
        courseStart: null,
        courseDeadline: null,
        organizer: null,
        courseType: null,
        availableOnly: false, // 只显示可选课课程
        academy: null, // 书院过滤
      },
      // 表单参数
      form: {
        courseId: null,
        courseName: null,
        courseStart: null,
        courseDeadline: null,
        courseTotalCapacity: null,
        courseCapacity: null,
        status: null,
        organizer: null
      },
      // 表单校验
      rules: {
        courseName: [
          { required: true, message: "课程名称不能为空", trigger: "blur" }
        ],
        courseStart: [
          { required: true, message: "选课开始时间不能为空", trigger: "blur" }
        ],
        courseDeadline: [
          { required: true, message: "选课截止时间不能为空", trigger: "blur" }
        ],
        courseTotalCapacity: [
          { required: true, message: "课程总容量不能为空", trigger: "blur", type: "number" }
        ],
        courseCapacity: [
          { required: true, message: "已选人数不能为空", trigger: "blur", type: "number" },
          {
            validator: (rule, value, callback) => {
              if (this.form.courseTotalCapacity !== null && value > this.form.courseTotalCapacity) {
                callback(new Error("已选人数不能大于总容量"));
              } else if (value < 0) {
                callback(new Error("已选人数不能为负数"));
              } else {
                callback();
              }
            },
            trigger: "blur"
          }
        ],
        status: [
          { required: true, message: "课程状态不能为空", trigger: "blur" }
        ],
        organizer: [
          { required: true, message: "组织者不能为空", trigger: "blur" }
        ],
      },
      // 当前学生书院
      currentAcademy: null,
      // 预览图片相关
      previewImageUrl: '',
      imagePreviewVisible: false
    };
  },
  computed: {
    // 显示选课按钮的条件
    showSignUpButton() {
      if (!this.selectedCourse) return false;

      return this.getCourseStatusText(this.selectedCourse) === "选课进行中" &&
        !this.selectedCourse.isBooked &&
        this.selectedCourse.courseCapacity < this.selectedCourse.courseTotalCapacity;
    },

    // 显示选课已满提示的条件
    showFullCapacityAlert() {
      if (!this.selectedCourse) return false;
      return this.getCourseStatusText(this.selectedCourse) === "选课进行中" &&
        !this.selectedCourse.isBooked &&
        this.selectedCourse.courseCapacity >= this.selectedCourse.courseTotalCapacity;
    },

    // 显示取消选课按钮的条件
    showCancelButton() {
      if (!this.selectedCourse) return false;
      const status = this.getCourseStatusText(this.selectedCourse);
      return this.selectedCourse.isBooked &&
        (status === "选课进行中" || status === "选课未开始");
    },
  },
  async created() {
    // 获取服务器时间
    await this.getServerTime();
    // 先获取学生信息
    await this.getCurrentStudentInfo();
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

    // 修复：提交取消报名
    async submitCancelSignUp(course) {
      try {
        // 使用原子性取消选课操作
        const cancelData = {
          courseId: Number(course.courseId),
          studentId: this.$store.state.user.name,
          version: Number(course.version) || 0
        };

        const response = await cancelSignUpCourse(cancelData);

        if (response.code === 200) {
          this.$message.success("取消选课成功！");
          this.detailDialogVisible = false;
          this.selectedCourse = null;
          // 重新获取课程列表以更新状态
          await this.getList();
        } else {
          this.$message.error(response.msg || "取消选课失败");
        }

      } catch (error) {
        console.error("取消选课失败:", error);
        this.$message.error("取消选课失败: " + (error.message || "请稍后重试"));
      }
    },

    // 获取当前学生信息
    async getCurrentStudentInfo() {
      try {
        const response = await getStudent(this.$store.state.user.name);

        if (response && response.studentInfo) {
          this.currentAcademy = response.studentInfo.academy;
          // 设置书院过滤参数
          this.queryParams.academy = this.currentAcademy;
          // 获取学生信息后，根据书院获取课程
          this.getList();
        } else {
          console.error('获取学生信息失败，响应中没有studentInfo:', response);
          this.currentAcademy = '未知';
          this.queryParams.academy = null; // 如果获取失败，不进行书院过滤
          // 即使获取失败，也尝试获取课程
          this.getList();
        }
      } catch (error) {
        console.error('获取学生信息异常:', error);
        this.currentAcademy = '未知';
        this.queryParams.academy = null; // 如果获取失败，不进行书院过滤
        // 即使获取失败，也尝试获取课程
        this.getList();
      }
    },
    // 修复：提交选课
    async submitSignUp(course) {
      try {
        // 检查必要参数
        if (!course || !course.courseId) {
          this.$message.error('课程信息不完整，无法选课');
          return;
        }

        // 使用原子性选课操作
        const signUpData = {
          courseId: Number(course.courseId),
          studentId: this.$store.state.user.name,
          version: Number(course.version) || 0
        };

        const response = await signUpCourse(signUpData);

        if (response.code === 200) {
          this.$message.success("选课成功！");
          this.detailDialogVisible = false;
          // 重新获取课程列表以更新状态
          await this.getList();
        } else {
          this.$message.error(response.msg || "选课失败");
        }

      } catch (error) {
        console.error("选课失败:", error);
        this.$message.error("选课失败: " + (error.message || "请稍后重试"));
      }
    },



// 修复checkCourseBookingSimple 方法的调用
    async checkBookingStatus() {
      if (!this.coursesList || this.coursesList.length === 0) return;

      try {
        const checkPromises = this.coursesList.map(course => {
          // 确保参数类型正确
          const params = {
            courseId: Number(course.courseId),  // 转换为数字
            studentId: this.$store.state.user.name
          };

          return checkCourseBookingSimple(
            Number(course.courseId),
            this.$store.state.user.name
          )
            .then(res => {
              this.$set(course, 'isBooked', res.data.isBooked || false);
            })
            .catch(error => {
              console.error('检查选课状态失败:', error);
              this.$set(course, 'isBooked', false);
            });
        });

        await Promise.all(checkPromises);
      } catch (error) {
        console.error('检查选课状态总体失败:', error);
      }
    },



    // 修复：处理取消选课
    async handleCancel(row) {
      this.$confirm('确定要取消该课程选课吗？', '确认取消', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.submitCancelSignUp(row);
      }).catch(() => {
        this.$message.info('已取消取消选课操作');
      });
    },
    // 修复：处理选课
    async handleSignUp(row) {
      this.$confirm('确定要选课该课程吗？', '选课确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.submitSignUp(row);
      }).catch(() => {
        this.$message.info('已取消选课');
      });
    },
    /** 获取课程图片完整URL（仿照审核界面实现） */
    getCourseImageUrl(pictureUrl) {
      if (!pictureUrl) return '';

      // 如果已经是完整URL，直接返回
      if (pictureUrl.startsWith('http://') || pictureUrl.startsWith('https://')) {
        return pictureUrl;
      }

      // 如果以/profile/开头，说明是相对路径，需要拼接基础API路径（仿照审核界面）
      if (pictureUrl.startsWith('/profile/')) {
        return `${process.env.VUE_APP_BASE_API}${pictureUrl}`;
      }

      return pictureUrl;
    },
    // 格式化日期时间
    formatDateTime(datetime) {
      return parseTime(datetime, "{y}-{m}-{d} {h}:{i}");
    },
    // 获取容量样式
    getCapacityClass(course) {
      // courseCapacity 是已选人数
      const percentage = (course.courseCapacity || 0) / course.courseTotalCapacity;
      if (percentage >= 0.8) return 'capacity-high';
      if (percentage >= 0.5) return 'capacity-medium';
      return 'capacity-low';
    },

    // 计算容量百分比
    calculateCapacityPercentage(row) {
      if (!row.courseTotalCapacity || row.courseTotalCapacity <= 0) return 0;
      // courseCapacity 是已选人数
      const used = row.courseCapacity || 0;
      return Math.round((used / row.courseTotalCapacity) * 100);
    },

    // 获取进度条颜色
    getProgressColor(percentage) {
      if (percentage >= 80) return '#f87171';
      if (percentage >= 50) return '#fbbf24';
      return '#4ade80';
    },
    // 课程类型映射函数：将数字转换为对应的类型名称
    getCourseTypeName(courseType) {
      const typeMap = {
        1: '人格塑造与价值引领课程类',
        2: '知识融合与思维进阶课程类',
        3: '能力锻造与实践创新课程类',
        4: '社会责任与领军意识课程类'
      };
      return typeMap[courseType] || courseType;
    },

    // 获取课程类型标签颜色
    getCourseTypeTagType(courseType) {
      const map = {
        1: 'primary',   // 人格塑造与价值引领课程类 - 蓝色
        2: 'success',   // 知识融合与思维进阶课程类 - 绿色
        3: 'warning',   // 能力锻造与实践创新课程类 - 橙色
        4: 'danger',    // 社会责任与领军意识课程类 - 红色
        '其他': ''        // 默认蓝色
      }
      return map[courseType] || 'info';
    },

    // 获取课程分类名称
    getCourseCategoryName(courseCategory) {
      const categoryMap = {
        '必修': '必修',
        '选修': '选修',
        '实践': '实践',
        '其他': '其他'
      };
      return categoryMap[courseCategory] || courseCategory;
    },

    // 获取课程分类标签颜色
    getCourseCategoryTagType(courseCategory) {
      const map = {
        '必修': 'danger',    // 必修 - 红色
        '选修': 'success',   // 选修 - 绿色
        '实践': 'warning',   // 实践 - 橙色
        '其他': 'info'       // 其他 - 蓝色
      }
      return map[courseCategory] || 'info';
    },
    // 获取选课状态标签类型
    getSignStatusTag(course) {
      const status = this.getSignStatusText(course);
      switch (status) {
        case "可选课": return "success";
        case "未提交": return "warning";
        case "不可选课": return "danger";
        default: return "info";
      }
    },
    // 处理详情弹窗关闭
    handleDetailClose(done) {
      this.detailDialogVisible = false;
      this.selectedCourse = null;
      done();
    },
    // 判断课程是否已满
    isCourseFull(row) {
      const status = this.getCourseStatusText(row);
      // 只有在选课进行中且已选人数达到总容量时才显示已满
      return status === "选课进行中" && row.courseCapacity >= row.courseTotalCapacity;
    },
    // 修复：处理详情
    async handleDetail(row) {
      this.selectedCourse = { ...row };
      this.detailDialogVisible = true;
    },

    // 获取选课状态文本
    getSignStatusText(row) {
      // 简化逻辑，只检查是否已选课
      if (row.isBooked) {
        return "未提交";
      }

      const status = this.getCourseStatusText(row);
      // courseCapacity 是已选人数，判断是否还有剩余容量
      const hasCapacity = row.courseCapacity < row.courseTotalCapacity;

      if (["选课进行中"].includes(status) && hasCapacity) {
        return "可选课";
      }

      return "不可选课";
    },
    /** 查询课程列表 */
    getList() {
      this.loading = true;
      listCourses(this.queryParams).then(async response => {
        let courses = response.rows;

        // 按书院过滤课程（如果设置了书院参数）
        if (this.queryParams.academy && this.queryParams.academy !== '未知') {
          courses = courses.filter(course => {
            // 检查课程的组织者是否包含学生的书院
            return course.organizer && course.organizer.includes(this.queryParams.academy);
          });
        }

        // 如果选择了"只显示可选课课程"，则进行前端过滤
        if (this.queryParams.availableOnly) {
          courses = courses.filter(course => {
            // 检查课程状态是否为"选课进行中"
            const status = this.getCourseStatusText(course);
            if (status !== "选课进行中") {
              return false;
            }

            // 检查是否有剩余容量（courseCapacity 是已选人数）
            if (course.courseCapacity >= course.courseTotalCapacity) {
              return false;
            }

            // 检查是否已经选过课
            if (course.isBooked) {
              return false;
            }

            return true;
          });
        }

        this.coursesList = courses;
        this.total = courses.length;
        this.loading = false;

        // 获取课程列表后检查选课状态
        await this.checkBookingStatus();
      });
    },
    // 获取表格行样式类名
    getRowClassName({row}) {
      if (row.isBooked) {
        return 'booked-row';
      }
      return '';
    },
    /** 取消弹窗 */
    cancel() {
      this.open = false;
      this.reset();
    },

    /** 表单重置 */
    reset() {
      this.form = {
        courseId: null,
        courseName: null,
        courseStart: null,
        courseDeadline: null,
        courseTotalCapacity: null,
        courseCapacity: null,
        status: null,
        organizer: null
      };
      this.resetForm("form");
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 重置搜索 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },

    /** 多选框选中事件 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.courseId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },

    /** 新增课程 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加课程";
    },

    /** 修改课程 */
    handleUpdate(row) {
      this.reset();
      const courseId = row.courseId || this.ids;
      getCourses(courseId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改课程";
      });
    },

    /** 提交表单（新增/修改） */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.courseId != null) {
            updateCourses(this.form).then(() => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCourses(this.form).then(() => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    /** 删除课程 */
    handleDelete(row) {
      const courseIds = row.courseId || this.ids;
      this.$modal.confirm(`是否确认删除课程编号为"${courseIds}"的数据？`).then(() => {
        return delCourses(courseIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 导出课程 */
    handleExport() {
      this.download('system/courses/export', { ...this.queryParams }, `courses_${new Date().getTime()}.xlsx`);
    },


    // 获取课程状态文本
    getCourseStatusText(course) {
      // 使用服务器时间，如果服务器时间不可用则使用本地时间
      const now = this.serverTime || new Date();
      const deadline = new Date(course.courseDeadline);
      const courseStart = new Date(course.courseStart);

      if (now < courseStart) return "选课未开始";
      if (now < deadline && now >= courseStart) return "选课进行中";
      if (now >= deadline ) return "选课已截止";

      return course.status || "未知";
    },
    /** 课程状态标签颜色映射 */
    getCourseStatusTagType(course) {
      const status = this.getCourseStatusText(course);
      switch (status) {
        case "选课未开始": return "info";
        case "选课进行中": return "success";
        case "选课已截止": return "danger";
        default: return "danger";
      }
    },

    // 同步课程数据（在选课操作后调用）
    async syncCourseData() {
      try {
        // 静默获取最新数据，不显示loading状态
        const response = await listCourses(this.queryParams);
        let courses = response.rows;

        // 按书院过滤课程（如果设置了书院参数）
        if (this.queryParams.academy && this.queryParams.academy !== '未知') {
          courses = courses.filter(course => {
            return course.organizer && course.organizer.includes(this.queryParams.academy);
          });
        }

        // 如果选择了"只显示可选课课程"，则进行前端过滤
        if (this.queryParams.availableOnly) {
          courses = courses.filter(course => {
            const status = this.getCourseStatusText(course);
            if (status !== "选课进行中") {
              return false;
            }
            if (course.courseCapacity >= course.courseTotalCapacity) {
              return false;
            }
            return !course.isBooked;
          });
        }

        // 更新课程列表数据
        this.coursesList = courses;
        this.total = response.total;
      } catch (error) {
        // 同步失败，不显示错误信息给用户
      }
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

.table-card {
  padding-bottom: 0;
  margin-bottom: 40px;
  overflow: visible;
}

.time-range-info {
  margin-left: auto;  /* 关键：这个属性让它右对齐 */
  margin-right: 20px;
  font-size: 13px;
  color: #409EFF;  /* 蓝色文字 */
  font-weight: 500;
  background: rgba(64, 158, 255, 0.1);  /* 浅蓝色背景 */
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid rgba(64, 158, 255, 0.2);  /* 蓝色边框 */
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

.search-select {
  width: 200px;
}

.search-actions {
  margin-left: auto;
}

.academy-info {
  margin-left: auto;
  margin-right: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  background: rgba(64, 158, 255, 0.1);
  padding: 6px 12px;
  border-radius: 12px;
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.academy-info i {
  color: #409EFF;
  font-size: 16px;
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
.mb8 {
  margin-bottom: 8px;
}

.record-count {
  margin-left: auto;
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}
.small-padding {
  padding: 0 5px;
}

.fixed-width {
  width: 120px;
}

/* 课程详情弹窗样式 */
.course-detail-dialog {
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

.course-detail {
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

/* 课程图片展示样式 */
.course-image-container {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.course-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  }
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

.dialog-footer {
  text-align: right;
  margin-top: 16px;
}

/* 课程类型标签样式 */
.course-type-tag {
  font-weight: 500;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
  font-size: 13px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 学分显示样式 */
.credit-value {
  font-weight: 600;
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 13px;
}

/* 课程分类标签样式 */
.category-tag {
  font-weight: 500;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  font-size: 12px;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 已选课课程行样式 */
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

/* 时间安排样式 */
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
}

.time-inline-item.signup-time {
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

.time-inline-item.activity-time {
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

.time-inline-item i {
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

/* 选课人数列样式 */
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

</style>
