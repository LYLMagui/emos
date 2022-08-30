package com.ukir.emos.wx.aop;

import com.ukir.emos.wx.common.util.Result;
import com.ukir.emos.wx.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用于向客户端返回新令牌的切面类
 **/
@Aspect //声明这是一个切面类
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    //声明切入点，拦截所有controller包下的所有controller类的所有方法
    @Pointcut("execution(public * com.ukir.emos.wx.controller.*.*(..)))")
    public void aspect() {

    }

    //定义事件
    @Around("aspect()") //环绕事件
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Result result = (Result) point.proceed(); //获得方法的返回结果

        String token = threadLocalToken.getToken();

        //如果token不为空，则返回token
        if (null != token) {
            result.put("token", token);
            //清空threadLocalToken里的token
            threadLocalToken.clear();
        }
        return result;
    }

}
