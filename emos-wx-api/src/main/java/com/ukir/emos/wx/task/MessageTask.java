package com.ukir.emos.wx.task;

import com.rabbitmq.client.*;
import com.ukir.emos.wx.db.pojo.MessageEntity;
import com.ukir.emos.wx.db.pojo.MessageRefEntity;
import com.ukir.emos.wx.exception.EmosException;
import com.ukir.emos.wx.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ukir
 * @date 2022/09/21 17:12
 * 收发消息的线程任务类
 **/
@Slf4j
@Component
public class MessageTask {
    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private MessageService messageService;

    /**
     * 发送消息方法
     *
     * @param topic
     * @param entity
     */
    public void send(String topic, MessageEntity entity) {
        String id = messageService.insertMessage(entity);
        try (Connection connection = factory.newConnection(); //创建连接
             Channel channel = connection.createChannel(); //创建队列
        ) {
            channel.queueDeclare(topic, true, false, false, null); //队列配置
            HashMap map = new HashMap();
            map.put("messageId", id);
            //将hashMap中的数据添加到AMQP的请求头中
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(map).build();
            channel.basicPublish("", topic, properties, entity.getMsg().getBytes());
            log.debug("消息发送成功");
        } catch (Exception e) {
            log.error("执行异常", e);
            throw new EmosException("向MQ发送消息失败");
        }
    }

    /**
     * 异步发送消息
     * @param topic
     * @param entity
     */
    @Async
    public void sendAsync(String topic,MessageEntity entity){
        send(topic,entity);
    }

    /**
     * 从队列中接收消息
     * @param topic
     * @return
     */
    public int recive(String topic){
        int i = 0; //接收到的消息数量
        try (Connection connection = factory.newConnection(); //创建连接
             Channel channel = connection.createChannel(); //创建队列
        ) {
            channel.queueDeclare(topic, true, false, false, null); //队列配置
            while (true){
                GetResponse response = channel.basicGet(topic, false); //从队列中拿出消息
                if(response != null){
                    AMQP.BasicProperties properties = response.getProps();//提取绑定数据
                    Map<String, Object> map = properties.getHeaders();//获取请求头
                    String messageId = map.get("messageId").toString();//获取id
                    byte[] body = response.getBody(); //获取消息主体
                    String message = new String(body);
                    log.debug("从RabbitMQ接收的消息：" + message);

                    MessageRefEntity entity = new MessageRefEntity();
                    entity.setMessageId(messageId);
                    entity.setReceiverId(Integer.parseInt(topic));
                    entity.setReadFlag(false);
                    entity.setLastFlag(true);

                    messageService.inserRef(entity);
                    long deliveryTag = response.getEnvelope().getDeliveryTag();

                    channel.basicAck(deliveryTag,false);//返回ACK应答 表示消费者接收到消息
                    i++;
                }else {
                    break;
                }

            }
        } catch (Exception e) {
            log.error("执行异常", e);
            throw new EmosException("接收消息失败");
        }
        return i;
    }

    /**
     * 异步接收消息
     * @param topic
     * @return
     */
    @Async
    public int receiveAsync(String topic){
        return recive(topic);
    }

    /**
     * 删除队列
     * @param topic
     */
    public void deleteQueue(String topic){
        try (Connection connection = factory.newConnection(); //创建连接
             Channel channel = connection.createChannel(); //创建队列
        ) {
            channel.queueDelete(topic);
            log.debug("消息队列成功删除");
        } catch (Exception e) {
            log.error("执行异常", e);
            throw new EmosException("删除队列失败");
        }
    }

    @Async
    public void deleteQueueAsync(String topic){
        deleteQueue(topic);
    }

}
