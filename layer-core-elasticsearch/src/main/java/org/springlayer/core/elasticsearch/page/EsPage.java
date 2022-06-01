package org.springlayer.core.elasticsearch.page;

import lombok.Data;

import java.util.List;

/**
 * @Author Hzhi
 * @Date 2022-05-12 15:44
 * @description
 **/
@Data
public class EsPage<T> {
    private Integer current;
    private Integer size;
    private Long total;
    private Integer pages;
    private List<T> records;
}