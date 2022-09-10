package com.ukir.emos.wx.config.shiro;

import com.ukir.emos.wx.db.pojo.TbUser;
import com.ukir.emos.wx.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 *
 * 用于实现认证与授权
 */

@Component //注入到Spring里
public class OAuth2Realm extends AuthorizingRealm {

    //实现用于处理令牌字符串的方法
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

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
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection collection) {

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        //获取保存在认证方法中添加的用户信息
        TbUser user = (TbUser) collection.getPrimaryPrincipal();

        //获取用户id
        Integer userId = user.getId();


        //获取用户权限列表
        Set<String> permiSet = userService.searchUserPermissions(userId);

        //把权限列表添加到info对象中
        info.setStringPermissions(permiSet);
        return info;
    }

    /**
     * 认证（验证登录时调用）
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //从AuthenticationToken的对象中获取token字符串
        String accessToken = (String) token.getCredentials();
        //从token字符串中获取用户id
        int userId = jwtUtil.getUserId(accessToken);
        //获取用户基本信息
        TbUser user = userService.searchById(userId);

        //用户离职时抛出错误
        if(user == null){
            throw new LockedAccountException("账号已被锁定，请联系管理员");
        }

        // 往info对象中添加用户信息、Token字符串、类名
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,accessToken,getName());
        return info;
    }
}
