import request from '@/utils/request'

// 查询活动列表
export function listActivities(query) {
  return request({
    url: '/system/activities/list',
    method: 'get',
    params: query
  })
}

// 查询活动详细
export function getActivities(activityId) {
  return request({
    url: '/system/activities/' + activityId,
    method: 'get'
  })
}

// 新增活动信息
export function addActivities(data) {
  return request({
    url: '/system/activities',
    method: 'post',
    data: data
  })
}

// 修改活动信息
export function updateActivities(data) {
  return request({
    url: '/system/activities/update',
    method: 'post',
    data: data
  })
}
// 修改活动信息
export function updateActivities2(data) {
  return request({
    url: '/system/activities/updateActivity',
    method: 'post',
    data: data
  })
}
// 删除活动信息
export function delActivities(activityId) {
  return request({
    url: '/system/activities/' + activityId,
    method: 'delete'
  })
}
//报名容量变化
export function signUpCapacity(activityId,version) {
  return request({
    url: '/system/activities/signUpCapacity',
    method: 'post',
    data: {
      activityId: activityId,
      version: version
    }
  })
}
//取消报名容量变化
export function cancelSignUpCapacity(activityId,version) {
  return request({
    url: '/system/activities/cancelSignUpCapacity',
    method: 'post',
    data: {
      activityId: activityId,
      version: version
    }
  })
}

// 检查活动唯一性
export function checkActivityUnique(params) {
  return request({
    url: '/system/activities/checkUnique',
    method: 'post',
    data: params
  })
}

// 申请扩容
export function requestExpandCapacity(activityId) {
  return request({
    url: '/system/activities/requestExpand',
    method: 'post',
    data: {
      activityId: activityId
    }
  })
}

// 获取扩容申请数量
export function getExpandRequestCount(activityId) {
  return request({
    url: '/system/activities/expandRequestCount/' + activityId,
    method: 'get'
  })
}

// 检查当前用户是否已申请扩容
export function checkExpandRequest(activityId) {
  return request({
    url: '/system/activities/checkExpandRequest/' + activityId,
    method: 'get'
  })
}

