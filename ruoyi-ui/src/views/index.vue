<template>
  <component :is="homeComponent" />
</template>

<script>
import StudentHome from "./HomePage/StudentHome.vue";
import AcademicTeacherHome from "./HomePage/AcademicTeacherHome.vue";
import AdminTeacherHome from "./HomePage/AdminTeacherHome.vue";

export default {
  name: "HomePage",
  components: { StudentHome, AcademicTeacherHome, AdminTeacherHome },
  computed: {
    homeComponent() {
      const userName = this.$store.state.user.name;
      if (userName === '10000') {
        return 'AdminTeacherHome';
      }
      if (userName >= '10001' && userName <= '10007') {
        return 'AcademicTeacherHome';
      }

      if (userName >= '20001' && userName <= '20020') {
        return 'AcademicTeacherHome';
      }
      // 账号为10000以及学生账号默认显示学生首页
      return 'StudentHome';
    }
  }
};
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #f5f7fa;
  padding: 0;
  position: relative;
}

/* 欢迎页面样式 */
.welcome-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 84px);
  padding: 40px 20px;
}

.welcome-card {
  background: white;
  border-radius: 20px;
  padding: 60px 80px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid #e4e7ed;
  text-align: center;
  max-width: 600px;
  width: 100%;
  position: relative;
  overflow: hidden;
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409EFF, #67C23A, #E6A23C, #F56C6C);
}

.welcome-icon {
  margin-bottom: 30px;
}

.welcome-icon i {
  font-size: 80px;
  color: #409EFF;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-content {
  text-align: center;
}

.welcome-title {
  font-size: 36px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 20px 0;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 18px;
  color: #606266;
  margin: 0;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-card {
    padding: 40px 30px;
    margin: 20px;
  }

  .welcome-title {
    font-size: 28px;
  }

  .welcome-subtitle {
    font-size: 16px;
  }

  .welcome-icon i {
    font-size: 60px;
  }
}
</style>
