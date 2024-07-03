package com.gls.content.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gls.base.exception.ValidationGroups;
import com.gls.content.model.po.Teachplan;
import io.swagger.annotations.Api;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 教学计划接收模型
 * @createDate 2024/6/30 14:52
 **/

@Data
public class TeachPlanDto {
    /***
     * 教学计划id
     */
    private Long id;

    /**
     * 课程计划名称
     */
    @NotEmpty(message = "课程计划名称不能为空！",groups = {ValidationGroups.Insert.class})
    private String pname;

    /**
     * 课程计划父级Id
     */
    @NotNull(message = "父级id不能为空！",groups = {ValidationGroups.Insert.class})
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    @NotNull(message = "层级不能为空！",groups = {ValidationGroups.Insert.class})
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;


    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 课程发布标识
     */
    private Long coursePubId;


    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;

    /**
     * 排序字段
     */
    private Integer orderby;

}
