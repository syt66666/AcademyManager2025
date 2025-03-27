<template>
  <div class="test-container">
    <h2>双数据雷达图测试</h2>

    <div class="controls">
      <div>
        <label>图表标题：
          <input v-model="title" class="title-input">
        </label>
      </div>
    </div>

    <enhanced-radar-chart
      :data="bothData"
      :indicators="indicators"
      :title="title"
      style="height: 500px; margin-top: 20px;"
    />

    <div class="data-info">
      <h3>当前展示数据：</h3>
      <div class="data-group">
        <div v-for="(item, index) in bothData" :key="index" class="data-item">
          <span class="data-label">{{ item.name }}：</span>
          <span class="data-values">{{ item.value.join(', ') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EnhancedRadarChart from '@/components/RadarChart/index.vue';

export default {
  components: {
    EnhancedRadarChart
  },
  data() {
    return {
      title: '双数据对比分析',
      indicators: [
        { name: '技术能力', max: 100 },
        { name: '沟通能力', max: 100 },
        { name: '执行效率', max: 100 },
        { name: '创新指数', max: 100 },
        { name: '行业经验', max: 100 }
      ],
      bothData: [
        {
          name: '前端团队',
          value: [85, 78, 92, 80, 88]
        },
        {
          name: '后端团队',
          value: [75, 85, 80, 72, 90]
        }
      ]
    }
  }
}
</script>

<style scoped>
.test-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.controls {
  margin: 20px 0;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.title-input {
  margin-left: 10px;
  padding: 8px;
  width: 300px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.data-info {
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;

  h3 {
    margin-top: 0;
    color: #333;
  }
}

.data-group {
  display: grid;
  gap: 15px;
}

.data-item {
  padding: 12px;
  background: white;
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.data-label {
  font-weight: 500;
  color: #646cff;
}

.data-values {
  color: #666;
  font-family: monospace;
}
</style>
