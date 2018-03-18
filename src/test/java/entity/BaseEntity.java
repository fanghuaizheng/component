package entity;

import cn.com.fhz.component.entity.SearchBaseEntity;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by hzfang on 2018/2/23.
 */
public class BaseEntity extends SearchBaseEntity{

    @JSONField(serialize = false)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}
