package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.AppUser;




public interface AppUserService {

    AppUser getAppUserMsg(String id);


    void updateAppUser(AppUser appUser);
}
