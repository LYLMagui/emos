package com.ukir.emos.wx.service;

import java.util.Set;

/**
 * 用户模块业务层接口
 **/
public interface UserService {

    /**
     * 用户注册
     */
    public int registerUser(String registerCode, String code, String nickname, String photo); //参数：激活码、临时授权字符串、微信昵称、微信头像地址

    /**
     * 根据用户id查询用户的权限
     */
    public Set<String> searchUserPermissions(int userId);


}
