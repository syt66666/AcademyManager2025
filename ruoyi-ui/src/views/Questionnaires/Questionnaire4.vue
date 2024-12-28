<template>
  <div class="questionnaire">
    <div class="questionnaire-form">
      <div v-for="(question, index) in currentDisplay" :key="question.id" class="question-container">
        <div class="question-box">
          <h2>{{ index + 1 }}. {{ question.text }}</h2>
          <div class="options">
            <label v-for="option in question.options" :key="option.id">
              <input
                type="radio"
                :value="option.id"
                v-model="selectedOptions[index]"
                @click="handleOptionClick(option, index)"
              />
              {{ option.text }}
            </label>
          </div>
        </div>
      </div>
      <button v-if="completed" @click="showConfirmDialog" class="submit-button">提交</button>

      <!-- 提交确认弹窗 -->
      <div v-if="showConfirmDialogFlag" class="dialog-container">
        <div class="dialog-box">
          <h2>确认提交</h2>
          <p>您确定要提交问卷吗？</p>
          <div class="dialog-footer">
            <el-button @click="handleCancel">取消</el-button>
            <el-button type="primary" @click="submitQuestionnaire" class="confirm-button">确认提交</el-button>
          </div>
        </div>
      </div>

      <!-- 提交完成弹窗 -->
      <div v-if="showEndMessage" class="dialog-container">
        <div class="dialog-box end-message">
          <span class="close-btn" @click="closeModal">&times;</span> <!-- 关闭按钮 -->
          <h2>问卷结束，感谢您的参与！</h2>
          <el-button @click="closeModal">关闭</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import store from "@/store";
export default {

  name: "Questionnaire",
  data() {
    return {
      questionnaireId: this.$route.query.num, // 获取动态路由参数
      questionnaire: [
        {
          id: 1,
          text: '请先确定专业选择方案 [单选题] *',
          options: [
            {id: 1, text: '保持现有专业', next: null},
            {id: 2, text: '域内任选专业', next: 9},
            {id: 3, text: '跨学域转专业', next: 2}
          ]
        },
        {
          id: 2,
          text: '意向书院学域 [单选题] *',
          options: [
            {id: 1, text: '大煜书院——物质创造学域', next: 3},
            {id: 2, text: '伯川书院——智能制造学域', next: 4},
            {id: 3, text: '笃学书院——理科强基学域', next: 5},
            {id: 4, text: '令希书院——智能建造学域', next: 6},
            {id: 5, text: '厚德书院——人文社科学域', next: 7},
            {id: 6, text: '知行书院——信息技术学域（一）', next: 8},
            {id: 7, text: '求实书院——信息技术学域（二）', next: 9}
          ]
        },
        {
          id: 3,
          text: '意向专业 [单选题] *',
          options: [
            {id: 1, text: '化学工程与工艺', next: null},
            {id: 2, text: '精细化工', next: null},
            {id: 3, text: '制药工程', next: null},
            {id: 4, text: '高分子材料与工程', next: null},
            {id: 5, text: '安全工程', next: null},
            {id: 6, text: '过程装备与控制工程', next: null},
            {id: 7, text: '环境工程', next: null},
            {id: 8, text: '环境科学', next: null},
            {id: 9, text: '生物工程', next: null}
          ]
        },
        {
          id: 4,
          text: '意向专业 [单选题] *',
          options: [
            {id: 1, text: '智能制造工程', next: null},
            {id: 2, text: '能源与动力工程', next: null},
            {id: 3, text: '机械设计制造及其自动化', next: null},
            {id: 4, text: '车辆工程（英语强化）', next: null},
            {id: 5, text: '测控技术与仪器', next: null},
            {id: 6, text: '金属材料工程', next: null},
            {id: 7, text: '功能材料', next: null},
            {id: 8, text: '材料成型及控制工程', next: null},
            {id: 9, text: '生物医学工程', next: null}
          ]
        },
        {
          id: 5,
          text: '意向专业 [单选题] *',
          options: [
            {id: 1, text: '数学与应用数学', next: null},
            {id: 2, text: '信息科学与计算科学', next: null}
          ]
        },
        {
          id: 6,
          text: '意向专业 [单选题] *',
          options: [
            {id: 1, text: '工程力学', next: null},
            {id: 2, text: '飞行器设计与工程', next: null},
            {id: 3, text: '船舶与海洋工程', next: null},
            {id: 4, text: '智能建造', next: null},
            {id: 5, text: '水利水电工程', next: null},
            {id: 6, text: '港口航道与海岸工程', next: null},
            {id: 7, text: '海洋资源开发技术', next: null},
            {id: 8, text: '交通工程', next: null},
            {id: 9, text: '工程管理', next: null},
            {id: 10, text: '建筑环境与能源应用工程', next: null},
            {id: 11, text: '土木工程', next: null}
          ]
        },
        {
          id: 7,
          text: '意向专业 [单选题] *',
          options: [
            {id: 1, text: '工商管理类——金融学', next: null},
            {id: 2, text: '工商管理类——工商管理', next: null},
            {id: 3, text: '工商管理类——物流管理', next: null},
            {id: 4, text: '工商管理类——国际经济与贸易', next: null},
            {id: 5, text: '公共事业管理、哲学类、新闻传播学类——知识产权', next: null},
            {id: 6, text: '公共事业管理、哲学类、新闻传播学类——公共事业管理', next: null},
            {id: 7, text: '公共事业管理、哲学类、新闻传播学类——马克思主义理论', next: null},
            {id: 8, text: '公共事业管理、哲学类、新闻传播学类——哲学', next: null},
            {id: 9, text: '公共事业管理、哲学类、新闻传播学类——广播电视学', next: null},
            {id: 10, text: '公共事业管理、哲学类、新闻传播学类——汉语言文学', next: null},
            {id: 11, text: '英语、日语——英语', next: null},
            {id: 12, text: '英语、日语——翻译', next: null},
            {id: 13, text: '英语、日语——日语', next: null},
            {id: 14, text: '建筑类——建筑学', next: null},
            {id: 15, text: '建筑类——城乡规划', next: null},
            {id: 16, text: '建筑类——工业设计', next: null},
            {id: 17, text: '设计学类——视觉传达设计', next: null},
            {id: 18, text: '设计学类——环境设计', next: null},
            {id: 19, text: '设计学类——雕塑', next: null},
            {id: 20, text: '运动训练——运动训练', next: null}
          ]
        },
        {
          id: 8,
          text: '意向专业 [单选题] *',
          options: [
            {id: 1, text: '电气工程及其自动化', next: null},
            {id: 2, text: '自动化', next: null},
            {id: 3, text: '电子信息工程', next: null},
            {id: 4, text: '计算机科学与技术', next: null},
            {id: 5, text: '生物医学工程', next: null},
            {id: 6, text: '光电信息科学与工程', next: null},
            {id: 7, text: '大数据管理与应用', next: null},
            {id: 8, text: '信息管理与信息系统', next: null}
          ]
        },
        {
          id: 9,
          text: '意向专业 [单选题] *',
          options: [
            {id: 1, text: '软件工程', next: null},
            {id: 2, text: '网络工程', next: null},
            {id: 3, text: '集成电路设计与集成系统', next: null},
            {id: 4, text: '电子科学与技术', next: null}
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ],
      currentDisplay: [], // 当前显示的问题列表
      selectedOptions: [], // 用户选择的答案
      completed: false, // 是否已完成
      showEndMessage: false, // 弹窗显示状态
      showConfirmDialogFlag: false, // 提交确认弹窗的显示状态
      finalAnswerText : ' ',//最后选择答案
      userId:store.state.user.id//将缓存的用户id赋值给userId
    };
  },
  methods: {
    showConfirmDialog() {
      this.showConfirmDialogFlag = true;  // 显示确认弹窗
    },
    // 初始化问卷
    initializeQuestionnaire() {
      this.currentDisplay.push(this.questionnaire[0]);
    },
    // 处理选项点击事件
    handleOptionClick(option, index) {
      // 更新最后一个选项
      this.finalAnswerText=option.text;
      // 使用 Vue 的响应式方法确保 selectedOptions 更新正确
      this.$set(this.selectedOptions, index, option.id);

      // 更新 completed 状态：如果选择了最后一个问题，则完成问卷
      this.completed = option.next === null;

      // 根据选项的 next 属性更新显示的问题
      this.currentDisplay = this.currentDisplay.slice(0, index + 1);  // 保持当前已经回答的问题

      if (option.next !== null) {
        const nextQuestion = this.questionnaire.find(q => q.id === option.next);
        if (nextQuestion) {
          this.currentDisplay.push(nextQuestion);  // 添加下一个问题
        }
      }
    },


    //提交问卷
    submitQuestionnaire() {
      this.showConfirmDialogFlag = false;  // 关闭确认弹窗
      this.showEndMessage = true;  // 显示结束弹窗

      // 提交最后一个问题的答案到后台
      axios
        .post('http://localhost:3000/questionnaire/submit', {
          user_id: this.userId, // 用户ID，示例为1
          questionnaire_id: this.questionnaireId,
          answer: this.finalAnswerText  // 只提交最后一个问题的答案
        })
        .then(response => {
          setTimeout(() => {
            this.$router.push('/Questionnaires');
          }, 2000);  // 2000毫秒即2秒
        })
        .catch(error => {
          console.error('提交失败:', error);
        });
    },

    // 关闭弹窗
    closeModal() {
      this.showEndMessage = false;
    },
    // 取消提交
    handleCancel() {
      this.showConfirmDialogFlag = false;  // 关闭确认弹窗
    }
  },
  mounted() {
    this.initializeQuestionnaire();
  }
};


</script>

<style scoped>
/* 问卷容器样式 */
.questionnaire {
  max-width: 700px;
  margin: 20px auto;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  font-family: "Arial", sans-serif;
}

/* 问题标题 */
.questionnaire h2 {
  margin-bottom: 10px;
  font-size: 18px;
  color: #333;
}

/* 选项样式 */
.options label {
  display: block;
  margin-bottom: 8px;
  font-size: 16px;
  color: #555;
}

.options input[type="radio"] {
  margin-right: 10px;
}

/* 提交按钮 */
.submit-button {
  display: block;
  width: 100%;
  padding: 12px 20px;
  margin-top: 20px;
  background: linear-gradient(to right, #395cdc, #2b76de); /* 渐变背景 */
  color: white;
  border: none;
  border-radius: 25px; /* 圆角按钮 */
  cursor: pointer;
  font-size: 18px; /* 增加字体大小 */
  font-weight: bold;
  text-align: center;
  transition: all 0.3s ease; /* 增加动画效果 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 阴影效果 */
}

.submit-button:hover {
  background: linear-gradient(to right,  #2b76de,#395cdc ); /* 反转渐变颜色 */
  transform: translateY(-2px); /* 微微上移 */
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3); /* 增强阴影 */
}

.submit-button:active {
  transform: translateY(1px); /* 按下时微微下移 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); /* 减弱阴影 */
}


.dialog-container {
  position: fixed; /* 固定定位，确保弹窗始终居中 */
  top: 0;
  left: 0;
  width: 100vw; /* 全屏宽度 */
  height: 100vh; /* 全屏高度 */
  display: flex; /* 使用 Flexbox 布局 */
  justify-content: center; /* 横向居中 */
  align-items: center; /* 纵向居中 */
  background-color: rgba(0, 0, 0, 0.5); /* 半透明背景 */
  z-index: 9999; /* 确保弹窗在最上层 */
  animation: fadeIn 0.3s ease-out; /* 弹窗淡入动画 */
}

/* 弹窗内容容器 */
.dialog-box {
  background-color: #fff; /* 卡片背景颜色 */
  border-radius: 12px; /* 圆角效果 */
  padding: 40px 30px; /* 内边距 */
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15); /* 卡片阴影 */
  text-align: center;
  width: 100%; /* 设置宽度为100%，确保容器自适应 */
  max-width: 500px; /* 设置最大宽度 */
  box-sizing: border-box; /* 包括内边距在内的宽度计算 */
}

/* 弹窗标题 */
.dialog-box h2 {
  font-size: 22px;
  color: #395cdc; /* 蓝色字体 */
  margin-bottom: 20px;
}

/* 弹窗内容 */
.dialog-box p {
  font-size: 16px;
  color: #333;
  margin-bottom: 30px; /* 增加底部间距 */
}

/* 弹窗按钮 */
.dialog-footer {
  display: flex;
  justify-content: space-around;
  gap: 15px;
}

.dialog-footer el-button {
  width: 100px;
}

/* 确认弹窗按钮样式 */
.confirm-button {
  background-color: #395cdc; /* 蓝色背景 */
  color: white;
  border: none;
  border-radius: 5px;
}

.confirm-button:hover {
  background-color: #2b76de; /* 蓝色渐变 */
}

.confirm-button:active {
  background-color: #1f5bb5; /* 按下时颜色变化 */
}

/* 结束弹窗的具体样式调整 */
.end-message h2 {
  font-size: 24px;
  color: #00b16a; /* 绿色字体 */
}

/* 弹窗动画 */
@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: scale(0.95);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}


/* 弹窗容器通用样式 */
.dialog-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  animation: fadeIn 0.3s ease-out;
}

/* 弹窗内容容器 */
.dialog-box {
  background-color: #fff;
  border-radius: 12px;
  padding: 40px 30px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
  text-align: center;
  width: 100%;
  max-width: 500px;
  position: relative; /* 使得子元素可以定位 */
  box-sizing: border-box;
}

/* 右上角关闭按钮 */
.dialog-box .close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 20px;
  cursor: pointer;
  color: #ccc;
}

.dialog-box .close-btn:hover {
  color: #000; /* 悬停时改变颜色 */
}

/* 弹窗标题 */
.dialog-box h2 {
  font-size: 22px;
  color: #395cdc;
  margin-bottom: 20px;
}

/* 弹窗内容 */
.dialog-box p {
  font-size: 16px;
  color: #333;
  margin-bottom: 30px;
}

/* 弹窗按钮 */
.dialog-footer {
  display: flex;
  justify-content: space-around;
  gap: 15px;
}

.dialog-footer el-button {
  width: 100px;
}

/* 确认弹窗按钮样式 */
.confirm-button {
  background-color: #395cdc;
  color: white;
  border: none;
  border-radius: 5px;
}

.confirm-button:hover {
  background-color: #2b76de;
}

.confirm-button:active {
  background-color: #1f5bb5;
}

/* 结束弹窗的具体样式调整 */
.end-message h2 {
  font-size: 24px;
  color: #00b16a;
}

@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: scale(0.95);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
