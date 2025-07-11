package com.ruoyi.system.domain;

import java.time.LocalDateTime;

public class Activities {
    /**  主键id  */
    private int activity_id;

    /**  活动名称  */
    private String activity_name;

    /** 活动的创建日期 */
    private LocalDateTime created_at;

    /**  活动开始时间  */
    private LocalDateTime start_time;

    /**  活动结束时间  */
    private LocalDateTime end_time;

    /** 报名截止日期 */
    private LocalDateTime activity_deadline;

    /**  活动地点  */
    private String activity_location;

    /**  活动容量 */
    private int activity_capacity;

    /** 活动的主要内容 */
    private String activity_description;

    /** 活动的状态（upcoming/ongoing/ended） */
    private enum status {upcoming,ongoing,ended;};

    /** 活动的组织单位 */
    private String organizer;

    /** 注意事项 */
    private String notes;





}
