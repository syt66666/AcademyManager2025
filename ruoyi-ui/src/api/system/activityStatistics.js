import request from '@/utils/request'

/**
 * 获取活动统计概览
 */
export function getActivityOverview() {
  return request({
    url: '/system/activityStatistics/overview',
    method: 'get'
  })
}

/**
 * 获取各书院活动统计
 */
export function getCollegeActivityStats() {
  return request({
    url: '/system/activityStatistics/collegeStats',
    method: 'get'
  })
}

/**
 * 获取活动类型分布
 */
export function getActivityTypeDistribution() {
  return request({
    url: '/system/activityStatistics/typeDistribution',
    method: 'get'
  })
}

/**
 * 获取最近活动列表
 */
export function getRecentActivities(limit = 10) {
  return request({
    url: '/system/activityStatistics/recentActivities',
    method: 'get',
    params: { limit }
  })
}

/**
 * 根据书院筛选活动统计
 */
export function getActivityStatsByCollege(college) {
  return request({
    url: '/system/activityStatistics/collegeFilter',
    method: 'get',
    params: { college }
  })
}

/**
 * 获取书院活动列表
 */
export function getCollegeActivities(college) {
  return request({
    url: '/system/activityStatistics/collegeActivities',
    method: 'get',
    params: { college }
  })
}
