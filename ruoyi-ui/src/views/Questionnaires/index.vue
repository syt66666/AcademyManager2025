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
          <!-- 显示问卷的开始和结束时间 -->
          <div class="questionnaire-time">
            <div class="time-field">
              <strong>开始时间：</strong>
              <span>{{ formatDate(questionnaire.start_time) }}</span>
            </div>
            <div class="time-field">
              <strong>结束时间：</strong>
              <span>{{ formatDate(questionnaire.end_time) }}</span>
            </div>
          </div>
        </div>
        <button
          :class="{'not-allowed': !questionnaire.isInTimeRange && !questionnaire.completed}"
          :disabled="questionnaire.completed || !questionnaire.isInTimeRange"
          @click="goToQuestionnaire(questionnaire.id)"
          class="btn"
        >
          {{
            questionnaire.completed
              ? '已完成'
              : (!questionnaire.isInTimeRange ? '不可作答' : '进入问卷')
          }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import store from "../../store";
import {getQuestionnaireTimes} from "@/api/system/questionnaire";

export default {
  data() {
    return {
      userName: store.state.user.name, // 获取用户名

      questionnaires: [
        { id: 1, title: '问卷1', description: '2024书院学生专业选择意向摸底调查', completed: false, start_time: '2024-12-31T00:00:00', end_time: '2025-01-01T00:00:00' },
        { id: 2, title: '问卷2', description: '2024书院学生专业选择意向摸底调查', completed: false, start_time: '2025-02-01T00:00:00', end_time: '2026-01-01T00:00:00' }
      ]
    };
  },
  methods: {
    // 查询问卷是否被该学生回答过
    async checkQuestionnaireStatus() {
      try {
        for (const questionnaire of this.questionnaires) {
          const response = await axios.get(process.env.VUE_APP_BASE_API+'/system/questionnaire/checkCompleted', {
            params: {
              userName: this.userName,
              questionnaireId: questionnaire.id
            }
          });
          questionnaire.completed = response.data;
          console.log(
            `问卷 ${questionnaire.id} 的完成状态：${response.data.success ? '已完成' : '未完成'}`
          );
        }
      } catch (error) {
        console.error('检查问卷状态失败:', error);
      }
    },
    async checkQuestionnaireTime() {
      for (const questionnaire of this.questionnaires) {
        try {
          // 使用新的接口函数
          const response = await getQuestionnaireTimes(questionnaire.id);

          // 根据接口返回数据结构调整
          if (response.data) {
            const startTime = new Date(response.data.startTime);
            const endTime = new Date(response.data.endTime);
            const now = new Date();

            // 时间有效性校验
            const isValidTime = (date) => !isNaN(date.getTime());

            // 赋值时间字段
            questionnaire.start_time = isValidTime(startTime) ? startTime.toISOString() : 'Invalid Date';
            questionnaire.end_time = isValidTime(endTime) ? endTime.toISOString() : 'Invalid Date';

            // 状态判断
            questionnaire.isInTimeRange = isValidTime(startTime) && isValidTime(endTime)
              && (now >= startTime)
              && (now <= endTime);
          } else {
            console.error('接口返回数据异常:', response);
          }
        } catch (error) {
          console.error('获取问卷时间失败:', {
            questionnaireId: questionnaire.id,
            error: error.response?.data || error.message
          });
          // 设置默认值
          questionnaire.start_time = 'Error';
          questionnaire.end_time = 'Error';
          questionnaire.isInTimeRange = false;
        }
      }
    },

    goToQuestionnaire(questionnaireId) {
      const questionnaire = this.questionnaires.find(q => q.id === questionnaireId);
      if (questionnaire.completed || !questionnaire.isInTimeRange) {
        console.log(`无法进入问卷 ${questionnaireId}`); // 测试日志
      } else {

        this.$router.push({ path: `/Questionnaires/Questionnaire${questionnaireId}` });
      }
    },
    // 格式化日期为更友好的显示方式
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString(); // 格式化为本地日期时间字符串
    }
  },
  created() {
    // 页面加载时检查问卷完成状态
    this.checkQuestionnaireStatus();//查询问卷是否被该学生回答过
    this.checkQuestionnaireTime();//查询该问卷是否在可回答时间内
  }

};
</script>

<style scoped>
.questionnaire-list {
  padding: 40px 20px;
  text-align: center;
  margin-left: 100px;
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

.questionnaire-time {
  margin-top: 15px;
  font-size: 1rem;
  color: #666;
}

.time-field {
  margin-bottom: 0; /* Remove margin between the fields */
  display: inline-flex; /* Align items horizontally */
  justify-content: flex-start;
  font-size: 0.95rem;
  padding-right: 15px; /* Add spacing between the fields */
}

.time-field strong {
  color: #395cdc; /* 强调标签 */
  margin-right: 5px; /* Add a small space between label and date */
}

.time-field span {
  color: #555;
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
  cursor: not-allowed; /* 禁止光标 */
  color: #666; /* 更浅的文本颜色 */
  transform: none; /* 禁用悬停时的放大效果 */
}

/* 不可作答按钮的样式 */
.btn:not(:disabled).not-allowed {
  background-color: #f5c6cb; /* 浅红色背景 */
  color: #721c24; /* 深红色字体 */
  border: 1px solid #f5c6cb;
  cursor: not-allowed; /* 禁止光标 */
  box-shadow: none;
}

.btn:not(:disabled).not-allowed:hover {
  background-color: #f8d7da; /* 更浅的红色 */
}

</style>
