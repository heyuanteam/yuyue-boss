package com.yuyue.boss.api.controller;

import com.yuyue.boss.api.domain.UserVO;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.Constants;
import com.yuyue.boss.enums.ResponseData;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * Create by lujun.chen on 2018/09/29
 */
@RestController
@RequestMapping(value = "/login", produces = "application/json; charset=UTF-8")
public class LoginController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LoginService loginService;

    /**
     * 登录
     *
     * shiro登录，shiro采用Facade模式（门面模式），所有与shiro的交互都通过Subject对象API。
     * 调用Subject.login后会触发UserRealm的doGetAuthenticationInfo方法，进行具体的登录验证处理。
     *
     * @param password 密码
     * @return
     */
    @RequestMapping("/userLogin")
    @ResponseBody
    public ResponseData userLogin(String loginName, String password, HttpServletRequest request) {
        /*打印参数=======================*/
        getParameterMap(request);

        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginName, password);
        try {
            //会触发com.itclj.common.shiro.UserRealm的doGetAuthenticationInfo方法
            currentUser.login(token);
//            Session session = SecurityUtils.getSubject().getSession();
//            UserVO userVO = (UserVO) session.getAttribute(Constants.SESSION_USER_INFO);
//            Constants.REDIS_KEY_PREFIX_SHIRO_TOKEN + userVO.getToken()
            UserVO userVO = (UserVO) SecurityUtils.getSubject().getPrincipal();
            redisTemplate.opsForValue().set(userVO.getId(),
                    userVO.getPermissions(),
                    Constants.REDIS_SHIRO_TOKEN_EXPIRES, TimeUnit.SECONDS);
            userVO.setToken(loginService.getToken(userVO));
            return new ResponseData(userVO);
        } catch (AuthenticationException e) {
            return new ResponseData("登录失败");
        }
    }

    /**
     * 没有权限，会跳到这里
     * @return
     */
    @RequestMapping("/returnLogin")
    @ResponseBody
    public ResponseData returnLogin(HttpServletRequest request) {
        /*打印参数=======================*/
        getParameterMap(request);
        log.info("没有权限，会跳到这里=======================>>>");
        try {
            return new ResponseData(CodeEnum.E_502);
        } catch (AuthenticationException e) {
            return new ResponseData("登录失败");
        }
    }
}
