<template>
  <div class="welcome-container">
    <div class="welcome-card">
      <h1 class="welcome-message">您好，{{ studentName }}！</h1>
      <p class="user-info" v-if="this.userName !== 'admin'">
        书院：{{ department }}<br />
        系统内专业：{{ specialty }}<br />
<!--        招生录取专业：{{ major }}<br />-->
<!--        是否为创新班/拔尖班：{{specialClass}}<br />-->
      </p>
      <p class="user-info" v-if="this.userName !== 'admin'&&this.splitFlow==='不可变更专业'">
        建国、材日、机日、机俄：您目前所在专业为入学后选拔专业，根据学院、学校政策要求，您不再具有专业变更资格，请知悉。
      </p>
      <p class="greeting-message">祝你今天有个愉快的一天！😊</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import store from "@/store";

export default {
  name: "WelcomePage",
  data() {
    return {
      studentName: '',
      department: '',
      major: '',
      specialty: '',
      specialClass:'',
      splitFlow:''
    };
  },
  computed: {
    userName() {
      return this.$store.state.user.name; // 获取用户名
    }
  },
  mounted() {
    console.log(this.userName)
    this.initializeUserData();
  },
  methods: {
    async initializeUserData() {
      if (this.userName === 'admin') {
        this.studentName = '管理员'
      } else {

          const response = await axios.get(process.env.VUE_APP_BASE_API+`/api/student/${this.userName}`);
          const studentInfo = response.data.studentInfo;
          console.log(studentInfo);
          this.studentName=studentInfo.studentName;
          this.department=studentInfo.academy;
          this.splitFlow = studentInfo.divertForm;
          this.major=studentInfo.major;
          this.specialty=studentInfo.systemMajor;
          if(studentInfo.innovationClass===1){
            this.specialClass='是';
          }
          else {
            this.specialClass='否';
          }


        //   if (response && response.data && response.data.studentInfo) {
        //     const studentInfo = response.data.studentInfo;
        //     console.log(studentInfo);
        //   } else {
        //     console.warn("No student info found.");
        //   }
        //
        // } catch (error) {
        //   console.error("Error fetching student info:", error);
        // }




        // axios
        //   .get(`http://localhost:3000/api/users/${this.userName}`)
        //   .then(response => {
        //     const userData = response.data;  // 获取用户数据
        //     this.splitFlow = userData.divertForm // 分流形式
        //     this.studentName = userData.studentName; // 姓名
        //     this.studentId = userData.studentId; // 学号
        //     this.major = userData.major; // 招生录取专业
        //     this.department = userData.academy; // 管理部门
        //     this.specialty = userData.systemMajor; // 系统内专业
        //     if(userData.InnovationClass===1){
        //       this.specialClass='是'
        //     }else{
        //       this.specialClass='否'
        //     }
        //   })
        //   .catch(error => {
        //     console.error("获取用户信息失败", error);
        //     this.$message.error("获取用户信息失败");
        //   });
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
  background-color: #f0f4f8;
  padding: 20px;
}

.welcome-card {
  background-color: #ffffff;
  padding: 50px;  /* 增加内边距让欢迎框更大 */
  border-radius: 15px;  /* 使圆角更大 */
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1); /* 增加阴影使其更立体 */
  max-width: 800px;  /* 设置最大宽度 */
  width: 100%;
  text-align: center;
}

.welcome-message {
  font-size: 30px;  /* 增加欢迎信息的字体大小 */
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;  /* 增加下边距 */
}

.user-info {
  font-size: 18px;  /* 增加字体大小 */
  color: #555;
  margin-bottom: 30px;
  text-align: left;
}

.greeting-message {
  font-size: 20px;  /* 增加问候信息的字体大小 */
  color: #28a745;
  font-weight: bold;
}

@media (max-width: 768px) {
  .welcome-card {
    padding: 30px;  /* 减少移动端的内边距 */
  }

  .welcome-message {
    font-size: 24px;  /* 在小屏幕上减小欢迎信息字体大小 */
  }

  .user-info {
    font-size: 16px;  /* 调整移动端字体大小 */
  }

  .greeting-message {
    font-size: 18px;  /* 调整移动端字体大小 */
  }
}
</style>
