package com.ukir.emos.wx.db.dao;

import com.ukir.emos.wx.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.Set;

@Mapper
public interface TbUserDao {
    //查询是否存在超级管理员账户
    public boolean haveRootuser();

    /**
     * 保存用户记录
     */
    public int insert(HashMap param);

    /**
     * 查询用户ID
     */
    public Integer searchIdByOpenId(String openId);



    /**
     * 根据用户id查询用户的权限
     * 返回结果为Set集合,内容不会重复
     */
    public Set<String> searchUserPermissions(int userId);

    /**
     * 查询用户信息
     * @param id
     * @return
     */
    public TbUser searchById(int id);

    /**
     * 查询员工姓名和所属部门
     * @param userId
     */
    public HashMap searchNameAndDept(int userId);


    /**
     * 查询用户入职日期
     * @param userId
     * @return
     */
    public String searchUserHiredate(int userId);

}