package com.ukir.emos.wx.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 根据ID查询的接收数据的表单类
 **/
@Data
public class SearchMessageByIdForm {
    @NotBlank // 非空
    private String id;
}
