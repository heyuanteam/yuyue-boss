package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.SystemMenu;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统用户配置
 */
@RestController
@RequestMapping(value = "/user", produces = "application/json; charset=UTF-8")
public class UserController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private LoginService loginService;

    /**
     * 获取菜单
     *
     * @return
     */
    @RequestMapping(value = "/getMenuList")
    @ResponseBody
//    @RequiresPermissions("video:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getMenuList(@CurrentUser SystemUser systemUser, HttpServletRequest request) {
        getParameterMap(request);
//        Subject subject = SecurityUtils.getSubject();
//        if(subject.isPermitted("video:menu3")){
//            return "video:menu";
//        }else{
//            return "没权限你Rap个锤子啊!";
//        }
        List<SystemMenu> menuList = loginService.getMenuList(systemUser.getLoginName(), systemUser.getPassword());
        List<Map<String,Object>> list = new ArrayList<>();
        for (SystemMenu systemMenu: menuList) {
            if(StringUtils.isNotEmpty(systemMenu.getId()) && !"0".equals(systemMenu.getId())){
                Map<String,Object> map = new HashMap<>();
                List<String> menus = loginService.getMenu(systemMenu.getId());
                map.put("menuName",systemMenu.getMenuName());
//                if(CollectionUtils.isNotEmpty(menus)){
                map.put("menuLsits",menus);
                list.add(map);
            }
        }
        return new ResponseData(list);
    }
}
