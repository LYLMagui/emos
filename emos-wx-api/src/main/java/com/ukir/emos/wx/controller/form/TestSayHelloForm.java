package com.ukir.emos.wx.controller.form;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@ApiModel //开启Swagger
@Data
public class TestSayHelloForm {
    //定义变量，接收客户端提交的数据

    @NotBlank //变量不能为空或空字符串
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,15}$")
    @ApiModelProperty("姓名") //Swagger页面描述变量存储的值
    private String name;

}
