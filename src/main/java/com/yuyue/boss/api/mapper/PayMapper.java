package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.ChangeMoney;
import com.yuyue.boss.api.domain.Order;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PayMapper extends MyBaseMapper<Order> {

    @Transactional
    @Update("UPDATE yuyue_order b SET b.`status`=#{status},b.responseCode=#{responseCode},b.responseMessage=#{responseMessage} " +
            "WHERE b.orderNo = #{orderNo} ")
    void updateOrderStatus(@Param("responseCode") String responseCode, @Param("responseMessage") String responseMessage,
                           @Param("status") String status, @Param("orderNo") String orderNo);

    @Select("SELECT * FROM yuyue_order b where DATE_FORMAT(b.COMPLETE_TIME ,'%Y-%m-%d %H:%i:%s') < #{startTime} AND b.`status` = '10A'")
    List<Order> findOrderList(@Param(value = "startTime") String startTime);

    @Transactional
    @Insert("INSERT into yuyue_change_money (id,changeNo,tradeType,money,merchantId,mobile,note,sourceId)  values  " +
            " (#{id},#{changeNo},#{tradeType},#{money},#{merchantId},#{mobile},#{note},#{sourceId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void createShouMoney(ChangeMoney tGMoney);
}
