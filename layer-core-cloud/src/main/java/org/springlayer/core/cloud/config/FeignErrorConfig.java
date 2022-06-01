package org.springlayer.core.cloud.config;

import com.alibaba.fastjson.JSON;
import org.springlayer.core.tool.api.R;
import org.springlayer.core.tool.exception.BusinessException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @Author Hzhi
 * @Date 2022/3/16 11:35
 * @description Feign 异常配置拦截处理
 **/
@Slf4j
@Configuration
public class FeignErrorConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {

    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    /**
     * 自定义错误
     */
    public class FeignErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            Exception exception = null;
            try {
                // 获取原始的返回内容
                String json = Util.toString(response.body().asReader(Charset.defaultCharset()));
                exception = new RuntimeException(json);
                // 将返回内容反序列化为Result，这里应根据自身项目作修改
                R result = JSON.parseObject(json, R.class);
                // 业务异常抛出简单的 RuntimeException，保留原来错误信息
                if (!result.isSuccess()) {
                    exception = new BusinessException(result.getMsg());
                }
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
            return exception;
        }
    }
}