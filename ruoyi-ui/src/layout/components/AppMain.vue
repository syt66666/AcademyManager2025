<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view v-if="!$route.meta.link" :key="key" />
      </keep-alive>
    </transition>
    <div class="welcome-card">
      <p class="welcome-message">你好，{{ studentName }}！</p>
      <div class="user-info">
        <p>书院：{{ department }}</p>
        <p>系统内专业：{{ major }}</p>
        <p>招生录取专业：{{ specialty }}</p>
        <p>分流形式：{{ splitFlow }}</p>
      </div>
      <iframe-toggle />
    </div>
  </section>
</template>

<script>
import iframeToggle from "./IframeToggle/index";
import axios from "axios";
import store from "@/store";

export default {
  name: 'AppMain',
  components: { iframeToggle },
  data() {
    return {
      splitFlow: '',
      studentName: '',
      studentId: '',
      major: '',
      department: '',
      specialty: ''
    };
  },
  computed: {
    userName() {
      return this.$store.state.user.name; // 响应式的用户名
    },
    cachedViews() {
      return this.$store.state.tagsView.cachedViews;
    },
    key() {
      return this.$route.path;
    }
  },
  watch: {
    $route() {
      this.addIframe();
    }
  },
  mounted() {
    this.addIframe();
    this.initializeQuestionnaire(); // 初始化问卷数据
  },
  methods: {
    addIframe() {
      const { name } = this.$route;
      if (name && this.$route.meta.link && !this.$store.state.tagsView.iframeViews.includes(name)) {
        this.$store.dispatch('tagsView/addIframeView', this.$route);
      }
    },
    initializeQuestionnaire() {
      axios
        .get(`http://localhost:3000/api/users/${this.userName}`)
        .then(response => {
          const userData = response.data;
          console.log(userData);

          this.splitFlow = userData.分流形式;
          this.studentName = userData.姓名;
          this.studentId = userData.学号;
          this.major = userData.招生录取专业;
          this.department = userData.管理部门;
          this.specialty = userData.系统内专业;
        })
        .catch(error => {
          console.error('获取用户信息失败', error);
          this.$message.error('获取用户信息失败');
        });
    },
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow: hidden;
  background-color: #f4f7fc;  // 背景色
}

.fixed-header + .app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    min-height: calc(100vh - 84px);
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }
}

.welcome-card {
  max-width: 800px;
  margin: 40px auto;
  padding: 30px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.welcome-message {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.user-info {
  text-align: left;
  font-size: 16px;
  color: #555;
  line-height: 1.6;
}

.user-info p {
  margin: 8px 0;
}

@media (max-width: 768px) {
  .welcome-card {
    padding: 20px;
  }

  .welcome-message {
    font-size: 20px;
  }

  .user-info {
    font-size: 14px;
  }
}
</style>
