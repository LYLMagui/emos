package com.ukir.emos.wx.service.Impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.iai.v20200303.IaiClient;
import com.tencentcloudapi.iai.v20200303.models.CreatePersonRequest;
import com.tencentcloudapi.iai.v20200303.models.CreatePersonResponse;
import com.tencentcloudapi.iai.v20200303.models.VerifyPersonRequest;
import com.tencentcloudapi.iai.v20200303.models.VerifyPersonResponse;
import com.ukir.emos.wx.common.util.ImgToBase64Util;
import com.ukir.emos.wx.config.SystemConstants;
import com.ukir.emos.wx.db.dao.*;
import com.ukir.emos.wx.db.pojo.TbCheckin;
import com.ukir.emos.wx.db.pojo.TbFaceModel;
import com.ukir.emos.wx.db.pojo.TbUser;
import com.ukir.emos.wx.exception.EmosException;
import com.ukir.emos.wx.service.CheckinService;
import com.ukir.emos.wx.task.EmailTask;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Autowired
    private EmailTask emailTask;

    //将yml文件中的人脸签到url注入变量
    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;
    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;


    //注入yml中腾讯云的密钥
    @Value("${emos.tencent-could.secret-id}")
    private String secretId;
    @Value("${emos.tencent-could.secret-key}")
    private String secretKey;
    @Value("${emos.tencent-could.region}")
    private String region;//广州服务器
    //请求地址
    @Value("${emos.tencent-could.request-url}")
    private String requestUrl;

    //注入yml文件中的hr邮箱和系统管理员邮箱
    @Value("${emos.email.hr}")
    private String hrEmail;
    @Value("${emos.email.system}")
    private String systemEmail;

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
//            } else if (now.isAfter(attendanceEnd)) {
//                return "考勤时间已结束";
            } else {
                //判断用户当前是否已经考勤过
                System.out.println("判断是否可以考勤中");
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", start);
                map.put("end", end);
                boolean bool = checkinDao.haveCheckin(map) != null ? true : false;
                return bool ? "不能重复考勤" : "可以考勤";

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
        } else {
            // TODO 记得去掉注释
//            throw new EmosException("超出考勤时间段，无法考勤");
        }
        //获取用户id
        String userId = String.valueOf(param.get("userId"));
        String faceModel = faceModelDao.searchFaceModel(Integer.parseInt(userId));

        if (faceModel == null) {
            throw new EmosException("不存在人脸模型");
        } else {
            String path = (String) param.get("path");
            String base64Image = ImgToBase64Util.imageToBase64(path);


            //从腾讯云人员管理库中查询是否存在人脸

            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            Credential cred = new Credential(secretId, secretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(requestUrl);
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的

            com.tencentcloudapi.iai.v20200303.IaiClient client = new IaiClient(cred, region, clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            VerifyPersonRequest req = new VerifyPersonRequest();
//            req.setUrl(path);
            req.setImage(base64Image);
            req.setPersonId(userId);


            try {
                // 返回的resp是一个VerifyPersonResponse的实例，与请求对象对应
                VerifyPersonResponse response = client.VerifyPerson(req);
                // 接收json格式的字符串回包
                String resp = VerifyPersonResponse.toJsonString(response);


                JSONObject json = JSONUtil.parseObj(resp);

                if ("false".equals(json.get("IsMatch").toString())) {
                    throw new EmosException("非本人签到，签到无效");

                } else if (json.get("IsMatch") == null) {
                    throw new EmosException("人脸验证异常，请稍后再试");
                } else if ("true".equals(json.get("IsMatch").toString())) {
                    // 查询疫情风险等级
                    int risk = 1; //风险等级 1：低风险 2：中风险 3：高风险
                    String city = (String) param.get("city"); //城市
                    String district = (String) param.get("district"); //区

                    //不为空
                    if (!StrUtil.isBlank(city) && !StrUtil.isBlank(district)) {
                        //查询城市代码
                        String code = cityDao.searchCode(city);

                        try {
                            String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                            Document document = null;

                            document = Jsoup.connect(url).get();


                            //解析HTML
                            Elements elements = document.getElementsByClass("list");
                            if (elements.size() > 0) {

                                //获取疫情风险等级
                                String result = elements.select("div > div:nth-child(2) > p").text();
                                result = "高风险";
                                //设置风险等级
                                if ("常态化防控".equals(result)) {
                                    risk = 1;
                                } else if ("中风险".equals(result)) {
                                    risk = 2;
                                } else if ("高风险".equals(result)) {
                                    risk = 3;
                                    //TODO 发送警告邮件

                                    HashMap<String,String> map = userDao.searchNameAndDept(Integer.parseInt(userId));
                                    //获取员工姓名
                                    String name = map.get("name");
                                    //获取员工所属部门
                                    String deptName = map.get("dept_name");
                                    deptName = deptName != null ? deptName : "";

                                    SimpleMailMessage message = new SimpleMailMessage();

                                    //向系统管理员邮箱发送邮件（收件人地址）
                                    message.setTo(systemEmail);
                                    //邮件标题
                                    message.setSubject("员工" + name + "身处疫情高风险地区");
                                    //正文内容
                                    message.setText(deptName + "员工，" + name + "，" + DateUtil.format(new Date(),"yyyy年MM月dd日") + "" +
                                            "处于" + param.get("address").toString() + "，属于新冠疫情高风险地区，请及时与该员工联系，核实情况！" );
                                    emailTask.sendAsync(message);


                                }
                            }
                        } catch (IOException e) {
                            log.error("执行异常", e);
                            throw new EmosException("获取风险等级失败");
                        }

                    }

                    String address = (String) param.get("address");
                    String country = (String) param.get("country");
                    String province = (String) param.get("province");

                    TbCheckin entity = new TbCheckin();
                    entity.setUserId((Integer) param.get("userId"));
                    entity.setAddress(address);
                    entity.setCountry(country);
                    entity.setProvince(province);
                    entity.setCity(city);
                    entity.setDistrict(district);
                    entity.setStatus((byte) status);
                    entity.setDate(DateUtil.today());
                    entity.setCreateTime(d1);
                    entity.setRisk(risk);
                    checkinDao.insert(entity);
                }

                //
            } catch (TencentCloudSDKException e) {
                throw new EmosException("签到失败，"+e.getMessage());
            }





        /*
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
*/
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
        String base64Image = ImgToBase64Util.imageToBase64(path);

        try {
            TbUser user = userDao.searchById(userId);

            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            Credential cred = new Credential(secretId, secretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(requestUrl);
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, region, clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreatePersonRequest req = new CreatePersonRequest();
            req.setGroupId("emos-face");
            req.setPersonName(user.getNickname());
            req.setPersonId(String.valueOf(userId)); //设置人员id
            req.setImage(base64Image); //传入base64图片数据
//            req.setUrl(path);
            // 返回的resp是一个CreatePersonResponse的实例，与请求对象对应
            CreatePersonResponse resp = client.CreatePerson(req);
            // 输出json格式的字符串回包
            String response = CreatePersonResponse.toJsonString(resp);
            JSONObject json = JSONUtil.parseObj(response);
            System.out.println(json.toString());
            if (json.get("FaceId") != null) {
                TbFaceModel entity = new TbFaceModel();
                entity.setUserId(userId);
                entity.setFaceModel(json.get("FaceId").toString());
                faceModelDao.insert(entity);
            }else if(json.get("Error") != null){
                throw new EmosException(json.get("Message").toString());
            }
        } catch (TencentCloudSDKException e) {
            log.error("录入人脸数据出现异常", e);
            throw new EmosException("录入失败"+"\n" + e.getMessage());
        }


    /*
        //向python程序发送创建人脸模型的Http请求
        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
        request.form("photo", FileUtil.file(path));//表单信息
        HttpResponse response = request.execute(); //发送请求

        String body = response.body();//接收响应
        if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
            throw new EmosException(body);
        } else {

            TbFaceModel entity = new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);

            //向数据库写入人脸模型
            faceModelDao.insert(entity);
        }
    */
    }


}
