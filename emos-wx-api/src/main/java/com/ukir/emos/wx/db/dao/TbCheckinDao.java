package com.ukir.emos.wx.db.dao;

import com.ukir.emos.wx.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface TbCheckinDao {
    /**
     * 查询当天是否可以签到
     * @param param
     * @return
     */
    public Integer haveCheckin(HashMap param);

    /**
     * 保存用户签到数据
     * @param checkin
     */
    public void insert(TbCheckin checkin);


}