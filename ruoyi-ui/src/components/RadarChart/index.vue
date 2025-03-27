<template>
  <div class="radar-chart-container">
    <v-chart
      :options="chartOptions"
      :autoresize="true"
      style="width: 100%; height: 500px"
      @legendselectchanged="handleLegendSelect"
    />
  </div>
</template>

<script>
import ECharts from 'vue-echarts/components/ECharts'
import 'echarts/lib/chart/radar'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/legend'
import 'echarts/lib/component/title'
import 'zrender/lib/svg/svg'
import echarts from "echarts/lib/export";

// 默认五维指标配置
const DEFAULT_INDICATORS = [
  { name: '力量', max: 100 },
  { name: '敏捷', max: 100 },
  { name: '耐力', max: 100 },
  { name: '智力', max: 100 },
  { name: '幸运', max: 100 }
];

export default {
  name: 'FiveDimensionRadar',
  components: {
    'v-chart': ECharts
  },
  props: {
    data: {
      type: Array,
      default: () => [],
      validator: value => {
        return value.every(item =>
          'name' in item &&
          'value' in item &&
          Array.isArray(item.value) &&
          item.value.length === 5
        )
      }
    },
    indicators: {
      type: Array,
      default: () => DEFAULT_INDICATORS,
      validator: value => {
        return value.length === 5 &&
          value.every((item, index) =>
            'name' in item &&
            'max' in item &&
            typeof item.max === 'number' &&
            item.max > 0
          )
      }
    },
    title: {
      type: String,
      default: '五维能力雷达图'
    },
    subtext: {
      type: String,
      default: ''
    }
  },
  computed: {
    chartOptions() {
      return {
        title: {
          text: this.title,
          subtext: this.subtext,
          left: 'center',
          top: 20,
          textStyle: {
            fontSize: 22,
            fontWeight: 'bold',
            color: 'linear-gradient(45deg, #606dbc, #465298)',
            fontFamily: 'Microsoft YaHei'
          },
          subtextStyle: {
            fontSize: 14,
            color: '#888',
            padding: [5, 0]
          }
        },
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(20, 25, 35, 0.95)',
          borderWidth: 0,
          padding: [12, 20],
          borderRadius: 8,
          textStyle: {
            color: '#fff',
            fontSize: 14,
            fontWeight: 300
          },
          formatter: params => {
            const indicator = this.indicators[params.dataIndex] || {};
            return `
              <div style="text-align:center">
                <div style="font-size:16px;color:${params.color};margin-bottom:8px">
                  ${params.seriesName}<br>
                  <span style="font-size:12px">${indicator.name}</span>
                </div>
                <span style="font-size:24px;font-weight:bold">${params.value}</span>
                <span style="font-size:12px;opacity:0.8">/ ${indicator.max}</span>
              </div>`
          }
        },
        legend: {
          data: this.data.map(item => item.name),
          bottom: 20,
          icon: 'circle',
          itemWidth: 10,
          itemHeight: 10,
          itemGap: 20,
          textStyle: {
            color: '#666',
            fontSize: 12,
            padding: [0, 5]
          }
        },
        radar: {
          indicator: this.indicators,
          shape: 'polygon',
          splitNumber: 4,
          radius: '85%',
          center: ['50%', '55%'],
          axisName: {
            color: '#666',
            fontSize: 12,
            formatter: (value, indicator) => {
              return `{a|${value}}\n{b|${indicator.max}}`
            },
            rich: {
              a: {
                fontSize: 12,
                lineHeight: 18
              },
              b: {
                fontSize: 9,
                color: '#999'
              }
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(100, 100, 100, 0.3)',
              type: 'dashed'
            }
          },
          splitLine: {
            lineStyle: {
              color: [
                'rgba(200, 200, 200, 0.1)',
                'rgba(200, 200, 200, 0.2)',
                'rgba(200, 200, 200, 0.3)',
                'rgba(200, 200, 200, 0.4)'
              ],
              width: 1
            }
          },
          splitArea: { show: false } // 隐藏背景色块
        },
        series: this.generateSeries(),
        animationDuration: 1800,
        animationEasing: 'elasticOut',
        animationDelay: function(idx) {
          return idx * 200
        }
      }
    }
  },
  methods: {
    generateSeries() {
      const colors = [
        { main: '#7460F2', area: 'rgba(116, 96, 242, 0.4)' },
        { main: '#23B3EF', area: 'rgba(35, 179, 239, 0.4)' },
        { main: '#4AD991', area: 'rgba(74, 217, 145, 0.4)' }
      ];

      return this.data.map((item, index) => ({
        type: 'radar',
        name: item.name,
        data: [{
          value: item.value,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: {
            width: 2,
            shadowBlur: 8,
            shadowColor: colors[index % 3].main
          }
        }],
        itemStyle: {
          color: colors[index % 3].main,
          borderWidth: 2,
          borderColor: '#fff'
        },
        areaStyle: {
          color: new echarts.graphic.RadialGradient(0.5, 0.5, 0.8, [
            { offset: 0, color: colors[index % 3].area },
            { offset: 1, color: 'rgba(255, 255, 255, 0.8)' }
          ])
        }
      }))
    },
    handleLegendSelect({ name, selected }) {
      this.$emit('legend-change', { name, selected })
    }
  }
}
</script>

<style scoped>
  /* 完全透明的极简容器 */
.radar-chart-container {
  padding: 0 !important;
  background: transparent !important;
  border-radius: 0 !important;
  box-shadow: none !important;
  overflow: visible;
}

/* 移除所有装饰效果 */
.radar-chart-container::before,
.radar-chart-container::after {
  display: none !important;
}

/* 调整图表主体位置 */
.echarts {
  margin: -20px; /* 抵消默认边距 */
}

</style>
