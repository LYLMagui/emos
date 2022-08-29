package com.ukir.emos.wx.config.shiro;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.Date;

//Jwt工具类
@Component //注入Bean
@Slf4j //开启日志
public class JwtUtil {

    /*
     * springfarmework包下的Value注解
     * 作用：读取yml文件内的属性注入到变量中
     * */
    @Value("$(emos.jwt.secret)") //注入配置文件中的秘钥
    private String secret;

    //令牌过期时间
    @Value("$(emos.jwt.expire)")
    private int expire;

    //创建令牌 获取userId
    private String createToken(int userId) {
        //日期偏移，token在当前日期的五天后失效
        Date date = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, 5);
        //创建加密算法对象
        Algorithm algorithm = Algorithm.HMAC256(secret); // 传入秘钥
        //执行加密
        JWTCreator.Builder builder = JWT.create();
        //绑定用户id，设置过期时间，传入加密算法
        String token = builder.withClaim("userId", userId).withExpiresAt(date).sign(algorithm);
        return token;
    }

    //从令牌中获取userId
    public int getUserId(String token) {
        //对加密字符串进行解码
        DecodedJWT jwt = JWT.decode(token);
        //通过属性名获取id
        Integer userId = jwt.getClaim("userId").asInt();
        return userId;
    }

    //验证令牌字符串的有效性
    public void verifierToken(String token) {
        //创建算法对象
        Algorithm algorithm = Algorithm.HMAC256(secret);
        //使用算法对象解密
        JWTVerifier verifier = JWT.require(algorithm).build();
        //调用验证方法
        verifier.verify(token);
    }
}
