package com.ruoyi.system.domain;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
/**
 * 系统通知公告对象 notifications
 *
 * @author ruoyi
 * @date 2025-10-12
 */
public class Notifications extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    /** 通知唯一ID */
    private Long notiId;
    /** 通知标题 */
    @Excel(name = "通知标题")
    private String notiTitle;
    /** 通知正文内容 */
    @Excel(name = "通知正文内容")
    private String notiContent;
    /** 通知类型 */
    @Excel(name = "通知类型")
    private String notiType;
    /** 文档附件地址列表 */
    // 注意：@Excel注解对于List类型的导出可能不理想，它会导出为 [path1, path2] 格式。
    // 如果需要导出为更友好的格式，可能需要自定义处理。这里暂时保留注解。
    @Excel(name = "文档附件地址")
    private List<String> fileAttachments;
    /** 图片附件地址列表 */
    @Excel(name = "图片附件地址")
    private List<String> pictureAttachments;
    /** 创建者ID (字符串类型，用于关联用户名或UUID) */
    @Excel(name = "创建者ID")
    private String creatorId;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;
    // ------------------- 查询专用字段 (非数据库字段) -------------------
    /** 用于查询的文档附件关键词 */
    private String searchFile;
    /** 用于查询的图片附件关键词 */
    private String searchPicture;
    // ------------------- Getter 和 Setter (必须完整) -------------------
    public Long getNotiId() {
        return notiId;
    }
    public void setNotiId(Long notiId) {
        this.notiId = notiId;
    }
    public String getNotiTitle() {
        return notiTitle;
    }
    public void setNotiTitle(String notiTitle) {
        this.notiTitle = notiTitle;
    }
    public String getNotiContent() {
        return notiContent;
    }
    public void setNotiContent(String notiContent) {
        this.notiContent = notiContent;
    }
    public String getNotiType() {
        return notiType;
    }
    public void setNotiType(String notiType) {
        this.notiType = notiType;
    }
    public List<String> getFileAttachments() {
        return fileAttachments;
    }
    public void setFileAttachments(List<String> fileAttachments) {
        this.fileAttachments = fileAttachments;
    }
    public List<String> getPictureAttachments() {
        return pictureAttachments;
    }
    public void setPictureAttachments(List<String> pictureAttachments) {
        this.pictureAttachments = pictureAttachments;
    }
    public String getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public String getSearchFile() {
        return searchFile;
    }
    public void setSearchFile(String searchFile) {
        this.searchFile = searchFile;
    }
    public String getSearchPicture() {
        return searchPicture;
    }
    public void setSearchPicture(String searchPicture) {
        this.searchPicture = searchPicture;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("notiId", getNotiId())
                .append("notiTitle", getNotiTitle())
                .append("notiContent", getNotiContent())
                .append("notiType", getNotiType())
                .append("fileAttachments", getFileAttachments())
                .append("pictureAttachments", getPictureAttachments())
                // 注意：BaseEntity 中的字段 (如 createBy, createTime) 也会被包含在 toString 中
                .append("creatorId", getCreatorId())
                .append("createdAt", getCreatedAt())
                .toString();
    }
}