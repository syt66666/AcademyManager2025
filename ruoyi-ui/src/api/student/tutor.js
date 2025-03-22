import request from '@/utils/request'

// 查询导师信息列表
export function listTutors(query) {
  return request({
    url: '/system/tutors/list',
    method: 'get',
    params: query
  })
}

// 查询导师信息详细
export function getTutors(tutorId) {
  return request({
    url: '/system/tutors/' + tutorId,
    method: 'get'
  })
}

// 新增导师信息
export function addTutors(data) {
  return request({
    url: '/system/tutors',
    method: 'post',
    data: data
  })
}

// 修改导师信息
export function updateTutors(data) {
  return request({
    url: '/system/tutors',
    method: 'put',
    data: data
  })
}

// 删除导师信息
export function delTutors(tutorId) {
  return request({
    url: '/system/tutors/' + tutorId,
    method: 'delete'
  })
}
