import request from '@/utils/request'

// 查询课程列表（带分页）
export function fetchCourseList(params) {
  return request({
    url: '/stu/courseInfo/list',
    method: 'get',
    params
  })
}

// 获取课程详情
export function getCourseDetail(courseCode) {
  return request({
    url: `/stu/courseInfo/get/${courseCode}`,
    method: 'get'
  })
}

// 新增课程
export function createCourse(data) {
  return request({
    url: '/stu/courseInfo/add',
    method: 'post',
    data
  })
}

// 更新课程信息
export function updateCourse(data) {
  return request({
    url: '/stu/courseInfo/update',
    method: 'put',
    data
  })
}

// 删除课程
export function deleteCourse(courseCode) {
  return request({
    url: `/stu/courseInfo/delete/${courseCode}`,
    method: 'delete'
  })
}
