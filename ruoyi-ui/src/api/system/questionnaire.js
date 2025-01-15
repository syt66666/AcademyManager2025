// 查询问卷数据
import request from "@/utils/request";

export function fetchEchartsData(questionnaireId) {
  return request({
    url: '/system/questionnaire/echarts2',
    method: 'get',
    params: { questionnaireId },
  });
}

