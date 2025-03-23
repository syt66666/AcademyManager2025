<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">书院综合管理系统</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        // code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: false,
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
// 动画定义
@keyframes formEnter {
  0% { opacity: 0; transform: translateY(30px) scale(0.95); }
  100% { opacity: 1; transform: translateY(0) scale(1); }
}

@keyframes gradientGlow {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

// 主容器样式
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  min-height: 100vh;
  background: url("../assets/images/login-background.jpg") center/cover;
  background-blend-mode: multiply;
  background-color: rgba(255,255,255,0.92);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(33, 150, 243, 0.08) 0%, rgba(227, 242, 253, 0.04) 100%);
    z-index: 0;
  }
}

// 登录表单容器
.login-form {
  animation: formEnter 0.6s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  z-index: 1;
  width: 400px;
  padding: 25px 25px 5px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 12px 40px rgba(33, 150, 243, 0.15);
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-2px);
  }

  // 渐变边框效果
  &::before, &::after {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    border-radius: 8px;
    z-index: -1;
  }

  &::before {
    opacity: 0.2;
  }

  &::after {
    background-size: 300% 300%;
    animation: gradientGlow 6s ease infinite;
  }
}

// 标题样式
.title {
  margin: 0 auto 30px;
  text-align: center;
  color: #1976D2 !important;
  font-size: 24px;

  &::after {
    content: '';
    display: block;
    width: 60px;
    height: 3px;
    background: #90CAF9;
    margin: 20px auto;
  }
}
// 输入框组
.el-input {
  height: 38px;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateX(5px);
  }

  .el-input__inner {
    height: 38px;
    padding-left: 40px;
    border: 1px solid #e0e4e9;
    border-radius: 4px;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover { border-color: #64B5F6; }
    &:focus {
      border-color: #1976D2;
      box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
    }
  }

  .input-icon {
    width: 20px;
    height: 20px;
    margin-left: 3px;
    color: #90CAF9;
    transition: color 0.3s;

    &:hover { color: #42A5F5; }
  }
}

// 验证码区域
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(33, 150, 243, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.03) rotate(1deg);

    .login-code-img {
      filter: brightness(1.1);
    }
  }

  &-img {
    height: 40px;
    border: 1px solid #BBDEFB;
    transition: all 0.3s ease;
  }
}

// 按钮样式
.el-button--primary {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 2px;
  background: linear-gradient(45deg, #1976D2, #2196F3);
  border: none;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-2px) scale(1.02);
    box-shadow: 0 4px 12px rgba(25, 118, 210, 0.2);

    &::before {
      left: 100%;
    }
  }

  &:active { transform: translateY(1px); }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg,
      transparent,
      rgba(255,255,255,0.3),
      transparent
    );
    transition: left 0.6s;
  }

  // 加载状态
  &.is-loading {
    &::after {
      content: '';
      position: absolute;
      width: 20px;
      height: 20px;
      border: 2px solid rgba(255,255,255,0.3);
      border-top-color: #fff;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
  }
}

// 其他组件
.el-checkbox {
  .el-checkbox__inner {
    transition: all 0.3s ease;
    border-color: #e0e4e9;
  }

  &.is-checked {
    .el-checkbox__inner {
      background: #2196F3;
      border-color: #2196F3;
      transform: scale(1.1);
    }
  }
}

.link-type {
  color: #1976D2 !important;
  position: relative;
  text-decoration: none;
  transition: color 0.3s;

  &::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 0;
    height: 1px;
    background: #42A5F5;
    transition: width 0.3s;
  }

  &:hover {
    color: #42A5F5 !important;

    &::after {
      width: 100%;
    }
  }
}

// 辅助样式调整
.el-login-footer {
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 40px;
  line-height: 40px;
  text-align: center;
  color: #fff;
  font: 12px Arial;
  letter-spacing: 1px;
}

</style>
