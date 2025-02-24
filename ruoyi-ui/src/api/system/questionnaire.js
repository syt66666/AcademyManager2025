// 查询问卷数据
import request from "@/utils/request";

export function fetchEchartsData(questionnaireId) {
  return request({
    url: '/system/questionnaire/echarts2',
    method: 'get',
    params: { questionnaireId },
  });
}
export function getStudentQuestionnaieData(query) {
  return request({
    url: `/system/questionnaire/answers`, // 使用路径变量
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
