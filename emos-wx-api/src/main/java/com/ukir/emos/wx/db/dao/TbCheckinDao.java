package com.ukir.emos.wx.db.dao;

import com.ukir.emos.wx.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
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

    /**
     * 查询用户今日签到情况
     * @param userId
     * @return
     */
    public HashMap searchTodayCheckin(int userId);

    /**
     * 查询用户签到总天数
     * @param userId
     * @return
     */
    public long searchCheckinDays(int userId);


    /**
     * 查询用户周签到情况
     * @param param
     * @return
     */
    public ArrayList<HashMap> searchWeekCheckin(HashMap param);
}