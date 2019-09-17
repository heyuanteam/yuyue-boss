package com.yuyue.boss.api.controller;

import com.yuyue.boss.api.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 系统用户配置
 */
@RestController
@RequestMapping(value = "/user", produces = "application/json; charset=UTF-8")
public class UserController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private LoginService loginService;


}
