<template>
  <div class="main">
    <!-- 上半部分，放置四个echarts图 -->
    <el-col span="24">
      <el-card class="upper-section">
        <div class="echarts-container">
          <div ref="echarts1" class="echarts-item"></div>
          <div ref="echarts2" class="echarts-item"></div>
          <div ref="echarts3" class="echarts-item"></div>
          <div ref="echarts4" class="echarts-item"></div>
        </div>
      </el-card>
    </el-col>
    <!-- 下半部分，分为左右两部分 -->
    <el-col span="24">
      <el-row>
        <!-- 下半部分左边 -->
        <el-col :span="12">
          <el-card class="major-selection">
            <div class="card-content">
              <p>专业选择</p>
              <div class="select-wrapper">
                <el-select v-model="selectedOption" placeholder="请选择">
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </div>
              <div class="button-wrapper">
                <el-button type="primary" style="margin-right: 30px">保存</el-button>
                <el-button type="primary">提交</el-button>
              </div>
            </div>
          </el-card>
          <el-card class="major-table">
            <div class="majorTable">
              <el-table :data="tableData">
                <el-table-column prop="index" label="序号"></el-table-column>
                <el-table-column prop="major" label="专业名称"></el-table-column>
                <el-table-column prop="count" label="已选学生数量"></el-table-column>
              </el-table>
            </div>
          </el-card>
        </el-col>
        <!-- 下半部分右边 -->
        <el-col :span="12">
          <el-card class="lower-right-section">
            <div ref="echartsChart" class="echarts-item" style="height: 400px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-col>
  </div>
</template>

<script>
import echarts from 'echarts';

export default {
  data() {
    return {
      selectedOption: '',
      options: [
        {
          value: 'option1',
          label: '软件工程'
        },
        {
          value: 'option2',
          label: '微电子'
        },
        {
          value: 'option3',
          label: '网络工程'
        },
        {
          value: 'option4',
          label: '电子科学与技术'
        }
      ],
      tableData: [
        { index: 1,major: "软件工程", count: 300 },
        { index: 2,major: "微电子", count: 180 },
        { index: 3,major: "网络工程", count: 190 },
        { index: 4,major: "电子科学与技术", count: 170 }
      ]
    };
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      // 获取 div 容器的引用
      const chartElement = this.$refs.echartsChart;

      // 初始化 ECharts 实例
      const myChart = echarts.init(chartElement);

      // 设置图表的配置项（你提供的 option）
      const option = {
        xAxis: {
          type: 'category',
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [120, 200, 150, 80, 70, 110, 130],
            type: 'bar'
          }
        ]
      };

      // 使用刚定义的配置项更新图表
      myChart.setOption(option);
    }
  }
};
</script>

<style scoped>
.main {
  margin: 0;
  width: 100%;
  padding: 20px;
}
.upper-section {
  height: 25vh;
  margin-bottom: 20px;
}
.echarts-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
}
.echarts-item {
  width: 25vh;
  height: 18vh;
  background-color: #f4f4f4;
}
.major-selection{
  margin-right: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.major-table{
  margin-top: 20px;
  margin-right: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.majorTable {
  width: 800px;
  height: 100%;
}

.card-content {
  text-align: center;
}

.select-wrapper {
  margin-top: 10px;
}

.button-wrapper {
  margin-top: 10px;
}
.lower-right-section {
  height: 300px;
}
</style>
