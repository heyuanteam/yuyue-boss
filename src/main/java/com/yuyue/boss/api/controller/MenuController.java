package com.yuyue.boss.api.controller;

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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 菜单管理
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/menu", produces = "application/json; charset=UTF-8")
@Slf4j
public class MenuController extends BaseController {

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
        log.info("获取系统菜单------------>>/menu/getMenuList");
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
        log.info("添加系统菜单------------>>/menu/addMenu");
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
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单权限已经存在！");
                }
            }
        }
        List<SystemMenu> menuNameList = loginService.getMenu("", 0, "", parameterMap.get("menuName"), "");
        if (CollectionUtils.isNotEmpty(menuNameList)) {
            for (SystemMenu systemMenu:menuNameList) {
                if (parameterMap.get("menuName").equals(systemMenu.getMenuName())){
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单名称已经存在！");
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
        log.info("修改系统菜单------------>>/menu/editMenu");
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
                loginService.updateSystemMenu(menuList.get(0).getId(), number, "", "","");
                List<SystemMenu> list2 = loginService.getMenu("", number, "", "", "");
                loginService.updateSystemMenu(list2.get(0).getId(), sort, "", "","");
                List<SystemMenu> list = loginService.getMenu("", 0, "", "", "");
                return new ResponseData(list);
            } catch (Exception e) {
                log.info("=======>>>>>>修改系统菜单失败！");
                return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "修改系统菜单失败！");
            }
        } else if (StringUtils.isNotEmpty(parameterMap.get("status")) && ("10A".equals(parameterMap.get("status"))
                || "10B".equals(parameterMap.get("status"))) && StringUtils.isNotEmpty(parameterMap.get("menuName"))
                && StringUtils.isNotEmpty(parameterMap.get("menuAction"))) {

            List<SystemMenu> list = loginService.getMenu(parameterMap.get("id"), 0, "","","");
            if (CollectionUtils.isEmpty(list)) {
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改的菜单不存在！");
            }
            if (!parameterMap.get("menuName").equals(list.get(0).getMenuName())) {
                List<SystemMenu> menuNameList = loginService.getMenu("", 0, "", parameterMap.get("menuName"), "");
                if (CollectionUtils.isNotEmpty(menuNameList)) {
                    for (SystemMenu systemMenu:menuNameList) {
                        if (parameterMap.get("menuName").equals(systemMenu.getMenuName())){
                            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单名称已经存在！");
                        }
                    }
                }
            }
            loginService.updateSystemMenu(parameterMap.get("id"), 0, parameterMap.get("status"), parameterMap.get("menuName"),
                    parameterMap.get("menuAction"));
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
        log.info("删除系统菜单------------>>/menu/delMenu");
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
}
