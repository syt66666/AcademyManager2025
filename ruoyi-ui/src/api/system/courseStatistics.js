import request from '@/utils/request'

/**
 * 获取课程统计概览
 */
export function getCourseOverview() {
  return request({
    url: '/system/courseStatistics/overview',
    method: 'get'
  })
}

/**
 * 获取各书院课程统计
 */
export function getCollegeCourseStats() {
  return request({
    url: '/system/courseStatistics/collegeStats',
    method: 'get'
  })
}

/**
 * 获取课程类型分布
 */
export function getCourseTypeDistribution() {
  return request({
    url: '/system/courseStatistics/typeDistribution',
    method: 'get'
  })
}

/**
 * 获取最近课程列表
 */
export function getRecentCourses() {
  return request({
    url: '/system/courseStatistics/recentCourses',
    method: 'get'
  })
}

/**
 * 根据书院筛选课程统计
 */
export function getCourseStatsByCollege(college) {
  return request({
    url: '/system/courseStatistics/statsByCollege',
    method: 'get',
    params: { college }
  })
}

/**
 * 获取书院课程列表
 */
export function getCollegeCourses(college) {
  return request({
    url: '/system/courseStatistics/collegeCourses',
    method: 'get',
    params: { college }
  })
}
