<template>
  <div class="main">
    <!-- 上半部分，放置四个echarts图 -->
    <el-col :span="24">
      <el-card class="upper-section">
        <div class="echarts-container">
        </div>
      </el-card>
    </el-col>
    <!-- 下半部分，分为左右两部分 -->
    <el-col :span="24">
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
            <div id="MajorEcharts" style="width: 100%; height: 430px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-col>
  </div>
</template>

<script>
import * as echarts from 'echarts'

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
    this.MajorEcharts();
  },

  methods: {
    MajorEcharts(){
      let myChart = echarts.init(document.getElementById("MajorEcharts"));
      // 固定的四个专业名称
      const categories = ['软件工程', '微电子', '网络工程', '电子科学与技术'];

      // 初始化数据，使用固定的数值或生成一些初始值
      const data = [100, 200, 150, 180]; // 这里是示例数据，可以根据需要调整
      let originalData = [...data]; // 保存原始数据，以防排名变化

      // 图表的配置
      var option = {
        xAxis: {
          max: 'dataMax',
          axisLabel: {
            formatter: '{value}' // 显示轴标签
          }
        },
        yAxis: {
          type: 'category',
          data: categories,  // 固定的四个专业名称
          inverse: true,  // 逆序显示
          animationDuration: 300,
          animationDurationUpdate: 300,
          max: 4 // 只显示四个条目
        },
        series: [
          {
            name: '专业数据',
            type: 'bar',
            data: originalData,  // 使用固定的数据，不受实时变化影响
            label: {
              show: true,
              position: 'right',
              valueAnimation: true
            },
            itemStyle: {
              color: '#3398DB'  // 设置柱状图的颜色
            }
          }
        ],
        legend: {
          show: true
        },
        animationDuration: 0,
        animationDurationUpdate: 3000,
        animationEasing: 'linear',
        animationEasingUpdate: 'linear'
      };

      // 更新数据的方法
      function run() {
        // 模拟数据变化，但不改变专业的排名顺序
        for (var i = 0; i < data.length; ++i) {
          if (Math.random() > 0.9) {
            data[i] += Math.round(Math.random() * 2000);
          } else {
            data[i] += Math.round(Math.random() * 200);
          }
        }

        // 使用原始排序的数据，但更新它的数值
        let sortedData = data.slice().sort((a, b) => b - a);  // 按照值降序排序

        myChart.setOption({
          series: [
            {
              type: 'bar',
              data: sortedData
            }
          ]
        });
      }

      // 初始化时立即调用 run()
      setTimeout(function () {
        run();
      }, 0);

      // 每3秒更新一次数据
      setInterval(function () {
        run();
      }, 3000);

      // 设置图表的初始配置
      myChart.setOption(option);
    },
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
  min-height: 400px;
}
</style>
