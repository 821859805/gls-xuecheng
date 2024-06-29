package com.gls.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 全局异常处理器
 * @createDate 2024/6/29 12:23
 **/

@Slf4j
@RestControllerAdvice   //@ResponseBody+controller，下面方法不用写responseBody了
public class GlobalExceptionHandler {
    @ExceptionHandler(XueChengException.class)          //拦截自定义的异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   //拦截500的错误
    public RestErrorResponse customException(XueChengException e){
        return new RestErrorResponse(e.getErrMessage());
    }

    @ExceptionHandler(Exception.class)  //拦截所有Exception类型的异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   //错误类型为500
    public RestErrorResponse exception(Exception e){
        return new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());    //只要是500就报未知异常
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        List<String> msgList = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item->msgList.add(item.getDefaultMessage()));   //将所有消息加入list

        String msg = StringUtils.join(msgList,","); //拼接消息
        return new RestErrorResponse(msg);
    }

}
