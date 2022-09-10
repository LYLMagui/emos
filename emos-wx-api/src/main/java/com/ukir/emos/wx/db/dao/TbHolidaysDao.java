package com.ukir.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbHolidaysDao {
    //查询今天是否是节假日
    public Integer searchTodayIsHolidays();
}