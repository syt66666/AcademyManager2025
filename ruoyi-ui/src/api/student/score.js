import request from '@/utils/request'

// 查询课程成绩列表
export function listScore(query) {
  return request({
    url: '/student/score/list',
    method: 'get',
    params: query
  })
}

// 查询课程成绩详细
export function getScore(scoreId) {
  return request({
    url: '/student/score/' + scoreId,
    method: 'get'
  })
}

// 新增课程成绩
export function addScore(data) {
  return request({
    url: '/student/score',
    method: 'post',
    data: data
  })
}

// 修改课程成绩
export function updateScore(data) {
  return request({
    url: '/student/score',
    method: 'put',
    data: data
  })
}

// 删除课程成绩
export function delScore(scoreId) {
  return request({
    url: '/student/score/' + scoreId,
    method: 'delete'
  })
}
