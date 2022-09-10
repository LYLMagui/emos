package com.ukir.emos.wx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Java线程池配置类
 **/
@Configuration
public class ThreadPoolConfig {
    @Bean("AsyncTaskExecutor")
    public AsyncTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置核心线程数
        executor.setCorePoolSize(4);
        //设置最大线程数
        executor.setMaxPoolSize(8);
        //设置队列容量
        executor.setQueueCapacity(16);
        //设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        //设置默认线程名称
        executor.setThreadNamePrefix("task-");
        //设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }
}