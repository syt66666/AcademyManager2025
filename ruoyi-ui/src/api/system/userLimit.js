import request from '@/utils/request'

// 记录取消信息
export function recordCancel(data) {
  return request({
    url: '/system/userLimit/recordCancel',
    method: 'post',
    data: data
  })
}

// 检查取消限制
export function checkCancelLimit(studentId) {
  return request({
    url: '/system/userLimit/checkCancelLimit',
    method: 'get',
    params: { studentId }
  })
}

// 获取取消次数
export function getCancelCount(studentId) {
  return request({
    url: '/system/userLimit/getCancelCount',
    method: 'get',
    params: { studentId }
  })
}
