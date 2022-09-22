package com.ukir.emos.wx.service.Impl;

import com.ukir.emos.wx.db.dao.MessageDao;
import com.ukir.emos.wx.db.dao.MessageRefDao;
import com.ukir.emos.wx.db.pojo.MessageEntity;
import com.ukir.emos.wx.db.pojo.MessageRefEntity;
import com.ukir.emos.wx.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 消息模块实现类
 **/
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MessageRefDao messageRefDao;

    /**
     * 创建消息
     *
     * @param entity
     * @return
     */
    @Override
    public String insertMessage(MessageEntity entity) {
        String id = messageDao.insert(entity);
        return id;
    }

    /**
     * 创建接对象
     *
     * @param entity
     * @return
     */
    @Override
    public String inserRef(MessageRefEntity entity) {
        String id = messageRefDao.insert(entity);
        return id;
    }

    /**
     * 查询用户的未读消息
     *
     * @param userId
     * @return
     */
    @Override
    public long searchUnreadcount(int userId) {
        long count = messageRefDao.searchUnreadCount(userId);
        return count;
    }

    /**
     * 查询最新消息的数量
     *
     * @param userId
     * @return
     */
    @Override
    public long searchLastcount(int userId) {
        long count = messageRefDao.searchLastCount(userId);
        return count;
    }

    /**
     * 分页查询
     *
     * @param userId
     * @param start
     * @param length
     * @return
     */
    @Override
    public List<HashMap> searchMessageByPage(int userId, long start, int length) {
        List<HashMap> list = messageDao.searchMessageByPage(userId, start, length);
        return list;
    }

    /**
     * 根据id查询消息
     *
     * @param id
     * @return
     */
    @Override
    public HashMap searchMessageById(String id) {
        HashMap map = messageDao.searchMessageById(id);
        return map;
    }

    /**
     * 更新未读消息的状态
     *
     * @param id
     * @return
     */
    @Override
    public long updateUnreadMessage(String id) {
        long rows = messageRefDao.updateUnreadMessage(id);
        return rows;
    }

    /**
     * 根据id删除消息
     *
     * @param id
     * @return
     */
    @Override
    public long deleteMessageRefById(String id) {
        long rows = messageRefDao.deleteMessageRefById(id);
        return rows;
    }

    /**
     * 删除用户的消息
     *
     * @param userId
     * @return
     */
    @Override
    public long deleteUserMessageRef(int userId) {
        long rows = messageRefDao.deleteuserMassageRef(userId);
        return rows;
    }
}
