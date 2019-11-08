package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.JPush;
import com.yuyue.boss.api.domain.MallShop;
import com.yuyue.boss.api.domain.OrderItem;

import java.util.List;

public interface SendService {
    //极光更新
    void updateValid(String status, String id);

    //极光插入
    void insertJPush(JPush jPush);

    //极光获取
    List<JPush> getValid(String notificationTitle, String msgTitle, String msgContent, String extras, String valid);

    //获取关注的人
    List<String> getAttentionList(String authorId);

    //获取公告列表
    List<JPush> getJPushList(String id,String msgTitle, String extras, String startTime, String endTime);

    //删除公告
    void delJPush(String id);

    //查询库存为0
    MallShop findShopId(String shopid);

    //查询商家订单
    List<OrderItem> findOrderItemId(String orderId);
}
