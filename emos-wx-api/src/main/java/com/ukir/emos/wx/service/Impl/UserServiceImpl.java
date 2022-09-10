package com.ukir.emos.wx.service.Impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ukir.emos.wx.db.dao.TbUserDao;
import com.ukir.emos.wx.db.pojo.TbUser;
import com.ukir.emos.wx.exception.EmosException;
import com.ukir.emos.wx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * 用户模块业务层实现类
 **/
@Service
@Slf4j
@Scope("prototype") //使用ThreadLocalToken需要的注解，prototype 为多例
@Transactional
public class UserServiceImpl implements UserService {

    //注入yml文件中的app-id
    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserDao userDao;

    /**
     * 获取openId
     * @param code
     * @return
     */
    //定义为私有方法，防止外部访问
    private String getOpenId(String code){
        //获取OpenId的请求地址
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid",appId);
        map.put("secret",appSecret);
        map.put("js_code",code); //临时授权字符串
        map.put("grant_type","authorization_code");

        //发起请求，获取openId
        String response = HttpUtil.post(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        String openId = json.getStr("openid");
        //判断openid是否为空
        if (openId.length() == 0 || openId == null){
            throw new RuntimeException("临时登录凭证错误");
        }
        return openId;
    }


    /**
     * 用户注册
     * @param registerCode
     * @param code
     * @param nickname
     * @param photo
     * @return
     */
    @Override
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        //判断是否注册超级管理员
        if("000000".equals(registerCode)){
            //查询是否存在超级管理员
            boolean bool = userDao.haveRootuser();
            //如果不存在超级管理员则可以注册超级管理员
            if(!bool){
                String openId = getOpenId(code);
                HashMap param = new HashMap();
                param.put("openId",openId);
                param.put("nickname",nickname);
                param.put("photo",photo);
                param.put("role", "[0]"); //用户角色，字段是json，一个用户可以有多种角色
                param.put("status",1); //员工在职状态
                param.put("createTime",new Date()); //记录创建时间
                param.put("root",true); //表示该用户是超级管理员

                userDao.insert(param); //插入记录

                int id = userDao.searchIdByOpenId(openId);//查找主键值
                return id;
            }else {
                throw new EmosException("超级管理员账号已存在");
            }
        }else { //普通员工注册


        }
        return 0;
    }


    /**
     * 根据用户id查询用户的权限
     * @param userId
     */
    @Override
    public Set<String> searchUserPermissions(int userId) {
        //获取用户权限
        Set<String> permissions = userDao.searchUserPermissions(userId);
        return permissions;
    }

    /**
     * 用户登录
     *
     * @param code
     */
    @Override
    public Integer login(String code) {
        //获得openId
        String openId = getOpenId(code);
        //查找用户id
        Integer id = userDao.searchIdByOpenId(openId);
        //判断用户是否存在
        if(id == null){
            throw new EmosException("用户不存在");
        }
        //TODO 从消息队列中接收消息，转移到消息列表


        return id;
    }

    /**
     * 查询用户信息
     *
     * @param id
     * @return
     */
    @Override
    public TbUser searchById(int id) {
        TbUser user = userDao.searchById(id);
        return user;
    }
}
