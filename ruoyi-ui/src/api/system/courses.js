import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listCourses(query) {
  return request({
    url: '/system/courses/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getCourses(courseId) {
  return request({
    url: '/system/courses/' + courseId,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addCourses(data) {
  return request({
    url: '/system/courses',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateCourses(data) {
  return request({
    url: '/system/courses',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delCourses(courseId) {
  return request({
    url: '/system/courses/' + courseId,
    method: 'delete'
  })
}

// 获取课程预约学生列表
export function getCourseBookings(courseId) {
  return request({
    url: '/system/courseBookings/listWithCourse',
    method: 'get',
    params: { courseId: courseId }
  })
}

// 导出课程学生名单
export function exportCourseStudents(courseId) {
  return request({
    url: '/system/courseBookings/export',
    method: 'post',
    responseType: 'blob',
    data: { courseId: courseId }
  })
}

// 选课容量变化
export function signUpCapacity(courseId, version) {
  return request({
    url: '/system/courses/signUpCapacity',
    method: 'post',
    data: {
      courseId: courseId,
      version: version
    }
  })
}

// 取消选课容量变化
export function cancelSignUpCapacity(courseId, version) {
  return request({
    url: '/system/courses/cancelSignUpCapacity',
    method: 'post',
    data: {
      courseId: courseId,
      version: version
    }
  })
}

// 检查课程唯一性
export function checkCourseUnique(params) {
  return request({
    url: '/system/courses/checkUnique',
    method: 'post',
    data: params
  })
}
