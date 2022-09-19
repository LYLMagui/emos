package com.ukir.emos.wx.service;

import java.util.ArrayList;
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


    /**
     * 查询用户月签到情况
     * @param param
     * @return
     */
    public ArrayList<HashMap> searchMonthCheckin(HashMap param);


}
