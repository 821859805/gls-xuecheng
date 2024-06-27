package com.gls.content.api;

import com.gls.content.model.vo.CourseCategoryInfoVO;
import com.gls.content.service.CourseCategoryInfoService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程分类信息接口
 * @createDate 2024/6/27 11:47
 **/

@Api(value="课程分类信息接口",tags = "课程分类信息接口")
@RestController
@RequestMapping("/course-category")
@RequiredArgsConstructor
public class CourseCategoryInfoController {
    private final CourseCategoryInfoService courseCategoryInfoService;

    @GetMapping("/tree-nodes")
    public List<CourseCategoryInfoVO> queryTreeNodes(){
        return courseCategoryInfoService.queryTreeNodes();
    }
}
