package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.SystemUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ly
 */
@Repository
public interface LoginMapper extends MyBaseMapper<SystemUser> {

    SystemUser getAppUserMsg(@Param("password") String password, @Param("phone") String phone, @Param("id") String id);

}
