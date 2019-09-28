package com.yuyue.boss.shiro;

import com.alibaba.fastjson.JSONObject;
import com.yuyue.boss.enums.CodeEnum;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Create by lujun.chen on 2018/09/29
 */
public class AjaxPermissionsAuthorizationFilter extends FormAuthenticationFilter {

    /**
     * 表示当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；如果返回false表示该拦截器实例已经处理了，将直接返回即可。
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        PrintWriter out = null;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        //解决一下跨域问题
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");

        JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", CodeEnum.E_20011.getCode());
            jsonObject.put("message", CodeEnum.E_20011.getMessage());
            try {
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("application/json");
                out = response.getWriter();
                out.println(jsonObject);
            } catch (Exception e) {
            } finally {
                if (null != out) {
                    out.flush();
                    out.close();
                }
            }
//        }
        return false;
    }

    @Bean
    public FilterRegistrationBean registration(AjaxPermissionsAuthorizationFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }
}
