<template>
  <el-row type="flex" justify="center" style="margin-top: 4vh;">
    <el-card id="reportCard" shadow="hover" style="width: 70%; margin-top: 2vh; border-radius: 10px;">
      <!-- 头部区域 -->
      <div style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 10px;">
        <h1 style="font-size: 24px; font-weight: 500; color: #2c3e50;">科创竞赛</h1>
        <el-button type="primary" icon="el-icon-plus" circle size="medium" @click="openDialog"
                   style="background-color: #42b983; border-color: #42b983;"></el-button>
      </div>

      <!-- 竞赛表格 -->
      <el-table
        :data="competitionRecords" style="width: 100%" border stripe highlight-current-row>
        <el-table-column type="index" label="序号"></el-table-column>
        <el-table-column prop="competitionName" label="竞赛名称" min-width="100"></el-table-column>
        <el-table-column prop="competitionLevel" label="竞赛级别"></el-table-column>
        <el-table-column prop="awardLevel" label="竞赛奖项"></el-table-column>
        <el-table-column prop="awardDate" label="获奖日期" min-width="100"></el-table-column>
        <el-table-column label="证明材料" width="120">
          <template v-slot:default="scope">
            <div class="proof-material-cell">
              <el-link
                type="primary"
                :underline="false"
                @click="handlePreview(scope.row.proofMaterial)"
                style="margin-right: 10px;"
              >
                <i class="el-icon-view"></i> 预览
              </el-link>
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
            <el-tag v-if="scope.row.auditStatus === '未审核'" type="warning">未审核</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '已通过'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '未通过'" type="danger">未通过</el-tag>
            <el-tag v-else-if="scope.row.auditStatus === '未提交'" type="info">未提交</el-tag>
            <el-tag v-else>未知状态</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button
              v-if="scope.row.auditStatus === '未通过'"
              type="text"
              size="mini"
              @click="handleEditDraft(scope.row)"
            >重新提交</el-button>

            <template v-if="scope.row.auditStatus === '未提交'">
              <el-button
                type="text"
                size="mini"
                @click="handleEditDraft(scope.row)"
              >编辑草稿</el-button>
              <el-button
                type="text"
                size="mini"
                style="color: #F56C6C;"
                @click="handleDelete(scope.row)"
              >删除</el-button>
            </template>

            <el-tag
              v-if="['未审核', '已通过'].includes(scope.row.auditStatus)"
              type="info"
              size="mini"
            >不可修改</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" min-width="100"></el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" min-width="120"></el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalRecords"
        :page-sizes="[10, 20, 30, 50]"
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

    <!-- 竞赛填写对话框 -->
    <el-dialog :visible.sync="showDialog" title="竞赛填写" width="50%" @close="closeDialog">
      <el-form ref="form" :model="formData" :rules="rules" label-width="120px" style="padding: 20px;">
        <el-form-item label="竞赛名称" prop="competitionName">
          <el-input v-model="formData.competitionName" placeholder="请输入竞赛名称"></el-input>
        </el-form-item>

        <el-form-item label="竞赛级别" prop="competitionLevel">
          <el-select v-model="formData.competitionLevel" placeholder="请选择竞赛级别">
            <el-option label="院级" value="院级"></el-option>
            <el-option label="校级" value="校级"></el-option>
            <el-option label="省级" value="省级"></el-option>
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="国际级" value="国际级"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="奖项等级" prop="awardLevel">
          <el-select v-model="formData.awardLevel" placeholder="请选择奖项">
            <el-option label="特等奖" value="特等奖"></el-option>
            <el-option label="一等奖" value="一等奖"></el-option>
            <el-option label="二等奖" value="二等奖"></el-option>
            <el-option label="三等奖" value="三等奖"></el-option>
            <el-option label="优秀奖" value="优秀奖"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="获奖日期" prop="awardDate">
          <el-date-picker
            v-model="formData.awardDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="选择获奖日期">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="证明材料" prop="proofMaterial">
          <el-upload
            multiple
            :limit="5"
            :file-list="fileList"
            :auto-upload="false"
            :on-change="handleFileChange"
          >
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">最多上传5个文件，单个不超过10MB</div>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="info" @click="handleSave">保存草稿</el-button>
          <el-button type="primary" @click="handleSubmit">正式提交</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-row>
</template>

<script>
import axios from "axios";
import { addRecord, listRecord, delRecord , updateRecord, checkRecordUnique } from "@/api/student/competition";
import store from "@/store"; // 根据实际路径调整

export default {
  data() {
    return {
      currentCompetitionId: null, // 当前修改的竞赛记录ID
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
      competitionName: '',
      competitionLevel: '',
      awardLevel: '',
      awardDate: '',
      proofMaterial: [],
      auditStatus: '未提交',
      auditTime: null,
      auditRemark: '',
      semester: '',
      formData: {
        competitionName: '', // 竞赛名称
        competitionLevel: '', // 竞赛级别
        awardLevel: '', // 奖项
        awardDate:'',//竞赛获奖时间
        scholarshipPoints: '', // 折合分数
        proofMaterial: '', // 图片地址
      },
      rules: {
          competitionName: [{ required: true, message: '竞赛名称不能为空', trigger: 'blur' }],
          competitionLevel: [{ required: true, message: '请选择竞赛级别', trigger: 'change' }],
          awardLevel: [{ required: true, message: '请选择奖项', trigger: 'change' }],
          awardDate: [{ required: true, message: '请选择获奖日期', trigger: 'change' }]
      },
    };
  },
  mounted() {
    // 获取学期数据
    this.activeSemester = this.$route.query.semester || '未知学期';
    this.fetchCompetitionRecords(); // 加载数据
  },
  methods: {

    // 处理草稿修改
    handleEditDraft(row) {
      this.handleEdit(row);
      localStorage.removeItem(this.getDraftKey());
    },

    // 处理编辑未通过记录
    handleEdit(row) {
      this.formData = {
        ...row,
        competitionId: row.id,
        auditTime:null,
        auditRemark:"",
        awardDate: row.awardDate ? new Date(row.awardDate) : null
      };
      this.isEdit = true;
      this.currentCompetitionId = row.id;
      this.showDialog = true;
    },

    // 新增方法
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该记录吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        console.log("row.id:"+row.id)
        const response = await delRecord(row.id);
        if (response.code === 200) {
          this.$message.success('删除成功');
          await this.fetchCompetitionRecords();
          localStorage.removeItem(this.getDraftKey());
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`删除失败: ${error.message}`);
        }
      }
    },

    async handleSave() {
      this.submitData('未提交');
    },

    async handleSubmit() {
      this.submitData('未审核');
    },

    getDraftKey() {
      return `competition_draft_${this.$store.state.user.name}_${this.activeSemester}`;
    },

    // 修改后的打开对话框方法
    openDialog() {
      this.isEdit = false;
      this.currentCompetitionId= null;
      this.formData = this.initFormData();
      this.showDialog = true;

      // 加载草稿
      const draft = localStorage.getItem(this.getDraftKey());
      if (draft) {
        this.formData = JSON.parse(draft);
      }
    },

    initFormData() {
      return {
        competitionName: '',
        competitionLevel: '',
        awardLevel: '',
        awardDate: '',
        proofMaterial: [],
        auditStatus: '未提交',
        auditTime: null,
        auditRemark: '',
        semester: this.activeSemester
      };
    },
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


    // 关闭对话框
    closeDialog() {
      this.showDialog = false;
      this.fileList = []; // 清空已上传的文件列表
    },
    submitData(state) {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const formData = new FormData();

          // 构建核心数据对象
          const recordData = {
            competitionId: null,
            competitionName: this.formData.competitionName,
            competitionLevel: this.formData.competitionLevel,
            awardLevel: this.formData.awardLevel,
            semester: this.activeSemester,
            studentId: store.state.user.name,
            auditStatus: state,
            auditTime: null,
            auditRemark: '',
            awardDate: this.formData.awardDate
          };

          // 如果是编辑操作，添加ID字段
          if (this.currentCompetitionId) {
            recordData.competitionId = this.currentCompetitionId;
          }

          // 构建 JSON 部分（指定类型为 application/json）
          const recordBlob = new Blob(
            [JSON.stringify(recordData)],
            {type: "application/json"}
          );
          formData.append("record", recordBlob);

          // 添加文件
          this.fileList.forEach((file) => {
            formData.append("proofMaterial", file.raw);
          });

          // 配置headers
          const config = {
            headers: {
              "Authorization": "Bearer " + localStorage.getItem("token"),
              "Content-Type": "multipart/form-data"
            }
          };

          // 根据模式选择API方法
          const apiMethod = this.currentCompetitionId ? updateRecord : addRecord;

          apiMethod(formData, config)
            .then(() => {
              this.$message.success(this.currentCompetitionId ? "更新成功！" : "提交成功！");
              this.fetchCompetitionRecords();
              this.closeDialog();
            })
            .catch(error => {
              this.$message.error(`操作失败：${error.message}`);
            });
        }
      });
    },

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
      this.fetchCompetitionRecords();
    },

    // 当前页变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchCompetitionRecords();
    },
    handleImageClick(imageUrl) {
      this.previewImage = this.getFullUrl(imageUrl);
      this.previewVisible = true; // 使用正确的变量名
    },

    // 加载竞赛记录
    async fetchCompetitionRecords() {
      try {
        const response = await listRecord({
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          semester: this.activeSemester,
          studentId:store.state.user.name
        });

        if (response && response.code === 200) {
          this.competitionRecords = response.rows || [];
          this.totalRecords = response.total || 0;

          // 添加数据转换
          this.competitionRecords = this.competitionRecords.map(item => ({
            ...item,
            id: item.competitionId,
            auditStatus: item.auditStatus,
            proofMaterial: this.parseMaterial(item.proofMaterial)
          }));

        } else {
          this.$message.error(response.msg || '数据加载失败');
        }
      } catch (error) {
        console.error("数据加载失败:", error);
        this.$message.error('数据加载异常');
      }
    },

// 新增材料解析方法
    parseMaterial(material) {
      try {
        return material ? JSON.parse(material) : [];
      } catch (e) {
        console.error('材料解析失败:', material);
        return [];
      }
    }


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
