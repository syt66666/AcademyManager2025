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

    <el-table v-loading="loading" :data="activitiesList">

      <el-table-column label="活动名称" align="center" prop="activityName" />
      <el-table-column label="活动地点" align="center" prop="activityLocation" />
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="组织单位" align="center" prop="organizer" />
      <!-- 修改活动状态列为材料提交列 -->
      <el-table-column label="材料提交" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <!-- 未提交状态 - 可提交 -->
          <el-button
            v-if="scope.row.status === '未提交'"
            type="primary"
            size="mini"
            @click="openUploadDialog(scope.row)">
            提交材料
          </el-button>

          <!-- 未通过状态 - 可重新提交 -->
          <el-button
            v-if="scope.row.status === '未通过'"
            type="warning"
            size="mini"
            @click="openUploadDialog(scope.row)">
            重新提交
          </el-button>

          <!-- 未审核/已通过状态 - 仅显示文字 -->
          <span v-if="scope.row.status === '未审核' || scope.row.status === '已通过'">
            {{ scope.row.status }}
          </span>
        </template>
      </el-table-column>

<!--      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-edit"-->
<!--            @click="handleUpdate(scope.row)"-->
<!--          >修改</el-button>-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-delete"-->
<!--            @click="handleDelete(scope.row)"-->
<!--          >删除</el-button>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <!-- 活动描述+注意事项 -->
      <el-table-column type="expand" width="60" align="center">
        <template slot-scope="props">
          <div class="expand-container">
            <div class="expand-row">
              <div class="expand-label">活动描述:</div>
              <div class="expand-content">{{ props.row.activityDescription }}</div>
            </div>
            <div class="expand-row">
              <div class="expand-label">注意事项:</div>
              <div class="expand-content">{{ props.row.notes }}</div>
            </div>
          </div>
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
    <!-- 修改后的文件上传对话框 -->
    <el-dialog
      :title="uploadTitle"
      :visible.sync="uploadVisible"
      width="60%"
      :close-on-click-modal="false"
    >
      <!-- 图片上传区域 -->
      <div class="section">
        <h3>上传图片证明材料</h3>
        <el-upload
          :action="uploadUrl"
          list-type="picture-card"
          :file-list="imageFiles"
          :on-preview="handlePicturePreview"
          :on-remove="handleRemoveImage"
          :on-success="handleImageSuccess"
          :limit="5"
          multiple
          accept=".jpg,.jpeg,.png,.gif"
          :before-upload="beforeImageUpload"
          :headers="headers"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
        <div class="el-upload__tip">最多可上传5张图片，每张大小不超过2MB</div>
      </div>

      <!-- 文档上传区域 -->
      <div class="section">
        <h3>上传文档材料</h3>
        <el-upload
          :action="uploadUrl"
          :file-list="docFiles"
          :on-remove="handleRemoveDoc"
          :on-success="handleDocSuccess"
          :limit="1"
          accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx"
          :before-upload="beforeDocUpload"
          :headers="headers"
          :data="{ filePath: 'bookingImages' }"
        >
          <el-button size="small" type="primary">上传文件</el-button>
          <div slot="tip" class="el-upload__tip">
            支持上传PDF、Word、Excel、PPT等文档，大小不超过10MB
          </div>
        </el-upload>
      </div>

      <el-dialog :visible.sync="imagePreviewVisible" width="60%">
        <img width="100%" :src="previewImageUrl" alt />
      </el-dialog>

      <span slot="footer" class="dialog-footer">
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProof">确认提交</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import {listBookingsWithActivity, updateBooking, getBooking} from "@/api/system/bookings";
import { getToken } from "@/utils/auth";

export default {
  name: "Activities",
  data() {
    return {
      // 上传相关数据
      uploadVisible: false,
      uploadTitle: "提交材料",
      currentBooking: null,

      // 上传URL和headers
      uploadUrl: process.env.VUE_APP_BASE_API + "/common/upload",
      headers: { Authorization: "Bearer " + getToken() },

      // 图片上传
      imageFiles: [],
      previewImageUrl: "",
      imagePreviewVisible: false,

      // 文档上传
      docFiles: [],

      // 已有文件
      existingFiles: [],

      // 活动总结
      activitySummary: "",
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
      // 【请填写功能名称】表格数据
      activitiesList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        activityName: null,
        startTime: null,
        endTime: null,
        activityLocation: null,
        activityCapacity: null,
        activityDeadline: null,
        activityDescription: null,
        status: null,
        createdAt: null,
        organizer: null,
        notes: null
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
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/system/activities/importData"
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        activityName: [
          { required: true, message: "活动名称不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" }
        ],
        activityLocation: [
          { required: true, message: "活动地点不能为空", trigger: "blur" }
        ],
        activityCapacity: [
          { required: true, message: "活动容量不能为空", trigger: "blur" }
        ],
        activityDeadline: [
          { required: true, message: "报名截止时间不能为空", trigger: "blur" }
        ],
        organizer: [
          { required: true, message: "组织单位不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 从URL中提取文件名的帮助函数
    extractFileName(url) {
      if (!url) return '';
      // 从URL中提取文件名部分
      return url.substring(url.lastIndexOf('/') + 1);
    },

    // 修改初始化文件列表
    initFileLists(data) {
      this.imageFiles = [];
      this.docFiles = [];

      // 初始化图片文件 - 从完整URL中提取文件名
      if (data.proof && Array.isArray(data.proof)) {
        data.proof.forEach(fullUrl => {
          this.imageFiles.push({
            name: this.extractFileName(fullUrl),
            url: fullUrl
          });
        });
      }

      // 初始化文档文件 - 从完整URL中提取文件名
      if (data.summary) {
        const fileName = this.extractFileName(data.summary);
        this.docFiles.push({
          name: fileName,
          url: data.summary
        });
      }
    },

    // 修改图片上传成功处理 - 存储文件名而非完整URL
    handleImageSuccess(response, file, fileList) {
      if (response.code === 200) {
        const fileName = this.extractFileName(response.url);
        const fileNameOnly = fileName || this.extractFileName(response.fileName);
        this.imageFiles = fileList.map(f => ({
          name: fileNameOnly, // 只存储文件名
          url: response.url || response.fileName
        }));
        this.$message.success('图片上传成功');
      } else {
        this.$message.error('图片上传失败: ' + response.msg);
      }
    },

    // 修改文档上传成功处理 - 存储文件名而非完整URL
    handleDocSuccess(response, file, fileList) {
      if (response.code === 200) {
        const fileName = this.extractFileName(response.url);
        const fileNameOnly = fileName || this.extractFileName(response.fileName);
        this.docFiles = fileList.map(f => ({
          name: fileNameOnly, // 只存储文件名
          url: response.url || response.fileName
        }));
        this.$message.success('文档上传成功');
      } else {
        this.$message.error('文档上传失败: ' + response.msg);
      }
    },

    // 修改提交函数 - 只使用文件名
    async submitProof() {
      if (!this.currentBooking) return;

      try {
        // 准备proof字段 - 只提交文件名数组
        const proofFileNames = this.imageFiles.map(file => file.name);

        // 准备summary字段 - 只提交文件名（取第一个文档）
        const summaryFileName = this.docFiles.length > 0 ? this.docFiles[0].name : null;

        console.log("提交的文件名:", {
          proof: proofFileNames,
          summary: summaryFileName
        });
        console.log(this.currentBooking)
        // 提交到后端
        const res = await updateBooking({
          bookingId: this.currentBooking.bookingId, // 确保使用正确的ID字段
          proof: proofFileNames, // 只提交文件名数组
          summary: summaryFileName, // 只提交文件名
          status: "未审核"
        });

        if (res.code === 200) {
          this.$message.success("材料提交成功");
          this.uploadVisible = false;
          this.getList(); // 刷新列表
        } else {
          this.$message.error("材料提交失败: " + res.msg);
        }
      } catch (error) {
        this.$message.error("材料提交失败: " + error.message);
      }
    },
    // 打开上传对话框
    async openUploadDialog(booking) {
      this.currentBooking = booking;
      this.uploadTitle = booking.status === '未提交' ? '提交材料' : '重新提交';
      this.uploadVisible = true;

      // 重置上传状态
      this.resetUploadState();

      try {
        // 获取已有的材料信息
        const res = await getBooking(booking.bookingId);
        if (res.code === 200) {
          const data = res.data || {};

          // 初始化文件列表
          this.initFileLists(data);
        }
      } catch (error) {
        console.error("获取材料信息失败:", error);
        this.$message.error("获取材料信息失败");
      }
    },


    // 重置上传状态
    resetUploadState() {
      this.imageFiles = [];
      this.docFiles = [];
      this.imagePreviewVisible = false;
    },

    // 图片上传前校验
    beforeImageUpload(file) {
      const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type);
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error('请上传图片格式文件!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!');
        return false;
      }

      return true;
    },

    // 文档上传前校验
    beforeDocUpload(file) {
      const allowedTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-powerpoint',
        'application/vnd.openxmlformats-officedocument.presentationml.presentation'
      ];

      const isDoc = allowedTypes.includes(file.type);
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isDoc) {
        this.$message.error('请上传PDF、Word、Excel或PPT文件!');
        return false;
      }
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过 10MB!');
        return false;
      }

      return true;
    },


    // 图片预览
    handlePicturePreview(file) {
      if (file.url) {
        this.previewImageUrl = file.url;
        this.imagePreviewVisible = true;
      }
    },

    // 移除图片
    handleRemoveImage(file, fileList) {
      this.imageFiles = fileList;
    },

    // 移除文档
    handleRemoveDoc(file, fileList) {
      this.docFiles = fileList;
    },

    // 移除已有文件
    removeExistingFile(file) {
      // 根据文件类型移除
      if (file.type === 'image') {
        this.imageFiles = this.imageFiles.filter(f => f.name !== file.name);
      } else if (file.type === 'doc') {
        this.docFiles = this.docFiles.filter(f => f.name !== file.name);
      }
    },




    /** 查询活动列表 */
    getList() {
      this.loading = true;
      listBookingsWithActivity(this.queryParams).then(response => {
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
        activityDeadline: null,
        activityDescription: null,
        status: null,
        createdAt: null,
        organizer: null,
        notes: null
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
  }
};
</script>

<style scoped>
.app-container {
  margin-left: 100px;
}
</style>
