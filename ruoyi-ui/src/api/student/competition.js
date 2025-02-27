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
