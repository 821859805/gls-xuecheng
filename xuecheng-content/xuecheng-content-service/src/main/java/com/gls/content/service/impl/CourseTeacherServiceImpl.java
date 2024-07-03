package com.gls.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gls.base.exception.XueChengException;
import com.gls.content.mapper.CourseBaseMapper;
import com.gls.content.mapper.CourseTeacherMapper;
import com.gls.content.model.po.CourseBase;
import com.gls.content.model.po.CourseTeacher;
import com.gls.content.service.CourseTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程教师服务接口实现类
 * @createDate 2024/7/3 11:08
 **/
@Service
@RequiredArgsConstructor
public class CourseTeacherServiceImpl implements CourseTeacherService {
    private final CourseTeacherMapper courseTeacherMapper;
    private final CourseBaseMapper courseBaseMapper;
    /**
     * @description 根据课程id查询课程所有教师信息
     *
     * @param courseId
     * @return java.util.List<com.gls.content.model.po.CourseTeacher>
     * @author 郭林赛
     * @createDate 2024/7/3 11:15
     **/
    @Override
    public List<CourseTeacher> listByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    /**
     * @description 课程是否为本机构课程
     *
     * @param companyId
     * @param courseId
     * @return boolean
     * @author 郭林赛
     * @createDate 2024/7/3 11:31
     **/
    private boolean isCompanyValid(Long companyId, Long courseId){
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseBase::getCompanyId,companyId)
                .eq(CourseBase::getId,courseId);
        CourseBase courseBase = courseBaseMapper.selectOne(queryWrapper);
        return courseBase != null;
    }

    @Override
    @Transactional
    public CourseTeacher insert(Long companyId,CourseTeacher courseTeacher) {
        if(courseTeacher.getId()==null){
            if(!isCompanyValid(companyId,courseTeacher.getCourseId()))
                XueChengException.cast("只能新增本机构的课程！！！");
            courseTeacherMapper.insert(courseTeacher);
            return courseTeacher;
        }
        updateById(companyId,courseTeacher);
        return courseTeacher;
    }

    @Override
    @Transactional
    public CourseTeacher updateById(Long companyId,CourseTeacher courseTeacher) {
        if(!isCompanyValid(companyId,courseTeacher.getCourseId()))
            XueChengException.cast("只能修改本机构的课程！！！");
        CourseTeacher preCourseTeacher = courseTeacherMapper.selectById(courseTeacher.getId());
        if(preCourseTeacher==null)
            XueChengException.cast("课程教师不存在！！！");
        courseTeacherMapper.updateById(courseTeacher);
        return courseTeacher;
    }

    @Override
    @Transactional
    public void deleteByCourseIdAndId(Long companyId,Long courseId, Long id) {
        if(!isCompanyValid(companyId,courseId))
            XueChengException.cast("只能删除本机构的教师！！！");
        CourseTeacher preCourseTeacher = courseTeacherMapper.selectById(id);
        if(preCourseTeacher==null)
            XueChengException.cast("课程教师不存在！！！");
        courseTeacherMapper.deleteById(id);
    }
}
