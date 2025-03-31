// 策略接口
package com.ruoyi.system.service.strategy;

import com.ruoyi.system.domain.StuMajor;
import java.util.List;

public interface MajorStrategy {
    List<StuMajor> getAvailableMajors(String major, String academy);
}