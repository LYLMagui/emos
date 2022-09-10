package com.ukir.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbWorkdayDao {
    //查询今天是否是特殊工作日
    public Integer searchTodayIsWorkday();
}