package com.ukir.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 *  封装数据类
 **/
@Data
@ApiModel
public class RegisterForm {
    //激活码，不能为空，6位数字
    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$")
    private String registerCode;

    //临时授权字符串
    @NotBlank
    private String code;

    //微信昵称
    @NotBlank
    private String nickname;

    //用户头像地址
    @NotBlank
    private String photo;

}
