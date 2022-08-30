package com.ukir.emos.wx.config.shiro;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 拦截器，拦截请求，对token进行处理
 */
@Component
@Scope("prototype") //设置类为多例模式
public class OAuth2Filter extends AuthenticatingFilter {

    /**
     * 需要往媒介类中保存数据，需要引用
     */
    @Autowired
    private ThreadLocalToken threadLocalToken;

    //注入配置文件中缓存过期时间，用于存入Redis
    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    //token检验
    @Autowired
    private JwtUtil jwtUtil;

    //注入RedisTemplate，需要调用其中的方法存储缓存过期时间
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 拦截请求后，用于把令牌字符串封装成令牌对象
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        //获取请求token
        String token = getRequestToken(req);
        //如果token不为空，封装成AuthenticationToken对象
        //如果token为空，返回null
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return new OAuth2Token(token);
    }

    //处理请求
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        //判断请求是否为options类型的，如果是则直接放行
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            //如果为true，则直接放行；如果为false则由shiro处理
            return true;
        }
        return false;
    }


    /**
     * 功能：
     * 1. 获取token，判断是否过期
     * 2. 将新的字符串保存到redis和ThreadLocalToken内
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //请求类型的强转
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        //设置响应内容
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8"); // 设置响应字符集
        //设置响应头中跨域的参数
        // 前后端分离项目需要跨域请求，因此需要咋请求头中设置跨域参数
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));

        //清空ThreadLocalToken中的参数
        threadLocalToken.clear();

        //从请求头中获取token字符串
        String token = getRequestToken(req);

        //如果token为空，向客户端返回错误消息
        if (StrUtil.isBlank(token)) {
            //设置Http状态码
            resp.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            resp.getWriter().println("无效的令牌");
            //返回false，无需Shiro再次进行验证
            return false;
        }

        //如果不为空，验证token内容是否有效
        try {
            //验证token，如果有问题则会抛出异常，需要捕获
            jwtUtil.verifierToken(token);
        } catch (TokenExpiredException e) { //token过期的异常
            /*
                判断redis内的缓存是否过期
                如果redis内的令牌未过期，则刷新令牌
             */
            if (redisTemplate.hasKey(token)) {
                //删除旧令牌
                redisTemplate.delete(token);
                //获取userid
                int userId = jwtUtil.getUserId(token);
                //根据userid生成新的token
                token = jwtUtil.createToken(userId);
                /*
                    key：token
                    value：userId
                    过期时间：cacheExpire
                    单位：天
                 */
                redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);

                //ThreadLocalToken中也要存储token
                threadLocalToken.setToken(token);

            } else { //redis中的token也过期，则用户需要重新登录
                resp.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
                resp.getWriter().println("令牌已过期");
                return false;
            }
        } catch (JWTDecodeException e) { //如果内容有问题，则捕JWTDecodeException获异常
            resp.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            resp.getWriter().println("无效的令牌");
            return false;
        }
        //若token没有问题，则调用OAuth2Realm类，进行认证授权
        boolean bool = executeLogin(request, response); //通过Shiro间接调用Realm类
        //如果bool为fals，则认证失败；
        return bool;
    }

    //认证失败的处理方法
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        //请求类型的强转
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        //设置响应内容
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        //设置请求头跨域参数
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        //设置响应状态码
        resp.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
        try {
            resp.getWriter().println(e.getMessage());
        } catch (IOException ioException) {
        }

        return false;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        super.doFilterInternal(request, response, chain);
    }

    //获取请求的token
    private String getRequestToken(HttpServletRequest request) {
        //提取请求头中的token
        String token = request.getHeader("token");
        //判断token是否为空或为空字符串
        if (StrUtil.isBlank(token)) {
            //若为空，则从请求体中寻找token
            token = request.getParameter("token");
        }
        return token;
    }


}
