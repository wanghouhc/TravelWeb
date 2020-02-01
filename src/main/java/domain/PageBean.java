package domain;

import java.util.List;

public class PageBean<T> {
    /*当前页码 */
    private Integer pageNumber;
    /*每页多少条*/
    private Integer pageSize;
    /*总共多少数据*/
    private Integer totalCount;
    /*分了多少页*/
    private Integer pageCount;
    /*页码条从几开始显示*/
    private Integer start;
    /*页码条显示到几结束*/
    private Integer end;
    /*当前页的数据集合*/
    private List<T> data;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
