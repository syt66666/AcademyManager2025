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
 * 审核更新预约（只更新审核相关字段，不影响文件字段）
 */
export function auditBooking(data) {
  return request({
    url: '/system/bookings/audit',
    method: 'put',
    data: data
  })
}


/**
 * 批量删除预约
 */
export function deleteBookings(bookingId) {
  return request({
    url: '/system/bookings/' + bookingId,
    method: 'delete'
  })
}
/**
 * 根据活动ID和学生ID删除预约
 */
export function deleteBookingsByActivityAndStudent(activityId, studentId) {
  return request({
    url: '/system/bookings/byActivityAndStudent',
    method: 'delete',
    params: { activityId, studentId }
  })
}
//查询不同审核状态数量
export function getAuditCount(query) {
  return request({
    url: '/system/bookings/auditCount',
    method: 'get',
    params: query
  })
}
/**
 * 查询预约详细
 */
export function listBookings2(query) {
  return request({
    url: '/system/bookings/list2',
    method: 'get',
    params: query
  })
}

export function checkBookingSimple(activityId, studentId) {
  return request({
    url: '/system/bookings/checkBooking',
    method: 'get',
    params: { activityId, studentId }
  })
}
