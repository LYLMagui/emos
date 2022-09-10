package com.ukir.emos.wx.service;

import java.util.HashMap;

/**
 * 考勤业务
 **/

public interface CheckinService  {
    /**
     * 获取用户的考勤记录并判断是否可以考勤
     */
    public String validCanCheckIn(int userId,String date);

    /**
     * 实现人脸签到
     * @param param
     */
    public void checkin(HashMap param);

    /**
     * 创建人脸模型
     * @param userId
     * @param path
     */
    public void createFaceModel(int userId,String path);
}
