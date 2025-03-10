import request from '@/utils/request'

// 查询学生文体活动记录列表
export function listActivity(query) {
  return request({
    url: '/system/activity/list',
    method: 'get',
    params: query
  })
}

// 查询学生文体活动记录详细
export function getActivity(activityId) {
  return request({
    url: '/system/activity/' + activityId,
    method: 'get'
  })
}

// 新增学生文体活动记录
export function addActivity(data) {
  return request({
    url: '/system/activity',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

// 修改学生文体活动记录
export function updateActivity(data) {
  return request({
    url: '/system/activity',
    method: 'put',
    data: data
  })
}

// 删除学生文体活动记录
export function delActivity(activityId) {
  return request({
    url: '/system/activity/' + activityId,
    method: 'delete'
  })
}

// 管理员端文体活动审核
export function auditActivity(data) {
  return request({
    url: '/system/activity/audit',
    method: 'put',
    data: {
      activityId: data.activityId,
      auditStatus: data.auditStatus,
      auditRemark: data.auditRemark
    }
  })
}

// 管理员端查询学生文体活动记录列表
export function listAuditActivity(query) {
  return request({
    url: '/system/activity/auditList',
    method: 'get',
    params: query
  })
}

//唯一性校验
export function checkActivityUnique(params) {
  return request({
    url: '/system/activity/checkUnique',
    method: 'post',
    data: params
  })
}

