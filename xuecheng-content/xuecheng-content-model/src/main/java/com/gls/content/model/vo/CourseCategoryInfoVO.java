package com.gls.content.model.vo;

import com.gls.content.model.po.CourseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程分类前端展示模型
 * @createDate 2024/6/27 12:17
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseCategoryInfoVO extends CourseCategory implements Serializable {
    List<CourseCategoryInfoVO> childrenTreeNodes;
}
