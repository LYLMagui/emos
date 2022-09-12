package com.ukir.emos.wx.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 定义线程任务
 **/
@Component
@Scope("prototype")
public class EmailTask {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void  sendAsync(SimpleMailMessage message){
        //发件人邮箱地址
        message.setFrom(from);
        javaMailSender.send(message);
    }

}
