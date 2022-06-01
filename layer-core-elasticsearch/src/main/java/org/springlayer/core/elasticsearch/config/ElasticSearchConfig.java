package org.springlayer.core.elasticsearch.config;

import lombok.Setter;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Hzhi
 * @Date 2022-05-10 11:26
 * @description es配置类
 **/
@Configuration
@ConfigurationProperties(prefix = "es")
@Setter
public class ElasticSearchConfig {
    private String hostname;
    private int port;
    private String protocol;

    //请求的一些选项
    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestClientBuilder restClientBuilder =
                RestClient.builder(new HttpHost(hostname, port, protocol));
        RestHighLevelClient restHighLevelClient =
                new RestHighLevelClient(restClientBuilder);
        return restHighLevelClient;
    }
}