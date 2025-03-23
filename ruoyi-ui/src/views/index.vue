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
    (specialty === '土木工程(国际班)' ||
     specialty === '金属材料工程（中日精英班）' ||
     specialty === '机械设计制造及其自动化(日语强化)' ||
     specialty === '机械设计制造及其自动化（卓越国合班）')
  "
      >
        (您目前所在专业为入学后选拔专业，根据学院、学校政策要求，您不再具有专业变更资格，请知悉。)
      </p>
      <p class="greeting-message">大工书院祝您心想事成！😊</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import store from "@/store";
import {getNickName} from "@/api/system/student";

export default {
  name: "WelcomePage",
  data() {
    return {
      list: {},
      studentName: '',
      department: '',
      major: '',
      specialty: '',
      specialClass: '',
      splitFlow: '',
      nickName:'',
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
        const response = await axios.get(process.env.VUE_APP_BASE_API + `/system/student/${this.userName}`);
        const studentInfo = response.data.studentInfo;
        this.studentName = studentInfo.studentName;
        this.department = studentInfo.academy;
        this.splitFlow = studentInfo.divertForm;
        this.major = studentInfo.major;
        this.specialty = studentInfo.systemMajor;
        if (studentInfo.innovationClass === 1) {
          this.specialClass = '是';
        } else {
          this.specialClass = '否';
        }
      }
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
</style>
