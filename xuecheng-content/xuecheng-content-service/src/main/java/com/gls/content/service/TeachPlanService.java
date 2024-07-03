package com.gls.content.service;

import com.gls.content.model.dto.TeachPlanDto;
import com.gls.content.model.vo.TeachPlanVo;

import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 教学计划接口声明
 * @createDate 2024/6/30 15:41
 **/
public interface TeachPlanService {
    void insert(TeachPlanDto dto);
//    void update(TeachPlanDto dto);
    List<TeachPlanVo> selectListByCourseId(Long courseId);

    void updateByCourseIdAndParentId(TeachPlanDto dto);

    void deleteById(Long id);

    void moveUpById(Long id);

    void moveDownById(Long id);
}
