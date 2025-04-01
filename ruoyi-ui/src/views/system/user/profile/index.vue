<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24">
        <el-card class="user-card">
          <div slot="header" class="card-header">
            <span>个人信息</span>
          </div>
          <div class="card-content">
            <div class="avatar-wrapper">
              <userAvatar />
            </div>
            <ul class="info-list">
              <li class="info-item">
                <svg-icon icon-class="user" />
                <span class="label">用户名称</span>
                <span class="value">{{ user.nickName }}</span>
              </li>
              <li v-if="user.userName != 'admin'" class="info-item">
                <svg-icon icon-class="lock" />
                <span class="label">学号</span>
                <span class="value">{{ user.userName }}</span>
              </li>
              <li v-else class="info-item">
                <svg-icon icon-class="phone" />
                <span class="label">手机号</span>
                <span class="value">{{ user.phonenumber }}</span>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card class="setting-card">
          <div slot="header" class="card-header">
            <span>账户设置</span>
          </div>
          <el-tabs v-model="activeTab" class="setting-tabs">
            <el-tab-pane label="修改密码" name="resetPwd">
              <resetPwd />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userAvatar from "./userAvatar";
import resetPwd from "./resetPwd";
import { getUserProfile } from "@/api/system/user";

export default {
  name: "Profile",
  components: { userAvatar, resetPwd },
  data() {
    return {
      user: {},
      activeTab: "resetPwd"
    };
  },
  created() {
    this.getUser();
  },
  methods: {
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data;
      });
    }
  }
};
</script>

<style lang="scss" scoped>
// 颜色变量
$primary-color: #409EFF;
$secondary-color: #67C23A;
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$border-color: #ebeef5;
$background-color: #f5f7fa;

.app-container {
  padding: 24px;
  background: $background-color;
  min-height: calc(100vh - 84px);
  margin-left: 100px;

  // 卡片通用样式
  .el-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
    }

    ::v-deep .el-card__header {
      padding: 18px 24px;
      background: linear-gradient(135deg, $primary-color, darken($primary-color, 10%));
      border-radius: 12px 12px 0 0;

      .card-header {
        font-size: 18px;
        font-weight: 600;
        color: white;
      }
    }
  }

  // 用户信息卡片
  .user-card {
    margin-right: 20px;

    .card-content {
      padding: 16px;

      .avatar-wrapper {
        padding: 20px 0;
        text-align: center;

        ::v-deep .user-avatar {
          border: 3px solid rgba($primary-color, 0.15);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
      }

      .info-list {
        margin: 0;
        padding: 0;

        .info-item {
          display: flex;
          align-items: center;
          padding: 14px 16px;
          margin: 8px 0;
          background: rgba($primary-color, 0.03);
          border-radius: 8px;
          transition: all 0.2s;

          &:hover {
            transform: translateX(4px);
            background: rgba($primary-color, 0.08);
          }

          .svg-icon {
            font-size: 18px;
            color: $primary-color;
            margin-right: 12px;
          }

          .label {
            font-size: 14px;
            color: $text-regular;
            margin-right: 12px;
          }

          .value {
            margin-left: auto;
            font-weight: 500;
            color: $text-primary;
          }
        }
      }
    }
  }

  // 设置卡片
  .setting-card {
    ::v-deep .setting-tabs {
      .el-tabs__nav-wrap::after {
        background-color: $border-color;
      }

      .el-tabs__item {
        font-size: 15px;
        color: $text-regular;
        padding: 0 28px;

        &.is-active {
          color: $primary-color;
          font-weight: 600;
        }
      }

      .el-tabs__active-bar {
        height: 3px;
        background-color: $primary-color;
      }
    }
  }

  // 响应式布局
  @media (max-width: 992px) {
    padding: 16px;

    .el-col {
      margin-bottom: 16px;

      &.user-card {
        margin-right: 0;
      }
    }

    .info-item {
      padding: 12px !important;

      .label {
        font-size: 13px !important;
      }
    }

    ::v-deep .el-card__header {
      padding: 14px 20px !important;
    }
  }

  @media (max-width: 576px) {
    .el-card {
      border-radius: 8px !important;
    }

    .info-item {
      flex-wrap: wrap;

      .value {
        width: 100%;
        margin-top: 8px;
        padding-left: 34px;
      }
    }
  }
}
</style>
