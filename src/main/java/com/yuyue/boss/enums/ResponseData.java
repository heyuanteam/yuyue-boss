package com.yuyue.boss.enums;

/**
 *  Rest请求响应数据
 * Create by lujun.chen on 2018/09/29
 */
public class ResponseData<T> {
    private Integer code;

    private String msg;

    private T data;

    public ResponseData() {
        this.code = CodeEnum.SUCCESS.getCode();
        this.msg = CodeEnum.SUCCESS.getMessage();
    }

    public ResponseData(T obj) {
        this.code = CodeEnum.SUCCESS.getCode();
        this.msg = CodeEnum.SUCCESS.getMessage();
        this.data = obj;
    }

    public ResponseData(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseData(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
