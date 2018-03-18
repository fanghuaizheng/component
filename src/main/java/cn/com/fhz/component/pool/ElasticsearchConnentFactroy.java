package cn.com.fhz.component.pool;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by hzfang on 2018/2/9.
 * 连接工厂
 * 提供两种获取资源池里面链接
 * 1、先调用initPool方法，重载啦，里面可以指定配置文件地址，然后直接调用getClient方法
 *2、调用重载的方法getClient方法，方法的参数传指定配置文件地址
 */
public class ElasticsearchConnentFactroy {

    private static Logger logger = LoggerFactory.getLogger(ElasticsearchConnentFactroy.class);


   private static ElasticsearchPoolFactrory elasticsearchPoolFactrory = new ElasticsearchPoolFactrory();

   private static ElasticsearchPoolConfig elasticsearchPoolConfig = new ElasticsearchPoolConfig();

   private static GenericObjectPool<RestHighLevelClient> pool = null;


    /**
     * 指定默认文件的客户端
     * @return
     */
    public static RestHighLevelClient getClient(){


        RestHighLevelClient client = null;

        if (pool==null){
            initPool();
        }
        try {
            client =  pool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return client;
        }

    }

    /**
     * 指定配置文件的客户端
     * @param path path可以传null,那样会从默认地址获取配置信息
     * @return
     */
    public static RestHighLevelClient getClient(String path){


        RestHighLevelClient client = null;

        if (pool==null){
            initPool(path);
        }
        try {
            client =  pool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return client;
        }

    }

    /**
     * 初始化资源池，配置信息从指定路径获取
     * @param path
     */
    public static void initPool(String path){
        if (pool==null){
            logger.info("开始初始化资源池，配置信息获取地址是:\t"+path);
            elasticsearchPoolConfig = new ElasticsearchPoolConfig(path);

            elasticsearchPoolFactrory = new ElasticsearchPoolFactrory(elasticsearchPoolConfig);
            pool  = new GenericObjectPool<RestHighLevelClient>(elasticsearchPoolFactrory, elasticsearchPoolConfig.getConfig());
        }

    }

    /**
     * 初始化资源池信息，配置默认地址信息
     */
    public static void initPool(){
        if (pool==null){
            logger.info("开始初始化资源池，配置信息是默认地址配置");
            pool = new GenericObjectPool<RestHighLevelClient>(elasticsearchPoolFactrory, elasticsearchPoolConfig.getConfig());

        }
    }

    public static void closeClient(RestHighLevelClient client){

        if (client!=null){
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    public static void main(String[] args) {



        RestHighLevelClient client = getClient(null);

        Integer maxTotal = elasticsearchPoolConfig.getMaxTotal();

        System.out.println("maxTotal\t"+maxTotal);

        closeClient(client);
        logger.info("开始获取指定配置文件的客户端连接");

        String path = "elasticsearch.properties";

        RestHighLevelClient levelClient = getClient(path);

        Integer total = elasticsearchPoolConfig.getMaxTotal();

        logger.info("获取配置文件中maxTotal:\t"+total);

    }


}
