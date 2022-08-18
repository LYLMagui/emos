package com.ukir.emos.wx.exception;

import lombok.Data;

/**
 * @author ukir
 * @date 2022/08/18 18:19
 **/
@Data
public class EmosException extends RuntimeException{

    private String msg;

}
