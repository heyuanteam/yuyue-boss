package com.yuyue.boss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//开启定时任务
@EnableScheduling
//开启异步调用方法
@EnableAsync
@SpringBootApplication
public class BossApplication {

    public static void main(String[] args) { SpringApplication.run(BossApplication.class, args); }

    //上线放开
//    public class AppApplication extends SpringBootServletInitializer {
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        // 注意这里要指向原先用main方法执行的Application启动类
//        return builder.sources(AppApplication.class);
//    }
}
