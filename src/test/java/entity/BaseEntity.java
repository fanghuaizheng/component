package entity;

import cn.com.fhz.component.annotation.Id;
import cn.com.fhz.component.base.HighLightEntity;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by hzfang on 2018/2/23.
 */
public class BaseEntity extends HighLightEntity{

    @Id
    @JSONField(serialize = false)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}
