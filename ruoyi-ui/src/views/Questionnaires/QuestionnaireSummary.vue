<template>
  <div class="app-container">
    <div class="summary-header">
      <h1 class="page-title">问卷评价统计</h1>
      <p class="page-subtitle">大工书院育人导师工作评价统计表</p>
      
      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="书院">
            <el-select v-model="queryParams.tutorDepartment" placeholder="请选择书院" clearable @change="handleQuery">
              <el-option label="大煜书院" value="大煜书院" />
              <el-option label="伯川书院" value="伯川书院" />
              <el-option label="令希书院" value="令希书院" />
              <el-option label="厚德书院" value="厚德书院" />
              <el-option label="知行书院" value="知行书院" />
              <el-option label="笃学书院" value="笃学书院" />
              <el-option label="求实书院" value="求实书院" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
            <el-button type="success" icon="el-icon-download" @click="exportData">导出统计表</el-button>
            <el-button type="warning" icon="el-icon-download" @click="exportUnfinishedStudents">导出未填写学生</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计概览 -->
      <div class="statistics-overview">
        <div class="stat-card">
          <div class="stat-label">导师总数</div>
          <div class="stat-value">{{ statisticsData.length }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">学生评价数</div>
          <div class="stat-value">{{ totalStudentEvaluations }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">未填写学生数</div>
          <div class="stat-value">{{ unfinishedStudentCount }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">辅导员评价数</div>
          <div class="stat-value">{{ totalCounselorEvaluations }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">平均综合得分</div>
          <div class="stat-value">{{ averageOverallScore }}</div>
        </div>
      </div>
    </div>

    <!-- 统计表格 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="displayedData"
        border
        stripe
        style="width: 100%"
        :default-sort="{prop: 'overallAverage', order: 'descending'}"
      >
        <el-table-column label="排名" width="80" align="center">
          <template slot-scope="scope">
            <span :class="getRankClass(scope.row.rank)">{{ scope.row.rank }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="tutorId" label="工号" min-width="120" align="center" />
        
        <el-table-column prop="tutorName" label="导师姓名" min-width="120" align="center" />
        <el-table-column prop="tutorDepartment" label="所属书院" min-width="120" align="center" />

        <el-table-column label="学生评价" align="center">
          <el-table-column prop="studentAverage" label="平均分" min-width="100" align="center" sortable>
            <template slot-scope="scope">
              <span :class="getScoreClass(scope.row.studentAverage)">
                {{ scope.row.studentAverage || '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="studentCount" label="评价人数" min-width="100" align="center" sortable />
        </el-table-column>

        <el-table-column label="辅导员/执行院长评价" align="center">
          <el-table-column prop="counselorAverage" label="平均分" min-width="100" align="center" sortable>
            <template slot-scope="scope">
              <span :class="getScoreClass(scope.row.counselorAverage)">
                {{ scope.row.counselorAverage || '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="counselorCount" label="评价人数" min-width="100" align="center" sortable />
        </el-table-column>

        <el-table-column prop="overallAverage" label="综合平均分" min-width="120" align="center" sortable>
          <template slot-scope="scope">
            <span :class="getScoreClass(scope.row.overallAverage)">
              {{ scope.row.overallAverage || '-' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row)">
              <i class="el-icon-view"></i> 详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="handlePageChange"
      />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog title="导师评价详情" :visible.sync="detailDialogVisible" width="50%">
      <div v-if="currentTutor" class="detail-content">
        <div class="tutor-info-section">
          <h3>导师信息</h3>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="工号">{{ currentTutor.tutorId }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ currentTutor.tutorName }}</el-descriptions-item>
            <el-descriptions-item label="所属书院">{{ currentTutor.tutorDepartment }}</el-descriptions-item>
            <el-descriptions-item label="综合平均分">
              <span :class="getScoreClass(currentTutor.overallAverage)">
                {{ currentTutor.overallAverage }}
              </span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="evaluation-list-section">
          <h3>学生评价列表 ({{ currentTutor.studentCount }} 条)</h3>
          <el-table :data="studentEvaluations" border size="small">
            <el-table-column type="index" label="序号" min-width="60" align="center" />
            <el-table-column prop="userName" label="学生学号" min-width="150" align="center" />
            <el-table-column prop="quesScore" label="评分" min-width="100" align="center" />
            <el-table-column label="电子签名" min-width="150" align="center">
              <template slot-scope="scope">
                <el-image
                  v-if="scope.row.signature"
                  :src="getSignatureUrl(scope.row.signature)"
                  :preview-src-list="[getSignatureUrl(scope.row.signature)]"
                  style="width: 100px; height: 50px; cursor: pointer;"
                  fit="contain"
                >
                  <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
                <span v-else class="no-signature">未签名</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="evaluation-list-section">
          <h3>辅导员/执行院长评价列表 ({{ currentTutor.counselorCount }} 条)</h3>
          <el-table :data="counselorEvaluations" border size="small">
            <el-table-column type="index" label="序号" min-width="60" align="center" />
            <el-table-column prop="userName" label="评价人" min-width="150" align="center" />
            <el-table-column prop="quesScore" label="评分" min-width="100" align="center" />
            <el-table-column label="电子签名" min-width="150" align="center">
              <template slot-scope="scope">
                <el-image
                  v-if="scope.row.signature"
                  :src="getSignatureUrl(scope.row.signature)"
                  :preview-src-list="[getSignatureUrl(scope.row.signature)]"
                  style="width: 100px; height: 50px; cursor: pointer;"
                  fit="contain"
                >
                  <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
                <span v-else class="no-signature">未签名</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTutors } from "@/api/student/tutor";
import { listScore } from "@/api/system/questionnaire";
import { listRelation } from "@/api/system/relation";
import { listStudent } from "@/api/system/student";

export default {
  name: "QuestionnaireSummary",
  data() {
    return {
      loading: false,
      queryParams: {
        tutorDepartment: '',
        pageNum: 1,
        pageSize: 10
      },
      total: 0,
      statisticsData: [],
      detailDialogVisible: false,
      currentTutor: null,
      studentEvaluations: [],
      counselorEvaluations: [],
      unfinishedStudentCount: 0
    };
  },
  computed: {
    // 学生评价总数
    totalStudentEvaluations() {
      return this.statisticsData.reduce((sum, item) => sum + (item.studentCount || 0), 0);
    },
    // 辅导员评价总数
    totalCounselorEvaluations() {
      return this.statisticsData.reduce((sum, item) => sum + (item.counselorCount || 0), 0);
    },
    // 平均综合得分
    averageOverallScore() {
      if (this.statisticsData.length === 0) return '0.00';
      const sum = this.statisticsData.reduce((acc, item) => acc + parseFloat(item.overallAverage || 0), 0);
      return (sum / this.statisticsData.length).toFixed(2);
    },
    // 当前页显示的数据
    displayedData() {
      const start = (this.queryParams.pageNum - 1) * this.queryParams.pageSize;
      const end = start + this.queryParams.pageSize;
      return this.statisticsData.slice(start, end);
    }
  },
  created() {
    this.loadStatistics();
    this.loadUnfinishedStudentCount();
  },
  methods: {
    // 加载统计数据
    async loadStatistics() {
      this.loading = true;
      try {
        // 1. 获取导师列表
        const tutorResponse = await listTutors(this.queryParams);
        const tutors = tutorResponse.rows || [];

        // 2. 获取所有评价记录
        const [studentScoresResponse, counselorScoresResponse] = await Promise.all([
          listScore({ quesType: 4 }), // 学生评价
          listScore({ quesType: 5 })  // 辅导员/执行院长评价
        ]);

        const studentScores = studentScoresResponse.rows || [];
        const counselorScores = counselorScoresResponse.rows || [];

        // 3. 为每个导师计算统计数据
        this.statisticsData = tutors.map(tutor => {
          // 学生评价（ques_type=4）
          const studentEvals = studentScores.filter(score => score.tutorId === tutor.tutorId);
          const studentCount = studentEvals.length;
          const studentTotal = studentEvals.reduce((sum, s) => sum + (parseFloat(s.quesScore) || 0), 0);
          const studentAverage = studentCount > 0 ? (studentTotal / studentCount).toFixed(2) : null;

          // 辅导员评价（ques_type=5）
          const counselorEvals = counselorScores.filter(score => score.tutorId === tutor.tutorId);
          const counselorCount = counselorEvals.length;
          const counselorTotal = counselorEvals.reduce((sum, s) => sum + (parseFloat(s.quesScore) || 0), 0);
          const counselorAverage = counselorCount > 0 ? (counselorTotal / counselorCount).toFixed(2) : null;

          // 综合平均分（所有评价的平均）
          const allEvals = [...studentEvals, ...counselorEvals];
          const totalCount = allEvals.length;
          const totalSum = allEvals.reduce((sum, s) => sum + (parseFloat(s.quesScore) || 0), 0);
          const overallAverage = totalCount > 0 ? (totalSum / totalCount).toFixed(2) : '0.00';

          return {
            tutorId: tutor.tutorId,
            tutorName: tutor.tutorName,
            tutorTitle: tutor.tutorTitle,
            tutorDepartment: tutor.tutorDepartment,
            studentAverage,
            studentCount,
            counselorAverage,
            counselorCount,
            overallAverage
          };
        });

        // 4. 按综合平均分降序排序
        this.statisticsData.sort((a, b) => parseFloat(b.overallAverage) - parseFloat(a.overallAverage));

        // 5. 添加排名
        this.statisticsData = this.statisticsData.map((item, index) => ({
          ...item,
          rank: index + 1
        }));

        // 6. 设置总数
        this.total = this.statisticsData.length;

      } catch (error) {
        console.error('加载统计数据失败:', error);
        this.$message.error('加载统计数据失败');
      } finally {
        this.loading = false;
      }
    },

    // 查询
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.loadStatistics();
    },

    // 重置查询
    resetQuery() {
      this.queryParams = {
        tutorDepartment: '',
        pageNum: 1,
        pageSize: 10
      };
      this.loadStatistics();
    },

    // 分页切换
    handlePageChange() {
      // 分页参数已通过.sync自动更新，无需额外操作
      // 数据通过计算属性displayedData自动切换
    },

    // 查看详情
    async viewDetail(tutor) {
      this.currentTutor = tutor;
      this.detailDialogVisible = true;

      try {
        // 获取该导师的所有评价详情
        const [studentResponse, counselorResponse] = await Promise.all([
          listScore({ tutorId: tutor.tutorId, quesType: 4 }),
          listScore({ tutorId: tutor.tutorId, quesType: 5 })
        ]);

        this.studentEvaluations = studentResponse.rows || [];
        this.counselorEvaluations = counselorResponse.rows || [];
      } catch (error) {
        console.error('加载评价详情失败:', error);
        this.$message.error('加载评价详情失败');
      }
    },

    // 导出数据为CSV
    exportData() {
      if (this.statisticsData.length === 0) {
        this.$message.warning('暂无数据可导出');
        return;
      }

      // CSV表头
      const headers = [
        '排名',
        '工号',
        '导师姓名',
        '所属书院',
        '学生评价平均分',
        '学生评价人数',
        '辅导员评价平均分',
        '辅导员评价人数',
        '综合平均分'
      ];

      // 构建CSV内容
      let csvContent = '\uFEFF'; // 添加BOM以支持中文
      csvContent += headers.join(',') + '\n';

      this.statisticsData.forEach((item, index) => {
        const row = [
          index + 1,
          item.tutorId || '',
          item.tutorName || '',
          item.tutorDepartment || '',
          item.studentAverage || '-',
          item.studentCount || 0,
          item.counselorAverage || '-',
          item.counselorCount || 0,
          item.overallAverage || '0.00'
        ];
        csvContent += row.join(',') + '\n';
      });

      // 创建Blob并下载
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
      const link = document.createElement('a');
      const url = URL.createObjectURL(blob);
      const filename = `导师评价统计表_${this.parseTime(new Date(), '{y}{m}{d}{h}{i}{s}')}.csv`;
      
      link.setAttribute('href', url);
      link.setAttribute('download', filename);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      this.$message.success('导出成功');
    },

    // 加载未填写问卷的学生数量
    async loadUnfinishedStudentCount() {
      try {
        // 1. 获取所有导师学生关联关系
        const relationResponse = await listRelation({});
        const allRelations = relationResponse.rows || [];
        
        // 2. 获取所有学生的评价记录（问卷4）
        const scoreResponse = await listScore({ quesType: 4 });
        const finishedStudents = scoreResponse.rows || [];
        
        // 创建已填写学生的Set
        const finishedStudentSet = new Set(finishedStudents.map(s => s.userName));
        
        // 3. 计算未填写的学生数量
        const unfinishedCount = allRelations.filter(relation => 
          !finishedStudentSet.has(relation.studentId)
        ).length;
        
        this.unfinishedStudentCount = unfinishedCount;
        
      } catch (error) {
        console.error('加载未填写学生数量失败:', error);
        this.unfinishedStudentCount = 0;
      }
    },

    // 导出未填写问卷的学生名单
    async exportUnfinishedStudents() {
      try {
        this.$message.info('正在加载数据，请稍候...');
        
        // 1. 并行获取所有需要的数据
        const [relationResponse, scoreResponse, studentResponse, tutorResponse] = await Promise.all([
          listRelation({}),
          listScore({ quesType: 4 }),
          listStudent({}),
          listTutors({})
        ]);
        
        const allRelations = relationResponse.rows || [];
        const finishedStudents = scoreResponse.rows || [];
        const allStudents = studentResponse.rows || [];
        const allTutors = tutorResponse.rows || [];
        
        if (allRelations.length === 0) {
          this.$message.warning('暂无导师学生关联数据');
          return;
        }
        
        // 2. 创建ID到姓名的映射
        const studentMap = new Map();
        allStudents.forEach(student => {
          studentMap.set(student.studentId, student.studentName || student.name || '');
        });
        
        const tutorMap = new Map();
        allTutors.forEach(tutor => {
          tutorMap.set(tutor.tutorId, tutor.tutorName || tutor.name || '');
        });
        
        // 3. 创建已填写学生的Set（用学号作为key）
        const finishedStudentSet = new Set(finishedStudents.map(s => s.userName));
        
        // 4. 找出未填写的学生，并通过映射获取姓名
        const unfinishedList = [];
        allRelations.forEach(relation => {
          if (!finishedStudentSet.has(relation.studentId)) {
            unfinishedList.push({
              studentId: relation.studentId,
              studentName: studentMap.get(relation.studentId) || '',
              tutorId: relation.tutorId,
              tutorName: tutorMap.get(relation.tutorId) || ''
            });
          }
        });
        
        if (unfinishedList.length === 0) {
          this.$message.success('所有学生均已填写问卷！');
          return;
        }
        
        // 5. 构建CSV内容
        const headers = ['学号', '导师工号', '导师姓名'];
        let csvContent = '\uFEFF'; // 添加BOM以支持中文
        csvContent += headers.join(',') + '\n';
        
        unfinishedList.forEach(item => {
          const row = [
            item.studentId || '',
            item.tutorId || '',
            item.tutorName || ''
          ];
          csvContent += row.join(',') + '\n';
        });
        
        // 6. 创建Blob并下载
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        const filename = `未填写问卷学生名单_${this.parseTime(new Date(), '{y}{m}{d}{h}{i}{s}')}.csv`;
        
        link.setAttribute('href', url);
        link.setAttribute('download', filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        
        this.$message.success(`导出成功！共${unfinishedList.length}名学生未填写问卷`);
        
      } catch (error) {
        console.error('导出未填写学生名单失败:', error);
        this.$message.error('导出失败，请重试');
      }
    },

    // 获取排名样式
    getRankClass(rank) {
      if (rank === 1) return 'rank-first';
      if (rank === 2) return 'rank-second';
      if (rank === 3) return 'rank-third';
      return '';
    },

    // 获取分数样式
    getScoreClass(score) {
      if (!score) return '';
      const s = parseFloat(score);
      if (s >= 45) return 'score-excellent';
      if (s >= 40) return 'score-good';
      if (s >= 35) return 'score-medium';
      return 'score-low';
    },

    // 获取签名图片完整URL
    getSignatureUrl(signature) {
      if (!signature) return '';
      
      // 如果是base64数据，直接返回
      if (signature.startsWith('data:image')) {
        return signature;
      }
      
      // 如果是完整URL（http或https开头），直接返回
      if (signature.startsWith('http://') || signature.startsWith('https://')) {
        return signature;
      }
      
      // 如果是相对路径，拼接baseURL
      const baseURL = process.env.VUE_APP_BASE_API;
      return baseURL + signature;
    }
  }
};
</script>

<style scoped>
.summary-header {
  background: white;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 24px;
  color: #2c3e50;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.page-subtitle {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0 0 20px 0;
}

.filter-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

/* 统计概览卡片 */
.statistics-overview {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 8px;
  color: white;
  text-align: center;
}

.stat-card:nth-child(2) {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card:nth-child(3) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-card:nth-child(4) {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card:nth-child(5) {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
}

/* 表格容器 */
.table-container {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.table-container >>> .pagination-container {
  margin-top: 20px;
}

/* 排名样式 */
.rank-first {
  display: inline-block;
  width: 32px;
  height: 32px;
  line-height: 32px;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  color: white;
  border-radius: 50%;
  font-weight: 600;
  font-size: 16px;
}

.rank-second {
  display: inline-block;
  width: 32px;
  height: 32px;
  line-height: 32px;
  background: linear-gradient(135deg, #C0C0C0 0%, #A9A9A9 100%);
  color: white;
  border-radius: 50%;
  font-weight: 600;
  font-size: 16px;
}

.rank-third {
  display: inline-block;
  width: 32px;
  height: 32px;
  line-height: 32px;
  background: linear-gradient(135deg, #CD7F32 0%, #B87333 100%);
  color: white;
  border-radius: 50%;
  font-weight: 600;
  font-size: 16px;
}

/* 分数样式 */
.score-excellent {
  color: #67C23A;
  font-weight: 600;
  font-size: 16px;
}

.score-good {
  color: #409EFF;
  font-weight: 600;
  font-size: 16px;
}

.score-medium {
  color: #E6A23C;
  font-weight: 600;
  font-size: 16px;
}

.score-low {
  color: #F56C6C;
  font-weight: 600;
  font-size: 16px;
}

/* 详情弹窗 */
.detail-content {
  max-height: 600px;
  overflow-y: auto;
  width: 100%;
}

.tutor-info-section {
  margin-bottom: 24px;
}

.tutor-info-section h3,
.evaluation-list-section h3 {
  font-size: 16px;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #667eea;
}

.evaluation-list-section {
  margin-bottom: 24px;
}

/* 签名相关样式 */
.no-signature {
  color: #909399;
  font-size: 12px;
}

.image-slot {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 24px;
}

.evaluation-list-section >>> .el-image {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 4px;
  background: #f5f7fa;
}

.evaluation-list-section >>> .el-image:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

/* 响应式 */
@media (max-width: 1400px) {
  .statistics-overview {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1000px) {
  .statistics-overview {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .statistics-overview {
    grid-template-columns: 1fr;
  }

  .summary-header,
  .table-container {
    padding: 16px;
  }
}
</style>

