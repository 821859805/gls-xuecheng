package com.gls.content.service;

import com.gls.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程教师服务接口
 * @createDate 2024/7/3 11:08
 **/
public interface CourseTeacherService {
    List<CourseTeacher> listByCourseId(Long courseId);
    CourseTeacher insert(Long companyId,CourseTeacher courseTeacher);
    CourseTeacher updateById(Long companyId,CourseTeacher courseTeacher);
    void deleteByCourseIdAndId(Long companyId,Long courseId,Long id);
}
