import request from '@/utils/request'

// 查询导师指导记录列表
export function listMentorship(query) {
  return request({
    url: '/system/mentorship/list',
    method: 'get',
    params: query
  })
}

// 查询导师指导记录详细
export function getMentorship(recordId) {
  return request({
    url: '/system/mentorship/' + recordId,
    method: 'get'
  })
}

// 新增导师指导记录
export function addMentorship(data) {
  return request({
    url: '/system/mentorship',
    method: 'post',
    data: data,
    headers: {
      "Content-Type": "multipart/form-data", // 确保使用 multipart/form-data
    },
  })
}

// 修改导师指导记录
export function updateMentorship(data) {
  return request({
    url: '/system/mentorship',
    method: 'put',
    data: data,
    headers: {
      "Content-Type": "multipart/form-data", // 确保使用 multipart/form-data
    },
  })
}

// 删除导师指导记录
export function delMentorship(recordId) {
  return request({
    url: '/system/mentorship/' + recordId,
    method: 'delete'
  })
}
  //唯一性校验
  export function checkMentorshipUnique(params) {
    return request({
      url: '/system/mentorship/checkUnique',
      method: 'post',
      data: params
    })
}
