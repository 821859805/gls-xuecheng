package com.gls.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 通用错误枚举类
 * @createDate 2024/6/29 11:53
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum CommonError {
    UNKNOWN_ERROR("未知错误！"),
    PARAMS_ERROR("非法参数！"),
    OBJECT_NULL("对象为空！"),
    QUERY_NULL("查询结果为空！"),
    REQUEST_NULL("请求参数为空！");

    private String errMessage;  //展示给前端的异常信息
}
