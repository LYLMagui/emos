package com.ukir.emos.wx.config.xss;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;


/**
 * 此类用于做抵御Xss攻击
 * 继承HttpRequestWrapper类，
 */
public class XssHttpServletRequestWrapper extends HttpRequestWrapper {


    public XssHttpServletRequestWrapper(HttpRequest request) {
        super(request);
    }
}
