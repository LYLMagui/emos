package com.ukir.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbWorkdayDao {
    //查询今天是否是特殊工作日
    public Integer searchTodayIsWorkday();

    /**
     * 查询是否特殊工作日
     * @param param
     * @return
     */
    public ArrayList<String> searchWorkdayInRange(HashMap param);
}