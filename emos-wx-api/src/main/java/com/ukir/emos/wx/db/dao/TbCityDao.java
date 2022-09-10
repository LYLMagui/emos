package com.ukir.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbCityDao {

    /**
     * 查询城市代码
     * @param city
     * @return
     */
    public String searchCode(String city);

}