package cn.com.fhz.component.pool;

import cn.com.fhz.component.utils.ConfigReadUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by hzfang on 2018/2/9.
 * 连接池的配置信息
 */
public class ElasticsearchPoolConfig{

    private String url="127.0.0.1";//请求客户端的ip

    private Integer port=9200;//端口

    private String schema="http";//elasticsearch 传输协议

    //链接池中最大连接数，默认值20
    private Integer maxTotal =8;

    //连接池中最大空闲的连接数,默认为20
    private Integer maxIdle =8;

    //连接池中最少空闲的连接数,默认为0
    private Integer minIdle =0;

    //当连接池资源耗尽时，调用者最大阻塞的时间，超时将跑出异常。
    // 单位，毫秒数;默认为-1.表示永不超时. 默认值 -1
    private Long maxWaitMillis = -1L;


    public ElasticsearchPoolConfig(){}

    /**
     * 带配置地址的初始化配置文件
     * @param path
     */
    public ElasticsearchPoolConfig(String path){
        getConfig4Properties(path);
    }

    /**
     * 从配置文件中
     * @param path
     */
    private void getConfig4Properties(String path){
        Configuration properties = ConfigReadUtils.getProperties(path);

        if (StringUtils.isNotBlank(properties.getString("elasticsearch.url"))){
            url = properties.getString("elasticsearch.url");
        }

        if (properties.getInteger("elasticsearch.port", 9200)!=null){
            port = properties.getInteger("elasticsearch.port", 9200);
        }

        if (StringUtils.isNotBlank(properties.getString("elasticsearch.schme"))){
            schema = properties.getString("elasticsearch.schme");
        }

        if (properties.getInteger("elasticsearch.maxTotal",20)!=null){
            maxTotal = properties.getInteger("elasticsearch.maxTotal",20);
        }

        if (properties.getInteger("elasticsearch.maxIdle",20)!=null){
            maxIdle = properties.getInteger("elasticsearch.maxIdle",20);
        }

        if (properties.getInteger("elasticsearch.minIdle",0)!=null){
            minIdle = properties.getInteger("elasticsearch.minIdle",0);
        }

        Long aLong = properties.getLong("elasticsearch.maxWaitMillis", -1L);

        if (aLong!=null){

            maxWaitMillis = aLong;
        }



    }


    public GenericObjectPoolConfig getConfig(){

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

        poolConfig.setMaxTotal(maxTotal);

        poolConfig.setMaxIdle(maxIdle);

        poolConfig.setMinIdle(minIdle);

        poolConfig.setMaxWaitMillis(maxWaitMillis);
        return poolConfig;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

}
