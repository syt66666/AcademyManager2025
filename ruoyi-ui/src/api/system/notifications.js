import request from '@/utils/request'

// 查询通知列表
export function listNotifications(query) {
  return request({
    url: '/system/notifications/list',
    method: 'get',
    params: query
  })
}

// 查询通知列表（学生用户）
export function listNotificationsForStudent(query) {
  return request({
    url: '/system/notifications/student/list',
    method: 'get',
    params: query
  })
}

// 查询通知列表（公开接口）
export function listNotificationsPublic(query) {
  return request({
    url: '/system/notifications/public/list',
    method: 'get',
    params: query
  })
}

// 查询通知详细
export function getNotifications(notiId) {
  return request({
    url: '/system/notifications/' + notiId,
    method: 'get'
  })
}

// 新增通知
export function addNotifications(data) {
  return request({
    url: '/system/notifications',
    method: 'post',
    data: data
  })
}

// 修改通知
export function updateNotifications(data) {
  return request({
    url: '/system/notifications',
    method: 'put',
    data: data
  })
}

// 删除通知
export function delNotifications(notiId) {
  return request({
    url: '/system/notifications/' + notiId,
    method: 'delete'
  })
}
