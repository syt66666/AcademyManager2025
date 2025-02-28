import request from '@/utils/request'

// 新增学生管理
export function addCompetitionRecord(query) {
  return request({
    url: '/competition/add',
    method: 'post',
    data: query,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
// 查询学生管理详细
// 查询竞赛记录
export function fetchCompetitionRecords(queryParams, currentPage, pageSize) {
  return request({
    url: '/competition/records',
    method: 'get',
    params: {
      ...queryParams,        // 查询条件
      currentPage,           // 当前页码
      pageSize               // 每页显示条数
    }
  });
}

