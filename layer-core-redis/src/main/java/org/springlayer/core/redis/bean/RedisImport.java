package org.springlayer.core.redis.bean;

import org.springlayer.core.redis.config.RedisRepositoryConfig;
import org.springlayer.core.redis.helper.RedisOperator;
import org.springframework.context.annotation.Import;

/**
 * @Author Hzhi
 * @Date 2022/3/17 11:02
 * @description
 **/
@Import({
        RedisRepositoryConfig.class, RedisOperator.class
})
public class RedisImport {
}
