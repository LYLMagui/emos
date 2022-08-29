package com.ukir.emos.wx.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 扩展AuthenticationToken这个接口，成为一个封装类，用于保存
 * token字符串
 */
public class OAuth2Token implements AuthenticationToken {
    private String token;

    public OAuth2Token(String token) {
        this.token = token;
    }

    /**
     * setter&getter方法
     * @return
     */

    @Override
    public String getPrincipal() {
        return token;
    }


    @Override
    public String getCredentials() {
        return token;
    }
}
