package com.yuyue.boss.enums;

/**
 *  Rest请求响应数据
 * Create by lujun.chen on 2018/09/29
 */
public class ResponseData<T> {
    private Integer code;

    private String message;


    private int total;

    private int pages;

    private int currentPage;

    private T data;

    public ResponseData() {
        this.code = CodeEnum.SUCCESS.getCode();
        this.message = CodeEnum.SUCCESS.getMessage();
    }



    public ResponseData(T obj) {
        this.code = CodeEnum.SUCCESS.getCode();
        this.message = CodeEnum.SUCCESS.getMessage();
        this.data = obj;
    }
    public ResponseData(T obj,int currentPage,int total,int pages) {
        this.code = CodeEnum.SUCCESS.getCode();
        this.message = CodeEnum.SUCCESS.getMessage();
        this.data = obj;
        this.currentPage=currentPage;
        this.total=total;
        this.pages=pages;
    }

    public ResponseData(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public ResponseData(Integer code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    public void setCurrentPage(int currentPage){ this.currentPage=currentPage;}

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setTotal(int total){ this.total=total;}

    public int getTotal() {
        return this.total;
    }

    public void setPages(int total){ this.pages=pages;}

    public int getPages() {
        return this.pages;
    }
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
