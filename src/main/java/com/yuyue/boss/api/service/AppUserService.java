package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.AppUser;

import java.util.List;

public interface AppUserService {

    AppUser getAppUserMsg(String id,String phone);
    
    void updateAppUser(AppUser appUser);
    
    void insertAppUser(AppUser appUser);

    List<AppUser> getAppUserMsgList(AppUser appUser);

    void delAppUser(String id);

    List<AppUser> getAppUserFatherPhoneList();

    //通过艺名模糊查询查出所有艺人
    List<AppUser> getAuthorList(String nickName);
}
