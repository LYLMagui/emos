package com.ukir.emos.wx.service.Impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.ukir.emos.wx.config.SystemConstants;
import com.ukir.emos.wx.db.dao.*;
import com.ukir.emos.wx.db.pojo.TbCheckin;
import com.ukir.emos.wx.db.pojo.TbFaceModel;
import com.ukir.emos.wx.exception.EmosException;
import com.ukir.emos.wx.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

/**
 * 考勤业务实现类
 **/
@Service
@Scope("prototype") //设置为多例之后才可以设置多线程
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private SystemConstants constants;

    @Autowired
    private TbHolidaysDao holidaysDao;

    @Autowired
    private TbWorkdayDao workdayDao;

    @Autowired
    private TbCheckinDao checkinDao;

    //注入人脸签到Dao
    @Autowired
    private TbFaceModelDao faceModelDao;

    @Autowired
    private TbCityDao cityDao;

    @Autowired
    private TbUserDao userDao;
    //将yml文件中的人脸签到url注入变量
    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;
    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;

    /**
     * 获取用户的考勤记录并判断是否可以考勤
     *
     * @param userId
     * @param date
     */
    @Override
    public String validCanCheckIn(int userId, String date) {
        //查询今天是否是节假日或是特殊节假日
        boolean bool1_1 = holidaysDao.searchTodayIsHolidays() != null ? true : false;
        boolean bool1_2 = workdayDao.searchTodayIsWorkday() != null ? true : false;

        String type = "工作日";
        //判断是否是周末
        if (DateUtil.date().isWeekend()) {
            type = "节假日";
        }
        if (bool1_1) {
            type = "节假日";
        } else if (bool1_2) {
            type = "工作日";
        }
        //判断需不需要考勤
        if (type.equals("节假日")) {
            return "节假日不需要考勤";
        } else {
            //获取当前时间
            DateTime now = DateUtil.date();

            //获取考勤开始时间
            String start = DateUtil.today() + " " + constants.attendanceStartTime;
            //获取考勤结束时间
            String end = DateUtil.today() + " " + constants.attendanceEndTime;

            DateTime attendanceStart = DateUtil.parse(start);//转换成字符串
            DateTime attendanceEnd = DateUtil.parse(end);//转换成字符串

            if (now.isBefore(attendanceStart)) {
                return "未到上班考勤时间";
            } else if (now.isAfter(attendanceEnd)) {
                return "考勤时间已结束";
            } else {
                //判断用户当前是否已经考勤过
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", start);
                map.put("end", end);
                boolean bool = checkinDao.haveCheckin(map) != null ? true : false;
                return bool ? "当前已考勤，不能重复考勤" : "可以考勤";

            }
        }


    }

    /**
     * 实现人脸签到
     *
     * @param param
     */
    @Override
    public void checkin(HashMap param) {
        //获取当前时间
        Date d1 = DateUtil.date();
        //获取上班时间日期对象
        Date d2 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceTime);
        //获取上班考勤结束时间
        Date d3 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceEndTime);

        int status = 1;
        if (d1.compareTo(d2) <= 0) {
            //如果当前时间早于上班时间，属于正常签到
            status = 1;
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            //如果当前时间晚于上班时间，属于迟到
            status = 2;
        }
        int userId = (Integer) param.get("userId");

        //查询人脸模型数据
        String faceModel = faceModelDao.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("不存在人脸模型");
        } else { //人脸模型存在


            //获取照片路径
            String path = (String) param.get("path");

            //向人脸识别程序发起请求核验人脸模型
            HttpRequest request = HttpUtil.createPost(checkinUrl);
            request.form("photo", FileUtil.file(path), "targetModel"); // 上传图片路径和人脸模型数据  targetModel这个参数由python决定
            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                log.error("人脸识别服务异常");
                throw new EmosException("人脸识别服务异常");
            }
            String body = response.body();
            if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new EmosException(body);

            } else if ("False".equals(body)) {
                throw new EmosException("非本人签到，签到无效");
            }else if ("True".equals(body)){
                // 查询疫情风险等级
                int risk =1; //风险等级 1：低风险 2：中风险 3：高风险
                String city = (String) param.get("city"); //城市
                String district = (String) param.get("district"); //区

                //不为空
                if(!StrUtil.isBlank(city) && !StrUtil.isBlank(district)){
                    //查询城市代码
                    String code = cityDao.searchCode(city);

                    try{
                        String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                        Document document = Jsoup.connect(url).get();
                        //解析HTML
                        Elements elements = document.getElementsByClass("list");
                        if (elements.size() > 0){
                            //获取疫情风险等级
                            String result = elements.select("#\\33 3166 > div:nth-child(2) > p").text();
                            //设置风险等级
                            if("常态化防控".equals(result)){
                                risk = 1;
                            }else if("中风险".equals(result)){
                                risk = 2;
                            }else if("高风险".equals(result)){
                                risk = 3;
                                //TODO 发送警告邮件

                            }
                        }

                    }catch (Exception e){
                        log.error("执行异常",e);
                        throw new EmosException("获取风险等级失败");
                    }

                }
                // 保存签到记录
                String address = (String) param.get("address");
                String country = (String) param.get("country");
                String province = (String) param.get("province");

                TbCheckin entity = new TbCheckin();
                entity.setUserId(userId);
                entity.setAddress(address);
                entity.setCountry(country);
                entity.setProvince(province);
                entity.setCity(city);
                entity.setDistrict(district);
                entity.setStatus((byte) status);
                entity.setDate(DateUtil.today());
                entity.setCreateTime(d1);
                checkinDao.insert(entity);
            }
        }

    }

    /**
     * 创建人脸模型
     *
     * @param userId
     * @param path
     */
    @Override
    public void createFaceModel(int userId, String path) {
        //向python程序发送创建人脸模型的Http请求
        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
        request.form("photo",FileUtil.file(path));//表单信息
        HttpResponse response = request.execute(); //发送请求

        String body = response.body();//接收响应
        if("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)){
            throw new EmosException(body);
        }
        else {

            TbFaceModel entity = new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);

            //向数据库写入人脸模型
            faceModelDao.insert(entity);
        }

    }


}
