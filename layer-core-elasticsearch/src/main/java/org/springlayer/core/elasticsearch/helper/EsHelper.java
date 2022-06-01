package org.springlayer.core.elasticsearch.helper;

import com.alibaba.fastjson.JSON;
import org.springlayer.core.elasticsearch.config.ElasticSearchConfig;
import org.springlayer.core.elasticsearch.page.EsPage;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Hzhi
 * @Date 2022-05-10 11:30
 * @description
 **/
@Component
@Slf4j
public class EsHelper {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * @param index    索引
     * @param bachList 保存的数据
     * @param <T>
     * @return
     */
    public <T extends Object> boolean saveBatch(String index, List<T> bachList) {
        BulkRequest bulkRequest = new BulkRequest();
        //封装保存的数据
        for (int i = 0; i < bachList.size(); i++) {
            T t = bachList.get(i);
            String jsonString = JSON.toJSONString(t);
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index(index);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        try {
            restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ES保存数据
     *
     * @param index      索引
     * @param id         主键
     * @param jsonString 新增的数据
     * @return
     */
    public boolean saveOne(String index, String id, String jsonString) {
        //封装保存的数据
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index(index);
        indexRequest.id(id);
        indexRequest.source(jsonString, XContentType.JSON);
        try {
            //执行新增操作
            IndexResponse indexResponse =
                    restHighLevelClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
            log.info("新增的结果:" + indexResponse);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID删除单条记录
     *
     * @param index
     * @param id
     * @return
     */
    public boolean deleteById(String index, String id) {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index(index);
        deleteRequest.id(id);
        try {
            DeleteResponse deleteResponse =
                    restHighLevelClient.delete(deleteRequest,
                            ElasticSearchConfig.COMMON_OPTIONS);
            log.info("删除的结果:" + deleteResponse);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新数据
     *
     * @param index
     * @param id
     * @param jsonString
     * @return
     */
    public boolean updateById(String index, String id, String jsonString) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index);
        updateRequest.id(id);
        updateRequest.doc(jsonString, XContentType.JSON);
        try {
            UpdateResponse response =
                    restHighLevelClient.update(updateRequest, ElasticSearchConfig.COMMON_OPTIONS);
            log.info("更新的结果:" + response);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID从ES中查询数据
     *
     * @param index
     * @param id
     * @param targetClass
     * @return
     */
    public <T extends Object> T selectById(String index, String id, Class<T> targetClass) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        //构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("_id", id));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();  //查询结果
            if (searchHits == null || searchHits.length == 0) {  //如果数组为空,表示没有查询结果
                return null;
            }
            String jsonString = searchHits[0].getSourceAsString();
            T t = JSON.parseObject(jsonString, targetClass);
            return t;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param index
     * @param targetClass
     * @param searchSourceBuilder 查选条件的构建器
     * @param <T>
     * @return
     */
    public <T extends Object> List<T> select(String index, Class<T> targetClass,
                                             SearchSourceBuilder searchSourceBuilder) {
        SearchRequest searchRequest = new SearchRequest(); //查询请求
        searchRequest.indices(index);
        searchRequest.source(searchSourceBuilder); //请求中添加中查询条件
        List<T> list = new ArrayList<>();
        try {
            //执行查询,获得查询结果
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            SearchHits hits = searchResponse.getHits();  //封装结果
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit searchHit : searchHits) {
                String jsonString = searchHit.getSourceAsString();
                T t = JSON.parseObject(jsonString, targetClass);
                list.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 使用from+size的方式实现 ES的分页查询
     *
     * @param index               索引
     * @param searchSourceBuilder 查询条件
     * @param targetClass         目标类
     * @param from                从第几条开始
     * @param size                显示多少条记录
     * @param <T>
     * @return Map: 当前页的数据   总页数
     */
    public <T extends Object> EsPage<T> page(String index,
                                             SearchSourceBuilder searchSourceBuilder,
                                             Class<T> targetClass, int from, int size) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        searchRequest.source(searchSourceBuilder);
        EsPage<T> tEsPage = new EsPage<>();
        List<T> resultList = new ArrayList<>();
        int pages = 0;
        long totalValue = 0;
        try {
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            SearchHits hits = searchResponse.getHits();
            totalValue = hits.getTotalHits().value; //获得总记录数    3.0/2=1.5    Math.ceil(1.5)   2.0;
            pages = (int) Math.ceil((double) totalValue / size); //总页数
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit searchHit : searchHits) {
                String jsonString = searchHit.getSourceAsString();
                T t = JSON.parseObject(jsonString, targetClass);
                resultList.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tEsPage.setCurrent(from);
        tEsPage.setSize(size);
        tEsPage.setTotal(totalValue);
        tEsPage.setPages(pages);
        tEsPage.setRecords(resultList);
        return tEsPage;
    }

    /**
     * 使用scroll分页
     *
     * @param index
     * @param searchSourceBuilder
     * @param targetClass
     * @param size
     * @param scrollId
     * @param <T>
     * @return Map 当前页的数据   scrollId   page
     */
    public <T extends Object> Map<String, Object> page(String index,
                                                       SearchSourceBuilder searchSourceBuilder,
                                                       Class<T> targetClass, int size, String scrollId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1)); //指定scroll镜像的时间为1分钟
        searchSourceBuilder.size(size); //每页显示多少条记录
        Map<String, Object> map = new HashMap<>();
        SearchResponse searchResponse = null;
        try {
            if (StringUtils.isBlank(scrollId)) { //scroll方式的第一次查询
                searchRequest.scroll(scroll); //查询是scroll查询 镜像的时间为1分钟
                searchRequest.source(searchSourceBuilder);  //查询请求中添加查询条件
                searchResponse = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            } else {   //scroll方式的后面查询   请求:GET /_search/scroll
                SearchScrollRequest searchScrollRequest = new SearchScrollRequest();
                searchScrollRequest.scroll(scroll);
                searchScrollRequest.scrollId(scrollId);
                searchResponse = restHighLevelClient.scroll(searchScrollRequest, ElasticSearchConfig.COMMON_OPTIONS);
            }
            //封装查询结果
            map = searchResponseToMap(searchResponse, size, targetClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    //当前页的数据    scrollId    总页数
    private <T extends Object> Map<String, Object>
    searchResponseToMap(SearchResponse searchResponse, int size, Class<T> targetClass) {
        SearchHits hits = searchResponse.getHits(); //查询的结果
        double count = hits.getTotalHits().value; //获得总记录数
        int page = (int) Math.ceil(count / size);  //算出总页数
        Map<String, Object> map = new HashMap<>();  //返回的结果
        List<T> list = new ArrayList<>();   //当前页的数据
        SearchHit[] searchHits = hits.getHits(); //获得hits中的数据
        for (SearchHit temp : searchHits) {
            String jsonString = temp.getSourceAsString();
            T t = JSON.parseObject(jsonString, targetClass);
            list.add(t);
        }
        map.put("page", page);
        map.put("scrollId", searchResponse.getScrollId());
        map.put("list", list);
        return map;
    }
}