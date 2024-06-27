package com.gls.content.service;

import com.gls.content.model.po.CourseCategory;
import com.gls.content.model.vo.CourseCategoryInfoVO;

import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程分类信息查询
 * @createDate 2024/6/27 11:42
 **/

public interface CourseCategoryInfoService {
    List<CourseCategoryInfoVO> queryTreeNodes();
}
