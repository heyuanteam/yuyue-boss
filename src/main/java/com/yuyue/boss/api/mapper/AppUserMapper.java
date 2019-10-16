package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.AppUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface AppUserMapper extends MyBaseMapper<AppUser> {

    AppUser getAppUserMsg(@Param("id") String id);


    void updateAppUser(AppUser appUser);

    @Transactional
    @Insert("INSERT into yuyue_merchant (id,userNo,nickName,realName,phone,password,salt) values " +
            "(#{id},#{userNo},#{nickName},#{realName},#{phone},#{password},#{salt})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertAppUser(AppUser appUser);
}
