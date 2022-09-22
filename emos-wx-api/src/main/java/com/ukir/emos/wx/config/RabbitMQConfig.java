package com.ukir.emos.wx.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ukir
 * @date 2022/09/21 17:08
 * RabbitMQ配置类
 **/
@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory getFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.124.13"); //Linux主机地址
        factory.setPort(5672); //RabbitMQ端口号
        return factory;
    }
}
