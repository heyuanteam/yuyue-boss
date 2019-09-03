package com.yuyue.boss.annotation;

import com.yuyue.boss.enums.CodeEnum;

/**
 * Create by lujun.chen on 2018/09/29
 */
public class MyException extends RuntimeException {
    private int code;
    private String message;

    public MyException(CodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMessage();
    }

    public MyException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public MyException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
