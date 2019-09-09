package com.yuyue.boss.api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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

/**
 * @author ly
 */
@Service(value = "LoginService")
public class LoginServiceImpl implements LoginService {
    private Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public UserVO getUser(String loginName, String password) {
        SystemUser systemUser = loginMapper.getSystemUserMsg(loginName, password);
        UserVO userVO = BeanUtil.copyProperties(systemUser, UserVO.class);
        userVO.setPermissions(loginMapper.getSystemUserVO(systemUser.getId()));
        userVO.setToken(getToken(loginName));
        return userVO;
    }

    private String getToken(String loginName) {
        return DigestUtils.md5DigestAsHex((loginName + System.currentTimeMillis()).getBytes());
    }
}
