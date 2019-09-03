package com.yuyue.boss.api.controller;

import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.service.LoginService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 登录模块
 */
@RestController
//@RequestMapping(value="/login", produces = "application/json; charset=UTF-8")
public class LoginController {
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    public String login(String username, String password, Model model){
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        Subject currentUser = SecurityUtils.getSubject();
        try {
            //主体提交登录请求到SecurityManager
            currentUser.login(token);
        }catch (IncorrectCredentialsException ice){
            model.addAttribute("msg","密码不正确");
        }catch(UnknownAccountException uae){
            model.addAttribute("msg","账号不存在");
        }catch(AuthenticationException ae){
            model.addAttribute("msg","状态不正常");
        }
        if(currentUser.isAuthenticated()){
            System.out.println("认证成功");
            model.addAttribute("currentUser",currentUser());
            return "success";
        }else{
            token.clear();
            return "login";
        }
    }

    /**
     * shiro获取当前用户
     * @return
     */
    private SystemUser currentUser(){
        SystemUser currentUser = (SystemUser) SecurityUtils.getSubject().getPrincipal();
        return  currentUser;
    }
}

