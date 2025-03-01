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
