package com.yuyue.boss.api.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.RandomSaltUtil;
import com.yuyue.boss.utils.StringUtils;
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
public class SystemController extends BaseController {
    private static Logger log = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private LoginService loginService;

    /**
     * 获取系统菜单
     *
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
            if (CollectionUtils.isEmpty(menuList)){
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "搜索的不存在！");
            }
            PageUtil pageUtil = new PageUtil(menuList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统菜单失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统菜单失败！");
        }
    }

    /**
     * 添加系统菜单
     *
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
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单权限已经存在！");
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
     *
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
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单名称已经存在！");
                }
            }
            loginService.updateSystemMenu(parameterMap.get("id"), 0, parameterMap.get("status"), parameterMap.get("menuName"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "修改系统菜单成功！");
        }
        return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "参数错误！");
    }

    /**
     * 删除系统菜单和权限
     *
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
     *
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
            if (CollectionUtils.isEmpty(menuList)){
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "搜索的不存在！");
            }
            PageUtil pageUtil = new PageUtil(menuList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统用户失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统用户失败！");
        }
    }

    /**
     * 添加系统用户和添加权限
     *
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
            return new ResponseData(CodeEnum.E_10009.getCode(), "登录名已经存在！");
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
     *
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
            List<SystemUser> loginName = loginService.getSystemUser("", "", parameterMap.get("loginName"),"");
            if (CollectionUtils.isNotEmpty(loginName)) {
                return new ResponseData(CodeEnum.E_10009.getCode(), "登录名已经存在！");
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
     *
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
     *
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
//            List<SystemUser> userList = loginService.getSystemUser("", "", "", "");
//            if (CollectionUtils.isEmpty(userList)){
//                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "搜索的不存在！");
//            }
            PageUtil.getPage(parameterMap.get("page"));
            List<SystemUserVO> systemUserVO = loginService.getAppUserMsg(parameterMap.get("loginName"),parameterMap.get("password"));
            if (CollectionUtils.isEmpty(systemUserVO)){
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "搜索的不存在！");
            }
            PageUtil pageUtil = new PageUtil(systemUserVO);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统用户失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统用户失败！");
        }
    }

    /**
     * 修改用户分配系统权限详情
     *
     * @return
     */
    @RequestMapping(value = "/editSystemPermission")
    @ResponseBody
    @RequiresPermissions("PermissionManager:save")
    @LoginRequired
    public ResponseData editSystemPermission(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response
                                            ,@RequestBody List<SystemPermission> systemPermissionList) {
        log.info("修改用户分配系统权限详情----------->>/system/editSystemPermission");
        Map<String, String> parameterMap = getParameterMap(request, response);
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
            PageUtil.getPage(parameterMap.get("page"));
            List<SystemPermission> list = loginService.getSystemPermission("", "", "");
            PageUtil pageUtil = new PageUtil(list);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>修改用户分配系统权限失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改用户分配系统权限失败！");
        }
    }

}
