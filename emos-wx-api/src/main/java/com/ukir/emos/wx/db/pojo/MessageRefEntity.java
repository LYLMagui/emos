package com.ukir.emos.wx.db.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息模块映射类
 **/
@Data
@Document(collection = "message") //映射到mongoDB中的message这个集合
public class MessageEntity implements Serializable { //序列化

    @Id //标记为主键
    private String _id;

    @Indexed(unique = true) //设置唯一性
    private String uuid;

    @Indexed
    private Integer senderId; //生产者id

    //头像
    private String senderPhoto = "https://thirdwx.qlogo.cn/mmopen/vi_32/xzaIew8zNic4xjFcYlInwBQiaibGv1LpL3ia30oiaC0mT941652MFEwGVPeOhtwicQgFibUH0axjhRrD6fib1O0vZhf1eg/132";


    private String msg; //正文消息

    @Indexed
    private Date sendTime;
}
