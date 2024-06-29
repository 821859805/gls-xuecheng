package com.gls.content.api;

import com.gls.base.exception.ValidationGroups;
import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.content.model.dto.AddCourseDto;
import com.gls.content.model.dto.QueryCourseParamsDto;
import com.gls.content.model.po.CourseBase;
import com.gls.content.model.vo.CourseBaseInfoVo;
import com.gls.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(value="课程信息编辑接口",tags = "课程信息编辑接口")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseBaseInfoController {
    private final CourseBaseInfoService courseBaseInfoService;

    /**
     * 根据条件分页查询课程信息
     *
     * @param pageParams
     * @param queryCourseParamsDto
     * @return com.gls.base.model.PageResult<com.gls.content.model.po.CourseBase>
     * @author 郭林赛 2024/6/25 16:17
     **/
    @ApiOperation("根据条件分页查询课程信息")
    @PostMapping("/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
        return courseBaseInfoService.queryCourseBaseList(pageParams,queryCourseParamsDto);
    }
    
    /**
     * @description 创建课程基本信息
     *
     * @param addCourseDto
     * @return com.gls.content.model.vo.CourseBaseInfoVo
     * @author 郭林赛
     * @createDate 2024/6/28 11:32
     **/

    @PostMapping
    public CourseBaseInfoVo createCourseBase(@RequestBody @Validated({ValidationGroups.Insert.class}) AddCourseDto addCourseDto){
        Long companyId = 1232141425L;
        return courseBaseInfoService.insert(addCourseDto,companyId);
    }

}
