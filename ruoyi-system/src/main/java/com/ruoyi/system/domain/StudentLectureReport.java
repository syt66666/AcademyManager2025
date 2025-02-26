package com.ruoyi.system.domain;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 学生讲座报告实体类
 */
public class StudentLectureReport {
    /**
     * 主键id
     */
    private int id;
    /**
     * 题目
     */
    private String title;
    /**
     * 报告人
     */
    private String reporter;
    /**
     * 报告时间
     */
    private LocalDateTime reportDate;
    /**
     * 内容简介
     */
    private String reportContent;
    /**
     * 链接
     */
    private String link;
    /**
     *报告心得体会
     */
    private byte[] reportFeeling;
    /**
     * 图片
     */
    private byte[] picture;
    /**
     * 审核状态
     */
    private int status = 0;
    /**
     * 学生学号
     */
    private String studentId;
    /**
     * 学生姓名
     */
    private String studentName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public byte[] getReportFeeling() {
        return reportFeeling;
    }

    public void setReportFeeling(byte[] reportFeeling) {
        this.reportFeeling = reportFeeling;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
