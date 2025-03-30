import request from '@/utils/request'

// 查询不同学院转专业人数
export function echarts1() {
  return request({
    url: '/system/student/echarts1',
    method: 'get',
  })
}

// 查询学生管理列表
export function listStudent(query) {
  return request({
    url: '/system/student/list',
    method: 'get',
    params: query
  })
}

// 查询学生管理详细
export function getStudent(id) {
  return request({
    url: '/system/student/' + id,
    method: 'get'
  })
}

// 新增学生管理
export function addStudent(data) {
  return request({
    url: '/system/student',
    method: 'post',
    data: data
  })
}

// 修改学生管理
export function updateStudent(data) {
  return request({
    url: '/system/student',
    method: 'put',
    data: data
  })
}

// 删除学生管理
export function delStudent(id) {
  return request({
    url: '/system/student/' + id,
    method: 'delete'
  })
}

// 查询用户昵称
export function getNickName() {
  return request({
    url: '/system/student/nickName',
    method: 'get'
  })
}

/**
 * 获取专业树数据
 * @param {Object} params - 查询参数
 * @param {string} params.major - 专业名称
 * @param {string} params.academy - 所属学院
 * @param {int} params.innovationStatus - 专业类型
 * @param {int} params.policyStatus - 专业类型
 * @returns {Promise} 包含树结构数据的 Promise
 */
export function getMajorTree(params) {
  return request({
    url: '/system/major/tree',
    method: 'get',
    params: {
      ...params,
    }
  })
}
// 专业人数统计接口
export function getMajorCount(params) {
  return request({
    url: '/system/major/count',
    method: 'get',
    params: {
      major_id: params.majorId,     // 前端使用驼峰参数名
      divert_from: params.divertFrom,
      // 可扩展其他参数...
    }
  })
}
