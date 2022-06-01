package org.springlayer.core.mp.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 分页工具
 *
 * @author houzhi
 */
public class Condition {

    /**
     * 获取mybatis plus中的QueryWrapper
     *
     * @param entity
     * @param <T>
     * @return <T>
     */
    public static <T> QueryWrapper getQueryWrapper(T entity) {
        return new QueryWrapper<>(entity);
    }
}