package com.ukir.emos.wx.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 删除消息
 **/
@Data
public class DeleteMessageRefByIdForm {
    @NotBlank
    private String id;
}
