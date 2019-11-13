package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.JPush;
import com.yuyue.boss.api.domain.MallShop;
import com.yuyue.boss.api.domain.OrderItem;
import com.yuyue.boss.api.mapper.SendMapper;
import com.yuyue.boss.api.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "SendSmsService")
public class SendServiceImpl implements SendService {

    @Autowired
    private SendMapper sendMapper;

    @Override
    public void updateValid(String status, String id) { sendMapper.updateValid(status,id); }

    @Override
    public void insertJPush(JPush jPush) { sendMapper.insertJPush(jPush); }

    @Override
    public List<JPush> getValid(String notificationTitle, String msgTitle, String msgContent,
                                String extras, String valid) {
        return sendMapper.getValid(notificationTitle,msgTitle,msgContent,extras,valid); }

    @Override
    public List<String> getAttentionList(String authorId) {
        return sendMapper.getAttentionList(authorId); }


    @Override
    public List<JPush> getJPushList(String id,String msgTitle, String extras, String startTime, String endTime) {
        return sendMapper.getJPushList(id,msgTitle,extras,startTime,endTime); }

    @Override
    public void delJPush(String id) {
        sendMapper.delJPush(id); }

    @Override
    public List<MallShop> findShopId(String shopid,String userId,String startTime) {
        return sendMapper.findShopId(shopid,userId,startTime);
    }

    @Override
    public List<OrderItem> findOrderItemId(String orderId) {
        return sendMapper.findOrderItemId(orderId);
    }
}
