import request from '@/utils/request'

// 查询课程选课记录列表
export function listBookings(query) {
  return request({
    url: '/course/bookings/list',
    method: 'get',
    params: query
  })
}

// 查询课程选课记录详细
export function getBooking(bookingId) {
  return request({
    url: '/course/bookings/' + bookingId,
    method: 'get'
  })
}

// 新增课程选课记录
export function addBooking(data) {
  return request({
    url: '/course/bookings',
    method: 'post',
    data: data
  })
}

// 修改课程选课记录
export function updateBooking(data) {
  return request({
    url: '/course/bookings',
    method: 'put',
    data: data
  })
}

// 删除课程选课记录
export function delBookings(bookingId) {
  return request({
    url: '/course/bookings/' + bookingId,
    method: 'delete'
  })
}
// 检查课程选课状态
export function checkCourseBookingSimple(courseId, studentId) {
  return request({
    url: `/course/bookings/checkCourseBooking`,
    method: 'get',
    params: { courseId, studentId }
  })
}


// 删除选课记录
export function deleteBookingsByCourseAndStudent(courseId, studentId) {
  return request({
    url: '/course/bookings/deleteByCourseAndStudent',
    method: 'delete',
    params: { courseId, studentId }
  })
}

