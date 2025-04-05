<template>
  <div>
    <time-countdown
      :start-time="startTimestamp"
      :end-time="endTimestamp"
    />
  </div>
</template>

<script>
import TimeCountdown from '@/components/FlipCountdown/index'

export default {
  components: {
    TimeCountdown
  },
  computed: {
    // 将时间字符串转换为时间戳
    startTimestamp() {
      return Date.parse('2023-09-01 08:00:00')
    },
    endTimestamp() {
      return Date.parse('2025-09-05 18:00:00')
    }
  }
}
</script>
