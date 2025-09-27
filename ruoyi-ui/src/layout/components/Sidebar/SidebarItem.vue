<template>
  <div v-if="!item.hidden">
    <template v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)&&!item.alwaysShow">
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path, onlyOneChild.query)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{'submenu-title-noDropdown':!isNest}">
          <item :icon="onlyOneChild.meta.icon||(item.meta&&item.meta.icon)" :title="getDynamicTitle(onlyOneChild.meta)" />
        </el-menu-item>
      </app-link>
    </template>

    <el-submenu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template slot="title">
        <item v-if="item.meta" :icon="item.meta && item.meta.icon" :title="item.meta.title" />
      </template>
      <sidebar-item
        v-for="(child, index) in item.children"
        :key="child.path + index"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-submenu>
  </div>
</template>

<script>
import path from 'path'
import { isExternal } from '@/utils/validate'
import Item from './Item'
import AppLink from './Link'
import FixiOSBug from './FixiOSBug'

export default {
  name: 'SidebarItem',
  components: { Item, AppLink },
  mixins: [FixiOSBug],
  props: {
    // route object
    item: {
      type: Object,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    }
  },
  data() {
    this.onlyOneChild = null
    return {}
  },
  methods: {
    // 获取动态标题
    getDynamicTitle(meta) {
      if (meta && meta.dynamicTitle) {
        // 判断用户角色
        const userName = this.$store.state.user.name;
        const isAdmin = userName && userName >= '10000' && userName <= '10007';
        
        if (meta.title === '首页') {
          return isAdmin ? '活动管理' : '活动预约';
        }
      }
      return meta ? meta.title : '';
    },
    
    hasOneShowingChild(children = [], parent) {
      if (!children) {
        children = [];
      }
      const showingChildren = children.filter(item => {
        if (item.hidden) {
          return false
        }
        // Temp set(will be used if only has one showing child)
        this.onlyOneChild = item
        return true
      })

      // When there is only one child router, the child router is displayed by default
      if (showingChildren.length === 1) {
        return true
      }

      // Show parent if there are no child router to display
      if (showingChildren.length === 0) {
        this.onlyOneChild = { ... parent, path: '', noShowingChildren: true }
        return true
      }

      return false
    },
    resolvePath(routePath, routeQuery) {
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(this.basePath)) {
        return this.basePath
      }
      if (routeQuery) {
        let query = JSON.parse(routeQuery);
        return { path: path.resolve(this.basePath, routePath), query: query }
      }
      return path.resolve(this.basePath, routePath)
    }
  }
}
</script>
<style scoped>
/* 主菜单容器 - 现代渐变背景 */
::v-deep .el-menu {
  background: linear-gradient(to bottom, rgb(69, 127, 202), rgb(86, 145, 200)) !important;
  border-right: none !important;
  padding: 20px 0;
  position: relative;
  overflow: hidden;
}

/* 添加装饰性背景元素 */
::v-deep .el-menu::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.2) 0%, transparent 50%);
  pointer-events: none;
}

/* 菜单项通用样式 - 融入背景的设计 */
::v-deep .el-menu-item,
::v-deep .el-submenu__title {
  height: 52px !important;
  line-height: 52px !important;
  margin: 8px 12px;
  border-radius: 12px;
  color: rgba(255,255,255,0.9) !important;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
  display: flex !important;
  align-items: center;
  position: relative;
  background: transparent !important;
  border: 1px solid transparent;
  font-weight: 500;
  letter-spacing: 0.3px;
  padding: 0 16px !important;
  overflow: hidden;
  backdrop-filter: blur(10px);
  
  /* 添加微妙的内部光效 */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, 
      rgba(255, 255, 255, 0.05) 0%, 
      transparent 50%, 
      rgba(255, 255, 255, 0.02) 100%);
    border-radius: 12px;
    opacity: 0;
    transition: opacity 0.3s ease;
    pointer-events: none;
  }
}

/* 悬停效果 - 融合的渐变效果 */
::v-deep .el-menu-item:hover,
::v-deep .el-submenu__title:hover {
  background: linear-gradient(135deg, 
    rgba(69, 127, 202, 0.3) 0%, 
    rgba(86, 145, 200, 0.2) 50%,
    rgba(69, 127, 202, 0.1) 100%) !important;
  transform: translateX(8px) scale(1.02);
  box-shadow: 
    0 8px 25px rgba(69, 127, 202, 0.2),
    0 4px 15px rgba(0,0,0,0.1),
    inset 0 1px 0 rgba(255,255,255,0.2);
  color: rgba(255,255,255,1) !important;
  border: 1px solid rgba(255,255,255,0.2);
  
  /* 悬停时显示内部光效 */
  &::before {
    opacity: 1;
  }
}

/* 激活菜单项样式 - 融合的突出设计 */
::v-deep .el-menu-item.is-active {
  background: linear-gradient(135deg, 
    rgba(69, 127, 202, 0.5) 0%, 
    rgba(86, 145, 200, 0.4) 50%,
    rgba(69, 127, 202, 0.3) 100%) !important;
  color: rgba(255,255,255,1) !important;
  font-weight: 600;
  position: relative;
  box-shadow: 
    0 10px 30px rgba(69, 127, 202, 0.3),
    0 6px 20px rgba(0,0,0,0.15),
    inset 0 1px 0 rgba(255,255,255,0.3),
    0 0 0 1px rgba(255,255,255,0.25);
  transform: translateX(6px) scale(1.03);
  border: 1px solid rgba(255,255,255,0.25);
  
  /* 激活时显示内部光效 */
  &::before {
    opacity: 1;
    background: linear-gradient(135deg, 
      rgba(255, 255, 255, 0.1) 0%, 
      transparent 50%, 
      rgba(255, 255, 255, 0.05) 100%);
  }
}

/* 激活菜单指示器 - 融合的渐变设计 */
::v-deep .el-menu-item.is-active::after {
  content: '';
  position: absolute;
  left: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 70%;
  background: linear-gradient(135deg, 
    rgba(69, 127, 202, 0.9) 0%, 
    rgba(86, 145, 200, 0.7) 50%,
    rgba(69, 127, 202, 0.5) 100%);
  border-radius: 2px;
  box-shadow: 
    0 0 15px rgba(86, 145, 200, 0.6),
    0 0 5px rgba(255, 255, 255, 0.3);
  animation: indicatorPulse 2s ease-in-out infinite;
}

/* 子菜单标题 - 增强视觉效果 */
::v-deep .el-submenu__title {
  position: relative;
  overflow: hidden;
}

/* 子菜单展开时的父菜单项样式 */
::v-deep .el-submenu.is-opened > .el-submenu__title {
  background: linear-gradient(135deg, 
    rgba(69, 127, 202, 0.25) 0%, 
    rgba(86, 145, 200, 0.15) 50%,
    rgba(69, 127, 202, 0.1) 100%) !important;
  color: rgba(255,255,255,1) !important;
  border: 1px solid rgba(255,255,255,0.2);
  box-shadow: 
    0 6px 20px rgba(0,0,0,0.1),
    inset 0 1px 0 rgba(255,255,255,0.2);
  transform: translateX(4px);
}

/* 子菜单展开箭头动画 - 更流畅 */
::v-deep .el-submenu__icon-arrow {
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
  font-size: 18px;
  color: rgba(255,255,255,0.9);
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  position: relative;
}

/* 箭头光晕效果 */
::v-deep .el-submenu__icon-arrow::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: radial-gradient(circle, rgba(255,255,255,0.3) 0%, transparent 70%);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.3s ease;
  pointer-events: none;
}

::v-deep .el-submenu.is-opened .el-submenu__icon-arrow {
  transform: rotate(180deg) scale(1.1);
  color: #ffffff;
  text-shadow: 0 0 10px rgba(255,255,255,0.5);
}

::v-deep .el-submenu.is-opened .el-submenu__icon-arrow::before {
  width: 30px;
  height: 30px;
  background: radial-gradient(circle, rgba(255,255,255,0.4) 0%, transparent 70%);
}

/* 嵌套菜单样式 - 浅蓝色系设计 */
::v-deep .el-menu--inline {
  background: linear-gradient(135deg, 
    rgba(135, 206, 235, 0.3) 0%, 
    rgba(173, 216, 230, 0.25) 50%,
    rgba(135, 206, 235, 0.2) 100%) !important;
  padding: 8px 12px !important;
  margin: 2px 12px;
  border-radius: 10px;
  border: 1px solid rgba(135, 206, 235, 0.4);
  backdrop-filter: blur(8px);
  box-shadow: inset 0 1px 5px rgba(135, 206, 235, 0.15);
  position: relative;
  overflow: hidden;
  width: calc(100% - 24px);
  box-sizing: border-box;
}

/* 子菜单容器装饰线 */
::v-deep .el-menu--inline::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, 
    rgba(69, 127, 202, 0.6) 0%, 
    rgba(86, 145, 200, 0.4) 50%,
    rgba(69, 127, 202, 0.2) 100%);
  border-radius: 0 1px 1px 0;
}

::v-deep .el-menu--inline .el-menu-item {
  margin: 3px 0;
  height: 40px !important;
  line-height: 40px !important;
  font-size: 13px;
  border-radius: 8px;
  background: linear-gradient(135deg, 
    rgba(135, 206, 235, 0.2) 0%, 
    rgba(173, 216, 230, 0.15) 50%,
    rgba(135, 206, 235, 0.1) 100%) !important;
  border: 1px solid rgba(135, 206, 235, 0.3);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
  color: rgba(255,255,255,0.9) !important;
  font-weight: 500;
  letter-spacing: 0.2px;
  padding: 0 12px !important;
  text-align: left;
  display: flex !important;
  align-items: center;
  backdrop-filter: blur(10px);
}

/* 子菜单项内部光效 - 与父菜单相似 */
::v-deep .el-menu--inline .el-menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.05) 0%, 
    rgba(255, 255, 255, 0.02) 50%,
    rgba(255, 255, 255, 0.08) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

::v-deep .el-menu--inline .el-menu-item:hover {
  background: linear-gradient(135deg, 
    rgba(135, 206, 235, 0.5) 0%, 
    rgba(173, 216, 230, 0.4) 50%,
    rgba(135, 206, 235, 0.35) 100%) !important;
  transform: translateY(-2px);
  border: 1px solid rgba(135, 206, 235, 0.5);
  box-shadow: 0 8px 25px rgba(135, 206, 235, 0.25);
  color: rgba(255,255,255,1) !important;
}

::v-deep .el-menu--inline .el-menu-item:hover::before {
  opacity: 1;
}

/* 子菜单项激活状态 */
::v-deep .el-menu--inline .el-menu-item.is-active {
  background: linear-gradient(135deg, 
    rgba(135, 206, 235, 0.6) 0%, 
    rgba(173, 216, 230, 0.5) 50%,
    rgba(135, 206, 235, 0.45) 100%) !important;
  color: rgba(255,255,255,1) !important;
  font-weight: 600;
  border: 1px solid rgba(135, 206, 235, 0.6);
  box-shadow: 0 8px 30px rgba(135, 206, 235, 0.3);
  transform: translateY(-1px);
}

::v-deep .el-menu--inline .el-menu-item.is-active::before {
  opacity: 1;
  background: linear-gradient(135deg, 
    rgba(135, 206, 235, 0.2) 0%, 
    rgba(173, 216, 230, 0.15) 50%,
    rgba(135, 206, 235, 0.25) 100%);
}

/* 子菜单项图标样式 */
::v-deep .el-menu--inline .el-menu-item [class^="el-icon-"] {
  font-size: 14px !important;
  margin-left: 8px !important;
  margin-right: 8px !important;
  transition: all 0.3s ease;
  filter: drop-shadow(0 1px 3px rgba(0,0,0,0.2));
}

::v-deep .el-menu--inline .el-menu-item:hover [class^="el-icon-"] {
  transform: scale(1.1) rotate(3deg);
  filter: drop-shadow(0 2px 6px rgba(255,255,255,0.3));
}

::v-deep .el-menu--inline .el-menu-item.is-active [class^="el-icon-"] {
  color: #667eea !important;
  transform: scale(1.05);
  filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.3));
}

/* 子菜单展开/收起动画 */
::v-deep .el-menu--inline {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  transform-origin: top;
}

/* 子菜单项进入动画 */
::v-deep .el-menu--inline .el-menu-item {
  animation: slideInFromLeft 0.3s ease-out;
}

@keyframes slideInFromLeft {
  0% {
    opacity: 0;
    transform: translateX(-20px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 图标样式优化 - 更精致的图标设计 */
::v-deep .svg-icon,
::v-deep .el-menu-item [class^="el-icon-"],
::v-deep .el-submenu [class^="el-icon-"] {
  font-size: 20px !important;
  margin-left: 16px !important;
  margin-right: 16px !important;
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

/* 菜单项悬停时图标动画 - 更生动的效果 */
::v-deep .el-menu-item:hover [class^="el-icon-"],
::v-deep .el-submenu:hover [class^="el-icon-"] {
  transform: scale(1.15) rotate(5deg);
  color: #ffffff !important;
  filter: drop-shadow(0 4px 8px rgba(255,255,255,0.3));
}

/* 激活状态的图标 */
::v-deep .el-menu-item.is-active [class^="el-icon-"] {
  color: #667eea !important;
  transform: scale(1.1);
}

/* 子菜单弹出框样式 - 更现代的弹出效果 */
::v-deep .el-menu--popup {
  border-radius: 16px !important;
  box-shadow: 
    0 20px 60px rgba(0,0,0,0.2),
    0 0 0 1px rgba(255,255,255,0.1) !important;
  padding: 12px 0 !important;
  backdrop-filter: blur(20px);
  background: rgba(255,255,255,0.95) !important;
  border: 1px solid rgba(255,255,255,0.2);
}

/* 弹出菜单项样式 - 更精致的弹出项 */
::v-deep .el-menu--popup .el-menu-item {
  min-width: 180px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
  margin: 4px 8px;
  border-radius: 8px;
  color: #333 !important;
}

::v-deep .el-menu--popup .el-menu-item:hover {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: #ffffff !important;
  transform: translateX(8px);
}

/* 菜单折叠动画 - 更流畅的过渡 */
::v-deep .el-menu--vertical {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
}

/* 滚动条美化 */
::v-deep .el-scrollbar__bar.is-vertical .el-scrollbar__thumb {
  background: rgba(255,255,255,0.3) !important;
  border-radius: 6px !important;
}

::v-deep .el-scrollbar__bar.is-vertical .el-scrollbar__thumb:hover {
  background: rgba(255,255,255,0.5) !important;
}

/* 响应式调整 - 移动端优化 */
@media screen and (max-width: 768px) {
  ::v-deep .el-menu-item,
  ::v-deep .el-submenu__title {
    margin: 6px 12px;
    font-size: 15px;
    height: 48px !important;
    line-height: 48px !important;
  }

  ::v-deep .el-menu--inline .el-menu-item {
    font-size: 13px;
    height: 40px !important;
    line-height: 40px !important;
  }

  ::v-deep .el-menu-item:hover,
  ::v-deep .el-submenu__title:hover {
    transform: translateX(8px) scale(1.01);
  }
}

/* 暗色主题适配 */
::v-deep .theme-dark .el-menu {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%) !important;
}

::v-deep .theme-dark .el-menu-item,
::v-deep .theme-dark .el-submenu__title {
  color: rgba(255,255,255,0.9) !important;
  border: 1px solid rgba(255,255,255,0.05);
}

::v-deep .theme-dark .el-menu-item:hover,
::v-deep .theme-dark .el-submenu__title:hover {
  background: rgba(255,255,255,0.1) !important;
}

::v-deep .theme-dark .el-menu-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: #ffffff !important;
}

/* 动画效果 */
@keyframes indicatorPulse {
  0%, 100% {
    opacity: 0.8;
    transform: translateY(-50%) scaleY(1);
  }
  50% {
    opacity: 1;
    transform: translateY(-50%) scaleY(1.1);
  }
}

@keyframes menuItemGlow {
  0% {
    box-shadow: 
      0 8px 25px rgba(69, 127, 202, 0.2),
      0 4px 15px rgba(0,0,0,0.1),
      inset 0 1px 0 rgba(255,255,255,0.2);
  }
  50% {
    box-shadow: 
      0 12px 35px rgba(69, 127, 202, 0.3),
      0 6px 20px rgba(0,0,0,0.15),
      inset 0 1px 0 rgba(255,255,255,0.3);
  }
  100% {
    box-shadow: 
      0 8px 25px rgba(69, 127, 202, 0.2),
      0 4px 15px rgba(0,0,0,0.1),
      inset 0 1px 0 rgba(255,255,255,0.2);
  }
}
</style>
