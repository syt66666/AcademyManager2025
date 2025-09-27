import request from '@/utils/request'

/**
 * 查询课程选课记录列表
 */
export function listCourseBookings(query) {
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

/**
 * 查询课程选课记录列表（包含详情）
 */
export function listCourseBookingsWithDetails(query) {
  return request({
    url: '/system/courseBookings/listWithDetails',
    method: 'get',
    params: query
  })
}

/**
 * 查询课程选课记录列表（已提交状态）
 */
export function listCourseBookingsSubmitted(query) {
  return request({
    url: '/system/courseBookings/listSubmitted',
    method: 'get',
    params: query
  })
}

/**
 * 查询课程选课记录详细
 */
export function getCourseBooking(bookingId) {
  return request({
    url: '/system/courseBookings/' + bookingId,
    method: 'get'
  })
}

/**
 * 新增课程选课记录
 */
export function addCourseBooking(data) {
  return request({
    url: '/system/courseBookings',
    method: 'post',
    data: data
  })
}

/**
 * 修改课程选课记录
 */
export function updateCourseBooking(data) {
  return request({
    url: '/system/courseBookings',
    method: 'put',
    data: data
  })
}

/**
 * 审核课程选课记录
 */
export function auditCourseBooking(data) {
  return request({
    url: '/system/courseBookings/audit',
    method: 'put',
    data: data
  })
}

/**
 * 删除课程选课记录
 */
export function deleteCourseBooking(bookingId) {
  return request({
    url: '/system/courseBookings/' + bookingId,
    method: 'delete'
  })
}

/**
 * 根据课程ID和学生ID删除选课记录
 */
export function deleteCourseBookingsByCourseAndStudent(courseId, studentId) {
  return request({
    url: '/system/courseBookings/course/' + courseId + '/student/' + studentId,
    method: 'delete'
  })
}

/**
 * 检查课程选课记录是否存在
 */
export function checkCourseBookingSimple(courseId, studentId) {
  const url = '/system/courseBookings/check/' + courseId + '/' + studentId;
  console.log('checkCourseBookingSimple API调用:', { courseId, studentId, url });
  return request({
    url: url,
    method: 'get'
  })
}

/**
 * 检查是否已选课
 */
export function checkIfCourseBooked(courseId, studentId) {
  return request({
    url: '/system/courseBookings/checkBooked/' + courseId + '/' + studentId,
    method: 'get'
  })
}

/**
 * 根据学生ID查询已选课程ID列表
 */
export function getCoursesByStudent(studentId) {
  return request({
    url: '/system/courseBookings/courses/' + studentId,
    method: 'get'
  })
}

/**
 * 统计课程选课审核状态
 */
export function getCourseBookingAuditStatus() {
  return request({
    url: '/system/courseBookings/auditStatus',
    method: 'get'
  })
}

/**
 * 导出课程选课记录
 */
export function exportCourseBookings(query) {
  return request({
    url: '/system/courseBookings/export',
    method: 'post',
    data: query
  })
}

/**
 * 导出课程选课学生列表
 */
export function exportCourseBookingStudents(query) {
  return request({
    url: '/system/courseBookings/exportStudents',
    method: 'post',
    data: query
  })
}
