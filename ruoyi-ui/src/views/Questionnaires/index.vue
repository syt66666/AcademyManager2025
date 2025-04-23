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

    <!-- 承诺书弹窗 -->
    <div v-if="showDialog" class="dialog-overlay">
      <div class="promise-dialog">
        <div class="dialog-content">
          <h3>专业选择承诺书</h3>
          <div class="promise-text">
            <p>本人 {{ studentName }}（学号：{{ userName }}），已完整阅读并充分理解以下内容：</p>
            <ol>
              <li>本次专业选择为专业分流阶段性摸底调查；</li>
              <li>本次开放时间为{{ selectedStartTime }}至{{ selectedEndTime }}；</li>
              <li>请务必保证本人已充分了解专业分流相关政策后再进行专业选择；</li>
              <li>请务必保证此次操作为本人操作，不得他人代选；</li>
              <li>请务必保证此次选择完全遵照个人意愿；</li>
              <li>请务必慎重考虑、认真选择，一经提交不得更改。</li>
            </ol>
          </div>
          <div class="dialog-buttons">
            <button
              @click="confirmSubmit"
              :disabled="isCountdownActive"
              class="confirm-btn"
            >
              {{ isCountdownActive ? `确认签字（${countdown}秒）` : '确认签字' }}
            </button>
            <button @click="showDialog = false" class="cancel-btn">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import store from "../../store";
import {getQuestionnaireTimes} from "@/api/system/questionnaire";
import {getStudent} from "../../api/system/student";

export default {
  data() {
    return {
      userName: store.state.user.name,
      studentName: '',
      questionnaires: [
        {
          id: 1,
          title: '问卷1',
          description: '2024书院学生专业选择意向摸底调查',
          type: '专业分流阶段性摸底调查',
          completed: false,
          start_time: '2024-12-31T00:00:00',
          end_time: '2025-01-01T00:00:00'
        },
        {
          id: 2,
          title: '问卷2',
          description: '2024书院学生专业选择意向摸底调查',
          type: '正式专业分流',
          completed: false,
          start_time: '2025-02-01T00:00:00',
          end_time: '2026-01-01T00:00:00'
        }
      ],
      showDialog: false,
      selectedQuestionnaireId: null,
      selectedQuestionnaireType: "",
      selectedStartTime: "",
      selectedEndTime: "",
      // 倒计时相关状态
      countdown: 20,
      isCountdownActive: false,
      countdownTimer: null
    };
  },
  methods: {
    goToQuestionnaire(questionnaireId) {
      const questionnaire = this.questionnaires.find(q => q.id === questionnaireId);
      if (questionnaire.completed || !questionnaire.isInTimeRange) return;

      // 重置倒计时状态
      if (this.countdownTimer) clearInterval(this.countdownTimer);

      this.selectedQuestionnaireId = questionnaireId;
      this.selectedQuestionnaireType = questionnaire.type;
      this.selectedStartTime = this.formatDate(questionnaire.start_time);
      this.selectedEndTime = this.formatDate(questionnaire.end_time);
      this.showDialog = true;

      // 启动倒计时
      this.startCountdown();
    },
    startCountdown() {
      this.countdown = 20;
      this.isCountdownActive = true;
      this.countdownTimer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          this.isCountdownActive = false;
          clearInterval(this.countdownTimer);
        }
      }, 1000);
    },

    confirmSubmit() {
      if (this.isCountdownActive) return;
      this.showDialog = false;
      this.$router.push({
        path: `/Questionnaires/Questionnaire${this.selectedQuestionnaireId}`
      });
    },
    async checkQuestionnaireStatus() {
      try {
        for (const questionnaire of this.questionnaires) {
          const response = await axios.get(
            process.env.VUE_APP_BASE_API + '/system/questionnaire/checkCompleted',
            {
              params: {
                userName: this.userName,
                questionnaireId: questionnaire.id
              }
            }
          );
          questionnaire.completed = response.data;
        }
      } catch (error) {
        console.error('检查问卷状态失败:', error);
      }
    },

    async checkQuestionnaireTime() {
      for (const questionnaire of this.questionnaires) {
        try {
          const response = await getQuestionnaireTimes(questionnaire.id);
          if (response.data) {
            const startTime = new Date(response.data.startTime);
            const endTime = new Date(response.data.endTime);
            const now = new Date();

            questionnaire.type = response.data.type;
            questionnaire.start_time = startTime.toISOString();
            questionnaire.end_time = endTime.toISOString();
            questionnaire.isInTimeRange = now >= startTime && now <= endTime;
          }
        } catch (error) {
          console.error('获取问卷时间失败:', error);
          questionnaire.isInTimeRange = false;
        }
      }
    },


    getStuInfo() {
      getStudent(this.userName).then(response => {
        const studentInfo = response.studentInfo;
        this.studentName = studentInfo.studentName;
      });
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }).replace(/\//g, '-');
    }
  },
  created() {
    this.checkQuestionnaireStatus();
    this.checkQuestionnaireTime();
    this.getStuInfo();
  },
  watch: {
    showDialog(newVal) {
      if (!newVal) {
        clearInterval(this.countdownTimer);
        this.isCountdownActive = false;
        this.countdown = 20;
      }
    }
  },
  beforeDestroy() {
    clearInterval(this.countdownTimer);
  }
};
</script>

<style scoped>
/* 新增倒计时按钮样式 */
.confirm-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}
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
  margin-bottom: 0;
  display: inline-flex;
  justify-content: flex-start;
  font-size: 0.95rem;
  padding-right: 15px;
}

.time-field strong {
  color: #395cdc;
  margin-right: 5px;
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
  background-color: #ccc;
  cursor: not-allowed;
  color: #666;
  transform: none;
}

.btn:not(:disabled).not-allowed {
  background-color: #f5c6cb;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

/* 弹窗样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.promise-dialog {
  background: white;
  border-radius: 12px;
  padding: 30px;
  width: 600px;
  max-width: 90%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.dialog-content h3 {
  color: #395cdc;
  margin-bottom: 20px;
  font-size: 1.5rem;
}

.promise-text {
  text-align: left;
  margin-bottom: 25px;
}

.promise-text ol {
  padding-left: 20px;
}

.promise-text li {
  margin-bottom: 10px;
  line-height: 1.6;
}

.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 20px;
}

.confirm-btn, .cancel-btn {
  padding: 10px 25px;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.confirm-btn {
  background-color: #395cdc;
  color: white;
  border: none;
}

.confirm-btn:hover {
  background-color: #2b76de;
}

.cancel-btn {
  background-color: #f4f4f4;
  color: #666;
  border: 1px solid #ddd;
}

.cancel-btn:hover {
  background-color: #e9e9e9;
}
</style>
