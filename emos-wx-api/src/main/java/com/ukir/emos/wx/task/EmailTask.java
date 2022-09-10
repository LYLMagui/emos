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

    @Value("${emos.email.system}")
    private String mailBox;

    @Async
    public void  sendAsync(SimpleMailMessage message){
        message.setFrom(mailBox);
        javaMailSender.send(message);
    }

}
