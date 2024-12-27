import request from '@/utils/request'

// 查询问卷信息列表
export function listPages(query) {
  return request({
    url: '/system/pages/list',
    method: 'get',
    params: query
  })
}

// 查询问卷信息详细
export function getPages(id) {
  return request({
    url: '/system/pages/' + id,
    method: 'get'
  })
}

// 新增问卷信息
export function addPages(data) {
  return request({
    url: '/system/pages',
    method: 'post',
    data: data
  })
}

// 修改问卷信息
export function updatePages(data) {
  return request({
    url: '/system/pages',
    method: 'put',
    data: data
  })
}

// 删除问卷信息
export function delPages(id) {
  return request({
    url: '/system/pages/' + id,
    method: 'delete'
  })
}
