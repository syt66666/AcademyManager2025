<template>
  <div :class="{'hidden': hidden}" class="pagination-container">
    <el-pagination
      :background="background"
      :current-page.sync="currentPage"
      :page-size.sync="pageSize"
      :layout="layout"
      :page-sizes="pageSizes"
      :pager-count="pagerCount"
      :total="total"
      v-bind="$attrs"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script>
import { scrollTo } from '@/utils/scroll-to'

export default {
  name: 'Pagination',
  props: {
    total: {
      required: true,
      type: Number
    },
    page: {
      type: Number,
      default: 1
    },
    limit: {
      type: Number,
      default: 20
    },
    pageSizes: {
      type: Array,
      default() {
        return [10, 20, 30, 50]
      }
    },
    // 移动端页码按钮的数量端默认值5
    pagerCount: {
      type: Number,
      default: document.body.clientWidth < 992 ? 5 : 7
    },
    layout: {
      type: String,
      default: 'total, sizes, prev, pager, next, jumper'
    },
    background: {
      type: Boolean,
      default: true
    },
    autoScroll: {
      type: Boolean,
      default: true
    },
    hidden: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
    };
  },
  computed: {
    currentPage: {
      get() {
        return this.page
      },
      set(val) {
        this.$emit('update:page', val)
      }
    },
    pageSize: {
      get() {
        return this.limit
      },
      set(val) {
        this.$emit('update:limit', val)
      }
    }
  },
  methods: {
    handleSizeChange(val) {
      if (this.currentPage * val > this.total) {
        this.currentPage = 1
      }
      this.$emit('pagination', { page: this.currentPage, limit: val })
      if (this.autoScroll) {
        scrollTo(0, 800)
      }
    },
    handleCurrentChange(val) {
      this.$emit('pagination', { page: val, limit: this.pageSize })
      if (this.autoScroll) {
        scrollTo(0, 800)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.pagination-container {
  margin: 24px 0;
  transition: opacity 0.3s ease;

  &.hidden {
    opacity: 0;
    pointer-events: none;
  }

  ::v-deep .el-pagination {
    // 主要文字颜色
    color: #606266;

    // 常规按钮样式
    .btn-prev,
    .btn-next,
    .el-pager li {
      background: #fff;
      border: 1px solid #e4e7ed;
      border-radius: 6px;
      min-width: 32px;
      height: 32px;
      margin: 0 4px;
      transition: all 0.2s ease;

      &:hover {
        color: #409eff;
        border-color: #c6e2ff;
        background-color: #f0f7ff;
      }
    }

    // 当前页样式
    .el-pager li.active {
      background: #409eff;
      border-color: #409eff;
      color: #fff;
      font-weight: normal;

      &:hover {
        background: #66b1ff;
        border-color: #66b1ff;
      }
    }

    // 每页显示选择器
    .el-pagination__sizes {
      .el-input {
        .el-input__inner {
          border-radius: 6px;
          &:hover {
            border-color: #c6e2ff;
          }
        }
      }
    }

    // 跳转输入框
    .el-pagination__jump {
      .el-pagination__editor {
        border-radius: 6px;
        &:hover {
          border-color: #c6e2ff;
        }
        &:focus {
          border-color: #409eff;
        }
      }
    }

    // 禁用状态
    button.disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

  @media (max-width: 768px) {
    ::v-deep .el-pagination {
      .btn-prev,
      .btn-next,
      .el-pager li {
        min-width: 28px;
        height: 28px;
        margin: 0 2px;
      }

      .el-pagination__sizes,
      .el-pagination__jump {
        margin-top: 8px;
      }
    }
  }
}
</style>
