<template>
  <div class="questionnaire">
    <div class="questionnaire-form">
      <div v-for="(question, index) in currentDisplay" :key="question.id" class="question-container">
        <div class="question-box">
          <h2>{{ index + 1 }}.{{ question.text }}</h2>
          <div class="options">
            <label v-for="option in question.options" :key="option.id">
              <input
                type="radio"
                :value="option.id"
                v-model="selectedOptions[index]"

                @click="selectOption(option, index)"/>
<!--            :disabled="option.disabled || completed"-->

              {{ option.text }}
            </label>
          </div>
        </div>
      </div>
      <button v-if="completed " @click="submitQuestionnaire" class="submit-button">提交</button>



      <!-- 弹窗内容 -->
      <el-dialog :visible.sync="showEndMessage"  style="height: auto; width: auto" >
        <el-card class="endMessageCard">
        <div  class="end-message">

          <h2>问卷结束，感谢您的参与！</h2>
           <el-button @click="closeModal">关闭</el-button>
        </div>
        </el-card>
      </el-dialog>

    </div>
  </div>
</template>

<script>
export default {
  name: 'Questionnaire',
  data() {
    return {
      questionnaire: [
        {
          id: 1,
          text: '请先确定专业选择方案 [单选题] *',
          options: [
            {id: 1, text: '保持现有专业', next: null},
            {id: 2, text: '类内任选专业', next: 10},
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
          id: 10,
          text: '材料类 意向专业 [单选题] *',
          options: [
            {id: 1, text: '金属材料工程', next: null},
            {id: 2, text: '功能材料', next: null},
            {id: 3, text: '材料成型及控制工程', next: null}
          ]
        },
        {
          id: 11,
          text: '管理科学与工程类 意向专业 [单选题] *',
          options: [
            {id: 1, text: '大数据管理与应用', next: null},
            {id: 2, text: '信息管理与信息系统', next: null}
          ]
        },
        {
          id: 12,
          text: '化工与制药类 意向专业 [单选题] *',
          options: [
            {id: 1, text: '化学工程与工艺', next: null},
            {id: 2, text: '精细化工', next: null},
            {id: 3, text: '制药工程', next: null},
            {id: 4, text: '高分子材料与工程', next: null},
            {id: 5, text: '安全工程', next: null}
          ]
        },
        {
          id: 13,
          text: '环境科学与工程类 意向专业 [单选题] *',
          options: [
            {id: 1, text: '环境科学', next: null},
            {id: 2, text: '环境工程', next: null}
          ]
        },
        {
          id: 14,
          text: '新闻传播学类 意向专业 [单选题] *',
          options: [
            {id: 1, text: '广播电视学', next: null},
            {id: 2, text: '汉语言文学', next: null}
          ]
        },
        {
          id: 15,
          text: '智能建造 意向专业 [单选题] *',
          options: [
            {id: 1, text: '智能建造', next: null},
            {id: 2, text: '水利水电工程', next: null},
            {id: 3, text: '港口航道与海岸工程', next: null},
            {id: 4, text: '海洋资源开发技术', next: null},
            {id: 5, text: '交通工程', next: null},
            {id: 6, text: '工程管理', next: null},
            {id: 7, text: '建筑环境与能源应用工程', next: null},
            {id: 8, text: '土木工程', next: null}
          ]
        },
        {
          id: 16,
          text: '智能制造工程 意向专业 [单选题] *',
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
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ],
      displayedQuestions: [],
      completed: false, //选中某专业
      showEndMessage: false,// 控制结束消息的显示
      currentDisplay: [], //当前展示的问题的数组
      selectedOptions: []
    };
  },
  computed: {

  },
  methods: {
    closeModal() {
      this.showEndMessage = false; // 关闭弹窗
    },
    selectOption(option, index) {
      this.completed = false;
      if (option.next === null) {
        this.completed = true;
        // return;
      }
      this.selectedOptions[index] = option.id;
      //option.disabled = true;

      // 清空当前显示的问题，除了第一个问题
      this.currentDisplay = this.currentDisplay.slice(0, index + 1);

      // 添加新的问题到当前显示的问题列表
      if (this.questionnaire[option.next - 1]) {
        this.currentDisplay.push(this.questionnaire[option.next - 1]);
      }
    },
    submitQuestionnaire() {
      this.showEndMessage = true;

    },
  },
  mounted() {
    // 初始化显示第一个问题
    this.currentDisplay.push(this.questionnaire[0]);
  }
};
</script>

<style scoped>

/* 问卷容器样式 */
.questionnaire {
  max-width: 600px; /* 问卷最大宽度 */
  margin: 20px auto; /* 问卷居中显示 */
  padding: 20px; /* 内边距 */
  background-color: #f9f9f9; /* 背景颜色 */
  border-radius: 8px; /* 圆角 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 阴影 */
  font-family: 'Arial', sans-serif; /* 字体 */
}

/* 问题标题样式 */
.questionnaire h2 {
  margin-bottom: 10px; /* 下边距 */
  font-size: 18px; /* 字体大小 */
  color: #333; /* 字体颜色 */
}

/* 单选题选项样式 */
.questionnaire .option-button[type="radio"] {
  margin-right: 5px; /* 右边距 */
}

/* 选项文本样式 */
.questionnaire label {
  display: inline-block; /* 行内块显示 */
  margin-right: 20px; /* 右边距 */
  font-size: 16px; /* 字体大小 */
  color: #555; /* 字体颜色 */
}

/* 提交按钮样式 */
.submit-button {
  display: block; /* 块级元素 */
  width: 100%; /* 宽度100% */
  padding: 10px; /* 内边距 */
  margin-top: 20px; /* 上边距 */
  background-color: #007bff; /* 背景颜色 */
  color: white; /* 字体颜色 */
  border: none; /* 无边框 */
  border-radius: 5px; /* 圆角 */
  cursor: pointer; /* 鼠标指针 */
  font-size: 16px; /* 字体大小 */
}

/* 提交按钮悬停效果 */
.submit-button:hover {
  background-color: #0056b3; /* 悬停时背景颜色 */
}
.modal-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 弹窗内容样式 */
.end-message {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  text-align: center;
}

/* 关闭按钮样式 */
.modal-content button {
  margin-top: 10px;
}
</style>
