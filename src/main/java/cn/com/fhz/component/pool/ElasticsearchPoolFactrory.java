package cn.com.fhz.component.pool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

/**
 * Created by hzfang on 2018/1/26.
 * 后期准备做连接池设置
 */
public class ElasticsearchPoolFactrory implements PooledObjectFactory<RestHighLevelClient> {

    private ElasticsearchPoolConfig elasticsearchPoolConfig = new ElasticsearchPoolConfig();

    public ElasticsearchPoolFactrory(){}

    public ElasticsearchPoolFactrory(ElasticsearchPoolConfig config){
        this.elasticsearchPoolConfig = config;
    }

    /**
     * 生产一个连接对象
     * @return
     * @throws Exception
     */
    public PooledObject<RestHighLevelClient> makeObject() throws Exception {

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(elasticsearchPoolConfig.getUrl(), elasticsearchPoolConfig.getPort(),
                        elasticsearchPoolConfig.getSchema())));

         return new DefaultPooledObject<RestHighLevelClient>(client);

    }

    public void destroyObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {

        closeResource(pooledObject.getObject());



    }

    private void closeResource(RestHighLevelClient client) throws IOException {
        client.close();
    }

    /**
     * 功能描述：判断资源对象是否有效，有效返回 true，无效返回 false
     *
     * 什么时候会调用此方法
     * 1：从资源池中获取资源的时候，参数 testOnBorrow 或者 testOnCreate 中有一个 配置 为 true 时，则调用  factory.validateObject() 方法
     * 2：将资源返还给资源池的时候，参数 testOnReturn，配置为 true 时，调用此方法
     * 3：资源回收线程，回收资源的时候，参数 testWhileIdle，配置为 true 时，调用此方法
     */
    public boolean validateObject(PooledObject<RestHighLevelClient> pooledObject) {
        return true;
    }

    /**
     * 功能描述：激活资源对象
     *
     * 什么时候会调用此方法
     * 1：从资源池中获取资源的时候
     * 2：资源回收线程，回收资源的时候，根据配置的 testWhileIdle 参数，判断 是否执行 factory.activateObject()方法，true 执行，false 不执行
     */
    public void activateObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {

    }

    /**
     * 功能描述：钝化资源对象
     *
     * 什么时候会调用此方法
     * 1：将资源返还给资源池时，调用此方法。
     */
    public void passivateObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {


    }



}
