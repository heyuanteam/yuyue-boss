package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.Order;

import java.util.List;

public interface OrderService {
    Order getOrderById(String orderId);

    List<Order> getOrderList(Order order);

    void  deleteOrderById(String id);

    void updateOrder(Order order);
}
