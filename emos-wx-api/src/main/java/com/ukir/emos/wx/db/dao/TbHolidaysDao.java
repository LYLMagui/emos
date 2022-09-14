package com.ukir.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbHolidaysDao {
    //查询今天是否是节假日
    public Integer searchTodayIsHolidays();

    /**
     * 查询日期是否特殊节假日
     * @param param
     * @return
     */
    public ArrayList<String> searchHolidaysInRange(HashMap param);


}