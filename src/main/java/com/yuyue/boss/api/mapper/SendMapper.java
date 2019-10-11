package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.JPush;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SendMapper extends MyBaseMapper<JPush> {

    @Select("SELECT * FROM yuyue_jpush b WHERE b.valid = '10B' and b.id = #{id} ")
    List<JPush> getValid(@Param("id") String id);

    @Transactional
    @Update("UPDATE yuyue_jpush c SET c.valid = #{status} WHERE c.id = #{id} ")
    void updateValid(@Param("status") String status, @Param("id") String id);

    @Transactional
    @Insert("INSERT into yuyue_jpush (id,notificationTitle,msgTitle,msgContent,extras) values " +
            " (#{id},#{notificationTitle},#{msgTitle},#{msgContent},#{extras})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertJPush(JPush jPush);
}
