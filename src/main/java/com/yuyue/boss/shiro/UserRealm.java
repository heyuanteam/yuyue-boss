package com.yuyue.boss.shiro;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuyue.boss.api.domain.UserVO;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.Constants;
import com.yuyue.boss.utils.BeanUtil;
import com.yuyue.boss.utils.RedisUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Create by lujun.chen on 2018/09/29
 */
public class UserRealm extends AuthorizingRealm {
    private Logger logger = LoggerFactory.getLogger(UserRealm.class);

    @Autowired
    private LoginService loginService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        //查询用户的权限
//        Session session = SecurityUtils.getSubject().getSession();
//        UserVO userVO = (UserVO) session.getAttribute(Constants.SESSION_USER_INFO);
//        UserVO userVO = (UserVO) SecurityUtils.getSubject().getPrincipal();
        UserVO userVO = BeanUtil.copyProperties(principals.getPrimaryPrincipal(), UserVO.class);
        logger.info("本用户信息：{}", JSON.toJSONString(userVO));
        logger.info("本用户权限为:" + userVO.getPermissions());
//        //为当前用户设置角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addStringPermissions(userVO.getPermissions());
        return authorizationInfo;
//        return null;
    }

    /**
     * 验证当前登录的Subject
     * LoginController.login()方法中执行Subject.login()时 执行此方法
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        String loginName = (String) authcToken.getPrincipal();
        // 获取用户密码
        String password = new String((char[]) authcToken.getCredentials());
        UserVO userVO = loginService.getUser(loginName, password);
        if (userVO == null) {
            //没找到帐号
            throw new UnknownAccountException();
        }
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userVO,
                userVO.getPassword(),
                //ByteSource.Util.bytes("salt"), salt=username+salt,采用明文访问时，不需要此句
                getName()
        );
        //session中不需要保存密码
//        userVO.setPassword("");
        //将用户信息放入session中
        SecurityUtils.getSubject().getSession().setAttribute(Constants.SESSION_USER_INFO, userVO);
        return authenticationInfo;
    }
}
