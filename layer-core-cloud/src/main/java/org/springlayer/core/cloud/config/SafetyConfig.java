package org.springlayer.core.cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @Author Hzhi
 * @Date 2022/3/16 12:00
 * @description AES 加密
 */
@Component
@ConfigurationProperties(prefix = "safety")
@Data
@RefreshScope
public class SafetyConfig {

    /**
     * AES密码
     */
    private String aesPassword;
}