package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.AppUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface AppUserMapper extends MyBaseMapper<AppUser> {

    AppUser getAppUserMsg(@Param("id") String id);


    void updateAppUser(AppUser appUser);


}
