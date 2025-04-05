<template>
  <div class="countdown-container">
    <div class="time-row">
      <div class="time-block">
        <div class="time-label">开始时间</div>
        <div class="time-value">{{ formattedStartTime }}</div>
      </div>

      <div class="countdown-block">
        <div class="digit-container">
          <div class="digit-box" ref="days">{{ days }}</div>
          <span class="digit-unit">天</span>
          <div class="digit-box" ref="hours">{{ hours }}</div>
          <span class="colon">:</span>
          <div class="digit-box" ref="minutes">{{ minutes }}</div>
          <span class="colon">:</span>
          <div class="digit-box" ref="seconds">{{ seconds }}</div>
        </div>
      </div>

      <div class="time-block">
        <div class="time-label">结束时间</div>
        <div class="time-value">{{ formattedEndTime }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  props: {
    startTime: {
      type: [Number, String],
      required: true
    },
    endTime: {
      type: [Number, String],
      required: true
    }
  },
  data() {
    return {
      days: 0,
      hours: 0,
      minutes: 0,
      seconds: 0,
      timer: null
    }
  },
  computed: {
    formattedStartTime() {
      return dayjs(Number(this.startTime)).format('YYYY-MM-DD HH:mm')
    },
    formattedEndTime() {
      return dayjs(Number(this.endTime)).format('YYYY-MM-DD HH:mm')
    }
  },
  watch: {
    days(newVal, oldVal) {
      if (newVal !== oldVal) this.triggerAnimation('days')
    },
    hours(newVal, oldVal) {
      if (newVal !== oldVal) this.triggerAnimation('hours')
    },
    minutes(newVal, oldVal) {
      if (newVal !== oldVal) this.triggerAnimation('minutes')
    },
    seconds(newVal, oldVal) {
      if (newVal !== oldVal) this.triggerAnimation('seconds')
    }
  },
  mounted() {
    this.updateCountdown()
    this.timer = setInterval(this.updateCountdown, 1000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    updateCountdown() {
      const now = Date.now()
      const diff = Math.max(Number(this.endTime) - now, 0)

      // 计算时间差
      this.days = Math.floor(diff / (1000 * 60 * 60 * 24))
      this.hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      this.minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
      this.seconds = Math.floor((diff % (1000 * 60)) / 1000)
    },
    triggerAnimation(refName) {
      const element = this.$refs[refName]
      element.classList.add('updating')
      setTimeout(() => {
        element.classList.remove('updating')
      }, 600)
    }
  }
}
</script>

<style scoped>
/* 保持原有的样式不变 */
.countdown-container {
  background: #f8f9fa;
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s ease;
}

.countdown-container:hover {
  transform: translateY(-2px);
}

.time-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.time-block {
  flex-shrink: 0;
}

.time-label {
  color: #8d99ae;
  font-size: 12px;
  margin-bottom: 6px;
  letter-spacing: 0.5px;
}

.time-value {
  color: #4a4e69;
  font-size: 16px;
  font-family: 'Helvetica Neue', sans-serif;
  font-weight: 500;
}

.countdown-block {
  flex-grow: 1;
  text-align: center;
}

.digit-container {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 12px rgba(142, 141, 208, 0.08);
}

.digit-box {
  background: #f1f3f5;
  color: #5c7cfa;
  font-size: 24px;
  font-weight: 400;
  padding: 8px 16px;
  border-radius: 8px;
  min-width: 40px;
  font-family: 'Helvetica Neue', monospace;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.digit-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(120deg,
  rgba(255, 255, 255, 0) 0%,
  rgba(255, 255, 255, 0.4) 50%,
  rgba(255, 255, 255, 0) 100%
  );
  opacity: 0;
  transition: opacity 0.3s;
}

.digit-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(92, 124, 250, 0.2);
}

.digit-box:hover::before {
  opacity: 1;
}

@keyframes digitChange {
  0% {
    transform: translateY(0);
    opacity: 1
  }
  50% {
    transform: translateY(-10px);
    opacity: 0
  }
  51% {
    transform: translateY(10px);
    opacity: 0
  }
  100% {
    transform: translateY(0);
    opacity: 1
  }
}

.digit-box.updating {
  animation: digitChange 0.6s ease;
}

.colon {
  color: #adb5bd;
  font-size: 24px;
  margin: 0 2px;
  font-weight: 300;
  transition: color 0.3s;
}

.digit-unit {
  color: #748ffc;
  font-size: 12px;
  margin-right: 8px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .countdown-container {
    padding: 20px;
    margin: 16px;
  }

  .digit-box {
    font-size: 20px;
    padding: 6px 12px;
    min-width: 36px;
  }

  .colon {
    font-size: 20px;
  }
}
</style>
