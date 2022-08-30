package com.ukir.emos.wx.config;

import com.ukir.emos.wx.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * 精简异常消息
 */

@Slf4j //引用日志模块
@RestControllerAdvice //捕获SpringMVC抛出的各种异常
public class ExceptionAdvice {

    @ResponseBody //向前端返回json字符串
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //响应状态码：500
    @ExceptionHandler(Exception.class) // 捕获Execption类型的异常
    public String exceptionHandler(Exception e) {

        log.error("执行异常", e);
        //判断异常类型
        if (e instanceof MethodArgumentNotValidException) {
            //异常类型强转
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;

            return exception.getBindingResult().getFieldError().getDefaultMessage(); //获得具体的精简过的异常消息

        } else if (e instanceof EmosException) {
            EmosException exception = (EmosException) e;
            return exception.getMsg();
        } else if (e instanceof UnauthenticatedException) {
            return "你不具备相关权限";
        } else {
            return "后端执行异常";

        }

    }

}
