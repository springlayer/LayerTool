package org.springlayer.core.tool.api;

import org.springlayer.core.tool.constant.ApiConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务代码枚举
 *
 * @Author Hzhi
 * @Date 2022/3/14 15:08
 * @description 自定义业务代码枚举
 **/
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {

    /**
     * 操作成功
     */
    SUCCESS(ApiConstant.SUCCESS, "操作成功"),

    /**
     * 业务异常
     */
    FAILURE(ApiConstant.FAILURE, "业务异常"),

    /**
     * 请求未授权
     */
    UN_AUTHORIZED(ApiConstant.UN_AUTHORIZED, "请求未授权"),

    /**
     * 404 没找到请求
     */
    NOT_FOUND(ApiConstant.NOT_FOUND, "404 没找到请求"),

    /**
     * 不支持当前请求方法
     */
    METHOD_NOT_SUPPORTED(ApiConstant.NOT_FOUND, "不支持当前请求方法"),

    /**
     * 不支持当前媒体类型
     */
    MEDIA_TYPE_NOT_SUPPORTED(ApiConstant.NOT_FOUND, "不支持当前媒体类型"),

    /**
     * 请求被拒绝
     */
    REQ_REJECT(ApiConstant.UN_AUTHORIZED, "请求被拒绝"),

    /**
     * 服务器异常
     */
    INTERNAL_SERVER_ERROR(ApiConstant.FAILURE, "服务器异常"),

    /**
     * 缺少必要的请求参数
     */
    PARAM_MISS(ApiConstant.PARAM_BIND_ERROR, "缺少必要的请求参数"),

    /**
     * 请求参数类型错误
     */
    PARAM_TYPE_ERROR(ApiConstant.PARAM_BIND_ERROR, "请求参数类型错误"),

    /**
     * 请求参数绑定错误
     */
    PARAM_BIND_ERROR(ApiConstant.PARAM_BIND_ERROR, "请求参数绑定错误"),

    /**
     * 参数校验失败
     */
    PARAM_VALID_ERROR(ApiConstant.PARAM_BIND_ERROR, "参数校验失败"),

    /**
     * 无权限访问
     */
    PARAM_UNAUTHORIZED_ERROR(ApiConstant.UN_AUTHORIZED, "无权限访问"),
    ;

    /**
     * code编码
     */
    final int code;
    /**
     * 中文信息描述
     */
    final String message;

}
