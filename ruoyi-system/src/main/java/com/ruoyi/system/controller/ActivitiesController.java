package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Activities;
import com.ruoyi.system.service.IActivitiesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisCache;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 活动信息 Controller
 */
@RestController
@RequestMapping("/system/activities")
public class ActivitiesController extends BaseController {
    @Autowired
    private IActivitiesService activityService;
    
    @Autowired
    private RedisCache redisCache;

    /**
     * 查询活动列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Activities activity) {
        startPage();
        List<Activities> list = activityService.selectActivityList(activity);
        return getDataTable(list);
    }

    /**
     * 导出活动列表
     */
    @Log(title = "导出数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Activities activities) {
        List<Activities> list = activityService.selectActivityList(activities);
        ExcelUtil<Activities> util = new ExcelUtil<Activities>(Activities.class);
        util.exportExcel(response, list, "导出数据");
    }

    /**
     * 获取活动详细信息
     */
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Integer activityId) {
        return AjaxResult.success(activityService.selectActivityById(activityId));
    }

    /**
     * 新增活动
     */
    @Log(title = "活动信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Activities activity) {
        return toAjax(activityService.insertActivity(activity));
    }

    /**
     * 修改活动
     */
    @Log(title = "活动信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult edit(@RequestBody Activities activity) {
        // 检查容量是否增加，如果增加则清空扩容申请数据
        if (activity.getActivityId() != null && activity.getActivityTotalCapacity() != null) {
            Activities oldActivity = activityService.selectActivityById(activity.getActivityId());
            if (oldActivity != null && oldActivity.getActivityTotalCapacity() != null) {
                // 如果新容量大于旧容量（扩容了），清空扩容申请数据
                if (activity.getActivityTotalCapacity() > oldActivity.getActivityTotalCapacity()) {
                    clearExpandRequestData(activity.getActivityId());
                }
            }
        }
        return toAjax(activityService.updateActivity(activity));
    }
    /**
     * 修改活动
     */
    @Log(title = "活动信息", businessType = BusinessType.UPDATE)
    @PostMapping("/updateActivity")
    public AjaxResult edit2(@RequestBody Activities activity) {
        // 检查容量是否增加，如果增加则清空扩容申请数据
        if (activity.getActivityId() != null && activity.getActivityTotalCapacity() != null) {
            Activities oldActivity = activityService.selectActivityById(activity.getActivityId());
            if (oldActivity != null && oldActivity.getActivityTotalCapacity() != null) {
                // 如果新容量大于旧容量（扩容了），清空扩容申请数据
                if (activity.getActivityTotalCapacity() > oldActivity.getActivityTotalCapacity()) {
                    clearExpandRequestData(activity.getActivityId());
                }
            }
        }
        return toAjax(activityService.updateActivity2(activity));
    }
    /**
     * 删除活动
     */
    @Log(title = "活动信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Integer[] activityIds) {
        // 删除活动时，清空所有相关活动的扩容申请数据
        if (activityIds != null) {
            for (Integer activityId : activityIds) {
                clearExpandRequestData(activityId);
            }
        }
        return toAjax(activityService.deleteActivityByIds(activityIds));
    }

    /**
     * 导入活动列表
     */
    @Log(title = "活动信息管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<Activities> util = new ExcelUtil<Activities>(Activities.class);
        List<Activities> userList = util.importExcel(file.getInputStream());
        LoginUser loginUser = getLoginUser();
        String operName = loginUser.getUsername();
        String message = activityService.importActivity(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Activities> util = new ExcelUtil<>(Activities.class);
        util.importTemplateExcel(response, "活动数据");
    }

    /**
     * 报名 - 基于实际预约人数更新容量
     *
     * @param params
     * @return
     */
    @PostMapping("/signUpCapacity")
    public AjaxResult signUpCapacity(@RequestBody Map<String, Integer> params) {
        Integer activityId = params.get("activityId");
        Integer version = params.get("version"); // 获取请求体中的 version
        
        try {
            int result = activityService.updateCapacityByActualBookings(activityId, version);
            return result > 0 ? AjaxResult.success("报名成功") : AjaxResult.error("报名失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 取消报名 - 基于实际预约人数更新容量
     *
     * @param params
     * @return
     */
    @PostMapping("/cancelSignUpCapacity")
    public AjaxResult cancelSignUpCapacity(@RequestBody Map<String, Integer> params) {
        Integer activityId = params.get("activityId");
        Integer version = params.get("version");
        
        try {
            int result = activityService.updateCapacityByActualBookings(activityId, version);
            return result > 0 ? AjaxResult.success("取消报名成功") : AjaxResult.error("取消报名失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取活动当前预约人数
     *
     * @param activityId 活动ID
     * @return 预约人数
     */
    @GetMapping("/bookingCount/{activityId}")
    public AjaxResult getBookingCount(@PathVariable Integer activityId) {
        try {
            int count = activityService.getCurrentBookingCount(activityId);
            return AjaxResult.success(count);
        } catch (Exception e) {
            return AjaxResult.error("获取预约人数失败: " + e.getMessage());
        }
    }

    /**
     * 检查活动剩余容量
     *
     * @param activityId 活动ID
     * @return 剩余容量（总容量 - 已选人数）
     */
    @GetMapping("/remainingCapacity/{activityId}")
    public AjaxResult getRemainingCapacity(@PathVariable Integer activityId) {
        try {
            int capacity = activityService.checkActivityCapacity(activityId);
            return AjaxResult.success(capacity);
        } catch (Exception e) {
            return AjaxResult.error("获取剩余容量失败: " + e.getMessage());
        }
    }

    /**
     * 检查活动名称和组织单位的唯一性
     */
    @PostMapping("/checkUnique")
    public AjaxResult checkUnique(@RequestBody Map<String, Object> params) {
        String activityName = (String) params.get("activityName");
        String organizer = (String) params.get("organizer");
        Integer activityId = (Integer) params.get("activityId");
        
        boolean isUnique = activityService.checkActivityUnique(activityName, organizer, activityId);
        return AjaxResult.success(isUnique);
    }

    /**
     * 申请扩容 - 使用Redis原子性操作增加扩容申请数量
     * 每个用户对于同一活动只能申请一次
     *
     * @param params 包含activityId的Map
     * @return 扩容申请成功后的数量
     */
    @PostMapping("/requestExpand")
    public AjaxResult requestExpand(@RequestBody Map<String, Integer> params) {
        Integer activityId = params.get("activityId");
        if (activityId == null) {
            return AjaxResult.error("活动ID不能为空");
        }
        
        try {
            // 获取当前登录用户
            String username = getUsername();
            if (username == null || username.isEmpty()) {
                return AjaxResult.error("获取用户信息失败，请先登录");
            }
            
            // 构建用户申请记录的Redis键：activity:expand:user:{activityId}
            String userSetKey = "activity:expand:user:" + activityId;
            
            // 检查用户是否已经申请过扩容
            @SuppressWarnings("unchecked")
            Boolean isMember = redisCache.redisTemplate.opsForSet().isMember(userSetKey, username);
            if (Boolean.TRUE.equals(isMember)) {
                return AjaxResult.error("您已经申请过扩容，每个活动只能申请一次");
            }
            
            // 构建扩容申请总数的Redis键：activity:expand:request:{activityId}
            String countKey = "activity:expand:request:" + activityId;
            
            // 检查计数key是否存在，用于判断是否需要设置过期时间
            Boolean countKeyExists = redisCache.hasKey(countKey);
            
            // 检查用户集合是否存在，用于判断是否需要设置过期时间
            Boolean userSetExists = redisCache.hasKey(userSetKey);
            
            // 使用Redis INCR原子性操作增加计数
            @SuppressWarnings("unchecked")
            Long count = redisCache.redisTemplate.opsForValue().increment(countKey);
            
            // 将用户添加到申请集合中（原子性操作，防止重复申请）
            @SuppressWarnings({"unchecked", "unused"})
            Long added = redisCache.redisTemplate.opsForSet().add(userSetKey, username);
            
            // 设置过期时间为365天（可根据需要调整）
            // 如果countKey不存在（新创建的），设置过期时间
            if (count != null && (countKeyExists == null || !countKeyExists)) {
                redisCache.expire(countKey, 365, TimeUnit.DAYS);
            }
            // 如果用户集合不存在（新创建的），设置过期时间
            if (userSetExists == null || !userSetExists) {
                redisCache.expire(userSetKey, 365, TimeUnit.DAYS);
            }
            
            return AjaxResult.success("申请扩容成功", count);
        } catch (Exception e) {
            return AjaxResult.error("申请扩容失败: " + e.getMessage());
        }
    }

    /**
     * 获取扩容申请数量
     *
     * @param activityId 活动ID
     * @return 扩容申请数量
     */
    @GetMapping("/expandRequestCount/{activityId}")
    public AjaxResult getExpandRequestCount(@PathVariable Integer activityId) {
        if (activityId == null) {
            return AjaxResult.error("活动ID不能为空");
        }
        
        try {
            String redisKey = "activity:expand:request:" + activityId;
            Object countObj = redisCache.getCacheObject(redisKey);
            
            // 如果不存在，返回0
            Long count = 0L;
            if (countObj != null) {
                if (countObj instanceof Number) {
                    count = ((Number) countObj).longValue();
                } else if (countObj instanceof String) {
                    count = Long.parseLong((String) countObj);
                }
            }
            
            return AjaxResult.success(count);
        } catch (Exception e) {
            return AjaxResult.error("获取扩容申请数量失败: " + e.getMessage());
        }
    }

    /**
     * 检查当前用户是否已申请扩容
     *
     * @param activityId 活动ID
     * @return 是否已申请（true表示已申请，false表示未申请）
     */
    @GetMapping("/checkExpandRequest/{activityId}")
    public AjaxResult checkExpandRequest(@PathVariable Integer activityId) {
        if (activityId == null) {
            return AjaxResult.error("活动ID不能为空");
        }
        
        try {
            // 获取当前登录用户
            String username = getUsername();
            if (username == null || username.isEmpty()) {
                return AjaxResult.success(false); // 未登录视为未申请
            }
            
            // 构建用户申请记录的Redis键：activity:expand:user:{activityId}
            String userSetKey = "activity:expand:user:" + activityId;
            
            // 检查用户是否在申请集合中
            @SuppressWarnings("unchecked")
            Boolean isMember = redisCache.redisTemplate.opsForSet().isMember(userSetKey, username);
            
            return AjaxResult.success(Boolean.TRUE.equals(isMember));
        } catch (Exception e) {
            return AjaxResult.error("检查申请状态失败: " + e.getMessage());
        }
    }

    /**
     * 清空活动的扩容申请数据
     * 当管理员扩容活动时调用此方法清空之前的申请记录
     *
     * @param activityId 活动ID
     */
    private void clearExpandRequestData(Integer activityId) {
        if (activityId == null) {
            return;
        }
        
        try {
            // 清空扩容申请数量
            String countKey = "activity:expand:request:" + activityId;
            redisCache.deleteObject(countKey);
            
            // 清空申请扩容的用户集合
            String userSetKey = "activity:expand:user:" + activityId;
            redisCache.deleteObject(userSetKey);
        } catch (Exception e) {
            // 清空失败不影响活动更新，只记录日志
            // 这里可以选择记录日志或者忽略错误
        }
    }
}


