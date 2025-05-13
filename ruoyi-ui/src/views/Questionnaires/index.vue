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
        <!-- 顶部装饰条 -->
        <div class="dialog-header-bar"></div>
        <div class="dialog-content">
          <!-- 标题区域 -->
          <div class="dialog-header">
            <h3>专业选择承诺书</h3>
          </div>

          <!-- 主要内容区域 -->
          <div class="promise-content">
            <!-- 用户信息卡片 -->
            <div class="user-info-card">
              <div class="info-item">
                <span class="info-label">承诺人：</span>
                <span class="info-value">{{ studentName }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">学 号：</span>
                <span class="info-value">{{ userName }}</span>
              </div>
            </div>

            <!-- 承诺条款 -->
            <div class="clause-container">
              <p class="clause-preamble">本人已完整阅读并充分理解以下条款内容：</p>
              <ol class="clause-list">
                <li class="clause-item">
                  <div class="clause-content">
                    <span class="clause-number">1</span>
                    本次专业选择为专业分流阶段性摸底调查；
                  </div>
                </li>
                <li class="clause-item">
                  <div class="clause-content">
                    <span class="clause-number">2</span>
                    本次开放时间为{{ selectedStartTime }}至{{ selectedEndTime }}，请务必在此时间段内进行专业选择；
                  </div>
                </li>
                <li class="clause-item">
                  <div class="clause-content">
                    <span class="clause-number">3</span>
                    请务必保证本人已充分了解专业分流相关政策后再进行专业选择；
                  </div>
                </li>
                <li class="clause-item">
                  <div class="clause-content">
                    <span class="clause-number">4</span>
                    请务必保证此次操作为本人操作，不得他人代选，否则后果自负；
                  </div>
                </li>
                <li class="clause-item">
                  <div class="clause-content">
                    <span class="clause-number">5</span>
                    请务必保证此次选择完全遵照个人意愿；
                  </div>
                </li>
                <li class="clause-item">
                  <div class="clause-content">
                    <span class="clause-number">6</span>
                    请务必慎重考虑、认真选择，一经提交不得更改。
                  </div>
                </li>
              </ol>
            </div>

            <!-- 签名上传区域 -->
            <div class="signature-upload">
              <div class="upload-header">
                <span class="upload-title">请上传手写电子签名</span>
              </div>
              <ImageUpload
                :limit="1"
                :file-size="10"
                :file-type="['png', 'jpg', 'jpeg']"
                v-model="img"
                class="signature-uploader"
              >
                <template #default>
                  <div class="uploader-guide">
                    <i class="icon-upload"></i>
                    <p>点击上传签字文件</p>
                  </div>
                </template>
              </ImageUpload>
            </div>
          </div>

          <!-- 操作按钮组 -->
          <div class="dialog-actions">
            <button
              @click="confirmSubmit"
              :disabled="isCountdownActive || !hasUploadedImage"
              class="confirm-btn"
            >
              <span class="btn-text">{{ getButtonText }}</span>
              <span v-if="isCountdownActive" class="countdown-circle"></span>
            </button>
            <button @click="showDialog = false" class="cancel-btn">
              <span class="btn-text">取消</span>
            </button>
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
import ImageUpload from "@/components/ImageUpload";

export default {
  components: {
    ImageUpload
  },
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
          type: '专业分流阶段性摸底调查',
          completed: false,
          start_time: '2025-04-24T00:00:00',
          end_time: '2025-04-26T16:00:00'
        },
        {
          id: 3,
          title: '问卷3',
          description: '2024届书院学生正式专业分流',
          type: '正式专业分流',
          completed: false,
          start_time: '2025-05-1T00:00:00',
          end_time: '2025-05-20T16:00:00'
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
      countdownTimer: null,
      img: []
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
      if (this.isCountdownActive || !this.hasUploadedImage) return;
      // 提交前的最后验证
      if (!this.validateSignature()) {
        alert('请上传有效的签字图片！');
        return;
      }
      this.showDialog = false;
      this.$router.push({
        path: `/Questionnaires/Questionnaire${this.selectedQuestionnaireId}`
      });
    },
    validateSignature() {
      // 这里可以添加更复杂的图片验证逻辑
      return this.hasUploadedImage;
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
  },
  computed: {
    hasUploadedImage() {
      return this.img.length > 0; // 检测是否有上传的图片
    },
    getButtonText() {
      if (this.isCountdownActive) {
        return `确认（${this.countdown}秒）`;
      }
      return this.hasUploadedImage ? '确认' : '请上传签字图片';
    }
  },
};
</script>

<style scoped>

/* 遮罩层 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(3px);
}

/* 弹窗容器 */
.promise-dialog {
  background: #ffffff;
  border-radius: 12px;
  width: 640px;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

/* 顶部装饰条 */
.dialog-header-bar {
  height: 4px;
  background: linear-gradient(135deg, #395cdc 0%, #2b76de 100%);
}

/* 内容区域 */
.dialog-content {
  padding: 28px 32px;
}

/* 标题样式 */
.dialog-header {
  text-align: center;
  margin-bottom: 24px;
}
.dialog-header h3 {
  font-size: 24px;
  color: #1a2b6d;
  font-weight: 600;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

/* 用户信息卡片 */
.user-info-card {
  background: #f8f9ff;
  border: 1px solid #e6ebff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}
.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 15px;
}
.info-item:last-child {
  margin-bottom: 0;
}
.info-label {
  color: #666;
  min-width: 60px;
}
.info-value {
  color: #333;
  font-weight: 500;
}

/* 承诺条款 */
.clause-container {
  margin-bottom: 28px;
}
.clause-preamble {
  color: #666;
  font-size: 14px;
  margin-bottom: 16px;
  line-height: 1.6;
  text-align: left;
}
.clause-list {
  padding-left: 0;
  list-style: none;
}
.clause-item {
  margin-bottom: 16px;
  position: relative;
  padding-left: 32px;
}
.clause-number {
  position: absolute;
  left: 0;
  top: 2px;
  width: 20px;
  height: 20px;
  background: #395cdc;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}
.clause-content {
  color: #444;
  line-height: 1.7;
  font-size: 14px;
}

/* 签名上传 */
.signature-upload {
  margin-top: 24px;
}
.upload-header {
  margin-bottom: 12px;
}
.upload-title {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}
.signature-uploader {
  border: 2px dashed #e0e4f5;
  border-radius: 8px;
  padding: 24px;
  transition: all 0.3s ease;
}
.signature-uploader:hover {
  border-color: #395cdc;
  background: #f8faff;
}
.uploader-guide {
  text-align: center;
  color: #888;
}
.icon-upload::before {
  content: "↑";
  display: block;
  font-size: 32px;
  margin: 0 auto 8px;
  color: #395cdc;
}
.uploader-guide p {
  margin: 0;
  font-size: 14px;
}

/* 操作按钮 */
.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
}
.confirm-btn, .cancel-btn {
  padding: 12px 32px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}
.confirm-btn {
  background: #395cdc;
  color: white;
  border: none;
}
.confirm-btn:not(:disabled):hover {
  background: #2b76de;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(43, 118, 222, 0.3);
}
.cancel-btn {
  background: #f0f2f7;
  color: #666;
  border: 1px solid #ddd;
}
.cancel-btn:hover {
  background: #e4e6eb;
}

/* 禁用状态 */
.confirm-btn:disabled {
  background: #c5cdf0;
  cursor: not-allowed;
  opacity: 0.8;
}

/* 倒计时动画 */
.countdown-circle {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-left: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式调整 */
@media (max-width: 640px) {
  .promise-dialog {
    width: 90%;
    margin: 0 16px;
  }
  .dialog-content {
    padding: 24px;
  }
  .dialog-actions {
    flex-direction: column;
  }
  .confirm-btn, .cancel-btn {
    width: 100%;
  }
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
/* 移动端专属适配 - 所有修改都包裹在媒体查询中 */
@media screen and (max-width: 768px) {
  /* 问卷列表适配 */
  .questionnaire-list {
    padding: 20px 10px !important;
    margin-left: 0 !important;
  }

  .questionnaire-grid {
    grid-template-columns: 1fr !important;
    gap: 20px !important;
    padding: 0 10px !important;
  }

  .questionnaire-card {
    padding: 15px !important;
    margin: 0 10px !important;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
  }

  .card-content h3 {
    font-size: 1.4rem !important;
    line-height: 1.3;
  }

  .card-content p {
    font-size: 0.9rem !important;
    margin: 8px 0 !important;
  }

  .time-field {
    display: block !important;
    margin-bottom: 8px !important;
    font-size: 0.85rem !important;
  }

  .btn {
    padding: 12px 20px !important;
    font-size: 0.95rem !important;
    width: 100% !important;
  }

  /* 承诺书弹窗适配 */
  .promise-dialog {
    width: 95% !important;
    border-radius: 8px !important;
  }

  .dialog-content {
    padding: 15px !important;
  }

  .dialog-header h3 {
    font-size: 1.3rem !important;
    margin: 12px 0 !important;
  }

  .user-info-card {
    padding: 12px !important;
    margin-bottom: 15px !important;
  }

  .info-item {
    font-size: 0.9rem !important;
  }

  .clause-item {
    padding-left: 26px !important;
  }

  .clause-number {
    width: 18px !important;
    height: 18px !important;
    font-size: 11px !important;
  }

  .clause-content {
    font-size: 0.85rem !important;
    line-height: 1.5;
  }

  .signature-uploader {
    padding: 15px !important;
  }

  .dialog-actions {
    flex-direction: column !important;
    gap: 12px !important;
  }

  .confirm-btn,
  .cancel-btn {
    width: 100% !important;
    padding: 14px !important;
  }
}

/* 超小屏幕优化 */
@media screen and (max-width: 480px) {
  .clause-item {
    padding-left: 24px !important;
  }

  .clause-number {
    width: 16px !important;
    height: 16px !important;
  }

  .dialog-header h3 {
    font-size: 1.2rem !important;
  }

  .info-item {
    font-size: 0.85rem !important;
  }

  .time-field strong {
    min-width: 68px !important;
  }
}
.clause-content {
  /* 确保容器使用标准盒模型 */
  display: block;
  /* 设置足够左内边距避开编号 */
  padding-left: 32px;
  /* 强制文本左对齐 */
  text-align: left;
  /* 保持原有样式 */
  color: #444;
  line-height: 1.7;
  font-size: 14px;
}

.clause-number {
  /* 绝对定位确保不影响文字流 */
  position: absolute;
  left: 0;
  /* 垂直居中调整 */
  top: 50%;
  transform: translateY(-50%);
  /* 原有编号样式 */
  width: 20px;
  height: 20px;
  background: #395cdc;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}
</style>
