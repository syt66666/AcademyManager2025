import request from '@/utils/request'

// 查询审核数据列表
export function listAuditHistory(query) {
  return request({
    url: '/system/history/list',
    method: 'get',
    params: query
  })
}
