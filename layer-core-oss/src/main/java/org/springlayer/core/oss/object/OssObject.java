package org.springlayer.core.oss.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Hzhi
 * @Date 2022-05-31 10:29
 * @description
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OssObject {

    /**
     * 对象名
     */
    private String objectName;
    /**
     * 原始文件名
     */
    private String originalName;
    /**
     * 桶名
     */
    private String bucketName;
    /**
     * 文件类型
     */
    private String fileType;

}