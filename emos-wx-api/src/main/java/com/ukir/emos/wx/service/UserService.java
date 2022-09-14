package com.ukir.emos.wx.service;

import com.ukir.emos.wx.db.pojo.TbUser;

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

    /**
     * 用户登录
     */
    public Integer login(String code);

    /**
     * 查询用户信息
     * @param id
     * @return
     */
    public TbUser searchById(int id);


    /**
     * 查询用户入职日期
     * @param userId
     * @return
     */
    public String searchUserHiredate(int userId);

}
