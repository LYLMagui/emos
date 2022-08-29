package com.ukir.emos.wx.common.util;

import cn.hutool.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一封装返回方法
 * 继承HashMap类，用于保存对象
 **/
public class Result extends HashMap<String, Object> {


    public Result() {
        //继承了HashMap，因此可以使用put方法
        //引入的HttpStatus类库中封装了许多状态码，可以直接使用
        put("code", HttpStatus.HTTP_OK); // 请求成功的状态码，返回值200
        put("msg", "成功");
    }

    //声明一个Request类的put方法，这样再次调用Result方法时可以再调用里面的put方法，形成链式调用
    @Override
    public Result put(String key, Object value) {
        //调用父类的put方法，绑定数据
        super.put(key, value);
        return this;
    }

    /**
     * 定义静态工厂方法,省去创建Result对象的步骤
     *
     * @return
     */
    //1. 创建ok静态工厂方法，返回创建Result对象
    public static Result ok() {
        return new Result();
    }

    //2. 重载ok方法，绑定业务消息
    public static Result ok(String msg) {
        Result r = new Result();
        r.put("msg", msg);
        return r;
    }

    //3. 继续重载方法，绑定不同的数据
    public static Result ok(Map<String, Object> map) {
        Result r = new Result();
        r.putAll(map);
        return r;
    }

    //4. 创建error静态工厂方法
    public static Result error(Integer code, String msg) {
        Result r = new Result();
        //请求失败的状态码有很多种，这里直接传入具体的失败状态码，覆盖上面的HttpStatus类库的状态码
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    //5. 重载error方法
    public static Result error(String msg) {
        //固定传入500状态码的方法
        return error(HttpStatus.HTTP_INTERNAL_ERROR, msg);
    }

    //6. 固定状态码和业务消息的重载方法
    public static Result error() {
        return error(HttpStatus.HTTP_INTERNAL_ERROR, "服务器出错，请检查服务器");
    }

}
