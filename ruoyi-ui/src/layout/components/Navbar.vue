<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
               @toggleClick="toggleSideBar"/>
    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!topNav"/>
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav"/>
    <search id="header-search" class="right-menu-item"/>
    <div class="right-menu">
      <template v-if="device!=='mobile'">
<!--<span class="user-info"-->
<!--      v-if="userName !== 'admin'&&this.userName!== '10001'&&this.userName!== '10002'&&this.userName!== '10003'&&this.userName!== '10004'&&this.userName!== '10005'&&this.userName!== '10006'&&this.userName!== '10007'&&this.splitFlow!=='不可变更专业'">-->
<!--  <span class="label">书院：</span>{{ department }}-->
<!--  <span class="label">系统内专业：</span>{{ specialty }}-->
<!--  <span class="label">招生录取专业：</span>{{ major }}-->
<!--  <span class="label">域内专业任选形式：</span>{{ splitFlow }}-->
<!--</span>-->
        <span class="user-info"
              v-if="userName !== 'admin'&&this.userName!== '10001'&&this.userName!== '10002'&&this.userName!== '10003'&&this.userName!== '10004'&&this.userName!== '10005'&&this.userName!== '10006'&&this.userName!== '10007'&&this.splitFlow==='不可变更专业'">
  <span class="label">书院：</span>{{ department }}
  <span class="label">系统内专业：</span>{{ specialty }}
  <span class="label">域内专业任选形式：</span>{{ splitFlow }}
</span>
        <search id="header-search" class="right-menu-item"/>
        <screenfull id="screenfull" class="right-menu-item hover-effect"/>
      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <i class="el-icon-caret-bottom"/>
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
<!--          <el-dropdown-item @click.native="setting = true">-->
<!--            <span>布局设置</span>-->
<!--          </el-dropdown-item>-->
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import axios from "axios";
import {getStudent} from "@/api/system/student";
import sidebar from "@/layout/components/Sidebar/index.vue";
import {Breadcrumb} from "element-ui";


export default {
  data() {
    return {
      studentName: '',
      department: '',
      major: '',
      specialty: '',
      splitFlow: '',
      specialClass: ''
    };
  },
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
  },
  computed: {
    sidebar() {
      return sidebar
    },
    userName() {
      return this.$store.state.user.name; // 获取用户名
    },
    ...mapGetters([
      'sidebar',
      'avatar',
      'device'
    ]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
      }
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav
      }
    }
  },
  mounted() {
    this.initializeUserData();
  },
  methods: {
    async initializeUserData() {
      if (this.userName === 'admin') {
        this.studentName = '管理员'
      } else {
        getStudent(this.userName).then(response => {
          const studentInfo = response.studentInfo;
          if (studentInfo) {
            this.studentName = studentInfo.studentName;
            this.department = studentInfo.academy;
            this.major = studentInfo.major;
            if (studentInfo.divertForm === '可类内任选，并转专业') {
              this.splitFlow = '类内任选专业'
            } else if (studentInfo.divertForm === '可类内任选，不能转专业') {
              this.splitFlow = '域内任选专业'
            } else if (studentInfo.divertForm === '可域内任选，并转专业') {
              this.splitFlow = '域内任选专业'
            }else this.splitFlow = studentInfo.divertForm;
            this.specialty = studentInfo.originalSystemMajor;
            if (studentInfo.innovationClass === 1) {
              this.specialClass = '是';
            } else {
              this.specialClass = '否';
            }
          }
        });
      }
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index';
        })
      }).catch(() => {
      });
    },

  }
};

</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, .08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .user-info {
    display: inline-block;
    font-size: 14px;
    color: #333;
    white-space: nowrap; /* 防止换行 */
    letter-spacing: 4px;
  }

  .user-info .label {
    color: #395cdc; /* 设置冒号前文字为蓝色 */
    //font-weight: bold; /* 加粗文字以突出显示 */
    margin-right: 4px; /* 增加冒号后的小间距 */
  }


  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
