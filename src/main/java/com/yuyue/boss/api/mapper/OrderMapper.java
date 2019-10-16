package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.Order;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper extends MyBaseMapper<Order> {

    @Select("SELECT *,DATE_FORMAT(COMPLETE_TIME ,'%Y-%m-%d %H:%i:%s') completeTime FROM yuyue_order b WHERE b.id = #{orderId} limit 1")
    Order getOrderById(String orderId);

    List<Order> getOrderList(Order order);

    void  deleteOrderById(String id);

    void updateOrder(Order order);


}
