<template>
  <div class="container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-card class="custom-card">
        <div class="loading-content">
          <i class="el-icon-loading"></i>
          <p>正在加载图表数据...</p>
        </div>
      </el-card>
    </div>

    <!-- 左侧部分 (echarts 图表部分) -->
    <div v-else class="left-container">
      <el-card class="custom-card">
        <!-- 用于放置Echarts图表的DOM元素 -->
        <div id="echarts1" class="echart-container" />
        <div id="echarts3" class="echart-container" />
        <div id="changeTypeChart" class="echart-container" />
      </el-card>
    </div>
    <div class="right-container">
      <div id="echarts-container" class="echart-container"></div>
      <div class="student-section">
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
      academyChangeType : { '1': 0, '2': 0, '3': 0, '4': 0, '5': 0, 'Unknown': 0 },
      selected: null,
      options: ["令希书院","伯川书院","厚德书院","大煜书院","求实书院","知行书院","笃学书院"],
      loading: true,
      chartData: null,
      chartInstances: {},
      updateTimer: null
    }
  },
  async mounted() {
    this.loading = true;
    try {
      await this.loadChartData();
      // 确保DOM完全渲染后再初始化图表
      this.$nextTick(() => {
        this.getEcharts1();
      });
    } catch (error) {
      console.error('加载图表数据失败:', error);
      this.$message.error('加载图表数据失败');
    } finally {
      this.loading = false;
    }
  },
  watch: {
    selected(newVal, oldVal) {
      // 清除之前的定时器
      if (this.updateTimer) {
        clearTimeout(this.updateTimer);
      }

      // 使用防抖处理
      this.updateTimer = setTimeout(() => {
        if(this.selected!==null){
          this.noHideCharts();
          this.getEcharts3();
          this.renderChangeTypePieChart(this.academyChangeType[this.selected], 'echarts-container');
        }else {
          // 如果 selected 为 null，显示所有学生的专业分流类型分布
          this.showAllStudentsChangeTypeChart();
        }
      }, 100); // 100ms防抖延迟
    }
  },
  beforeDestroy() {
    // 清理定时器
    if (this.updateTimer) {
      clearTimeout(this.updateTimer);
    }

    // 销毁图表实例
    Object.values(this.chartInstances).forEach(chart => {
      if (chart) {
        chart.dispose();
      }
    });
  },
  methods:{
    // 加载图表数据
    async loadChartData() {
      if (this.chartData) {
        return this.chartData; // 使用缓存数据
      }

      try {
        const response = await fetchEchartsData2(3);
        if (response.code === 200) {
          this.chartData = response.data;
          return this.chartData;
        } else {
          throw new Error('获取数据失败');
        }
      } catch (error) {
        console.error('获取图表数据失败:', error);
        throw error;
      }
    },

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

    // 显示所有学生的专业分流类型分布
    showAllStudentsChangeTypeChart() {
      console.log('showAllStudentsChangeTypeChart 被调用');
      console.log('academyChangeType 数据:', this.academyChangeType);

      // 隐藏其他图表
      const chartContainer = document.getElementById('echarts3');
      if (chartContainer) {
        chartContainer.style.display = 'none';
      }
      const chartContainer2 = document.getElementById('changeTypeChart');
      if (chartContainer2) {
        chartContainer2.style.display = 'none';
      }

      // 显示专业分流类型分布图表
      const chartContainer3 = document.getElementById('echarts-container');
      if (chartContainer3) {
        chartContainer3.style.display = 'block';
        console.log('echarts-container 容器已显示');
      } else {
        console.error('echarts-container 容器不存在');
      }

      // 计算所有学生的专业分流类型分布
      const allStudentsChangeType = { '1': 0, '2': 0, '3': 0, '4': 0, '5': 0 };

      // 遍历所有书院的数据
      if (this.academyChangeType && Object.keys(this.academyChangeType).length > 0) {
        Object.values(this.academyChangeType).forEach(academyData => {
          Object.entries(academyData).forEach(([type, count]) => {
            if (type !== 'Unknown') {
              allStudentsChangeType[type] = (allStudentsChangeType[type] || 0) + count;
            }
          });
        });
        console.log('计算后的所有学生专业分流类型数据:', allStudentsChangeType);
      } else {
        console.warn('academyChangeType 数据为空或未初始化');
      }

      // 渲染所有学生的专业分流类型分布图
      this.renderAllStudentsChangeTypePieChart(allStudentsChangeType, 'echarts-container');
    },

    // 渲染所有学生的专业分流类型分布饼图
    renderAllStudentsChangeTypePieChart(allStudentsChangeTypeData, chartDomId) {
      // 检查数据
      console.log('renderAllStudentsChangeTypePieChart 被调用');
      console.log('所有学生专业分流类型数据:', allStudentsChangeTypeData);
      console.log('图表容器ID:', chartDomId);

      const chartData = Object.entries(allStudentsChangeTypeData)
        .filter(([changeType, count]) => changeType !== 'Unknown' && count > 0)
        .map(([changeType, count]) => ({
          name: this.getChangeTypeName(changeType),
          value: count
        }));

      console.log('处理后的图表数据:', chartData);

      const option = {
        backgroundColor: '#ffffff',
        title: {
          text: '所有学生专业分流类型分布',
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
            name: '专业分流类型',
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

      console.log('找到图表容器:', chartDom);
      console.log('容器尺寸:', chartDom.offsetWidth, 'x', chartDom.offsetHeight);

      // 销毁已存在的图表实例
      if (this.chartInstances[chartDomId]) {
        this.chartInstances[chartDomId].dispose();
      }

      // 创建新的图表实例
      const myChart = echarts.init(chartDom);
      this.chartInstances[chartDomId] = myChart;

      console.log('图表实例已创建:', myChart);

      // 设置图表配置
      myChart.setOption(option);
      console.log('图表配置已设置');

      // 添加点击事件
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
        this.$refs.student.type = type;
      });
    },
    echarts1(data) {
      var chartDom = document.getElementById('echarts1');

      // 检查DOM元素是否存在
      if (!chartDom) {
        console.error('图表容器不存在: echarts1');
        return;
      }

      // 使用缓存的图表实例或创建新的
      if (this.chartInstances.echarts1) {
        this.chartInstances.echarts1.dispose();
      }
      var myChart = echarts.init(chartDom);
      this.chartInstances.echarts1 = myChart;

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

      // 获取数据源 - 固定使用分流后数据
      const afterCnt = data.afterCnt1;
      const afterMajorChangeType = data.changeMajorType;
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

      // ECharts配置 - 优化性能
      const option = {
        animation: false, // 禁用动画提升性能
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

      // 在数据初始化完成后，如果没有选择书院，显示所有学生的专业分流类型分布
      this.$nextTick(() => {
        if (this.selected === null) {
          this.showAllStudentsChangeTypeChart();
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

    async getEcharts1() {
      try {
        const data = await this.loadChartData();
        // 确保DOM已经渲染完成
        this.$nextTick(() => {
          this.echarts1(data);
        });
      } catch (error) {
        console.error('获取图表数据失败:', error);
        this.$message.error('获取图表数据失败');
      }
    },
    async getEcharts3() {
      try {
        const data = await this.loadChartData();
        // 确保DOM已经渲染完成
        this.$nextTick(() => {
          this.echarts3(data, this.selected);
        });
      } catch (error) {
        console.error('获取图表数据失败:', error);
        this.$message.error('获取图表数据失败');
      }
    },

    echarts3(data, academy) {
      var chartDom = document.getElementById('echarts3');

      // 检查DOM元素是否存在
      if (!chartDom) {
        console.error('图表容器不存在: echarts3');
        return;
      }

      // 使用缓存的图表实例或创建新的
      if (this.chartInstances.echarts3) {
        this.chartInstances.echarts3.dispose();
      }
      var myChart = echarts.init(chartDom);
      this.chartInstances.echarts3 = myChart;
      let allMajors;

      // 固定使用分流后数据
      allMajors = data.afterCnt1 && data.afterCnt1[academy]
        ? Object.keys(data.afterCnt1[academy])
        : [];

      let xData = allMajors;
      let yData = new Array(allMajors.length).fill(0); // 初始化所有专业的人数为 0

      // 固定使用分流后数据填充
      if (data.afterCnt1 && data.afterCnt1[academy]) {
        for (let [k2, v2] of Object.entries(data.afterCnt1[academy])) {
          // 如果当前专业存在于 allMajors 中，则累计该专业的学生人数
          let majorIndex = allMajors.indexOf(k2); // 获取专业名称在 allMajors 中的位置
          if (majorIndex !== -1) {
            yData[majorIndex] += v2; // 增加该专业的学生人数
          }
        }
      }
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
          // 获取该专业的专业类型分布数据
          const majorChangeType = getChangeMajorTypeForMajor(data, academy, clickedMajor);
          // 显示该专业的转专业类型的比例，可以用饼图展示
          updateChangeTypeChart(majorChangeType,clickedMajor);
          that.$refs.student.major = clickedMajor;
          that.$refs.student.type = null;
        }
      });

      // 获取该专业的转专业类型分布
      function getChangeMajorTypeForMajor(data, academy, major) {
        const afterMajorChangeType = {};  // 统计该专业的转专业类型

        // 固定使用分流后数据
        if (data.changeMajorType && data.changeMajorType[academy] && data.changeMajorType[academy][major]) {
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
/* 加载状态样式 */
.loading-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
}

.loading-content i {
  font-size: 32px;
  margin-bottom: 16px;
  color: #409eff;
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-content p {
  margin: 0;
  font-size: 14px;
}

.container {
  display: flex; /* 使用 flex 布局 */
  justify-content: space-between; /* 在水平上分布子元素 */
}

.left-container {
  width: 60%; /* 左侧部分占屏幕的60% */
  display: flex;
  flex-direction: column; /* 垂直排列图表 */

}

.right-container {
  width: 40%; /* 右侧部分占屏幕的40% */
  display: flex;
  flex-direction: column; /* 垂直排列图表 */
  gap: 20px; /* 设置图表之间的间距 */
}

.student-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.echart-container {
  width: 100%;
  gap: 10px; /* 设置图表之间的间距 */
  height: 370px; /* 恢复固定高度，避免图表过扁 */
  min-height: 370px; /* 最小高度 */
}

.student {
  width: 100%;
  background-color: rgba(255, 255, 255, 0);
}


.student-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 300px; /* 确保学生信息组件有足够的最小高度 */
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
