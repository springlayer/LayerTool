package org.springlayer.core.log.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

/**
 * @author houzhi
 * @date 2020-1-14
 * @description
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnWebApplication
public class ServiceLogToolAutoConfiguration {

//    private final LogClient logService;

//    @Bean
//    public ApiLogListener apiLogListener() {
//        return new ApiLogListener(logService);
//    }
}
