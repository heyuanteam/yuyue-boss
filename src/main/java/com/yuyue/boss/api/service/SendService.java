package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.JPush;

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
}
