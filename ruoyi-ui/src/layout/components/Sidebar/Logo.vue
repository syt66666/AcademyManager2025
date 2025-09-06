<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1 v-else class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor }">{{ title }} </h1>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1 class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor }">{{ title }} </h1>
      </router-link>
    </transition>
  </div>
</template>

<script>
import logoImg from '@/assets/logo/logo.png'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    variables() {
      return variables;
    },
    sideTheme() {
      return this.$store.state.settings.sideTheme
    }
  },
  data() {
    return {
      title: '书院学生管理系统',
      logo: logoImg // 将导入的图片绑定到 data 中的 logo
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: all 0.8s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 60px;
  line-height: 60px;
  background: linear-gradient(rgb(69, 127, 202));
  text-align: center;
  overflow: hidden;
  border-bottom: none;
  box-shadow: 
    0 4px 30px rgba(69, 127, 202, 0.3),
    0 2px 10px rgba(0,0,0,0.1),
    inset 0 1px 0 rgba(255,255,255,0.2);
  position: relative;

  /* 添加装饰性背景 */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
      radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.08) 0%, transparent 50%),
      linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.05) 50%, transparent 70%);
    pointer-events: none;
    animation: logoShimmer 3s ease-in-out infinite;
  }

  /* 添加底部光晕效果 */
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(255, 255, 255, 0.3) 20%, 
      rgba(255, 255, 255, 0.6) 50%, 
      rgba(255, 255, 255, 0.3) 80%, 
      transparent 100%);
    animation: logoGlow 2s ease-in-out infinite alternate;
  }

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    text-decoration: none;
    transition: all 0.3s ease;
    position: relative;
    z-index: 1;

    &:hover {
      transform: scale(1.02);
    }

    & .sidebar-logo {
      width: 36px;
      height: 36px;
      vertical-align: middle;
      margin-right: 12px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      transition: all 0.3s ease;
      filter: brightness(1.1) contrast(1.1);
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: #ffffff;
      font-weight: 700;
      line-height: 60px;
      font-size: 16px;
      font-family: 'PingFang SC', 'Microsoft YaHei', Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
      letter-spacing: 0.5px;
      text-shadow: 0 2px 4px rgba(0,0,0,0.1);
      transition: all 0.3s ease;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
      transform: scale(1.1);
    }
    
    .sidebar-title {
      opacity: 0;
      transform: translateX(-10px);
    }
  }

  /* 暗色主题适配 */
  &.theme-dark {
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
    
    .sidebar-logo-link {
      .sidebar-title {
        color: #ffffff;
        text-shadow: 0 2px 4px rgba(0,0,0,0.3);
      }
    }
  }
}

/* Logo动画效果 */
@keyframes logoShimmer {
  0%, 100% {
    opacity: 0.8;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.02);
  }
}

@keyframes logoGlow {
  0% {
    opacity: 0.3;
    transform: scaleX(0.8);
  }
  100% {
    opacity: 0.8;
    transform: scaleX(1);
  }
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .sidebar-logo-container {
    height: 50px;
    line-height: 50px;
    
    .sidebar-logo-link {
      .sidebar-logo {
        width: 28px;
        height: 28px;
        margin-right: 8px;
      }
      
      .sidebar-title {
        font-size: 14px;
        line-height: 50px;
      }
    }
  }
}
</style>
