<template>
  <div class="major-diversion-container">
    <Qstatistics />
  </div>
</template>

<script>
import Qstatistics from '@/views/Qstatistics/index.vue'

export default {
  name: 'MajorDiversion',
  components: {
    Qstatistics
  }
}
</script>

<style scoped>
.major-diversion-container {
  margin-left: 100px;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
  width: 100%;
}
</style>

