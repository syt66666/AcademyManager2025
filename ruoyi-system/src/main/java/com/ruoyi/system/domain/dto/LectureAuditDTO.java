package com.ruoyi.system.domain.dto;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LectureAuditDTO {
    @NotNull(message = "讲座记录ID不能为空")
    private Long reportId;

    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Size(max = 500, message = "审核意见不能超过500个字符")
    private String auditRemark;

    public @NotNull(message = "讲座记录ID不能为空") Long getReportId() {
        return reportId;
    }

    public void setReportId(@NotNull(message = "讲座记录ID不能为空") Long reportId) {
        this.reportId = reportId;
    }

    public @NotNull(message = "审核状态不能为空") Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(@NotNull(message = "审核状态不能为空") Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public @Size(max = 500, message = "审核意见不能超过500个字符") String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(@Size(max = 500, message = "审核意见不能超过500个字符") String auditRemark) {
        this.auditRemark = auditRemark;
    }
}
