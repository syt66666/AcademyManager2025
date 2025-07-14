import request from '@/utils/request'

/**
 * 查询预约列表
 */
export function listBookings(query) {
  return request({
    url: '/system/bookings/list',
    method: 'get',
    params: query
  })
}
export function listBookingsWithActivity(query) {
  return request({
    url: '/system/bookings/list2',
    method: 'get',
    params: query
  })
}
export function listBookingsAudit(query) {
  return request({
    url: '/system/bookings/list3',
    method: 'get',
    params: query
  })
}
/**
 * 查询预约详细
 */
export function getBooking(bookingId) {
  return request({
    url: '/system/bookings/' + bookingId,
    method: 'get'
  })
}

/**
 * 新增预约
 */
export function addBooking(data) {
  return request({
    url: '/system/bookings',
    method: 'post',
    data: data
  })
}

/**
 * 修改预约
 */
export function updateBooking(data) {
  return request({
    url: '/system/bookings',
    method: 'put',
    data: data
  })
}


/**
 * 批量删除预约
 */
export function deleteBookings(bookingIds) {
  return request({
    url: '/system/bookings/' + bookingId,
    method: 'delete'
  })
}
