package com.gls.content.service;

import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.content.model.dto.AddCourseDto;
import com.gls.content.model.dto.QueryCourseParamsDto;
import com.gls.content.model.po.CourseBase;
import com.gls.content.model.vo.CourseBaseInfoVo;

/**
 * 课程基本信息管理服务接口
 *
 * @author 郭林赛
 * @version 1.0
 * @createDate 2024/6/26 10:04
 **/

public interface CourseBaseInfoService {

    /**
     * @description 课程查询接口
     *
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 条件查询
     * @return com.gls.base.model.PageResult<com.gls.content.model.po.CourseBase>
     * @author 郭林赛
     * @createDate 2024/6/26 10:10
     **/
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @description 创建课程基本信息服务接口
     *
     * @param addCourseDto
     * @return com.gls.content.model.vo.CourseBaseInfoVo
     * @author 郭林赛
     * @createDate 2024/6/28 11:34
     **/

    CourseBaseInfoVo insert(AddCourseDto addCourseDto, Long companyId);
}
