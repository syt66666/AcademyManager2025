import request from '@/utils/request'

// 查询学生选课记录列表
export function listEnrollments(query) {
  return request({
    url: '/system/enrollments/list',
    method: 'get',
    params: query
  })
}
export function listEnrollments2(query) {
  return request({
    url: '/system/enrollments/list2',
    method: 'get',
    params: query
  })
}
// 查询学生选课记录详细
export function getEnrollments(enrollmentId) {
  return request({
    url: '/system/enrollments/' + enrollmentId,
    method: 'get'
  })
}

// 新增学生选课记录
export function addEnrollments(data) {
  return request({
    url: '/system/enrollments',
    method: 'post',
    data: data
  })
}

// 修改学生选课记录
export function updateEnrollments(data) {
  return request({
    url: '/system/enrollments',
    method: 'put',
    data: data
  })
}

// 删除学生选课记录
export function delEnrollments(enrollmentId) {
  return request({
    url: '/system/enrollments/' + enrollmentId,
    method: 'delete'
  })
}
