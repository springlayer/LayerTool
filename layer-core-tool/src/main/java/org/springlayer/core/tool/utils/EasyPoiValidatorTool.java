package org.springlayer.core.tool.utils;

import cn.afterturn.easypoi.excel.annotation.Excel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Author zhaoyl
 * @Date 2022-05-25 17:28
 * 使用new方式创建对象并使用
 */
public class EasyPoiValidatorTool<T> {
    /**
     * 需要被反射的对象，使用泛型规范传入对象
     */
    public T t;

    /**
     * 修改注解@Excel的属性值
     * @param attributeName
     * @param columnName
     * @param targetValue
     * @throws Exception
     */
    public void changeAttribute(String attributeName, String columnName, Object targetValue) throws Exception {
        if (t == null) {
            throw new ClassNotFoundException("未找到目标类");
        }
        if (StringUtils.isEmpty(attributeName)) {
            throw new NullPointerException("传入的注解属性为空");
        }
        if (StringUtils.isEmpty(columnName)) {
            throw new NullPointerException("传入的属性列名为空");
        }
        //获取目标对象的属性值
        Field field = t.getClass().getDeclaredField(columnName);
        //获取注解反射对象
        Excel excelAnion = field.getAnnotation(Excel.class);
        //获取代理
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(excelAnion);
        Field excelField = invocationHandler.getClass().getDeclaredField("memberValues");
        excelField.setAccessible(true);
        Map memberValues = (Map) excelField.get(invocationHandler);
        memberValues.put(attributeName, targetValue);
    }
}

