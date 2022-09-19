package com.ukir.emos.wx.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.ukir.emos.wx.common.util.Result;
import com.ukir.emos.wx.config.SystemConstants;
import com.ukir.emos.wx.config.shiro.JwtUtil;
import com.ukir.emos.wx.controller.form.CheckinForm;
import com.ukir.emos.wx.controller.form.SearchMonthCheckinForm;
import com.ukir.emos.wx.exception.EmosException;
import com.ukir.emos.wx.service.CheckinService;
import com.ukir.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 用户考勤
 **/
@RestController
@RequestMapping("/checkin")
@Api(tags = "签到模块Web接口")
@Slf4j
public class CheckinController {
    //用于从token中获取用户id
    @Autowired
    private JwtUtil jwtUtil;


    //注入yml文件中的存放照片的路径
    @Value("${emos.image-folder}")
    private String imageFolder;

    @Autowired
    private CheckinService checkinService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConstants constants;


    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户今天是否可以签到")
    public Result validCanCheckIn(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckIn(userId, DateUtil.today());
        return Result.ok(result);
    }


    @PostMapping("/checkin")
    @ApiOperation("签到")
    public Result checkin(@Valid CheckinForm form, @RequestParam("photo") MultipartFile file, HttpServletRequest request) {
        String token = request.getHeader("token");

        if (file == null) { //判断上传的照片是否为空
            return Result.error("没有上传文件");
        }
        int userId = jwtUtil.getUserId(token); //获取用户id
        String fileName = file.getOriginalFilename().toLowerCase(); // 获取文件后缀名

        if (!fileName.endsWith(".jpg")) {
            //文件非jpg格式
            return Result.error("必须提交JPG格式的图片");
        } else {
            String path = imageFolder + "/" + fileName;
            try {
                file.transferTo(Paths.get(path));

                HashMap param = new HashMap();
                param.put("userId", userId);
                param.put("path", path);
                param.put("city", form.getCity());
                param.put("district", form.getDistrict());
                param.put("address", form.getAddress());
                param.put("country", form.getCountry());
                param.put("province", form.getProvince());

                checkinService.checkin(param);
                return Result.ok("签到成功");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new EmosException("图片保存错误");
            } finally {
                //删除照片
                FileUtil.del(path);
            }
        }

    }


    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public Result createFaceModel(@RequestParam("photo") MultipartFile file, HttpServletRequest request) {
        String token = request.getHeader("token");
        if (file == null) { //判断上传的照片是否为空
            return Result.error("没有上传文件");
        }
        int userId = jwtUtil.getUserId(token); //获取用户id
        String fileName = file.getOriginalFilename().toLowerCase(); // 获取文件后缀名

        if (!fileName.endsWith(".jpg")) {
            //文件非jpg格式
            return Result.error("必须提交JPG格式的图片");
        } else {
            String path = imageFolder + "/" + fileName;
            try {
                //获取图片路径并生成图片文件
                file.transferTo(Paths.get(path));
                //调用创建人脸模型方法
                checkinService.createFaceModel(userId, path);
                return Result.ok("人脸建模成功");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new EmosException("图片保存错误");
            } finally {
                //删除照片
                FileUtil.del(path);
            }
        }

    }

    @GetMapping("/searchTodayCheckin")
    @ApiOperation("查询用户当日签到数据")
    public Result searchTodayCheckin(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);

        HashMap map = checkinService.searchTodayCheckin(userId); //查询当天考勤结果
        map.put("attendanceStartTime", constants.attendanceStartTime); //存入考勤开始时间
        map.put("attendanceTime", constants.attendanceTime);//上班时间
        map.put("closingTime", constants.closingTime); //下班考勤结束时间

        long days = checkinService.searchCheckinDays(userId); //获取考勤总天数
        map.put("checkinDays", days);

        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId)); // 获取入职日期对象

        DateTime startDate = DateUtil.beginOfWeek(DateUtil.date());//获取当前周的周一日期

        if (startDate.isBefore(hiredate)) { //本周开始日期是否在入职日期之前
            startDate = hiredate;
        }

        DateTime endDate = DateUtil.endOfWeek(DateUtil.date()); // 获取本周结束日期


        HashMap param = new HashMap();
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());
        param.put("userId", userId);

        ArrayList<HashMap> list = checkinService.searchWeekCheckin(param);//查询本周考勤数据
        map.put("weekCheckin", list);
        return Result.ok().put("result", map);
    }


    @PostMapping("/searchMonthCheckin")
    @ApiOperation("查询用户某月签到数据")
    public Result searchMonthCheckin(@Valid @RequestBody SearchMonthCheckinForm form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        System.out.println("年份是：" + form.getYear());
        //查询员工入职日期
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        String month = form.getMonth() < 10 ? "0" + form.getMonth() : form.getMonth().toString(); //月份小于10转成字符串时在前面加一个0，大于等于10则不用

        //获取某年某月的第一天
        DateTime startDate = DateUtil.parse(form.getYear() + "-" + form.getMonth() + "-01");
        if (startDate.isBefore(DateUtil.beginOfMonth(hiredate))) { //员工入职前的日期不可查看
            throw new EmosException("只能查询入职当月即之后的数据");
        }
        if (startDate.isBefore(hiredate)) {
            //将查询的日期从月第一天变为入职那一天
            startDate = hiredate;
        }

        DateTime endDate = DateUtil.endOfMonth(startDate);

        HashMap param = new HashMap();
        param.put("uerId", userId);
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());

        //查询当月的考勤记录
        ArrayList<HashMap> list = checkinService.searchMonthCheckin(param);
        int sum_1 = 0, sum_2 = 0, sum_3 = 0;

        for (HashMap<String, String> one : list) {
            String type = one.get("type"); //当天是工作日还是节假日

            String status = one.get("status"); // 获取考勤结果


            if ("工作日".equals(type)) {
                if ("正常".equals(status)) {
                    //统计正常签到天数
                    sum_1++;
                } else if ("迟到".equals(status)) {
                    //统计迟到签到天数
                    sum_2++;
                } else if ("缺勤".equals(status)) {
                    //统计缺勤签到天数
                    sum_3++;
                }
            }
        }
        return Result.ok().put("list", list).put("sum_1", sum_1).put("sum_2", sum_2).put("sum_3", sum_3);
    }

}
