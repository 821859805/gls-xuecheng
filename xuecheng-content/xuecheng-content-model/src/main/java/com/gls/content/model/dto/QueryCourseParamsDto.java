package com.gls.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString   //lombok里面的tostring，想想有什么作用？
public class QueryCourseParamsDto {
    //课程名称（课程基本表）
    @ApiModelProperty("课程名称")
    private String courseName;

    //课程审核状态（课程审核表）
    @ApiModelProperty("审核状态")
    private String auditStatus;

    //课程发布状态（课程发布表）
    @ApiModelProperty("发布状态")
    private String publishStatus;
}
