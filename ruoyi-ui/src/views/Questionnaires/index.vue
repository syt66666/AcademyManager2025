<template>
  <div class="questionnaire-list">
    <div class="questionnaire-grid">
      <div
        v-for="questionnaire in questionnaires"
        :key="questionnaire.id"
        class="questionnaire-card"
      >
        <div class="card-content">
          <h3>{{ questionnaire.title }}</h3>
          <p>{{ questionnaire.description }}</p>
        </div>
        <button
          :disabled="questionnaire.completed"
          @click="goToQuestionnaire(questionnaire.id)"
          class="btn"
        >
          {{ questionnaire.completed ? '已完成' : '进入问卷' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import store from "../../store";

export default {
  data() {
    return {
      userName: store.state.user.name, // 获取用户名
      questionnaires: [
        { id: 1, title: '问卷1', description: '书院专业分流调查问卷', completed: false },
        { id: 2, title: '问卷2', description: '之后可添加问卷', completed: false }
      ]
    };
  },
  methods: {
    async checkQuestionnaireStatus() {
      console.log("开始检查问卷状态..."); // 测试日志
      try {
        for (const questionnaire of this.questionnaires) {
          console.log(`检查问卷 ${questionnaire.id} 的状态...`); // 测试日志
          const response = await axios.get('http://localhost:3000/api/check-questionnaire-completed', {
            params: {
              userName:  this.userName,
              questionnaireId: questionnaire.id
            }
          });
          questionnaire.completed = response.data.completed;
          console.log(
            `问卷 ${questionnaire.id} 的完成状态：${response.data.completed ? '已完成' : '未完成'}`
          );
        }
      } catch (error) {
        console.error('检查问卷状态失败:', error);
      }
    },
    goToQuestionnaire(questionnaireId) {
      console.log(`尝试进入问卷 ${questionnaireId} ...`); // 测试日志
      const questionnaire = this.questionnaires.find(q => q.id === questionnaireId);
      if (questionnaire.completed) {
        console.log(`问卷 ${questionnaireId} 已完成，无法进入。`); // 测试日志
      } else {
        console.log(`跳转到问卷 ${questionnaireId}`); // 测试日志
        this.$router.push({
          path: `/Questionnaires/Questionnaire${questionnaireId}`
        });
      }
    }
  },
  created() {
    // 页面加载时检查问卷完成状态
    console.log("页面加载，初始化检查问卷完成状态..."); // 测试日志
    this.checkQuestionnaireStatus();
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

.btn:disabled {
  background-color: #ccc; /* 灰色背景 */
  cursor: not-allowed;   /* 禁止光标 */
  color: #666;           /* 更浅的文本颜色 */
  transform: none;       /* 禁用悬停时的放大效果 */
}

</style>
