<template>
  <div class="app-container">
    <!-- 页面头部 -->
    <div class="page-header fade-in">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="el-icon-data-analysis"></i>
            问卷评价统计
          </h1>
          <p class="page-subtitle">大工书院育人导师工作评价统计表</p>
        </div>
        <div class="action-section">
          <el-button 
            type="success" 
            icon="el-icon-download" 
            @click="exportData"
            class="action-btn"
          >
            导出统计表
          </el-button>
          <el-button 
            type="warning" 
            icon="el-icon-download" 
            @click="exportUnfinishedStudents"
            class="action-btn"
          >
            导出未填写学生
          </el-button>
        </div>
      </div>
    </div>

    <!-- 各书院数据一览 -->
    <div class="colleges-overview fade-in" style="animation-delay: 0.1s">
      <h2 class="section-title">
        <i class="el-icon-office-building"></i>
        各书院评价数据
      </h2>
      <p class="section-hint">点击书院卡片查看该书院的详细数据</p>
      <div class="colleges-grid">
        <div
          v-for="college in collegeList"
          :key="college.value"
          class="college-card"
          :class="{ 'active': queryParams.tutorDepartment === college.value }"
          @click="filterByCollege(college.value)"
        >
          <div class="college-header">
            <i class="el-icon-school college-icon"></i>
            <span class="college-name">{{ college.label }}</span>
          </div>
          <div class="college-stats">
            <div class="stat-row">
              <span class="label">
                <i class="el-icon-user"></i>
                导师数
              </span>
              <span class="value">{{ getCollegeTutorCount(college.value) }}</span>
            </div>
            <div class="stat-row">
              <span class="label">
                <i class="el-icon-edit"></i>
                学生评价
              </span>
              <span class="value">{{ getCollegeStudentEvaluations(college.value) }}</span>
            </div>
            <div class="stat-row">
              <span class="label">
                <i class="el-icon-star-on"></i>
                综合得分
              </span>
              <span class="value highlight">{{ getCollegeAverageScore(college.value) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计表格 -->
    <div class="table-container fade-in" style="animation-delay: 0.2s">
      <div class="table-header">
        <h2 class="table-title">
          <i class="el-icon-s-data"></i>
          导师评价详细数据
        </h2>
        <div class="filter-tag" v-if="queryParams.tutorDepartment">
          <el-tag type="primary" effect="plain" closable @close="resetQuery">
            <i class="el-icon-school"></i>
            {{ queryParams.tutorDepartment }}
          </el-tag>
        </div>
      </div>
      <el-table
        v-loading="loading"
        :data="displayedData"
        border
        stripe
        style="width: 100%"
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
          <el-table-column prop="studentAverage" label="平均分" min-width="100" align="center">
            <template slot-scope="scope">
              <span :class="getScoreClass(scope.row.studentAverage)">
                {{ scope.row.studentAverage || '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="studentCount" label="评价人数" min-width="100" align="center" />
        </el-table-column>

        <el-table-column label="执行院长/执行副院长评价" align="center">
          <el-table-column prop="counselorScore2" label="执行院长评分" min-width="120" align="center">
            <template slot-scope="scope">
              <span :class="getScoreClass(scope.row.counselorScore2)">
                {{ scope.row.counselorScore2 || '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="counselorScore1" label="执行副院长评分" min-width="120" align="center">
            <template slot-scope="scope">
              <span :class="getScoreClass(scope.row.counselorScore1)">
                {{ scope.row.counselorScore1 || '-' }}
              </span>
            </template>
          </el-table-column>
        </el-table-column>

        <el-table-column prop="overallAverage" label="综合平均分" min-width="120" align="center">
          <template slot-scope="scope">
            <span :class="getScoreClass(scope.row.overallAverage)">
              {{ scope.row.overallAverage || '-' }}
            </span>
          </template>
        </el-table-column>

        <!-- <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row)">
              <i class="el-icon-view"></i> 详情
            </el-button>
          </template>
        </el-table-column> -->
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
            <el-table-column prop="quesScore" label="评分" min-width="100" align="center">
              <template slot-scope="scope">
                <span :class="getScoreClass(scope.row.quesScore)">
                  {{ scope.row.quesScore }}
                </span>
              </template>
            </el-table-column>
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
            <el-table-column label="角色" min-width="120" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.$index === 0 ? 'warning' : 'danger'" size="small">
                  {{ scope.$index === 0 ? '执行副院长' : '执行院长' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="评价人账号" min-width="120" align="center" />
            <el-table-column prop="quesScore" label="评分" min-width="100" align="center">
              <template slot-scope="scope">
                <span :class="getScoreClass(scope.row.quesScore)">
                  {{ scope.row.quesScore }}
                </span>
              </template>
            </el-table-column>
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
import { parseTime } from "@/utils/ruoyi";

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
      allStatisticsData: [], // 保存所有书院的完整数据
      detailDialogVisible: false,
      currentTutor: null,
      studentEvaluations: [],
      counselorEvaluations: [],
      unfinishedStudentCount: 0,
      collegeList: [
        { label: '大煜书院', value: '大煜书院' },
        { label: '伯川书院', value: '伯川书院' },
        { label: '令希书院', value: '令希书院' },
        { label: '厚德书院', value: '厚德书院' },
        { label: '知行书院', value: '知行书院' },
        { label: '笃学书院', value: '笃学书院' },
        { label: '求实书院', value: '求实书院' }
      ],
      parseTime // 导入的工具函数
    };
  },
  computed: {
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
    // 根据评价账号获取所属书院
    getAcademyByAccount(userName) {
      // 账号与书院的映射关系
      const accountAcademyMap = {
        '20001': '大煜书院',  // 大煜书院院长（奇数）
        '20002': '大煜书院',  // 大煜书院副院长（偶数）
        '20003': '伯川书院',  // 伯川书院院长（奇数）
        '20004': '伯川书院',  // 伯川书院副院长（偶数）
        '20005': '令希书院',  // 令希书院院长（奇数）
        '20006': '令希书院',  // 令希书院副院长（偶数）
        '20007': '厚德书院',  // 厚德书院院长（奇数）
        '20008': '厚德书院',  // 厚德书院副院长（偶数）
        '20009': '知行书院',  // 知行书院院长（奇数）
        '20010': '知行书院',  // 知行书院副院长（偶数）
        '20011': '笃学书院',  // 笃学书院院长（奇数）
        '20012': '笃学书院',  // 笃学书院副院长（偶数）
        '20013': '求实书院',  // 求实书院院长（奇数）
        '20014': '求实书院'   // 求实书院副院长（偶数）
      };
      
      return accountAcademyMap[userName] || null;
    },

    // 加载统计数据
    async loadStatistics() {
      this.loading = true;
      try {
        // 1. 获取所有导师列表（不传查询参数，获取全部数据）
        const tutorResponse = await listTutors({ pageNum: 1, pageSize: 1000 });
        const tutors = tutorResponse.rows || [];

        // 2. 获取所有评价记录和学生信息
        const [studentScoresResponse, viceScoresResponse, deanScoresResponse, studentsResponse] = await Promise.all([
          listScore({ quesType: 4 }), // 学生评价
          listScore({ quesType: 5 }), // 执行副院长评价
          listScore({ quesType: 6 }), // 执行院长评价
          listStudent({ pageNum: 1, pageSize: 10000 }) // 获取所有学生信息
        ]);

        const studentScores = studentScoresResponse.rows || [];
        const viceScores = viceScoresResponse.rows || [];
        const deanScores = deanScoresResponse.rows || [];
        const allStudents = studentsResponse.rows || [];

        // 3. 创建学生学号到书院的映射
        const studentAcademyMap = new Map();
        allStudents.forEach(student => {
          if (student.studentId && student.academy) {
            studentAcademyMap.set(student.studentId, student.academy);
          }
        });

        // 4. 为每个导师的每个书院组合计算统计数据
        const statisticsMap = new Map();
        
        tutors.forEach(tutor => {
          const departments = tutor.tutorDepartment ? tutor.tutorDepartment.split(',') : [tutor.tutorDepartment];
          
          departments.forEach(department => {
            const dept = department ? department.trim() : '';
            if (!dept) return;
            
            const key = `${tutor.tutorId}_${dept}`;
            
            // 学生评价（ques_type=4）- 根据学生所属书院过滤，只计算当前书院学生的评价
            const studentEvals = studentScores.filter(score => {
              if (score.tutorId !== tutor.tutorId) return false;
              const studentAcademy = studentAcademyMap.get(score.userName);
              return studentAcademy === dept;
            });
            const studentCount = studentEvals.length;
            const studentTotal = studentEvals.reduce((sum, s) => sum + (parseFloat(s.quesScore) || 0), 0);
            const studentAverage = studentCount > 0 ? (studentTotal / studentCount).toFixed(2) : null;

            // 辅导员评价 - 根据评价人所属书院过滤，只计算当前书院的评分
            const viceEvals = viceScores.filter(score => {
              const evaluatorAcademy = this.getAcademyByAccount(score.userName);
              return score.tutorId === tutor.tutorId && evaluatorAcademy === dept;
            });
            
            const deanEvals = deanScores.filter(score => {
              const evaluatorAcademy = this.getAcademyByAccount(score.userName);
              return score.tutorId === tutor.tutorId && evaluatorAcademy === dept;
            });
            
            // 获取执行副院长评分（quesType=5）
            const counselorScore1 = viceEvals.length > 0 ? parseFloat(viceEvals[0].quesScore).toFixed(2) : null;
            
            // 获取执行院长评分（quesType=6）
            const counselorScore2 = deanEvals.length > 0 ? parseFloat(deanEvals[0].quesScore).toFixed(2) : null;
            
            // 合并所有辅导员评价用于计算
            const counselorEvals = [...viceEvals, ...deanEvals];
            const counselorCount = counselorEvals.length;

            // 综合平均分（所有评价的平均）
            const allEvals = [...studentEvals, ...counselorEvals];
            const totalCount = allEvals.length;
            const totalSum = allEvals.reduce((sum, s) => sum + (parseFloat(s.quesScore) || 0), 0);
            const overallAverage = totalCount > 0 ? (totalSum / totalCount).toFixed(2) : '0.00';

            statisticsMap.set(key, {
              tutorId: tutor.tutorId,
              tutorName: tutor.tutorName,
              tutorTitle: tutor.tutorTitle,
              tutorDepartment: dept,
              studentAverage,
              studentCount,
              counselorScore1,
              counselorScore2,
              counselorCount,
              overallAverage
            });
          });
        });

        // 5. 转换为数组
        this.statisticsData = Array.from(statisticsMap.values());

        // 6. 按综合平均分从高到低排序
        this.statisticsData.sort((a, b) => {
          const scoreA = parseFloat(a.overallAverage) || 0;
          const scoreB = parseFloat(b.overallAverage) || 0;
          return scoreB - scoreA; // 降序：分数高的在前
        });

        // 7. 添加排名（按索引从1开始）
        this.statisticsData = this.statisticsData.map((item, index) => ({
          ...item,
          rank: index + 1
        }));

        // 8. 保存所有书院的完整数据
        this.allStatisticsData = [...this.statisticsData];

        // 9. 设置总数
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
        // 获取该导师的所有评价详情和学生信息
        const [studentResponse, viceResponse, deanResponse, studentsResponse] = await Promise.all([
          listScore({ tutorId: tutor.tutorId, quesType: 4 }), // 学生评价
          listScore({ tutorId: tutor.tutorId, quesType: 5 }), // 执行副院长评价
          listScore({ tutorId: tutor.tutorId, quesType: 6 }), // 执行院长评价
          listStudent({ pageNum: 1, pageSize: 10000 }) // 获取所有学生信息
        ]);

        // 创建学生学号到书院的映射
        const studentAcademyMap = new Map();
        if (studentsResponse && studentsResponse.rows) {
          studentsResponse.rows.forEach(student => {
            if (student.studentId && student.academy) {
              studentAcademyMap.set(student.studentId, student.academy);
            }
          });
        }
        
        // 根据当前书院过滤学生评价
        const allStudentEvals = studentResponse.rows || [];
        this.studentEvaluations = allStudentEvals.filter(score => {
          const studentAcademy = studentAcademyMap.get(score.userName);
          return studentAcademy === tutor.tutorDepartment;
        });
        
        // 合并执行副院长和执行院长的评价，并根据当前书院过滤
        const viceEvals = (viceResponse.rows || []).filter(score => {
          const evaluatorAcademy = this.getAcademyByAccount(score.userName);
          return evaluatorAcademy === tutor.tutorDepartment;
        });
        const deanEvals = (deanResponse.rows || []).filter(score => {
          const evaluatorAcademy = this.getAcademyByAccount(score.userName);
          return evaluatorAcademy === tutor.tutorDepartment;
        });
        this.counselorEvaluations = [...viceEvals, ...deanEvals];
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
        '执行副院长评分',
        '执行院长评分',
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
          item.counselorScore1 || '-',
          item.counselorScore2 || '-',
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

    // 获取分数样式（满分100分）
    getScoreClass(score) {
      if (!score) return '';
      const s = parseFloat(score);
      if (s >= 90) return 'score-excellent';  // 90分以上 - 优秀
      if (s >= 80) return 'score-good';       // 80-89分 - 良好
      if (s >= 70) return 'score-medium';     // 70-79分 - 中等
      return 'score-low';                     // 70分以下 - 较低
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
    },

    // 点击书院卡片进行筛选
    filterByCollege(collegeName) {
      if (this.queryParams.tutorDepartment === collegeName) {
        // 如果已经选中该书院，则取消筛选，恢复全部数据（保持排序）
        this.queryParams.tutorDepartment = '';
        this.statisticsData = [...this.allStatisticsData];
      } else {
        // 选中该书院，筛选数据（保持原有排序，只需重新分配排名）
        this.queryParams.tutorDepartment = collegeName;
        // 从allStatisticsData中筛选指定书院的数据（过滤后的数据仍然按综合平均分从高到低排序）
        const filteredData = this.allStatisticsData.filter(item => item.tutorDepartment === collegeName);
        // 重新分配排名
        this.statisticsData = filteredData.map((item, index) => ({
          ...item,
          rank: index + 1
        }));
      }
      // 设置总数
      this.total = this.statisticsData.length;
      // 重置到第一页
      this.queryParams.pageNum = 1;
    },

    // 获取指定书院的导师数量
    getCollegeTutorCount(collegeName) {
      return this.allStatisticsData.filter(item => item.tutorDepartment === collegeName).length;
    },

    // 获取指定书院的学生评价数量
    getCollegeStudentEvaluations(collegeName) {
      return this.allStatisticsData
        .filter(item => item.tutorDepartment === collegeName)
        .reduce((sum, item) => sum + (item.studentCount || 0), 0);
    },

    // 获取指定书院的平均综合得分
    getCollegeAverageScore(collegeName) {
      const collegeData = this.allStatisticsData.filter(item => item.tutorDepartment === collegeName);
      if (collegeData.length === 0) return '0.00';
      const sum = collegeData.reduce((acc, item) => acc + parseFloat(item.overallAverage || 0), 0);
      return (sum / collegeData.length).toFixed(2);
    }
  }
};
</script>

<style scoped>
/* 全局容器 */
.app-container {
  margin-left: 100px;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in {
  animation: fadeIn 0.6s ease forwards;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #409EFF 0%, #3b82f6 100%);
  border-radius: 12px;
  padding: 32px 40px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.25);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
}

.title-section {
  flex: 1;
}

.page-title {
  font-size: 28px;
  color: white;
  margin: 0 0 8px 0;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i {
  font-size: 32px;
}

.page-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.95);
  margin: 0;
  font-weight: 400;
}

.action-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.action-btn {
  font-size: 14px;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

/* 各书院数据一览 */
.colleges-overview {
  margin-bottom: 32px;
}

.section-title {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title i {
  color: #409EFF;
  font-size: 24px;
}

.section-hint {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 20px 0;
  padding-left: 34px;
}

.colleges-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(min(130px, 100%), 1fr));
  gap: 12px;
  width: 100%;
  max-width: 100%;
}

.college-card {
  background: white;
  border-radius: 10px;
  padding: 16px 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 0;
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden;
  will-change: transform;
  cursor: pointer;
  border: 2px solid #e5e7eb;
  position: relative;
}

.college-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409EFF 0%, #3b82f6 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.college-card:hover::before {
  transform: scaleX(1);
}

.college-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #409EFF;
}

.college-card.active {
  border-color: #409EFF;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
  transform: translateY(-4px);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.03) 0%, rgba(59, 130, 246, 0.03) 100%);
}

.college-card.active::before {
  transform: scaleX(1);
}

.college-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f3f4f6;
}

.college-icon {
  font-size: 26px;
  color: #409EFF;
  transition: all 0.3s ease;
}

.college-card:hover .college-icon,
.college-card.active .college-icon {
  color: #3b82f6;
  transform: scale(1.1);
}

.college-name {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  flex: 1;
}

.college-stats {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.college-stats .stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  padding: 4px 0;
}

.college-stats .label {
  color: #6b7280;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.college-stats .label i {
  font-size: 14px;
  color: #9ca3af;
}

.college-stats .value {
  font-weight: 700;
  color: #1f2937;
  font-size: 16px;
}

.college-stats .value.highlight {
  color: #409EFF;
  font-size: 18px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(59, 130, 246, 0.1) 100%);
  padding: 4px 12px;
  border-radius: 8px;
  font-weight: 700;
}

/* 表格容器 */
.table-container {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e5e7eb;
}

.table-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.table-title i {
  color: #409EFF;
  font-size: 22px;
}

.filter-tag {
  display: flex;
  gap: 8px;
  align-items: center;
}

.filter-tag .el-tag {
  font-size: 14px;
  padding: 8px 16px;
}

.filter-tag .el-tag i {
  margin-right: 6px;
}

.table-container >>> .el-table {
  border-radius: 8px;
  overflow: hidden;
}

.table-container >>> .el-table th {
  background: #f8fafc !important;
  color: #374151;
  font-weight: 600;
  font-size: 14px;
  padding: 14px 0;
}

.table-container >>> .el-table td {
  font-size: 14px;
  padding: 12px 0;
}

.table-container >>> .el-table__row:hover {
  background: #f9fafb !important;
}

.table-container >>> .pagination-container {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

/* 排名样式 */
.rank-first {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  color: white;
  border-radius: 50%;
  font-weight: 700;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
}

.rank-second {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #C0C0C0 0%, #A9A9A9 100%);
  color: white;
  border-radius: 50%;
  font-weight: 700;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(192, 192, 192, 0.4);
}

.rank-third {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #CD7F32 0%, #B87333 100%);
  color: white;
  border-radius: 50%;
  font-weight: 700;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(205, 127, 50, 0.4);
}

/* 分数样式 */
.score-excellent {
  color: #10b981;
  font-weight: 700;
  font-size: 16px;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(5, 150, 105, 0.1) 100%);
  padding: 4px 10px;
  border-radius: 6px;
  display: inline-block;
}

.score-good {
  color: #3b82f6;
  font-weight: 700;
  font-size: 16px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(37, 99, 235, 0.1) 100%);
  padding: 4px 10px;
  border-radius: 6px;
  display: inline-block;
}

.score-medium {
  color: #f59e0b;
  font-weight: 700;
  font-size: 16px;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1) 0%, rgba(217, 119, 6, 0.1) 100%);
  padding: 4px 10px;
  border-radius: 6px;
  display: inline-block;
}

.score-low {
  color: #ef4444;
  font-weight: 700;
  font-size: 16px;
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1) 0%, rgba(220, 38, 38, 0.1) 100%);
  padding: 4px 10px;
  border-radius: 6px;
  display: inline-block;
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
  border-bottom: 2px solid #409EFF;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .action-section {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .app-container {
    padding: 12px 8px;
  }

  .page-header {
    padding: 24px 20px;
  }
  
  .page-title {
    font-size: 22px;
  }
  
  .page-title i {
    font-size: 26px;
  }
  
  .page-subtitle {
    font-size: 13px;
  }
  
  .action-section {
    flex-direction: column;
    width: 100%;
  }
  
  .action-btn {
    width: 100%;
    justify-content: center;
  }
  
  .table-container {
    padding: 16px;
  }
  
  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .colleges-grid {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 12px;
  }
  
  .college-card {
    padding: 14px 12px;
  }
  
  .section-title {
    font-size: 18px;
  }
  
  .section-hint {
    font-size: 13px;
    padding-left: 28px;
  }
}
</style>

