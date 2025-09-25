<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="课程名称" prop="courseName">
        <el-input
          v-model="queryParams.courseName"
          placeholder="请输入课程名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程地点" prop="courseLocation">
        <el-input
          v-model="queryParams.courseLocation"
          placeholder="请输入课程地点"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="选课开始时间" prop="courseStart">
        <el-date-picker clearable
          v-model="queryParams.courseStart"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择选课开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="选课截止时间" prop="courseDeadline">
        <el-date-picker clearable
          v-model="queryParams.courseDeadline"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择选课截止时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="课程开始时间" prop="startTime">
        <el-date-picker clearable
          v-model="queryParams.startTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择课程开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="课程结束时间" prop="endTime">
        <el-date-picker clearable
          v-model="queryParams.endTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择课程结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="课程容量" prop="courseTotalCapacity">
        <el-input
          v-model="queryParams.courseTotalCapacity"
          placeholder="请输入课程容量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程剩余容量" prop="courseCapacity">
        <el-input
          v-model="queryParams.courseCapacity"
          placeholder="请输入课程剩余容量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程图片URL" prop="pictureUrl">
        <el-input
          v-model="queryParams.pictureUrl"
          placeholder="请输入课程图片URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createdAt">
        <el-date-picker clearable
          v-model="queryParams.createdAt"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="组织者" prop="organizer">
        <el-input
          v-model="queryParams.organizer"
          placeholder="请输入组织者"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="乐观锁" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入乐观锁"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程学分" prop="courseCredit">
        <el-input
          v-model="queryParams.courseCredit"
          placeholder="请输入课程学分"
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
          v-hasPermi="['system:courses:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:courses:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:courses:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:courses:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="coursesList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="课程id" align="center" prop="courseId" />
      <el-table-column label="课程名称" align="center" prop="courseName" />
      <el-table-column label="课程类型" align="center" prop="courseType" />
      <el-table-column label="课程地点" align="center" prop="courseLocation" />
      <el-table-column label="选课开始时间" align="center" prop="courseStart" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.courseStart, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="选课截止时间" align="center" prop="courseDeadline" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.courseDeadline, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="课程开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="课程结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="课程容量" align="center" prop="courseTotalCapacity" />
      <el-table-column label="课程剩余容量" align="center" prop="courseCapacity" />
      <el-table-column label="${comment}" align="center" prop="courseDescription" />
      <el-table-column label="课程图片URL" align="center" prop="pictureUrl" />
      <el-table-column label="课程状态" align="center" prop="status" />
      <el-table-column label="创建时间" align="center" prop="createdAt" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="组织者" align="center" prop="organizer" />
      <el-table-column label="注意事项" align="center" prop="notes" />
      <el-table-column label="乐观锁" align="center" prop="version" />
      <el-table-column label="课程学分" align="center" prop="courseCredit" />
      <el-table-column label="课程类别" align="center" prop="courseCategory" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:courses:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:courses:remove']"
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

    <!-- 添加或修改【请填写功能名称】对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程地点" prop="courseLocation">
          <el-input v-model="form.courseLocation" placeholder="请输入课程地点" />
        </el-form-item>
        <el-form-item label="选课开始时间" prop="courseStart">
          <el-date-picker clearable
            v-model="form.courseStart"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择选课开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="选课截止时间" prop="courseDeadline">
          <el-date-picker clearable
            v-model="form.courseDeadline"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择选课截止时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="课程开始时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择课程开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="课程结束时间" prop="endTime">
          <el-date-picker clearable
            v-model="form.endTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择课程结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="课程容量" prop="courseTotalCapacity">
          <el-input v-model="form.courseTotalCapacity" placeholder="请输入课程容量" />
        </el-form-item>
        <el-form-item label="课程剩余容量" prop="courseCapacity">
          <el-input v-model="form.courseCapacity" placeholder="请输入课程剩余容量" />
        </el-form-item>
        <el-form-item label="${comment}" prop="courseDescription">
          <el-input v-model="form.courseDescription" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="课程图片URL" prop="pictureUrl">
          <el-input v-model="form.pictureUrl" placeholder="请输入课程图片URL" />
        </el-form-item>
        <el-form-item label="创建时间" prop="createdAt">
          <el-date-picker clearable
            v-model="form.createdAt"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="组织者" prop="organizer">
          <el-input v-model="form.organizer" placeholder="请输入组织者" />
        </el-form-item>
        <el-form-item label="注意事项" prop="notes">
          <el-input v-model="form.notes" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="乐观锁" prop="version">
          <el-input v-model="form.version" placeholder="请输入乐观锁" />
        </el-form-item>
        <el-form-item label="课程学分" prop="courseCredit">
          <el-input v-model="form.courseCredit" placeholder="请输入课程学分" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCourses, getCourses, delCourses, addCourses, updateCourses } from "@/api/system/courses";

export default {
  name: "Courses",
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
      // 【请填写功能名称】表格数据
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
        version: null,
        courseCredit: null,
        courseCategory: null
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
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      listCourses(this.queryParams).then(response => {
        this.coursesList = response.rows;
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
        version: null,
        courseCredit: null,
        courseCategory: null
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
      this.ids = selection.map(item => item.courseId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加【请填写功能名称】";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const courseId = row.courseId || this.ids
      getCourses(courseId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改【请填写功能名称】";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.courseId != null) {
            updateCourses(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCourses(this.form).then(response => {
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
      const courseIds = row.courseId || this.ids;
      this.$modal.confirm('是否确认删除【请填写功能名称】编号为"' + courseIds + '"的数据项？').then(function() {
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
    }
  }
};
</script>
