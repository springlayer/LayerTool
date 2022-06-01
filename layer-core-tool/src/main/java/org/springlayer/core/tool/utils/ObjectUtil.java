package org.springlayer.core.tool.utils;

import org.springframework.lang.Nullable;

/**
 * 对象工具类
 *
 * @author houzhi
 */
public class ObjectUtil extends org.springframework.util.ObjectUtils {

	/**
	 * 判断元素不为空
	 * @param obj object
	 * @return boolean
	 */
	public static boolean isNotEmpty(@Nullable Object obj) {
		return !ObjectUtil.isEmpty(obj);
	}

	/**
	 * 判断字符串不为空
	 * @param str
	 * @return
      */
     public static boolean notEmpty(String str){
        return str != null && !"".equals(str);
    }

	/**
	 * 判断字符串不为空
	 * jdk StringUtils工具类实现如下所示
	 * @param str
	 * @return
	 */
     public static boolean isNotEmpty(String str){
	         return !isEmpty(str);
	 }

	 /**
	* 判断字符串为空
	* @param str
	* @return
	*/
	public static boolean isEmpty(String str){
		return str == null || str.length() == 0;
	}
}
