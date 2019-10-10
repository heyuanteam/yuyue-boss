package com.yuyue.boss.api.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.utils.HttpUtils;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * controller基类
 */
@Slf4j
public class BaseController {

    protected Map<String,String> getParameterMap(HttpServletRequest request, HttpServletResponse response){
        //解决一下跨域问题
        HttpUtils.setHeader(request,response);
        Map<String,String> map = new HashMap<>();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = enumeration.nextElement();
            String propertyValue = request.getParameter(propertyName.trim());
            map.put(propertyName,propertyValue);
        }
        log.info("打印参数=======================>>>"+map.toString());
        return map;
    }

    /**
     * 认证失败
     * @param request
     * @param response
     */
    @ExceptionHandler({UnauthenticatedException.class})
    public void unauthenticatedException(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        log.info("认证失败=======================>>>");
        map.put("code",CodeEnum.AUTH_ERROR.getCode());
        map.put("message", CodeEnum.AUTH_ERROR.getMessage());
        writeJson(map,request, response);
    }

    /**
     * 登录认证异常
     * @param request
     * @param response
     */
    @ExceptionHandler({AuthenticationException.class})
    public void authenticationException(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        log.info("登陆已过期,请重新登陆=======================>>>");
        map.put("code",CodeEnum.AUTH_ERROR.getCode());
        map.put("message", CodeEnum.AUTH_ERROR.getMessage());
        writeJson(map,request, response);
    }

    /**
     * 无操作权限的处理
     */
    @ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
    public void authorizationException(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        log.info("权限不足=======================>>>");
        map.put("code",CodeEnum.E_502.getCode());
        map.put("message",CodeEnum.E_502.getMessage());
        writeJson(map,request, response);
    }

    protected void writeJson(Map<String,Object> map,HttpServletRequest request, HttpServletResponse response){
        PrintWriter out = null;
        try {
            //跨域的header设置
            response.setHeader("Access-control-Allow-Origin",request.getHeader("Origin"));
            response.setHeader("Access-control-Allow-Methods",request.getMethod());
            response.setHeader("Access-control-Allow-Credentisls","true");
            response.setHeader("Access-control-Allow-Headers",request.getHeader("Access-control-Request-Headers"));
            //防止乱码，使用于传输JSON数据
            response.setHeader("Content-Type","application/json;charset=UTF-8");
            out = response.getWriter();
            out.write(JSONUtils.toJSONString(map));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
