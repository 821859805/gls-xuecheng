package com.gls.content.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 郭林赛
 * @since 2024-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_audit")
public class CourseAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程id
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 审核意见
     */
    @TableField("audit_mind")
    private String auditMind;

    /**
     * 审核状态
     */
    @TableField("audit_status")
    private String auditStatus;

    /**
     * 审核人
     */
    @TableField("audit_people")
    private String auditPeople;

    /**
     * 审核时间
     */
    @TableField("audit_date")
    private LocalDateTime auditDate;


}
