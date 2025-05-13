<template>
  <div class="welcome-container">
    <div class="welcome-card">
      <h1 class="welcome-message"
          v-if="this.userName !== 'admin'&&this.userName!== '10001'&&this.userName!== '10002'&&this.userName!== '10003'&&this.userName!== '10004'&&this.userName!== '10005'&&this.userName!== '10006'&&this.userName!== '10007'">
        您好，{{ studentName }}！</h1>
      <h1 class="welcome-message"
          v-if="this.userName === 'admin'">
        您好，管理员！</h1>
      <h1 class="welcome-message"
          v-if="this.userName === '10001'||this.userName === '10002'||this.userName === '10003'||this.userName === '10004'||this.userName === '10005'||this.userName === '10006'||this.userName === '10007'">
        您好，{{ studentName }}管理员！</h1>
      <p class="user-info" v-if="this.userName !== 'admin'&&this.userName!== '10001'&&this.userName!== '10002'&&this.userName!== '10003'&&this.userName!== '10004'&&this.userName!== '10005'&&this.userName!== '10006'&&this.userName!== '10007'">
        书院：{{ department }}<br/>
        系统内专业：{{ specialty }}
        <span v-if="this.splitFlow !== '不可变更专业'">
    <br/>招生录取专业：{{ major }}
  </span>
      </p>
      <p
        class="user-info2"
        v-if="
      userName !== 'admin' &&
    (specialty === '土木工程（国际班）' ||
     specialty === '金属材料工程（中日精英班）' ||
     specialty === '机械设计制造及其自动化(日语强化)' ||
     specialty === '机械设计制造及其自动化（卓越国合班）')
  "
      >
        (您目前所在专业为入学后选拔专业，根据学院、学校政策要求，您不再具有专业变更资格，请知悉。)
      </p>
      <p class="greeting-message">大工书院祝您心想事成！😊</p>
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
</template>

<script>
import {getNickName, getStudent} from "@/api/system/student";
import axios from "axios";

export default {
  name: "WelcomePage",
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
      isLoading: false
    };
  },
  computed: {
    userName() {
      return this.$store.state.user.name; // 获取用户名
    }
  },
  mounted() {
    this.initializeUserData();
  },
  methods: {
    async initializeUserData() {
      if (this.userName === 'admin') {
        this.studentName = '管理员'
      } else if(this.userName === '10001' ||
        this.userName === '10002'||
        this.userName === '10003'||
        this.userName === '10004'||
        this.userName === '10005'||
        this.userName === '10006'||
        this.userName === '10007'){
        const response = await getNickName();
        this.studentName = response.msg;
      }
      else{

        getStudent(this.userName).then(response => {
          const studentInfo = response.studentInfo;
          this.studentName = studentInfo.studentName;
          this.department = studentInfo.academy;
          this.splitFlow = studentInfo.divertForm;
          this.major = studentInfo.major;
          this.specialty = studentInfo.originalSystemMajor;
          if (studentInfo.innovationClass === 1) {
            this.specialClass = '是';
          } else {
            this.specialClass = '否';
          }
        });
      }
    },
    toggleChat() {
      this.chatVisible = !this.chatVisible
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
.welcome-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); /* 柔和的渐变背景 */
  padding: 20px;
}

.welcome-card {
  background: rgba(255, 255, 255, 0.95); /* 半透明白色背景 */
  padding: 3rem;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1),
  inset 0 0 15px rgba(255, 255, 255, 0.3); /* 内外阴影结合 */
  max-width: 800px;
  width: 100%;
  text-align: center;
  transition: transform 0.3s ease;
  backdrop-filter: blur(5px); /* 背景模糊效果 */
  border: 1px solid rgba(255, 255, 255, 0.3); /* 柔和边框 */
}

.welcome-card:hover {
  transform: translateY(-5px); /* 悬停微动效 */
}

.welcome-message {
  font-size: 2.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  position: relative;
  padding-bottom: 1rem;
}

.welcome-message::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #6a89cc 0%, #82ccdd 100%); /* 装饰性下划线 */
}

.user-info {
  font-size: 1.1rem;
  color: #4a5568;
  margin-bottom: 2rem;
  text-align: left;
  line-height: 1.8;
  background: rgba(241, 245, 249, 0.4); /* 浅色背景 */
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.user-info2 {
  font-size: 0.95rem;
  color: #2b6cb0;
  margin-bottom: 2rem;
  text-align: left;
  padding: 1rem;
  background: rgba(66, 153, 225, 0.08);
  border-left: 4px solid #4299e1; /* 左侧装饰条 */
  border-radius: 6px;
  animation: fadeIn 0.5s ease; /* 淡入动画 */
}

.greeting-message {
  font-size: 1.2rem;
  color: #48bb78;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  border-radius: 30px;
  background: rgba(72, 187, 120, 0.1); /* 浅绿色背景 */
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .welcome-card {
    padding: 2rem;
    margin: 1rem;
  }

  .welcome-message {
    font-size: 2rem;
  }

  .user-info {
    font-size: 1rem;
    padding: 1rem;
  }

  .greeting-message {
    font-size: 1.1rem;
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

@keyframes messageAppear {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  margin-left: auto;
}

.message.user .content {
  background: #409EFF;
  color: white;
  border-radius: 18px 18px 4px 18px;
}

.message.assistant .content {
  background: #ffffff;
  color: #2d3748;
  border-radius: 18px 18px 18px 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.content {
  padding: 14px 18px;
  font-size: 15px;
  line-height: 1.5;
  position: relative;
  transition: transform 0.2s ease;
}

.time {
  font-size: 12px;
  color: #718096;
  margin-top: 6px;
  text-align: right;
}

/* 输入区域美化 */
.input-area {
  padding: 20px;
  background: #fff;
  border-top: 1px solid #edf2f7;
}

.input-area .el-textarea__inner {
  border-radius: 12px;
  padding: 14px;
  font-size: 15px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.input-area .el-textarea__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.input-area .el-button {
  margin-top: 16px;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.loading {
  text-align: center;
  padding: 16px;
  color: #718096;
  font-size: 14px;
}

.loading i {
  margin-right: 8px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>

