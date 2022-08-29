package com.ukir.emos.wx.controller;

import com.ukir.emos.wx.common.util.Result;
import com.ukir.emos.wx.controller.form.TestSayHelloForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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
}
