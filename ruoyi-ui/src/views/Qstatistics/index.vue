<template>
  <!-- 创建el-card组件 -->
  <div>

    <el-card class="chart-card">
      <!-- 用于放置Echarts图表的DOM元素，设置合适的宽高 -->
      <div id="echarts1" style="width: 100%; height: 300px;"></div>
      <el-select v-model="selected" filterable placeholder="请选择" @change="getEcharts3">
        <el-option
          v-for="item in options"
          :key="item"
          :label="item"
          :value="item">
        </el-option>
      </el-select>
      <div id="echarts3" style="width: 100%; height: 400px;"></div>
      <div id="echarts2" style="width: 100%; height: 300px;"></div>
    </el-card>
  </div>

</template>

<script>
import * as echarts from 'echarts';
import {echarts1} from '@/api/system/student';
export default {
  data() {
    return {
      selected: "求实书院",
      options:["令希书院","伯川书院","厚德书院","大煜书院","求实书院","知行书院","笃学书院"],
    }
  },
  mounted() {
    this.getEcharts1();
    this.getEcharts3();
    this.echarts2();
  },
  methods:{
    getEcharts1() {
      echarts1().then(res => {
        this.echarts1(res.data);
      });
    },
    getEcharts3() {
      echarts1().then(res => {
        this.echarts3(res.data, this.selected);
      });
    },
    echarts1(data){
      console.log("data",data)
      var chartDom = document.getElementById('echarts1');
      var myChart = echarts.init(chartDom);

      const cnt = {};
      for(let [k1, v1] of Object.entries(data.before)) {
        let count = 0;
        for(let [k2, v2] of Object.entries(v1)) {
          count += v2.length;
        }
        cnt[k1] = count;
      }

      let xData = [];
      let yData = [];

      for(let [k, v] of Object.entries(cnt)) {
        xData.push(k);
        yData.push(v);
      }

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: xData,
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: yData,
            type: 'bar',
            showBackground: true,
            backgroundStyle: {
              color: 'rgba(180, 180, 180, 0.2)'
            }
          }
        ]
      };

      option && myChart.setOption(option);
    },
    echarts3(data, academy){
      var chartDom = document.getElementById('echarts3');
      var myChart = echarts.init(chartDom);
      let xData = [];
      let yData = [];
      for(let [k1, v1] of Object.entries(data.before)) {
        if(k1 === academy) {
          for(let [k2, v2] of Object.entries(v1)) {
            xData.push(k2);
            yData.push(v2.length);
          }
        }
      }

      const option = {
        grid: {
          bottom: 150
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: xData,
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            interval: 0,
            rotate: 45
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: yData,
            type: 'bar',
            showBackground: true,
            backgroundStyle: {
              color: 'rgba(180, 180, 180, 0.2)'
            }
          }
        ]
      };
      myChart.resize();
      option && myChart.setOption(option);
    },
    echarts2() {
      const chartDom = document.getElementById('echarts2');
      const myChart = echarts.init(chartDom);
      const posList = [
        'left',
        'right',
        'top',
        'bottom',
        'inside',
        'insideTop',
        'insideLeft',
        'insideRight',
        'insideBottom',
        'insideTopLeft',
        'insideTopRight',
        'insideBottomLeft',
        'insideBottomRight'
      ];
      const app = {};
      app.configParameters = {
        rotate: {
          min: -90,
          max: 90
        },
        align: {
          options: {
            left: 'left',
            center: 'center',
            right: 'right'
          }
        },
        verticalAlign: {
          options: {
            top: 'top',
            middle: 'middle',
            bottom: 'bottom'
          }
        },
        position: {
          options: posList.reduce(function (map, pos) {
            map[pos] = pos;
            return map;
          }, {})
        },
        distance: {
          min: 0,
          max: 100
        }
      };
      app.config = {
        rotate: 90,
        align: 'left',
        verticalAlign: 'middle',
        position: 'insideBottom',
        distance: 15,
        onChange: function () {
          const labelOption = {
            rotate: app.config.rotate,
            align: app.config.align,
            verticalAlign: app.config.verticalAlign,
            position: app.config.position,
            distance: app.config.distance
          };
          myChart.setOption({
            series: [
              {
                label: labelOption
              },
              {
                label: labelOption
              },
              {
                label: labelOption
              },
              {
                label: labelOption
              }
            ]
          });
        }
      };
      const labelOption = {
        show: true,
        position: app.config.position,
        distance: app.config.distance,
        align: app.config.align,
        verticalAlign: app.config.verticalAlign,
        rotate: app.config.rotate,
        formatter: '{c}  {name|{a}}',
        fontSize: 16,
        rich: {
          name: {}
        }
      };
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['Forest', 'Steppe']
        },
        toolbox: {
          show: true,
          orient: 'vertical',
          left: 'right',
          top: 'center',
          feature: {
            mark: { show: true },
            dataView: { show: true, readOnly: false },
            magicType: { show: true, type: ['line', 'bar', 'stack'] },
            restore: { show: true },
            saveAsImage: { show: true }
          }
        },
        xAxis: [
          {
            type: 'category',
            axisTick: { show: false },
            data: ['2012', '2012', '2014', '2015', '2016']
          }
        ],
        yAxis: [
          {
            type: 'value'
          }
        ],
        series: [
          {
            name: 'Forest',
            type: 'bar',
            barGap: 0,
            label: labelOption,
            emphasis: {
              focus: 'series'
            },
            data: [320, 332, 301, 334, 390]
          },
          {
            name: 'Steppe',
            type: 'bar',
            label: labelOption,
            emphasis: {
              focus: 'series'
            },
            data: [220, 182, 191, 234, 290]
          }
        ]
      };
      option && myChart.setOption(option);
    }
  },
};
</script>

<style scoped>
.chart-card {
  width: 50%;
  margin: 10px;
  border: 1px solid #ccc;
}
</style>
