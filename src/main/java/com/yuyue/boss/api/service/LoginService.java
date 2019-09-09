package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.UserVO;


public interface LoginService {

    //获取系统用户个人信息以及权限
    UserVO getUser(String loginName, String password);

    //获取系统用户个人信息
    SystemUser getSystemUserMsg(String password,String phone,String id);

    //获取token
    String getToken(UserVO systemUser);

}
