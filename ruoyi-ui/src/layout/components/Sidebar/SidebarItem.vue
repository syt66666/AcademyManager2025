<template>
  <div v-if="!item.hidden">
    <template v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)&&!item.alwaysShow">
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path, onlyOneChild.query)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{'submenu-title-noDropdown':!isNest}">
          <item :icon="onlyOneChild.meta.icon||(item.meta&&item.meta.icon)" :title="onlyOneChild.meta.title" />
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border-right: none !important;
  padding: 16px 0;
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

/* 菜单项通用样式 - 更现代的卡片式设计 */
::v-deep .el-menu-item,
::v-deep .el-submenu__title {
  height: 52px !important;
  line-height: 52px !important;
  margin: 8px 16px;
  border-radius: 12px;
  color: rgba(255,255,255,0.95) !important;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
  display: flex !important;
  align-items: center;
  position: relative;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.1);
  font-weight: 500;
  letter-spacing: 0.3px;
}

/* 悬停效果 - 更丰富的交互 */
::v-deep .el-menu-item:hover,
::v-deep .el-submenu__title:hover {
  background: rgba(255,255,255,0.2) !important;
  transform: translateX(12px) scale(1.02);
  box-shadow: 
    0 8px 32px rgba(0,0,0,0.12),
    0 0 0 1px rgba(255,255,255,0.2);
  color: #ffffff !important;
}

/* 激活菜单项样式 - 更突出的设计 */
::v-deep .el-menu-item.is-active {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%) !important;
  color: #667eea !important;
  font-weight: 700;
  position: relative;
  box-shadow: 
    0 12px 40px rgba(102, 126, 234, 0.3),
    0 0 0 1px rgba(255,255,255,0.8);
  transform: translateX(8px);
}

/* 激活菜单指示器 - 更醒目的设计 */
::v-deep .el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: -16px;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 70%;
  background: linear-gradient(135deg, #ff6b6b, #ffa500);
  border-radius: 3px;
  box-shadow: 0 0 20px rgba(255, 107, 107, 0.6);
}

/* 子菜单标题 - 增强视觉效果 */
::v-deep .el-submenu__title {
  position: relative;
  overflow: hidden;
}

/* 子菜单展开箭头动画 - 更流畅 */
::v-deep .el-submenu__icon-arrow {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
  font-size: 16px;
  color: rgba(255,255,255,0.8);
}

::v-deep .el-submenu.is-opened .el-submenu__icon-arrow {
  transform: rotate(180deg);
  color: #ffffff;
}

/* 嵌套菜单样式 - 更清晰的层次 */
::v-deep .el-menu--inline {
  background: rgba(255,255,255,0.05) !important;
  padding-left: 20px !important;
  margin: 8px 0;
  border-radius: 8px;
  border: 1px solid rgba(255,255,255,0.1);
}

::v-deep .el-menu--inline .el-menu-item {
  margin: 6px 12px;
  height: 44px !important;
  line-height: 44px !important;
  font-size: 14px;
  border-radius: 8px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);
}

::v-deep .el-menu--inline .el-menu-item:hover {
  background: rgba(255,255,255,0.15) !important;
  transform: translateX(8px);
}

/* 图标样式优化 - 更精致的图标设计 */
::v-deep .svg-icon,
::v-deep .el-menu-item [class^="el-icon-"],
::v-deep .el-submenu [class^="el-icon-"] {
  font-size: 20px !important;
  margin-left: 12px !important;
  margin-right: 12px !important;
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
</style>
