package org.springlayer.core.tool.utils;

import org.springlayer.core.tool.exception.BusinessException;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/**
 * @author zhihou
 * @date 2022/03/16 8:53
 * @description
 */
public class AssertUtil {

    /**
     * 判断对象为空
     *
     * @param obj
     * @param msg
     */
    public static void isEmpty(Object obj, String msg) {
        if (ObjectUtils.isEmpty(obj)) {
            throw new BusinessException(msg);
        }
    }

    /**
     * 判断对象不为空
     *
     * @param msg
     * @param obj
     */
    public static void isNotEmpty(Object obj, String msg) {
        if (!ObjectUtils.isEmpty(obj)) {
            throw new BusinessException(msg);
        }
    }

    /**
     * 判断对象不为空
     *
     * @param obj
     * @param msg
     */
    public static void isBolEmpty(@Nullable Boolean obj, @Nullable String msg) {
        if (obj) {
            throw new BusinessException(msg);
        }
    }
}
