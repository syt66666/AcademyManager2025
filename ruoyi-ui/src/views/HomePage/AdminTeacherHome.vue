<template>
  <div class="app-container">
    <!-- 仪表盘首页 -->
    <Dashboard />
        </div>
</template>

<script>
import Dashboard from './Dashboard.vue'

export default {
  name: 'AdminTeacherHome',
  components: {
    Dashboard
  }
}
</script>

<style scoped>
.app-container {
  background: #f8fafc;
  min-height: 100vh;
  width: calc(100% - 100px);
  box-sizing: border-box;
  overflow-x: hidden;
  margin-left: 100px;
  padding: 0;
}
</style>
