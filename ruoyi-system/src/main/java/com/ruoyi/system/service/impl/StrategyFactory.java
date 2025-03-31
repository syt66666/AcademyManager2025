package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.strategy.InnovationDropStrategy;
import com.ruoyi.system.service.strategy.InnovationKeepStrategy;
import com.ruoyi.system.service.strategy.MajorStrategy;
import com.ruoyi.system.service.strategy.RegularStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// 策略工厂
@Component
public class StrategyFactory {
    private final Map<String, MajorStrategy> strategies = new HashMap<>();

    @Autowired
    public StrategyFactory(
            RegularStrategy regular,
            InnovationKeepStrategy keep,
            InnovationDropStrategy drop
    ) {
        strategies.put("regular", regular);
        strategies.put("keep", keep);
        strategies.put("drop", drop);
    }

    public MajorStrategy getStrategy(Integer innovationStatus, Integer policyStatus) {
        if (innovationStatus == 0) {
            System.out.println("Innovation status is 0, use regular strategy");
            return strategies.get("regular");
        } else {
            System.out.println("Innovation status is 1, use keep or drop strategy"+strategies.get("keep"));
            return policyStatus == 0 ? strategies.get("keep") : strategies.get("drop");
        }
    }
}
