package com.ukir.emos.wx.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 常量封装类
 **/
@Data
@Component
public class SystemConstants {
    public String attendanceStartTime; //上班考勤开始时间
    public String attendanceTime;
    public String attendanceEndTime; // 上班考勤结束时间
    public String closingStartTime; //下班考勤开始时间
    public String closingTime;
    public String closingEndTime; //下班考勤结束时间
}


