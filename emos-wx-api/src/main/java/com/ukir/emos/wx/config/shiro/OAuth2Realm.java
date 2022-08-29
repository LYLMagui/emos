package com.ukir.emos.wx.config.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * 用于实现认证与授权
 */

@Component //注入到Spring里
public class OAuth2Realm extends AuthorizingRealm {

    //实现用于处理令牌字符串的方法
    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public boolean supports(AuthenticationToken token) {
        //判断令牌字符串的类型是否属于OAuth2Token类型的
        return token instanceof OAuth2Token;
    }

    /**
     * 授权方法（验证权限时调用）
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //TODO 查询用户的权限列表
        //TODO 把权限列表添加到info对象中
        return info;
    }

    /**
     * 认证（登录时调用）
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //TODO 从令牌中获取userId，然后检测该账户是否被冻结
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
        //TODO 往info对象中添加用户信息、Token字符串
        return info;
    }
}
