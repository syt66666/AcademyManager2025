<template>
  <div class="survey-container">
    <h1>问卷调查</h1>

    <!-- 单选问题 -->
    <form @submit.prevent="handleSubmit">
      <div class="question">
        <p>你最喜欢的编程语言是什么？</p>
        <label>
          <input type="radio" name="language" value="JavaScript" v-model="answer"> JavaScript
        </label>
        <label>
          <input type="radio" name="language" value="Python" v-model="answer"> Python
        </label>
        <label>
          <input type="radio" name="language" value="Java" v-model="answer"> Java
        </label>
        <label>
          <input type="radio" name="language" value="C++" v-model="answer"> C++
        </label>
      </div>

      <!-- 提交按钮 -->
      <button type="submit">提交</button>
    </form>

    <!-- 提交后的反馈消息 -->
    <div v-if="responseMessage" :class="responseClass">{{ responseMessage }}</div>
  </div>
</template>

<script>
// 导入 axios
import axios from 'axios';

export default {
  data() {
    return {
      answer: '', // 存储单选问题的答案
      responseMessage: '', // 提交后的反馈消息
      responseClass: '', // 反馈消息的样式
      user_id: 1, // 示例用户ID，后续需要在登录系统中获取
      questionnaire_id: 5, // 示例问卷ID，后续需要在系统里可以设置设置
    };
  },
  methods: {
    handleSubmit() {
      // 验证用户是否选择了答案
      if (!this.answer) {
        this.responseMessage = '请先选择你最喜欢的编程语言！';
        this.responseClass = 'error';
        return;
      }

      // 准备提交的数据
      const payload = {
        user_id: this.user_id,
        questionnaire_id: this.questionnaire_id,
        answer: this.answer,
      };

      // 发送 POST 请求到后端接口
      axios
        .post('http://localhost:3000/questionnaire/submit', payload)  // 这里的 URL 应根据实际的 API 地址设置
        .then(response => {
          this.responseMessage = response.data.message || '感谢您的参与！';
          this.responseClass = 'success';
        })
        .catch(error => {
          this.responseMessage = '提交失败，请重试。';
          this.responseClass = 'error';
          console.error('提交问卷失败:', error);
        });
    },
  },
};
</script>

<style scoped>
.survey-container {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 350px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  color: #333;
}

.question {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-size: 16px;
}

button {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 100%;
}

button:hover {
  background-color: #45a049;
}

.success {
  color: green;
  font-size: 16px;
  text-align: center;
}
</style>
