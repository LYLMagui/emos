package com.ukir.emos.wx.db.dao;

import com.ukir.emos.wx.db.pojo.TbFaceModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbFaceModelDao {
    /**
     * 查询人脸模型
     * @param userId
     * @return
     */
    public String searchFaceModel(int userId);

    /**
     * 添加人脸模型
     * @param faceModel
     */
    public void insert(TbFaceModel faceModel);

    /**
     * 删除人脸模型
     * @param userId
     * @return
     */
    public int deleteFaceModel(int userId);

}