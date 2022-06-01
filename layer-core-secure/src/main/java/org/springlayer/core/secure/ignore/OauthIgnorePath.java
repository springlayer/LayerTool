package org.springlayer.core.secure.ignore;

/**
 * @author zhihou
 * @date 2020/12/19 10:49
 * @description
 */
public class OauthIgnorePath {
    /**
     * 默认忽略
     */
    public static final String[] DEFAULT_IGNORE_URLS = new String[]{
            "/",
            "/env/getValue/**",
            "/favicon.ico"
    };
}
