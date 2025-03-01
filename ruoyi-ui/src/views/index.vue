<template>
  <div class="welcome-container">
    <div class="welcome-card">
      <h1 class="welcome-message">您好，{{ studentName }}！</h1>
      <p class="user-info" v-if="this.userName !== 'admin'">
        书院：{{ department }}<br />
        系统内专业：{{ specialty }}
        <span v-if="this.splitFlow !== '不可变更专业'">
    <br />招生录取专业：{{ major }}
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
      <p class="greeting-message" >大工书院祝您心想事成！😊</p>
<!--      <button @click="submitForm">?????????</button>-->
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { addCompetitionRecord } from "@/api/student/competition";
import store from "@/store";
import {upLoad} from "@/api/student/letcure";

export default {
  name: "WelcomePage",
  data() {
    return {
      list:{

      },
      studentName: '',
      department: '',
      major: '',
      specialty: '',
      specialClass:'',
      splitFlow:'',
      formData: {
        competition_name: '',
        competition_level: '',
        award_level: '',
        scholarship_points: '',
        proof_material: '',
        //学期
        semester: '2',
      },
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
    submitForm(){
      this.$refs.form.validate((valid) => {
        if (valid) {
          console.log('表单数据:', this.formData.reportDate);
          const formData = new FormData();
          const json = JSON.stringify(this.formData);
          formData.append('studentLectureReport', json);
          formData.append('proofMaterial', this.proofMterial);
          // 在这里编写提交表单的逻辑，例如将表单数据发送到后端
          console.log('表单数据:', this.formData);
          console.log('传递后端数据:', formData);
          // 可以使用 axios 或 fetch 发送请求
          // 例如：
          addCompetitionRecord(formData).then(response => {
            console.log("+++++++++", response);
          })
            .catch(error => {
              console.error(error);
            });
        } else {
          this.$message.error('请填写完整表单信息');
        }
      })
    },
    async initializeUserData() {
      if (this.userName === 'admin') {
        this.studentName = '管理员'
      } else {

          const response = await axios.get(process.env.VUE_APP_BASE_API+`/system/student/${this.userName}`);
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
.user-info2 {
  font-size: 14px;
  color: #395cdc;
  margin-bottom: 30px;
  text-align: left;
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

  .user-info2 {
    font-size: 12px;  /* 调整移动端字体大小 */
  }

  .greeting-message {
    font-size: 18px;  /* 调整移动端字体大小 */
  }
}
</style>
