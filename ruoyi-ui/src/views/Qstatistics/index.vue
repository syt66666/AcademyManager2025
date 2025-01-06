<template>
  <!-- 创建el-card组件 -->
  <div class="container">
    <el-card class="custom-card">
      <!-- 用于放置Echarts图表的DOM元素，设置合适的宽高 -->
      <div id="echarts1" style="width: 100%; height: 300px;"/>
      <div id="echarts3" style="width: 100%; height: 400px;"/>
<!--      <div id="echarts2" style="width: 100%; height: 300px;"/>-->
    </el-card>
    <div class="custom-card">
      <student-index ref="student"/>
    </div>
  </div>

</template>

<script>
import * as echarts from 'echarts';
import StudentIndex from '@/views/Qstatistics/student';
import {echarts1} from '@/api/system/student';
export default {
  components: { StudentIndex },
  data() {
    return {
      selected: "求实书院",
      options: ["令希书院","伯川书院","厚德书院","大煜书院","求实书院","知行书院","笃学书院"],
    }
  },
  mounted() {
    this.getEcharts1();
    this.getEcharts3();
    this.echarts2();
  },
  watch: {
    selected(newVal, oldVal) {
      // 当 academy 发生变化时，刷新数据
      this.getEcharts3();
    }
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
    echarts1(data) {
      var chartDom = document.getElementById('echarts1');
      var myChart = echarts.init(chartDom);

      const beforeCnt = {};  // 转专业前的学生数量
      const afterCnt = {};   // 转专业后的学生数量

      // 处理转专业前的数据
      for (let [academy, majors] of Object.entries(data.before)) {
        let count = 0;
        for (let [major, students] of Object.entries(majors)) {
          count += students.length;
        }
        beforeCnt[academy] = count;
      }

      // 处理转专业后的数据
      for (let [academy, majors] of Object.entries(data.after)) {
        let count = 0;
        for (let [major, students] of Object.entries(majors)) {
          count += students.length;
        }
        afterCnt[academy] = count;
      }

      let xData = [];
      let beforeData = [];
      let afterData = [];

      // 准备数据
      for (let [academy, count] of Object.entries(afterCnt)) {
        xData.push(academy);
        beforeData.push(beforeCnt[academy] || 0);  // 如果转专业前没有数据，默认值为0
        afterData.push(count);
      }

      const option = {
        title: {
          text: "专业分流前后书院人数统计"
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['转专业前', '转专业后'],
        },
        xAxis: {
          type: 'category',
          data: xData,  // 书院名称
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
            name: '转专业前',
            type: 'bar',
            data: beforeData,
            barWidth: 20,
            itemStyle: {
              normal: {
                color: '#c23531'  // 设置转专业前的颜色
              }
            },
            label: {
              show: true,  // 显示标签
              position: 'top',  // 标签位置在柱子顶部
              formatter: '{c}',  // 显示数据的值
              fontSize: 14,  // 标签字体大小
              color: '#000000'  // 标签字体颜色
            }
          },
          {
            name: '转专业后',
            type: 'bar',
            data: afterData,
            barWidth: 20,
            itemStyle: {
              normal: {
                color: '#2b76de'  // 设置转专业后的颜色
              }
            },
            label: {
              show: true,  // 显示标签
              position: 'top',  // 标签位置在柱子顶部
              formatter: '{c}',  // 显示数据的值
              fontSize: 14,  // 标签字体大小
              color: '#000000'  // 标签字体颜色
            }
          }
        ]
      };

      option && myChart.setOption(option);

      // 注册点击事件（可选）
      myChart.getZr().off('click');
      const that = this;
      myChart.getZr().on('click', function(param) {
        const pointInPixel = [param.offsetX, param.offsetY];
        if (myChart.containPixel('grid', pointInPixel)) {
          const xIndex = myChart.convertFromPixel({seriesIndex: 0}, [param.offsetX, param.offsetY])[0];
          const value = option.xAxis.data[xIndex];
          that.$refs.student.academy = value;
          that.selected = value;
        }
      });
    },
    // echarts3(data, academy){
    //   console.log(academy)
    //   var chartDom = document.getElementById('echarts3');
    //   var myChart = echarts.init(chartDom);
    //   let xData = [];
    //   let yData = [];
    //   for(let [k1, v1] of Object.entries(data.after)) {
    //     if(k1 === academy) {
    //       for(let [k2, v2] of Object.entries(v1)) {
    //         xData.push(k2);
    //         yData.push(v2.length);
    //       }
    //     }
    //   }
    //
    //   const option = {
    //     title: {
    //       text: "专业分流后各专业人数统计"
    //     },
    //     grid: {
    //       bottom: 150
    //     },
    //     tooltip: {
    //       trigger: 'axis',
    //       axisPointer: {
    //         type: 'shadow'
    //       }
    //     },
    //     xAxis: {
    //       type: 'category',
    //       data: xData,
    //       axisTick: {
    //         alignWithLabel: true
    //       },
    //       axisLabel: {
    //         interval: 0,
    //         rotate: 45
    //       }
    //     },
    //     yAxis: {
    //       type: 'value'
    //     },
    //     series: [
    //       {
    //         data: yData,
    //         type: 'bar',
    //         showBackground: true,
    //         backgroundStyle: {
    //           color: 'rgba(180, 180, 180, 0.2)'
    //         }
    //       }
    //     ]
    //   };
    //   myChart.resize();
    //   option && myChart.setOption(option);
    //
    //   // 注册事件前先销毁点击事件（避免点击事件会重复执行）
    //   myChart.getZr().off('click');
    //   const that = this;
    //   // 注册区域的点击事件，解决数据较少时不方便点击柱状图的问题
    //   myChart.getZr().on('click', function(param) {
    //     const pointInPixel= [param.offsetX, param.offsetY];
    //     if (myChart.containPixel('grid',pointInPixel)) {
    //       const xIndex = myChart.convertFromPixel({seriesIndex:0},[param.offsetX, param.offsetY])[0];
    //       that.$refs.student.major = option.xAxis.data[xIndex];
    //     }
    //   });
    // },
    echarts3(data, academy) {
      var chartDom = document.getElementById('echarts3');
      var myChart = echarts.init(chartDom);

      // 定义所有可能的专业列表（横坐标）
      let allMajors;  // 声明变量，作用域外可访问
      if(academy==='求实书院'){
        allMajors = ["软件工程", "网络工程", "集成电路设计与集成系统", "电子科学与技术"];
      }
      else if(academy==='令希书院'){
        allMajors = ["智能建造", "水利水电工程", "港口航道与海岸工程", "海洋资源开发技术", "交通工程", "工程管理", "建筑环境与能源应用工程", "土木工程", "工程力学", "飞行器设计与工程", "船舶与海洋工程", "建筑学", "城乡规划"];
      }
      else if(academy==='伯川书院'){
        allMajors = ["智能制造工程", "能源与动力工程", "机械设计制造及其自动化", "车辆工程（英语强化）", "测控技术与仪器", "金属材料工程", "功能材料", "材料成型及控制工程", "生物医学工程", "金属材料工程", "功能材料", "材料成型及控制工程", "能源与动力工程", "生物医学工程"];
      }
      else if(academy==='厚德书院'){
        allMajors = ["金融学", "工商管理", "国际经济与贸易", "知识产权", "公共事业管理", "马克思主义理论", "广播电视学", "汉语言文学", "英语", "翻译", "日语", "建筑学", "城乡规划", "视觉传达设计", "环境设计", "雕塑", "运动训练"];
      }
      else if(academy==='大煜书院'){
        allMajors = ["精细化工", "化学工程与工艺", "制药工程", "高分子材料与工程", "安全工程", "过程装备与控制工程", "环境科学", "环境工程", "生物工程"];
      }
      else if(academy==='知行书院'){
        allMajors = ["电气工程及其自动化", "自动化", "电子信息工程", "计算机科学与技术", "生物医学工程", "光电信息科学与工程", "大数据管理与应用", "信息管理与信息系统"];
      }
      else if(academy==='笃学书院'){
        allMajors = ["数学与应用数学", "信息与计算科学", "应用物理学", "应用化学", "工程力学", "生物工程"];
      }

      let xData = allMajors;
      let yData = new Array(allMajors.length).fill(0); // 初始化所有专业的人数为 0

      // 遍历数据，根据数据更新 yData
      for (let [k1, v1] of Object.entries(data.before)) {
        if (k1 === academy) {
          for (let [k2, v2] of Object.entries(v1)) {
            // 如果当前专业存在于 allMajors 中，则累计该专业的学生人数
            let majorIndex = allMajors.indexOf(k2); // 获取专业名称在 allMajors 中的位置
            if (majorIndex !== -1) {
              yData[majorIndex] += v2.length; // 增加该专业的学生人数
            }
          }
        }
      }

      const option = {
        title: {
          text: "专业分流后各专业人数统计"
        },
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

      // 注册事件前先销毁点击事件（避免点击事件会重复执行）
      myChart.getZr().off('click');
      const that = this;
      // 注册区域的点击事件，解决数据较少时不方便点击柱状图的问题
      myChart.getZr().on('click', function(param) {
        const pointInPixel = [param.offsetX, param.offsetY];
        if (myChart.containPixel('grid', pointInPixel)) {
          const xIndex = myChart.convertFromPixel({ seriesIndex: 0 }, [param.offsetX, param.offsetY])[0];
          that.$refs.student.major = option.xAxis.data[xIndex];
        }
      });
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
    },
  },
};
</script>

<style scoped>
.container {
  width: 100%;
  height: 90vh;
  display: flex;
  justify-content: space-around;
}

.custom-card {
  width: 50%;
  flex: 1;
  margin: 5px;
  border: 1px solid #ccc;
}
</style>
