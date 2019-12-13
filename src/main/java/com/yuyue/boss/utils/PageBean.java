package com.yuyue.boss.utils;

import java.util.List;

/**
 * @author Jesse
 * @date 2018/1/23
 */
public class PageBean<T> {
    /**
     * 当前页
     */
    private Integer currentPage = 1;
    /**
     * 每页显示的总条数
     */
    private Integer pageSize = 10;
    /**
     * 总条数
     */
    private Integer totalNum;
    /**
     * 是否有下一页
     */
    private Integer isMore;
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 开始索引
     */
    private Integer startIndex;
    /**
     * 结束索引
     */
    private Integer endIndex;
    /**
     * 分页结果
     */
    private List<T> items;

    public PageBean() {
        super();
    }

    public PageBean(List<T> list,Integer currentPage) {
        super();
        this.currentPage = currentPage;
        this.totalPage = (list.size()+this.pageSize-1)/this.pageSize;
        this.startIndex = (this.currentPage-1)*this.pageSize;
        this.endIndex = this.currentPage*this.pageSize>=list.size()?list.size():this.currentPage*this.pageSize;
        this.isMore = this.currentPage >= this.totalPage?0:1;
        this.items = get(list,this.startIndex,this.endIndex);
    }
    public List<T> get(List<T> list,Integer startIndex,Integer endIndex){
        if(list == null) return null;
        if (startIndex > list.size()) return  null;
        if (endIndex >= list.size()){
            endIndex = list.size();
        }
        return list.subList(startIndex,endIndex);
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getIsMore() {
        return isMore;
    }

    public void setIsMore(Integer isMore) {
        this.isMore = isMore;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(Integer endIndex){this.endIndex = endIndex;}

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
