package org.springlayer.core.cloud.http;

import lombok.extern.slf4j.Slf4j;

/**
 * OkHttp Slf4j logger
 *
 * @author houzhi
 */
@Slf4j
public class OkHttpSlf4jLogger implements OkHttpLoggingInterceptor.Logger {

	@Override
	public void log(String message) {
		log.info(message);
	}
}
