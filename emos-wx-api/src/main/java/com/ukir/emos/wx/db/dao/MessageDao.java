package com.ukir.emos.wx.db.dao;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.ukir.emos.wx.db.pojo.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 消息模块Dao
 **/
@Repository
public class MessageDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public String insert(MessageEntity entity){
        //把北京时间转换成格林尼治时间
        Date sendTime = entity.getSendTime();
        DateUtil.offset(sendTime, DateField.HOUR,8);

        entity.setSendTime(sendTime);

        //将entity传入
        entity = mongoTemplate.save(entity);

        return entity.get_id();

    }


}
