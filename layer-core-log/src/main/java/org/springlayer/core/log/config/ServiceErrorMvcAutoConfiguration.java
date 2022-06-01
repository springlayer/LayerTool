//package com.com.example.core.log.config;
//
//import com.com.example.core.log.error.ServiceErrorAttributes;
//import com.com.example.core.log.error.ServiceErrorController;
//import lombok.AllArgsConstructor;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.boot.autoconfigure.condition.SearchStrategy;
//import org.springframework.boot.autoconfigure.web.ServerProperties;
//import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
//import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
//import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
//import org.springframework.boot.web.servlet.error.ErrorAttributes;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.DispatcherServlet;
//
//import javax.servlet.Servlet;
//
///**
// * 统一异常处理
// *
// * @author houzhi
// */
//@Configuration
//@AllArgsConstructor
//@ConditionalOnWebApplication
//@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
//@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
//public class ServiceErrorMvcAutoConfiguration {
//
//    private final ServerProperties serverProperties;
//
//    @Bean
//    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
//    public DefaultErrorAttributes errorAttributes() {
//        return new ServiceErrorAttributes();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
//    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes) {
//        return new ServiceErrorController(errorAttributes, serverProperties.getError());
//    }
//
//}
