package org.springlayer.core.log.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 实体类
 *
 * @author houzhi
 */
@Data
@TableName("sys_oper_log")
public class LogApi extends LogAbstract implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    private Integer businessType;

    /**
     * 业务类型数组
     */
    private Integer[] businessTypes;
}