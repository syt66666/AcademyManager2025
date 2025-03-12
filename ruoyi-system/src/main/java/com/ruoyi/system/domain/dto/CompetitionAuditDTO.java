package com.ruoyi.system.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CompetitionAuditDTO {
    @NotNull(message = "竞赛ID不能为空")
    private Integer competitionId;

    @NotBlank(message = "审核状态不能为空")
    @Pattern(regexp = "已通过|未通过", message = "无效的审核状态")
    private String auditStatus;

    @Size(max = 500, message = "审核意见不能超过500个字符")
    private String auditRemark;


    public @NotNull(message = "竞赛ID不能为空") Integer getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(@NotNull(message = "竞赛ID不能为空") Integer competitionId) {
        this.competitionId = competitionId;
    }

    public @NotBlank(message = "审核状态不能为空") @Pattern(regexp = "已通过|未通过", message = "无效的审核状态") String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(@NotBlank(message = "审核状态不能为空") @Pattern(regexp = "已通过|未通过", message = "无效的审核状态") String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public @Size(max = 500, message = "审核意见不能超过500个字符") String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(@Size(max = 500, message = "审核意见不能超过500个字符") String auditRemark) {
        this.auditRemark = auditRemark;
    }
}
