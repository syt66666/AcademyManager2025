import request from '@/utils/request'

// 查询学生管理列表
export function upLoad(query) {
  return request({
    url: '/Lecture/insert',
    method: 'post',
    data: query,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
// 查询讲座记录
export function fetchLectureReportRecords(queryParams, currentPage, pageSize) {
  return request({
    url: '/Lecture/records',
    method: 'get',
    params: {
      ...queryParams,        // 查询条件
      currentPage,           // 当前页码
      pageSize               // 每页显示条数
    }
  });
}
//更新学生讲座报告信息
export function updateActivity(data) {
  return request({
    url: '/Lecture/update',
    method: 'put',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
//删除学生未提交讲座记录
// 删除学生科创竞赛记录
export function delLectureReport(reportId) {
  return request({
    url: '/Lecture/delete/' + reportId,
    method: 'delete'
  })
}
