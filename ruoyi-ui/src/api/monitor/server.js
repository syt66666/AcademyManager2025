import request from '@/utils/request'

// 获取服务信息
export function getServer() {
  return request({
    url: '/monitor/server',
    method: 'get'
  })
}

// 提交问卷答案
export function submitSurvey(data) {
  return request({
    url: '/questionnaire/submit',  // 后端提交问卷的接口
    method: 'post',
    data  // 传递的请求体数据
  })
}
