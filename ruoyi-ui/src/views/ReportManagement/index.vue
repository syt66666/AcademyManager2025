<template>
  <el-row type="flex" justify="center">

    <!-- 讲座报告卡片 -->
    <el-card id="reportCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <!-- 顶部标题栏 -->
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">
          <span>📚</span>
          讲座报告
          <span class="current-semester">{{ activeSemester }} 报告记录</span>
        </h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="addNewCard"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <el-table :data="competitionRecords" style="width: 100%" border stripe highlight-current-row>
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="reportTitle" label="题目" min-width="180"></el-table-column>
        <el-table-column prop="reporter" label="报告人" min-width="150"></el-table-column>
        <el-table-column prop="reportDate" label="报告时间" min-width="151"></el-table-column>
        <el-table-column prop="reportContent" label="内容简介" min-width="150"></el-table-column>
        <el-table-column prop="reportLink" label="链接" min-width="150"></el-table-column>
        <el-table-column label="报告海报" width="120">
          <template v-slot:default="scope">
            <img
              :src="getImageUrl(scope.row.lecturePoster)"
              alt="报告海报"
              style="width: 50px; height: 50px; cursor: pointer;"
              v-if="scope.row.lecturePoster"
              @click="handleImageClick(scope.row.lecturePoster)"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column label="心得体会" width="120">
          <template v-slot:default="scope">
            <div class="proof-material-cell">
              <el-button
                type="primary"
                icon="el-icon-download"
                size="mini"
                v-if="scope.row.reportFeeling"
                @click="downloadReportFeeling(scope.row.reportFeeling)"
              >下载
              </el-button>
              <span v-else>暂未上交过心得体会</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="现场照片" width="120">
          <template v-slot:default="scope">
            <div class="proof-material-cell">
              <el-link
                type="primary"
                :underline="false"
                @click="handlePreview(scope.row.reportPicture)"
                style="margin-right: 10px;"
              >
                <i class="el-icon-view"></i> 预览
              </el-link>
              <el-button
                type="primary"
                icon="el-icon-download"
                size="mini"
                @click="downloadReportPicture(scope.row.reportPicture)"
                :disabled="!scope.row.reportPicture"
              >下载
              </el-button>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button
              v-if="formatAuditStatus(scope.row.auditStatus) === '未通过'"
              type="text"
              size="mini"
              @click="handleEditDraft(scope.row)"
            >重新提交
            </el-button>

            <template v-if="formatAuditStatus(scope.row.auditStatus) === '未提交'">
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
              v-if="['未审核', '已通过'].includes(formatAuditStatus(scope.row.auditStatus))"
              type="info"
              size="mini"
            >不可修改
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" min-width="150">
          <template slot-scope="scope">
            <span>
              <el-tag v-if="formatAuditStatus(scope.row.auditStatus) === '未审核'"
                      type="warning">{{ formatAuditStatus(scope.row.auditStatus) }}</el-tag>
              <el-tag v-else-if="formatAuditStatus(scope.row.auditStatus) === '已通过'"
                      type="success">{{ formatAuditStatus(scope.row.auditStatus) }}</el-tag>
              <el-tag v-else-if="formatAuditStatus(scope.row.auditStatus) === '未通过'"
                      type="danger">{{ formatAuditStatus(scope.row.auditStatus) }}</el-tag>
              <el-tag v-else-if="formatAuditStatus(scope.row.auditStatus) === '未提交'"
                      type="info">{{ formatAuditStatus(scope.row.auditStatus) }}</el-tag>
              <el-tag v-else>未知状态</el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="reportAdmitTime" label="报告提交时间" min-width="151"></el-table-column>
        <el-table-column prop="auditTime" label="审核时间" min-width="150"></el-table-column>
        <el-table-column prop="auditRemark" label="审核意见" min-width="150"></el-table-column>
        <el-table-column prop="nickName" label="审核人姓名" min-width="150"></el-table-column>

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
          alt="证明材料预览"
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

    <!-- 图片预览对话框 -->
    <el-dialog :visible.sync="dialogVisible" title="查看图片" width="50%">
      <div style="position: relative;">
        <img :src="getImageUrl(currentLecturePoster)" alt="报告海报大图" style="width: 100%; height: auto;"/>
        <div style="position: absolute; bottom: 20px; right: 20px;">
          <el-button
            type="primary"
            icon="el-icon-download"
            @click="downloadFiles(currentLecturePoster)"
            style="background-color: #42b983; border-color: #42b983;">
            下载图片
          </el-button>
        </div>
      </div>
    </el-dialog>

    <transition name="fade">
      <el-dialog :visible.sync="showSecondCard" id="newCard" style="width: 100%; margin-top: 2vh;margin-left: 1%"
                 @close="closeCard">
        <div style="display: flex; align-items: center; justify-content: center;">
          <h1>报告填写</h1>
        </div>
        <div style="display: flex; flex-direction: column;">
          <el-form ref="form" :model="formData" label-width="120px" label-position="right">
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">题目</span>
                <el-input v-model="formData.reportTitle" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">报告人</span>
                <el-input v-model="formData.reporter" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">时间</span>
                <el-date-picker v-model="formData.reportDate" type="datetime"
                                style="width: 100%; flex: 1;"></el-date-picker>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">讲座简介</span>
                <el-input v-model="formData.reportContent" type="textarea" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;">链接</span>
                <el-input v-model="formData.reportLink" style="width: 100%; flex: 1;"></el-input>
              </div>
            </el-form-item>
            <el-form-item>
              <div style="display: flex; align-items: center;">
                <span class="form-item-label" style="font-size: 16px;height: auto">报告心得体会上传</span>
                <input type="file" @change="onFileChange" accept="*/*" ref="fileInput"/>
              </div>
            </el-form-item>
            <el-form-item label="报告海报上传" prop="lecturePoster">
              <imageUpload
                v-model="formData.lecturePoster"
                :limit="1"
                :fileSize="5"
                :fileType="['png','jpg','jpeg']"
                :isShowTip="true"
              />
            </el-form-item>
            <!-- 报告现场图片上传 -->
            <el-form-item label="现场图片上传" prop="reportPicture">
              <el-upload
                multiple
                :limit="5"
                :value="pushReportPicture"
                :auto-upload="false"
                :on-change="handleFileChange"
                :on-remove="handleRemoveFile"
                :file-list="pushReportPicture"
              >
                <i class="el-icon-plus"></i>
                <template #tip>
                  <div class="el-upload__tip">最少上传3个图片，最多上传5个图片，单个不超过10MB
                    <br>
                    <span style="color: red; font-size: 16px;">注意:如果用户选择正式提交，必须填写报告心得和现场图片，且之前报告和现场图片不会保留</span>
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
import {upLoad, fetchLectureReportRecords, updateActivity, delLectureReport} from "@/api/student/letcure";
import {formatDate} from "@/utils";

export default {
  data() {
    return {
      isEdit: false,//判断修改还是插入
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      currentDownloadFile: '',
      dialogVisible: false,
      currentLecturePoster: '',
      baseUrl: process.env.VUE_APP_BASE_API,
      competitionRecords: [],// 存储后端返回的讲座报告记录数据
      queryParams: {}, // 查询条件
      currentPage: 1, // 当前页
      pageSize: 10, // 每页显示的条数
      totalRecords: 0, // 总记录数
      showSecondCard: false,
      newCardInfo: '',
      selectedFile: null,
      uploadMessage: null,
      reportFeeling: null,
      currentImage: '',
      pushReportPicture: [],
      formData: {
        reportTitle: '',
        reporter: '',
        reportDate: '',
        reportContent: '',
        reportLink: '',
        lecturePoster: '',
        reportPicture: [],
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
    this.formData.semester = this.findSemester(this.activeSemester);
    this.fetchLectureReportRecords();  // 在页面加载时获取数据
  },
  methods: {

    async handleSave() {
      this.formData.auditStatus = 3;
      this.submitForm();
    },
    async handleSubmit() {
      this.formData.auditStatus = 0;
      this.submitForm();
    },

    // 删除未提交记录
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该记录吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        const response = await delLectureReport(row.reportId);
        if (response.code === 200) {
          this.$message.success('删除成功');
          await this.initData();
          // localStorage.removeItem(this.getDraftKey());
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
      localStorage.removeItem(this.getDraftKey());
    },
    // 处理编辑未通过记录
    handleEdit(row) {
      this.formData = {
        // ...row,
        reportTitle: row.reportTitle,
        reporter: row.reporter,
        reportDate: row.reportDate,
        reportContent: row.reportContent,
        reportLink: row.reportLink,
        lecturePoster: row.lecturePoster,
        reportPicture: [],
        reportId: row.reportId,
        semester: this.formData.semester,
      };
      this.isEdit = true;
      this.showSecondCard = true;
    },


    // 生成带时间戳的文件名
    generateFeelingFileName() {
      const date = new Date().toISOString().slice(0, 10);
      const ext = this.getFeelingFileExtension();
      return `reportFeeling_${date}_${Math.random().toString(36).substr(2, 5)}.${ext}`;
    },

    // 获取文件扩展名
    getFeelingFileExtension() {
      try {
        return this.currentImage.split('.').pop().split(/[#?]/)[0] || 'docx';
      } catch {
        return 'docx';
      }
    },
    //心得体会下载
    async downloadReportFeeling(filePaths) {
      try {
        const link = document.createElement('a');
        link.href = `${process.env.VUE_APP_BASE_API}/profile/${filePaths}`;
        link.download = this.generateFeelingFileName();

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
        console.error("下载错误详情:", error);
      }
    },
    //现场报告多张图片下载
    async downloadReportPicture(filePaths) {
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
    // 给现场报告图片生成带时间戳的文件名
    generateFileName1(filePath) {
      const originalName = filePath.split('/').pop() || '现场报告图片';
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
      this.pushReportPicture = fileList.slice(-5); // 保持最多5个文件
    },
    handleRemoveFile(file, fileList) {
      this.pushReportPicture = fileList;  // 更新文件列表
    },

    //展开报告海报大图
    handleImageClick(imageUrl) {
      this.currentLecturePoster = imageUrl;
      this.dialogVisible = true;
    },
    // 获取完整图片路径
    getImageUrl(path) {
      if (!path) return '';
      // 处理两种路径情况：
      // 1. 完整路径直接返回
      // 2. 相对路径拼接基础URL
      return path.startsWith('http')
        ? `${path}?t=${Date.now()}`  // 添加时间戳防止缓存
        : `${this.baseUrl}${path}?t=${Date.now()}`;
    },
    // 生成带时间戳的文件名
    generateFileName() {
      const date = new Date().toISOString().slice(0, 10);
      const ext = this.getFileExtension();
      return `activity_${date}_${Math.random().toString(36).substr(2, 5)}.${ext}`;
    },

    // 获取文件扩展名
    getFileExtension() {
      try {
        return this.currentImage.split('.').pop().split(/[#?]/)[0] || 'jpg';
      } catch {
        return 'jpg';
      }
    },
    // 下载图片
    downloadFiles(filePaths) {
      if (!filePaths) {
        this.$message.warning('没有可下载的图片');
        return;
      }

      const link = document.createElement('a');
      link.href = this.getImageUrl(filePaths);
      link.download = this.generateFileName();

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    // 分页大小变化
    handleSizeChange(size) {
      this.pageSize = size;
      this.fetchLectureReportRecords(this.queryParams, this.currentPage, this.pageSize);
    },

    // 当前页变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchLectureReportRecords(this.queryParams, this.currentPage, this.pageSize);
    },

    //转化学期
    findSemester(semester) {
      const semesterTrimmed = semester.trim();  // 去除前后空格
      switch (semester) {
        case '大一上':
          return 1;
        case '大一下':
          return 2;
        case '大二上':
          return 3;
        case '大二下':
          return 4;
        case '大三上':
          return 5;
        case '大三下':
          return 6;
        case '大四上':
          return 7;
        case '大四下':
          return 8;
        default:
          return 0;
      }
    },

    // 格式化审核状态
    formatAuditStatus(status) {
      switch (status) {
        case 0:
          return "未审核";
        case 1:
          return "已通过";
        case 2:
          return "未通过";
        case 3:
          return "未提交";
        default:
          return "未审核";
      }
    },
    addNewCard() {
      this.showSecondCard = true;
      this.isEdit = false;
    },
    closeCard() {
      this.showSecondCard = false;
      this.reportFeeling = null;
      this.pushReportPicture = [];
      this.showSecondCard = false;
      this.formData = {
        reportTitle: '',
        reporter: '',
        reportDate: '',
        reportContent: '',
        reportLink: '',
        lecturePoster: '',
        reportPicture: [],
        //审核状态
        auditStatus: '',
        //学期
        semester: this.findSemester(this.activeSemester),
      };
    },
    onFileChange(e) {
      // 当用户选择文件时，更新file变量
      this.reportFeeling = e.target.files[0];
    },
    // onImageChange(e) {
    //   // 当用户选择图片时，更新images数组
    //   this.lecturePoster = Array.from(e.target.files);
    // },
    async fetchLectureReportRecords(queryParams = {}, currentPage = 1, pageSize = 20) {
      this.isLoading = true; // 设置为加载状态
      try {
        const data = await fetchLectureReportRecords({
          ...queryParams,
          pageNum: currentPage,
          pageSize: pageSize
        });
        console.log(data);
        console.log(data.data);
        this.competitionRecords = data.data.rows || []; // 假设后端返回的数据格式包含 rows
        this.totalRecords = data.data.total || 0;       // 假设返回总记录数 total
      } catch (error) {
        console.error("Error fetching competition records:", error);
      } finally {
        this.isLoading = false; // 无论成功还是失败，结束加载状态
      }
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          //如果不是保存
          if (this.formData.auditStatus !== 3) {
            // 如果文件数量少于3张，弹出提示
            if (this.pushReportPicture.length < 3) {
              this.$message.error('最少上传3张图片');
              return;
            }
            // 如果文件数量大于5张，弹出提示
            if (this.pushReportPicture.length > 5) {
              this.$message.error('最多上传5张图片');
              return;
            }
          }
          console.log('表单数据:', this.formData.reportDate);
          //将本地时间转为 ISO 格式的 UTC 时间
          // if (this.formData.reportDate && !isNaN(Date.parse(this.formData.reportDate))) {
          //   this.formData.reportDate = new Date(`${this.formData.reportDate} UTC`).toISOString();
          // }
          const formData = new FormData();
          const json = JSON.stringify(this.formData);
          formData.append('studentLectureReport', json);
          formData.append('reportFeeling', this.reportFeeling);
          // 添加图片（字段名必须与后端一致）
          this.pushReportPicture.forEach((file) => {
            formData.append("reportPicture", file.raw);
          });
          console.log('表单数据:', this.formData);
          console.log('传递后端数据:', formData);
          // 可以使用 axios 或 fetch 发送请求
          // 例如：
          if (this.isEdit) {
            //修改信息
            updateActivity(formData).then(response => {
              console.log("+++++++++", response);
              this.$message.success('保存成功');
              this.initData();
            })
              .catch(error => {
                console.error(error);
                this.initData();
              });
          } else {
            //第一次添加信息
            upLoad(formData).then(response => {
              console.log("+++++++++", response);
              this.$message.success('提交成功');
              this.initData();
            })
              .catch(error => {
                console.error(error);
                this.initData();
              });
          }
        } else {
          this.$message.error('请填写完整表单信息');
        }
      });
    },
    initData() {
      this.reportFeeling = null;
      this.pushReportPicture = [];
      this.showSecondCard = false;
      this.competitionRecords = [];
      this.formData = {
        reportTitle: '',
        reporter: '',
        reportDate: '',
        reportContent: '',
        reportLink: '',
        lecturePoster: '',
        reportPicture: [],
        //审核状态
        auditStatus: '',
        //学期
        semester: this.findSemester(this.activeSemester),
      };
      this.$refs.fileInput.value = '';
      this.fetchLectureReportRecords();  // 在页面加载时获取数据
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
