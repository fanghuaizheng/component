package cn.com.fhz.component.utils;




import cn.com.fhz.component.entity.SearchResult;
import cn.com.fhz.component.vo.ElasticSearchResponseVo;

import java.util.List;

/**
 * 普通的一些工具类方法集合
 * Created by woni on 18/1/28.
 */
public class CommonUtils {



    /**
     * 组装客户端返回值
     * @param responseVO
     * @param result
     */
    public static void putValue2Result(ElasticSearchResponseVo responseVO, SearchResult result, List<Object> data){
        responseVO.setCode(result.getCode());
        responseVO.setMsg(result.getMsg());
        responseVO.setData(data);
    }



}
