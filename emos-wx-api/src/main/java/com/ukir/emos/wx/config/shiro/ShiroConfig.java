package com.ukir.emos.wx.config.shiro;


import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于把OAuth2Fileter和OAuth2Realm配置到Shiro框架
 **/

@Configuration //声明一个配置类
public class ShiroConfig {


    /**
     * 用于封装Realm对象
     * @param oAuth2Realm
     * @return
     */
    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm oAuth2Realm){ //导入的是import org.apache.shiro.web.mgt包下的SecurityManager类
        //创建DefaultWebSecurityManager用于封装OAuth2Realm对象
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(oAuth2Realm);
        //token保存在客户端而不保存在服务端，所以传入的值为null
        securityManager.setRememberMeManager(null);
        return securityManager;
    }


    /**
     * 用于封装Filter对象和设置Filter拦截路径
     * @param securityManager
     * @param oAuth2Filter
     * @return
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager,OAuth2Filter oAuth2Filter){
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        Map<String, Filter> map = new HashMap<>();
        map.put("oauth2",oAuth2Filter);
        shiroFilter.setFilters(map);

        //不需要拦截的路径
        Map<String,String> filterMap = new LinkedHashMap<>();
        filterMap.put("/webjars/**","anon"); //anon是指不要拦截
        filterMap.put("/druid/**","anon");
        filterMap.put("/app/**","anon");
        filterMap.put("/swagger-ui.html","anon");
        filterMap.put("/swagger-resources/**","anon");
        filterMap.put("/v3/**","anon");
        filterMap.put("/favicon.ico","anon");
        filterMap.put("/doc.html","anon");
        filterMap.put("/captcha.jpg/**","anon");
        filterMap.put("/user/register","anon");
        filterMap.put("/user/login","anon");
//        filterMap.put("/test/**","anon");
        filterMap.put("/**","oauth2"); //oauth2是上面放到map里的拦截器的名字

        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return  shiroFilter;
    }

    /**
     * 管理Shiro对象生命周期
     * @return
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }


    /**
     * AOP切面类，Web方法执行前，验证权限
     * @param securityManager
     * @return
     */
    @Bean("authorizationAttributeSourceAdvisor")
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){

        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
