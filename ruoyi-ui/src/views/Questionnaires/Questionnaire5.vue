<template>
  <div class="questionnaire-container">
    <!-- 问卷标题 -->
    <div class="questionnaire-header">
      <h1 class="questionnaire-title">大工书院育人导师工作评价问卷</h1>
      <p class="questionnaire-subtitle">（辅导员/执行院长版）</p>
    </div>

    <!-- 问卷内容 -->
    <div class="questionnaire-content">
      <!-- 问题列表 -->
      <div v-for="(question, index) in questions" :key="question.id" class="question-block">
        <div class="question-header">
          <span class="question-number">{{ index + 1 }}</span>
          <div class="question-text-wrapper">
            <h3 class="question-category" v-if="question.category">{{ question.category }}</h3>
            <p class="question-text">{{ question.text }}</p>
          </div>
        </div>
        
        <div class="options-list">
          <div 
            v-for="option in question.options" 
            :key="option.value"
            class="option-item"
            :class="{ 'selected': selectedAnswers[index] === option.value }"
            @click="selectOption(index, option.value)"
          >
            <div class="option-radio">
              <span class="radio-dot" v-if="selectedAnswers[index] === option.value"></span>
            </div>
            <div class="option-content">
              <span class="option-label">{{ option.label }}</span>
              <span class="option-text">{{ option.text }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 进度条 -->
    <div class="progress-bar">
      <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
    </div>
    <p class="progress-text">已完成 {{ answeredCount }}/{{ questions.length }} 题 ({{ progressPercentage }}%)</p>

    <!-- 提交按钮 -->
    <div class="submit-section">
      <button 
        class="submit-btn"
        :class="{ 'disabled': !isAllAnswered }"
        :disabled="!isAllAnswered"
        @click="showConfirmDialog"
      >
        <span v-if="isAllAnswered">提交问卷</span>
        <span v-else>请完成所有题目后提交 ({{ answeredCount }}/{{ questions.length }})</span>
      </button>
    </div>

    <!-- 确认弹窗 -->
    <div v-if="showConfirmDialogFlag" class="dialog-overlay">
      <div class="dialog-box">
        <div class="dialog-header">
          <h3>确认提交</h3>
        </div>
        <div class="dialog-content">
          <p class="confirm-text">您已完成全部 {{ questions.length }} 道题目</p>
          <p class="warning-text">提交后将无法修改，确认提交吗？</p>
        </div>
        <div class="dialog-actions">
          <button @click="handleCancel" class="cancel-button">再检查一下</button>
          <button @click="submitQuestionnaire" class="confirm-button">确认提交</button>
        </div>
      </div>
    </div>

    <!-- 提交成功弹窗 -->
    <div v-if="showEndMessage" class="dialog-overlay">
      <div class="dialog-box success-dialog">
        <div class="success-icon">
          <svg viewBox="0 0 52 52">
            <circle cx="26" cy="26" r="25" fill="none"/>
            <path fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
          </svg>
        </div>
        <h3 class="success-title">提交成功！</h3>
        <p class="success-text">感谢您的评价，您的反馈对我们非常重要</p>
        <p class="success-score">本次评分：{{ totalScore }} 分</p>
        <button @click="goBack" class="back-button">返回问卷列表</button>
      </div>
    </div>
  </div>
</template>

<script>
import store from "@/store";
import { addScore } from "@/api/system/questionnaire";

export default {
  name: "Questionnaire5",
  data() {
    return {
      userName: store.state.user.name,
      selectedAnswers: [], // 存储用户选择的答案（1-5分）
      showConfirmDialogFlag: false,
      showEndMessage: false,
      signature: '', // 签名数据（从路由参数获取）
      questions: [
        {
          id: 1,
          category: '一、基本履职与师德师风',
          text: '该导师在政治素养、职业操守、教书育人、为人师表等方面的表现如何？',
          options: [
            { value: 1, label: 'A', text: '存在不足' },
            { value: 2, label: 'B', text: '表现一般，需提升' },
            { value: 3, label: 'C', text: '符合基本要求' },
            { value: 4, label: 'D', text: '表现良好' },
            { value: 5, label: 'E', text: '师德典范' }
          ]
        },
        {
          id: 2,
          text: '该导师在完成指导学生发展、定期见面、学期初计划与期末小结等基础职责方面的表现如何？',
          options: [
            { value: 1, label: 'A', text: '未能有效履行' },
            { value: 2, label: 'B', text: '履行情况不佳，时有缺漏' },
            { value: 3, label: 'C', text: '基本履行' },
            { value: 4, label: 'D', text: '较好履行，记录完整' },
            { value: 5, label: 'E', text: '超额、主动且高质量完成' }
          ]
        },
        {
          id: 3,
          category: '二、育人过程与书院参与',
          text: '您认为该导师对学生的指导是否深入且系统，能否有效回应学生成长需求？',
          options: [
            { value: 1, label: 'A', text: '流于形式，缺乏深度' },
            { value: 2, label: 'B', text: '深度不够，系统性差' },
            { value: 3, label: 'C', text: '达到基本预期' },
            { value: 4, label: 'D', text: '过程扎实，有一定系统性，效果较好' },
            { value: 5, label: 'E', text: '非常系统、深入，能精准对接学生需求' }
          ]
        },
        {
          id: 4,
          text: '该导师在学生价值塑造、思想引导、学术诚信教育方面的工作是否扎实有效？',
          options: [
            { value: 1, label: 'A', text: '未见成效' },
            { value: 2, label: 'B', text: '成效不显，方法单一' },
            { value: 3, label: 'C', text: '有一定成效' },
            { value: 4, label: 'D', text: '成效良好，方法得当' },
            { value: 5, label: 'E', text: '成效显著，有可推广的典型案例' }
          ]
        },
        {
          id: 5,
          text: '该导师在促进学生学业进步、专业认知深化和学术兴趣培养方面的成效如何？',
          options: [
            { value: 1, label: 'A', text: '无明显作用' },
            { value: 2, label: 'B', text: '作用有限' },
            { value: 3, label: 'C', text: '达到基本效果' },
            { value: 4, label: 'D', text: '效果较为明显' },
            { value: 5, label: 'E', text: '效果突出，学生反馈积极' }
          ]
        },
        {
          id: 6,
          text: '该导师在参与书院课程建设、活动组织、公共服务及资源引入等方面的积极性与贡献如何？',
          options: [
            { value: 1, label: 'A', text: '消极，未参与或极少参与' },
            { value: 2, label: 'B', text: '被动参与，贡献度低' },
            { value: 3, label: 'C', text: '参与1-2项，达到基本要求' },
            { value: 4, label: 'D', text: '主动参与多项，贡献较大' },
            { value: 5, label: 'E', text: '深度参与并发挥核心作用，积极引入资源' }
          ]
        },
        {
          id: 7,
          text: '该导师在遵守书院管理制度、按时提交材料、配合书院工作安排等方面的表现如何？',
          options: [
            { value: 1, label: 'A', text: '配合度差，规范性不足' },
            { value: 2, label: 'B', text: '配合度与规范性一般' },
            { value: 3, label: 'C', text: '基本符合要求' },
            { value: 4, label: 'D', text: '较好配合，工作规范' },
            { value: 5, label: 'E', text: '主动配合，极为规范可靠' }
          ]
        },
        {
          id: 8,
          category: '三、育人成效与协同合作',
          text: '根据您的观察，该导师所指导的学生在综合素质、创新能力、精神面貌等方面是否有明显进步？',
          options: [
            { value: 1, label: 'A', text: '进步不明显' },
            { value: 2, label: 'B', text: '略有进步' },
            { value: 3, label: 'C', text: '有明显进步' },
            { value: 4, label: 'D', text: '进步非常显著' },
            { value: 5, label: 'E', text: '成长卓越，有突出案例或成果' }
          ]
        },
        {
          id: 9,
          text: '该导师在与辅导员、书院管理团队沟通协作，共同解决学生问题、形成育人合力方面的表现如何？',
          options: [
            { value: 1, label: 'A', text: '缺乏沟通，疲于合作' },
            { value: 2, label: 'B', text: '沟通不畅，协作困难' },
            { value: 3, label: 'C', text: '保持基本沟通' },
            { value: 4, label: 'D', text: '主动沟通，协作良好' },
            { value: 5, label: 'E', text: '是协同育人的积极倡导者和践行者' }
          ]
        },
        {
          id: 10,
          category: '四、总体评价',
          text: '综合考量，您对该导师本学年育人工作的总体评价是？',
          options: [
            { value: 1, label: 'A', text: '不合格' },
            { value: 2, label: 'B', text: '需要较大改进' },
            { value: 3, label: 'C', text: '合格' },
            { value: 4, label: 'D', text: '良好' },
            { value: 5, label: 'E', text: '优秀' }
          ]
        }
      ]
    };
  },
  computed: {
    // 已回答题目数
    answeredCount() {
      return this.selectedAnswers.filter(answer => answer !== null).length;
    },
    
    // 是否全部回答
    isAllAnswered() {
      return this.answeredCount === this.questions.length;
    },
    
    // 进度百分比
    progressPercentage() {
      return Math.round((this.answeredCount / this.questions.length) * 100);
    },
    
    // 计算总分（1-50分）
    totalScore() {
      return this.selectedAnswers.reduce((sum, score) => sum + (score || 0), 0);
    }
  },
  methods: {
    // 选择选项
    selectOption(questionIndex, value) {
      this.$set(this.selectedAnswers, questionIndex, value);
    },
    
    // 显示确认弹窗
    showConfirmDialog() {
      if (this.isAllAnswered) {
        this.showConfirmDialogFlag = true;
      }
    },
    
    // 提交问卷
    submitQuestionnaire() {
      this.showConfirmDialogFlag = false;
      
      // 准备提交数据到 evaluation_score 表
      const submitData = {
        userName: this.userName,
        quesScore: this.totalScore, // 总得分（1-50分）
        tutorId: 1, // 导师ID，先统一设置为1（对应导师A）
        quesType: 5, // 问卷类型：5表示问卷5（辅导员/执行院长版评价问卷）
        signature: this.signature // 签名数据（图片URL或base64）
      };

      console.log('提交评价数据：', submitData);
      
      // 调用后端API提交数据
      addScore(submitData)
        .then(response => {
          console.log('提交成功:', response);
          this.showEndMessage = true;
        })
        .catch(error => {
          console.error('提交失败:', error);
          this.$message.error('提交失败，请重试！' + (error.msg || ''));
          // 即使失败也关闭确认弹窗
          this.showConfirmDialogFlag = false;
        });
    },
    
    // 返回问卷列表
    goBack() {
      this.$router.push('/Questionnaires/index');
    },
    
    // 取消提交
    handleCancel() {
      this.showConfirmDialogFlag = false;
    }
  },
  created() {
    // 初始化答案数组
    this.selectedAnswers = new Array(this.questions.length).fill(null);
    
    // 从路由参数获取签名数据
    if (this.$route.query.signature) {
      this.signature = this.$route.query.signature;
      console.log('接收到的签名数据:', this.signature);
    }
  }
};
</script>

<style scoped>
/* 问卷容器样式 */
.questionnaire-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

/* 问卷标题 */
.questionnaire-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.questionnaire-title {
  font-size: 28px;
  color: #2c3e50;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.questionnaire-subtitle {
  font-size: 16px;
  color: #7f8c8d;
  margin: 0;
}

/* 问卷内容 */
.questionnaire-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

/* 问题块 */
.question-block {
  margin-bottom: 35px;
  padding-bottom: 30px;
  border-bottom: 2px solid #ecf0f1;
}

.question-block:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.question-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.question-number {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  margin-right: 15px;
}

.question-text-wrapper {
  flex: 1;
}

.question-category {
  font-size: 16px;
  color: #667eea;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.question-text {
  font-size: 16px;
  color: #2c3e50;
  line-height: 1.6;
  margin: 0;
  font-weight: 500;
}

/* 选项列表 */
.options-list {
  margin-left: 51px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.option-item:hover {
  background: #e3f2fd;
  border-color: #667eea;
  transform: translateX(5px);
}

.option-item.selected {
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-color: #667eea;
  box-shadow: 0 3px 10px rgba(102, 126, 234, 0.2);
}

.option-radio {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  border: 2px solid #95a5a6;
  border-radius: 50%;
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.option-item.selected .option-radio {
  border-color: #667eea;
  background: white;
}

.radio-dot {
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
}

.option-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.option-label {
  font-weight: 600;
  color: #667eea;
  font-size: 15px;
  min-width: 30px;
}

.option-text {
  color: #34495e;
  font-size: 15px;
  line-height: 1.5;
}

/* 进度条 */
.progress-bar {
  width: 100%;
  height: 8px;
  background: #ecf0f1;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.4s ease;
  border-radius: 10px;
}

.progress-text {
  text-align: center;
  color: #7f8c8d;
  font-size: 14px;
  margin-bottom: 20px;
}

/* 提交按钮 */
.submit-section {
  text-align: center;
  margin-bottom: 30px;
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 15px 60px;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.submit-btn:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.submit-btn.disabled {
  background: linear-gradient(135deg, #bdc3c7 0%, #95a5a6 100%);
  cursor: not-allowed;
  box-shadow: none;
}

/* 弹窗样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(5px);
}

.dialog-box {
  background: white;
  border-radius: 16px;
  padding: 30px;
  max-width: 450px;
  width: 90%;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dialog-header h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 22px;
  text-align: center;
}

.dialog-content {
  margin-bottom: 25px;
  text-align: center;
}

.confirm-text {
  color: #34495e;
  font-size: 15px;
  margin: 10px 0;
}

.score-highlight {
  color: #667eea;
  font-size: 28px;
  font-weight: 700;
}

.warning-text {
  color: #e74c3c;
  font-size: 14px;
  margin-top: 15px;
  font-weight: 500;
}

.dialog-actions {
  display: flex;
  gap: 15px;
}

.cancel-button,
.confirm-button {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-button {
  background: #ecf0f1;
  color: #7f8c8d;
}

.cancel-button:hover {
  background: #bdc3c7;
}

.confirm-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.confirm-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 成功弹窗 */
.success-dialog {
  text-align: center;
}

.success-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
}

.success-icon svg {
  width: 100%;
  height: 100%;
}

.success-icon circle {
  stroke: #52c41a;
  stroke-width: 2;
  stroke-dasharray: 166;
  stroke-dashoffset: 166;
  animation: stroke 0.6s ease forwards;
}

.success-icon path {
  stroke: #52c41a;
  stroke-width: 2;
  stroke-dasharray: 48;
  stroke-dashoffset: 48;
  animation: stroke 0.3s 0.4s ease forwards;
}

@keyframes stroke {
  to {
    stroke-dashoffset: 0;
  }
}

.success-title {
  color: #2c3e50;
  font-size: 24px;
  margin: 0 0 15px 0;
}

.success-text {
  color: #7f8c8d;
  font-size: 15px;
  margin: 0 0 10px 0;
}

.success-score {
  color: #667eea;
  font-size: 18px;
  font-weight: 600;
  margin: 15px 0 25px 0;
}

.back-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 40px;
  border-radius: 50px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .questionnaire-container {
    padding: 20px 15px;
  }

  .questionnaire-title {
    font-size: 22px;
  }

  .questionnaire-content {
    padding: 20px 15px;
  }

  .options-list {
    margin-left: 0;
  }

  .question-header {
    flex-direction: column;
  }

  .question-number {
    margin-bottom: 10px;
  }

  .option-item {
    padding: 12px 15px;
  }

  .submit-btn {
    padding: 12px 40px;
    font-size: 15px;
  }
}
</style>

