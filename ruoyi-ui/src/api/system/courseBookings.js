import request from '@/utils/request'

// 查询课程选课记录列表
export function listBookings(query) {
  return request({
    url: '/system/courseBookings/list',
    method: 'get',
    params: query
  })
}

// 查询课程选课记录列表（包含课程信息）
export function listBookingsWithCourse(query) {
  return request({
    url: '/system/courseBookings/listWithCourse',
    method: 'get',
    params: query
  })
}

// 查询课程选课记录详细
export function getBookings(bookingId) {
  return request({
    url: '/system/courseBookings/' + bookingId,
    method: 'get'
  })
}

// 新增课程选课记录
export function addBookings(data) {
  return request({
    url: '/system/courseBookings',
    method: 'post',
    data: data
  })
}

// 修改课程选课记录
export function updateBookings(data) {
  return request({
    url: '/system/courseBookings',
    method: 'put',
    data: data
  })
}

// 删除课程选课记录
export function delBookings(bookingId) {
  return request({
    url: '/system/courseBookings/' + bookingId,
    method: 'delete'
  })
}

// 根据课程ID和学生ID删除选课记录
export function deleteBookingsByCourseAndStudent(courseId, studentId) {
  return request({
    url: '/system/courseBookings/byCourseAndStudent',
    method: 'delete',
    params: { courseId, studentId }
  })
}

// 检查课程选课状态
export function checkCourseBookingSimple(courseId, studentId) {
  return request({
    url: '/system/courseBookings/checkBooking',
    method: 'get',
    params: { courseId, studentId }
  })
}

// 新增课程选课记录（简化版）
export function addBooking(data) {
  return request({
    url: '/system/courseBookings',
    method: 'post',
    data: data
  })
}