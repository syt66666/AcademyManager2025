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
        <div class="academic-year">2023-2027 学籍周期</div>
      </div>
    </div>

    <!-- 学期卡片容器 -->
    <div class="semester-grid">
      <div
        v-for="(semester, index) in semesters"
        :key="index"
        class="semester-card"
        :class="[semester.status, { 'active': activeIndex === index }]"
        @click="togglePanel(index)"
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
          <span class="gpa">GPA: {{ currentSemester.gpa }}</span>
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

      semesters: [
        {
          name: '大一上',
          time: '2023.09-2024.01',
          progress: 100,
          status: 'completed',
          gpa: 3.2,
          stats: {
            score: '4门优秀',
            competition: '新生编程赛参与',
            activity: '5次文体活动',
            report: '2场学术报告',
            meeting: '3次导师会议'
          }
        },
        {
          name: '大一下',
          time: '2024.03-2024.07',
          progress: 100,
          status: 'completed',
          gpa: 3.5,
          stats: {
            score: '2门A+课程',
            competition: '数学建模校赛二等奖',
            activity: '运动会田径冠军',
            report: '4场专业讲座',
            meeting: '5次课题讨论'
          }
        },
        {
          name: '大二上',
          time: '2024.09-2025.01',
          progress: 100,
          status: 'completed',
          gpa: 3.7,
          stats: {
            score: '专业排名前10%',
            competition: '挑战杯省赛金奖',
            activity: '校园歌手大赛',
            report: '6场行业前沿报告',
            meeting: '8次实验室组会'
          }
        },
        {
          name: '大二下',
          time: '2025.03-2025.07',
          progress: 85,
          status: 'current',
          gpa: 3.8,
          stats: {
            score: '3篇课程论文优秀',
            competition: '互联网+创业大赛入围',
            activity: '篮球联赛MVP',
            report: '5场跨学科论坛',
            meeting: '10次项目推进会'
          }
        },
        {
          name: '大三上',
          time: '2025.09-2026.01',
          progress: 0,
          status: 'future',
          gpa: null,
          stats: {
            score: '--',
            competition: '（未开始）',
            activity: '（未开始）',
            report: '（未开始）',
            meeting: '（未开始）'
          }
        },
        {
          name: '大三下',
          time: '2026.03-2026.07',
          progress: 0,
          status: 'future',
          gpa: null,
          stats: {
            score: '--',
            competition: '（未开始）',
            activity: '（未开始）',
            report: '（未开始）',
            meeting: '（未开始）'
          }
        },
        {
          name: '大四上',
          time: '2026.09-2027.01',
          progress: 0,
          status: 'future',
          gpa: null,
          stats: {
            score: '--',
            competition: '（未开始）',
            activity: '（未开始）',
            report: '（未开始）',
            meeting: '（未开始）'
          }
        },
        {
          name: '大四下',
          time: '2027.03-2027.07',
          progress: 0,
          status: 'future',
          gpa: null,
          stats: {
            score: '--',
            competition: '（未开始）',
            activity: '（未开始）',
            report: '（未开始）',
            meeting: '（未开始）'
          }
        }
      ],
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
    // gpaToPercent() {
    //   return Math.floor((this.currentSemester.gpa / 4.0) * 100)
    // },
  },
  methods: {
    togglePanel(index) {
      if (this.activeIndex === index) {
        this.activeIndex = null
      } else {
        this.activeIndex = index
        // 简单计算面板位置（实际需根据元素位置计算）
        this.panelTop = 200 + Math.floor(index / 4) * 150
      }
    },
    closePanel() {
      this.activeIndex = null;
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
      // if (key === 'score') {
      //   this.$router.push('/GrowthArchive/StudentScoreShow')
      // }
      if (key === 'score') {
        this.$router.push({ path: '/GrowthArchive/StudentScoreShow', query: { semester: this.currentSemester.name } });
      }
      if (key === 'competition') {
        this.$router.push({path:'/GrowthArchive/CompetitonRecord', query: { semester: this.currentSemester.name } });
      }
      if (key === 'activity') {
        this.$router.push({path:'/GrowthArchive/ActivityRecord', query: { semester: this.currentSemester.name } })
      }
      if (key === 'report') {
        this.$router.push('/ReportManagement/index')
      }
      if (key === 'meeting') {
        this.$router.push('')
      }
    }

  }
}
</script>

<style scoped>
/* 新增设计系统变量 */
:root {
  --primary: #2B6CB0;
  --secondary: #4299E1;
  --success: #48BB78;
  --warning: #ECC94B;
  --info: #718096;
  --surface: #F7FAFC;
  --bg-gradient: linear-gradient(160deg, #EBF4FF 0%, #EBF8FF 100%);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

/* 容器优化 */
.container {
  max-width: 1440px;
  background: var(--bg-gradient);
  min-height: 100vh;
  padding: 2rem;
}

/* 导航栏高级效果 */
.nav {
  background: linear-gradient(135deg, #2B6CB0 0%, #4299E1 100%);
  border-radius: 1rem;
  margin-bottom: 2.5rem;
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
  padding: 1.5rem 30rem;
  position: relative;
  z-index: 1;
}

.nav h2 {
  color: white;
  font-size: 2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 0;
}

.campus-icon {
  font-size: 1.8em;
  filter: drop-shadow(2px 2px 2px rgba(0,0,0,0.2));
}


.academic-year {
  color: rgba(255,255,255,0.9);
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

/* 学期卡片高级效果 */
.semester-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  row-gap: 3rem;
  padding-bottom: 15rem;
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

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(66,153,225,0.3); }
  100% { box-shadow: 0 0 0 15px rgba(66,153,225,0); }
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

/* 进度条高级样式 */
.progress-wrapper {
  background: rgba(237, 242, 247, 0.6);
  border-radius: 0.5rem;
  padding: 0.3rem;
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
  background: linear-gradient(90deg,
  var(--primary) 0%,
  var(--secondary) 100%);
  border-radius: 0.4rem;
  position: relative;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
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

.blink {
  animation: blink 1.5s infinite;
}

@keyframes blink {
  50% { opacity: 0.5; }
}

/* 模块面板高级样式 */
.module-panel {
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(12px) saturate(180%);
  border: 1px solid rgba(255,255,255,0.4);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
  border-radius: 1.5rem;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(0,0,0,0.08);
}


.gpa {
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #48BB78 0%, #38A169 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 模块项高级样式 */
.module-item {
  background: rgba(247, 250, 252, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.6);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.module-item:hover {
  background: white;
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
}

/* 模块图标颜色 */
.module-item.score .module-icon { color: #48BB78; }
.module-item.competition .module-icon { color: #F6AD55; }
.module-item.activity .module-icon { color: #4299E1; }
.module-item.report .module-icon { color: #9F7AEA; }
.module-item.meeting .module-icon { color: #F56565; }

/* 响应式优化增强 */
@media (max-width: 1024px) {
  .semester-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .module-panel {
    width: 95%;
  }
}

@media (max-width: 768px) {
  .nav h2 {
    font-size: 1.5rem;
  }

  .semester-grid {
    grid-template-columns: 1fr;
  }

  .module-list {
    grid-template-columns: repeat(2, 1fr);
  }
}


/* 新增玻璃材质效果 */
.module-panel {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  max-width: 1000px;
  background: rgba(255,255,255,0.98);
  border-radius: 16px;
  padding: 2rem;
  z-index: 1000;
  backdrop-filter: blur(10px);
  box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04);
  border: 1px solid rgba(255,255,255,0.3);
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(0,0,0,0.1);
  margin-bottom: 1.5rem;
}

.panel-header h3 {
  margin: 0;
  font-size: 1.5rem;
  color: #1a237e;
}

.gpa {
  font-size: 1.2rem;
  background: linear-gradient(45deg, #67C23A, #85ce61);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
/* 模块项升级 */
.module-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 1.5rem;
}

.module-item {
  padding: 1.5rem;
  border-radius: 12px;
  background: rgba(250,250,250,0.8);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.module-item:hover {
  background: white;
  transform: translateY(-3px);
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
}

.module-item::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: var(--primary);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.module-item:hover::before {
  transform: scaleX(1);
}

.module-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  filter: drop-shadow(2px 2px 2px rgba(0,0,0,0.1));
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

/* 标题区域升级 */
.panel-header {
  padding: 2rem;
  background: linear-gradient(
    135deg,
    rgba(66, 153, 225, 0.08) 0%,
    rgba(255,255,255,0.02) 100%
  );
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.gpa {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #48BB78 0%, #38A169 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}


.progress-ring circle {
  fill: none;
  stroke-width: 4;
  stroke: #4299E1;
  stroke-linecap: round;
  transform: rotate(-90deg);
  transform-origin: center;
  transition: stroke-dasharray 0.8s ease;
}

/* 模块项深度美化 */
.module-item {
  background: rgba(255,255,255,0.9);
  border-radius: 1.5rem;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: visible;
  position: relative;
}

.module-item:hover {
  transform: translateY(-5px);
  box-shadow:
    0 16px 24px -6px rgba(66, 153, 225, 0.15),
    inset 0 0 24px rgba(255,255,255,0.4);
}


.module-icon {
  position: relative;
  font-size: 2.5rem;
  z-index: 1;
}


@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}
/* 修改容器样式 */
.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 2rem;
  display: grid;
  place-items: center;
}


/* 网格布局优化 */
.semester-grid {
  width: 100%;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  justify-items: center;
}

/* 卡片宽度限制 */
.semester-card {
  width: 100%;
  max-width: 350px;
}

/* 模块面板居中增强 */
.module-panel {
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
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
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.progress-bar {
  transition:
    width 0.6s cubic-bezier(0.4, 0, 0.2, 1),
    background 0.4s ease;
}

/* 添加状态指示条纹 */
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

/* 响应式优化 */
@media (max-width: 1024px) {
  .semester-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .semester-grid {
    grid-template-columns: 1fr;
  }

  .nav h2 {
    font-size: 1.4rem;
  }
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

/* 调整导航栏边距 */
.nav {
  margin: -2rem -2rem 2rem; /* 扩展导航栏到容器边缘 */
  border-radius: 1.5rem 1.5rem 0 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .main-container {
    padding: 1rem;
    border-radius: 1rem;
  }

  .nav {
    margin: -1rem -1rem 1.5rem;
    border-radius: 1rem 1rem 0 0;
  }
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(66,153,225,0.3); }
  100% { box-shadow: 0 0 0 15px rgba(66,153,225,0); }
}

.blink {
  animation: blink 1.5s infinite; /* 文字闪烁效果 */
}

</style>
