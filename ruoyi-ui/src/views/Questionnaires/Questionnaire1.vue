<template>
  <div class="questionnaire">
    <div class="questionnaire-form">
      <div v-for="(question, index) in currentDisplay" :key="question.id" class="question-container">
        <div class="question-box">
          <h2>{{ index + 1 }}. {{ question.text }}</h2>
          <div class="options">
            <label v-for="option in getFilteredOptions(question.options)" :key="option.id">
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
      questionnaire: [], // 用于存放问卷
      currentDisplay: [], // 当前显示的问题列表
      selectedOptions: [], // 用户选择的答案
      completed: false, // 是否已完成
      showEndMessage: false, // 弹窗显示状态
      showConfirmDialogFlag: false, // 提交确认弹窗的显示状态
      finalAnswerText: ' ', // 不转专业的专业选择
      finalAnswerText2: ' ', // 转专业的书院选择
      finalAnswerText3: ' ',//转专业的专业选择
      finalAnswerText4: 0,//分流类型记录
      userName: store.state.user.name, // 获取用户名
      splitFlow: '', // 分流形式
      num: null , // 转换书院信息
      num2: null, //转换专业类信息
      num3: null, //转换厚德书院专业类信息
      num4: null, //转换创新班专业信息
      filter:null, //是否过滤
      userSelections: [],
    };
  },
  methods: {
    //设置num1值，书院的对应值
    setNumBasedOnDepartment(department) {
      switch (department) {
        case '大煜书院':
          this.num = 3;
          break;
        case '伯川书院':
          this.num = 4;
          break;
        case '笃学书院':
          this.num = 5;
          break;
        case '令希书院':
          this.num = 6;
          break;
        case '厚德书院':
          this.num = 7;
          break;
        case '知行书院':
          this.num = 8;
          break;
        case '求实书院':
          this.num = 9;
          break;
        default:
          this.num = null;  // 如果department不匹配，设置num为null
      }
    },
    //设置num2值，专业类的对应值
    setNumBasedOnMajor(major) {
      switch (major) {
        case '材料类':
          this.num2 = 10;
          break;
        case '管理科学与工程类':
          this.num2 = 11;
          break;
        case '化工与制药类':
          this.num2 = 12;
          break;
        case '环境科学与工程类':
          this.num2 = 13;
          break;
        case '新闻传播学类':
          this.num2 = 14;
          break;
        case '智能建造':
          this.num2 = 15;
          break;
        case '智能制造工程':
          this.num2 = 16;
          break;
        default:
          this.num2 = null;
      }
    },
    //设置num3值，厚德
    setNumBasedOnSpecialty(specialty) {
      switch (specialty) {
        case '工商管理类':
          this.num3 = 10;
          break;
        case '人文社科学域':
          this.num3 = 11;
          break;
        case '英语类':
          this.num3 = 12;
          break;
        case '日语':
          this.num3 = 12;
          break;
        case '建筑类':
          this.num3 = 13;
          break;
        default:
          this.num3 = this.num;
      }
    },
    //设置num4值，创新班的逻辑：有的创新班型内部需要任选
    setNumBasedOnClass(major) {
      switch (major) {
        case '智能制造工程':
          this.num4 = 100;
          break;
        case '储能科学与工程':
          this.num4 = 101;
          break;
        case '环境科学与工程类':
          this.num4 = 102;
          break;
        case '化工与制药类':
          this.num4 = 103;
          break;
        case '建筑类':
          this.num4 = 104;
          break;
        case '大数据管理与应用':
          this.num4 = 105;
          break;
        case '电子信息类':
          this.num4 = 106;
          break;
        default:
          this.num4 = 200;
      }
    },
    // 显示确认弹窗
    showConfirmDialog() {
      this.showConfirmDialogFlag = true;
    },
    // 初始化问卷
    initializeQuestionnaire() {
      axios
        .get(`http://localhost:3000/api/users/${this.userName}`)
        .then(response => {
          const userData = response.data;  // 获取用户数据
          // 获取学生信息
          this.splitFlow = userData.divertForm // 分流形式
          this.studentName = userData.studentName; // 姓名
          this.studentId = userData.studentId; // 学号
          this.major = userData.major; // 招生录取专业
          this.department = userData.academy; // 书院
          this.specialty = userData.systemMajor; // 系统内专业
          this.specialClass=userData.InnovationClass;//0：不是 1：是
          this.setNumBasedOnDepartment(this.department);//得到书院对应num,用于问卷选项加载
          this.setNumBasedOnMajor(this.major);//得到专业对应num2,用于问卷选项加载
          this.setNumBasedOnSpecialty(this.specialty);//得到专业类别对应num3,用于问卷选项加载
          this.setNumBasedOnClass(this.specialty);//得到创新班专业类别对应num4,用于问卷选项加载
          this.loadQuestionnaireBySplitFlow();// 根据分流形式加载不同的问卷
          this.getLastAnswerForQuestion(questionId);
        })
        .catch(error => {
          console.error('获取用户信息失败', error); // 错误处理
        })
    },

    // 根据分流形式加载不同的问卷
    loadQuestionnaireBySplitFlow() {
      if(this.specialClass===0){
        switch (this.splitFlow) {
          case '仅可转专业':
            this.questionnaire = this.getQuestionnaireA();
            break;
          case '可类内任选，并转专业':
            this.questionnaire = this.getQuestionnaireB();
            break;
          case '可类内任选，不能转专业':
            this.questionnaire = this.getQuestionnaireC();
            break;
          case '可域内任选，并转专业':
            this.questionnaire = this.getQuestionnaireD();
            break;
          case '不可变更专业':
            this.questionnaire = this.getQuestionnaire();
            break;
          default:
            this.questionnaire = []; // 默认空的问卷
            break;
        }
      }
      else {
        this.questionnaire = this.getQuestionnaireE();
      }

      // 初始化显示第一个问题
      if (this.questionnaire.length > 0) {
        this.currentDisplay.push(this.questionnaire[0]);
      }
    },
    //不可变更专业
    getQuestionnaire() {
      return [
        {
          id: 1,
          text: '您的分流形式是 [单选题]',
          options: [
            {id: 1, text: '不可变更专业', next: null},
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ];
    },
    // 只可转专业的问卷数据
    getQuestionnaireA() {
      this.filter=1;
      return [
        {
          id: 1,
          text: '满足相应要求，是否决定转专业 [单选题]',
          options: [
            {id: 1, text: '是', next: 2},
            {id: 2, text: '否', next: null},
          ]
        },
        {
          id: 2,
          text: '意向书院学域 [单选题]',
          options: [
            {id: 1, text: '大煜书院', next: 3},
            {id: 2, text: '伯川书院', next: 4},
            {id: 3, text: '笃学书院', next: 5},
            {id: 4, text: '令希书院', next: 6},
            {id: 5, text: '厚德书院', next: 7},
            {id: 6, text: '知行书院', next: 8},
            {id: 7, text: '求实书院', next: 9}
          ]
        },
        {
          id: 3,
          text: '大煜书院——物质创造学域 意向专业 [单选题]',
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
          text: '伯川书院——智能制造学域 意向专业 [单选题]',
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
          text: '笃学书院——理科强基学域 意向专业 [单选题]',
          options: [
            {id: 1, text: '数学与应用数学', next: null},
            {id: 2, text: '信息科学与计算科学', next: null}
          ]
        },
        {
          id: 6,
          text: '令希书院——智能建造学域 意向专业 [单选题]',
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
          text: '厚德书院——人文社科学域 意向专业 [单选题]',
          options: [
            {id: 1, text: '金融学', next: null},
            {id: 2, text: '工商管理', next: null},
            {id: 3, text: '物流管理', next: null},
            {id: 4, text: '国际经济与贸易', next: null},
            {id: 5, text: '知识产权', next: null},
            {id: 6, text: '公共事业管理、公共事业管理', next: null},
            {id: 7, text: '马克思主义理论', next: null},
            {id: 8, text: '哲学', next: null},
            {id: 9, text: '广播电视学', next: null},
            {id: 10, text: '汉语言文学', next: null},
            {id: 11, text: '英语', next: null},
            {id: 12, text: '翻译', next: null},
            {id: 13, text: '日语', next: null},
            {id: 14, text: '建筑学', next: null},
            {id: 15, text: '城乡规划', next: null},
            {id: 16, text: '工业设计', next: null},
            {id: 17, text: '视觉传达设计', next: null},
            {id: 18, text: '环境设计', next: null},
            {id: 19, text: '雕塑', next: null},
            {id: 20, text: '运动训练', next: null}
          ]
        },
        {
          id: 8,
          text: '知行书院——信息技术学域（一） 意向专业 [单选题]',
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
          text: '求实书院——信息技术学域（二） 意向专业 [单选题]',
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
      ]
    },

    //类内任选，并转专业 类型的问卷数据
    getQuestionnaireB() {
      this.filter=2;
      return [
        {
          id: 1,
          text: '您的分流形式是 [单选题] ',
          options: [
            {id: 1, text: '类内任选专业，并转专业', next: this.num2},
          ]
        },
        {
          id: 20,
          text: '是否选择转专业 [单选题] ',
          options: [
            {id: 1, text: '是', next: 2},
            {id: 2, text: '否', next: null},
          ]
        },
        {
          id: 2,
          text: '如果您满足转专业要求您期望转入的专业是 [单选题] ',
          options: [
            {id: 1, text: '大煜书院', next: 3},
            {id: 2, text: '伯川书院', next: 4},
            {id: 3, text: '笃学书院', next: 5},
            {id: 4, text: '令希书院', next: 6},
            {id: 5, text: '厚德书院', next: 7},
            {id: 6, text: '知行书院', next: 8},
            {id: 7, text: '求实书院', next: 9}
          ]
        },
        {
          id: 3,
          text: '大煜书院——物质创造学域 意向专业 [单选题] ',
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
          text: '伯川书院——智能制造学域 意向专业 [单选题] ',
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
          text: '笃学书院——理科强基学域 意向专业 [单选题] ',
          options: [
            {id: 1, text: '数学与应用数学', next: null},
            {id: 2, text: '信息科学与计算科学', next: null}
          ]
        },
        {
          id: 6,
          text: '令希书院——智能建造学域 意向专业 [单选题] ',
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
          text: '厚德书院——人文社科学域 意向专业 [单选题] ',
          options: [
            {id: 1, text: '金融学', next: null},
            {id: 2, text: '工商管理', next: null},
            {id: 3, text: '物流管理', next: null},
            {id: 4, text: '国际经济与贸易', next: null},
            {id: 5, text: '知识产权', next: null},
            {id: 6, text: '公共事业管理、公共事业管理', next: null},
            {id: 7, text: '马克思主义理论', next: null},
            {id: 8, text: '哲学', next: null},
            {id: 9, text: '广播电视学', next: null},
            {id: 10, text: '汉语言文学', next: null},
            {id: 11, text: '英语', next: null},
            {id: 12, text: '翻译', next: null},
            {id: 13, text: '日语', next: null},
            {id: 14, text: '建筑学', next: null},
            {id: 15, text: '城乡规划', next: null},
            {id: 16, text: '工业设计', next: null},
            {id: 17, text: '视觉传达设计', next: null},
            {id: 18, text: '环境设计', next: null},
            {id: 19, text: '雕塑', next: null},
            {id: 20, text: '运动训练', next: null}
          ]
        },
        {
          id: 8,
          text: '知行书院——信息技术学域（一） 意向专业 [单选题] ',
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
          text: '求实书院——信息技术学域（二）意向专业 [单选题] ',
          options: [
            {id: 1, text: '软件工程', next: null},
            {id: 2, text: '网络工程', next: null},
            {id: 3, text: '集成电路设计与集成系统', next: null},
            {id: 4, text: '电子科学与技术', next: null}
          ]
        },
        {
          id: 10,
          text: '材料类 意向专业 [单选题] ',
          options: [
            {id: 111, text: '金属材料工程', next: 20},
            {id: 222, text: '功能材料', next: 20},
            {id: 333, text: '材料成型及控制工程', next: 20}
          ]
        },
        {
          id: 11,
          text: '管理科学与工程类 意向专业 [单选题] ',
          options: [
            {id: 111, text: '大数据管理与应用', next: 20},
            {id: 222, text: '信息管理与信息系统', next: 20}
          ]
        },
        {
          id: 12,
          text: '化工与制药类 意向专业 [单选题] ',
          options: [
            {id: 111, text: '化学工程与工艺', next: 20},
            {id: 222, text: '精细化工', next: 20},
            {id: 333, text: '制药工程', next: 20},
            {id: 444, text: '高分子材料与工程', next: 20},
            {id: 555, text: '安全工程', next: 20}
          ]
        },
        {
          id: 13,
          text: '环境科学与工程类 意向专业 [单选题] ',
          options: [
            {id: 111, text: '环境科学', next: 20},
            {id: 222, text: '环境工程', next: 20}
          ]
        },
        {
          id: 14,
          text: '新闻传播学类 意向专业 [单选题] ',
          options: [
            {id: 111, text: '广播电视学', next: 20},
            {id: 222, text: '汉语言文学', next: 20}
          ]
        },
        {
          id: 15,
          text: '智能建造 意向专业 [单选题] ',
          options: [
            {id: 111, text: '智能建造', next: 20},
            {id: 222, text: '水利水电工程', next: 20},
            {id: 333, text: '港口航道与海岸工程', next: 20},
            {id: 444, text: '海洋资源开发技术', next: 20},
            {id: 555, text: '交通工程', next: 20},
            {id: 666, text: '工程管理', next: 20},
            {id: 777, text: '建筑环境与能源应用工程', next: 20},
            {id: 888, text: '土木工程', next: 20}
          ]
        },
        {
          id: 16,
          text: '智能制造工程 意向专业 [单选题] ',
          options: [
            {id: 111, text: '智能制造工程', next: 20},
            {id: 222, text: '能源与动力工程', next: 20},
            {id: 333, text: '机械设计制造及其自动化', next: 20},
            {id: 444, text: '车辆工程（英语强化）', next: 20},
            {id: 555, text: '测控技术与仪器', next: 20},
            {id: 666, text: '金属材料工程', next: 20},
            {id: 777, text: '功能材料', next: 20},
            {id: 888, text: '材料成型及控制工程', next: 20},
            {id: 999, text: '生物医学工程', next: 20}
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ];
    },

    // C类型的问卷数据
    getQuestionnaireC() {
      this.filter=null;
      return [
        {
          id: 1,
          text: '如果进行类内任选，将选择的普通专业是 [单选题]',
          options: [
            {id: 1, text: '视觉传达设计', next: null},
            {id: 2, text: '环境设计', next: null},
            {id: 3, text: '雕塑', next: null},
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ];
    },

    // D类型的问卷数据
    getQuestionnaireD() {
      console.log(this.num3)
      this.filter=3;
      if(this.department==='厚德书院') this.filter=4;
      return [
        {
          id: 1,
          text: '您的分流形式是 [单选题]',
          options: [
            {id: 1, text: '保持现有专业', next: 20},
            {id: 2, text: '域内任选专业，并转专业', next: this.num3},
          ]
        },
        {
          id: 20,
          text: '是否选择转专业 [单选题]',
          options: [
            {id: 1, text: '是', next: 2},
            {id: 2, text: '否', next: null},
          ]
        },
        {
          id: 2,
          text: '如果您满足转专业要求您期望转入的专业是 [单选题]',
          options: [
            {id: 1, text: '大煜书院', next: 14},
            {id: 2, text: '伯川书院', next: 15},
            {id: 3, text: '笃学书院', next: 16},
            {id: 4, text: '令希书院', next: 17},
            {id: 5, text: '厚德书院', next: 18},
            {id: 6, text: '知行书院', next: 19},
            {id: 7, text: '求实书院', next: 21}
          ]
        },
        {
          id: 3,
          text: '请在 大煜书院——物质创造学域 任选专业 [单选题]',
          options: [
            {id: 1, text: '化学工程与工艺', next: 20},
            {id: 2, text: '精细化工', next: 20},
            {id: 3, text: '制药工程', next: 20},
            {id: 4, text: '高分子材料与工程', next: 20},
            {id: 5, text: '安全工程', next: 20},
            {id: 6, text: '过程装备与控制工程', next: 20},
            {id: 7, text: '环境工程', next: 20},
            {id: 8, text: '环境科学', next: 20},
            {id: 9, text: '生物工程', next: 20}
          ]
        },
        {
          id: 4,
          text: '请在 伯川书院——智能制造学域 任选专业 [单选题]',
          options: [
            {id: 1, text: '智能制造工程', next: 20},
            {id: 2, text: '能源与动力工程', next: 20},
            {id: 3, text: '机械设计制造及其自动化', next: 20},
            {id: 4, text: '车辆工程（英语强化）', next: 20},
            {id: 5, text: '测控技术与仪器', next: 20},
            {id: 6, text: '金属材料工程', next: 20},
            {id: 7, text: '功能材料', next: 20},
            {id: 8, text: '材料成型及控制工程', next: 20},
            {id: 9, text: '生物医学工程', next: 20}
          ]
        },
        {
          id: 5,
          text: '请在 笃学书院——理科强基学域 任选专业 [单选题]',
          options: [
            {id: 1, text: '数学与应用数学', next: 20},
            {id: 2, text: '信息科学与计算科学', next: 20}
          ]
        },
        {
          id: 6,
          text: '请在 令希书院——智能建造学域 任选专业[单选题]',
          options: [
            {id: 1, text: '工程力学', next: 20},
            {id: 2, text: '飞行器设计与工程', next: 20},
            {id: 3, text: '船舶与海洋工程', next: 20},
            {id: 4, text: '智能建造', next: 20},
            {id: 5, text: '水利水电工程', next: 20},
            {id: 6, text: '港口航道与海岸工程', next: 20},
            {id: 7, text: '海洋资源开发技术', next: 20},
            {id: 8, text: '交通工程', next: 20},
            {id: 9, text: '工程管理', next: 20},
            {id: 10, text: '建筑环境与能源应用工程', next: 20},
            {id: 11, text: '土木工程', next: 20}
          ]
        },
        {
          id: 7,
          text: '请在 厚德书院——人文社科学域 任选专业 [单选题]',
          options: [
            {id: 5, text: '知识产权', next: 20},
            {id: 6, text: '公共事业管理、公共事业管理', next: 20},
            {id: 7, text: '马克思主义理论', next: 20},
            {id: 8, text: '哲学', next: 20},
            {id: 9, text: '广播电视学', next: 20},
            {id: 10, text: '汉语言文学', next: 20},
            {id: 11, text: '英语', next: 20},
            {id: 12, text: '翻译', next: 20},
            {id: 13, text: '日语', next: 20},
            {id: 14, text: '建筑学', next: 20},
            {id: 15, text: '城乡规划', next: 20},
            {id: 16, text: '工业设计', next: 20},
            {id: 17, text: '视觉传达设计', next: 20},
            {id: 18, text: '环境设计', next: 20},
            {id: 19, text: '雕塑', next: 20},
            {id: 20, text: '运动训练', next: 20}
          ]
        },
        {
          id: 8,
          text: '请在 知行书院——信息技术学域（一） 任选专业 [单选题]',
          options: [
            {id: 1, text: '电气工程及其自动化', next: 20},
            {id: 2, text: '自动化', next: 20},
            {id: 3, text: '电子信息工程', next: 20},
            {id: 4, text: '计算机科学与技术', next: 20},
            {id: 5, text: '生物医学工程', next: 20},
            {id: 6, text: '光电信息科学与工程', next: 20},
            {id: 7, text: '大数据管理与应用', next: 20},
            {id: 8, text: '信息管理与信息系统', next: 20}
          ]
        },
        {
          id: 9,
          text: '请在 求实书院——信息技术学域（二） 任选专业 [单选题]',
          options: [
            {id: 1, text: '软件工程', next: 20},
            {id: 2, text: '网络工程', next: 20},
            {id: 3, text: '集成电路设计与集成系统', next: 20},
            {id: 4, text: '电子科学与技术', next: 20}
          ]
        },
        {
          id: 10,
          text: '请在 工商管理类 任选专业 [单选题]',
          options: [
            {id: 111, text: '金融学', next: 20},
            {id: 222, text: '工商管理', next: 20},
            {id: 333, text: '物流管理', next: 20},
            {id: 444, text: '国际经济与贸易', next: 20},
          ]
        },
        {
          id: 11,
          text: '请在 人文社科学域 任选专业 [单选题]',
          options: [
            {id: 111, text: '知识产权', next: 20},
            {id: 222, text: '公共事业管理', next: 20},
            {id: 333, text: '马克思主义理论', next: 20},
            {id: 444, text: '哲学', next: 20},
            {id: 555, text: '广播电视学', next: 20},
            {id: 666, text: '汉语言文学', next: 20},
          ]
        },
        {
          id: 12,
          text: '请在 英语、日语类 任选专业 [单选题]',
          options: [
            {id: 111, text: '英语', next: 20},
            {id: 222, text: '翻译', next: 20},
            {id: 333, text: '日语', next: 20},
          ]
        },
        {
          id: 13,
          text: '请在 建筑类 任选专业 [单选题]',
          options: [
            {id: 111, text: '建筑学', next: 20},
            {id: 222, text: '城乡规划', next: 20},
            {id: 333, text: '工业设计', next: 20},
          ]
        },
        {
          id: 14,
          text: '请在 大煜书院——物质创造学域 任选专业 [单选题]',
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
          id: 15,
          text: '请在 伯川书院——智能制造学域 任选专业 [单选题]',
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
          id: 16,
          text: '请在 笃学书院——理科强基学域 任选专业 [单选题]',
          options: [
            {id: 1, text: '数学与应用数学', next: null},
            {id: 2, text: '信息科学与计算科学', next: null}
          ]
        },
        {
          id: 17,
          text: '请在 令希书院——智能建造学域 任选专业[单选题]',
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
          id: 18,
          text: '请在 厚德书院——人文社科学域 任选专业 [单选题]',
          options: [
            {id: 1, text: '金融学', next: null},
            {id: 2, text: '工商管理', next: null},
            {id: 3, text: '物流管理', next: null},
            {id: 4, text: '国际经济与贸易', next: null},
            {id: 5, text: '知识产权', next: null},
            {id: 6, text: '公共事业管理、公共事业管理', next: null},
            {id: 7, text: '马克思主义理论', next: null},
            {id: 8, text: '哲学', next: null},
            {id: 9, text: '广播电视学', next: null},
            {id: 10, text: '汉语言文学', next: null},
            {id: 11, text: '英语', next: null},
            {id: 12, text: '翻译', next: null},
            {id: 13, text: '日语', next: null},
            {id: 14, text: '建筑学', next: null},
            {id: 15, text: '城乡规划', next: null},
            {id: 16, text: '工业设计', next: null},
            {id: 17, text: '视觉传达设计', next: null},
            {id: 18, text: '环境设计', next: null},
            {id: 19, text: '雕塑', next: null},
            {id: 20, text: '运动训练', next: null}
          ]
        },
        {
          id: 19,
          text: '请在 知行书院——信息技术学域（一） 任选专业 [单选题]',
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
          id: 21,
          text: '请在 求实书院——信息技术学域（二） [单选题]',
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
      ];
    },

    // E类型的问卷数据（针对于创新班/拔尖班的学生）
    getQuestionnaireE() {
      //只过滤部分信息
      this.filter=3;
      return [
        {
          id: 1,
          text: '您的分流形式是 [单选题] *',
          options: [
            {id: 1, text: '域内任选专业，并转专业', next: 50},
          ]
        },
        {
          id: 50,
          text: '您是否放弃现有拔尖班、创新班身份选择普通专业 [单选题] *',
          options: [
            {id: 1, text: '是', next: this.num},
            {id: 2, text: '否', next: this.num4},//定制的num4
          ]
        },
        {
          id: 20,
          text: '是否选择转专业 [单选题] *',
          options: [
            {id: 1, text: '是', next: 2},
            {id: 2, text: '否', next: null},
          ]
        },
        {
          id: 2,
          text: '如果您满足转专业要求您期望转入的专业是 [单选题] *',
          options: [
            {id: 1, text: '大煜书院', next: 13},
            {id: 2, text: '伯川书院', next: 14},
            {id: 3, text: '笃学书院', next: 15},
            {id: 4, text: '令希书院', next: 16},
            {id: 5, text: '厚德书院', next: 17},
            {id: 6, text: '知行书院', next: 18},
            {id: 7, text: '求实书院', next: 19}
          ]
        },
        {
          id: 3,
          text: '大煜书院——物质创造学域 意向专业 [单选题] *',
          options: [
            {id: 1, text: '化学工程与工艺', next: 20},
            {id: 2, text: '精细化工', next: 20},
            {id: 3, text: '制药工程', next: 20},
            {id: 4, text: '高分子材料与工程', next: 20},
            {id: 5, text: '安全工程', next: 20},
            {id: 6, text: '过程装备与控制工程', next: 20},
            {id: 7, text: '环境工程', next: 20},
            {id: 8, text: '环境科学', next: 20},
            {id: 9, text: '生物工程', next: 20}
          ]
        },
        {
          id: 4,
          text: '伯川书院——智能制造学域 意向专业 [单选题] *',
          options: [
            {id: 1, text: '智能制造工程', next: 20},
            {id: 2, text: '能源与动力工程', next: 20},
            {id: 3, text: '机械设计制造及其自动化', next: 20},
            {id: 4, text: '车辆工程（英语强化）', next: 20},
            {id: 5, text: '测控技术与仪器', next: 20},
            {id: 6, text: '金属材料工程', next: 20},
            {id: 7, text: '功能材料', next: 20},
            {id: 8, text: '材料成型及控制工程', next: 20},
            {id: 9, text: '生物医学工程', next: 20}
          ]
        },
        {
          id: 5,
          text: '笃学书院——理科强基学域 意向专业 [单选题] *',
          options: [
            {id: 1, text: '数学与应用数学', next: 20},
            {id: 2, text: '信息科学与计算科学', next: 20}
          ]
        },
        {
          id: 6,
          text: '令希书院——智能建造学域 意向专业 [单选题] *',
          options: [
            {id: 1, text: '工程力学', next: 20},
            {id: 2, text: '飞行器设计与工程', next: 20},
            {id: 3, text: '船舶与海洋工程', next: 20},
            {id: 4, text: '智能建造', next: 20},
            {id: 5, text: '水利水电工程', next: 20},
            {id: 6, text: '港口航道与海岸工程', next: 20},
            {id: 7, text: '海洋资源开发技术', next: 20},
            {id: 8, text: '交通工程', next: 20},
            {id: 9, text: '工程管理', next: 20},
            {id: 10, text: '建筑环境与能源应用工程', next: 20},
            {id: 11, text: '土木工程', next: 20}
          ]
        },
        {
          id: 7,
          text: '厚德书院——人文社科学域 意向专业 [单选题] *',
          options: [
            {id: 1, text: '金融学', next: 20},
            {id: 2, text: '工商管理', next: 20},
            {id: 3, text: '物流管理', next: 20},
            {id: 4, text: '国际经济与贸易', next: 20},
            {id: 5, text: '知识产权', next: 20},
            {id: 6, text: '公共事业管理、公共事业管理', next: 20},
            {id: 7, text: '马克思主义理论', next: 20},
            {id: 8, text: '哲学', next: 20},
            {id: 9, text: '广播电视学', next: 20},
            {id: 10, text: '汉语言文学', next: 20},
            {id: 11, text: '英语', next: 20},
            {id: 12, text: '翻译', next: 20},
            {id: 13, text: '日语', next: 20},
            {id: 14, text: '建筑学', next: 20},
            {id: 15, text: '城乡规划', next: 20},
            {id: 16, text: '工业设计', next: 20},
            {id: 17, text: '视觉传达设计', next: 20},
            {id: 18, text: '环境设计', next:20},
            {id: 19, text: '雕塑', next: 20},
            {id: 20, text: '运动训练', next: 20}
          ]
        },
        {
          id: 8,
          text: '知行书院——信息技术学域（一） 意向专业 [单选题] *',
          options: [
            {id: 1, text: '电气工程及其自动化', next: 20},
            {id: 2, text: '自动化', next: 20},
            {id: 3, text: '电子信息工程', next: 20},
            {id: 4, text: '计算机科学与技术', next: 20},
            {id: 5, text: '生物医学工程', next: 20},
            {id: 6, text: '光电信息科学与工程', next: 20},
            {id: 7, text: '大数据管理与应用', next: 20},
            {id: 8, text: '信息管理与信息系统', next: 20}
          ]
        },
        {
          id: 9,
          text: '求实书院——信息技术学域（二）意向专业 [单选题] *',
          options: [
            {id: 1, text: '软件工程', next: 20},
            {id: 2, text: '网络工程', next: 20},
            {id: 3, text: '集成电路设计与集成系统', next: 20},
            {id: 4, text: '电子科学与技术', next: 20}
          ]
        },
        {
          id: 13,
          text: '大煜书院——物质创造学域 意向专业 [单选题] *',
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
          id: 14,
          text: '伯川书院——智能制造学域 意向专业 [单选题] *',
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
          id: 15,
          text: '笃学书院——理科强基学域 意向专业 [单选题] *',
          options: [
            {id: 1, text: '数学与应用数学', next: null},
            {id: 2, text: '信息科学与计算科学', next: null}
          ]
        },
        {
          id: 16,
          text: '令希书院——智能建造学域 意向专业 [单选题] *',
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
          id: 17,
          text: '厚德书院——人文社科学域 意向专业 [单选题] *',
          options: [
            {id: 1, text: '金融学', next: null},
            {id: 2, text: '工商管理', next: null},
            {id: 3, text: '物流管理', next: null},
            {id: 4, text: '国际经济与贸易', next: null},
            {id: 5, text: '知识产权', next: null},
            {id: 6, text: '公共事业管理、公共事业管理', next: null},
            {id: 7, text: '马克思主义理论', next: null},
            {id: 8, text: '哲学', next: null},
            {id: 9, text: '广播电视学', next: null},
            {id: 10, text: '汉语言文学', next: null},
            {id: 11, text: '英语', next: null},
            {id: 12, text: '翻译', next: null},
            {id: 13, text: '日语', next: null},
            {id: 14, text: '建筑学', next: null},
            {id: 15, text: '城乡规划', next: null},
            {id: 16, text: '工业设计', next: null},
            {id: 17, text: '视觉传达设计', next: null},
            {id: 18, text: '环境设计', next: null},
            {id: 19, text: '雕塑', next: null},
            {id: 20, text: '运动训练', next: null}
          ]
        },
        {
          id: 18,
          text: '知行书院——信息技术学域（一） 意向专业 [单选题] *',
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
          id: 19,
          text: '求实书院——信息技术学域（二）意向专业 [单选题] *',
          options: [
            {id: 1, text: '软件工程', next: null},
            {id: 2, text: '网络工程', next: null},
            {id: 3, text: '集成电路设计与集成系统', next: null},
            {id: 4, text: '电子科学与技术', next: null}
          ]
        },
        {
          id: 100,
          text: '请选择您所在拔尖班、创新班的内设专业 [单选题] *',
          options: [
            {id: 1, text: '智能制造工程', next: 20},
            {id: 2, text: '车辆工程（英语强化）', next: 20},
            {id: 3, text: '测控技术与仪器', next: 20},
            {id: 4, text: '机械设计制造及其自动化', next: 20},
          ]
        },
        {
          id: 101,
          text: '请选择 储能科学与工程(新工科拔尖班) 的内设专业 [单选题] *',
          options: [
            {id: 1, text: '储能科学与工程', next: 20},
            {id: 2, text: '过程装备与控制工程', next: 20},
            {id: 3, text: '化学工程与工艺', next: 20},
            {id: 4, text: '精细化工', next: 20},
          ]
        },
        {
          id: 102,
          text: '请选择 环境科学与工程类(新工科拔尖班) 的内设专业 [单选题] *',
          options: [
            {id: 1, text: '环境科学', next: 20},
            {id: 2, text: '环境工程', next: 20},
          ]
        },
        {
          id: 103,
          text: '请选择 化工与制药类(创新班) 的内设专业 [单选题] *',
          options: [
            {id: 1, text: '化学工程与工艺', next: 20},
            {id: 2, text: '制药工程', next: 20},
            {id: 3, text: '高分子材料与工程', next: 20},
            {id: 4, text: '安全工程', next: 20},
          ]
        },
        {
          id: 104,
          text: '请选择 建筑类(新工科拔尖班) 的内设专业 [单选题] *',
          options: [
            {id: 1, text: '建筑学', next: 20},
            {id: 2, text: '城乡规划', next: 20},
          ]
        },
        {
          id: 105,
          text: '请选择 大数据管理与应用(新工科拔尖班) 的内设专业 [单选题] *',
          options: [
            {id: 1, text: '大数据管理与应用', next: 20},
            {id: 2, text: '信息管理与信息系统', next: 20},
          ]
        },
        {
          id: 106,
          text: '请选择 电子信息类(创新班) 的内设专业 [单选题] *',
          options: [
            {id: 1, text: '电气工程及其自动化', next: 20},
            {id: 2, text: '自动化', next: 20},
            {id: 3, text: '电子信息工程', next: 20},
            {id: 4, text: '计算机科学与技术', next: 20},
            {id: 5, text: '生物医学工程', next: 20},
            {id: 6, text: '光电信息科学与工程', next: 20},
          ]
        },
        {
          id: 200,
          text: '请选择 '+this.major +' 的内设专业 [单选题] *',
          options: [
            {id: 1, text: this.major, next: 20},
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ];
    },

    // 处理选项点击事件
    handleOptionClick(option, index) {
      //记录最后一次点击的内容
      this.finalAnswerText = option.text;
      this.finalAnswerText3 = option.text;
      this.$set(this.selectedOptions, index, option.id);
      this.completed = option.next === null;
      this.currentDisplay = this.currentDisplay.slice(0, index + 1);

      // 将选择的详细信息存入 userSelections
      const question = this.currentDisplay[index];
      this.userSelections[index] = {
        questionId: question.id,
        questionText: question.text,
        selectedOptionId: option.id,
        selectedOptionText: option.text,
      };
      if (option.next !== null) {
        const nextQuestion = this.questionnaire.find(q => q.id === option.next);
        if (nextQuestion) {
          this.currentDisplay.push(nextQuestion);
        }
      }
    },

    // 提交问卷
    submitQuestionnaire() {
      this.showConfirmDialogFlag = false; // 关闭确认弹窗
      this.showEndMessage = true; // 显示结束弹窗
      if(this.splitFlow==="不可变更专业"){
        this.finalAnswerText=this.major;
        this.finalAnswerText2 = '否';
        this.finalAnswerText3='否';
        this.finalAnswerText4=1;
      }
      //第一种问卷(测试完成)
      else if (this.finalAnswerText === "否"&&this.splitFlow==="仅可转专业") {
        this.finalAnswerText = this.major;
        this.finalAnswerText2 = '否';
        this.finalAnswerText3='否';
        this.finalAnswerText4=1;
      }else if(this.splitFlow==="仅可转专业"){
        this.finalAnswerText = this.major;
        this.finalAnswerText2 = this.getLastAnswerForQuestion(2).selectedOptionText;
        this.finalAnswerText4=5;
      }

      //第二种问卷（测试完成）
      else if(this.splitFlow==="可类内任选，并转专业"&&this.finalAnswerText==="否"){
        this.finalAnswerText=this.getLastAnswerForQuestion(this.num2).selectedOptionText;
        this.finalAnswerText2='否';
        this.finalAnswerText3='否';
        this.finalAnswerText4=3;
      }
      else if(this.splitFlow==="可类内任选，并转专业"){//转专业
        this.finalAnswerText=this.getLastAnswerForQuestion(this.num2).selectedOptionText;
        this.finalAnswerText2 = this.getLastAnswerForQuestion(2).selectedOptionText;
        this.finalAnswerText4=5;
      }


      //第三种问卷（测试完成）
      else if(this.splitFlow==="可类内任选，不能转专业"){
        this.finalAnswerText2='否';
        this.finalAnswerText3='否'
        this.finalAnswerText4=3;
      }

      //第四种问卷（测试完成）
      else if(this.splitFlow==="可域内任选，并转专业"&&this.finalAnswerText==="否"&&this.getLastAnswerForQuestion(1).selectedOptionText==="保持现有专业"&&this.specialClass===0){
        this.finalAnswerText=this.major;
        this.finalAnswerText2='否';
        this.finalAnswerText3='否';
        this.finalAnswerText4=1;
      }
      else if(this.splitFlow==="可域内任选，并转专业"&&this.getLastAnswerForQuestion(1).selectedOptionText==="保持现有专业"&&this.specialClass===0){
        this.finalAnswerText=this.major;
        this.finalAnswerText2=this.getLastAnswerForQuestion(2).selectedOptionText;
        this.finalAnswerText4=5;
      }
      else if(this.splitFlow==="可域内任选，并转专业"&&this.finalAnswerText==="否"&&this.specialClass===0){//不转专业
        this.finalAnswerText=this.getLastAnswerForQuestion(this.num3).selectedOptionText;
        this.finalAnswerText2='否';
        this.finalAnswerText4=2;
      }
      else if(this.splitFlow==="可域内任选，并转专业"&&this.specialClass===0){
        this.finalAnswerText=this.getLastAnswerForQuestion(this.num3).selectedOptionText;
        this.finalAnswerText2=this.getLastAnswerForQuestion(2).selectedOptionText;
        this.finalAnswerText4=5;
      }

      //第五种问卷
      else if(this.specialClass===1&&this.finalAnswerText==="否"){//不转专业
        if(this.getLastAnswerForQuestion(50).selectedOptionText==="否"){//不放弃创新班身份
          this.finalAnswerText=this.getLastAnswerForQuestion(this.num4).selectedOptionText;
          this.finalAnswerText2='否';
          this.finalAnswerText3='否';
          this.finalAnswerText4=4;
        }else{//放弃创新班身份
          this.finalAnswerText=this.getLastAnswerForQuestion(this.num3).selectedOptionText;
          this.finalAnswerText2='否';
          this.finalAnswerText3='否';
          this.finalAnswerText4=2;
        }
      }
      else if(this.specialClass===1){
        if(this.getLastAnswerForQuestion(50).selectedOptionText==="否"){//不放弃创新班身份
          this.finalAnswerText=this.getLastAnswerForQuestion(this.num4).selectedOptionText;
          this.finalAnswerText2=this.getLastAnswerForQuestion(2).selectedOptionText;
          this.finalAnswerText4=5;
        }else{//放弃创新班身份
          this.finalAnswerText=this.getLastAnswerForQuestion(this.num3).selectedOptionText;
          this.finalAnswerText2=this.getLastAnswerForQuestion(2).selectedOptionText;
          this.finalAnswerText4=5;
        }
      }

      // 提交最后一个问题的答案到后台
      axios
        .post('http://localhost:3000/submit', {
          user_name: this.userName, // 用户名称
          questionnaire_id: 1, // 问卷编号
          change_adress:this.department ,//提交问卷的书院
          change_major: this.finalAnswerText, // 只提交最后一个问题的答案
          after_change_adress:this.finalAnswerText2,
          after_change_major:this.finalAnswerText3,
          change_major_type:this.finalAnswerText4
        })
        .then(response => {
          console.log('提交成功:', response.data);
          setTimeout(() => {
            // 跳转回问卷页面
            this.$router.go(-1);
          }, 2000); // 2秒后跳转
        })
        .catch(error => {
          console.error('提交失败:', error);
        });
      if(this.finalAnswerText2!=='否'){
        axios
          .post('http://localhost:3000/updateStudent', {
            studentId: this.userName,
            afterMajor: this.finalAnswerText3,
            afterAcademy: this.finalAnswerText2,
            change_major_type:this.finalAnswerText4
          })
          .catch(error => {
            console.error('提交失败:', error);
          });
      }
      else{
        axios
          .post('http://localhost:3000/updateStudent', {
            studentId: this.userName,
            afterMajor: this.finalAnswerText,
            afterAcademy: this.department,
            change_major_type:this.finalAnswerText4
          })
          .catch(error => {
            console.error('提交失败:', error);
          });
      }
    },
    // 提交最后一个问题的答案到后台
    //过滤问卷答案
    getLastAnswerForQuestion(questionId) {
      // 过滤出所有 questionId=2 的记录
      const filteredAnswers = this.userSelections.filter(selection => selection.questionId === questionId);

      // 如果找到记录，返回最后一个；否则返回 null
      return filteredAnswers.length > 0 ? filteredAnswers[filteredAnswers.length - 1] : null;
    },

    // 关闭弹窗
    closeModal() {
      this.showEndMessage = false;
    },

    // 取消提交
    handleCancel() {
      this.showConfirmDialogFlag = false;
    },

    getFilteredOptions(options) {
      // 根据 department 和 major 字段来过滤选项，隐藏含有 department 或 major 的选项
      if (this.major.includes('类')||this.department==='厚德书院') {
        options=options.filter(option => option.text !== '保持现有专业');
      }
      return options.filter(option => !this.isOptionHidden(option));
    },

    isOptionHidden(option) {
      // 如果选项的文本包含当前的 department 或 major，返回 true（表示隐藏该选项）
      if (this.filter === 1) {
        return option.text.includes(this.major)
      } else if (this.filter === 2 && !['111', '222', '333', '444', '555', '666', '777', '888', '999'].includes(option.id.toString())) {
        switch (this.num2) {
          case 10:
            return ['金属材料工程', '功能材料', '材料成型及控制工程'].some(text => option.text.includes(text));
          case 11:
            return ['大数据管理与应用', '信息管理与信息系统'].some(text => option.text.includes(text));
          case 12:
            return ['化学工程与工艺', '精细化工', '制药工程', '高分子材料与工程', '安全工程'].some(text => option.text.includes(text));
          case 13:
            return ['环境科学', '环境工程'].some(text => option.text.includes(text));
          case 14:
            return ['广播电视学', '汉语言文学'].some(text => option.text.includes(text));
          case 15:
            return ['智能建造', '水利水电工程', '港口航道与海岸工程', '海洋资源开发技术', '交通工程', '工程管理', '建筑环境与能源应用工程', '土木工程'].some(text => option.text.includes(text));
          case 16:
            return ['智能制造工程', '能源与动力工程', '机械设计制造及其自动化', '车辆工程（英语强化）', '测控技术与仪器', '金属材料工程', '功能材料', '材料成型及控制工程', '生物医学工程'].some(text => option.text.includes(text));
        }
      }
      else if(this.filter===3){
        return option.text.includes(this.department);
      }
      else if(this.filter===4&& !['111', '222', '333', '444', '555', '666'].includes(option.id.toString())){
        switch (this.num3) {
          case 10:
            return ['金融学', '工商管理', '物流管理', '国际经济与贸易'].some(text => option.text.includes(text));
          case 11:
            return ['知识产权', '公共事业管理', '马克思主义理论', '哲学', '广播电视学', '汉语言文学'].some(text => option.text.includes(text));
          case 12:
            return ['英语', '翻译', '日语'].some(text => option.text.includes(text));
          case 13:
            return ['建筑学', '城乡规划', '工业设计'].some(text => option.text.includes(text));
        }
      }

    },

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
  background-color: #f4f7fc;
  border-radius: 8px;
  font-family: "Arial", sans-serif;
}

/* 每个问题的容器 */
.question-container {
  margin-bottom: 20px; /* 每个问题间距 */
  padding: 15px;
  background-color: #ffffff; /* 背景色 */
  border-radius: 10px; /* 圆角 */
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
  background: linear-gradient(to right, #2b76de, #395cdc); /* 反转渐变颜色 */
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
