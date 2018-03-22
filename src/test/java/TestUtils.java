import Repositoty.AdRespository;
import cn.com.fhz.component.entity.PageRequest;
import cn.com.fhz.component.entity.SearchResult;
import cn.com.fhz.component.vo.ElasticSearchResponseVo;
import com.alibaba.fastjson.JSONObject;
import entity.AdEntity;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by woni on 18/3/18.
 */

public class TestUtils extends TestCase{
    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    public void test(){
        System.out.println(12);
    }

    public void testAdSearchById(){
        AdRespository respository = new AdRespository("/Users/woni/Documents/Intellij/elasticsearch/src/main/resources/elasticsearch.properties");

        String id = "91";

        AdEntity entity = respository.findById(id);

        logger.info("通过id获取到的数据{}->"+ JSONObject.toJSONString(entity));

        logger.info("开始进行删除操作");

        SearchResult searchResult = respository.deleteById(id);

        logger.info("删除操作结果{}->"+JSONObject.toJSONString(searchResult));

        //开始增加操作
        entity.setCreatetime(new Date());
        SearchResult result = respository.saveOrUpdate(entity, "getId");

        logger.info("保存操作的结果：\t"+JSONObject.toJSONString(result));

    }

    public void testAdSearch(){
        String field = "keyWords";
        String value = "游戏";
        AdRespository respository = new AdRespository();

//        ElasticSearchResponseVo<AdEntity> byField = respository.findByField(field, value, null);

        ElasticSearchResponseVo<AdEntity> byField = respository.findByField(field, value);

        logger.info("{}->"+ JSONObject.toJSONString(byField));
    }

    public void testPageAdSearch(){
        String field = "keyWords";
        String value = "游戏";
        AdRespository respository = new AdRespository();

//        ElasticSearchResponseVo<AdEntity> byField = respository.findByField(field, value, null);

        PageRequest<AdEntity> pageRequest = new PageRequest<AdEntity>();
        pageRequest.setCurrentPage(0);
        pageRequest.setSize(6);

        ElasticSearchResponseVo<AdEntity> byField = respository.findByField(field, value,pageRequest);

        logger.info("{分页的数据}->"+ JSONObject.toJSONString(byField));
    }

}
