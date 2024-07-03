package com.gls.content.api;

import com.gls.base.exception.ValidationGroups;
import com.gls.content.model.dto.TeachPlanDto;
import com.gls.content.model.vo.TeachPlanVo;
import com.gls.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程计划接口
 * @createDate 2024/6/30 12:53
 **/

@Api(value = "课程计划接口",tags = "课程计划接口")
@RestController
@RequestMapping("/teachplan")
@RequiredArgsConstructor
public class TeachPlanController {
    private final TeachPlanService teachPlanService;

    /**
     * @description 根据id查询教学计划
     *
     * @param courseId
     * @return com.gls.content.model.vo.TeachPlanVo
     * @author 郭林赛
     * @createDate 2024/6/30 15:46
     **/
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachPlanVo> selectListByCourseId(@PathVariable Long courseId){
        return teachPlanService.selectListByCourseId(courseId);
    }

    /**
     * @description 添加教学计划
     *
     * @param dto
     * @return void
     * @author 郭林赛
     * @createDate 2024/6/30 15:47
     **/
    @PostMapping
    public void insert(@RequestBody @Validated(ValidationGroups.Insert.class) TeachPlanDto dto){
        teachPlanService.insert(dto);
    }

    /**
     * @description 修改教学计划
     *
     * @param dto
     * @return void
     * @author 郭林赛
     * @createDate 2024/6/30 15:47
     **/
    @PutMapping
    public void updateByCourseIdAndParentId(@RequestBody @Validated(ValidationGroups.Update.class) TeachPlanDto dto){
        teachPlanService.updateByCourseIdAndParentId(dto);
    }


    /**
     * @description 通过课程计划id删除课程
     *
     * @param id
     * @return void
     * @author 郭林赛
     * @createDate 2024/7/3 9:35
     **/
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        teachPlanService.deleteById(id);
    }

    /**
     * @description 将课程向本级目录上移一位
     *
     * @param id
     * @return void
     * @author 郭林赛
     * @createDate 2024/7/3 9:35
     **/
    @PostMapping("/moveup/{id}")
    public void moveUpById(@PathVariable Long id){
        teachPlanService.moveUpById(id);
    }

    /**
     * @description 将课程计划向本级目录下移一位
     *
     * @param id
     * @return void
     * @author 郭林赛
     * @createDate 2024/7/3 9:36
     **/
    @PostMapping("/movedown/{id}")
    public void moveDownById(@PathVariable Long id){
        teachPlanService.moveDownById(id);
    }

}
