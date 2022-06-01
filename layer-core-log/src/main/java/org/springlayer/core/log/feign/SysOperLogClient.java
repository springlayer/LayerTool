package org.springlayer.core.log.feign;

import org.springlayer.core.cloud.constant.AppConstant;
import org.springlayer.core.log.model.LogApi;
import org.springlayer.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author Hzhi
 * @Date 2022-04-23 15:50
 * @description 系统操作日志
 **/
@FeignClient(value = AppConstant.APPLICATION_SYS_NAME, url = "${service-url." + AppConstant.APPLICATION_SYS_NAME + ":}", fallback = SysOperLogClientFallback.class)
public interface SysOperLogClient {

    String API_PREFIX = "/sys/log";

    /**
     * 保存操作日志
     *
     * @param log 日志实体
     * @return boolean
     */
    @PostMapping(API_PREFIX + "/saveApiLog")
    R<Boolean> saveApiLog(@RequestBody LogApi log);
}