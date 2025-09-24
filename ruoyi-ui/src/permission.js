import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

router.beforeEach(async (to, from, next) => {
  NProgress.start()

  try {
    if (getToken()) {
      to.meta.title && store.dispatch('settings/setTitle', to.meta.title)

      /* has token*/
      if (to.path === '/login') {
        next({ path: '/' })
        NProgress.done()
        return
      }

      if (isWhiteList(to.path)) {
        next()
        return
      }

      // 修复：检查用户信息是否已加载
      if (store.getters.roles.length === 0) {
        isRelogin.show = true

        try {
          // 获取用户信息
          await store.dispatch('GetInfo')
          isRelogin.show = false

          // 生成路由
          const accessRoutes = await store.dispatch('GenerateRoutes')

          // 修复：安全地添加路由
          if (accessRoutes && Array.isArray(accessRoutes)) {
            router.addRoutes(accessRoutes)

            // 修复：确保路由添加完成后再跳转
            if (to.redirectedFrom) {
              next(to.redirectedFrom)
            } else {
              next({ ...to, replace: true })
            }
          } else {
            throw new Error('生成的路由表无效')
          }
        } catch (err) {
          console.error('权限验证错误:', err)
          isRelogin.show = false

          await store.dispatch('LogOut')
          Message.error(err?.message || '权限验证失败，请重新登录')
          next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
        }
      } else {
        next()
      }
    } else {
      // 没有token
      if (isWhiteList(to.path)) {
        next()
      } else {
        next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
        NProgress.done()
      }
    }
  } catch (error) {
    console.error('路由守卫全局错误:', error)
    NProgress.done()
    next('/login') // 降级处理，跳转到登录页
  }
})

router.afterEach(() => {
  NProgress.done()
})
