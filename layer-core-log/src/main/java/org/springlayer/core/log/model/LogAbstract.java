package org.springlayer.core.log.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * logApi 的父类，拥有相同的属性值,方便以后扩展
 *
 * @author houzhi
 */
@Data
public class LogAbstract implements Serializable {

    protected static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long operId;

    /**
     * 操作模块
     */
    private String title;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    private Integer operatorType;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 请求url
     */
    private String operUrl;

    /**
     * 操作地址
     */
    private String operIp;

    /**
     * 请求参数
     */
    private String operParam;

    /**
     * 操作时间
     */
    private LocalDateTime operTime;
}