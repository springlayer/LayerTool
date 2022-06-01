package org.springlayer.core.log.event;

import org.springlayer.core.log.constant.EventConstant;
import org.springlayer.core.log.feign.SysOperLogClient;
import org.springlayer.core.log.model.LogApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * 异步监听日志事件
 *
 * @author Chill
 */
@Slf4j
@AllArgsConstructor
public class ApiLogListener {

    private final SysOperLogClient sysOperLogClient;

    @Async
    @Order
    @EventListener(ApiLogEvent.class)
    public void saveApiLog(ApiLogEvent event) {
        Map<String, Object> source = (Map<String, Object>) event.getSource();
        LogApi logApi = (LogApi) source.get(EventConstant.EVENT_LOG);
        logApi.setOperTime(LocalDateTime.now());
        sysOperLogClient.saveApiLog(logApi);
    }
}