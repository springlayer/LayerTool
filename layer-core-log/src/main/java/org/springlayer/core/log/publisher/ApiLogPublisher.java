package org.springlayer.core.log.publisher;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import org.springlayer.core.log.annotation.ApiLog;
import org.springlayer.core.log.constant.EventConstant;
import org.springlayer.core.log.event.ApiLogEvent;
import org.springlayer.core.log.model.LogAbstract;
import org.springlayer.core.log.model.LogApi;
import org.springlayer.core.log.util.IpUtils;
import org.springlayer.core.log.util.SpringUtil;
import org.springlayer.core.tool.utils.ObjectUtil;
import org.springlayer.core.tool.utils.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * API日志信息事件发送
 *
 * @author houzhi
 */
@Slf4j
public class ApiLogPublisher {

    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};

    @SneakyThrows
    public static void publishEvent(ApiLog apiLog, String methodName) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        LogApi logApi = new LogApi();
        logApi.setTitle(apiLog.title());
        logApi.setBusinessType(apiLog.businessType().ordinal());
        logApi.setMethod(methodName);
        logApi.setOperatorType(apiLog.operatorType().ordinal());

        String userStr = request.getHeader("user");
        log.info("userStr is :" + userStr);
        if (null != userStr) {
            JSONObject userJsonObject = new JSONObject(userStr);
            log.info("Token 内容信息：" + JSON.toJSONString(userJsonObject));
            Object username = userJsonObject.get("username");
            if (null != username) {
                String decodeUsername = URLDecoder.decode(username.toString(), "UTF-8");
                logApi.setOperName(decodeUsername);
            }
        }
        addRequestInfoToLog(request, logApi);
        Map<String, Object> event = new HashMap<>(16);
        event.put(EventConstant.EVENT_LOG, logApi);
        SpringUtil.publishEvent(new ApiLogEvent(event));
    }

    /**
     * 向log中添加补齐request的信息
     *
     * @param request     请求
     * @param logAbstract 日志基础类
     */
    public static void addRequestInfoToLog(HttpServletRequest request, LogAbstract logAbstract) throws Exception {
        if (ObjectUtil.isNotEmpty(request)) {
            logAbstract.setOperIp(IpUtils.getIpAddr(request));
            logAbstract.setOperUrl(getPath(request.getRequestURI()));
            logAbstract.setRequestMethod(request.getMethod());
            Map<String, String[]> map = request.getParameterMap();
            if (StringUtil.isNotEmpty(map)) {
                String params = JSON.toJSONString(map, excludePropertyPreFilter());
                logAbstract.setOperParam(StringUtils.substring(params, 0, 2000));
            }
        }
    }

    /**
     * 忽略敏感属性
     */
    public static PropertyPreFilters.MySimplePropertyPreFilter excludePropertyPreFilter() {
        return new PropertyPreFilters().addFilter().addExcludes(EXCLUDE_PROPERTIES);
    }

    /**
     * 获取url路径
     *
     * @param uriStr 路径
     * @return url路径
     */
    public static String getPath(String uriStr) {
        URI uri;

        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException var3) {
            throw new RuntimeException(var3);
        }

        return uri.getPath();
    }
}