package com.gls.content.model.vo;

import com.gls.content.model.po.CourseBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程基本信息前端展示
 * @createDate 2024/6/28 11:25
 **/



@Data
public class CourseBaseInfoVo extends CourseBase {

    /**
     * 收费规则，对应数据字典
     */
    private String charge;

    /**
     * 价格
     */
    private Float price;


    /**
     * 原价
     */
    private Float originalPrice;

    /**
     * 咨询qq
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    private Integer validDays;

    /**
     * 大分类名称
     */
    private String mtName;

    /**
     * 小分类名称
     */
    private String stName;
}
