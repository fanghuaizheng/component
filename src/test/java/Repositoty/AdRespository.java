package Repositoty;

import cn.com.fhz.component.api.BaseApi;
import cn.com.fhz.component.pool.ElasticsearchConnentFactroy;
import entity.AdEntity;

/**
 * Created by woni on 18/3/18.
 */
public class AdRespository extends BaseApi<AdEntity>{

    public AdRespository(){
        super();
    }

    /**
     * 这个方法是为了初始化带配置文件地址
     * @param path
     */
    public AdRespository(String path){
        super(path);
    }

    public void initElasticSearchConfig(String path) {

        //初始化链接配置信息
        ElasticsearchConnentFactroy.initPool();
        //设置索引信息
        setIndexName(path);

    }
}
