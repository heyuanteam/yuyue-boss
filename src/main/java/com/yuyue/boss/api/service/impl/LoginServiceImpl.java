package com.yuyue.boss.api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yuyue.boss.api.domain.SystemMenu;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.UserVO;
import com.yuyue.boss.api.mapper.LoginMapper;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author ly
 */
@Service(value = "LoginService")
public class LoginServiceImpl implements LoginService {
    private Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public SystemUser getSystemUserMsg(String password, String phone, String id) { return loginMapper.getSystemUserMsg(password,phone,id); }


    @Override
    public UserVO getUser(String loginName, String password) {
        SystemUser systemUser = getSystemUserMsg(loginName, password,"");
        UserVO userVO = BeanUtil.copyProperties(systemUser, UserVO.class);
        userVO.setPermissions(loginMapper.getSystemUserVO(systemUser.getId()));
        userVO.setToken(getToken(userVO));
        return userVO;
    }

    @Override
    public String getToken(UserVO systemUser) {
        String token = "";
        try {
            token = JWT.create()
                    .withAudience(systemUser.getId())          // 将 user id 保存到 token 里面
                    .sign(Algorithm.HMAC256(systemUser.getPassword()));   // 以 password 作为 token 的密钥
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
            log.info("token生成错误！" );
        }
        return token;
    }

    @Override
    public List<SystemMenu> getMenuList(String loginName, String password){
        return loginMapper.getMenuList(loginName,password);
    }

    @Override
    public List<SystemMenu> getMenu(String type, String id){
        return loginMapper.getMenu(type,id);
    }
}
