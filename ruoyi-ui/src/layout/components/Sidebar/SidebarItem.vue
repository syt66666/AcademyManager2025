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
/* 主菜单容器 */
::v-deep .el-menu {
  background: linear-gradient(195deg, #2c3e50, #3498db) !important;
  border-right: none !important;
  padding: 12px 0;
}

/* 菜单项通用样式 */
::v-deep .el-menu-item,
::v-deep .el-submenu__title {
  height: 48px !important;
  line-height: 48px !important;
  margin: 6px 12px;
  border-radius: 8px;
  color: rgba(255,255,255,0.9) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  display: flex !important;
  align-items: center;
}

/* 悬停效果 */
::v-deep .el-menu-item:hover,
::v-deep .el-submenu__title:hover {
  background: rgba(255,255,255,0.15) !important;
  transform: translateX(8px);
  box-shadow: 2px 2px 12px rgba(0,0,0,0.1);
}

/* 激活菜单项样式 */
::v-deep .el-menu-item.is-active {
  background: #fff !important;
  color: #2c3e50 !important;
  font-weight: 600;
  position: relative;
}

/* 激活菜单指示器 */
::v-deep .el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 60%;
  background: #e74c3c;
  border-radius: 2px;
}

/* 子菜单标题 */
::v-deep .el-submenu__title {
  position: relative;
  overflow: hidden;
}

/* 子菜单展开箭头动画 */
::v-deep .el-submenu__icon-arrow {
  transition: transform 0.3s ease !important;
  font-size: 14px;
}

/* 嵌套菜单样式 */
::v-deep .el-menu--inline {
  background: rgba(255,255,255,0.03) !important;
  padding-left: 16px !important;
}

::v-deep .el-menu--inline .el-menu-item {
  margin: 4px 0;
  height: 40px !important;
  line-height: 40px !important;
  font-size: 13px;
}

/* 图标样式优化 */
::v-deep .svg-icon,
::v-deep .el-menu-item [class^="el-icon-"],
::v-deep .el-submenu [class^="el-icon-"] {
  font-size: 18px !important;
  margin-left: 10px !important;  /* 新增左移调整 */
  margin-right: 6px !important;  /* 原12px改为8px */
  transform: translateX(-4px);   /* 微调像素级对齐 */
}

/* 菜单项悬停时图标动画 */
::v-deep .el-menu-item:hover [class^="el-icon-"],
::v-deep .el-submenu:hover [class^="el-icon-"] {
  transform: scale(1.1);
  color: #e74c3c !important;
}

/* 子菜单弹出框样式 */
::v-deep .el-menu--popup {
  border-radius: 8px !important;
  box-shadow: 0 4px 24px rgba(0,0,0,0.15) !important;
  padding: 8px 0 !important;
}

/* 弹出菜单项样式 */
::v-deep .el-menu--popup .el-menu-item {
  min-width: 160px;
  transition: all 0.2s ease !important;
}

/* 菜单折叠动画 */
::v-deep .el-menu--vertical {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  ::v-deep .el-menu-item,
  ::v-deep .el-submenu__title {
    margin: 4px 8px;
    font-size: 14px;
  }

  ::v-deep .el-menu--inline .el-menu-item {
    font-size: 12px;
  }
}
</style>
