<template>
  <div class="questionnaire">
    <!-- 过滤选择界面 -->
    <div v-if="showFilterPage" class="filter-page">
      <div class="filter-container">
        <h2 class="filter-title">请选择以下信息</h2>

        <!-- 书院选择 -->
        <div class="filter-row">
          <label class="filter-label">书院：</label>
          <el-select
            v-model="selectedAcademy"
            placeholder="请选择书院"
            class="filter-select"
            @change="onAcademyChange"
          >
            <el-option
              v-for="academy in academyOptions"
              :key="academy"
              :label="academy"
              :value="academy"
            />
          </el-select>
        </div>

        <!-- 专业选择 -->
        <div class="filter-row">
          <label class="filter-label">专业：</label>
          <el-select
            v-model="selectedMajor"
            placeholder="请选择专业"
            class="filter-select"
            :disabled="!selectedAcademy"
            @change="onMajorChange"
          >
            <el-option
              v-for="major in majorOptions"
              :key="major"
              :label="major"
              :value="major"
            />
          </el-select>
        </div>

        <!-- 分流类型选择 -->
        <div class="filter-row">
          <label class="filter-label">分流类型：</label>
          <el-select
            v-model="selectedDivertForm"
            placeholder="请选择分流类型"
            class="filter-select"
            :disabled="!selectedMajor"
          >
            <el-option
              v-for="divertForm in divertFormOptions"
              :key="divertForm"
              :label="divertForm"
              :value="divertForm"
            />
          </el-select>
        </div>

        <!-- 操作按钮 -->
        <div class="filter-actions">
          <el-button @click="goBack" class="cancel-btn">返回问卷列表</el-button>
          <el-button
            type="primary"
            @click="confirmFilters"
            :disabled="!selectedDivertForm"
            class="confirm-btn"
          >
            确认并进入问卷
          </el-button>
        </div>
      </div>
    </div>

    <!-- 问卷界面 -->
    <div v-else class="questionnaire-form">
      <div v-for="(question, index) in currentDisplay" :key="question.id" class="question-container">
        <div class="question-box">
          <h2>{{ index + 1 }}. {{ question.text }}</h2>
          <div class="options">
            <label v-for="option in (question.options || [])" :key="option.id">
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
      <button v-if="completed" @click="submitQuestionnaire" class="submit-button">提交</button>

      <!-- 提交确认弹窗 -->
      <div v-if="showConfirmDialogFlag" class="dialog-container">
        <div class="dialog-box">
          <h2>确认提交</h2>
          <p>您确定要提交问卷吗？您的选择是：{{ this.finalAnswerText }}</p>
          <div class="dialog-footer">
            <el-button @click="handleCancel">取消</el-button>
            <el-button type="primary" @click="submitQuestionnaire" class="confirm-button">确认提交</el-button>
          </div>
        </div>
      </div>

      <!-- 提交预览弹窗 -->
      <div v-if="showPreviewDialog" class="dialog-container">
        <div class="dialog-box preview-dialog">
          <span class="close-btn" @click="closePreviewDialog">&times;</span>
          <h2>提交内容预览</h2>
          <div class="preview-content">
            <div class="preview-item">
              <span class="preview-label">用户名：</span>
              <span class="preview-value">{{ this.userName }}</span>
            </div>
            <div class="preview-item">
              <span class="preview-label">当前书院：</span>
              <span class="preview-value">{{ this.academy }}</span>
            </div>
            <div class="preview-item">
              <span class="preview-label">选择的专业：</span>
              <span class="preview-value">{{ this.finalAnswerText}}</span>
            </div>
            <div class="preview-item">
              <span class="preview-label">分流类型：</span>
              <span class="preview-value">{{ this.getMajorTypeName(this.finalAnswerText2) }}</span>
            </div>
            <div class="preview-item">
              <span class="preview-label">转专业后的书院：</span>
              <span class="preview-value">{{ this.academy2}}</span>
            </div>
            <div class="preview-item">
              <span class="preview-label">转专业后的专业：</span>
              <span class="preview-value">{{ this.finalAnswerText3}}</span>
            </div>
            <div class="preview-item">
              <span class="preview-label">转专业后的分流类型：</span>
              <span class="preview-value">{{ this.getMajorTypeName(this.finalAnswerText4) }}</span>
            </div>
          </div>
          <div class="dialog-footer">
            <el-button @click="closePreviewDialog">取消</el-button>
            <el-button type="primary" @click="confirmSubmit">确认提交</el-button>
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
import store from "@/store";
import {submitQuestionnaireData} from "@/api/system/questionnaire";
import {getStudent, listStudent} from "@/api/system/student";

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
      showPreviewDialog: false, // 提交预览弹窗的显示状态
      finalAnswerText: '', // 分流的专业选择
      finalAnswerText2: 0,//分流类型记录
      userName: store.state.user.name, // 获取用户名
      splitFlow: '', // 分流形式
      num: null, // 转换书院信息
      num2: null, //转换专业类信息
      num3: null, //转换厚德书院专业类信息
      num4: null, //转换创新班专业信息
      filter: null, //是否过滤
      userSelections: [],
      // 过滤选择相关
      showFilterPage: true, // 是否显示过滤选择页面
      selectedAcademy: '', // 选中的书院
      selectedMajor: '', // 选中的专业
      selectedDivertForm: '', // 选中的分流类型
      academyOptions: [], // 书院选项列表
      majorOptions: [], // 专业选项列表
      divertFormOptions: [], // 分流类型选项列表
      allStudentData: [], // 所有学生数据
    };
  },

  methods: {
    //过滤掉专业括号内容
    filterText(text) {
      //创新班或者是英语强化这个专业
      if(this.specialClass === 1) {
        return text;
      }
      else return text.replace(/（.*?）|\(.*?\)/g, '').trim();
    },
    // 根据num3值过滤学域选项，避免选择当前学域
    getFilteredDomainOptions() {
      const allOptions = [
        {id: 1, text: '大煜书院——物质创造学域', next: 102},
        {id: 2, text: '伯川书院——智能制造学域', next: 103},
        {id: 3, text: '笃学书院——理科强基学域', next: 104},
        {id: 4, text: '令希书院——智能建造学域', next: 105},
        {id: 5, text: '厚德书院——人文社科学域', next: 106},
        {id: 6, text: '知行书院——信息技术学域（一）', next: 107},
        {id: 7, text: '求实书院——信息技术学域（二）', next: 108}
      ];

      // 根据num3值过滤掉当前学域
      // num3 = 3: 大煜书院, num3 = 4: 伯川书院, num3 = 5: 笃学书院
      // num3 = 6: 令希书院, num3 = 7: 厚德书院, num3 = 8: 知行书院, num3 = 9: 求实书院
      // num3 = 11: 厚德书院人文社科, num3 = 12: 厚德书院英语日语, num3 = 13: 厚德书院建筑类

      if (this.num3 === 3) {
        // 大煜书院学生，过滤掉大煜书院选项
        return allOptions.filter(option => option.id !== 1);
      } else if (this.num3 === 4) {
        // 伯川书院学生，过滤掉伯川书院选项
        return allOptions.filter(option => option.id !== 2);
      } else if (this.num3 === 5) {
        // 笃学书院学生，过滤掉笃学书院选项
        return allOptions.filter(option => option.id !== 3);
      } else if (this.num3 === 6) {
        // 令希书院学生，过滤掉令希书院选项
        return allOptions.filter(option => option.id !== 4);
      } else if (this.num3 === 7 || this.num3 === 11 || this.num3 === 12 || this.num3 === 13) {
        // 厚德书院学生（包括人文社科、英语日语、建筑类），过滤掉厚德书院选项
        return allOptions.filter(option => option.id !== 5);
      } else if (this.num3 === 8) {
        // 知行书院学生，过滤掉知行书院选项
        return allOptions.filter(option => option.id !== 6);
      } else if (this.num3 === 9) {
        // 求实书院学生，过滤掉求实书院选项
        return allOptions.filter(option => option.id !== 7);
      }

      // 如果num3不匹配任何已知值，返回所有选项
      return allOptions;
    },
    //设置num1值，书院的对应值
    setNumBasedOnAcademy(academy) {
      switch (academy) {
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
          this.num = null;  // 如果academy不匹配，设置num为null
      }
    },
    //设置num2值，专业类的对应值：对应只可类内任选，不能转专业
    setNumBasedOnMajor(major) {
      switch (major) {
        case '材料类':
          this.num2 = 10;
          break;
        // case '船舶与海洋工程':
        //   this.num2 = 11;
        //   break;
        case '大数据管理与应用':
          this.num2 = 12;
          break;
        // case '过程装备与控制工程':
        //   this.num2 = 13;
        //   break;
        case '化工与制药类':
          this.num2 = 14;
          break;
        // case '环境工程':
        //   this.num2 = 15;
        //   break;
        case '建筑类':
          this.num2 = 16;
          break;
        // case '能源与动力工程':
        //   this.num2 = 17;
        //   break;
        case '设计学类':
          this.num2 = 18;
          break;
        // case '生物工程':
        //   this.num2 = 19;
        //   break;
        // case '生物医学工程':
        //   this.num2 = 20;
        //   break;
        case '智能建造':
          this.num2 = 21;
          break;
        default:
          this.num2 = 200;
      }
    },
    //设置num3值，厚德
    setNumBasedOnSpecialty(major) {
      switch (major) {
        // case '工商管理类':
        //   this.num3 = 10;
        //   break;
        case '公共事业管理':
          this.num3 = 11;
          break;
        case '哲学类':
          this.num3 = 11;
          break;
        case '新闻传播学类':
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
        case '智能制造工程（创新班）':
          this.num4 = 51;
          break;
        case '储能科学与工程（新工科拔尖班）':
          this.num4 = 52;
          break;
        // case '环境工程（新工科拔尖班）':
        //   this.num4 = 102;
        //   break;
        case '工程力学（创新班）':
          this.num4 = 53;
          break;
        case '智能分子工程（新工科拔尖班）':
          this.num4 = 54;
          break;
        case '建筑类（新工科拔尖班）':
          this.num4 = 55;
          break;
        case '大数据管理与应用（新工科拔尖班）':
          this.num4 = 56;
          break;
        case '电子信息类（创新班）':
          this.num4 = 57;
          break;
        case '智能建造（新工科拔尖班）':
          this.num4 = 58;
          break;
        case '材料类（新工科拔尖班）':
          this.num4 = 59;
          break;
        case '数学类（华罗庚数学国家基础学科拔尖计划班）':
          this.num4 = 60;
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
    async initializeQuestionnaire() {
      const response = await getStudent(this.userName);
      const studentInfo = response.studentInfo;
      this.studentName = studentInfo.studentName;
      this.academy = studentInfo.academy;
      this.major = studentInfo.major;
      this.splitFlow = studentInfo.divertForm;
      this.systemMajor = studentInfo.originalSystemMajor;
      this.specialClass = studentInfo.innovationClass;
      this.setNumBasedOnAcademy(this.academy);//得到书院对应num,用于问卷选项加载
      this.setNumBasedOnMajor(this.major);//得到专业对应num2,用于问卷选项加载
      this.setNumBasedOnSpecialty(this.major);//得到专业类别对应num3,用于问卷选项加载
      this.setNumBasedOnClass(this.major);//得到创新班专业类别对应num4,用于问卷选项加载
      this.loadQuestionnaireBySplitFlow();// 根据分流形式加载不同的问卷
      // this.getLastAnswerForQuestion(questionId);
      this.academyFilter();
    },
    // 根据分流形式加载不同的问卷：2025年没有类内任选
    loadQuestionnaireBySplitFlow() {
      if (this.specialClass === 0) {
        switch (this.splitFlow) {
          case '不可变更专业':
            this.questionnaire = this.getQuestionnaire();
            break;
          case '不参与此次专业任选':
            this.questionnaire = this.getQuestionnaireA();
            break;
          case '仅可转专业':
            this.questionnaire = this.getQuestionnaireB();
            break;
          case '可类内任选，不能转专业':
            this.questionnaire = this.getQuestionnaireC();
            break;
          case '可域内任选，并转专业':
            this.questionnaire = this.getQuestionnaireD();
            break;

        }
      } else if(this.splitFlow ==='不参与此次专业任选'){
        this.questionnaire = this.getQuestionnaireA();
      } else this.questionnaire = this.getQuestionnaireE();

      // 初始化显示第一个问题
      if (this.questionnaire.length > 0) {
        this.currentDisplay.push(this.questionnaire[0]);
      }
    },
    //不可变更专业的学生问卷
    getQuestionnaire() {
      return [
        {
          id: 1,
          text: '您的分流形式是 [单选题]',
          options: [
            {id: 1, text: '保持现有专业', next: null},
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ];
    },
    //不参与专业任选
    getQuestionnaireA() {
      return [
        {
          id: 1,
          text: '您的域内专业任选形式是 [单选题]',
          options: [
            {id: 1, text: '不参与此次专业任选', next: null},
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ];
    },
    // 仅可转专业的学生问卷
    getQuestionnaireB() {
      return [
        {
          id: 100,
          text: '是否要转专业 [单选题]',
          options: [
            {id: 1, text: '是', next: 101},
            {id: 2, text: '否', next: null},
          ]
        },
        {
          id: 101,
          text: '请选择要转入的学域 [单选题]',
          options: this.getFilteredDomainOptions()
        },
        {
          id: 102,
          text: '请在 大煜书院——物质创造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '化学工程与工艺', next: null},
            {id: 2, text: '精细化工', next: null},
            {id: 3, text: '制药工程', next: null},
            {id: 4, text: '高分子材料与工程', next: null},
            {id: 5, text: '安全工程', next: null},
            {id: 6, text: '过程装备与控制工程', next: null},
            {id: 7, text: '环境工程', next: null},
            {id: 8, text: '生物工程', next: null}
          ]
        },
        {
          id: 103,
          text: '请在 伯川书院——智能制造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能制造工程', next: null},
            {id: 2, text: '机械设计制造及其自动化', next: null},
            {id: 3, text: '智能车辆工程', next: null},
            {id: 4, text: '测控技术与仪器', next: null},
            {id: 5, text: '电子信息材料', next: null},
            {id: 6, text: '金属材料工程', next: null},
            {id: 7, text: '功能材料', next: null},
            {id: 8, text: '材料成型及控制工程', next: null},
            {id: 9, text: '能源与动力工程', next: null},
            {id: 10, text: '生物医学工程', next: null}
          ]
        },
        {
          id: 104,
          text: '请在 笃学书院——理科强基学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '数学与应用数学', next: null},
            {id: 2, text: '信息与计算科学', next: null}
          ]
        },
        {
          id: 105,
          text: '请在 令希书院——智能建造学域 选择专业[单选题]',
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
          id: 106,
          text: '请在 厚德书院——人文社科学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '知识产权', next: null},
            {id: 2, text: '公共事业管理', next: null},
            {id: 3, text: '马克思主义理论', next: null},
            {id: 4, text: '哲学', next: null},
            {id: 5, text: '广播电视学', next: null},
            {id: 6, text: '汉语言文学', next: null},
            {id: 7, text: '英语', next: null},
            {id: 8, text: '翻译', next: null},
            {id: 9, text: '日语', next: null},
            {id: 10, text: '建筑学', next: null},
            {id: 11, text: '城乡规划', next: null},
            {id: 12, text: '工业设计', next: null},
            {id: 13, text: '视觉传达设计', next: null},
            {id: 14, text: '环境设计', next: null},
            {id: 15, text: '雕塑', next: null}
          ]
        },
        {
          id: 107,
          text: '请在 知行书院——信息技术学域（一） 选择专业 [单选题]',
          options: [
            {id: 1, text: '电子信息工程', next: null},
            {id: 2, text: '计算机科学与技术', next: null},
            {id: 3, text: '未来机器人', next: null},
            {id: 4, text: '自动化', next: null},
            {id: 5, text: '电气工程及其自动化', next: null},
            {id: 6, text: '光电信息科学与工程', next: null},
            {id: 7, text: '生物医学工程', next: null},
            {id: 8, text: '大数据管理与应用', next: null},
            {id: 9, text: '信息管理与信息系统', next: null}
          ]
        },
        {
          id: 108,
          text: '请在 求实书院——信息技术学域（二） 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能无人系统技术', next: null},
            {id: 2, text: '软件工程', next: null},
            {id: 3, text: '集成电路设计与集成系统', next: null},
            {id: 4, text: '电子科学与技术', next: null},
            {id: 5, text: '电子信息材料', next: null}
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ];

    },
    //类内任选，不可转专业的学生问卷
    getQuestionnaireC() {
      this.filter = 2;
      return [
        {
          id: 1,
          text: '您的类内专业任选形式是 [单选题] ',
          options: [
            {id: 1, text: '类内任选专业，不可转专业', next: this.num2},
          ]
        },
        {
          id: 10,
          text: '请您选择 材料类 内的意向专业 [单选题] ',
          options: [
            {id: 111, text: '金属材料工程', next: null},
            {id: 222, text: '功能材料', next: null},
            {id: 333, text: '材料成型及控制工程', next: null}
          ]
        },
        {
          id: 12,
          text: '请您选择 大数据管理与应用 内的意向专业 [单选题] ',
          options: [
            {id: 111, text: '大数据管理与应用', next: null},
            {id: 222, text: '信息管理与信息系统', next: null},
          ]
        },
        {
          id: 14,
          text: '请您选择 化工与制药类 内的意向专业 [单选题] ',
          options: [
            {id: 111, text: '化学工程与工艺', next: null},
            {id: 222, text: '精细化工', next: null},
            {id: 333, text: '制药工程', next: null},
            {id: 444, text: '高分子材料与工程', next: null},
            {id: 555, text: '安全工程', next: null}
          ]
        },
        {
          id: 16,
          text: '请您选择 建筑类 内的意向专业 [单选题] ',
          options: [
            {id: 111, text: '建筑学', next: null},
            {id: 222, text: '城乡规划', next: null},
          ]
        },
        {
          id: 18,
          text: '请您选择 设计学类 内的意向专业 [单选题]',
          options: [
            {id: 1, text: '视觉传达设计', next: null},
            {id: 2, text: '环境设计', next: null},
            {id: 3, text: '雕塑', next: null},
          ]
        },
        {
          id: 21,
          text: ' 请您选择 智能建造 内的意向专业 [单选题] ',
          options: [
            {id: 111, text: '智能建造', next: null},
            {id: 222, text: '水利水电工程', next: null},
            {id: 333, text: '港口航道与海岸工程', next: null},
            {id: 444, text: '建筑环境与能源应用工程', next: null},
            {id: 555, text: '土木工程', next: null}
          ]
        },
        {
          id: 200,
          text: '请选择 ' + this.major + ' 的内设专业 [单选题]',
          options: [
            {id: 111, text: this.major, next: null},
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ];
    },
    // 域内任选的学生问卷
    getQuestionnaireD() {
      this.filter = 3;
      if (this.academy === '厚德书院') this.filter = 4;
      return [
        {
          id: 1,
          text: '您的域内专业任选形式是 [单选题]',
          options: [
            {id: 1, text: '域内任选专业，并转专业', next: this.num3},
          ]
        },
        {
          id: 3,
          text: '请在 大煜书院——物质创造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '化学工程与工艺', next: 100},
            {id: 2, text: '精细化工', next: 100},
            {id: 3, text: '制药工程', next: 100},
            {id: 4, text: '高分子材料与工程', next: 100},
            {id: 5, text: '安全工程', next: 100},
            {id: 6, text: '过程装备与控制工程', next: 100},
            {id: 7, text: '环境工程', next: 100},
            {id: 8, text: '生物工程', next: 100}
          ]
        },
        {
          id: 4,
          text: '请在 伯川书院——智能制造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能制造工程', next: 100},
            {id: 2, text: '机械设计制造及其自动化', next: 100},
            {id: 3, text: '智能车辆工程', next: 100},
            {id: 4, text: '测控技术与仪器', next: 100},
            {id: 5, text: '电子信息材料', next: 100},
            {id: 6, text: '金属材料工程', next: 100},
            {id: 7, text: '功能材料', next: 100},
            {id: 8, text: '材料成型及控制工程', next: 100},
            {id: 9, text: '能源与动力工程', next: 100},
            {id: 10, text: '生物医学工程', next: 100}
          ]
        },
        {
          id: 5,
          text: '请在 笃学书院——理科强基学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '数学与应用数学', next: 100},
            {id: 2, text: '信息与计算科学', next: 100}
          ]
        },
        {
          id: 6,
          text: '请在 令希书院——智能建造学域 选择专业[单选题]',
          options: [
            {id: 1, text: '工程力学', next: 100},
            {id: 2, text: '飞行器设计与工程', next: 100},
            {id: 3, text: '船舶与海洋工程', next: 100},
            {id: 4, text: '智能建造', next: 100},
            {id: 5, text: '水利水电工程', next: 100},
            {id: 6, text: '港口航道与海岸工程', next: 100},
            {id: 7, text: '海洋资源开发技术', next: 100},
            {id: 8, text: '交通工程', next: 100},
            {id: 9, text: '工程管理', next: 100},
            {id: 10, text: '建筑环境与能源应用工程', next: 100},
            {id: 11, text: '土木工程', next: 100}
          ]
        },
        {
          id: 7,
          text: '请在 厚德书院——人文社科学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '知识产权', next: 100},
            {id: 2, text: '公共事业管理', next: 100},
            {id: 3, text: '马克思主义理论', next: 100},
            {id: 4, text: '哲学', next: 100},
            {id: 5, text: '广播电视学', next: 100},
            {id: 6, text: '汉语言文学', next: 100},
            {id: 7, text: '英语', next: 100},
            {id: 8, text: '翻译', next: 100},
            {id: 9, text: '日语', next: 100},
            {id: 10, text: '建筑学', next: 100},
            {id: 11, text: '城乡规划', next: 100},
            {id: 12, text: '工业设计', next: 100},
            {id: 13, text: '视觉传达设计', next: 100},
            {id: 14, text: '环境设计', next: 100},
            {id: 15, text: '雕塑', next: 100}
          ]
        },
        {
          id: 8,
          text: '请在 知行书院——信息技术学域（一） 选择专业 [单选题]',
          options: [
            {id: 1, text: '电子信息工程', next: 100},
            {id: 2, text: '计算机科学与技术', next: 100},
            {id: 3, text: '未来机器人', next: 100},
            {id: 4, text: '自动化', next: 100},
            {id: 5, text: '电气工程及其自动化', next: 100},
            {id: 6, text: '光电信息科学与工程', next: 100},
            {id: 7, text: '生物医学工程', next: 100},
            {id: 8, text: '大数据管理与应用', next: 100},
            {id: 9, text: '信息管理与信息系统', next: 100}
          ]
        },
        {
          id: 9,
          text: '请在 求实书院——信息技术学域（二） 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能无人系统技术', next: 100},
            {id: 2, text: '软件工程', next: 100},
            {id: 3, text: '集成电路设计与集成系统', next: 100},
            {id: 4, text: '电子科学与技术', next: 100},
            {id: 5, text: '电子信息材料', next: 100}
          ]
        },
        // {
        //   id: 10,
        //   text: '请在 工商管理类 任选专业 [单选题]',
        //   options: [
        //     {id: 111, text: '金融学', next: null},
        //     {id: 222, text: '工商管理', next: null},
        //     {id: 333, text: '物流管理', next: null},
        //     {id: 444, text: '国际经济与贸易', next: null},
        //   ]
        // },
        {
          id: 11,
          text: '请在 人文社科学域 任选专业 [单选题]',
          options: [
            {id: 111, text: '知识产权', next: 100},
            {id: 222, text: '公共事业管理', next: 100},
            {id: 333, text: '马克思主义理论', next: 100},
            {id: 444, text: '哲学', next: 100},
            {id: 555, text: '广播电视学', next: 100},
            {id: 666, text: '汉语言文学', next: 100},
          ]
        },
        {
          id: 12,
          text: '请在 英语、日语类 任选专业 [单选题]',
          options: [
            {id: 111, text: '英语', next: 100},
            {id: 222, text: '翻译', next: 100},
            {id: 333, text: '日语', next: 100},
          ]
        },
        {
          id: 13,
          text: '请在 建筑类 任选专业 [单选题]',
          options: [
            {id: 111, text: '建筑学', next: 100},
            {id: 222, text: '城乡规划', next: 100},
            {id: 333, text: '工业设计', next: 100},
          ]
        },
        {
          id: 100,
          text: '是否要转专业 [单选题]',
          options: [
            {id: 1, text: '是', next: 101},
            {id: 2, text: '否', next: null},
          ]
        },
        {
          id: 101,
          text: '请选择要转入的学域 [单选题]',
          options: this.getFilteredDomainOptions()
        },
        {
          id: 102,
          text: '请在 大煜书院——物质创造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '化学工程与工艺', next: null},
            {id: 2, text: '精细化工', next: null},
            {id: 3, text: '制药工程', next: null},
            {id: 4, text: '高分子材料与工程', next: null},
            {id: 5, text: '安全工程', next: null},
            {id: 6, text: '过程装备与控制工程', next: null},
            {id: 7, text: '环境工程', next: null},
            {id: 8, text: '生物工程', next: null}
          ]
        },
        {
          id: 103,
          text: '请在 伯川书院——智能制造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能制造工程', next: null},
            {id: 2, text: '机械设计制造及其自动化', next: null},
            {id: 3, text: '智能车辆工程', next: null},
            {id: 4, text: '测控技术与仪器', next: null},
            {id: 5, text: '电子信息材料', next: null},
            {id: 6, text: '金属材料工程', next: null},
            {id: 7, text: '功能材料', next: null},
            {id: 8, text: '材料成型及控制工程', next: null},
            {id: 9, text: '能源与动力工程', next: null},
            {id: 10, text: '生物医学工程', next: null}
          ]
        },
        {
          id: 104,
          text: '请在 笃学书院——理科强基学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '数学与应用数学', next: null},
            {id: 2, text: '信息与计算科学', next: null}
          ]
        },
        {
          id: 105,
          text: '请在 令希书院——智能建造学域 选择专业[单选题]',
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
          id: 106,
          text: '请在 厚德书院——人文社科学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '知识产权', next: null},
            {id: 2, text: '公共事业管理', next: null},
            {id: 3, text: '马克思主义理论', next: null},
            {id: 4, text: '哲学', next: null},
            {id: 5, text: '广播电视学', next: null},
            {id: 6, text: '汉语言文学', next: null},
            {id: 7, text: '英语', next: null},
            {id: 8, text: '翻译', next: null},
            {id: 9, text: '日语', next: null},
            {id: 10, text: '建筑学', next: null},
            {id: 11, text: '城乡规划', next: null},
            {id: 12, text: '工业设计', next: null},
            {id: 13, text: '视觉传达设计', next: null},
            {id: 14, text: '环境设计', next: null},
            {id: 15, text: '雕塑', next: null}
          ]
        },
        {
          id: 107,
          text: '请在 知行书院——信息技术学域（一） 选择专业 [单选题]',
          options: [
            {id: 1, text: '电子信息工程', next: null},
            {id: 2, text: '计算机科学与技术', next: null},
            {id: 3, text: '未来机器人', next: null},
            {id: 4, text: '自动化', next: null},
            {id: 5, text: '电气工程及其自动化', next: null},
            {id: 6, text: '光电信息科学与工程', next: null},
            {id: 7, text: '生物医学工程', next: null},
            {id: 8, text: '大数据管理与应用', next: null},
            {id: 9, text: '信息管理与信息系统', next: null}
          ]
        },
        {
          id: 108,
          text: '请在 求实书院——信息技术学域（二） 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能无人系统技术', next: null},
            {id: 2, text: '软件工程', next: null},
            {id: 3, text: '集成电路设计与集成系统', next: null},
            {id: 4, text: '电子科学与技术', next: null},
            {id: 5, text: '电子信息材料', next: null}
          ]
        },
        {
          id: 999,
          text: '问卷结束，感谢您的参与！',
          options: []
        }
      ];
    },
    // 针对于创新班/拔尖班的学生问卷
    getQuestionnaireE() {
      //只过滤部分信息
      this.filter = 5;
      return [
        {
          id: 1,
          text: '您的域内专业任选形式是 [单选题]',
          options: [
            {id: 1, text: '域内任选专业，并转专业', next: 50},
          ]
        },
        {
          id: 50,
          text: '您是否放弃现有拔尖班、创新班身份选择普通专业 [单选题]',
          options: [
            {id: 1, text: '是', next: this.num},
            {id: 2, text: '否', next: this.num4},
          ]
        },
        {
          id: 3,
          text: '请在 大煜书院——物质创造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '化学工程与工艺', next: 100},
            {id: 2, text: '精细化工', next: 100},
            {id: 3, text: '制药工程', next: 100},
            {id: 4, text: '高分子材料与工程', next: 100},
            {id: 5, text: '安全工程', next: 100},
            {id: 6, text: '过程装备与控制工程', next: 100},
            {id: 7, text: '环境工程', next: 100},
            {id: 8, text: '生物工程', next: 100}
          ]
        },
        {
          id: 4,
          text: '请在 伯川书院——智能制造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能制造工程', next: 100},
            {id: 2, text: '机械设计制造及其自动化', next: 100},
            {id: 3, text: '智能车辆工程', next: 100},
            {id: 4, text: '测控技术与仪器', next: 100},
            {id: 5, text: '电子信息材料', next: 100},
            {id: 6, text: '金属材料工程', next: 100},
            {id: 7, text: '功能材料', next: 100},
            {id: 8, text: '材料成型及控制工程', next: 100},
            {id: 9, text: '能源与动力工程', next: 100},
            {id: 10, text: '生物医学工程', next: 100}
          ]
        },
        {
          id: 5,
          text: '请在 笃学书院——理科强基学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '数学与应用数学', next: 100},
            {id: 2, text: '信息与计算科学', next: 100}
          ]
        },
        {
          id: 6,
          text: '请在 令希书院——智能建造学域 选择专业[单选题]',
          options: [
            {id: 1, text: '工程力学', next: 100},
            {id: 2, text: '飞行器设计与工程', next: 100},
            {id: 3, text: '船舶与海洋工程', next: 100},
            {id: 4, text: '智能建造', next: 100},
            {id: 5, text: '水利水电工程', next: 100},
            {id: 6, text: '港口航道与海岸工程', next: 100},
            {id: 7, text: '海洋资源开发技术', next: 100},
            {id: 8, text: '交通工程', next: 100},
            {id: 9, text: '工程管理', next: 100},
            {id: 10, text: '建筑环境与能源应用工程', next: 100},
            {id: 11, text: '土木工程', next: 100}
          ]
        },
        {
          id: 7,
          text: '请在 厚德书院——人文社科学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '知识产权', next: 100},
            {id: 2, text: '公共事业管理', next: 100},
            {id: 3, text: '马克思主义理论', next: 100},
            {id: 4, text: '哲学', next: 100},
            {id: 5, text: '广播电视学', next: 100},
            {id: 6, text: '汉语言文学', next: 100},
            {id: 7, text: '英语', next: 100},
            {id: 8, text: '翻译', next: 100},
            {id: 9, text: '日语', next: 100},
            {id: 10, text: '建筑学', next: 100},
            {id: 11, text: '城乡规划', next: 100},
            {id: 12, text: '工业设计', next: 100},
            {id: 13, text: '视觉传达设计', next: 100},
            {id: 14, text: '环境设计', next: 100},
            {id: 15, text: '雕塑', next: 100}
          ]
        },
        {
          id: 8,
          text: '请在 知行书院——信息技术学域（一） 选择专业 [单选题]',
          options: [
            {id: 1, text: '电子信息工程', next: 100},
            {id: 2, text: '计算机科学与技术', next: 100},
            {id: 3, text: '未来机器人', next: 100},
            {id: 4, text: '自动化', next: 100},
            {id: 5, text: '电气工程及其自动化', next: 100},
            {id: 6, text: '光电信息科学与工程', next: 100},
            {id: 7, text: '生物医学工程', next: 100},
            {id: 8, text: '大数据管理与应用', next: 100},
            {id: 9, text: '信息管理与信息系统', next: 100}
          ]
        },
        {
          id: 9,
          text: '请在 求实书院——信息技术学域（二） 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能无人系统技术', next: 100},
            {id: 2, text: '软件工程', next: 100},
            {id: 3, text: '集成电路设计与集成系统', next: 100},
            {id: 4, text: '电子科学与技术', next: 100},
            {id: 5, text: '电子信息材料', next: 100}
          ]
        },
        {
          id: 51,
          text: '请选择 智能制造工程（创新班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '智能制造工程（创新班）', next: 100},
            {id: 222, text: '机械设计制造及其自动化（创新班）', next: 100},
            {id: 333, text: '智能车辆工程（创新班）', next: 100},
            {id: 444, text: '测控技术与仪器（创新班）', next: 100},
          ]
        },
        {
          id: 52,
          text: '请选择 储能科学与工程（新工科拔尖班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '储能科学与工程（新工科拔尖班）', next: 100},
            {id: 222, text: '过程装备与控制工程（新工科拔尖班）', next: 100},
            {id: 333, text: '化学工程与工艺（新工科拔尖班）', next: 100},
            {id: 444, text: '安全工程（新工科拔尖班）', next: 100},
          ]
        },
        // {
        //   id: 102,
        //   text: '请选择 环境工程（新工科拔尖班） 的内设专业 [单选题]',
        //   options: [
        //     {id: 111, text: '环境科学（新工科拔尖班）', next: null},
        //     {id: 222, text: '环境工程（新工科拔尖班）', next: null},
        //   ]
        // },
        {
          id: 53,
          text: '请选择 工程力学（创新班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '工程力学（钱令希力学创新班）', next: 100},
            {id: 222, text: '工程力学（智算力学创新班）', next: 100},
          ]
        },
        {
          id: 54,
          text: '请选择 智能分子工程（新工科拔尖班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '智能分子工程（拔尖班）', next: 100},
            {id: 222, text: '制药工程（拔尖班）', next: 100},
            {id: 333, text: '高分子材料与工程（拔尖班）', next: 100},
            {id: 444, text: '精细化工（拔尖班）', next: 100},
          ]
        },
        {
          id: 55,
          text: '请选择 建筑学（新工科拔尖班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '建筑学（新工科拔尖班）', next: 100},
            {id: 222, text: '城乡规划（新工科拔尖班）', next: 100},
          ]
        },
        {
          id: 56,
          text: '请选择 大数据管理与应用（新工科拔尖班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '大数据管理与应用（新工科拔尖班）', next: 100},
            {id: 222, text: '信息管理与信息系统（新工科拔尖班）', next: 100},
          ]
        },
        {
          id: 57,
          text: '请选择 电子信息类（创新班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '电子信息工程（创新班）', next: 100},
            {id: 222, text: '计算机科学与技术（创新班）', next: 100},
            {id: 333, text: '未来机器人（创新班）', next: 100},
            {id: 444, text: '自动化（创新班）', next: 100},
            {id: 555, text: '电气工程及其自动化（创新班）', next: 100},
            {id: 666, text: '光电信息科学与工程（创新班）', next: 100},
            {id: 777, text: '生物医学工程（创新班）',next: 100}
          ]
        },
        {
          id: 58,
          text: '请选择 智能建造（新工科拔尖班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '智能建造（拔尖班）', next: 100},
            {id: 222, text: '水利水电工程（拔尖班）', next: 100},
            {id: 333, text: '港口航道与海岸工程（拔尖班）', next: 100},
            {id: 444, text: '建筑环境与能源应用工程（拔尖班）', next: 100},
            {id: 555, text: '土木工程（拔尖班）', next: 100}
          ]
        },
        {
          id: 59,
          text: '请选择 材料类（新工科拔尖班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '金属材料工程（新工科拔尖班）', next: 100},
            {id: 222, text: '功能材料（新工科拔尖班）', next: 100},
            {id: 333, text: '材料成型及控制工程（新工科拔尖班）', next: 100},
          ]
        },
        {
          id: 60,
          text: '请选择 数学类（华罗庚数学国家基础学科拔尖计划班） 的内设专业 [单选题]',
          options: [
            {id: 111, text: '数学与应用数学（拔尖班）', next: 100},
            {id: 222, text: '信息与计算科学（拔尖班）', next: 100}
          ]
        },
        {
          id: 100,
          text: '是否要转专业 [单选题]',
          options: [
            {id: 1, text: '是', next: 101},
            {id: 2, text: '否', next: null},
          ]
        },
        {
          id: 101,
          text: '请选择要转入的学域 [单选题]',
          options: this.getFilteredDomainOptions()
        },
        {
          id: 102,
          text: '请在 大煜书院——物质创造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '化学工程与工艺', next: null},
            {id: 2, text: '精细化工', next: null},
            {id: 3, text: '制药工程', next: null},
            {id: 4, text: '高分子材料与工程', next: null},
            {id: 5, text: '安全工程', next: null},
            {id: 6, text: '过程装备与控制工程', next: null},
            {id: 7, text: '环境工程', next: null},
            {id: 8, text: '生物工程', next: null}
          ]
        },
        {
          id: 103,
          text: '请在 伯川书院——智能制造学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能制造工程', next: null},
            {id: 2, text: '机械设计制造及其自动化', next: null},
            {id: 3, text: '智能车辆工程', next: null},
            {id: 4, text: '测控技术与仪器', next: null},
            {id: 5, text: '电子信息材料', next: null},
            {id: 6, text: '金属材料工程', next: null},
            {id: 7, text: '功能材料', next: null},
            {id: 8, text: '材料成型及控制工程', next: null},
            {id: 9, text: '能源与动力工程', next: null},
            {id: 10, text: '生物医学工程', next: null}
          ]
        },
        {
          id: 104,
          text: '请在 笃学书院——理科强基学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '数学与应用数学', next: null},
            {id: 2, text: '信息与计算科学', next: null}
          ]
        },
        {
          id: 105,
          text: '请在 令希书院——智能建造学域 选择专业[单选题]',
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
          id: 106,
          text: '请在 厚德书院——人文社科学域 选择专业 [单选题]',
          options: [
            {id: 1, text: '知识产权', next: null},
            {id: 2, text: '公共事业管理', next: null},
            {id: 3, text: '马克思主义理论', next: null},
            {id: 4, text: '哲学', next: null},
            {id: 5, text: '广播电视学', next: null},
            {id: 6, text: '汉语言文学', next: null},
            {id: 7, text: '英语', next: null},
            {id: 8, text: '翻译', next: null},
            {id: 9, text: '日语', next: null},
            {id: 10, text: '建筑学', next: null},
            {id: 11, text: '城乡规划', next: null},
            {id: 12, text: '工业设计', next: null},
            {id: 13, text: '视觉传达设计', next: null},
            {id: 14, text: '环境设计', next: null},
            {id: 15, text: '雕塑', next: null}
          ]
        },
        {
          id: 107,
          text: '请在 知行书院——信息技术学域（一） 选择专业 [单选题]',
          options: [
            {id: 1, text: '电子信息工程', next: null},
            {id: 2, text: '计算机科学与技术', next: null},
            {id: 3, text: '未来机器人', next: null},
            {id: 4, text: '自动化', next: null},
            {id: 5, text: '电气工程及其自动化', next: null},
            {id: 6, text: '光电信息科学与工程', next: null},
            {id: 7, text: '生物医学工程', next: null},
            {id: 8, text: '大数据管理与应用', next: null},
            {id: 9, text: '信息管理与信息系统', next: null}
          ]
        },
        {
          id: 108,
          text: '请在 求实书院——信息技术学域（二） 选择专业 [单选题]',
          options: [
            {id: 1, text: '智能无人系统技术', next: null},
            {id: 2, text: '软件工程', next: null},
            {id: 3, text: '集成电路设计与集成系统', next: null},
            {id: 4, text: '电子科学与技术', next: null},
            {id: 5, text: '电子信息材料', next: null}
          ]
        },
        {
          id: 200,
          text: '请选择 ' + this.major + ' 的内设专业 [单选题]',
          options: [
            {id: 111, text: this.major, next: 100},
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
      if (this.finalAnswerText === '保持现有专业') {//只有不可变更专业的
        this.finalAnswerText = this.major;
        console.log(this.finalAnswerText);
      }
      if (this.finalAnswerText === '不参与此次专业任选') {
        this.finalAnswerText = '不参与此次专业任选';
      }
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
      //不可+仅可转专业，均为保持当前专业
      if (this.splitFlow === "不可变更专业") {
        this.finalAnswerText2 = 1;
        this.finalAnswerText3 = this.major;
        this.finalAnswerText4 = 1;
        this.academy2 = this.academy;
      }
      //不参与此次专业任选
      else if (this.splitFlow === "不参与此次专业任选") {
        this.finalAnswerText2 = 0;
        this.finalAnswerText3 = this.major;
        this.finalAnswerText4 = 0;
        this.academy2 = this.academy;
      }

      //仅可转专业
      else if (this.splitFlow === "仅可转专业") {
        if (this.getLastAnswerForQuestion(100).selectedOptionText === "是") {
          this.finalAnswerText2 = 1;//要转专业
          this.finalAnswerText3 = this.finalAnswerText;
          this.finalAnswerText4 = 5;
          this.finalAnswerText = this.major;
          this.academy2 = this.getLastAnswerForQuestion(101).selectedOptionText;
        } else if (this.getLastAnswerForQuestion(100).selectedOptionText === "否") {
          this.finalAnswerText2 = 1;//不转专业，保持现有专业
          this.finalAnswerText3 = this.major;
          this.finalAnswerText4 = 1;
          this.academy2 = this.academy;
        }
      }
      //类内任选
      else if (this.splitFlow === "可类内任选，不能转专业") {
        this.finalAnswerText2 = 3;//类内任选
        this.finalAnswerText3 = this.finalAnswerText;
        this.finalAnswerText4 = 3;
        this.academy2 = this.academy;
      }

      //域内非创新班
      else if (this.splitFlow === "可域内任选，并转专业" && this.specialClass === 0) {
        //不转专业
        if (this.getLastAnswerForQuestion(100).selectedOptionText === "是") {
          this.finalAnswerText2 = 2;
          this.finalAnswerText = this.getLastAnswerForQuestion(this.num3).selectedOptionText;
          this.finalAnswerText3 = this.finalAnswerText;
          this.finalAnswerText4 = 5;
          this.academy2 = this.getLastAnswerForQuestion(101).selectedOptionText;
        } else if (this.getLastAnswerForQuestion(100).selectedOptionText === "否") {
          this.finalAnswerText2 = 2;
          this.finalAnswerText = this.getLastAnswerForQuestion(this.num3).selectedOptionText;
          this.finalAnswerText3 = this.finalAnswerText;
          this.finalAnswerText4 = 2;
          this.academy2 = this.academy;
        }
      }

      //域内创新班
      else if (this.specialClass === 1) {
        if (this.getLastAnswerForQuestion(50).selectedOptionText === "是") {
          //放弃创新班
          if (this.getLastAnswerForQuestion(100).selectedOptionText === "是") {
          this.finalAnswerText2 = 2;//要转专业
          this.finalAnswerText3 = this.finalAnswerText;
          this.finalAnswerText4 = 5;
          this.finalAnswerText = this.getLastAnswerForQuestion(this.num).selectedOptionText;
          this.academy2 = this.getLastAnswerForQuestion(101).selectedOptionText;
        } else if (this.getLastAnswerForQuestion(100).selectedOptionText === "否") {
          this.finalAnswerText2 = 2;//不转专业，保持现有专业
          this.finalAnswerText3 = this.getLastAnswerForQuestion(this.num).selectedOptionText;
          this.finalAnswerText = this.finalAnswerText3;
          this.finalAnswerText4 = 2;
          this.academy2 = this.academy;
        }
         } else if (this.getLastAnswerForQuestion(50).selectedOptionText === "否") {
          //不放弃创新班
        if (this.getLastAnswerForQuestion(100).selectedOptionText === "是") {
          this.finalAnswerText2 = 4;//要转专业
          this.finalAnswerText3 = this.getLastAnswerForQuestion(this.num4).selectedOptionText;
          this.finalAnswerText4 = 5;
          this.academy2 = this.getLastAnswerForQuestion(101).selectedOptionText;
        } else if (this.getLastAnswerForQuestion(100).selectedOptionText === "否") {
          this.finalAnswerText2 = 4;//不转专业，保持现有专业
          this.finalAnswerText3 = this.getLastAnswerForQuestion(this.num4).selectedOptionText;
          this.finalAnswerText = this.finalAnswerText3;
          this.finalAnswerText4 = 4;
          this.academy2 = this.academy;
        }
        }
      }
      this.academyFilter();
      // 显示预览弹窗，而不是直接提交
      this.showPreviewDialog = true;
    },
    // 过滤问卷答案
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
    // 关闭预览弹窗
    closePreviewDialog() {
      this.showPreviewDialog = false;
    },
    // 确认提交
    confirmSubmit() {
      this.showPreviewDialog = false;
      this.submitData();
    },
    // 专业类型映射函数：将数字转换为对应的类型名称
    getMajorTypeName(majorType) {
      const typeMap = {
        '1': '保持当前专业',
        '2': '域内任选专业',
        '3': '类内任选专业',
        '4': '域内任选专业（创新班）',
        '5': '转专业'
      };
      return typeMap[majorType] || majorType;
    },
    //过滤书院
    academyFilter() {
      switch (this.academy2) {
        case '大煜书院——物质创造学域':
          this.academy2 = '大煜书院';
          break;
        case '伯川书院——智能制造学域':
          this.academy2 = '伯川书院';
          break;
        case '笃学书院——理科强基学域':
          this.academy2 = '笃学书院';
          break;
        case '令希书院——智能建造学域':
          this.academy2 = '令希书院';
          break;
        case '厚德书院——人文社科学域':
          this.academy2 = '厚德书院';
          break;
        case '知行书院——信息技术学域（一）':
          this.academy2 = '知行书院';
          break;
        case '求实书院——信息技术学域（二）':
          this.academy2 = '求实书院';
          break;
      }
    },
    //提交数据到问卷数据库
    submitData() {
      this.academyFilter()
      const answer = {
        userName: this.userName,
        questionnaireId: 3,
        changeAdress: this.academy,
        changeMajor: this.filterText(this.finalAnswerText),
        changeMajorType: this.finalAnswerText2,

        //转专业后的书院、专业、分流类型
        afterChangeAdress: this.academy2,
        afterChangeMajor: this.finalAnswerText3,//没过滤的
        afterChangeMajorType: this.finalAnswerText4
      };

      submitQuestionnaireData(answer)
        .then(response => {
          // 处理提交成功的情况
          console.log('提交成功:', response);
          setTimeout(() => {
            // 跳转回问卷页面
            this.$router.go(-1);
          }, 2000); // 2秒后跳转
        })
        .catch(error => {
          // 处理提交失败的情况
          console.error('提交失败:', error);
        });
    },
    // 加载过滤选项数据
    async loadFilterOptions() {
      try {
        const response = await listStudent({
          pageNum: 1,
          pageSize: 10000,
          enrollmentYear: '2025'
        });

        if (response && response.rows) {
          this.allStudentData = response.rows;

          // 提取书院选项（去重）
          const academies = new Set();
          response.rows.forEach(student => {
            if (student.academy) {
              academies.add(student.academy);
            }
          });
          this.academyOptions = Array.from(academies).sort();
        }
      } catch (error) {
        console.error('加载过滤选项失败:', error);
        this.$message.error('加载选项数据失败，请刷新页面重试');
      }
    },
    // 书院选择变化
    onAcademyChange() {
      this.selectedMajor = '';
      this.majorOptions = [];
      this.selectedDivertForm = '';
      this.divertFormOptions = [];

      if (!this.selectedAcademy) return;

      // 过滤出该书院的数据
      const academyData = this.allStudentData.filter(
        student => student.academy === this.selectedAcademy
      );

      // 提取专业选项（去重）
      const majors = new Set();
      academyData.forEach(student => {
        if (student.major) {
          majors.add(student.major);
        }
      });
      this.majorOptions = Array.from(majors).sort();
    },
    // 专业选择变化
    onMajorChange() {
      this.selectedDivertForm = '';
      this.divertFormOptions = [];

      if (!this.selectedAcademy || !this.selectedMajor) return;

      // 过滤出该书院该专业的数据
      const majorData = this.allStudentData.filter(
        student => student.academy === this.selectedAcademy &&
          student.major === this.selectedMajor
      );

      // 提取分流类型选项（去重）
      const divertForms = new Set();
      majorData.forEach(student => {
        if (student.divertForm) {
          divertForms.add(student.divertForm);
        }
      });
      this.divertFormOptions = Array.from(divertForms).sort();
    },
    // 确认过滤器
    confirmFilters() {
      // 设置选中的值并初始化问卷
      this.academy = this.selectedAcademy;
      this.major = this.selectedMajor;
      this.splitFlow = this.selectedDivertForm;

      // 从学生数据中获取innovationClass（创新班状态）
      const matchedStudent = this.allStudentData.find(
        student =>
          student.academy === this.selectedAcademy &&
          student.major === this.selectedMajor &&
          student.divertForm === this.selectedDivertForm
      );
      if (matchedStudent) {
        this.specialClass = matchedStudent.innovationClass || 0;
        this.systemMajor = matchedStudent.originalSystemMajor || '';
        this.studentName = matchedStudent.studentName || '';
      } else {
        this.specialClass = 0;
        this.systemMajor = '';
        this.studentName = '';
      }

      // 切换显示状态
      this.showFilterPage = false;

      // 初始化问卷（设置一些默认值）
      this.setNumBasedOnAcademy(this.academy);
      this.setNumBasedOnMajor(this.major);
      this.setNumBasedOnSpecialty(this.major);
      this.setNumBasedOnClass(this.major);

      // 加载问卷
      this.loadQuestionnaireBySplitFlow();
      this.academyFilter();
    },
    // 返回问卷列表
    goBack() {
      this.$router.go(-1);
    },
  },
  created() {
    // 首先加载过滤选项
    this.loadFilterOptions();
    // 注释掉原来的自动初始化，改为通过过滤选择触发
    // this.initializeQuestionnaire();
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

/* 预览弹窗样式 */
.preview-dialog {
  max-width: 600px;
}

.preview-content {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 5px;
  max-height: 400px;
  overflow-y: auto;
}

.preview-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
}

.preview-item:last-child {
  border-bottom: none;
}

.preview-label {
  font-weight: bold;
  color: #333;
  min-width: 150px;
  display: inline-block;
}

.preview-value {
  color: #666;
  flex: 1;
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

/* 过滤选择页面样式 */
.filter-page {
  max-width: 600px;
  margin: 40px auto;
  padding: 30px;
  background-color: #f4f7fc;
  border-radius: 12px;
}

.filter-container {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-title {
  font-size: 24px;
  color: #395cdc;
  text-align: center;
  margin-bottom: 30px;
  font-weight: bold;
}

.filter-row {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
}

.filter-label {
  width: 100px;
  font-size: 16px;
  color: #333;
  font-weight: 500;
  flex-shrink: 0;
}

.filter-select {
  flex: 1;
}

.filter-actions {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  margin-top: 40px;
}

.filter-actions .el-button {
  flex: 1;
  height: 45px;
  font-size: 16px;
  border-radius: 6px;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #d9d9d9;
}

.cancel-btn:hover {
  background-color: #e8e8e8;
}

.confirm-btn {
  background-color: #395cdc;
  color: white;
}

.confirm-btn:hover {
  background-color: #2b76de;
}

.confirm-btn:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

/* 移动端弹窗适配 */
@media (max-width: 768px) {
  .dialog-box {
    max-width: 95%;
    padding: 30px 20px;
    margin: 10px;
  }

  .dialog-box h2 {
    font-size: 18px;
    margin-bottom: 15px;
  }

  .dialog-box p {
    font-size: 14px;
    margin-bottom: 20px;
  }

  .dialog-footer {
    flex-direction: column;
    gap: 10px;
  }

  .dialog-footer .el-button {
    width: 100%;
    margin: 0;
  }

  .preview-dialog {
    max-width: 95%;
  }

  .preview-content {
    max-height: 300px;
    padding: 12px;
  }

  .preview-item {
    flex-direction: column;
    gap: 8px;
  }

  .preview-label {
    min-width: auto;
    width: 100%;
  }

  .preview-value {
    width: 100%;
  }

  .dialog-box .close-btn {
    top: 8px;
    right: 8px;
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .dialog-box {
    max-width: 98%;
    padding: 20px 15px;
    margin: 5px;
  }

  .dialog-box h2 {
    font-size: 16px;
    margin-bottom: 12px;
  }

  .dialog-box p {
    font-size: 13px;
    margin-bottom: 15px;
  }

  .preview-content {
    max-height: 250px;
    padding: 10px;
  }
}
</style>
