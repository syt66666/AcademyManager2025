<template>
  <el-row type="flex" justify="center" style="margin-top: 4vh;">
    <!-- 竞赛列表卡片 -->
    <el-card id="reportCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <!-- 顶部标题栏 -->
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">
          <span>📚</span>
          科创竞赛
          <span class="current-semester">{{ activeSemester }} 竞赛记录</span>
        </h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="openDialog"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <!-- 竞赛表格 -->
      <el-table
        :data="competitionRecords"
        style="width: 100%"
        border
        stripe
        highlight-current-row
        v-loading="loading"
        element-loading-text="拼命加载中..."
        element-loading-spinner="el-icon-loading"
      >
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="competitionName" label="竞赛名称" min-width="180"></el-table-column>
        <el-table-column prop="competitionLevel" label="竞赛级别" min-width="150"></el-table-column>
        <el-table-column prop="awardLevel" label="竞赛奖项" min-width="150"></el-table-column>
        <el-table-column prop="auditTime" label="审核时间" min-width="150"></el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" min-width="150"></el-table-column>
        <!--        <el-table-column label="证明材料" width="120">
                  <template v-slot:default="scope">
                    <el-button
                      type="primary"
                      icon="el-icon-download"
                      size="mini"
                      @click="downloadFiles(scope.row.proofMaterial)"
                      :disabled="!scope.row.proofMaterial"
                    >下载</el-button>
                  </template>
                </el-table-column>-->
        <!-- 修改证明材料列的模板 -->
        <el-table-column label="证明材料" width="200"> <!-- 加宽列宽 -->
          <template v-slot:default="scope">
            <div class="proof-material-cell">
              <!-- 添加可点击的预览链接 -->
              <el-link
                type="primary"
                :underline="false"
                @click="handlePreview(scope.row.proofMaterial)"
                style="margin-right: 10px;"
              >
                <i class="el-icon-view"></i> 预览
              </el-link>

              <!-- 原有下载按钮 -->
              <el-button
                type="primary"
                icon="el-icon-download"
                size="mini"
                @click="downloadFiles(scope.row.proofMaterial)"
                :disabled="!scope.row.proofMaterial"
              >下载
              </el-button>
            </div>
          </template>
        </el-table-column>


        <el-table-column prop="auditStatus" label="审核状态" min-width="150">
          <template v-slot:default="scope">
            <el-tag v-if="scope.row.auditStatus === '未审核'" type="warning">{{ scope.row.auditStatus }}</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '已通过'" type="success">{{ scope.row.auditStatus }}</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '未通过'" type="danger">{{ scope.row.auditStatus }}</el-tag>
            <el-tag v-else>未知状态</el-tag>
          </template>
        </el-table-column>

      </el-table>

      <!-- 分页组件 -->
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        :total="totalRecords"
        :page-sizes="[10, 20, 30, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="text-align: center; margin-top: 10px;"
      />
    </el-card>
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

    <!-- 竞赛填写弹出对话框 -->
    <el-dialog :visible.sync="showDialog" title="竞赛填写" width="50%" @close="closeDialog">
      <el-form ref="form" :model="formData" :rules="rules" label-width="120px" style="padding: 20px;">
        <!-- 竞赛名称 -->
        <el-form-item label="竞赛名称" prop="competitionName">
          <el-input v-model="formData.competitionName" placeholder="请输入竞赛名称" style="width: 100%;"></el-input>
        </el-form-item>

        <!-- 竞赛级别 -->
        <el-form-item label="竞赛级别" prop="competitionLevel">
          <el-select v-model="formData.competitionLevel" placeholder="请选择竞赛级别" style="width: 100%;">
            <el-option label="院级" value="院级"></el-option>
            <el-option label="校级" value="校级"></el-option>
            <el-option label="省级" value="省级"></el-option>
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="国际级" value="国际级"></el-option>
          </el-select>
        </el-form-item>

        <!-- 奖项等级 -->
        <el-form-item label="奖项等级" prop="awardLevel">
          <el-select v-model="formData.awardLevel" placeholder="请选择奖项" style="width: 100%;">
            <el-option label="特等奖" value="特等奖"></el-option>
            <el-option label="一等奖" value="一等奖"></el-option>
            <el-option label="二等奖" value="二等奖"></el-option>
            <el-option label="三等奖" value="三等奖"></el-option>
            <el-option label="优秀奖" value="优秀奖"></el-option>
            <el-option label="未获奖" value="未获奖"></el-option>
          </el-select>
        </el-form-item>

        <!-- 图片上传 -->
        <el-form-item label="图片上传" prop="proofMaterial">
          <el-upload
            multiple
            :limit="5"
            :file-list="fileList"
            :auto-upload="false"
            :on-change="handleFileChange"
          >
            <i class="el-icon-plus"></i>
            <template #tip>
              <div class="el-upload__tip">最多上传5个文件，单个不超过10MB</div>
            </template>
          </el-upload>

        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm" style="float: right;">提交</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-row>
</template>

<script>
import axios from "axios";
import {addCompetitionRecord, fetchCompetitionRecords} from "@/api/student/competition";

export default {
  data() {
    return {
      uploadUrl: "http://localhost:8080/competition/add", // 上传接口
      fileList: [], // 已上传的文件列表
      previewVisible: false,
      previewImages: [],
      currentPreviewIndex: 0,
      loading: false,
      previewImage: '',
      currentDownloadFile: '',
      currentImage: '', // 当前点击的图片 URL
      competitionRecords: [], // 竞赛记录数据
      queryParams: {}, // 查询条件
      currentPage: 1, // 当前页
      pageSize: 10, // 每页显示的条数
      totalRecords: 0, // 总记录数
      showDialog: false, // 控制对话框显示
      activeSemester: '', // 当前学期
      formData: {
        competitionName: '', // 竞赛名称
        competitionLevel: '', // 竞赛级别
        awardLevel: '', // 奖项
        scholarshipPoints: '', // 折合分数
        proofMaterial: '', // 图片地址
      },
      rules: {
        competitionName: [{required: true, message: '竞赛名称不能为空', trigger: 'blur'}],
        competitionLevel: [{required: true, message: '请选择竞赛级别', trigger: 'change'}],
        awardLevel: [{required: true, message: '请选择奖项', trigger: 'change'}],
        scholarshipPoints: [{required: true, message: '折合分数不能为空', trigger: 'blur'}],
      },
    };
  },
  mounted() {
    // 获取学期数据
    this.activeSemester = this.$route.query.semester || '未知学期';
    this.fetchCompetitionRecords(); // 加载数据
  },
  methods: {
    async downloadFiles(filePaths) {
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
          this.$confirm(`本次下载包含${paths.length}个文件，是否继续？`, '批量下载提示', {
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
        link.setAttribute('download', this.generateFileName(filePath));
        document.body.appendChild(link);
        link.click();
        URL.revokeObjectURL(url);
        link.remove();
      } catch (error) {
        this.$message.error(`下载失败: ${error.message}`);
      }
    },
    // 生成带时间戳的文件名
    generateFileName(filePath) {
      const originalName = filePath.split('/').pop() || '证明材料';
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
// 获取文件名
    getFileName(filePath) {
      return filePath.split('/').pop() || '证明材料';
    },
    handleFileChange(file, fileList) {
      this.fileList = fileList.slice(-5); // 保持最多5个文件
    },
// 打开对话框
    openDialog() {
      this.showDialog = true;
    },

    // 关闭对话框
    closeDialog() {
      this.showDialog = false;
      this.fileList = []; // 清空已上传的文件列表
    },

    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const formData = new FormData();

          // 构建 JSON 部分（指定类型为 application/json）
          const recordBlob = new Blob(
            [JSON.stringify({
              competitionName: this.formData.competitionName,
              competitionLevel: this.formData.competitionLevel,
              awardLevel: this.formData.awardLevel,
              scholarshipPoints: this.formData.scholarshipPoints,
              semester: this.activeSemester
            })],
            {type: "application/json"}
          );
          formData.append("record", recordBlob);

          // 添加文件（字段名必须与后端一致）
          this.fileList.forEach((file) => {
            formData.append("proofMaterial", file.raw);
          });

          // 添加 Token 到 Headers
          const config = {
            headers: {
              "Authorization": "Bearer " + localStorage.getItem("token"),
            }
          };

          addCompetitionRecord(formData, config)
            .then(() => {
              this.$message.success("提交成功！");
              this.fetchCompetitionRecords();
              this.closeDialog();
            })
            .catch(error => {
              this.$message.error("提交失败：" + error.message);
            });
        }
      });
    }
    ,



    // 重置表单
    resetForm() {
      this.formData = {
        competitionName: "",
        competitionLevel: "",
        awardLevel: "",
        scholarshipPoints: "",
        proofMaterial: "",
      };
      this.fileList = []; // 清空已上传的文件列表
    },

    // 分页大小变化
    handleSizeChange(size) {
      this.pageSize = size;
      this.fetchCompetitionRecords(this.queryParams, this.currentPage, this.pageSize);
    },

    // 当前页变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchCompetitionRecords(this.queryParams, this.currentPage, this.pageSize);
    },
    handleImageClick(imageUrl) {
      this.previewImage = this.getFullUrl(imageUrl);
      this.previewVisible = true; // 使用正确的变量名
    }
    ,

    // 加载竞赛记录
    async fetchCompetitionRecords(queryParams = {}, currentPage = 1, pageSize = 10) {
      try {
        const response = await fetchCompetitionRecords({
          ...queryParams,
          semester: this.activeSemester, // 传递学期参数
          pageNum: currentPage,
          pageSize: pageSize,
        });

        if (response && response.data) {
          this.competitionRecords = response.data.rows;
          this.totalRecords = response.data.total || 0;
        }
      } catch (error) {
        console.error("Error fetching competition records:", error);
      }
    },
  },
};
</script>

<style scoped>
h1 {
  color: #333;
}

input, button {
  margin: 10px;
}

#reportCard {
  transition: all 0.3s ease;
}

#reportCard:hover {
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.2);
}

.el-upload__tip {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.el-upload-list--picture .el-upload-list__item {
  width: 100px;
  height: 100px;
  margin-top: 10px;
}

.current-semester {
  font-size: 16px;
  color: #666;
  margin-left: 10px;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
}

.file-item:hover {
  cursor: pointer;
  color: #409EFF;
}

.el-button--mini {
  padding: 5px 10px;
  font-size: 12px;
}

.el-button--primary {
  background-color: #42b983;
  border-color: #42b983;
}

.el-button--primary:hover {
  background-color: #3aa876;
  border-color: #3aa876;
}
</style>
