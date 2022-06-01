package org.springlayer.core.boot.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @Author Hzhi
 * @Date 2022/3/16 9:19
 * @description
 **/
@Configuration
@SuppressWarnings("all")
public class RestTemplateConfig {

    @Resource
    private RestTemplateBuilder builder;

    @Bean
    public RestTemplate restTemplate() {
        return builder.build();
    }
}
