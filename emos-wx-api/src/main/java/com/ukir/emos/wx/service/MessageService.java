package com.ukir.emos.wx.service;

import com.ukir.emos.wx.db.pojo.MessageEntity;
import com.ukir.emos.wx.db.pojo.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

/**
 *  消息模块业务层
 **/
public interface MessageService  {

    /**
     * 创建消息
     * @param entity
     * @return
     */
    public String insertMessage(MessageEntity entity);

    /**
     * 创建接对象
     * @param entity
     * @return
     */
    public String inserRef(MessageRefEntity entity);

    /**
     * 查询用户的未读消息
     * @param userId
     * @return
     */
    public long searchUnreadcount(int userId);

    /**
     * 查询最新消息的数量
     * @param userId
     * @return
     */
    public long searchLastcount(int userId);

    /**
     * 分页查询
     * @param userId
     * @param start
     * @param length
     * @return
     */
    public List<HashMap> searchMessageByPage(int userId, long start, int length);

    /**
     * 根据id查询消息
     * @param id
     * @return
     */
    public HashMap searchMessageById(String id);

    /**
     * 更新未读消息的状态
     * @param id
     * @return
     */
    public long updateUnreadMessage(String id);

    /**
     * 根据id删除消息
     * @param id
     * @return
     */
    public long deleteMessageRefById(String id);

    /**
     * 删除用户的消息
     * @param userId
     * @return
     */
    public long deleteUserMessageRef(int userId);
}
