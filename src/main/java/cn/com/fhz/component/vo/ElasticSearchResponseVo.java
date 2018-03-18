package cn.com.fhz.component.vo;


import cn.com.fhz.component.entity.PageRequest;

import java.util.List;

/**
 * Created by woni on 18/3/15.
 */
public class ElasticSearchResponseVo<T> {

    private Integer code;

    private String msg;

    private List<T> data;//返回不是分页的数据

    private PageRequest<T> page;//返回的是分页数据

    public PageRequest<T> getPage() {
        return page;
    }

    public void setPage(PageRequest<T> page) {
        this.page = page;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
