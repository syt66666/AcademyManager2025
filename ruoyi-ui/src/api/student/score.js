import request from '@/utils/request'

export function fetchList(params) {
  return request({
    url: '/stu/score/list',
    method: 'get',
    params
  })
}

export function createScore(data) {
  return request({
    url: '/stu/score/add',
    method: 'post',
    data
  })
}

export function updateScore(data) {
  return request({
    url: '/stu/score/update',
    method: 'put',
    data
  })
}

export function deleteScore(scoreId) {
  return request({
    url: `/stu/score/delete/${scoreId}`,
    method: 'delete'
  })
}
