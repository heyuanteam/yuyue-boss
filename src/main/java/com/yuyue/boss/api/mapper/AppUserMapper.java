package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.AppUser;
import org.apache.ibatis.annotations.*;
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

    @Select("SELECT id,user_no userNo,user_nick_name nickName,user_real_name realName,id_type idType,id_card idCard,phone,sex,city,jpushName,password,salt,\n" +
            "                    user_status userStatus,total ,income,ADDR_DETAIL addrDetail,HEADP_URL headpUrl,USER_TYPE userType,cardFUrl,cardZUrl,userUrl,fatherPhone,\n" +
            "                    ATTENTION_TOTAL attentionTotal,LIKE_TOTAL likeTotal,COMMENT_TOTAL commentTotal,education ,wechat ,signature,opendId,wechatName,extensionStatus,\n" +
            "                    DATE_FORMAT(CREATE_TIME ,'%Y-%m-%d %H:%i:%s') createTime,DATE_FORMAT(UPDATE_TIME ,'%Y-%m-%d %H:%i:%s') updateTime,frontCover,rewardStatus,\n" +
            "                    zfbNumber,zfbRealName\n" +
            "                    FROM yuyue_merchant\n" +
            "                   -- where fatherPhone != NULL or fatherPhone != null or fatherPhone != '' order  by  CREATE_TIME DESC ")
//            "                   -- where fatherPhone is Null or fatherPhone is null or fatherPhone = '' order  by  CREATE_TIME DESC\n" +
//            "                   where 1=1 order  by  CREATE_TIME DESC " )
    List<AppUser> getAppUserFatherPhoneList();
}
