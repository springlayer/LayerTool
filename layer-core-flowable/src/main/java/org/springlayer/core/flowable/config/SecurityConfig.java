package org.springlayer.core.flowable.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description
 **/
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        //ignore
        web.ignoring().antMatchers("/info","/health","/hystrix.stream","/actuator/*");
    }
}
