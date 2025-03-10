// import request from "@/utils/request";
//


// // 查询竞赛记录
// export function fetchCompetitionRecords(queryParams, currentPage, pageSize) {
//   return request({
//     url: "/competition/records",
//     method: "get",
//     params: {
//       ...queryParams, // 查询条件
//       currentPage, // 当前页码
//       pageSize, // 每页显示条数
//     },
//   });
// }


import request from '@/utils/request'
import {auditActivity} from "@/api/system/activity";

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

