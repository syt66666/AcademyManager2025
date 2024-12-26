<template>
  <div class="questionnaire-list">
    <div class="questionnaire-grid">
      <div v-for="questionnaire in questionnaires" :key="questionnaire.id" class="questionnaire-card">
        <div class="card-content">
          <h3>{{ questionnaire.title }}</h3>
          <p>{{ questionnaire.description }}</p>
        </div>
        <button @click="goToQuestionnaire(questionnaire.id)" class="btn">进入问卷</button>
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
        {id: 1, title: '问卷1', description: '仅可转专业'},
        {id: 2, title: '问卷2', description: '可类内任选，并转专业'},
        {id: 3, title: '问卷3', description: '可类内任选，不能转专业'},
        {id: 4, title: '问卷4', description: '可域内任选，并转专业'}
      ]
    };
  },
  methods: {
    goToQuestionnaire(id) {
      // 根据 id 跳转到相应的问卷页面
      if (id === 1) {
        this.$router.push('/questionnaire1');
      } else if (id === 2) {
        this.$router.push('/questionnaire2');
      } else if (id === 3) {
        this.$router.push('/questionnaire3');
      } else if (id === 4) {
        this.$router.push('/questionnaire4');
      }
    }
  }
};
</script>

<style scoped>
.questionnaire-list {
  padding: 40px 20px;
  text-align: center;
}

.title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #395cdc;
  margin-bottom: 30px;
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
