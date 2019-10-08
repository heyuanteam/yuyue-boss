package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.mapper.AppUserMapper;
import com.yuyue.boss.api.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "/AppUserService")
public class AppUserServiceImpl implements AppUserService {
    @Autowired
    private AppUserMapper appUserMapper;

    @Override
    public AppUser getAppUserMsg(String id) {
        return appUserMapper.getAppUserMsg(id);
    }

    @Override
    public void updateAppUser(AppUser appUser) {
        appUserMapper.updateAppUser(appUser);
    }
}
