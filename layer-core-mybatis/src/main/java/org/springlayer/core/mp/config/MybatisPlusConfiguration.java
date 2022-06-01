package org.springlayer.core.mp.config;

/**
 * @Author Hzhi
 * @Date 2022-05-07 10:49
 * @description
 **/

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

/**
 * @Author Hzhi
 * @Date 2022-05-07 10:46
 * @description 重写mybatis-plus 自动配置类
 **/
@MapperScan(
        basePackages = {
                "com.example.*.*.engine.mapper.**"
        },
        sqlSessionTemplateRef = "mySqlSessionTemplate",
        sqlSessionFactoryRef = "mySqlSessionFactory"
)
@EnableConfigurationProperties(MybatisPlusProperties.class)
@Configuration
public class MybatisPlusConfiguration extends AbstractMybatisPlusConfiguration {

    @Bean(name = "mySqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource,
                                                   MybatisPlusProperties properties,
                                                   ResourceLoader resourceLoader,
                                                   ApplicationContext applicationContext) throws Exception {
        return getSqlSessionFactory(dataSource,
                properties,
                resourceLoader,
                null,
                null,
                applicationContext);
    }

    @Bean(name = "mySqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(MybatisPlusProperties properties,
                                                 @Qualifier("mySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return getSqlSessionTemplate(sqlSessionFactory, properties);
    }
}