package com.yuyue.boss.token;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;


public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginService loginService;


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //设置跨域--开始
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        //无条件放行OPTIONS
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            setHeader(httpRequest, httpResponse);
//            return true;
        }
        ResponseData returnResult=new ResponseData();
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 判断接口是否需要登录
        LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
        // 有 @LoginRequired 注解，需要认证
        if (methodAnnotation != null) {
            // 执行认证
            String token = request.getHeader("token");  // 从 http 请求头中取出 token
            if (token == null) {
                returnResult.setMessage(CodeEnum.AUTH_ERROR.getMessage());
                returnResult.setCode(CodeEnum.AUTH_ERROR.getCode());
                //设置状态码
//                response.setStatus(500);
                response.setContentType("application/json;charset=UTF-8");
                //将 登录失败 信息打包成json格式返回
                response.getWriter().write(JSON.toJSONString(returnResult));
//                throw new RuntimeException("信息已失效，请重新登录");
                return false;
            }
            String userId = "";
            try {
                userId = String.valueOf(JWT.decode(token).getAudience().get(0));  // 获取 token 中的 user id
            } catch (JWTDecodeException e) {
                returnResult.setMessage(CodeEnum.AUTH_ERROR.getMessage());
                returnResult.setCode(CodeEnum.AUTH_ERROR.getCode());
                //设置状态码
//                response.setStatus(500);
                response.setContentType("application/json;charset=UTF-8");
                //将 登录失败 信息打包成json格式返回
                response.getWriter().write(JSON.toJSONString(returnResult));
//                throw new RuntimeException("信息已失效，请重新登录");
                return false;
            }
            SystemUser user = loginService.getSystemUserMsg("","",userId,"");
            if (user == null) {
                returnResult.setMessage(CodeEnum.AUTH_ERROR.getMessage());
                returnResult.setCode(CodeEnum.AUTH_ERROR.getCode());
                //设置状态码
//                response.setStatus(500);
                response.setContentType("application/json;charset=UTF-8");
                //将 登录失败 信息打包成json格式返回
                response.getWriter().write(JSON.toJSONString(returnResult));
//                throw new RuntimeException("信息已失效，请重新登录");
                return false;
            }
            // 验证 token
            try {
                JWTVerifier verifier =  JWT.require(Algorithm.HMAC256(user.getPassword())).build();
                try {
                    verifier.verify(token);
                } catch (JWTVerificationException e) {
                    returnResult.setMessage(CodeEnum.AUTH_ERROR.getMessage());
                    returnResult.setCode(CodeEnum.AUTH_ERROR.getCode());
                    //设置状态码
//                    response.setStatus(500);
                    response.setContentType("application/json;charset=UTF-8");
                    //将 登录失败 信息打包成json格式返回
                    response.getWriter().write(JSON.toJSONString(returnResult));
//                throw new RuntimeException("信息已失效，请重新登录");
                    return false;
                }
            } catch (UnsupportedEncodingException ignore) {}
            request.setAttribute("currentUser", user);
            return true;
        }
        return true;
    }

    /**
     * 为response设置header，实现跨域
     */
    private void setHeader(HttpServletRequest request, HttpServletResponse response) {
        System.err.println("ShiroFilter");
//        System.err.println("setHeader -- " + request.getHeader("Access-Control-Request-Headers"));
        //跨域的header设置
//        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
//        response.setHeader("Access-Control-Allow-Methods", request.getMethod());
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST,PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "content-type,x-requested-with,token");
//        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Allow-Headers") + ",token");
        //防止乱码，适用于传输JSON数据
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {}

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}
}
