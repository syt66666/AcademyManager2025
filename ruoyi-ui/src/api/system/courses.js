import request from '@/utils/request'

// 查询书院选课列表
export function listCourses(query) {
  return request({
    url: '/system/courses/list',
    method: 'get',
    params: query
  })
}

// 查询书院选课详细
export function getCourses(courseId) {
  return request({
    url: '/system/courses/' + courseId,
    method: 'get'
  })
}

// 新增书院选课
export function addCourses(data) {
  return request({
    url: '/system/courses',
    method: 'post',
    data: data
  })
}

// 修改书院选课
export function updateCourses(data) {
  return request({
    url: '/system/courses',
    method: 'post',
    data: data
  })
}

// 删除书院选课
export function delCourses(courseId) {
  return request({
    url: '/system/courses/' + courseId,
    method: 'delete'
  })
}
// 选课减容量
export function signUpCapacity(data) {
  return request({
    url: '/system/courses/signUp',
    method: 'post',
    data: data  // 使用data传递参数
  })
}

// 取消选课恢复容量
export function cancelSignUpCapacity(data) {
  return request({
    url: '/system/courses/cancelSignUp',
    method: 'post',
    data: data
  })
}

// 检查活动唯一性
export function checkCourseUnique(params) {
  return request({
    url: '/system/courses/checkUnique',
    method: 'post',
    data: params
  })
}
