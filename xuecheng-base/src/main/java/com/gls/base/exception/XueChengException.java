package com.gls.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 自定义异常类型
 * @createDate 2024/6/29 12:02
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XueChengException extends RuntimeException {
    private String errMessage;

    public static void cast(CommonError commonError){
        throw new XueChengException(commonError.getErrMessage());
    }

    public static void cast(String errMessage){
        throw new XueChengException(errMessage);
    }

}
