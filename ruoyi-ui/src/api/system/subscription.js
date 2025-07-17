import request from '@/utils/request'

// 查询用户活动订阅列表
export function listSubscription(query) {
  return request({
    url: '/system/subscription/list',
    method: 'get',
    params: query
  })
}

// 查询用户活动订阅详细
export function getSubscription(subscriptionId) {
  return request({
    url: '/system/subscription/' + subscriptionId,
    method: 'get'
  })
}

// 新增用户活动订阅
export function addSubscription(data) {
  return request({
    url: '/system/subscription',
    method: 'post',
    data: data
  })
}

// 修改用户活动订阅
export function updateSubscription(data) {
  return request({
    url: '/system/subscription',
    method: 'put',
    data: data
  })
}

// 删除用户活动订阅
export function delSubscription(subscriptionId) {
  return request({
    url: '/system/subscription/' + subscriptionId,
    method: 'delete'
  })
}
