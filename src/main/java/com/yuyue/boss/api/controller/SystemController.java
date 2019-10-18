package com.yuyue.boss.api.controller;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Maps;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.service.AppUserService;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.MD5Utils;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.RandomSaltUtil;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 系统权限配置
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/system", produces = "application/json; charset=UTF-8")
@Slf4j
public class SystemController extends BaseController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private AppUserService appUserService;

    /**
     * 获取系统用户
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getSystemUser")
    @ResponseBody
    @RequiresPermissions("PermissionManager:menu")
    @LoginRequired
    public ResponseData getSystemUser(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取系统用户----------->>/system/getSystemUser");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<SystemUser> menuList = loginService.getSystemUser(parameterMap.get("status"), parameterMap.get("systemName"),parameterMap.get("loginName"),"");
            PageUtil pageUtil = new PageUtil(menuList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统用户失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统用户失败！");
        }
    }

    /**
     * 添加系统用户和添加权限
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addSystemUser")
    @ResponseBody
    @RequiresPermissions("PermissionManager:save")
    @LoginRequired
    public ResponseData addSystemUser(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加系统用户------------>>/system/addSystemUser");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("loginName"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "登录名不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("password"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "密码不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("systemName"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "系统职称不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("phone"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "手机号不可以为空！");
        }
        List<SystemUser> loginName = loginService.getSystemUser("", "", parameterMap.get("loginName"),"");
        if (CollectionUtils.isNotEmpty(loginName)){
            for (SystemUser user:loginName) {
                if (parameterMap.get("loginName").equals(user.getLoginName())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "登录名已经存在！");
                }
            }
        }
        SystemUser user = new SystemUser();
        try {
            user.setId(RandomSaltUtil.generetRandomSaltCode(32));
            user.setLoginName(parameterMap.get("loginName"));
            user.setPassword(parameterMap.get("password"));
            user.setSystemName(parameterMap.get("systemName"));
            user.setPhone(parameterMap.get("phone"));
            user.setCreateUserId(systemUser.getId());

            AppUser appUserMsg = appUserService.getAppUserMsg(user.getId(),"");
            AppUser app = appUserService.getAppUserMsg("",parameterMap.get("phone"));

            String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            String salt = RandomSaltUtil.generetRandomSaltCode(4);
            AppUser appUser = new AppUser();
            appUser.setId(uuid);
            appUser.setUserNo(RandomSaltUtil.randomNumber(15));
            appUser.setNickName(parameterMap.get("phone"));
            appUser.setRealName(parameterMap.get("phone"));
            appUser.setPhone(parameterMap.get("phone"));
            appUser.setPassword(MD5Utils.getMD5Str(parameterMap.get("password") + salt));
            appUser.setSalt(salt);//盐
            if (StringUtils.isNotNull(appUserMsg)){
                user.setId(appUserMsg.getId());
            } else if (StringUtils.isNotNull(app)){
                user.setId(app.getId());
            }
            appUserService.insertAppUser(appUser);
            loginService.insertSystemUser(user);

            List<SystemMenu> menuList = loginService.getMenu("",0,"","","");
            if (CollectionUtils.isNotEmpty(menuList)){
                for (SystemMenu systemMenu: menuList) {
                    loginService.insertSystemPermission(RandomSaltUtil.generetRandomSaltCode(32),user.getId(),
                            systemMenu.getId(),"","","");
                }
            }
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>添加系统用户失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加系统用户失败！");
        }
    }

    /**
     * 修改系统用户
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editSystemUser")
    @ResponseBody
    @RequiresPermissions("PermissionManager:save")
    @LoginRequired
    public ResponseData editSystemUser(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改系统用户------------>>/system/editSystemUser");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("loginName"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "登录名不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("password"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "密码不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("systemName"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "系统职称不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("phone"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "手机号不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "系统用户ID不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "系统用户状态不可以为空！");
        }

        List<SystemUser> list = loginService.getSystemUser("", "", "",parameterMap.get("id"));
        if (CollectionUtils.isEmpty(list)) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改的用户不存在！");
        }
        if (!parameterMap.get("loginName").equals(list.get(0).getLoginName())) {
            List<SystemUser> userList = loginService.getSystemUser("", "", parameterMap.get("loginName"),"");
            if (CollectionUtils.isNotEmpty(userList)) {
                for (SystemUser user:userList) {
                    if (parameterMap.get("loginName").equals(user.getLoginName())){
                        return new ResponseData(CodeEnum.SUCCESS.getCode(), "登录名已经存在！");
                    }
                }
            }
        }
        try {
            loginService.updateSystemUser(parameterMap.get("id"),parameterMap.get("loginName"),parameterMap.get("password"),
                    parameterMap.get("systemName"),parameterMap.get("phone"),parameterMap.get("status"));
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>修改系统用户失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改系统用户失败！");
        }
    }

    /**
     * 删除系统用户和权限
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delSystemUser")
    @ResponseBody
    @RequiresPermissions("PermissionManager:remove")
    @LoginRequired
    public ResponseData delSystemUser(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除系统用户------------>>/system/delSystemUser");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "系统用户ID不可以为空！");
        }
        if (StringUtils.isNotEmpty(parameterMap.get("id"))) {
            List<SystemUser> list = loginService.getSystemUser("", "", "",parameterMap.get("id"));
            if (CollectionUtils.isEmpty(list)) {
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "搜索的不存在！");
            }
        }
        try {
            loginService.delSystemUser(parameterMap.get("id"));
            List<SystemPermission> SystemPermissionList = loginService.getSystemPermission("",parameterMap.get("id"),"");
            if (CollectionUtils.isNotEmpty(SystemPermissionList)){
                for (SystemPermission systemPermission : SystemPermissionList) {
                    loginService.delSystemPermission(systemPermission.getId());
                }
            }
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>删除系统用户失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除系统用户失败！");
        }
    }

    /**
     * 获取用户分配系统权限详情
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getSystemPermissionList")
    @ResponseBody
    @RequiresPermissions("PermissionManager:menu")
    @LoginRequired
    public ResponseData getSystemPermissionList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取用户分配系统权限详情----------->>/system/getSystemPermissionList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            List<SystemUser> userList = loginService.getSystemUser("", "", "", parameterMap.get("id"));
            if (CollectionUtils.isEmpty(userList)){
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "用户不存在！");
            }
            PageUtil.getPage(parameterMap.get("page"));
            List<SystemUserVO> systemUserVO = loginService.getAppUserMsg(userList.get(0).getLoginName(),userList.get(0).getPassword());
            PageUtil pageUtil = new PageUtil(systemUserVO);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统用户失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统用户失败！");
        }
    }

    /**
     * 修改用户分配系统权限详情
     * @param systemUser
     * @param response
     * @param systemPermissionList
     * @return
     */
    @PostMapping(value = "/editSystemPermission")
    @ResponseBody
    @RequiresPermissions("PermissionManager:save")
    @LoginRequired
    public ResponseData editSystemPermission(@CurrentUser SystemUser systemUser, HttpServletResponse response,HttpServletRequest request,
                                             @RequestBody List<SystemPermission> systemPermissionList) {
        log.info("修改用户分配系统权限详情----------->>/system/editSystemPermission");
        getParameterMap(request, response);
        log.info("参数======>>>>>>"+JSON.toJSONString(systemPermissionList));
        try {
            for (SystemPermission systemPermission : systemPermissionList) {
                List<SystemPermission> list = loginService.getSystemPermission("", "", systemPermission.getId());
                if (CollectionUtils.isNotEmpty(list)){
                    loginService.updateSystemPermission(systemPermission.getId(),systemPermission.getMenuKey(),
                            systemPermission.getSaveKey(),systemPermission.getRemoveKey());
                } else {
                    log.info("系统权限不存在===========>>>>>>>>"+systemPermission.getId());
                }
            }
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>修改用户分配系统权限失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改用户分配系统权限失败！");
        }
    }
}
