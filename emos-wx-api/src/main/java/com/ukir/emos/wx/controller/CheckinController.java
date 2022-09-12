package com.ukir.emos.wx.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.ukir.emos.wx.common.util.Result;
import com.ukir.emos.wx.config.shiro.JwtUtil;
import com.ukir.emos.wx.controller.form.CheckinForm;
import com.ukir.emos.wx.exception.EmosException;
import com.ukir.emos.wx.service.CheckinService;
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
    public Result createFaceModel(@RequestParam("photo") MultipartFile file,HttpServletRequest request) {
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
}
