package com.ukir.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 更新未读消息表单类
 **/
@Data
@ApiModel
public class UpdateUnreadMessage {
    @NotBlank
    private String id;
}
