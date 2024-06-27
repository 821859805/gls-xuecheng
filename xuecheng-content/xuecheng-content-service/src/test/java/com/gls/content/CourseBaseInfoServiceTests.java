package com.gls.content;

import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.content.model.dto.QueryCourseParamsDto;
import com.gls.content.model.po.CourseBase;
import com.gls.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程基本信息业务测试
 * @createDate 2024/6/26 10:53
 **/

@SpringBootTest
public class CourseBaseInfoServiceTests {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Test
    void testCourseBaseInfoService(){
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto("java","202004","203001");
        PageParams pageParams = new PageParams(1L,3L);

        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams,queryCourseParamsDto);
        System.out.println(courseBasePageResult);
    }
}
