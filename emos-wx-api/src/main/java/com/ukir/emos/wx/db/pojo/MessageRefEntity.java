package com.ukir.emos.wx.db.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 消息接收人模块映射类
 **/
@Data
@Document(collection = "message_ref") //映射到mongoDB中的message_ref这个集合
public class MessageRefEntity implements Serializable { //序列化

    @Id //标记为主键
    private String _id;

    @Indexed
    private String messageId;

    @Indexed
    private Integer receiverId; //接受者id

    @Indexed
    private Boolean readFlag;

    @Indexed
    private Boolean lastFlag;
}
