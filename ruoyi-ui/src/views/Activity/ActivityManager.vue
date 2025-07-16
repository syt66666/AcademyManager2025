<template>
  <div class="app-container">
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

      <el-form-item label="组织单位" prop="organizer">
        <el-input
          v-model="queryParams.organizer"
          placeholder="请输入组织单位"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增
        </el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleImport"
        >导入
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="activitiesList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" width="80" align="center">
        <template v-slot="scope">
              <span class="index-badge">
                {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
              </span>
        </template>
      </el-table-column>
      <el-table-column label="活动名称" align="center" prop="activityName"/>
      <el-table-column label="活动地点" align="center" prop="activityLocation"/>
      <el-table-column label="活动容量" align="center" prop="activityTotalCapacity"/>
      <el-table-column label="报名开始时间" align="center" prop="activityStart" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.activityStart, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报名截止时间" align="center" prop="activityDeadline" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.activityDeadline, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="活动开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="活动结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="组织单位" align="center" prop="organizer"/>
      <el-table-column label="活动状态" align="center" prop="status"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-user"
            class="action-icon view-icon"
            @click="handleViewStudents(scope.row)">
          </el-button>
        </template>
      </el-table-column>
      <!-- 活动描述+注意事项 -->
      <el-table-column type="expand" width="60" align="center">
        <template slot-scope="props">
          <div class="expand-layout">
            <!-- 左侧活动描述 -->
            <div class="expand-section">
              <div class="section-header">活动描述</div>
              <div class="section-content scrollable">
                {{ props.row.activityDescription || "无描述信息" }}
              </div>
            </div>

            <!-- 分隔线 -->
            <div class="divider"></div>

            <!-- 右侧注意事项 -->
            <div class="expand-section">
              <div class="section-header">注意事项</div>
              <div class="section-content scrollable">
                {{ props.row.notes || "无注意事项" }}
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改活动对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="活动名称" prop="activityName">
          <el-input v-model="form.activityName" placeholder="请输入活动名称"/>
        </el-form-item>
        <!-- 报名开始时间 -->
        <el-form-item label="报名开始时间" prop="activityStart">
          <el-date-picker clearable
                          v-model="form.activityStart"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择活动报名开始时间">
          </el-date-picker>
        </el-form-item>
        <!-- 报名截止时间 -->
        <el-form-item label="报名截止时间" prop="activityDeadline">
          <el-date-picker clearable
                          v-model="form.activityDeadline"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择活动报名截止时间">
          </el-date-picker>
        </el-form-item>

        <!-- 开始时间 -->
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable
                          v-model="form.startTime"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择活动开始时间">
          </el-date-picker>
        </el-form-item>

        <!-- 结束时间 -->
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable
                          v-model="form.endTime"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择活动结束时间">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="活动地点" prop="activityLocation">
          <el-input v-model="form.activityLocation" placeholder="请输入活动地点"/>
        </el-form-item>
        <el-form-item label="活动容量" prop="activityTotalCapacity">
          <el-input v-model="form.activityTotalCapacity" placeholder="请输入活动容量"/>
        </el-form-item>
        <el-form-item label="组织单位" prop="organizer">
          <el-input v-model="form.organizer" placeholder="请输入组织单位"/>
        </el-form-item>
        <el-form-item label="活动描述" prop="activityDescription">
          <el-input v-model="form.activityDescription" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
        <el-form-item label="注意事项" prop="notes">
          <el-input v-model="form.notes" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 学生选课信息对话框 -->
    <el-dialog
      title="选课学生列表"
      :visible.sync="dialogVisibleStudents"
      width="60%"
      append-to-body
      class="student-dialog">
      <el-table
        :data="selectedStudents"
        border
        v-loading="studentLoading"
        class="enhanced-student-table"
        :header-cell-style="{
      'background': '#f5f7fa',
      'color': '#2d3540',
      'font-weight': '600'
    }">
        <el-table-column label="序号" width="80" align="center">
          <template v-slot="scope">
              <span class="index-badge">
                {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="studentId" label="学号" width="180">
          <template slot-scope="{row}">
            <span class="student-id">{{ row.studentId }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="studentName" label="姓名">
          <template slot-scope="{row}">
            <span class="student-name">{{ row.studentName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="bookedAt" label="预约时间" width="180">
          <template slot-scope="{row}">
            <div class="booked_at">
              <i class="el-icon-time"></i>
              {{ parseTime(row.bookedAt) }}
            </div>
          </template>
        </el-table-column>

      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          icon="el-icon-download"
          @click="handleExportStudents"
          class="export-btn"
          v-hasPermi="['system:course:export']">
          导出名单
        </el-button>
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
import {listActivities, getActivities, delActivities, addActivities, updateActivities2} from "@/api/system/activities";
import {getToken} from "@/utils/auth";
import {listBookingsWithActivity} from "@/api/system/bookings";

export default {
  name: "Activities",
  data() {
    return {
      // 新增状态
      dialogVisibleStudents: false,
      selectedStudents: [],
      studentLoading: false,
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
        status: null,
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

      // 表单校验
      rules: {
        activityName: [
          {required: true, message: "活动名称不能为空", trigger: "blur"}
        ],
        activityLocation: [
          {required: true, message: "活动地点不能为空", trigger: "blur"}
        ],
        activityTotalCapacity: [
          {required: true, message: "活动容量不能为空", trigger: "blur"}
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
  created() {
    this.getList();
  },
  methods: {
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

      this.download('/system/bookings/export2', {
        activityId: activityId
      }, `预约活动名单_${activityName}_${this.parseTime(new Date(), '{y}{m}{d}')}.xlsx`)

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
          this.form.status = '不可报名';
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
      listActivities(this.queryParams).then(response => {
        this.activitiesList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
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
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 计算活动状态
          this.calculateStatus();
          if (this.form.activityId != null) {
            updateActivities2(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addActivities(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const activityIds = row.activityId || this.ids;
      const activityNames = row.activityName || this.names;
      this.$modal.confirm('是否确认删除活动名称为"' + activityNames + '"的数据项？').then(function () {
        return delActivities(activityIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/activities/export', {
        ...this.queryParams
      }, `activities_${new Date().getTime()}.xlsx`)
    }
  },
  watch: {
    'form.activityTotalCapacity'(newVal) {
      this.form.activityCapacity = newVal;
    }
  }
};
</script>

<style scoped>
.app-container {
  margin-left: 100px;
}
/* 左右布局容器 */
.expand-layout {
  display: flex;
  padding: 15px;
}

/* 分区样式 */
.expand-section {
  flex: 1;
  padding: 0 15px;
}

/* 分区头部样式 */
.section-header {
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
  border-bottom: 1px solid #eee;
  padding-bottom: 5px;
}

/* 分区内容样式 */
.section-content {
  line-height: 1.6;
  color: #606266;
}

/* 可滚动区域 */
.scrollable {
  max-height: 200px;
  overflow-y: auto;
}

/* 分区间的分隔线 */
.divider {
  width: 1px;
  background-color: #EBEEF5;
  margin: 0 10px;
}
</style>
