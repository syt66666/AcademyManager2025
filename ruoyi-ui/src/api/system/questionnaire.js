// 查询问卷数据
import request from "@/utils/request";

export function fetchEchartsData2(questionnaireId) {
  return request({
    url: '/system/questionnaire/echarts2',
    method: 'get',
    params: { questionnaireId },
  });
}
export function fetchEchartsData() {
  return request({
    url: '/system/major/majorNum',
    method: 'get',
  });
}
export function getStudentQuestionnaieData(query) {
  return request({
    url: `/system/questionnaire/answers`, // 使用路径变量
    method: 'get',
    params: query,
  });
}
export function getAllStudentQuestionnaieData2(query) {
  return request({
    url: `/system/questionnaire/exportAllAnswers2`, // 使用路径变量
    method: 'get',
    params: query,
  });
}
export function getStudentQuestionnaieData2(query) {
  return request({
    url: `/system/questionnaire/answers2`, // 使用路径变量
    method: 'get',
    params: query,
  });
}
export function getAllStudentQuestionnaieData(query) {
  return request({
    url: `/system/questionnaire/exportAllAnswers`, // 使用路径变量
    method: 'get',
    params: query,
  });
}
export function getQuestionnaireTimes(questionnaireId) {
  return request({
    url: `/system/questionnaire/getQuestionnaireTimes`,
    method: 'get',
    params: { questionnaireId },
  });
}
export function submitQuestionnaireData(answer) {
  return request({
    url: '/system/questionnaire/submitAnswers', // 提交问卷答案的接口路径
    method: 'post',
    data: answer,  // 请求体中的数据
  });

}

// 查询问卷得分列表
export function listScore(query) {
  return request({
    url: '/questionnaire/score/list',
    method: 'get',
    params: query
  })
}

// 查询问卷得分详细信息
export function getScore(id) {
  return request({
    url: '/questionnaire/score/' + id,
    method: 'get'
  })
}

// 新增问卷得分
export function addScore(data) {
  return request({
    url: '/questionnaire/score',
    method: 'post',
    data: data
  })
}

// 修改问卷得分
export function updateScore(data) {
  return request({
    url: '/questionnaire/score',
    method: 'put',
    data: data
  })
}

// 删除问卷得分
export function delScore(id) {
  return request({
    url: '/questionnaire/score/' + id,
    method: 'delete'
  })
}

// 检查用户是否已完成问卷评价
export function checkScoreCompleted(userName, quesType) {
  return request({
    url: '/questionnaire/score/checkCompleted',
    method: 'get',
    params: {
      userName: userName,
      quesType: quesType
    }
  })
}

