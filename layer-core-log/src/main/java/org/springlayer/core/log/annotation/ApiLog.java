package org.springlayer.core.log.annotation;

import org.springlayer.core.log.enums.BusinessType;
import org.springlayer.core.log.enums.OperatorType;

import java.lang.annotation.*;

/**
 * @author zhihou
 * @date 2022/4/23 14:59
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiLog {

    /**
     * 日志标题
     *
     * @return {String}
     */
    String title() default "日志标题";

    /**
     * 功能
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;
}