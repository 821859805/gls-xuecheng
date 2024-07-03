package com.gls.content.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gls.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author 郭林赛
 * @since 2024-06-24
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    @Select("select min(orderby) from teachplan where parentid=#{parentid} and course_id=#{courseId}")
    Integer selectFirstOrder(Teachplan teachplan);
    @Select("select max(orderby) from teachplan where parentid=#{parentid} and course_id=#{courseId}")
    Integer selectLastOrder(Teachplan teachplan);
}
