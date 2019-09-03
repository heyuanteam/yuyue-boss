package com.yuyue.boss.enums;

import com.yuyue.boss.api.domain.SystemUser;

import java.util.List;

/**
 * Create by lujun.chen on 2018/09/29
 */
public class UserVO extends SystemUser {

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
