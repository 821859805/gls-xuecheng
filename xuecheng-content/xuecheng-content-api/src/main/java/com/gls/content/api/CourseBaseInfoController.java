package com.gls.content.api;

import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.content.model.dto.CourseDto;
import com.gls.content.model.dto.QueryCourseParamsDto;
import com.gls.content.model.po.CourseBase;
import com.gls.content.model.vo.CourseBaseInfoVo;
import com.gls.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(value="课程信息编辑接口",tags = "课程信息编辑接口")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Slf4j
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
     * @param dto
     * @return com.gls.content.model.vo.CourseBaseInfoVo
     * @author 郭林赛
     * @createDate 2024/6/28 11:32
     **/

    @PostMapping
    public CourseBaseInfoVo createCourseBase(@RequestBody @Validated CourseDto dto){
        Long companyId = 1232141425L;
        return courseBaseInfoService.insert(dto,companyId);
    }

    /**
     * @description 查询课程基本信息接口
     *
     * @param courseId
     * @return com.gls.content.model.vo.CourseBaseInfoVo
     * @author 郭林赛
     * @createDate 2024/7/1 11:13
     **/
    @GetMapping("/{courseId}")
    public CourseBaseInfoVo queryById(@PathVariable Long courseId){
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    /**
     * @description 修改课程信息并返回课程信息
     *
     * @param dto
     * @return com.gls.content.model.vo.CourseBaseInfoVo
     * @author 郭林赛
     * @createDate 2024/7/1 11:13
     **/
    @PutMapping
    public CourseBaseInfoVo updateAndQuery(@RequestBody CourseDto dto){
        log.info("修改课程接口测试：{}",dto);
        return courseBaseInfoService.updateAndQuery(1232141425L,dto);
    }

    /**
     * @description 通过课程id删除课程基本信息，课程营销信息，课程计划，课程教师信息
     *
     * @param id
     * @return void
     * @author 郭林赛
     * @createDate 2024/7/3 11:49
     **/
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        courseBaseInfoService.deleteById(id,1232141425L);
    }

}
