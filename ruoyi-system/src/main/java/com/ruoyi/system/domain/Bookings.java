package com.ruoyi.system.domain;

import java.time.LocalDateTime;

public class Bookings {

    /**  主键id  */
    private int booking_id;

    /**  外键id */
    private int student_id;

    /**  外键id */
    private int activity_id;

    /**  预约时间 */
    private LocalDateTime book_at;



}
