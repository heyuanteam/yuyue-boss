package com.yuyue.boss.enums;

/**
 * Create by lujun.chen on 2018/09/29
 */
public class Constants {
    public static final String SUCCESS_CODE = "100";
    public static final String SUCCESS_MSG = "请求成功";

    /**
     * session中存放用户信息的key值
     */
    public static final String SESSION_USER_INFO = "userInfo";
    public static final String SESSION_USER_PERMISSION = "userPermission";

    //redis中存放TOKEN与shiro sessionid 映射关系Key 前缀
    public static final String REDIS_KEY_PREFIX_SHIRO_TOKEN = "itclj:shiro:sessionid:token:";

    //TOKEN 与shiro sessionid 映射关系过期时间
    public static final Integer REDIS_SHIRO_TOKEN_EXPIRES = 4 * 8600;

    //http请求头TOKEN key
    public static final String HTTP_REQUEST_HEADER_TOKEN_KEY = "Access-Token";

}
