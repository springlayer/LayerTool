package org.springlayer.core.tool.exception;

import org.springlayer.core.tool.api.IResultCode;
import org.springlayer.core.tool.api.ResultCode;
import lombok.Getter;

/**
 * @Author Hzhi
 * @Date 2022/3/14 16:08
 * @description 业务异常
 **/
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 2359767895161832954L;

    @Getter
    private final IResultCode resultCode;


    public BusinessException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILURE;
    }

    public BusinessException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(IResultCode resultCode, Throwable cause) {
        super(cause);
        this.resultCode = resultCode;
    }

    /**
     * 提高性能
     *
     * @return Throwable
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public Throwable doFillInStackTrace() {
        return super.fillInStackTrace();
    }

}
