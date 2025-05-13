<template>
  <div class="container">
    <!-- 左侧部分 (echarts 图表部分) -->
    <div class="left-container">
      <el-card class="custom-card">
        <!-- 滑动按钮 -->
        <div class="switch-container">
          <el-switch
            v-model="isAfterMajorChange"
            active-text="转专业后"
            inactive-text="分流后"
            @change="updateChart"
          />
        </div>
        <!-- 用于放置Echarts图表的DOM元素 -->
        <div id="echarts1" class="echart-container" />
        <div id="echarts3" class="echart-container" />
        <div id="changeTypeChart" class="echart-container" />
      </el-card>
    </div>
    <div class="right-container">
      <div id="echarts-container" class="echart-container"></div>
      <div class="student">
        <student-index ref="student" />
      </div>
    </div>
  </div>
</template>


<script>
import * as echarts from 'echarts';
import StudentIndex from '@/views/Qstatistics/student';
import {fetchEchartsData2} from "@/api/system/questionnaire";
export default {
  components: { StudentIndex },
  data() {
    return {
      isAfterMajorChange: false,//初始状态为分流后
      academyChangeType : { '1': 0, '2': 0, '3': 0, '4': 0, '5': 0, 'Unknown': 0 },
      selected: null,
      options: ["令希书院","伯川书院","厚德书院","大煜书院","求实书院","知行书院","笃学书院"],
    }
  },
  mounted() {
    this.isAfterMajorChange = false; // 确保状态初始化
    this.getEcharts1();
    //this.getEcharts3();
  },
  watch: {
    selected(newVal, oldVal) {
      if(this.selected!==null){
        this.noHideCharts();
        this.getEcharts3();
        this.renderChangeTypePieChart(this.academyChangeType[this.selected], 'echarts-container');
      }else {
        // 如果 selected 为 null，隐藏图表
        this.hideCharts();
      }
    },
    isAfterMajorChange(newVal) {
      // 在状态改变时执行的逻辑，可以用于触发其他操作
      this.getEcharts1();
      this.selected=null;

    }
  },
  methods:{
    noHideCharts() {
      // 假设你有一个容器 ID 为 'echarts-container'
      const chartContainer = document.getElementById('echarts3');
      if (chartContainer) {
        chartContainer.style.display = 'block';
      }
      const chartContainer2 = document.getElementById('changeTypeChart');
      if (chartContainer2) {
        chartContainer2.style.display = 'block';
      }
      const chartContainer3 = document.getElementById('echarts-container');
      if (chartContainer3) {
        chartContainer3.style.display = 'block';
      }
    },
    hideCharts() {
      // 假设你有一个容器 ID 为 'echarts-container'
      const chartContainer = document.getElementById('echarts3');
      if (chartContainer) {
        chartContainer.style.display = 'none';
      }
      const chartContainer2 = document.getElementById('changeTypeChart');
      if (chartContainer2) {
        chartContainer2.style.display = 'none';
      }
      const chartContainer3 = document.getElementById('echarts-container');
      if (chartContainer3) {
        chartContainer3.style.display = 'none';
      }
    },
    echarts1(data) {
      var chartDom = document.getElementById('echarts1');
      var myChart = echarts.init(chartDom);

      // 固定书院顺序
      const fixedAcademies = [
        '大煜书院',
        '伯川书院',
        '令希书院',
        '厚德书院',
        '知行书院',
        '笃学书院',
        '求实书院'
      ];

      // 初始化数据结构
      let xData = [...fixedAcademies];
      let beforeData = new Array(fixedAcademies.length).fill(0);
      let afterData = new Array(fixedAcademies.length).fill(0);
      this.academyChangeType = {};

      // 获取数据源
      const afterCnt = this.isAfterMajorChange ? data.afterCnt2 : data.afterCnt1;
      const afterMajorChangeType = this.isAfterMajorChange
        ? data.afterMajorChangeType
        : data.changeMajorType;
      const beforeCountConfig = {
        '大煜书院': 395,
        '伯川书院': 674,
        '令希书院': 446,
        '厚德书院': 519,
        '知行书院': 962,
        '笃学书院': 363,
        '求实书院': 657
      };
      // 填充数据
      fixedAcademies.forEach((academy, index) => {
        // 分流前数据
        beforeData[index] = beforeCountConfig[academy] || 0; // 安全取值

        // 分流后数据
        let afterTotal = 0;
        if (afterCnt?.[academy]) {
          afterTotal = Object.values(afterCnt[academy]).reduce((sum, val) => sum + val, 0);
        }
        afterData[index] = afterTotal;

        // 初始化类型统计
        this.academyChangeType[academy] = { '1': 0, '2': 0, '3': 0, '4': 0, '5': 0 };
        if (afterMajorChangeType?.[academy]) {
          Object.values(afterMajorChangeType[academy]).forEach(majors => {
            Object.entries(majors).forEach(([type, count]) => {
              this.academyChangeType[academy][type] = (this.academyChangeType[academy][type] || 0) + count;
            });
          });
        }
      });

      // ECharts配置
      const option = {
        title: {
          text: "专业分流前后书院人数统计",
          left: 'center',
          textStyle: {
            fontSize: 18,
            fontWeight: 'bold',
            color: '#333'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: params => {
            const before = params[0];
            const after = params[1];
            return `${before.name}<br>
          分流前: ${before.value}人<br>
          分流后: ${after.value}人<br>
          变化量: ${after.value - before.value}人`;
          }
        },
        legend: {
          data: ['专业分流前', '专业分流后'],
          top: '10%'
        },
        grid: {
          bottom: 80,
          left: 50,
          right: 50
        },
        xAxis: {
          type: 'category',
          data: xData,
          axisLabel: {
            rotate: 45,
            fontSize: 12
          }
        },
        yAxis: {
          type: 'value',
          splitLine: { show: false }
        },
        series: [
          {
            name: '专业分流前',
            type: 'bar',
            data: beforeData,
            barWidth: 30,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#ff7f7f' },
                { offset: 1, color: '#ff3d3d' }
              ]),
              borderRadius: [10, 10, 0, 0]
            },
            label: { show: true, position: 'top' }
          },
          {
            name: '专业分流后',
            type: 'bar',
            data: afterData,
            barWidth: 30,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#4c9aff' },
                { offset: 1, color: '#1d76d1' }
              ]),
              borderRadius: [10, 10, 0, 0]
            },
            label: { show: true, position: 'top' }
          }
        ]
      };

      // 渲染图表
      myChart.setOption(option);

      // 点击事件处理
      const that = this;
      myChart.getZr().off('click');
      myChart.getZr().on('click', function(param) {
        const pointInPixel = [param.offsetX, param.offsetY];
        if (myChart.containPixel('grid', pointInPixel)) {
          const xIndex = myChart.convertFromPixel({ seriesIndex: 0 }, pointInPixel)[0];
          const academy = xData[xIndex];
          that.$refs.student.academy = academy;
          that.selected = academy;
        }
      });
    },
    // 获取转专业类型名称
    getChangeTypeName(changeType) {
      const typeMapping = {
        '1': '保持当前专业',
        '2': '域内任选专业',
        '3': '类内任选专业',
        '4': '拔尖/创新班政策内任选专业',
        '5': '转专业',
        'Unknown': '未知类型'
      };
      return typeMapping[changeType] || changeType;
    },

    // 渲染转专业类型饼图
    renderChangeTypePieChart(academyChangeTypeData, chartDomId) {
      // 检查数据
      console.log('发送的数据:', academyChangeTypeData);
      const chartData =  Object.entries(academyChangeTypeData)
        .filter(([changeType, count]) => changeType !== 'Unknown' && count > 0)
        .map(([changeType, count]) => ({
          name: this.getChangeTypeName(changeType),
          value: count
        }));

      const option = {
        backgroundColor: '#ffffff',
        title: {
          text: `${this.selected}专业分流类型分布`,
          left: 'center',
          textStyle: {
            fontSize: 18,
            fontWeight: 'bold',
            color: '#333'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)',
          backgroundColor: 'rgba(0,0,0,0.7)',
          textStyle: {
            color: '#fff'
          }
        },
        legend: {
          orient: 'vertical',
          right: 'right',
          top: 'center',
          data: chartData.map(item => item.name),
          textStyle: {
            fontSize: 14,
            color: '#555'
          }
        },
        series: [
          {
            name: '转专业类型',
            type: 'pie',
            radius: ['40%', '70%'],
            data: chartData,
            emphasis: {
              itemStyle: {
                color: '#f77f00',
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowOffsetY: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              show: true,
              position: 'outside',
              fontSize: 14,
              color: '#333',
              formatter: '{b}: {c} ({d}%)',
              textStyle: {
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: true,
              length: 15,
              lineStyle: {
                width: 2,
                color: '#aaa'
              }
            },
            itemStyle: {
              normal: {
                borderWidth: 2,
                borderColor: '#fff',
                borderRadius: 5,
                color: function (params) {
                  const colors = [
                    '#ff7f50', '#87cefa', '#32cd32', '#ff6347', '#7b68ee', '#ffa07a',
                    '#3cb371', '#f0e68c', '#dda0dd', '#ff69b4'
                  ];
                  return colors[params.dataIndex % colors.length];
                }
              }
            },
            animationType: 'scale',
            animationEasing: 'elasticOut',
            animationDuration: 1500
          }
        ]
      };

      // 获取 DOM 容器并渲染图表
      const chartDom = document.getElementById(chartDomId);
      if (!chartDom) {
        console.error(`Chart container with ID ${chartDomId} not found!`);
        return;
      }

      const myChart = echarts.init(chartDom);
      myChart.setOption(option);
      myChart.on('click', (params) => {
        console.log('Clicked on:', params.name, 'with value:', params.value);
        let type = null;
        switch (params.name) {
          case '保持当前专业': type = 1; break;
          case '域内任选专业': type = 2; break;
          case '类内任选专业': type = 3; break;
          case '拔尖/创新班政策内任选专业': type = 4; break;
          case '转专业': type = 5; break;
        }
        this.$refs.student.major=null;
        this.$refs.student.type = type;
      });
    },

    getEcharts1() {
      // 调用 fetchEchartsData2 来获取数据
        fetchEchartsData2(3)
          .then(response => {
            this.echarts1(response.data);
          })
    },
    getEcharts3() {
      fetchEchartsData2(3)
        .then(response => {
          this.echarts3(response.data,this.selected);
        });
    },

    echarts3(data, academy) {
      var chartDom = document.getElementById('echarts3');
      var myChart = echarts.init(chartDom);
      let allMajors;
      if(this.isAfterMajorChange===false){
        // // 定义所有可能的专业列表（横坐标）
        // if (academy === '求实书院') {
        //   allMajors = ["软件工程", "网络工程", "集成电路设计与集成系统", "电子科学与技术"];
        // } else if (academy === '令希书院') {
        //   allMajors = ["智能建造", "水利水电工程", "港口航道与海岸工程", "海洋资源开发技术", "交通工程", "工程管理", "建筑环境与能源应用工程", "土木工程", "工程力学", "飞行器设计与工程", "船舶与海洋工程", "建筑学", "城乡规划"];
        // } else if (academy === '伯川书院') {
        //   allMajors = ["智能制造工程", "能源与动力工程", "机械设计制造及其自动化", "车辆工程（英语强化）", "测控技术与仪器", "金属材料工程", "功能材料", "材料成型及控制工程", "生物医学工程"];
        // } else if (academy === '厚德书院') {
        //   allMajors = ["金融学", "工商管理", "国际经济与贸易", "知识产权", "公共事业管理", "马克思主义理论", "广播电视学", "汉语言文学", "英语", "翻译", "日语", "建筑学", "城乡规划", "视觉传达设计", "环境设计", "雕塑", "运动训练"];
        // } else if (academy === '大煜书院') {
        //   allMajors = ["精细化工", "化学工程与工艺", "制药工程", "高分子材料与工程", "安全工程", "过程装备与控制工程", "环境科学", "环境工程", "生物工程","储能科学与工程"];
        // } else if (academy === '知行书院') {
        //   allMajors = ["电气工程及其自动化", "自动化", "电子信息工程", "计算机科学与技术", "生物医学工程", "光电信息科学与工程", "大数据管理与应用", "信息管理与信息系统","金融学", "工商管理", "国际经济与贸易","知识产权","物流管理"];
        // } else if (academy === '笃学书院') {
        //   allMajors = ["数学与应用数学", "信息与计算科学", "应用物理学", "应用化学", "工程力学", "生物工程"];
        // } else {
        //   allMajors = []; // 默认为空数组
        // }
        allMajors = data.afterCnt1 && data.afterCnt1[academy]
          ? Object.keys(data.afterCnt1[academy])
          : [];
      }else{
          // 动态获取当前书院所有存在的专业（来自 afterCnt2 数据）
          allMajors = data.afterCnt2 && data.afterCnt2[academy]
            ? Object.keys(data.afterCnt2[academy])
            : [];
      }

      let xData = allMajors;
      let yData = new Array(allMajors.length).fill(0); // 初始化所有专业的人数为 0
      // 遍历数据，根据数据更新 yData
      if (this.isAfterMajorChange===false&&data.afterCnt1 && data.afterCnt1[academy]) {
        for (let [k2, v2] of Object.entries(data.afterCnt1[academy])) {
          // 如果当前专业存在于 allMajors 中，则累计该专业的学生人数
          let majorIndex = allMajors.indexOf(k2); // 获取专业名称在 allMajors 中的位置
          if (majorIndex !== -1) {
            yData[majorIndex] += v2; // 增加该专业的学生人数
          }
        }
      }
      // 遍历数据，根据数据更新 yData
      if (this.isAfterMajorChange===true&&data.afterCnt2 && data.afterCnt2[academy]) {
        for (let [k2, v2] of Object.entries(data.afterCnt2[academy])) {
          // 如果当前专业存在于 allMajors 中，则累计该专业的学生人数
          let majorIndex = allMajors.indexOf(k2); // 获取专业名称在 allMajors 中的位置
          if (majorIndex !== -1) {
            yData[majorIndex] += v2; // 增加该专业的学生人数
          }
        }
      }
      let type=this.isAfterMajorChange;
      const option = {
        title: {
          text: `${this.selected}分流后各专业人数统计`,
          left: 'center',
          textStyle: {
            fontSize: 18,
            fontWeight: 'bold',
            color: '#333'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: '{b}: {c}' // 显示数量
        },
        grid: {
          bottom: 100,
          left: 50,
          right: 50
        },
        xAxis: {
          type: 'category',
          data: xData,
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            interval: 0,
            rotate: 45,
            fontSize: 12,
            color: '#555'
          },
          axisLine: {
            lineStyle: {
              color: '#ccc'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            fontSize: 12,
            color: '#555'
          },
          axisLine: {
            lineStyle: {
              color: '#ccc'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#f2f2f2'
            }
          }
        },
        series: [
          {
            data: yData,
            type: 'bar',
            barWidth: '50%', // 设置柱子宽度
            itemStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: '#3b82f6' },
                  { offset: 1, color: '#9333ea' }
                ]),
                borderRadius: [10, 10, 0, 0] // 圆角设置
              }
            },
            label: {
              show: true,
              position: 'top',
              formatter: '{c}', // 显示柱状图的值
              fontSize: 14,
              color: '#333'
            },
            emphasis: {
              itemStyle: {
                color: '#f77f00'  // 鼠标悬停时的颜色变化
              }
            },
            backgroundStyle: {
              color: 'rgba(180, 180, 180, 0.2)'
            }
          }
        ]
      };

      myChart.resize();
      option && myChart.setOption(option);
      const that = this;
      myChart.on('click', function(param) {
        if (param.componentType === 'series') {
          const clickedMajor = param.name;  // 获取点击的专业名称

            console.log('this.isAfterMajorChange11:'+type)
          // 获取该专业的专业类型分布数据
          const majorChangeType = getChangeMajorTypeForMajor(data, academy, clickedMajor,type);
          // 显示该专业的转专业类型的比例，可以用饼图展示
          updateChangeTypeChart(majorChangeType,clickedMajor);
          that.$refs.student.major = clickedMajor;
          that.$refs.student.type = null;
        }
      });

      // 获取该专业的转专业类型分布
      function getChangeMajorTypeForMajor(data, academy, major,type) {
        const afterMajorChangeType = {};  // 统计该专业的转专业类型
        // // 遍历数据，统计该专业的转专业类型
        if (type===true&&data.afterMajorChangeType && data.afterMajorChangeType[academy] && data.afterMajorChangeType[academy][major]) {
          for (let [changeType, count] of Object.entries(data.afterMajorChangeType[academy][major])) {
            afterMajorChangeType[changeType] = count;
          }
        }

        // 遍历数据，统计该专业的转专业类型
        if (type===false&&data.changeMajorType && data.changeMajorType[academy] && data.changeMajorType[academy][major]) {
          for (let [changeType, count] of Object.entries(data.changeMajorType[academy][major])) {
            afterMajorChangeType[changeType] = count;
          }
        }
        return afterMajorChangeType;
      }

      // 更新转专业类型分布图
      function updateChangeTypeChart(majorChangeType,clickedMajor) {
        var chartDom = document.getElementById('changeTypeChart');
        var myChart = echarts.init(chartDom);

        // 创建一个映射表，用于替换类型数字
        const typeMapping = {
          '1': '保持当前专业',
          '2': '域内任选专业',
          '3': '类内任选专业',
          '4': '拔尖/创新班政策内任选专业',
          '5': '转专业',
          'Unknown': '未知类型'
        };

        // 根据映射表修改每个类型的名称
        const data = Object.entries(majorChangeType).map(([type, count]) => ({
          name: typeMapping[type] || type,  // 如果没有找到映射，则保持原来的类型
          value: count
        })).filter(item => item.name !== '未知类型');  // 过滤掉 "未知类型"

        const option = {
          title: {
            text: `${clickedMajor}分流类型分布`,
            left: 'center',  // 标题居中
            textStyle: {
              fontSize: 18,
              fontWeight: 'bold',
              color: '#333'  // 标题颜色
            }
          },
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} ({d}%)',
            backgroundColor: 'rgba(0,0,0,0.7)',  // 设置工具提示背景颜色
            textStyle: {
              color: '#fff'  // 设置工具提示文本颜色
            }
          },
          legend: {
            orient: 'vertical',
            right: 'right',
            top: 'center',  // 图例居中
            data: data.map(item => item.name),
            textStyle: {
              fontSize: 14,
              color: '#555'  // 图例字体颜色
            }
          },
          series: [
            {
              name: '专业分流类型',
              type: 'pie',
              radius: ['40%', '70%'],  // 设置内外半径，形成环形图
              data: data,
              emphasis: {
                itemStyle: {
                  color: '#f77f00',  // 强调项的颜色
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowOffsetY: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              label: {
                show: true,
                position: 'outside',
                fontSize: 14,
                color: '#333',
                formatter: '{b}: {c} ({d}%)',  // 显示名称、数量和百分比
                textStyle: {
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: true,
                length: 15,  // 标签线长度
                lineStyle: {
                  width: 2,
                  color: '#aaa'
                }
              },
              itemStyle: {
                normal: {
                  borderWidth: 2,
                  borderColor: '#fff',  // 扇形之间的边框颜色
                  borderRadius: 5,  // 圆角
                  color: function (params) {
                    const colors = [
                      '#ff7f50', '#87cefa', '#32cd32', '#ff6347', '#7b68ee', '#ffa07a',
                      '#3cb371', '#f0e68c', '#dda0dd', '#ff69b4'
                    ];
                    return colors[params.dataIndex % colors.length];  // 使用不同的颜色
                  }
                }
              },
              animationType: 'scale',  // 设置动画类型
              animationEasing: 'elasticOut',  // 动画的缓动效果
              animationDuration: 1500  // 动画持续时间
            }
          ]
        };

        myChart.setOption(option);
        myChart.on('click', (params) => {
          console.log('Clicked on:', params.name, 'with value:', params.value);
          let type = null;
          switch (params.name) {
            case '保持当前专业': type = 1; break;
            case '域内任选专业': type = 2; break;
            case '类内任选专业': type = 3; break;
            case '拔尖/创新班政策内任选专业': type = 4; break;
            case '转专业': type = 5; break;
          }
          that.$refs.student.type = type;
        });
      }
    }

  },
};
</script>
<style scoped>
.container {
  display: flex; /* 使用 flex 布局 */
  justify-content: space-between; /* 在水平上分布子元素 */
  gap: 20px; /* 设置左右容器之间的间距 */
  margin-left: 100px;
}

.left-container {
  width: 60%; /* 左侧部分占屏幕的60% */
  display: flex;
  flex-direction: column; /* 垂直排列图表 */
  gap: 20px; /* 设置图表之间的间距 */
}

.right-container {
  width: 40%; /* 右侧部分占屏幕的35% */
  flex-direction: column; /* 垂直排列图表 */

}

.echart-container {
  width: 100%;
  height: 300px; /* 适当调整图表的高度 */
  min-height: 200px; /* 最小高度 */
}

.student {
  width: 100%;
  background-color: rgba(255, 255, 255, 0);
}

/* 在小屏幕上重新调整 */
@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }

  .left-container, .right-container {
    width: 100%;
  }

  .left-container {
    width: 65%; /* 增加左侧部分宽度 */
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .right-container {
    width: 30%; /* 调整右侧部分宽度 */
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
}
</style>
