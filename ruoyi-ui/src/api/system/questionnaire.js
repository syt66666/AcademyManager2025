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

