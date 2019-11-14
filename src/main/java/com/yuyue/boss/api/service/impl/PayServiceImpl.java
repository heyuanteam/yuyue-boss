package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.ChangeMoney;
import com.yuyue.boss.api.domain.Order;
import com.yuyue.boss.api.mapper.PayMapper;
import com.yuyue.boss.api.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "PayService")
public class PayServiceImpl implements PayService {

    @Autowired
    private PayMapper payMapper;

    @Override
    public void updateOrderStatus(String responseCode, String responseMessage, String status,String orderno) {
        payMapper.updateOrderStatus(responseCode,responseMessage,status,orderno);
    }

    @Override
    public List<Order> findOrderList(String startTime) { return payMapper.findOrderList(startTime);}

    @Override
    public void createShouMoney(ChangeMoney tGMoney) {
        payMapper.createShouMoney(tGMoney);}

}
