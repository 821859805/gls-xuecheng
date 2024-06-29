package com.gls.content.api;

import cn.hutool.core.bean.BeanUtil;
import com.gls.content.model.po.CourseBase;
import com.gls.content.model.po.CourseCategory;
import com.gls.content.model.vo.CourseBaseInfoVo;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 测试函数
 * @createDate 2024/6/28 17:43
 **/

public class test01 {
    public static void main(String[] args) {
        CourseBase courseBase = new CourseBase();
        CourseBaseInfoVo courseBaseInfoVo = new CourseBaseInfoVo();
        courseBaseInfoVo.setId(1L);
        System.out.println(courseBaseInfoVo);
        BeanUtil.copyProperties(courseBase,courseBaseInfoVo);
        System.out.println(courseBaseInfoVo);
    }
}
