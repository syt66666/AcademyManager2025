<template>
  <div class="questionnaire-list">
    <div class="questionnaire-grid">
      <div class="questionnaire-card">
        <div class="card-content">
          <h3>问卷1</h3>
          <p>书院专业分流调查问卷</p >
        </div>
        <button @click="goToQuestionnaire1()" class="btn">进入问卷</button>
      </div>
      <div class="questionnaire-card">
        <div class="card-content">
          <h3>问卷2</h3>
          <p>之后可添加问卷</p >
        </div>
        <button @click="goToQuestionnaire2()" class="btn">进入问卷</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {

      // 模拟问卷数据
      questionnaires: [
        {id: 1, title: '问卷1', description: '书院专业分流调查问卷'},
        {id: 2, title: '问卷2', description: '之后可添加问卷'},
      ]
    };
  },
  methods: {
    goToQuestionnaire1() {
      // 动态跳转到对应问卷页面
      this.$router.push({
        path: `/Questionnaires/Questionnaire1`,
        // 可根据需要添加其他参数，此处暂未添加
      });
    }
  }
};
</script>

<style scoped>
.questionnaire-list {
  padding: 40px 20px;
  text-align: center;
}


.questionnaire-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.questionnaire-card {
  background: white;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  padding: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.questionnaire-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.card-content h3 {
  font-size: 1.8rem;
  color: #395cdc;
}

.card-content p {
  font-size: 1rem;
  color: #666;
  margin-top: 10px;
}

.btn {
  background-color: #395cdc;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 30px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
  margin-top: 20px;
}

.btn:hover {
  background-color: #2b76de;
  transform: scale(1.05);
}
</style>
