package com.ukir.emos.wx.exception;

import lombok.Data;

/**
 * 自定义异常类
 **/
@Data
public class EmosException extends RuntimeException{

    //异常消息
    private String msg;
    //异常状态码
    private Integer code = 500;

    public EmosException(String msg) {
        super(msg);
        this.msg = msg;
    }


    public EmosException(String msg,Throwable e) {
        super(msg, e);
        this.msg = msg;
    }


    public EmosException(String msg, Integer code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }


    public EmosException(String msg, Integer code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }
}
