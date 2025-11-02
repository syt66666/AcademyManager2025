<template>
  <div class="questionnaire-container">
    <!-- 导师选择界面 -->
    <div v-if="!selectedTutor" class="tutor-selection-container">
      <div class="selection-header">
        <h1 class="selection-title">选择要评价的导师</h1>
        <p class="academy-info" v-if="currentAcademy">当前书院：{{ currentAcademy }}</p>
      </div>

      <!-- 加载状态 -->
      <div v-if="loadingTutors" class="loading-state">
        <i class="el-icon-loading"></i>
        <p>正在加载导师列表...</p>
      </div>

      <!-- 导师列表 -->
      <div v-else-if="tutorList.length > 0" class="tutor-grid">
        <div 
          v-for="tutor in tutorList" 
          :key="tutor.tutorId" 
          class="tutor-grid-item"
          :class="{ 'evaluated': isEvaluated(tutor.tutorId) }"
          @click="selectTutor(tutor)"
        >
          <!-- 已评价标记 -->
          <div v-if="isEvaluated(tutor.tutorId)" class="evaluated-badge">
            <i class="el-icon-check"></i>
          </div>
          <div class="tutor-name">{{ tutor.tutorName }}</div>
          <div class="tutor-id">{{ tutor.tutorId }}</div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <i class="el-icon-folder-opened"></i>
        <p>暂无可评价的导师</p>
      </div>

      <!-- 返回按钮 -->
      <div class="back-section">
        <button @click="goBack" class="back-btn">返回问卷列表</button>
      </div>
    </div>

    <!-- 问卷界面 -->
    <div v-else class="questionnaire-main">
      <!-- 问卷标题 -->
      <div class="questionnaire-header">
        <h1 class="questionnaire-title">大工书院育人导师工作评价问卷</h1>
        <!-- 被评价导师信息 -->
        <div class="evaluated-tutor-info">
          <span class="evaluated-label">正在评价：</span>
          <span class="evaluated-name">{{ selectedTutor.tutorName }}</span>
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

      <!-- 返回选择导师按钮 -->
      <div class="reselect-section">
        <button @click="backToSelection" class="reselect-btn">
          <i class="el-icon-back"></i> 返回选择其他导师
        </button>
      </div>
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
        <p class="success-text">感谢您对 {{ selectedTutor ? selectedTutor.tutorName : '' }} 的评价</p>
        <div class="success-actions">
          <button @click="backToSelection" class="continue-button">继续评价其他导师</button>
          <button @click="goBack" class="back-button">返回问卷列表</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import store from "@/store";
import { addScore, listScore } from "@/api/system/questionnaire";
import { getNickName } from "@/api/system/student";
import { listTutors } from "@/api/student/tutor";

export default {
  name: "Questionnaire5",
  data() {
    return {
      userName: store.state.user.name,
      currentAcademy: '', // 当前用户所属书院
      tutorList: [], // 可评价的导师列表
      selectedTutor: null, // 当前选择的导师
      loadingTutors: false, // 加载状态
      evaluatedTutorIds: [], // 已评价的导师ID列表
      selectedAnswers: [], // 存储用户选择的答案（1-5分）
      showConfirmDialogFlag: false,
      showEndMessage: false,
      signature: '', // 签名数据（从路由参数获取）
      questions: [
        {
          id: 1,
          category: '一、导师基本职责考核',
          text: '思想引领：该导师在加强学生思想政治教育，引领树立崇高理想信念，深化家国情怀等方面表现是否合格？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
          ]
        },
        {
          id: 2,
          text: '学业指导：该导师在引导学生明确学习目标，掌握正确学习方法，培养学生自主学习能力和良好的学习习惯方面表现是否合格？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
          ]
        },
        {
          id: 3,
          text: '专业引导：该导师在指导学生积极开展读书活动、科学研究及其他学术活动方面表现是否合格？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
          ]
        },
        {
          id: 4,
          text: '能力培养：该导师在引导或指导学生参加各类项目、竞赛、活动，培养学生能力等方面表现是否合格？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
          ]
        },
        {
          id: 5,
          text: '人生规划：该导师在引导学生树立人生目标、规划大学生活、职业规划、价值观培养及其他能力培养方面表现是否合格？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
          ]
        },
        {
          id: 6,
          category: '二、导师工作量及整体考核',
          text: '该导师所指导的学生数量是否符合内设书院要求？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
          ]
        },
        {
          id: 7,
          text: '该导师是否达到了大工书院要求的每4周进行1次面对面指导的基本要求？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
          ]
        },
        {
          id: 8,
          text: '该导师是否按照规定向内设书院提交指导情况小结？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
          ]
        },
        {
          id: 9,
          text: '该导师是否符合内设书院对于导师日常管理的其他要求？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
          ]
        },
        {
          id: 10,
          text: '内设书院对于该导师的整体评价是否合格？',
          options: [
            { value: 5, label: 'A', text: '是' },
            { value: 0, label: 'B', text: '否' }
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
    // 获取当前用户所属书院
    async loadCurrentAcademy() {
      try {
        const response = await getNickName();
        if (response && response.msg) {
          this.currentAcademy = response.msg;
        }
      } catch (error) {
        this.$message.error('获取书院信息失败');
      }
    },

    // 加载导师列表
    async loadTutorList() {
      this.loadingTutors = true;
      try {
        // 先获取当前用户所属书院
        await this.loadCurrentAcademy();
        
        if (!this.currentAcademy) {
          this.$message.warning('未获取到书院信息');
          this.loadingTutors = false;
          return;
        }
        
        // 查询同一书院的所有导师
        const response = await listTutors({ 
          tutorDepartment: this.currentAcademy 
        });
        
        if (response && response.rows) {
          this.tutorList = response.rows;
          // 加载已评价记录
          await this.loadEvaluatedRecords();
        }
      } catch (error) {
        this.$message.error('加载导师列表失败');
      } finally {
        this.loadingTutors = false;
      }
    },

    // 加载已评价记录
    async loadEvaluatedRecords() {
      try {
        // 查询当前用户在问卷5中的所有评价记录
        const response = await listScore({
          userName: this.userName,
          quesType: 5 // 问卷类型5
        });
        
        if (response && response.rows) {
          // 提取已评价的导师ID列表
          this.evaluatedTutorIds = response.rows.map(record => record.tutorId);
        }
      } catch (error) {
        console.error('加载已评价记录失败:', error);
      }
    },

    // 选择导师
    selectTutor(tutor) {
      this.selectedTutor = tutor;
      // 重置问卷答案
      this.selectedAnswers = new Array(this.questions.length).fill(null);
      this.showConfirmDialogFlag = false;
      this.showEndMessage = false;
    },

    // 返回导师选择界面
    async backToSelection() {
      this.selectedTutor = null;
      this.selectedAnswers = new Array(this.questions.length).fill(null);
      this.showConfirmDialogFlag = false;
      this.showEndMessage = false;
      // 重新加载已评价记录，确保显示最新状态
      await this.loadEvaluatedRecords();
    },

    // 判断导师是否已被评价
    isEvaluated(tutorId) {
      return this.evaluatedTutorIds.includes(tutorId);
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
      
      // 检查是否选择了导师
      if (!this.selectedTutor || !this.selectedTutor.tutorId) {
        this.$message.error('未选择评价的导师');
        return;
      }
      
      // 根据账号奇偶性判断 quesType
      // 账号是偶数：quesType=6（执行院长）
      // 账号是奇数：quesType=5（执行副院长）
      const userNumber = parseInt(this.userName);
      const quesType = (userNumber % 2 === 0) ? 6 : 5;
      
      // 准备提交数据到 evaluation_score 表
      const submitData = {
        userName: this.userName,
        quesScore: this.totalScore * 2, // 总得分×2（满分100分）
        tutorId: this.selectedTutor.tutorId, // 使用选中的导师ID
        quesType: quesType, // 问卷类型：5=执行副院长（奇数账号），6=执行院长（偶数账号）
        signature: this.signature // 签名数据（图片URL或base64）
      };
      
      // 调用后端API提交数据
      addScore(submitData)
        .then(response => {
          // 标记该导师已被评价
          if (!this.evaluatedTutorIds.includes(this.selectedTutor.tutorId)) {
            this.evaluatedTutorIds.push(this.selectedTutor.tutorId);
          }
          this.showEndMessage = true;
        })
        .catch(error => {
          this.$message.error('提交失败，请重试！' + (error.msg || ''));
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

    // 加载导师列表
    this.loadTutorList();
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

/* 导师选择界面样式 */
.tutor-selection-container {
  max-width: 800px;
  margin: 0 auto;
}

.selection-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.selection-title {
  font-size: 28px;
  color: #2c3e50;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.selection-subtitle {
  font-size: 16px;
  color: #7f8c8d;
  margin: 0 0 10px 0;
}

.academy-info {
  font-size: 14px;
  color: #667eea;
  margin: 15px 0 0 0;
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 20px;
  display: inline-block;
}

/* 加载和空状态 */
.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.loading-state i,
.empty-state i {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 20px;
  display: block;
}

.loading-state p,
.empty-state p {
  font-size: 16px;
  color: #7f8c8d;
  margin: 0;
}

/* 导师网格列表 */
.tutor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
}

.tutor-grid-item {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 16px;
  text-align: center;
  border: 2px solid transparent;
  position: relative;
}

.tutor-grid-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.25);
  border-color: #667eea;
}

/* 已评价状态 */
.tutor-grid-item.evaluated {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-color: #52c41a;
}

.tutor-grid-item.evaluated:hover {
  border-color: #52c41a;
  box-shadow: 0 6px 16px rgba(82, 196, 26, 0.25);
}

/* 已评价徽章 */
.evaluated-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 24px;
  height: 24px;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.4);
  animation: checkBounce 0.5s ease;
}

@keyframes checkBounce {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

.tutor-name {
  font-size: 16px;
  color: #2c3e50;
  font-weight: 600;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tutor-id {
  font-size: 13px;
  color: #7f8c8d;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 被评价导师信息 */
.evaluated-tutor-info {
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

.evaluated-label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
}

.evaluated-name {
  font-size: 16px;
  color: #667eea;
  font-weight: 600;
}

.evaluated-title {
  font-size: 14px;
  color: #95a5a6;
}

/* 返回选择导师按钮 */
.reselect-section {
  text-align: center;
  margin-top: 20px;
}

.reselect-btn {
  background: #ecf0f1;
  color: #7f8c8d;
  border: none;
  padding: 12px 30px;
  border-radius: 50px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.reselect-btn:hover {
  background: #bdc3c7;
  transform: translateY(-2px);
}

/* 成功弹窗按钮组 */
.success-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 20px;
}

.continue-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 50px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.continue-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.back-section {
  text-align: center;
  margin-top: 30px;
}

.back-btn {
  background: #ecf0f1;
  color: #7f8c8d;
  border: none;
  padding: 12px 40px;
  border-radius: 50px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #bdc3c7;
  transform: translateY(-2px);
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

  /* 导师网格响应式 */
  .tutor-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 10px;
  }

  .tutor-grid-item {
    padding: 12px;
  }

  .tutor-name {
    font-size: 14px;
  }

  .tutor-id {
    font-size: 12px;
  }
}
</style>

