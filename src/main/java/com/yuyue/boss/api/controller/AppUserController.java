package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.domain.ChangeMoney;
import com.yuyue.boss.api.domain.Order;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.service.AppUserService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/userManager", produces = "application/json; charset=UTF-8")
public class AppUserController extends BaseController {
    @Autowired
    private AppUserService appUserService;

    /**
     * 获取用户信息
     * @param request
     * @param response
     * @return
     */
/*    @RequestMapping("/getAppUserMsg")
    @ResponseBody
    @RequiresPermissions("UserManager:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getAppUserMsg(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("获取用户信息-------------->>/userManager/getAppUserMsg");
        String id = request.getParameter("id");

        return new ResponseData(appUserService.getAppUserMsg(id));
    }*/

    /**
     *获取用户列表
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getAppUserMsg" )
    @ResponseBody
    @RequiresPermissions("UserManager:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getAppUserMsg(AppUser appUser, HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("获取用户信息-------------->>/userManager/getAppUserMsgList");
        String page=request.getParameter("page");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        if (StringUtils.isNotEmpty(appUser.getId())){
            return new ResponseData(appUserService.getAppUserMsg(appUser.getId(),""));
        }
        PageHelper.startPage(Integer.parseInt(page), 10);
        List<AppUser> appUserMsgList = appUserService.getAppUserMsgList(appUser);
        PageInfo<AppUser> pageInfo=new PageInfo<>(appUserMsgList);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(appUserMsgList, currentPage,(int) total,pages);
    }


    /**
     * 更新用户信息
     * @param appUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/updateAppUser")
    @ResponseBody
    @RequiresPermissions("UserManager:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateAppUser(AppUser appUser,HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("更新用户信息-------------->>/userManager/updateAppUser");
        appUserService.updateAppUser(appUser);
        return new ResponseData();
    }

    /**
     * 删除用户
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delAppUser")
    @ResponseBody
    @RequiresPermissions("UserManager:remove")
    @LoginRequired
    public ResponseData delAppUser(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除用户----------->>/userManager/delAppUser");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "用户id不可以为空！");
        }
        AppUser appUser = appUserService.getAppUserMsg(parameterMap.get("id"),"");
        if (StringUtils.isNull(appUser)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "用户不存在！");
        }
        try {
            appUserService.delAppUser(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除用户成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除用户失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除用户失败！");
        }
    }

}
