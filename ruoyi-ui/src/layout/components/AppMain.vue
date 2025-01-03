<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view v-if="!$route.meta.link" :key="key" />
      </keep-alive>
    </transition>
      <iframe-toggle />
  </section>
</template>

<script>
import iframeToggle from "./IframeToggle/index";


export default {
  name: 'AppMain',
  components: { iframeToggle },
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
