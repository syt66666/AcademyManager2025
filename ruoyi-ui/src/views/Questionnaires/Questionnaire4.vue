<template>
  <div class="questionnaire-container">
    <!-- 问卷标题 -->
    <div class="questionnaire-header">
      <h1 class="questionnaire-title">大工书院育人导师工作评价问卷</h1>
      <!-- 导师信息显示 -->
      <div class="tutor-info" v-if="tutorInfo">
        <span class="tutor-label">我的导师：</span>
        <span class="tutor-name">{{ tutorInfo.tutorName }}</span>
       
      </div>
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
        <button @click="goBack" class="back-button">返回问卷列表</button>
      </div>
    </div>
  </div>
</template>

<script>
import store from "@/store";
import { addScore } from "@/api/system/questionnaire";
import { listRelation } from "@/api/system/relation";
import { getTutors } from "@/api/student/tutor";

export default {
  name: "Questionnaire4",
  data() {
    return {
      userName: store.state.user.name,
      studentId: store.state.user.userId, // 当前学生ID
      tutorInfo: null, // 导师信息
      selectedAnswers: [], // 存储用户选择的答案（1-5分）
      showConfirmDialogFlag: false,
      showEndMessage: false,
      signature: '', // 签名数据（从路由参数获取）
      questions: [
        {
          id: 1,
          category: '一、师德师风与指导投入',
          text: '你认为导师在为人师表、关爱学生、言传身教等方面的总体表现如何？',
          options: [
            { value: 1, label: 'A', text: '有待加强' },
            { value: 2, label: 'B', text: '一般' },
            { value: 3, label: 'C', text: '合格' },
            { value: 4, label: 'D', text: '良好' },
            { value: 5, label: 'E', text: '非常优秀' }
          ]
        },
        {
          id: 2,
          text: '指导频次与规律：导师与你进行面对面交流的频率与规律性如何？',
          options: [
            { value: 1, label: 'A', text: '极少联系，无规律' },
            { value: 2, label: 'B', text: '联系较少，规律性差' },
            { value: 3, label: 'C', text: '基本达到"每4周1次"的要求' },
            { value: 4, label: 'D', text: '经常联系，较为规律' },
            { value: 5, label: 'E', text: '交流频繁，非常规律且及时' }
          ]
        },
        {
          id: 3,
          category: '二、职责履行与育人实效',
          text: '导师是否在理想信念、学术诚信、家国情怀等方面给予你积极的引导和影响？',
          options: [
            { value: 1, label: 'A', text: '无明显影响' },
            { value: 2, label: 'B', text: '偶尔提及，影响甚微' },
            { value: 3, label: 'C', text: '有正面影响' },
            { value: 4, label: 'D', text: '引导较为积极，有一定成效' },
            { value: 5, label: 'E', text: '引导非常深刻和正面，成效显著' }
          ]
        },
        {
          id: 4,
          text: '学业指导与方法培养：导师在帮助你适应大学学习、制定学习计划、改进学习方法等方面作用如何？',
          options: [
            { value: 1, label: 'A', text: '基本没有帮助' },
            { value: 2, label: 'B', text: '帮助有限' },
            { value: 3, label: 'C', text: '有一定帮助' },
            { value: 4, label: 'D', text: '有较大帮助' },
            { value: 5, label: 'E', text: '帮助显著，使我受益匪浅' }
          ]
        },
        {
          id: 5,
          text: '专业引导与兴趣激发：导师在加深你对专业的理解、激发你的专业兴趣与学术热情方面做得如何？',
          options: [
            { value: 1, label: 'A', text: '未涉及' },
            { value: 2, label: 'B', text: '略有提及，但引导不足' },
            { value: 3, label: 'C', text: '有一定启发' },
            { value: 4, label: 'D', text: '有效引导，提升了我的兴趣' },
            { value: 5, label: 'E', text: '极大地激发了我的学术潜能和热爱' }
          ]
        },
        {
          id: 6,
          text: '创新能力与实践培养：导师在培养你的创新、批判性思维，或引导你参与科研、竞赛、社会实践等方面作用如何？',
          options: [
            { value: 1, label: 'A', text: '未提供任何指导或机会' },
            { value: 2, label: 'B', text: '仅有口头鼓励，无实质支持' },
            { value: 3, label: 'C', text: '提供了一些建议和基本引导' },
            { value: 4, label: 'D', text: '积极创造条件，有意识地培养我的能力' },
            { value: 5, label: 'E', text: '提供了关键性指导和宝贵机会，成效明显' }
          ]
        },
        {
          id: 7,
          text: '生涯规划与未来发展：导师在帮助你进行大学生涯规划、职业生涯规划或升学/就业指导方面的有效性如何？',
          options: [
            { value: 1, label: 'A', text: '未提供有效指导' },
            { value: 2, label: 'B', text: '指导较为笼统，缺乏针对性' },
            { value: 3, label: 'C', text: '提供了基本方向和参考' },
            { value: 4, label: 'D', text: '指导具体，对我有切实帮助' },
            { value: 5, label: 'E', text: '指导非常系统，对我未来选择至关重要' }
          ]
        },
        {
          id: 8,
          text: '人文关怀与心理支持：导师在关心你的身心健康、个人成长，以及当你遇到困难时给予支持的情况如何？',
          options: [
            { value: 1, label: 'A', text: '缺乏基本关心' },
            { value: 2, label: 'B', text: '关心较少，支持不足' },
            { value: 3, label: 'C', text: '能够给予例行性关心' },
            { value: 4, label: 'D', text: '能主动关心并提供有益建议' },
            { value: 5, label: 'E', text: '关怀备至，是我信任的良师益友' }
          ]
        },
        {
          id: 9,
          category: '三、总体评价',
          text: '导师能否根据你的个人特点、兴趣和需求，提供个性化的指导？',
          options: [
            { value: 1, label: 'A', text: '完全没有' },
            { value: 2, label: 'B', text: '很少考虑个人特点' },
            { value: 3, label: 'C', text: '仅具有个性化培养理念' },
            { value: 4, label: 'D', text: '比较注重因材施教' },
            { value: 5, label: 'E', text: '善于指导且极具针对性' }
          ]
        },
        {
          id: 10,
          text: '综合来看，你对导师本学年育人工作的总体满意度是？',
          options: [
            { value: 1, label: 'A', text: '非常不满意' },
            { value: 2, label: 'B', text: '不太满意' },
            { value: 3, label: 'C', text: '一般' },
            { value: 4, label: 'D', text: '满意' },
            { value: 5, label: 'E', text: '非常满意' }
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
    // 加载导师信息
    loadTutorInfo() {
      // 使用 listRelation API 查询该学生的导师关系
      listRelation({ studentId: this.studentId })
        .then(response => {
          if (response.rows && response.rows.length > 0) {
            const tutorId = response.rows[0].tutorId;
            // 根据导师ID获取导师详细信息
            return getTutors(tutorId);
          } else {
            return null;
          }
        })
        .then(response => {
          if (response && response.data) {
            this.tutorInfo = response.data;
          }
        })
        .catch(error => {
          this.$message.error('获取导师信息失败，请刷新页面重试');
        });
    },
    
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
      
      // 检查是否已获取导师信息
      if (!this.tutorInfo || !this.tutorInfo.tutorId) {
        this.$message.error('未获取到导师信息，无法提交问卷！');
        return;
      }
      
      // 准备提交数据到 evaluation_score 表
      const submitData = {
        userName: this.userName, // 使用 data 中定义的 userName
        quesScore: this.totalScore, // 总得分（1-50分）
        tutorId: this.tutorInfo.tutorId, // 使用实际获取到的导师ID
        quesType: 4, // 问卷类型：4表示问卷4（学生版评价问卷）
        signature: this.signature // 签名数据（图片URL或base64）
      };
      
      // 调用后端API提交数据
      addScore(submitData)
        .then(response => {
          this.showEndMessage = true;
        })
        .catch(error => {
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
    }

    // 获取导师信息
    this.loadTutorInfo();
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
  margin: 0 0 15px 0;
}

/* 导师信息 */
.tutor-info {
  margin-top: 15px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 8px;
  border: 1px solid #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.tutor-label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
}

.tutor-name {
  font-size: 16px;
  color: #667eea;
  font-weight: 600;
}

.tutor-title {
  font-size: 14px;
  color: #95a5a6;
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

