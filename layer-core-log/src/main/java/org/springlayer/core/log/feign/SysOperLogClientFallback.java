package org.springlayer.core.log.feign;

import org.springlayer.core.log.model.LogApi;
import org.springlayer.core.tool.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 日志fallback
 *
 * @author houzhi
 */
@Slf4j
@Component
public class SysOperLogClientFallback implements SysOperLogClient {

    @Override
    public R<Boolean> saveApiLog(LogApi log) {
        return R.fail("api log send fail");
    }
}