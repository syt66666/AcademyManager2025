<template>
  <el-row type="flex" justify="center">

    <!-- 导师会议卡片 -->
    <el-card id="meetingCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <!-- 顶部标题栏 -->
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">
          <span>📚</span>
          导师会议
          <span class="current-semester">{{ activeSemester }} 会议记录</span>
        </h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="addNewCard"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <el-table :data="meetingRecords" style="width: 100%" border stripe highlight-current-row>
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="guidanceTopic" label="会议主题" min-width="180"></el-table-column>
        <el-table-column prop="studentId" label="学生学号" min-width="180"></el-table-column>
        <el-table-column prop="tutorName" label="导师" min-width="150"></el-table-column>
        <el-table-column prop="guidanceLocation" label="会议地点" min-width="150"></el-table-column>
        <el-table-column prop="guidanceTime" label="会议时间" min-width="151"></el-table-column>
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
              <span v-else>暂未上传总结文档</span>
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
              <span v-else>暂未上传现场图片</span>
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
              v-if="['待审核', '已通过'].includes(scope.row.auditStatus)"
              type="info"
              size="mini"
            >不可修改
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" min-width="150">
          <template slot-scope="scope">
            <span>
              <el-tag v-if="scope.row.auditStatus === '待审核'"
                      type="warning">{{ scope.row.auditStatus }}</el-tag>
              <el-tag v-else-if="scope.row.auditStatus === '已通过'"
                      type="success">{{ scope.row.auditStatus }}</el-tag>
              <el-tag v-else-if="scope.row.auditStatus === '未通过'"
                      type="danger">{{ scope.row.auditStatus }}</el-tag>
              <el-tag v-else-if="scope.row.auditStatus === '未提交'"
                      type="info">{{ scope.row.auditStatus }}</el-tag>
              <el-tag v-else>未知状态</el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" min-width="150"></el-table-column>
        <el-table-column prop="auditRemark" label="审核意见" min-width="150"></el-table-column>
        <el-table-column prop="auditorName" label="审核人姓名" min-width="150"></el-table-column>

      </el-table>

      <!-- 分页器 -->
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

    <transition name="fade">
      <el-dialog :visible.sync="showSecondCard" id="newCard" style="width: 100%; margin-top: 2vh;margin-left: 1%"
                 @close="closeCard">
        <div style="display: flex; align-items: center; justify-content: center;">
          <h1>会议填写</h1>
        </div>
        <div style="display: flex; flex-direction: column;">
          <el-form ref="form" :model="formData" label-width="120px" label-position="right">
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">会议主题</span>
                <el-input v-model="formData.guidanceTopic" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">会议地点</span>
                <el-input v-model="formData.guidanceLocation" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">会议时间</span>
                <el-date-picker v-model="formData.guidanceTime" type="datetime"
                                style="width: 100%; flex: 1;"></el-date-picker>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">导师评价</span>
                <el-input v-model="formData.studentComment" type="textarea" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">总结文档上传</span>
                <input type="file" @change="onFileChange" accept="*/*" ref="fileInput"/>
              </div>
            </el-form-item>
            <!-- 现场图片上传 -->
            <el-form-item label="现场图片上传" prop="photoPaths">
              <el-upload
                multiple
                :limit="5"
                :value="pushMeetingPictures"
                :auto-upload="false"
                :on-change="handleFileChange"
                :on-remove="handleRemoveFile"
                :file-list="pushMeetingPictures"
              >
                <i class="el-icon-plus"></i>
                <template #tip>
                  <div class="el-upload__tip">最少上传3个图片，最多上传5个图片，单个不超过10MB
                    <br>
                    <span style="color: red; font-size: 16px;">注意:如果用户选择正式提交，必须填写总结文档和现场图片，且之前总结文档和现场图片不会保留</span>
                  </div>
                </template>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center; justify-content: right;">
                <el-button type="info" @click="handleSave">保存草稿</el-button>
                <el-button type="primary" @click="handleSubmit" style="margin-right: 3vh">正式提交</el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </el-dialog>
    </transition>
  </el-row>
</template>

<script>
import axios from "axios";
import {
  getMentorship,
  listMentorship,
  addMentorship,
  delMentorship,
  updateMentorship,
  checkMentorshipUnique,
} from "@/api/system/mentorship";
import {formatDate} from "@/utils";
import {addActivity, checkActivityUnique, listActivity, updateActivity} from "@/api/system/activity";
import {dataScope} from "@/api/system/role";
import {addRecord, updateRecord} from "@/api/student/competition";

export default {
  data() {
    return {
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
      showSecondCard: false,
      selectedFile: null,
      uploadMessage: null,
      summaryFilePath: null,
      pushMeetingPictures: [],
      formData: {
        guidanceTopic: '',
        guidanceLocation: '',
        guidanceTime: '',
        studentComment: '',
        summaryFilePath: '',
        photoPaths: [],
        //审核状态
        auditStatus: '',
        //学期
        semester: '',
      },
      activeSemester: '', // 当前学期
    };
  },
  mounted() {
    // 获取学期数据
    this.activeSemester = this.$route.query.semester || '未知学期';
    this.formData.semester = this.activeSemester;
    this.fetchMeetingRecords();  // 在页面加载时获取数据
  },
  methods: {
//保存草稿
    async handleSave() {
      this.submitForm("未提交");
    },
    //正式提交
    async handleSubmit() {
      this.submitForm("待审核");
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
          await this.initData();
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
    // 处理编辑未通过记录
    handleEdit(row) {
      this.formData = {
        guidanceTopic: row.guidanceTopic,
        guidanceLocation: row.guidanceLocation,
        guidanceTime: row.guidanceTime,
        studentComment: row.studentComment,
        summaryFilePath: row.summaryFilePath,
        photoPaths: [],
        recordId: row.recordId,
        semester: this.formData.semester,
      };
      this.isEdit = true;
      this.showSecondCard = true;
    },

    // 生成带时间戳的文件名
    generateSummaryFileName() {
      const date = new Date().toISOString().slice(0, 10);
      const ext = this.getSummaryFileExtension();
      return `summaryFilePath_${date}_${Math.random().toString(36).substr(2, 5)}.${ext}`;
    },

    // 获取文件扩展名
    getSummaryFileExtension() {
      try {
        return this.selectedFile.name.split('.').pop().split(/[#?]/)[0] || 'docx';
      } catch {
        return 'docx';
      }
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
        if (paths.length > 1) {
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
        } else if (paths.length === 1) {
          this.previewImage = this.getFullUrl(paths[0]);
          this.currentDownloadFile = paths[0];
          this.previewVisible = true;
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
    handleFileChange(file, fileList) {

      this.pushMeetingPictures = fileList.slice(-5); // 保持最多5个文件
    },

    handleRemoveFile(file, fileList) {
      this.pushMeetingPictures = fileList;  // 更新文件列表
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
      this.showSecondCard = true;
      this.isEdit = false;
    },
    closeCard() {
      this.summaryFilePath = null;
      this.pushMeetingPictures = [];
      this.showSecondCard = false;
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
      console.log("文件：",this.summaryFilePath)
    },
    // 数据获取方法
    async fetchMeetingRecords() {
      this.isLoading = true;
      try {
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          studentId: this.$store.state.user.name,
          semester:this.activeSemester,
          pushMeetingPictures:this.pushMeetingPictures,
          ...this.queryParams
        };

        const response = await listMentorship(params);
        console.log("获取的参数：",params)
        if (response.code === 200) {
          this.meetingRecords = response.rows || [];
          console.log("会议记录列表:", this.meetingRecords);

          this.totalRecords = response.total || 0;

        }
      } catch (error) {
        console.error("获取会议记录失败:", error);
      } finally {
        this.isLoading = false;
      }
    },
    // async fetchMeetingRecords(queryParams = {}, currentPage = 1, pageSize = 20) {
    //   this.isLoading = true; // 设置为加载状态
    //   try {
    //     const data = await listMentorship({
    //       ...queryParams,
    //       pageNum: currentPage,
    //       pageSize: pageSize
    //     });
    //     console.log(data);
    //     console.log(data.data);
    //     this.meetingRecords = data.data.rows || []; // 假设后端返回的数据格式包含 rows
    //     this.totalRecords = data.data.total || 0;       // 假设返回总记录数 total
    //   } catch (error) {
    //     console.error("Error fetching meeting records:", error);
    //   } finally {
    //     this.isLoading = false; // 无论成功还是失败，结束加载状态
    //   }
    // },
    // 统一提交方法
    async submitForm(status) {
      try {
        const checkParams = {
          studentId: this.$store.state.user.name,
          guidanceTopic: this.formData.guidanceTopic,
          guidanceLocation: this.formData.guidanceLocation,
          guidanceTime: this.formData.guidanceTime,
          semester: this.activeSemester,
          studentComment: this.formData.studentComment,
          auditStatus: status,
        };
        const checkRes = await checkMentorshipUnique(checkParams);
        if (checkRes.code !== 200) {
          return this.$message.error('已存在相同活动记录，不可重复添加');
        }

        console.log("pushMeetingPictures", this.pushMeetingPictures);

        // 创建 FormData 对象
        const formData = new FormData();
        const params = {
          ...this.formData,
          auditTime: null,
          auditRemark: "",
          auditStatus: status,
          studentId: this.$store.state.user.name,
          semester: this.activeSemester,
          // 将 photoPaths 转换为 JSON 字符串
          photoPaths: JSON.stringify(this.pushMeetingPictures.map(file => file.url))
        };
        console.log("参数：", params);

        // 构建 JSON 部分（指定类型为 application/json）
        const recordBlob = new Blob(
          [JSON.stringify(params)],
          { type: "application/json" }
        );
        formData.append("record", recordBlob);

        // 添加文件
        console.log("pushMeetingPictures参数：", this.pushMeetingPictures);
        this.pushMeetingPictures.forEach((file) => {
          formData.append("photoPaths", file.raw);
        });
        console.log("photoPaths：", formData.get("photoPaths"));

        // 配置 headers
        const config = {
          headers: {
            "Authorization": "Bearer " + localStorage.getItem("token"),
            "Content-Type": "multipart/form-data"
          }
        };

        // 根据模式选择 API 方法
        const apiMethod = this.isEdit ? updateMentorship : addMentorship;

        apiMethod(formData, config)
          .then(() => {
            this.$message.success(this.isEdit ? "更新成功！" : "提交成功！");
            this.fetchMeetingRecords();
            this.closeCard();
          })
          .catch(error => {
            this.$message.error(`操作失败：${error.message}`);
          });
      } catch (error) {
        console.error('操作失败:', error);
        this.$message.error(`操作失败: ${error.message || '服务器错误'}`);
      }
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
