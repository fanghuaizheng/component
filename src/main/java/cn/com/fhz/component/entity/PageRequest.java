package cn.com.fhz.component.entity;

import java.util.List;

/**
 * Created by woni on 18/3/15.
 */
public class PageRequest<T> {

    private Integer currentPage;

    private Integer size;

    private Long total;

    private List<T> rows;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
