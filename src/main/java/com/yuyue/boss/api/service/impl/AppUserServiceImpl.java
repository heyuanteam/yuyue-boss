package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.mapper.AppUserMapper;
import com.yuyue.boss.api.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "/AppUserService")
public class AppUserServiceImpl implements AppUserService {
    @Autowired
    private AppUserMapper appUserMapper;

    @Override
    public AppUser getAppUserMsg(String id,String phone) {
        return appUserMapper.getAppUserMsg(id,phone);
    }

    @Override
    public void updateAppUser(AppUser appUser) {
        appUserMapper.updateAppUser(appUser);
    }

    @Override
    public List<AppUser> getAppUserMsgList(AppUser appUser) {
        return appUserMapper.getAppUserMsgList(appUser);
    }

    @Override
    public void delAppUser(String id) { appUserMapper.delAppUser(id); }

    @Override
    public List<AppUser> getAppUserFatherPhoneList() {
        return appUserMapper.getAppUserFatherPhoneList();
    }

    @Override
    public List<AppUser> getAuthorList(String nickName) {
        return appUserMapper.getAuthorList(nickName);
    }

    @Override
    public void insertAppUser(AppUser appUser) { appUserMapper.insertAppUser(appUser); }
}
