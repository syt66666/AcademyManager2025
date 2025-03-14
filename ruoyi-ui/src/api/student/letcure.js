import request from '@/utils/request'

// 新增讲座记录
export function addReport(query) {
  return request({
    url: '/system/lecture/add',
    method: 'post',
    data: query,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
// 查询讲座记录
export function listReport(query) {
  return request({
    url: '/system/lecture/list',
    method: 'get',
    params: query,
  });
}
//更新学生讲座报告信息
export function updateReport(data) {
  return request({
    url: '/system/lecture/update',
    method: 'put',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

// 删除学生讲座报告信息
export function delReport(reportId) {
  return request({
    url: '/system/lecture/delete/' + reportId,
    method: 'delete'
  })
}
