<template>
  <div class="container">
    <div class="main-container">
      <!-- 顶部导航栏增强 -->
      <div class="nav">
        <div class="nav-content">
          <h2>
            <span class="campus-icon">🏫</span>
            书院学生成长档案
          </h2>
          <div class="academic-year">{{ academicPeriod }} 学籍周期</div>
        </div>
      </div>

      <!-- 学期卡片容器 -->
      <div class="semester-grid-wrapper">

        <div class="semester-grid">

          <!-- 上半部分学期卡片 -->
          <div
            v-for="(semester, index) in firstHalfSemesters"
            :key="'top'+index"
            class="semester-card"
            :class="['top'+(index+1), semester.status, {
              'active': activeIndex === index,
              'disabled': semester.status === 'future'
            }]"
            @click="handleCardClick(index)"
          >

            <div class="glow-effect"></div>
            <div class="semester-header">
              <span class="semester-name">{{ semester.name }}</span>
              <span class="time">{{ semester.time }}</span>
            </div>
            <div class="progress-wrapper">
              <div class="progress">
                <div
                  class="progress-bar"
                  :style="{ width: semester.progress + '%',
                            background: progressColor(semester)}"
                >
                  <span class="progress-text">{{ semester.progress }}%</span>
                </div>
              </div>
            </div>
            <div class="status-indicator">
              <span v-if="semester.status === 'completed'">✅ 已完成</span>
              <span v-if="semester.status === 'current'" class="blink">🎯 进行中</span>
              <span v-if="semester.status === 'future'">⏳ 未开启</span>
            </div>
          </div>

          <!-- 中间雷达图 -->
          <div class="radar-card">
            <div class="radar-header">
              <span class="chart-icon">📊</span>
              综合能力评估
              <span class="update-time">(最近更新: {{ lastUpdate }})</span>
            </div>
            <enhanced-radar-chart
              :data="radarData"
              :indicators="indicators"
              :title="''"
              style="height: 280px;"
            />
          </div>

          <!-- 下半部分学期卡片 -->
          <div
            v-for="(semester, index) in secondHalfSemesters"
            :key="'bottom'+index"
            class="semester-card"
            :class="['bottom'+(index+1), semester.status, {
              'active': activeIndex === (index + 4),
              'disabled': semester.status === 'future'
            }]"
            @click="handleCardClick(index + 4)"
          >

            <div class="glow-effect"></div>
            <div class="semester-header">
              <span class="semester-name">{{ semester.name }}</span>
              <span class="time">{{ semester.time }}</span>
            </div>
            <div class="progress-wrapper">
              <div class="progress">
                <div
                  class="progress-bar"
                  :style="{ width: semester.progress + '%',
                            background: progressColor(semester)}"
                >
                  <span class="progress-text">{{ semester.progress }}%</span>
                </div>
              </div>
            </div>
            <div class="status-indicator">
              <span v-if="semester.status === 'completed'">✅ 已完成</span>
              <span v-if="semester.status === 'current'" class="blink">🎯 进行中</span>
              <span v-if="semester.status === 'future'">⏳ 未开启</span>
            </div>
          </div>

        </div>

        <!-- 展开的模块面板 -->
        <transition name="panel">
          <div
            v-if="activeIndex !== null"
            class="module-panel"
            :style="panelPosition"
          >
            <div class="panel-header">
              <h3>{{ currentSemester.name }}成长记录</h3>
              <span class="gpa">GPA: {{ currentSemester.gpa || '--' }}</span>
              <button class="close-btn" @click.stop="closePanel">×</button>
            </div>

            <div class="module-list">
              <div
                v-for="(module, key) in modules"
                :key="key"
                class="module-item"
                @click="handleModuleClick(key)"
              >
                <div class="module-icon">
                  {{ module.icon }}
                </div>
                <div class="module-info">
                  <h4>{{ module.label }}</h4>
                  <p>{{ currentSemester.stats[key] }}</p>
                </div>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import {getAbility} from "@/api/system/student";
import EnhancedRadarChart from '@/components/RadarChart/index.vue';
import store from "../../store";
export default {
  components: {
    EnhancedRadarChart
  },
  data() {
    return {
      title: '双数据对比分析',
      abilityData: null, // 新增存储能力数据
      radarData: [],   // 初始化为空数组
      activeIndex: null,
      panelTop: 0,
      semesters: [], // 改为动态生成
      modules: {
        score: {label: '学业成绩', icon: '📊'},
        competition: {label: '科创竞赛', icon: '🏆'},
        activity: {label: '文体活动', icon: '🎨'},
        report: {label: '讲座报告', icon: '📝'},
        meeting: {label: '导师指导', icon: '👥'}
      }
    }
  },
  computed: {
    studentId() {
      return this.$store.state.user.name;
    },
    // 新增计算属性
    firstHalfSemesters() {
      return this.semesters.slice(0, 4);
    },
    secondHalfSemesters() {
      return this.semesters.slice(4, 8);
    },
    indicators() {
      return this.abilityData ? [
        { name: '学业成绩', max: 100 },
        { name: '科创竞赛', max: 100 },
        { name: '文体活动', max: 100 },
        { name: '讲座报告', max: 100 },
        { name: '导师指导', max: 100 }
      ] : [];
    },
    lastUpdate() {
      return new Date().toLocaleDateString();
    },
    currentSemester() {
      return this.semesters[this.activeIndex] || {}
    },
    panelPosition() {
      return {top: this.panelTop + 'px'}
    },
    admissionYear() {
      return this.$store.state.user.name.substring(0, 4)
    },
    academicPeriod() {
      const start = parseInt(this.admissionYear)
      return `${start}-${start + 4}`
    }
  },
  created() {
    this.semesters = Array.from({length: 8}, (_, index) => {
      const status = this.determineStatus(index)
      return {
        name: `大${['一', '二', '三', '四'][Math.floor(index / 2)]}${index % 2 ? '下' : '上'}`,
        time: this.generateSemesterTime(index),
        progress: this.calculateProgress(index),
        status: status,
        gpa: this.getDefaultGPA(status),
        stats: this.generateDefaultStats(status)
      }
    })
  },
  mounted() {
    this.fetchAbilityData();
  },
  methods: {
    async fetchAbilityData() {
      try {
        const response = await getAbility(this.studentId);
        if (response.code === 200) {
          this.abilityData = response.data;
          console.log(this.abilityData)
          this.prepareRadarData();
        } else {
          this.$message.error('数据获取失败: ' + response.msg);
        }
      } catch (error) {
        console.error('API请求错误:', error);
        this.$message.error('获取能力数据失败');
      } finally {
        this.loading = false;
      }
    },
    // 准备雷达图数据格式
    prepareRadarData() {
      if (!this.abilityData) return;

      this.radarData = [
        {
          name: '综合能力',
          value: [
            this.abilityData.academicScore,
            this.abilityData.competitionScore,
            this.abilityData.activityScore,
            this.abilityData.lectureScore,
            this.abilityData.tutorialScore
          ]
        },
        {
          name: '书院平均',
          value: [60, 65, 70, 60, 55] // 示例数据，可根据实际情况替换
        }
      ];
    },
    // 修改点击处理方法
    handleCardClick(index) {
      if (this.semesters[index].status === 'future') {
        this.$message.warning('该学期尚未开启，请等待');
        return;
      }
      this.togglePanel(index);
    },
    generateSemesterTime(index) {
      const baseYear = parseInt(this.admissionYear);
      const academicYearOffset = Math.floor(index / 2);
      const isFirstSemester = index % 2 === 0;

      // 上学期（9月-次年1月）
      if (isFirstSemester) {
        const startYear = baseYear + academicYearOffset;
        return `${startYear}.09-${startYear + 1}.01`;
      }
      // 下学期（3月-7月）
      else {
        const startYear = baseYear + academicYearOffset + 1;
        return `${startYear}.03-${startYear}.07`;
      }
    },
    determineStatus(index) {
      const currentDate = new Date();
      const currentYear = currentDate.getFullYear();
      const currentMonth = currentDate.getMonth() + 1;

      // 解析学期开始年份
      const [startStr] = this.generateSemesterTime(index).split('-');
      const startYear = parseInt(startStr.split('.')[0]);

      // 状态判断逻辑
      if (startYear < currentYear) return 'completed';
      if (startYear > currentYear) return 'future';

      // 同年度学期判断
      const isFirstSemester = index % 2 === 0;
      if (isFirstSemester) {
        // 上学期（9月-次年1月）
        const isCurrent = currentMonth >= 9 || (currentMonth <= 1 && currentYear === startYear + 1);
        return isCurrent ? 'current' : 'future';
      } else {
        // 下学期（3月-7月）
        const isCurrent = currentMonth >= 3 && currentMonth <= 7;
        return isCurrent ? 'current' : 'future';
      }
    },
    calculateProgress(index) {
      const status = this.determineStatus(index);
      if (status !== 'current') return status === 'completed' ? 100 : 0;

      // 获取学期时间段
      const [startStr, endStr] = this.generateSemesterTime(index).split('-');
      const [startYear, startMonth] = startStr.split('.').map(Number);
      const [endYear, endMonth] = endStr.split('.').map(Number);

      // 获取当前日期
      const current = new Date();
      const currentYear = current.getFullYear();
      const currentMonth = current.getMonth() + 1; // 1-12

      // 计算学期总月数（固定4个月）
      const totalMonths = 4;

      // 计算已过月份（基于学期开始时间）
      let elapsedMonths = 0;

      // 处理上学期（9月-次年1月）
      if (startMonth === 9) {
        elapsedMonths = currentMonth >= 9 ? currentMonth - 9 + 1 : 0;
        if (currentYear > startYear && currentMonth <= 1) {
          elapsedMonths = 5 - (1 - currentMonth); // 处理跨年1月
        }
      }
      // 处理下学期（3月-7月）
      else if (startMonth === 3) {
        elapsedMonths = Math.max(currentMonth - 3 + 1, 0);
      }

      // 计算进度百分比（每月25%）
      let progress = Math.min(elapsedMonths, totalMonths) * 25;

      // 处理学期结束后的进度
      if (currentMonth > endMonth && currentYear >= endYear) {
        progress = 100;
      }

      return Math.min(progress, 100);
    },

    getDefaultGPA(status) {
      return status === 'future' ? null : Number((Math.random() * 0.8 + 3.0).toFixed(1))
    },
    generateDefaultStats(status) {
      const defaults = {
        score: '--',
        competition: '（未开始）',
        activity: '（未开始）',
        report: '（未开始）',
        meeting: '（未开始）'
      }
      if (status === 'future') return defaults

      return {
        score: `${Math.floor(Math.random() * 5)}门优秀`,
        competition: ['参与', '入围', '获奖'][Math.floor(Math.random() * 3)],
        activity: `${Math.floor(Math.random() * 8)}次活动`,
        report: `${Math.floor(Math.random() * 6)}场报告`,
        meeting: `${Math.floor(Math.random() * 10)}次会议`
      }
    },
    togglePanel(index) {
      this.activeIndex = this.activeIndex === index ? null : index
      this.panelTop = window.innerHeight * 0.3
    },
    closePanel() {
      this.activeIndex = null
    },
    progressColor(semester) {
      const statusColors = {
        completed: 'linear-gradient(90deg, #48bb78 0%, #38a169 100%)',
        current: 'linear-gradient(90deg, #4299e1 0%, #3182ce 100%)',
        future: 'linear-gradient(90deg, #cbd5e0 0%, #a0aec0 100%)'
      }
      return statusColors[semester.status]
    },
    handleModuleClick(key) {
      const routes = {
        score: '/GrowthArchive/StudentScoreShow',
        competition: '/GrowthArchive/CompetitonRecord',
        activity: '/GrowthArchive/ActivityRecord',
        report: '/GrowthArchive/ReportManagement',
        meeting: '/GrowthArchive/MentorshipRecord'
      }
      if (routes[key]) {
        this.$router.push({
          path: routes[key],
          query: {semester: this.currentSemester.name}
        })
      }

    }
  }
}
</script>

<style scoped>

/* 设计系统变量 */
:root {
  --primary: #2B6CB0;
  --secondary: #4299E1;
  --success: #48BB78;
  --info: #718096;
  --surface: #F7FAFC;
  --bg-gradient: linear-gradient(160deg, #EBF4FF 0%, #EBF8FF 100%);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

/* 基础容器 */
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); /* 柔和的渐变背景 */
  padding: 20px;
}

.main-container {
  background: #ffffff;
  border-radius: 1.5rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  max-width: 1440px;
  overflow: hidden;
  padding: 2rem;
  position: relative;
  width: 100%;
  margin-left: 100px;
}

/* 导航栏样式 */
.nav {
  background: linear-gradient(135deg, #2B6CB0 0%, #4299E1 100%);
  border-radius: 1.5rem 1.5rem 0 0;
  box-shadow: var(--shadow-lg);
  margin: -2rem -2rem 2rem;
  overflow: hidden;
  position: relative;
}

.nav::after {
  background: linear-gradient(45deg,
  rgba(255, 255, 255, 0.1) 25%,
  transparent 50%,
  rgba(255, 255, 255, 0.1) 75%
  );
  content: "";
  height: 100%;
  left: 0;
  opacity: 0.3;
  position: absolute;
  top: 0;
  width: 100%;
}

.nav-content {
  align-items: center;
  display: flex;
  gap: 0.5rem;
  justify-content: space-between;
  padding: 1.5rem 2rem;
}

.nav h2 {
  align-items: center;
  color: white;
  display: flex;
  font-size: clamp(1.2rem, 3vw, 2rem);
  gap: 0.5rem;
  margin: 0;
  white-space: nowrap;
}

.campus-icon {
  filter: drop-shadow(2px 2px 2px rgba(0, 0, 0, 0.2));
  font-size: 1.8em;
}

.academic-year {
  color: rgba(255, 255, 255, 0.9);
  font-size: clamp(0.8rem, 1.5vw, 0.9rem);
  white-space: nowrap;
}

/* 学期卡片容器 */
.semester-grid-wrapper {
  overflow-x: hidden;
  padding-bottom: 2rem;
  width: 100%;
}

/* 修改网格布局 */
.semester-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: auto minmax(400px, 1fr) auto;/* 中间行高度设为雷达图高度 */
  grid-template-areas:
    "top1 top2 top3 top4"
    "radar radar radar radar"
    "bottom1 bottom2 bottom3 bottom4";
  gap: 1rem;
}

/* 分配网格区域 */
.semester-card.top1 { grid-area: top1; }
.semester-card.top2 { grid-area: top2; }
.semester-card.top3 { grid-area: top3; }
.semester-card.top4 { grid-area: top4; }
.semester-card.bottom1 { grid-area: bottom1; }
.semester-card.bottom2 { grid-area: bottom2; }
.semester-card.bottom3 { grid-area: bottom3; }
.semester-card.bottom4 { grid-area: bottom4; }
.radar-card {
  grid-area: radar;
  margin: 20px 0;
}

.radar-card {
  grid-area: radar;
  background: white;
  border-radius: 1rem;
  padding: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.radar-chart-container {
  padding: 15px;
  height: 100% !important; /* 重要声明 */
}

.radar-card:hover {
  transform: translateY(-3px);
}

.radar-header {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  color: #2B6CB0;
  font-weight: 500;
}

.chart-icon {
  font-size: 1.5em;
  margin-right: 0.8rem;
}

.update-time {
  font-size: 0.8em;
  color: #718096;
  margin-left: 1rem;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .semester-grid {
    grid-template-areas:
      "top1 top2 top3 top4"
      "radar radar radar radar"
      "bottom1 bottom2 bottom3 bottom4";
  }
}

@media (max-width: 992px) {
  .semester-grid {
    grid-template-columns: repeat(2, 1fr);
    grid-template-areas:
      "top1 top2"
      "top3 top4"
      "radar radar"
      "bottom1 bottom2"
      "bottom3 bottom4";
  }
}

@media (max-width: 576px) {
  .semester-grid {
    grid-template-columns: 1fr;
    grid-template-areas:
      "top1"
      "top2"
      "top3"
      "top4"
      "radar"
      "bottom1"
      "bottom2"
      "bottom3"
      "bottom4";
  }

  .radar-card {
    padding: 1rem;
  }
}

/* 卡片状态样式 */
.semester-card.current {
  animation: pulse 2s infinite;
  border-left: 4px solid var(--primary);
}

.semester-card.disabled {
  cursor: not-allowed;
  filter: grayscale(0.8);
  opacity: 0.7;
  overflow: hidden;
  position: relative;
}

.semester-card.disabled::after {
  background: rgba(255, 255, 255, 0.6);
  bottom: 0;
  content: "";
  left: 0;
  position: absolute;
  right: 0;
  top: 0;
  z-index: 1;
}

/* 卡片内部元素 */
.glow-effect {
  height: 100%;
  left: 0;
  opacity: 0;
  position: absolute;
  top: 0;
  transition: opacity 0.3s;
  width: 100%;
}

.semester-card:hover .glow-effect {
  opacity: 1;
}

.semester-header {
  align-items: center;
  display: flex;
  justify-content: space-between;
  margin-bottom: 1.2rem;
}

.semester-name {
  color: #2D3748;
  font-weight: 600;
}

.time {
  color: var(--info);
  font-size: 0.9rem;
}

/* 进度条相关 */
.progress-wrapper {
  background: rgba(237, 242, 247, 0.6);
  border-radius: 0.5rem;
  margin-top: auto;
  padding: 0.3rem;
}

.progress {
  background: rgba(237, 242, 247, 0.8);
  border-radius: 0.4rem;
  height: 1rem;
  overflow: hidden;
  position: relative;
}

.progress-bar {
  border-radius: 0.4rem;
  height: 100%;
  position: relative;
  transition: background 0.4s ease,
  width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.progress-bar::after {
  background-image: linear-gradient(
    -45deg,
    rgba(255, 255, 255, 0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.15) 75%,
    transparent 75%
  );
  background-size: 1.5rem 1.5rem;
  border-radius: 0.4rem;
  bottom: 0;
  content: "";
  left: 0;
  position: absolute;
  right: 0;
  top: 0;
}

.progress-text {
  color: white;
  font-size: 0.7rem;
  font-weight: 500;
  position: absolute;
  right: 0.5rem;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  top: 50%;
  transform: translateY(-50%);
}

/* 状态指示器 */
.status-indicator {
  align-items: center;
  color: var(--success);
  display: flex;
  font-size: 0.8rem;
  gap: 0.5rem;
  margin-top: 1rem;
}

/* 模块面板 */
.module-panel {
  /* 定位相关样式 */
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-40%, -30%); /* 同时修正X/Y轴偏移 */

  /* 其他保持原有样式 */
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 1.5rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
  max-width: 1000px;
  padding: 2rem;
  width: 80%;
  z-index: 1000;
}

.panel-enter-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.panel-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 1, 1);
}

.panel-enter,
.panel-leave-to {
  opacity: 0;
  transform: translate(-50%, -40%); /* 与居中位置保持相同X轴偏移 */
}

.panel-header {
  align-items: center;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
}

.gpa {
  background: linear-gradient(135deg, #48BB78 0%, #38A169 100%);
  font-size: 2rem;
  font-weight: 700;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 模块列表 */
.module-list {
  display: flex;
  gap: 1rem;
  justify-content: space-between;
  overflow: hidden;
}

.module-item {
  cursor: pointer;
  flex: 1;
  flex-shrink: 0;
  max-width: 220px;
  min-width: 160px;
  transition: all 0.3s ease;
}

.module-item:hover {
  box-shadow: 0 16px 24px -6px rgba(66, 153, 225, 0.15);
  transform: translateY(-5px);
}

.module-icon {
  filter: drop-shadow(2px 2px 2px rgba(0, 0, 0, 0.1));
  font-size: 2.5rem;
  margin-bottom: 1rem;
  position: relative;
  z-index: 1;
}

.module-info h4 {
  color: #2d3748;
  font-weight: 600;
  margin: 0 0 0.5rem;
}

.module-info p {
  color: #718096;
  font-size: 0.9rem;
  line-height: 1.4;
  margin: 0;
}

/* 关闭按钮 */
.close-btn {
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  color: #718096;
  cursor: pointer;
  display: flex;
  font-size: 1.5rem;
  height: 32px;
  justify-content: center;
  transition: all 0.3s ease;
  width: 32px;
}

.close-btn:hover {
  background: var(--primary);
  border-color: transparent;
  color: white;
  transform: rotate(90deg);
}

/* 动画效果 */
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(66, 153, 225, 0.3);
  }
  100% {
    box-shadow: 0 0 0 15px rgba(66, 153, 225, 0);
  }
}

@keyframes blink {
  50% {
    opacity: 0.5;
  }
}

.blink {
  animation: blink 1.5s infinite;
}

/* 响应式设计 */
@media (max-width: 1280px) {
  .module-item {
    min-width: 140px;
    padding: 1.2rem;
  }

  .module-icon {
    font-size: 2.2rem;
  }
}

@media (max-width: 1024px) {
  .module-item {
    min-width: 120px;
    padding: 1rem;
  }

  .module-icon {
    font-size: 2rem;
  }

  .module-info h4 {
    font-size: 0.95rem;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0.5rem;
  }

  .main-container {
    border-radius: 1rem;
    padding: 1rem;
  }

  .nav-content {
    flex-direction: column;
  }

  .semester-grid {
    gap: 1rem;
    grid-template-columns: 1fr;
  }

  .module-panel {
    padding: 1rem;
    width: 95%;
  }

  .module-item {
    min-width: 100px;
    padding: 0.8rem;
  }

  .module-icon {
    font-size: 1.8rem;
  }

  .module-info h4 {
    font-size: 0.85rem;
  }

  .module-info p {
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .module-item {
    min-width: 80px;
    padding: 0.6rem;
  }

  .module-icon {
    font-size: 1.5rem;
  }

  .module-info h4 {
    font-size: 0.75rem;
  }
}
/* 学期卡片 - 视觉升级 */
.semester-card {
  background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  padding: 1.5rem;
  position: relative;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  cursor: pointer;
  overflow: hidden;
  border: 1px solid rgba(235, 244, 255, 0.6);
}

/* 卡片悬停效果 */
.semester-card:not(.disabled):hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px -6px rgba(43, 108, 176, 0.15),
  0 0 15px -3px rgba(66, 153, 225, 0.1);
}

/* 卡片激活状态 */
.semester-card.active {
  border-color: #93c5fd;
  background: linear-gradient(145deg, #f0f7ff 0%, #ebf4ff 100%);
}

/* 卡片光晕效果 */
.semester-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 200%;
  height: 100%;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 25%,
    rgba(255, 255, 255, 0.2) 50%,
    rgba(255, 255, 255, 0) 75%
  );
  transform: skewX(-20deg);
  transition: left 0.6s ease-in-out;
}

.semester-card:not(.disabled):hover::after {
  left: 100%;
}

/* 状态指示器增强 */
.status-indicator {
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 0.85rem;
  display: inline-flex;
  align-items: center;
  backdrop-filter: blur(4px);
}

/* 不同状态背景 */
.status-indicator span {
  padding: 4px 8px;
  border-radius: 6px;
}

.completed .status-indicator {
  background: rgba(72, 187, 120, 0.1);
  color: #2b6c53;
}

.current .status-indicator {
  background: rgba(66, 153, 225, 0.1);
  color: #1a4d8c;
}

.future .status-indicator {
  background: rgba(160, 174, 192, 0.1);
  color: #4a5568;
}

/* 进度条立体效果 */
.progress-wrapper {
  border-radius: 8px;
  background: rgba(237, 242, 247, 0.5);
  box-shadow: inset 2px 2px 4px rgba(0, 0, 0, 0.05);
}

.progress {
  height: 12px;
  border-radius: 6px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.8);
  box-shadow: inset 1px 1px 3px rgba(0, 0, 0, 0.08);
}

.progress-bar {
  border-radius: 6px;
  position: relative;
  transition: width 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 进度条光泽效果 */
.progress-bar::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0.3) 0%,
    transparent 50%,
    rgba(255, 255, 255, 0.3) 100%
  );
  mix-blend-mode: overlay;
}

/* 学期名称样式升级 */
.semester-name {
  font-weight: 600;
  color: #2d3748;
  font-size: 1.1rem;
  letter-spacing: 0.5px;
  position: relative;
  display: inline-flex;
  align-items: center;
}

.semester-name::before {
  content: '';
  display: block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 8px;
  background: currentColor;
}

.current .semester-name::before {
  animation: dot-pulse 1.5s infinite;
}

@keyframes dot-pulse {
  0% { opacity: 0.5; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1.2); }
  100% { opacity: 0.5; transform: scale(0.8); }
}

/* 时间显示优化 */
.time {
  font-size: 0.9rem;
  color: #4a5568;
  background: rgba(237, 242, 247, 0.6);
  padding: 4px 8px;
  border-radius: 6px;
  font-feature-settings: "tnum";
}
</style>

