<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!topNav"/>
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav"/>
    <search id="header-search" class="right-menu-item" />
    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <span class="user-info" v-if="this.userName !== 'admin'">
          姓名：{{studentName}}
          学号：{{userName}}
          书院：{{ department }}
          系统内专业：{{ major }}
          招生录取专业：{{ specialty }}
          分流形式：{{ splitFlow }}
          是否为创新班/拔尖班：{{ specialClass}}
        </span>
        <search id="header-search" class="right-menu-item" />
        <screenfull id="screenfull" class="right-menu-item hover-effect" />
      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setting = true">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import axios from "axios";


export default {
  data() {
    return {
      studentName: '',
      department: '',
      major: '',
      specialty: '',
      splitFlow:'',
      specialClass:''
    };
  },
  components: {
    // Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
  },
  computed: {
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
    console.log(this.userName)
    this.initializeUserData();
  },
  methods: {
    initializeUserData() {
      if(this.userName === 'admin'){
        this.studentName = '管理员'
      }
      else{
        axios
          .get(`http://localhost:3000/api/users/${this.userName}`)
          .then(response => {
            const userData = response.data;  // 获取用户数据
            this.splitFlow = userData.divertForm // 分流形式
            this.studentName = userData.studentName; // 姓名
            this.studentId = userData.studentId; // 学号
            this.major = userData.major; // 招生录取专业
            this.department = userData.academy; // 管理部门
            this.specialty = userData.systemMajor; // 系统内专业
            if(userData.InnovationClass===1){
              this.specialClass='是'
            }else{
              this.specialClass='否'
            }
          })
          .catch(error => {
            console.error("获取用户信息失败", error);
            this.$message.error("获取用户信息失败");
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
      }).catch(() => {});
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
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

.user-info {
  //  display: inline-block;
  //  padding: 10px;
  margin: 10px auto; /* 垂直方向居中 */
font-size: 14px; /* 字体大小 */
color: #5d5d5d; /* 字体颜色 */
  //  line-height: 1.6; /* 行间距 */
  //  white-space: pre-line; /* 保留换行格式 */
  text-align: center; /* 文本居中 */
}
  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
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
