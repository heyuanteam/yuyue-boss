package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.service.AppUserService;
import com.yuyue.boss.enums.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/userManager", produces = "application/json; charset=UTF-8")
@Slf4j
public class AppUserController extends BaseController {
    @Autowired
    private AppUserService appUserService;

    /**
     * 获取用户信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getAppUserMsg")
    @ResponseBody
    @RequiresPermissions("UserManager:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getAppUserMsg(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("获取用户信息-------------->>/userManager/getAppUserMsg");
        String id = request.getParameter("id");

        return new ResponseData(appUserService.getAppUserMsg(id,""));
    }

    /**
     *获取用户列表
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getAppUserMsgList")
    @ResponseBody
    @RequiresPermissions("UserManager:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getAppUserMsgList(AppUser appUser,HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("获取用户列表-------------->>/userManager/getAppUserMsgList");
        List<AppUser> appUserMsgList = appUserService.getAppUserMsgList(appUser);
        return new ResponseData(appUserMsgList);
    }


    /**
     * 更新用户信息
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateAppUser")
    @ResponseBody
    @RequiresPermissions("UserManager:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateAppUser(AppUser appUser,HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("更新用户信息-------------->>/userManager/updateAppUser");
        appUserService.updateAppUser(appUser);
        return new ResponseData();
    }

}
