package com.gls.content.model.vo;

import com.gls.content.model.po.Teachplan;
import com.gls.content.model.po.TeachplanMedia;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 教学计划发送模型
 * @createDate 2024/6/30 14:53
 **/


//vo和dto一样，不需要校验
@Data
public class TeachPlanVo extends Teachplan {
    //树形结构的子结点
    List<TeachPlanVo> teachPlanTreeNodes;

    //教学视频
    TeachplanMedia teachplanMedia;
}
