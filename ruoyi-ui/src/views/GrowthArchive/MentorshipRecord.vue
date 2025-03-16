<template>
  <el-row type="flex" justify="center" style="margin-top: 4vh;">
    <el-card id="reportCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <!-- 头部区域 -->
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">导师指导</h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="addNewCard"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <el-table :data="meetingRecords" style="width: 100%" border stripe highlight-current-row>
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="guidanceTopic" label="指导主题" min-width="100"></el-table-column>
        <el-table-column prop="guidanceLocation" label="指导地点" min-width="100"></el-table-column>
        <el-table-column prop="guidanceTime" label="指导时间" min-width="100"></el-table-column>
        <el-table-column label="总结文档" width="120">
          <template v-slot:default="scope">
            <div class="proof-material-cell">
              <el-button
                type="primary"
                icon="el-icon-download"
                size="mini"
                v-if="scope.row.summaryFilePath"
                @click="downloadSummaryDocument(scope.row.summaryFilePath)"
              >下载
              </el-button>
              <span v-else> </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="现场图片" width="120">
          <template v-slot:default="scope">
            <div class="proof-material-cell">
              <el-link
                type="primary"
                :underline="false"
                v-if="scope.row.photoPaths !=='[]'"
                @click="handlePreview(scope.row.photoPaths)"
                style="margin-right: 10px;"
              >
                <i class="el-icon-view"></i> 预览
              </el-link>
              <span v-else> </span>
              <el-button
                type="primary"
                icon="el-icon-download"
                size="mini"
                @click="downloadMeetingPictures(scope.row.photoPaths)"
                v-if="scope.row.photoPaths !=='[]'"
              >下载
              </el-button>

            </div>

          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" min-width="80">
          <template v-slot:default="scope">
            <el-tag v-if="scope.row.auditStatus === '未审核'" type="warning">未审核</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '已通过'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '未通过'" type="danger">未通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '未提交'" type="info">未提交</el-tag>
            <el-tag v-else type="info">未知状态</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button
              v-if="scope.row.auditStatus === '未通过'"
              type="text"
              size="mini"
              @click="handleEditDraft(scope.row)"
            >重新提交
            </el-button>
            <template v-if="scope.row.auditStatus === '未提交'">
              <el-button
                type="text"
                size="mini"
                @click="handleEditDraft(scope.row)"
              >编辑草稿
              </el-button>
              <el-button
                type="text"
                size="mini"
                style="color: #F56C6C;"
                @click="handleDelete(scope.row)"
              >删除
              </el-button>
            </template>

            <el-tag
              v-if="['未审核', '已通过'].includes(scope.row.auditStatus)"
              type="info"
              size="mini"
            >不可修改
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" min-width="100"></el-table-column>
        <el-table-column prop="auditRemark" label="审核意见" min-width="150"></el-table-column>
      </el-table>

      <!-- 图片预览对话框 -->
      <el-dialog :visible.sync="previewVisible" title="图片预览" width="60%">
        <div style="text-align: center; margin-bottom: 20px;">
          <img
            :src="previewImages[currentPreviewIndex]"
            style="max-width: 100%; display: block; margin: 0 auto;"
            alt="现场图片预览"
          />
          <el-button
            icon="el-icon-arrow-left"
            :disabled="currentPreviewIndex === 0"
            @click="currentPreviewIndex--"
          ></el-button>
          <span style="margin: 0 20px;">{{ currentPreviewIndex + 1 }} / {{ previewImages.length }}</span>
          <el-button
            icon="el-icon-arrow-right"
            :disabled="currentPreviewIndex === previewImages.length - 1"
            @click="currentPreviewIndex++"
          ></el-button>
        </div>

        <div slot="footer">
          <el-button
            type="primary"
            @click="downloadSingleFile(previewImages[currentPreviewIndex])"
            style="background-color: #42b983; border-color: #42b983;"
          >
            <i class="el-icon-download"></i> 下载当前图片
          </el-button>
        </div>
      </el-dialog>

      <!-- 分页组件 -->
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        :total="totalRecords"
        :page-sizes="[10, 20, 30, 40]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="text-align: center; margin-top: 10px;"
      />
    </el-card>

    <!-- 新增指导对话框 -->
    <el-dialog :visible.sync="showDialog" title="导师指导填写" id="newCard"
               style="width: 100%; margin-top: 2vh;margin-left: 1%" @close="closeCard">
      <el-form ref="form" :model="formData" :rules="rules" label-width="120px" style="padding: 20px;">
        <el-form-item label="指导主题" prop="guidanceTopic">
          <el-input v-model="formData.guidanceTopic" placeholder="请输入指导主题" style="width: 100%;"></el-input>
        </el-form-item>
        <el-form-item label="指导地点" prop="guidanceLocation">
          <el-input v-model="formData.guidanceLocation" placeholder="请输入指导地点" style="width: 100%;"></el-input>
        </el-form-item>

        <el-form-item label="指导日期" prop="guidanceTime">
          <el-date-picker
            clearable
            v-model="formData.guidanceTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择指导日期"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="评价导师" prop="studentComment">
          <el-input v-model="formData.studentComment" placeholder="请输入对本次导师指导的评价"
                    style="width: 100%;"></el-input>
        </el-form-item>
        <!-- 总结文档上传 -->
        <el-form-item label="总结文档" prop="summaryFilePath">
          <el-upload
            :auto-upload="false"
            :limit="1"
            :on-change="handleSummaryChange"
            :on-remove="handleSummaryRemove"
            :file-list="summaryFileList"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">仅支持单个文件上传</div>
            </template>
          </el-upload>
        </el-form-item>
        <!-- 现场图片上传 -->
        <el-form-item label="现场图片上传" prop="photoPaths">
          <el-upload
            multiple
            :limit="5"
            :value="fileList"
            :file-list="fileList"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :on-preview="handlePreviewFile"
            list-type="picture-card"
            class="custom-upload"
          >
            <i class="el-icon-plus"></i>
              <div slot="tip" class="el-upload__tip">支持格式：JPG/PNG 单文件≤10MB 最多5个文件</div>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button
            type="info"
            @click="handleSave"
            style="margin-right: 10px;"
          >保存草稿
          </el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            style="background-color: #42b983; border-color: #42b983;"
          >正式提交
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-row>
</template>

<script>
import axios from "axios";
import {
  listMentorship,
  addMentorship,
  delMentorship,
  updateMentorship,
  checkMentorshipUnique,
} from "@/api/system/mentorship";
import {Properties as $download} from "svg-sprite-loader/examples/custom-runtime-generator/build/main";

export default {
  data() {
    return {
      currentRecordId: null, // 新增当前记录ID
      isEdit: false,//判断修改还是插入
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      currentDownloadFile: '',
      meetingRecords: [],// 存储后端返回的导师会议记录数据
      queryParams: {}, // 查询条件
      currentPage: 1, // 当前页
      pageSize: 10, // 每页显示的条数
      totalRecords: 0, // 总记录数
      showDialog: false,
      selectedFile: null,
      uploadMessage: null,
      summaryFilePath: null,
      summaryFileList: [],
      fileList: [],
      activeSemester: '', // 当前学期
      formData: {
        guidanceTopic: '',
        guidanceLocation: '',
        guidanceTime: '',
        studentComment: '',
        summaryFilePath: '',
        photoPaths: [],
        auditStatus: '未提交',
        semester: '',
        auditTime: null,
        auditRemark: '',
      },
      rules: {
        guidanceTopic: [
          {required: true, message: '指导主题不能为空', trigger: 'blur'}
        ],
        guidanceLocation: [
          {required: true, message: '指导地点不能为空', trigger: 'blur'}
        ],
        guidanceTime: [
          {required: true, message: '请选择指导时间', trigger: 'change'}
        ]
      }
    };
  },
  mounted() {
    // 获取学期数据
    this.activeSemester = this.$route.query.semester || '未知学期';
    this.formData.semester = this.activeSemester;
    this.fetchMeetingRecords();  // 在页面加载时获取数据
  },
  methods: {
// 修改后的总结文档处理方法
    handleSummaryChange(file, fileList) {
      if (fileList.length > 1) {
        this.$message.warning('只能上传一个文件')
        fileList.splice(0, 1)
      }

      // 关键修改：获取原生文件对象
      this.summaryFileList = fileList
      this.selectedFile = file.raw // 使用 raw 属性获取原生 File
      console.log(this.selectedFile)
    },

    // 文件移除回调
    handleSummaryRemove() {
      this.summaryFileList = []
    },

    $download() {
      return $download
    },
    //保存草稿
    async handleSave() {
      await this.submitData("未提交");
    },
    //正式提交
    async handleSubmit() {
      await this.submitData("未审核");
    },

    // 删除未提交记录
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该记录吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        const response = await delMentorship(row.recordId);
        if (response.code === 200) {
          this.$message.success('删除成功');
          this.initData();
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`删除失败: ${error.message}`);
        }
      }
    },

    // 处理草稿修改
    handleEditDraft(row) {
      this.handleEdit(row);
    },

    handleEdit(row) {
      const rawData = JSON.parse(JSON.stringify(row));
      this.formData = {...rawData};
      this.isEdit = true;
      this.currentRecordId = rawData.id;
      this.showDialog = true;

      const proofMaterial = this.parseMaterial(rawData.photoPaths);
      this.fileList = proofMaterial.map((path, index) => ({
        uid: Date.now() + index,
        name: path.split('/').pop(),
        url: `${process.env.VUE_APP_BASE_API}/profile/${encodeURIComponent(path)}`,
        status: 'success',
        isOld: true,
        path: path
      })).filter(Boolean);
    },

    parseMaterial(material) {
      try {
        if (Array.isArray(material)) return [...material];
        if (typeof material === 'string') {
          const cleaned = material.replace(/__ob__:.*?($$|$$)/gs, '');
          return JSON.parse(cleaned);
        }
        return [];
      } catch (e) {
        console.error('材料解析失败:', e);
        return [];
      }
    },

    // 生成带时间戳的文件名
    generateSummaryFileName() {
      const date = new Date().toISOString().slice(0, 10);
      const ext = this.getSummaryFileExtension();
      return `summaryFilePath_${date}_${Math.random().toString(36).substr(2, 5)}.${ext}`;
    },

    // 获取文件扩展名
    getSummaryFileExtension() {
      if (!this.selectedFile) return '';
      const match = this.selectedFile.name.match(/\.([a-zA-Z0-9]+)(\?.*)?$/);
      return match ? match[1].toLowerCase() : '';
    },

    //总结文档下载
    async downloadSummaryDocument(filePath) {
      try {
        const link = document.createElement('a');
        link.href = `${process.env.VUE_APP_BASE_API}/profile/${filePath}`;
        link.download = this.generateSummaryFileName();

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
        console.error("下载错误详情:", error);
      }
    },
    //现场图片下载
    async downloadMeetingPictures(filePaths) {
      try {
        // 解析文件路径
        const paths = typeof filePaths === 'string'
          ? JSON.parse(filePaths)
          : filePaths;
        if (!Array.isArray(paths)) {
          throw new Error("无效的文件路径格式");
        }
        // 处理多个文件下载
        if (paths.length >= 1) {
          this.$confirm(`本次下载包含${paths.length}个图片，是否继续？`, '批量下载提示', {
            confirmButtonText: '立即下载',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            paths.forEach(path => {
              const url = `${process.env.VUE_APP_BASE_API}/profile/${path}`;
              this.downloadSingleFile(url);
            });
          });
        }
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
        console.error("下载错误详情:", error);
      }
    },
    // 下载单个文件
    async downloadSingleFile(filePath) {
      try {
        const response = await axios.get(
          filePath,
          {
            responseType: 'blob',
            headers: {
              Authorization: "Bearer " + localStorage.getItem("token")
            }
          }
        );
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', this.generateFileName1(filePath));
        document.body.appendChild(link);
        link.click();
        URL.revokeObjectURL(url);
        link.remove();
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
      }
    },
    // 给现场图片生成带时间戳的文件名
    generateFileName1(filePath) {
      const originalName = filePath.split('/').pop() || '现场图片';
      const timestamp = new Date().getTime();
      const ext = originalName.split('.').pop() || 'jpg';
      return `${originalName.split('.')[0]}_${timestamp}.${ext}`;
    },

    // 获取完整URL（带缓存清除）
    getFullUrl(filePath) {
      return `${process.env.VUE_APP_BASE_API}/profile/${filePath}`;
    },
    handlePreview(filePath) {
      try {
        const paths = typeof filePath === 'string'
          ? JSON.parse(filePath)
          : filePath;

        if (paths.length === 0) {
          this.$message.error('预览失败,当前没有添加过现场图片');
        }
        if (paths.length > 0) {
          this.previewImages = paths.map(path => this.getFullUrl(path));
          this.currentPreviewIndex = 0;
          this.currentDownloadFile = paths[0];
          this.previewVisible = true;
        }
      } catch (error) {
        this.$message.error('预览失败：文件路径格式不正确');
      }
    },

    handlePreviewFile(file) {
      if (file.isOld) {
        window.open(file.url);
      } else {
        const reader = new FileReader();
        reader.onload = (e) => {
          window.open(e.target.result);
        };
        reader.readAsDataURL(file.raw);
      }
    },
    handleFileChange(file, fileList) {
      this.fileList = fileList.slice(-5); // 保持最多5个文件
    },

    handleFileRemove(file, fileList) {
      this.fileList = fileList;
    },

    // 分页大小变化
    handleSizeChange(size) {
      this.pageSize = size;
      this.fetchMeetingRecords(this.queryParams, this.currentPage, this.pageSize);
    },

    // 当前页变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchMeetingRecords(this.queryParams, this.currentPage, this.pageSize);
    },


    addNewCard() {
      this.showDialog = true;
      this.isEdit = false;
    },
    closeCard() {
      this.summaryFilePath = null;
      this.fileList = [];
      this.currentRecordId = null;
      this.showDialog = false;
      this.formData = {
        guidanceTopic: '',
        guidanceLocation: '',
        guidanceTime: '',
        studentComment: '',
        summaryFilePath: '',
        photoPaths: [],
        //审核状态
        auditStatus: '',
        //学期
        semester: this.activeSemester,
      };
    },
    onFileChange(e) {
      // 当用户选择文件时，更新file变量
      this.summaryFilePath = e.target.files[0];
      this.formData.summaryFilePath = this.summaryFilePath ? this.summaryFilePath.name : '';
    },
    // 数据获取方法
    async fetchMeetingRecords() {
      this.isLoading = true;
      try {
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          studentId: this.$store.state.user.name,
          semester: this.activeSemester,
          fileList: this.fileList,
          ...this.queryParams
        };

        const response = await listMentorship(params);
        if (response.code === 200) {
          this.meetingRecords = response.rows || [];
          this.totalRecords = response.total || 0;

        }
      } catch (error) {
        console.error("获取会议记录失败:", error);
      } finally {
        this.isLoading = false;
      }
    },
    // 统一提交方法
    async submitData(status) {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          // 获取原始记录数据（编辑时）
          const originalRecord = this.meetingRecords.find(
            item => item.recordId === this.currentRecordId
          );
          // 检测关键字段是否修改
          const isKeyFieldChanged = !originalRecord ||
            this.formData.guidanceTopic !== originalRecord.guidanceTopic ||
            this.formData.guidanceLocation !== originalRecord.guidanceLocation ||
            this.formData.guidanceTime !== originalRecord.guidanceTime;

          // 编辑时排除自身
          if (isKeyFieldChanged) {
            // 唯一性校验参数
            const checkParams = {
              studentId: this.$store.state.user.name,
              guidanceTopic: this.formData.guidanceTopic,
              guidanceLocation: this.formData.guidanceLocation,
              guidanceTime: this.formData.guidanceTime,
              semester: this.activeSemester,
            };
            const checkRes = await checkMentorshipUnique(checkParams);
            if (checkRes.code !== 200) {
              return this.$message.error('已存在相同活动记录，不可重复添加');
            }
          }

          const existingPaths = this.fileList
            .filter(file => file.isOld)
            .map(file => file.path);
          const newFiles = this.fileList
            .filter(file => !file.isOld)
            .map(file => file.raw);
          const formData = new FormData();

          // 构建核心数据对象
          const recordData = {
            recordId: this.currentRecordId,
            guidanceTopic: this.formData.guidanceTopic,
            guidanceLocation: this.formData.guidanceLocation,
            guidanceTime: this.formData.guidanceTime,
            semester: this.activeSemester,
            auditStatus: status,
            studentComment: this.formData.studentComment,
            studentId: this.$store.state.user.name,
            summaryFilePath: this.formData.summaryFilePath,
            photoPaths: JSON.stringify(this.fileList.map(file => file.url)),
            auditTime: null,
            auditRemark: "",
            existingProofMaterial: existingPaths,
          };

          // 如果是编辑操作，添加ID字段
          if (this.currentRecordId) {
            recordData.recordId = this.currentRecordId;
          };

          // 构建 JSON 部分（指定类型为 application/json）
          const recordBlob = new Blob(
            [JSON.stringify(recordData)],
            {type: "application/json"}
          );
          formData.append("record", recordBlob);

          //添加文件
          this.fileList.forEach((file) => {
            formData.append("photoPaths", file.raw);
          });

          formData.append('summaryFile', this.selectedFile);

          // 配置 headers
          const config = {
            headers: {
              "Authorization": "Bearer " + localStorage.getItem("token"),
              "Content-Type": "multipart/form-data"
            }
          };

          // 根据模式选择 API 方法
          const apiMethod = this.currentRecordId ? updateMentorship : addMentorship;

          apiMethod(formData, config)
            .then(() => {
              this.$message.success(this.currentRecordId ? "更新成功！" : "提交成功！");
              this.fetchMeetingRecords();
              this.closeCard();
              this.currentRecordId = null; // 清空当前记录ID
            })
            .catch(error => {
              this.$message.error(`操作失败：${error.message}`);
            });
        }
      });
    },
    initData() {
      this.fetchMeetingRecords();  // 在页面加载时获取数据
    },

  }
};
</script>

<style>
h1 {
  color: #333;
}

input, button {
  margin: 10px;
}

.el-form-item__label {
  text-align: right;
}

.form-item-label {
  display: inline-block;
  height: 32px;
  line-height: 32px;
  margin-right: 10px;
}
</style>
