package com.gls.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 响应给用户的统一类型
 * @createDate 2024/6/29 12:24
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestErrorResponse implements Serializable {
    private String errMessage;
}
