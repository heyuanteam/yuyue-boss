package com.yuyue.boss.api.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.SystemMenu;
import com.yuyue.boss.api.domain.SystemMenuVo;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.UserVO;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.Constants;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.RedisUtil;
import com.yuyue.boss.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.Iterator;
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
     * @param loginName
     * @param password
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/userPasswordLogin")
    @ResponseBody
    public ResponseData userLogin(String loginName, String password, HttpServletRequest request, HttpServletResponse response){
        log.info("登录------------>>/login/userPasswordLogin");
        getParameterMap(request,response);
        if(StringUtils.isEmpty(loginName)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"用户名为空！");
        } else if(StringUtils.isEmpty(password)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"密码为空！");
        }
        UserVO userVO = loginService.getUser(loginName, password);
        if(StringUtils.isNull(userVO)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"用户名或密码不正确！");
        }
        if ("10A".equals(userVO.getStatus())) {
            return new ResponseData(CodeEnum.E_502.getCode(),"该账号已禁用！");
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
            user.setToken(loginService.getToken(user));
            redisUtil.setString(Constants.REDIS_KEY_PREFIX_SHIRO_TOKEN + user.getToken(),
                    user.getPermissions(), Constants.REDIS_SHIRO_TOKEN_EXPIRES);
//            获取菜单
            List<SystemMenuVo> menuList = loginService.getMenuList(user.getLoginName(), user.getPassword());
            Iterator<SystemMenuVo> iterator = menuList.iterator();
            while (iterator.hasNext()) {
                SystemMenuVo systemMenuVo = iterator.next();
                if (StringUtils.isEmpty(systemMenuVo.getMenuKey())){
                    iterator.remove();
                }
            }
            Map<String,Object> map = Maps.newHashMap();
            map.put("menu",menuList);
            map.put("user",user);
            return new ResponseData(map);
        } catch (AuthenticationException e) {
            return new ResponseData(CodeEnum.E_400.getCode(),"登录失败");
        }
    }

    /**
     * 短信验证登录
     * @param phone
     * @param code
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/userPhoneLogin")
    @ResponseBody
    public ResponseData userPhoneLogin(String phone, String code, HttpServletRequest request, HttpServletResponse response){
        log.info("登录------------>>/login/userPhoneLogin");
        getParameterMap(request,response);
        if(StringUtils.isEmpty(phone)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"用户名为空！");
        } else if(StringUtils.isEmpty(code) || code.length() != 6 || !code.equals(redisUtil.getString(phone))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"验证码错误！");
        }
        List<SystemUser> systemUser = loginService.getSystemUserMsg("","","",phone);
        if(CollectionUtils.isEmpty(systemUser)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"用户名或密码不正确！");
        }
        if ("10A".equals(systemUser.get(0).getStatus())) {
            return new ResponseData(CodeEnum.E_502.getCode(),"该账号已禁用！");
        }
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(systemUser.get(0).getLoginName(), systemUser.get(0).getPassword());
        try {
            currentUser.login(token);
            UserVO user = (UserVO) SecurityUtils.getSubject().getPrincipal();
            user.setToken(loginService.getToken(user));
            redisUtil.setString(Constants.REDIS_KEY_PREFIX_SHIRO_TOKEN + user.getToken(),
                    user.getPermissions(), Constants.REDIS_SHIRO_TOKEN_EXPIRES);

//            获取菜单
            List<SystemMenuVo> menuList = loginService.getMenuList(user.getLoginName(), user.getPassword());
            Iterator<SystemMenuVo> iterator = menuList.iterator();
            while (iterator.hasNext()) {
                SystemMenuVo systemMenuVo = iterator.next();
                if (StringUtils.isEmpty(systemMenuVo.getMenuKey())){
                    iterator.remove();
                }
            }
            Map<String,Object> map = Maps.newHashMap();
            map.put("menu",menuList);
            map.put("user",user);
            return new ResponseData(map);
        } catch (AuthenticationException e) {
            return new ResponseData(CodeEnum.E_400.getCode(),"登录失败");
        }
    }

}
