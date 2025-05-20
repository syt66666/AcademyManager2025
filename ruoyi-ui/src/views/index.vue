<template>
  <div class="dashboard-container">
    <!-- 左侧信息面板 -->
    <div class="left-panel">
      <!-- 学生信息卡片 -->
      <div class="info-card">
        <div class="greeting-section">
          <h1 class="greeting-text">{{ greetingText }}</h1>
          <p class="greeting-subtext">大工书院祝您学业进步！🎓</p>
        </div>

        <div class="user-info">
          <div class="info-row">
            <span class="info-label">所属书院</span>
            <span class="info-value">{{ department }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">系统专业</span>
            <span class="info-value">{{ specialty }}</span>
          </div>
          <div v-if="major" class="info-row">
            <span class="info-label">录取专业</span>
            <span class="info-value">{{ major }}</span>
          </div>
        </div>

        <div v-if="showSpecialNotice" class="special-notice">
          <i class="el-icon-warning"></i>
          您所在专业为选拔专业，不可变更
        </div>
      </div>

      <!-- 能力雷达图 -->
      <div class="radar-card">
        <div class="card-header">
          <span class="header-icon">📊</span>
          <h3 class="header-title">综合能力评估</h3>
          <span class="update-time">更新于 {{ lastUpdate }}</span>
        </div>
        <enhanced-radar-chart
          :data="radarData"
          :indicators="indicators"
          style="height: 380px; margin-top: 15px;"
        />
      </div>
    </div>

    <!-- 右侧成长档案 -->
    <div class="right-panel">
      <h2 class="growth-title">成长档案概览</h2>
      <div class="growth-grid">
        <div
          v-for="(item, key) in growthData"
          :key="key"
          class="growth-card"
          :class="[key, { 'highlight': item.highlight }]"
          @click="handleCardClick(key)"
        >
          <div class="card-icon">{{ item.icon }}</div>
          <div class="card-content">
            <h3 class="card-title">{{ item.label }}</h3>
            <div class="data-section">
              <span class="main-value">{{ item.value }}</span>
              <span class="unit">{{ item.unit }}</span>
            </div>
            <div class="trend-section">
              <span :class="['trend', item.trend]">
                {{ trendIcon(item.trend) }} {{ item.ratio }}%
              </span>
              <span class="trend-label">学期同比</span>
            </div>
          </div>
        </div>
      </div>
      <!--     智能助手浮窗 -->
      <div class="chat-assistant" @click="toggleChat">
        <i class="el-icon-chat-dot-round"></i>
      </div>
      <!--     聊天对话框 -->
      <el-dialog
        :visible.sync="chatVisible"
        title="大工智能助手"
        width="600px"
        custom-class="chat-dialog"
        @closed="resetChat"
      >
        <div class="chat-container">
          <div class="messages" ref="messages">
            <div
              v-for="(msg, index) in chatMessages"
              :key="index"
              :class="['message', msg.role]"
            >
              <div class="content">{{ msg.content }}</div>
              <div class="time">{{ msg.time }}</div>
            </div>
            <div v-if="isLoading" class="loading">
              <i class="el-icon-loading"></i> 助手思考中...
            </div>
          </div>

          <div class="input-area">
            <el-input
              type="textarea"
              :rows="3"
              v-model="inputMessage"
              @keyup.enter.native="sendMessage"
              placeholder="输入您的问题（Shift+Enter换行）"
            ></el-input>
            <el-button
              type="primary"
              @click="sendMessage"
              :disabled="!inputMessage.trim()"
            >
              发送
            </el-button>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>

</template>

<script>
import {getNickName, getStudent} from "@/api/system/student";
import axios from "axios";
import { getAbility } from "@/api/system/student";
import EnhancedRadarChart from '@/components/RadarChart/index.vue';
export default {
  name: "DashboardPage",
  components: {
    EnhancedRadarChart,
  },
  data() {
    return {
      // 表单参数
      form: {},
      list: {},
      studentName: '',
      department: '',
      major: '',
      specialty: '',
      specialClass: '',
      splitFlow: '',
      nickName:'',
      chatVisible: false,
      inputMessage: '',
      chatMessages: [],
      isLoading: false,
      // 雷达图数据
      abilityData: null,
      radarData: [],

      // 成长数据
      growthData: {
        competition: {
          label: '科创竞赛',
          icon: '🏆',
          value: 0,
          unit: '次参与',
          ratio: 0,
          trend: 'up',
          highlight: false
        },
        activity: {
          label: '文体活动',
          icon: '🎨',
          value: 0,
          unit: '项成果',
          ratio: 0,
          trend: 'up',
          highlight: true
        },
        report: {
          label: '讲座报告',
          icon: '📚',
          value: 0,
          unit: '场参与',
          ratio: 0,
          trend: 'flat',
          highlight: false
        },
        mentorship: {
          label: '导师指导',
          icon: '👥',
          value: 0,
          unit: '次交流',
          ratio: 0,
          trend: 'up',
          highlight: false
        }
      }
    };
  },
  computed: {
    userName() {
      return this.$store.state.user.name;
    },
    greetingText() {
      if (this.userName === 'admin') return '您好，管理员';
      if (this.isSpecialAdmin) return `您好，${this.studentName}管理员`;
      return `欢迎回来，${this.studentName}同学`;
    },
    showSpecialNotice() {
      const specialMajors = [
        '土木工程（国际班）',
        '金属材料工程（中日精英班）',
        '机械设计制造及其自动化(日语强化)',
        '机械设计制造及其自动化（卓越国合班）'
      ];
      return specialMajors.includes(this.specialty);
    },
    isSpecialAdmin() {
      return /^1000[1-7]$/.test(this.userName);
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
      return new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
    }
  },
  mounted() {
    this.loadStudentData();
    this.loadAbilityData();
  },
  methods: {
    async loadStudentData() {
      try {
        if (this.userName === 'admin') return;

        const response = await getStudent(this.userName);
        const data = response.studentInfo;

        this.studentName = data.studentName;
        this.department = data.academy || '--';
        this.specialty = data.originalSystemMajor || '--';
        this.major = data.major;
        this.splitFlow = data.divertForm;

        // 初始化成长数据
        this.initGrowthData(data);
      } catch (error) {
        console.error('学生数据加载失败:', error);
      }
    },

    async loadAbilityData() {
      try {
        const response = await getAbility(this.userName);
        if (response.code === 200) {
          this.abilityData = response.data;
          this.prepareRadarData();
        }
      } catch (error) {
        console.error('能力数据加载失败:', error);
      }
    },

    initGrowthData(studentData) {
      // 模拟数据，实际应替换为API数据
      this.growthData.competition.value = studentData.competitionCount || 0;
      this.growthData.activity.value = studentData.activityCount || 0;
      this.growthData.report.value = studentData.lectureCount || 0;
      this.growthData.mentorship.value = studentData.mentorMeetingCount || 0;
    },

    prepareRadarData() {
      if (!this.abilityData) return;

      this.radarData = [
        {
          name: '个人能力',
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
          value: [75, 60, 65, 70, 68]
        }
      ];
    },

    trendIcon(trend) {
      return {
        up: '↑',
        down: '↓',
        flat: '→'
      }[trend];
    },

    handleCardClick(type) {
      // 处理卡片点击事件
      const routes = {
        competition: '/GrowthArchive/CompetitonRecord',
        activity: '/GrowthArchive/ActivityRecord',
        report: '/GrowthArchive/ReportManagement',
        mentorship: '/GrowthArchive/MentorshipRecord'
      };
      if (routes[type]) {
        this.$router.push(routes[type]);
      }
    },
    toggleChat() {
      this.chatVisible = !this.chatVisible
      console.log(this.chatVisible)
    },

    async sendMessage() {
      if (!this.inputMessage.trim()) return

      // 添加用户消息
      const userMsg = {
        content: this.inputMessage,
        role: 'user',
        time: this.formatTime(new Date())
      }
      this.chatMessages.push(userMsg)

      const question = this.inputMessage
      this.inputMessage = ''
      this.isLoading = true

      try {
        const response = await // 修改axios请求为绝对路径
          axios.post(process.env.VUE_APP_BASE_API+'/system/assistant/chat', {
            message: question
          })

        // 添加助手消息
        this.chatMessages.push({
          content: response.data.msg,
          role: 'assistant',
          time: this.formatTime(new Date())
        })

        this.scrollToBottom()
      } catch (error) {
        this.$message.error('请求失败：' + error.message)
        this.chatMessages.pop() // 移除用户消息
      } finally {
        this.isLoading = false
      }
    },

    formatTime(date) {
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messages
        container.scrollTop = container.scrollHeight
      })
    },

    resetChat() {
      this.chatMessages = []
      this.inputMessage = ''
    }
  }
};
</script>

<style scoped>
.dashboard-container {
  display: grid;
  grid-template-columns: 1fr 1.8fr;
  gap: 30px;
  padding-left: 120px;
  padding-top: 20px;
  min-height: calc(100vh - 60px);
  background: #f8fafc;
}

/* 左侧面板样式 */
.left-panel {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.info-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.greeting-section {
  margin-bottom: 20px;
}

.greeting-text {
  font-size: 2.2rem;
  color: #2c3e50;
  margin-bottom: 8px;
}

.greeting-subtext {
  color: #718096;
  font-size: 1rem;
}

.user-info {
  margin: 25px 0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px;
  margin: 10px 0;
  background: #f8f9fa;
  border-radius: 8px;
}

.info-label {
  color: #4a5568;
  font-size: 0.95rem;
}

.info-value {
  color: #2d3748;
  font-weight: 500;
}

.special-notice {
  padding: 15px;
  background: #fff9e6;
  border-radius: 8px;
  color: #e67e22;
  display: flex;
  align-items: center;
  font-size: 0.9rem;
  margin-top: 20px;
}

.special-notice i {
  margin-right: 10px;
  font-size: 1.2rem;
}

/* 雷达图卡片 */
.radar-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  flex: 1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.header-icon {
  font-size: 1.8rem;
  margin-right: 12px;
}

.header-title {
  font-size: 1.2rem;
  color: #2d3748;
  margin: 0;
  flex: 1;
}

.update-time {
  color: #718096;
  font-size: 0.9rem;
}

/* 右侧成长档案 */
.right-panel {
  background: #ffffff;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.growth-title {
  color: #2d3748;
  margin-bottom: 25px;
  font-size: 1.4rem;
}

.growth-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.growth-card {
  padding: 20px;
  border-radius: 12px;
  background: #ffffff;
  display: flex;
  transition: all 0.3s ease;
  cursor: pointer;
  border-left: 4px solid;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.05);
}

.growth-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.growth-card.competition { border-color: #ff6b6b; }
.growth-card.activity { border-color: #4ecdc4; }
.growth-card.report { border-color: #ff9f43; }
.growth-card.mentorship { border-color: #5f27cd; }

.card-icon {
  font-size: 2.8rem;
  margin-right: 20px;
  width: 60px;
  text-align: center;
}

.card-content {
  flex: 1;
}

.card-title {
  color: #4a5568;
  font-size: 1.1rem;
  margin: 0 0 12px 0;
}

.data-section {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.main-value {
  font-size: 2rem;
  font-weight: 600;
  color: #2d3748;
  margin-right: 8px;
}

.unit {
  color: #718096;
  font-size: 0.9rem;
}

.trend-section {
  display: flex;
  align-items: center;
}

.trend {
  font-size: 0.95rem;
  margin-right: 8px;
  font-weight: 500;
}

.trend.up { color: #2ecc71; }
.trend.down { color: #e74c3c; }
.trend.flat { color: #f39c12; }

.trend-label {
  color: #718096;
  font-size: 0.85rem;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .dashboard-container {
    grid-template-columns: 1fr;
  }

  .left-panel {
    order: 2;
  }

  .right-panel {
    order: 1;
    margin-bottom: 30px;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 15px;
  }

  .growth-grid {
    grid-template-columns: 1fr;
  }

  .greeting-text {
    font-size: 1.8rem;
  }

  .card-icon {
    font-size: 2.2rem;
    margin-right: 15px;
  }

  .main-value {
    font-size: 1.6rem;
  }
}
/* 智能助手图标美化 */
.chat-assistant {
  position: fixed;
  right: 40px;
  bottom: 40px;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #6c5ce7 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
  z-index: 9999;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: float 3s ease-in-out infinite;
}

.chat-assistant:hover {
  transform: scale(1.1) rotate(10deg);
  box-shadow: 0 12px 32px rgba(64, 158, 255, 0.4);
}

.chat-assistant i {
  font-size: 28px;
  transition: transform 0.3s ease;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

/* 对话框美化 */
.chat-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}

.chat-dialog .el-dialog__header {
  background: linear-gradient(135deg, #409EFF 0%, #6c5ce7 100%);
  padding: 20px;
  border-radius: 16px 16px 0 0;
}

.chat-dialog .el-dialog__title {
  color: white !important;
  font-size: 20px;
  font-weight: 600;
}

.chat-dialog .el-dialog__headerbtn {
  top: 24px;
  right: 24px;
  font-size: 20px;
}

/* 消息容器美化 */
.chat-container {
  height: 60vh;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

.messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: white;
}

.message {
  margin: 12px 0;
  max-width: 80%;
  animation: messageAppear 0.3s ease;
}

</style>

