import request from '@/utils/request'

// 查询课程选课记录列表
export function listBookings(query) {
  return request({
    url: '/system/bookings/list',
    method: 'get',
    params: query
  })
}

// 查询课程选课记录详细
export function getBookings(bookingId) {
  return request({
    url: '/system/bookings/' + bookingId,
    method: 'get'
  })
}

// 新增课程选课记录
export function addBookings(data) {
  return request({
    url: '/system/bookings',
    method: 'post',
    data: data
  })
}

// 修改课程选课记录
export function updateBookings(data) {
  return request({
    url: '/system/bookings',
    method: 'put',
    data: data
  })
}

// 删除课程选课记录
export function delBookings(bookingId) {
  return request({
    url: '/system/bookings/' + bookingId,
    method: 'delete'
  })
}
