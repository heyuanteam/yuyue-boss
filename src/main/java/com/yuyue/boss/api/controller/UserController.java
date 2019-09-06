package com.yuyue.boss.api.controller;

import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.ResponseData;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理
 * Create by lujun.chen on 2018/09/29
 */
@RestController
@RequestMapping("/sys/user")
public class UserController {

    @Autowired
    private LoginService sysUserService;

    /**
     * 获取用户详细
     *
     * @return
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    @RequiresPermissions("user:detail")//具有 user:detail 权限的用户才能访问此方法
    public ResponseData detail(String loginName, String password) {
        return new ResponseData(sysUserService.getUser(loginName,password));
    }
}
