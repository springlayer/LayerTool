package org.springlayer.core.cloud.config;

import org.springlayer.core.cloud.constant.AppConstant;
import org.springlayer.core.cloud.http.OkHttpLoggingInterceptor;
import org.springlayer.core.cloud.http.OkHttpSlf4jLogger;
import feign.Feign;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

/**
 * @Author Hzhi
 * @Date 2022/3/16 10:50
 * @description Feign 配置环境区分
 **/
@Slf4j
@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class FeignConfig {

    @Bean
    @Profile(AppConstant.ENV_DEVELOP)
    public OkHttpLoggingInterceptor devLoggingInterceptor() {
        OkHttpLoggingInterceptor interceptor = new OkHttpLoggingInterceptor(new OkHttpSlf4jLogger());
        interceptor.setLevel(OkHttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Bean
    @Profile(AppConstant.ENV_PROD)
    public OkHttpLoggingInterceptor prodLoggingInterceptor() {
        OkHttpLoggingInterceptor interceptor = new OkHttpLoggingInterceptor(new OkHttpSlf4jLogger());
        interceptor.setLevel(OkHttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

    @Bean
    public okhttp3.OkHttpClient okHttpClient(OkHttpLoggingInterceptor interceptor) {
        return new okhttp3.OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool()).addInterceptor(interceptor)
                .build();
    }
}