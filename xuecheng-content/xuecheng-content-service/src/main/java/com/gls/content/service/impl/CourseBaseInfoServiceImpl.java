package com.gls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gls.base.exception.XueChengException;
import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.content.mapper.CourseBaseMapper;
import com.gls.content.mapper.CourseCategoryMapper;
import com.gls.content.mapper.CourseMarketMapper;
import com.gls.content.model.dto.AddCourseDto;
import com.gls.content.model.dto.QueryCourseParamsDto;
import com.gls.content.model.po.CourseBase;
import com.gls.content.model.po.CourseCategory;
import com.gls.content.model.po.CourseMarket;
import com.gls.content.model.vo.CourseBaseInfoVo;
import com.gls.content.service.CourseBaseInfoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程信息管理接口实现类
 * @createDate 2024/6/26 10:22
 **/

@Service
@RequiredArgsConstructor
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    private final CourseBaseMapper courseBaseMapper;
    private final CourseMarketMapper courseMarketMapper;
    private final CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //构建查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());

        //构建分页参数并查询
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        PageResult<CourseBase> courseBasePageResult = new PageResult<>(pageResult);
        return courseBasePageResult;
    }

    @Override
    @Transactional
    public CourseBaseInfoVo insert(AddCourseDto addCourseDto, Long companyId) {
        //合法性校验
//        if (StringUtils.isBlank(addCourseDto.getName())) {
//            throw new XueChengException("课程名称为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getMt())) {
//            throw new XueChengException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getSt())) {
//            throw new XueChengException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getGrade())) {
//            throw new XueChengException("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
//            throw new XueChengException("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getUsers())) {
//            throw new XueChengException("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getCharge())) {
//            throw new XueChengException("收费规则为空");
//        }

        //向课程基本信息表插入数据
        CourseBase courseBase = new CourseBase();
        BeanUtil.copyProperties(addCourseDto,courseBase);
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");
        courseBase.setCompanyId(companyId);     //先写companyId会导致原始数据被覆盖，null值可以覆盖非null值
        courseBase.setCreateDate(LocalDateTime.now());

        int insert = courseBaseMapper.insert(courseBase);
        if(insert<=0)
            throw new RuntimeException("新增课程失败");

        //向课程营销表插入数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtil.copyProperties(addCourseDto,courseMarket);
        courseMarket.setId(courseBase.getId());
        int i = saveCourseMarket(courseMarket);
        if(i<=0)
            throw new RuntimeException("保存课程营销信息失败");

        //查询课程基本信息以及营销信息并返回
        return getCourseBaseInfo(courseBase.getId());

    }

    private int saveCourseMarket(CourseMarket courseMarket){
        //规则校验略
        //收费规则
        String charge = courseMarket.getCharge();
        if(StringUtils.isBlank(charge)){
            throw new RuntimeException("收费规则没有选择");
        }
        //收费规则为收费
        if(charge.equals("201001")){
            if(courseMarket.getPrice() == null || courseMarket.getPrice().floatValue()<=0){
                throw new RuntimeException("课程为收费价格不能为空且必须大于0");
            }
        }

        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarket.getId()); //查询原课程营销信息

        //如果不存在就插入最新的
        if(courseMarketObj==null){
            return courseMarketMapper.insert(courseMarket);
        }else{  //存在则更新
            BeanUtil.copyProperties(courseMarket,courseMarketObj);
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }

    public CourseBaseInfoVo getCourseBaseInfo(long courseId){
        CourseBaseInfoVo courseBaseInfoVo = new CourseBaseInfoVo();

        //查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null)
            return null;
        BeanUtil.copyProperties(courseBase,courseBaseInfoVo);

        //课程营销信息。一对一关系，两张表id一一对应
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        //CourseBaseInfoVo不会被覆盖的原因：其继承自courseBase，其它数据也不和courseBase冲突
        if(courseMarket!=null)
            BeanUtil.copyProperties(courseMarket,courseBaseInfoVo);

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoVo.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoVo.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoVo;

    }
}









