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

// 新增【请填写功能名称】
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
    url: '/system/activities',
    method: 'put',
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
