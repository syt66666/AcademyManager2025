import request from '@/utils/request'

// 新增讲座记录
export function addReport(query) {
  return request({
    url: '/system/lecture/add',
    method: 'post',
    data: query,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
// 查询讲座记录
export function listReport(query) {
  return request({
    url: '/system/lecture/list',
    method: 'get',
    params: query,
  });
}
//更新学生讲座报告信息
export function updateReport(data) {
  return request({
    url: '/system/lecture/update',
    method: 'put',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

// 删除学生讲座报告信息
export function delReport(reportId) {
  return request({
    url: '/system/lecture/delete/' + reportId,
    method: 'delete'
  })
}
// 管理员端查询学生讲座报告记录列表
export function listAuditReport(query) {
  return request({
    url: '/system/lecture/auditList',
    method: 'get',
    params: query
  })
}

// 审核学生讲座报告记录
export function auditReport(data) {
  return request({
    url: '/system/lecture/audit',
    method: 'put',
    data: {
      reportId: data.reportId,
      auditStatus: data.auditStatus,
      auditRemark: data.auditRemark
    }
  })
}
//唯一性校验
export function checkLectureUnique(params) {
  return request({
    url: '/system/lecture/checkUnique',
    method: 'post',
    data: params
  })
}
//查询不同审核状态数量
export function getAuditCount(query) {
  return request({
    url: '/system/lecture/auditCount',
    method: 'get',
    params: query
  })
}
