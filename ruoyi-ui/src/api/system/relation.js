import request from '@/utils/request'

// 查询导师学生关联列表
export function listRelation(query) {
  return request({
    url: '/system/relation/list',
    method: 'get',
    params: query
  })
}

// 查询导师学生关联详细
export function getRelation(relationId) {
  return request({
    url: '/system/relation/' + relationId,
    method: 'get'
  })
}

// 新增导师学生关联
export function addRelation(data) {
  return request({
    url: '/system/relation',
    method: 'post',
    data: data
  })
}

// 修改导师学生关联
export function updateRelation(data) {
  return request({
    url: '/system/relation',
    method: 'put',
    data: data
  })
}

// 删除导师学生关联
export function delRelation(relationId) {
  return request({
    url: '/system/relation/' + relationId,
    method: 'delete'
  })
}
