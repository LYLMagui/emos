package com.ukir.emos.wx;

import cn.hutool.core.util.StrUtil;
import com.ukir.emos.wx.config.SystemConstants;
import com.ukir.emos.wx.db.dao.SysConfigDao;
import com.ukir.emos.wx.db.pojo.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

@SpringBootApplication
@ServletComponentScan //声明开启过滤器
@EnableTransactionManagement
@Slf4j
@EnableAsync //开启异步多线程
public class EmosWxApiApplication {
    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private SystemConstants systemConstants;


    @Value("${emos.image-folder}")
    private String imageFolder;




    public static void main(String[] args) {
        SpringApplication.run(EmosWxApiApplication.class, args);
    }

    @PostConstruct //相对于static
    public void init(){
        List<SysConfig> list = sysConfigDao.selectAllParam();
        list.forEach(one -> {

            String key = one.getParamKey();
            key = StrUtil.toCamelCase(key); //将常量名字转为驼峰命名法
            String value = one.getParamValue();
            try {
                //通过反射赋值
                Field field = systemConstants.getClass().getDeclaredField(key);
                //对封装类中的变量赋值
                field.set(systemConstants,value);
            } catch (Exception e) {
                log.error("执行异常",e);
            }
        });
        //创建文件夹
        new File(imageFolder).mkdirs();

    }
}
