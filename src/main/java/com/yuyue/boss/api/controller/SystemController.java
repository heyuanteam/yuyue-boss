package com.yuyue.boss.api.controller;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Maps;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
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

/**
 * 系统配置
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/system", produces = "application/json; charset=UTF-8")
@Slf4j
public class SystemController extends BaseController {

    @Autowired
    private LoginService loginService;

    /**
     * 获取系统菜单
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getMenu")
    @ResponseBody
    @RequiresPermissions("MenuManager:menu")
    @LoginRequired
    public ResponseData getMenuList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取系统菜单------------>>/system/getMenuList");
        Map<String, String> parameterMap = getParameterMap(request, response);
//        Subject subject = SecurityUtils.getSubject();
//        if(subject.isPermitted("video:menu3")){
//            return "video:menu";
//        }else{
//            return "没权限你Rap个锤子啊!";
//        }
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<SystemMenu> menuList = loginService.getMenu("", 0, "", parameterMap.get("menuName"), parameterMap.get("status"));
            PageUtil pageUtil = new PageUtil(menuList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统菜单失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统菜单失败！");
        }
    }

    /**
     * 添加系统菜单
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addMenu")
    @ResponseBody
    @RequiresPermissions("MenuManager:save")
    @LoginRequired
    public ResponseData addMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加系统菜单------------>>/system/addMenu");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("menuName"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单名不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("menuAction"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单图标链接不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("role"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单权限不可以为空！");
        }
        List<SystemMenu> roleList = loginService.getMenu("", 0, parameterMap.get("role"), "", "");
        if (CollectionUtils.isNotEmpty(roleList)) {
            for (SystemMenu systemMenu:roleList) {
                if (parameterMap.get("role").equals(systemMenu.getRole())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "菜单权限已经存在！");
                }
            }
        }
        List<SystemMenu> menuNameList = loginService.getMenu("", 0, "", parameterMap.get("menuName"), "");
        if (CollectionUtils.isNotEmpty(menuNameList)) {
            for (SystemMenu systemMenu:menuNameList) {
                if (parameterMap.get("menuName").equals(systemMenu.getMenuName())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "菜单名称已经存在！");
                }
            }
        }

        List<SystemMenu> menuList = loginService.getMenu("", 0, "", "", "");
        int sort = menuList.get(menuList.size() - 1).getSort();
        sort += 1;
        SystemMenu systemMenu = new SystemMenu();
        try {
            systemMenu.setId(RandomSaltUtil.generetRandomSaltCode(32));
            systemMenu.setMenuName(parameterMap.get("menuName"));
            systemMenu.setMenuAction(parameterMap.get("menuAction"));
            systemMenu.setRole(parameterMap.get("role"));
            systemMenu.setSort(sort);
            loginService.insertSystemMenu(systemMenu);
            //插入权限记录
            List<SystemUser> userList = loginService.getSystemUserMsg("", "", "", "");
            if (CollectionUtils.isNotEmpty(userList)){
                for (SystemUser user: userList) {
                    loginService.insertSystemPermission(RandomSaltUtil.generetRandomSaltCode(32),user.getId(),
                            systemMenu.getId(),"","","");
                }
            }
            List<SystemMenu> list = loginService.getMenu("", 0, "", "", "");
            return new ResponseData(list);
        } catch (Exception e) {
            log.info("=======>>>>>>添加系统菜单失败！");
            return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "添加系统菜单失败！");
        }
    }

    /**
     * 修改系统菜单
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editMenu")
    @ResponseBody
    @RequiresPermissions("MenuManager:save")
    @LoginRequired
    public ResponseData editMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改系统菜单------------>>/system/editMenu");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单ID不可以为空！");
        }
        if (StringUtils.isNotEmpty(parameterMap.get("sort")) &&
                (0 == Integer.valueOf(parameterMap.get("sort")) || 1 == Integer.valueOf(parameterMap.get("sort")))) {
            try {
                List<SystemMenu> menuList = loginService.getMenu(parameterMap.get("id"), 0, "", "", "");
                if (CollectionUtils.isEmpty(menuList)) {
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单ID错误！");
                }
                int sort = menuList.get(0).getSort();
                int number = 0;
                if (0 == Integer.valueOf(parameterMap.get("sort"))) {
                    number = sort + 1;//向下
                } else {
                    number = sort - 1;//向上
                }
                loginService.updateSystemMenu(menuList.get(0).getId(), number, "", "");
                List<SystemMenu> list2 = loginService.getMenu("", number, "", "", "");
                loginService.updateSystemMenu(list2.get(0).getId(), sort, "", "");
                List<SystemMenu> list = loginService.getMenu("", 0, "", "", "");
                return new ResponseData(list);
            } catch (Exception e) {
                log.info("=======>>>>>>修改系统菜单失败！");
                return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "修改系统菜单失败！");
            }
        } else if (StringUtils.isNotEmpty(parameterMap.get("status")) && ("10A".equals(parameterMap.get("status"))
                || "10B".equals(parameterMap.get("status"))) && StringUtils.isNotEmpty(parameterMap.get("menuName"))) {

            List<SystemMenu> list = loginService.getMenu(parameterMap.get("id"), 0, "","","");
            if (CollectionUtils.isEmpty(list)) {
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改的菜单不存在！");
            }
            if (!parameterMap.get("menuName").equals(list.get(0).getMenuName())) {
                List<SystemMenu> menuNameList = loginService.getMenu("", 0, "", parameterMap.get("menuName"), "");
                if (CollectionUtils.isNotEmpty(menuNameList)) {
                    for (SystemMenu systemMenu:menuNameList) {
                        if (parameterMap.get("menuName").equals(systemMenu.getMenuName())){
                            return new ResponseData(CodeEnum.SUCCESS.getCode(), "菜单名称已经存在！");
                        }
                    }
                }
            }
            loginService.updateSystemMenu(parameterMap.get("id"), 0, parameterMap.get("status"), parameterMap.get("menuName"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "修改系统菜单成功！");
        }
        return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "参数错误！");
    }

    /**
     * 删除系统菜单和权限
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delMenu")
    @ResponseBody
    @RequiresPermissions("MenuManager:remove")
    @LoginRequired
    public ResponseData delMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除系统菜单------------>>/system/delMenu");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单ID不可以为空！");
        }
        try {
            List<SystemMenu> menuList = loginService.getMenu(parameterMap.get("id"), 0, "", "", "");
            if (CollectionUtils.isEmpty(menuList)) {
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单ID错误！");
            }
            loginService.delMenu(parameterMap.get("id"));
            List<SystemPermission> list = loginService.getSystemPermission(parameterMap.get("id"),"","");
            if (CollectionUtils.isNotEmpty(list)) {
                for (SystemPermission systemPermission : list) {
                    loginService.delSystemPermission(systemPermission.getId());
                }
            }
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("=======>>>>>>删除系统菜单失败！");
            return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "删除系统菜单失败！");
        }
    }

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

    /**
     * 获取系统字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getLookupCdeSystem")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:menu")
    @LoginRequired
    public ResponseData getLookupCdeSystem(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取系统字典----------->>/system/getSystem");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<LookupCde> menuList = loginService.getLookupCdeSystem(parameterMap.get("status"), parameterMap.get("typeName"),"");
            PageUtil pageUtil = new PageUtil(menuList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统字典失败！");
        }
    }

    /**
     * 添加系统字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addLookupCdeSystem")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:save")
    @LoginRequired
    public ResponseData addLookupCdeSystem(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加系统字典----------->>/system/addLookupCdeSystem");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("typeName"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典名称不可以为空！");
        }
        List<LookupCde> LookupCdeList = loginService.getLookupCdeSystem("", parameterMap.get("typeName"),"");
        if (CollectionUtils.isNotEmpty(LookupCdeList)){
            for (LookupCde lookupCde:LookupCdeList) {
                if (parameterMap.get("typeName").equals(lookupCde.getTypeName())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "字典名称已经存在！");
                }
            }
        }
        try {
            List<LookupCde> list = loginService.getLookupCdeSystem("", "","");
            int sort = list.get(list.size()-1).getSort();
            sort += 1;
            LookupCde lookupCde = new LookupCde();
            lookupCde.setId(RandomSaltUtil.generetRandomSaltCode(32));
            lookupCde.setTypeName(parameterMap.get("typeName"));
            lookupCde.setSort(sort);
            loginService.insertLookupCde(lookupCde);
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>添加系统字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加系统字典失败！");
        }
    }

    /**
     * 修改系统字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editLookupCdeSystem")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:save")
    @LoginRequired
    public ResponseData editLookupCdeSystem(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改系统字典----------->>/system/editLookupCdeSystem");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("typeName"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典名称不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典ID不可以为空！");
        }
        List<LookupCde> list = loginService.getLookupCdeSystem("", "", parameterMap.get("id"));
        if (CollectionUtils.isEmpty(list)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改字典不存在！");
        }
        if (!parameterMap.get("typeName").equals(list.get(0).getTypeName())) {
            List<LookupCde> LookupCdeList = loginService.getLookupCdeSystem("", parameterMap.get("typeName"),"");
            if (CollectionUtils.isNotEmpty(LookupCdeList)){
                for (LookupCde lookupCde:LookupCdeList) {
                    if (parameterMap.get("typeName").equals(lookupCde.getTypeName())){
                        return new ResponseData(CodeEnum.SUCCESS.getCode(), "字典名称已经存在！");
                    }
                }
            }
        }
        try {
            loginService.updateLookupCde(parameterMap.get("id"),parameterMap.get("typeName"),parameterMap.get("status"));
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>修改系统字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改系统字典失败！");
        }
    }

    /**
     * 删除系统字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delLookupCde")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:remove")
    @LoginRequired
    public ResponseData delLookupCde(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除系统字典---------->>/system/delLookupCde");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典id不可以为空！");
        }
        try {
            List<LookupCde> list = loginService.getLookupCdeSystem("", "", parameterMap.get("id"));
            if (CollectionUtils.isEmpty(list)){
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改字典不存在！");
            }
            loginService.delLookupCdeSystem(parameterMap.get("id"));
            List<LookupCdeConfig> menuList = loginService.getLookupCdeList(parameterMap.get("id"),"");
            if (CollectionUtils.isNotEmpty(menuList)){
                for (LookupCdeConfig lookupCdeConfig: menuList) {
                    loginService.delLookupCdeList(lookupCdeConfig.getId());
                }
            }
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>删除系统字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除系统字典失败！");
        }
    }

    /**
     * 获取系统字典下级
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getLookupCdeList")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:menu")
    @LoginRequired
    public ResponseData getLookupCdeList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取系统字典下级----------->>/system/getLookupCdeList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典id不可以为空！");
        }
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<LookupCdeConfig> menuList = loginService.getLookupCdeList(parameterMap.get("id"),"");
            PageUtil pageUtil = new PageUtil(menuList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统字典下级失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统字典下级失败！");
        }
    }

    /**
     * 添加系统字典下级
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addLookupCdeList")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:save")
    @LoginRequired
    public ResponseData addLookupCdeList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加系统字典下级----------->>/system/addLookupCdeList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("type"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典类型不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("systemId"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典systemId不可以为空！");
        }
        try {
            LookupCdeConfig lookupCdeConfig = new LookupCdeConfig();
            lookupCdeConfig.setId(RandomSaltUtil.generetRandomSaltCode(32));
            lookupCdeConfig.setStatus(parameterMap.get("status"));
            lookupCdeConfig.setType(parameterMap.get("type"));
            lookupCdeConfig.setSystemId(parameterMap.get("systemId"));
            loginService.insertLookupCdeConfig(lookupCdeConfig);
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>添加系统字典下级失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加系统字典下级失败！");
        }
    }

    /**
     * 修改系统字典下级
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editLookupCdeList")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:save")
    @LoginRequired
    public ResponseData editLookupCdeList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改系统字典下级----------->>/system/editLookupCdeList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("type"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典类型不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典ID不可以为空！");
        }
        try {
            loginService.updateLookupCdeList(parameterMap.get("id"),parameterMap.get("type"),parameterMap.get("status"));
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>修改系统字典下级失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改系统字典下级失败！");
        }
    }

    /**
     * 删除系统字典下级
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delLookupCdeList")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:remove")
    @LoginRequired
    public ResponseData delLookupCdeList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除系统字典下级----------->>/system/delLookupCdeList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典id不可以为空！");
        }
        List<LookupCdeConfig> menuList = loginService.getLookupCdeList("",parameterMap.get("id"));
        if (CollectionUtils.isEmpty(menuList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "搜索的不存在！");
        }
        try {
            loginService.delLookupCdeList(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>删除系统字典下级失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除系统字典下级失败！");
        }
    }

    /**
     * 获取全部字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getSystemList")
    @ResponseBody
    @LoginRequired
    public ResponseData getSystemList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取全部字典----------->>/system/getSystemList");
        getParameterMap(request, response);
        try {
            List<LookupCde> lookupCdeList = loginService.getLookupCdeSystem("", "","");
            List list = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(lookupCdeList)){
                for (LookupCde lookupCde: lookupCdeList) {
                    Map<String,Object> map = Maps.newHashMap();
                    map.put("id",lookupCde.getId());
                    map.put("typeName",lookupCde.getTypeName());
                    List<LookupCdeConfig> lookupCdeConfigList = loginService.getLookupCdeList(lookupCde.getId(),"");
                    map.put("content",lookupCdeConfigList);
                    list.add(map);
                }
            }
            return new ResponseData(list);
        } catch (Exception e) {
            log.info("===========>>>>>>获取全部字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取全部字典失败！");
        }
    }

    /**
     * 获取版本列表
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getVersionList")
    @ResponseBody
    @RequiresPermissions("appversion:menu")
    @LoginRequired
    public ResponseData getVersionList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取版本列表----------->>/system/getVersionList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<AppVersion> appVersionList = loginService.getVersionList(parameterMap.get("systemType"), parameterMap.get("versionNo"),"");
            PageUtil pageUtil = new PageUtil(appVersionList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取版本列表失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取版本列表失败！");
        }
    }

    /**
     * 添加版本
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addVersion")
    @ResponseBody
    @RequiresPermissions("appversion:save")
    @LoginRequired
    public ResponseData addVersion(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加版本----------->>/system/addVersion");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("versionNo"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本号不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("systemType"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本类型不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("programDescription"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本介绍不可以为空！");
        }
        List<AppVersion> appVersionList = loginService.getVersionList(parameterMap.get("systemType"), parameterMap.get("versionNo"),"");
        if (CollectionUtils.isNotEmpty(appVersionList)){
            for (AppVersion appVersion:appVersionList) {
                if (parameterMap.get("versionNo").equals(appVersion.getVersionNo())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "版本号已经存在！");
                }
            }
        }
        try {
            List<AppVersion> list = loginService.getVersionList(parameterMap.get("systemType"), "","");
            int sort = list.get(list.size()-1).getNumber();
            sort += 1;
            AppVersion appVersion = new AppVersion();
            appVersion.setAppVersionId(RandomSaltUtil.generetRandomSaltCode(32));
            appVersion.setSystemType(parameterMap.get("systemType"));
            appVersion.setVersionNo(parameterMap.get("versionNo"));
            appVersion.setUpdateUser(systemUser.getLoginName());
            appVersion.setProgramDescription(parameterMap.get("programDescription"));
            appVersion.setNumber(sort);
            loginService.insertAppVersion(appVersion);
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "添加版本成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>添加版本失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加版本失败！");
        }
    }

    /**
     * 修改版本
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editVersion")
    @ResponseBody
    @RequiresPermissions("appversion:save")
    @LoginRequired
    public ResponseData editVersion(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改版本----------->>/system/editVersion");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("versionNo"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本号不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("systemType"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本类型不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("programDescription"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本介绍不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "强制更新状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("appVersionId"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本appVersionId不可以为空！");
        }
        List<AppVersion> appVersionList = loginService.getVersionList(parameterMap.get("systemType"), parameterMap.get("versionNo"),"");
        if (CollectionUtils.isNotEmpty(appVersionList)){
            for (AppVersion appVersion:appVersionList) {
                if (parameterMap.get("versionNo").equals(appVersion.getVersionNo())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "版本号已经存在！");
                }
            }
        }
        try {
            loginService.updateAppVersion(parameterMap.get("appVersionId"),parameterMap.get("versionNo"),
                    parameterMap.get("systemType"),parameterMap.get("programDescription"),parameterMap.get("status"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "修改版本成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>修改版本失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改版本失败！");
        }
    }

    /**
     * 删除版本
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delVersion")
    @ResponseBody
    @RequiresPermissions("appversion:remove")
    @LoginRequired
    public ResponseData delVersion(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除版本----------->>/system/delVersion");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("appVersionId"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本appVersionId不可以为空！");
        }
        List<AppVersion> appVersionList = loginService.getVersionList("","",parameterMap.get("appVersionId"));
        if (CollectionUtils.isEmpty(appVersionList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本不存在！");
        }
        try {
            loginService.delVersion(parameterMap.get("appVersionId"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除版本成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除版本失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除版本失败！");
        }
    }

    /**
     * 获取公告列表
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getJPushList")
    @ResponseBody
    @RequiresPermissions("Announcement:menu")
    @LoginRequired
    public ResponseData getJPushList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取公告列表----------->>/system/getJPushList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<JPush> jPushList = loginService.getJPushList("",parameterMap.get("msgTitle"), parameterMap.get("extras"),
                                    parameterMap.get("startTime"),parameterMap.get("endTime"));
            PageUtil pageUtil = new PageUtil(jPushList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取公告列表失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取公告列表失败！");
        }
    }

    /**
     * 删除公告
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delJPush")
    @ResponseBody
    @RequiresPermissions("Announcement:remove")
    @LoginRequired
    public ResponseData delJPush(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除公告----------->>/system/delJPush");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "公告id不可以为空！");
        }
        List<JPush> jPushList = loginService.getJPushList(parameterMap.get("id"),"","","","");
        if (CollectionUtils.isEmpty(jPushList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "公告不存在！");
        }
        try {
            loginService.delJPush(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除公告成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除公告失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除公告失败！");
        }
    }

    /**
     * 获取APP菜单列表
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getAPPMenuList")
    @ResponseBody
    @RequiresPermissions("appMenu:menu")
    @LoginRequired
    public ResponseData getAPPMenuList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取APP菜单列表----------->>/system/getAPPMenuList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<VideoCategory> videoCategoryList = loginService.getAPPMenuList("",parameterMap.get("category"), parameterMap.get("status"),0);
            PageUtil pageUtil = new PageUtil(videoCategoryList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取APP菜单列表失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取APP菜单列表失败！");
        }
    }

    /**
     * 添加APP菜单
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addAPPMenu")
    @ResponseBody
    @RequiresPermissions("appMenu:save")
    @LoginRequired
    public ResponseData addAPPMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加APP菜单----------->>/system/addAPPMenu");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("category"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单名称不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("url"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单图标不可以为空！");
        }
        List<VideoCategory> videoCategoryList = loginService.getAPPMenuList("",parameterMap.get("category"), "",0);
        if (CollectionUtils.isNotEmpty(videoCategoryList)){
            for (VideoCategory videoCategory:videoCategoryList) {
                if (parameterMap.get("category").equals(videoCategory.getCategory())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "菜单名称已经存在！");
                }
            }
        }
        try {
            List<VideoCategory> list = loginService.getAPPMenuList("","", "",0);
            int sort = list.get(list.size()-1).getCategoryNo();
            sort += 1;
            VideoCategory videoCategory = new VideoCategory();
            videoCategory.setId(RandomSaltUtil.generetRandomSaltCode(32));
            videoCategory.setCategory(parameterMap.get("category"));
            videoCategory.setUrl(parameterMap.get("url"));
            videoCategory.setCategoryNo(sort);
            loginService.insertVideoCategory(videoCategory);
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "添加APP菜单成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>添加APP菜单失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加APP菜单失败！");
        }
    }

    /**
     * 修改APP菜单
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editAPPMenu")
    @ResponseBody
    @RequiresPermissions("appMenu:save")
    @LoginRequired
    public ResponseData editAPPMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改APP菜单------------>>/system/editAPPMenu");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单ID不可以为空！");
        }
        if (StringUtils.isNotEmpty(parameterMap.get("sort")) &&
                (0 == Integer.valueOf(parameterMap.get("sort")) || 1 == Integer.valueOf(parameterMap.get("sort")))) {
            try {
                List<VideoCategory> videoCategoryList = loginService.getAPPMenuList(parameterMap.get("id"),"", "",0);
                if (CollectionUtils.isEmpty(videoCategoryList)) {
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单ID错误！");
                }
                int sort = videoCategoryList.get(0).getCategoryNo();
                int number = 0;
                if (0 == Integer.valueOf(parameterMap.get("sort"))) {
                    number = sort + 1;//向下
                } else {
                    number = sort - 1;//向上
                }
                loginService.updateAPPMenu(videoCategoryList.get(0).getId(), number,"","","");
                List<VideoCategory> list2 = loginService.getAPPMenuList("", "", "",number);
                loginService.updateAPPMenu(list2.get(0).getId(), sort, "", "","");
                List<VideoCategory> list = loginService.getAPPMenuList("","","",0);
                return new ResponseData(list);
            } catch (Exception e) {
                log.info("=======>>>>>>修改修改APP菜单失败！");
                return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "修改APP菜单失败！");
            }
        } else if (StringUtils.isNotEmpty(parameterMap.get("status")) && ("10A".equals(parameterMap.get("status"))
                || "10B".equals(parameterMap.get("status"))) && StringUtils.isNotEmpty(parameterMap.get("category"))
                && StringUtils.isNotEmpty(parameterMap.get("url"))) {

            List<VideoCategory> videoCategoryList = loginService.getAPPMenuList(parameterMap.get("id"),"", "",0);
            if (CollectionUtils.isEmpty(videoCategoryList)) {
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改的菜单不存在！");
            }
            if (!parameterMap.get("category").equals(videoCategoryList.get(0).getCategory())) {
                List<VideoCategory> categoryList = loginService.getAPPMenuList("",parameterMap.get("category"), "",0);
                if (CollectionUtils.isNotEmpty(categoryList)) {
                    for (VideoCategory videoCategory:categoryList) {
                        if (parameterMap.get("category").equals(videoCategory.getCategory())){
                            return new ResponseData(CodeEnum.SUCCESS.getCode(), "菜单名称已经存在！");
                        }
                    }
                }
            }
            loginService.updateAPPMenu(videoCategoryList.get(0).getId(), 0, parameterMap.get("status"), parameterMap.get("category"),
                    parameterMap.get("url"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "修改APP菜单成功！");
        }
        return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "参数错误！");
    }

    /**
     * 删除APP菜单
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delAPPMenu")
    @ResponseBody
    @RequiresPermissions("appMenu:remove")
    @LoginRequired
    public ResponseData delAPPMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除APP菜单----------->>/system/delAPPMenu");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "APP菜单id不可以为空！");
        }
        List<VideoCategory> categoryList = loginService.getAPPMenuList(parameterMap.get("id"),"", "",0);
        if (CollectionUtils.isEmpty(categoryList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "APP菜单不存在！");
        }
        try {
            loginService.delAPPMenu(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除APP菜单成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除APP菜单失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除APP菜单失败！");
        }
    }
}
