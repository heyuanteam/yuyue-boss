package com.yuyue.boss.enums;

import java.util.List;

/**
 * Create by lujun.chen on 2018/09/29
 */
public class UserVO extends SysUser {

    //令牌
    private String token;

    /**
     * 权限列表
     */
    private List<String> permissions;

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
