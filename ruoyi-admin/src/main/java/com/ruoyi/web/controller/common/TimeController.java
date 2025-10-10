package com.ruoyi.web.controller.common;

import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间控制器
 * 提供服务器时间获取功能
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/common/time")
public class TimeController
{
    /**
     * 获取服务器当前时间
     * 
     * @return 服务器当前时间
     */
    @GetMapping("/now")
    public AjaxResult getServerTime()
    {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        return AjaxResult.success("获取成功", timeStr);
    }
}
