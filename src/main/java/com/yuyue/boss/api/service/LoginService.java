package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.enums.UserVO;

public interface LoginService {

    //根据手机号查询
    SystemUser getAppUserMsg(String password, String phone, String id);

    UserVO getUser(String loginName, String password);
}
