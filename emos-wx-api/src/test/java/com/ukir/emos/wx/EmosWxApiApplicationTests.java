package com.ukir.emos.wx;

import cn.hutool.core.util.IdUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.ukir.emos.wx.db.pojo.MessageEntity;
import com.ukir.emos.wx.db.pojo.MessageRefEntity;
import com.ukir.emos.wx.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@SpringBootTest
class EmosWxApiApplicationTests {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 发送测试消息
     */
    @Test
    void contextLoads() {
        for(int i = 1;i <=100;i++){
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("系统消息");
            message.setMsg("这是第" + i + "条测试消息");
            message.setSendTime(new Date());
            String id = messageService.insertMessage(message);

            MessageRefEntity ref = new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(52);
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.inserRef(ref);
        }
    }

    @Test
    void rebbitMQ() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("192.168.124.13");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("hello",false,false,false,null);
        String message = "Hello World!";
        channel.basicPublish("","hello",null,message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        channel.close();
        connection.close();
    }

    @Test
    void searchUnreadMessage(){
        Query query = new Query();
        query.addCriteria(Criteria.where("readFlag").is(false).and("receiverId").is(52));
        long count = mongoTemplate.count(query, MessageRefEntity.class,"message_ref");
        System.out.println("查询到："+count);
    }

    @Test
    void searchLastCount(){
        Query query = new Query();
        query.addCriteria(Criteria.where("lastFlag").is(false).and("receiverId").is(52));
        long count = mongoTemplate.count(query, MessageRefEntity.class,"message_ref");
        System.out.println("查询到："+count);

//        Update update = new Update();
//        update.set("lastFlag",false);
//        UpdateResult result = mongoTemplate.updateMulti(query, update, "message_ref");
//        long rows = result.getModifiedCount(); //获取修改了多少条数据
    }



}
