package com.ukir.emos.wx.db.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.ukir.emos.wx.db.pojo.MessageRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * 消息模块Dao
 **/
@Repository
public class MessageRefDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public String insert(MessageRefEntity entity){
        entity = mongoTemplate.save(entity);
        return entity.get_id();
    }


    /**
     * 查询未读消息数量
     * @param userId
     * @return
     */
    public long searchUnreadCount(int userId){
        Query query = new Query();
        query.addCriteria(Criteria.where("readFlag").is(false).and("receiverId").is(userId));
        long count = mongoTemplate.count(query, MessageRefEntity.class,"message_ref");
        return count;
    }

    /**
     * 查询最新消息的数量
     * @param userId
     * @return
     */
    public  long searchLastCount(int userId){
        Query query = new Query();
        query.addCriteria(Criteria.where("lastFlag").is(true).and("receiverId").is(userId));
        Update update = new Update();
        update.set("lastFlag",false);
        UpdateResult result = mongoTemplate.updateMulti(query, update, "message_ref");
        long rows = result.getModifiedCount(); //获取修改了多少条数据
        return rows;
    }

    /**
     * 更新消息的状态
     * @param id
     * @return
     */
    public long updateUnreadMessage(String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("readFlag",true);
        UpdateResult result = mongoTemplate.updateFirst(query, update, "message_ref");
        long rows = result.getModifiedCount();
        return rows;
    }

    /**
     * 删除消息
     * @param id
     * @return
     */
    public long deleteMessageRefById(String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long rows = result.getDeletedCount();
        return rows;
    }

    /**
     * 根据用户id删除用户所有的信息
     */

    public long deleteuserMassageRef(int userId){
        Query query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(userId));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long rows = result.getDeletedCount();
        return rows;
    }

}
