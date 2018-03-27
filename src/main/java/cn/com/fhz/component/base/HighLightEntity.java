package cn.com.fhz.component.base;


import javax.persistence.Transient;

/**
 * Created by hzfang on 2018/3/19.
 * 专门用户返回高亮的数据
 */
public class HighLightEntity {
    @Transient
    private String highLightValue;

    public String getHighLightValue() {
        return highLightValue;
    }

    public void setHighLightValue(String highLightValue) {
        this.highLightValue = highLightValue;
    }
}
