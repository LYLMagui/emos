package com.ukir.emos.wx.controller;

import com.ukir.emos.wx.common.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建测试Swagger接口
 */
@RestController //使用这个注解，所有方法接收和返回的数据都是json格式
@RequestMapping("/test")
@Api(tags = "测试web接口") // 开启Swagger，并且扫描这个包下的接口
public class TestController {

    @GetMapping("/swagger")
    @ApiOperation("Swagger的测试方法") //方法的描述
    public Result testSwagger(){
        return Result.ok().put("message","HelloWorld");
    }
}
