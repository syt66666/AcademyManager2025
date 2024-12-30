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
