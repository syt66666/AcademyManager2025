import request from '@/utils/request'

// 获取服务器当前时间
export function getServerTime() {
  return request({
    url: '/common/time/now',
    method: 'get'
  })
}
