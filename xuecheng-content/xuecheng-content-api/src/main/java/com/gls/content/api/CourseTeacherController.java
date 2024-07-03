package com.gls.content.api;

import com.gls.content.model.po.CourseTeacher;
import com.gls.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description TODO
 * @createDate 2024/7/3 9:24
 **/

@Api(value = "教师管理",tags = "教师管理")
@RestController
@RequestMapping("/courseTeacher")
@RequiredArgsConstructor
public class CourseTeacherController {
    private final CourseTeacherService courseTeacherService;

    /**
     * @description 根据课程id查询其所有教师信息
     *
     * @param courseId
     * @return java.util.List<com.gls.content.model.po.CourseTeacher>
     * @author 郭林赛
     * @createDate 2024/7/3 9:33
     **/
    @GetMapping("list/{courseId}")
    public List<CourseTeacher> listByCourseId(@PathVariable Long courseId){
        return courseTeacherService.listByCourseId(courseId);
    }

    /**
     * @description 添加课程教师
     *
     * @param courseTeacher
     * @return com.gls.content.model.po.CourseTeacher
     * @author 郭林赛
     * @createDate 2024/7/3 9:45
     **/
    @PostMapping
    public CourseTeacher insert(@RequestBody CourseTeacher courseTeacher){
        Long companyId = 1232141425L;
        return courseTeacherService.insert(companyId,courseTeacher);
    }

    /**
     * @description 修改教师信息
     *
     * @param courseTeacher
     * @return com.gls.content.model.po.CourseTeacher
     * @author 郭林赛
     * @createDate 2024/7/3 9:46
     **/
    @PutMapping
    public CourseTeacher updateById(@RequestBody CourseTeacher courseTeacher){
        Long companyId = 1232141425L;
        return courseTeacherService.updateById(companyId,courseTeacher);
    }

    /**
     * @description 通过课程id和教师id删除教师
     *
     * @param courseId
     * @param id
     * @return void
     * @author 郭林赛
     * @createDate 2024/7/3 9:46
     **/
    @DeleteMapping("/course/{courseId}/{id}")
    public void deleteByCourseIdAndId(@PathVariable("courseId") Long courseId,@PathVariable("id") Long id){
        Long companyId = 1232141425L;
        courseTeacherService.deleteByCourseIdAndId(companyId,courseId,id);
    }

}
