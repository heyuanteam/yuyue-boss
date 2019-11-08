package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.JPush;
import com.yuyue.boss.api.domain.MallShop;
import com.yuyue.boss.api.domain.OrderItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SendMapper extends MyBaseMapper<JPush> {

    @Select("SELECT * FROM yuyue_jpush b WHERE b.notificationTitle = #{notificationTitle} and b.msgTitle = #{msgTitle} " +
            " and b.msgContent= #{msgContent} and b.extras= #{extras} and b.valid= #{valid}")
    List<JPush> getValid(@Param("notificationTitle") String notificationTitle,
                         @Param("msgTitle") String msgTitle,@Param("msgContent") String msgContent,
                         @Param("extras") String extras,@Param("valid") String valid);

    @Transactional
    @Update("UPDATE yuyue_jpush c SET c.valid = #{status} WHERE c.id = #{id} ")
    void updateValid(@Param("status") String status, @Param("id") String id);

    @Transactional
    @Insert("INSERT into yuyue_jpush (id,notificationTitle,msgTitle,msgContent,extras) values " +
            " (#{id},#{notificationTitle},#{msgTitle},#{msgContent},#{extras})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertJPush(JPush jPush);

    @Select("SELECT userId FROM yuyue_attention b WHERE b.authorId = #{authorId} ")
    List<String> getAttentionList(@Param("authorId")String authorId);

    List<JPush> getJPushList(@Param("id")String id,@Param("msgTitle")String msgTitle,@Param("extras") String extras,
                             @Param("startTime") String startTime, @Param("endTime")String endTime);

    @Transactional
    @Delete("DELETE FROM yuyue_jpush WHERE id =#{id} ")
    void delJPush(@Param("id")String id);

    MallShop findShopId(@Param("shopid") String shopid);

    List<OrderItem> findOrderItemId(@Param("orderId") String orderId);
}
