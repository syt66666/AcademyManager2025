import request from '@/utils/request'

// 查询学生科创竞赛记录列表
export function listRecord(query) {
  return request({
    url: '/system/record/list',
    method: 'get',
    params: query
  })
}

// 查询学生科创竞赛记录详细
export function getRecord(competitionId) {
  return request({
    url: '/system/record/' + competitionId,
    method: 'get'
  })
}

// 新增学生科创竞赛记录
export function addRecord(data) {
  return request({
    url: '/system/record/add',
    method: 'post',
    data: data,
    headers: {
      "Content-Type": "multipart/form-data", // 确保使用 multipart/form-data
    },
  })
}

// 修改学生科创竞赛记录
export function updateRecord(data) {
  return request({
    url: '/system/record',
    method: 'put',
    data: data,
    headers: {
      "Content-Type": "multipart/form-data", // 确保使用 multipart/form-data
    },
  })
}

// 删除学生科创竞赛记录
export function delRecord(competitionId) {
  return request({
    url: '/system/record/' + competitionId,
    method: 'delete'
  })
}
// 审核学生科创竞赛记录
export function auditRecord(data) {
  return request({
    url: '/system/record/audit',
    method: 'put',
    data: {
      competitionId: data.competitionId,
      auditStatus: data.auditStatus,
      auditRemark: data.auditRemark
    }
  })
}

// 管理员端查询学生科创竞赛记录列表
export function listAuditCompetition(query) {
  return request({
    url: '/system/record/auditList',
    method: 'get',
    params: query
  })
}

//唯一性校验
export function checkCompetitionUnique(params) {
  return request({
    url: '/system/record/checkUnique',
    method: 'post',
    data: params
  })
}
