package cn.com.fhz.component.api;

import cn.com.fhz.component.entity.PageRequest;
import cn.com.fhz.component.entity.SearchResult;
import cn.com.fhz.component.pool.ElasticsearchConnentFactroy;
import cn.com.fhz.component.utils.CommonUtils;
import cn.com.fhz.component.utils.ConfigReadUtils;
import cn.com.fhz.component.vo.ElasticSearchResponseVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by woni on 18/2/28.
 *
 */
public abstract class BaseApi<T> {



    private static Logger logger = LoggerFactory.getLogger(BaseApi.class);

    private Class<T> entityClass;


    public BaseApi() {
        setEntityClass();
        initElasticSearchConfig(null);
    }

    public BaseApi(String path){
        setEntityClass();
        initElasticSearchConfig(path);
    }

    //索引名称,写成静态的，可能后期不好处理多数据库配置
    private static String indexName = null;


    /**
     * 设置索引的信息
     * @param path 索引配置文件地址
     */
    protected void setIndexName(String path){

        Object index =  ConfigReadUtils.getValue4Properties(path,"elasticsearch.index");

        if (index!=null){
            indexName = index.toString();
        }

    }

    /**
     * 初始化ELasticSearch的配置信息
     */
    public abstract void initElasticSearchConfig(String path);


    private RestHighLevelClient getClient(){

        //获取默认的配置
        return ElasticsearchConnentFactroy.getClient();

    }

    private void setEntityClass(){
        Type genericSuperclass = getClass().getGenericSuperclass();

        Type[] types = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        entityClass = (Class<T>) types[0];
    }


    /**
     * 保存操作
     * @param t
     * @param idName 获取主键的方法名称
     * @return
     */
    public SearchResult saveOrUpdate(T t,String idName){


        RestHighLevelClient client = getClient();

        IndexRequest indexRequest = null;

        SearchResult  searchResult = null;

        try {
            Method method = entityClass.getMethod(idName);

            Object idValue =  method.invoke(t);

            String type = entityClass.getSimpleName();
            String data = JSONObject.toJSONString(t);


            if (idValue!=null){


                indexRequest = new IndexRequest(indexName,type,idValue.toString()).source(data, XContentType.JSON);
            }else {
                indexRequest = new IndexRequest(indexName,type).source(data,XContentType.JSON);
            }


            IndexResponse indexResponse = client.index(indexRequest);

            if (indexResponse.getResult()== DocWriteResponse.Result.CREATED){
                searchResult = SearchResult.CREATE;
            }else if (indexResponse.getResult()==DocWriteResponse.Result.UPDATED){
                searchResult = SearchResult.UPDATE;
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //说明发生啦错误
            if (searchResult==null){
                searchResult = SearchResult.ERROR;
            }
            ElasticsearchConnentFactroy.closeClient(client);
            return searchResult;
        }


    }

    /**
     * 批操作
     * @param dataList
     * @param idName
     * @return
     */
    public SearchResult batchSaveOrUpdate(List<T> dataList,String idName){

        SearchResult result = null;
        RestHighLevelClient client = null;

        try {

            client = getClient();
            BulkRequest bulkRequest = new BulkRequest();

            Method method = entityClass.getMethod(idName);

            String type =entityClass.getSimpleName();

            for (T t:dataList
                 ) {

                Object invoke = method.invoke(t);

                String data = JSONObject.toJSONString(t);

                //说明是增加操作
                if (invoke==null){
                    bulkRequest.add(new IndexRequest(indexName,type).source(data,XContentType.JSON));
                }else {
                    bulkRequest.add(new IndexRequest(indexName,type,invoke.toString()).source(data,XContentType.JSON));
                }
            }
            //开启执行操作

            BulkResponse bulkResponse = client.bulk(bulkRequest);

            int index = 0;
            int update = 0;

            for (BulkItemResponse itemResponse:bulkResponse
                    ) {
                if (itemResponse.getOpType()== DocWriteRequest.OpType.INDEX||
                        itemResponse.getOpType()==DocWriteRequest.OpType.CREATE){
                    index++;
                }else if (itemResponse.getOpType()==DocWriteRequest.OpType.UPDATE){
                    update++;
                }

            }

            logger.info("此次操作数据条数:\t"+dataList+"\t其中"+index+"插入操作\t"+update+"更新");



        }catch (Exception e){
            logger.error("发生啦错误");
            logger.error(e.getMessage());
            e.printStackTrace();
        }finally {
            if (result==null){
                result = SearchResult.ERROR;
            }
            ElasticsearchConnentFactroy.closeClient(client);

            return result;
        }


    }

    public SearchResult deleteById(String id){

        String type = entityClass.getSimpleName();

        RestHighLevelClient client = null;

        SearchResult searchResult = null;


        try {

            client = getClient();

            DeleteRequest deleteRequest = new DeleteRequest(indexName, type, id);

            DeleteResponse deleteResponse = client.delete(deleteRequest);

            ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();

            if (shardInfo.getTotal() == shardInfo.getSuccessful()) {

                searchResult = SearchResult.SUCCESS;

            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    searchResult = SearchResult.ERROR;
                    String reason = failure.reason();
                    logger.info("失败的原因:\t"+reason);
                }
            }


        }catch (Exception e){

        }finally {
            if (searchResult==null){
                searchResult = SearchResult.ERROR;
            }
            ElasticsearchConnentFactroy.closeClient(client);

            return searchResult;
        }


    }


    //通过主键查询，不带高亮的返回
    /**
     * @param id 主键值
     * @return
     */
    public T findById(String id){
        RestHighLevelClient client = null;
        T t = null;
        try {

            client = getClient();

            String type = entityClass.getSimpleName();

            GetRequest request = new GetRequest(indexName, type, id);

            GetResponse response = client.get(request);
            String idMethodName = null;
            Field[] fields = entityClass.getDeclaredFields();
            Field[] superFields = entityClass.getSuperclass().getDeclaredFields();
            for (Field itemField: superFields
                 ) {
                itemField.setAccessible(true);

                Annotation[] annotations = itemField.getAnnotations();
                for (Annotation itemAnnon: annotations
                     ) {
                    //如果是主键
                    if (itemAnnon.annotationType().getSimpleName().equals("id")){

                    }
                }

                itemField.setAccessible(false);
            }
            for (Field itemField: fields
                 ) {
                itemField.setAccessible(true);

                Annotation[] annotations = itemField.getAnnotations();

                for (Annotation itemAnnon: annotations
                     ) {
                    if (itemAnnon.annotationType().getSimpleName().equals("id")){

                    }
                }

                itemField.setAccessible(false);
            }
            if (response.isExists()){
                String string = response.getSourceAsString();
                String idValue = response.getId();
                Map<String, Object> sourceAsMap = response.getSourceAsMap();
//                sourceAsMap.put("");

                t = (T) JSONObject.parseObject(string,entityClass);

            }

        }catch (ElasticsearchException elasticsearchException){

            //没找到
            if (elasticsearchException.status()== RestStatus.NOT_FOUND){


            }

        }catch (Exception e){
            logger.error("查询发生错误");
            e.printStackTrace();
        }finally {
            return  t;
        }
    }

    /**
     * 高亮显示查询的字段
     * @param field 字段名称
     * @param value 字段的值
     * @param page 分页的数据，注：此处的分页，是在查询结果的基础上的分页，不是基于整个库的数据，可以为空
     * @return
     */
    public ElasticSearchResponseVo<T> findByField(String field, String value, PageRequest<T> page){

        RestHighLevelClient client = null;

        ElasticSearchResponseVo<T> vo = new ElasticSearchResponseVo<T>();

        try {

            //如果传入的参数有空的，那么不查询
            if (StringUtils.isBlank(field)||StringUtils.isBlank(value)){
                SearchResult isNull = SearchResult.PARAM_IS_NULL;

                CommonUtils.putValue2Result(vo,isNull,null);
                return vo;

            }

            client = getClient();

            SearchRequest searchRequest = getSearchRequest(field,value,page);

            //开始发送请求
            SearchResponse searchResponse = client.search(searchRequest);

            SearchHits hits = searchResponse.getHits();

            SearchResult success = SearchResult.SUCCESS;

            vo.setCode(success.getCode());
            vo.setMsg(success.getMsg());

            List<T> data = getDataFromHits(hits,field);

            if (page!=null){
                page.setTotal(hits.totalHits);

                page.setRows(data);
                vo.setPage(page);
            }else vo.setData(data);



        }catch (Exception e){
            logger.error("发生错误啦");
            logger.error(e.getMessage());
            e.printStackTrace();
        }finally {

            ElasticsearchConnentFactroy.closeClient(client);

            return vo;
        }


    }



    /**
     * 从请求的参数里面组装Elasticsearch请求原
     * @param field 请求的域
     * @param value 请求的值
     * @param page 分页
     * @return
     */
    private SearchSourceBuilder getSearchSourceBuilderFromParams(String field,String value,PageRequest<T> page){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (page!=null){
            //开始分页
            if (page.getCurrentPage().intValue()>=0){
                searchSourceBuilder.from(page.getCurrentPage());
                searchSourceBuilder.size(page.getCurrentPage()*page.getSize());
            }
        }

        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery(field, value);
        matchQuery.fuzziness(Fuzziness.AUTO);

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.must(matchQuery);

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highField = new HighlightBuilder.Field(field);
        highlightBuilder.field(highField);

        //将查询与高亮加入到searchSourceBuild里面
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.highlighter(highlightBuilder);

        return searchSourceBuilder;

    }

    /**
     * 组装请求
     * @param field 查询的域
     * @param value 查询域的值
     * @param page 分页数据
     * @return
     */
    private SearchRequest getSearchRequest(String field,String value,PageRequest<T> page){

        SearchSourceBuilder searchSourceBuilder = getSearchSourceBuilderFromParams(field,value,page);

       return getSearchRequest(searchSourceBuilder);
    }


    /**
     * 从请求原中组装请求
     * @param searchSourceBuilder
     * @return
     */
    private SearchRequest getSearchRequest(SearchSourceBuilder searchSourceBuilder){
        //没有加入排序
        SearchRequest searchRequest = new SearchRequest(indexName);

        searchRequest.source(searchSourceBuilder);
        searchRequest.types(entityClass.getSimpleName());

        return searchRequest;
    }


    /**
     *从查询的结果中组装数据
     * @param hits 返回的结果
     * @param field 高亮的域
     * @return
     */
    private List<T> getDataFromHits(SearchHits hits,String field){
        List<T> data = new ArrayList<T>();

        for (SearchHit hit:hits
             ) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //获取高亮的属性
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get(field);
            String fieldValue = highlightField.fragments()[0].toString();
            sourceAsMap.put("highLightValue",fieldValue);
            T itemData = (T)JSONObject.parseObject(JSONObject.toJSONString(sourceAsMap),entityClass);
            data.add(itemData);

        }
        return data;


    }




}
