package com.ukir.emos.wx.config.xss;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

//拦截所有请求
@WebFilter(urlPatterns = "/*")
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        //将 servletRequest 强转为 HttpServletRequest
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 这里需要传入请求，必须是HttpServletRequest类型的
        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        //传入转义后的请求 wrapper 放行
        chain.doFilter(wrapper,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
