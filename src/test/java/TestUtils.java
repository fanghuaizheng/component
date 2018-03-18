import Repositoty.AdRespository;
import cn.com.fhz.component.entity.SearchResult;
import cn.com.fhz.component.vo.ElasticSearchResponseVo;
import com.alibaba.fastjson.JSONObject;
import entity.AdEntity;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by woni on 18/3/18.
 */

public class TestUtils extends TestCase{

    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    public void test(){
        System.out.println(12);
    }

    public void testAdSearchById(){
        AdRespository respository = new AdRespository();

        String id = "92";

        AdEntity entity = respository.findById(id);

        logger.info("通过id获取到的数据{}->"+ JSONObject.toJSONString(entity));

        logger.info("开始进行删除操作");

        SearchResult searchResult = respository.deleteById(id);

        logger.info("删除操作结果{}->"+JSONObject.toJSONString(searchResult));
    }

    public void testAdSearch(){
        String field = "keyWords";
        String value = "游戏";
        AdRespository respository = new AdRespository();

//        ElasticSearchResponseVo<AdEntity> byField = respository.findByField(field, value, null);

        ElasticSearchResponseVo<AdEntity> byField = respository.findByField(field, value, null);

        logger.info("{}->"+ JSONObject.toJSONString(byField));
    }


}
