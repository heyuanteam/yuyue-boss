package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.mapper.LoginMapper;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author ly
 */
@Service(value = "LoginService")
public class LoginServiceImpl implements LoginService {
    private Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public SystemUser getAppUserMsg(String password, String phone, String id) { return loginMapper.getAppUserMsg(password,phone,id); }

    @Override
    public UserVO getUser(String loginName, String password) {
        return null;
    }


}
