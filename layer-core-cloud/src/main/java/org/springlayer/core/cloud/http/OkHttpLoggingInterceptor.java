package org.springlayer.core.cloud.http;

import okhttp3.*;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Hzhi
 * @Date 2022/3/16 10:00
 * @description 基于OkHttp本地调试调用追踪
 */
public final class OkHttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    public interface Logger {
        /**
         * log
         *
         * @param message message
         */
        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
//        Logger DEFAULT = message -> Platform.get().log(INFO, message, null);
    }

//    public OkHttpLoggingInterceptor() {
//        this(Logger.DEFAULT);
//    }

    public OkHttpLoggingInterceptor(Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;

    private volatile Level level = Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     *
     * @param level log Level
     * @return HttpLoggingInterceptor
     */
    public OkHttpLoggingInterceptor setLevel(Level level) {
        Objects.requireNonNull(level, "level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    private String gzip = "gzip";
    private String contentEncoding = "Content-Encoding";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;

        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == Level.BODY;
        boolean logHeaders = logBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        String requestStartMessage = "--> "
                + request.method()
                + ' ' + request.url()
                + (connection != null ? " " + connection.protocol() : "");
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        logger.log(requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                if (requestBody.contentType() != null) {
                    logger.log("Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    logger.log("Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    logger.log(name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                logger.log("--> END " + request.method());
            } else if (bodyHasUnknownEncoding(request.headers())) {
                logger.log("--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                logger.log("");
                if (isPlaintext(buffer)) {
                    logger.log(buffer.readString(charset));
                    logger.log("--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    logger.log("--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logger.log("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        logger.log("<-- "
                + response.code()
                + (response.message().isEmpty() ? "" : ' ' + response.message())
                + ' ' + response.request().url()
                + " (" + tookMs + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                logger.log(headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                logger.log("<-- END HTTP");
            } else if (bodyHasUnknownEncoding(response.headers())) {
                logger.log("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                // Buffer the entire body.
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();

                Long gzippedLength = null;
                if (gzip.equalsIgnoreCase(headers.get(contentEncoding))) {
                    gzippedLength = buffer.size();
                    GzipSource gzippedResponseBody = null;
                    try {
                        gzippedResponseBody = new GzipSource(buffer.clone());
                        buffer = new Buffer();
                        buffer.writeAll(gzippedResponseBody);
                    } finally {
                        if (gzippedResponseBody != null) {
                            gzippedResponseBody.close();
                        }
                    }
                }

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (!isPlaintext(buffer)) {
                    logger.log("");
                    logger.log("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    logger.log("");
                    logger.log(buffer.clone().readString(charset));
                }

                if (gzippedLength != null) {
                    logger.log("<-- END HTTP (" + buffer.size() + "-byte, "
                            + gzippedLength + "-gzipped-byte body)");
                } else {
                    logger.log("<-- END HTTP (" + buffer.size() + "-byte body)");
                }
            }
        }

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static int plainCnt = 16;

    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < plainCnt; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !"identity".equalsIgnoreCase(contentEncoding)
                && !"gzip".equalsIgnoreCase(contentEncoding);
    }
}
