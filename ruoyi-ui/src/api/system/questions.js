import request from '@/utils/request'

// 查询试题管理列表
export function listQuestions(query) {
  return request({
    url: '/system/questions/list',
    method: 'get',
    params: query
  })
}

// 查询试题管理详细
export function getQuestions(id) {
  return request({
    url: '/system/questions/' + id,
    method: 'get'
  })
}

// 新增试题管理
export function addQuestions(data) {
  return request({
    url: '/system/questions',
    method: 'post',
    data: data
  })
}

// 修改试题管理
export function updateQuestions(data) {
  return request({
    url: '/system/questions',
    method: 'put',
    data: data
  })
}

// 删除试题管理
export function delQuestions(id) {
  return request({
    url: '/system/questions/' + id,
    method: 'delete'
  })
}
