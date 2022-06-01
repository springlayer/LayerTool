package org.springlayer.core.oss.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author Hzhi
 * @Date 2022-05-30 15:00
 * @description
 **/
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    /**
     * 注入minio 客户端
     *
     * @return MinioClient
     */
    @Bean
    public MinioClient minioClient() {

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}