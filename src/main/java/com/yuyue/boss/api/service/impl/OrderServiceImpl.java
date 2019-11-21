package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.Order;
import com.yuyue.boss.api.mapper.OrderMapper;
import com.yuyue.boss.api.service.OrderService;
import com.yuyue.boss.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "OrderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order getOrderById(String orderId) {
        return orderMapper.getOrderById(orderId);
    }

    @Override
    public List<Order> getOrderList(String orderNo, String realName, String mobile,
                                    String tradeType, String status, String startTime,
                                    String endTime, String type,String userType) {
        return orderMapper.getOrderList(orderNo, realName, mobile, tradeType,
                status, startTime, endTime, type, userType);
    }

    @Override
    public void deleteOrderById(String id) {
        orderMapper.deleteOrderById(id);
    }

    @Override
    public void updateOrder(Order order) {
        orderMapper.updateOrder(order);
    }


}
