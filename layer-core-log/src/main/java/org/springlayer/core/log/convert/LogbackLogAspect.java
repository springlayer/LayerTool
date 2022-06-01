package org.springlayer.core.log.convert;

import com.alibaba.fastjson.JSON;
import org.springlayer.core.cloud.constant.AppConstant;
import org.springlayer.core.launch.utils.WebUtil;
import org.springlayer.core.tool.api.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Spring boot 控制器 请求日志
 *
 * @author houzhi
 */
@Slf4j
@Aspect
@Configuration
@Profile(AppConstant.ENV_PROD)
public class LogbackLogAspect {

    private final static String REQUEST_ID = "request_id";

    /**
     * AOP 环切 控制器 R 返回值
     *
     * @param point JoinPoint
     * @return Object
     * @throws Throwable 异常
     */
    @Around(
            "execution(!static org.springlayer.core.tool.api.R *(..)) || " + "execution(* com.example.*..*Controller.*(..)) &&" +
                    "(@within(org.springframework.stereotype.Controller) || " +
                    "@within(org.springframework.web.bind.annotation.RestController))"
    )
    public Object aroundApi(ProceedingJoinPoint point) throws Throwable {
        UUID uuid = UUID.randomUUID();
        HttpServletRequest request = WebUtil.getRequest();
        if (null == request) {
            return point.proceed();
        }
        String requestURI = Objects.requireNonNull(request).getRequestURI();
        String requestMethod = request.getMethod();
        long startNs = System.nanoTime();
        try {
            Object result = point.proceed();
            try {
                R r = (R) result;
                mdc(uuid, requestURI, requestMethod, request, startNs, r.getCode() + "");
                if (r.getCode() == HttpServletResponse.SC_OK) {
                    log.info(JSON.toJSONString(result));

                } else if (r.getCode() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                    log.error(JSON.toJSONString(result));

                } else if (r.getCode() == HttpServletResponse.SC_BAD_REQUEST) {
                    log.error(JSON.toJSONString(result));
                }
                return result;
            } catch (Exception e) {
                mdc(uuid, requestURI, requestMethod, request, startNs, HttpServletResponse.SC_OK + "");
            }
            log.info(JSON.toJSONString(result));
            return result;
        } finally {
            MDC.remove(REQUEST_ID);
        }
    }

    private void mdc(UUID uuid, String requestURI, String requestMethod, HttpServletRequest request, long startNs, String status) {
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        MDC.put("log_id", uuid.toString().replace("-", ""));
        MDC.put("cost", tookMs + "ms");
        MDC.put(REQUEST_ID, uuid.toString().replace("-", ""));
        MDC.put("method", requestMethod.toUpperCase());
        MDC.put("pathname", requestURI);
        MDC.put("remote_ip", request.getServerName());
        MDC.put("service", request.getServletPath());
        MDC.put("tag", request.getAuthType());
        MDC.put("status", status);
    }
}