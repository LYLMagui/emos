package com.ukir.emos.wx.controller;

import com.ukir.emos.wx.common.util.Result;
import com.ukir.emos.wx.config.shiro.JwtUtil;
import com.ukir.emos.wx.controller.form.LoginForm;
import com.ukir.emos.wx.controller.form.RegisterForm;
import com.ukir.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户模块
 **/
@RestController
@RequestMapping("/user")
@Api(tags = "用户模块web接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    //redis缓存过期时间
    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @PostMapping("/register")
    @ApiOperation("注册用户")
    public Result register(@Valid @RequestBody RegisterForm form) {
        //调用用户注册方法
        int id = userService.registerUser(form.getRegisterCode(), form.getCode(), form.getNickname(), form.getPhoto());
        //生成token
        String token = jwtUtil.createToken(id);
        //查询用户权限列表
        Set<String> permsSet = userService.searchUserPermissions(id);
        //往redis存储token
        saveCacheToken(token, id);

        return Result.ok("用户注册成功").put("token", token).put("permission", permsSet);
    }

    /**
     * 用户登录
     * @param form
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("登录系统")
    public Result login(@Valid @RequestBody LoginForm form){
        int id = userService.login(form.getCode());
        String token = jwtUtil.createToken(id);
        saveCacheToken(token,id);
        Set<String> permsSet = userService.searchUserPermissions(id);
        System.out.println(token);
        return Result.ok("登录成功").put("token",token).put("permission",permsSet);

    }

    /**
     * 往redis存储令牌
     *
     * @param token
     * @param userId
     */
    private void saveCacheToken(String token, int userId) {
        /*
            往redis中存储token
            参数：
                key：token            键
                value：userId         值
                timeout：cacheExpire  过期时间
                unit：TimeUnit.DAYS   时间单位
         */
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }


}
