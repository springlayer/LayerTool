//package com.example.core.transaction.config;
//
//import com.alibaba.cloud.seata.web.SeataHandlerInterceptor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * @author zhihou
// * @date 2020/8/19 20:29
// * @description
// */
//@Slf4j
//@Configuration
//public class SeataConfiguration implements WebMvcConfigurer {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        log.info("seata 上下文xid1 ...");
//        registry.addInterceptor(new SeataHandlerInterceptor()).addPathPatterns(new String[]{"/**"});
//    }
//}
