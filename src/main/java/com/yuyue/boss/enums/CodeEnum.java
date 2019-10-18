package com.yuyue.boss.enums;

/**
 * Create by lujun.chen on 2018/09/29
 */
public enum CodeEnum {
    SUCCESS(0, "操作成功"),
    SYSTEM_BUSY(-1, "系统繁忙"),
    SYSTEM_ERROR(102, "系统错误"),
    AUTH_ERROR(40001, "认证失败"),
    PARAM_ERROR(40002, "参数不合法"),
    E_400(400, "请求处理异常，请稍后再试"),
    E_500(500, "请求方式有误,请检查 GET/POST"),
    E_501(501, "请求路径不存在"),
    E_502(502, "权限不足"),
    E_10008(10008, "角色删除失败,尚有用户属于此角色"),
    E_10009(10009, "账户已存在"),
    E_20011(20011, "登陆已过期,请重新登陆"),
    E_90003(90003, "缺少必填参数");

    private final int code;
    private final String message;

    CodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessageByCode(int code) {
        for (CodeEnum _enum : values()) {
            if (_enum.getCode() == code) {
                return _enum.getMessage();
            }
        }
        return null;
    }

    public static CodeEnum getByCode(int code) {
        for (CodeEnum _enum : values()) {
            if (_enum.getCode() == code) {
                return _enum;
            }
        }
        return null;
    }
}
