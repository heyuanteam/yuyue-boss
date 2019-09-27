package com.yuyue.boss.api.controller;

import com.google.common.collect.Maps;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.SystemMenu;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.UserVO;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.Constants;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.RedisUtil;
import com.yuyue.boss.utils.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Create by lujun.chen on 2018/09/29
 */
@RestController
@RequestMapping(value = "/login", produces = "application/json; charset=UTF-8")
public class LoginController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private LoginService loginService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 密码登录
     *
     * shiro登录，shiro采用Facade模式（门面模式），所有与shiro的交互都通过Subject对象API。
     * 调用Subject.login后会触发UserRealm的doGetAuthenticationInfo方法，进行具体的登录验证处理。
     *
     * @param password 密码
     * @return
     */
    @RequestMapping("/userPasswordLogin")
    @ResponseBody
    public ResponseData userLogin(String loginName, String password, HttpServletRequest request, HttpServletResponse response){
        log.info("登录------------>>/login/userPasswordLogin");
        //允许跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        getParameterMap(request);
        if(StringUtils.isEmpty(loginName)){
            return new ResponseData("用户名为空！");
        } else if(StringUtils.isEmpty(password)){
            return new ResponseData("密码为空！");
        }
        UserVO userVO = loginService.getUser(loginName, password);
        if(StringUtils.isNull(userVO)){
            return new ResponseData("用户名或密码不正确！");
        }
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginName, password);
        try {
            //会触发com.itclj.common.shiro.UserRealm的doGetAuthenticationInfo方法
            currentUser.login(token);
//            Session session = SecurityUtils.getSubject().getSession();
//            UserVO userVO = (UserVO) session.getAttribute(Constants.SESSION_USER_INFO);
//            Constants.REDIS_KEY_PREFIX_SHIRO_TOKEN + userVO.getToken()
            UserVO user = (UserVO) SecurityUtils.getSubject().getPrincipal();
            redisUtil.setString(user.getId(), user.getPermissions(), Constants.REDIS_SHIRO_TOKEN_EXPIRES);
            user.setToken(loginService.getToken(user));

            List<SystemMenu> menuList = loginService.getMenuList(userVO.getLoginName(), userVO.getPassword());
            Map<String,Object> map = Maps.newHashMap();
            map.put("menu",menuList);
            map.put("user",user);
            return new ResponseData(map);
        } catch (AuthenticationException e) {
            return new ResponseData("登录失败");
        }
    }

    /**
     * 短信验证登录
     * @return
     */
    @RequestMapping("/userPhoneLogin")
    @ResponseBody
    public ResponseData userPhoneLogin(String phone, String code, HttpServletRequest request, HttpServletResponse response){
        log.info("登录------------>>/login/userPhoneLogin");
        //允许跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        getParameterMap(request);
        if(StringUtils.isEmpty(phone)){
            return new ResponseData("用户名为空！");
        } else if(StringUtils.isEmpty(code) || code.length() != 6 || !code.equals(redisUtil.getString(phone))){
            return new ResponseData("验证码错误！");
        }
        SystemUser systemUser = loginService.getSystemUserMsg("","","",phone);
        if(StringUtils.isNull(systemUser)){
            return new ResponseData("用户名或密码不正确！");
        }

        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(systemUser.getLoginName(), systemUser.getPassword());
        try {
            currentUser.login(token);
            UserVO user = (UserVO) SecurityUtils.getSubject().getPrincipal();
            redisUtil.setString(user.getId(), user.getPermissions(), Constants.REDIS_SHIRO_TOKEN_EXPIRES);
            user.setToken(loginService.getToken(user));

            List<SystemMenu> menuList = loginService.getMenuList(user.getLoginName(), user.getPassword());
            Map<String,Object> map = Maps.newHashMap();
            map.put("menu",menuList);
            map.put("user",user);
            return new ResponseData(map);
        } catch (AuthenticationException e) {
            return new ResponseData("登录失败");
        }
    }

    /**
     * 获取菜单
     *
     * @return
     */
//    @RequestMapping(value = "/getMenuList")
//    @ResponseBody
//    @RequiresPermissions("video:menu")//具有 user:detail 权限的用户才能访问此方法
//    @LoginRequired
//    public ResponseData getMenuList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response){
//        log.info("获取菜单------------>>/login/getMenuList");
//        //允许跨域
//        response.setHeader("Access-Control-Allow-Origin","*");
//        getParameterMap(request);
//        Subject subject = SecurityUtils.getSubject();
//        if(subject.isPermitted("video:menu3")){
//            return "video:menu";
//        }else{
//            return "没权限你Rap个锤子啊!";
//        }
//        List<String> menuList = loginService.getMenuList(systemUser.getLoginName(), systemUser.getPassword());
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
//        return new ResponseData("menuList");
//    }
}
