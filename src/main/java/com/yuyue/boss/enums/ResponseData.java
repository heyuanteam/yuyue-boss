package com.yuyue.boss.enums;

/**
 *  Rest请求响应数据
 * Create by lujun.chen on 2018/09/29
 */
public class ResponseData<T> {
    private Integer code;

    private String message;

    private int begin;

    private T data;

    public ResponseData() {
        this.code = CodeEnum.SUCCESS.getCode();
        this.message = CodeEnum.SUCCESS.getMessage();
    }

    public ResponseData(int begin) {
        this.begin = begin;
    }

    public ResponseData(T obj) {
        this.code = CodeEnum.SUCCESS.getCode();
        this.message = CodeEnum.SUCCESS.getMessage();
        this.data = obj;
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
    public void setCode(int begin) {
        this.begin = begin;
    }

    public int getBegin() {
        return begin;
    }

    public String getMessage() {
        return message;
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
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
