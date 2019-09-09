package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.ResponseData;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户管理
 * Create by lujun.chen on 2018/09/29
 */
@RestController
@RequestMapping(value = "/user", produces = "application/json; charset=UTF-8")
public class UserController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private LoginService loginService;

    /**
     * 获取用户详细
     *
     * @return
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    @RequiresPermissions("video:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData detail(@CurrentUser SystemUser systemUser, HttpServletRequest request) {
        getParameterMap(request);
        Subject subject = SecurityUtils.getSubject();
//        if(subject.isPermitted("video:menu3")){
//            return "video:menu";
//        }else{
//            return "没权限你Rap个锤子啊!";
//        }
        return new ResponseData(loginService.getUser(systemUser.getLoginName(),systemUser.getPassword()));
    }
}
