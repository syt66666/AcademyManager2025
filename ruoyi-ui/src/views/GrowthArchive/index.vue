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
          <div
            v-for="(semester, index) in semesters"
            :key="index"
            class="semester-card"
            :class="[semester.status, {
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
</template>

<script>
export default {
  data() {
    return {
      activeIndex: null,
      panelTop: 0,
      semesters: [], // 改为动态生成
      modules: {
        score: { label: '学业成绩', icon: '📊' },
        competition: { label: '科创竞赛', icon: '🏆' },
        activity: { label: '文体活动', icon: '🎭' },
        report: { label: '学术报告', icon: '📝' },
        meeting: { label: '导师会议', icon: '👥' }
      }
    }
  },
  computed: {
    currentSemester() {
      return this.semesters[this.activeIndex] || {}
    },
    panelPosition() {
      return { top: this.panelTop + 'px' }
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
    this.semesters = Array.from({ length: 8 }, (_, index) => {
      const status = this.determineStatus(index)
      return {
        name: `大${['一','二','三','四'][Math.floor(index/2)]}${index%2 ? '下' : '上'}`,
        time: this.generateSemesterTime(index),
        progress: this.calculateProgress(index),
        status: status,
        gpa: this.getDefaultGPA(status),
        stats: this.generateDefaultStats(status)
      }
    })
  },
  methods: {
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
        score: `${Math.floor(Math.random()*5)}门优秀`,
        competition: ['参与','入围','获奖'][Math.floor(Math.random()*3)],
        activity: `${Math.floor(Math.random()*8)}次活动`,
        report: `${Math.floor(Math.random()*6)}场报告`,
        meeting: `${Math.floor(Math.random()*10)}次会议`
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
        report: '/ReportManagement/index',
        meeting: '/GrowthArchive/MentorshipRecord'
      }
      if (routes[key]) {
        this.$router.push({
          path: routes[key],
          query: { semester: this.currentSemester.name }
        })
      }

      // if (key === 'report') {
      //   this.$router.push({path:'/ReportManagement/index', query: { semester: this.currentSemester.name } })
      // }
      // if (key === 'meeting') {
      //   this.$router.push({path:'/GrowthArchive/MentorshipRecord', query: { semester: this.currentSemester.name } })
      // }
    }
  }
}
</script>


<style scoped>
/* 添加禁用样式 */
.semester-card.disabled {
  cursor: not-allowed;
  opacity: 0.7;
  filter: grayscale(0.8);
  position: relative;
  overflow: hidden;
}

/* 禁用状态的辉光效果 */
.semester-card.disabled .glow-effect {
  display: none;
}

/* 添加遮罩层 */
.semester-card.disabled::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255,255,255,0.6);
  z-index: 1;
}

/* 调整进度条颜色 */
.semester-card.disabled .progress-bar {
  background: linear-gradient(90deg, #e2e8f0 0%, #cbd5e0 100%) !important;
}

/* 禁用状态下的文字颜色 */
.semester-card.disabled .semester-name,
.semester-card.disabled .time {
  color: #94a3b8;
}


.semester-card.current {
  border-left: 4px solid var(--primary);
  animation: pulse 2s infinite; /* 核心动画效果 */
}

/* 动画 */
@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(66,153,225,0.3); }
  100% { box-shadow: 0 0 0 15px rgba(66,153,225,0); }
}

@keyframes blink {
  50% { opacity: 0.5; }
}
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
  max-width: 100%; /* 改为全宽 */
  margin: 0 auto;
  padding: 1rem; /* 减少内边距 */
  background: var(--bg-gradient);
  min-height: 100vh;
  /* 移除 grid 布局 */
}

.main-container {
  background: #ffffff;
  border-radius: 1.5rem;
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1);
  padding: 2rem;
  margin: 0 auto;
  max-width: 1440px;
  position: relative;
  overflow: hidden;
}

/* 导航栏 */
.nav {
  background: linear-gradient(135deg, #2B6CB0 0%, #4299E1 100%);
  border-radius: 1.5rem 1.5rem 0 0;
  margin: -2rem -2rem 2rem;
  box-shadow: var(--shadow-lg);
  position: relative;
  overflow: hidden;
}

.nav::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg,
  rgba(255,255,255,0.1) 25%,
  transparent 50%,
  rgba(255,255,255,0.1) 75%
  );
  opacity: 0.3;
}

.nav-content {
  padding: 1.5rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav h2 {
  color: white;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0;
  font-size: clamp(1.2rem, 3vw, 2rem);
  white-space: nowrap;
}

.campus-icon {
  font-size: 1.8em;
  filter: drop-shadow(2px 2px 2px rgba(0,0,0,0.2));
}

.academic-year {
  color: rgba(255,255,255,0.9);
  font-size: clamp(0.8rem, 1.5vw, 0.9rem);
  white-space: nowrap;
}

/* 优化学期网格容器 */
.semester-grid-wrapper {
  width: 100%;
  overflow-x: hidden; /* 隐藏横向滚动 */
  padding-bottom: 2rem;
}

/* 修改网格布局 */
.semester-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); /* 自动适应列数 */
  gap: 1.5rem;
  width: 100%;
  padding: 0 1rem;
  margin-top: 1.5rem;
  min-width: auto; /* 移除固定最小宽度 */
}

.semester-card {
  background: var(--surface);
  border-radius: 1rem;
  padding: 1.5rem;
  position: relative;
  overflow: hidden;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1),
  box-shadow 0.3s ease;
  cursor: pointer;
  width: 100%;
  height: 100%;
  min-height: 180px;
  box-sizing: border-box;
}

.glow-effect {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at var(--x) var(--y),
  rgba(66, 153, 225, 0.1) 0%,
  transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
}

.semester-card:hover .glow-effect {
  opacity: 1;
}

.semester-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
}

.semester-name {
  font-weight: 600;
  color: #2D3748;
}

.time {
  color: var(--info);
  font-size: 0.9rem;
}

/* 进度条 */
.progress-wrapper {
  background: rgba(237, 242, 247, 0.6);
  border-radius: 0.5rem;
  padding: 0.3rem;
  margin-top: auto;
}

.progress {
  height: 1rem;
  background: rgba(237, 242, 247, 0.8);
  border-radius: 0.4rem;
  overflow: hidden;
  position: relative;
}

.progress-bar {
  height: 100%;
  border-radius: 0.4rem;
  position: relative;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1),
  background 0.4s ease;
}

.progress-bar::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: linear-gradient(
    -45deg,
    rgba(255,255,255,0.15) 25%,
    transparent 25%,
    transparent 50%,
    rgba(255,255,255,0.15) 50%,
    rgba(255,255,255,0.15) 75%,
    transparent 75%
  );
  background-size: 1.5rem 1.5rem;
  border-radius: 0.4rem;
}

.progress-text {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  color: white;
  font-size: 0.7rem;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

.status-indicator {
  margin-top: 1rem;
  font-size: 0.8rem;
  color: var(--success);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* 模块面板 */
.module-panel {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -30%);
  width: 80%;
  max-width: 1000px;
  z-index: 1000;
  background: rgba(255,255,255,0.98);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.3);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
  border-radius: 1.5rem;
  padding: 2rem;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(0,0,0,0.1);
  margin-bottom: 1.5rem;
}

.gpa {
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #48BB78 0%, #38A169 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 模块项 */
.module-list {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  overflow: hidden;
}

.module-item {
  flex: 1;
  min-width: 160px;
  max-width: 220px;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.module-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 16px 24px -6px rgba(66, 153, 225, 0.15);
}

.module-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  filter: drop-shadow(2px 2px 2px rgba(0,0,0,0.1));
  position: relative;
  z-index: 1;
}

.module-info h4 {
  margin: 0 0 0.5rem;
  color: #2d3748;
  font-weight: 600;
}

.module-info p {
  margin: 0;
  color: #718096;
  font-size: 0.9rem;
  line-height: 1.4;
}

.close-btn {
  background: rgba(255,255,255,0.9);
  border: 1px solid rgba(0,0,0,0.1);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1.5rem;
  color: #718096;
}

.close-btn:hover {
  background: var(--primary);
  color: white;
  transform: rotate(90deg);
  border-color: transparent;
}

.blink {
  animation: blink 1.5s infinite;
}

/* 优化导航栏响应式 */
.nav-content {
  flex-direction: column; /* 小屏垂直排列 */
  align-items: flex-start;
  gap: 0.5rem;
}


/* 优化模块面板 */
.module-panel {
  width: 100%;
  max-width: 900px;
  max-height: 80vh; /* 限制高度 */
}

/* 添加移动端触摸滚动优化 */
.semester-grid-wrapper {
  -webkit-overflow-scrolling: touch;
}/* iOS 平滑滚动 */
/* 响应式调整 */
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
  .container {
    padding: 0.5rem;
  }

  .main-container {
    padding: 1rem;
    border-radius: 1rem;
  }

  .semester-grid {
    grid-template-columns: 1fr; /* 小屏幕单列显示 */
    gap: 1rem;
  }

  .module-panel {
    width: 95%; /* 弹窗更窄 */
    padding: 1rem;
  }

  .nav-content {
    flex-direction: row; /* 大屏水平排列 */
    align-items: center;
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
</style>

