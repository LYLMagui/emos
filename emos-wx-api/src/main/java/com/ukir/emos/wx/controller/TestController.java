package com.ukir.emos.wx.controller;

import com.ukir.emos.wx.common.util.Result;
import com.ukir.emos.wx.controller.form.TestSayHelloForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 创建测试Swagger接口
 */
@RestController //使用这个注解，所有方法接收和返回的数据都是json格式
@RequestMapping("/test")
@Api(tags = "测试web接口") // 开启Swagger，并且扫描这个包下的接口
public class TestController {

    @PostMapping("/swagger")
    @ApiOperation("Swagger的测试方法") //方法的描述
    public Result testSwagger(@Valid @RequestBody TestSayHelloForm form) { //@Valid:开启后端验证的注解
        return Result.ok().put("message", "Hello," + form.getName());
    }

    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    //开启权限验证，当用户的权限列表为'ROOT'或'USER:ADD'这个权限时才可以执行这个方法
    @RequiresPermissions(value = {"a","b"},logical = Logical.OR)
    public Result addUser(){
        return Result.ok("添加成功");
    }
}
