package com.ukir.emos.wx.db.dao;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.ukir.emos.wx.db.pojo.MessageEntity;
import com.ukir.emos.wx.db.pojo.MessageRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 消息模块Dao
 **/
@Repository
public class MessageDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 插入数据
     * @param entity
     * @return 插入的id
     */
    public String insert(MessageEntity entity){
        //把北京时间转换成格林尼治时间
        Date sendTime = entity.getSendTime();
        DateUtil.offset(sendTime, DateField.HOUR,8);

        entity.setSendTime(sendTime);

        //将entity传入
        entity = mongoTemplate.save(entity);

        return entity.get_id();

    }

    /**
     * 分页查询
     * @param userId
     * @param start
     * @param length
     * @return
     */
    public List<HashMap> searchMessageByPage(int userId ,long start,int length){
        JSONObject json = new JSONObject();
        json.set("$toString","$_id"); // 封装成json并转换成字符串类型
        //构建集合连接对象，用于集合查询
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.addFields().addField("id").withValue(json).build(), //创建一个临时变量存储id
                Aggregation.lookup("message_ref","id","messageId","ref"), //关联ref集合中的messageId与上面保存的临时字段id
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)), //查询发送给某个人的消息
                Aggregation.sort(Sort.by(Sort.Direction.DESC,"sendTime")), //通过时间降序排序
                Aggregation.skip(start), //分页
                Aggregation.limit(length) //一页展示多少条

        );

        //联合message这个集合进行查询，泛型为HashMap
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "message", HashMap.class);
        List<HashMap> list = results.getMappedResults(); //转换为list
        list.forEach(one ->{
            List<MessageRefEntity> refList  = (List<MessageRefEntity>) one.get("ref");
            MessageRefEntity entity = refList.get(0);
            Boolean readFlag = entity.getReadFlag();
            String refId = entity.get_id();
            one.put("readFlag",readFlag);
            one.put("refId",refId);
            //删除无用数据
            one.remove("ref");
            one.remove("_id");
            Date sendTime = (Date) one.get("sendTime");
            DateUtil.offset(sendTime,DateField.HOUR,-8);//把格林尼治时间转换成北京时间

            String today = DateUtil.today();
            if(today.equals(DateUtil.date(sendTime).toDateStr())){
                one.put("sendTime", DateUtil.format(sendTime,"HH:mm"));
            }else {
                one.put("sendTime",DateUtil.format(sendTime,"yyyy/MM/dd"));
            }

        });
        return list;
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public HashMap searchMessageById(String id){
        HashMap map = mongoTemplate.findById(id, HashMap.class, "message");
        Date sendTime = (Date) map.get("sendTime");
        DateUtil.offset(sendTime,DateField.HOUR,-8);//把格林尼治时间转换成北京时间
        map.replace("sendTime",DateUtil.format(sendTime,"yyyy-MM-dd HH:mm"));
        return map;
    }
}
