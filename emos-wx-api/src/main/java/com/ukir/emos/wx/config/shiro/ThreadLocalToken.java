package com.ukir.emos.wx.config.shiro;

import org.springframework.stereotype.Component;

/**
 * 作为存储新生成的Token的媒介类
 **/
@Component //将实体类类注入到Spring容器中，方便其他类调用
public class ThreadLocalToken {
    private ThreadLocal<String> local = new ThreadLocal<>();

    //将Token存到local对象中
    public void setToken(String token) {
        local.set(token);
    }

    //从local中获取Token对象
    public String getToken() {
        return local.get();
    }

    //清空local中的数据
    public void clear() {
        local.remove();
    }

}
