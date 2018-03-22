package cn.com.fhz.component.entity;

import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by woni on 18/3/22.
 * 用户查询域排序
 */
public class SortFieldRequest {

    private String sortType;

    private String fieldName;

    /**
     * 默认按照查询域的升序
     */
    private SortOrder px = SortOrder.ASC;

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public SortOrder getPx() {
        return px;
    }

    public void setPx(SortOrder px) {
        this.px = px;
    }
}
