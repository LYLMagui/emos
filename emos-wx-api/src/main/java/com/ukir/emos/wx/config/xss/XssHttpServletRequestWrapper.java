package com.ukir.emos.wx.config.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 此类用于做抵御Xss攻击
 * 继承HttpRequestWrapper类，
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    //父类构造器
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    //重写getParameter()方法，对接收到的值进行转义
    @Override
    public String getParameter(String name) {
        //获取父类中接收到的原始参数
        String value = super.getParameter(name);
        //判断字符串是否为空
        if (!StrUtil.hasEmpty(value)) {
            //转义，去掉指定标签（例如广告栏等）、去除JS、去掉样式等等
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    //对多个参数进行转义
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (null != values) {
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (!StrUtil.hasEmpty(value)) {
                    //转义，去掉指定标签（例如广告栏等）、去除JS、去掉样式等等
                    value = HtmlUtil.filter(value);
                }
                values[i] = value;
            }
        }
        return values;
    }

    //重写第三个方法

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        //定义一个新的map。将转义后的数据放入map中返回
        //LinkedHashMap是有序集合
        LinkedHashMap<String, String[]> map = new LinkedHashMap();
        if (null != parameterMap) {
            for (String key : parameterMap.keySet()) {
                String[] values = parameterMap.get(key);
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    if (!StrUtil.hasEmpty(value)) {
                        //转义，去掉指定标签（例如广告栏等）、去除JS、去掉样式等等
                        value = HtmlUtil.filter(value);
                    }
                    values[i] = value;
                }
                map.put(key,values);
            }
        }
        return map;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (!StrUtil.hasEmpty(value)) {
            //转义，去掉指定标签（例如广告栏等）、去除JS、去掉样式等等
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        //获取 请求数据的IO流
        InputStream in = super.getInputStream();
        //读取流里的字符数据
        InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
        BufferedReader buffer = new BufferedReader(reader);
        StringBuffer body = new StringBuffer();
        String line = buffer.readLine();
        //如果每一行的数据不等于空,数据有效
        while(null != line){
            body.append(line); //字符串拼接
            line= buffer.readLine();
        }
        buffer.close();
        reader.close();
        in.close();
        //将字符串转换为map对象
        Map<String,Object> map = JSONUtil.parseObj(body.toString());
        Map<String,Object> result = new LinkedHashMap<>();
        for(String key:map.keySet()){
            Object val = map.get(key);
            // instanceof 判断一个对象是否属于某个指定的类或其子类的实例
            if(val instanceof String){
                if(!StrUtil.hasEmpty(val.toString())){
                    result.put(key,HtmlUtil.filter(val.toString()));
                }
            }else {
                 result.put(key,val);
            }
        }
        //转为json字符串
        String json = JSONUtil.toJsonStr(result);
        //创建IO流，从字符串中读取数据
        ByteArrayInputStream bain = new ByteArrayInputStream(json.getBytes());
        //返回匿名内部类
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }
            //只需要覆盖这个方法
            @Override
            public int read() throws IOException {
                return bain.read();
            }
        };
    }

}

