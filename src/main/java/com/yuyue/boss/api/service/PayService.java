package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.Order;

import java.util.List;

public interface PayService {

    void updateOrderStatus(String responseCode, String responseMessage, String status, String orderno);

    List<Order> findOrderList(String startTime);
}
