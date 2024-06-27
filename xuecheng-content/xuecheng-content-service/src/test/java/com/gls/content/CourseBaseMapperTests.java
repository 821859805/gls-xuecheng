package com.gls.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.content.mapper.CourseBaseMapper;
import com.gls.content.model.dto.QueryCourseParamsDto;
import com.gls.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 描述
 *
 * @author 郭林赛
 * @version 1.0
 * @createDate 2024/6/25 19:19
 **/

@SpringBootTest
public class CourseBaseMapperTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Test
    void testCourseBaseMapper(){
        CourseBase courseBase = courseBaseMapper.selectById(74L);   //根据id查课程
        Assertions.assertNotNull(courseBase);

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();   //创建条件查询
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto(); //前端发来的条件
        queryCourseParamsDto.setCourseName("java");
        queryCourseParamsDto.setAuditStatus("202004");
        queryCourseParamsDto.setPublishStatus("203001");    //不在base表中，暂时不查

        //like %java%
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        //课程审核状态（在base表中）
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());

        //分页参数，由前端发来，这里写死
        PageParams pageParams = new PageParams(1L,3L);
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(),pageParams.getPageSize());

        //查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //取出数据
        List<CourseBase> records = pageResult.getRecords();
        long total = pageResult.getTotal();

        PageResult<CourseBase> courseBasePageResult = new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());
        System.out.println(courseBasePageResult);
    }
}
