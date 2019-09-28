package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.SystemMenu;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统配置
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/system", produces = "application/json; charset=UTF-8")
public class SystemController extends BaseController{
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
    @RequiresPermissions("MenuManager:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getMenuList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response){
        log.info("获取系统菜单------------>>/system/getMenuList");
        getParameterMap(request,response);
//        Subject subject = SecurityUtils.getSubject();
//        if(subject.isPermitted("video:menu3")){
//            return "video:menu";
//        }else{
//            return "没权限你Rap个锤子啊!";
//        }
        List<SystemMenu> menuList = loginService.getMenu("");
//        List<Map<String,Object>> list = new ArrayList<>();
//        for (SystemMenu systemMenu: menuList) {
//            if(StringUtils.isNotEmpty(systemMenu.getId()) && !"0".equals(systemMenu.getId())){
//                Map<String,Object> map = new HashMap<>();
//                List<String> menus = loginService.getMenu(systemMenu.getId());
//                map.put("menuName",systemMenu.getMenuName());
//                map.put("menuLsits",menus);
//                list.add(map);
//            }
//        }
        return new ResponseData(menuList);
    }

    /**
     * 添加系统菜单
     *
     * @return
     */
    @RequestMapping(value = "/addMenu")
    @ResponseBody
    @RequiresPermissions("MenuManager:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData addMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response){
        log.info("获取系统菜单------------>>/system/getMenuList");
        getParameterMap(request,response);
        List<SystemMenu> menuList = loginService.getMenu("");
        SystemMenu systemMenu = menuList.get(menuList.size() - 1);

        return new ResponseData(menuList);
    }

}
