package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.AppUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface AppUserMapper extends MyBaseMapper<AppUser> {

    AppUser getAppUserMsg(@Param("id") String id,@Param("phone") String phone);

    List<AppUser> getAppUserMsgList(AppUser appUser);

    @Transactional
    void updateAppUser(AppUser appUser);

    @Transactional
    @Insert("INSERT into yuyue_merchant (ID,USER_NO,USER_NICK_NAME,USER_REAL_NAME,PHONE,PASSWORD,SALT) values " +
            "(#{id},#{userNo},#{nickName},#{realName},#{phone},#{password},#{salt})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertAppUser(AppUser appUser);

    @Transactional
    @Delete("DELETE FROM yuyue_merchant WHERE id =#{id} ")
    void delAppUser(@Param("id") String id);
}
